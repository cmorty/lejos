/***************************************************************************
 * File:	verbose.c
 * Purpose:	Provides verbose descriptions of each system call to the
 *		interpreter.
 * Author:	Phil Berry (phil.berry@xinetica.com)
 * Date:	December, 2000
 * Copyright:	(c) Phil Berry, 2000.
 * Notes:	This file contains routines that are used by nativeemul.c
 *		to print descriptive text messages, rather than numeric
 *		output.
 *		For this to be optional, a switch has been added to tvmemul.c
 *		so the user can choose whether to use these routines or not.
 *		For details see that file.
 *
 * Todo:	There are several things that still need to be done in this
 *		file. I have marked them with Todo:, but they are summarised
 *		here.
 * 		--0x1ff2 set_lcd_number
 *		Todo: This should be modifed to just print the display that
 *		appears on the LCD. It does not matter that it is signed,
 *		unsigned or whatever. What you see on the RCX you see here.
 *		--0x29f2 get_power_status
 *		Todo: add call to srand() to set the seed to the current time,
 *		so the value assigned to power is variable. Also need to check
 *		the range of values that power can take - it seems that 65535
 *		is far too big.
 *		--0x30d0 init_serial
 *		Todo: Either this is a biggie, or it's not used and so we can
 *		ignore it.		    
 *		--0x3426 check_for_data
 *		Todo: should this show more than the operation?
 *		--0x3de0 control_output
 *		Todo: add code to 700a to indicate which sensor.
 ***************************************************************************/

#include	<stdlib.h>	/* For rand(). */
#include	"types.h"
char *get_lcd_segment_msg(char *, STACKWORD);

static char* motorModes[5] = {
  "UNKNOWN",
  "FORWARD",
  "BACKWARD",
  "BRAKE",
  "FLOAT"
};

char	outstring[80];	/* No line should be longer than 80 chars. */

/***************************************************************************
 * char *get_meaning
 *
 * This returns a formated string describing the action. Note that it may not
 * have the same format as the raw output as it used the information in all the
 * arguments to build a sensible description.
 *
 * STACKWORD is a typedef of FOURBYTES (from ../vmsrc/types.h), and FOURBYTES
 * itself is an unsigned long defined in platform_config.h.
 * The commentary is from the rom.h file of the librcx package.
 ***************************************************************************/
