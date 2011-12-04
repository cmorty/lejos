/**
 * Generic usart interface routines
 */
// Bit values used for events
#define US_READABLE   0x1
#define US_WRITEABLE  0x2
#define US_WRITEEMPTY 0x4

#define BUF_CNT 2
typedef struct
{
  AT91S_PDC *dma;
  AT91S_USART *dev;
  U8 *in_buf[BUF_CNT];
  U8 *out_buf[BUF_CNT];
  int in_offset;
  int in_size;
  int out_size;
  U8 in_base;
  U8 out_base;
} usart;

usart *usart_allocate(AT91S_USART *dev, AT91S_PDC *dma, int inSz, int outSz);
void usart_enable(usart *us);
void usart_disable(usart *us);
void usart_free(usart *us);
U32 usart_status(usart *us);
U32 usart_write(usart *us, U8 *buf, U32 off, U32 len);
U32 usart_read(usart *us, U8 * buf, U32 off, U32 length);
U8 * usart_get_write_buffer(usart *us);
void usart_write_buffer(usart *us, U32 len);





