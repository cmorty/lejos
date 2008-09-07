
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
 * The return value indicates the impact of the call on the VM
 * system. EXEC_CONTINUE normal return the system should return to the return
 * address provided by the VM. EXEC_RUN The call has modified the value of
 * VM PC and this should be used to restart execution. EXEC_RETRY The call
 * needs to be re-tried (typically for a GC failure), all global state
 * should be left intact, the PC has been set appropriately.
 *
 */

int dispatch_native(TWOBYTES signature, STACKWORD * paramBase)
{
  switch (signature) {
  case wait_4_5V:
    monitor_wait((Object *) word2ptr(paramBase[0]), 0);
    return EXEC_CONTINUE;
  case wait_4J_5V:
    monitor_wait((Object *) word2ptr(paramBase[0]), paramBase[2]);
    return EXEC_CONTINUE;
  case notify_4_5V:
    monitor_notify((Object *) word2ptr(paramBase[0]), false);
    return EXEC_CONTINUE;
  case notifyAll_4_5V:
    monitor_notify((Object *) word2ptr(paramBase[0]), true);
    return EXEC_CONTINUE;
  case start_4_5V:
    // Create thread, allow for instruction restart
    return init_thread((Thread *) word2ptr(paramBase[0]));
  case yield_4_5V:
    schedule_request(REQUEST_SWITCH_THREAD);
    return EXEC_CONTINUE;
  case sleep_4J_5V:
    sleep_thread(paramBase[1]);
    schedule_request(REQUEST_SWITCH_THREAD);
    return EXEC_CONTINUE;
  case getPriority_4_5I:
    push_word(get_thread_priority((Thread *) word2ptr(paramBase[0])));
    return EXEC_CONTINUE;
  case setPriority_4I_5V:
    {
      STACKWORD p = (STACKWORD) paramBase[1];

      if (p > MAX_PRIORITY || p < MIN_PRIORITY)
	throw_exception(illegalArgumentException);
      else
	set_thread_priority((Thread *) word2ptr(paramBase[0]), p);
    }
    return EXEC_CONTINUE;
  case currentThread_4_5Ljava_3lang_3Thread_2:
    push_ref(ptr2ref(currentThread));
    return EXEC_CONTINUE;
  case interrupt_4_5V:
    interrupt_thread((Thread *) word2ptr(paramBase[0]));
    return EXEC_CONTINUE;
  case interrupted_4_5Z:
    {
      JBYTE i = currentThread->interruptState != INTERRUPT_CLEARED;

      currentThread->interruptState = INTERRUPT_CLEARED;
      push_word(i);
    }
    return EXEC_CONTINUE;
  case isInterrupted_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->interruptState
	      != INTERRUPT_CLEARED);
    return EXEC_CONTINUE;
  case setDaemon_4Z_5V:
    ((Thread *) word2ptr(paramBase[0]))->daemon = (JBYTE) paramBase[1];
    return EXEC_CONTINUE;
  case isDaemon_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->daemon);
    return EXEC_CONTINUE;
  case join_4_5V:
    join_thread((Thread *) word2ptr(paramBase[0]));
    return EXEC_CONTINUE;
  case join_4J_5V:
    join_thread((Thread *) word2obj(paramBase[0]));
    return EXEC_CONTINUE;
  case exit_4I_5V:
    schedule_request(REQUEST_EXIT);
    return EXEC_CONTINUE;
  case currentTimeMillis_4_5J:
    push_word(0);
    push_word(get_sys_time());
    return EXEC_CONTINUE;
  case setPoller_4_5V:
    set_poller(word2ptr(paramBase[0]));
    return EXEC_CONTINUE;
  case readSensorValue_4I_5I:
    push_word(sensor_adc(paramBase[0]));
    return EXEC_CONTINUE;
  case setADTypeById_4II_5V:
    if (paramBase[1] & 1)
      set_digi0(paramBase[0]);
    else
      unset_digi0(paramBase[0]);
    if (paramBase[1] & 2)
      set_digi1(paramBase[0]);
    else
      unset_digi1(paramBase[0]);
    return EXEC_CONTINUE;
  case setPowerTypeById_4II_5V:
    nxt_avr_set_input_power(paramBase[0], paramBase[1]);
    return EXEC_CONTINUE;
  case freeMemory_4_5J:
    push_word(0);
    push_word(getHeapFree());
    return EXEC_CONTINUE;
  case totalMemory_4_5J:
    push_word(0);
    push_word(getHeapSize());
    return EXEC_CONTINUE;
  case test_4Ljava_3lang_3String_2Z_5V:
    if (!paramBase[1]) {
      throw_exception(error);
    }
    return EXEC_CONTINUE;
  case testEQ_4Ljava_3lang_3String_2II_5V:
    if (paramBase[1] != paramBase[2]) {
      throw_exception(error);
    }
    return EXEC_CONTINUE;
  case floatToIntBits_4F_5I:	// Fall through
  case intBitsToFloat_4I_5F:
    push_word(paramBase[0]);
    return EXEC_CONTINUE;
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
    return EXEC_CONTINUE;
  case drawInt_4III_5V:
    display_goto_xy(paramBase[1], paramBase[2]);
    display_int(paramBase[0], 0);
    return EXEC_CONTINUE;
  case drawInt_4IIII_5V:
     display_goto_xy(paramBase[2], paramBase[3]);
     display_int(paramBase[0], paramBase[1]);
    return EXEC_CONTINUE;   
  case refresh_4_5V:
    display_update();
    return EXEC_CONTINUE;
  case clear_4_5V:
    display_clear(0);
    return EXEC_CONTINUE;
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
    return EXEC_CONTINUE;
  case getDisplay_4_5_1B:
    push_word(display_get_array());
    return EXEC_CONTINUE;
  case setAutoRefresh_4I_5V:
    display_set_auto_update(paramBase[0]);
    return EXEC_CONTINUE;
  case bitBlt_4_1BIIII_1BIIIIIII_5V:
    {
      Object *src = word2ptr(paramBase[0]);
      Object *dst = word2ptr(paramBase[5]);
      display_bitblt((byte *)jbyte_array(src), paramBase[1], paramBase[2], paramBase[3], paramBase[4], (byte *)jbyte_array(dst), paramBase[6], paramBase[7], paramBase[8], paramBase[9], paramBase[10], paramBase[11], paramBase[12]);
      return EXEC_CONTINUE;
    }
  case getSystemFont_4_5_1B:
    push_word(display_get_font());
    return EXEC_CONTINUE;
  case getVoltageMilliVolt_4_5I:
    push_word(battery_voltage());
    return EXEC_CONTINUE;
  case getButtons_4_5I:
    push_word(buttons_get());
    return EXEC_CONTINUE;
  case getTachoCountById_4I_5I:
    push_word(nxt_motor_get_count(paramBase[0]));
    return EXEC_CONTINUE;
  case controlMotorById_4III_5V:
    nxt_motor_set_speed(paramBase[0], paramBase[1], paramBase[2]); 
    return EXEC_CONTINUE;
  case resetTachoCountById_4I_5V:
    nxt_motor_set_count(paramBase[0], 0);
    return EXEC_CONTINUE;
  case i2cEnableById_4I_5V:
    i2c_enable(paramBase[0]);
    return EXEC_CONTINUE;
  case i2cDisableById_4I_5V:
    i2c_disable(paramBase[0]);
    return EXEC_CONTINUE;
  case i2cBusyById_4I_5I:
    push_word(i2c_busy(paramBase[0]));
    return EXEC_CONTINUE;
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
    return EXEC_CONTINUE; 
  case playFreq_4III_5V:
    sound_freq(paramBase[0],paramBase[1], paramBase[2]);
    return EXEC_CONTINUE;
  case btSend_4_1BI_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      bt_send(byteArray,paramBase[1]);                      
    }
    return EXEC_CONTINUE;
  case btReceive_4_1B_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      bt_receive(byteArray);                      
    }
    return EXEC_CONTINUE;
  case btGetBC4CmdMode_4_5I:
    push_word(bt_get_mode());
    return EXEC_CONTINUE;
  case btSetArmCmdMode_4I_5V:
    if (paramBase[0] == 0) bt_set_arm7_cmd();
    else bt_clear_arm7_cmd(); 
    return EXEC_CONTINUE;
  case btStartADConverter_4_5V:
    bt_start_ad_converter();
    return EXEC_CONTINUE;
  case btSetResetLow_4_5V:
    bt_set_reset_low();
    return EXEC_CONTINUE;
  case btSetResetHigh_4_5V:
    bt_set_reset_high();
    return EXEC_CONTINUE;
  case btWrite_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(bt_write(byteArray, paramBase[1], paramBase[2]));                      
    }
    return EXEC_CONTINUE;
  case btRead_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(bt_read(byteArray, paramBase[1], paramBase[2]));                      
    }
    return EXEC_CONTINUE;
  case btPending_4_5I:
    {
      push_word(bt_pending());
    }
    return EXEC_CONTINUE;
  case usbRead_4_1BII_5I:
     {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(udp_read(byteArray,paramBase[1], paramBase[2]));                      
    } 
    return EXEC_CONTINUE;
  case usbWrite_4_1BII_5I:
     {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(udp_write(byteArray,paramBase[1], paramBase[2]));                      
    }
    return EXEC_CONTINUE; 
  case usbStatus_4_5I:
    {
      push_word(udp_status());
    }
    return EXEC_CONTINUE;
  case usbEnable_4I_5V:
    {
      udp_enable(paramBase[0]);
    }
    return EXEC_CONTINUE;
  case usbDisable_4_5V:
    {
      udp_disable();
    }
    return EXEC_CONTINUE;
  case usbReset_4_5V:
    udp_reset();
    return EXEC_CONTINUE; 
  case usbSetSerialNo_4Ljava_3lang_3String_2_5V: 
    {
      byte *p = word2ptr(paramBase[0]);
      int len;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      udp_set_serialno((U8 *)jchar_array(charArray), len);
    }
    return EXEC_CONTINUE;
  case usbSetName_4Ljava_3lang_3String_2_5V:
    {
      byte *p = word2ptr(paramBase[0]);
      int len;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      udp_set_name((U8 *)jchar_array(charArray), len);
    }
    return EXEC_CONTINUE;
  case writePage_4_1BI_5V:
    {
      Object *p = word2ptr(paramBase[0]);
      unsigned long *intArray = (unsigned long *) jint_array(p);
      flash_write_page(intArray,paramBase[1]);                      
    }
    return EXEC_CONTINUE;
  case readPage_4_1BI_5V:
    {
      int i;
      Object *p = word2ptr(paramBase[0]);
      unsigned long *intArray = (unsigned long *) jint_array(p);
      for(i=0;i<64;i++) intArray[i] = FLASH_BASE[(paramBase[1]*64)+i];                       
    }
    return EXEC_CONTINUE;
  case exec_4II_5V:
    gNextProgram = (unsigned int) &FLASH_BASE[(paramBase[0]*64)];
    gNextProgramSize = paramBase[1];
    schedule_request(REQUEST_EXIT);
    return EXEC_CONTINUE;
  case playSample_4IIIII_5V:
    sound_play_sample(((unsigned char *) &FLASH_BASE[(paramBase[0]*64)]) + paramBase[1],paramBase[2],paramBase[3],paramBase[4]);
    return EXEC_CONTINUE;
  case getTime_4_5I:
    push_word(sound_get_time());
    return EXEC_CONTINUE;
  case getDataAddress_4Ljava_3lang_3Object_2_5I:
    push_word (ptr2word ((byte *) fields_start(word2ptr(paramBase[0]))));
    return EXEC_CONTINUE;
  case gc_4_5V:
    // Restartable garbage collection
    return garbage_collect();
  case diagn_4II_5I:
    push_word (sys_diagn(paramBase[0], paramBase[1]));
    return EXEC_CONTINUE;
  case shutDown_4_5V:
    while (1) nxt_avr_power_down(); // does not return 
  case boot_4_5V:
    while (1) nxt_avr_firmware_update_mode(); // does not return 
  case arraycopy_4Ljava_3lang_3Object_2ILjava_3lang_3Object_2II_5V:
    {
      Object *p1 = word2ptr(paramBase[0]);
      Object *p2 = word2ptr(paramBase[2]);
      arraycopy(p1, paramBase[1], p2, paramBase[3], paramBase[4]);
    }
    return EXEC_CONTINUE;
  case executeProgram_4I_5V:
    // Exceute program, allow for instruction re-start
    return execute_program(paramBase[0]);
  case setDebug_4_5V:
    set_debug(word2ptr(paramBase[0]));
    return EXEC_CONTINUE;
  case peekWord_4I_5I:
    push_word(*((unsigned long *)(paramBase[0])));
    return EXEC_CONTINUE;
  case eventOptions_4II_5I:
    {
      byte old = debugEventOptions[paramBase[0]];
      debugEventOptions[paramBase[0]] = (byte)paramBase[1];
      push_word(old);
    }
    return EXEC_CONTINUE;
  case suspendThread_4Ljava_3lang_3Object_2_5V:
    suspend_thread(ref2ptr(paramBase[0]));
    return EXEC_CONTINUE;
  case resumeThread_4Ljava_3lang_3Object_2_5V:
    resume_thread(ref2ptr(paramBase[0]));
    return EXEC_CONTINUE;
  case getProgramExecutionsCount_4_5I:
    push_word(gProgramExecutions);
    return EXEC_CONTINUE;
  case getFirmwareRevision_4_5I:
    push_word((STACKWORD) getRevision());
    return EXEC_CONTINUE;
  case getFirmwareMajorVersion_4_5I:
    push_word((STACKWORD) MAJOR_VERSION);
    return EXEC_CONTINUE;
  case getFirmwareMinorVersion_4_5I:
    push_word((STACKWORD) MINOR_VERSION); 
    return EXEC_CONTINUE;
  default:
    throw_exception(noSuchMethodError);
  }
  return EXEC_CONTINUE;
}
