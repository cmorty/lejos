
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
extern const U32 menu_address;
extern const U32 menu_length;

unsigned int gNextProgramSize;
byte *gNextProgram;
unsigned int gProgramExecutions=0;

byte *region;

void
shutdown()
{
  nxt_lcd_enable(false);
  for(;;)
    nxt_avr_power_down();
}

/**
 * Wait for the user to press ESCAPE and then exit the program
 */
static
void
wait_for_exit()
{
  // wait for all buttons to be released
  while (buttons_get()) ;
  // now wait for escape
  while (buttons_get() != BUTTON_ESCAPE) ;
  // Exit the program
  schedule_request(REQUEST_EXIT);
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
handle_uncaught_exception(Throwable * exception,
			  const Thread * thread,
			  const int methodRecord,
			  int pc)
{
  int line = 0;
  int *frame;
  int cnt;
  int dummy;
  nxt_motor_reset_all();
  sound_freq(100,500, 80); // buzz
  display_reset();
  display_goto_xy(0, line++);
  display_string("Exception: ");
  display_int(get_class_index(&(exception->_super)), 0);
  if (exception->msg && ((String *)(exception->msg))->characters)
  {
    display_goto_xy(0, line++);
    display_jstring((String *)(exception->msg));
  }
  if (!exception->stackTrace)
  {
    dummy = methodRecord << 16 | pc;
    frame = &dummy;
    cnt = 1;
  }
  else
  {
    frame = (int *)jint_array((Object *)(exception->stackTrace));
    cnt = get_array_length((Object *)(exception->stackTrace));
  }
  while (cnt-- > 0 && line < 7)
  {
    display_goto_xy(0, line++);
    display_string(" at: ");
    display_int(*frame >> 16, 0);
    display_string("(");
    display_int(*frame & 0xffff, 0);
    display_string(")");
    frame++;
  }
  display_update();
  wait_for_exit();
}

void
switch_thread_hook()
{
  int b = buttons_get();

  // Check for ENTER and ESCAPE pressed
  if (b == (BUTTON_ENTER|BUTTON_ESCAPE)) {
    if (debug_user_interrupt()) return;
    // exit the program
    schedule_request(REQUEST_EXIT);
  }
}

#ifdef VERIFY
void
assert_hook(boolean aCond, int aCode)
{
  if (aCond) return;
  display_reset();
  display_goto_xy(0,0);
  display_string("Assert failed");
  display_goto_xy(0,1);
  display_string("Code: ");
  display_goto_xy(6,1);
  display_int(aCode,0);
  display_update();
  while(1); // Hang
}
#endif

/**
 * Request that a new program be started.
 */
int
run_program(byte *start, unsigned int len)
{
  if (!is_valid_executable(start, len)) return -1;
  gNextProgram = start;
  gNextProgramSize = len;
  schedule_request(REQUEST_EXIT);
  return 0;
}


void
run(int jsize)
{
  byte *ram_end = (byte *) (&__free_ram_end__);
  byte *ram_start = (byte *) (&__free_ram_start__);

  init_events();
  init_debug();

  //printf("Initializing Binary\n");

  // Init the static storage and class sync objects in ram
  {
    MasterRecord *mrec = get_master_record();
    int staticSize = mrec->staticStateLength;
    int syncSize = (mrec->lastClass + 1) * sizeof(objSync);
    int statusSize = (mrec->lastClass + 1) * sizeof( classStatusBase[0]);
#if ! EXECUTE_FROM_FLASH
    // Skip java binary if it is an top of ram
    if (jsize > 0)
      ram_end -= (jsize + 4);
#endif

    staticSize = (staticSize + 3) & ~(3);
    statusSize = (statusSize + 3) & ~(3);
    syncSize = (syncSize + 3) & ~(3);
  
    ram_end -= staticSize;
    classStaticStateBase = ram_end;
    memset( (byte *)classStaticStateBase, 0, staticSize);
    ram_end -= syncSize;
    staticSyncBase = (objSync *)ram_end;
    memset( (byte *)staticSyncBase, 0, syncSize);

#if EXECUTE_FROM_FLASH
    // When we execute from flash we need extra storage for the class state.
    ram_end -= statusSize;
    classStatusBase = ram_end;
    memset( (byte *)classStatusBase, 0, statusSize);
#endif
  }

  // Initialize binary image state
  initialize_binary();

  //printf("Initializing memory\n");

  // Initialize memory
  memory_init();
  region = ram_start;
  memory_add_region(region, (byte *) ram_end);

  //printf("Initializing exceptions\n");

  // Create the execution environment and boot thread
  init_threads();
  // Initialize exceptions
  init_exceptions();

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
nxt_main(const byte *bin, int size)
{
  int jsize = 0;
  const byte *binary; 

#if EXECUTE_FROM_FLASH
  if (bin != NULL) {
    size = (size + 3) & ~3;
    binary = bin;
    jsize = size - 4;
  } else {
    // Execute flash menu

    bin = (byte *) menu_address;
    size = menu_length;
    size = (size + 3) & ~3;
    binary = bin;
    jsize = size - 4;
  }
#else
  unsigned *temp;
  if (bin != NULL) {
    size = (size + 3) & ~3;
    temp = ((unsigned *) (&__free_ram_end__)) - (size >> 2);
    memcpy(temp,bin,size);
    binary = (byte *) temp;
    jsize = size - 4;
  } else {
    // Execute flash menu

    bin = (byte *) menu_address;
    size = menu_length;
    size = (size + 3) & ~3;
    temp = ((unsigned *) (&__free_ram_end__)) - (size >> 2);   
    memcpy(temp,bin,size);
    binary = ((byte *) temp);
    jsize = size - 4;
  }
#endif
  
  // reset all motors, sensors and devices

  sp_init();
  display_set_auto_update_period(DEFAULT_UPDATE_PERIOD);
  //       printf("Installing Binary\n");

  install_binary(binary);

  //      printf("Running\n");
  run(jsize);
  init_events();
  display_reset();
  nxt_motor_reset_all();
  udp_reset();
  bt_reset();
  bt_disable();
  hs_disable();
  i2c_disable_all();
  sound_reset();
  return 0;
}

#ifdef FW_SPLASH
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
show_splash()
{
  display_clear(0);
  display_bitmap_copy(splash_data, 26, 4, 37, 1);

  display_goto_xy(6, 6);
  display_string("LEJOS");
  display_update();
}
#endif


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
  sp_init();
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
#ifdef FW_SPLASH
  show_splash(); 
#endif
  gNextProgram = NULL;
  do 
  {
  	byte *next = gNextProgram;
  	gNextProgram = NULL;
  	gProgramExecutions++;
  	nxt_main(next, gNextProgramSize);
  }
  while (true);

  shutdown();
}
