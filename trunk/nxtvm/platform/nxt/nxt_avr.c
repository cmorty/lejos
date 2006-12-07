#include "nxt_avr.h"

#include "systick.h"
#include <string.h>

#include "twi.h"



#define NXT_AVR_ADDRESS 1
#define NXT_AVR_N_OUTPUTS 4
#define NXT_AVR_N_INPUTS  4


const char avr_brainwash_string[] = "\xCC""Let's samba nxt arm in arm, (c)LEGO System A/S";



static struct {
  U8 power;
  U8 pwmFreq;
  S8 outPercent[NXT_AVR_N_OUTPUTS];
  U8 outputMode;
  U8 inputPower;
} io_to_avr;



static void nxt_avr_send(const U8 *buffer, U32 nBytes)
{
  twi_write(NXT_AVR_ADDRESS,0,0,buffer,nBytes);
}

static void nxt_avr_read(U8 *buffer, U32 nBytes)
{
  twi_read(NXT_AVR_ADDRESS,0,0,buffer,nBytes);
}

static void nxt_avr_send_io(void)
{
  // NB we use a marshalling area to get around any ARM-vs-AVR packing issues
  static U8 x[4 + NXT_AVR_N_OUTPUTS];
  U8 *p = x;
  int i;
  
  *p = io_to_avr.power;   p++;
  *p = io_to_avr.pwmFreq; p++;  
  for(i = 0; i < NXT_AVR_N_OUTPUTS; i++) {
    *p = io_to_avr.outPercent[i];
    p++;
  }
  *p = io_to_avr.outputMode; p++;
  *p = io_to_avr.inputPower;
  
  nxt_avr_send(x,sizeof(x));
}

void nxt_avr_power_down(void)
{
  io_to_avr.power = 0x5a;
  io_to_avr.pwmFreq = 0x00;
  nxt_avr_send_io();
}


void nxt_avr_firmware_update_mode(void)
{
  io_to_avr.power = 0xA5;
  io_to_avr.pwmFreq = 0x5A;
  nxt_avr_send_io();
}

void nxt_avr_brainwash(void)
{
  nxt_avr_send(avr_brainwash_string,strlen(avr_brainwash_string));
}


static struct {
  U16 adcVal[NXT_AVR_N_INPUTS];
  U16 buttons;
  U16 batteryAA;
  U16 batterymV;
  U16 avrFwVerMajor;
  U16 avrFwVerMinor;  
} io_from_avr;


static  U16 Unpack16(const U8 *x)
{
  U16 retval;
  retval =  (((U16)(x[0])) & 0xff) |
          ((((U16)(x[1])) << 8) & 0xff00);
  return retval;
}

static void nxt_avr_get_io(void)
{
  static U8 x[(2 * NXT_AVR_N_INPUTS)+ 5];
  U8 *p = &x[1];
  U16 buttonsVal;
  U32 voltageVal;
  int i;
  
  nxt_avr_read(x,sizeof(x));
  
  // Marshall
  for(i = 0; i < NXT_AVR_N_INPUTS; i++){
    io_from_avr.adcVal[i] = Unpack16(p);
    p+=2;
  }
  
  buttonsVal = Unpack16(p);
  p+=2;
  
  
  io_from_avr.buttons = 0;
  
  if(buttonsVal > 1023) {
    io_from_avr.buttons |= 1;
    buttonsVal -= 0x7ff;
  }
  
  if(buttonsVal > 720)
    io_from_avr.buttons |= 0x08;
  else if(buttonsVal > 270)
    io_from_avr.buttons |= 0x04;
  else if(buttonsVal > 60)
    io_from_avr.buttons |= 0x02;

  
  
  
  voltageVal = Unpack16(p);
  
  io_from_avr.batteryAA = (voltageVal & 0x8000) ? 1 : 0;
  io_from_avr.avrFwVerMajor = (voltageVal >>13) & 3;
  io_from_avr.avrFwVerMinor = (voltageVal >>10) & 7;
  
    
  // Figure out voltage
  // The units are 13.848 mV per bit.
  // To prevent fp, we substitute 13.848 with 14180/1024
  
  voltageVal &= 0x3ff; // Toss unwanted bits.
  voltageVal *= 14180;
  voltageVal >>= 10;
  io_from_avr.batterymV = voltageVal;
  
}

int nxt_avr_init(void)
{
  twi_init();
  systick_wait_ms(10);
  //nxt_avr_brainwash();
  systick_wait_ms(200);
  
  return 1;
}

void nxt_avr_update(void)
{
  nxt_avr_get_io();
}

U32 buttons_get(void)
{
  return io_from_avr.buttons;
}

U32 battery_voltage(void)
{
  return io_from_avr.batterymV;
}

U32 sensor_adc(U32 n)
{
  if(n < 4)
    return io_from_avr.adcVal[n];
  else
    return 0;
}

void nxt_avr_test_loop(void)
{
  while(1){
    nxt_avr_get_io();
  }
}