/**
 * NXT bootstrap interface; low-level USB functions.
 *
 * Copyright 2006 David Anderson <david.anderson@calixo.net>
 * Modified to work with lejos NXJ by Lawrie Griffiths (lawrie.griffiths@ntlwworld.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <unistd.h>
#include <errno.h>
#include <usb.h>

#include "lowlevel.h"

enum nxt_usb_ids {
  VENDOR_LEGO   = 0x0694,
  VENDOR_ATMEL  = 0x03EB,
  PRODUCT_NXT   = 0x0002,
  PRODUCT_SAMBA = 0x6124
};


struct nxt_t {
  struct usb_device *dev;
  struct usb_dev_handle *hdl;
  int is_in_reset_mode;
};

nxt_error_t nxt_init(nxt_t **nxt)
{
  usb_init();
  *nxt = calloc(1, sizeof(**nxt));

  return NXT_OK;
}

static int initialised = 0;

/* Create the device address string. We use the same format as the
 * Lego Fantom device driver.
 */
static
void create_address(struct usb_device *dev, char *address)
{
  // Do the easy one first. There is only one Samba device
  if (dev->descriptor.idVendor == VENDOR_ATMEL &&
       dev->descriptor.idProduct == PRODUCT_SAMBA)
     sprintf(address, "USB0::0x%04X::0x%04X::NI-VISA-0::1::RAW", VENDOR_ATMEL, PRODUCT_SAMBA);
  else
  {
    // Do the more general case. We need to get the serial number into non
    // unicode format.
    unsigned char sn_unicode[MAX_SERNO];
    unsigned char sn_ascii[MAX_SERNO];
    struct usb_dev_handle *hdl;
    hdl = usb_open(dev);
    sn_ascii[0] = '\0';
    if (hdl)
    {
      int i;
      int len = usb_get_string(hdl, dev->descriptor.iSerialNumber, 0, (char *)sn_unicode, MAX_SERNO);
      usb_close(hdl);
      if (len > 2)
        // First byte of the desciptor is the length. Second byte is type.
        // Both bytes are included in the length.
        len = ((int) sn_unicode[0] - 2)/2;
      if (len < 0)
        len = 0;
      for(i = 0; i < len; i++)
        sn_ascii[i] = sn_unicode[i*2 + 2];
      sn_ascii[i] = '\0';
    }
    if (sn_ascii[0] != '\0')
     sprintf(address, "USB0::0x%04X::0x%04X::%s::RAW", dev->descriptor.idVendor, dev->descriptor.idProduct, sn_ascii);
    else
     sprintf(address, "USB0::0x%04X::0x%04X::000000000000::RAW", dev->descriptor.idVendor, dev->descriptor.idProduct);
  }
}

/* Return a handle to the nth NXT device, or null if not found/error
 * Also return a dvice address string that contains all of the details of
 * this device. This string can be used later to re-locate the device  */
long nxt_find_nth(int idx, char *address)
{
  struct usb_bus *busses, *bus;
  address[0] = '\0';
  if (!initialised)
  {
    usb_init();
    initialised = 1;
  }
  if (idx == 0)
  {
    usb_find_busses();
    usb_find_devices();
  }

  int cnt = 0;
  busses = usb_get_busses();
  for (bus = busses; bus != NULL; bus = bus->next)
    {
      struct usb_device *dev;

      for (dev = bus->devices; dev != NULL; dev = dev->next)
        {
          if ((dev->descriptor.idVendor == VENDOR_LEGO &&
                   dev->descriptor.idProduct == PRODUCT_NXT) ||
              (dev->descriptor.idVendor == VENDOR_ATMEL &&
                   dev->descriptor.idProduct == PRODUCT_SAMBA))
            {
              if (cnt++ < idx) continue;
              // Now create the address string we use the same format as the
              // Lego Fantom driver
              create_address(dev, address);
              return (long) dev;
            }
        }
    }
  return 0;
}

nxt_error_t nxt_find(nxt_t *nxt)
{
  struct usb_bus *busses, *bus;
  usb_find_busses();
  usb_find_devices();
  busses = usb_get_busses();
  for (bus = busses; bus != NULL; bus = bus->next)
    {
      struct usb_device *dev;

      for (dev = bus->devices; dev != NULL; dev = dev->next)
        {
          if (dev->descriptor.idVendor == VENDOR_ATMEL &&
              dev->descriptor.idProduct == PRODUCT_SAMBA)
            {
              nxt->dev = dev;
              nxt->is_in_reset_mode = 1;

              return NXT_OK;
            }
          else if (dev->descriptor.idVendor == VENDOR_LEGO &&
                   dev->descriptor.idProduct == PRODUCT_NXT)
            {
              nxt->dev = dev;
              return NXT_OK;
            }
        }
    }
  return NXT_NOT_PRESENT;
}

