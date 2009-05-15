/**
 * NXT bootstrap interface; NXT onboard flashing driver.
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

typedef volatile unsigned int vint_t;

#define VINTPTR(addr) ((vint_t *)(addr))
#define VINT(addr) (*(VINTPTR(addr)))

#define USER_PAGE VINTPTR(0x00202100)
#define USER_PAGE_NUM VINT(0x00202300)

#define FLASH_BASE VINTPTR(0x00100000)
#define FLASH_CMD_REG VINT(0xFFFFFF64)
#define FLASH_STATUS_REG VINT(0xFFFFFF68)
#define OFFSET_PAGE_NUM ((USER_PAGE_NUM & 0x000003FF) << 8)
#define FLASH_CMD_WRITE (0x5A000001 + OFFSET_PAGE_NUM)

void nxt_main(void)
{
	while (!(FLASH_STATUS_REG & 0x1));

	//copies a chunk of 256bytes
	vint_t *base = FLASH_BASE + USER_PAGE_NUM * 64;
	for (int i = 0; i < 64; i++)
		base[i] = USER_PAGE[i];

	FLASH_CMD_REG = FLASH_CMD_WRITE;

	while (!(FLASH_STATUS_REG & 0x1));
}
