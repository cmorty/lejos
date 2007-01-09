#ifndef __AIC_H__
#  define __AIC_H__
#  include "mytypes.h"

void aic_initialise(void);
void aic_set_vector(U32 vector, U32 mode, U32 isr);
void aic_mask_on(U32 vector);
void aic_mask_off(U32 vector);
void aic_clear(U32 mask);

#  define AIC_INT_LEVEL_LOW    2
#  define AIC_INT_LEVEL_NORMAL 4
#  define AIC_INT_LEVEL_ABOVE_NORMAL 5

#endif
