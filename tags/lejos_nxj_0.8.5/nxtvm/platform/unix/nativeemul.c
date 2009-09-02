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

#define MAJOR_VERSION 0
#define MINOR_VERSION 6

#undef push_word
#undef push_ref
#define push_word( a) push_word_cur( a)
#define push_ref( a)  push_ref_cur( a)

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

#define DISPLAY_WIDTH 100
#define DISPLAY_DEPTH 8

static struct
{
  Object hdr;
  unsigned char display[DISPLAY_DEPTH+1][DISPLAY_WIDTH];
} __attribute__((packed)) display_array;

static char display_init() 
{
  // Initialise the array parameters so that the display can
  // be memory mapped into the Java address space
  display_array.hdr.flags.arrays.isArray = 1;
  // NOTE This object must always be marked, otherwise very, very bad
  // things will happen!
  display_array.hdr.flags.arrays.mark = 1;
  display_array.hdr.flags.arrays.length = 200;
  display_array.hdr.flags.arrays.isAllocated = 1;
  display_array.hdr.flags.arrays.type = T_INT;
  display_array.hdr.monitorCount = 0;
  display_array.hdr.threadId = 0;
}

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
    obj = word2obj(get_word_4_ns((byte*)(&(s->characters))));
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

boolean debug_uncaught_exception(Object * exception,
                          const Thread * thread,
                          const MethodRecord * methodRecord,
                          const MethodRecord * rootMethod,
                          byte * pc, int exceptionFrame)
{
  return false;
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
    case readSensorValue_4I_5I:
      // Parameters: int portId
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Reading sensor %d, returned value %d\n",paramBase[0], sensors[paramBase[0]].value);
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
        Object *charArray = (Object *) word2ptr(get_word_4_ns(p + HEADER_SIZE));
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
         printf("drawString called with parameters %s, %d, %d\n",buff,paramBase[1],paramBase[2]);
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
    case getDisplay_4_5_1B:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Get display\n");
      display_init();
      push_word((STACKWORD) ptr2word(&display_array));
      return;
    case setAutoRefresh_4I_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Set autodisplay to %d\n", paramBase[0]);
      return;
    case bitBlt_4_1BIIII_1BIIIIIII_5V:
      {
        Object *src = word2ptr(paramBase[0]);
        Object *dst = word2ptr(paramBase[5]);
        if (verbose)
          printf("> ");
        else
          printf("& ");
        printf("bitBlt called\n");
        return;
      }
    case getSystemFont_4_5_1B:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("getSystemFont called\n");
      push_word(0);
      return;    
    case getVoltageMilliVolt_4_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("getVoltageMillivolts returning 9999\n");
      push_word(9999);
      return;
    case getButtons_4_5I:
      if (verbose)
      {
         printf("> ");
         printf("readButtons returning 0\n");
      }     
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
    case controlMotorById_4III_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("controlMotor called with parameters %d, %d, %d\n",paramBase[0],paramBase[1],paramBase[2]);
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
    case playFreq_4III_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("playFreq with freq = %d, duration = %d, volume = %d\n", paramBase[0], paramBase[1], paramBase[2]);
      return;
    case btSend_4_1BI_5V:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose)
          printf("> ");
        else
          printf("& "); 
          printf("btSend called with parameters %x, %d\n", byteArray, paramBase[1]);                       
      }
      return;
    case btReceive_4_1B_5V:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose)
          printf("> ");
        else
          printf("& "); 
        printf("btReceive called with parameter %x\n", byteArray);                                           
      }
      return;
    case btGetBC4CmdMode_4_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
         printf("btGetBC4CmdMode returning 1\n");
      push_word(1);
      return;
    case btSetArmCmdMode_4I_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("btSetArmCmdMode\n");
      return;
    case btStartADConverter_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("btStartAdConverter\n");
      return;
    case btSetResetLow_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("btSetResetLow\n");
      return;
    case btSetResetHigh_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("btSetResetHigh\n");
    return;
    case btWrite_4_1BII_5I:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose)
          printf("> ");
        else
          printf("& "); 
        printf("btWrite called with parameters %x, %d, %d\n", byteArray,paramBase[1],paramBase[2]);                                           
      }
      return;
    case btRead_4_1BII_5I:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose)
          printf("> ");
        else
          printf("& "); 
        printf("btRead called with parameters %x, %d, %d\n", byteArray,paramBase[1],paramBase[2]);                                                               
      }
      return;
    case btPending_4_5I:
      if (verbose) 
      {
        printf("> ");
        printf("btPending called\n");                                           
      }
      push_word(0);
      return;
    case usbRead_4_1BII_5I:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose) 
        {
          printf("> ");
          printf("usbReceive called with parameters %x, %d\n", byteArray, paramBase[1]);                                           
        }
        push_word(0);                      
      } 
      return;
    case usbWrite_4_1BII_5I:
      {
        Object *p = word2ptr(paramBase[0]);
        byte *byteArray = (((byte *) p) + HEADER_SIZE);
        if (verbose) 
        {
          printf("> ");
          printf("usbWrite called with parameters %x, %d\n", byteArray, paramBase[1]);                                           
        }                     
      }
      return; 
    case usbStatus_4_5I:
      {
        push_word(0);
      }
      return;
    case usbEnable_4I_5V:
      {
        if (verbose) 
        {
          printf("> ");
          printf("usbEnable called\n");;                                           
        }
      }
      return;
    case usbDisable_4_5V:
      {
        if (verbose) 
        {
          printf("> ");
          printf("usbDisable called\n");;                                           
        }
      }
      return;
    case usbReset_4_5V :
      if (verbose) 
      {
        printf("> ");
        printf("udpReset called\n");                                           
      }
      return;
    case usbSetSerialNo_4Ljava_3lang_3String_2_5V: 
      {
        byte *p = word2ptr(paramBase[0]);
        int len;
        Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

        len = get_array_length(charArray);
        if (verbose) 
        {
          printf("> ");
          printf("udpSetSerial called\n");                                           
        }
      }
      return;
    case usbSetName_4Ljava_3lang_3String_2_5V:
      {
        byte *p = word2ptr(paramBase[0]);
        int len;
        Object *charArray = (Object *) word2ptr(get_word_4_ns(fields_start(p)));

        len = get_array_length(charArray);
        if (verbose) 
        {
          printf("> ");
          printf("udpSetName called\n");
        }                                            
      }
      return;
    case writePage_4_1BI_5V:
      {
        Object *p = word2ptr(paramBase[0]);
        unsigned long *intArray = (unsigned long *) (((byte *) p) + HEADER_SIZE);
        if (verbose) 
        {
          printf("> ");
          printf("writePage called with parameters %x, %d\n", intArray, paramBase[1]);                                           
        }                       
      }
      return;
    case readPage_4_1BI_5V:
      {
        int i;
        Object *p = word2ptr(paramBase[0]);
        unsigned long *intArray = (unsigned long *) (((byte *) p) + HEADER_SIZE);
        if (verbose) 
        {
          printf("> ");
          printf("readPage called with parameters %x, %d\n", intArray, paramBase[1]);                                           
        }                       
      }
      return;
    case exec_4II_5V:
      if (verbose) 
      {
        printf("> ");
        printf("exec called\n");                                           
      }
      return;
    case playSample_4IIIII_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Playing sound sample\n");
      return;
    case getTime_4_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Sound getTime called\n");
      push_word(0);
      return;   
    case getDataAddress_4Ljava_3lang_3Object_2_5I:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Data address is %x\n",ptr2word (((byte *) word2ptr (paramBase[0])) + HEADER_SIZE));
      return;
    case gc_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Collecting garbage\n");
      return;
    case diagn_4II_5I:
      push_word (sys_diagn(paramBase[0], paramBase[1]));
      return;
    case shutDown_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Shutting down\n");
      exit(0);
    case arraycopy_4Ljava_3lang_3Object_2ILjava_3lang_3Object_2II_5V:
      {
        Object *p1 = word2ptr(paramBase[0]);
        Object *p2 = word2ptr(paramBase[2]);
        arraycopy(p1, paramBase[1], p2, paramBase[3], paramBase[4]);
      }
      return;
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
      return;
    case setDebug_4_5V:
      if (verbose)
         printf("> ");
      else
         printf("& ");
      printf("Set debug\n");
      return;
    case peekWord_4I_5I:
      push_word(*((unsigned long *)(paramBase[0])));
      return;
    case eventOptions_4II_5I:
      {
        if (verbose)
          printf("> ");
        else
          printf("& ");
        printf("Debug event options\n");
        push_word(0);
      }
      return;
    case suspendThread_4Ljava_3lang_3Object_2_5V:
      suspend_thread(ref2ptr(paramBase[0]));
      return;
    case resumeThread_4Ljava_3lang_3Object_2_5V:
      resume_thread(ref2ptr(paramBase[0]));
      return;
    case getProgramExecutionsCount_4_5I:
      push_word(gProgramExecutions);
      return;
    case getFirmwareRevision_4_5I:
      push_word(0);
      return;
    case getFirmwareMajorVersion_4_5I:
      push_word((STACKWORD) MAJOR_VERSION);
      return;
    case getFirmwareMinorVersion_4_5I:
      push_word((STACKWORD) MINOR_VERSION); 
      return;
    default:
#ifdef DEBUG_METHODS
      printf("Received bad native method code: %d\n", signature);
#endif
      throw_exception (noSuchMethodError);
  }
} 
