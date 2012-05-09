/**
 * JNI interface for libUSB
 *
 * Copyright 2007 Lawrie Griffiths <lawrie.griffiths@ntlwworld.com>
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
#include <unistd.h>
#include <errno.h>
#include <usb.h>

#include "jlibnxt.h"


#define MAX_DEVS 64
#define MAX_SERNO 64
#define MAX_WRITE 64

// On windows ETIMEDOUT is not defined. The windows driver uses 116 as the value
#ifndef ETIMEDOUT
#define ETIMEDOUT 116
#endif



enum nxt_usb_ids {
  VENDOR_LEGO   = 0x0694,
  VENDOR_ATMEL  = 0x03EB,
  PRODUCT_NXT   = 0x0002,
  PRODUCT_SAMBA = 0x6124
};


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
static long nxt_find_nth(int idx, char *address)
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


// Version of open that works with lejos NXJ firmware.
static
long
nxt_open(long hdev)
{
  struct usb_dev_handle *hdl;
  struct usb_device *dev = (struct usb_device *)hdev;
  int ret, interf;
  char buf[64];
  hdl = usb_open((struct usb_device *) hdev);
  if (!hdl) return 0;
  
  // If we are in SAMBA mode we need interface 1, otherwise 0
  if (dev->descriptor.idVendor == VENDOR_ATMEL &&
      dev->descriptor.idProduct == PRODUCT_SAMBA) {
	  interf = 1;

	  // detach cdc_acm or sam-ba kernel driver (issue in linux >=2.6.35.5)
	  // if detach unsuccessfull, other calls will fail
	  usb_detach_kernel_driver_np(hdl, interf);

	  // TODO this actually also detaches other libusb clients if kernel driver name == "usbfs"
	  // Problem: libusb-compat supplies dummy kernel driver name
	  // and libusb-1.0 doesn't even allow for querying the kernel driver name.
	  // Also querying driver name and detaching depending on the result
	  // results in a race condition.
  } else {
	  interf = 0;
  }

  
  ret = usb_set_configuration(hdl, 1);
  if (ret < 0)
  {
    usb_close(hdl);
    return 0;
  }
  
  ret = usb_claim_interface(hdl, interf);
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
static void nxt_close(long hhdl)
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


// Implement 20sec timeout write, and return amount actually written
static
int
nxt_write_buf(long hdl, char *buf, int len)
{
  int ret = usb_bulk_write((struct usb_dev_handle *)hdl, 0x1, buf, len, 20000);
  return ret;
}


// Implement 20 second timeout read, and return amount actually read
static
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



JNIEXPORT jobjectArray JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1find
  (JNIEnv *env, jobject obj)
{
  jstring names[MAX_DEVS];
  int cnt = 0;
  char name[MAX_SERNO];
  int i = 0;
  while(nxt_find_nth(cnt, name) != 0)
  {
    names[cnt++] = (*env)->NewStringUTF(env, name) ;
  }
  if (cnt <= 0) return NULL;

  // Now copy names in a java array
  jclass sclass = (*env)->FindClass(env, "java/lang/String");
  jobjectArray arr = (*env)->NewObjectArray(env, cnt, sclass, NULL);
  for(i = 0; i < cnt; i++)
    (*env)->SetObjectArrayElement(env, arr, i, names[i]);
   
  return arr;
}

JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1open
  (JNIEnv *env, jobject obj, jstring jnxt)
{
  const char* nxt = (*env)->GetStringUTFChars(env, jnxt, 0);
  long dev;
  char name[MAX_SERNO];
  int cnt = 0;
  while((dev = nxt_find_nth(cnt, name)) != 0)
  {
    if (strcmp(name, nxt) == 0)
    {
      return (jlong) nxt_open( dev ); 
    }
    cnt++;
  }
  return (jlong) 0;
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1close(JNIEnv *env, jobject obj, jlong nxt)  {
  nxt_close( (long) nxt); 
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1send_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray data, jint offset, jint len)  {
  int ret;
  char *jb = (char *) (*env)->GetByteArrayElements(env, data, 0);  
  ret = nxt_write_buf((long) nxt, jb+offset, len);
  (*env)->ReleaseByteArrayElements(env, data, (jbyte *) jb, 0);
  if (ret == -ETIMEDOUT) ret = 0;
  return ret;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1read_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)  {
   int read_len;
   char *jb = (char *)(*env)->GetByteArrayElements(env, jdata, 0);

   read_len = nxt_read_buf((long)nxt, jb + offset, len);
   (*env)->ReleaseByteArrayElements(env, jdata, (jbyte *)jb, 0);
   if (read_len == -ETIMEDOUT) read_len = 0;
   return read_len;
}

