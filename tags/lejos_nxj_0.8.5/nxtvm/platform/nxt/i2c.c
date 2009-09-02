
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
#define I2C_MAX_PARTIAL_TRANSACTIONS 5
#define I2C_N_PORTS 4
#define I2C_BUF_SIZE 32
#define I2C_ADDRESS_SIZE 4

// Options
#define I2C_LEGO_MODE 1
#define I2C_ALWAYS_ACTIVE 2
#define I2C_NO_RELEASE 4

// Delay used when in Lego mode
#define I2C_LEGO_DELAY 5

// A partial transaction has the following form:
// 1. It has a transaction start state, to indicate the type of transaction
// 2. It has an optional data pointer

// A transaction has the following form:
// 1. One to 5 partial transactions.

// Some examples:
//
//  Transaction to write a single byte to an address will have:
//  partial transaction:
//  0: Start. Tx 1 byte address
//  1: Date send the 1 byte of data 
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
//  0: Start. Send address + internal address.
//  1: Send data bytes.
//  2: Stop 
//
//  Transaction to read some bytes from a particular internal address at an address:
//
//  0: Start. Send address + internal address.
//  1: Restart. Send address.
//  2: Read data.
//  3. Stop
//
// Note: It appears that the Lego ultrasonic sensor needs a 
// stop and an extra clock before the restart.
//
// Additional changes made by Andy Shaw...
//
// Port 4 on the nxt is a little odd. It has extra hardware to implement
// an RS485 interface. However this interacts with the digital I/O lines
// which means that the original open collector driver used in this code
// did not work. The code now uses a combination of open collector drive
// on the clock lines (with pull up resistors enabled), plus a fully
// driven ineterface on the data lines. This differs from the Lego
// firmware which uses a fully driven clock inetrface. However doing so
// means that it is hard (or impossible), to operate with the devices
// that make use of clock stretching. It is hoped that the compromise
// implemented here will work with all devices.
//
// Re-worked by Andy Shaw 02/2009
//
// In attempt to make the code work with a wider range of sensors I've
// reworked it to bring it more into line with the i2c spec and the
// Lego implementation. There are now more partial transactions and
// there is a mode setting to allow standard mode or Lego mode operation.
// In addition tests showed that the i2c code was using up to 40% of the
// available cpu. To reduce this teh state machine has been simplified
// and modified to require only 2 states per clock instead of 4 (and thus
// allowing the use of a lower interrupt rate). We now track the usage of
// the i2c system. When no ports are active the clock is disabled.
// Finally the overall i2c operation has been split into 3 stages...
// 1. Start the transaction.
// 2. Wait for the transaction to complete.
// 3. Read the results.


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
  I2C_RELEASE,
  I2C_BEGIN,
  I2C_NEWSTART,
  I2C_NEWRESTART,
  I2C_NEWREAD,
  I2C_NEWWRITE,
  I2C_RESTART1,
  I2C_START1,
  I2C_START2,
  I2C_DELAY,
  I2C_RXDATA1,
  I2C_RXDATA2,
  I2C_RXENDACK1,
  I2C_RXENDACK2,
  I2C_RXENDACK3,
  I2C_RXACK1,
  I2C_RXACK2,
  I2C_TXDATA1,
  I2C_TXDATA2,
  I2C_TXACK1,
  I2C_TXACK2,
  I2C_LEGOEND,
  I2C_ENDNORELEASE,
  I2C_ENDRELEASE1,
  I2C_ENDRELEASE2,
  I2C_ENDRELEASE3,
  I2C_LEGOSTOP1,
  I2C_LEGOSTOP2,
  I2C_LEGOSTOP3,
} i2c_port_state;

typedef struct {
  U32 scl_pin;
  U32 sda_pin;
  U8  addr_int[I2C_ADDRESS_SIZE+1]; /* Device address with internal address */
  U8  addr;	                    /* Just device address */
  U8  buffer[I2C_BUF_SIZE];
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
  U16 nbytes;
} i2c_port;

static i2c_port *i2c_ports[I2C_N_PORTS];
// We maintain an active port list of those ports that currently need
// attention at interrupt time. This list is double buffered and is built
// dynamically as required.
static i2c_port *i2c_active[2][I2C_N_PORTS+1];
static i2c_port **active_list = i2c_active[0];

// The I2C state machines are pumped by a timer interrupt
// running at 2x the bit speed. This state machine has been
// optimized to minimize the time spent processing during
// an interrupt.


extern void i2c_timer_isr_entry(void);

