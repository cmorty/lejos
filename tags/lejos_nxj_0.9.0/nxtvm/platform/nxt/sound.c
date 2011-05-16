/* leJOS Sound generation
 * The module provides support for audio output. Two modes are supported, tone
 * generation and the playback of PCM based audio samples. Both use pulse 
 * density modulation to actually produce the output.
 * To produce a tone a single pdm encoded cycle is created (having the 
 * requested amplitude), this single cycle is then played repeatedly to
 * generate the tone. The bit rate used to output the sample defines the
 * frequency of the tone and the number of repeats represents then length.
 * To play an encoded sample (only 8 bit PCM is currently supported), each PCM
 * sample is turned into a 256 bit pdm block, which is then output (at the
 * sample rate), to create the output. Again the amplitude of the samples may
 * be controlled. 
 * The actual output of the bits is performed using the built in Synchronous
 * Serial Controller (SSC). This is capable of outputing a series of bits to
 * port at fixed intervals and is used to output the pdm audio.
 */
#include "mytypes.h"
#include "sound.h"
#include "AT91SAM7.h"
#include "aic.h"
#include "nxt_avr.h"
#include <string.h>
#include "display.h"
#include "systick.h"
#include "memory.h"
#include "rconsole.h"

// We have two possible types of PDM encoding for use when playing PCM
// data. The first is based on the Lego firmware and encodes each 8 bit
// value to a 256bit PDM value by using a lookup table. The second uses
// a second order Sigma-Delta encoder to create a 32bit encoding. The
// Sigma-Delta encoder uses less memory and produces a slightly better
// sounding result, but requires approximately 15% of the cpu when 
// playing 8MHz samples. The lookup method requires more memory but only
// uses approx 4% of the cpu when playing the same clip. 
// The sigma-delta method generates fewer interrupts (due to the smaller
// sample size), and because of this has more accurate timing (because the
// clock divisor is larger).
#define PDM_LOOKUP 1
#define PDM_SIGMA_DELTA 2
#define PDM_ENCODE PDM_SIGMA_DELTA
/* Buffer length must be a multiple of 8 and at most 64 (preferably as long as possible) */
#define PDM_BUFFER_LENGTH 64
/* Main clock frequency */
#define OSC CLOCK_FREQUENCY
/* Size of a sample block */
#if (PDM_ENCODE == PDM_LOOKUP)
#define SAMPBITS 256
#else
#define SAMPBITS 32
#endif
#define SAMPBYTES (SAMPBITS/8)
#define SAMPPERBUF ((PDM_BUFFER_LENGTH*32)/SAMPBITS)
#define SAMPLEBUFSZ 256
#define MAXRATE 22050
#define MINRATE 2000
#define DEFRATE 8000

extern void sound_isr_entry(void);

enum {
  SOUND_MODE_NONE,
  SOUND_MODE_TONE,
  SOUND_MODE_PCM
};

U8 sound_mode = SOUND_MODE_NONE;

struct {
  // pointer to the sample data
  U8 *ptr;
  // The number of hardware samples ahead
  S32 count;
  // Double buffer
  U32 buf[2][PDM_BUFFER_LENGTH];
  // Amplification LUT
  S8 amp[257];
  // Ring buffer
  U8 *sample_buf;
  // Volume val used to create the amp LUT
  S32 cur_vol;
  // Clock divisor to use to play the sample
  U32 clock_div;
  // Current sample index
  U32 out_index;
  // Place to add new samples
  U32 in_index;
  // 0 or 1, identifies the current buffer
  U8 buf_id;
  // Size of the sample in 32 bit words
  U8 len;
} sample;

#if (PDM_ENCODE == PDM_LOOKUP)
// Lookup table for PDM encoding. Contains 0-32 evenly spaced set bits.
const U32 sample_pattern[33] =
  {
    0x00000000, 0x80000000, 0x80008000, 0x80200400,
    0x80808080, 0x82081040, 0x84208420, 0x88442210,
    0x88888888, 0x91224488, 0x92489248, 0xa4924924,
    0xa4a4a4a4, 0xa94a5294, 0xaa54aa54, 0xaaaa5554,
    0xaaaaaaaa, 0xd555aaaa, 0xd5aad5aa, 0xd6b5ad6a,
    0xdadadada, 0xdb6db6da, 0xedb6edb6, 0xeeddbb76,
    0xeeeeeeee, 0xf7bbddee, 0xfbdefbde, 0xfdf7efbe,
    0xfefefefe, 0xffdffbfe, 0xfffefffe, 0xfffffffe,
    0xffffffff
  };
