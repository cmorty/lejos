
/*
 * This file implements the software-based I2C for accessing I2C
 * devices via the sensor ports.
 */

#include "i2c.h"
#include "AT91SAM7.h"
#include "interrupts.h"
#include "aic.h"
#include "systick.h"
#include "memory.h"
#include "sensors.h"
#include <string.h>
//Sizes etc.
#define I2C_CLOCK 9600
#define I2C_HS_CLOCK 125000
#define I2C_MAX_PARTIAL_TRANSACTIONS 5
#define I2C_N_PORTS 4
#define I2C_BUF_SIZE 32

// Options
#define I2C_LEGO_MODE 1
#define I2C_ALWAYS_ACTIVE 2
#define I2C_NO_RELEASE 4
#define I2C_HIGH_SPEED 8

// Delay used when in Lego mode
#define I2C_LEGO_DELAY 5
// Fast pulse stretch
#define I2C_CLOCK_RETRY 5
// Timeout for pulse stretch
#define I2C_MAX_STRETCH 100

#define IO_COMPLETE_MASK 0xf
#define BUS_FREE_MASK 0xf00
#define BUS_FREE_SHIFT 8

// A partial transaction has the following form:
// 1. It has a transaction start state, to indicate the type of transaction
// 2. It has an optional data pointer

// A transaction has the following form:
// 1. One to 5 partial transactions.

// Some examples:
//
//  Transaction to write a single byte to a device will have:
//  partial transaction:
//  0: Start. Tx 1 byte address
//  1: send the 1 byte of data 
//  2: Stop
//
//
//  Transaction to read one byte from an address will have:
//  partial transactions:
//  0: Start. Tx one address byte.
//  1: Read byte.
//  2. Stop
//
//  Transaction to write some bytes to a particular internal address at an address:
//
//  0: Start. Send address
//  1: Send internal address + data bytes.
//  2: Stop 
//
//  Transaction to read some bytes from a particular internal address at an address:
//
//  0: Start. Send address
//  1: Send internal address
//  2: Restart. Send address.
//  3: Read data.
//  4. Stop
//
// Note: It appears that the Lego ultrasonic sensor needs a 
// stop and an extra clock before the restart.
//
// Additional changes made by Andy Shaw...
//
// Port 4 on the nxt is a little odd. It has extra hardware to implement
// an RS485 interface. However this interacts with the digital I/O lines
// which means that the original open collector driver used in this code
// did not work. The code now uses full drive on both clock and data. The 
// extra hardware means that the output voltage is lower than on other ports
// this is particularly bad when using the external pull up where the voltage
// may only reach approx 3.5 volts rather than the more normal 4.5 volts.
// Because of this some sensors may not correctly recognize clock signals,
// generated using only the pull up resistor. The only time this mode is
// used is when in standard mode (and with NO_RELEASE not set), so care
// should be taken if using this mode on port 4.
//
// Re-worked by Andy Shaw 02/2009
//
// In attempt to make the code work with a wider range of sensors I've
// reworked it to bring it more into line with the i2c spec and the
// Lego implementation. There are now more partial transactions and
// there is a mode setting to allow standard mode or Lego mode operation.
// In addition tests showed that the i2c code was using up to 40% of the
// available cpu. To reduce this the state machine has been simplified
// and modified to require only 2 states per clock instead of 4 (and thus
// allowing the use of a lower interrupt rate). We now track the usage of
// the i2c system. When no ports are active the clock is disabled.
// Finally the overall i2c operation has been split into 3 stages...
// 1. Start the transaction.
// 2. Wait for the transaction to complete.
// 3. Read the results.
//
// High Speed mode 02/2011
// I've added the option of using a high speed transfer mode whic operates at
// 125KHz instead of the Lego standard 9.6KHz. This basically just drives the
// pins using a timed busy wait loop (driven by a hardware timer). This will
// hog the cpu during the transaction, but should overall actually use less
// cpu time (since we do not have the interrupt overhead). I've tested the 
// speed with a number of i2c based sensors and all but the Lego sesnor seem
// to work at this speed. I did experiment with an optimised version of the
// state machine (which could run faster), but had a large number of errors,
// so have reverted back to the standard state machine (which saves spece).