void
i2c_timer_isr_C(void)
{
  U32 dummy = *AT91C_TC0_SR;
  i2c_port **ap;
  i2c_port *p;
//*AT91C_PIOA_SODR = 1<<29;
  for(ap = active_list; (p = *ap); ap++){
    switch (p->state) {
    default:
    case I2C_DISABLED:
    case I2C_IDLE:		// Not in a transaction
    case I2C_ACTIVEIDLE:	// Not in a transaction but active
    case I2C_COMPLETE:          // Transaction completed
      continue;
      break;
    case I2C_RELEASE:
      // Release the bus completely
      *AT91C_PIOA_ODR = p->sda_pin|p->scl_pin;
      *AT91C_PIOA_SODR = p->sda_pin|p->scl_pin;;
      p->state = I2C_COMPLETE;
      break;
    case I2C_BEGIN:		
      // Start new transaction
      *AT91C_PIOA_OER |= p->sda_pin|p->scl_pin;
      p->fault = 0;
      p->state = p->current_pt->state;
      break;
    case I2C_NEWSTART:		
      // Start the current partial transaction
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = *(p->data);
      *AT91C_PIOA_SODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      p->state = I2C_START1;
      break;
    case I2C_NEWRESTART:		
      // restart a new partial transaction
      // Take the clock low
      *AT91C_PIOA_CODR = p->scl_pin;
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = *(p->data);
      *AT91C_PIOA_SODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      p->state = I2C_START1;
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
        p->state = I2C_RXDATA2;
      else
        p->delay--;
      break;
    case I2C_NEWWRITE:		
      // Start the write partial transaction
      p->data = p->current_pt->data;
      p->nbits = p->current_pt->nbits;
      p->bits = *(p->data);
      *AT91C_PIOA_OER = p->sda_pin;
      p->state = I2C_TXDATA1;
      // FALLTHROUGH
    case I2C_TXDATA1:
      if(*AT91C_PIOA_PDSR & p->scl_pin)
      {
        // Take SCL low
        *AT91C_PIOA_CODR = p->scl_pin;
        p->nbits--;
        if(p->bits & 0x80)
          *AT91C_PIOA_SODR = p->sda_pin;
        else
          *AT91C_PIOA_CODR = p->sda_pin;
        *AT91C_PIOA_OER = p->sda_pin;
        p->bits <<= 1;
        p->state = I2C_TXDATA2;
      }
      break;
    case I2C_TXDATA2:
      // Take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      if((p->nbits & 7) == 0) 
          p->state = I2C_TXACK1;
        
        else
          p->state = I2C_TXDATA1;
      break;
    case I2C_TXACK1:
      // Wait for high pulse width
      // If someone else is not holding the pin down, then advance
      if(*AT91C_PIOA_PDSR & p->scl_pin)
      {
        // Take SCL low
        *AT91C_PIOA_CODR = p->scl_pin;
        // release the data line
        *AT91C_PIOA_ODR = p->sda_pin;
        p->state = I2C_TXACK2;
      }
      break;
    case I2C_TXACK2:
      // Take SCL High
      *AT91C_PIOA_SODR = p->scl_pin;
      if(*AT91C_PIOA_PDSR & p->sda_pin) {
        p->fault=1;
        p->state = I2C_ENDRELEASE1;
      }
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
      if (p->lego_mode) {
        // Take SCL Low
        *AT91C_PIOA_CODR = p->scl_pin;
        // get ready to read
        *AT91C_PIOA_ODR = p->sda_pin;
        p->state = I2C_DELAY;
        p->delay = I2C_LEGO_DELAY;
        break;
      }
      else
        p->state = I2C_RXDATA1;
      // Fall through
    case I2C_RXDATA1:
      if(*AT91C_PIOA_PDSR & p->scl_pin){
        // Take SCL Low
        *AT91C_PIOA_CODR = p->scl_pin;
        // get ready to read
        *AT91C_PIOA_ODR = p->sda_pin;
        p->state = I2C_RXDATA2;
      }
      break;
    case I2C_RXDATA2:
      // Take SCL High
      *AT91C_PIOA_SODR = p->scl_pin;
      // Receive a bit.
      p->bits <<= 1;
      if(*AT91C_PIOA_PDSR & p->sda_pin)
        p->bits |= 1;
      p->nbits--;
      if((p->nbits & 7) == 0){
        *(p->data) = p->bits;
        if (p->nbits)
          p->state = I2C_RXACK1;
        else
          p->state = I2C_RXENDACK1;
      }
      else
        p->state = I2C_RXDATA1;
      break;
    case I2C_RXACK1:
      if(*AT91C_PIOA_PDSR & p->scl_pin)
      {
        // Take SCL low
        *AT91C_PIOA_CODR = p->scl_pin;
        *AT91C_PIOA_CODR = p->sda_pin;
        *AT91C_PIOA_OER = p->sda_pin;
        p->state = I2C_RXACK2;
      }
      break;
    case I2C_RXACK2:
      // Clock high
      *AT91C_PIOA_SODR = p->scl_pin;
      // Move on to next byte
      p->data++;
      p->bits = 0;
      p->state = I2C_RXDATA1;
      break;
    case I2C_RXENDACK1:
      if(*AT91C_PIOA_PDSR & p->scl_pin)
      {
        // Take SCL low
        *AT91C_PIOA_CODR = p->scl_pin;
        *AT91C_PIOA_SODR = p->sda_pin;
        *AT91C_PIOA_OER = p->sda_pin;
        p->state = I2C_RXENDACK2;
      }
      break;
    case I2C_RXENDACK2:
      // Clock high data is already high
      *AT91C_PIOA_SODR = p->scl_pin;
      p->current_pt++;
      p->state = p->current_pt->state;
      break;
    case I2C_LEGOEND:
      // Lego mode end case, does not issue stop, but does release the bus
      // Clock low data is already high
      *AT91C_PIOA_CODR = p->scl_pin;
      p->state = I2C_ENDRELEASE2;
      break;
    case I2C_ENDNORELEASE:
      // End the transaction but hold onto the bus, keeping the clock low
      // Clock low and keep it active
      *AT91C_PIOA_CODR = p->scl_pin;
      *AT91C_PIOA_ODR = p->sda_pin;
      *AT91C_PIOA_SODR = p->sda_pin;
      p->state = I2C_COMPLETE;
      break;
    case I2C_ENDRELEASE1:
      // Standard end case issue stop and release the bus
      // SCL is high, take it low
      *AT91C_PIOA_CODR = p->scl_pin;
      *AT91C_PIOA_CODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      p->state = I2C_ENDRELEASE2;
      break;
    case I2C_ENDRELEASE2:
      // Take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      p->state = I2C_ENDRELEASE3;
      break;  
    case I2C_ENDRELEASE3:
      // Take SDA pin high
      *AT91C_PIOA_SODR = p->sda_pin;
      p->state = I2C_RELEASE;
      break;
    case I2C_LEGOSTOP1:
      // Special case stop used mid read in lego mode
      // SCL is high, take it low
      *AT91C_PIOA_CODR = p->scl_pin;
      *AT91C_PIOA_CODR = p->sda_pin;
      *AT91C_PIOA_OER = p->sda_pin;
      p->state = I2C_LEGOSTOP2;
      break;
    case I2C_LEGOSTOP2:
      // Take SCL high
      *AT91C_PIOA_SODR = p->scl_pin;
      p->state = I2C_LEGOSTOP3;
      break;  
    case I2C_LEGOSTOP3:
      // Take SDA pin high
      *AT91C_PIOA_SODR = p->sda_pin;
      p->current_pt++;
      p->state = p->current_pt->state;
      break;
    }
  }
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
    if (i2c_ports[i] && i2c_ports[i]->state > I2C_IDLE)
      *ap++ = i2c_ports[i];
  *ap = NULL;
  // If there are no active ports then we can just disable things
  if (*new_list == NULL)
    *AT91C_TC0_CCR = AT91C_TC_CLKDIS;
  else
  {
    // if the clock is disabled enable it
    //if ((*AT91C_TC0_SR & AT91C_TC_CLKSTA) == 0)
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
    system_free((byte *)p);
    i2c_ports[port] = NULL;
    build_active_list();
    sp_reset(port);
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
    /* Set clock pin for output, open collector driver, with
     * pullups enabled. Set data to be enabled for output with
     * pullups disabled.
     */
    *AT91C_PIOA_SODR  = pinmask;
    *AT91C_PIOA_OER   = pinmask;
    *AT91C_PIOA_MDER  = p->scl_pin;
    *AT91C_PIOA_PPUDR = p->sda_pin;
    *AT91C_PIOA_PPUER = p->scl_pin;
    /* If we are always active, we never drop below the ACTIVEIDLE state */
    p->lego_mode = ((mode & I2C_LEGO_MODE) ? 1 : 0);
    p->no_release = ((mode & I2C_NO_RELEASE) ? 1 : 0);
    p->always_active = ((mode & I2C_ALWAYS_ACTIVE) ? 1 : 0);
    if (p->always_active) {
      p->state = I2C_ACTIVEIDLE;
      build_active_list();
    }
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
  
  /* Set up Timer Counter 0 */
  *AT91C_PMC_PCER = (1 << AT91C_ID_TC0);    /* Power enable */
    
  *AT91C_TC0_CCR = AT91C_TC_CLKDIS; /* Disable */
  *AT91C_TC0_IDR = ~0;
  dummy = *AT91C_TC0_SR;
  *AT91C_TC0_CMR = AT91C_TC_CLKS_TIMER_DIV1_CLOCK|AT91C_TC_CPCTRG; /* MCLK/2, RC compare trigger */
  *AT91C_TC0_RC = (CLOCK_FREQUENCY/2)/(2 * I2C_CLOCK);
  *AT91C_TC0_IER = AT91C_TC_CPCS;
  aic_mask_off(AT91C_ID_TC0);
  aic_set_vector(AT91C_ID_TC0, AIC_INT_LEVEL_NORMAL, (int)i2c_timer_isr_entry);
  aic_mask_on(AT91C_ID_TC0);
  
  if(istate)
    interrupts_enable();
}


// Is the port busy?
int
i2c_busy(int port)
{
  if(port >= 0 && (port < I2C_N_PORTS) && i2c_ports[port])
    return (i2c_ports[port]->state > I2C_COMPLETE);
  return 0;
}

/* Start a transaction. 
 */
int
i2c_start(int port, 
          U32 address, 
          int internal_address, 
          int n_internal_address_bytes, 
          U8 *data, 
          U32 nbytes,
          int write)
{ 
  i2c_port *p;
  struct i2c_partial_transaction *pt;
  if(port < 0 || port >= I2C_N_PORTS || !i2c_ports[port])
    return -1;
    
  if(i2c_busy(port))
    return -2;
  if (nbytes > I2C_BUF_SIZE) return -4;   
  if (n_internal_address_bytes > I2C_ADDRESS_SIZE) return -5;
  p = i2c_ports[port];
  pt = p->partial_transaction;
  p->current_pt = pt;
  
  
  if(n_internal_address_bytes > 0){
    int addrlen = n_internal_address_bytes;
    // Set up command to write the internal address to the device
    p->addr_int[0] = (address << 1); // This is a write
    pt->nbits = (n_internal_address_bytes + 1)*8;
    // copy internal address bytes, high bytes first
    while (addrlen > 0)
    {
      p->addr_int[addrlen--] = internal_address;
      internal_address >>= 8;
    }

    pt->data = p->addr_int;
    pt->state = I2C_NEWSTART;
    // We add an extra stop for the odd Lego i2c sensor, but only on a read
    if (!write && p->lego_mode){
      pt++;
      pt->state = I2C_LEGOSTOP1;
    }
    pt++;
  }

  if(n_internal_address_bytes == 0 || !write){  
    // Set up the next partial transaction: start/restart and address
    pt->state = (n_internal_address_bytes ? I2C_NEWRESTART : I2C_NEWSTART);
    p->addr = (address << 1) | (write ? 0 : 1);
    pt->data = &p->addr;
    pt->nbits = 1*8;
  
    pt++;
  }
  
  // Set up the data transfer partial transaction
  if (write) {
    pt->state = I2C_NEWWRITE;
    memcpy(p->buffer, data, nbytes);
  }
  else
    pt->state = I2C_NEWREAD;
  pt->data = p->buffer;
  pt->nbits = nbytes*8;
  // Sort out the final end state transaction
  pt++;
  if (p->lego_mode)
    pt->state = (write ? I2C_ENDRELEASE1 : I2C_LEGOEND);
  else
    pt->state = (p->no_release ? I2C_ENDNORELEASE : I2C_ENDRELEASE1);

  // We save the number of bytes for use for complete
  p->nbytes = nbytes;
  // Start the transaction
  p->state = I2C_BEGIN;
  if (!p->always_active)
    build_active_list();
  
  return 0;
}

// Check for the operation to be complete and return and read data.
int
i2c_complete(int port,
             U8 *data,
             U32 nbytes)
{
  i2c_port *p;
  if(port < 0 || port >= I2C_N_PORTS || !i2c_ports[port])
    return -1;
    
  if(i2c_busy(port))
    return -2;
  p = i2c_ports[port];
  if (p->fault)
    return -3;
  if (nbytes > I2C_BUF_SIZE) return -4;
  if (nbytes > p->nbytes) nbytes = p->nbytes;
  memcpy(data, p->buffer, nbytes);
  if (!p->always_active) {
    p->state = I2C_IDLE;
    build_active_list();
  }
  else
    p->state = I2C_ACTIVEIDLE;
  return nbytes;
}
