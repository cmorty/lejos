
#include "mytypes.h"
#include "udp.h"
#include "interrupts.h"
#include "at91sam7s256.h"

#include "aic.h"
#include "systick.h"
#include "display.h"

#define EP_OUT	1
#define EP_IN	2

#define AT91C_UDP_CSR0  ((AT91_REG *)   0xFFFB0030) 
#define AT91C_UDP_CSR1  ((AT91_REG *)   0xFFFB0034) 
#define AT91C_UDP_CSR2  ((AT91_REG *)   0xFFFB0038) 
#define AT91C_UDP_CSR3  ((AT91_REG *)   0xFFFB003C)

#define AT91C_UDP_FDR0  ((AT91_REG *)   0xFFFB0050) 
#define AT91C_UDP_FDR1  ((AT91_REG *)   0xFFFB0054) 
#define AT91C_UDP_FDR2  ((AT91_REG *)   0xFFFB0058) 
#define AT91C_UDP_FDR3  ((AT91_REG *)   0xFFFB005C) 

static unsigned currentConfig;
static unsigned currentConnection;
static unsigned currentRxBank;
static unsigned usbTimeOut;

// Device descriptor
static const U8 dd[] = {
  0x12, 
  0x01,
  0x00,
  0x02,
  0x00,
  0x00,
  0x00, 
  0x08,
  0x94,
  0x06,
  0x02,
  0x00,
  0x00,
  0x00,
  0x00, 
  0x00, 
  0x01,
  0x01  
};

// Configuration descriptor
static const U8 cfd[] = {
  0x09,
  0x02,
  0x20,
  0x00, 
  0x01,
  0x01, 
  0x00,
  0xC0,
  0x00,
  0x09,
  0x04,
  0x00,
  0x00,
  0x02,
  0xFF, 
  0xFF,
  0xFF,
  0x00,
  0x07, 
  0x05,
  0x01,
  0x02,
  64,
  0x00,
  0x00, 
  0x07,
  0x05,
  0x82,
  0x02,
  64,
  0x00,
  0x00};

// Serial Number Descriptor
static U8 snd[] =
{
      0x1A,
      0x03, 
      0x31, 0x00,     // MSD of Lap (Lap[2,3]) in UNICode
      0x32, 0x00,     // Lap[4,5]
      0x33, 0x00,     // Lap[6,7]
      0x34, 0x00,     // Lap[8,9]
      0x35, 0x00,     // Lap[10,11]
      0x36, 0x00,     // Lap[12,13]
      0x37, 0x00,     // Lap[14,15]
      0x38, 0x00,     // LSD of Lap (Lap[16,17]) in UNICode
      0x30, 0x00,     // MSD of Nap (Nap[18,19]) in UNICode
      0x30, 0x00,     // LSD of Nap (Nap[20,21]) in UNICode
      0x39, 0x00,     // MSD of Uap in UNICode
      0x30, 0x00      // LSD of Uap in UNICode
};

static const U8 ld[] = {0x04,0x03,0x09,0x04}; // Language descriptor
      
extern void udp_isr_entry(void);

static char x4[5];
static char* hexchars = "0123456789abcdef";
  
static char *
hex4(int i)
{
  x4[0] = hexchars[(i >> 12) & 0xF];
  x4[1] = hexchars[(i >> 8) & 0xF];
  x4[2] = hexchars[(i >> 4) & 0xF];
  x4[3] = hexchars[i & 0xF];
  x4[4] = 0;
  return x4;
}
 
void
udp_isr_C(void)
{

}

int
udp_init(void)
{
  int i_state;

  /* Make sure the USB PLL and clock are set up */
  *AT91C_CKGR_PLLR |= AT91C_CKGR_USBDIV_1;
  *AT91C_PMC_SCER = AT91C_PMC_UDP;
  *AT91C_PMC_PCER = (1 << AT91C_ID_UDP);

  /* Enable the UDP pull up by outputting a zero on PA.16 */
  *AT91C_PIOA_PER = (1 << 16);
  *AT91C_PIOA_OER = (1 << 16);
  *AT91C_PIOA_CODR = (1 << 16);

  /* Set up default state */

  currentConfig = 0;
  currentConnection = 0;
  currentRxBank = 0;

  /*i_state = interrupts_get_and_disable();

  aic_mask_off(AT91C_PERIPHERAL_ID_UDP);
  aic_set_vector(AT91C_PERIPHERAL_ID_UDP, AIC_INT_LEVEL_NORMAL,
		 (U32) udp_isr_entry);
  aic_mask_on(AT91C_PERIPHERAL_ID_UDP);


  if (i_state)
    interrupts_enable(); */

  return 1; 
}

