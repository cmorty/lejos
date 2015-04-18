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

#include <stdio.h>
#include <errno.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <libusb.h>
#include <iconv.h>

#include "jlibnxt.h"

// add missing typedef
typedef struct libusb_device_descriptor libusb_device_descriptor;

#define MAX_ADDR  64
#define MAX_SERNO 64
#define MAX_WRITE lejos_pc_comm_NXTCommLibnxt_USB_BUFSZ

// On windows ETIMEDOUT is not defined. The windows driver uses 116 as the value
#ifndef ETIMEDOUT
#define ETIMEDOUT 116
#endif

#define PTR2JLONG(arg) ((jlong)(uintptr_t)(arg))
#define JLONG2PTR(type, arg) ((type*)(uintptr_t)(arg))

#define VENDOR_LEGO   0x0694
#define PRODUCT_NXT   0x0002
#define VENDOR_ATMEL  0x03EB
#define PRODUCT_SAMBA 0x6124

#define TYPE_UNKNOWN lejos_pc_comm_NXTCommLibnxt_TYPE_UNKNOWN
#define TYPE_SAMBA   lejos_pc_comm_NXTCommLibnxt_TYPE_SAMBA
#define TYPE_LEGO    lejos_pc_comm_NXTCommLibnxt_TYPE_LEGO

static libusb_context *context = NULL;

__attribute__((__constructor__))
static void ctor() {
	libusb_init(&context);
}
__attribute__((__destructor__))
static void dtor() {
	libusb_exit(context);
	context = NULL;
}

static int getType(libusb_device_descriptor *dev) {
	if (dev->idVendor == VENDOR_ATMEL && dev->idProduct == PRODUCT_SAMBA)
		return TYPE_SAMBA;
	if (dev->idVendor == VENDOR_LEGO && dev->idProduct == PRODUCT_NXT)
		return TYPE_LEGO;

	return TYPE_UNKNOWN;
}

/**
 * Create the device address string. We use the same format as the
 * Lego Fantom device driver.
 */
static int create_address(libusb_device *dev, char *address) {
	uint8_t busnum = libusb_get_bus_number(dev);
	uint8_t devnum = libusb_get_device_address(dev);

	libusb_device_descriptor descriptor;
	if (libusb_get_device_descriptor(dev, &descriptor) < LIBUSB_SUCCESS) {
		*address = 0;
		return TYPE_UNKNOWN;
	}

	int type = getType(&descriptor);
	snprintf(address, MAX_ADDR, "%u,%u,%d", busnum, devnum, type);

	return type;
}

/* Return a handle to the nth NXT device, or null if not found/error
 * Also return a dvice address string that contains all of the details of
 * this device. This string can be used later to re-locate the device  */
static libusb_device *find_nxt(const char *address) {
	libusb_device **list;
	int len = libusb_get_device_list(context, &list);
	if (len < LIBUSB_SUCCESS)
		return NULL;

	libusb_device *dev = NULL;
	for (int i = 0; i < len; i++) {
		dev = list[i];

		char adr[MAX_ADDR];
		int type = create_address(dev, adr);

		if (type != TYPE_UNKNOWN && strcmp(address, adr) == 0) {
			libusb_ref_device(dev);
			break;
		}
	}
	libusb_free_device_list(list, 1);
	return dev;
}

