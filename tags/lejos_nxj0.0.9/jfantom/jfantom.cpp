/* The fantom includes are not designed to compile with gcc for Windows.
 * The following lets us fool the system so that it will build.
 */
#if (( defined( __GNUG__ ) || defined( __GNUC__ )) && defined( __WIN32__))
#define _M_I86
#define _MSC_VER
#include "fantom/platform.h"
#undef _M_I86
#undef _MSC_VER
#endif
#include "fantom/iNXT.h"
#include "fantom/iNXTIterator.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "jfantom.h"
#define MAX_DEVS 64
#define MAX_READ 64
#define MAX_WRITE 512


// Pointer type as integer
typedef long INTPTR;

#if (defined(__WIN32__) || defined(_MSC_VER))
// Locking version for Windows
#include <windows.h>
HANDLE lockDevice(const ViChar* name)
{
   /*
    * Windows device locking code. The Fantom driver does not prevent
    * two processes from opening the same USB device. So we provide
    * our own locking. We use a Windows mutex to provide the lock
    * since this will be automatically released by the system in the
    * event of a program crash. We use the documented feature of a 
    * mutex that the first user of a mutex will create it and subsequent 
    * calls to create will simply open it and will set an error code
    * to indicate that the mutex already exists. The mutex is automatically
    * deleted on the last close.
    */
   HANDLE h = CreateMutex(NULL, FALSE, name);
   if (h == NULL) return NULL;
   if (GetLastError() == NOERROR) return h;
   CloseHandle(h);
   return NULL;
}

void unlockDevice(HANDLE h)
{
   // Release the lock
   if (h != NULL)
      CloseHandle(h);
}
#else
// Non Windows version, do nothing
typedef void *  HANDLE;
HANDLE lockDevice(const ViChar* name)
{
    return (HANDLE)1;
}

void unlockDevice(HANDLE h)
{
}
#endif

// Hold information about the USB device
typedef struct 
{
   HANDLE hLock;
   nFANTOM100_iNXT nxtPtr;
} NXTDev;


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
   if (status >= VI_SUCCESS)
   {
      do {
         // get the name of the NXT

         nFANTOM100_iNXTIterator_getName(nxtIteratorPtr, name, &status);

         if (status < VI_SUCCESS) {
            return NULL;
         }
         names[cnt++] = env->NewStringUTF(name) ;

         nFANTOM100_iNXTIterator_advance(nxtIteratorPtr, &status);
      } while (status >= VI_SUCCESS);
      nFANTOM100_destroyNXTIterator( nxtIteratorPtr, &status );
   }
   // Look to see if there is a lego device in samba mode
   status = 0;
   nFANTOM100_iNXT_findDeviceInFirmwareDownloadMode(name, &status);
   if (status >= VI_SUCCESS)
   {
     names[cnt++] = env->NewStringUTF(name);
   }
   if (cnt <= 0) return NULL;

   // Now copy names in a java array
   jclass sclass = env->FindClass("java/lang/String");
   jobjectArray arr = env->NewObjectArray(cnt, sclass, NULL);
   for(i = 0; i < cnt; i++)
      env->SetObjectArrayElement(arr, i, names[i]);
   
   return arr;
}


JNIEXPORT jlong JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1open
  (JNIEnv *env, jobject obj, jstring nxt)
{
   ViStatus status=0;;
   ViChar resourceString[256];
   nFANTOM100_iNXT nxtPtr; 
   NXTDev *dev = (NXTDev *)malloc(sizeof(NXTDev));
   // Make sure we have space to store the device info.
   if (dev == NULL) return 0;

   // Try and lock the device
   const ViChar* cstr = env->GetStringUTFChars(nxt, 0);
   HANDLE hLock = lockDevice(cstr);
   if (hLock != NULL)
   {
      // Device locked, try and open it
      strcpy(resourceString, cstr);
      nxtPtr = nFANTOM100_createNXT(resourceString, &status, false);
      if (status >= VI_SUCCESS) 
      {
         // Remember the details
         dev->hLock = hLock;
         dev->nxtPtr = nxtPtr;
         return (jlong) (INTPTR)dev;
      }
      // Open failed release the lock
      unlockDevice(hLock);
   }
   // failed clean things up.
   free(dev);
   return 0;
}


JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1close
  (JNIEnv *env, jobject obj, jlong nxt)
{
   NXTDev *dev = (NXTDev *)(INTPTR)nxt;
   if (dev == NULL) return;
   ViStatus status=0;;
   nFANTOM100_destroyNXT( dev->nxtPtr, &status );
   unlockDevice(dev->hLock);
   free(dev);
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1send_1data
  (JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)
{
   ViStatus status=0;
   int ret;
   NXTDev *dev = (NXTDev *)(INTPTR)nxt;
   if (dev == NULL) return -1;

   jbyte *jb = env->GetByteArrayElements(jdata, 0);
   if (len > MAX_WRITE) len = MAX_WRITE;
   ret = nFANTOM100_iNXT_write(dev->nxtPtr, (const unsigned char *) jb + offset, len, &status);
   env->ReleaseByteArrayElements(jdata, jb, 0);
   return ret;
}

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommFantom_jfantom_1read_1data
  (JNIEnv *env, jobject obj, jlong nxt, jbyteArray jdata, jint offset, jint len)
{
   ViStatus status=0;
   int read_len;
   char *data;
   NXTDev *dev = (NXTDev *)(INTPTR)nxt;
   if (dev == NULL) return -1;

   jbyte *jb = env->GetByteArrayElements(jdata, 0);
   if (len > MAX_READ) len = MAX_READ;
   read_len = nFANTOM100_iNXT_read(dev->nxtPtr, (unsigned char *)jb + offset, len, &status);
   env->ReleaseByteArrayElements(jdata, jb, 0);
   return read_len;
}
