
test.bin:     file format binary

Disassembly of section .data:

00000000 <.data>:
   0:	e3a0d821 	mov	sp, #2162688	; 0x210000
   4:	e92d4000 	stmdb	sp!, {lr}
   8:	eb000000 	bl	0x10
   c:	e8bd8000 	ldmia	sp!, {pc}
  10:	e3e00000 	mvn	r0, #0	; 0x0
  14:	e5103bff 	ldr	r3, [r0, #-3071]
  18:	e3833202 	orr	r3, r3, #536870912	; 0x20000000
  1c:	e5003bff 	str	r3, [r0, #-3071]
  20:	e5102bef 	ldr	r2, [r0, #-3055]
  24:	e3a01983 	mov	r1, #2146304	; 0x20c000
  28:	e1a0c00d 	mov	ip, sp
  2c:	e3a03000 	mov	r3, #0	; 0x0
  30:	e3822202 	orr	r2, r2, #536870912	; 0x20000000
  34:	e2811a03 	add	r1, r1, #12288	; 0x3000
  38:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
  3c:	e5002bef 	str	r2, [r0, #-3055]
  40:	e24cb004 	sub	fp, ip, #4	; 0x4
  44:	e5c13000 	strb	r3, [r1]
  48:	eb000089 	bl	0x274
  4c:	e3a03001 	mov	r3, #1	; 0x1
  50:	ea000002 	b	0x60
  54:	e2833001 	add	r3, r3, #1	; 0x1
  58:	e353007f 	cmp	r3, #127	; 0x7f
  5c:	ca000012 	bgt	0xac
  60:	e3530001 	cmp	r3, #1	; 0x1
  64:	1afffffa 	bne	0x54
  68:	e3a05783 	mov	r5, #34340864	; 0x20c0000
  6c:	e2855803 	add	r5, r5, #196608	; 0x30000
  70:	e3a04000 	mov	r4, #0	; 0x0
  74:	ea000001 	b	0x80
  78:	e3540064 	cmp	r4, #100	; 0x64
  7c:	0a000012 	beq	0xcc
  80:	e1a01004 	mov	r1, r4
  84:	e3a00001 	mov	r0, #1	; 0x1
  88:	eb00005f 	bl	0x20c
  8c:	e3500000 	cmp	r0, #0	; 0x0
  90:	e2844001 	add	r4, r4, #1	; 0x1
  94:	0afffff7 	beq	0x78
  98:	e3a03002 	mov	r3, #2	; 0x2
  9c:	e2833001 	add	r3, r3, #1	; 0x1
  a0:	e353007f 	cmp	r3, #127	; 0x7f
  a4:	e5c50000 	strb	r0, [r5]
  a8:	daffffec 	ble	0x60
  ac:	e3e02000 	mvn	r2, #0	; 0x0
  b0:	e5123bcb 	ldr	r3, [r2, #-3019]
  b4:	e3a00000 	mov	r0, #0	; 0x0
  b8:	e3833202 	orr	r3, r3, #536870912	; 0x20000000
  bc:	e5023bcb 	str	r3, [r2, #-3019]
  c0:	e24bd014 	sub	sp, fp, #20	; 0x14
  c4:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
  c8:	e12fff1e 	bx	lr
  cc:	e3a03002 	mov	r3, #2	; 0x2
  d0:	eaffffdf 	b	0x54
  d4:	e3a03c06 	mov	r3, #1536	; 0x600
  d8:	e2833001 	add	r3, r3, #1	; 0x1
  dc:	e3e02000 	mvn	r2, #0	; 0x0
  e0:	e50233df 	str	r3, [r2, #-991]
  e4:	e5123397 	ldr	r3, [r2, #-919]
  e8:	e3130001 	tst	r3, #1	; 0x1
  ec:	0afffffc 	beq	0xe4
  f0:	e3a032e1 	mov	r3, #268435470	; 0x1000000e
  f4:	e2833712 	add	r3, r3, #4718592	; 0x480000
  f8:	e2833b07 	add	r3, r3, #7168	; 0x1c00
  fc:	e50233d3 	str	r3, [r2, #-979]
 100:	e3e02000 	mvn	r2, #0	; 0x0
 104:	e5123397 	ldr	r3, [r2, #-919]
 108:	e3130004 	tst	r3, #4	; 0x4
 10c:	0afffffc 	beq	0x104
 110:	e3e02000 	mvn	r2, #0	; 0x0
 114:	e5123397 	ldr	r3, [r2, #-919]
 118:	e3130008 	tst	r3, #8	; 0x8
 11c:	0afffffc 	beq	0x114
 120:	e3a03004 	mov	r3, #4	; 0x4
 124:	e50233cf 	str	r3, [r2, #-975]
 128:	e3e02000 	mvn	r2, #0	; 0x0
 12c:	e5123397 	ldr	r3, [r2, #-919]
 130:	e3130008 	tst	r3, #8	; 0x8
 134:	0afffffc 	beq	0x12c
 138:	e12fff1e 	bx	lr
 13c:	e3e03000 	mvn	r3, #0	; 0x0
 140:	e5803028 	str	r3, [r0, #40]
 144:	e3a02080 	mov	r2, #128	; 0x80
 148:	e2833005 	add	r3, r3, #5	; 0x5
 14c:	e5802000 	str	r2, [r0]
 150:	e5803000 	str	r3, [r0]
 154:	e12fff1e 	bx	lr
 158:	e5801010 	str	r1, [r0, #16]
 15c:	e12fff1e 	bx	lr
 160:	e1813002 	orr	r3, r1, r2
 164:	e5801070 	str	r1, [r0, #112]
 168:	e5802074 	str	r2, [r0, #116]
 16c:	e5803004 	str	r3, [r0, #4]
 170:	e12fff1e 	bx	lr
 174:	e3e03c0b 	mvn	r3, #2816	; 0xb00
 178:	e3a01018 	mov	r1, #24	; 0x18
 17c:	e3a02000 	mov	r2, #0	; 0x0
 180:	e503108f 	str	r1, [r3, #-143]
 184:	e503208b 	str	r2, [r3, #-139]
 188:	e50310fb 	str	r1, [r3, #-251]
 18c:	e12fff1e 	bx	lr
 190:	e3a02c02 	mov	r2, #512	; 0x200
 194:	e3e03c03 	mvn	r3, #768	; 0x300
 198:	e50320ef 	str	r2, [r3, #-239]
 19c:	e12fff1e 	bx	lr
 1a0:	e1a01801 	mov	r1, r1, lsl #16
 1a4:	e3e0ca47 	mvn	ip, #290816	; 0x47000
 1a8:	e1a01821 	mov	r1, r1, lsr #16
 1ac:	e1a00800 	mov	r0, r0, lsl #16
 1b0:	e50c1ff3 	str	r1, [ip, #-4083]
 1b4:	e20008ff 	and	r0, r0, #16711680	; 0xff0000
 1b8:	e38004ff 	orr	r0, r0, #-16777216	; 0xff000000
 1bc:	e51c1ffb 	ldr	r1, [ip, #-4091]
 1c0:	e3800cef 	orr	r0, r0, #61184	; 0xef00
 1c4:	e38000ff 	orr	r0, r0, #255	; 0xff
 1c8:	e20220ff 	and	r2, r2, #255	; 0xff
 1cc:	e3a03007 	mov	r3, #7	; 0x7
 1d0:	e0011000 	and	r1, r1, r0
 1d4:	e50c1ffb 	str	r1, [ip, #-4091]
 1d8:	e50c3fff 	str	r3, [ip, #-4095]
 1dc:	e50c2fcb 	str	r2, [ip, #-4043]
 1e0:	e51c2fdf 	ldr	r2, [ip, #-4063]
 1e4:	e3a03983 	mov	r3, #2146304	; 0x20c000
 1e8:	e2833a03 	add	r3, r3, #12288	; 0x3000
 1ec:	e3120001 	tst	r2, #1	; 0x1
 1f0:	e5832004 	str	r2, [r3, #4]
 1f4:	112fff1e 	bxne	lr
 1f8:	e1a0200c 	mov	r2, ip
 1fc:	e5123fdf 	ldr	r3, [r2, #-4063]
 200:	e3130001 	tst	r3, #1	; 0x1
 204:	0afffffc 	beq	0x1fc
 208:	e12fff1e 	bx	lr
 20c:	e1a01801 	mov	r1, r1, lsl #16
 210:	e3e0ca47 	mvn	ip, #290816	; 0x47000
 214:	e1a01821 	mov	r1, r1, lsr #16
 218:	e50c1ff3 	str	r1, [ip, #-4083]
 21c:	e51c3ffb 	ldr	r3, [ip, #-4091]
 220:	e1a00800 	mov	r0, r0, lsl #16
 224:	e3833a01 	orr	r3, r3, #4096	; 0x1000
 228:	e20008ff 	and	r0, r0, #16711680	; 0xff0000
 22c:	e1800003 	orr	r0, r0, r3
 230:	e3a03007 	mov	r3, #7	; 0x7
 234:	e50c0ffb 	str	r0, [ip, #-4091]
 238:	e50c3fff 	str	r3, [ip, #-4095]
 23c:	e51c2fdf 	ldr	r2, [ip, #-4063]
 240:	e3120001 	tst	r2, #1	; 0x1
 244:	1a000006 	bne	0x264
 248:	e3a01983 	mov	r1, #2146304	; 0x20c000
 24c:	e1a0200c 	mov	r2, ip
 250:	e2811a03 	add	r1, r1, #12288	; 0x3000
 254:	e5123fdf 	ldr	r3, [r2, #-4063]
 258:	e3130001 	tst	r3, #1	; 0x1
 25c:	0afffffc 	beq	0x254
 260:	e5813004 	str	r3, [r1, #4]
 264:	e3e03a47 	mvn	r3, #290816	; 0x47000
 268:	e5130fcf 	ldr	r0, [r3, #-4047]
 26c:	e20000ff 	and	r0, r0, #255	; 0xff
 270:	e12fff1e 	bx	lr
 274:	e3e03c0b 	mvn	r3, #2816	; 0xb00
 278:	e3a02018 	mov	r2, #24	; 0x18
 27c:	e3a0c000 	mov	ip, #0	; 0x0
 280:	e503208f 	str	r2, [r3, #-143]
 284:	e3a01c02 	mov	r1, #512	; 0x200
 288:	e503c08b 	str	ip, [r3, #-139]
 28c:	e50320fb 	str	r2, [r3, #-251]
 290:	e2833b02 	add	r3, r3, #2048	; 0x800
 294:	e50310ef 	str	r1, [r3, #-239]
 298:	e3e00a47 	mvn	r0, #290816	; 0x47000
 29c:	e2422019 	sub	r2, r2, #25	; 0x19
 2a0:	e3a03080 	mov	r3, #128	; 0x80
 2a4:	e5002fd7 	str	r2, [r0, #-4055]
 2a8:	e5003fff 	str	r3, [r0, #-4095]
 2ac:	e3a03a21 	mov	r3, #135168	; 0x21000
 2b0:	e2822005 	add	r2, r2, #5	; 0x5
 2b4:	e24330f1 	sub	r3, r3, #241	; 0xf1
 2b8:	e5002fff 	str	r2, [r0, #-4095]
 2bc:	e5003fef 	str	r3, [r0, #-4079]
 2c0:	e500cffb 	str	ip, [r0, #-4091]
 2c4:	e12fff1e 	bx	lr
