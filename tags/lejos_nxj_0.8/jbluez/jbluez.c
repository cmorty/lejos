#include <stdio.h>
#include <errno.h>
#include <ctype.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/socket.h>

#include <bluetooth/bluetooth.h>
#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>
#include <bluetooth/rfcomm.h>

#include "jbluez.h"

void throwNewBlueZException(JNIEnv *env, char *msg)
{
	jclass exception_cls;

	exception_cls = (*env)->FindClass(env, "lejos/pc/comm/BlueZException");
	if ((*env)->ThrowNew(env, exception_cls, msg) < 0)
	{
		fprintf(stderr, "** Error throwing BlueZException exception - exiting **\n");
		fprintf(stderr, "Message:\n%s\n", msg);
		exit(1);
	}
	return;
}

//---------------------------------------------------------------------------//

void throwIOException(JNIEnv *env, char *msg)
{
	jclass exception_cls;

	exception_cls = (*env)->FindClass(env, "java/io/IOException");
	if ((*env)->ThrowNew(env, exception_cls, msg) < 0)
	{
		fprintf(stderr, "** Error throwing IOException - exiting **\n");
		fprintf(stderr, "Message:\n%s\n", msg);
		exit(1);
	}
	return;
}

//---------------------------------------------------------------------------//

JNIEXPORT jobjectArray JNICALL Java_lejos_pc_comm_NXTCommBluez_search
  (JNIEnv *env, jobject obj, jstring jname)
{
	jstring str;
	inquiry_info *ii = NULL;
    int max_rsp, num_rsp;
    int dev_id, sock, len, flags;
    int i, j;
    char addr[19] = { 0 };
    char name[60] = { 0 };
    //unsigned char cod[3] = {0,0,0};
  	char msg[80];
  	char return_str[80] = {0};
  	int num_nxts = 0;
  	char* name_str;

  	if (jname != NULL) name_str = (char*) (*env)->GetStringUTFChars(env, jname, NULL);

  	jclass sclass = (*env)->FindClass(env,"java/lang/String");

    dev_id = hci_get_route(NULL);
    sock = hci_open_dev( dev_id );
    if (dev_id < 0 || sock < 0) {
        sprintf(msg, "Can't create socket: %s (%d)", strerror(errno), errno);
		throwNewBlueZException(env, msg);
		return NULL;
    }

    len  = 8;
    max_rsp = 255;
    flags = IREQ_CACHE_FLUSH;
    ii = (inquiry_info*)malloc(max_rsp * sizeof(inquiry_info));

    num_rsp = hci_inquiry(dev_id, len, max_rsp, NULL, &ii, flags);
    if( num_rsp < 0 ) {
    	sprintf(msg, "Inquiry failed: %s (%d)", strerror(errno), errno);
		throwNewBlueZException(env, msg);
		return NULL;
    }

    for (i = 0; i < num_rsp; i++) {
    	memset(name, 0, sizeof(name));
        if (hci_read_remote_name(sock, &(ii+i)->bdaddr, sizeof(name),
            name, 0) < 0) strcpy(name, "[unknown]");
    	if (jname != NULL && strcmp(name,name_str) != 0) continue;
        if ((ii+i)->dev_class[1] == 8 && (ii+i)->dev_class[0] == 4)
        	num_nxts++;
    }

    if (num_nxts == 0) return NULL;

    jobjectArray arr = (*env)->NewObjectArray(env, num_nxts, sclass, NULL);

    j=0;

    for (i = 0; i < num_rsp; i++) {
        ba2str(&(ii+i)->bdaddr, addr);
        memset(name, 0, sizeof(name));
        if (hci_read_remote_name(sock, &(ii+i)->bdaddr, sizeof(name),
            name, 0) < 0) strcpy(name, "[unknown]");
        if (jname != NULL && strcmp(name,name_str) != 0) continue;
        //printf("%s  %s %x %x\n", addr, name, (ii+i)->dev_class[1], (ii+i)->dev_class[0]);
        strcpy(return_str,name);
        strcat(return_str,"::");
        strcat(return_str,addr);
        if ((ii+i)->dev_class[1] == 8 && (ii+i)->dev_class[0] == 4) {
        	str = (*env)->NewStringUTF (env, return_str);
        	(*env)->SetObjectArrayElement(env, arr, j++, str);
        }
    }

    if (jname != NULL) (*env)->ReleaseStringUTFChars(env, jname, name_str);
    free( ii );
    close( sock );

	return arr;
}

