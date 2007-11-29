/**
 * NXT bootstrap interface; NXT firmware handling code.
 *
 * Copyright 2006 David Anderson <david.anderson@calixo.net>
 * Modified 2007 by Lawrie Griffiths <lawrie.griffiths@ntlworld.com> 
 * to support lejos NXJ firmware flashing.
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
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>

#include "error.h"
#include "lowlevel.h"
#include "samba.h"
#include "flash.h"
#include "firmware.h"
#include "flash_routine.h"

static nxt_error_t
nxt_flash_prepare(nxt_t *nxt, int unlock)
{
  // Put the clock in PLL/2 mode
  NXT_ERR(nxt_write_word(nxt, 0xFFFFFC30, 0x7));

  // Unlock the flash chip
  if (unlock) NXT_ERR(nxt_flash_unlock_all_regions(nxt));

  // Send the flash writing routine
  NXT_ERR(nxt_send_file(nxt, 0x202000, flash_bin, flash_len));

  return NXT_OK;
}


static nxt_error_t
nxt_flash_block(nxt_t *nxt, nxt_word_t block_num, char *buf)
{
  // Set the target block number
  NXT_ERR(nxt_write_word(nxt, 0x202300, block_num));

  // Send the block to flash
  NXT_ERR(nxt_send_file(nxt, 0x202100, buf, 256));

  // Jump into the flash writing routine
  NXT_ERR(nxt_jump(nxt, 0x202000));

  return NXT_OK;
}


static nxt_error_t
nxt_flash_finish(nxt_t *nxt)
{
  return nxt_flash_wait_ready(nxt);
}


static nxt_error_t
nxt_firmware_validate_fd(int fd, int max_size)
{
  struct stat s;

  if (fstat(fd, &s) < 0)
    return NXT_FILE_ERROR;

  if (s.st_size > max_size)
    return NXT_INVALID_FIRMWARE;

  return NXT_OK;
}


nxt_error_t
nxt_firmware_validate(char *fw_path, int max_pages)
{
  nxt_error_t err;
  int fd;

  fd = open(fw_path, O_RDONLY);
  if (fd < 0)
    return NXT_FILE_ERROR;

  err = nxt_firmware_validate_fd(fd, max_pages * 256);
  close(fd);

  return err;
}


nxt_error_t
nxt_firmware_flash(nxt_t *nxt, char *fw_path, 
                   int start_page, int max_pages, int unlock, int write_len)
{
  int fd, i, err, len = 0;
  char buf[256];

  fd = open(fw_path, O_RDONLY);
  if (fd < 0)
    return NXT_FILE_ERROR;

  err = nxt_firmware_validate_fd(fd, max_pages * 1024);
  if (err != NXT_OK)
    {
      close(fd);
      return NXT_INVALID_FIRMWARE;
    }

  NXT_ERR(nxt_flash_prepare(nxt, unlock));

  for (i = start_page; i < start_page + max_pages; i++) 
    {

      int ret;

      memset(buf, 0, 256);
      ret = read(fd, buf, 256);

      if (ret != -1) 
        {
          NXT_ERR(nxt_flash_block(nxt, i, buf));
          len += ret;
        }

      if (ret < 256)
        {
          close(fd);
        
          if (ret != -1 && write_len)
            {
              ((unsigned *) buf)[63] = len;
              NXT_ERR(nxt_flash_block(nxt,start_page + max_pages -1, buf));
            }

          NXT_ERR(nxt_flash_finish(nxt));
          
          return ret == -1 ? NXT_FILE_ERROR : NXT_OK;
        }
    }

  close(fd);
  
  if (write_len) 
    { 
      ((unsigned *) buf)[63] = len;
      NXT_ERR(nxt_flash_block(nxt,start_page + max_pages -1, buf));
    }
  
  NXT_ERR(nxt_flash_finish(nxt));

  return NXT_OK;
}

