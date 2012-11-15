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
#include <libusb.h>

#include "jlibnxt.h"


#define MAX_DEVS 64
#define MAX_SERNO 64
#define MAX_WRITE 64

// On windows ETIMEDOUT is not defined. The windows driver uses 116 as the value
#ifndef ETIMEDOUT
#define ETIMEDOUT 116
#endif

typedef struct libusb_device_descriptor libusb_device_descriptor;


enum nxt_usb_ids {
  VENDOR_LEGO   = 0x0694,
  PRODUCT_NXT   = 0x0002,
  VENDOR_ATMEL  = 0x03EB,
  PRODUCT_SAMBA = 0x6124
};


libusb_context *context;

__attribute((constructor)) void ctor() {
	libusb_init(&context);
}
__attribute((destructor)) void dtor() {
	libusb_exit(context);
	context = NULL;
}


/* Create the device address string. We use the same format as the
 * Lego Fantom device driver.
 */
static void create_address(libusb_device *dev, char *address)
{
  libusb_device_descriptor descriptor;
  libusb_get_device_descriptor(dev, &descriptor);

  // Do the easy one first. There is only one Samba device
  if (descriptor.idVendor == VENDOR_ATMEL &&
       descriptor.idProduct == PRODUCT_SAMBA)
     sprintf(address, "USB0::0x%04X::0x%04X::NI-VISA-0::1::RAW", VENDOR_ATMEL, PRODUCT_SAMBA);
  else
  {
    // Do the more general case. We need to get the serial number into non
    // unicode format.
    //unsigned char sn_unicode[MAX_SERNO];
    unsigned char sn_ascii[MAX_SERNO + 1];
    libusb_device_handle *hdl;
    libusb_open(dev, &hdl);
    sn_ascii[0] = '0';
    if (hdl)
    {
      int len = libusb_get_string_descriptor_ascii(hdl, descriptor.iSerialNumber, sn_ascii, MAX_SERNO);
      libusb_close(hdl);
      if (len < 0)
        len = 0;
      sn_ascii[len] = 0;
    }
    if (sn_ascii[0] != 0)
     sprintf(address, "USB0::0x%04X::0x%04X::%s::RAW", descriptor.idVendor, descriptor.idProduct, sn_ascii);
    else
     sprintf(address, "USB0::0x%04X::0x%04X::000000000000::RAW", descriptor.idVendor, descriptor.idProduct);
  }
}

/* Return a handle to the nth NXT device, or null if not found/error
 * Also return a dvice address string that contains all of the details of
 * this device. This string can be used later to re-locate the device  */
static libusb_device *nxt_find_nth(int idx, char *address)
{
  //struct usb_bus *busses, *bus;
  address[0] = '\0';

  libusb_device **list;
  int len = libusb_get_device_list(context, &list);
  for (int i = 0, cnt = 0; i<len; i++)
  {
    libusb_device *dev=list[i];
    libusb_device_descriptor descriptor;
    libusb_get_device_descriptor(dev, &descriptor);

    if ((descriptor.idVendor == VENDOR_LEGO &&
            descriptor.idProduct == PRODUCT_NXT) ||
        (descriptor.idVendor == VENDOR_ATMEL &&
            descriptor.idProduct == PRODUCT_SAMBA))
    {
      if (cnt++ < idx) continue;
      // Now create the address string we use the same format as the
      // Lego Fantom driver
      create_address(dev, address);
      libusb_ref_device(dev);
      libusb_free_device_list(list, 1);
      return dev;
    }
  }
  libusb_free_device_list(list, 1);
  return 0;
}


