/**
 * poll.h
 * Contains conterparts of special classes as C structs.
 */
 
#ifndef _POLL_H
#define _POLL_H

#include "constants.h"
#include "classes.h"

/**
 * NXTEvent class as native structure
 */
typedef struct S_NXTEvent
{
  Object _super;	     // Superclass object storage
  JINT state;
  struct S_NXTEvent *sync;
  JINT updatePeriod;
  JINT updateCnt;
  JINT typ;
  JINT filter;
  JINT eventData;
  JINT userEvents;
} NXTEvent;
extern void check_events();
extern int register_event(NXTEvent *event);
extern int unregister_event(NXTEvent *event);
extern int change_event(NXTEvent *event, JINT set, JINT clear);
extern void init_events();

#endif // _POLL_H
