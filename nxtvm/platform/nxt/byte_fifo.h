
/*
 *  Byte fifo implementation
 */

#ifndef __BFIFO_H__
#  define __BFIFO_H__

#  include "mytypes.h"

struct byte_fifo {
  U8 *head;
  U8 *tail;
  U8 *buffer;
  U8 *buffer_end;
  U32 buffer_size;
  U32 holding;
};

void byte_fifo_clear(struct byte_fifo *f);
void byte_fifo_init(struct byte_fifo *f, U8 *buffer, U32 buff_size);
int byte_fifo_put(struct byte_fifo *f, U32 force, U8 b);
int byte_fifo_get(struct byte_fifo *f, U8 *b);
#endif