// Version of open that works with lejos NXJ firmware.
static libusb_device_handle *nxt_open(libusb_device *dev) {
	libusb_device_handle *hdl;
	if (libusb_open(dev, &hdl) < LIBUSB_SUCCESS)
		return 0;

	libusb_device_descriptor descriptor;
	if (libusb_get_device_descriptor(dev, &descriptor) < LIBUSB_SUCCESS)
		goto release;

	// If we are in SAMBA mode we need interface 1, otherwise 0
	int interf;
	if (getType(&descriptor) == TYPE_SAMBA) {
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

	if (libusb_set_configuration(hdl, 1) < LIBUSB_SUCCESS)
		goto release;

	if (libusb_claim_interface(hdl, interf) < LIBUSB_SUCCESS)
		goto release;

	// Discard any data that is left in the buffer
	unsigned char buf[64];
	while (1) {
		int alen = 0;
		int ret = libusb_bulk_transfer(hdl, 0x82, buf, sizeof(buf), &alen, 1);
		if (alen == 0 || ret < 0)
			break;
	}

	return hdl;
release:
	libusb_close(hdl);
	return 0;
}

// Version of close that uses interface 0
static void nxt_close(libusb_device_handle *hdl) {
	// Discard any data that is left in the buffer
	unsigned char buf[64];
	while (1) {
		int alen = 0;
		int ret = libusb_bulk_transfer(hdl, 0x82, buf, sizeof(buf), &alen, 1);
		if (alen == 0 || ret < 0)
			break;
	}

	// Release interface. This is a little iffy we do not know which interface
	// we are actually using. Releasing both seems to work... but...
//	libusb_release_interface(hdl, 0);
//	libusb_release_interface(hdl, 1);
	libusb_close(hdl);
}

static int nxt_get_serial(libusb_device_handle *hdl, char *rbuf) {
	libusb_device *dev = libusb_get_device(hdl);

	libusb_device_descriptor descriptor;
	int ret = libusb_get_device_descriptor(dev, &descriptor);
	if (ret < LIBUSB_SUCCESS)
		return ret;

	unsigned char buf[2+2*MAX_SERNO];
	int len = libusb_get_string_descriptor(hdl, descriptor.iSerialNumber, 0, buf, sizeof(buf));
	if (len < LIBUSB_SUCCESS)
		return len;

	if (len > 0 && len > buf[0])
		len = buf[0];

	if (len <= 2) {
		*rbuf = 0;
	} else {
		char *ibuf[1] = { (char*)(buf + 2) };
		size_t ilen[1] = { len - 2 };
		char *obuf[1] = { rbuf };
		size_t olen[1] = { 4 * MAX_SERNO };

		iconv_t cd = iconv_open("utf8", "utf16le");
		iconv(cd, ibuf, ilen, obuf, olen);
		iconv_close(cd);
		**obuf = 0;
	}
	return LIBUSB_SUCCESS;
}

// Implement 20sec timeout write, and return amount actually written
static int nxt_write_buf(libusb_device_handle *hdl, void *buf, int len) {
	int alen = 0;
	int ret = libusb_bulk_transfer(hdl, 0x01, buf, len, &alen, 20000);
	return (ret < 0) ? ret : alen;
}

// Implement 20 second timeout read, and return amount actually read
static int nxt_read_buf(libusb_device_handle *hdl, void *buf, int len) {
	int alen = 0;
	int ret = libusb_bulk_transfer(hdl, 0x82, buf, len, &alen, 20000);
	return (ret < 0) ? ret : alen;
}

JNIEXPORT jobjectArray JNICALL Java_lejos_pc_comm_NXTCommLibnxt_nList(
		JNIEnv *env, jobject obj) {
	libusb_device **list;
	int len = libusb_get_device_list(context, &list);

	int found = 0;
	jstring *buf = malloc(len * sizeof(jstring));

	for (int i = 0; i < len; i++) {
		libusb_device *dev = list[i];

		char adr[MAX_ADDR];
		int type = create_address(dev, adr);

		if (type != TYPE_UNKNOWN) {
			buf[found++] = (*env)->NewStringUTF(env, adr);
		}
	}
	libusb_free_device_list(list, 1);

	// Now copy names in a java array
	jclass sclass = (*env)->FindClass(env, "java/lang/String");
	jobjectArray arr = (*env)->NewObjectArray(env, found, sclass, NULL);
	for (int i = 0; i < found; i++)
		(*env)->SetObjectArrayElement(env, arr, i, buf[i]);

	free(buf);
	return arr;
}

JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommLibnxt_nOpen(JNIEnv *env,
		jobject obj, jstring jnxt) {
	const char* nxt = (*env)->GetStringUTFChars(env, jnxt, 0);
	jlong r = 0;

	libusb_device *dev = find_nxt(nxt);
	if (dev != NULL) {
		libusb_device_handle *ret = nxt_open(dev);
		libusb_unref_device(dev);
		r = PTR2JLONG(ret);
	}

	(*env)->ReleaseStringUTFChars(env, jnxt, nxt);
	return r;
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommLibnxt_nClose(JNIEnv *env,
		jobject obj, jlong nxt) {
	nxt_close(JLONG2PTR(libusb_device_handle, nxt));
}

JNIEXPORT jstring JNICALL Java_lejos_pc_comm_NXTCommLibnxt_nGetSerial(
		JNIEnv *env, jobject obj, jlong nxt) {
	char serial[4 * MAX_SERNO + 1];
	int ret = nxt_get_serial(JLONG2PTR(libusb_device_handle, nxt), serial);
	if (ret < LIBUSB_SUCCESS)
		return NULL;
	return (*env)->NewStringUTF(env, serial);
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_nSendData(JNIEnv *env,
		jobject obj, jlong nxt, jbyteArray data, jint offset, jint len) {
	jbyte *jb = (*env)->GetByteArrayElements(env, data, 0);
	int ret = nxt_write_buf(JLONG2PTR(libusb_device_handle, nxt),
			jb + offset, len);
	(*env)->ReleaseByteArrayElements(env, data, jb, 0);
	if (ret == LIBUSB_ERROR_TIMEOUT)
		ret = 0;
	return ret;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_nReadData(JNIEnv *env,
		jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len) {
	jbyte *jb = (*env)->GetByteArrayElements(env, jdata, 0);
	int ret = nxt_read_buf(JLONG2PTR(libusb_device_handle, nxt),
			jb + offset, len);
	(*env)->ReleaseByteArrayElements(env, jdata, jb, 0);
	if (ret == LIBUSB_ERROR_TIMEOUT)
		ret = 0;
	return ret;
}

