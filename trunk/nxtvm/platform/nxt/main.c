
#include "mytypes.h"
#include "interrupts.h"
#include "aic.h"
#include "AT91SAM7.h"
#include "uart.h"
#include "systick.h"
#include "stdio.h"
#include "flashprog.h"
#include "nxt_avr.h"
#include "twi.h"
#include "types.h"
#include "constants.h"
#include "classes.h"
#include "threads.h"
#include "stack.h"
#include "specialclasses.h"
#include "specialsignatures.h"
#include "language.h"
#include "memory.h"
#include "interpreter.h"
#include "exceptions.h"
#include "trace.h"
#include "poll.h"
#include "sensors.h"
#include "platform_hooks.h"
#include "java_binary.h"

#include "nxt_avr.h"
#include "nxt_lcd.h"
#include "i2c.h"
#include "nxt_motors.h"

#include "lejos_nxt.h"

#include "display.h"
#include "sound.h"
#include "bt.h"

extern U32 __free_ram_start__;
extern U32 __free_ram_end__;
extern U32 __extra_ram_start__;
extern U32 __extra_ram_end__;


byte *region;
Thread *bootThread;

void
wait_for_power_down_signal()
{
  for (;;) {
    int b = buttons_get();

    // Check for ENTER and ESCAPE pressed
    if (b == 9) {
      // Shut down power immediately
      while (1) {
	nxt_avr_power_down();
      }
    }
  }
}

void
handle_uncaught_exception(Object * exception,
			  const Thread * thread,
			  const MethodRecord * methodRecord,
			  const MethodRecord * rootMethod, byte * pc)
{
  display_clear(0);
  display_goto_xy(0, 0);
  display_string("Java Exception:");
  display_goto_xy(0, 1);
  display_string("Class:");
  display_goto_xy(7, 1);
  display_int(get_class_index(exception), 0);
  display_goto_xy(0, 2);
  display_string("Method:");
  display_goto_xy(8, 2);
  display_int(methodRecord->signatureId, 0);
  display_update();
  wait_for_power_down_signal();
}

void
switch_thread_hook()
{
  int b = buttons_get();

  // Check for ENTER and ESCAPE pressed
  if (b == 9) {
    // Shut down power immediately
    while (1) {
      nxt_avr_power_down();
    }
  }
}

void
assert_hook(boolean aCond, int aCode)
{
}


void
run(int jsize)
{
  init_poller();

  //printf("Initializing Binary\n");

  // Initialize binary image state
  initialize_binary();

  //printf("Initializing memory\n");

  // Initialize memory
  {
    byte *ram_end = (byte *) (&__free_ram_end__);
    byte *ram_start = (byte *) (&__free_ram_start__);
    int size, i;

    // Skip java binary if it is an top of ram

    if (jsize > 0)
      ram_end -= (jsize + 4);
    size = ((unsigned) ram_end) - ((unsigned) ram_start);

    memory_init();

    region = ram_start;
    memory_add_region(region, (byte *) ram_end);

    /*Add extra RAM if available */
    ram_end = (byte *) (&__extra_ram_end__);
    ram_start = (byte *) (&__extra_ram_start__);
    size = ((unsigned) ram_end) - ((unsigned) ram_start);

    //if(size > 0)
    //  memory_add_region(ram_start, ram_end);
  }

  //printf("Initializing exceptions\n");

  // Initialize exceptions
  init_exceptions();

  // Create the boot thread (bootThread is a special global)
  bootThread = (Thread *) new_object_for_class(JAVA_LANG_THREAD);

  init_threads();
  if (!init_thread(bootThread)) {
    return;
  }
  //printf("Executing Interpreter\n");

  // Execute the bytecode interpreter
  set_program_number(0);

  engine();
  // Engine returns when all non-daemon threads are dead
}

/***************************************************************************
 * int nxt_main *--------------------------------------------------------------------------
 ***************************************************************************/
//int main (int argc, char *argv[])
int
nxt_main()
{
  int jsize = 0;
  char *binary = java_binary;
  unsigned *temp;

  if (__extra_ram_start__ != __extra_ram_end__) {
    // Samba RAM mode

    temp = ((unsigned *) (&__free_ram_end__)) - 1;
    jsize = *temp;
    binary = ((char *) temp) - jsize;
  }

  init_sensors();

  //       printf("Installing Binary\n");

  install_binary(binary);

  //      printf("Running\n");

  run(jsize);

  return 0;
}

