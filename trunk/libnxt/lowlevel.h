/**
 * NXT bootstrap interface; low-level USB functions.
 *
 * Copyright 2006 David Anderson <david.anderson@calixo.net>
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

#ifndef __LOWLEVEL_H__
#define __LOWLEVEL_H__

#include <usb.h>
#include "error.h"

struct nxt_t;
typedef struct nxt_t nxt_t;
#define MAX_WRITE 64
#define MAX_SERNO MAX_WRITE

nxt_error_t nxt_init(nxt_t **nxt);
nxt_error_t nxt_find(nxt_t *nxt);
long nxt_find_nth(int idx, char *address);
nxt_error_t nxt_open(nxt_t *nxt);
nxt_error_t nxt_close(nxt_t *nxt);
long nxt_open0(long nxt);
void nxt_close0(long nxt);
int nxt_in_reset_mode(nxt_t *nxt);
nxt_error_t nxt_send_buf(nxt_t *nxt, char *buf, int len);
nxt_error_t nxt_send_str(nxt_t *nxt, char *str);
nxt_error_t nxt_recv_buf(nxt_t *nxt, char *buf, int len);
int nxt_write_buf(long nxt, char *buf, int len);
int nxt_read_buf(long nxt, char *buf, int len);
int nxt_serial_no(long nxt, char *serno, int maxlen);

#endif /* __LOWLEVEL_H__ */
