
#include "mytypes.h"
#include "interrupts.h"
#include "aic.h"
#include "AT91SAM7.h"
#include "uart.h"
#include "systick.h"
#include "stdio.h"
#include "flashprog.h"
#include "nxt_avr.h"
#include "twi.h"
#include "types.h"
#include "constants.h"
#include "classes.h"
#include "threads.h"
#include "stack.h"
#include "specialclasses.h"
#include "specialsignatures.h"
#include "language.h"
#include "memory.h"
#include "interpreter.h"
#include "exceptions.h"
#include "trace.h"
#include "poll.h"
#include "sensors.h"
#include "platform_hooks.h"
#include "java_binary.h"

#include "nxt_avr.h"
#include "nxt_lcd.h"

#include "nxt_motors.h"

#include "lejos_nxt.h"

#include "display.h"

extern U32 __free_ram_start__;
extern U32 __free_ram_end__;
extern U32 __extra_ram_start__;
extern U32 __extra_ram_end__;


byte *region;
Thread   *bootThread;

void handle_uncaught_exception (Object *exception,
                                       const Thread *thread,
				       const MethodRecord *methodRecord,
				       const MethodRecord *rootMethod,
				       byte *pc)
{
					       
}

void switch_thread_hook()
{
  // NOP
}


void assert_hook (boolean aCond, int aCode)
{
}


void run(void)
{
  init_poller();

  //printf("Initializing Binary\n");

  // Initialize binary image state
  initialize_binary();

  //printf("Initializing memory\n");

  // Initialize memory
  {
    byte *ram_end = (byte *)(& __free_ram_end__);
    byte * ram_start = (byte *)(& __free_ram_start__);
    unsigned size = ((unsigned)ram_end) - ((unsigned) ram_start);
    
    memory_init ();
    
    region = ram_start;
    memory_add_region (region, (byte *)ram_end);
    
    /*Add extra RAM if available */
    ram_end = (byte *)(&__extra_ram_end__);
    ram_start = (byte *)(&__extra_ram_start__);
    size = ((unsigned)ram_end) - ((unsigned)ram_start);
    
    if(size > 0)
    	memory_add_region(ram_start, ram_end);
  }

  //printf("Initializing exceptions\n");

  // Initialize exceptions
  init_exceptions();

  // Create the boot thread (bootThread is a special global)
  bootThread = (Thread *) new_object_for_class (JAVA_LANG_THREAD);

  init_threads();
  if (!init_thread (bootThread))
  {
    return;	  
  }

  //printf("Executing Interpreter\n");

  // Execute the bytecode interpreter
  set_program_number (0);

  engine();
  // Engine returns when all non-daemon threads are dead
}

/***************************************************************************
 * int nxt_main *--------------------------------------------------------------------------
 ***************************************************************************/
//int main (int argc, char *argv[])
int nxt_main()
{
        init_sensors ();

 //       printf("Installing Binary\n");

        install_binary(java_binary);

 //      printf("Running\n");

	run();

        return 0;
} 

const U8 splash_data[4*26] = {
 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
 0x00,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0xFF,0xFF,0xFF,0xFF,0xFE,0xFC,
 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xA0,0x40,0xA0,0x40,
 0xA0,0x40,0xA0,0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,
 0xF0,0xF0,0xF0,0xF0,0xF0,0xF0,0x00,0x00,0x00,0x0A,0x05,0x0A,0x05,
 0x0A,0x05,0x0A,0x00,0x00,0x00,0x00,0xFF,0xFF,0xFF,0xFF,0xFF,0xFF,
 0x0F,0x1F,0x3F,0x3F,0x7F,0x7F,0xFC,0xF8,0xF0,0xF0,0xF0,0xF0,0xF0,
 0xF0,0xF0,0xF0,0xF0,0xF0,0xF8,0xFC,0x7F,0x7F,0x3F,0x3F,0x1F,0x0F,};
  
void show_splash(U32 milliseconds)
{
  display_clear(0);
  display_bitmap_copy(splash_data, 26, 4, 37,1);
  
  display_goto_xy(6,6); display_string("LEJOS");
  display_update();
  
  systick_wait_ms(milliseconds);
}



U32 free_stack(void)
{
	extern U32 __system_stack_bottom__;
	extern U32 __system_stack_top__;
	U32 *sp = & __system_stack_bottom__;
	U32 space = 0;
	while( (sp < &__system_stack_top__) &&
		*sp == 0x6b617453){
		sp++;
		space += 4;
	}
	return space;
}

void xx_show(void)
{
  int iterator = 0;
  U32 buttons;
  
  show_splash(3000);
  
  while(1){
    display_clear(0);
    iterator++;
    
    if((iterator & 8) == 0){
	    // nxt_avr_update();
	    buttons = buttons_get();
    
	    display_goto_xy(iterator & 7,0); display_string("LEJOS NXT");
    
	    display_goto_xy(0,1); display_string("TIME ");display_unsigned(systick_get_ms(),0);
    
	    display_goto_xy(0,2); display_string("BATTERY ");display_unsigned(battery_voltage(),0);
    
	    display_goto_xy(0,3); display_string("BUTTONS "); 
	    if(buttons & 1) display_string("0 ");
	    if(buttons & 2) display_string("1 ");
	    if(buttons & 4) display_string("2 ");
	    if(buttons & 8) display_string("3 ");

	    display_goto_xy(0,4); display_string("ADC ");
	    display_unsigned(sensor_adc(0),5);
	    display_unsigned(sensor_adc(1),5);
	    display_goto_xy(0,5); display_string("    "); 
	    display_unsigned(sensor_adc(2),5);
	    display_unsigned(sensor_adc(3),5);
    
	    display_update();
	    systick_wait_ms(500);
    } else {
    
	    display_goto_xy(iterator & 7,0); display_string("LEJOS NXT");
    
	    display_goto_xy(0,1); display_string("TIME ");display_unsigned(systick_get_ms(),0);
	    display_goto_xy(0,2); display_string("Stack ");display_unsigned(free_stack(),0);
	    display_goto_xy(0,3); display_string("MOTORS");
//	    display_goto_xy(1,4); display_integer(motor_get_count(0));
//	    display_goto_xy(1,5); display_integer(motor_get_count(1));
//	    display_goto_xy(1,6); display_integer(motor_get_count(2));

	    display_update();
	    systick_wait_ms(500);
    }
  }
}

void main(void)
{
	int error;
	unsigned int i;
	unsigned char b;
	unsigned now,prev;
	char *s;
	
	/* When we get here:
	 * PLL and flash have been initialised and
	 * interrupts are off, but the AIC has not been initialised.
	 */
	aic_initialise();
	interrupts_enable();
	systick_init();
	nxt_avr_init();
	display_init();


//	while(1){
//		nxt_avr_power_down();
//		nxt_avr_test_loop();
//		nxt_lcd_test();
//		systick_test();
//		display_test();

	xx_show();

//	}
	
	nxt_main();



#if 0
	error = uart_init(0,9600,8,1,'N');
	
	
	*AT91C_PIOA_PER = LED1 | LED2 | LED3 | LED4;
	*AT91C_PIOA_OER = LED1 | LED2 | LED3 | LED4;
	
	uart_put_str(0,"baah, baah!\n");
	
	
	while(1){
		if(uart_get_byte(0,&b))
			uart_put_byte(0,b+1);
			
		systick_get_time(&now,(void *)0);
		
		if(now != prev){
			prev = now;
			
			if(now & 1)
				*AT91C_PIOA_CODR = LED1;
			else
				*AT91C_PIOA_SODR = LED1;
		}
	}
#endif

}


