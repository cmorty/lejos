#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#include "lejos_nxt_SensorPort.h"
#include "lejos_nxt_NXTEvent.h"

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_i2cEnableById
  (JNIEnv *env, jclass cls, jint id, jint mode)
{
  printf("Enabling port %d in mode %d\n",id,mode);
}

JNIEXPORT void JNICALL Java_lejos_nxt_SensorPort_i2cDisableById
  (JNIEnv *env, jclass cls, jint id) 
{
  printf("Disabling port %d\n",id);
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_i2cStartById
  (JNIEnv *env, jclass cls, jint id, jint addr, jbyteArray buf, jint offset, jint wlen, jint rlen)
{
  printf("Writing %d bytes on port %d at address %d\n",wlen,id,addr);
  return wlen;
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_i2cCompleteById
  (JNIEnv *env, jclass cls, jint id, jbyteArray buf, jint offset, jint rlen) 
{
  printf("Reading %d bytes from port %d\n",rlen,id);
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
}

JNIEXPORT jint JNICALL Java_lejos_nxt_SensorPort_readSensorPin
  (JNIEnv *env, jclass cls, jint id, jint pin)
{
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
 





