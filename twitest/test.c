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
#include "twi.h"

#define STATUS_BYTE ((unsigned char *) 0x20F000)

int nxt_main(void)
{

  int i, j;
  unsigned char c;
  char startup[] = "\xCC""Let's samba nxt arm in arm, (c)LEGO System A/S";
  
  
  /* Enable output on light LED */
  
  *AT91C_PIOA_PER |= AT91C_PIO_PA29;

  *AT91C_PIOA_OER |= AT91C_PIO_PA29;
  
  *((unsigned char *) 0x20F000) = 0;

  twi_init();
  
  // Write startup message
  
  //for(i=0;i<47;i++) twi_write(1,0,(unsigned char) startup[i]);
 
  // Write IOTOAVR struct
  
  /*
  twi_write(1,0,0);
    twi_write(1,0,8);
    twi_write(1,0,100);
    twi_write(1,0,100);
    twi_write(1,0,100);
    twi_write(1,0,1);
    twi_write(1,0,0);
 
 

  for(i=0;i<10;i++) { 
    twi_write(1,0,0);
    twi_write(1,0,8);
    twi_write(1,0,100);
    twi_write(1,0,100);
    twi_write(1,0,100);
    twi_write(1,0,1);
    twi_write(1,0,0);
    
    for(j=0;j<10;j++) *AT91C_PIOA_SODR |= AT91C_PIO_PA29;
    
    for(j=0;j<10;j++) *AT91C_PIOA_CODR |= AT91C_PIO_PA29;

  }
  

  
  */
  
  for(i=0;i<128;i++) {
  	if (i == 1) {
  		for(j=0;j<100;j++) {
  			c =twi_read(i,j);
  			if (c != 0) {
  				*((char *) 0x20F0000) = c;
  				break;
  			}
  		}
  	}
  }
  
 
  // Switch off the light LED
  
  *AT91C_PIOA_CODR |= AT91C_PIO_PA29;
  
  
  return 0;
}
