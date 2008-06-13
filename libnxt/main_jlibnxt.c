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
#define MAX_DEVS 64

// On windows ETIMEDOUT is not defined. The windows driver uses 116 as the value
#ifndef ETIMEDOUT
#define ETIMEDOUT 116
#endif

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
      return (jlong) nxt_open0( dev ); 
    }
    cnt++;
  }
  return (jlong) 0;
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
   // Assume any error is a timeout!!! Need to fix this!
   if (ret < 0) ret = 0;
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

