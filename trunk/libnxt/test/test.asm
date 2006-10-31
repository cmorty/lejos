
test.bin:     file format binary

Disassembly of section .data:

00000000 <.data>:
   0:	e3a0d821 	mov	sp, #2162688	; 0x210000
   4:	e92d4000 	stmdb	sp!, {lr}
   8:	eb000000 	bl	0x10
   c:	e8bd8000 	ldmia	sp!, {pc}
  10:	e3a03983 	mov	r3, #2146304	; 0x20c000
  14:	e2833a03 	add	r3, r3, #12288	; 0x3000
  18:	e3a00000 	mov	r0, #0	; 0x0
  1c:	e5c30000 	strb	r0, [r3]
  20:	e3e01000 	mvn	r1, #0	; 0x0
  24:	e5113bff 	ldr	r3, [r1, #-3071]
  28:	e3833202 	orr	r3, r3, #536870912	; 0x20000000
  2c:	e5013bff 	str	r3, [r1, #-3071]
  30:	e5112bef 	ldr	r2, [r1, #-3055]
  34:	e3822202 	orr	r2, r2, #536870912	; 0x20000000
  38:	e5012bef 	str	r2, [r1, #-3055]
  3c:	e3a0c000 	mov	ip, #0	; 0x0
  40:	e3e01000 	mvn	r1, #0	; 0x0
  44:	e5112bcb 	ldr	r2, [r1, #-3019]
  48:	e3a03bc3 	mov	r3, #199680	; 0x30c00
  4c:	e28cc001 	add	ip, ip, #1	; 0x1
  50:	e2833d05 	add	r3, r3, #320	; 0x140
  54:	e3822202 	orr	r2, r2, #536870912	; 0x20000000
  58:	e15c0003 	cmp	ip, r3
  5c:	e5012bcb 	str	r2, [r1, #-3019]
  60:	1afffff6 	bne	0x40
  64:	e3a02000 	mov	r2, #0	; 0x0
  68:	e5113bcf 	ldr	r3, [r1, #-3023]
  6c:	e2822001 	add	r2, r2, #1	; 0x1
  70:	e3833202 	orr	r3, r3, #536870912	; 0x20000000
  74:	e152000c 	cmp	r2, ip
  78:	e5013bcf 	str	r3, [r1, #-3023]
  7c:	1afffff9 	bne	0x68
  80:	e2800001 	add	r0, r0, #1	; 0x1
  84:	e3500014 	cmp	r0, #20	; 0x14
  88:	1affffeb 	bne	0x3c
  8c:	e3a00000 	mov	r0, #0	; 0x0
  90:	e12fff1e 	bx	lr