void
udp_close(U32 u)
{
  /* Nothing */
}

void
udp_disable()
{
  *AT91C_PIOA_PER = (1 << 16);
  *AT91C_PIOA_OER = (1 << 16);
  *AT91C_PIOA_SODR = (1 << 16);
}

void 
udp_reset()
{
  udp_disable();  
  systick_wait_ms(1);
  udp_init();
}

int
udp_short_timed_out()
{
  return (USB_TIMEOUT < 
     ((((*AT91C_PITC_PIIR) & AT91C_PITC_CPIV) 
         - usbTimeOut) & AT91C_PITC_CPIV));
}

static int timeout_counter = 0;

int
udp_timed_out()
{
   if(udp_short_timed_out())
   {
      timeout_counter++;
      udp_short_reset_timeout();
   }
   return (timeout_counter > 500);
}

void
udp_reset_timeout()
{
  timeout_counter = 0;
  udp_short_reset_timeout();  
}

void
udp_short_reset_timeout()
{
  usbTimeOut = ((*AT91C_PITC_PIIR) & AT91C_PITC_CPIV);  
}

int
udp_read(U8* buf, int len)
{
  int packetSize = 0, i;
  
  if ((*AT91C_UDP_CSR1) & currentRxBank) // data to read
  {
  	packetSize = (*AT91C_UDP_CSR1) >> 16;
  	if (packetSize > len) packetSize = len;
  	
  	for(i=0;i<packetSize;i++) buf[i] = *AT91C_UDP_FDR1;
  	
  	*AT91C_UDP_CSR1 &= ~(currentRxBank);	

    if (currentRxBank == AT91C_UDP_RX_DATA_BK0)	
      currentRxBank = AT91C_UDP_RX_DATA_BK1;
    else
      currentRxBank = AT91C_UDP_RX_DATA_BK0;
  }
  return packetSize;
}

void
udp_write(U8* buf, int len)
{
  int i;
  
  for(i=0;i<len;i++) *AT91C_UDP_FDR2 = buf[i];
  
  *AT91C_UDP_CSR2 |= AT91C_UDP_TXPKTRDY;
  
  udp_reset_timeout();
  
  while ( !((*AT91C_UDP_CSR2) & AT91C_UDP_TXCOMP) )	
     if ( !(udp_configured()) || udp_timed_out()) return;
            
 (*AT91C_UDP_CSR2) &= ~(AT91C_UDP_TXCOMP);

  while ((*AT91C_UDP_CSR2) & AT91C_UDP_TXCOMP);
}

void 
udp_send_null()
{
   (*AT91C_UDP_CSR0) |= AT91C_UDP_TXPKTRDY;

   udp_reset_timeout();

   while ( !((*AT91C_UDP_CSR0) & AT91C_UDP_TXCOMP) && !udp_timed_out());

   (*AT91C_UDP_CSR0) &= ~(AT91C_UDP_TXCOMP);
   while ((*AT91C_UDP_CSR0) & AT91C_UDP_TXCOMP);
}

void udp_send_stall()
{
  (*AT91C_UDP_CSR0) |= AT91C_UDP_FORCESTALL;                           
  while ( !((*AT91C_UDP_CSR0) & AT91C_UDP_ISOERROR) );                    

  (*AT91C_UDP_CSR0) &= ~(AT91C_UDP_FORCESTALL | AT91C_UDP_ISOERROR);
  while ((*AT91C_UDP_CSR0) & (AT91C_UDP_FORCESTALL | AT91C_UDP_ISOERROR));
}

void udp_send_control(U8* p, int len)
{
  int i = 0, j, tmp;
  
  do
  {
  	// send 8 bytes or less 

  	for(j=0;j<8 && i<len;j++)
  	{
  	  *AT91C_UDP_FDR0 = p[i++];
  	}

  	// Packet ready to send 
  	
  	(*AT91C_UDP_CSR0) |= AT91C_UDP_TXPKTRDY;
    udp_reset_timeout(); 	
    
  	do 
  	{
  	  tmp = (*AT91C_UDP_CSR0);

  	  if (tmp & AT91C_UDP_RX_DATA_BK0)
	  {

	    (*AT91C_UDP_CSR0) &= ~(AT91C_UDP_TXPKTRDY);

		(*AT91C_UDP_CSR0) &= ~(AT91C_UDP_RX_DATA_BK0);
        return;
	  }
  	}
  	while (!(tmp & AT91C_UDP_TXCOMP) && !udp_timed_out());
	
	(*AT91C_UDP_CSR0) &= ~(AT91C_UDP_TXCOMP);
    
  	while ((*AT91C_UDP_CSR0) & AT91C_UDP_TXCOMP);

  }
  while (i < len);
	
  udp_reset_timeout();

  while(!((*AT91C_UDP_CSR0) & AT91C_UDP_RX_DATA_BK0) && !udp_timed_out());

  (*AT91C_UDP_CSR0) &= ~(AT91C_UDP_RX_DATA_BK0);

}

