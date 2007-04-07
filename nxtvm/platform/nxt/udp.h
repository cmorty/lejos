#ifndef __UDP_H__
#  define __UDP_H__

#  include "mytypes.h"

void udp_isr_C(void);
int udp_init(void);
void udp_close(U32 u);
void udp_disable(void);
void udp_reset(void);
int udp_timed_out(void);
void udp_reset_timeout(void);
int udp_short_timed_out(void);
void udp_short_reset_timeout(void);
void udp_write(U8* buf, int len);
void udp_enumerate(void);
void udp_send_control(U8* p,int len);
void udp_send_null(void);
void udp_send_stall(void);
int udp_configured(void);
void udp_set_configured(int conf);
int udp_read(U8* buf, int len);
void udp_wait_for_connection(void);

#define   USB_TIMEOUT   0x0BB8 

/* USB standard request codes */

#define STD_GET_STATUS_ZERO           0x0080
#define STD_GET_STATUS_INTERFACE      0x0081
#define STD_GET_STATUS_ENDPOINT       0x0082

#define STD_CLEAR_FEATURE_ZERO        0x0100
#define STD_CLEAR_FEATURE_INTERFACE   0x0101
#define STD_CLEAR_FEATURE_ENDPOINT    0x0102

#define STD_SET_FEATURE_ZERO          0x0300
#define STD_SET_FEATURE_INTERFACE     0x0301
#define STD_SET_FEATURE_ENDPOINT      0x0302

#define STD_SET_ADDRESS               0x0500
#define STD_GET_DESCRIPTOR            0x0680
#define STD_SET_DESCRIPTOR            0x0700
#define STD_GET_CONFIGURATION         0x0880
#define STD_SET_CONFIGURATION         0x0900
#define STD_GET_INTERFACE             0x0A81
#define STD_SET_INTERFACE             0x0B01
#define STD_SYNCH_FRAME               0x0C82
#endif
