/**
 * lejos on NXT test program.
 *
 * Copyright 2006 Lawrie Griffithsa <lawrie.griffiths@ntlworld.com>
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

#include "at91sam7s256.h"

#define STATUS_BYTE ((unsigned char *) 0x20F000)

int nxt_main(void)
{

  int i,j;

  *STATUS_BYTE = (unsigned char) 0;

  /* Enable output on light LED */

  *AT91C_PIOA_PER |= AT91C_PIO_PA29;

  *AT91C_PIOA_OER |= AT91C_PIO_PA29;

  for (i=0;i<20;i++) {

     /* Switch the LED off */

    for(j=0;j<200000;j++) *AT91C_PIOA_CODR |= AT91C_PIO_PA29;

     /* Switch the LED on */

    for(j=0;j<200000;j++) *AT91C_PIOA_SODR |= AT91C_PIO_PA29;

  }

  return 0;
}