struct i2c_partial_transaction {
  U8  state;    // Initial state for this transaction
  U16 nbits;	// N bits to transfer
  U8* data;	// Data buffer
};

typedef enum {
  I2C_DISABLED = 0,
  I2C_IDLE,
  I2C_ACTIVEIDLE,
  I2C_COMPLETE,
  I2C_BEGIN,
  I2C_NEWSTART,
  I2C_NEWRESTART,
  I2C_NEWREAD,
  I2C_NEWWRITE,
  I2C_START1,
  I2C_START2,
  I2C_DELAY,
  I2C_RXDATA1,
  I2C_RXDATA2,
  I2C_RXDATA3,
  I2C_RXENDACK,
  I2C_RXACK1,
  I2C_RXACK2,
  I2C_TXDATA1,
  I2C_TXDATA2,
  I2C_TXACK1,
  I2C_TXACK2,
  I2C_STOP1,
  I2C_STOP2,
  I2C_STOP3,
  I2C_ENDLEGO1,
  I2C_ENDLEGO2,
  I2C_END,
  I2C_ENDSTOP1,
  I2C_ENDSTOP2,
  I2C_ENDSTOP3,
  I2C_FAULT,
  I2C_RELEASE,
} i2c_port_state;

typedef struct {
  U32 scl_pin;
  U32 sda_pin;
  U32 ready_mask;
  U8  buffer[I2C_BUF_SIZE+2];
  struct i2c_partial_transaction partial_transaction[I2C_MAX_PARTIAL_TRANSACTIONS];
  struct i2c_partial_transaction *current_pt;

  i2c_port_state state;

  U8  *data;
  U32 nbits;
  int delay;
  U8  bits;
  U8 fault:1;
  U8 lego_mode:1;
  U8 always_active:1;
  U8 no_release:1;
  U8 high_speed:1;
  U8 port_bit;
  U16 read_len;
} i2c_port;

static i2c_port *i2c_ports[I2C_N_PORTS];
// We maintain an active port list of those ports that currently need
// attention at interrupt time. This list is double buffered and is built
// dynamically as required.
static i2c_port *i2c_active[2][I2C_N_PORTS+1];
static i2c_port **active_list = i2c_active[0];
static U32 i2c_port_busy = 0;

// The I2C state machines are pumped by a timer interrupt
// running at 2x the bit speed. This state machine has been
// optimized to minimize the time spent processing during
// an interrupt.


extern void i2c_timer_isr_entry(void);