// Version of open that works with lejos NXJ firmware.
static libusb_device_handle *nxt_open(libusb_device *dev)
{
  libusb_device_handle *hdl;
  int ret, interf;
  libusb_open(dev, &hdl);
  if (!hdl) return 0;
  
  libusb_device_descriptor descriptor;
  libusb_get_device_descriptor(dev, &descriptor);

  // If we are in SAMBA mode we need interface 1, otherwise 0
  if (descriptor.idVendor == VENDOR_ATMEL &&
      descriptor.idProduct == PRODUCT_SAMBA) {
	  interf = 1;

	  // detach cdc_acm or sam-ba kernel driver (issue in linux >=2.6.35.5)
	  // if detach unsuccessfull, other calls will fail
	  libusb_detach_kernel_driver(hdl, interf);

	  // TODO this actually also detaches other libusb clients if kernel driver name == "usbfs"
	  // Problem: libusb-compat supplies dummy kernel driver name
	  // and libusb-1.0 doesn't even allow for querying the kernel driver name.
	  // Also querying driver name and detaching depending on the result
	  // results in a race condition.
  } else {
	  interf = 0;
  }

  
  ret = libusb_set_configuration(hdl, 1);
  if (ret < 0)
  {
    libusb_close(hdl);
    return 0;
  }
  
  ret = libusb_claim_interface(hdl, interf);
  if (ret < 0)
  {
    libusb_close(hdl);
    return 0;
  }

  // Discard any data that is left in the buffer
  unsigned char buf[64];
  while (1) {
    int alen = 0;
    int ret= libusb_bulk_transfer(hdl, 0x82, buf, sizeof(buf), &alen, 1);
    if (alen == 0 || ret < 0)
    	break;
  }

  return hdl;
}

// Version of close that uses interface 0
static void nxt_close(libusb_device_handle *hdl)
{
  // Discard any data that is left in the buffer
  unsigned char buf[64];
  while (1) {
    int alen = 0;
    int ret= libusb_bulk_transfer(hdl, 0x82, buf, sizeof(buf), &alen, 1);
    if (alen == 0 || ret < 0)
		break;
  }

  // Release interface. This is a little iffy we do not know which interface
  // we are actually using. Releasing both seems to work... but...
  libusb_release_interface(hdl, 0);
  libusb_release_interface(hdl, 1);
  libusb_close(hdl);
}


// Implement 20sec timeout write, and return amount actually written
static int nxt_write_buf(libusb_device_handle *hdl, unsigned char *buf, int len)
{
  int alen = 0;
  int ret = libusb_bulk_transfer(hdl, 0x01, buf, len, &alen, 20000);
  return (ret < 0) ? ret : alen;
}


// Implement 20 second timeout read, and return amount actually read
static int nxt_read_buf(libusb_device_handle *hdl, unsigned char *buf, int len)
{
  int alen = 0;
  int ret = libusb_bulk_transfer(hdl, 0x82, buf, len, &alen, 20000);
  return (ret < 0) ? ret : alen;
}


JNIEXPORT jobjectArray JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1find
  (JNIEnv *env, jobject obj)
{
  libusb_device *dev;
  jstring names[MAX_DEVS];
  int cnt = 0;
  char name[MAX_SERNO];
  int i = 0;
  while((dev = nxt_find_nth(cnt, name)) != 0)
  {
    libusb_unref_device(dev);
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
  char name[MAX_SERNO];

  int cnt = 0;
  libusb_device *dev;
  while((dev = nxt_find_nth(cnt, name)) != 0)
  {
    if (strcmp(name, nxt) == 0)
    {
      libusb_device_handle *ret = nxt_open( dev );
      if (!ret)
        libusb_unref_device(dev);

      (*env)->ReleaseStringUTFChars(env, jnxt, nxt);
      return (jlong) ret ;
    }
    libusb_unref_device(dev);
    cnt++;
  }
  (*env)->ReleaseStringUTFChars(env, jnxt, nxt);
  return 0;
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1close(JNIEnv *env, jobject obj, jlong nxt)
{
  nxt_close( (libusb_device_handle*) nxt);
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1send_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray data, jint offset, jint len)
{
  jbyte *jb = (*env)->GetByteArrayElements(env, data, 0);
  int ret = nxt_write_buf((libusb_device_handle*) nxt, (unsigned char *)jb + offset, len);
  (*env)->ReleaseByteArrayElements(env, data, jb, 0);
  if (ret == LIBUSB_ERROR_TIMEOUT) ret = 0;
  return ret;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1read_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)
{
  jbyte *jb = (*env)->GetByteArrayElements(env, jdata, 0);
  int ret = nxt_read_buf((libusb_device_handle*) nxt, (unsigned char *)jb + offset, len);
  (*env)->ReleaseByteArrayElements(env, jdata, jb, 0);
  if (ret == LIBUSB_ERROR_TIMEOUT) ret = 0;
  return ret;
}


