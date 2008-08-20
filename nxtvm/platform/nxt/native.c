
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
#include "udp.h"
#include "flashprog.h"
#include "debug.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#undef push_word()
#undef push_ref()
#define push_word( a) push_word_cur( a)
#define push_ref( a)  push_ref_cur( a)

// Declared below to avoid needing STACKWORD everywhere we use display
extern STACKWORD display_get_array(void);
extern STACKWORD display_get_font(void);

// Extract the highest revision number from the output of svnversion
int getRevision() {
  char* rev = SVN_REV;
  char *str = strchr(rev,':'); // Skip start revision if present
  if (str == NULL) str = rev;else str++;
  return atoi(str);
}
	
/**
 * NOTE: The technique is not the same as that used in TinyVM.
 * returns true for normal operation. Return false to allow the
 * call to be re-started (gloval state should have been preserved).
 *
 */
boolean
dispatch_native(TWOBYTES signature, STACKWORD * paramBase)
{
  switch (signature) {
  case wait_4_5V:
    monitor_wait((Object *) word2ptr(paramBase[0]), 0);
    return true;
  case wait_4J_5V:
    monitor_wait((Object *) word2ptr(paramBase[0]), paramBase[2]);
    return true;
  case notify_4_5V:
    monitor_notify((Object *) word2ptr(paramBase[0]), false);
    return true;
  case notifyAll_4_5V:
    monitor_notify((Object *) word2ptr(paramBase[0]), true);
    return true;
  case start_4_5V:
    init_thread((Thread *) word2ptr(paramBase[0]));
    return true;
  case yield_4_5V:
    schedule_request(REQUEST_SWITCH_THREAD);
    return true;
  case sleep_4J_5V:
    sleep_thread(paramBase[1]);
    schedule_request(REQUEST_SWITCH_THREAD);
    return true;
  case getPriority_4_5I:
    push_word(get_thread_priority((Thread *) word2ptr(paramBase[0])));
    return true;
  case setPriority_4I_5V:
    {
      STACKWORD p = (STACKWORD) paramBase[1];

      if (p > MAX_PRIORITY || p < MIN_PRIORITY)
	throw_exception(illegalArgumentException);
      else
	set_thread_priority((Thread *) word2ptr(paramBase[0]), p);
    }
    return true;
  case currentThread_4_5Ljava_3lang_3Thread_2:
    push_ref(ptr2ref(currentThread));
    return true;
  case interrupt_4_5V:
    interrupt_thread((Thread *) word2ptr(paramBase[0]));
    return true;
  case interrupted_4_5Z:
    {
      JBYTE i = currentThread->interruptState != INTERRUPT_CLEARED;

      currentThread->interruptState = INTERRUPT_CLEARED;
      push_word(i);
    }
    return true;
  case isInterrupted_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->interruptState
	      != INTERRUPT_CLEARED);
    return true;
  case setDaemon_4Z_5V:
    ((Thread *) word2ptr(paramBase[0]))->daemon = (JBYTE) paramBase[1];
    return true;
  case isDaemon_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->daemon);
    return true;
  case join_4_5V:
    join_thread((Thread *) word2ptr(paramBase[0]));
    return true;
  case join_4J_5V:
    join_thread((Thread *) word2obj(paramBase[0]));
    return true;
  case exit_4I_5V:
    schedule_request(REQUEST_EXIT);
    return true;
  case currentTimeMillis_4_5J:
    push_word(0);
    push_word(get_sys_time());
    return true;
  case setPoller_4_5V:
    set_poller(word2ptr(paramBase[0]));
    return true;
  case readSensorValue_4I_5I:
    push_word(sensor_adc(paramBase[0]));
    return true;
  case setADTypeById_4II_5V:
    if (paramBase[1] & 1)
      set_digi0(paramBase[0]);
    else
      unset_digi0(paramBase[0]);
    if (paramBase[1] & 2)
      set_digi1(paramBase[0]);
    else
      unset_digi1(paramBase[0]);
    return true;
  case setPowerTypeById_4II_5V:
    nxt_avr_set_input_power(paramBase[0], paramBase[1]);
    return true;
  case freeMemory_4_5J:
    push_word(0);
    push_word(getHeapFree());
    return true;
  case totalMemory_4_5J:
    push_word(0);
    push_word(getHeapSize());
    return true;
  case test_4Ljava_3lang_3String_2Z_5V:
    if (!paramBase[1]) {
      throw_exception(error);
    }
    return true;
  case testEQ_4Ljava_3lang_3String_2II_5V:
    if (paramBase[1] != paramBase[2]) {
      throw_exception(error);
    }
    return true;
  case floatToIntBits_4F_5I:	// Fall through
  case intBitsToFloat_4I_5F:
    push_word(paramBase[0]);
    return true;
  case drawString_4Ljava_3lang_3String_2II_5V:
    {
      byte *p = word2ptr(paramBase[0]);
      int len, i;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      {
	char buff[len + 1];
	char *chars = (char *) jchar_array(charArray);

	for (i = 0; i < len; i++)
	  buff[i] = chars[i + i];
	buff[len] = 0;
	display_goto_xy(paramBase[1], paramBase[2]);
	display_string(buff);
      }
    }
    return true;
  case drawInt_4III_5V:
    display_goto_xy(paramBase[1], paramBase[2]);
    display_int(paramBase[0], 0);
    return true;
  case drawInt_4IIII_5V:
     display_goto_xy(paramBase[2], paramBase[3]);
     display_int(paramBase[0], paramBase[1]);
    return true;   
  case refresh_4_5V:
    display_update();
    return true;
  case clear_4_5V:
    display_clear(0);
    return true;
  case setDisplay_4_1I_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      int len, i;

      len = get_array_length(p);
      unsigned *intArray = (unsigned *) jint_array(p);
      unsigned *display_buffer = (unsigned *) display_get_buffer();

      for (i = 0; i < 200; i++)
	display_buffer[i] = intArray[i];
    }
    return true;
  case getDisplay_4_5_1B:
    push_word(display_get_array());
    return true;
  case setAutoRefresh_4I_5V:
    display_set_auto_update(paramBase[0]);
    return true;
  case bitBlt_4_1BIIII_1BIIIIIII_5V:
    {
      Object *src = word2ptr(paramBase[0]);
      Object *dst = word2ptr(paramBase[5]);
      display_bitblt((byte *)jbyte_array(src), paramBase[1], paramBase[2], paramBase[3], paramBase[4], (byte *)jbyte_array(dst), paramBase[6], paramBase[7], paramBase[8], paramBase[9], paramBase[10], paramBase[11], paramBase[12]);
      return true;
    }
  case getSystemFont_4_5_1B:
    push_word(display_get_font());
    return true;
  case getVoltageMilliVolt_4_5I:
    push_word(battery_voltage());
    return true;
  case getButtons_4_5I:
    push_word(buttons_get());
    return true;
  case getTachoCountById_4I_5I:
    push_word(nxt_motor_get_count(paramBase[0]));
    return true;
  case controlMotorById_4III_5V:
    nxt_motor_set_speed(paramBase[0], paramBase[1], paramBase[2]); 
    return true;
  case resetTachoCountById_4I_5V:
    nxt_motor_set_count(paramBase[0], 0);
    return true;
  case i2cEnableById_4I_5V:
    i2c_enable(paramBase[0]);
    return true;
  case i2cDisableById_4I_5V:
    i2c_disable(paramBase[0]);
    return true;
  case i2cBusyById_4I_5I:
    push_word(i2c_busy(paramBase[0]));
    return true;
  case i2cStartById_4IIII_1BII_5I:
    {
    	Object *p = word2ptr(paramBase[4]);
    	byte *byteArray = (byte *) jbyte_array(p);
    	push_word(i2c_start_transaction(paramBase[0],
    	                                paramBase[1],
    	                                paramBase[2],
    	                                paramBase[3],
    	                                byteArray,
    	                                paramBase[5],
    	                                paramBase[6]));                      
    }
    return true; 
  case playFreq_4III_5V:
    sound_freq(paramBase[0],paramBase[1], paramBase[2]);
    return true;
  case btSend_4_1BI_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      bt_send(byteArray,paramBase[1]);                      
    }
    return true;
  case btReceive_4_1B_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      bt_receive(byteArray);                      
    }
    return true;
  case btGetBC4CmdMode_4_5I:
    push_word(bt_get_mode());
    return true;
  case btSetArmCmdMode_4I_5V:
    if (paramBase[0] == 0) bt_set_arm7_cmd();
    else bt_clear_arm7_cmd(); 
    return true;
  case btStartADConverter_4_5V:
    bt_start_ad_converter();
    return true;
  case btSetResetLow_4_5V:
    bt_set_reset_low();
    return true;
  case btSetResetHigh_4_5V:
    bt_set_reset_high();
    return true;
  case btWrite_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(bt_write(byteArray, paramBase[1], paramBase[2]));                      
    }
    return true;
  case btRead_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(bt_read(byteArray, paramBase[1], paramBase[2]));                      
    }
    return true;
  case btPending_4_5I:
    {
      push_word(bt_pending());
    }
    return true;
  case usbRead_4_1BII_5I:
     {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(udp_read(byteArray,paramBase[1], paramBase[2]));                      
    } 
    return true;
  case usbWrite_4_1BII_5I:
     {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(udp_write(byteArray,paramBase[1], paramBase[2]));                      
    }
    return true; 
  case usbStatus_4_5I:
    {
      push_word(udp_status());
    }
    return true;
  case usbEnable_4I_5V:
    {
      udp_enable(paramBase[0]);
    }
    return true;
  case usbDisable_4_5V:
    {
      udp_disable();
    }
    return true;
  case usbReset_4_5V:
    udp_reset();
    return true; 
  case usbSetSerialNo_4Ljava_3lang_3String_2_5V: 
    {
      byte *p = word2ptr(paramBase[0]);
      int len;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      udp_set_serialno((U8 *)jchar_array(charArray), len);
    }
    return true;
  case usbSetName_4Ljava_3lang_3String_2_5V:
    {
      byte *p = word2ptr(paramBase[0]);
      int len;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      udp_set_name((U8 *)jchar_array(charArray), len);
    }
    return true;
  case writePage_4_1BI_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      unsigned long *intArray = (unsigned long *) jint_array(p);
      flash_write_page(intArray,paramBase[1]);                      
    }
    return true;
  case readPage_4_1BI_5V:
    {
      int i;
      Object *p = word2ptr(paramBase[0]);
      unsigned long *intArray = (unsigned long *) jint_array(p);
      for(i=0;i<64;i++) intArray[i] = FLASH_BASE[(paramBase[1]*64)+i];                       
    }
    return true;
  case exec_4II_5V:
    gNextProgram = (unsigned int) &FLASH_BASE[(paramBase[0]*64)];
    gNextProgramSize = paramBase[1];
    schedule_request(REQUEST_EXIT);
    return true;
  case playSample_4IIIII_5V:
    sound_play_sample(((unsigned char *) &FLASH_BASE[(paramBase[0]*64)]) + paramBase[1],paramBase[2],paramBase[3],paramBase[4]);
    return true;
  case getTime_4_5I:
    push_word(sound_get_time());
    return true;
  case getDataAddress_4Ljava_3lang_3Object_2_5I:
    push_word (ptr2word ((byte *) fields_start(word2ptr(paramBase[0]))));
    return true;
  case gc_4_5V:
    garbage_collect(0x7fffffff);
    garbage_collect(0x7fffffff);
    return true;
  case diagn_4II_5I:
    push_word (sys_diagn(paramBase[0], paramBase[1]));
    return true;
  case shutDown_4_5V:
    while (1) nxt_avr_power_down(); // does not return true
  case boot_4_5V:
    while (1) nxt_avr_firmware_update_mode(); // does not return true_
  case arraycopy_4Ljava_3lang_3Object_2ILjava_3lang_3Object_2II_5V:
    {
      Object *p1 = word2ptr(paramBase[0]);
      Object *p2 = word2ptr(paramBase[2]);
      arraycopy(p1, paramBase[1], p2, paramBase[3], paramBase[4]);
    }
    return true;
  case executeProgram_4I_5V:
    {
      MethodRecord *mRec;
      ClassRecord *classRecord;
      classRecord = get_class_record (get_entry_class (paramBase[0]));
      // Initialize top word with fake parameter for main():
      set_top_ref_cur (JNULL);
      // Push stack frame for main method:
      mRec= find_method (classRecord, main_4_1Ljava_3lang_3String_2_5V);
      dispatch_special (mRec, curPc);
      dispatch_static_initializer (classRecord, curPc);
    }
    return true;
  case setDebug_4_5V:
    set_debug(word2ptr(paramBase[0]));
    return true;
  case peekWord_4I_5I:
    push_word(*((unsigned long *)(paramBase[0])));
    return true;
  case eventOptions_4II_5I:
    {
      byte old = debugEventOptions[paramBase[0]];
      debugEventOptions[paramBase[0]] = (byte)paramBase[1];
      push_word(old);
    }
    return true;
  case suspendThread_4Ljava_3lang_3Object_2_5V:
    suspend_thread(ref2ptr(paramBase[0]));
    return true;
  case resumeThread_4Ljava_3lang_3Object_2_5V:
    resume_thread(ref2ptr(paramBase[0]));
    return true;
  case getProgramExecutionsCount_4_5I:
    push_word(gProgramExecutions);
    return true;
  case getFirmwareRevision_4_5I:
    push_word((STACKWORD) getRevision());
    return true;
  case getFirmwareMajorVersion_4_5I:
    push_word((STACKWORD) MAJOR_VERSION);
    return true;
  case getFirmwareMinorVersion_4_5I:
    push_word((STACKWORD) MINOR_VERSION); 
    return true;
  default:
    throw_exception(noSuchMethodError);
  }
  return true;
}