static void
i2c_doio(i2c_port *p)
{
    switch (p->state) {
    default:
    case I2C_DISABLED:
    case I2C_IDLE:		// Not in a transaction
    case I2C_ACTIVEIDLE:	// Not in a transaction but active
    case I2C_COMPLETE:          // Transaction completed
      break;
    case I2C_BEGIN:		
      // Start new transaction
      *AT91C_PIOA_OER = p->sda_pin|p->scl_pin;
      p->fault = 0;
      p->state = p->current_pt->state;
      break;
    case I2C_NEWSTART:		
      // Start the current partial transaction
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = *(p->data);
      p->state = I2C_START1;
      *AT91C_PIOA_SODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      break;
    case I2C_NEWRESTART:		
      // restart a new partial transaction
      // Take the clock low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = *(p->data);
      p->state = I2C_START1;
      *AT91C_PIOA_SODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      break;
    case I2C_START1:
      // SDA high, take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      p->state = I2C_START2;
      break;
    case I2C_START2:		
      // Take SDA low while SCL is high
      *AT91C_PIOA_CODR = p->sda_pin;
      p->state = I2C_TXDATA1;
      break;
    case I2C_DELAY:
      if (p->delay == 0)
        p->state = I2C_RXDATA1;
      else
        p->delay--;
      break;
    case I2C_NEWWRITE:		
      // Start the write partial transaction
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = *(p->data);
      p->state = I2C_TXDATA1;
      // FALLTHROUGH
    case I2C_TXDATA1:
      // Take SCL low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_TXDATA2;
      break;
    case I2C_TXDATA2:
      // set the data line
      p->nbits--;
      if(p->bits & 0x80)
        *AT91C_PIOA_SODR = p->sda_pin;
      else
        *AT91C_PIOA_CODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      p->bits <<= 1;
      if((p->nbits & 7) == 0) 
          p->state = I2C_TXACK1;
        else
          p->state = I2C_TXDATA1;
      // Take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      break;
    case I2C_TXACK1:
      // Take SCL low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_TXACK2;
      // release the data line
      *AT91C_PIOA_ODR = p->sda_pin;
      break;
    case I2C_TXACK2:
      // Take SCL High
      *AT91C_PIOA_SODR = p->scl_pin;
      if(*AT91C_PIOA_PDSR & p->sda_pin)
        p->state = I2C_FAULT;
      else if (p->nbits == 0)
      {
        p->current_pt++;
        p->state = p->current_pt->state;
      }
      else
      {
        p->data++;
        p->bits = *(p->data);
        p->state = I2C_TXDATA1;
      }
      break;
    case I2C_NEWREAD:		
      // Start the read partial transaction
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = 0;
      // Take SCL Low
      *AT91C_PIOA_CODR = p->scl_pin;
      if (p->lego_mode) {
        p->state = I2C_DELAY;
        p->delay = I2C_LEGO_DELAY;
      }
      else
        p->state = I2C_RXDATA1;
      // get ready to read
      *AT91C_PIOA_ODR = p->sda_pin;
      break;
    case I2C_RXDATA1:
      p->delay = I2C_MAX_STRETCH;
      p->state = I2C_RXDATA2;
      // Fall through
    case I2C_RXDATA2:
      if (p->high_speed)
      {
        // Allow pulse stretching, make clock line float
        *AT91C_PIOA_ODR = p->scl_pin;
        if (--p->delay <= 0)
        {
          p->state = I2C_FAULT;
          break;
        }
          
        int i = 0;
        while(!(*AT91C_PIOA_PDSR & p->scl_pin) && i++ < I2C_CLOCK_RETRY) ;
        if (!(*AT91C_PIOA_PDSR & p->scl_pin))
          break;
      }
      // Take SCL High
      *AT91C_PIOA_SODR = p->scl_pin;
      *AT91C_PIOA_OER = p->scl_pin;
      p->state = I2C_RXDATA3;
      break;
    case I2C_RXDATA3:
      // Receive a bit.
      p->bits <<= 1;
      if(*AT91C_PIOA_PDSR & p->sda_pin)
        p->bits |= 1;
      // Take SCL Low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->nbits--;
      if((p->nbits & 7) == 0){
        *(p->data) = p->bits;
        if (p->nbits)
          p->state = I2C_RXACK1;
        else
          p->state = I2C_RXENDACK;
      }
      else
        p->state = I2C_RXDATA1;
      break;
    case I2C_RXACK1:
      // take data low
      *AT91C_PIOA_CODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      // Move on to next byte
      p->data++;
      p->bits = 0;
      p->state = I2C_RXACK2;
      // Clock high
      *AT91C_PIOA_SODR = p->scl_pin;
      break;
    case I2C_RXACK2:
      // Take SCL Low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_RXDATA1;
      // get ready to read
      *AT91C_PIOA_ODR = p->sda_pin;
      break;
    case I2C_RXENDACK:
      // take data high
      *AT91C_PIOA_SODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      // get ready to move to the next state
      p->current_pt++;
      p->state = p->current_pt->state;
      // Clock high data is already high
      *AT91C_PIOA_SODR = p->scl_pin;
      break;
    case I2C_STOP1:
      // Issue a Stop state
      // SCL is high, take it low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_STOP2;
      *AT91C_PIOA_CODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      break;
    case I2C_STOP2:
      // Take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      p->state = I2C_STOP3;
      break;  
    case I2C_STOP3:
      // Take SDA pin high while the clock is high
      *AT91C_PIOA_SODR = p->sda_pin;
      // and move to the next state
      p->current_pt++;
      p->state = p->current_pt->state;
      break;
    case I2C_ENDLEGO1:
      // Lego mode end case, does not issue stop. Used at end of rx
      // Clock low data is already high, release the data line
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_ENDLEGO2;
      *AT91C_PIOA_ODR = p->sda_pin;
      break;
    case I2C_ENDLEGO2:
      // Clock high, data is already high and we are done
      *AT91C_PIOA_SODR = p->scl_pin;
      p->state = I2C_RELEASE;
      break;
    case I2C_END:
      // End the transaction but hold onto the bus, keeping the clock low
      // Clock low and keep it active
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_RELEASE;
      break;
    case I2C_ENDSTOP1:
      // Issue a Stop state
      // SCL is high, take it low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_ENDSTOP2;
      *AT91C_PIOA_CODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      break;
    case I2C_ENDSTOP2:
      // Take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      p->state = I2C_ENDSTOP3;
      break;  
    case I2C_ENDSTOP3:
      // Take SDA pin high while the clock is high
      *AT91C_PIOA_SODR = p->sda_pin;
      p->state = I2C_RELEASE;
      break;
    case I2C_FAULT:
      p->fault = 1;
      p->state = I2C_ENDSTOP1;
      break;
    case I2C_RELEASE:
      // Release whichever lines we can
      *AT91C_PIOA_ODR = p->ready_mask;
      // All done
      i2c_port_busy &= ~p->port_bit;
      p->state = I2C_COMPLETE;
      break;
    }
}

