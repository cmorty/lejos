/**
 * JNI interface for libnxt.
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
#include "lowlevel.h"
#include "jlibnxt.h"

#define MAX_WRITE 64
#define MAX_SERNO 26

JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1find(JNIEnv *env, jobject obj, jint idx) {
    return (jlong) nxt_find_nth((int)idx);
}

JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1open(JNIEnv *env, jobject obj, jlong nxt)  {
  return (jlong) nxt_open0( (long) nxt); 
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1close(JNIEnv *env, jobject obj, jlong nxt)  {
  nxt_close0( (long) nxt); 
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1send_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray data, jint offset, jint len)  {
  int ret;
  char *jb = (char *) (*env)->GetByteArrayElements(env, data, 0);  
  if (len > MAX_WRITE) len = MAX_WRITE;

  ret = nxt_write_buf((long) nxt, jb+offset, len);
  (*env)->ReleaseByteArrayElements(env, data, (jbyte *) jb, 0);
  return ret;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1read_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)  {
   int read_len;
   char *jb = (char *)(*env)->GetByteArrayElements(env, jdata, 0);

   read_len = nxt_read_buf((long)nxt, jb + offset, len);
   (*env)->ReleaseByteArrayElements(env, jdata, (jbyte *)jb, 0);
   return read_len;
}


JNIEXPORT jstring JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1serial(JNIEnv *env, jobject obj, jlong nxt)
{
  char serno[MAX_SERNO];
  int len = nxt_serial_no((long)nxt, serno, sizeof(serno));
  if (len <= 0) return NULL;
  // Length of the string descriptor is in the first byte, the 2nd byte is 
  // a type field (always 3) and the length contains these two bytes.
  len = (serno[0] - 2)/2;
  if (len <= 0) return NULL;
  return (*env)->NewString(env, (jchar *)(serno+2), len);
}

