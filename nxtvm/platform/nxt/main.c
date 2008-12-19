
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
#include "debug.h"
#include "sensors.h"
#include "platform_hooks.h"
//#include "java_binary.h"

#include "nxt_avr.h"
#include "nxt_lcd.h"
#include "i2c.h"
#include "nxt_motors.h"

#include "lejos_nxt.h"

#include "display.h"
#include "sound.h"
#include "bt.h"
#include "udp.h"
#include "flashprog.h"
#include "hs.h"
#include "debug.h"

#include <string.h>

extern U32 __free_ram_start__;
extern U32 __free_ram_end__;
extern U32 __extra_ram_start__;
extern U32 __extra_ram_end__;


byte *region;
Thread *bootThread;
static unsigned int menu_address;
static unsigned int menu_length_address = 0x00114ffc;
static unsigned int menu_address_address = 0x00114ff8;

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

#if USE_VARSTAT
void disp_varstat( VarStat* vs)
{
  const char* sep = ",";
  display_unsigned( vs->min, 0);
  display_string( sep);
  display_unsigned( vs->max, 0);
  display_string( sep);
  display_unsigned( vs->sum, 0);
  display_string( sep);
  display_unsigned( vs->count, 0);
}
#endif

void
handle_uncaught_exception(Object * exception,
			  const Thread * thread,
			  const MethodRecord * methodRecord,
			  const MethodRecord * rootMethod, byte * pc)
{
  sound_freq(100,500, 80); // buzz
  display_clear(0);
  display_goto_xy(0, 0);
  display_string("Java Exception:");
  display_goto_xy(0, 1);
  display_string("Class: ");
  display_int(get_class_index(exception), 0);
  display_goto_xy(0, 2);
  display_string("Method: ");
  display_int(methodRecord - get_method_table(get_class_record(0)), 0);
  display_goto_xy(0, 3);
  display_string("PC: ");
  display_int(pc - get_binary_base(), 0);
  if( get_class_index(exception) == JAVA_LANG_OUTOFMEMORYERROR)
  {
    display_goto_xy(0, 3);
    display_string("Size: ");
    display_int(failed_alloc_size << 1, 0);
#if USE_VARSTAT
    display_goto_xy(0, 4);
    disp_varstat( &mem_freeblk_vs);
    display_goto_xy(0, 6);
    disp_varstat( &mem_usedblk_vs);
#endif
  }
  display_update();
  wait_for_power_down_signal();
}

void
switch_thread_hook()
{
  int b = buttons_get();

  // Check for ENTER and ESCAPE pressed
  if (b == 9) {
    if (debug_user_interrupt()) return;
    // Shut down power immediately
    while (1) {
      nxt_avr_power_down();
    }
  }
}

void
assert_hook(boolean aCond, int aCode)
{
  if (aCond) return;
  display_clear(0);
  display_goto_xy(0,0);
  display_string("Assert failed");
  display_goto_xy(0,1);
  display_string("Code: ");
  display_goto_xy(6,1);
  display_int(aCode,0);
  display_update();
  while(1); // Hang
}


void
run(int jsize)
{
  byte *ram_end = (byte *) (&__free_ram_end__);
  byte *ram_start = (byte *) (&__free_ram_start__);

  init_poller();
  init_debug();

  //printf("Initializing Binary\n");

#if EXECUTE_FROM_FLASH
  {
    MasterRecord *mrec = get_master_record();
    int staticSize = mrec->staticStateLength;
    int statusSize = (mrec->lastClass + 1) * sizeof( classStatusBase[0]);

    staticSize = (staticSize + 3) & ~(3);
    statusSize = (statusSize + 3) & ~(3);

    ram_end -= staticSize;
    classStaticStateBase = ram_end;

    ram_end -= statusSize;
    classStatusBase = ram_end;

    memset( classStatusBase, 0, statusSize);
  }
#endif

  // Initialize binary image state
  initialize_binary();

  //printf("Initializing memory\n");

  // Initialize memory
  {
    //int size;

#if ! EXECUTE_FROM_FLASH
    // Skip java binary if it is an top of ram
    if (jsize > 0)
      ram_end -= (jsize + 4);
#endif

    //size = ((unsigned) ram_end) - ((unsigned) ram_start);

    memory_init();

    region = ram_start;
    memory_add_region(region, (byte *) ram_end);

    /*Add extra RAM if available */
    //ram_end = (byte *) (&__extra_ram_end__);
    //ram_start = (byte *) (&__extra_ram_start__);
    //size = ((unsigned) ram_end) - ((unsigned) ram_start);

    //if(size > 0)
    //  memory_add_region(ram_start, ram_end);
  }

  //printf("Initializing exceptions\n");

  // Initialize exceptions
  init_exceptions();

  // Create the boot thread (bootThread is a special global)
  bootThread = (Thread *) new_object_for_class(JAVA_LANG_THREAD);

  init_threads();
  if (init_thread(bootThread) != EXEC_CONTINUE) {
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
nxt_main(int bin, int size)
{
  int jsize = 0;
  const char *binary; 
  unsigned *temp;

#if EXECUTE_FROM_FLASH
  if (bin > 0) {
    size = (size + 3) & ~3;
  	binary = (char *) bin;
  	jsize = size - 4;
  } else {
    // Execute flash menu

    bin = (unsigned *) menu_address;
    size = *((unsigned *) menu_length_address);
    size = (size + 3) & ~3;
  	binary = (char *) bin;
    jsize = size - 4;
  }
#else
  if (bin > 0) {
    size = (size + 3) & ~3;
  	temp = ((unsigned *) (&__free_ram_end__)) - (size >> 2);
  	memcpy(temp,bin,size);
  	binary = (char *) temp;
  	jsize = size - 4;
  } else {
    // Execute flash menu

    bin = (unsigned *) menu_address;
    size = *((unsigned *) menu_length_address);
    size = (size + 3) & ~3;
    temp = ((unsigned *) (&__free_ram_end__)) - (size >> 2);   
    memcpy(temp,bin,size);
    binary = ((char *) temp);
    jsize = size - 4;
  }
#endif
  
  // reset all motors, sensors and devices

  init_sensors();
  nxt_motor_reset_all();
  bt_reset(); 
  hs_disable();
  udp_disable();
  display_clear(1);
  display_set_auto_update(1);
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

#if 0
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
  nxt_avr_set_input_power(0,1);
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
#endif

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
  nxt_motor_init();
  i2c_init();
  bt_init();
  hs_init();
  udp_init();
  systick_wait_ms(1000); // wait for LCD to stabilize
  display_init();
  show_splash(3000); 
  
  // Get the address of the menu
  menu_address = 0x00100000 + *((unsigned *) menu_address_address); 
 
  gNextProgram = 0;
  do 
  {
  	int next = gNextProgram;
  	gNextProgram = 0;
  	gProgramExecutions++;
  	nxt_main(next, gNextProgramSize);
  }
  while (true);

  while (1) {
    nxt_avr_power_down();
  }
  
}
