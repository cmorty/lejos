#include "constants.h"
#include "poll.h"
#include "threads.h"
#include "platform_hooks.h"

#include "display.h"
#define MAX_EVENTS 16
#define WAITING 1
#define SET 2
NXTEvent *events[MAX_EVENTS];
int eventCnt;
static JINT (* const eventCheckers[])(JINT) = {null, bt_event_check, null, null, sp_check_event, i2c_event_check, buttons_check_event};

/**
 * Initialize the event system. At startup we are not monitoring for any events.
 */
void init_events()
{
  int i;
  for(i = 0; i < MAX_EVENTS; i++)
    events[i] = null;
  eventCnt = 0;
}

/**
 * register a new event object. After this the system will check for these
 * events, and notify the Java code if any fire.
 */
int register_event(NXTEvent *event)
{
  if (eventCnt >= MAX_EVENTS) return -1;
  events[eventCnt++] = event;
  return 1;
}

/**
 * remove an event object from being monitored. Following this the event will
 * no longer be checked by the system.
 */
int unregister_event(NXTEvent *event)
{
  int i;
  for(i = 0; i < eventCnt; i++)
    if (events[i] == event)
    {
      while(++i < eventCnt)
        events[i-1] = events[i];
      eventCnt--;
      return 1;
    }
  return -1;
}

/**
 * Check all active event objects. If an event is detected, notify the 
 * associated waiting Java thread.
 */
void check_events()
{
  int i;
  for(i = 0; i < eventCnt; i++)
  {
    NXTEvent *event = events[i];
    NXTEvent *sync = event->sync;
    // Is this event being waited on, and has the event check period expired?
    if (sync->state != 0 && (--event->updateCnt <= 0) && get_monitor_count(&(sync->_super.sync)) == 0)
    {
      // Check to see if we have anything of interest
      int changed = (*(eventCheckers[event->typ]))(event->filter);
      event->eventData |= changed;
      // notify the user thread
      if (changed && sync->state == WAITING)
      { 
        monitor_notify_unchecked(&sync->_super, 1);
        // Don't notify this thread again.
        sync->state |= SET;
        schedule_request(REQUEST_SWITCH_THREAD);
      }
      // Reste the check period counter
      event->updateCnt = event->updatePeriod;
    }
  }
}