nxt_error_t
nxt_open(nxt_t *nxt)
{
  char buf[2];
  int ret;

  nxt->hdl = usb_open(nxt->dev);

  ret = usb_set_configuration(nxt->hdl, 1);

  if (ret < 0)
    {
      usb_close(nxt->hdl);
      return NXT_CONFIGURATION_ERROR;
    }

  ret = usb_claim_interface(nxt->hdl, 1);

  if (ret < 0)
    {
      usb_close(nxt->hdl);
      return NXT_IN_USE;
    }

  /* NXT handshake */
  nxt_send_str(nxt, "N#");
  nxt_recv_buf(nxt, buf, 2);
  if (memcmp(buf, "\n\r", 2) != 0)
    {
      usb_release_interface(nxt->hdl, 1);
      usb_close(nxt->hdl);
      return NXT_HANDSHAKE_FAILED;
    }

  return NXT_OK;
}


nxt_error_t
nxt_close(nxt_t *nxt)
{
  usb_release_interface(nxt->hdl, 1);
  usb_close(nxt->hdl);
  free(nxt);

  return NXT_OK;
}

// Version of open that works with lejos NXJ firmware.
// Uses interface zero, and does not send samba N# message
long
nxt_open0(long hdev)
{
  struct usb_dev_handle *hdl;
  struct usb_device *dev = (struct usb_device *)hdev;
  int ret;
  char buf[64];
  hdl = usb_open((struct usb_device *) hdev);
  if (!hdl) return 0;
  ret = usb_set_configuration(hdl, 1);

  if (ret < 0)
    {
      usb_close(hdl);
      return 0;
    }
  // If we are in SAMBA mode we need interface 1, otherwise 0
  if (dev->descriptor.idVendor == VENDOR_ATMEL &&
                   dev->descriptor.idProduct == PRODUCT_SAMBA)
  {
    ret = usb_claim_interface(hdl, 1);
  }
  else
  {
    ret = usb_claim_interface(hdl, 0);
  }

  if (ret < 0)
    {
      usb_close(hdl);
      return 0;
    }
  // Discard any data that is left in the buffer
  while (usb_bulk_read(hdl, 0x82, buf, sizeof(buf), 1) > 0)
    ;

  return (long) hdl;
}

// Version of close that uses interface 0
void nxt_close0(long hhdl)
{
  char buf[64];
  struct usb_dev_handle *hdl = (struct usb_dev_handle *) hhdl;
  // Discard any data that is left in the buffer
  while (usb_bulk_read(hdl, 0x82, buf, sizeof(buf), 1) > 0)
    ;
  // Release interface. This is a little iffy we do not know which interface
  // we are actually using. Releasing both seems to work... but...
  usb_release_interface(hdl, 0);
  usb_release_interface(hdl, 1);
  usb_close(hdl);
}

int
nxt_in_reset_mode(nxt_t *nxt)
{
  return nxt->is_in_reset_mode;
}

// Timeout set to 10 seconds for lejos NXJ
nxt_error_t
nxt_send_buf(nxt_t *nxt, char *buf, int len)
{
  int ret = usb_bulk_write(nxt->hdl, 0x1, buf, len, 10000);
  if (ret < 0)
    return NXT_USB_WRITE_ERROR;

  return NXT_OK;
}


nxt_error_t
nxt_send_str(nxt_t *nxt, char *str)
{
  return nxt_send_buf(nxt, str, strlen(str));
}


nxt_error_t
nxt_recv_buf(nxt_t *nxt, char *buf, int len)
{
  // set timeout to 10 seconds to allow time for defrag
  int ret = usb_bulk_read(nxt->hdl, 0x82, buf, len, 10000);
  if (ret < 0)
    return NXT_USB_READ_ERROR;

  return NXT_OK;
}


// Implement 20sec timeout write, and return amount actually written
int
nxt_write_buf(long hdl, char *buf, int len)
{
  int ret = usb_bulk_write((struct usb_dev_handle *)hdl, 0x1, buf, len, 20000);
  return ret;
}


// Implement 20 second timeout read, and return amount actually read
int
nxt_read_buf(long hdl, char *buf, int len)
{
  int ret = usb_bulk_read((struct usb_dev_handle *)hdl, 0x82, buf, len, 20000);
  return ret;
}

static char samba_serial_no[] = {4, 3, '1', 0};
int nxt_serial_no(long hdev, char *serno, int maxlen)
{
  struct usb_device *dev = (struct usb_device *)hdev;
  struct usb_dev_handle *hdl;
  // If the device is in samba mode it will not have a serial number so we
  // return "1" to be in line with the Lego Fantom driver.
  if (dev->descriptor.idVendor == VENDOR_ATMEL &&
         dev->descriptor.idProduct == PRODUCT_SAMBA)
  {
    memcpy(serno, samba_serial_no, sizeof(samba_serial_no));
    return sizeof(samba_serial_no);
  }
  hdl = usb_open(dev);
  if (!hdl) return 0;
  int len = usb_get_string(hdl, dev->descriptor.iSerialNumber, 0, serno, maxlen);
  usb_close(hdl);
  return len;
}
