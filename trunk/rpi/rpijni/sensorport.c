#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <linux/i2c-dev.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

#include "lejos_nxt_SensorPort.h"
#include "lejos_nxt_NXTEvent.h"

#define debug 0

int fd;														// File description
char *fileName = "/dev/i2c-0";								// Name of the port we will be using

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_i2cEnableById
  (JNIEnv *env, jclass cls, jint id, jint mode)
{
  if (debug) printf("Enabling port %d in mode %d\n",id,mode);
  if ((fd = open(fileName, O_RDWR)) < 0) {					// Open port for reading and writing
	printf("Failed to open i2c port\n");
	exit(1);
  }
}

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_i2cDisableById
  (JNIEnv *env, jclass cls, jint id) 
{
  if (debug) printf("Disabling port %d\n",id);
  close(fd);
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_i2cStartById
  (JNIEnv *env, jclass cls, jint id, jint addr, jbyteArray buf, jint offset, jint wlen, jint rlen)
{
  if (debug)printf("Writing %d bytes on port %d at address %d\n",wlen,id,addr);
  if (ioctl(fd, I2C_SLAVE, (addr >> 1)) < 0) {	// Set the port options and set the address of the device we wish to speak to
	printf("Unable to get bus access to talk to slave\n");
	exit(1);
  }
  char *jb = (char *) (*env)->GetByteArrayElements(env, buf, 0); 
  if (debug) printf("First byte is %d\n",*jb);
  if ((write(fd, jb, wlen)) != wlen) {	// Send the register to read from
	printf("Error writing to i2c slave - %s\n",strerror(errno));
	exit(1);
  }
  (*env)->ReleaseByteArrayElements(env, buf, (jbyte *) jb, 0);
  return wlen;
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_i2cCompleteById
  (JNIEnv *env, jclass cls, jint id, jbyteArray buf, jint offset, jint rlen) 
{
  if (debug) printf("Reading %d bytes from port %d\n",rlen,id);
  if (rlen > 0) {
    char *jb = (char *) (*env)->GetByteArrayElements(env, buf, 0);
  	if (read(fd, jb, rlen) != rlen) {	// Read back data into buf[]
		printf("Unable to read from slave\n");
		exit(1);
	}
	(*env)->ReleaseByteArrayElements(env, buf, (jbyte *) jb, 0);
  }
  return rlen;
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_i2cStatusById
  (JNIEnv *env, jclass cls, jint id)
{
  return 0;
}

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_setPowerTypeById
  (JNIEnv *env, jclass cls, jint id, jint typ) 
{
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_readSensorValue
  (JNIEnv *env, jclass cls, jint id)
{
  return 0;
}

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_setSensorPinMode
  (JNIEnv *env, jclass cls, jint id, jint pin , jint mode) 
{
}

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_setSensorPin
  (JNIEnv *env, jclass cls, jint id, jint pin, jint val)
{
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_getSensorPin
  (JNIEnv *env, jclass cls, jint id, jint pin) 
{
  return 0;
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_readSensorPin
  (JNIEnv *env, jclass cls, jint id, jint pin)
{
  return 0;
}

JNIEXPORT jint JNICALL Java_lejos_nxt_NXTEvent_registerEvent
  (JNIEnv *env, jobject obj)
 {
   return 0;
 }
 
 JNIEXPORT jint JNICALL Java_lejos_nxt_NXTEvent_unregisterEvent
  (JNIEnv *env, jobject obj)
 {
   return 0;
 }
 
 JNIEXPORT jint JNICALL Java_lejos_nxt_NXTEvent_changeEvent
  (JNIEnv *env, jobject obj, jint x, jint y) 
 {
   return 0;
 }
 





