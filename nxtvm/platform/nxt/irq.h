#ifndef PLATFORM_NXT_IRQ_H_
#define PLATFORM_NXT_IRQ_H_


/* Default handlers for the three general kinds of interrupts that the
 * ARM core has to handle. These are defined in irq.s, and just freeze
 * the board in an infinite loop.
 */
extern void default_isr(void);
extern void default_fiq(void);
extern void spurious_isr(void);

extern void systick_isr_entry(void);
extern void systick_low_priority_entry(void);
extern void udp_isr_entry(void);
extern void spi_isr_entry(void);
extern void twi_isr_entry(void);
extern void sound_isr_entry(void);
extern void nxt_motor_isr_entry(void);
extern void i2c_timer_isr_entry(void);


#endif /* PLATFORM_NXT_IRQ_H_ */
