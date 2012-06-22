/*
 * breakpoints.h
 *
 *  Created on: 10.05.2011
 *      Author: felix
 */

#ifndef BREAKPOINTS_H_
#define BREAKPOINTS_H_

#include "classes.h"
#include "language.h"

typedef struct S_Breakpoint
{
  Object _super;
  JINT methodId;
  JINT pc;
  JINT refCount;
  JBOOLEAN enabled;
  byte opcode;
} Breakpoint;

#define breakpoint_get_method(bp) (get_method_record(get_class_record(0), ((Breakpoint*)bp)->methodId))
#define breakpoint_get_pc(bp) (get_code_ptr(breakpoint_get_method(bp)) + ((Breakpoint*)bp)->methodId)

void init_breakpoint();
void breakpoint_set_list(Breakpoint**list, int length);
void breakpoint_enable(Breakpoint* point, boolean enable);
Breakpoint* breakpoint_get(MethodRecord* method, byte* pc);
int check_breakpoint(MethodRecord *method, byte * pc);



#endif /* BREAKPOINTS_H_ */
