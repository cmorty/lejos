
/**
 * native.c
 * Native method handling for nxt.
 */
#include "types.h"
#include "trace.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "stack.h"
#include "memory.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "exceptions.h"
#include "platform_config.h"
#include "sensors.h"
#include "poll.h"
#include "display.h"
#include "nxt_avr.h"
#include "nxt_motors.h"
#include "i2c.h"
#include "sound.h"
#include "bt.h"

/**
 * NOTE: The technique is not the same as that used in TinyVM.
 */
void
dispatch_native(TWOBYTES signature, STACKWORD * paramBase)
{
  ClassRecord *classRecord;
  STACKWORD *paramBase1 = paramBase + 1;
  STACKWORD *paramBase2 = paramBase + 2;

  switch (signature) {
  case wait_4_5V:
    monitor_wait((Object *) word2ptr(paramBase[0]), 0);
    return;
  case wait_4J_5V:
    monitor_wait((Object *) word2ptr(paramBase[0]), paramBase[2]);
    return;
  case notify_4_5V:
    monitor_notify((Object *) word2ptr(paramBase[0]), false);
    return;
  case notifyAll_4_5V:
    monitor_notify((Object *) word2ptr(paramBase[0]), true);
    return;
  case start_4_5V:
    init_thread((Thread *) word2ptr(paramBase[0]));
    return;
  case yield_4_5V:
    schedule_request(REQUEST_SWITCH_THREAD);
    return;
  case sleep_4J_5V:
    sleep_thread(paramBase[1]);
    schedule_request(REQUEST_SWITCH_THREAD);
    return;
  case getPriority_4_5I:
    push_word(get_thread_priority((Thread *) word2ptr(paramBase[0])));
    return;
  case setPriority_4I_5V:
    {
      STACKWORD p = (STACKWORD) paramBase[1];

      if (p > MAX_PRIORITY || p < MIN_PRIORITY)
	throw_exception(illegalArgumentException);
      else
	set_thread_priority((Thread *) word2ptr(paramBase[0]), p);
    }
    return;
  case currentThread_4_5Ljava_3lang_3Thread_2:
    push_ref(ptr2ref(currentThread));
    return;
  case interrupt_4_5V:
    interrupt_thread((Thread *) word2ptr(paramBase[0]));
    return;
  case interrupted_4_5Z:
    {
      JBYTE i = currentThread->interruptState != INTERRUPT_CLEARED;

      currentThread->interruptState = INTERRUPT_CLEARED;
      push_word(i);
    }
    return;
  case isInterrupted_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->interruptState
	      != INTERRUPT_CLEARED);
    return;
  case setDaemon_4Z_5V:
    ((Thread *) word2ptr(paramBase[0]))->daemon = (JBYTE) paramBase[1];
    return;
  case isDaemon_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->daemon);
    return;
  case join_4_5V:
    join_thread((Thread *) word2ptr(paramBase[0]));
    return;
  case join_4J_5V:
    join_thread((Thread *) word2obj(paramBase[0]));
    return;
  case exit_4I_5V:
    schedule_request(REQUEST_EXIT);
    return;
  case currentTimeMillis_4_5J:
    push_word(0);
    push_word(get_sys_time());
    return;
  case setPoller_4_5V:
    set_poller(word2ptr(paramBase[0]));
    return;
  case readSensorValue_4I_5I:
    push_word(sensor_adc(paramBase[0]));
    return;
  case setADTypeById_4II_5V:
    if (paramBase[1] & 1)
      set_digi0(paramBase[0]);
    else
      unset_digi0(paramBase[0]);
    if (paramBase[1] & 2)
      set_digi1(paramBase[0]);
    else
      unset_digi1(paramBase[0]);
    return;
  case setPowerTypeById_4II_5V:
    nxt_avr_set_input_power(paramBase[0], paramBase[1]);
    return;
  case freeMemory_4_5J:
    push_word(0);
    push_word(getHeapFree());
    return;
  case totalMemory_4_5J:
    push_word(0);
    push_word(getHeapSize());
    return;
  case test_4Ljava_3lang_3String_2Z_5V:
    if (!paramBase[1]) {
      throw_exception(error);
    }
    return;
  case testEQ_4Ljava_3lang_3String_2II_5V:
    if (paramBase[1] != paramBase[2]) {
      throw_exception(error);
    }
    return;
  case floatToIntBits_4F_5I:	// Fall through
  case intBitsToFloat_4I_5F:
    push_word(paramBase[0]);
    return;
  case drawString_4Ljava_3lang_3String_2II_5V:
    {
      byte *p = word2ptr(paramBase[0]);
      int len, i;
      Object *charArray = (Object *) word2ptr(get_word(p + HEADER_SIZE, 4));

      len = charArray->flags.arrays.length;
      {
	char buff[len + 1];
	char *chars = ((char *) charArray) + HEADER_SIZE;

	for (i = 0; i < len; i++)
	  buff[i] = chars[i + i];
	buff[len] = 0;
	display_goto_xy(paramBase[1], paramBase[2]);
	display_string(buff);
      }
    }
    return;
  case drawInt_4III_5V:
    display_goto_xy(paramBase[1], paramBase[2]);
    display_int(paramBase[0], 0);
    return;
  case drawInt_4IIII_5V:
     display_goto_xy(paramBase[2], paramBase[3]);
     display_int(paramBase[0], paramBase[1]);
    return;   
  case refresh_4_5V:
    display_update();
    return;
  case clear_4_5V:
    display_clear(0);
    return;
  case setDisplay_4_1I_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      int len, i;

      len = p->flags.arrays.length;
      unsigned *intArray = (unsigned *) (((byte *) p) + HEADER_SIZE);
      unsigned *display_buffer = (unsigned *) display_get_buffer();

      for (i = 0; i < 200; i++)
	display_buffer[i] = intArray[i];
    }
    return;
  case getVoltageMilliVolt_4_5I:
    push_word(battery_voltage());
    return;
  case readButtons_4_5I:
    push_word(buttons_get());
    return;
  case getTachoCountById_4I_5I:
    push_word(nxt_motor_get_count(paramBase[0]));
    return;
  case controlMotorById_4III_5V:
    nxt_motor_set_speed(paramBase[0], paramBase[1], paramBase[2]); 
    return;
  case resetTachoCountById_4I_5V:
    nxt_motor_set_count(paramBase[0], 0);
    return;
  case i2cEnableById_4I_5V:
    i2c_enable(paramBase[0]);
    return;
  case i2cDisableById_4I_5V:
    i2c_disable(paramBase[0]);
    return;
  case i2cBusyById_4I_5I:
    push_word(i2c_busy(paramBase[0]));
    return;
  case i2cStartById_4IIII_1BII_5I:
    {
    	Object *p = word2ptr(paramBase[4]);
    	byte *byteArray = (((byte *) p) + HEADER_SIZE);
    	push_word(i2c_start_transaction(paramBase[0],
    	                                paramBase[1],
    	                                paramBase[2],
    	                                paramBase[3],
    	                                byteArray,
    	                                paramBase[5],
    	                                paramBase[6]));                      
    }
    return; 
  case playTone_4II_5V:
    sound_freq(paramBase[0],paramBase[1]);
    return;
  case btSend_4_1BI_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (((byte *) p) + HEADER_SIZE);
      bt_send(byteArray,paramBase[1]);                      
    }
    return;
  case btReceive_4_1B_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (((byte *) p) + HEADER_SIZE);
      bt_receive(byteArray);                      
    }
    return;
  case btGetCmdMode_4_5I:
    push_word(bt_get_mode());
    break;
  case btSetCmdMode_4I_5V:
    if (paramBase[0] == 0) bt_set_arm7_cmd();
    else bt_clear_arm7_cmd(); 
    break;
  case btStartADConverter_4_5V:
    bt_start_ad_converter();
    break;
  default:
    throw_exception(noSuchMethodError);
  }
}
