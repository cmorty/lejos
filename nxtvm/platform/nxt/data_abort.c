#include "display.h"
#include "AT91SAM7.h"

U32 data_abort_pc;

void data_abort_C(void)
{
  display_clear(0);
  display_goto_xy(0,0); display_string("Data abort");
  display_goto_xy(0,1); display_string("PC   "); display_hex(data_abort_pc,8);
  display_goto_xy(0,2); display_string("AASR "); display_hex(*AT91C_MC_AASR,8);
  display_goto_xy(0,3); display_string("ASR  "); display_hex(*AT91C_MC_ASR,8);
  
  display_update();
  
  while(1){}
}


