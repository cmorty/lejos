#ifndef SOUND_H_
#define SOUND_H_

#include "mytypes.h"

void sound_init();
void sound_isr_C();

void sound_freq(U32 freq, U32 ms, int vol);
void sound_play_sample(U8 *data, U32 length, U32 freq, int vol);
int sound_add_sample(U8 *data, U32 length, U32 freq, int vol);
void sound_set_volume(int vol);
int sound_get_volume();
int sound_get_time();
void sound_wait();
void sound_reset();

#define MAXVOL 100

#endif /*SOUND_H_*/