#endif

/* The following tables provide input to the wave generation code. This
 * code takes a set of points describing one half cycle of a symmetric waveform
 * and from this creates a pdm encoded version of a single full cycle of the
 * wave. 
 *
 * A number of sample wave shapes have been tried, an accurates sine wave, a
 * square wave, triangular wave and a rough approximation of a sine wave.
 * Currently the rough sine wave is used, the sqaure wave also works well.
 * The purer shapes do not seem to work very well at frequencies below
 * about 800Hz. It seems that a combination of the sounder and the Lego
 * amplifier electronics mean that the response below this is very poor.
 * However by using a waveform like a square wave that has a lot of harminics
 * the ear can be fooled into hearing the lower frequencies. Using a pure wave
 * form like the sine wave results in a very low volume. This rather surprising
 * result has to some extent been validated using an audio spectrum analyzer
 * which shows vertually no fundamental below 700Hz but lots of harmonics.
 *
 * The square wave also produces a higher volume than other wave shapes. 
 * Higher volumes can be achieved when using the rough sine wave by allowing
 * the volume setting to push the shape into distortion and effectively
 * becomming a square wave!
 */
//const byte sine[] =  {0xa8, 0xc0, 0xca, 0xd4, 0xe0, 0xe9, 0xff, 0xff, 0xff, 0xff, 0xe9, 0xe0, 0xd4, 0xca, 0xc0, 0xa8};
//const byte sine[] =  {0xb0, 0xc0, 0xca, 0xd4, 0xe0, 0xe9, 0xf2, 0xff, 0xff, 0xf2, 0xe9, 0xe0, 0xd4, 0xca, 0xc0, 0xb0};
//const byte sine[] =  {0xc0, 0xc8, 0xd0, 0xd8, 0xe0, 0xea, 0xf4, 0xff, 0xff, 0xf4, 0xea, 0xe0, 0xd8, 0xd0, 0xc8, 0xc0};
const byte sine[] =  {0xc0, 0xc8, 0xd0, 0xd8, 0xe0, 0xea, 0xf4, 0xff, 0xff, 0xf0, 0xe5, 0xdc, 0xd4, 0xcc, 0xc4, 0xbc};
//const byte sine[] =  {0x98, 0xb0, 0xc7, 0xda, 0xea, 0xf6, 0xfd, 0xff, 0xff, 0xfd, 0xf6, 0xea, 0xda, 0xc7, 0xb0, 0x98};
// Time required to generate the tone and volume lookup table...
#define TONE_OVERHEAD 1

// We use a volume law that approximates a log function. The values in the
// table below have been hand tuned to try and provide a smooth change in
// the loudness of both tones and samples.
const byte logvol[] = {0, 8, 24, 40, 56, 80, 104, 128, 162, 196, 255, 255};


static void sound_interrupt_enable(U32 typ)
{
  // Enable interrupt notification of either the end of the next buffer
  // or the end of all output. Having both enabled does not seem to work
  // the end notifaction seems to get lost.
  *AT91C_SSC_IDR = AT91C_SSC_TXBUFE | AT91C_SSC_ENDTX | AT91C_SSC_TXEMPTY;
  *AT91C_SSC_IER = typ;
}

static void sound_interrupt_disable()
{
  *AT91C_SSC_IDR = AT91C_SSC_TXBUFE | AT91C_SSC_ENDTX | AT91C_SSC_TXEMPTY;
}

static void sound_enable()
{
  *AT91C_PIOA_PDR = AT91C_PA17_TD;
}

static void sound_disable()
{
  sound_mode = SOUND_MODE_NONE;
  *AT91C_PIOA_PER = AT91C_PA17_TD;
}


void sound_init()
{
  // Initialise the hardware. We make use of the SSC module.
  sound_interrupt_disable();
  sound_disable();
  
  *AT91C_PMC_PCER = (1 << AT91C_ID_SSC);

  *AT91C_PIOA_ODR = AT91C_PA17_TD;
  *AT91C_PIOA_OWDR = AT91C_PA17_TD;
  *AT91C_PIOA_MDDR = AT91C_PA17_TD;
  *AT91C_PIOA_PPUDR = AT91C_PA17_TD;
  *AT91C_PIOA_IFDR = AT91C_PA17_TD;
  *AT91C_PIOA_CODR = AT91C_PA17_TD;
  *AT91C_PIOA_IDR = AT91C_PA17_TD;
  
  *AT91C_SSC_CR = AT91C_SSC_SWRST;
  *AT91C_SSC_TCMR = AT91C_SSC_CKS_DIV + AT91C_SSC_CKO_CONTINOUS + AT91C_SSC_START_CONTINOUS;
  *AT91C_SSC_TFMR = 31 + (7 << 8) + AT91C_SSC_MSBF; // 8 32-bit words
  *AT91C_SSC_CR = AT91C_SSC_TXEN;                                        

  aic_mask_on(AT91C_ID_SSC);
  aic_clear(AT91C_ID_SSC);
  aic_set_vector(AT91C_ID_SSC, AT91C_AIC_PRIOR_LOWEST | AT91C_AIC_SRCTYPE_INT_EDGE_TRIGGERED,
		 (U32)sound_isr_entry); /*PG*/
  sample.buf_id = 0;
  sample.cur_vol = -1;
  sample.sample_buf = NULL;
}

