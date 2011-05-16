#include "nxt_motors.h"

#include "nxt_avr.h"
#include "aic.h"
#include "interrupts.h"
#include "AT91SAM7.h"

#define MA0 15
#define MA1 1
#define MB0 26
#define MB1 9
#define MC0 0
#define MC1 8

#define MOTOR_PIN_MASK 		((1 << MA0) | (1<<MA1) | (1<<MB0) | (1<<MB1) | (1<<MC0) | (1<<MC1))
#define MOTOR_INTERRUPT_PINS 	((1 << MA0) | (1<<MB0) | (1<<MC0))

// define to use full 720 counts per rev
//#define USE_FULL_DECODE

static struct motor_struct {
  int current_count;
  int speed_percent;
  U32 last;
} motor[NXT_N_MOTORS];

static U32 nxt_motor_initialised;
static U32 interrupts_this_period;

int
nxt_motor_get_count(U32 n)
{
  if (n < NXT_N_MOTORS)
    return motor[n].current_count;
  else
    return 0;
}

void
nxt_motor_set_count(U32 n, int count)
{
  if (n < NXT_N_MOTORS)
    motor[n].current_count = count;
}

void
nxt_motor_set_speed(U32 n, int speed_percent, int brake)
{
  if (n < NXT_N_MOTORS) {
    if (speed_percent > 100)
      speed_percent = 100;
    if (speed_percent < -100)
      speed_percent = -100;
    motor[n].speed_percent = speed_percent;
    nxt_avr_set_motor(n, speed_percent, brake);
  }
}

void nxt_motor_reset_all() 
{
  int i;
	
  for(i=0;i<NXT_N_MOTORS;i++)
  {
    nxt_motor_set_speed(i,0,1);
    nxt_motor_set_count(i,0);   
  }	
}

void
nxt_motor_1kHz_process(void)
{
  if (nxt_motor_initialised) {
    interrupts_this_period = 0;
    *AT91C_PIOA_IER = MOTOR_INTERRUPT_PINS;
  }

}
#ifdef USE_FULL_DECODE
/*
 * This table provides full quad decode giving 720 counts per rotation
static const S8 quad_lookup[4][4] = 
*/
  {{0, 1, -1, 0},
  {-1, 0, 0, 1},
  {1, 0, 0, -1},
  {0, -1, 1, 0}};
#else
/*
 * This version provides half decode giving 360 counts per rotation
 */
static const S8 quad_lookup[4][4] = 
  {{0, 1, 0, -1},
  {-1, 0, 1, 0},
  {0, 1, 0, -1},
  {-1, 0, 1, 0}};
#endif

void
nxt_motor_quad_decode(struct motor_struct *m, U32 value)
{
  if (value != m->last)
  {
    m->current_count += quad_lookup[m->last][value];
    m->last = value;
  }
}


extern void nxt_motor_isr_entry(void);

void
nxt_motor_isr_C(void)
{
  U32 i_state = interrupts_get_and_disable();

  U32 pinChanges = *AT91C_PIOA_ISR;	// Acknowledge change
  U32 currentPins = *AT91C_PIOA_PDSR;	// Read pins

  U32 pins;

  interrupts_this_period++;
  if (interrupts_this_period > 4) {
    *AT91C_PIOA_IDR = MOTOR_INTERRUPT_PINS;
    // Todo : tacho speed fault
  }


  /* Motor A */
  pins = ((currentPins >> MA0) & 1) | ((currentPins >> (MA1 - 1)) & 2);
  nxt_motor_quad_decode(&motor[0], pins);

  /* Motor B */
  pins = ((currentPins >> MB0) & 1) | ((currentPins >> (MB1 - 1)) & 2);
  nxt_motor_quad_decode(&motor[1], pins);

  /* Motor C */
  pins = ((currentPins >> MC0) & 1) | ((currentPins >> (MC1 - 1)) & 2);
  nxt_motor_quad_decode(&motor[2], pins);

  if (i_state)
    interrupts_enable();

}



void
nxt_motor_init(void)
{
  *AT91C_PMC_PCER = (1 << AT91C_ID_PIOA);	/* Power to the pins! */
  *AT91C_PIOA_IDR = ~0;
  *AT91C_PIOA_IFER = MOTOR_PIN_MASK;
  *AT91C_PIOA_PPUDR = MOTOR_PIN_MASK;
  *AT91C_PIOA_PER = MOTOR_PIN_MASK;
  *AT91C_PIOA_ODR = MOTOR_PIN_MASK;

  /* Enable ISR */
  aic_mask_off(AT91C_ID_PIOA);
  aic_set_vector(AT91C_ID_PIOA, AIC_INT_LEVEL_NORMAL,
		 nxt_motor_isr_entry);
  aic_mask_on(AT91C_ID_PIOA);

#ifdef USE_FULL_DECODE
  *AT91C_PIOA_IER = MOTOR_PIN_MASK;
#else
  *AT91C_PIOA_IER = MOTOR_INTERRUPT_PINS;
#endif

  nxt_motor_initialised = 1;
  U32 currentPins = *AT91C_PIOA_PDSR;	// Read pins
  motor[0].last = ((currentPins >> MA0) & 1) | ((currentPins >> (MA1 - 1)) & 2);
  motor[1].last = ((currentPins >> MB0) & 1) | ((currentPins >> (MB1 - 1)) & 2);
  motor[2].last = ((currentPins >> MC0) & 1) | ((currentPins >> (MC1 - 1)) & 2);


}
