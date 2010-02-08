
#include "types.h"
#include "constants.h"
#include "classes.h"

#ifndef _INTERPRETER_H
#define _INTERPRETER_H

#define REQUEST_TICK          0
#define REQUEST_SWITCH_THREAD 1
#define REQUEST_EXIT          2

extern volatile boolean gMakeRequest;
extern byte    gRequestCode;
extern unsigned int gVMOptions;

// Allow external control of VM
#define VM_DEFAULT 0
#define VM_TYPECHECKS 1
#define VM_ASSERTS 2
#define VM_MEMCOMPACT 4
#define type_checks_enabled() (gVMOptions & VM_TYPECHECKS)
#define memory_compact_enabled() (gVMOptions & VM_MEMCOMPACT)

#define getPc() (curPc + curPcOffset)

extern int curPcOffset;
extern byte *curPc;
extern STACKWORD *curStackTop;
extern STACKWORD *curLocalsBase;

extern byte *old_pc;
extern unsigned int debug_word1, debug_word2;

// Temp globals:

//extern byte tempByte;
//extern STACKWORD tempStackWord;

extern void engine();
#if FAST_DISPATCH
typedef const unsigned short DISPATCH_LABEL;
extern DISPATCH_LABEL * volatile dispatchTable;
extern DISPATCH_LABEL *checkEvent;
#define FORCE_EVENT_CHECK() (gMakeRequest = true, dispatchTable = checkEvent)
#else
#define FORCE_EVENT_CHECK() (gMakeRequest = true)
#endif

static inline void schedule_request (const byte aCode)
{
  FORCE_EVENT_CHECK();
  gRequestCode = aCode;
}

#endif /* _INTERPRETER_H */





