/**
 * NXT bootstrap interface; NXT firmware handling code.
 *
 * Copyright 2006 David Anderson <david.anderson@calixo.net>
 * Modified 2008 by Lawrie Griffiths <lawrie.griffiths@ntlworld.com> 

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

#ifndef __FIRMWARE_H__
#define __FIRMWARE_H__

#include "error.h"
#include "lowlevel.h"
#include "flash.h"

nxt_error_t nxt_firmware_flash(nxt_t *nxt, char *fw_path, int start_page, int num_pages, int unlock, int write_addr_len);
nxt_error_t nxt_firmware_validate(char *fw_path, int max_size, int *file_size);

#endif /* __FIRMWARE_H__ */
