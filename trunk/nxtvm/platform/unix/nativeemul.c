/**
 * nativeemul.c
 * Native method handling for unix_impl (emulation).
 */
#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
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

static TWOBYTES gSensorValue = 0;

static char* sensorReadTypes[3] = {
  "RAW",
  "CANONICAL",
  "BOOLEAN"
};

static char *sensorSetTypes[5] = {
  "RAW",
  "TOUCH",
  "TEMP",
  "LIGHT",
  "ROT"
};

static char *getSensorMode(byte code)
{
  static char smBuffer[256];

  strcpy(smBuffer, "mode = ");  
  switch (code & 0xF0)
  {
    case 0x00: strcat(smBuffer, "RAW"); break;
    case 0x20: strcat(smBuffer, "BOOLEAN"); break;
    case 0x40: strcat(smBuffer, "EDGE"); break;
    case 0x60: strcat(smBuffer, "PULSE"); break;
    case 0x80: strcat(smBuffer, "PERCENT"); break;
    case 0xA0: strcat(smBuffer, "DEGC"); break;
    case 0xC0: strcat(smBuffer, "DEGF"); break;
    case 0xE0: strcat(smBuffer, "ANGLE"); break;
    default: sprintf(smBuffer, "mode = INVALID (0x%1X)", code & 0xF0); break;
  }
  
  sprintf(&smBuffer[strlen(smBuffer)], ", slope = %d", code & 0x0F);
  return smBuffer;
}

extern int	verbose;	/* If non-zero, generates verbose output. */
extern byte *region;

char *get_meaning(STACKWORD *);

void dump_flag(Object *obj)
{
  if (is_allocated(obj))
  {
    if (is_gc(obj))
    {
      printf("Ready for the garbage\n");
    }
    else if (is_array(obj))
    {
      printf("Array, type=%d, length=%d\n", get_element_type(obj), get_array_length(obj));
    }
    else
    {
      printf("Class index = %d\n", get_na_class_index(obj));
    }
  }
  else
  {
    printf ("Free block, length=%d\n", get_free_length(obj));
  }
  
  /**
   * Object/block flags.
   * Free block:
   *  -- bits 0-14: Size of free block in words.
   *  -- bit 15   : Zero (not allocated).
   * Objects:
   *  -- bits 0-7 : Class index.
   *  -- bits 8-12: Unused.
   *  -- bit 13   : Garbage collection mark.
   *  -- bit 14   : Zero (not an array).
   *  -- bit 15   : One (allocated).
   * Arrays:
   *  -- bits 0-8 : Array length (0-527).
   *  -- bits 9-12: Element type.
   *  -- bit 13   : Garbage collection mark.
   *  -- bit 14   : One (is an array).
   *  -- bit 15   : One (allocated).
   */

}
char* string2chp(String* s)
{
  char *ret = "null";
  if (s->characters)
  {
    int i;
    Object *obj;
    JCHAR *pA;
    int length;
    obj = word2obj(get_word((byte*)(&(s->characters)), 4));
    pA = jchar_array(obj);
    length = get_array_length(obj);
    ret = malloc(length+1);
    for (i=0; i<length; i++)
    {
      ret[i] = pA[i];
    }
    ret[i] = 0;
  }

  return ret;
}

/**
 * NOTE: The technique is not the same as that used in TinyVM.
 */
