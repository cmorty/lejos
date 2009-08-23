#include "constants.h"

#include "poll.h"
#include "threads.h"
#include "platform_hooks.h"
#include "sensors.h"

#define SENSOR_POS		0
#define BUTTON_POS		4
#define SERIAL_RECEIVED_POS	9

Poll *poller;
short old_sensor_values[N_SENSORS];
short old_button_state;

byte throttle;
byte throttle_count;

void set_poller(Poll *_poller)
{
  byte i;
  
  poller = _poller;
  old_button_state = 0;
  for (i=0; i<N_SENSORS; i++)
    old_sensor_values[i] = sp_read(i, SP_ANA);
}


void poll_inputs()
{
  short changed = 0;
  short button_state = 0;    
  short i;
  short port = 0;
  short *pOldValue = old_sensor_values;

  throttle_count--;
  if( throttle_count == 0){
    throttle_count = throttle;

    // If we're not polling or someone already has the monitor
    // return.
    if (!poller || get_monitor_count((&(poller->_super.sync))) != 0)
      return;

    // We do not have a thread that we can use to grab
    // the monitor but that's OK because we are atomic
    // anyway.
      
    // Check the sensor canonical values.
    for (i = 1<<SENSOR_POS; i<(1<<BUTTON_POS); i <<= 1, pOldValue++, port++)
    {
      short val = (short)sp_read(port, SP_ANA);
      if (*pOldValue != val) {
        changed |= i;
        *pOldValue = val;
      }
    }

    // Check the button status
    button_state = buttons_get();
    button_state <<= BUTTON_POS; // Shift into poll position  
    changed |= button_state ^ old_button_state;
    old_button_state = button_state;

    // Only wake threads up if things have changed since
    // we last looked.    
    if (changed)
    {
      // Or in the latest changes. Some threads may not have
      // responded to earlier changes yet so we can't
      // just overwrite them.
      short jword = 0;
      store_word_ns((byte*)(&jword), T_SHORT, changed);
      poller->changed |= jword;
      
#if DEBUG_POLL
      jword = get_word((byte*)&poller->changed, 2);
      printf("Poller: poller->changed = 0x%1X\n", jword);      
#endif
           
      // poller.notifyAll()
      monitor_notify_unchecked(&poller->_super, 1);
    }
  }
}