void
i2c_timer_isr_C(void)
{
  U32 dummy = *AT91C_TC0_SR;
  i2c_port **ap;
  for(ap = active_list; *ap; ap++)
    i2c_doio(*ap);
}

static
void
build_active_list()
{
  /** Build a new active list. 
   * Scan the i2c ports looking to see if any are active, if they are
   * add it to the list. The list is actually double bufered so we can
   * simply flip from one to the other and avoiding having to disable
   * interrupts. Onece we have built the list we can decide what to do
   * If the list is empty disable the timer. If not start it...
   */
  int i;
  i2c_port **new_list = (active_list == i2c_active[0] ? i2c_active[1] : i2c_active[0]);
  i2c_port **ap = new_list;
  // build the list
  for(i=0; i < I2C_N_PORTS; i++)
    if (i2c_ports[i] && i2c_ports[i]->state > I2C_IDLE && !i2c_ports[i]->high_speed)
      *ap++ = i2c_ports[i];
  *ap = NULL;
  // If there are no active ports then we can just disable things
  if (*new_list == NULL)
    *AT91C_TC0_CCR = AT91C_TC_CLKDIS;
  else
  {
    // if the clock is disabled enable it
    if (*active_list == NULL)
    {
      *AT91C_TC0_CCR = AT91C_TC_CLKEN; /* Enable */
      *AT91C_TC0_CCR = AT91C_TC_SWTRG; /* Software trigger */
    }
  }
  // Install the new list, on a 32 bit system this operation is atomic
  // and safe...
  active_list = new_list;
}


// Disable an I2C port
void
i2c_disable(int port)
{
  if (port >= 0 && port < I2C_N_PORTS && i2c_ports[port]) {
    i2c_port *p = i2c_ports[port];

    U32 pinmask = p->scl_pin | p->sda_pin;
    *AT91C_PIOA_ODR = pinmask;
    i2c_ports[port] = NULL;
    build_active_list();
    system_free((byte *)p);
    sp_reset(port);
    i2c_port_busy &= ~(1 << port);
  }
}

void
i2c_disable_all()
{
  int i;
  for(i = 0; i < I2C_N_PORTS; i++)
    i2c_disable(i);
}


