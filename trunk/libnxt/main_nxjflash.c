/**
 * Main program code for the nxjflash utility.
 *
 * Copyright 2006 David Anderson <david.anderson@calixo.net>
 * Modified 2007 by Lawrie Griffiths <lawrie.griffiths@ntlworld.com>
 * to flash nxj firmware and Java menu
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

#define MAX_VM_PAGES 128
#define MAX_MENU_PAGES 64

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
  char *fw_file, *menu_file;
  unsigned fmcn = 50;

  if (argc < 3 || argc > 4)
    {
      printf("Syntax: %s <VM binary> <java menu binary> [fmcn]\n"
             "\n"
             "Example: %s lejos_nxt_rom.bin Menu.bin\n", argv[0], argv[0]);
      exit(1);
    }

  fw_file = argv[1];

  printf("Checking VM... ");
  NXT_HANDLE_ERR(nxt_firmware_validate(fw_file, MAX_VM_PAGES * 256), NULL,
                 "Error in VM file");
  printf("VM OK.\n");
  
  menu_file = argv[2];
  
  if (argc == 4) {
  	fmcn = atoi(argv[3]);
  }
  
  printf("Setting fmcn to %d\n", fmcn);
  
  printf("Checking Menu... ");
  NXT_HANDLE_ERR(nxt_firmware_validate(menu_file, (MAX_MENU_PAGES * 256) - 4), NULL,
                 "Error in Menu file");
  printf("Menu OK.\n");

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

  if (!nxt_in_reset_mode(nxt))
    {
      printf("NXT found, but not running in reset mode.\n");
      printf("Please reset your NXT manually and restart this program.\n");
      exit(2);
    }

  NXT_HANDLE_ERR(nxt_open(nxt), NULL, "Error while connecting to NXT");

  printf("NXT device in reset mode located and opened.\n"
         "Starting VM flash procedure now...\n");

  NXT_HANDLE_ERR(nxt_firmware_flash(nxt, fw_file, 0, MAX_VM_PAGES, 1, 0), nxt,
                 "Error flashing VM");
  printf("VM flash complete.\n");

  printf("Starting menu flash procedure now...\n");
  
  NXT_HANDLE_ERR(nxt_firmware_flash(nxt, menu_file, MAX_VM_PAGES, MAX_MENU_PAGES, 0, fmcn), nxt,
                 "Error flashing menu");
  printf("Menu flash complete.\n");
  
  NXT_HANDLE_ERR(nxt_jump(nxt, 0x00100000), nxt,
                 "Error booting new firmware");
  printf("New firmware started!\n");

  NXT_HANDLE_ERR(nxt_close(nxt), NULL,
                 "Error while closing connection to NXT");
  return 0;
}
