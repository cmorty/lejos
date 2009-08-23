
/**
 * native.c
 * Native method handling for nxt.
 */
#include "types.h"
#include "mytypes.h"
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
#include "at91sam7s256.h"
#include "sensors.h"
#include "poll.h"
#include "display.h"
#include "nxt_avr.h"
#include "nxt_motors.h"
#include "i2c.h"
#include "sound.h"
#include "bt.h"
#include "hs.h"
#include "udp.h"
#include "flashprog.h"
#include "debug.h"
#include "systick.h"
#include "main.h"
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
  /*
  char* rev = SVN_REV;
  char *str = strchr(rev,':'); // Skip start revision if present
  if (str == NULL) str = rev;else str++;
  return atoi(str);
  */
  return SVN_REV;
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
    return monitor_wait((Object *) word2ptr(paramBase[0]), 0);
  case wait_4J_5V:
    return monitor_wait((Object *) word2ptr(paramBase[0]), paramBase[2]);
  case notify_4_5V:
    return monitor_notify((Object *) word2ptr(paramBase[0]), false);
  case notifyAll_4_5V:
    return monitor_notify((Object *) word2ptr(paramBase[0]), true);
  case start_4_5V:
    // Create thread, allow for instruction restart
    return init_thread((Thread *) word2ptr(paramBase[0]));
  case yield_4_5V:
    schedule_request(REQUEST_SWITCH_THREAD);
    break;
  case sleep_4J_5V:
    sleep_thread(paramBase[1]);
    schedule_request(REQUEST_SWITCH_THREAD);
    break;
  case getPriority_4_5I:
    push_word(get_thread_priority((Thread *) word2ptr(paramBase[0])));
    break;
  case setPriority_4I_5V:
    {
      STACKWORD p = (STACKWORD) paramBase[1];

      if (p > MAX_PRIORITY || p < MIN_PRIORITY)
	return throw_exception(illegalArgumentException);
      else
	set_thread_priority((Thread *) word2ptr(paramBase[0]), p);
    }
    break;
  case currentThread_4_5Ljava_3lang_3Thread_2:
    push_ref(ptr2ref(currentThread));
    break;
  case interrupt_4_5V:
    interrupt_thread((Thread *) word2ptr(paramBase[0]));
    break;
  case interrupted_4_5Z:
    {
      JBYTE i = currentThread->interruptState != INTERRUPT_CLEARED;

      currentThread->interruptState = INTERRUPT_CLEARED;
      push_word(i);
    }
    break;
  case isInterrupted_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->interruptState
	      != INTERRUPT_CLEARED);
    break;
  case setDaemon_4Z_5V:
    ((Thread *) word2ptr(paramBase[0]))->daemon = (JBYTE) paramBase[1];
    break;
  case isDaemon_4_5Z:
    push_word(((Thread *) word2ptr(paramBase[0]))->daemon);
    break;
  case join_4_5V:
    join_thread((Thread *) word2ptr(paramBase[0]), 0);
    break;
  case join_4J_5V:
    join_thread((Thread *) word2obj(paramBase[0]), paramBase[2]);
    break;
  case exit_4I_5V:
    schedule_request(REQUEST_EXIT);
    break;
  case currentTimeMillis_4_5J:
    push_word(0);
    push_word(systick_get_ms());
    break;
  case setPoller_4_5V:
    set_poller(word2ptr(paramBase[0]));
    break;
  case readSensorValue_4I_5I:
    push_word(sp_read(paramBase[0], SP_ANA));
    break;
  case setPowerTypeById_4II_5V:
    sp_set_power(paramBase[0], paramBase[1]);
    break;
  case freeMemory_4_5J:
    push_word(0);
    push_word(getHeapFree());
    break;
  case totalMemory_4_5J:
    push_word(0);
    push_word(getHeapSize());
    break;
  case floatToRawIntBits_4F_5I:	// Fall through
  case intBitsToFloat_4I_5F:
    push_word(paramBase[0]);
    break;
  case doubleToRawLongBits_4D_5J:	// Fall through
  case longBitsToDouble_4J_5D:
    {
      U32 w1 = paramBase[0];
      U32 w2 = paramBase[1];
      push_word(w1);
      push_word(w2);
    }
    break;
  case drawString_4Ljava_3lang_3String_2II_5V:
    {
      byte *p = word2ptr(paramBase[0]);
      int len, i;
      Object *charArray;
      if (!p) return throw_exception(nullPointerException);
      charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));
      if (!charArray) return throw_exception(nullPointerException);

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
    break;
  case drawInt_4III_5V:
    display_goto_xy(paramBase[1], paramBase[2]);
    display_int(paramBase[0], 0);
    break;
  case drawInt_4IIII_5V:
     display_goto_xy(paramBase[2], paramBase[3]);
     display_int(paramBase[0], paramBase[1]);
    break;   
  case refresh_4_5V:
    display_update();
    break;
  case clear_4_5V:
    display_clear(0);
    break;
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
    break;
  case getDisplay_4_5_1B:
    push_word(display_get_array());
    break;
  case setAutoRefresh_4I_5V:
    display_set_auto_update(paramBase[0]);
    break;
  case bitBlt_4_1BIIII_1BIIIIIII_5V:
    {
      Object *src = word2ptr(paramBase[0]);
      Object *dst = word2ptr(paramBase[5]);
      display_bitblt((byte *)jbyte_array(src), paramBase[1], paramBase[2], paramBase[3], paramBase[4], (byte *)jbyte_array(dst), paramBase[6], paramBase[7], paramBase[8], paramBase[9], paramBase[10], paramBase[11], paramBase[12]);
      break;
    }
  case getSystemFont_4_5_1B:
    push_word(display_get_font());
    break;
  case getBatteryStatus_4_5I:
    push_word(battery_voltage());
    break;
  case getButtons_4_5I:
    push_word(buttons_get());
    break;
  case getTachoCountById_4I_5I:
    push_word(nxt_motor_get_count(paramBase[0]));
    break;
  case controlMotorById_4III_5V:
    nxt_motor_set_speed(paramBase[0], paramBase[1], paramBase[2]); 
    break;
  case resetTachoCountById_4I_5V:
    nxt_motor_set_count(paramBase[0], 0);
    break;
  case i2cEnableById_4II_5V:
    if (i2c_enable(paramBase[0], paramBase[1]) == 0)
      return EXEC_RETRY;
    else
      break;
  case i2cDisableById_4I_5V:
    i2c_disable(paramBase[0]);
    break;
  case i2cBusyById_4I_5I:
    push_word(i2c_busy(paramBase[0]));
    break;
  case i2cStartById_4IIII_1BII_5I:
    {
    	Object *p = word2ptr(paramBase[4]);
    	byte *byteArray = (byte *) jbyte_array(p);
    	push_word(i2c_start(paramBase[0],
    	                    paramBase[1],
    	                    paramBase[2],
    	                    paramBase[3],
    	                    byteArray,
    	                    paramBase[5],
    	                    paramBase[6]));                      
    }
    break; 
  case i2cCompleteById_4I_1BI_5I:
    {
    	Object *p = word2ptr(paramBase[1]);
    	byte *byteArray = (byte *) jbyte_array(p);
    	push_word(i2c_complete(paramBase[0],
    	                       byteArray,
    	                       paramBase[2]));
    }
    break; 
  case playFreq_4III_5V:
    sound_freq(paramBase[0],paramBase[1], paramBase[2]);
    break;
  case btGetBC4CmdMode_4_5I:
    push_word(bt_get_mode());
    break;
  case btSetArmCmdMode_4I_5V:
    if (paramBase[0] == 0) bt_set_arm7_cmd();
    else bt_clear_arm7_cmd(); 
    break;
  case btSetResetLow_4_5V:
    bt_set_reset_low();
    break;
  case btSetResetHigh_4_5V:
    bt_set_reset_high();
    break;
  case btWrite_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(bt_write(byteArray, paramBase[1], paramBase[2]));                      
    }
    break;
  case btRead_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(bt_read(byteArray, paramBase[1], paramBase[2]));                      
    }
    break;
  case btPending_4_5I:
    {
      push_word(bt_pending());
    }
    break;
  case btEnable_4_5V:
    if (bt_enable() == 0)
      return EXEC_RETRY;
    else
      break;
  case btDisable_4_5V:
    bt_disable();
    break;
  case usbRead_4_1BII_5I:
     {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(udp_read(byteArray,paramBase[1], paramBase[2]));
    } 
    break;
  case usbWrite_4_1BII_5I:
     {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(udp_write(byteArray,paramBase[1], paramBase[2]));                      
    }
    break; 
  case usbStatus_4_5I:
    {
      push_word(udp_status());
    }
    break;
  case usbEnable_4I_5V:
    {
      udp_enable(paramBase[0]);
    }
    break;
  case usbDisable_4_5V:
    {
      udp_disable();
    }
    break;
  case usbReset_4_5V:
    udp_reset();
    break; 
  case usbSetSerialNo_4Ljava_3lang_3String_2_5V: 
    {
      byte *p = word2ptr(paramBase[0]);
      int len;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      udp_set_serialno((U8 *)jchar_array(charArray), len);
    }
    break;
  case usbSetName_4Ljava_3lang_3String_2_5V:
    {
      byte *p = word2ptr(paramBase[0]);
      int len;
      Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

      len = get_array_length(charArray);
      udp_set_name((U8 *)jchar_array(charArray), len);
    }
    break;
  case flashWritePage_4_1BI_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      unsigned long *intArray = (unsigned long *) jint_array(p);
      push_word(flash_write_page(intArray,paramBase[1]));                      
    }
    break;
  case flashReadPage_4_1BI_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      unsigned long *intArray = (unsigned long *) jint_array(p);
      push_word(flash_read_page(intArray,paramBase[1]));                      
    }
    break;
  case flashExec_4II_5I:
    push_word(run_program((byte *)(&FLASH_BASE[(paramBase[0]*FLASH_PAGE_SIZE)]), paramBase[1]));
    break;
  case playSample_4IIIII_5V:
    sound_play_sample(((unsigned char *) &FLASH_BASE[(paramBase[0]*FLASH_PAGE_SIZE)]) + paramBase[1],paramBase[2],paramBase[3],paramBase[4]);
    break;
  case getTime_4_5I:
    push_word(sound_get_time());
    break;
  case getDataAddress_4Ljava_3lang_3Object_2_5I:
    if (is_array(word2obj(paramBase[0])))
      push_word (ptr2word ((byte *) array_start(word2ptr(paramBase[0]))));
    else
      push_word (ptr2word ((byte *) fields_start(word2ptr(paramBase[0]))));
    break;
  case getObjectAddress_4Ljava_3lang_3Object_2_5I:
    push_word(paramBase[0]);
    break;
  case gc_4_5V:
    // Restartable garbage collection
    return garbage_collect();
  case shutDown_4_5V:
    display_clear(1);
    while (1) nxt_avr_power_down(); // does not return 
  case boot_4_5V:
    display_clear(1);
    while (1) nxt_avr_firmware_update_mode(); // does not return 
  case arraycopy_4Ljava_3lang_3Object_2ILjava_3lang_3Object_2II_5V:
    {
      Object *p1 = word2ptr(paramBase[0]);
      Object *p2 = word2ptr(paramBase[2]);
      return arraycopy(p1, paramBase[1], p2, paramBase[3], paramBase[4]);
    }
  case executeProgram_4I_5V:
    // Exceute program, allow for instruction re-start
    return execute_program(paramBase[0]);
  case setDebug_4_5V:
    set_debug(word2ptr(paramBase[0]));
    break;
  case eventOptions_4II_5I:
    {
      byte old = debugEventOptions[paramBase[0]];
      debugEventOptions[paramBase[0]] = (byte)paramBase[1];
      push_word(old);
    }
    break;
  case suspendThread_4Ljava_3lang_3Object_2_5V:
    suspend_thread(ref2ptr(paramBase[0]));
    break;
  case resumeThread_4Ljava_3lang_3Object_2_5V:
    resume_thread(ref2ptr(paramBase[0]));
    break;
  case getProgramExecutionsCount_4_5I:
    push_word(gProgramExecutions);
    break;
  case getFirmwareRevision_4_5I:
    push_word((STACKWORD) getRevision());
    break;
  case getFirmwareMajorVersion_4_5I:
    push_word((STACKWORD) MAJOR_VERSION);
    break;
  case getFirmwareMinorVersion_4_5I:
    push_word((STACKWORD) MINOR_VERSION); 
    break;
  case hsEnable_4_5V:
    {
      if (hs_enable() == 0)
        return EXEC_RETRY;
    }
    break;
  case hsDisable_4_5V:
    {
      hs_disable();
    }
    break;
  case hsWrite_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(hs_write(byteArray, paramBase[1], paramBase[2]));                      
    }
    break;
  case hsRead_4_1BII_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      byte *byteArray = (byte *) jbyte_array(p);
      push_word(hs_read(byteArray, paramBase[1], paramBase[2]));                      
    }
    break;
  case hsPending_4_5I:
    {
      push_word(hs_pending());
    }
    break;
  case hsSend_4BB_1BII_1C_5I:
    {
      Object *p = word2ptr(paramBase[2]);
      U8 *data = (U8 *)jbyte_array(p);
      p = word2ptr(paramBase[5]);
      U16 *crc = (U16 *)jchar_array(p);
      push_word(hs_send((U8) paramBase[0], (U8)paramBase[1], data, paramBase[3], paramBase[4], crc));
    }
    break;
  case hsRecv_4_1BI_1CI_5I:
    {
      Object *p = word2ptr(paramBase[0]);
      U8 *data = (U8 *)jbyte_array(p);
      p = word2ptr(paramBase[2]);
      U16 *crc = (U16 *)jchar_array(p);
      push_word(hs_recv(data, paramBase[1], crc, paramBase[3]));
    }
    break;
    
  case getUserPages_4_5I:
    push_word(FLASH_MAX_PAGES - flash_start_page);
    break;
  case setVMOptions_4I_5V:
    gVMOptions = paramBase[0];
    break;
  case getVMOptions_4_5I:
    push_word(gVMOptions);
    break;
  case isAssignable_4II_5Z:
    push_word(is_assignable(paramBase[0], paramBase[1]));
    break;
  case cloneObject_4Ljava_3lang_3Object_2_5Ljava_3lang_3Object_2:
    {
      Object *newObj = clone((Object *)ref2obj(paramBase[0]));
      if (newObj == NULL) return EXEC_RETRY;
      push_word(obj2ref(newObj));
    }
    break;
  case memPeek_4III_5I:
    push_word(mem_peek(paramBase[0], paramBase[1], paramBase[2]));
    break;
  case memCopy_4Ljava_3lang_3Object_2IIII_5V:
    mem_copy(word2ptr(paramBase[0]), paramBase[1], paramBase[2], paramBase[3], paramBase[4]);
    break;
  case memGetReference_4II_5Ljava_3lang_3Object_2:
    push_word(mem_get_reference(paramBase[0], paramBase[1]));
    break;
  case setSensorPin_4III_5V:
    sp_set(paramBase[0], paramBase[1], paramBase[2]);
    break;
  case getSensorPin_4II_5I:
    push_word(sp_get(paramBase[0], paramBase[1]));
    break;
  case setSensorPinMode_4III_5V:
    sp_set_mode(paramBase[0], paramBase[1], paramBase[2]);
    break;
  case readSensorPin_4II_5I:
    push_word(sp_read(paramBase[0], paramBase[1]));
    break;
  case nanoTime_4_5J:
    {
      U64 ns = systick_get_ns();
      push_word(ns >> 32);
      push_word(ns);
    }
    break;
  default:
    return throw_exception(noSuchMethodError);
  }
  return EXEC_CONTINUE;
}
