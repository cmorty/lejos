#ifndef SOUND_H_
#define SOUND_H_

#include "mytypes.h"

#define SAMPLEWORD  U32
#define SAMPLEWORDS 8
#define SAMPLEWORDBITS (sizeof(SAMPLEWORD) * 8)
#define SAMPLEBITS (SAMPLEWORDS * SAMPLEWORDBITS)

void sound_init();
void sound_int_enable();
void sound_int_enable();
void sound_enable();
void sound_disable();
void sound_isr_C();

void sound_freq(U32 freq, U32 ms);

#endif /*SOUND_H_*/
