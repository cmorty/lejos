#include "display.h"
#include "AT91SAM7.h"
#include "interpreter.h"
#include "nxt_spi.h"

U32 data_abort_pc;

void
data_abort_C(void)
{
  // Need to reset the link to the display to see any output
  nxt_spi_init();
  display_reset();
  display_goto_xy(0, 0);
  display_string("Data abort");
  display_goto_xy(0, 1);
  display_string("PC   ");
  display_hex(data_abort_pc, 8);
  display_goto_xy(0, 2);
  display_string("AASR ");
  display_hex(*AT91C_MC_AASR, 8);
  display_goto_xy(0, 3);
  display_string("ASR  ");
  display_hex(*AT91C_MC_ASR, 8);
  display_goto_xy(0, 4);
  display_string("OPCODE ");
  if( old_pc != JNULL) display_int((int) *old_pc, 4); else display_string("???");
  display_goto_xy(0,5);
  display_string("DEBUG1 ");
  display_hex(debug_word1,8);
  display_goto_xy(0,6);
  display_string("DEBUG2 ");
  display_hex(debug_word2,8);

  display_force_update();

  while (1) {
  }
}