const U8 splash_data[4 * 26] = {
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0xFF, 0xFF, 0xFF, 0xFF, 0xFE, 0xFC,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xA0, 0x40, 0xA0, 0x40,
  0xA0, 0x40, 0xA0, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
  0xF0, 0xF0, 0xF0, 0xF0, 0xF0, 0xF0, 0x00, 0x00, 0x00, 0x0A, 0x05, 0x0A, 0x05,
  0x0A, 0x05, 0x0A, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
  0x0F, 0x1F, 0x3F, 0x3F, 0x7F, 0x7F, 0xFC, 0xF8, 0xF0, 0xF0, 0xF0, 0xF0, 0xF0,
  0xF0, 0xF0, 0xF0, 0xF0, 0xF0, 0xF8, 0xFC, 0x7F, 0x7F, 0x3F, 0x3F, 0x1F, 0x0F,
};

void
show_splash(U32 milliseconds)
{
  display_clear(0);
  display_bitmap_copy(splash_data, 26, 4, 37, 1);

  display_goto_xy(6, 6);
  display_string("LEJOS");
  display_update();

  systick_wait_ms(milliseconds);
}



U32
free_stack(void)
{
  extern U32 __system_stack_bottom__;
  extern U32 __system_stack_top__;
  U32 *sp = &__system_stack_bottom__;
  U32 space = 0;

  while ((sp < &__system_stack_top__) && *sp == 0x6b617453) {
    sp++;
    space += 4;
  }
  return space;
}

void
xx_show(void)
{
  int iterator = 0;
  U32 buttons;
  U32 motor_mode = 0;
  int result;
  U8 distance;

  show_splash(3000);

  /* set up power for ultrasonic sensor on port 1 (port 0 here )*/  
  nxt_avr_set_input_power(0,2);
  i2c_enable(0);
                        
  while (1) {
    display_clear(0);

    if ((iterator & 15) == 0) {
      motor_mode = (iterator >> 4) & 3;
      switch (motor_mode) {

      case 0:
	nxt_motor_set_speed(2, -100, 0);
	break;
      case 1:
	nxt_motor_set_speed(2, 100, 0);
	break;
      case 2:
	nxt_motor_set_speed(2, 0, 0);
	break;
      case 3:
	nxt_motor_set_speed(2, 0, 1);
	break;
      }
    }
    iterator++;

    if ((iterator %10) < 5) {
      buttons = buttons_get();

      display_goto_xy(iterator & 7, 0);
      display_string("LEJOS NXT");

      display_goto_xy(0, 1);
      display_string("TIME ");
      display_unsigned(systick_get_ms(), 0);

      display_goto_xy(0, 2);
      display_string("BATTERY ");
      display_unsigned(battery_voltage(), 0);

      display_goto_xy(0, 3);
      display_string("BUTTONS ");
      if (buttons & 1)
	display_string("0 ");
      if (buttons & 2)
	display_string("1 ");
      if (buttons & 4)
	display_string("2 ");
      if (buttons & 8)
	display_string("3 ");

      display_goto_xy(0, 4);
      display_string("ADC ");
      display_unsigned(sensor_adc(0), 5);
      display_unsigned(sensor_adc(1), 5);
      display_goto_xy(0, 5);
      display_string("    ");
      display_unsigned(sensor_adc(2), 5);
      display_unsigned(sensor_adc(3), 5);
      
      i2c_start_transaction(0,1,0x42,1,&distance,1,0);
      systick_wait_ms(200);
      result = i2c_busy(0);

      display_goto_xy(0,6);
      display_string("DIST ");
      display_unsigned(distance,3);

      display_update();
      systick_wait_ms(500);
      
    } else {

      display_goto_xy(iterator & 7, 0);
      display_string("LEJOS NXT");

      display_goto_xy(0, 1);
      display_string("TIME ");
      display_unsigned(systick_get_ms(), 0);
      display_goto_xy(0, 2);
      display_string("Stack ");
      display_unsigned(free_stack(), 0);
      display_goto_xy(0, 3);
      switch (motor_mode) {
      case 0:
	display_string("MOTORS REV");
	break;
      case 1:
	display_string("MOTORS FWD");
	break;
      case 2:
	display_string("MOTORS COAST");
	break;
      case 3:
	display_string("MOTORS BRAKE");
	break;
      }

      display_goto_xy(1, 4);
      display_int(nxt_motor_get_count(0), 0);
      display_goto_xy(1, 5);
      display_int(nxt_motor_get_count(1), 0);
      display_goto_xy(1, 6);
      display_int(nxt_motor_get_count(2), 0);

      display_update();
      systick_wait_ms(500);
    }
  }
}

void
main(void)
{
  /* When we get here:
   * PLL and flash have been initialised and
   * interrupts are off, but the AIC has not been initialised.
   */
  aic_initialise();
  interrupts_enable();
  systick_init();
  sound_init();
  nxt_avr_init();
  display_init();
  nxt_motor_init();
  i2c_init();
  bt_init();
    
  //xx_show();

  show_splash(3000);    
  display_clear(1);
  nxt_main();
  systick_wait_ms(5000);

  while (1) {
    nxt_avr_power_down();
  }
  
}