//---------------------------------------------------------------------------//

bdaddr_t jstr2ba(JNIEnv *env, jstring bdaddr_jstr)
{
	char *bdaddr_str;
	bdaddr_t bdaddr;

	bdaddr_str = (char*) (*env)->GetStringUTFChars(env, bdaddr_jstr, NULL);
	str2ba(bdaddr_str, &bdaddr);
	(*env)->ReleaseStringUTFChars(env, bdaddr_jstr, bdaddr_str);

	return bdaddr;
}

//---------------------------------------------------------------------------//

JNIEXPORT jint JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketCreate
  (JNIEnv *env, jobject jobj)
{
  	jint sk;

  	char msg[80];

  	sk = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
	if (sk < 0) {
		sprintf(msg, "Can't create socket: %s (%d)", strerror(errno), errno);
		throwNewBlueZException(env, msg);
	}

	return sk;
}

//---------------------------------------------------------------------------//

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketBind

  (JNIEnv *env, jobject jobj, jint sk, jstring bdaddr_jstr)
{
	struct sockaddr_rc addr;

	char msg[80];
	int status;

	addr.rc_family = AF_BLUETOOTH;
	addr.rc_bdaddr = jstr2ba(env, bdaddr_jstr);
	addr.rc_channel = 0;

	status = bind(sk, (struct sockaddr *)&addr, sizeof(addr));
	if (status < 0) {
		sprintf(msg, "Can't bind socket: %s (%d)", strerror(errno), errno);
		throwNewBlueZException(env, msg);
	}
	return;
}

//---------------------------------------------------------------------------//

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketConnect
  (JNIEnv *env, jobject jobj, jint sk, jstring bdaddr_jstr, jint channel)
{
	struct sockaddr_rc addr;

	char msg[80];
	int status;

	addr.rc_family = AF_BLUETOOTH;
	addr.rc_bdaddr = jstr2ba(env, bdaddr_jstr);
	addr.rc_channel = (uint8_t) channel;

	status = connect(sk, (struct sockaddr *)&addr, sizeof(addr));
	if (status < 0) {
		sprintf(msg, "Can't connect: %s (%d)", strerror(errno), errno);
		throwNewBlueZException(env, msg);
	}
	return;
}

//---------------------------------------------------------------------------//

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketSend
  (JNIEnv *env, jobject jobj, jint sk, jbyteArray data)
{
  	ssize_t bytes_sent;

  	char msg[80];

  	jsize len = (*env)->GetArrayLength(env, data);
  	jbyte *elements = (*env)->GetByteArrayElements(env, data, 0);

    bytes_sent = send(sk, elements, len, 0);

    (*env)->ReleaseByteArrayElements(env, data, elements, 0);

	if (bytes_sent < 0) {
		sprintf(msg, "Send failed: %s (%d)", strerror(errno), errno);
		throwIOException(env, msg);
	}
	return;
}

//---------------------------------------------------------------------------//

JNIEXPORT jbyteArray JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketRecv
  (JNIEnv *env, jobject jobj, jint sk)
{
	jbyte response[66]; //LSB, MSB + telegram (maximum length is 64)
	ssize_t bytes_recved;

  	char msg[80];

	bytes_recved = recv(sk, response, sizeof(response), 0);

	if (bytes_recved < 0) {
		sprintf(msg, "Read failed: %s (%d)", strerror(errno), errno);
		throwIOException(env, msg);
		return NULL;
	}

	jsize len = bytes_recved;
	jarray newByteArray = (*env)->NewByteArray(env, len);
	(*env)->SetByteArrayRegion(env, newByteArray, 0, len, &response[0]);

	return(newByteArray);
}

//---------------------------------------------------------------------------//

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketShutdown
  (JNIEnv *env, jobject jobj, jint sk)
{
  	char msg[80];
  	int status;

	status = shutdown(sk, SHUT_RDWR);
	if (status < 0) {
		sprintf(msg, "Shutdown failed: %s (%d)", strerror(errno), errno);
		throwIOException(env, msg);
	}
	return;
}

//---------------------------------------------------------------------------//

JNIEXPORT void JNICALL Java_lejos_pc_comm_NXTCommBluez_rcSocketClose
  (JNIEnv *env, jobject jobj, jint sk)
{
  	char msg[80];
  	int status;

	status = close(sk);
	if (status < 0) {
		sprintf(msg, "Close failed: %s (%d)", strerror(errno), errno);
		throwIOException(env, msg);
	}
	return;
}
