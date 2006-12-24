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

  if (argc != 2)
    {
      printf("Syntax: %s <C program image to write>\n"
             "\n"
             "Example: %s flashled.bin\n", argv[0], argv[0]);
      exit(1);
    }

  fw_file = argv[1];

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

  if (!nxt_in_reset_mode(nxt))
    {
      printf("NXT found, but not running in reset mode.\n");
      printf("Please reset your NXT manually and restart this program.\n");
      exit(2);
    }

  NXT_HANDLE_ERR(nxt_open(nxt), NULL, "Error while connecting to NXT");

  printf("NXT device in reset mode located and opened.\n"
         "Starting C program now...\n");

  // Send the C program
  NXT_HANDLE_ERR(nxt_send_file(nxt, 0x202000, buf, (int) lsize), nxt, 
                  "Error Sending file");

  NXT_HANDLE_ERR(nxt_jump(nxt, 0x202000), nxt,
                 "Error jumping to C program");

  NXT_HANDLE_ERR(nxt_close(nxt), NULL,
                 "Error while closing connection to NXT");
  return 0;
}