void dispatch_native (TWOBYTES signature, STACKWORD *paramBase)
{
	ClassRecord	*classRecord;

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
    case setPoller_4_5V:
      set_poller(word2ptr(paramBase[0]));
      return;
    case readSensorValue_4II_5I:
      // Parameters: int portId
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Reading sensor %ld, returned value %d\n",sensors[paramBase[0]].value);
      push_word (sensors[paramBase[0]].value);
      return;
    case setADTypeById_4II_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Setting sensor %d to AD type %d\n",paramBase[0], paramBase[1]);
      return;
    case setPowerTypeById_4II_5V:
       if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Setting sensor %d to power type %d\n",paramBase[0], paramBase[1]);
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
        printf("%s\n",string2chp((String*)word2ptr(paramBase[0])));
        throw_exception(error);
      }
      return;
    case testEQ_4Ljava_3lang_3String_2II_5V:
      if (paramBase[1] != paramBase[2])
      {
        printf("%s: expected %ld, got %ld\n",string2chp((String*)word2ptr(paramBase[0])), paramBase[1], paramBase[2]);
        throw_exception(error);
      }
      return;
    case floatToIntBits_4F_5I: // Fall through
    case intBitsToFloat_4I_5F:
      push_word (paramBase[0]);
      return;
    case drawString_4Ljava_3lang_3String_2II_5V:
      {
        byte *p = word2ptr(paramBase[0]);
        int len, i;
        //printf("displayString: pointer is %x\n",p);
        //printf("Object size is %d\n",sizeof(Object));
        Object *charArray = (Object *) word2ptr(get_word(p + HEADER_SIZE, 4));
        //printf("Chars is %x\n",charArray);
        len = charArray->flags.arrays.length;
        //printf("length is %d\n",len);
        {
         char buff[len+1];
         char *chars = ((char *) charArray) + HEADER_SIZE;
         //printf("chars is %x\n",chars);
         for(i=0;i<len;i++) buff[i] = chars[i+i];
         buff[len] = 0;
         if (verbose)
           printf("> ");
         else
           printf("& ");
         printf("drawString called with parameters %s,%d,%d\n",buff,paramBase[1],paramBase[2]);
        }
      }
      return;
    case drawInt_4III_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("drawInt called with parameters %d, %d, %d\n",paramBase[0],paramBase[1],paramBase[2]);
      return;
    case drawInt_4IIII_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("drawInt called with parameters %d, %d, %d, %d\n",paramBase[0],paramBase[1],paramBase[2],paramBase[3]);
    return; 
    case controlMotorById_4III_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("controlMotor called with parameters %d,%d,%d\n",paramBase[0],paramBase[1],paramBase[2]);
      return; 
    case refresh_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Displayed Refreshed\n");
      return;
    case clear_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Display cleared\n");
      return;
    case setDisplay_4_1I_5V:
       if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Display set\n");
      return;  
    case getVoltageMilliVolt_4_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("getVoltageMillivolts returning 9999\n");
      push_word(9999);
      return;
    case readButtons_4_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("readButtons returning 0\n");
      push_word(0);
      return;
    case getTachoCountById_4I_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("getTachoCount on Motor %d returning 0\n", paramBase[0]);
      push_word(0);
      return;  
    case resetTachoCountById_4I_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("resetTachoCount on Motor %d \n", paramBase[0]);
      return;  
    case i2cEnableById_4I_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("i2cEnableById\n"); 
      return;
    case i2cDisableById_4I_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("i2cDisableById\n");
      return;
    case i2cBusyById_4I_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("i2cBusyById\n");
      push_word(0);
      return;
    case i2cStartById_4IIII_1BII_5I:
      {
    	Object *p = word2ptr(paramBase[4]);
    	byte *byteArray = (((byte *) p) + HEADER_SIZE);
       if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("i2cStart called with parameters %d, %d, %d, %d %x, %d, %d\n",
                                        paramBase[0],
    	                                paramBase[1],
    	                                paramBase[2],
    	                                paramBase[3],
    	                                byteArray,
    	                                paramBase[5],
    	                                paramBase[6]);                      
      }
      push_word(0);
      return; 
    case playTone_4II_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("playTone with tone = %d, duration = %d\n", paramBase[0], paramBase[1]);
      return;
    case btSend_4_1BI_5V:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose)
          printf("> ");
        else
          printf("& "); 
          printf("btSend with parameter &d\n", byteArray);                       
      }
      return;
    case btReceive_4_1B_5V:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose) 
        {
          printf("> ");
          printf("btReceive called with parameter %x\n", byteArray);                                           
        }
      }
      return;
    case btGetCmdMode_4_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("btGetCmdMode returning 1\n");
      push_word(1);
      break;
    case btSetCmdMode_4I_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("btSetCmdMode\n");
      break;
    case btStartADConverter_4_5V:
      break;
    default:
#ifdef DEBUG_METHODS
      printf("Received bad native method code: %d\n", signature);
#endif
      throw_exception (noSuchMethodError);
  }
} 
