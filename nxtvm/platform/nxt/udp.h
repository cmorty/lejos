#ifndef __UDP_H__
#  define __UDP_H__

#  include "mytypes.h"

int udp_init(void);
void udp_disable(void);
void udp_enable(int reset);
void udp_reset(void);
int udp_write(U8* buf, int off, int len);
int udp_read(U8* buf, int off, int len);
void udp_set_serialno(U8 *serNo, int len);
void udp_set_name(U8 *name, int len);
void udp_rconsole(U8* buf, int len);
S32 udp_event_check(S32 filter);

#define   USB_TIMEOUT   3000
#define END_OF_BUS_RESET ((unsigned int) 0x1 << 12)
#define SUSPEND_INT      ((unsigned int) 0x1 << 8)
#define SUSPEND_RESUME   ((unsigned int) 0x1 << 9)
#define WAKEUP           ((unsigned int) 0x1 << 13)

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

#define VENDOR_SET_FEATURE_INTERFACE  0x0341
#define VENDOR_CLEAR_FEATURE_INTERFACE  0x0141
#define VENDOR_GET_DESCRIPTOR         0x06c0



typedef struct __attribute__((__packed__)) {
	uint8_t bLength;
	uint8_t bDescriptorType;
	uint8_t bcdUSB[2];
	uint8_t bDeviceClass;
	uint8_t bDeviceSubClass;
	uint8_t bDeviceProtocol;
	uint8_t bMaxPacketSize0;
	uint8_t idVendor[2];
	uint8_t idProduct[2];
	uint8_t bcdDevice[2];
	uint8_t iManufacturer;
	uint8_t iProduct;
	uint8_t iSerial;
	uint8_t bNumConfigurations;
} usb_device_descriptor_t;

typedef struct __attribute__((__packed__)) {
	uint8_t bLength;
	uint8_t bDescriptorType;
	uint8_t wTotalLength[2];
	uint8_t bNumInterfaces;
	uint8_t bConfigurationValue;
	uint8_t iConfiguration;
	uint8_t bmAttributes;
	uint8_t MaxPower;
} usb_configuration_descriptor_t;

typedef struct __attribute__((__packed__)) {
	uint8_t bLength;
	uint8_t bDescriptorType;
	uint8_t bInterfaceNumber;
	uint8_t bAlternateSetting;
	uint8_t bNumEndpoints;
	uint8_t bInterfaceClass;
	uint8_t bInterfaceSubClass;
	uint8_t bInterfaceProtocol;
	uint8_t iInterface;
} usb_interface_descriptor_t;

typedef struct __attribute__((__packed__)) {
	uint8_t bLength;
	uint8_t bDescriptorType;
	uint8_t bEndpointAddress;
	uint8_t bmAttributes;
	uint8_t wMaxPacketSize[2];
	uint8_t bInterval;
} usb_endpoint_descriptor_t;

typedef struct __attribute__((__packed__)) {
	usb_configuration_descriptor_t config;
	usb_interface_descriptor_t interface;
	usb_endpoint_descriptor_t endpoints[2];
} nxt_configuration_descriptor_t;

#define USB_DESCR_WORD(x) { (x) & 0xFF, (x) >> 8 }

#define USB_DESCR_STRLEN(x) (2+2*(x))
#define USB_DESCR_STRDEF(x) \
	typedef struct __attribute__((__packed__)) { \
		uint8_t bLength;                         \
		uint8_t bDescriptorType;                 \
		uint8_t data[x][2];                      \
	}

#endif
