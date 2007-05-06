/**
 * Main program code for the runc utility.
 *
 * Copyright 2006 Lawrie Griffiths <lawrie.griffiths@ntlworld.com>
 * Based on fwflash by David Anderson <david.anderson@calixo.net>
 * 
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
#include <stdlib.h>
#include <string.h>

#include "error.h"
#include "lowlevel.h"
#include "samba.h"
#include "firmware.h"

#define NXT_HANDLE_ERR(expr, nxt, msg)     \
  do {                                     \
    nxt_error_t nxt__err_temp = (expr);    \
    if (nxt__err_temp)                     \
      return handle_error(nxt, msg, nxt__err_temp);  \
  } while(0)

static int handle_error(nxt_t *nxt, char *msg, nxt_error_t err)
{
  printf("%s: %s\n", msg, nxt_str_error(err));
  if (nxt != NULL)
    nxt_close(nxt);
  exit(err);
}

int main(int argc, char *argv[])
{
  nxt_t *nxt;
  nxt_error_t err;
  char *fw_file;
  FILE *f;
  int ret;
  char *buf;
  long lsize;
  char open_write_cmd[26];
  int i;
  char reply[32];
  char write_cmd[3];
  char close_cmd[3];
  char start_cmd[22];
  char msg_cmd[5];
  int start_base = 0;
  int farg = 0;
  int run = 0;

  if (argc < 2 || argc > 3)
    {
      printf("Syntax: %s [-r] <lejos NXJ binary file>\n"
             "\n"
             "Example: %s -r Test.bin\n", argv[0], argv[0]);
      exit(1);
    }
    
  for(i=1;i<argc;i++) 
    {
  	  if (argv[i][0] == '-') 
  	    {
  	      if (argv[i][1] == 'r')
  	        run = 1;	        
  	      else 
  	        {
  	  	      printf("%s: Invalid flags %s\n",argv[0],argv[i]);
              exit(1);
  	        }
  	     }
  	  else 
  	    {
  	      fw_file = argv[i];
  	      farg = i;
  	    }  
    }
  
  if (farg == 0)     
    {
      printf("Syntax: %s [-r] <lejos NXJ binary file>\n"
             "\n"
             "Example: %s -r Test.bin\n", argv[0], argv[0]);
      exit(1);
    }

  NXT_HANDLE_ERR(nxt_init(&nxt), NULL,
                 "Error during library initialization");

  err = nxt_find(nxt);
  if (err)
    {
      if (err == NXT_NOT_PRESENT)
        printf("NXT not found. Is it properly plugged in via USB?\n");
      else
        NXT_HANDLE_ERR(0, NULL, "Error while scanning for NXT");
      exit(1);
    }

  f = fopen(fw_file, "rb");
  if (f == NULL) NXT_HANDLE_ERR(8, NULL, "Error opening file");

  fseek (f, 0, SEEK_END);
  lsize=ftell (f);

  rewind(f);

  buf = (char *) malloc(lsize);
  if (buf == NULL) NXT_HANDLE_ERR(8, NULL, "Error allocating memory");

  ret = fread(buf, 1, lsize, f);

  if (ret != (int) lsize) NXT_HANDLE_ERR(8, NULL, "Error reading file");

  printf("File size is %ld\n", lsize);

  fclose(f);

  if (nxt_in_reset_mode(nxt))
    {
      printf("NXT found, but running in reset mode.\n");
      printf("Please run lejos NXJ\n");
      exit(2);
    }

  NXT_HANDLE_ERR(nxt_open0(nxt), NULL, "Error while connecting to NXT");

  printf("NXT device located and opened.\n"
         "Uploading file now...\n");
         
  // Find the base filename
  
  for(i=strlen(fw_file)-1;i>=0 && fw_file[i] != '\\' && fw_file[i] != '/';i--) start_base = i;
  
  // Send the open write command
  
  for(i=0;i<26;i++) open_write_cmd[i] = 0;
  
  open_write_cmd[0] = 0x01;
  open_write_cmd[1] = 0x81;
  strcpy(&open_write_cmd[2], &fw_file[start_base]);
  open_write_cmd[22] = lsize & 0xFF;
  open_write_cmd[23] = (lsize >> 8) & 0xFF;
  open_write_cmd[24] = (lsize >> 16) & 0xFF;
  open_write_cmd[25] = (lsize >> 24) & 0xFF;
  
  NXT_HANDLE_ERR(nxt_send_buf(nxt, open_write_cmd, 26), nxt,
                  "Error sending OPEN WRITE command");

  NXT_HANDLE_ERR(nxt_recv_buf(nxt, reply  , 4), nxt,
                  "Error receiving reply from OPEN WRITE");
    
  write_cmd[0] = 0;
  write_cmd[1] = 0x83;
  write_cmd[2] = 0;
  
  NXT_HANDLE_ERR(nxt_send_buf(nxt, write_cmd, 3), nxt,
                  "Error sending WRITE command");
            
  NXT_HANDLE_ERR(nxt_send_buf(nxt, buf, (int) lsize), nxt, 
                  "Error Sending file");

  NXT_HANDLE_ERR(nxt_recv_buf(nxt, reply  , 6), nxt,
                  "Error receiving reply from WRITE");

  close_cmd[0] = 0;
  close_cmd[1] = 0x84;
  close_cmd[2] = 0;
  
  NXT_HANDLE_ERR(nxt_send_buf(nxt, close_cmd, 3), nxt,
                  "Error sending CLOSE command");

  NXT_HANDLE_ERR(nxt_recv_buf(nxt, reply  , 4), nxt,
                  "Error receiving reply from CLOSE");

  if (run) {
  	start_cmd[0] = 0x80;
  	start_cmd[1] = 0;
  	strcpy(&start_cmd[2],&fw_file[start_base]);
  	NXT_HANDLE_ERR(nxt_send_buf(nxt, start_cmd, 22), nxt,
                  "Error sending START command");
  } else {
  	msg_cmd[0] = 0x80;
  	msg_cmd[1] = 0x09;
  	msg_cmd[2] = 0;
  	msg_cmd[3] = 1;
  	msg_cmd[4] = 0; 
  	NXT_HANDLE_ERR(nxt_send_buf(nxt, msg_cmd, 5), nxt,
                  "Error sending START command");
  }
  
  NXT_HANDLE_ERR(nxt_close0(nxt), NULL,
                 "Error while closing connection to NXT");
  return 0;
}
