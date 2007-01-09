
#include "byte_fifo.h"

void
byte_fifo_clear(struct byte_fifo *f)
{
  f->tail = f->head = f->buffer;
  f->holding = 0;
}

void
byte_fifo_init(struct byte_fifo *f, U8 *buffer, U32 buffer_size)
{
  f->buffer = buffer;
  f->buffer_size = buffer_size;
  f->buffer_end = buffer + buffer_size;
  byte_fifo_clear(f);
}

int
byte_fifo_put(struct byte_fifo *f, U32 force, U8 b)
{
  if (f->holding < f->buffer_size || force) {
    *(f->head) = b;
    f->holding++;
    f->head++;
    if (f->head >= f->buffer_end)
      f->head = f->buffer;
    if (f->tail == f->head) {
      f->holding--;
      f->tail++;
      if (f->tail >= f->buffer_end)
	f->tail = f->buffer;
    }
    return 1;
  }

  return 0;
}

int
byte_fifo_get(struct byte_fifo *f, U8 *b)
{
  if (f->holding) {
    *b = *(f->tail);
    f->holding--;
    f->tail++;
    if (f->tail >= f->buffer_end)
      f->tail = f->buffer;
    return 1;
  }
  return 0;
}