void sound_wait()
{
  // Allow any currently playing sample to complete.
  int i = sound_get_time();
  if (i > 0)
    systick_wait_ms(i + 2);
}

void sound_reset()
{
  // Reset the sound system. In paricular remove any references to allocated
  // memory, as the system is about to release all ram.
  sound_wait();
  sample.sample_buf = NULL;
}

static void create_tone(const byte *lookup, int lulen, U32 *pat, int len)
{
  // Fill the supplied buffer with len longs representing a pdm encoded
  // wave. We use a pre-generated lookup table for the wave shape.
  // The shape will be symmetric. The original code used interpolation as part
  // of the lookup but this was too slow. The encoder uses a second order
  // sigma-delta convertor.
  int numsamples = len*32/2;
  int step = numsamples/lulen;
  int word = 0;
  U32 bit = 0x80000000;
  U32 bit2 = 0x00000001;
  int i = numsamples/step;;
  int error = 0;
  int error2 = 0;
  int out=0;
  U32 bits = 0;
  U32 bits2 = 0;
  int entry = 0;
  
  while (i-- > 0)
  {
    int res = lookup[entry++];
    res = sample.amp[res];
    int j = step;
    while (j-- > 0)
    {
      // Perform pdm conversion    
      error = res - out + error;
      error2 = error - out + error2;
      if (error2 > 0)
      {
        out = 128;
        bits |= bit;
      }
      else
      {
        out = -128;
        bits2 |= bit2;
      }
      bit2 <<= 1;
      bit >>= 1;
      if (bit == 0)
      {
        bit = 0x80000000;
        bit2 = 0x00000001;
        pat[word++] = bits;
        pat[len - word] = bits2;
        bits2 = bits = 0;
      }
    }
  }
}

static void set_vol(int vol)
{
  // Create the amplification control lookup table. We use a logartihmic
  // volume system mapped into a range of 0 to 120. 0 is muted, 100 is
  // full volume, 120 is driving into overload.
  int i;
  S32 output;

  // Get into range and use log conversion
  if (vol < 0) vol = 0;
  if (vol > MAXVOL) vol = MAXVOL;
  // Do we need to create a new LUT?
  if (sample.cur_vol == vol) return;
  output = logvol[vol/10];
  output = output + ((logvol[vol/10 + 1]-output)*(vol%10))/10;

  // Create the symmetric lookup table
  for(i = 0; i <= 128; i++)
  {
    S32 a = (i*output)/128;
    if (a > 127)
      a = 127;
    sample.amp[128-i] = -a; 
    sample.amp[i+128] = a;
  }
  sample.cur_vol = vol;
}


void sound_freq(U32 freq, U32 ms, int vol)
{
  // Set things up ready to go, note we avoid using anything that may
  // be used by the interupt routine because ints may still be enabled
  // at this point
  int len;
  // we use longer samples for lower frequencies
  if (freq > 1000)
    len = 16;
  else if (freq < 500)
    len = 64;
  else
    len = 32;
  sound_mode = SOUND_MODE_TONE;
  // Update the volume lookup table if we need to
  set_vol(vol);
  int buf = sample.buf_id^1;
  create_tone(sine, sizeof(sine), sample.buf[buf], len); 
  // The note gneration takes approx 1ms, to ensure that we do not get gaps
  // when playing a series of tones we extend the requested period to cover
  // this 1ms cost.
  ms += TONE_OVERHEAD;
  // Turn of ints while we update shared values
  sound_interrupt_disable();
  /* Genrate the pdm wave of the correct amplitude */
  sample.clock_div = (OSC/(len*32*2) + freq/2)/freq;
  // Calculate actual frequency and use this for length calc
  freq = (OSC/(2*sample.clock_div))/(len*32);
  if (ms <= TONE_OVERHEAD)
    sample.count = 0;
  else
    sample.count = (freq*ms + 1000-1)/1000;
  sample.len = len;
  sample.ptr = (U8 *)sample.buf[buf];
  sample.buf_id = buf;
  *AT91C_SSC_PTCR = AT91C_PDC_TXTEN;
  sound_mode = SOUND_MODE_TONE;
  sample.in_index = sample.out_index;
  sound_interrupt_enable(AT91C_SSC_TXBUFE);
}

