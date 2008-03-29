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

void throwIOException(JNIEnv *env, char *msg)
{
	jclass exception_cls;

	exception_cls = (*env)->FindClass(env, "java/io/IOException");
	if ((*env)->ThrowNew(env, exception_cls, msg) < 0)
	{
		fprintf(stderr, "** Error throwing IOexception - exiting **\n");
		fprintf(stderr, "Message:\n%s\n", msg);
		exit(1);
	}
	return;
}

JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1find(JNIEnv *env, jobject obj) {
  nxt_t *nxt;
  nxt_error_t nxt_err;
	
  nxt_err = nxt_init(&nxt);
	
  if (nxt_err == NXT_OK) {
    nxt_err = nxt_find(nxt);
    if (nxt_err == NXT_OK && !(nxt_in_reset_mode(nxt))) {
      return (jlong) (unsigned long) nxt;
    }
  }
  return (jlong) 0;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1open(JNIEnv *env, jobject obj, jlong nxt)  {
  return (jint) nxt_open0( (nxt_t *) (unsigned long) nxt); 
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1close(JNIEnv *env, jobject obj, jlong nxt)  {
  nxt_error_t nxt_err;
  
  nxt_err = nxt_close0( (nxt_t *) (unsigned long) nxt); 
  
  if (nxt_err != NXT_OK) {
    throwIOException(env,"Close failed");
  } 
   
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1send_1data(JNIEnv *env, jobject obj, jlong nxt, jbyteArray data)  {
  int write_len;
  int written = 0;

  jsize len2 = (*env)->GetArrayLength(env, data);
  char *elements2 = (char *) (*env)->GetByteArrayElements(env, data, 0);  

  while (written < len2)
  {
    write_len = nxt_write_buf((nxt_t *) (unsigned long) nxt, elements2+written, len2 - written);
    if (write_len < 0) {
      throwIOException(env,"Send failed");
      break;
    }
    written += write_len;
  }

  (*env)->ReleaseByteArrayElements(env, data, (jbyte *) elements2, 0);
}

JNIEXPORT jbyteArray JNICALL Java_lejos_pc_comm_NXTCommLibnxt_jlibnxt_1read_1data(JNIEnv *env, jobject obj, jlong nxt, jint len)  {
  int read_len;
  char *data;
  jbyteArray jb;

  data = (char *) calloc(1, len);
  
  read_len = nxt_read_buf((nxt_t *) (unsigned long) nxt, data, len); // read data
  
  if (read_len < 0) {
    throwIOException(env,"Read failed");
    free(data);
    return NULL;
  }
    
  jb=(*env)->NewByteArray(env, read_len);
  (*env)->SetByteArrayRegion(env, jb, 0, read_len, (jbyte *) data);
  free(data);
  return (jb);    
}
