
#include "fantom/iNXT.h"
#include "fantom/iNXTIterator.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "jfantom.h"

#define MAX_DEVS 64
#define MAX_WRITE 64

JNIEXPORT jobjectArray JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1find
  (JNIEnv *env, jobject obj)
{
   jstring names[MAX_DEVS];
   int cnt = 0;
   ViStatus status=0;;
   nFANTOM100_iNXTIterator nxtIteratorPtr;
   ViChar name[65];
   int i = 0;

   // Create an NXT iterator to find all available NXTs

   nxtIteratorPtr = nFANTOM100_createNXTIterator(
         false /* search Bluetooth and USB */,
         0 /* Infinite timeout */, &status );
   if (status < VI_SUCCESS)
   {
      return NULL;
   }
   while (status >= VI_SUCCESS) {

      // get the name of the NXT

      nFANTOM100_iNXTIterator_getName(nxtIteratorPtr, name, &status);

      if (status < VI_SUCCESS) {
         return NULL;
      }

      names[cnt++] = env->NewStringUTF(name) ;

      nFANTOM100_iNXTIterator_advance(nxtIteratorPtr, &status);
   }

   // Now copy names in a java array
   jclass sclass = env->FindClass("java/lang/String");
   jobjectArray arr = env->NewObjectArray(cnt, sclass, NULL);
   for(i = 0; i < cnt; i++)
      env->SetObjectArrayElement(arr, i, names[i]);
   nFANTOM100_destroyNXTIterator( nxtIteratorPtr, &status );
   
   return arr;
}


JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1open
  (JNIEnv *env, jobject obj, jstring nxt)
{
   ViStatus status=0;;
   ViChar resourceString[256];
   nFANTOM100_iNXT nxtPtr; 

   const ViChar* cstr = env->GetStringUTFChars(nxt, 0);

   strcpy(resourceString, cstr);


   nxtPtr = nFANTOM100_createNXT(resourceString, &status, true);

   if (status < VI_SUCCESS) {
      return 0;
   }
   return (jlong) nxtPtr;
}


JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1close
  (JNIEnv *env, jobject obj, jlong nxt)
{
   ViStatus status=0;;
   nFANTOM100_destroyNXT( (nFANTOM100_iNXT) nxt, &status );
   if (status < VI_SUCCESS)
      printf("Failed to close nxt\n");
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1send_1data
  (JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)
{
   ViStatus status=0;
   int ret;



   jbyte *jb = env->GetByteArrayElements(jdata, 0);
   if (len > MAX_WRITE) len = MAX_WRITE;
   ret = nFANTOM100_iNXT_write((nFANTOM100_iNXT)nxt, (const unsigned char *) jb + offset, len, &status);
   env->ReleaseByteArrayElements(jdata, jb, 0);
   return ret;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1read_1data
  (JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)
{
   ViStatus status=0;
   int read_len;
   char *data;
   jbyte *jb = env->GetByteArrayElements(jdata, 0);

   read_len = nFANTOM100_iNXT_read((nFANTOM100_iNXT)nxt, (unsigned char *)jb + offset, len, &status);
   env->ReleaseByteArrayElements(jdata, jb, 0);
   return read_len;
}
