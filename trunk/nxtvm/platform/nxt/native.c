/**
 * native.c
 * Native method handling for nxt (dummy).
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

extern byte *region;

/**
 * NOTE: The technique is not the same as that used in TinyVM.
 */
void dispatch_native (TWOBYTES signature, STACKWORD *paramBase)
{
  ClassRecord	*classRecord;

  *((char *) 0x20F000) = 99;

  switch (signature)
  {
    case wait_4_5V:
      monitor_wait((Object*) word2ptr(paramBase[0]), 0);
      return;
    case wait_4J_5V:
      monitor_wait((Object*) word2ptr(paramBase[0]), paramBase[2]);
      return;
    case notify_4_5V:
      monitor_notify((Object*) word2ptr(paramBase[0]), false);
      return;
    case notifyAll_4_5V:
      monitor_notify((Object*) word2ptr(paramBase[0]), true);
      return;
    case start_4_5V:
      init_thread ((Thread *) word2ptr(paramBase[0]));
      return;
    case yield_4_5V:
      schedule_request( REQUEST_SWITCH_THREAD);
      return;
    case sleep_4J_5V:
      sleep_thread (paramBase[1]);
      schedule_request( REQUEST_SWITCH_THREAD);
      return;
    case getPriority_4_5I:
      push_word (get_thread_priority ((Thread*)word2ptr(paramBase[0])));
      return;
    case setPriority_4I_5V:
      {
        STACKWORD p = (STACKWORD)paramBase[1];
        if (p > MAX_PRIORITY || p < MIN_PRIORITY)
          throw_exception(illegalArgumentException);
        else
          set_thread_priority ((Thread*)word2ptr(paramBase[0]), p);
      }
      return;
    case currentThread_4_5Ljava_3lang_3Thread_2:
      push_ref(ptr2ref(currentThread));
      return;
    case interrupt_4_5V:
      interrupt_thread((Thread*)word2ptr(paramBase[0]));
      return;
    case interrupted_4_5Z:
      {
      	JBYTE i = currentThread->interruptState != INTERRUPT_CLEARED;
      	currentThread->interruptState = INTERRUPT_CLEARED;
      	push_word(i);
      }
      return;
    case isInterrupted_4_5Z:
      push_word(((Thread*)word2ptr(paramBase[0]))->interruptState
                != INTERRUPT_CLEARED);
      return;
    case setDaemon_4Z_5V:
      ((Thread*)word2ptr(paramBase[0]))->daemon = (JBYTE)paramBase[1];
      return;
    case isDaemon_4_5Z:
      push_word(((Thread*)word2ptr(paramBase[0]))->daemon);
      return;
    case join_4_5V:
      join_thread((Thread*)word2ptr(paramBase[0]));
      return;
    case join_4J_5V:
      join_thread((Thread*)word2obj(paramBase[0]));
      return;
    case exit_4I_5V:
      schedule_request(REQUEST_EXIT);
      return;
    case currentTimeMillis_4_5J:
      push_word (0);
      push_word (get_sys_time());
      return;
    case call_4S_5V:
	return;      
    case call_4SS_5V:
        if (paramBase[0] == 0x1946) activate((int) (paramBase[1] - 0x1000));
        if (paramBase[0] == 0x19C4) passivate((int) (paramBase[1] - 0x1000));
	return;      
    case call_4SSS_5V:
	return;      
    case call_4SSSS_5V:
	return;      
    case call_4SSSSS_5V:
      return;      
    case readByte_4I_5B:
	push_word (0);
	return;
    case writeByte_4IB_5V:
      return;
    case setBit_4III_5V:
      return;      
    case getDataAddress_4Ljava_3lang_3Object_2_5I:
      push_word (ptr2word (((byte *) word2ptr (paramBase[0])) + HEADER_SIZE));
      return;
    case resetSerial_4_5V:
      return;
    case setPoller_4_5V:
      set_poller(word2ptr(paramBase[0]));
      return;
    case readSensorValue_4II_5I:
      // Parameters: int romId (0..2), int requestedValue (0..2).
      push_word (sensors[paramBase[0]].value);
      return;
    case setSensorValue_4III_5V:
      // Arguments: int romId (1..3), int value, int requestedValue (0..3) 
      switch ((byte) paramBase[2])
      {
         case 0:
           return;
         case 1:	      
           return;
         case 2:
           sensors[paramBase[0]].value = paramBase[1];
           return;
         case 3:
           sensors[paramBase[0]].value = paramBase[1];
           return;
      }
      return;
    case freeMemory_4_5J:
      push_word (0);
      push_word (getHeapFree());
      return;
    case totalMemory_4_5J:
      push_word (0);
      push_word (getHeapSize());
      return;
    case test_4Ljava_3lang_3String_2Z_5V:
      if (!paramBase[1])
      {
        throw_exception(error);
      }
      return;
    case testEQ_4Ljava_3lang_3String_2II_5V:
      if (paramBase[1] != paramBase[2])
      {
        throw_exception(error);
      }
      return;
    case floatToIntBits_4F_5I: // Fall through
    case intBitsToFloat_4I_5F:
      push_word (paramBase[0]);
      return;
    case init_4_5V:
      return;
    case read_4_5I:
      push_word(0);
      return;
    case write_4_1BI_5V:
      return;
    case isSending_4_5Z:
      push_word(0);
      return;
    case isSendError_4_5Z:
      push_word(0);
      return;   
    case getRegionAddress_4_5I:
      push_word ((int)region);
      return;
    default:
      throw_exception (noSuchMethodError);
  }
} 