#if (PDM_ENCODE == PDM_LOOKUP)
static void sound_fill_sample_buffer() {
  sample.buf_id ^= 1;
  U32 *sbuf = sample.buf[sample.buf_id];
  U8 *samples = sample.ptr;
  U32 bufmask = (samples == sample.sample_buf ? SAMPLEBUFSZ-1 : 0xffffffff);
  U32 out = sample.out_index;
  U32 in = sample.in_index;
  S8* amp = sample.amp;
  U8 i;
  /* Each 8-bit sample is turned into 8 32-bit numbers, i.e. 256 bits altogether */
  for (i = 0; i < PDM_BUFFER_LENGTH >> 3; i++) {
    U8 smp;
    U8 msk;
    U8 s3;
    if (out != in)
    {
      smp = amp[samples[out]] + 128;
      out = (out + 1) & bufmask;
    }
    else
      smp = 128;
    msk = "\x00\x10\x22\x4a\x55\x6d\x77\x7f"[smp & 7];
    s3 = smp >> 3;
    *sbuf++ = sample_pattern[s3 + (msk & 1)]; msk >>= 1;
    *sbuf++ = sample_pattern[s3 + (msk & 1)]; msk >>= 1;
    *sbuf++ = sample_pattern[s3 + (msk & 1)]; msk >>= 1;
    *sbuf++ = sample_pattern[s3 + (msk & 1)]; msk >>= 1;
    *sbuf++ = sample_pattern[s3 + (msk & 1)]; msk >>= 1;
    *sbuf++ = sample_pattern[s3 + (msk & 1)]; msk >>= 1;
    *sbuf++ = sample_pattern[s3 + (msk & 1)];
    *sbuf++ = sample_pattern[s3];
    /*//An alternative that doesn't need a sample_pattern array:
    U32 msb = 0xffffffff << (32 - (smp >> 3));
    U32 lsb = msb | (msb >> 1);
    *sbuf++ = ((msk & 1) ? lsb : msb); msk >>= 1;
    *sbuf++ = ((msk & 1) ? lsb : msb); msk >>= 1;
    *sbuf++ = ((msk & 1) ? lsb : msb); msk >>= 1;
    *sbuf++ = ((msk & 1) ? lsb : msb); msk >>= 1;
    *sbuf++ = ((msk & 1) ? lsb : msb); msk >>= 1;
    *sbuf++ = ((msk & 1) ? lsb : msb); msk >>= 1;
    *sbuf++ = ((msk & 1) ? lsb : msb);
    *sbuf++ = msb;
  */}
  sample.out_index = out;
}

#else
static void sound_fill_sample_buffer(){
  // Fill the sample buufer converting the PCM samples in PDM samples. This
  // version uses a second order sigma-delta convertor. The actual conversion
  // used is unstable for values at the full range points. To avoid this
  // problem we limit the accumlated error. In this case we limit the error
  // by using a simple mod operation.
  sample.buf_id ^= 1;
  U32 *sbuf = sample.buf[sample.buf_id];
  U8 *samples = sample.ptr;
  U32 bufmask = (samples == sample.sample_buf ? SAMPLEBUFSZ-1 : 0xffffffff);
  U32 out = sample.out_index;
  U32 in = sample.in_index;
  S8* amp = sample.amp;
  int i;
  // Error accumulation terms
  static int e = 0;
  static int e2 = 0;
  for (i = 0; i < SAMPPERBUF; i++) {
    int res;
    if (out != in)
    {
      res = (int)amp[samples[out]];
      out = (out + 1) & bufmask;
    }
    else
      res = 0;
    // Perform sigma-delta conversion
    U32 bits = 0;
    U32 bit = 0x80000000;
    while (bit)
    {
      if (e2 >= 0)
      {
        e = res - 128 + e;
        e2 = e - 128 + (e2 & 0x1ff);
        bits |= bit;
      }
      else
      {
        e = res - -128 + e;
        e2 = e - -128 + (e2 | 0xfffffe00);
      }
      bit >>= 1;
    }
    *sbuf++ = bits;
  }
  sample.out_index = out;
}
#endif


