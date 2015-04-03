#ifndef PLATFORM_NXT_IRQ_H_
#define PLATFORM_NXT_IRQ_H_


/* Default handlers for the three general kinds of interrupts that the
 * ARM core has to handle. These are defined in irq.s, and just freeze
 * the board in an infinite loop.
 */
void default_isr(void);
void default_fiq(void);
void spurious_isr(void);

void systick_isr_C(void);
void systick_isr_entry(void);
void systick_low_priority_C(void);
void systick_low_priority_entry(void);
void udp_isr_C(void);
void udp_isr_entry(void);
void spi_isr_C(void);
void spi_isr_entry(void);
void twi_isr_C(void);
void twi_isr_entry(void);
void sound_isr_C(void);
void sound_isr_entry(void);
void uart_isr_C_0(void);
void uart_isr_entry_0(void);
void uart_isr_C_1(void);
void uart_isr_entry_1(void);
void nxt_motor_isr_C(void);
void nxt_motor_isr_entry(void);
void i2c_timer_isr_C(void);
void i2c_timer_isr_entry(void);


#endif /* PLATFORM_NXT_IRQ_H_ */