// Enable an I2C port
// returns > 0 OK, == 0 no memory < 0 error
int i2c_enable(int port, int mode)
{
  if (port >= 0 && port < I2C_N_PORTS) {
    U32 pinmask;
    i2c_port *p = i2c_ports[port];
    // Allocate memory if required
    if (!p)
    {
      p = (i2c_port *) system_allocate(sizeof(i2c_port));
      if (!p) return 0;
      i2c_ports[port] = p;
    }
    p->scl_pin = sensor_pins[port].pins[SP_DIGI0];
    p->sda_pin = sensor_pins[port].pins[SP_DIGI1];
    pinmask = p->scl_pin | p->sda_pin;
    p->state = I2C_IDLE;
    /* Set data & clock to be enabled for output with
     * pullups disabled.
     */
    *AT91C_PIOA_SODR  = pinmask;
    *AT91C_PIOA_OER   = pinmask;
    *AT91C_PIOA_PPUDR = pinmask;
    /* If we are always active, we never drop below the ACTIVEIDLE state */
    p->lego_mode = ((mode & I2C_LEGO_MODE) ? 1 : 0);
    p->no_release = ((mode & I2C_NO_RELEASE) ? 1 : 0);
    p->always_active = ((mode & I2C_ALWAYS_ACTIVE) ? 1 : 0);
    p->high_speed = ((mode & I2C_HIGH_SPEED) ? 1 : 0);
    p->port_bit = 1 << port;
    if (p->always_active) {
      p->state = I2C_ACTIVEIDLE;
      build_active_list();
    }
    // Select which lines to test to see if the bus is busy.
    // If the clock line is being driven by us we do not test it.
    if (p->lego_mode || p->no_release)
      p->ready_mask = p->sda_pin;
    else
      p->ready_mask = pinmask;
    // Release whichever lines we can
    *AT91C_PIOA_ODR = p->ready_mask;
    return 1;
  }
  return -1;
}

// Initialise the module
void
i2c_init(void)
{
  int i;
  int istate;
  U32 dummy; 
  for (i = 0; i < I2C_N_PORTS; i++) {
    i2c_ports[i] = NULL;
  }
  
  istate = interrupts_get_and_disable();
  
  /* Set up Timer Counter 0 to drive standard speed i2c */
  *AT91C_PMC_PCER = (1 << AT91C_ID_TC0);    /* Power enable */
    
  *AT91C_TC0_CCR = AT91C_TC_CLKDIS; /* Disable */
  *AT91C_TC0_IDR = ~0;
  dummy = *AT91C_TC0_SR;
  *AT91C_TC0_CMR = AT91C_TC_CLKS_TIMER_DIV1_CLOCK|AT91C_TC_CPCTRG; /* MCLK/2, RC compare trigger */
  *AT91C_TC0_RC = ((CLOCK_FREQUENCY/2)/(2 * I2C_CLOCK))/1;
  *AT91C_TC0_IER = AT91C_TC_CPCS;
  aic_mask_off(AT91C_ID_TC0);
  aic_set_vector(AT91C_ID_TC0, AIC_INT_LEVEL_NORMAL, (int)i2c_timer_isr_entry);
  aic_mask_on(AT91C_ID_TC0);
  /* Setup timer counter 2 to drive high speed i2c */
  *AT91C_PMC_PCER = (1 << AT91C_ID_TC2);    /* Power enable */
  *AT91C_TC2_CCR = AT91C_TC_CLKDIS; /* Disable */
  *AT91C_TC2_IDR = ~0;
  dummy = *AT91C_TC0_SR;
  *AT91C_TC2_CMR = AT91C_TC_CLKS_TIMER_DIV1_CLOCK|AT91C_TC_CPCTRG; /* MCLK/2, RC compare trigger */
  *AT91C_TC2_RC = ((CLOCK_FREQUENCY/2)/(2 * I2C_HS_CLOCK))/1;
  
  if(istate)
    interrupts_enable();
}


// Is the port busy?
int
i2c_status(int port)
{
  i2c_port *p;
  if(port < 0 || port >= I2C_N_PORTS || !i2c_ports[port])
    return I2C_ERR_INVALID_PORT;
  if ((i2c_port_busy & (1 << port)) != 0) return I2C_ERR_BUSY;
  p = i2c_ports[port];
  if (p->state == I2C_COMPLETE) return 0;
  // only now is it safe to test the bus
  if ((*AT91C_PIOA_PDSR & (p->ready_mask)) != (p->ready_mask))
    return I2C_ERR_BUS_BUSY;
  return 0;
}