static int configured = 0;

int
udp_configured()
{
  return configured;
}

void
udp_set_configured(int conf)
{
  configured = conf;
}

static int reqno = 0;

void 
udp_enumerate()
{
  U8 bt, br;
  int req, len, ind, val; 
  
  while(!((*AT91C_UDP_CSR0) & AT91C_UDP_RXSETUP)); // Wait for setup
  
  bt = *AT91C_UDP_FDR0;
  br = *AT91C_UDP_FDR0;
  
  val = ((*AT91C_UDP_FDR0 & 0xFF) | (*AT91C_UDP_FDR0 << 8));
  ind = ((*AT91C_UDP_FDR0 & 0xFF) | (*AT91C_UDP_FDR0 << 8));
  len = ((*AT91C_UDP_FDR0 & 0xFF) | (*AT91C_UDP_FDR0 << 8));
  
  if (bt & 0x80)
  {
    *AT91C_UDP_CSR0 |= AT91C_UDP_DIR; 
    while ( !((*AT91C_UDP_CSR0) & AT91C_UDP_DIR) );
  }
  
  *AT91C_UDP_CSR0 &= ~AT91C_UDP_RXSETUP;
  while ( ((*AT91C_UDP_CSR0)  & AT91C_UDP_RXSETUP)  );

  req = br << 8 | bt;
    
  switch(req)
  {
    case STD_GET_DESCRIPTOR: 
      if (val == 0x100) // Get device descriptor
      {
        udp_send_control(dd, sizeof(dd));
      }
      else if (val == 0x200) // Configuration descriptor
      {  
        systick_wait_ms(100);     
        udp_send_control(cfd, (len < sizeof(cfd) ? len : sizeof(cfd)));
        if (len > sizeof(cfd)) udp_send_null();
      }	
      else if ((val & 0xF00) == 0x300)
      {
        switch(val & 0xFF)
        {
          case 0x00:
	        udp_send_control(ld, sizeof(ld));
            break;
          case 0x01:
		    udp_send_control(snd, sizeof(snd));
            break;
          default:
			udp_send_stall();
        }
      }  
      else
      {
        udp_send_stall();
      }
      break;
        
    case STD_SET_ADDRESS:
      
      (*AT91C_UDP_CSR0) |= AT91C_UDP_TXPKTRDY;

      udp_reset_timeout();

      while(((*AT91C_UDP_CSR0) & AT91C_UDP_TXPKTRDY) && !udp_timed_out());
        
      *AT91C_UDP_FADDR = (AT91C_UDP_FEN | val);            
                                                                   
      *AT91C_UDP_GLBSTATE  = (val) ? AT91C_UDP_FADDEN : 0;
      break;
        
    case STD_SET_CONFIGURATION:

      configured = 1;
      udp_send_null();                            // Signal request processed OK
      *AT91C_UDP_GLBSTATE  = (val) ? AT91C_UDP_CONFG : AT91C_UDP_FADDEN;           // If wanted configuration != 0

      *AT91C_UDP_CSR1 = (val) ? (AT91C_UDP_EPEDS | AT91C_UDP_EPTYPE_BULK_OUT) : 0; // Endpoint 1 enabled and set as BULK OUT
      *AT91C_UDP_CSR2 = (val) ? (AT91C_UDP_EPEDS | AT91C_UDP_EPTYPE_BULK_IN)  : 0; // Endpoint 2 enabled and set as BULK IN
      *AT91C_UDP_CSR3 = (val) ? (AT91C_UDP_EPTYPE_INT_IN)   : 0;                   // Endpoint 3 disabled and set as INTERRUPT IN

      currentRxBank = AT91C_UDP_RX_DATA_BK0;
        
      break;
        
    case STD_CLEAR_FEATURE_INTERFACE:
      udp_send_null();
      break;
    case STD_CLEAR_FEATURE_ZERO:
    default:
      udp_send_stall();
  } 
}
   
void udp_wait_for_connection()
{
  int connected = 0;
    
  udp_set_configured(0);
  
  while (!udp_configured())
  {
    udp_enumerate();
  }
   
  while (!connected)
  {
    U8 buf[2];
 	
    int i = udp_read(buf,2);
  	
    if (i != 0) 
    {
	  U8 reply[3];
	  reply[0] = '\n';
	  reply[1] = '\r';
	  udp_write(reply,2);
	  connected = 1;
    } 	    
  }
}





