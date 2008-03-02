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
#include <string.h>

#include "error.h"
#include "lowlevel.h"
#include "samba.h"
#include "firmware.h"

#define MAX_FIRMWARE_PAGES 320
#define MAX_FILE_SIZE 256

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
  char *nxj_home;
  int vm_file_size, menu_file_size;
  int num_vm_pages, num_menu_pages;

  if (argc == 1)
    {
      nxj_home = getenv("NXJ_HOME");
      
      if (nxj_home == NULL || strlen(nxj_home) == 0)
        {
          printf("NXJ_HOME is not defined\n");
          exit(1);
        }
      printf("NXJ_HOME is %s\n", nxj_home);
      
      fw_file = (char *) calloc(1,MAX_FILE_SIZE);
      strcpy(fw_file, nxj_home);
      strcat(fw_file,"/bin/lejos_nxt_rom.bin");
      menu_file = calloc(1,MAX_FILE_SIZE);
      strcpy(menu_file,nxj_home);
      strcat(menu_file,"/bin/StartUpText.bin");
    }
  else if (argc != 3)
    {
      printf("Syntax: %s [<VM binary> <java menu binary>]\n"
             "\n"
             "Example: %s lejos_nxt_rom.bin StartUpText.bin\n", argv[0], argv[0]);
      exit(1);
    }
  else
    {
      fw_file = argv[1];     
      menu_file = argv[2];
    }
  
  printf("Checking VM %s ... ", fw_file);
  NXT_HANDLE_ERR(nxt_firmware_validate(fw_file, MAX_FIRMWARE_PAGES * FLASH_PAGE_SIZE - 8, &vm_file_size ), NULL,
                 "Error in VM file");
  printf("VM OK.\n");
  num_vm_pages = (vm_file_size + FLASH_PAGE_SIZE - 1) / FLASH_PAGE_SIZE;
 
  printf("Checking Menu %s ... ", menu_file);
  NXT_HANDLE_ERR(nxt_firmware_validate(menu_file, MAX_FIRMWARE_PAGES * FLASH_PAGE_SIZE - 8, &menu_file_size), NULL,
                 "Error in Menu file");
  printf("Menu OK.\n");
  num_menu_pages = (menu_file_size + FLASH_PAGE_SIZE - 1)/FLASH_PAGE_SIZE;
  
  if (((num_vm_pages * FLASH_PAGE_SIZE) + menu_file_size) >
      ((MAX_FIRMWARE_PAGES * FLASH_PAGE_SIZE) - 8))
    {
      printf("Combined size of VM and menu  > %d\n", MAX_FIRMWARE_PAGES*FLASH_PAGE_SIZE - 8);
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

  if (!nxt_in_reset_mode(nxt))
    {
      printf("NXT found, but not running in reset mode.\n");
      printf("Please reset your NXT manually and restart this program.\n");
      exit(2);
    }

  NXT_HANDLE_ERR(nxt_open(nxt), NULL, "Error while connecting to NXT");

  printf("NXT device in reset mode located and opened.\n"
         "Starting VM flash procedure now...\n");

  NXT_HANDLE_ERR(nxt_firmware_flash(nxt, fw_file, 0, MAX_FIRMWARE_PAGES, 1, 0), nxt,
                 "Error flashing VM");
  printf("VM flash complete.\n");

  printf("Starting menu flash procedure now...\n");
  
  NXT_HANDLE_ERR(nxt_firmware_flash(nxt, menu_file, num_vm_pages, MAX_FIRMWARE_PAGES - num_vm_pages, 0, 1), nxt,
                 "Error flashing menu");
  printf("Menu flash complete.\n");
  
  NXT_HANDLE_ERR(nxt_jump(nxt, 0x00100000), nxt,
                 "Error booting new firmware");
  printf("New firmware started!\n");
  printf("If battery level reads 0.0, remove and re-insert a battery\n");

  NXT_HANDLE_ERR(nxt_close(nxt), NULL,
                 "Error while closing connection to NXT");
  return 0;
}