void sound_play_sample(U8 *data, U32 length, U32 freq, int vol)
{
  // Play a series of PCM samples
  if (data == (U8 *) 0 || length == 0) return;
  // Calculate the clock divisor based upon the recorded sample frequency */
  if (freq == 0) freq = DEFRATE;
  if (freq > MAXRATE) freq = MAXRATE;
  if (freq < MINRATE) freq = MINRATE;
  U32 cdiv = (OSC/(2*SAMPBITS) + freq/2)/freq;
  set_vol(vol);
  // Turn off ints while we update shared values
  sound_interrupt_disable();
  sound_mode = SOUND_MODE_PCM;
  sample.count = (length + SAMPPERBUF - 1)/SAMPPERBUF;
  sample.out_index = 0;
  sample.in_index = length;
  sample.ptr = data;
  sample.len = PDM_BUFFER_LENGTH;
  sample.clock_div = cdiv;
  // re-enable and wait for the current sample to complete
  sound_interrupt_enable(AT91C_SSC_TXBUFE);
  *AT91C_SSC_PTCR = AT91C_PDC_TXTEN;
}

int sound_add_sample(U8 *data, U32 length, U32 freq, int vol)
{
  // Add a set of samples into the playback queue, if not currently
  // playing start the playback process.
  
  // If this is the first sample simply start to play it
  if (sound_mode != SOUND_MODE_PCM || sample.ptr != sample.sample_buf)
  {
    if (sample.sample_buf == NULL)
    {
      sample.sample_buf = system_allocate(SAMPLEBUFSZ);
      if (sample.sample_buf == NULL) return -1;
    }
    if (length > SAMPLEBUFSZ - 1) length = SAMPLEBUFSZ - 1;
    memcpy(sample.sample_buf, data, length);
    sound_play_sample(sample.sample_buf, length, freq, vol);
    return length;
  }
  // Otherwise add the data to the buffer
  // Turn off ints while we update shared values
  sound_interrupt_disable();
  // add the data
  U8 *sbuf = sample.ptr;
  U32 in = sample.in_index;
  U32 out = sample.out_index;
  int cnt = (int) ((out - in - 1) & (SAMPLEBUFSZ - 1));
  if (cnt > length) cnt = length;
  length = cnt;
  while (cnt-- > 0)
  {
    sbuf[in] = *data++;
    in = (in + 1) & (SAMPLEBUFSZ - 1);
  }
  sample.in_index = in;
  sample.count = (((in - out) & (SAMPLEBUFSZ - 1)) + SAMPPERBUF - 1)/SAMPPERBUF;
   
  // re-enable and wait for the current sample to complete
  sound_interrupt_enable(AT91C_SSC_ENDTX);
  *AT91C_SSC_PTCR = AT91C_PDC_TXTEN;
  return length;
}

int sound_get_time()
{
  // Return the amount of time still to play for the current tone/sample
  if (sound_mode > SOUND_MODE_NONE)
  {
    int ms = (sample.count*sample.len*32)/((OSC/(2*1000))/sample.clock_div);
    // remove the extra time we added
    if (sound_mode == SOUND_MODE_TONE && ms > 0) ms -= TONE_OVERHEAD;
    return ms;
  }
  else
    return 0;
}

void sound_isr_C()
{
//U64 s = systick_get_ns();
    if (sample.count > 0)
    {
      // refill the buffer, and adjust any clocks
      *AT91C_SSC_CMR = sample.clock_div;
      sound_enable();
      if (*AT91C_SSC_TCR == 0)
      {
        if (sound_mode == SOUND_MODE_PCM)
        {
          sound_fill_sample_buffer();
          *AT91C_SSC_TPR = (unsigned int)sample.buf[sample.buf_id];
        }
        else
          *AT91C_SSC_TPR = (unsigned int)sample.ptr;
        *AT91C_SSC_TCR = sample.len;
        sample.count--;
      }
      if (sound_mode == SOUND_MODE_PCM)
      {
        sound_fill_sample_buffer();
        *AT91C_SSC_TNPR = (unsigned int)sample.buf[sample.buf_id];
      }
      else
        *AT91C_SSC_TNPR = (unsigned int)sample.ptr;
      *AT91C_SSC_TNCR = sample.len;
      sample.count--;
      // If this is the last sample wait for it to complete, otherwise wait
      // to switch buffers
      sound_interrupt_enable(AT91C_SSC_ENDTX);
    }
    else
    {
      sound_disable();
      sound_interrupt_disable();
    }
//ttime += (systick_get_ns() - s);
}