/* Start a transaction. 
 */
int
i2c_start(int port, 
          U32 address, 
          U8 *write_data,
          int write_len,
          int read_len)
{ 
  i2c_port *p;
  struct i2c_partial_transaction *pt;
  U8 *data;
  int status = i2c_status(port);
  if (status < 0) return status;
  // check buffer size
  if (read_len > I2C_BUF_SIZE) return I2C_ERR_INVALID_LENGTH;
  if (write_len > I2C_BUF_SIZE) return I2C_ERR_INVALID_LENGTH;   
  // must have some data to transfer
  if (read_len + write_len <= 0) return I2C_ERR_INVALID_LENGTH;
  p = i2c_ports[port];
  pt = p->partial_transaction;
  p->current_pt = pt;
  data = p->buffer;
  
  // process the write data (if any)
  if (write_len > 0){
    *data++ = address; // This is a write
    pt->nbits = (write_len + 1)*8;
    // copy the write data
    memcpy(data, write_data, write_len);
    data += write_len;
    pt->data = p->buffer;
    pt->state = I2C_NEWSTART;
    // We add an extra stop for the odd Lego i2c sensor, but only on a read
    if (read_len > 0 && p->lego_mode){
      pt++;
      pt->state = I2C_STOP1;
    }
    pt++;
  }
  // now add the read transaction (if any)
  if (read_len > 0)
  {
    // first we have to write the device address
    pt->state = (data != p->buffer ? I2C_NEWRESTART : I2C_NEWSTART);
    pt->data = data;
    *data++ = address |  1; // this is a read
    pt->nbits = 8;
    pt++;
    // now we have the read
    pt->state = I2C_NEWREAD;
    pt->data = p->buffer;
    pt->nbits = read_len*8;
    pt++;
  }
  // define what happens at the end of the operation
  if (p->lego_mode)
    pt->state = (read_len > 0 ? I2C_ENDLEGO1 : I2C_ENDSTOP1);
  else
    pt->state = (p->no_release ? I2C_END : I2C_ENDSTOP1);
  // We save the number of bytes to read for completion
  p->read_len = read_len;
  // Start the transaction
  i2c_port_busy |= 1 << port;
  p->state = I2C_BEGIN;
  if (!p->always_active)
    build_active_list();
  if (p->high_speed)
  {
    *AT91C_TC2_CCR = AT91C_TC_CLKEN; /* Enable */
    *AT91C_TC2_CCR = AT91C_TC_SWTRG; /* Software trigger */
    while(p->state != I2C_COMPLETE)
    {
      i2c_doio(p);
      while(!(*AT91C_TC2_SR & AT91C_TC_CPCS)) ;
    }
    *AT91C_TC2_CCR = AT91C_TC_CLKDIS; /* Enable */
  }
  return 0;
}

// Check for the operation to be complete and return and read data.
int
i2c_complete(int port,
             U8 *data,
             U32 nbytes)
{
  i2c_port *p;
  int status = i2c_status(port);
  if (status < 0) return status;
  p = i2c_ports[port];
  if (!p->always_active) {
    p->state = I2C_IDLE;
    build_active_list();
  }
  else
    p->state = I2C_ACTIVEIDLE;
  if (p->fault)
    return I2C_ERR_FAULT;
  if (nbytes > I2C_BUF_SIZE) return -4;
  if (nbytes > p->read_len) nbytes = p->read_len;
  if (data) {
    memcpy(data, p->buffer, nbytes);
  }
  return nbytes;
}

int
i2c_event_check(int filter)
{
  // get I/O complete status
  int ret = ~i2c_port_busy & filter & IO_COMPLETE_MASK;
  // do we need bus status?
  if (filter & BUS_FREE_MASK)
  {
    int port;
    int bit = 1 << BUS_FREE_SHIFT;
    for(port = 0; port < I2C_N_PORTS; port++, bit <<= 1)
      if (filter & bit)
      {
        i2c_port *p = i2c_ports[port];
        if ((*AT91C_PIOA_PDSR & (p->ready_mask)) == (p->ready_mask))
          ret |= bit;
      }
  }
  return ret;
}
