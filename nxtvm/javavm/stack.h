
#ifndef _STACK_H
#define _STACK_H

#include "configure.h"
#include "threads.h"
#include "interpreter.h"
#include "memory.h"
#include "language.h"

#define get_local_word(IDX_)       (localsBase[(IDX_)])
#define get_local_ref(IDX_)        (localsBase[(IDX_)])
#define inc_local_word(IDX_,NUM_)  (localsBase[(IDX_)] += (NUM_))
#define just_set_top_word(WRD_)    (stackTop[0] = (WRD_))
#define get_top_word()             (stackTop[0])
#define get_top_ref()              (stackTop[0])
#define get_word_at(DOWN_)         (*(stackTop-(DOWN_)))
#define get_ref_at(DOWN_)          *(stackTop-(DOWN_))
#define get_stack_ptr()            (stackTop)
#define get_stack_ptr_cur()        (curStackTop)
#define get_stack_ptr_at(DOWN_)    (stackTop-(DOWN_))
#define get_stack_ptr_at_cur(DOWN_)(curStackTop-(DOWN_))


/**
 * Clears the operand stack for the given stack frame.
 */
static inline STACKWORD * init_sp (StackFrame *stackFrame, MethodRecord *methodRecord)
{
  return stackFrame->localsBase + methodRecord->numLocals - 1;
}

/**
 * Clears/initializes the operand stack at the bottom-most stack frame,
 * and pushes a void (unitialized) element, which should be overriden
 * immediately with set_top_word or set_top_ref.
 */
static inline void init_sp_pv (void)
{
  curStackTop = stack_array();
}

/**
 * With stack cleared, checks for stack overflow in given method.
 */
static inline boolean is_stack_overflow (STACKWORD *stackTop, MethodRecord *methodRecord)
{
  return (stackTop + methodRecord->maxOperands) >= (stack_array() + get_array_length((Object *) word2ptr (currentThread->stackArray)));
}

extern void update_stack_frame (StackFrame *stackFrame);

extern void update_registers (StackFrame *stackFrame);

/**--**/

static inline void update_constant_registers (StackFrame *stackFrame)
{
  curLocalsBase = stackFrame->localsBase;
}

#define push_word(word)     (*(++stackTop) = word)
#define push_word_cur(word) (*(++curStackTop) = word)
#define push_ref(word)      (*(++stackTop) = word)
#define push_ref_cur(word)  (*(++curStackTop) = word)

#define pop_word()          (*stackTop--)
#define pop_ref()           (*stackTop--)

#define pop_jint()          ((JINT)word2jint(*stackTop--))
#define pop_word_or_ref()   (*stackTop--)

#define pop_jlong(lword)    ((lword)->sw.lo = *stackTop--, (lword)->sw.hi = *stackTop--)
#define push_jlong(lword)   (*++stackTop = (lword)->sw.hi, *++stackTop = (lword)->sw.lo)

#define pop_jdouble(jd)     ((jd)->sw.lo = *stackTop--, (jd)->sw.hi = *stackTop--)
#define push_jdouble(jd)   (*++stackTop = (jd)->sw.hi, *++stackTop = (jd)->sw.lo)

#define pop_words(aNum)     (stackTop -= aNum)
#define pop_words_cur(aNum) (curStackTop -= aNum)

#define just_pop_word()     (--stackTop)
#define just_pop_ref()      (--stackTop)

#define push_void()         (++stackTop)

#define set_top_ref(aRef)   (*stackTop = aRef)
#define set_top_ref_cur(aRef)(*curStackTop = aRef)
#define set_top_word(aWord) (*stackTop = aWord)

#define dup() \
{ \
  stackTop++; \
  *stackTop = *(stackTop-1); \
}

#define dup2() \
{ \
  *(stackTop+1) = *(stackTop-1); \
  *(stackTop+2) = *stackTop; \
  stackTop += 2; \
}

#define dup_x1() \
{ \
  stackTop++; \
  *stackTop = *(stackTop-1); \
  *(stackTop-1) = *(stackTop-2); \
  *(stackTop-2) = *stackTop; \
}

#define dup2_x1() \
{ \
  stackTop += 2; \
  *stackTop = *(stackTop-2); \
  *(stackTop-1) = *(stackTop-3); \
  *(stackTop-2) = *(stackTop-4); \
  *(stackTop-3) = *stackTop; \
  *(stackTop-4) = *(stackTop-1); \
}

#define dup_x2() \
{ \
  stackTop++; \
  *stackTop = *(stackTop-1); \
  *(stackTop-1) = *(stackTop-2); \
  *(stackTop-2) = *(stackTop-3); \
  *(stackTop-3) = *stackTop; \
}

#define dup2_x2() \
{ \
  stackTop += 2; \
  *stackTop = *(stackTop-2); \
  *(stackTop-1) = *(stackTop-3); \
  *(stackTop-2) = *(stackTop-4); \
  *(stackTop-3) = *(stackTop-5); \
  *(stackTop-4) = *stackTop; \
  *(stackTop-5) = *(stackTop-1); \
}

#define swap() \
{ \
  STACKWORD tempStackWord = *stackTop; \
  *stackTop = *(stackTop-1); \
  *(stackTop-1) = tempStackWord; \
}

#define set_local_word(aIndex,aWord)    (localsBase[aIndex] = aWord)
#define set_local_ref(aIndex, aWord)    (localsBase[aIndex] = aWord)

#endif

