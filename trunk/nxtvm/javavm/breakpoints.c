/*
 * breakpoints.c
 *
 *  Created on: 10.05.2011
 *      Author: felix/andy
 */
#include "breakpoints.h"
#include "opcodes.h"
#include "threads.h"
#include "flashprog.h"
#include "language.h"
#include "exceptions.h"
#include "debug.h"

Breakpoint**breakpointList;
int breakpointCount;

void init_breakpoint()
{
  breakpointList = null;
  breakpointCount = 0;
}

/**
 * Insert or remove breakpoints from the code held in flash memory
 */
static void insertCode(Breakpoint** list, int count, boolean set)
{
#if EXECUTE_FROM_FLASH
  int curPage = -1;
  volatile FOURBYTES *curBuf = null;
  FOURBYTES wordBuf;
  byte *bufPtr = (byte *) &wordBuf;
  int i;

  for (i = 0; i < count; i++)
  {
    Breakpoint *bp = list[i];
    byte *address = get_code_ptr(breakpoint_get_method(bp)) + bp->pc;
    int page = FLASH_PAGE(address);
    int wordOffset = ((FOURBYTES *) address
        - &FLASH_BASE[page * FLASH_PAGE_SIZE]);
    int byteOffset = address
        - (byte *) &FLASH_BASE[page * FLASH_PAGE_SIZE + wordOffset];
    volatile FOURBYTES *code = &FLASH_BASE[page * FLASH_PAGE_SIZE];
    byte newVal;
    if (page != curPage)
    {
      int j;
      // write out the old page
      if (curPage >= 0)
        flash_write_page_buffer((FOURBYTES *) curBuf, curPage);
      curPage = page;
      // fill the current page buffer with the existing code
      curBuf = flash_get_page_buffer(page);
      for (j = 0; j < FLASH_PAGE_SIZE; j++)
        curBuf[j] = code[j];
    }
    // write the new value (must be written word at a time)
    wordBuf = code[wordOffset];
    if (set)
    {
      bp->opcode = *address;
      newVal = OP_BREAKPOINT;
    }
    else
      newVal = bp->opcode;
    bufPtr[byteOffset] = newVal;
    curBuf[wordOffset] = wordBuf;
  }
  // write out the old page
  if (curPage != -1)
    i = flash_write_page_buffer((FOURBYTES *) curBuf, curPage);
#endif
}

void breakpoint_set_list(Breakpoint**list, int length)
{
  if (breakpointList != null)
    insertCode(breakpointList, breakpointCount, false);
  breakpointList = list;
  breakpointCount = length;
  if (breakpointList != null)
    insertCode(breakpointList, breakpointCount, true);
}

void breakpoint_enable(Breakpoint* point, boolean enable)
{
  if(point->enabled == enable)
    return;
#if !EXECUTE_FROM_FLASH
  // In the flash case we leave the breakpoint in place to save flash writes
  MethodRecord* methodRecord = breakpoint_get_method(point);
  byte* pc = get_code_ptr(methodRecord) + point->pc;
  if (enable)
  {
    point->opcode = *pc;
    *pc = OP_BREAKPOINT;
  }
  else
    *pc = point->opcode;
#else

#endif
  point->enabled = enable;
}

Breakpoint* breakpoint_get(MethodRecord* method, byte* pc)
{
  if (!breakpointList || breakpointCount == 0)
    return null;

  MethodRecord *methodBase = get_method_table(get_class_record(0));
  int methodId = method - methodBase;
  int localPc = pc - get_code_ptr(method);

  // perform a simple binary searh as the breakpoint list is sorted
  int low = 0;
  int high = breakpointCount - 1;

  while (low <= high)
  {
    int mid = (low + high) / 2;
    Breakpoint* midVal = breakpointList[mid];
    int cmp = midVal->methodId - methodId;
    if (cmp == 0)
      cmp = midVal->pc - localPc;

    if (cmp < 0)
      low = mid + 1;
    else if (cmp > 0)
      high = mid - 1;
    else
      return midVal; // key found
  }
  return (Breakpoint*) null; // key not found
}

/**
 * Check to see if we sure execute a breakpoint request. 
 * Return < 0 if breakpoint request is handled
 * Return  and op code >= 0 if not.
 */
int check_breakpoint(MethodRecord* method, byte *pc)
{
  Breakpoint *bp = breakpoint_get(method, pc);
  // if we have failed to find it then this means we have a stale breakpoint
  // so we abort the program
  if (!bp)
    throw_new_exception(JAVA_LANG_ERROR);
  else
  {
    // are we going to execute the breakpoint
    if (is_system(currentThread) || currentThread->state == BREAKPOINT
        || is_stepping(currentThread) || !bp->enabled)
    {
      // no so return to running state, and execute the original opcode
      set_thread_debug_state(currentThread, RUNNING, null);
      return bp->opcode;
    }
    else
    {
      // enter breakpoint state
      set_thread_debug_state(currentThread, BREAKPOINT, null);
      debug_breakpoint(currentThread,
          method - get_method_table(get_class_record(0)),
          ptr2word(pc - (get_binary_base() + method->codeOffset)));
    }
  }
  return -1;
}
