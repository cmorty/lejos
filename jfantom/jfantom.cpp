
#include "fantom/iNXT.h"
#include "fantom/iNXTIterator.h"
#include "fantom/tStatus.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "jfantom.h"

static ViUInt8 responseBuffer[65];
static int responseLen = 0;
static nFANTOM100::iFileIterator* fileIteratorPtr;
static nFANTOM100::iFile* filePtr;


void sleep(unsigned int mseconds)
{
    clock_t goal = mseconds + clock();
    while (goal > clock());
}


int countNXTs() {
   nFANTOM100::tStatus status;
   int count = 0;

   // Create an NXT iterator to find all available NXTs

   nFANTOM100::iNXTIterator* nxtIteratorPtr = nFANTOM100::iNXT::createNXTIterator(
         true /* search Bluetooth and USB */,
         0 /* Infinite timeout */, status );

   while (status.isNotFatal()) {
     count++;
     nxtIteratorPtr->advance(status);
   }

   if (nxtIteratorPtr != NULL) nFANTOM100::iNXT::destroyNXTIterator( nxtIteratorPtr );

   return count;
}
   
JNIEXPORT jobjectArray JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1find
  (JNIEnv *env, jobject obj)
{
   nFANTOM100::tStatus status;
   nFANTOM100::iNXTIterator* nxtIteratorPtr = NULL;
   ViChar name[65];
   int i = 0;

   jclass sclass = env->FindClass("java/lang/String");
   jobjectArray arr = env->NewObjectArray(countNXTs(), sclass, NULL);

   
   // Create an NXT iterator to find all available NXTs

   nxtIteratorPtr = nFANTOM100::iNXT::createNXTIterator(
         true /* search Bluetooth and USB */,
         0 /* Infinite timeout */, status );

   while (status.isNotFatal()) {

      // get the name of the NXT

      nxtIteratorPtr->getName(name, status);

      if (status.isFatal()) {
         printf("Failed to get name of NXT\n");
         exit(1);
      }

      jstring j_str = env->NewStringUTF(name) ;

      env->SetObjectArrayElement(arr, i++, j_str);

      nxtIteratorPtr->advance(status);
   }

   if (nxtIteratorPtr != NULL) nFANTOM100::iNXT::destroyNXTIterator( nxtIteratorPtr );
   
   return arr;
}


JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1open
  (JNIEnv *env, jobject obj, jstring nxt)
{
   nFANTOM100::tStatus status;
   ViChar resourceString[256];

   const ViChar* cstr = env->GetStringUTFChars(nxt, 0);

   printf("Connecting to %s\n", cstr);

   ViBoolean paired = nFANTOM100::iNXT::isPaired(cstr,status);
   
   if (status.isFatal()) {
     printf("Failed to check is NXT is paired\n");
     exit(1);
   }

   if (paired) printf("Paired\n"); else printf("Not paired\n");

   /* if (paired) {
       nFANTOM100::iNXT::unpairBluetooth(cstr, status);

      if (status.isFatal()) {
         printf("Failed to unpair NXT\n");
         exit(1);
      }
      paired = false;
   } */

   if (!paired) {
      nFANTOM100::iNXT::pairBluetooth(cstr, "1234", resourceString, status);

      if (status.isFatal()) {
         printf("Failed to pair NXT\n");
         exit(1);
      }
   } strcpy(resourceString, cstr);

   printf("Resource string is %s\n", resourceString);

   nFANTOM100::iNXT* nxtPtr = nFANTOM100::iNXT::createNXT(resourceString, status, true);

   if (status.isFatal()) {
      printf("Failed to connect to NXT\n");
      exit(1);
   }

   return (int) nxtPtr;
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1close
  (JNIEnv *env, jobject obj, jint nxt)
{
}

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1send_1data
  (JNIEnv *env, jobject obj, jint nxt, jbyteArray jdata, jint len, jint replyLen)
{
   nFANTOM100::tStatus status;
   nFANTOM100::iNXT* nxtPtr = (nFANTOM100::iNXT*) nxt;
   int i;
   ViChar fileName[20];

   //printf("len = %d\n", len);
   //printf("replyLen = %d\n", replyLen);

   jbyte *jb = env->GetByteArrayElements(jdata, 0);

   //printf("Command = %d\n", jb[1]);

   responseBuffer[1] = jb[1];
   responseBuffer[2] = 0;
   if (jb[1] == -122) { // Find first
     if (nxtPtr == NULL) {
       printf("NXT Ptr is null\n");
       exit(1);
     }
     fileIteratorPtr = nxtPtr->createFileIterator("*.*",status);
     if (status.isFatal()) {
       printf("Failed to create iterator\n");
       exit(1);
     }
     fileIteratorPtr->getName(fileName,status);
     if (status.isFatal()) {
       printf("Failed to get filename\n");
       exit(1);
     }
     //printf("File = %s\n",fileName);
     for(i=0;i<20;i++) responseBuffer[i+4] = fileName[i];
     int size = fileIteratorPtr->getSize(status);
     if (status.isFatal()) {
       printf("Failed to get file size\n");
       exit(1);
     }
     responseBuffer[27] = (size >> 24) & 0xFF;
     responseBuffer[26] = (size >> 16) & 0xFF;
     responseBuffer[25] = (size >> 8) & 0xFF;
     responseBuffer[24] = size & 0xFF;
     responseLen = 28;
   } else if (jb[1] == -121) {
     if (fileIteratorPtr == NULL) {
       printf("Null file Iterator\n");
       exit(1);
     }
     fileIteratorPtr->advance(status);
     responseLen = 28;
     if (status.isFatal()) {
       //printf("No more files\n");
       responseBuffer[2] = 0x86;
     } else {
       fileIteratorPtr->getName(fileName,status);
       if (status.isFatal()) {
         printf("Failed to get filename\n");
         exit(1);
       }
       //printf("File = %s\n",fileName);
       for(i=0;i<20;i++) responseBuffer[i+4] = fileName[i];
       int size = fileIteratorPtr->getSize(status);
       if (status.isFatal()) {
         printf("Failed to get file size\n");
         exit(1);
       }
       responseBuffer[27] = (size >> 24) & 0xFF;
       responseBuffer[26] = (size >> 16) & 0xFF;
       responseBuffer[25] = (size >> 8) & 0xFF;
       responseBuffer[24] = size & 0xFF;
     }
   } else if (jb[1] == -128) { // Open for read
     if (filePtr == NULL) {
       printf("Null file ptr\n");
       exit(1);
     } 
     filePtr = nxtPtr->createFile(reinterpret_cast< ViConstString >(&jb[2]), status);
     if (status.isFatal()) {
       printf("Failed to create file object\n");
       exit(1);
     }
     filePtr->openForRead(status);
     if (status.isFatal()) {
       printf("Failed to open file for read\n");
       exit(1);
     }
     if (fileIteratorPtr == NULL) {
       printf("Null file iterator ptr\n");
       exit(1);
     } 
     int size = fileIteratorPtr->getSize(status);
     if (status.isFatal()) {
       printf("Failed to get file size\n");
       exit(1);
     } 
     responseBuffer[7] = (size >> 24) & 0xFF;
     responseBuffer[6] = (size >> 16) & 0xFF;
     responseBuffer[5] = (size >> 8) & 0xFF;
     responseBuffer[4] = size & 0xFF;
     responseLen = 8; 
   } else if (jb[1] == -127) { // Open for write
     //printf("Opening for write\n");
     if (nxtPtr == NULL) {
       printf("Null NXT ptr\n");
       exit(1);
     } 
     filePtr = nxtPtr->createFile(reinterpret_cast< ViConstString >(&jb[2]), status);
     if (status.isFatal()) {
       printf("Failed to create vfile object\n");
       exit(1);
     }
     int size = ((jb[25] & 0xFF) << 24) + ((jb[24] & 0xFF) << 16) + ((jb[23] & 0xFF) << 8) + (jb[22] & 0xFF);
     //size = 15000;
     printf("size is %d\n", size);
     filePtr->openForWrite(size, status);
     if (status.isFatal()) {
       printf("Failed to open file for writing\n");
       exit(1);
     }
     responseLen = 4;
   }else if (jb[1] == -124) { // Close
     if (filePtr == NULL) {
       printf("Null file ptr\n");
       exit(1);
     } 
     filePtr->close(status);
     if (status.isFatal()) {
       printf("Failed to close file\n");
       exit(1);
     }
     responseLen = 4;
   } else if (jb[1] == -123) { // Delete
     if (nxtPtr == NULL) {
       printf("Null NXT ptr\n");
       exit(1);
     }
     filePtr = nxtPtr->createFile(reinterpret_cast< ViConstString >(&jb[2]), status);
     if (status.isFatal()) {
       printf("Failed to create vfile object\n");
       exit(1);
     }
     filePtr->remove(status);
     if (status.isFatal()) {
       printf("Failed to remove file\n");
       exit(1);
     }
     responseLen = 23;
   } else if (jb[1] == -126) { // Read
     //printf("Reading %d bytes\n", jb[3]);
     int bytesRead = filePtr->read(reinterpret_cast< ViByte* > (&responseBuffer[6]),jb[3], status);
     //printf("%d bytes read\n", bytesRead);
     responseBuffer[4] = bytesRead;
     responseBuffer[5] = 0;
     responseLen = bytesRead+6;
   } else if (jb[1] == -125) { // Write
     //printf("Writing %d bytes\n", len-3);
     if (filePtr == NULL) {
       printf("Null file ptr\n");
       exit(1);
     }
     int bytesWritten = filePtr->write(reinterpret_cast< ViByte* > (&jb[3]),len-3, status);
     if (status.isFatal()) {
       printf("Failed to write file\n");
       exit(1);
     }
     //printf("%d bytes written\n", bytesWritten);
     responseBuffer[4] = bytesWritten;
     responseBuffer[5] = 0;
     responseLen = 6;
   } else if (jb[0] == 0 || jb[0] == -128) {
     // Send the direct command to the NXT.
     //printf("Byte 0 is %d\n", jb[0]);
     if (jb[1] == 0) len = 22; // start program
     if (nxtPtr == NULL) {
       printf("NXT Ptr is null\n");
       exit(1);
     }
     responseLen = nxtPtr->sendDirectCommand( jb[0] >= 0,
                                                reinterpret_cast< ViByte* >(&jb[1]), 
                                                len - 1,
                                                (replyLen > 0 ? reinterpret_cast< ViByte* > (&responseBuffer[1]) : NULL), (replyLen <= 0 ? 0 : replyLen), status );
     if (status.isFatal()) {
       printf("Failure to send a direct command\n");
       exit(1);
     }
     //printf("Response length = %d\n", responseLen);

     //for(i=0;i<replyLen;i++) printf("Reply[%d] = %d\n",i, responseBuffer[i+1]);
   }
   env->ReleaseByteArrayElements(jdata, jb, 0);
}

JNIEXPORT jbyteArray JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1read_1data
  (JNIEnv *env, jobject obj,jint nxt, jint len)
{

  jbyteArray newByteArray = env->NewByteArray(responseLen);
  env->SetByteArrayRegion(newByteArray, 0, responseLen, (const jbyte *) responseBuffer);
  //printf("Returning %d bytes\n",responseLen);
  return(newByteArray);
}