char *get_meaning(STACKWORD *paramBase)
{

	switch (paramBase[0]) {
	case 0x1498:
		/********************************************
		 * 0x1498 init_sensors
		 * No parameters.
		 *******************************************/
		return "init_sensors";
	case 0x14c0:
		/********************************************
		 * 0x14c0 read_sensor
		 * 1000	Read sensor 0
		 * 1001	Read sensor 1
		 * 1002	Read sensor 2
		 *******************************************/
		switch (paramBase[1]) {
		case 0x1000:
			sprintf(outstring, "read_sensor 0");
			return outstring;
		case 0x1001:
			sprintf(outstring, "read_sensor 1");
			return outstring;
		case 0x1002:
			sprintf(outstring, "read_sensor 2");
			return outstring;
		default:
			sprintf(outstring, "read_sensor: unknown code: 0x%0lx", paramBase[1]);
			return outstring;
		}
	case 0x1946:
		/********************************************
		 * 0x1946 set_sensor_active
		 * 1000	Set sensor 0 active
		 * 1001	Set sensor 1 active
		 * 1002	Set sensor 2 active
		 *******************************************/
		switch (paramBase[1]) {
		case 0x1000:
			sprintf(outstring, "set_sensor_active 0");
			return outstring;
		case 0x1001:
			sprintf(outstring, "set_sensor_active 1");
			return outstring;
		case 0x1002:
			sprintf(outstring, "set_sensor_active 2");
			return outstring;
		default:
			sprintf(outstring, "set_sensor: unknown code: 0x%0lx", paramBase[1]);
			return outstring;
		}
	case 0x19c4:
		return "set_sensor_passive";
	case 0x1a22:
		/********************************************
		 * 0x1a22 shutdown_sensors
		 * No parameters.
		 *******************************************/
		return "shutdown_sensors";
	case 0x1a4e:
		/********************************************
		 * 0x1a4e control_motor
		 * 2000	Control motor 0
		 * 2001	Control motor 1
		 * 2002	Control motor 2
		 *******************************************/
		switch (paramBase[1]) {
		case 0x2000:
		case 0x2001:
		case 0x2002:
			sprintf(outstring, "set_motor %ld %s %ld", paramBase[1] & 3, motorModes[paramBase[2]], paramBase[3] & 7);
			return outstring;
		default:
			sprintf(outstring, "set_motor: unknown code: 0x%0lx", paramBase[1]);
			return outstring;
		}
	case 0x1aba:
		/********************************************
		 * 0x1aba init_buttons
		 * No parameters.
		 *******************************************/
		return "init_buttons";
	case 0x1b32:
		/********************************************
		 * 0x1b32 play_view_button_sound
		 * 301e	Play view button sound
		 *******************************************/
		switch (paramBase[1]) {
		case 0x301e:
			return "play_view_button_sound";
		default:
			sprintf(outstring, "play_view_button_sound: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x1b62:
		/********************************************
		 * 0x1b62 set_lcd_segment
		 *******************************************/
		return get_lcd_segment_msg("set", paramBase[1]);
	case 0x1e4a:
		/********************************************
		 * 0x1e4a clear_lcd_segment
		 *******************************************/
		return get_lcd_segment_msg("clear", paramBase[1]);
		return outstring;
	case 0x1fb6:
		/********************************************
		 * 0x1fb6 read_buttons
		 * 3000	Read buttons
		 *******************************************/
		switch (paramBase[1]) {
		case 0x3000:
			return "read_buttons";
		default:
			sprintf(outstring, "read_buttons: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x1ff2:
		/********************************************
		 * 0x1ff2 set_lcd_number
		 * Param1
		 * 3017	Set LCD program number
		 * 301f	Set LCD main number unsigned
		 * Param2
		 * The numeric value
		 * Param3
		 * 3002	no decmial point (param1=3001,301f)
		 * 3003	000.0 format (param1=3001,301f)
		 * 3004	00.00 format (param1=3001,301f)
		 * 3005    0.000 format (param1=3001,301f)
		 *------------------------------------------
		 * Todo: This should be modifed to just print
		 * the display that appears on the LCD. It
		 * does not matter that it is signed, unsigned
		 * or whatever. What you see on the RCX you
		 * see here.
		 *------------------------------------------
		 *******************************************/
		if(paramBase[1] == 0x3001) {	/* Signed. */
			if(paramBase[3] == 0x3002) {
				sprintf(outstring, "set_lcd_number (signed) %ld",
					paramBase[2]);
			}
			if(paramBase[3] == 0x3003) {
				sprintf(outstring, "set_lcd_number (signed) %5.1ld",
					paramBase[2]/10);
			}
			if(paramBase[3] == 0x3004) {
				sprintf(outstring, "set_lcd_number (signed) %5.2ld",
					paramBase[2]/100);
			}
			if(paramBase[3] == 0x3005) {
				sprintf(outstring, "set_lcd_number (signed) %5.3ld",
					paramBase[2]/1000);
			}
		}
		if(paramBase[1] == 0x3017) {	/* Program number. */
			sprintf(outstring, "set_lcd_number program number=%ld", paramBase[2]);
		}
		if(paramBase[1] == 0x301f) {	/* Unsigned. */
			if(paramBase[3] == 0x3002) {
				sprintf(outstring, "set_lcd_number (unsigned) %ld",
					paramBase[2]);
			}
			if(paramBase[3] == 0x3003) {
				sprintf(outstring, "set_lcd_number (unsigned) %5.1ld",
					paramBase[2]/10);
			}
			if(paramBase[3] == 0x3004) {
				sprintf(outstring, "set_lcd_number (unsigned) %5.2ld",
					paramBase[2]/100);
			}
			if(paramBase[3] == 0x3005) {
				sprintf(outstring, "set_lcd_number (unsigned) %5.3ld",
					paramBase[2]/1000);
			}
		}
		return outstring;
	case 0x27ac:
		/********************************************
		 * 0x27ac clear_display
		 * No parameters.
		 *******************************************/
		return "clear_display";
	case 0x27c8:
		/********************************************
		 * 0x27c8 refresh_display
		 * No parameters.
		 *******************************************/
		return "refresh_display";
	case 0x27f4:
		/********************************************
		 * 0x27f4 shutdown_buttons
		 * No parameters.
		 *******************************************/
		return "shutdown_buttons";
	case 0x2964:
		/********************************************
		 * 0x2964 init_power
		 * No parameters.
		 *******************************************/
		return "init_power";
	case 0x299a:
		/********************************************
		 * 0x299a play_system_sound
		 * code=4003: Play unqueued system sound
		 *   Calls 3de0 to play an unqueued system sound, index of sound is sound
		 * code=4004: Play queued system sound
		 *   Calls 3de0 to play a queued system sound, index of sound is sound
		 * In either case, if sound is not in 0..5, the sound is 4 (low buzz)
		 *
		 * Why is this an 0x4000 function?
		 *   Because it is called for power on/off sounds only
		 *******************************************/
		if(paramBase[2] > 5) paramBase[2] = 4;
		if(paramBase[2] < 0) paramBase[2] = 4;
		switch(paramBase[1]) {
		case 0x4003:	/* Play unqueued system sound. */
			sprintf(outstring, "play_system_sound unqueued sound %ld",
				paramBase[2]);
			return outstring;
		case 0x4004:	/* Play queued system sound. */
			sprintf(outstring, "play_system_sound queued sound %ld",
				paramBase[2]);
			return outstring;
		default:
			sprintf(outstring, "play_system_sound: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x29f2:
		/********************************************
		 * 0x29f2 get_power_status
		 * code=4000: Read on/off key
		 *   Read port 4 bit 1 (@ffb7 & 0x02)
		 *   Set *ptr to 0x20 if bit is set, set *ptr to 0x00 otherwise
		 * code=4001: Read battery power
		 *   Read a/d register d (@ffe6)
		 *   Set *ptr to value from a/d register
		 *   Units are strange, multiply by 43988 then divide by 1560 to get mV
		 * Todo: add call to srand() to set the seed
		 * to the current time, so the value assigned
		 * to power is variable. Also need to check
		 * the range of values that power can take -
		 * it seems that 65535 is far too big.
		 *******************************************/
		switch(paramBase[1]) {
		case 0x4000:	/* Read on/off key. */
			/* Should we just do nothing? */
			return "get_power_status: read on/off key";
		case 0x4001:	/* Read battery power. */
			/* Get register d. Assume this is 65535.
			 * The best way to do this is to get a random
			 * number between 0 and 65535. Then multiply
			 * by 43988/1560 to get mV.
			 */
			{
				int	power = rand();
				power = 1 + (int) (65535.0 * rand()/(RAND_MAX+1.0));
				sprintf(outstring, "get_power_status %d mV",
					power * 43988/1560);
				return outstring;
			}
		default:
			sprintf(outstring, "get_power_status: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x2a62:
		/********************************************
		 * 0x2a62 shutdown_power
		 * No parameters.
		 *******************************************/
		return "shutdown_power";
	case 0x30d0:
		/********************************************
		 * 0x30d0 init_serial
		 * YET TO WRITE 
		 * Todo: Either this is a biggie, or it's not
		 * used and so we can ignore it.		    
		 *******************************************/
		return "init_serial";
	case 0x3250:
		/********************************************
		 * 0x3250 set_range_long
		 * 1770	Set range long
		 *******************************************/
		switch (paramBase[1]) {
		case 0x1770:
			return "set_range_long";
		default:
			sprintf(outstring, "set_range_long: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x3266:
		/********************************************
		 * 0x3250 set_range_short
		 * 1770	Set range short
		 *******************************************/
		switch (paramBase[1]) {
		case 0x1770:
			return "set_range_short";
		default:
			sprintf(outstring, "set_range_short: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x327c:
		/********************************************
		 * 0x327c play_sound_or_set_data_pointer
		 * 1771	Set data pointer
		 * 1772 Play system sound
		 * 1773	Play tone
		 *******************************************/
		switch (paramBase[1]) {
		case 0x1771:	/* Set data pointer. */
			return "play_sound_or_set_data_pointer: set data pointer";
		case 0x1772:	/* Play system sound. */
			sprintf(outstring, "play_sound_or_set_data_pointer "
				"sound 0x%0lx", paramBase[3]);
			return outstring;
		case 0x1773:	/* Play tone. */
			sprintf(outstring, "play_sound_or_set_data_pointer"
				"tone freq 0x%0lx, duration 0x%0lx",
				paramBase[2], paramBase[3]);
			return outstring;
		default:
			sprintf(outstring, "play_sound_or_set_data_pointer: "
				"unknown code: 0x%0lx", paramBase[1]);
			return outstring;
		}
	case 0x339a:
		/********************************************
		 * 0x339a reset_minute_timer
		 * No parameters.
		 *******************************************/
		return "reset_minute_timer";
	case 0x33b0:
		/********************************************
		 * 0x33b0: receive_data
		 *******************************************/
		return "receive_data";
	case 0x3426:
		/********************************************
		 * 0x3426 check_for_data
		 * Todo: should this show more than the operation?
		 *******************************************/
		return "check_for_data";
	case 0x343e:
		/********************************************
		 * 0x343e send_data
		 *******************************************/
		switch (paramBase[1]) {
		case 0x1775:	/* Send data short. */
			return "send_data: send data short";
		case 0x1776:	/* Send data long. */
			return "send_data: send data long";
		default:
			sprintf(outstring, "send_data: "
				"unknown code: 0x%0lx", paramBase[1]);
			return outstring;
		}
	case 0x3636:
		/********************************************
		 * 0x3636 shutdown_serial
		 * No parameters.
		 *******************************************/
		return "shutdown_serial";
	case 0x3692:
		/********************************************
		 * 0x3692 init_port_6_bit_3
		 * No parameters.
		 *******************************************/
		return "init_port_6_bit_3";
	case 0x36aa:
		/********************************************
		 * 0x36aa shutdown_port_6_bit_3
		 * No parameters.
		 *******************************************/
		return "shutdown_port_6_bit_3";
	case 0x3b9a:
		/********************************************
		 * 0x3b9a init_timer
		 * No parameters.
		 * Should not see this!
		 *******************************************/
		return "init_timer";
	case 0x3ccc:
		/********************************************
		 * 0x3ccc get_sound_playing_flag
		 *******************************************/
		switch (paramBase[1]) {
		case 0x700c:	/* Copy sound playing flag. */
			return "get_sound_playing_flag: copy sound playing flag";
		default:
			sprintf(outstring, "get_sound_playing_flag: "
				"unknown code: 0x%0lx", paramBase[1]);
			return outstring;
		}
	case 0x3de0:
		/********************************************
		 * 0x3de0 control_output
		 * code=700a: Control sensor output (short code, short bits, char u0, char u1)
		 *   Sets output to sensors by setting @efd5 |= bits
		 *     bits: 0x01=sensor 2, 0x02=sensor 1, 0x04=sensor 0
		 * code=700b: Play queued sound (short code, short type, char p0, char p1)
		 *   Play a queued sound
		 *   If type = 0, Play pause (short code, short type, char duration, char u1)
		 *     Pause for duration ms
		 *   If type = 1, Play sound (short code, short type, char sound, char u1)
		 *     Play the specified system sound
		 *   If type > 1, Play tone (short code, short pitch, char duration, char ctl)
		 *     Play the tone specified by pitch/ctl for duration ms
		 * code=700d: Play unqueued sound (short code, short type, char p0, char p1)
		 *   Play an unqueued sound; flush sound queue before playing
		 *   If type = 0, Play pause (short code, short type, char duration, char u1)
		 *     Pause for duration ms
		 *   If type = 1, Play sound (short code, short type, char sound, char u1)
		 *     Play the specified system sound
		 *-------------------------------------------------
		 * Todo: add code to 700a to indicate which sensor.
		 *	paramBase[2]
		 *-------------------------------------------------
		 *******************************************/
		switch (paramBase[1]) {
		case 0x700a:	/* Control sensor output. */
			return "control_output: set sensor output";

		case 0x700b:	/* Play queued sound. */
			if(paramBase[2] == 0)		/* Play pause. */
				sprintf(outstring, "control_output: pause for "
					"0x%0lx ms", paramBase[3]);
			else if(paramBase[2] == 1)	/* Play sound. */
				sprintf(outstring, "control_output: play sound"
					"0x%0lx ms", paramBase[3]);
			else if(paramBase[1] > 1)	/* Play tone. */
				sprintf(outstring, "control_output: play tone:"
					"pitch(0x%0lx) duration(0x%0lx) ctl(0x%0lx)",
					paramBase[1], paramBase[2], paramBase[3]);
			else
				sprintf(outstring, "control_output: "
					"unknown code: 0x%0lx", paramBase[1]);
			return outstring;

		case 0x700d:	/* Play unqueued sound. */
			if(paramBase[2] == 0)		/* Play pause. */
				sprintf(outstring, "control_output: unqueued pause for "
					"0x%0lx ms", paramBase[3]);
			else if(paramBase[2] == 1)	/* Play sound. */
				sprintf(outstring, "control_output: play unqueued sound"
					"0x%0lx ms", paramBase[3]);
			else
				sprintf(outstring, "control_output: "
					"unknown code: 0x%0lx", paramBase[1]);
			return outstring;

		default:
			sprintf(outstring, "control_output: unknown code: 0x%0lx",
				paramBase[1]);
			return outstring;
		}
	case 0x3ed4:
		/********************************************
		 * 0x3ed4 shutdown_timer
		 * No parameters.
		 *******************************************/
		return "shutdown_timer";
	}
	return "";
}

/***************************************************************************
 * char *get_lcd_segment_msg
 * Param: char *, either "set" or "clear".
 * Common messages used for set_lcd_segment and clear_lcd_segment,
 ***************************************************************************/
char *get_lcd_segment_msg(char *action, STACKWORD code) {
	switch (code) {
	case 0x3006:
		sprintf(outstring, "%s_lcd_segment standing figure", action);
		return outstring;
	case 0x3007:
		sprintf(outstring, "%s_lcd_segment walking figure", action);
		return outstring;
	case 0x3008:
		sprintf(outstring, "%s_lcd_segment sensor 0 view selected", action);
		return outstring;
	case 0x3009:
		sprintf(outstring, "%s_lcd_segment sensor 0 active", action);
		return outstring;
	case 0x300a:
		sprintf(outstring, "%s_lcd_segment sensor 1 view selected", action);
		return outstring;
	case 0x300b:
		sprintf(outstring, "%s_lcd_segment sensor 1 active", action);
		return outstring;
	case 0x300c:
		sprintf(outstring, "%s_lcd_segment sensor 2 view selected", action);
		return outstring;
	case 0x300d:
		sprintf(outstring, "%s_lcd_segment sensor 2 active", action);
		return outstring;
	case 0x300e:
		sprintf(outstring, "%s_lcd_segment motor 0 view selected", action);
		return outstring;
	case 0x300f:
		sprintf(outstring, "%s_lcd_segment motor 0 backward arrow", action);
		return outstring;
	case 0x3010:
		sprintf(outstring, "%s_lcd_segment motor 0 forward arrow", action);
		return outstring;
	case 0x3011:
		sprintf(outstring, "%s_lcd_segment motor 1 view selected", action);
		return outstring;
	case 0x3012:
		sprintf(outstring, "%s_lcd_segment motor 1 backward arrow", action);
		return outstring;
	case 0x3013:
		sprintf(outstring, "%s_lcd_segment motor 1 forward arrow", action);
		return outstring;
	case 0x3014:
		sprintf(outstring, "%s_lcd_segment motor 2 view selected", action);
		return outstring;
	case 0x3015:
		sprintf(outstring, "%s_lcd_segment motor 2 backward arrow", action);
		return outstring;
	case 0x3016:
		sprintf(outstring, "%s_lcd_segment motor 2 forward arrow", action);
		return outstring;
	case 0x3018:
		sprintf(outstring, "%s_lcd_segment "
	 		"datalog indicator, multiple calls "
	 		"add 4 quarters clockwise", action);
		return outstring;
	case 0x3019:
		sprintf(outstring, "%s_lcd_segment "
	 		"download in progress, multiple calls "
	 		"adds up to 5 dots to right", action);
		return outstring;
	case 0x301a:
		sprintf(outstring, "%s_lcd_segment "
	 		"upload in progress, multiple calls "
	 		"removes up to 5 dots from left", action);
		return outstring;
	case 0x301b:
		sprintf(outstring, "%s_lcd_segment battery low", action);
		return outstring;
	case 0x301c:
		sprintf(outstring, "%s_lcd_segment short range indicator", action);
		return outstring;
	case 0x3001:
		sprintf(outstring, "%s_lcd_segment "
			"Set LCD main number signed", action);
		return outstring;
	case 0x301d:
		sprintf(outstring, "%s_lcd_segment "
	 		"long range indicator", action);
		return outstring;
	case 0x3020:
		sprintf(outstring, "%s_lcd_segment all segments", action);

		return outstring;
	}
	sprintf(outstring, "%s_lcd_segment unknown code: 0x%0lx", action, code);
	return outstring;
}
