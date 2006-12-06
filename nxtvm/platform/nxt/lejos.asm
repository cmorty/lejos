
nxt_lejos.bin:     file format binary

Disassembly of section .data:

00000000 <.data>:
       0:	e3a0d821 	mov	sp, #2162688	; 0x210000
       4:	e92d4000 	stmdb	sp!, {lr}
       8:	eb000057 	bl	0x16c
       c:	e8bd8000 	ldmia	sp!, {pc}
      10:	e59f3004 	ldr	r3, [pc, #4]	; 0x1c
      14:	e5930000 	ldr	r0, [r3]
      18:	e12fff1e 	bx	lr
      1c:	00207344 	eoreq	r7, r0, r4, asr #6
      20:	e12fff1e 	bx	lr
      24:	e12fff1e 	bx	lr
      28:	e12fff1e 	bx	lr
      2c:	e1a0c00d 	mov	ip, sp
      30:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
      34:	e59f2108 	ldr	r2, [pc, #264]	; 0x144
      38:	e59f4108 	ldr	r4, [pc, #264]	; 0x148
      3c:	e59f6108 	ldr	r6, [pc, #264]	; 0x14c
      40:	e3a01001 	mov	r1, #1	; 0x1
      44:	e5c41000 	strb	r1, [r4]
      48:	e5965000 	ldr	r5, [r6]
      4c:	e5c21000 	strb	r1, [r2]
      50:	e59f30f8 	ldr	r3, [pc, #248]	; 0x150
      54:	e1d500b6 	ldrh	r0, [r5, #6]
      58:	e3a04000 	mov	r4, #0	; 0x0
      5c:	e24cb004 	sub	fp, ip, #4	; 0x4
      60:	e0800005 	add	r0, r0, r5
      64:	e1d510b8 	ldrh	r1, [r5, #8]
      68:	e5834000 	str	r4, [r3]
      6c:	eb000c76 	bl	0x324c
      70:	e596c000 	ldr	ip, [r6]
      74:	e084e104 	add	lr, r4, r4, lsl #2
      78:	e08c208e 	add	r2, ip, lr, lsl #1
      7c:	e2820010 	add	r0, r2, #16	; 0x10
      80:	e5d01009 	ldrb	r1, [r0, #9]
      84:	e3c13001 	bic	r3, r1, #1	; 0x1
      88:	e5c03009 	strb	r3, [r0, #9]
      8c:	e284e001 	add	lr, r4, #1	; 0x1
      90:	e5d5c00f 	ldrb	ip, [r5, #15]
      94:	e20e40ff 	and	r4, lr, #255	; 0xff
      98:	e15c0004 	cmp	ip, r4
      9c:	2afffff3 	bcs	0x70
      a0:	eb000f50 	bl	0x3de8
      a4:	e59fc0a8 	ldr	ip, [pc, #168]	; 0x154
      a8:	e3a01982 	mov	r1, #2129920	; 0x208000
      ac:	e281ea02 	add	lr, r1, #8192	; 0x2000
      b0:	e1a0000e 	mov	r0, lr
      b4:	e3a01983 	mov	r1, #2146304	; 0x20c000
      b8:	e58ce000 	str	lr, [ip]
      bc:	eb000f51 	bl	0x3e08
      c0:	eb000b9f 	bl	0x2f44
      c4:	e3a02983 	mov	r2, #2146304	; 0x20c000
      c8:	e2823a03 	add	r3, r2, #12288	; 0x3000
      cc:	e3a00001 	mov	r0, #1	; 0x1
      d0:	e5c30000 	strb	r0, [r3]
      d4:	eb000dae 	bl	0x3794
      d8:	e59fc078 	ldr	ip, [pc, #120]	; 0x158
      dc:	e58c0000 	str	r0, [ip]
      e0:	e59f0074 	ldr	r0, [pc, #116]	; 0x15c
      e4:	e3a01000 	mov	r1, #0	; 0x0
      e8:	e59f4070 	ldr	r4, [pc, #112]	; 0x160
      ec:	e5c01000 	strb	r1, [r0]
      f0:	e59f206c 	ldr	r2, [pc, #108]	; 0x164
      f4:	e5841000 	str	r1, [r4]
      f8:	e3a03009 	mov	r3, #9	; 0x9
      fc:	e1a04001 	mov	r4, r1
     100:	e2533001 	subs	r3, r3, #1	; 0x1
     104:	e4824004 	str	r4, [r2], #4
     108:	5afffffc 	bpl	0x100
     10c:	e59c0000 	ldr	r0, [ip]
     110:	eb00091a 	bl	0x2580
     114:	e31000ff 	tst	r0, #255	; 0xff
     118:	1a000001 	bne	0x124
     11c:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
     120:	e12fff1e 	bx	lr
     124:	e3a03983 	mov	r3, #2146304	; 0x20c000
     128:	e59fc038 	ldr	ip, [pc, #56]	; 0x168
     12c:	e2830a03 	add	r0, r3, #12288	; 0x3000
     130:	e3a02003 	mov	r2, #3	; 0x3
     134:	e5cc4000 	strb	r4, [ip]
     138:	e5c02000 	strb	r2, [r0]
     13c:	eb0001a2 	bl	0x7cc
     140:	eafffff5 	b	0x11c
     144:	0020743b 	eoreq	r7, r0, fp, lsr r4
     148:	0020743a 	eoreq	r7, r0, sl, lsr r4
     14c:	00207424 	eoreq	r7, r0, r4, lsr #8
     150:	0020743c 	eoreq	r7, r0, ip, lsr r4
     154:	00207390 	mlaeq	r0, r0, r3, r7
     158:	00207394 	mlaeq	r0, r4, r3, r7
     15c:	002073f0 	streqd	r7, [r0], -r0
     160:	002073ec 	eoreq	r7, r0, ip, ror #7
     164:	002073c4 	eoreq	r7, r0, r4, asr #7
     168:	002073f1 	streqd	r7, [r0], -r1
     16c:	e1a0c00d 	mov	ip, sp
     170:	e92dd810 	stmdb	sp!, {r4, fp, ip, lr, pc}
     174:	e24cb004 	sub	fp, ip, #4	; 0x4
     178:	eb00111b 	bl	0x45ec
     17c:	e3a01983 	mov	r1, #2146304	; 0x20c000
     180:	e59f2024 	ldr	r2, [pc, #36]	; 0x1ac
     184:	e59f3024 	ldr	r3, [pc, #36]	; 0x1b0
     188:	e2810a03 	add	r0, r1, #12288	; 0x3000
     18c:	e3a04000 	mov	r4, #0	; 0x0
     190:	e5c04000 	strb	r4, [r0]
     194:	e5804004 	str	r4, [r0, #4]
     198:	e5832000 	str	r2, [r3]
     19c:	ebffffa2 	bl	0x2c
     1a0:	e1a00004 	mov	r0, r4
     1a4:	e89d6810 	ldmia	sp, {r4, fp, sp, lr}
     1a8:	e12fff1e 	bx	lr
     1ac:	00206b04 	eoreq	r6, r0, r4, lsl #22
     1b0:	00207424 	eoreq	r7, r0, r4, lsr #8
     1b4:	e1a0c00d 	mov	ip, sp
     1b8:	e1a00800 	mov	r0, r0, lsl #16
     1bc:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
     1c0:	e1a04820 	mov	r4, r0, lsr #16
     1c4:	e24cb004 	sub	fp, ip, #4	; 0x4
     1c8:	e3a0c983 	mov	ip, #2146304	; 0x20c000
     1cc:	e28c3a03 	add	r3, ip, #12288	; 0x3000
     1d0:	e2440004 	sub	r0, r4, #4	; 0x4
     1d4:	e3a02063 	mov	r2, #99	; 0x63
     1d8:	e5c32000 	strb	r2, [r3]
     1dc:	e1a04001 	mov	r4, r1
     1e0:	e350002d 	cmp	r0, #45	; 0x2d
     1e4:	979ff100 	ldrls	pc, [pc, r0, lsl #2]
     1e8:	ea0000f5 	b	0x5c4
     1ec:	002025b4 	streqh	r2, [r0], -r4
     1f0:	00202524 	eoreq	r2, r0, r4, lsr #10
     1f4:	00202514 	eoreq	r2, r0, r4, lsl r5
     1f8:	00202504 	eoreq	r2, r0, r4, lsl #10
     1fc:	002024f8 	streqd	r2, [r0], -r8
     200:	002024e0 	eoreq	r2, r0, r0, ror #9
     204:	002024a4 	eoreq	r2, r0, r4, lsr #9
     208:	00202484 	eoreq	r2, r0, r4, lsl #9
     20c:	00202470 	eoreq	r2, r0, r0, ror r4
     210:	00202594 	mlaeq	r0, r4, r5, r2
     214:	00202584 	eoreq	r2, r0, r4, lsl #11
     218:	00202550 	eoreq	r2, r0, r0, asr r5
     21c:	00202534 	eoreq	r2, r0, r4, lsr r5
     220:	002023f4 	streqd	r2, [r0], -r4
     224:	002023d4 	ldreqd	r2, [r0], -r4
     228:	002022f4 	streqd	r2, [r0], -r4
     22c:	002022f4 	streqd	r2, [r0], -r4
     230:	00202300 	eoreq	r2, r0, r0, lsl #6
     234:	00202350 	eoreq	r2, r0, r0, asr r3
     238:	00202330 	eoreq	r2, r0, r0, lsr r3
     23c:	002023b4 	streqh	r2, [r0], -r4
     240:	002025c4 	eoreq	r2, r0, r4, asr #11
     244:	002022d0 	ldreqd	r2, [r0], -r0
     248:	002022a4 	eoreq	r2, r0, r4, lsr #5
     24c:	002022d0 	ldreqd	r2, [r0], -r0
     250:	002022d0 	ldreqd	r2, [r0], -r0
     254:	002022d0 	ldreqd	r2, [r0], -r0
     258:	002022d8 	ldreqd	r2, [r0], -r8
     25c:	002022d0 	ldreqd	r2, [r0], -r0
     260:	002022d0 	ldreqd	r2, [r0], -r0
     264:	00202394 	mlaeq	r0, r4, r3, r2
     268:	002022d0 	ldreqd	r2, [r0], -r0
     26c:	0020236c 	eoreq	r2, r0, ip, ror #6
     270:	0020240c 	eoreq	r2, r0, ip, lsl #8
     274:	00202400 	eoreq	r2, r0, r0, lsl #8
     278:	002025c4 	eoreq	r2, r0, r4, asr #11
     27c:	00202460 	eoreq	r2, r0, r0, ror #8
     280:	00202448 	eoreq	r2, r0, r8, asr #8
     284:	0020242c 	eoreq	r2, r0, ip, lsr #8
     288:	0020242c 	eoreq	r2, r0, ip, lsr #8
     28c:	002022d0 	ldreqd	r2, [r0], -r0
     290:	002022d0 	ldreqd	r2, [r0], -r0
     294:	002022d8 	ldreqd	r2, [r0], -r8
     298:	002022d8 	ldreqd	r2, [r0], -r8
     29c:	002022d8 	ldreqd	r2, [r0], -r8
     2a0:	002025d4 	ldreqd	r2, [r0], -r4
     2a4:	e5912000 	ldr	r2, [r1]
     2a8:	e3a00d65 	mov	r0, #6464	; 0x1940
     2ac:	e2801006 	add	r1, r0, #6	; 0x6
     2b0:	e1520001 	cmp	r2, r1
     2b4:	0a0000d0 	beq	0x5fc
     2b8:	e3a0cd67 	mov	ip, #6592	; 0x19c0
     2bc:	e28c3004 	add	r3, ip, #4	; 0x4
     2c0:	e1520003 	cmp	r2, r3
     2c4:	0594e004 	ldreq	lr, [r4, #4]
     2c8:	024e0a01 	subeq	r0, lr, #4096	; 0x1000
     2cc:	0b00111b 	bleq	0x4740
     2d0:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
     2d4:	e12fff1e 	bx	lr
     2d8:	e59f2330 	ldr	r2, [pc, #816]	; 0x610
     2dc:	e5920000 	ldr	r0, [r2]
     2e0:	e3a01000 	mov	r1, #0	; 0x0
     2e4:	e280c004 	add	ip, r0, #4	; 0x4
     2e8:	e582c000 	str	ip, [r2]
     2ec:	e5801004 	str	r1, [r0, #4]
     2f0:	eafffff6 	b	0x2d0
     2f4:	e5910000 	ldr	r0, [r1]
     2f8:	eb000ae6 	bl	0x2e98
     2fc:	eafffff3 	b	0x2d0
     300:	e59f4308 	ldr	r4, [pc, #776]	; 0x610
     304:	e5943000 	ldr	r3, [r4]
     308:	e3a0c000 	mov	ip, #0	; 0x0
     30c:	e283e004 	add	lr, r3, #4	; 0x4
     310:	e583c004 	str	ip, [r3, #4]
     314:	e584e000 	str	lr, [r4]
     318:	ebffff3c 	bl	0x10
     31c:	e594c000 	ldr	ip, [r4]
     320:	e28c3004 	add	r3, ip, #4	; 0x4
     324:	e5843000 	str	r3, [r4]
     328:	e58c0004 	str	r0, [ip, #4]
     32c:	eaffffe7 	b	0x2d0
     330:	e59f42d8 	ldr	r4, [pc, #728]	; 0x610
     334:	e5940000 	ldr	r0, [r4]
     338:	e3a0c000 	mov	ip, #0	; 0x0
     33c:	e280e004 	add	lr, r0, #4	; 0x4
     340:	e580c004 	str	ip, [r0, #4]
     344:	e584e000 	str	lr, [r4]
     348:	eb000ecc 	bl	0x3e80
     34c:	eafffff2 	b	0x31c
     350:	e59f02bc 	ldr	r0, [pc, #700]	; 0x614
     354:	e59fc2bc 	ldr	ip, [pc, #700]	; 0x618
     358:	e3a02001 	mov	r2, #1	; 0x1
     35c:	e3a01002 	mov	r1, #2	; 0x2
     360:	e5c02000 	strb	r2, [r0]
     364:	e5cc1000 	strb	r1, [ip]
     368:	eaffffd8 	b	0x2d0
     36c:	e5910000 	ldr	r0, [r1]
     370:	e59fc298 	ldr	ip, [pc, #664]	; 0x610
     374:	e59f12a0 	ldr	r1, [pc, #672]	; 0x61c
     378:	e0813180 	add	r3, r1, r0, lsl #3
     37c:	e59c1000 	ldr	r1, [ip]
     380:	e1d300f4 	ldrsh	r0, [r3, #4]
     384:	e2812004 	add	r2, r1, #4	; 0x4
     388:	e58c2000 	str	r2, [ip]
     38c:	e5810004 	str	r0, [r1, #4]
     390:	eaffffce 	b	0x2d0
     394:	e59f0274 	ldr	r0, [pc, #628]	; 0x610
     398:	e5912000 	ldr	r2, [r1]
     39c:	e5901000 	ldr	r1, [r0]
     3a0:	e2822008 	add	r2, r2, #8	; 0x8
     3a4:	e2813004 	add	r3, r1, #4	; 0x4
     3a8:	e5803000 	str	r3, [r0]
     3ac:	e5812004 	str	r2, [r1, #4]
     3b0:	eaffffc6 	b	0x2d0
     3b4:	e59f4254 	ldr	r4, [pc, #596]	; 0x610
     3b8:	e5941000 	ldr	r1, [r4]
     3bc:	e3a02000 	mov	r2, #0	; 0x0
     3c0:	e2813004 	add	r3, r1, #4	; 0x4
     3c4:	e5812004 	str	r2, [r1, #4]
     3c8:	e5843000 	str	r3, [r4]
     3cc:	eb000ea6 	bl	0x3e6c
     3d0:	eaffffd1 	b	0x31c
     3d4:	e5912000 	ldr	r2, [r1]
     3d8:	e59fc230 	ldr	ip, [pc, #560]	; 0x610
     3dc:	e1d202d2 	ldrsb	r0, [r2, #34]
     3e0:	e59c1000 	ldr	r1, [ip]
     3e4:	e2813004 	add	r3, r1, #4	; 0x4
     3e8:	e58c3000 	str	r3, [ip]
     3ec:	e5810004 	str	r0, [r1, #4]
     3f0:	eaffffb6 	b	0x2d0
     3f4:	e8910009 	ldmia	r1, {r0, r3}
     3f8:	e5c03022 	strb	r3, [r0, #34]
     3fc:	eaffffb3 	b	0x2d0
     400:	e5910000 	ldr	r0, [r1]
     404:	eb0010e9 	bl	0x47b0
     408:	eaffffb0 	b	0x2d0
     40c:	e5d1c008 	ldrb	ip, [r1, #8]
     410:	e35c0003 	cmp	ip, #3	; 0x3
     414:	979ff10c 	ldrls	pc, [pc, ip, lsl #2]
     418:	eaffffac 	b	0x2d0
     41c:	002022d0 	ldreqd	r2, [r0], -r0
     420:	002022d0 	ldreqd	r2, [r0], -r0
     424:	002025e4 	eoreq	r2, r0, r4, ror #11
     428:	002025e4 	eoreq	r2, r0, r4, ror #11
     42c:	e59fc1dc 	ldr	ip, [pc, #476]	; 0x610
     430:	e59c1000 	ldr	r1, [ip]
     434:	e5940000 	ldr	r0, [r4]
     438:	e2813004 	add	r3, r1, #4	; 0x4
     43c:	e58c3000 	str	r3, [ip]
     440:	e5810004 	str	r0, [r1, #4]
     444:	eaffffa1 	b	0x2d0
     448:	e5911008 	ldr	r1, [r1, #8]
     44c:	e5942004 	ldr	r2, [r4, #4]
     450:	e1520001 	cmp	r2, r1
     454:	0affff9d 	beq	0x2d0
     458:	e59f31c0 	ldr	r3, [pc, #448]	; 0x620
     45c:	ea000059 	b	0x5c8
     460:	e5910004 	ldr	r0, [r1, #4]
     464:	e3500000 	cmp	r0, #0	; 0x0
     468:	1affff98 	bne	0x2d0
     46c:	eafffff9 	b	0x458
     470:	e5913000 	ldr	r3, [r1]
     474:	e59fc194 	ldr	ip, [pc, #404]	; 0x610
     478:	e1d302d0 	ldrsb	r0, [r3, #32]
     47c:	e59c1000 	ldr	r1, [ip]
     480:	eaffffd7 	b	0x3e4
     484:	e59f0184 	ldr	r0, [pc, #388]	; 0x610
     488:	e59f3194 	ldr	r3, [pc, #404]	; 0x624
     48c:	e590c000 	ldr	ip, [r0]
     490:	e5931000 	ldr	r1, [r3]
     494:	e28c3004 	add	r3, ip, #4	; 0x4
     498:	e5803000 	str	r3, [r0]
     49c:	e58c1004 	str	r1, [ip, #4]
     4a0:	eaffff8a 	b	0x2d0
     4a4:	e59fe178 	ldr	lr, [pc, #376]	; 0x624
     4a8:	e59e2000 	ldr	r2, [lr]
     4ac:	e3a04006 	mov	r4, #6	; 0x6
     4b0:	e5915004 	ldr	r5, [r1, #4]
     4b4:	e5c2401f 	strb	r4, [r2, #31]
     4b8:	e59e4000 	ldr	r4, [lr]
     4bc:	ebfffed3 	bl	0x10
     4c0:	e59fc14c 	ldr	ip, [pc, #332]	; 0x614
     4c4:	e0803005 	add	r3, r0, r5
     4c8:	e59f0148 	ldr	r0, [pc, #328]	; 0x618
     4cc:	e3a01001 	mov	r1, #1	; 0x1
     4d0:	e5843010 	str	r3, [r4, #16]
     4d4:	e5c01000 	strb	r1, [r0]
     4d8:	e5cc1000 	strb	r1, [ip]
     4dc:	eaffff7b 	b	0x2d0
     4e0:	e59f2130 	ldr	r2, [pc, #304]	; 0x618
     4e4:	e59f1128 	ldr	r1, [pc, #296]	; 0x614
     4e8:	e3a03001 	mov	r3, #1	; 0x1
     4ec:	e5c23000 	strb	r3, [r2]
     4f0:	e5c13000 	strb	r3, [r1]
     4f4:	eaffff75 	b	0x2d0
     4f8:	e5910000 	ldr	r0, [r1]
     4fc:	eb00081f 	bl	0x2580
     500:	eaffff72 	b	0x2d0
     504:	e5911008 	ldr	r1, [r1, #8]
     508:	e5940000 	ldr	r0, [r4]
     50c:	eb0009d1 	bl	0x2c58
     510:	eaffff6e 	b	0x2d0
     514:	e5910000 	ldr	r0, [r1]
     518:	e3a01000 	mov	r1, #0	; 0x0
     51c:	eb0009cd 	bl	0x2c58
     520:	eaffff6a 	b	0x2d0
     524:	e5910000 	ldr	r0, [r1]
     528:	e3a01001 	mov	r1, #1	; 0x1
     52c:	eb000a1a 	bl	0x2d9c
     530:	eaffff66 	b	0x2d0
     534:	e5911000 	ldr	r1, [r1]
     538:	e59f00d0 	ldr	r0, [pc, #208]	; 0x610
     53c:	e1d122d1 	ldrsb	r2, [r1, #33]
     540:	e5901000 	ldr	r1, [r0]
     544:	e2522000 	subs	r2, r2, #0	; 0x0
     548:	13a02001 	movne	r2, #1	; 0x1
     54c:	eaffff94 	b	0x3a4
     550:	e59fc0cc 	ldr	ip, [pc, #204]	; 0x624
     554:	e59c2000 	ldr	r2, [ip]
     558:	e3a03000 	mov	r3, #0	; 0x0
     55c:	e1d212d1 	ldrsb	r1, [r2, #33]
     560:	e59f00a8 	ldr	r0, [pc, #168]	; 0x610
     564:	e5c23021 	strb	r3, [r2, #33]
     568:	e5902000 	ldr	r2, [r0]
     56c:	e051c003 	subs	ip, r1, r3
     570:	13a0c001 	movne	ip, #1	; 0x1
     574:	e2823004 	add	r3, r2, #4	; 0x4
     578:	e5803000 	str	r3, [r0]
     57c:	e582c004 	str	ip, [r2, #4]
     580:	eaffff52 	b	0x2d0
     584:	e5911000 	ldr	r1, [r1]
     588:	e3a00001 	mov	r0, #1	; 0x1
     58c:	e5c10021 	strb	r0, [r1, #33]
     590:	eaffff4e 	b	0x2d0
     594:	e5911004 	ldr	r1, [r1, #4]
     598:	e241c001 	sub	ip, r1, #1	; 0x1
     59c:	e35c0009 	cmp	ip, #9	; 0x9
     5a0:	859f3080 	ldrhi	r3, [pc, #128]	; 0x628
     5a4:	8a000007 	bhi	0x5c8
     5a8:	e5940000 	ldr	r0, [r4]
     5ac:	eb000a3a 	bl	0x2e9c
     5b0:	eaffff46 	b	0x2d0
     5b4:	e5910000 	ldr	r0, [r1]
     5b8:	e3a01000 	mov	r1, #0	; 0x0
     5bc:	eb0009f6 	bl	0x2d9c
     5c0:	eaffff42 	b	0x2d0
     5c4:	e59f3060 	ldr	r3, [pc, #96]	; 0x62c
     5c8:	e5930000 	ldr	r0, [r3]
     5cc:	eb000a9d 	bl	0x3048
     5d0:	eaffff3e 	b	0x2d0
     5d4:	e59f0034 	ldr	r0, [pc, #52]	; 0x610
     5d8:	e59f3050 	ldr	r3, [pc, #80]	; 0x630
     5dc:	e590c000 	ldr	ip, [r0]
     5e0:	eaffffaa 	b	0x490
     5e4:	e5912000 	ldr	r2, [r1]
     5e8:	e59f102c 	ldr	r1, [pc, #44]	; 0x61c
     5ec:	e1d440b4 	ldrh	r4, [r4, #4]
     5f0:	e0813182 	add	r3, r1, r2, lsl #3
     5f4:	e1c340b4 	strh	r4, [r3, #4]
     5f8:	eaffff34 	b	0x2d0
     5fc:	e5942004 	ldr	r2, [r4, #4]
     600:	e2420a01 	sub	r0, r2, #4096	; 0x1000
     604:	eb001031 	bl	0x46d0
     608:	e5942000 	ldr	r2, [r4]
     60c:	eaffff29 	b	0x2b8
     610:	0020739c 	mlaeq	r0, ip, r3, r7
     614:	002073a3 	eoreq	r7, r0, r3, lsr #7
     618:	002073a2 	eoreq	r7, r0, r2, lsr #7
     61c:	00207374 	eoreq	r7, r0, r4, ror r3
     620:	00207414 	eoreq	r7, r0, r4, lsl r4
     624:	002073ec 	eoreq	r7, r0, ip, ror #7
     628:	00207408 	eoreq	r7, r0, r8, lsl #8
     62c:	002073f4 	streqd	r7, [r0], -r4
     630:	00207390 	mlaeq	r0, r0, r3, r7
     634:	e59fc034 	ldr	ip, [pc, #52]	; 0x670
     638:	e31000ff 	tst	r0, #255	; 0xff
     63c:	059c3000 	ldreq	r3, [ip]
     640:	02833002 	addeq	r3, r3, #2	; 0x2
     644:	058c3000 	streq	r3, [ip]
     648:	012fff1e 	bxeq	lr
     64c:	e59c3000 	ldr	r3, [ip]
     650:	e5d31000 	ldrb	r1, [r3]
     654:	e5d30001 	ldrb	r0, [r3, #1]
     658:	e1802401 	orr	r2, r0, r1, lsl #8
     65c:	e1a01802 	mov	r1, r2, lsl #16
     660:	e0830841 	add	r0, r3, r1, asr #16
     664:	e2403001 	sub	r3, r0, #1	; 0x1
     668:	e58c3000 	str	r3, [ip]
     66c:	e12fff1e 	bx	lr
     670:	002073b8 	streqh	r7, [r0], -r8
     674:	e59fc01c 	ldr	ip, [pc, #28]	; 0x698
     678:	e59c1000 	ldr	r1, [ip]
     67c:	e1a02001 	mov	r2, r1
     680:	e4120004 	ldr	r0, [r2], #-4
     684:	e5113004 	ldr	r3, [r1, #-4]
     688:	e0603003 	rsb	r3, r0, r3
     68c:	e58c2000 	str	r2, [ip]
     690:	e5013004 	str	r3, [r1, #-4]
     694:	e12fff1e 	bx	lr
     698:	0020739c 	mlaeq	r0, ip, r3, r7
     69c:	e1a0c00d 	mov	ip, sp
     6a0:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
     6a4:	e59f1068 	ldr	r1, [pc, #104]	; 0x714
     6a8:	e5913000 	ldr	r3, [r1]
     6ac:	e5130004 	ldr	r0, [r3, #-4]
     6b0:	e4132004 	ldr	r2, [r3], #-4
     6b4:	e24cb004 	sub	fp, ip, #4	; 0x4
     6b8:	e5813000 	str	r3, [r1]
     6bc:	e59fc054 	ldr	ip, [pc, #84]	; 0x718
     6c0:	e59f3054 	ldr	r3, [pc, #84]	; 0x71c
     6c4:	e3500000 	cmp	r0, #0	; 0x0
     6c8:	e5830000 	str	r0, [r3]
     6cc:	e1cc20b0 	strh	r2, [ip]
     6d0:	059f3048 	ldreq	r3, [pc, #72]	; 0x720
     6d4:	0a000009 	beq	0x700
     6d8:	e1dc20f0 	ldrsh	r2, [ip]
     6dc:	e3520000 	cmp	r2, #0	; 0x0
     6e0:	ba000005 	blt	0x6fc
     6e4:	e1d030b0 	ldrh	r3, [r0]
     6e8:	e1a01b83 	mov	r1, r3, lsl #23
     6ec:	e1a0cba1 	mov	ip, r1, lsr #23
     6f0:	e152000c 	cmp	r2, ip
     6f4:	e3a00001 	mov	r0, #1	; 0x1
     6f8:	ba000003 	blt	0x70c
     6fc:	e59f3020 	ldr	r3, [pc, #32]	; 0x724
     700:	e5930000 	ldr	r0, [r3]
     704:	eb000a4f 	bl	0x3048
     708:	e3a00000 	mov	r0, #0	; 0x0
     70c:	e89d6800 	ldmia	sp, {fp, sp, lr}
     710:	e12fff1e 	bx	lr
     714:	0020739c 	mlaeq	r0, ip, r3, r7
     718:	002073a0 	eoreq	r7, r0, r0, lsr #7
     71c:	002073a4 	eoreq	r7, r0, r4, lsr #7
     720:	00207400 	eoreq	r7, r0, r0, lsl #8
     724:	00207410 	eoreq	r7, r0, r0, lsl r4
     728:	e1a0c00d 	mov	ip, sp
     72c:	e92dd810 	stmdb	sp!, {r4, fp, ip, lr, pc}
     730:	e59f4080 	ldr	r4, [pc, #128]	; 0x7b8
     734:	e24cb004 	sub	fp, ip, #4	; 0x4
     738:	e594c000 	ldr	ip, [r4]
     73c:	e51c1004 	ldr	r1, [ip, #-4]
     740:	e59f3074 	ldr	r3, [pc, #116]	; 0x7bc
     744:	e41c2004 	ldr	r2, [ip], #-4
     748:	e59f0070 	ldr	r0, [pc, #112]	; 0x7c0
     74c:	e3510000 	cmp	r1, #0	; 0x0
     750:	e5831000 	str	r1, [r3]
     754:	e584c000 	str	ip, [r4]
     758:	e1c020b0 	strh	r2, [r0]
     75c:	059f3060 	ldreq	r3, [pc, #96]	; 0x7c4
     760:	0a000009 	beq	0x78c
     764:	e1d020f0 	ldrsh	r2, [r0]
     768:	e3520000 	cmp	r2, #0	; 0x0
     76c:	ba000005 	blt	0x788
     770:	e1d130b0 	ldrh	r3, [r1]
     774:	e1a01b83 	mov	r1, r3, lsl #23
     778:	e1a00ba1 	mov	r0, r1, lsr #23
     77c:	e1520000 	cmp	r2, r0
     780:	b3a03001 	movlt	r3, #1	; 0x1
     784:	ba000003 	blt	0x798
     788:	e59f3038 	ldr	r3, [pc, #56]	; 0x7c8
     78c:	e5930000 	ldr	r0, [r3]
     790:	eb000a2c 	bl	0x3048
     794:	e3a03000 	mov	r3, #0	; 0x0
     798:	e20300ff 	and	r0, r3, #255	; 0xff
     79c:	e3500000 	cmp	r0, #0	; 0x0
     7a0:	15943000 	ldrne	r3, [r4]
     7a4:	13a00001 	movne	r0, #1	; 0x1
     7a8:	12433004 	subne	r3, r3, #4	; 0x4
     7ac:	15843000 	strne	r3, [r4]
     7b0:	e89d6810 	ldmia	sp, {r4, fp, sp, lr}
     7b4:	e12fff1e 	bx	lr
     7b8:	0020739c 	mlaeq	r0, ip, r3, r7
     7bc:	002073a4 	eoreq	r7, r0, r4, lsr #7
     7c0:	002073a0 	eoreq	r7, r0, r0, lsr #7
     7c4:	00207400 	eoreq	r7, r0, r0, lsl #8
     7c8:	00207410 	eoreq	r7, r0, r0, lsl r4
     7cc:	e1a0c00d 	mov	ip, sp
     7d0:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
     7d4:	e59f3fb0 	ldr	r3, [pc, #4016]	; 0x178c
     7d8:	e59f9fb0 	ldr	r9, [pc, #4016]	; 0x1790
     7dc:	e24cb004 	sub	fp, ip, #4	; 0x4
     7e0:	e3a02001 	mov	r2, #1	; 0x1
     7e4:	e24dd008 	sub	sp, sp, #8	; 0x8
     7e8:	e3a00000 	mov	r0, #0	; 0x0
     7ec:	e5c32000 	strb	r2, [r3]
     7f0:	e5c92000 	strb	r2, [r9]
     7f4:	e50b002c 	str	r0, [fp, #-44]
     7f8:	e3a0a014 	mov	sl, #20	; 0x14
     7fc:	e59f5f88 	ldr	r5, [pc, #3976]	; 0x178c
     800:	e3a08001 	mov	r8, #1	; 0x1
     804:	e5c58000 	strb	r8, [r5]
     808:	e5d51000 	ldrb	r1, [r5]
     80c:	e3510000 	cmp	r1, #0	; 0x0
     810:	e1a02005 	mov	r2, r5
     814:	0a00002d 	beq	0x8d0
     818:	e59f6f74 	ldr	r6, [pc, #3956]	; 0x1794
     81c:	e59f7f6c 	ldr	r7, [pc, #3948]	; 0x1790
     820:	e1a08005 	mov	r8, r5
     824:	ea000014 	b	0x87c
     828:	e3540002 	cmp	r4, #2	; 0x2
     82c:	0a000020 	beq	0x8b4
     830:	e3540000 	cmp	r4, #0	; 0x0
     834:	024a3001 	subeq	r3, sl, #1	; 0x1
     838:	0203a0ff 	andeq	sl, r3, #255	; 0xff
     83c:	e35a0000 	cmp	sl, #0	; 0x0
     840:	13540001 	cmpne	r4, #1	; 0x1
     844:	0a00001d 	beq	0x8c0
     848:	e59f3f48 	ldr	r3, [pc, #3912]	; 0x1798
     84c:	e5930000 	ldr	r0, [r3]
     850:	e3500000 	cmp	r0, #0	; 0x0
     854:	1a000004 	bne	0x86c
     858:	e5d71000 	ldrb	r1, [r7]
     85c:	e3510000 	cmp	r1, #0	; 0x0
     860:	02811001 	addeq	r1, r1, #1	; 0x1
     864:	05c71000 	streqb	r1, [r7]
     868:	05c81000 	streqb	r1, [r8]
     86c:	e5d5c000 	ldrb	ip, [r5]
     870:	e35c0000 	cmp	ip, #0	; 0x0
     874:	e59f2f10 	ldr	r2, [pc, #3856]	; 0x178c
     878:	0a000014 	beq	0x8d0
     87c:	e3a0e000 	mov	lr, #0	; 0x0
     880:	e5c2e000 	strb	lr, [r2]
     884:	e5d94000 	ldrb	r4, [r9]
     888:	e5c9e000 	strb	lr, [r9]
     88c:	ebfffddf 	bl	0x10
     890:	e5962000 	ldr	r2, [r6]
     894:	e282c003 	add	ip, r2, #3	; 0x3
     898:	e15c0000 	cmp	ip, r0
     89c:	caffffe1 	bgt	0x828
     8a0:	e5860000 	str	r0, [r6]
     8a4:	eb000f5e 	bl	0x4624
     8a8:	eb000fd7 	bl	0x480c
     8ac:	e3540002 	cmp	r4, #2	; 0x2
     8b0:	1affffde 	bne	0x830
     8b4:	e24bd028 	sub	sp, fp, #40	; 0x28
     8b8:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
     8bc:	e12fff1e 	bx	lr
     8c0:	e3a0a014 	mov	sl, #20	; 0x14
     8c4:	eb0007a9 	bl	0x2770
     8c8:	ebfffdd5 	bl	0x24
     8cc:	eaffffdd 	b	0x848
     8d0:	e59f8ec4 	ldr	r8, [pc, #3780]	; 0x179c
     8d4:	e51b202c 	ldr	r2, [fp, #-44]
     8d8:	e5981000 	ldr	r1, [r8]
     8dc:	e3a03983 	mov	r3, #2146304	; 0x20c000
     8e0:	e2833a03 	add	r3, r3, #12288	; 0x3000
     8e4:	e282c001 	add	ip, r2, #1	; 0x1
     8e8:	e50bc02c 	str	ip, [fp, #-44]
     8ec:	e5831004 	str	r1, [r3, #4]
     8f0:	e51b002c 	ldr	r0, [fp, #-44]
     8f4:	e5d12000 	ldrb	r2, [r1]
     8f8:	e35000c9 	cmp	r0, #201	; 0xc9
     8fc:	e5c32000 	strb	r2, [r3]
     900:	0affffeb 	beq	0x8b4
     904:	e4d10001 	ldrb	r0, [r1], #1
     908:	e5881000 	str	r1, [r8]
     90c:	e35000c7 	cmp	r0, #199	; 0xc7
     910:	979ff100 	ldrls	pc, [pc, r0, lsl #2]
     914:	ea00024a 	b	0x1244
     918:	002027fc 	streqd	r2, [r0], -ip
     91c:	00203178 	eoreq	r3, r0, r8, ror r1
     920:	00203194 	mlaeq	r0, r4, r1, r3
     924:	00203194 	mlaeq	r0, r4, r1, r3
     928:	00203194 	mlaeq	r0, r4, r1, r3
     92c:	00203194 	mlaeq	r0, r4, r1, r3
     930:	00203194 	mlaeq	r0, r4, r1, r3
     934:	00203194 	mlaeq	r0, r4, r1, r3
     938:	00203194 	mlaeq	r0, r4, r1, r3
     93c:	002031b8 	streqh	r3, [r0], -r8
     940:	002031b8 	streqh	r3, [r0], -r8
     944:	002031ec 	eoreq	r3, r0, ip, ror #3
     948:	00203244 	eoreq	r3, r0, r4, asr #4
     94c:	00203244 	eoreq	r3, r0, r4, asr #4
     950:	00203208 	eoreq	r3, r0, r8, lsl #4
     954:	00203244 	eoreq	r3, r0, r4, asr #4
     958:	00203274 	eoreq	r3, r0, r4, ror r2
     95c:	00203298 	mlaeq	r0, r8, r2, r3
     960:	002032d0 	ldreqd	r3, [r0], -r0
     964:	00203244 	eoreq	r3, r0, r4, asr #4
     968:	002033b4 	streqh	r3, [r0], -r4
     96c:	00203f04 	eoreq	r3, r0, r4, lsl #30
     970:	00203f34 	eoreq	r3, r0, r4, lsr pc
     974:	00203f04 	eoreq	r3, r0, r4, lsl #30
     978:	00203f34 	eoreq	r3, r0, r4, lsr pc
     97c:	00203f7c 	eoreq	r3, r0, ip, ror pc
     980:	00203fac 	eoreq	r3, r0, ip, lsr #31
     984:	00203fac 	eoreq	r3, r0, ip, lsr #31
     988:	00203fac 	eoreq	r3, r0, ip, lsr #31
     98c:	00203fac 	eoreq	r3, r0, ip, lsr #31
     990:	00203b48 	eoreq	r3, r0, r8, asr #22
     994:	00203b48 	eoreq	r3, r0, r8, asr #22
     998:	00203b48 	eoreq	r3, r0, r8, asr #22
     99c:	00203b48 	eoreq	r3, r0, r8, asr #22
     9a0:	00203b9c 	mlaeq	r0, ip, fp, r3
     9a4:	00203b9c 	mlaeq	r0, ip, fp, r3
     9a8:	00203b9c 	mlaeq	r0, ip, fp, r3
     9ac:	00203b9c 	mlaeq	r0, ip, fp, r3
     9b0:	00203bcc 	eoreq	r3, r0, ip, asr #23
     9b4:	00203bcc 	eoreq	r3, r0, ip, asr #23
     9b8:	00203bcc 	eoreq	r3, r0, ip, asr #23
     9bc:	00203bcc 	eoreq	r3, r0, ip, asr #23
     9c0:	00203be0 	eoreq	r3, r0, r0, ror #23
     9c4:	00203be0 	eoreq	r3, r0, r0, ror #23
     9c8:	00203be0 	eoreq	r3, r0, r0, ror #23
     9cc:	00203be0 	eoreq	r3, r0, r0, ror #23
     9d0:	00203c10 	eoreq	r3, r0, r0, lsl ip
     9d4:	00203c98 	mlaeq	r0, r8, ip, r3
     9d8:	00203c10 	eoreq	r3, r0, r0, lsl ip
     9dc:	00203c98 	mlaeq	r0, r8, ip, r3
     9e0:	00203d50 	eoreq	r3, r0, r0, asr sp
     9e4:	00203fdc 	ldreqd	r3, [r0], -ip
     9e8:	00203a1c 	eoreq	r3, r0, ip, lsl sl
     9ec:	00203a1c 	eoreq	r3, r0, ip, lsl sl
     9f0:	00203aa4 	eoreq	r3, r0, r4, lsr #21
     9f4:	00203ad0 	ldreqd	r3, [r0], -r0
     9f8:	00203aa4 	eoreq	r3, r0, r4, lsr #21
     9fc:	00203ad0 	ldreqd	r3, [r0], -r0
     a00:	00203b1c 	eoreq	r3, r0, ip, lsl fp
     a04:	00204064 	eoreq	r4, r0, r4, rrx
     a08:	00204064 	eoreq	r4, r0, r4, rrx
     a0c:	00204064 	eoreq	r4, r0, r4, rrx
     a10:	00204064 	eoreq	r4, r0, r4, rrx
     a14:	002042a8 	eoreq	r4, r0, r8, lsr #5
     a18:	002042a8 	eoreq	r4, r0, r8, lsr #5
     a1c:	002042a8 	eoreq	r4, r0, r8, lsr #5
     a20:	002042a8 	eoreq	r4, r0, r8, lsr #5
     a24:	00204094 	mlaeq	r0, r4, r0, r4
     a28:	00204094 	mlaeq	r0, r4, r0, r4
     a2c:	00204094 	mlaeq	r0, r4, r0, r4
     a30:	00204094 	mlaeq	r0, r4, r0, r4
     a34:	002040c4 	eoreq	r4, r0, r4, asr #1
     a38:	002040c4 	eoreq	r4, r0, r4, asr #1
     a3c:	002040c4 	eoreq	r4, r0, r4, asr #1
     a40:	002040c4 	eoreq	r4, r0, r4, asr #1
     a44:	00203dd8 	ldreqd	r3, [r0], -r8
     a48:	00203dd8 	ldreqd	r3, [r0], -r8
     a4c:	00203dd8 	ldreqd	r3, [r0], -r8
     a50:	00203dd8 	ldreqd	r3, [r0], -r8
     a54:	002042bc 	streqh	r4, [r0], -ip
     a58:	00203e08 	eoreq	r3, r0, r8, lsl #28
     a5c:	002042bc 	streqh	r4, [r0], -ip
     a60:	00203e08 	eoreq	r3, r0, r8, lsl #28
     a64:	00203ee4 	eoreq	r3, r0, r4, ror #29
     a68:	00204118 	eoreq	r4, r0, r8, lsl r1
     a6c:	002041cc 	eoreq	r4, r0, ip, asr #3
     a70:	002041cc 	eoreq	r4, r0, ip, asr #3
     a74:	00204280 	eoreq	r4, r0, r0, lsl #5
     a78:	00204294 	mlaeq	r0, r4, r2, r4
     a7c:	00203458 	eoreq	r3, r0, r8, asr r4
     a80:	00203474 	eoreq	r3, r0, r4, ror r4
     a84:	002034a0 	eoreq	r3, r0, r0, lsr #9
     a88:	002034d4 	ldreqd	r3, [r0], -r4
     a8c:	002034f4 	streqd	r3, [r0], -r4
     a90:	0020352c 	eoreq	r3, r0, ip, lsr #10
     a94:	0020356c 	eoreq	r3, r0, ip, ror #10
     a98:	00203594 	mlaeq	r0, r4, r5, r3
     a9c:	00203244 	eoreq	r3, r0, r4, asr #4
     aa0:	00203244 	eoreq	r3, r0, r4, asr #4
     aa4:	00203244 	eoreq	r3, r0, r4, asr #4
     aa8:	002035c0 	eoreq	r3, r0, r0, asr #11
     aac:	00203244 	eoreq	r3, r0, r4, asr #4
     ab0:	00203244 	eoreq	r3, r0, r4, asr #4
     ab4:	00203244 	eoreq	r3, r0, r4, asr #4
     ab8:	002035d8 	ldreqd	r3, [r0], -r8
     abc:	00203244 	eoreq	r3, r0, r4, asr #4
     ac0:	00203244 	eoreq	r3, r0, r4, asr #4
     ac4:	00203244 	eoreq	r3, r0, r4, asr #4
     ac8:	00203244 	eoreq	r3, r0, r4, asr #4
     acc:	00203244 	eoreq	r3, r0, r4, asr #4
     ad0:	00203244 	eoreq	r3, r0, r4, asr #4
     ad4:	00203244 	eoreq	r3, r0, r4, asr #4
     ad8:	00203244 	eoreq	r3, r0, r4, asr #4
     adc:	00203244 	eoreq	r3, r0, r4, asr #4
     ae0:	00203244 	eoreq	r3, r0, r4, asr #4
     ae4:	00203244 	eoreq	r3, r0, r4, asr #4
     ae8:	00203604 	eoreq	r3, r0, r4, lsl #12
     aec:	00203244 	eoreq	r3, r0, r4, asr #4
     af0:	00203244 	eoreq	r3, r0, r4, asr #4
     af4:	00203244 	eoreq	r3, r0, r4, asr #4
     af8:	0020361c 	eoreq	r3, r0, ip, lsl r6
     afc:	00203244 	eoreq	r3, r0, r4, asr #4
     b00:	0020364c 	eoreq	r3, r0, ip, asr #12
     b04:	00203244 	eoreq	r3, r0, r4, asr #4
     b08:	0020367c 	eoreq	r3, r0, ip, ror r6
     b0c:	00203244 	eoreq	r3, r0, r4, asr #4
     b10:	002036ac 	eoreq	r3, r0, ip, lsr #13
     b14:	00203244 	eoreq	r3, r0, r4, asr #4
     b18:	002036d8 	ldreqd	r3, [r0], -r8
     b1c:	00203244 	eoreq	r3, r0, r4, asr #4
     b20:	00203704 	eoreq	r3, r0, r4, lsl #14
     b24:	00203244 	eoreq	r3, r0, r4, asr #4
     b28:	00203730 	eoreq	r3, r0, r0, lsr r7
     b2c:	0020375c 	eoreq	r3, r0, ip, asr r7
     b30:	00203244 	eoreq	r3, r0, r4, asr #4
     b34:	00203244 	eoreq	r3, r0, r4, asr #4
     b38:	002037fc 	streqd	r3, [r0], -ip
     b3c:	00203244 	eoreq	r3, r0, r4, asr #4
     b40:	00203244 	eoreq	r3, r0, r4, asr #4
     b44:	00203244 	eoreq	r3, r0, r4, asr #4
     b48:	00203244 	eoreq	r3, r0, r4, asr #4
     b4c:	002037d8 	ldreqd	r3, [r0], -r8
     b50:	00203244 	eoreq	r3, r0, r4, asr #4
     b54:	00203244 	eoreq	r3, r0, r4, asr #4
     b58:	002037fc 	streqd	r3, [r0], -ip
     b5c:	00203820 	eoreq	r3, r0, r0, lsr #16
     b60:	00203834 	eoreq	r3, r0, r4, lsr r8
     b64:	00203834 	eoreq	r3, r0, r4, lsr r8
     b68:	00203244 	eoreq	r3, r0, r4, asr #4
     b6c:	00203244 	eoreq	r3, r0, r4, asr #4
     b70:	00203244 	eoreq	r3, r0, r4, asr #4
     b74:	00203244 	eoreq	r3, r0, r4, asr #4
     b78:	00203244 	eoreq	r3, r0, r4, asr #4
     b7c:	00203128 	eoreq	r3, r0, r8, lsr #2
     b80:	00203170 	eoreq	r3, r0, r0, ror r1
     b84:	00203848 	eoreq	r3, r0, r8, asr #16
     b88:	00203890 	mlaeq	r0, r0, r8, r3
     b8c:	002038d8 	ldreqd	r3, [r0], -r8
     b90:	00203920 	eoreq	r3, r0, r0, lsr #18
     b94:	002039f8 	streqd	r3, [r0], -r8
     b98:	00202c78 	eoreq	r2, r0, r8, ror ip
     b9c:	00203968 	eoreq	r3, r0, r8, ror #18
     ba0:	0020398c 	eoreq	r3, r0, ip, lsl #19
     ba4:	002039b0 	streqh	r3, [r0], -r0
     ba8:	002039d4 	ldreqd	r3, [r0], -r4
     bac:	002039f8 	streqd	r3, [r0], -r8
     bb0:	00202c78 	eoreq	r2, r0, r8, ror ip
     bb4:	00202c54 	eoreq	r2, r0, r4, asr ip
     bb8:	00202c38 	eoreq	r2, r0, r8, lsr ip
     bbc:	00202cdc 	ldreqd	r2, [r0], -ip
     bc0:	00203244 	eoreq	r3, r0, r4, asr #4
     bc4:	00203244 	eoreq	r3, r0, r4, asr #4
     bc8:	00202cf8 	streqd	r2, [r0], -r8
     bcc:	00202cf8 	streqd	r2, [r0], -r8
     bd0:	00202cf8 	streqd	r2, [r0], -r8
     bd4:	00202cf8 	streqd	r2, [r0], -r8
     bd8:	00202cf8 	streqd	r2, [r0], -r8
     bdc:	00202d20 	eoreq	r2, r0, r0, lsr #26
     be0:	00202d2c 	eoreq	r2, r0, ip, lsr #26
     be4:	00202d2c 	eoreq	r2, r0, ip, lsr #26
     be8:	00202e04 	eoreq	r2, r0, r4, lsl #28
     bec:	00202e8c 	eoreq	r2, r0, ip, lsl #29
     bf0:	00202f3c 	eoreq	r2, r0, ip, lsr pc
     bf4:	00202f70 	eoreq	r2, r0, r0, ror pc
     bf8:	00202f70 	eoreq	r2, r0, r0, ror pc
     bfc:	00203244 	eoreq	r3, r0, r4, asr #4
     c00:	00203244 	eoreq	r3, r0, r4, asr #4
     c04:	00202f8c 	eoreq	r2, r0, ip, lsl #31
     c08:	00202fd4 	ldreqd	r2, [r0], -r4
     c0c:	00203244 	eoreq	r3, r0, r4, asr #4
     c10:	00202ffc 	streqd	r2, [r0], -ip
     c14:	00203030 	eoreq	r3, r0, r0, lsr r0
     c18:	00203060 	eoreq	r3, r0, r0, rrx
     c1c:	00203098 	mlaeq	r0, r8, r0, r3
     c20:	00203254 	eoreq	r3, r0, r4, asr r2
     c24:	00203224 	eoreq	r3, r0, r4, lsr #4
     c28:	00203244 	eoreq	r3, r0, r4, asr #4
     c2c:	002030c8 	eoreq	r3, r0, r8, asr #1
     c30:	00203128 	eoreq	r3, r0, r8, lsr #2
     c34:	00203170 	eoreq	r3, r0, r0, ror r1
     c38:	e59f0b94 	ldr	r0, [pc, #2964]	; 0x17d4
     c3c:	e5982000 	ldr	r2, [r8]
     c40:	e5901000 	ldr	r1, [r0]
     c44:	e2825002 	add	r5, r2, #2	; 0x2
     c48:	e2813004 	add	r3, r1, #4	; 0x4
     c4c:	e5803000 	str	r3, [r0]
     c50:	e5815004 	str	r5, [r1, #4]
     c54:	e5985000 	ldr	r5, [r8]
     c58:	e5d5c000 	ldrb	ip, [r5]
     c5c:	e5d52001 	ldrb	r2, [r5, #1]
     c60:	e182340c 	orr	r3, r2, ip, lsl #8
     c64:	e1a00803 	mov	r0, r3, lsl #16
     c68:	e0851840 	add	r1, r5, r0, asr #16
     c6c:	e241c001 	sub	ip, r1, #1	; 0x1
     c70:	e588c000 	str	ip, [r8]
     c74:	eafffee0 	b	0x7fc
     c78:	e59fcb54 	ldr	ip, [pc, #2900]	; 0x17d4
     c7c:	e59c2000 	ldr	r2, [ip]
     c80:	e1a05002 	mov	r5, r2
     c84:	e4150004 	ldr	r0, [r5], #-4
     c88:	e5121004 	ldr	r1, [r2, #-4]
     c8c:	e0603001 	rsb	r3, r0, r1
     c90:	e5023004 	str	r3, [r2, #-4]
     c94:	e58c5000 	str	r5, [ip]
     c98:	e59c5000 	ldr	r5, [ip]
     c9c:	e4152004 	ldr	r2, [r5], #-4
     ca0:	e3520000 	cmp	r2, #0	; 0x0
     ca4:	05983000 	ldreq	r3, [r8]
     ca8:	02833002 	addeq	r3, r3, #2	; 0x2
     cac:	e58c5000 	str	r5, [ip]
     cb0:	05883000 	streq	r3, [r8]
     cb4:	0afffed0 	beq	0x7fc
     cb8:	e5980000 	ldr	r0, [r8]
     cbc:	e5d0c000 	ldrb	ip, [r0]
     cc0:	e5d02001 	ldrb	r2, [r0, #1]
     cc4:	e182540c 	orr	r5, r2, ip, lsl #8
     cc8:	e1a01805 	mov	r1, r5, lsl #16
     ccc:	e0803841 	add	r3, r0, r1, asr #16
     cd0:	e243c001 	sub	ip, r3, #1	; 0x1
     cd4:	e588c000 	str	ip, [r8]
     cd8:	eafffec7 	b	0x7fc
     cdc:	e5982000 	ldr	r2, [r8]
     ce0:	e59f3acc 	ldr	r3, [pc, #2764]	; 0x17b4
     ce4:	e5d20000 	ldrb	r0, [r2]
     ce8:	e5935000 	ldr	r5, [r3]
     cec:	e7951100 	ldr	r1, [r5, r0, lsl #2]
     cf0:	e5881000 	str	r1, [r8]
     cf4:	eafffec0 	b	0x7fc
     cf8:	e5981000 	ldr	r1, [r8]
     cfc:	e5510001 	ldrb	r0, [r1, #-1]
     d00:	e24080ac 	sub	r8, r0, #172	; 0xac
     d04:	e088cfa8 	add	ip, r8, r8, lsr #31
     d08:	e3cc3001 	bic	r3, ip, #1	; 0x1
     d0c:	e0635008 	rsb	r5, r3, r8
     d10:	e2852001 	add	r2, r5, #1	; 0x1
     d14:	e20200ff 	and	r0, r2, #255	; 0xff
     d18:	eb000de7 	bl	0x44bc
     d1c:	eafffeb6 	b	0x7fc
     d20:	e3a00000 	mov	r0, #0	; 0x0
     d24:	eb000de4 	bl	0x44bc
     d28:	eafffeb3 	b	0x7fc
     d2c:	e5981000 	ldr	r1, [r8]
     d30:	e59f4a68 	ldr	r4, [pc, #2664]	; 0x17a0
     d34:	e4510001 	ldrb	r0, [r1], #-1
     d38:	e594e000 	ldr	lr, [r4]
     d3c:	e0802100 	add	r2, r0, r0, lsl #2
     d40:	e08ec082 	add	ip, lr, r2, lsl #1
     d44:	e28c0010 	add	r0, ip, #16	; 0x10
     d48:	eb000d5d 	bl	0x42c4
     d4c:	e210c0ff 	ands	ip, r0, #255	; 0xff
     d50:	1afffea9 	bne	0x7fc
     d54:	e5940000 	ldr	r0, [r4]
     d58:	e5986000 	ldr	r6, [r8]
     d5c:	e1d0e0b4 	ldrh	lr, [r0, #4]
     d60:	e5d63001 	ldrb	r3, [r6, #1]
     d64:	e08e1083 	add	r1, lr, r3, lsl #1
     d68:	e19150b0 	ldrh	r5, [r1, r0]
     d6c:	e59f4a30 	ldr	r4, [pc, #2608]	; 0x17a4
     d70:	e1a02805 	mov	r2, r5, lsl #16
     d74:	e7d45e22 	ldrb	r5, [r4, r2, lsr #28]
     d78:	e3550004 	cmp	r5, #4	; 0x4
     d7c:	e1a03822 	mov	r3, r2, lsr #16
     d80:	83a02001 	movhi	r2, #1	; 0x1
     d84:	e50bc030 	str	ip, [fp, #-48]
     d88:	850b2030 	strhi	r2, [fp, #-48]
     d8c:	e1d0e0b6 	ldrh	lr, [r0, #6]
     d90:	e5561001 	ldrb	r1, [r6, #-1]
     d94:	e1a04a03 	mov	r4, r3, lsl #20
     d98:	83a05004 	movhi	r5, #4	; 0x4
     d9c:	e08ec000 	add	ip, lr, r0
     da0:	e1a06a24 	mov	r6, r4, lsr #20
     da4:	e35100b2 	cmp	r1, #178	; 0xb2
     da8:	e08c4006 	add	r4, ip, r6
     dac:	0a0005a3 	beq	0x2440
     db0:	e51b6030 	ldr	r6, [fp, #-48]
     db4:	e3560000 	cmp	r6, #0	; 0x0
     db8:	059f6a14 	ldreq	r6, [pc, #2580]	; 0x17d4
     dbc:	0a000006 	beq	0xddc
     dc0:	e59f6a0c 	ldr	r6, [pc, #2572]	; 0x17d4
     dc4:	e596e000 	ldr	lr, [r6]
     dc8:	e41e2004 	ldr	r2, [lr], #-4
     dcc:	e2840004 	add	r0, r4, #4	; 0x4
     dd0:	e586e000 	str	lr, [r6]
     dd4:	e3a01004 	mov	r1, #4	; 0x4
     dd8:	eb000bd8 	bl	0x3d40
     ddc:	e5960000 	ldr	r0, [r6]
     de0:	e4102004 	ldr	r2, [r0], #-4
     de4:	e1a01005 	mov	r1, r5
     de8:	e5860000 	str	r0, [r6]
     dec:	e1a00004 	mov	r0, r4
     df0:	eb000bd2 	bl	0x3d40
     df4:	e5985000 	ldr	r5, [r8]
     df8:	e2852002 	add	r2, r5, #2	; 0x2
     dfc:	e5882000 	str	r2, [r8]
     e00:	eafffe7d 	b	0x7fc
     e04:	e59f79c8 	ldr	r7, [pc, #2504]	; 0x17d4
     e08:	e5973000 	ldr	r3, [r7]
     e0c:	e593c000 	ldr	ip, [r3]
     e10:	e59f69b8 	ldr	r6, [pc, #2488]	; 0x17d0
     e14:	e35c0000 	cmp	ip, #0	; 0x0
     e18:	e586c000 	str	ip, [r6]
     e1c:	0a000572 	beq	0x23ec
     e20:	e5981000 	ldr	r1, [r8]
     e24:	e5d10000 	ldrb	r0, [r1]
     e28:	e5d15001 	ldrb	r5, [r1, #1]
     e2c:	e59f2970 	ldr	r2, [pc, #2416]	; 0x17a4
     e30:	e200300f 	and	r3, r0, #15	; 0xf
     e34:	e7d2e220 	ldrb	lr, [r2, r0, lsr #4]
     e38:	e1858403 	orr	r8, r5, r3, lsl #8
     e3c:	e088500c 	add	r5, r8, ip
     e40:	e35e0004 	cmp	lr, #4	; 0x4
     e44:	31a0100e 	movcc	r1, lr
     e48:	23a01004 	movcs	r1, #4	; 0x4
     e4c:	e1a00005 	mov	r0, r5
     e50:	e1a02006 	mov	r2, r6
     e54:	e35e0004 	cmp	lr, #4	; 0x4
     e58:	93a04000 	movls	r4, #0	; 0x0
     e5c:	83a04001 	movhi	r4, #1	; 0x1
     e60:	eb000bc4 	bl	0x3d78
     e64:	e596c000 	ldr	ip, [r6]
     e68:	e5971000 	ldr	r1, [r7]
     e6c:	e3540000 	cmp	r4, #0	; 0x0
     e70:	e581c000 	str	ip, [r1]
     e74:	e59f4920 	ldr	r4, [pc, #2336]	; 0x179c
     e78:	1a000566 	bne	0x2418
     e7c:	e5945000 	ldr	r5, [r4]
     e80:	e2850002 	add	r0, r5, #2	; 0x2
     e84:	e5840000 	str	r0, [r4]
     e88:	eafffe5b 	b	0x7fc
     e8c:	e5982000 	ldr	r2, [r8]
     e90:	e59f790c 	ldr	r7, [pc, #2316]	; 0x17a4
     e94:	e5d2c000 	ldrb	ip, [r2]
     e98:	e7d7022c 	ldrb	r0, [r7, ip, lsr #4]
     e9c:	e3500004 	cmp	r0, #4	; 0x4
     ea0:	93a0c000 	movls	ip, #0	; 0x0
     ea4:	83a0c001 	movhi	ip, #1	; 0x1
     ea8:	e3500005 	cmp	r0, #5	; 0x5
     eac:	159f5920 	ldrne	r5, [pc, #2336]	; 0x17d4
     eb0:	059f591c 	ldreq	r5, [pc, #2332]	; 0x17d4
     eb4:	15953000 	ldrne	r3, [r5]
     eb8:	05953000 	ldreq	r3, [r5]
     ebc:	12433008 	subne	r3, r3, #8	; 0x8
     ec0:	02433004 	subeq	r3, r3, #4	; 0x4
     ec4:	e59fe904 	ldr	lr, [pc, #2308]	; 0x17d0
     ec8:	e5933000 	ldr	r3, [r3]
     ecc:	e58e3000 	str	r3, [lr]
     ed0:	31a07000 	movcc	r7, r0
     ed4:	23a07004 	movcs	r7, #4	; 0x4
     ed8:	e59e0000 	ldr	r0, [lr]
     edc:	e3500000 	cmp	r0, #0	; 0x0
     ee0:	0a00053d 	beq	0x23dc
     ee4:	e5983000 	ldr	r3, [r8]
     ee8:	e5d31000 	ldrb	r1, [r3]
     eec:	e5d34001 	ldrb	r4, [r3, #1]
     ef0:	e201e00f 	and	lr, r1, #15	; 0xf
     ef4:	e184640e 	orr	r6, r4, lr, lsl #8
     ef8:	e35c0000 	cmp	ip, #0	; 0x0
     efc:	e0864000 	add	r4, r6, r0
     f00:	e59f6894 	ldr	r6, [pc, #2196]	; 0x179c
     f04:	1a00053c 	bne	0x23fc
     f08:	e595e000 	ldr	lr, [r5]
     f0c:	e41e2004 	ldr	r2, [lr], #-4
     f10:	e1a01007 	mov	r1, r7
     f14:	e585e000 	str	lr, [r5]
     f18:	e1a00004 	mov	r0, r4
     f1c:	eb000b87 	bl	0x3d40
     f20:	e5951000 	ldr	r1, [r5]
     f24:	e5962000 	ldr	r2, [r6]
     f28:	e2418004 	sub	r8, r1, #4	; 0x4
     f2c:	e282c002 	add	ip, r2, #2	; 0x2
     f30:	e5858000 	str	r8, [r5]
     f34:	e586c000 	str	ip, [r6]
     f38:	eafffe2f 	b	0x7fc
     f3c:	e598e000 	ldr	lr, [r8]
     f40:	e59f188c 	ldr	r1, [pc, #2188]	; 0x17d4
     f44:	e5de8000 	ldrb	r8, [lr]
     f48:	e591c000 	ldr	ip, [r1]
     f4c:	e1a00228 	mov	r0, r8, lsr #4
     f50:	e5de3001 	ldrb	r3, [lr, #1]
     f54:	e04c2100 	sub	r2, ip, r0, lsl #2
     f58:	e208500f 	and	r5, r8, #15	; 0xf
     f5c:	e5920000 	ldr	r0, [r2]
     f60:	e1831405 	orr	r1, r3, r5, lsl #8
     f64:	e28e2002 	add	r2, lr, #2	; 0x2
     f68:	eb000c80 	bl	0x4170
     f6c:	eafffe22 	b	0x7fc
     f70:	e598e000 	ldr	lr, [r8]
     f74:	e24e3001 	sub	r3, lr, #1	; 0x1
     f78:	e5de0000 	ldrb	r0, [lr]
     f7c:	e5de1001 	ldrb	r1, [lr, #1]
     f80:	e28e2002 	add	r2, lr, #2	; 0x2
     f84:	eb000d04 	bl	0x439c
     f88:	eafffe1b 	b	0x7fc
     f8c:	e5985000 	ldr	r5, [r8]
     f90:	e2451001 	sub	r1, r5, #1	; 0x1
     f94:	e5d50001 	ldrb	r0, [r5, #1]
     f98:	eb000a6e 	bl	0x3958
     f9c:	e59f181c 	ldr	r1, [pc, #2076]	; 0x17c0
     fa0:	e3500000 	cmp	r0, #0	; 0x0
     fa4:	e1a0c000 	mov	ip, r0
     fa8:	e5810000 	str	r0, [r1]
     fac:	0afffe12 	beq	0x7fc
     fb0:	e59f181c 	ldr	r1, [pc, #2076]	; 0x17d4
     fb4:	e5985000 	ldr	r5, [r8]
     fb8:	e5910000 	ldr	r0, [r1]
     fbc:	e2852002 	add	r2, r5, #2	; 0x2
     fc0:	e2803004 	add	r3, r0, #4	; 0x4
     fc4:	e5813000 	str	r3, [r1]
     fc8:	e580c004 	str	ip, [r0, #4]
     fcc:	e5882000 	str	r2, [r8]
     fd0:	eafffe09 	b	0x7fc
     fd4:	e598e000 	ldr	lr, [r8]
     fd8:	e59f47f4 	ldr	r4, [pc, #2036]	; 0x17d4
     fdc:	e4de0001 	ldrb	r0, [lr], #1
     fe0:	e594c000 	ldr	ip, [r4]
     fe4:	e588e000 	str	lr, [r8]
     fe8:	e59c1000 	ldr	r1, [ip]
     fec:	eb00096b 	bl	0x35a0
     ff0:	e5948000 	ldr	r8, [r4]
     ff4:	e5880000 	str	r0, [r8]
     ff8:	eafffdff 	b	0x7fc
     ffc:	e59f37d0 	ldr	r3, [pc, #2000]	; 0x17d4
    1000:	e5938000 	ldr	r8, [r3]
    1004:	e598c000 	ldr	ip, [r8]
    1008:	e35c0000 	cmp	ip, #0	; 0x0
    100c:	11dcc0b0 	ldrneh	ip, [ip]
    1010:	11a0cb8c 	movne	ip, ip, lsl #23
    1014:	11a0cbac 	movne	ip, ip, lsr #23
    1018:	1588c000 	strne	ip, [r8]
    101c:	1afffdf6 	bne	0x7fc
    1020:	e59fe7a4 	ldr	lr, [pc, #1956]	; 0x17cc
    1024:	e59e0000 	ldr	r0, [lr]
    1028:	eb000806 	bl	0x3048
    102c:	eafffdf2 	b	0x7fc
    1030:	e59fe79c 	ldr	lr, [pc, #1948]	; 0x17d4
    1034:	e59ec000 	ldr	ip, [lr]
    1038:	e41c0004 	ldr	r0, [ip], #-4
    103c:	e59f878c 	ldr	r8, [pc, #1932]	; 0x17d0
    1040:	e3500000 	cmp	r0, #0	; 0x0
    1044:	e58ec000 	str	ip, [lr]
    1048:	e5880000 	str	r0, [r8]
    104c:	1a0004c7 	bne	0x2370
    1050:	e59f1774 	ldr	r1, [pc, #1908]	; 0x17cc
    1054:	e5910000 	ldr	r0, [r1]
    1058:	eb0007fa 	bl	0x3048
    105c:	eafffde6 	b	0x7fc
    1060:	e59f376c 	ldr	r3, [pc, #1900]	; 0x17d4
    1064:	e5931000 	ldr	r1, [r3]
    1068:	e5910000 	ldr	r0, [r1]
    106c:	e5981000 	ldr	r1, [r8]
    1070:	e59f5758 	ldr	r5, [pc, #1880]	; 0x17d0
    1074:	e281c001 	add	ip, r1, #1	; 0x1
    1078:	e3500000 	cmp	r0, #0	; 0x0
    107c:	e588c000 	str	ip, [r8]
    1080:	e5850000 	str	r0, [r5]
    1084:	1a0004cc 	bne	0x23bc
    1088:	e5980000 	ldr	r0, [r8]
    108c:	e2802001 	add	r2, r0, #1	; 0x1
    1090:	e5882000 	str	r2, [r8]
    1094:	eafffdd8 	b	0x7fc
    1098:	e59f4734 	ldr	r4, [pc, #1844]	; 0x17d4
    109c:	e5980000 	ldr	r0, [r8]
    10a0:	e5942000 	ldr	r2, [r4]
    10a4:	e5d01001 	ldrb	r1, [r0, #1]
    10a8:	e5920000 	ldr	r0, [r2]
    10ac:	eb000d35 	bl	0x4588
    10b0:	e5983000 	ldr	r3, [r8]
    10b4:	e594c000 	ldr	ip, [r4]
    10b8:	e2835002 	add	r5, r3, #2	; 0x2
    10bc:	e58c0000 	str	r0, [ip]
    10c0:	e5885000 	str	r5, [r8]
    10c4:	eafffdcc 	b	0x7fc
    10c8:	e598e000 	ldr	lr, [r8]
    10cc:	e5de3002 	ldrb	r3, [lr, #2]
    10d0:	e59f46e0 	ldr	r4, [pc, #1760]	; 0x17b8
    10d4:	e2435001 	sub	r5, r3, #1	; 0x1
    10d8:	e5c45000 	strb	r5, [r4]
    10dc:	e59f56f0 	ldr	r5, [pc, #1776]	; 0x17d4
    10e0:	e5d4c000 	ldrb	ip, [r4]
    10e4:	e5950000 	ldr	r0, [r5]
    10e8:	e5de2002 	ldrb	r2, [lr, #2]
    10ec:	e040310c 	sub	r3, r0, ip, lsl #2
    10f0:	e5de1001 	ldrb	r1, [lr, #1]
    10f4:	e5de0000 	ldrb	r0, [lr]
    10f8:	eb000ad7 	bl	0x3c5c
    10fc:	e5d42000 	ldrb	r2, [r4]
    1100:	e5951000 	ldr	r1, [r5]
    1104:	e598c000 	ldr	ip, [r8]
    1108:	e0411102 	sub	r1, r1, r2, lsl #2
    110c:	e59f26ac 	ldr	r2, [pc, #1708]	; 0x17c0
    1110:	e28c3003 	add	r3, ip, #3	; 0x3
    1114:	e5810000 	str	r0, [r1]
    1118:	e5883000 	str	r3, [r8]
    111c:	e5820000 	str	r0, [r2]
    1120:	e5851000 	str	r1, [r5]
    1124:	eafffdb4 	b	0x7fc
    1128:	e59fc6a4 	ldr	ip, [pc, #1700]	; 0x17d4
    112c:	e59c5000 	ldr	r5, [ip]
    1130:	e4151004 	ldr	r1, [r5], #-4
    1134:	e3510000 	cmp	r1, #0	; 0x0
    1138:	15983000 	ldrne	r3, [r8]
    113c:	12833002 	addne	r3, r3, #2	; 0x2
    1140:	e58c5000 	str	r5, [ip]
    1144:	15883000 	strne	r3, [r8]
    1148:	1afffdab 	bne	0x7fc
    114c:	e5980000 	ldr	r0, [r8]
    1150:	e5d0c001 	ldrb	ip, [r0, #1]
    1154:	e5d01000 	ldrb	r1, [r0]
    1158:	e18c5401 	orr	r5, ip, r1, lsl #8
    115c:	e1a02805 	mov	r2, r5, lsl #16
    1160:	e0803842 	add	r3, r0, r2, asr #16
    1164:	e243c001 	sub	ip, r3, #1	; 0x1
    1168:	e588c000 	str	ip, [r8]
    116c:	eafffda2 	b	0x7fc
    1170:	e59fc65c 	ldr	ip, [pc, #1628]	; 0x17d4
    1174:	eafffec7 	b	0xc98
    1178:	e59f5654 	ldr	r5, [pc, #1620]	; 0x17d4
    117c:	e5958000 	ldr	r8, [r5]
    1180:	e3a02000 	mov	r2, #0	; 0x0
    1184:	e2881004 	add	r1, r8, #4	; 0x4
    1188:	e5851000 	str	r1, [r5]
    118c:	e5882004 	str	r2, [r8, #4]
    1190:	eafffd99 	b	0x7fc
    1194:	e5985000 	ldr	r5, [r8]
    1198:	e59f0634 	ldr	r0, [pc, #1588]	; 0x17d4
    119c:	e5552001 	ldrb	r2, [r5, #-1]
    11a0:	e590c000 	ldr	ip, [r0]
    11a4:	e2428003 	sub	r8, r2, #3	; 0x3
    11a8:	e28c3004 	add	r3, ip, #4	; 0x4
    11ac:	e5803000 	str	r3, [r0]
    11b0:	e58c8004 	str	r8, [ip, #4]
    11b4:	eafffd90 	b	0x7fc
    11b8:	e59f1614 	ldr	r1, [pc, #1556]	; 0x17d4
    11bc:	e5915000 	ldr	r5, [r1]
    11c0:	e3a00000 	mov	r0, #0	; 0x0
    11c4:	e285c004 	add	ip, r5, #4	; 0x4
    11c8:	e5850004 	str	r0, [r5, #4]
    11cc:	e581c000 	str	ip, [r1]
    11d0:	e5983000 	ldr	r3, [r8]
    11d4:	e5532001 	ldrb	r2, [r3, #-1]
    11d8:	e28c0004 	add	r0, ip, #4	; 0x4
    11dc:	e2428009 	sub	r8, r2, #9	; 0x9
    11e0:	e5810000 	str	r0, [r1]
    11e4:	e58c8004 	str	r8, [ip, #4]
    11e8:	eafffd83 	b	0x7fc
    11ec:	e59f05e0 	ldr	r0, [pc, #1504]	; 0x17d4
    11f0:	e5903000 	ldr	r3, [r0]
    11f4:	e3a02000 	mov	r2, #0	; 0x0
    11f8:	e2835004 	add	r5, r3, #4	; 0x4
    11fc:	e5805000 	str	r5, [r0]
    1200:	e5832004 	str	r2, [r3, #4]
    1204:	eafffd7c 	b	0x7fc
    1208:	e59f05c4 	ldr	r0, [pc, #1476]	; 0x17d4
    120c:	e5908000 	ldr	r8, [r0]
    1210:	e3a01000 	mov	r1, #0	; 0x0
    1214:	e288c004 	add	ip, r8, #4	; 0x4
    1218:	e5881004 	str	r1, [r8, #4]
    121c:	e580c000 	str	ip, [r0]
    1220:	eafffff2 	b	0x11f0
    1224:	e59f85a8 	ldr	r8, [pc, #1448]	; 0x17d4
    1228:	e598e000 	ldr	lr, [r8]
    122c:	e59fc564 	ldr	ip, [pc, #1380]	; 0x1798
    1230:	e41e1004 	ldr	r1, [lr], #-4
    1234:	e59c0000 	ldr	r0, [ip]
    1238:	e588e000 	str	lr, [r8]
    123c:	eb00070d 	bl	0x2e78
    1240:	eafffd6d 	b	0x7fc
    1244:	e59fe55c 	ldr	lr, [pc, #1372]	; 0x17a8
    1248:	e59e0000 	ldr	r0, [lr]
    124c:	eb00077d 	bl	0x3048
    1250:	eafffd97 	b	0x8b4
    1254:	e59f5578 	ldr	r5, [pc, #1400]	; 0x17d4
    1258:	e5952000 	ldr	r2, [r5]
    125c:	e59f3534 	ldr	r3, [pc, #1332]	; 0x1798
    1260:	e4121004 	ldr	r1, [r2], #-4
    1264:	e5930000 	ldr	r0, [r3]
    1268:	e5852000 	str	r2, [r5]
    126c:	eb00051b 	bl	0x26e0
    1270:	eafffd61 	b	0x7fc
    1274:	e59fc558 	ldr	ip, [pc, #1368]	; 0x17d4
    1278:	e5983000 	ldr	r3, [r8]
    127c:	e59c5000 	ldr	r5, [ip]
    1280:	e0d310d1 	ldrsb	r1, [r3], #1
    1284:	e2852004 	add	r2, r5, #4	; 0x4
    1288:	e5883000 	str	r3, [r8]
    128c:	e58c2000 	str	r2, [ip]
    1290:	e5851004 	str	r1, [r5, #4]
    1294:	eafffd58 	b	0x7fc
    1298:	e5981000 	ldr	r1, [r8]
    129c:	e59fc530 	ldr	ip, [pc, #1328]	; 0x17d4
    12a0:	e5d12000 	ldrb	r2, [r1]
    12a4:	e5d10001 	ldrb	r0, [r1, #1]
    12a8:	e1803402 	orr	r3, r0, r2, lsl #8
    12ac:	e59c0000 	ldr	r0, [ip]
    12b0:	e1a05803 	mov	r5, r3, lsl #16
    12b4:	e1a03845 	mov	r3, r5, asr #16
    12b8:	e2802004 	add	r2, r0, #4	; 0x4
    12bc:	e2815002 	add	r5, r1, #2	; 0x2
    12c0:	e58c2000 	str	r2, [ip]
    12c4:	e5803004 	str	r3, [r0, #4]
    12c8:	e5885000 	str	r5, [r8]
    12cc:	eafffd4a 	b	0x7fc
    12d0:	e59f74c8 	ldr	r7, [pc, #1224]	; 0x17a0
    12d4:	e5975000 	ldr	r5, [r7]
    12d8:	e5982000 	ldr	r2, [r8]
    12dc:	e1d540b2 	ldrh	r4, [r5, #2]
    12e0:	e4d21001 	ldrb	r1, [r2], #1
    12e4:	e084c005 	add	ip, r4, r5
    12e8:	e08c4101 	add	r4, ip, r1, lsl #2
    12ec:	e5d40002 	ldrb	r0, [r4, #2]
    12f0:	e59f34b4 	ldr	r3, [pc, #1204]	; 0x17ac
    12f4:	e3500006 	cmp	r0, #6	; 0x6
    12f8:	e5882000 	str	r2, [r8]
    12fc:	e5834000 	str	r4, [r3]
    1300:	0a00041e 	beq	0x2380
    1304:	ca00041b 	bgt	0x2378
    1308:	e3500000 	cmp	r0, #0	; 0x0
    130c:	1afffd3a 	bne	0x7fc
    1310:	e2421002 	sub	r1, r2, #2	; 0x2
    1314:	e3a00002 	mov	r0, #2	; 0x2
    1318:	eb00098e 	bl	0x3958
    131c:	e2505000 	subs	r5, r0, #0	; 0x0
    1320:	0a000018 	beq	0x1388
    1324:	e3a00005 	mov	r0, #5	; 0x5
    1328:	e5d41003 	ldrb	r1, [r4, #3]
    132c:	eb00089b 	bl	0x35a0
    1330:	e2506000 	subs	r6, r0, #0	; 0x0
    1334:	0a000459 	beq	0x24a0
    1338:	e2850008 	add	r0, r5, #8	; 0x8
    133c:	e3a01004 	mov	r1, #4	; 0x4
    1340:	e1a02006 	mov	r2, r6
    1344:	eb000a7d 	bl	0x3d40
    1348:	e5d40003 	ldrb	r0, [r4, #3]
    134c:	e3500000 	cmp	r0, #0	; 0x0
    1350:	e3a0c000 	mov	ip, #0	; 0x0
    1354:	9a00000b 	bls	0x1388
    1358:	e5970000 	ldr	r0, [r7]
    135c:	e1d480b0 	ldrh	r8, [r4]
    1360:	e088100c 	add	r1, r8, ip
    1364:	e7d12000 	ldrb	r2, [r1, r0]
    1368:	e086308c 	add	r3, r6, ip, lsl #1
    136c:	e1c320b8 	strh	r2, [r3, #8]
    1370:	e28c8001 	add	r8, ip, #1	; 0x1
    1374:	e1a01808 	mov	r1, r8, lsl #16
    1378:	e5d42003 	ldrb	r2, [r4, #3]
    137c:	e1a0c821 	mov	ip, r1, lsr #16
    1380:	e152000c 	cmp	r2, ip
    1384:	8afffff4 	bhi	0x135c
    1388:	e1a00005 	mov	r0, r5
    138c:	e59fc41c 	ldr	ip, [pc, #1052]	; 0x17b0
    1390:	e3500000 	cmp	r0, #0	; 0x0
    1394:	e58c0000 	str	r0, [ip]
    1398:	0afffd17 	beq	0x7fc
    139c:	e59f3430 	ldr	r3, [pc, #1072]	; 0x17d4
    13a0:	e5935000 	ldr	r5, [r3]
    13a4:	e2852004 	add	r2, r5, #4	; 0x4
    13a8:	e5832000 	str	r2, [r3]
    13ac:	e5850004 	str	r0, [r5, #4]
    13b0:	eafffd11 	b	0x7fc
    13b4:	e59fc3e4 	ldr	ip, [pc, #996]	; 0x17a0
    13b8:	e5980000 	ldr	r0, [r8]
    13bc:	e59c7000 	ldr	r7, [ip]
    13c0:	e5d01001 	ldrb	r1, [r0, #1]
    13c4:	e5d03000 	ldrb	r3, [r0]
    13c8:	e1d750b2 	ldrh	r5, [r7, #2]
    13cc:	e1814403 	orr	r4, r1, r3, lsl #8
    13d0:	e085e007 	add	lr, r5, r7
    13d4:	e1a02104 	mov	r2, r4, lsl #2
    13d8:	e19e60b2 	ldrh	r6, [lr, r2]
    13dc:	e59f53ec 	ldr	r5, [pc, #1004]	; 0x17d0
    13e0:	e0864007 	add	r4, r6, r7
    13e4:	e59f33c0 	ldr	r3, [pc, #960]	; 0x17ac
    13e8:	e59f73d0 	ldr	r7, [pc, #976]	; 0x17c0
    13ec:	e08ec002 	add	ip, lr, r2
    13f0:	e59f63dc 	ldr	r6, [pc, #988]	; 0x17d4
    13f4:	e583c000 	str	ip, [r3]
    13f8:	e1a00004 	mov	r0, r4
    13fc:	e3a01004 	mov	r1, #4	; 0x4
    1400:	e1a02005 	mov	r2, r5
    1404:	e5874000 	str	r4, [r7]
    1408:	eb000a5a 	bl	0x3d78
    140c:	e5962000 	ldr	r2, [r6]
    1410:	e5971000 	ldr	r1, [r7]
    1414:	e5950000 	ldr	r0, [r5]
    1418:	e282e004 	add	lr, r2, #4	; 0x4
    141c:	e5820004 	str	r0, [r2, #4]
    1420:	e586e000 	str	lr, [r6]
    1424:	e2810004 	add	r0, r1, #4	; 0x4
    1428:	e1a02005 	mov	r2, r5
    142c:	e3a01004 	mov	r1, #4	; 0x4
    1430:	eb000a50 	bl	0x3d78
    1434:	e5961000 	ldr	r1, [r6]
    1438:	e598c000 	ldr	ip, [r8]
    143c:	e5950000 	ldr	r0, [r5]
    1440:	e28c2002 	add	r2, ip, #2	; 0x2
    1444:	e2813004 	add	r3, r1, #4	; 0x4
    1448:	e5863000 	str	r3, [r6]
    144c:	e5810004 	str	r0, [r1, #4]
    1450:	e5882000 	str	r2, [r8]
    1454:	eafffce8 	b	0x7fc
    1458:	e59f5374 	ldr	r5, [pc, #884]	; 0x17d4
    145c:	e5952000 	ldr	r2, [r5]
    1460:	e2820004 	add	r0, r2, #4	; 0x4
    1464:	e5103004 	ldr	r3, [r0, #-4]
    1468:	e5850000 	str	r0, [r5]
    146c:	e5823004 	str	r3, [r2, #4]
    1470:	eafffce1 	b	0x7fc
    1474:	e59fc358 	ldr	ip, [pc, #856]	; 0x17d4
    1478:	e59c0000 	ldr	r0, [ip]
    147c:	e2808004 	add	r8, r0, #4	; 0x4
    1480:	e5181004 	ldr	r1, [r8, #-4]
    1484:	e5801004 	str	r1, [r0, #4]
    1488:	e5183008 	ldr	r3, [r8, #-8]
    148c:	e5083004 	str	r3, [r8, #-4]
    1490:	e5902004 	ldr	r2, [r0, #4]
    1494:	e58c8000 	str	r8, [ip]
    1498:	e5082008 	str	r2, [r8, #-8]
    149c:	eafffcd6 	b	0x7fc
    14a0:	e59fe32c 	ldr	lr, [pc, #812]	; 0x17d4
    14a4:	e59e3000 	ldr	r3, [lr]
    14a8:	e2835004 	add	r5, r3, #4	; 0x4
    14ac:	e5152004 	ldr	r2, [r5, #-4]
    14b0:	e5832004 	str	r2, [r3, #4]
    14b4:	e5150008 	ldr	r0, [r5, #-8]
    14b8:	e515c00c 	ldr	ip, [r5, #-12]
    14bc:	e5050004 	str	r0, [r5, #-4]
    14c0:	e505c008 	str	ip, [r5, #-8]
    14c4:	e5938004 	ldr	r8, [r3, #4]
    14c8:	e58e5000 	str	r5, [lr]
    14cc:	e505800c 	str	r8, [r5, #-12]
    14d0:	eafffcc9 	b	0x7fc
    14d4:	e59fc2f8 	ldr	ip, [pc, #760]	; 0x17d4
    14d8:	e59c8000 	ldr	r8, [ip]
    14dc:	e5185004 	ldr	r5, [r8, #-4]
    14e0:	e5981000 	ldr	r1, [r8]
    14e4:	e5885004 	str	r5, [r8, #4]
    14e8:	e5a81008 	str	r1, [r8, #8]!
    14ec:	e58c8000 	str	r8, [ip]
    14f0:	eafffcc1 	b	0x7fc
    14f4:	e59fc2d8 	ldr	ip, [pc, #728]	; 0x17d4
    14f8:	e59c3000 	ldr	r3, [ip]
    14fc:	e2835008 	add	r5, r3, #8	; 0x8
    1500:	e5151008 	ldr	r1, [r5, #-8]
    1504:	e5831008 	str	r1, [r3, #8]
    1508:	e515000c 	ldr	r0, [r5, #-12]
    150c:	e5152010 	ldr	r2, [r5, #-16]
    1510:	e5050004 	str	r0, [r5, #-4]
    1514:	e5052008 	str	r2, [r5, #-8]
    1518:	e5938008 	ldr	r8, [r3, #8]
    151c:	e58c5000 	str	r5, [ip]
    1520:	e5050010 	str	r0, [r5, #-16]
    1524:	e505800c 	str	r8, [r5, #-12]
    1528:	eafffcb3 	b	0x7fc
    152c:	e59fe2a0 	ldr	lr, [pc, #672]	; 0x17d4
    1530:	e59e3000 	ldr	r3, [lr]
    1534:	e2835008 	add	r5, r3, #8	; 0x8
    1538:	e5152008 	ldr	r2, [r5, #-8]
    153c:	e5832008 	str	r2, [r3, #8]
    1540:	e2450010 	sub	r0, r5, #16	; 0x10
    1544:	e8900101 	ldmia	r0, {r0, r8}
    1548:	e515c014 	ldr	ip, [r5, #-20]
    154c:	e5050008 	str	r0, [r5, #-8]
    1550:	e505c00c 	str	ip, [r5, #-12]
    1554:	e5058004 	str	r8, [r5, #-4]
    1558:	e5931008 	ldr	r1, [r3, #8]
    155c:	e58e5000 	str	r5, [lr]
    1560:	e5058014 	str	r8, [r5, #-20]
    1564:	e5051010 	str	r1, [r5, #-16]
    1568:	eafffca3 	b	0x7fc
    156c:	e59f0260 	ldr	r0, [pc, #608]	; 0x17d4
    1570:	e5905000 	ldr	r5, [r0]
    1574:	e59f8254 	ldr	r8, [pc, #596]	; 0x17d0
    1578:	e595c000 	ldr	ip, [r5]
    157c:	e588c000 	str	ip, [r8]
    1580:	e5153004 	ldr	r3, [r5, #-4]
    1584:	e5853000 	str	r3, [r5]
    1588:	e5981000 	ldr	r1, [r8]
    158c:	e5051004 	str	r1, [r5, #-4]
    1590:	eafffc99 	b	0x7fc
    1594:	e59fe238 	ldr	lr, [pc, #568]	; 0x17d4
    1598:	e59e8000 	ldr	r8, [lr]
    159c:	e1a01008 	mov	r1, r8
    15a0:	e411c004 	ldr	ip, [r1], #-4
    15a4:	e59f5224 	ldr	r5, [pc, #548]	; 0x17d0
    15a8:	e585c000 	str	ip, [r5]
    15ac:	e5183004 	ldr	r3, [r8, #-4]
    15b0:	e083000c 	add	r0, r3, ip
    15b4:	e58e1000 	str	r1, [lr]
    15b8:	e5080004 	str	r0, [r8, #-4]
    15bc:	eafffc8e 	b	0x7fc
    15c0:	e59fe20c 	ldr	lr, [pc, #524]	; 0x17d4
    15c4:	e59ec000 	ldr	ip, [lr]
    15c8:	e59c2000 	ldr	r2, [ip]
    15cc:	e2625000 	rsb	r5, r2, #0	; 0x0
    15d0:	e58c5000 	str	r5, [ip]
    15d4:	eaffffef 	b	0x1598
    15d8:	e59fe1f4 	ldr	lr, [pc, #500]	; 0x17d4
    15dc:	e59e8000 	ldr	r8, [lr]
    15e0:	e1a01008 	mov	r1, r8
    15e4:	e411c004 	ldr	ip, [r1], #-4
    15e8:	e59f31e0 	ldr	r3, [pc, #480]	; 0x17d0
    15ec:	e583c000 	str	ip, [r3]
    15f0:	e5182004 	ldr	r2, [r8, #-4]
    15f4:	e000029c 	mul	r0, ip, r2
    15f8:	e58e1000 	str	r1, [lr]
    15fc:	e5080004 	str	r0, [r8, #-4]
    1600:	eafffc7d 	b	0x7fc
    1604:	e59f11c8 	ldr	r1, [pc, #456]	; 0x17d4
    1608:	e5915000 	ldr	r5, [r1]
    160c:	e5950000 	ldr	r0, [r5]
    1610:	e2608000 	rsb	r8, r0, #0	; 0x0
    1614:	e5858000 	str	r8, [r5]
    1618:	eafffc77 	b	0x7fc
    161c:	e59fe1b0 	ldr	lr, [pc, #432]	; 0x17d4
    1620:	e59e5000 	ldr	r5, [lr]
    1624:	e1a00005 	mov	r0, r5
    1628:	e4101004 	ldr	r1, [r0], #-4
    162c:	e59f219c 	ldr	r2, [pc, #412]	; 0x17d0
    1630:	e5821000 	str	r1, [r2]
    1634:	e515c004 	ldr	ip, [r5, #-4]
    1638:	e201301f 	and	r3, r1, #31	; 0x1f
    163c:	e1a0831c 	mov	r8, ip, lsl r3
    1640:	e58e0000 	str	r0, [lr]
    1644:	e5058004 	str	r8, [r5, #-4]
    1648:	eafffc6b 	b	0x7fc
    164c:	e59fe180 	ldr	lr, [pc, #384]	; 0x17d4
    1650:	e59e5000 	ldr	r5, [lr]
    1654:	e1a00005 	mov	r0, r5
    1658:	e4101004 	ldr	r1, [r0], #-4
    165c:	e59f216c 	ldr	r2, [pc, #364]	; 0x17d0
    1660:	e5821000 	str	r1, [r2]
    1664:	e515c004 	ldr	ip, [r5, #-4]
    1668:	e201301f 	and	r3, r1, #31	; 0x1f
    166c:	e1a0835c 	mov	r8, ip, asr r3
    1670:	e58e0000 	str	r0, [lr]
    1674:	e5058004 	str	r8, [r5, #-4]
    1678:	eafffc5f 	b	0x7fc
    167c:	e59fe150 	ldr	lr, [pc, #336]	; 0x17d4
    1680:	e59e5000 	ldr	r5, [lr]
    1684:	e1a00005 	mov	r0, r5
    1688:	e4101004 	ldr	r1, [r0], #-4
    168c:	e59f213c 	ldr	r2, [pc, #316]	; 0x17d0
    1690:	e5821000 	str	r1, [r2]
    1694:	e515c004 	ldr	ip, [r5, #-4]
    1698:	e201301f 	and	r3, r1, #31	; 0x1f
    169c:	e1a0833c 	mov	r8, ip, lsr r3
    16a0:	e58e0000 	str	r0, [lr]
    16a4:	e5058004 	str	r8, [r5, #-4]
    16a8:	eafffc53 	b	0x7fc
    16ac:	e59fe120 	ldr	lr, [pc, #288]	; 0x17d4
    16b0:	e59e5000 	ldr	r5, [lr]
    16b4:	e1a00005 	mov	r0, r5
    16b8:	e410c004 	ldr	ip, [r0], #-4
    16bc:	e59f110c 	ldr	r1, [pc, #268]	; 0x17d0
    16c0:	e581c000 	str	ip, [r1]
    16c4:	e5153004 	ldr	r3, [r5, #-4]
    16c8:	e003800c 	and	r8, r3, ip
    16cc:	e58e0000 	str	r0, [lr]
    16d0:	e5058004 	str	r8, [r5, #-4]
    16d4:	eafffc48 	b	0x7fc
    16d8:	e59fe0f4 	ldr	lr, [pc, #244]	; 0x17d4
    16dc:	e59e5000 	ldr	r5, [lr]
    16e0:	e1a08005 	mov	r8, r5
    16e4:	e4183004 	ldr	r3, [r8], #-4
    16e8:	e59fc0e0 	ldr	ip, [pc, #224]	; 0x17d0
    16ec:	e58c3000 	str	r3, [ip]
    16f0:	e5150004 	ldr	r0, [r5, #-4]
    16f4:	e1802003 	orr	r2, r0, r3
    16f8:	e58e8000 	str	r8, [lr]
    16fc:	e5052004 	str	r2, [r5, #-4]
    1700:	eafffc3d 	b	0x7fc
    1704:	e59fe0c8 	ldr	lr, [pc, #200]	; 0x17d4
    1708:	e59e5000 	ldr	r5, [lr]
    170c:	e1a01005 	mov	r1, r5
    1710:	e4110004 	ldr	r0, [r1], #-4
    1714:	e59f30b4 	ldr	r3, [pc, #180]	; 0x17d0
    1718:	e5830000 	str	r0, [r3]
    171c:	e5158004 	ldr	r8, [r5, #-4]
    1720:	e0282000 	eor	r2, r8, r0
    1724:	e58e1000 	str	r1, [lr]
    1728:	e5052004 	str	r2, [r5, #-4]
    172c:	eafffc32 	b	0x7fc
    1730:	e5982000 	ldr	r2, [r8]
    1734:	e59f0078 	ldr	r0, [pc, #120]	; 0x17b4
    1738:	e590c000 	ldr	ip, [r0]
    173c:	e5d20000 	ldrb	r0, [r2]
    1740:	e1d210d1 	ldrsb	r1, [r2, #1]
    1744:	e79c3100 	ldr	r3, [ip, r0, lsl #2]
    1748:	e0835001 	add	r5, r3, r1
    174c:	e2821002 	add	r1, r2, #2	; 0x2
    1750:	e78c5100 	str	r5, [ip, r0, lsl #2]
    1754:	e5881000 	str	r1, [r8]
    1758:	eafffc27 	b	0x7fc
    175c:	e59fe070 	ldr	lr, [pc, #112]	; 0x17d4
    1760:	e59e5000 	ldr	r5, [lr]
    1764:	e59fc064 	ldr	ip, [pc, #100]	; 0x17d0
    1768:	e5951000 	ldr	r1, [r5]
    176c:	e1a00005 	mov	r0, r5
    1770:	e3a02000 	mov	r2, #0	; 0x0
    1774:	e58c1000 	str	r1, [ip]
    1778:	e4802004 	str	r2, [r0], #4
    177c:	e59c8000 	ldr	r8, [ip]
    1780:	e58e0000 	str	r0, [lr]
    1784:	e5858004 	str	r8, [r5, #4]
    1788:	eafffc1b 	b	0x7fc
    178c:	002073a3 	eoreq	r7, r0, r3, lsr #7
    1790:	002073a2 	eoreq	r7, r0, r2, lsr #7
    1794:	00207398 	mlaeq	r0, r8, r3, r7
    1798:	002073ec 	eoreq	r7, r0, ip, ror #7
    179c:	002073b8 	streqh	r7, [r0], -r8
    17a0:	00207424 	eoreq	r7, r0, r4, lsr #8
    17a4:	00207338 	eoreq	r7, r0, r8, lsr r3
    17a8:	002073f4 	streqd	r7, [r0], -r4
    17ac:	002073a8 	eoreq	r7, r0, r8, lsr #7
    17b0:	002073ac 	eoreq	r7, r0, ip, lsr #7
    17b4:	002073b4 	streqh	r7, [r0], -r4
    17b8:	002073c0 	eoreq	r7, r0, r0, asr #7
    17bc:	002073a0 	eoreq	r7, r0, r0, lsr #7
    17c0:	002073a4 	eoreq	r7, r0, r4, lsr #7
    17c4:	00207410 	eoreq	r7, r0, r0, lsl r4
    17c8:	002073fc 	streqd	r7, [r0], -ip
    17cc:	00207400 	eoreq	r7, r0, r0, lsl #8
    17d0:	002073bc 	streqh	r7, [r0], -ip
    17d4:	0020739c 	mlaeq	r0, ip, r3, r7
    17d8:	e51f500c 	ldr	r5, [pc, #-12]	; 0x17d4
    17dc:	e595c000 	ldr	ip, [r5]
    17e0:	e1a0800c 	mov	r8, ip
    17e4:	e4980004 	ldr	r0, [r8], #4
    17e8:	e51f1020 	ldr	r1, [pc, #-32]	; 0x17d0
    17ec:	e5858000 	str	r8, [r5]
    17f0:	e5810000 	str	r0, [r1]
    17f4:	e58c0004 	str	r0, [ip, #4]
    17f8:	eafffbff 	b	0x7fc
    17fc:	e51f1030 	ldr	r1, [pc, #-48]	; 0x17d4
    1800:	e591c000 	ldr	ip, [r1]
    1804:	e1a0300c 	mov	r3, ip
    1808:	e4130004 	ldr	r0, [r3], #-4
    180c:	e51f2044 	ldr	r2, [pc, #-68]	; 0x17d0
    1810:	e5813000 	str	r3, [r1]
    1814:	e5820000 	str	r0, [r2]
    1818:	e50c0004 	str	r0, [ip, #-4]
    181c:	eafffbf6 	b	0x7fc
    1820:	e51f5054 	ldr	r5, [pc, #-84]	; 0x17d4
    1824:	e5950000 	ldr	r0, [r5]
    1828:	e1d010d0 	ldrsb	r1, [r0]
    182c:	e5801000 	str	r1, [r0]
    1830:	eafffbf1 	b	0x7fc
    1834:	e51f3068 	ldr	r3, [pc, #-104]	; 0x17d4
    1838:	e5938000 	ldr	r8, [r3]
    183c:	e1d820f0 	ldrsh	r2, [r8]
    1840:	e5882000 	str	r2, [r8]
    1844:	eafffbec 	b	0x7fc
    1848:	e51fc07c 	ldr	ip, [pc, #-124]	; 0x17d4
    184c:	e59c1000 	ldr	r1, [ip]
    1850:	e4115004 	ldr	r5, [r1], #-4
    1854:	e3550000 	cmp	r5, #0	; 0x0
    1858:	a5983000 	ldrge	r3, [r8]
    185c:	a2833002 	addge	r3, r3, #2	; 0x2
    1860:	e58c1000 	str	r1, [ip]
    1864:	a5883000 	strge	r3, [r8]
    1868:	aafffbe3 	bge	0x7fc
    186c:	e5980000 	ldr	r0, [r8]
    1870:	e5d0c001 	ldrb	ip, [r0, #1]
    1874:	e5d01000 	ldrb	r1, [r0]
    1878:	e18c5401 	orr	r5, ip, r1, lsl #8
    187c:	e1a02805 	mov	r2, r5, lsl #16
    1880:	e0803842 	add	r3, r0, r2, asr #16
    1884:	e243c001 	sub	ip, r3, #1	; 0x1
    1888:	e588c000 	str	ip, [r8]
    188c:	eafffbda 	b	0x7fc
    1890:	e51fc0c4 	ldr	ip, [pc, #-196]	; 0x17d4
    1894:	e59c1000 	ldr	r1, [ip]
    1898:	e4115004 	ldr	r5, [r1], #-4
    189c:	e3550000 	cmp	r5, #0	; 0x0
    18a0:	b5983000 	ldrlt	r3, [r8]
    18a4:	b2833002 	addlt	r3, r3, #2	; 0x2
    18a8:	e58c1000 	str	r1, [ip]
    18ac:	b5883000 	strlt	r3, [r8]
    18b0:	bafffbd1 	blt	0x7fc
    18b4:	e5980000 	ldr	r0, [r8]
    18b8:	e5d0c001 	ldrb	ip, [r0, #1]
    18bc:	e5d01000 	ldrb	r1, [r0]
    18c0:	e18c5401 	orr	r5, ip, r1, lsl #8
    18c4:	e1a02805 	mov	r2, r5, lsl #16
    18c8:	e0803842 	add	r3, r0, r2, asr #16
    18cc:	e243c001 	sub	ip, r3, #1	; 0x1
    18d0:	e588c000 	str	ip, [r8]
    18d4:	eafffbc8 	b	0x7fc
    18d8:	e51fc10c 	ldr	ip, [pc, #-268]	; 0x17d4
    18dc:	e59c5000 	ldr	r5, [ip]
    18e0:	e4152004 	ldr	r2, [r5], #-4
    18e4:	e3520000 	cmp	r2, #0	; 0x0
    18e8:	d5983000 	ldrle	r3, [r8]
    18ec:	d2833002 	addle	r3, r3, #2	; 0x2
    18f0:	e58c5000 	str	r5, [ip]
    18f4:	d5883000 	strle	r3, [r8]
    18f8:	dafffbbf 	ble	0x7fc
    18fc:	e5980000 	ldr	r0, [r8]
    1900:	e5d0c000 	ldrb	ip, [r0]
    1904:	e5d02001 	ldrb	r2, [r0, #1]
    1908:	e182540c 	orr	r5, r2, ip, lsl #8
    190c:	e1a01805 	mov	r1, r5, lsl #16
    1910:	e0803841 	add	r3, r0, r1, asr #16
    1914:	e243c001 	sub	ip, r3, #1	; 0x1
    1918:	e588c000 	str	ip, [r8]
    191c:	eafffbb6 	b	0x7fc
    1920:	e51fc154 	ldr	ip, [pc, #-340]	; 0x17d4
    1924:	e59c5000 	ldr	r5, [ip]
    1928:	e4152004 	ldr	r2, [r5], #-4
    192c:	e3520000 	cmp	r2, #0	; 0x0
    1930:	c5983000 	ldrgt	r3, [r8]
    1934:	c2833002 	addgt	r3, r3, #2	; 0x2
    1938:	e58c5000 	str	r5, [ip]
    193c:	c5883000 	strgt	r3, [r8]
    1940:	cafffbad 	bgt	0x7fc
    1944:	e5980000 	ldr	r0, [r8]
    1948:	e5d0c000 	ldrb	ip, [r0]
    194c:	e5d02001 	ldrb	r2, [r0, #1]
    1950:	e182540c 	orr	r5, r2, ip, lsl #8
    1954:	e1a01805 	mov	r1, r5, lsl #16
    1958:	e0803841 	add	r3, r0, r1, asr #16
    195c:	e243c001 	sub	ip, r3, #1	; 0x1
    1960:	e588c000 	str	ip, [r8]
    1964:	eafffba4 	b	0x7fc
    1968:	e51fc19c 	ldr	ip, [pc, #-412]	; 0x17d4
    196c:	e59c5000 	ldr	r5, [ip]
    1970:	e1a01005 	mov	r1, r5
    1974:	e4110004 	ldr	r0, [r1], #-4
    1978:	e5152004 	ldr	r2, [r5, #-4]
    197c:	e0603002 	rsb	r3, r0, r2
    1980:	e5053004 	str	r3, [r5, #-4]
    1984:	e58c1000 	str	r1, [ip]
    1988:	eaffffaf 	b	0x184c
    198c:	e51fc1c0 	ldr	ip, [pc, #-448]	; 0x17d4
    1990:	e59c5000 	ldr	r5, [ip]
    1994:	e1a01005 	mov	r1, r5
    1998:	e4110004 	ldr	r0, [r1], #-4
    199c:	e5152004 	ldr	r2, [r5, #-4]
    19a0:	e0603002 	rsb	r3, r0, r2
    19a4:	e5053004 	str	r3, [r5, #-4]
    19a8:	e58c1000 	str	r1, [ip]
    19ac:	eaffffb8 	b	0x1894
    19b0:	e51fc1e4 	ldr	ip, [pc, #-484]	; 0x17d4
    19b4:	e59c2000 	ldr	r2, [ip]
    19b8:	e1a05002 	mov	r5, r2
    19bc:	e4150004 	ldr	r0, [r5], #-4
    19c0:	e5121004 	ldr	r1, [r2, #-4]
    19c4:	e0603001 	rsb	r3, r0, r1
    19c8:	e5023004 	str	r3, [r2, #-4]
    19cc:	e58c5000 	str	r5, [ip]
    19d0:	eaffffc1 	b	0x18dc
    19d4:	e51fc208 	ldr	ip, [pc, #-520]	; 0x17d4
    19d8:	e59c2000 	ldr	r2, [ip]
    19dc:	e1a05002 	mov	r5, r2
    19e0:	e4150004 	ldr	r0, [r5], #-4
    19e4:	e5121004 	ldr	r1, [r2, #-4]
    19e8:	e0603001 	rsb	r3, r0, r1
    19ec:	e5023004 	str	r3, [r2, #-4]
    19f0:	e58c5000 	str	r5, [ip]
    19f4:	eaffffca 	b	0x1924
    19f8:	e51fc22c 	ldr	ip, [pc, #-556]	; 0x17d4
    19fc:	e59c1000 	ldr	r1, [ip]
    1a00:	e1a05001 	mov	r5, r1
    1a04:	e4150004 	ldr	r0, [r5], #-4
    1a08:	e5112004 	ldr	r2, [r1, #-4]
    1a0c:	e0603002 	rsb	r3, r0, r2
    1a10:	e5013004 	str	r3, [r1, #-4]
    1a14:	e58c5000 	str	r5, [ip]
    1a18:	eafffdc3 	b	0x112c
    1a1c:	e51f5250 	ldr	r5, [pc, #-592]	; 0x17d4
    1a20:	e5952000 	ldr	r2, [r5]
    1a24:	e5121004 	ldr	r1, [r2, #-4]
    1a28:	e51f4274 	ldr	r4, [pc, #-628]	; 0x17bc
    1a2c:	e4120004 	ldr	r0, [r2], #-4
    1a30:	e51f6278 	ldr	r6, [pc, #-632]	; 0x17c0
    1a34:	e3510000 	cmp	r1, #0	; 0x0
    1a38:	e5852000 	str	r2, [r5]
    1a3c:	e1c400b0 	strh	r0, [r4]
    1a40:	e5861000 	str	r1, [r6]
    1a44:	051f3280 	ldreq	r3, [pc, #-640]	; 0x17cc
    1a48:	0a000009 	beq	0x1a74
    1a4c:	e1d420f0 	ldrsh	r2, [r4]
    1a50:	e3520000 	cmp	r2, #0	; 0x0
    1a54:	ba000005 	blt	0x1a70
    1a58:	e1d180b0 	ldrh	r8, [r1]
    1a5c:	e1a03b88 	mov	r3, r8, lsl #23
    1a60:	e1a0cba3 	mov	ip, r3, lsr #23
    1a64:	e152000c 	cmp	r2, ip
    1a68:	b3a03001 	movlt	r3, #1	; 0x1
    1a6c:	ba000003 	blt	0x1a80
    1a70:	e51f32b4 	ldr	r3, [pc, #-692]	; 0x17c4
    1a74:	e5930000 	ldr	r0, [r3]
    1a78:	eb000572 	bl	0x3048
    1a7c:	e3a03000 	mov	r3, #0	; 0x0
    1a80:	e3530000 	cmp	r3, #0	; 0x0
    1a84:	0afffb5c 	beq	0x7fc
    1a88:	e1d420f0 	ldrsh	r2, [r4]
    1a8c:	e5963000 	ldr	r3, [r6]
    1a90:	e083c082 	add	ip, r3, r2, lsl #1
    1a94:	e1dc10f8 	ldrsh	r1, [ip, #8]
    1a98:	e5950000 	ldr	r0, [r5]
    1a9c:	e5801000 	str	r1, [r0]
    1aa0:	eafffb55 	b	0x7fc
    1aa4:	e51fe2d8 	ldr	lr, [pc, #-728]	; 0x17d4
    1aa8:	e5985000 	ldr	r5, [r8]
    1aac:	e59e1000 	ldr	r1, [lr]
    1ab0:	e51f2304 	ldr	r2, [pc, #-772]	; 0x17b4
    1ab4:	e4d50001 	ldrb	r0, [r5], #1
    1ab8:	e4113004 	ldr	r3, [r1], #-4
    1abc:	e592c000 	ldr	ip, [r2]
    1ac0:	e5885000 	str	r5, [r8]
    1ac4:	e78c3100 	str	r3, [ip, r0, lsl #2]
    1ac8:	e58e1000 	str	r1, [lr]
    1acc:	eafffb4a 	b	0x7fc
    1ad0:	e51f5304 	ldr	r5, [pc, #-772]	; 0x17d4
    1ad4:	e5980000 	ldr	r0, [r8]
    1ad8:	e595e000 	ldr	lr, [r5]
    1adc:	e5d01000 	ldrb	r1, [r0]
    1ae0:	e51f3334 	ldr	r3, [pc, #-820]	; 0x17b4
    1ae4:	e1a0c00e 	mov	ip, lr
    1ae8:	e5934000 	ldr	r4, [r3]
    1aec:	e2812001 	add	r2, r1, #1	; 0x1
    1af0:	e41c3004 	ldr	r3, [ip], #-4
    1af4:	e20210ff 	and	r1, r2, #255	; 0xff
    1af8:	e7843101 	str	r3, [r4, r1, lsl #2]
    1afc:	e585c000 	str	ip, [r5]
    1b00:	e51e2004 	ldr	r2, [lr, #-4]
    1b04:	e4d03001 	ldrb	r3, [r0], #1
    1b08:	e24c1004 	sub	r1, ip, #4	; 0x4
    1b0c:	e7842103 	str	r2, [r4, r3, lsl #2]
    1b10:	e5880000 	str	r0, [r8]
    1b14:	e5851000 	str	r1, [r5]
    1b18:	eafffb37 	b	0x7fc
    1b1c:	e51fe350 	ldr	lr, [pc, #-848]	; 0x17d4
    1b20:	e5985000 	ldr	r5, [r8]
    1b24:	e59e1000 	ldr	r1, [lr]
    1b28:	e51f237c 	ldr	r2, [pc, #-892]	; 0x17b4
    1b2c:	e4d50001 	ldrb	r0, [r5], #1
    1b30:	e4113004 	ldr	r3, [r1], #-4
    1b34:	e592c000 	ldr	ip, [r2]
    1b38:	e5885000 	str	r5, [r8]
    1b3c:	e78c3100 	str	r3, [ip, r0, lsl #2]
    1b40:	e58e1000 	str	r1, [lr]
    1b44:	eafffb2c 	b	0x7fc
    1b48:	e5984000 	ldr	r4, [r8]
    1b4c:	e554e001 	ldrb	lr, [r4, #-1]
    1b50:	e51f43a0 	ldr	r4, [pc, #-928]	; 0x17b8
    1b54:	e24e301e 	sub	r3, lr, #30	; 0x1e
    1b58:	e5c43000 	strb	r3, [r4]
    1b5c:	e5d40000 	ldrb	r0, [r4]
    1b60:	e51fe3b4 	ldr	lr, [pc, #-948]	; 0x17b4
    1b64:	e2802001 	add	r2, r0, #1	; 0x1
    1b68:	e5c42000 	strb	r2, [r4]
    1b6c:	e59ec000 	ldr	ip, [lr]
    1b70:	e51fe3a4 	ldr	lr, [pc, #-932]	; 0x17d4
    1b74:	e79c5100 	ldr	r5, [ip, r0, lsl #2]
    1b78:	e59e8000 	ldr	r8, [lr]
    1b7c:	e5885004 	str	r5, [r8, #4]
    1b80:	e5d41000 	ldrb	r1, [r4]
    1b84:	e2882004 	add	r2, r8, #4	; 0x4
    1b88:	e79c0101 	ldr	r0, [ip, r1, lsl #2]
    1b8c:	e2823004 	add	r3, r2, #4	; 0x4
    1b90:	e58e3000 	str	r3, [lr]
    1b94:	e5820004 	str	r0, [r2, #4]
    1b98:	eafffb17 	b	0x7fc
    1b9c:	e598c000 	ldr	ip, [r8]
    1ba0:	e51f13f4 	ldr	r1, [pc, #-1012]	; 0x17b4
    1ba4:	e55c5001 	ldrb	r5, [ip, #-1]
    1ba8:	e5918000 	ldr	r8, [r1]
    1bac:	e51f03e0 	ldr	r0, [pc, #-992]	; 0x17d4
    1bb0:	e0883105 	add	r3, r8, r5, lsl #2
    1bb4:	e5901000 	ldr	r1, [r0]
    1bb8:	e513c088 	ldr	ip, [r3, #-136]
    1bbc:	e2812004 	add	r2, r1, #4	; 0x4
    1bc0:	e5802000 	str	r2, [r0]
    1bc4:	e581c004 	str	ip, [r1, #4]
    1bc8:	eafffb0b 	b	0x7fc
    1bcc:	e5985000 	ldr	r5, [r8]
    1bd0:	e5558001 	ldrb	r8, [r5, #-1]
    1bd4:	e51f4424 	ldr	r4, [pc, #-1060]	; 0x17b8
    1bd8:	e2483026 	sub	r3, r8, #38	; 0x26
    1bdc:	eaffffdd 	b	0x1b58
    1be0:	e5985000 	ldr	r5, [r8]
    1be4:	e51f8438 	ldr	r8, [pc, #-1080]	; 0x17b4
    1be8:	e555c001 	ldrb	ip, [r5, #-1]
    1bec:	e5981000 	ldr	r1, [r8]
    1bf0:	e51f0424 	ldr	r0, [pc, #-1060]	; 0x17d4
    1bf4:	e081310c 	add	r3, r1, ip, lsl #2
    1bf8:	e5908000 	ldr	r8, [r0]
    1bfc:	e51350a8 	ldr	r5, [r3, #-168]
    1c00:	e2882004 	add	r2, r8, #4	; 0x4
    1c04:	e5802000 	str	r2, [r0]
    1c08:	e5885004 	str	r5, [r8, #4]
    1c0c:	eafffafa 	b	0x7fc
    1c10:	e51f5444 	ldr	r5, [pc, #-1092]	; 0x17d4
    1c14:	e5953000 	ldr	r3, [r5]
    1c18:	e5131004 	ldr	r1, [r3, #-4]
    1c1c:	e51f4468 	ldr	r4, [pc, #-1128]	; 0x17bc
    1c20:	e413c004 	ldr	ip, [r3], #-4
    1c24:	e51f646c 	ldr	r6, [pc, #-1132]	; 0x17c0
    1c28:	e3510000 	cmp	r1, #0	; 0x0
    1c2c:	e5853000 	str	r3, [r5]
    1c30:	e1c4c0b0 	strh	ip, [r4]
    1c34:	e5861000 	str	r1, [r6]
    1c38:	051f3474 	ldreq	r3, [pc, #-1140]	; 0x17cc
    1c3c:	0a000009 	beq	0x1c68
    1c40:	e1d420f0 	ldrsh	r2, [r4]
    1c44:	e3520000 	cmp	r2, #0	; 0x0
    1c48:	ba000005 	blt	0x1c64
    1c4c:	e1d100b0 	ldrh	r0, [r1]
    1c50:	e1a01b80 	mov	r1, r0, lsl #23
    1c54:	e1a08ba1 	mov	r8, r1, lsr #23
    1c58:	e1520008 	cmp	r2, r8
    1c5c:	b3a03001 	movlt	r3, #1	; 0x1
    1c60:	ba000003 	blt	0x1c74
    1c64:	e51f34a8 	ldr	r3, [pc, #-1192]	; 0x17c4
    1c68:	e5930000 	ldr	r0, [r3]
    1c6c:	eb0004f5 	bl	0x3048
    1c70:	e3a03000 	mov	r3, #0	; 0x0
    1c74:	e3530000 	cmp	r3, #0	; 0x0
    1c78:	0afffadf 	beq	0x7fc
    1c7c:	e1d410f0 	ldrsh	r1, [r4]
    1c80:	e5968000 	ldr	r8, [r6]
    1c84:	e0883101 	add	r3, r8, r1, lsl #2
    1c88:	e593c008 	ldr	ip, [r3, #8]
    1c8c:	e5952000 	ldr	r2, [r5]
    1c90:	e582c000 	str	ip, [r2]
    1c94:	eafffad8 	b	0x7fc
    1c98:	e51f54cc 	ldr	r5, [pc, #-1228]	; 0x17d4
    1c9c:	e5953000 	ldr	r3, [r5]
    1ca0:	e5131004 	ldr	r1, [r3, #-4]
    1ca4:	e51f44f0 	ldr	r4, [pc, #-1264]	; 0x17bc
    1ca8:	e4132004 	ldr	r2, [r3], #-4
    1cac:	e51f64f4 	ldr	r6, [pc, #-1268]	; 0x17c0
    1cb0:	e3510000 	cmp	r1, #0	; 0x0
    1cb4:	e5853000 	str	r3, [r5]
    1cb8:	e1c420b0 	strh	r2, [r4]
    1cbc:	e5861000 	str	r1, [r6]
    1cc0:	051f34fc 	ldreq	r3, [pc, #-1276]	; 0x17cc
    1cc4:	0a000009 	beq	0x1cf0
    1cc8:	e1d420f0 	ldrsh	r2, [r4]
    1ccc:	e3520000 	cmp	r2, #0	; 0x0
    1cd0:	ba000005 	blt	0x1cec
    1cd4:	e1d1c0b0 	ldrh	ip, [r1]
    1cd8:	e1a00b8c 	mov	r0, ip, lsl #23
    1cdc:	e1a08ba0 	mov	r8, r0, lsr #23
    1ce0:	e1520008 	cmp	r2, r8
    1ce4:	b3a03001 	movlt	r3, #1	; 0x1
    1ce8:	ba000003 	blt	0x1cfc
    1cec:	e51f3530 	ldr	r3, [pc, #-1328]	; 0x17c4
    1cf0:	e5930000 	ldr	r0, [r3]
    1cf4:	eb0004d3 	bl	0x3048
    1cf8:	e3a03000 	mov	r3, #0	; 0x0
    1cfc:	e3530000 	cmp	r3, #0	; 0x0
    1d00:	0afffabd 	beq	0x7fc
    1d04:	e1d420f0 	ldrsh	r2, [r4]
    1d08:	e1a0c082 	mov	ip, r2, lsl #1
    1d0c:	e1c4c0b0 	strh	ip, [r4]
    1d10:	e1d400b0 	ldrh	r0, [r4]
    1d14:	e596c000 	ldr	ip, [r6]
    1d18:	e1a08800 	mov	r8, r0, lsl #16
    1d1c:	e2801001 	add	r1, r0, #1	; 0x1
    1d20:	e08c3748 	add	r3, ip, r8, asr #14
    1d24:	e1c410b0 	strh	r1, [r4]
    1d28:	e5958000 	ldr	r8, [r5]
    1d2c:	e5932008 	ldr	r2, [r3, #8]
    1d30:	e1d400f0 	ldrsh	r0, [r4]
    1d34:	e1a03008 	mov	r3, r8
    1d38:	e4832004 	str	r2, [r3], #4
    1d3c:	e08c1100 	add	r1, ip, r0, lsl #2
    1d40:	e5912008 	ldr	r2, [r1, #8]
    1d44:	e5853000 	str	r3, [r5]
    1d48:	e5882004 	str	r2, [r8, #4]
    1d4c:	eafffaaa 	b	0x7fc
    1d50:	e51f5584 	ldr	r5, [pc, #-1412]	; 0x17d4
    1d54:	e5952000 	ldr	r2, [r5]
    1d58:	e5121004 	ldr	r1, [r2, #-4]
    1d5c:	e51f45a8 	ldr	r4, [pc, #-1448]	; 0x17bc
    1d60:	e4128004 	ldr	r8, [r2], #-4
    1d64:	e51f65ac 	ldr	r6, [pc, #-1452]	; 0x17c0
    1d68:	e3510000 	cmp	r1, #0	; 0x0
    1d6c:	e5852000 	str	r2, [r5]
    1d70:	e1c480b0 	strh	r8, [r4]
    1d74:	e5861000 	str	r1, [r6]
    1d78:	051f35b4 	ldreq	r3, [pc, #-1460]	; 0x17cc
    1d7c:	0a000009 	beq	0x1da8
    1d80:	e1d420f0 	ldrsh	r2, [r4]
    1d84:	e3520000 	cmp	r2, #0	; 0x0
    1d88:	ba000005 	blt	0x1da4
    1d8c:	e1d1c0b0 	ldrh	ip, [r1]
    1d90:	e1a00b8c 	mov	r0, ip, lsl #23
    1d94:	e1a01ba0 	mov	r1, r0, lsr #23
    1d98:	e1520001 	cmp	r2, r1
    1d9c:	b3a03001 	movlt	r3, #1	; 0x1
    1da0:	ba000003 	blt	0x1db4
    1da4:	e51f35e8 	ldr	r3, [pc, #-1512]	; 0x17c4
    1da8:	e5930000 	ldr	r0, [r3]
    1dac:	eb0004a5 	bl	0x3048
    1db0:	e3a03000 	mov	r3, #0	; 0x0
    1db4:	e3530000 	cmp	r3, #0	; 0x0
    1db8:	0afffa8f 	beq	0x7fc
    1dbc:	e1d420f0 	ldrsh	r2, [r4]
    1dc0:	e5960000 	ldr	r0, [r6]
    1dc4:	e0801102 	add	r1, r0, r2, lsl #2
    1dc8:	e5918008 	ldr	r8, [r1, #8]
    1dcc:	e5953000 	ldr	r3, [r5]
    1dd0:	e5838000 	str	r8, [r3]
    1dd4:	eafffa88 	b	0x7fc
    1dd8:	e598c000 	ldr	ip, [r8]
    1ddc:	e51fe610 	ldr	lr, [pc, #-1552]	; 0x17d4
    1de0:	e55c5001 	ldrb	r5, [ip, #-1]
    1de4:	e59e8000 	ldr	r8, [lr]
    1de8:	e51f363c 	ldr	r3, [pc, #-1596]	; 0x17b4
    1dec:	e4180004 	ldr	r0, [r8], #-4
    1df0:	e245104b 	sub	r1, r5, #75	; 0x4b
    1df4:	e593c000 	ldr	ip, [r3]
    1df8:	e20120ff 	and	r2, r1, #255	; 0xff
    1dfc:	e78c0102 	str	r0, [ip, r2, lsl #2]
    1e00:	e58e8000 	str	r8, [lr]
    1e04:	eafffa7c 	b	0x7fc
    1e08:	e51f563c 	ldr	r5, [pc, #-1596]	; 0x17d4
    1e0c:	e5954000 	ldr	r4, [r5]
    1e10:	e51f7648 	ldr	r7, [pc, #-1608]	; 0x17d0
    1e14:	e5148004 	ldr	r8, [r4, #-4]
    1e18:	e4146004 	ldr	r6, [r4], #-4
    1e1c:	e5878000 	str	r8, [r7]
    1e20:	e2441004 	sub	r1, r4, #4	; 0x4
    1e24:	e5110004 	ldr	r0, [r1, #-4]
    1e28:	e514c004 	ldr	ip, [r4, #-4]
    1e2c:	e51f8674 	ldr	r8, [pc, #-1652]	; 0x17c0
    1e30:	e51f467c 	ldr	r4, [pc, #-1660]	; 0x17bc
    1e34:	e2413004 	sub	r3, r1, #4	; 0x4
    1e38:	e3500000 	cmp	r0, #0	; 0x0
    1e3c:	e5853000 	str	r3, [r5]
    1e40:	e1c4c0b0 	strh	ip, [r4]
    1e44:	e5880000 	str	r0, [r8]
    1e48:	051f3684 	ldreq	r3, [pc, #-1668]	; 0x17cc
    1e4c:	0a000009 	beq	0x1e78
    1e50:	e1d420f0 	ldrsh	r2, [r4]
    1e54:	e3520000 	cmp	r2, #0	; 0x0
    1e58:	ba000005 	blt	0x1e74
    1e5c:	e1d030b0 	ldrh	r3, [r0]
    1e60:	e1a0cb83 	mov	ip, r3, lsl #23
    1e64:	e1a00bac 	mov	r0, ip, lsr #23
    1e68:	e1520000 	cmp	r2, r0
    1e6c:	b3a03001 	movlt	r3, #1	; 0x1
    1e70:	ba000003 	blt	0x1e84
    1e74:	e51f36b8 	ldr	r3, [pc, #-1720]	; 0x17c4
    1e78:	e5930000 	ldr	r0, [r3]
    1e7c:	eb000471 	bl	0x3048
    1e80:	e3a03000 	mov	r3, #0	; 0x0
    1e84:	e20310ff 	and	r1, r3, #255	; 0xff
    1e88:	e3510000 	cmp	r1, #0	; 0x0
    1e8c:	15951000 	ldrne	r1, [r5]
    1e90:	13a02001 	movne	r2, #1	; 0x1
    1e94:	12411004 	subne	r1, r1, #4	; 0x4
    1e98:	01a02001 	moveq	r2, r1
    1e9c:	15851000 	strne	r1, [r5]
    1ea0:	e3520000 	cmp	r2, #0	; 0x0
    1ea4:	0afffa54 	beq	0x7fc
    1ea8:	e1d400f0 	ldrsh	r0, [r4]
    1eac:	e1a02080 	mov	r2, r0, lsl #1
    1eb0:	e1c420b0 	strh	r2, [r4]
    1eb4:	e1d410b0 	ldrh	r1, [r4]
    1eb8:	e2815001 	add	r5, r1, #1	; 0x1
    1ebc:	e1c450b0 	strh	r5, [r4]
    1ec0:	e5980000 	ldr	r0, [r8]
    1ec4:	e1d430f0 	ldrsh	r3, [r4]
    1ec8:	e5978000 	ldr	r8, [r7]
    1ecc:	e1a0c801 	mov	ip, r1, lsl #16
    1ed0:	e0805103 	add	r5, r0, r3, lsl #2
    1ed4:	e080274c 	add	r2, r0, ip, asr #14
    1ed8:	e5828008 	str	r8, [r2, #8]
    1edc:	e5856008 	str	r6, [r5, #8]
    1ee0:	eafffa45 	b	0x7fc
    1ee4:	e51f5718 	ldr	r5, [pc, #-1816]	; 0x17d4
    1ee8:	e5950000 	ldr	r0, [r5]
    1eec:	e4101004 	ldr	r1, [r0], #-4
    1ef0:	e51f8728 	ldr	r8, [pc, #-1832]	; 0x17d0
    1ef4:	e2403008 	sub	r3, r0, #8	; 0x8
    1ef8:	e5881000 	str	r1, [r8]
    1efc:	e5853000 	str	r3, [r5]
    1f00:	eafffa3d 	b	0x7fc
    1f04:	e5982000 	ldr	r2, [r8]
    1f08:	e51fe75c 	ldr	lr, [pc, #-1884]	; 0x17b4
    1f0c:	e51f4740 	ldr	r4, [pc, #-1856]	; 0x17d4
    1f10:	e4d23001 	ldrb	r3, [r2], #1
    1f14:	e59ec000 	ldr	ip, [lr]
    1f18:	e5945000 	ldr	r5, [r4]
    1f1c:	e79ce103 	ldr	lr, [ip, r3, lsl #2]
    1f20:	e2851004 	add	r1, r5, #4	; 0x4
    1f24:	e5882000 	str	r2, [r8]
    1f28:	e5841000 	str	r1, [r4]
    1f2c:	e585e004 	str	lr, [r5, #4]
    1f30:	eafffa31 	b	0x7fc
    1f34:	e598e000 	ldr	lr, [r8]
    1f38:	e51f178c 	ldr	r1, [pc, #-1932]	; 0x17b4
    1f3c:	e51f4770 	ldr	r4, [pc, #-1904]	; 0x17d4
    1f40:	e5dec000 	ldrb	ip, [lr]
    1f44:	e5915000 	ldr	r5, [r1]
    1f48:	e5942000 	ldr	r2, [r4]
    1f4c:	e795010c 	ldr	r0, [r5, ip, lsl #2]
    1f50:	e282c004 	add	ip, r2, #4	; 0x4
    1f54:	e5820004 	str	r0, [r2, #4]
    1f58:	e584c000 	str	ip, [r4]
    1f5c:	e4de3001 	ldrb	r3, [lr], #1
    1f60:	e0851103 	add	r1, r5, r3, lsl #2
    1f64:	e5910004 	ldr	r0, [r1, #4]
    1f68:	e28c2004 	add	r2, ip, #4	; 0x4
    1f6c:	e5842000 	str	r2, [r4]
    1f70:	e58c0004 	str	r0, [ip, #4]
    1f74:	e588e000 	str	lr, [r8]
    1f78:	eafffa1f 	b	0x7fc
    1f7c:	e5982000 	ldr	r2, [r8]
    1f80:	e51f37d4 	ldr	r3, [pc, #-2004]	; 0x17b4
    1f84:	e51f47b8 	ldr	r4, [pc, #-1976]	; 0x17d4
    1f88:	e4d21001 	ldrb	r1, [r2], #1
    1f8c:	e593c000 	ldr	ip, [r3]
    1f90:	e5945000 	ldr	r5, [r4]
    1f94:	e79ce101 	ldr	lr, [ip, r1, lsl #2]
    1f98:	e2850004 	add	r0, r5, #4	; 0x4
    1f9c:	e5882000 	str	r2, [r8]
    1fa0:	e5840000 	str	r0, [r4]
    1fa4:	e585e004 	str	lr, [r5, #4]
    1fa8:	eafffa13 	b	0x7fc
    1fac:	e5985000 	ldr	r5, [r8]
    1fb0:	e51f8804 	ldr	r8, [pc, #-2052]	; 0x17b4
    1fb4:	e555c001 	ldrb	ip, [r5, #-1]
    1fb8:	e5983000 	ldr	r3, [r8]
    1fbc:	e51f07f0 	ldr	r0, [pc, #-2032]	; 0x17d4
    1fc0:	e083110c 	add	r1, r3, ip, lsl #2
    1fc4:	e5908000 	ldr	r8, [r0]
    1fc8:	e5115068 	ldr	r5, [r1, #-104]
    1fcc:	e2882004 	add	r2, r8, #4	; 0x4
    1fd0:	e5802000 	str	r2, [r0]
    1fd4:	e5885004 	str	r5, [r8, #4]
    1fd8:	eafffa07 	b	0x7fc
    1fdc:	e51f5810 	ldr	r5, [pc, #-2064]	; 0x17d4
    1fe0:	e5950000 	ldr	r0, [r5]
    1fe4:	e5101004 	ldr	r1, [r0, #-4]
    1fe8:	e51f4834 	ldr	r4, [pc, #-2100]	; 0x17bc
    1fec:	e4108004 	ldr	r8, [r0], #-4
    1ff0:	e51f6838 	ldr	r6, [pc, #-2104]	; 0x17c0
    1ff4:	e3510000 	cmp	r1, #0	; 0x0
    1ff8:	e5850000 	str	r0, [r5]
    1ffc:	e1c480b0 	strh	r8, [r4]
    2000:	e5861000 	str	r1, [r6]
    2004:	051f3840 	ldreq	r3, [pc, #-2112]	; 0x17cc
    2008:	0a000009 	beq	0x2034
    200c:	e1d420f0 	ldrsh	r2, [r4]
    2010:	e3520000 	cmp	r2, #0	; 0x0
    2014:	ba000005 	blt	0x2030
    2018:	e1d130b0 	ldrh	r3, [r1]
    201c:	e1a0cb83 	mov	ip, r3, lsl #23
    2020:	e1a01bac 	mov	r1, ip, lsr #23
    2024:	e1520001 	cmp	r2, r1
    2028:	b3a03001 	movlt	r3, #1	; 0x1
    202c:	ba000003 	blt	0x2040
    2030:	e51f3874 	ldr	r3, [pc, #-2164]	; 0x17c4
    2034:	e5930000 	ldr	r0, [r3]
    2038:	eb000402 	bl	0x3048
    203c:	e3a03000 	mov	r3, #0	; 0x0
    2040:	e3530000 	cmp	r3, #0	; 0x0
    2044:	0afff9ec 	beq	0x7fc
    2048:	e1d410f0 	ldrsh	r1, [r4]
    204c:	e596c000 	ldr	ip, [r6]
    2050:	e081000c 	add	r0, r1, ip
    2054:	e1d080d8 	ldrsb	r8, [r0, #8]
    2058:	e5952000 	ldr	r2, [r5]
    205c:	e5828000 	str	r8, [r2]
    2060:	eafff9e5 	b	0x7fc
    2064:	e5981000 	ldr	r1, [r8]
    2068:	e51fe89c 	ldr	lr, [pc, #-2204]	; 0x17d4
    206c:	e5512001 	ldrb	r2, [r1, #-1]
    2070:	e59e8000 	ldr	r8, [lr]
    2074:	e51f38c8 	ldr	r3, [pc, #-2248]	; 0x17b4
    2078:	e418c004 	ldr	ip, [r8], #-4
    207c:	e242003b 	sub	r0, r2, #59	; 0x3b
    2080:	e5931000 	ldr	r1, [r3]
    2084:	e20050ff 	and	r5, r0, #255	; 0xff
    2088:	e781c105 	str	ip, [r1, r5, lsl #2]
    208c:	e58e8000 	str	r8, [lr]
    2090:	eafff9d9 	b	0x7fc
    2094:	e5985000 	ldr	r5, [r8]
    2098:	e51fe8cc 	ldr	lr, [pc, #-2252]	; 0x17d4
    209c:	e5551001 	ldrb	r1, [r5, #-1]
    20a0:	e59e8000 	ldr	r8, [lr]
    20a4:	e51f38f8 	ldr	r3, [pc, #-2296]	; 0x17b4
    20a8:	e4180004 	ldr	r0, [r8], #-4
    20ac:	e2412043 	sub	r2, r1, #67	; 0x43
    20b0:	e5935000 	ldr	r5, [r3]
    20b4:	e202c0ff 	and	ip, r2, #255	; 0xff
    20b8:	e785010c 	str	r0, [r5, ip, lsl #2]
    20bc:	e58e8000 	str	r8, [lr]
    20c0:	eafff9cd 	b	0x7fc
    20c4:	e5985000 	ldr	r5, [r8]
    20c8:	e5558001 	ldrb	r8, [r5, #-1]
    20cc:	e51f091c 	ldr	r0, [pc, #-2332]	; 0x17b8
    20d0:	e2483047 	sub	r3, r8, #71	; 0x47
    20d4:	e51f4908 	ldr	r4, [pc, #-2312]	; 0x17d4
    20d8:	e5c03000 	strb	r3, [r0]
    20dc:	e594e000 	ldr	lr, [r4]
    20e0:	e5d08000 	ldrb	r8, [r0]
    20e4:	e51f3938 	ldr	r3, [pc, #-2360]	; 0x17b4
    20e8:	e1a0100e 	mov	r1, lr
    20ec:	e593c000 	ldr	ip, [r3]
    20f0:	e4115004 	ldr	r5, [r1], #-4
    20f4:	e2882001 	add	r2, r8, #1	; 0x1
    20f8:	e20280ff 	and	r8, r2, #255	; 0xff
    20fc:	e78c5108 	str	r5, [ip, r8, lsl #2]
    2100:	e5d00000 	ldrb	r0, [r0]
    2104:	e51e3004 	ldr	r3, [lr, #-4]
    2108:	e2412004 	sub	r2, r1, #4	; 0x4
    210c:	e78c3100 	str	r3, [ip, r0, lsl #2]
    2110:	e5842000 	str	r2, [r4]
    2114:	eafff9b8 	b	0x7fc
    2118:	e51f494c 	ldr	r4, [pc, #-2380]	; 0x17d4
    211c:	e5947000 	ldr	r7, [r4]
    2120:	e1a08007 	mov	r8, r7
    2124:	e4181004 	ldr	r1, [r8], #-4
    2128:	e51f6960 	ldr	r6, [pc, #-2400]	; 0x17d0
    212c:	e5861000 	str	r1, [r6]
    2130:	e5180004 	ldr	r0, [r8, #-4]
    2134:	e517c004 	ldr	ip, [r7, #-4]
    2138:	e51f5984 	ldr	r5, [pc, #-2436]	; 0x17bc
    213c:	e51f7984 	ldr	r7, [pc, #-2436]	; 0x17c0
    2140:	e2483004 	sub	r3, r8, #4	; 0x4
    2144:	e3500000 	cmp	r0, #0	; 0x0
    2148:	e5843000 	str	r3, [r4]
    214c:	e1c5c0b0 	strh	ip, [r5]
    2150:	e5870000 	str	r0, [r7]
    2154:	051f3990 	ldreq	r3, [pc, #-2448]	; 0x17cc
    2158:	0a000009 	beq	0x2184
    215c:	e1d520f0 	ldrsh	r2, [r5]
    2160:	e3520000 	cmp	r2, #0	; 0x0
    2164:	ba000005 	blt	0x2180
    2168:	e1d030b0 	ldrh	r3, [r0]
    216c:	e1a0cb83 	mov	ip, r3, lsl #23
    2170:	e1a00bac 	mov	r0, ip, lsr #23
    2174:	e1520000 	cmp	r2, r0
    2178:	b3a03001 	movlt	r3, #1	; 0x1
    217c:	ba000003 	blt	0x2190
    2180:	e51f39c4 	ldr	r3, [pc, #-2500]	; 0x17c4
    2184:	e5930000 	ldr	r0, [r3]
    2188:	eb0003ae 	bl	0x3048
    218c:	e3a03000 	mov	r3, #0	; 0x0
    2190:	e20380ff 	and	r8, r3, #255	; 0xff
    2194:	e3580000 	cmp	r8, #0	; 0x0
    2198:	15948000 	ldrne	r8, [r4]
    219c:	13a02001 	movne	r2, #1	; 0x1
    21a0:	12488004 	subne	r8, r8, #4	; 0x4
    21a4:	01a02008 	moveq	r2, r8
    21a8:	15848000 	strne	r8, [r4]
    21ac:	e3520000 	cmp	r2, #0	; 0x0
    21b0:	0afff991 	beq	0x7fc
    21b4:	e5972000 	ldr	r2, [r7]
    21b8:	e1d500f0 	ldrsh	r0, [r5]
    21bc:	e5961000 	ldr	r1, [r6]
    21c0:	e0805002 	add	r5, r0, r2
    21c4:	e5c51008 	strb	r1, [r5, #8]
    21c8:	eafff98b 	b	0x7fc
    21cc:	e51f4a00 	ldr	r4, [pc, #-2560]	; 0x17d4
    21d0:	e5947000 	ldr	r7, [r4]
    21d4:	e1a08007 	mov	r8, r7
    21d8:	e4180004 	ldr	r0, [r8], #-4
    21dc:	e51f6a14 	ldr	r6, [pc, #-2580]	; 0x17d0
    21e0:	e5860000 	str	r0, [r6]
    21e4:	e5180004 	ldr	r0, [r8, #-4]
    21e8:	e517c004 	ldr	ip, [r7, #-4]
    21ec:	e51f5a38 	ldr	r5, [pc, #-2616]	; 0x17bc
    21f0:	e51f7a38 	ldr	r7, [pc, #-2616]	; 0x17c0
    21f4:	e3500000 	cmp	r0, #0	; 0x0
    21f8:	e2481004 	sub	r1, r8, #4	; 0x4
    21fc:	e5841000 	str	r1, [r4]
    2200:	e1c5c0b0 	strh	ip, [r5]
    2204:	e5870000 	str	r0, [r7]
    2208:	051f3a44 	ldreq	r3, [pc, #-2628]	; 0x17cc
    220c:	0a000009 	beq	0x2238
    2210:	e1d520f0 	ldrsh	r2, [r5]
    2214:	e3520000 	cmp	r2, #0	; 0x0
    2218:	ba000005 	blt	0x2234
    221c:	e1d010b0 	ldrh	r1, [r0]
    2220:	e1a0cb81 	mov	ip, r1, lsl #23
    2224:	e1a03bac 	mov	r3, ip, lsr #23
    2228:	e1520003 	cmp	r2, r3
    222c:	b3a03001 	movlt	r3, #1	; 0x1
    2230:	ba000003 	blt	0x2244
    2234:	e51f3a78 	ldr	r3, [pc, #-2680]	; 0x17c4
    2238:	e5930000 	ldr	r0, [r3]
    223c:	eb000381 	bl	0x3048
    2240:	e3a03000 	mov	r3, #0	; 0x0
    2244:	e20380ff 	and	r8, r3, #255	; 0xff
    2248:	e3580000 	cmp	r8, #0	; 0x0
    224c:	15948000 	ldrne	r8, [r4]
    2250:	13a02001 	movne	r2, #1	; 0x1
    2254:	12488004 	subne	r8, r8, #4	; 0x4
    2258:	01a02008 	moveq	r2, r8
    225c:	15848000 	strne	r8, [r4]
    2260:	e3520000 	cmp	r2, #0	; 0x0
    2264:	0afff964 	beq	0x7fc
    2268:	e1d520f0 	ldrsh	r2, [r5]
    226c:	e5970000 	ldr	r0, [r7]
    2270:	e1d660b0 	ldrh	r6, [r6]
    2274:	e0805082 	add	r5, r0, r2, lsl #1
    2278:	e1c560b8 	strh	r6, [r5, #8]
    227c:	eafff95e 	b	0x7fc
    2280:	e51f2ab4 	ldr	r2, [pc, #-2740]	; 0x17d4
    2284:	e5921000 	ldr	r1, [r2]
    2288:	e2418004 	sub	r8, r1, #4	; 0x4
    228c:	e5828000 	str	r8, [r2]
    2290:	eafff959 	b	0x7fc
    2294:	e51f2ac8 	ldr	r2, [pc, #-2760]	; 0x17d4
    2298:	e592c000 	ldr	ip, [r2]
    229c:	e24c0004 	sub	r0, ip, #4	; 0x4
    22a0:	e5820000 	str	r0, [r2]
    22a4:	eafffff6 	b	0x2284
    22a8:	e598e000 	ldr	lr, [r8]
    22ac:	e55e4001 	ldrb	r4, [lr, #-1]
    22b0:	e51f0b00 	ldr	r0, [pc, #-2816]	; 0x17b8
    22b4:	e244303f 	sub	r3, r4, #63	; 0x3f
    22b8:	eaffff85 	b	0x20d4
    22bc:	e51f4af0 	ldr	r4, [pc, #-2800]	; 0x17d4
    22c0:	e5947000 	ldr	r7, [r4]
    22c4:	e1a03007 	mov	r3, r7
    22c8:	e4135004 	ldr	r5, [r3], #-4
    22cc:	e51f6b04 	ldr	r6, [pc, #-2820]	; 0x17d0
    22d0:	e5865000 	str	r5, [r6]
    22d4:	e5130004 	ldr	r0, [r3, #-4]
    22d8:	e517c004 	ldr	ip, [r7, #-4]
    22dc:	e51f5b28 	ldr	r5, [pc, #-2856]	; 0x17bc
    22e0:	e51f7b28 	ldr	r7, [pc, #-2856]	; 0x17c0
    22e4:	e2432004 	sub	r2, r3, #4	; 0x4
    22e8:	e3500000 	cmp	r0, #0	; 0x0
    22ec:	e5842000 	str	r2, [r4]
    22f0:	e1c5c0b0 	strh	ip, [r5]
    22f4:	e5870000 	str	r0, [r7]
    22f8:	051f3b34 	ldreq	r3, [pc, #-2868]	; 0x17cc
    22fc:	0a000009 	beq	0x2328
    2300:	e1d520f0 	ldrsh	r2, [r5]
    2304:	e3520000 	cmp	r2, #0	; 0x0
    2308:	ba000005 	blt	0x2324
    230c:	e1d0c0b0 	ldrh	ip, [r0]
    2310:	e1a01b8c 	mov	r1, ip, lsl #23
    2314:	e1a08ba1 	mov	r8, r1, lsr #23
    2318:	e1520008 	cmp	r2, r8
    231c:	b3a03001 	movlt	r3, #1	; 0x1
    2320:	ba000003 	blt	0x2334
    2324:	e51f3b68 	ldr	r3, [pc, #-2920]	; 0x17c4
    2328:	e5930000 	ldr	r0, [r3]
    232c:	eb000345 	bl	0x3048
    2330:	e3a03000 	mov	r3, #0	; 0x0
    2334:	e20300ff 	and	r0, r3, #255	; 0xff
    2338:	e3500000 	cmp	r0, #0	; 0x0
    233c:	15940000 	ldrne	r0, [r4]
    2340:	13a02001 	movne	r2, #1	; 0x1
    2344:	12400004 	subne	r0, r0, #4	; 0x4
    2348:	01a02000 	moveq	r2, r0
    234c:	15840000 	strne	r0, [r4]
    2350:	e3520000 	cmp	r2, #0	; 0x0
    2354:	0afff928 	beq	0x7fc
    2358:	e1d530f0 	ldrsh	r3, [r5]
    235c:	e5978000 	ldr	r8, [r7]
    2360:	e5965000 	ldr	r5, [r6]
    2364:	e0882103 	add	r2, r8, r3, lsl #2
    2368:	e5825008 	str	r5, [r2, #8]
    236c:	eafff922 	b	0x7fc
    2370:	eb000334 	bl	0x3048
    2374:	eafff920 	b	0x7fc
    2378:	e350000a 	cmp	r0, #10	; 0xa
    237c:	1afff91e 	bne	0x7fc
    2380:	e5934000 	ldr	r4, [r3]
    2384:	e5975000 	ldr	r5, [r7]
    2388:	e1d4e0b0 	ldrh	lr, [r4]
    238c:	e51f4bc4 	ldr	r4, [pc, #-3012]	; 0x17d0
    2390:	e08e0005 	add	r0, lr, r5
    2394:	e3a01004 	mov	r1, #4	; 0x4
    2398:	e1a02004 	mov	r2, r4
    239c:	eb000675 	bl	0x3d78
    23a0:	e51f8bd4 	ldr	r8, [pc, #-3028]	; 0x17d4
    23a4:	e5981000 	ldr	r1, [r8]
    23a8:	e5940000 	ldr	r0, [r4]
    23ac:	e281c004 	add	ip, r1, #4	; 0x4
    23b0:	e588c000 	str	ip, [r8]
    23b4:	e5810004 	str	r0, [r1, #4]
    23b8:	eafff90f 	b	0x7fc
    23bc:	e5d11001 	ldrb	r1, [r1, #1]
    23c0:	eb000870 	bl	0x4588
    23c4:	e3500000 	cmp	r0, #0	; 0x0
    23c8:	1afffb2e 	bne	0x1088
    23cc:	e51fec0c 	ldr	lr, [pc, #-3084]	; 0x17c8
    23d0:	e59e0000 	ldr	r0, [lr]
    23d4:	eb00031b 	bl	0x3048
    23d8:	eafffb2a 	b	0x1088
    23dc:	e51f8c18 	ldr	r8, [pc, #-3096]	; 0x17cc
    23e0:	e5980000 	ldr	r0, [r8]
    23e4:	eb000317 	bl	0x3048
    23e8:	eafff903 	b	0x7fc
    23ec:	e51f8c28 	ldr	r8, [pc, #-3112]	; 0x17cc
    23f0:	e5980000 	ldr	r0, [r8]
    23f4:	eb000313 	bl	0x3048
    23f8:	eafff8ff 	b	0x7fc
    23fc:	e5950000 	ldr	r0, [r5]
    2400:	e4102004 	ldr	r2, [r0], #-4
    2404:	e3a01004 	mov	r1, #4	; 0x4
    2408:	e5850000 	str	r0, [r5]
    240c:	e2840004 	add	r0, r4, #4	; 0x4
    2410:	eb00064a 	bl	0x3d40
    2414:	eafffabb 	b	0xf08
    2418:	e1a02006 	mov	r2, r6
    241c:	e2850004 	add	r0, r5, #4	; 0x4
    2420:	e3a01004 	mov	r1, #4	; 0x4
    2424:	eb000653 	bl	0x3d78
    2428:	e597c000 	ldr	ip, [r7]
    242c:	e5968000 	ldr	r8, [r6]
    2430:	e28c2004 	add	r2, ip, #4	; 0x4
    2434:	e5872000 	str	r2, [r7]
    2438:	e58c8004 	str	r8, [ip, #4]
    243c:	eafffa8e 	b	0xe7c
    2440:	e51f6c78 	ldr	r6, [pc, #-3192]	; 0x17d0
    2444:	e51f7c78 	ldr	r7, [pc, #-3192]	; 0x17d4
    2448:	e1a01005 	mov	r1, r5
    244c:	e1a00004 	mov	r0, r4
    2450:	e1a02006 	mov	r2, r6
    2454:	eb000647 	bl	0x3d78
    2458:	e5972000 	ldr	r2, [r7]
    245c:	e51b0030 	ldr	r0, [fp, #-48]
    2460:	e5965000 	ldr	r5, [r6]
    2464:	e282c004 	add	ip, r2, #4	; 0x4
    2468:	e3500000 	cmp	r0, #0	; 0x0
    246c:	e5825004 	str	r5, [r2, #4]
    2470:	e587c000 	str	ip, [r7]
    2474:	0afffa5e 	beq	0xdf4
    2478:	e3a01004 	mov	r1, #4	; 0x4
    247c:	e2840004 	add	r0, r4, #4	; 0x4
    2480:	e1a02006 	mov	r2, r6
    2484:	eb00063b 	bl	0x3d78
    2488:	e5973000 	ldr	r3, [r7]
    248c:	e5961000 	ldr	r1, [r6]
    2490:	e283c004 	add	ip, r3, #4	; 0x4
    2494:	e587c000 	str	ip, [r7]
    2498:	e5831004 	str	r1, [r3, #4]
    249c:	eafffa54 	b	0xdf4
    24a0:	e5973000 	ldr	r3, [r7]
    24a4:	e5d3e025 	ldrb	lr, [r3, #37]
    24a8:	e5d38024 	ldrb	r8, [r3, #36]
    24ac:	e1a00005 	mov	r0, r5
    24b0:	e188140e 	orr	r1, r8, lr, lsl #8
    24b4:	eb0005a2 	bl	0x3b44
    24b8:	e1a00006 	mov	r0, r6
    24bc:	eafffbb2 	b	0x138c
    24c0:	e59f0020 	ldr	r0, [pc, #32]	; 0x24e8
    24c4:	e5902000 	ldr	r2, [r0]
    24c8:	e5d2301c 	ldrb	r3, [r2, #28]
    24cc:	e3530000 	cmp	r3, #0	; 0x0
    24d0:	e1a00003 	mov	r0, r3
    24d4:	e0831103 	add	r1, r3, r3, lsl #2
    24d8:	15923014 	ldrne	r3, [r2, #20]
    24dc:	10833101 	addne	r3, r3, r1, lsl #2
    24e0:	1243000c 	subne	r0, r3, #12	; 0xc
    24e4:	e12fff1e 	bx	lr
    24e8:	002073ec 	eoreq	r7, r0, ip, ror #7
    24ec:	e59fc014 	ldr	ip, [pc, #20]	; 0x2508
    24f0:	e59f2014 	ldr	r2, [pc, #20]	; 0x250c
    24f4:	e59c1000 	ldr	r1, [ip]
    24f8:	e5923000 	ldr	r3, [r2]
    24fc:	e580300c 	str	r3, [r0, #12]
    2500:	e5801010 	str	r1, [r0, #16]
    2504:	e12fff1e 	bx	lr
    2508:	0020739c 	mlaeq	r0, ip, r3, r7
    250c:	002073b8 	streqh	r7, [r0], -r8
    2510:	e52de004 	str	lr, [sp, #-4]!
    2514:	e59f2024 	ldr	r2, [pc, #36]	; 0x2540
    2518:	e590e008 	ldr	lr, [r0, #8]
    251c:	e280100c 	add	r1, r0, #12	; 0xc
    2520:	e8911002 	ldmia	r1, {r1, ip}
    2524:	e59f3018 	ldr	r3, [pc, #24]	; 0x2544
    2528:	e59f0018 	ldr	r0, [pc, #24]	; 0x2548
    252c:	e582c000 	str	ip, [r2]
    2530:	e5801000 	str	r1, [r0]
    2534:	e583e000 	str	lr, [r3]
    2538:	e49de004 	ldr	lr, [sp], #4
    253c:	e12fff1e 	bx	lr
    2540:	0020739c 	mlaeq	r0, ip, r3, r7
    2544:	002073b4 	streqh	r7, [r0], -r4
    2548:	002073b8 	streqh	r7, [r0], -r8
    254c:	e5d01020 	ldrb	r1, [r0, #32]
    2550:	e59f2024 	ldr	r2, [pc, #36]	; 0x257c
    2554:	e241c001 	sub	ip, r1, #1	; 0x1
    2558:	e20c30ff 	and	r3, ip, #255	; 0xff
    255c:	e7921103 	ldr	r1, [r2, r3, lsl #2]
    2560:	e3510000 	cmp	r1, #0	; 0x0
    2564:	e7820103 	str	r0, [r2, r3, lsl #2]
    2568:	15913008 	ldrne	r3, [r1, #8]
    256c:	15803008 	strne	r3, [r0, #8]
    2570:	05800008 	streq	r0, [r0, #8]
    2574:	15810008 	strne	r0, [r1, #8]
    2578:	e12fff1e 	bx	lr
    257c:	002073c4 	eoreq	r7, r0, r4, asr #7
    2580:	e1a0c00d 	mov	ip, sp
    2584:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    2588:	e59f60e8 	ldr	r6, [pc, #232]	; 0x2678
    258c:	e5d61000 	ldrb	r1, [r6]
    2590:	e1a04000 	mov	r4, r0
    2594:	e2810001 	add	r0, r1, #1	; 0x1
    2598:	e5c4001e 	strb	r0, [r4, #30]
    259c:	e59f70d8 	ldr	r7, [pc, #216]	; 0x267c
    25a0:	e5973000 	ldr	r3, [r7]
    25a4:	e1d451df 	ldrsb	r5, [r4, #31]
    25a8:	e3530000 	cmp	r3, #0	; 0x0
    25ac:	02833005 	addeq	r3, r3, #5	; 0x5
    25b0:	05c43020 	streqb	r3, [r4, #32]
    25b4:	e3550000 	cmp	r5, #0	; 0x0
    25b8:	e24cb004 	sub	fp, ip, #4	; 0x4
    25bc:	0a000005 	beq	0x25d8
    25c0:	e59f20b8 	ldr	r2, [pc, #184]	; 0x2680
    25c4:	e5920000 	ldr	r0, [r2]
    25c8:	eb00029e 	bl	0x3048
    25cc:	e3a00000 	mov	r0, #0	; 0x0
    25d0:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    25d4:	e12fff1e 	bx	lr
    25d8:	e3a00001 	mov	r0, #1	; 0x1
    25dc:	e3a01004 	mov	r1, #4	; 0x4
    25e0:	eb0003ee 	bl	0x35a0
    25e4:	e3500000 	cmp	r0, #0	; 0x0
    25e8:	e5840014 	str	r0, [r4, #20]
    25ec:	01a00005 	moveq	r0, r5
    25f0:	0afffff6 	beq	0x25d0
    25f4:	e3a0000a 	mov	r0, #10	; 0xa
    25f8:	e1a01000 	mov	r1, r0
    25fc:	eb0003e7 	bl	0x35a0
    2600:	e3500000 	cmp	r0, #0	; 0x0
    2604:	e5840018 	str	r0, [r4, #24]
    2608:	0a000015 	beq	0x2664
    260c:	e5d61000 	ldrb	r1, [r6]
    2610:	e3a00002 	mov	r0, #2	; 0x2
    2614:	e2813001 	add	r3, r1, #1	; 0x1
    2618:	e5c63000 	strb	r3, [r6]
    261c:	e5c4001f 	strb	r0, [r4, #31]
    2620:	e5c4501c 	strb	r5, [r4, #28]
    2624:	e597c000 	ldr	ip, [r7]
    2628:	e35c0000 	cmp	ip, #0	; 0x0
    262c:	05874000 	streq	r4, [r7]
    2630:	e5d42020 	ldrb	r2, [r4, #32]
    2634:	e59f0048 	ldr	r0, [pc, #72]	; 0x2684
    2638:	e2421001 	sub	r1, r2, #1	; 0x1
    263c:	e20130ff 	and	r3, r1, #255	; 0xff
    2640:	e790c103 	ldr	ip, [r0, r3, lsl #2]
    2644:	e35c0000 	cmp	ip, #0	; 0x0
    2648:	e7804103 	str	r4, [r0, r3, lsl #2]
    264c:	159c3008 	ldrne	r3, [ip, #8]
    2650:	e3a00001 	mov	r0, #1	; 0x1
    2654:	15843008 	strne	r3, [r4, #8]
    2658:	05844008 	streq	r4, [r4, #8]
    265c:	158c4008 	strne	r4, [ip, #8]
    2660:	eaffffda 	b	0x25d0
    2664:	e5940014 	ldr	r0, [r4, #20]
    2668:	eb00053e 	bl	0x3b68
    266c:	e1a00005 	mov	r0, r5
    2670:	e5845014 	str	r5, [r4, #20]
    2674:	eaffffd5 	b	0x25d0
    2678:	002073f0 	streqd	r7, [r0], -r0
    267c:	002073ec 	eoreq	r7, r0, ip, ror #7
    2680:	00207404 	eoreq	r7, r0, r4, lsl #8
    2684:	002073c4 	eoreq	r7, r0, r4, asr #7
    2688:	e5d02020 	ldrb	r2, [r0, #32]
    268c:	e242c001 	sub	ip, r2, #1	; 0x1
    2690:	e20c10ff 	and	r1, ip, #255	; 0xff
    2694:	e59f2040 	ldr	r2, [pc, #64]	; 0x26dc
    2698:	e1a03101 	mov	r3, r1, lsl #2
    269c:	e793c002 	ldr	ip, [r3, r2]
    26a0:	e59c1008 	ldr	r1, [ip, #8]
    26a4:	e1510000 	cmp	r1, r0
    26a8:	e0832002 	add	r2, r3, r2
    26ac:	0a000003 	beq	0x26c0
    26b0:	e1a0c001 	mov	ip, r1
    26b4:	e5911008 	ldr	r1, [r1, #8]
    26b8:	e1510000 	cmp	r1, r0
    26bc:	1afffffb 	bne	0x26b0
    26c0:	e15c0000 	cmp	ip, r0
    26c4:	15903008 	ldrne	r3, [r0, #8]
    26c8:	03a03000 	moveq	r3, #0	; 0x0
    26cc:	05823000 	streq	r3, [r2]
    26d0:	1582c000 	strne	ip, [r2]
    26d4:	158c3008 	strne	r3, [ip, #8]
    26d8:	e12fff1e 	bx	lr
    26dc:	002073c4 	eoreq	r7, r0, r4, asr #7
    26e0:	e1a0c00d 	mov	ip, sp
    26e4:	e3510000 	cmp	r1, #0	; 0x0
    26e8:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
    26ec:	e24cb004 	sub	fp, ip, #4	; 0x4
    26f0:	0a000017 	beq	0x2754
    26f4:	e5d12004 	ldrb	r2, [r1, #4]
    26f8:	e3520000 	cmp	r2, #0	; 0x0
    26fc:	e282e001 	add	lr, r2, #1	; 0x1
    2700:	05d0301e 	ldreqb	r3, [r0, #30]
    2704:	0a00000e 	beq	0x2744
    2708:	e1d031de 	ldrsb	r3, [r0, #30]
    270c:	e5d12005 	ldrb	r2, [r1, #5]
    2710:	e1530002 	cmp	r3, r2
    2714:	e3a0c001 	mov	ip, #1	; 0x1
    2718:	e5d0301e 	ldrb	r3, [r0, #30]
    271c:	0a000008 	beq	0x2744
    2720:	e3a02004 	mov	r2, #4	; 0x4
    2724:	e580100c 	str	r1, [r0, #12]
    2728:	e5c0201f 	strb	r2, [r0, #31]
    272c:	e5c0c01d 	strb	ip, [r0, #29]
    2730:	e59f102c 	ldr	r1, [pc, #44]	; 0x2764
    2734:	e59f002c 	ldr	r0, [pc, #44]	; 0x2768
    2738:	e5c1c000 	strb	ip, [r1]
    273c:	e5c0c000 	strb	ip, [r0]
    2740:	ea000001 	b	0x274c
    2744:	e5c1e004 	strb	lr, [r1, #4]
    2748:	e5c13005 	strb	r3, [r1, #5]
    274c:	e89d6800 	ldmia	sp, {fp, sp, lr}
    2750:	e12fff1e 	bx	lr
    2754:	e59f3010 	ldr	r3, [pc, #16]	; 0x276c
    2758:	e5930000 	ldr	r0, [r3]
    275c:	eb000239 	bl	0x3048
    2760:	eafffff9 	b	0x274c
    2764:	002073a2 	eoreq	r7, r0, r2, lsr #7
    2768:	002073a3 	eoreq	r7, r0, r3, lsr #7
    276c:	00207400 	eoreq	r7, r0, r0, lsl #8
    2770:	e1a0c00d 	mov	ip, sp
    2774:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    2778:	e24cb004 	sub	fp, ip, #4	; 0x4
    277c:	e24dd008 	sub	sp, sp, #8	; 0x8
    2780:	e59fa4a0 	ldr	sl, [pc, #1184]	; 0x2c28
    2784:	e59a2000 	ldr	r2, [sl]
    2788:	e3520000 	cmp	r2, #0	; 0x0
    278c:	e3a08000 	mov	r8, #0	; 0x0
    2790:	0a000011 	beq	0x27dc
    2794:	e1d231df 	ldrsb	r3, [r2, #31]
    2798:	e3530001 	cmp	r3, #1	; 0x1
    279c:	0a0000da 	beq	0x2b0c
    27a0:	e5d2701c 	ldrb	r7, [r2, #28]
    27a4:	e3570000 	cmp	r7, #0	; 0x0
    27a8:	15923014 	ldrne	r3, [r2, #20]
    27ac:	10872107 	addne	r2, r7, r7, lsl #2
    27b0:	10833102 	addne	r3, r3, r2, lsl #2
    27b4:	01a00008 	moveq	r0, r8
    27b8:	1243000c 	subne	r0, r3, #12	; 0xc
    27bc:	e3500000 	cmp	r0, #0	; 0x0
    27c0:	0a000005 	beq	0x27dc
    27c4:	e59f1460 	ldr	r1, [pc, #1120]	; 0x2c2c
    27c8:	e59f2460 	ldr	r2, [pc, #1120]	; 0x2c30
    27cc:	e591c000 	ldr	ip, [r1]
    27d0:	e5923000 	ldr	r3, [r2]
    27d4:	e580c010 	str	ip, [r0, #16]
    27d8:	e580300c 	str	r3, [r0, #12]
    27dc:	e3a00009 	mov	r0, #9	; 0x9
    27e0:	e3a07000 	mov	r7, #0	; 0x0
    27e4:	e58a7000 	str	r7, [sl]
    27e8:	e50b0030 	str	r0, [fp, #-48]
    27ec:	e1a02000 	mov	r2, r0
    27f0:	e1a03102 	mov	r3, r2, lsl #2
    27f4:	e59f2438 	ldr	r2, [pc, #1080]	; 0x2c34
    27f8:	e7937002 	ldr	r7, [r3, r2]
    27fc:	e083c002 	add	ip, r3, r2
    2800:	e3570000 	cmp	r7, #0	; 0x0
    2804:	e50bc02c 	str	ip, [fp, #-44]
    2808:	e1a00007 	mov	r0, r7
    280c:	0a000041 	beq	0x2918
    2810:	e5905008 	ldr	r5, [r0, #8]
    2814:	e1d501df 	ldrsb	r0, [r5, #31]
    2818:	e2401002 	sub	r1, r0, #2	; 0x2
    281c:	e3510004 	cmp	r1, #4	; 0x4
    2820:	979ff101 	ldrls	pc, [pc, r1, lsl #2]
    2824:	ea00002d 	b	0x28e0
    2828:	002049a8 	eoreq	r4, r0, r8, lsr #19
    282c:	002048e0 	eoreq	r4, r0, r0, ror #17
    2830:	0020486c 	eoreq	r4, r0, ip, ror #16
    2834:	0020483c 	eoreq	r4, r0, ip, lsr r8
    2838:	00204a30 	eoreq	r4, r0, r0, lsr sl
    283c:	e595c010 	ldr	ip, [r5, #16]
    2840:	e35c0000 	cmp	ip, #0	; 0x0
    2844:	da000003 	ble	0x2858
    2848:	ebfff5f0 	bl	0x10
    284c:	e5953010 	ldr	r3, [r5, #16]
    2850:	e1500003 	cmp	r0, r3
    2854:	2a000004 	bcs	0x286c
    2858:	e1d522d1 	ldrsb	r2, [r5, #33]
    285c:	e3520000 	cmp	r2, #0	; 0x0
    2860:	0a00001e 	beq	0x28e0
    2864:	e3a04002 	mov	r4, #2	; 0x2
    2868:	e5c54021 	strb	r4, [r5, #33]
    286c:	e595400c 	ldr	r4, [r5, #12]
    2870:	e5d41005 	ldrb	r1, [r4, #5]
    2874:	e3510000 	cmp	r1, #0	; 0x0
    2878:	1a000078 	bne	0x2a60
    287c:	e3540000 	cmp	r4, #0	; 0x0
    2880:	0a0000d4 	beq	0x2bd8
    2884:	e5d41004 	ldrb	r1, [r4, #4]
    2888:	e3510000 	cmp	r1, #0	; 0x0
    288c:	05d5201e 	ldreqb	r2, [r5, #30]
    2890:	0a000093 	beq	0x2ae4
    2894:	e1d501de 	ldrsb	r0, [r5, #30]
    2898:	e3500000 	cmp	r0, #0	; 0x0
    289c:	e5d5201e 	ldrb	r2, [r5, #30]
    28a0:	0a00008f 	beq	0x2ae4
    28a4:	e59f338c 	ldr	r3, [pc, #908]	; 0x2c38
    28a8:	e59fc38c 	ldr	ip, [pc, #908]	; 0x2c3c
    28ac:	e3a01001 	mov	r1, #1	; 0x1
    28b0:	e3a02004 	mov	r2, #4	; 0x4
    28b4:	e5c5201f 	strb	r2, [r5, #31]
    28b8:	e585400c 	str	r4, [r5, #12]
    28bc:	e5c5101d 	strb	r1, [r5, #29]
    28c0:	e5c31000 	strb	r1, [r3]
    28c4:	e5cc1000 	strb	r1, [ip]
    28c8:	e5d5301d 	ldrb	r3, [r5, #29]
    28cc:	e3a01003 	mov	r1, #3	; 0x3
    28d0:	e3a0c000 	mov	ip, #0	; 0x0
    28d4:	e5c43004 	strb	r3, [r4, #4]
    28d8:	e5c5101f 	strb	r1, [r5, #31]
    28dc:	e585c00c 	str	ip, [r5, #12]
    28e0:	e59ac000 	ldr	ip, [sl]
    28e4:	e35c0000 	cmp	ip, #0	; 0x0
    28e8:	1a000004 	bne	0x2900
    28ec:	e1d511df 	ldrsb	r1, [r5, #31]
    28f0:	e3510003 	cmp	r1, #3	; 0x3
    28f4:	051b002c 	ldreq	r0, [fp, #-44]
    28f8:	058a5000 	streq	r5, [sl]
    28fc:	05805000 	streq	r5, [r0]
    2900:	e1d522d2 	ldrsb	r2, [r5, #34]
    2904:	e3520000 	cmp	r2, #0	; 0x0
    2908:	03a08001 	moveq	r8, #1	; 0x1
    290c:	e1a00005 	mov	r0, r5
    2910:	e1550007 	cmp	r5, r7
    2914:	1affffbd 	bne	0x2810
    2918:	e51b1030 	ldr	r1, [fp, #-48]
    291c:	e241c001 	sub	ip, r1, #1	; 0x1
    2920:	e1a0380c 	mov	r3, ip, lsl #16
    2924:	e1a00823 	mov	r0, r3, lsr #16
    2928:	e1b02843 	movs	r2, r3, asr #16
    292c:	e50b0030 	str	r0, [fp, #-48]
    2930:	5affffae 	bpl	0x27f0
    2934:	e3580000 	cmp	r8, #0	; 0x0
    2938:	0a0000aa 	beq	0x2be8
    293c:	e59a3000 	ldr	r3, [sl]
    2940:	e3530000 	cmp	r3, #0	; 0x0
    2944:	0a000013 	beq	0x2998
    2948:	e5d3c01c 	ldrb	ip, [r3, #28]
    294c:	e35c0000 	cmp	ip, #0	; 0x0
    2950:	15933014 	ldrne	r3, [r3, #20]
    2954:	e59a0000 	ldr	r0, [sl]
    2958:	108cc10c 	addne	ip, ip, ip, lsl #2
    295c:	e1d022d1 	ldrsb	r2, [r0, #33]
    2960:	1083310c 	addne	r3, r3, ip, lsl #2
    2964:	1243100c 	subne	r1, r3, #12	; 0xc
    2968:	01a0100c 	moveq	r1, ip
    296c:	e5910008 	ldr	r0, [r1, #8]
    2970:	e591c010 	ldr	ip, [r1, #16]
    2974:	e3520002 	cmp	r2, #2	; 0x2
    2978:	e591100c 	ldr	r1, [r1, #12]
    297c:	e59f22ac 	ldr	r2, [pc, #684]	; 0x2c30
    2980:	e59f32a4 	ldr	r3, [pc, #676]	; 0x2c2c
    2984:	e5821000 	str	r1, [r2]
    2988:	e59f22b0 	ldr	r2, [pc, #688]	; 0x2c40
    298c:	e583c000 	str	ip, [r3]
    2990:	e5820000 	str	r0, [r2]
    2994:	0a000073 	beq	0x2b68
    2998:	e3a00001 	mov	r0, #1	; 0x1
    299c:	e24bd028 	sub	sp, fp, #40	; 0x28
    29a0:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    29a4:	e12fff1e 	bx	lr
    29a8:	e59a6000 	ldr	r6, [sl]
    29ac:	e3560000 	cmp	r6, #0	; 0x0
    29b0:	1affffd2 	bne	0x2900
    29b4:	e5952018 	ldr	r2, [r5, #24]
    29b8:	e59f926c 	ldr	r9, [pc, #620]	; 0x2c2c
    29bc:	e2821008 	add	r1, r2, #8	; 0x8
    29c0:	e3a0c003 	mov	ip, #3	; 0x3
    29c4:	e5891000 	str	r1, [r9]
    29c8:	e58a5000 	str	r5, [sl]
    29cc:	e59f3270 	ldr	r3, [pc, #624]	; 0x2c44
    29d0:	e5c5c01f 	strb	ip, [r5, #31]
    29d4:	e593e000 	ldr	lr, [r3]
    29d8:	e155000e 	cmp	r5, lr
    29dc:	0a000067 	beq	0x2b80
    29e0:	e599c000 	ldr	ip, [r9]
    29e4:	e1a02006 	mov	r2, r6
    29e8:	e58c5000 	str	r5, [ip]
    29ec:	e1a00005 	mov	r0, r5
    29f0:	e3a01001 	mov	r1, #1	; 0x1
    29f4:	eb0005dd 	bl	0x4170
    29f8:	e59a3000 	ldr	r3, [sl]
    29fc:	e5d3201c 	ldrb	r2, [r3, #28]
    2a00:	e3520000 	cmp	r2, #0	; 0x0
    2a04:	01a01002 	moveq	r1, r2
    2a08:	15931014 	ldrne	r1, [r3, #20]
    2a0c:	10822102 	addne	r2, r2, r2, lsl #2
    2a10:	1081e102 	addne	lr, r1, r2, lsl #2
    2a14:	124e100c 	subne	r1, lr, #12	; 0xc
    2a18:	e59fe210 	ldr	lr, [pc, #528]	; 0x2c30
    2a1c:	e5990000 	ldr	r0, [r9]
    2a20:	e59e3000 	ldr	r3, [lr]
    2a24:	e5810010 	str	r0, [r1, #16]
    2a28:	e581300c 	str	r3, [r1, #12]
    2a2c:	eaffffab 	b	0x28e0
    2a30:	e1d5c2d1 	ldrsb	ip, [r5, #33]
    2a34:	e35c0000 	cmp	ip, #0	; 0x0
    2a38:	e5d53021 	ldrb	r3, [r5, #33]
    2a3c:	0a00002c 	beq	0x2af4
    2a40:	e3a00003 	mov	r0, #3	; 0x3
    2a44:	e3530000 	cmp	r3, #0	; 0x0
    2a48:	e5c5001f 	strb	r0, [r5, #31]
    2a4c:	e3a02000 	mov	r2, #0	; 0x0
    2a50:	12400001 	subne	r0, r0, #1	; 0x1
    2a54:	15c50021 	strneb	r0, [r5, #33]
    2a58:	e5852010 	str	r2, [r5, #16]
    2a5c:	eaffff9f 	b	0x28e0
    2a60:	e59a2000 	ldr	r2, [sl]
    2a64:	e3520000 	cmp	r2, #0	; 0x0
    2a68:	1affffa4 	bne	0x2900
    2a6c:	e5d5c01e 	ldrb	ip, [r5, #30]
    2a70:	e1a00c0c 	mov	r0, ip, lsl #24
    2a74:	e1510c40 	cmp	r1, r0, asr #24
    2a78:	0affff98 	beq	0x28e0
    2a7c:	e3a00009 	mov	r0, #9	; 0x9
    2a80:	e59f31ac 	ldr	r3, [pc, #428]	; 0x2c34
    2a84:	e7932100 	ldr	r2, [r3, r0, lsl #2]
    2a88:	e3520000 	cmp	r2, #0	; 0x0
    2a8c:	0a000007 	beq	0x2ab0
    2a90:	e5922008 	ldr	r2, [r2, #8]
    2a94:	e1d231de 	ldrsb	r3, [r2, #30]
    2a98:	e1530001 	cmp	r3, r1
    2a9c:	0a000006 	beq	0x2abc
    2aa0:	e59fe18c 	ldr	lr, [pc, #396]	; 0x2c34
    2aa4:	e79e3100 	ldr	r3, [lr, r0, lsl #2]
    2aa8:	e1530002 	cmp	r3, r2
    2aac:	1afffff7 	bne	0x2a90
    2ab0:	e2500001 	subs	r0, r0, #1	; 0x1
    2ab4:	5afffff1 	bpl	0x2a80
    2ab8:	eaffff88 	b	0x28e0
    2abc:	e1d231df 	ldrsb	r3, [r2, #31]
    2ac0:	e3530003 	cmp	r3, #3	; 0x3
    2ac4:	0a00002b 	beq	0x2b78
    2ac8:	e3530004 	cmp	r3, #4	; 0x4
    2acc:	1afffff3 	bne	0x2aa0
    2ad0:	e592300c 	ldr	r3, [r2, #12]
    2ad4:	e5d31005 	ldrb	r1, [r3, #5]
    2ad8:	e3510000 	cmp	r1, #0	; 0x0
    2adc:	1affffe3 	bne	0x2a70
    2ae0:	eaffffee 	b	0x2aa0
    2ae4:	e2810001 	add	r0, r1, #1	; 0x1
    2ae8:	e5c42005 	strb	r2, [r4, #5]
    2aec:	e5c40004 	strb	r0, [r4, #4]
    2af0:	eaffff74 	b	0x28c8
    2af4:	ebfff545 	bl	0x10
    2af8:	e5951010 	ldr	r1, [r5, #16]
    2afc:	e1500001 	cmp	r0, r1
    2b00:	3affff76 	bcc	0x28e0
    2b04:	e5d53021 	ldrb	r3, [r5, #33]
    2b08:	eaffffcc 	b	0x2a40
    2b0c:	e5920018 	ldr	r0, [r2, #24]
    2b10:	eb000414 	bl	0x3b68
    2b14:	e59a1000 	ldr	r1, [sl]
    2b18:	e5910014 	ldr	r0, [r1, #20]
    2b1c:	eb000411 	bl	0x3b68
    2b20:	e59a1000 	ldr	r1, [sl]
    2b24:	e5d1e020 	ldrb	lr, [r1, #32]
    2b28:	e24ec001 	sub	ip, lr, #1	; 0x1
    2b2c:	e20c00ff 	and	r0, ip, #255	; 0xff
    2b30:	e59f20fc 	ldr	r2, [pc, #252]	; 0x2c34
    2b34:	e1a07100 	mov	r7, r0, lsl #2
    2b38:	e7970002 	ldr	r0, [r7, r2]
    2b3c:	e59fe0f0 	ldr	lr, [pc, #240]	; 0x2c34
    2b40:	e5902008 	ldr	r2, [r0, #8]
    2b44:	e5818014 	str	r8, [r1, #20]
    2b48:	e5818018 	str	r8, [r1, #24]
    2b4c:	e1520001 	cmp	r2, r1
    2b50:	e087c00e 	add	ip, r7, lr
    2b54:	0a00002c 	beq	0x2c0c
    2b58:	e1a00002 	mov	r0, r2
    2b5c:	e5922008 	ldr	r2, [r2, #8]
    2b60:	e1520001 	cmp	r2, r1
    2b64:	eafffffa 	b	0x2b54
    2b68:	e59fe0d8 	ldr	lr, [pc, #216]	; 0x2c48
    2b6c:	e59e0000 	ldr	r0, [lr]
    2b70:	eb000134 	bl	0x3048
    2b74:	eaffff87 	b	0x2998
    2b78:	e58a2000 	str	r2, [sl]
    2b7c:	eaffff57 	b	0x28e0
    2b80:	e59f30c4 	ldr	r3, [pc, #196]	; 0x2c4c
    2b84:	e59f40c4 	ldr	r4, [pc, #196]	; 0x2c50
    2b88:	e593c000 	ldr	ip, [r3]
    2b8c:	e5d40000 	ldrb	r0, [r4]
    2b90:	e1dce0bc 	ldrh	lr, [ip, #12]
    2b94:	e08e2000 	add	r2, lr, r0
    2b98:	e7d2100c 	ldrb	r1, [r2, ip]
    2b9c:	e0813101 	add	r3, r1, r1, lsl #2
    2ba0:	e08c4083 	add	r4, ip, r3, lsl #1
    2ba4:	e5990000 	ldr	r0, [r9]
    2ba8:	e2844010 	add	r4, r4, #16	; 0x10
    2bac:	e5806000 	str	r6, [r0]
    2bb0:	e1a01006 	mov	r1, r6
    2bb4:	e1a00004 	mov	r0, r4
    2bb8:	eb0004bb 	bl	0x3eac
    2bbc:	e1a01006 	mov	r1, r6
    2bc0:	eb0004e1 	bl	0x3f4c
    2bc4:	e59fe064 	ldr	lr, [pc, #100]	; 0x2c30
    2bc8:	e1a00004 	mov	r0, r4
    2bcc:	e59e1000 	ldr	r1, [lr]
    2bd0:	eb0005bb 	bl	0x42c4
    2bd4:	eaffff87 	b	0x29f8
    2bd8:	e59fe074 	ldr	lr, [pc, #116]	; 0x2c54
    2bdc:	e59e0000 	ldr	r0, [lr]
    2be0:	eb000118 	bl	0x3048
    2be4:	eaffff37 	b	0x28c8
    2be8:	e59f004c 	ldr	r0, [pc, #76]	; 0x2c3c
    2bec:	e3a02001 	mov	r2, #1	; 0x1
    2bf0:	e59f3040 	ldr	r3, [pc, #64]	; 0x2c38
    2bf4:	e5c02000 	strb	r2, [r0]
    2bf8:	e3a01002 	mov	r1, #2	; 0x2
    2bfc:	e1a00008 	mov	r0, r8
    2c00:	e5c31000 	strb	r1, [r3]
    2c04:	e58a8000 	str	r8, [sl]
    2c08:	eaffff63 	b	0x299c
    2c0c:	e1500001 	cmp	r0, r1
    2c10:	15913008 	ldrne	r3, [r1, #8]
    2c14:	03a03000 	moveq	r3, #0	; 0x0
    2c18:	058c3000 	streq	r3, [ip]
    2c1c:	158c0000 	strne	r0, [ip]
    2c20:	15803008 	strne	r3, [r0, #8]
    2c24:	eafffeec 	b	0x27dc
    2c28:	002073ec 	eoreq	r7, r0, ip, ror #7
    2c2c:	0020739c 	mlaeq	r0, ip, r3, r7
    2c30:	002073b8 	streqh	r7, [r0], -r8
    2c34:	002073c4 	eoreq	r7, r0, r4, asr #7
    2c38:	002073a2 	eoreq	r7, r0, r2, lsr #7
    2c3c:	002073a3 	eoreq	r7, r0, r3, lsr #7
    2c40:	002073b4 	streqh	r7, [r0], -r4
    2c44:	00207394 	mlaeq	r0, r4, r3, r7
    2c48:	0020741c 	eoreq	r7, r0, ip, lsl r4
    2c4c:	00207424 	eoreq	r7, r0, r4, lsr #8
    2c50:	002073f1 	streqd	r7, [r0], -r1
    2c54:	00207400 	eoreq	r7, r0, r0, lsl #8
    2c58:	e1a0c00d 	mov	ip, sp
    2c5c:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    2c60:	e59fe08c 	ldr	lr, [pc, #140]	; 0x2cf4
    2c64:	e24cb004 	sub	fp, ip, #4	; 0x4
    2c68:	e59ec000 	ldr	ip, [lr]
    2c6c:	e5d02005 	ldrb	r2, [r0, #5]
    2c70:	e1dc31de 	ldrsb	r3, [ip, #30]
    2c74:	e1530002 	cmp	r3, r2
    2c78:	e1a05000 	mov	r5, r0
    2c7c:	e1a06001 	mov	r6, r1
    2c80:	0a000004 	beq	0x2c98
    2c84:	e59f106c 	ldr	r1, [pc, #108]	; 0x2cf8
    2c88:	e5910000 	ldr	r0, [r1]
    2c8c:	eb0000ed 	bl	0x3048
    2c90:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    2c94:	e12fff1e 	bx	lr
    2c98:	e3a02005 	mov	r2, #5	; 0x5
    2c9c:	e5cc201f 	strb	r2, [ip, #31]
    2ca0:	e5d04004 	ldrb	r4, [r0, #4]
    2ca4:	e59e0000 	ldr	r0, [lr]
    2ca8:	e5c0401d 	strb	r4, [r0, #29]
    2cac:	e59e4000 	ldr	r4, [lr]
    2cb0:	e3510000 	cmp	r1, #0	; 0x0
    2cb4:	e584500c 	str	r5, [r4, #12]
    2cb8:	05841010 	streq	r1, [r4, #16]
    2cbc:	1a000008 	bne	0x2ce4
    2cc0:	e59f0034 	ldr	r0, [pc, #52]	; 0x2cfc
    2cc4:	e59f3034 	ldr	r3, [pc, #52]	; 0x2d00
    2cc8:	e3a02000 	mov	r2, #0	; 0x0
    2ccc:	e3a01001 	mov	r1, #1	; 0x1
    2cd0:	e5c52004 	strb	r2, [r5, #4]
    2cd4:	e5c52005 	strb	r2, [r5, #5]
    2cd8:	e5c01000 	strb	r1, [r0]
    2cdc:	e5c31000 	strb	r1, [r3]
    2ce0:	eaffffea 	b	0x2c90
    2ce4:	ebfff4c9 	bl	0x10
    2ce8:	e080c006 	add	ip, r0, r6
    2cec:	e584c010 	str	ip, [r4, #16]
    2cf0:	eafffff2 	b	0x2cc0
    2cf4:	002073ec 	eoreq	r7, r0, ip, ror #7
    2cf8:	00207420 	eoreq	r7, r0, r0, lsr #8
    2cfc:	002073a2 	eoreq	r7, r0, r2, lsr #7
    2d00:	002073a3 	eoreq	r7, r0, r3, lsr #7
    2d04:	e92d4070 	stmdb	sp!, {r4, r5, r6, lr}
    2d08:	e3a04009 	mov	r4, #9	; 0x9
    2d0c:	e59fe084 	ldr	lr, [pc, #132]	; 0x2d98
    2d10:	e20160ff 	and	r6, r1, #255	; 0xff
    2d14:	e1a02004 	mov	r2, r4
    2d18:	e79e2102 	ldr	r2, [lr, r2, lsl #2]
    2d1c:	e3520000 	cmp	r2, #0	; 0x0
    2d20:	0a000016 	beq	0x2d80
    2d24:	e1a03804 	mov	r3, r4, lsl #16
    2d28:	e1a0c843 	mov	ip, r3, asr #16
    2d2c:	e3a05002 	mov	r5, #2	; 0x2
    2d30:	e3a01004 	mov	r1, #4	; 0x4
    2d34:	ea000002 	b	0x2d44
    2d38:	e79e310c 	ldr	r3, [lr, ip, lsl #2]
    2d3c:	e1530002 	cmp	r3, r2
    2d40:	0a00000e 	beq	0x2d80
    2d44:	e5922008 	ldr	r2, [r2, #8]
    2d48:	e1d231df 	ldrsb	r3, [r2, #31]
    2d4c:	e3530005 	cmp	r3, #5	; 0x5
    2d50:	1afffff8 	bne	0x2d38
    2d54:	e592300c 	ldr	r3, [r2, #12]
    2d58:	e1530000 	cmp	r3, r0
    2d5c:	1afffff5 	bne	0x2d38
    2d60:	e1d232d1 	ldrsb	r3, [r2, #33]
    2d64:	e3530000 	cmp	r3, #0	; 0x0
    2d68:	15c25021 	strneb	r5, [r2, #33]
    2d6c:	e3560000 	cmp	r6, #0	; 0x0
    2d70:	e5c2101f 	strb	r1, [r2, #31]
    2d74:	1affffef 	bne	0x2d38
    2d78:	e8bd4070 	ldmia	sp!, {r4, r5, r6, lr}
    2d7c:	e12fff1e 	bx	lr
    2d80:	e2442001 	sub	r2, r4, #1	; 0x1
    2d84:	e1a01802 	mov	r1, r2, lsl #16
    2d88:	e1b02841 	movs	r2, r1, asr #16
    2d8c:	e1a04821 	mov	r4, r1, lsr #16
    2d90:	5affffe0 	bpl	0x2d18
    2d94:	eafffff7 	b	0x2d78
    2d98:	002073c4 	eoreq	r7, r0, r4, asr #7
    2d9c:	e1a0c00d 	mov	ip, sp
    2da0:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    2da4:	e59f40c0 	ldr	r4, [pc, #192]	; 0x2e6c
    2da8:	e5942000 	ldr	r2, [r4]
    2dac:	e1a04000 	mov	r4, r0
    2db0:	e5d43005 	ldrb	r3, [r4, #5]
    2db4:	e1d201de 	ldrsb	r0, [r2, #30]
    2db8:	e1500003 	cmp	r0, r3
    2dbc:	e24cb004 	sub	fp, ip, #4	; 0x4
    2dc0:	e20160ff 	and	r6, r1, #255	; 0xff
    2dc4:	0a000004 	beq	0x2ddc
    2dc8:	e59f10a0 	ldr	r1, [pc, #160]	; 0x2e70
    2dcc:	e5910000 	ldr	r0, [r1]
    2dd0:	eb00009c 	bl	0x3048
    2dd4:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    2dd8:	e12fff1e 	bx	lr
    2ddc:	e3a0e009 	mov	lr, #9	; 0x9
    2de0:	e59fc08c 	ldr	ip, [pc, #140]	; 0x2e74
    2de4:	e1a0200e 	mov	r2, lr
    2de8:	e79c2102 	ldr	r2, [ip, r2, lsl #2]
    2dec:	e3520000 	cmp	r2, #0	; 0x0
    2df0:	0a000017 	beq	0x2e54
    2df4:	e1a0580e 	mov	r5, lr, lsl #16
    2df8:	e1a00845 	mov	r0, r5, asr #16
    2dfc:	e3a01004 	mov	r1, #4	; 0x4
    2e00:	e3a05002 	mov	r5, #2	; 0x2
    2e04:	ea000002 	b	0x2e14
    2e08:	e79c3100 	ldr	r3, [ip, r0, lsl #2]
    2e0c:	e1530002 	cmp	r3, r2
    2e10:	0a00000f 	beq	0x2e54
    2e14:	e5922008 	ldr	r2, [r2, #8]
    2e18:	e1d231df 	ldrsb	r3, [r2, #31]
    2e1c:	e3530005 	cmp	r3, #5	; 0x5
    2e20:	1afffff8 	bne	0x2e08
    2e24:	e592300c 	ldr	r3, [r2, #12]
    2e28:	e1530004 	cmp	r3, r4
    2e2c:	1afffff5 	bne	0x2e08
    2e30:	e1d232d1 	ldrsb	r3, [r2, #33]
    2e34:	e3530000 	cmp	r3, #0	; 0x0
    2e38:	15c25021 	strneb	r5, [r2, #33]
    2e3c:	e3560000 	cmp	r6, #0	; 0x0
    2e40:	e5c2101f 	strb	r1, [r2, #31]
    2e44:	0affffe2 	beq	0x2dd4
    2e48:	e79c3100 	ldr	r3, [ip, r0, lsl #2]
    2e4c:	e1530002 	cmp	r3, r2
    2e50:	1affffef 	bne	0x2e14
    2e54:	e24e2001 	sub	r2, lr, #1	; 0x1
    2e58:	e1a00802 	mov	r0, r2, lsl #16
    2e5c:	e1b02840 	movs	r2, r0, asr #16
    2e60:	e1a0e820 	mov	lr, r0, lsr #16
    2e64:	5affffdf 	bpl	0x2de8
    2e68:	eaffffd9 	b	0x2dd4
    2e6c:	002073ec 	eoreq	r7, r0, ip, ror #7
    2e70:	00207420 	eoreq	r7, r0, r0, lsr #8
    2e74:	002073c4 	eoreq	r7, r0, r4, asr #7
    2e78:	e3510000 	cmp	r1, #0	; 0x0
    2e7c:	012fff1e 	bxeq	lr
    2e80:	e5d12004 	ldrb	r2, [r1, #4]
    2e84:	e2420001 	sub	r0, r2, #1	; 0x1
    2e88:	e21030ff 	ands	r3, r0, #255	; 0xff
    2e8c:	05c13005 	streqb	r3, [r1, #5]
    2e90:	e5c13004 	strb	r3, [r1, #4]
    2e94:	e12fff1e 	bx	lr
    2e98:	e12fff1e 	bx	lr
    2e9c:	e1d032d0 	ldrsb	r3, [r0, #32]
    2ea0:	e1530001 	cmp	r3, r1
    2ea4:	e92d4010 	stmdb	sp!, {r4, lr}
    2ea8:	e5d02020 	ldrb	r2, [r0, #32]
    2eac:	0a000021 	beq	0x2f38
    2eb0:	e1d0c1df 	ldrsb	ip, [r0, #31]
    2eb4:	e35c0000 	cmp	ip, #0	; 0x0
    2eb8:	05c01020 	streqb	r1, [r0, #32]
    2ebc:	0a00001d 	beq	0x2f38
    2ec0:	e2424001 	sub	r4, r2, #1	; 0x1
    2ec4:	e20420ff 	and	r2, r4, #255	; 0xff
    2ec8:	e59fe070 	ldr	lr, [pc, #112]	; 0x2f40
    2ecc:	e1a03102 	mov	r3, r2, lsl #2
    2ed0:	e793c00e 	ldr	ip, [r3, lr]
    2ed4:	e59c2008 	ldr	r2, [ip, #8]
    2ed8:	e1520000 	cmp	r2, r0
    2edc:	e083400e 	add	r4, r3, lr
    2ee0:	0a000003 	beq	0x2ef4
    2ee4:	e1a0c002 	mov	ip, r2
    2ee8:	e5922008 	ldr	r2, [r2, #8]
    2eec:	e1520000 	cmp	r2, r0
    2ef0:	1afffffb 	bne	0x2ee4
    2ef4:	e15c0000 	cmp	ip, r0
    2ef8:	03a03000 	moveq	r3, #0	; 0x0
    2efc:	05843000 	streq	r3, [r4]
    2f00:	1584c000 	strne	ip, [r4]
    2f04:	e5c01020 	strb	r1, [r0, #32]
    2f08:	15903008 	ldrne	r3, [r0, #8]
    2f0c:	e5d02020 	ldrb	r2, [r0, #32]
    2f10:	158c3008 	strne	r3, [ip, #8]
    2f14:	e2423001 	sub	r3, r2, #1	; 0x1
    2f18:	e203c0ff 	and	ip, r3, #255	; 0xff
    2f1c:	e79e110c 	ldr	r1, [lr, ip, lsl #2]
    2f20:	e3510000 	cmp	r1, #0	; 0x0
    2f24:	15913008 	ldrne	r3, [r1, #8]
    2f28:	e78e010c 	str	r0, [lr, ip, lsl #2]
    2f2c:	15803008 	strne	r3, [r0, #8]
    2f30:	05800008 	streq	r0, [r0, #8]
    2f34:	15810008 	strne	r0, [r1, #8]
    2f38:	e8bd4010 	ldmia	sp!, {r4, lr}
    2f3c:	e12fff1e 	bx	lr
    2f40:	002073c4 	eoreq	r7, r0, r4, asr #7
    2f44:	e1a0c00d 	mov	ip, sp
    2f48:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
    2f4c:	e3a00005 	mov	r0, #5	; 0x5
    2f50:	e24cb004 	sub	fp, ip, #4	; 0x4
    2f54:	eb00020e 	bl	0x3794
    2f58:	e59f10b8 	ldr	r1, [pc, #184]	; 0x3018
    2f5c:	e5810000 	str	r0, [r1]
    2f60:	e3a00006 	mov	r0, #6	; 0x6
    2f64:	eb00020a 	bl	0x3794
    2f68:	e59f30ac 	ldr	r3, [pc, #172]	; 0x301c
    2f6c:	e5830000 	str	r0, [r3]
    2f70:	e3a00007 	mov	r0, #7	; 0x7
    2f74:	eb000206 	bl	0x3794
    2f78:	e59fe0a0 	ldr	lr, [pc, #160]	; 0x3020
    2f7c:	e58e0000 	str	r0, [lr]
    2f80:	e3a00008 	mov	r0, #8	; 0x8
    2f84:	eb000202 	bl	0x3794
    2f88:	e59fc094 	ldr	ip, [pc, #148]	; 0x3024
    2f8c:	e58c0000 	str	r0, [ip]
    2f90:	e3a00009 	mov	r0, #9	; 0x9
    2f94:	eb0001fe 	bl	0x3794
    2f98:	e59f2088 	ldr	r2, [pc, #136]	; 0x3028
    2f9c:	e5820000 	str	r0, [r2]
    2fa0:	e3a0000a 	mov	r0, #10	; 0xa
    2fa4:	eb0001fa 	bl	0x3794
    2fa8:	e59f107c 	ldr	r1, [pc, #124]	; 0x302c
    2fac:	e5810000 	str	r0, [r1]
    2fb0:	e3a0000b 	mov	r0, #11	; 0xb
    2fb4:	eb0001f6 	bl	0x3794
    2fb8:	e59f3070 	ldr	r3, [pc, #112]	; 0x3030
    2fbc:	e5830000 	str	r0, [r3]
    2fc0:	e3a0000c 	mov	r0, #12	; 0xc
    2fc4:	eb0001f2 	bl	0x3794
    2fc8:	e59fe064 	ldr	lr, [pc, #100]	; 0x3034
    2fcc:	e58e0000 	str	r0, [lr]
    2fd0:	e3a0000d 	mov	r0, #13	; 0xd
    2fd4:	eb0001ee 	bl	0x3794
    2fd8:	e59fc058 	ldr	ip, [pc, #88]	; 0x3038
    2fdc:	e58c0000 	str	r0, [ip]
    2fe0:	e3a0000e 	mov	r0, #14	; 0xe
    2fe4:	eb0001ea 	bl	0x3794
    2fe8:	e59f204c 	ldr	r2, [pc, #76]	; 0x303c
    2fec:	e5820000 	str	r0, [r2]
    2ff0:	e3a0000f 	mov	r0, #15	; 0xf
    2ff4:	eb0001e6 	bl	0x3794
    2ff8:	e59f1040 	ldr	r1, [pc, #64]	; 0x3040
    2ffc:	e5810000 	str	r0, [r1]
    3000:	e3a00004 	mov	r0, #4	; 0x4
    3004:	eb0001e2 	bl	0x3794
    3008:	e59f3034 	ldr	r3, [pc, #52]	; 0x3044
    300c:	e5830000 	str	r0, [r3]
    3010:	e89d6800 	ldmia	sp, {fp, sp, lr}
    3014:	e12fff1e 	bx	lr
    3018:	002073f8 	streqd	r7, [r0], -r8
    301c:	002073f4 	streqd	r7, [r0], -r4
    3020:	00207418 	eoreq	r7, r0, r8, lsl r4
    3024:	00207400 	eoreq	r7, r0, r0, lsl #8
    3028:	002073fc 	streqd	r7, [r0], -ip
    302c:	0020740c 	eoreq	r7, r0, ip, lsl #8
    3030:	00207410 	eoreq	r7, r0, r0, lsl r4
    3034:	00207408 	eoreq	r7, r0, r8, lsl #8
    3038:	0020741c 	eoreq	r7, r0, ip, lsl r4
    303c:	00207404 	eoreq	r7, r0, r4, lsl #8
    3040:	00207420 	eoreq	r7, r0, r0, lsr #8
    3044:	00207414 	eoreq	r7, r0, r4, lsl r4
    3048:	e1a0c00d 	mov	ip, sp
    304c:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    3050:	e59f21c4 	ldr	r2, [pc, #452]	; 0x321c
    3054:	e24cb004 	sub	fp, ip, #4	; 0x4
    3058:	e24dd004 	sub	sp, sp, #4	; 0x4
    305c:	e5921000 	ldr	r1, [r2]
    3060:	e3510000 	cmp	r1, #0	; 0x0
    3064:	e1a07000 	mov	r7, r0
    3068:	0a000050 	beq	0x31b0
    306c:	e59f81ac 	ldr	r8, [pc, #428]	; 0x3220
    3070:	e5985000 	ldr	r5, [r8]
    3074:	e1500005 	cmp	r0, r5
    3078:	03a03000 	moveq	r3, #0	; 0x0
    307c:	05c13021 	streqb	r3, [r1, #33]
    3080:	e59f419c 	ldr	r4, [pc, #412]	; 0x3224
    3084:	e59fa19c 	ldr	sl, [pc, #412]	; 0x3228
    3088:	e5940000 	ldr	r0, [r4]
    308c:	e59f1198 	ldr	r1, [pc, #408]	; 0x322c
    3090:	e3a03000 	mov	r3, #0	; 0x0
    3094:	e5810000 	str	r0, [r1]
    3098:	e58a3000 	str	r3, [sl]
    309c:	e59f918c 	ldr	r9, [pc, #396]	; 0x3230
    30a0:	e59f418c 	ldr	r4, [pc, #396]	; 0x3234
    30a4:	e59f818c 	ldr	r8, [pc, #396]	; 0x3238
    30a8:	e59f518c 	ldr	r5, [pc, #396]	; 0x323c
    30ac:	ebfffd03 	bl	0x24c0
    30b0:	e59a1000 	ldr	r1, [sl]
    30b4:	e5902000 	ldr	r2, [r0]
    30b8:	e59fc180 	ldr	ip, [pc, #384]	; 0x3240
    30bc:	e3510000 	cmp	r1, #0	; 0x0
    30c0:	e58c0000 	str	r0, [ip]
    30c4:	058a2000 	streq	r2, [sl]
    30c8:	e5892000 	str	r2, [r9]
    30cc:	e5d2e009 	ldrb	lr, [r2, #9]
    30d0:	e1d2c0b2 	ldrh	ip, [r2, #2]
    30d4:	e1d220b4 	ldrh	r2, [r2, #4]
    30d8:	e5c5e000 	strb	lr, [r5]
    30dc:	e59f3160 	ldr	r3, [pc, #352]	; 0x3244
    30e0:	e5d50000 	ldrb	r0, [r5]
    30e4:	e59f1138 	ldr	r1, [pc, #312]	; 0x3224
    30e8:	e593e000 	ldr	lr, [r3]
    30ec:	e2400001 	sub	r0, r0, #1	; 0x1
    30f0:	e5913000 	ldr	r3, [r1]
    30f4:	e082200e 	add	r2, r2, lr
    30f8:	e20010ff 	and	r1, r0, #255	; 0xff
    30fc:	e0623003 	rsb	r3, r2, r3
    3100:	e08cc00e 	add	ip, ip, lr
    3104:	e35100ff 	cmp	r1, #255	; 0xff
    3108:	e584c000 	str	ip, [r4]
    310c:	e1c830b0 	strh	r3, [r8]
    3110:	e5c50000 	strb	r0, [r5]
    3114:	0a000011 	beq	0x3160
    3118:	e5942000 	ldr	r2, [r4]
    311c:	e1d810b0 	ldrh	r1, [r8]
    3120:	e1d200b0 	ldrh	r0, [r2]
    3124:	e1500001 	cmp	r0, r1
    3128:	8a000003 	bhi	0x313c
    312c:	e1d230b2 	ldrh	r3, [r2, #2]
    3130:	e1530001 	cmp	r3, r1
    3134:	e1a00007 	mov	r0, r7
    3138:	2a00001f 	bcs	0x31bc
    313c:	e5d53000 	ldrb	r3, [r5]
    3140:	e5940000 	ldr	r0, [r4]
    3144:	e243e001 	sub	lr, r3, #1	; 0x1
    3148:	e20ec0ff 	and	ip, lr, #255	; 0xff
    314c:	e2801008 	add	r1, r0, #8	; 0x8
    3150:	e35c00ff 	cmp	ip, #255	; 0xff
    3154:	e5841000 	str	r1, [r4]
    3158:	e5c5e000 	strb	lr, [r5]
    315c:	1affffed 	bne	0x3118
    3160:	e59fe0b4 	ldr	lr, [pc, #180]	; 0x321c
    3164:	e59e6000 	ldr	r6, [lr]
    3168:	e3a00000 	mov	r0, #0	; 0x0
    316c:	eb0004d2 	bl	0x44bc
    3170:	e1d621df 	ldrsb	r2, [r6, #31]
    3174:	e3520001 	cmp	r2, #1	; 0x1
    3178:	1affffcb 	bne	0x30ac
    317c:	e1a00007 	mov	r0, r7
    3180:	eb000347 	bl	0x3ea4
    3184:	e20010ff 	and	r1, r0, #255	; 0xff
    3188:	e3510010 	cmp	r1, #16	; 0x10
    318c:	0a000007 	beq	0x31b0
    3190:	e59f0094 	ldr	r0, [pc, #148]	; 0x322c
    3194:	e590c000 	ldr	ip, [r0]
    3198:	e59a2000 	ldr	r2, [sl]
    319c:	e5993000 	ldr	r3, [r9]
    31a0:	e1a00007 	mov	r0, r7
    31a4:	e1a01006 	mov	r1, r6
    31a8:	e58dc000 	str	ip, [sp]
    31ac:	ebfff39b 	bl	0x20
    31b0:	e24bd028 	sub	sp, fp, #40	; 0x28
    31b4:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    31b8:	e12fff1e 	bx	lr
    31bc:	e5d21006 	ldrb	r1, [r2, #6]
    31c0:	eb0004f0 	bl	0x4588
    31c4:	e3500000 	cmp	r0, #0	; 0x0
    31c8:	0affffdb 	beq	0x313c
    31cc:	e59f206c 	ldr	r2, [pc, #108]	; 0x3240
    31d0:	e599c000 	ldr	ip, [r9]
    31d4:	e592e000 	ldr	lr, [r2]
    31d8:	e59f3064 	ldr	r3, [pc, #100]	; 0x3244
    31dc:	e5dc2006 	ldrb	r2, [ip, #6]
    31e0:	e5944000 	ldr	r4, [r4]
    31e4:	e5930000 	ldr	r0, [r3]
    31e8:	e1dc10b4 	ldrh	r1, [ip, #4]
    31ec:	e59ec008 	ldr	ip, [lr, #8]
    31f0:	e1a02102 	mov	r2, r2, lsl #2
    31f4:	e1d430b4 	ldrh	r3, [r4, #4]
    31f8:	e0811000 	add	r1, r1, r0
    31fc:	e78c7002 	str	r7, [ip, r2]
    3200:	e08c0002 	add	r0, ip, r2
    3204:	e59fc03c 	ldr	ip, [pc, #60]	; 0x3248
    3208:	e59f2014 	ldr	r2, [pc, #20]	; 0x3224
    320c:	e0811003 	add	r1, r1, r3
    3210:	e58c0000 	str	r0, [ip]
    3214:	e5821000 	str	r1, [r2]
    3218:	eaffffe4 	b	0x31b0
    321c:	002073ec 	eoreq	r7, r0, ip, ror #7
    3220:	0020741c 	eoreq	r7, r0, ip, lsl r4
    3224:	002073b8 	streqh	r7, [r0], -r8
    3228:	0020734c 	eoreq	r7, r0, ip, asr #6
    322c:	00207360 	eoreq	r7, r0, r0, ror #6
    3230:	00207348 	eoreq	r7, r0, r8, asr #6
    3234:	00207358 	eoreq	r7, r0, r8, asr r3
    3238:	00207350 	eoreq	r7, r0, r0, asr r3
    323c:	0020735c 	eoreq	r7, r0, ip, asr r3
    3240:	00207354 	eoreq	r7, r0, r4, asr r3
    3244:	00207424 	eoreq	r7, r0, r4, lsr #8
    3248:	0020739c 	mlaeq	r0, ip, r3, r7
    324c:	e1a02801 	mov	r2, r1, lsl #16
    3250:	e2421801 	sub	r1, r2, #65536	; 0x10000
    3254:	e3a03cff 	mov	r3, #65280	; 0xff00
    3258:	e1a01821 	mov	r1, r1, lsr #16
    325c:	e28330ff 	add	r3, r3, #255	; 0xff
    3260:	e1510003 	cmp	r1, r3
    3264:	012fff1e 	bxeq	lr
    3268:	e1a02003 	mov	r2, r3
    326c:	e241c001 	sub	ip, r1, #1	; 0x1
    3270:	e1a0380c 	mov	r3, ip, lsl #16
    3274:	e1a01823 	mov	r1, r3, lsr #16
    3278:	e3a0c000 	mov	ip, #0	; 0x0
    327c:	e1510002 	cmp	r1, r2
    3280:	e0c0c0b2 	strh	ip, [r0], #2
    3284:	1afffff8 	bne	0x326c
    3288:	e12fff1e 	bx	lr
    328c:	e59f3028 	ldr	r3, [pc, #40]	; 0x32bc
    3290:	e20110ff 	and	r1, r1, #255	; 0xff
    3294:	e7d32001 	ldrb	r2, [r3, r1]
    3298:	e1a00800 	mov	r0, r0, lsl #16
    329c:	e1a0c820 	mov	ip, r0, lsr #16
    32a0:	e00c0c92 	mul	ip, r2, ip
    32a4:	e28c3001 	add	r3, ip, #1	; 0x1
    32a8:	e1a020c3 	mov	r2, r3, asr #1
    32ac:	e2821004 	add	r1, r2, #4	; 0x4
    32b0:	e1a00801 	mov	r0, r1, lsl #16
    32b4:	e1a00820 	mov	r0, r0, lsr #16
    32b8:	e12fff1e 	bx	lr
    32bc:	00207338 	eoreq	r7, r0, r8, lsr r3
    32c0:	e1d030b0 	ldrh	r3, [r0]
    32c4:	e59f0028 	ldr	r0, [pc, #40]	; 0x32f4
    32c8:	e2031c1e 	and	r1, r3, #7680	; 0x1e00
    32cc:	e7d0c4a1 	ldrb	ip, [r0, r1, lsr #9]
    32d0:	e1a02b83 	mov	r2, r3, lsl #23
    32d4:	e1a01ba2 	mov	r1, r2, lsr #23
    32d8:	e000019c 	mul	r0, ip, r1
    32dc:	e2803001 	add	r3, r0, #1	; 0x1
    32e0:	e1a020c3 	mov	r2, r3, asr #1
    32e4:	e2821004 	add	r1, r2, #4	; 0x4
    32e8:	e1a00801 	mov	r0, r1, lsl #16
    32ec:	e1a00820 	mov	r0, r0, lsr #16
    32f0:	e12fff1e 	bx	lr
    32f4:	00207338 	eoreq	r7, r0, r8, lsr r3
    32f8:	e92d4070 	stmdb	sp!, {r4, r5, r6, lr}
    32fc:	e59f3120 	ldr	r3, [pc, #288]	; 0x3424
    3300:	e5932000 	ldr	r2, [r3]
    3304:	e5924000 	ldr	r4, [r2]
    3308:	e282e004 	add	lr, r2, #4	; 0x4
    330c:	e1a00800 	mov	r0, r0, lsl #16
    3310:	e15e0004 	cmp	lr, r4
    3314:	e1a05820 	mov	r5, r0, lsr #16
    3318:	2a000009 	bcs	0x3344
    331c:	e1de10b0 	ldrh	r1, [lr]
    3320:	e1a02801 	mov	r2, r1, lsl #16
    3324:	e3520000 	cmp	r2, #0	; 0x0
    3328:	e1a0c822 	mov	ip, r2, lsr #16
    332c:	ba000022 	blt	0x33bc
    3330:	e155000c 	cmp	r5, ip
    3334:	9a000007 	bls	0x3358
    3338:	e08ee08c 	add	lr, lr, ip, lsl #1
    333c:	e15e0004 	cmp	lr, r4
    3340:	3afffff5 	bcc	0x331c
    3344:	e3a00000 	mov	r0, #0	; 0x0
    3348:	e8bd4070 	ldmia	sp!, {r4, r5, r6, lr}
    334c:	e12fff1e 	bx	lr
    3350:	e1a0c823 	mov	ip, r3, lsr #16
    3354:	e1cec0b0 	strh	ip, [lr]
    3358:	e1a0008c 	mov	r0, ip, lsl #1
    335c:	e19030fe 	ldrsh	r3, [r0, lr]
    3360:	e080100e 	add	r1, r0, lr
    3364:	e1a02fa3 	mov	r2, r3, lsr #31
    3368:	e19030be 	ldrh	r3, [r0, lr]
    336c:	e1510004 	cmp	r1, r4
    3370:	23822001 	orrcs	r2, r2, #1	; 0x1
    3374:	e3520000 	cmp	r2, #0	; 0x0
    3378:	e08c1003 	add	r1, ip, r3
    337c:	e1a03801 	mov	r3, r1, lsl #16
    3380:	0afffff2 	beq	0x3350
    3384:	e155000c 	cmp	r5, ip
    3388:	2a000005 	bcs	0x33a4
    338c:	e065200c 	rsb	r2, r5, ip
    3390:	e1a00802 	mov	r0, r2, lsl #16
    3394:	e1a0c820 	mov	ip, r0, lsr #16
    3398:	e1cec0b0 	strh	ip, [lr]
    339c:	e08ee08c 	add	lr, lr, ip, lsl #1
    33a0:	e1ce50b0 	strh	r5, [lr]
    33a4:	e59f107c 	ldr	r1, [pc, #124]	; 0x3428
    33a8:	e1d1c0b0 	ldrh	ip, [r1]
    33ac:	e1a0000e 	mov	r0, lr
    33b0:	e065300c 	rsb	r3, r5, ip
    33b4:	e1c130b0 	strh	r3, [r1]
    33b8:	eaffffe2 	b	0x3348
    33bc:	e1a00b8c 	mov	r0, ip, lsl #23
    33c0:	e20c30ff 	and	r3, ip, #255	; 0xff
    33c4:	e3120101 	tst	r2, #1073741824	; 0x40000000
    33c8:	e20c6c1e 	and	r6, ip, #7680	; 0x1e00
    33cc:	e1a00ba0 	mov	r0, r0, lsr #23
    33d0:	e0831103 	add	r1, r3, r3, lsl #2
    33d4:	0a00000a 	beq	0x3404
    33d8:	e59f104c 	ldr	r1, [pc, #76]	; 0x342c
    33dc:	e7d124a6 	ldrb	r2, [r1, r6, lsr #9]
    33e0:	e00c0092 	mul	ip, r2, r0
    33e4:	e28c3001 	add	r3, ip, #1	; 0x1
    33e8:	e1a010c3 	mov	r1, r3, asr #1
    33ec:	e2812004 	add	r2, r1, #4	; 0x4
    33f0:	e1a0c802 	mov	ip, r2, lsl #16
    33f4:	e1a0182c 	mov	r1, ip, lsr #16
    33f8:	e08ee081 	add	lr, lr, r1, lsl #1
    33fc:	e15e0004 	cmp	lr, r4
    3400:	eaffffce 	b	0x3340
    3404:	e59f2024 	ldr	r2, [pc, #36]	; 0x3430
    3408:	e5920000 	ldr	r0, [r2]
    340c:	e0803081 	add	r3, r0, r1, lsl #1
    3410:	e5d30011 	ldrb	r0, [r3, #17]
    3414:	e5d3c010 	ldrb	ip, [r3, #16]
    3418:	e18c1400 	orr	r1, ip, r0, lsl #8
    341c:	e08ee081 	add	lr, lr, r1, lsl #1
    3420:	eafffff5 	b	0x33fc
    3424:	00207364 	eoreq	r7, r0, r4, ror #6
    3428:	0020736a 	eoreq	r7, r0, sl, ror #6
    342c:	00207338 	eoreq	r7, r0, r8, lsr r3
    3430:	00207424 	eoreq	r7, r0, r4, lsr #8
    3434:	e1a0c00d 	mov	ip, sp
    3438:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    343c:	e59f3148 	ldr	r3, [pc, #328]	; 0x358c
    3440:	e5932000 	ldr	r2, [r3]
    3444:	e5924000 	ldr	r4, [r2]
    3448:	e282e004 	add	lr, r2, #4	; 0x4
    344c:	e1a00800 	mov	r0, r0, lsl #16
    3450:	e15e0004 	cmp	lr, r4
    3454:	e24cb004 	sub	fp, ip, #4	; 0x4
    3458:	e1a05820 	mov	r5, r0, lsr #16
    345c:	2a000009 	bcs	0x3488
    3460:	e1de10b0 	ldrh	r1, [lr]
    3464:	e1a02801 	mov	r2, r1, lsl #16
    3468:	e3520000 	cmp	r2, #0	; 0x0
    346c:	e1a0c822 	mov	ip, r2, lsr #16
    3470:	ba00002b 	blt	0x3524
    3474:	e155000c 	cmp	r5, ip
    3478:	9a00000b 	bls	0x34ac
    347c:	e08ee08c 	add	lr, lr, ip, lsl #1
    3480:	e15e0004 	cmp	lr, r4
    3484:	3afffff5 	bcc	0x3460
    3488:	e3a04000 	mov	r4, #0	; 0x0
    348c:	e59fe0fc 	ldr	lr, [pc, #252]	; 0x3590
    3490:	e59e0000 	ldr	r0, [lr]
    3494:	ebfffeeb 	bl	0x3048
    3498:	e1a00004 	mov	r0, r4
    349c:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    34a0:	e12fff1e 	bx	lr
    34a4:	e1a0c823 	mov	ip, r3, lsr #16
    34a8:	e1cec0b0 	strh	ip, [lr]
    34ac:	e1a0008c 	mov	r0, ip, lsl #1
    34b0:	e19030fe 	ldrsh	r3, [r0, lr]
    34b4:	e080100e 	add	r1, r0, lr
    34b8:	e1a02fa3 	mov	r2, r3, lsr #31
    34bc:	e19030be 	ldrh	r3, [r0, lr]
    34c0:	e1510004 	cmp	r1, r4
    34c4:	23822001 	orrcs	r2, r2, #1	; 0x1
    34c8:	e3520000 	cmp	r2, #0	; 0x0
    34cc:	e08c1003 	add	r1, ip, r3
    34d0:	e1a03801 	mov	r3, r1, lsl #16
    34d4:	0afffff2 	beq	0x34a4
    34d8:	e155000c 	cmp	r5, ip
    34dc:	2a000005 	bcs	0x34f8
    34e0:	e065000c 	rsb	r0, r5, ip
    34e4:	e1a0c800 	mov	ip, r0, lsl #16
    34e8:	e1a0482c 	mov	r4, ip, lsr #16
    34ec:	e1ce40b0 	strh	r4, [lr]
    34f0:	e08ee084 	add	lr, lr, r4, lsl #1
    34f4:	e1ce50b0 	strh	r5, [lr]
    34f8:	e59f2094 	ldr	r2, [pc, #148]	; 0x3594
    34fc:	e1d230b0 	ldrh	r3, [r2]
    3500:	e25e4000 	subs	r4, lr, #0	; 0x0
    3504:	e0651003 	rsb	r1, r5, r3
    3508:	13a03000 	movne	r3, #0	; 0x0
    350c:	e1c210b0 	strh	r1, [r2]
    3510:	11c430b0 	strneh	r3, [r4]
    3514:	15c43004 	strneb	r3, [r4, #4]
    3518:	15c43005 	strneb	r3, [r4, #5]
    351c:	1affffdd 	bne	0x3498
    3520:	eaffffd9 	b	0x348c
    3524:	e1a00b8c 	mov	r0, ip, lsl #23
    3528:	e20c30ff 	and	r3, ip, #255	; 0xff
    352c:	e3120101 	tst	r2, #1073741824	; 0x40000000
    3530:	e20c6c1e 	and	r6, ip, #7680	; 0x1e00
    3534:	e1a00ba0 	mov	r0, r0, lsr #23
    3538:	e0831103 	add	r1, r3, r3, lsl #2
    353c:	0a00000a 	beq	0x356c
    3540:	e59f1050 	ldr	r1, [pc, #80]	; 0x3598
    3544:	e7d124a6 	ldrb	r2, [r1, r6, lsr #9]
    3548:	e00c0092 	mul	ip, r2, r0
    354c:	e28c3001 	add	r3, ip, #1	; 0x1
    3550:	e1a010c3 	mov	r1, r3, asr #1
    3554:	e2812004 	add	r2, r1, #4	; 0x4
    3558:	e1a0c802 	mov	ip, r2, lsl #16
    355c:	e1a0182c 	mov	r1, ip, lsr #16
    3560:	e08ee081 	add	lr, lr, r1, lsl #1
    3564:	e15e0004 	cmp	lr, r4
    3568:	eaffffc5 	b	0x3484
    356c:	e59f2028 	ldr	r2, [pc, #40]	; 0x359c
    3570:	e5920000 	ldr	r0, [r2]
    3574:	e0803081 	add	r3, r0, r1, lsl #1
    3578:	e5d30011 	ldrb	r0, [r3, #17]
    357c:	e5d3c010 	ldrb	ip, [r3, #16]
    3580:	e18c1400 	orr	r1, ip, r0, lsl #8
    3584:	e08ee081 	add	lr, lr, r1, lsl #1
    3588:	eafffff5 	b	0x3564
    358c:	00207364 	eoreq	r7, r0, r4, ror #6
    3590:	002073f8 	streqd	r7, [r0], -r8
    3594:	0020736a 	eoreq	r7, r0, sl, ror #6
    3598:	00207338 	eoreq	r7, r0, r8, lsr r3
    359c:	00207424 	eoreq	r7, r0, r4, lsr #8
    35a0:	e1a0c00d 	mov	ip, sp
    35a4:	e3510c02 	cmp	r1, #512	; 0x200
    35a8:	e92dddf0 	stmdb	sp!, {r4, r5, r6, r7, r8, sl, fp, ip, lr, pc}
    35ac:	e24cb004 	sub	fp, ip, #4	; 0x4
    35b0:	e20070ff 	and	r7, r0, #255	; 0xff
    35b4:	2a00006c 	bcs	0x376c
    35b8:	e59fa1c0 	ldr	sl, [pc, #448]	; 0x3780
    35bc:	e1a08801 	mov	r8, r1, lsl #16
    35c0:	e7da0007 	ldrb	r0, [sl, r7]
    35c4:	e1a01828 	mov	r1, r8, lsr #16
    35c8:	e00c0190 	mul	ip, r0, r1
    35cc:	e59f31b0 	ldr	r3, [pc, #432]	; 0x3784
    35d0:	e5932000 	ldr	r2, [r3]
    35d4:	e28c4001 	add	r4, ip, #1	; 0x1
    35d8:	e1a050c4 	mov	r5, r4, asr #1
    35dc:	e5924000 	ldr	r4, [r2]
    35e0:	e2851004 	add	r1, r5, #4	; 0x4
    35e4:	e282c004 	add	ip, r2, #4	; 0x4
    35e8:	e1a00801 	mov	r0, r1, lsl #16
    35ec:	e15c0004 	cmp	ip, r4
    35f0:	e1a05820 	mov	r5, r0, lsr #16
    35f4:	2a000009 	bcs	0x3620
    35f8:	e1dc30b0 	ldrh	r3, [ip]
    35fc:	e1a02803 	mov	r2, r3, lsl #16
    3600:	e3520000 	cmp	r2, #0	; 0x0
    3604:	e1a00822 	mov	r0, r2, lsr #16
    3608:	ba00003e 	blt	0x3708
    360c:	e1550000 	cmp	r5, r0
    3610:	9a00000b 	bls	0x3644
    3614:	e08cc080 	add	ip, ip, r0, lsl #1
    3618:	e15c0004 	cmp	ip, r4
    361c:	3afffff5 	bcc	0x35f8
    3620:	e3a04000 	mov	r4, #0	; 0x0
    3624:	e59fe15c 	ldr	lr, [pc, #348]	; 0x3788
    3628:	e59e0000 	ldr	r0, [lr]
    362c:	ebfffe85 	bl	0x3048
    3630:	e1a00004 	mov	r0, r4
    3634:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    3638:	e12fff1e 	bx	lr
    363c:	e1a00823 	mov	r0, r3, lsr #16
    3640:	e1cc00b0 	strh	r0, [ip]
    3644:	e1a01080 	mov	r1, r0, lsl #1
    3648:	e19120fc 	ldrsh	r2, [r1, ip]
    364c:	e081300c 	add	r3, r1, ip
    3650:	e1a02fa2 	mov	r2, r2, lsr #31
    3654:	e1530004 	cmp	r3, r4
    3658:	23822001 	orrcs	r2, r2, #1	; 0x1
    365c:	e19130bc 	ldrh	r3, [r1, ip]
    3660:	e3520000 	cmp	r2, #0	; 0x0
    3664:	e0801003 	add	r1, r0, r3
    3668:	e1a03801 	mov	r3, r1, lsl #16
    366c:	0afffff2 	beq	0x363c
    3670:	e1550000 	cmp	r5, r0
    3674:	2a000005 	bcs	0x3690
    3678:	e0651000 	rsb	r1, r5, r0
    367c:	e1a00801 	mov	r0, r1, lsl #16
    3680:	e1a04820 	mov	r4, r0, lsr #16
    3684:	e1cc40b0 	strh	r4, [ip]
    3688:	e08cc084 	add	ip, ip, r4, lsl #1
    368c:	e1cc50b0 	strh	r5, [ip]
    3690:	e59f20f4 	ldr	r2, [pc, #244]	; 0x378c
    3694:	e1d230b0 	ldrh	r3, [r2]
    3698:	e25c4000 	subs	r4, ip, #0	; 0x0
    369c:	e065c003 	rsb	ip, r5, r3
    36a0:	e1c2c0b0 	strh	ip, [r2]
    36a4:	0affffde 	beq	0x3624
    36a8:	e1a03805 	mov	r3, r5, lsl #16
    36ac:	e1a0c487 	mov	ip, r7, lsl #9
    36b0:	e2431805 	sub	r1, r3, #327680	; 0x50000
    36b4:	e3a00cff 	mov	r0, #65280	; 0xff00
    36b8:	e3a03000 	mov	r3, #0	; 0x0
    36bc:	e18c2828 	orr	r2, ip, r8, lsr #16
    36c0:	e3822903 	orr	r2, r2, #49152	; 0xc000
    36c4:	e1a0c821 	mov	ip, r1, lsr #16
    36c8:	e5c43004 	strb	r3, [r4, #4]
    36cc:	e28010ff 	add	r1, r0, #255	; 0xff
    36d0:	e5c43005 	strb	r3, [r4, #5]
    36d4:	e1c430b0 	strh	r3, [r4]
    36d8:	e1a00004 	mov	r0, r4
    36dc:	e1c420b0 	strh	r2, [r4]
    36e0:	e15c0001 	cmp	ip, r1
    36e4:	e2842008 	add	r2, r4, #8	; 0x8
    36e8:	0affffd1 	beq	0x3634
    36ec:	e24c3001 	sub	r3, ip, #1	; 0x1
    36f0:	e1a03803 	mov	r3, r3, lsl #16
    36f4:	e1a0c823 	mov	ip, r3, lsr #16
    36f8:	e3a03000 	mov	r3, #0	; 0x0
    36fc:	e0c230b2 	strh	r3, [r2], #2
    3700:	e15c0001 	cmp	ip, r1
    3704:	eafffff7 	b	0x36e8
    3708:	e1a0eb80 	mov	lr, r0, lsl #23
    370c:	e20030ff 	and	r3, r0, #255	; 0xff
    3710:	e3120101 	tst	r2, #1073741824	; 0x40000000
    3714:	e2006c1e 	and	r6, r0, #7680	; 0x1e00
    3718:	e1a0ebae 	mov	lr, lr, lsr #23
    371c:	e0831103 	add	r1, r3, r3, lsl #2
    3720:	0a000009 	beq	0x374c
    3724:	e7da24a6 	ldrb	r2, [sl, r6, lsr #9]
    3728:	e0010e92 	mul	r1, r2, lr
    372c:	e2813001 	add	r3, r1, #1	; 0x1
    3730:	e1a000c3 	mov	r0, r3, asr #1
    3734:	e2801004 	add	r1, r0, #4	; 0x4
    3738:	e1a02801 	mov	r2, r1, lsl #16
    373c:	e1a01822 	mov	r1, r2, lsr #16
    3740:	e08cc081 	add	ip, ip, r1, lsl #1
    3744:	e15c0004 	cmp	ip, r4
    3748:	eaffffb3 	b	0x361c
    374c:	e59f203c 	ldr	r2, [pc, #60]	; 0x3790
    3750:	e5920000 	ldr	r0, [r2]
    3754:	e0803081 	add	r3, r0, r1, lsl #1
    3758:	e5d30011 	ldrb	r0, [r3, #17]
    375c:	e5d32010 	ldrb	r2, [r3, #16]
    3760:	e1821400 	orr	r1, r2, r0, lsl #8
    3764:	e08cc081 	add	ip, ip, r1, lsl #1
    3768:	eafffff5 	b	0x3744
    376c:	e59f3014 	ldr	r3, [pc, #20]	; 0x3788
    3770:	e5930000 	ldr	r0, [r3]
    3774:	ebfffe33 	bl	0x3048
    3778:	e3a00000 	mov	r0, #0	; 0x0
    377c:	eaffffac 	b	0x3634
    3780:	00207338 	eoreq	r7, r0, r8, lsr r3
    3784:	00207364 	eoreq	r7, r0, r4, ror #6
    3788:	002073f8 	streqd	r7, [r0], -r8
    378c:	0020736a 	eoreq	r7, r0, sl, ror #6
    3790:	00207424 	eoreq	r7, r0, r4, lsr #8
    3794:	e1a0c00d 	mov	ip, sp
    3798:	e92dd9f0 	stmdb	sp!, {r4, r5, r6, r7, r8, fp, ip, lr, pc}
    379c:	e59f61a0 	ldr	r6, [pc, #416]	; 0x3944
    37a0:	e59f81a0 	ldr	r8, [pc, #416]	; 0x3948
    37a4:	e20070ff 	and	r7, r0, #255	; 0xff
    37a8:	e24cb004 	sub	fp, ip, #4	; 0x4
    37ac:	e5980000 	ldr	r0, [r8]
    37b0:	e596c000 	ldr	ip, [r6]
    37b4:	e0874107 	add	r4, r7, r7, lsl #2
    37b8:	e0803084 	add	r3, r0, r4, lsl #1
    37bc:	e59c4000 	ldr	r4, [ip]
    37c0:	e5d31011 	ldrb	r1, [r3, #17]
    37c4:	e5d32010 	ldrb	r2, [r3, #16]
    37c8:	e28cc004 	add	ip, ip, #4	; 0x4
    37cc:	e15c0004 	cmp	ip, r4
    37d0:	e1826401 	orr	r6, r2, r1, lsl #8
    37d4:	2a000009 	bcs	0x3800
    37d8:	e1dce0b0 	ldrh	lr, [ip]
    37dc:	e1a0280e 	mov	r2, lr, lsl #16
    37e0:	e3520000 	cmp	r2, #0	; 0x0
    37e4:	e1a0e822 	mov	lr, r2, lsr #16
    37e8:	ba00003c 	blt	0x38e0
    37ec:	e156000e 	cmp	r6, lr
    37f0:	9a00000b 	bls	0x3824
    37f4:	e08cc08e 	add	ip, ip, lr, lsl #1
    37f8:	e15c0004 	cmp	ip, r4
    37fc:	3afffff5 	bcc	0x37d8
    3800:	e3a04000 	mov	r4, #0	; 0x0
    3804:	e59fe140 	ldr	lr, [pc, #320]	; 0x394c
    3808:	e59e0000 	ldr	r0, [lr]
    380c:	ebfffe0d 	bl	0x3048
    3810:	e1a00004 	mov	r0, r4
    3814:	e89d69f0 	ldmia	sp, {r4, r5, r6, r7, r8, fp, sp, lr}
    3818:	e12fff1e 	bx	lr
    381c:	e1a0e823 	mov	lr, r3, lsr #16
    3820:	e1cce0b0 	strh	lr, [ip]
    3824:	e1a0108e 	mov	r1, lr, lsl #1
    3828:	e19100fc 	ldrsh	r0, [r1, ip]
    382c:	e081300c 	add	r3, r1, ip
    3830:	e1a02fa0 	mov	r2, r0, lsr #31
    3834:	e19100bc 	ldrh	r0, [r1, ip]
    3838:	e1530004 	cmp	r3, r4
    383c:	23822001 	orrcs	r2, r2, #1	; 0x1
    3840:	e08e1000 	add	r1, lr, r0
    3844:	e3520000 	cmp	r2, #0	; 0x0
    3848:	e1a03801 	mov	r3, r1, lsl #16
    384c:	0afffff2 	beq	0x381c
    3850:	e156000e 	cmp	r6, lr
    3854:	2a000005 	bcs	0x3870
    3858:	e066200e 	rsb	r2, r6, lr
    385c:	e1a01802 	mov	r1, r2, lsl #16
    3860:	e1a04821 	mov	r4, r1, lsr #16
    3864:	e1cc40b0 	strh	r4, [ip]
    3868:	e08cc084 	add	ip, ip, r4, lsl #1
    386c:	e1cc60b0 	strh	r6, [ip]
    3870:	e59f00d8 	ldr	r0, [pc, #216]	; 0x3950
    3874:	e1d030b0 	ldrh	r3, [r0]
    3878:	e25c4000 	subs	r4, ip, #0	; 0x0
    387c:	e066c003 	rsb	ip, r6, r3
    3880:	e1c0c0b0 	strh	ip, [r0]
    3884:	0affffde 	beq	0x3804
    3888:	e1a01806 	mov	r1, r6, lsl #16
    388c:	e3a03000 	mov	r3, #0	; 0x0
    3890:	e241c805 	sub	ip, r1, #327680	; 0x50000
    3894:	e3a00cff 	mov	r0, #65280	; 0xff00
    3898:	e1a0182c 	mov	r1, ip, lsr #16
    389c:	e3872902 	orr	r2, r7, #32768	; 0x8000
    38a0:	e280c0ff 	add	ip, r0, #255	; 0xff
    38a4:	e5c43004 	strb	r3, [r4, #4]
    38a8:	e5c43005 	strb	r3, [r4, #5]
    38ac:	e1c430b0 	strh	r3, [r4]
    38b0:	e1a00004 	mov	r0, r4
    38b4:	e1c420b0 	strh	r2, [r4]
    38b8:	e151000c 	cmp	r1, ip
    38bc:	e2842008 	add	r2, r4, #8	; 0x8
    38c0:	0affffd3 	beq	0x3814
    38c4:	e2413001 	sub	r3, r1, #1	; 0x1
    38c8:	e1a03803 	mov	r3, r3, lsl #16
    38cc:	e1a01823 	mov	r1, r3, lsr #16
    38d0:	e3a03000 	mov	r3, #0	; 0x0
    38d4:	e0c230b2 	strh	r3, [r2], #2
    38d8:	e151000c 	cmp	r1, ip
    38dc:	eafffff7 	b	0x38c0
    38e0:	e1a01b8e 	mov	r1, lr, lsl #23
    38e4:	e20e30ff 	and	r3, lr, #255	; 0xff
    38e8:	e3120101 	tst	r2, #1073741824	; 0x40000000
    38ec:	e1a00ba1 	mov	r0, r1, lsr #23
    38f0:	e20e5c1e 	and	r5, lr, #7680	; 0x1e00
    38f4:	e0831103 	add	r1, r3, r3, lsl #2
    38f8:	0a00000a 	beq	0x3928
    38fc:	e59fe050 	ldr	lr, [pc, #80]	; 0x3954
    3900:	e7de14a5 	ldrb	r1, [lr, r5, lsr #9]
    3904:	e0020091 	mul	r2, r1, r0
    3908:	e2823001 	add	r3, r2, #1	; 0x1
    390c:	e1a0e0c3 	mov	lr, r3, asr #1
    3910:	e28e0004 	add	r0, lr, #4	; 0x4
    3914:	e1a02800 	mov	r2, r0, lsl #16
    3918:	e1a01822 	mov	r1, r2, lsr #16
    391c:	e08cc081 	add	ip, ip, r1, lsl #1
    3920:	e15c0004 	cmp	ip, r4
    3924:	eaffffb4 	b	0x37fc
    3928:	e598e000 	ldr	lr, [r8]
    392c:	e08e3081 	add	r3, lr, r1, lsl #1
    3930:	e5d30011 	ldrb	r0, [r3, #17]
    3934:	e5d32010 	ldrb	r2, [r3, #16]
    3938:	e1821400 	orr	r1, r2, r0, lsl #8
    393c:	e08cc081 	add	ip, ip, r1, lsl #1
    3940:	eafffff6 	b	0x3920
    3944:	00207364 	eoreq	r7, r0, r4, ror #6
    3948:	00207424 	eoreq	r7, r0, r4, lsr #8
    394c:	002073f8 	streqd	r7, [r0], -r8
    3950:	0020736a 	eoreq	r7, r0, sl, ror #6
    3954:	00207338 	eoreq	r7, r0, r8, lsr r3
    3958:	e1a0c00d 	mov	ip, sp
    395c:	e92dd9f0 	stmdb	sp!, {r4, r5, r6, r7, r8, fp, ip, lr, pc}
    3960:	e59f81c8 	ldr	r8, [pc, #456]	; 0x3b30
    3964:	e20070ff 	and	r7, r0, #255	; 0xff
    3968:	e5982000 	ldr	r2, [r8]
    396c:	e0873107 	add	r3, r7, r7, lsl #2
    3970:	e1a04083 	mov	r4, r3, lsl #1
    3974:	e0840002 	add	r0, r4, r2
    3978:	e24cb004 	sub	fp, ip, #4	; 0x4
    397c:	e2800010 	add	r0, r0, #16	; 0x10
    3980:	eb00024f 	bl	0x42c4
    3984:	e31000ff 	tst	r0, #255	; 0xff
    3988:	13a00000 	movne	r0, #0	; 0x0
    398c:	1a000019 	bne	0x39f8
    3990:	e59f219c 	ldr	r2, [pc, #412]	; 0x3b34
    3994:	e598e000 	ldr	lr, [r8]
    3998:	e5921000 	ldr	r1, [r2]
    399c:	e084600e 	add	r6, r4, lr
    39a0:	e5914000 	ldr	r4, [r1]
    39a4:	e5d60011 	ldrb	r0, [r6, #17]
    39a8:	e5d6c010 	ldrb	ip, [r6, #16]
    39ac:	e281e004 	add	lr, r1, #4	; 0x4
    39b0:	e15e0004 	cmp	lr, r4
    39b4:	e18c6400 	orr	r6, ip, r0, lsl #8
    39b8:	2a000009 	bcs	0x39e4
    39bc:	e1de30b0 	ldrh	r3, [lr]
    39c0:	e1a02803 	mov	r2, r3, lsl #16
    39c4:	e3520000 	cmp	r2, #0	; 0x0
    39c8:	e1a0c822 	mov	ip, r2, lsr #16
    39cc:	ba00003e 	blt	0x3acc
    39d0:	e156000c 	cmp	r6, ip
    39d4:	9a00000b 	bls	0x3a08
    39d8:	e08ee08c 	add	lr, lr, ip, lsl #1
    39dc:	e15e0004 	cmp	lr, r4
    39e0:	3afffff5 	bcc	0x39bc
    39e4:	e3a04000 	mov	r4, #0	; 0x0
    39e8:	e59fe148 	ldr	lr, [pc, #328]	; 0x3b38
    39ec:	e59e0000 	ldr	r0, [lr]
    39f0:	ebfffd94 	bl	0x3048
    39f4:	e1a00004 	mov	r0, r4
    39f8:	e89d69f0 	ldmia	sp, {r4, r5, r6, r7, r8, fp, sp, lr}
    39fc:	e12fff1e 	bx	lr
    3a00:	e1a0c823 	mov	ip, r3, lsr #16
    3a04:	e1cec0b0 	strh	ip, [lr]
    3a08:	e1a0108c 	mov	r1, ip, lsl #1
    3a0c:	e19100fe 	ldrsh	r0, [r1, lr]
    3a10:	e081300e 	add	r3, r1, lr
    3a14:	e1a02fa0 	mov	r2, r0, lsr #31
    3a18:	e19100be 	ldrh	r0, [r1, lr]
    3a1c:	e1530004 	cmp	r3, r4
    3a20:	23822001 	orrcs	r2, r2, #1	; 0x1
    3a24:	e08c1000 	add	r1, ip, r0
    3a28:	e3520000 	cmp	r2, #0	; 0x0
    3a2c:	e1a03801 	mov	r3, r1, lsl #16
    3a30:	0afffff2 	beq	0x3a00
    3a34:	e156000c 	cmp	r6, ip
    3a38:	2a000005 	bcs	0x3a54
    3a3c:	e066100c 	rsb	r1, r6, ip
    3a40:	e1a0c801 	mov	ip, r1, lsl #16
    3a44:	e1a0482c 	mov	r4, ip, lsr #16
    3a48:	e1ce40b0 	strh	r4, [lr]
    3a4c:	e08ee084 	add	lr, lr, r4, lsl #1
    3a50:	e1ce60b0 	strh	r6, [lr]
    3a54:	e59f20e0 	ldr	r2, [pc, #224]	; 0x3b3c
    3a58:	e1d200b0 	ldrh	r0, [r2]
    3a5c:	e25e4000 	subs	r4, lr, #0	; 0x0
    3a60:	e0663000 	rsb	r3, r6, r0
    3a64:	e1c230b0 	strh	r3, [r2]
    3a68:	0affffde 	beq	0x39e8
    3a6c:	e1a00806 	mov	r0, r6, lsl #16
    3a70:	e2401805 	sub	r1, r0, #327680	; 0x50000
    3a74:	e3a0ccff 	mov	ip, #65280	; 0xff00
    3a78:	e1a00821 	mov	r0, r1, lsr #16
    3a7c:	e28c10ff 	add	r1, ip, #255	; 0xff
    3a80:	e3a03000 	mov	r3, #0	; 0x0
    3a84:	e3872902 	orr	r2, r7, #32768	; 0x8000
    3a88:	e1500001 	cmp	r0, r1
    3a8c:	e1c430b0 	strh	r3, [r4]
    3a90:	e1a0c004 	mov	ip, r4
    3a94:	e1c420b0 	strh	r2, [r4]
    3a98:	e5c43004 	strb	r3, [r4, #4]
    3a9c:	e5c43005 	strb	r3, [r4, #5]
    3aa0:	e2842008 	add	r2, r4, #8	; 0x8
    3aa4:	0a000006 	beq	0x3ac4
    3aa8:	e2403001 	sub	r3, r0, #1	; 0x1
    3aac:	e1a03803 	mov	r3, r3, lsl #16
    3ab0:	e1a00823 	mov	r0, r3, lsr #16
    3ab4:	e3a03000 	mov	r3, #0	; 0x0
    3ab8:	e1500001 	cmp	r0, r1
    3abc:	e0c230b2 	strh	r3, [r2], #2
    3ac0:	1afffff8 	bne	0x3aa8
    3ac4:	e1a0000c 	mov	r0, ip
    3ac8:	eaffffca 	b	0x39f8
    3acc:	e1a00b8c 	mov	r0, ip, lsl #23
    3ad0:	e20c30ff 	and	r3, ip, #255	; 0xff
    3ad4:	e3120101 	tst	r2, #1073741824	; 0x40000000
    3ad8:	e20c5c1e 	and	r5, ip, #7680	; 0x1e00
    3adc:	e1a00ba0 	mov	r0, r0, lsr #23
    3ae0:	e0831103 	add	r1, r3, r3, lsl #2
    3ae4:	0a00000a 	beq	0x3b14
    3ae8:	e59f2050 	ldr	r2, [pc, #80]	; 0x3b40
    3aec:	e7d214a5 	ldrb	r1, [r2, r5, lsr #9]
    3af0:	e00c0091 	mul	ip, r1, r0
    3af4:	e28c3001 	add	r3, ip, #1	; 0x1
    3af8:	e1a020c3 	mov	r2, r3, asr #1
    3afc:	e2821004 	add	r1, r2, #4	; 0x4
    3b00:	e1a0c801 	mov	ip, r1, lsl #16
    3b04:	e1a0182c 	mov	r1, ip, lsr #16
    3b08:	e08ee081 	add	lr, lr, r1, lsl #1
    3b0c:	e15e0004 	cmp	lr, r4
    3b10:	eaffffb2 	b	0x39e0
    3b14:	e5982000 	ldr	r2, [r8]
    3b18:	e0823081 	add	r3, r2, r1, lsl #1
    3b1c:	e5d30011 	ldrb	r0, [r3, #17]
    3b20:	e5d3c010 	ldrb	ip, [r3, #16]
    3b24:	e18c1400 	orr	r1, ip, r0, lsl #8
    3b28:	e08ee081 	add	lr, lr, r1, lsl #1
    3b2c:	eafffff6 	b	0x3b0c
    3b30:	00207424 	eoreq	r7, r0, r4, lsr #8
    3b34:	00207364 	eoreq	r7, r0, r4, ror #6
    3b38:	002073f8 	streqd	r7, [r0], -r8
    3b3c:	0020736a 	eoreq	r7, r0, sl, ror #6
    3b40:	00207338 	eoreq	r7, r0, r8, lsr r3
    3b44:	e59f2018 	ldr	r2, [pc, #24]	; 0x3b64
    3b48:	e1a01801 	mov	r1, r1, lsl #16
    3b4c:	e1d2c0b0 	ldrh	ip, [r2]
    3b50:	e1a01821 	mov	r1, r1, lsr #16
    3b54:	e081300c 	add	r3, r1, ip
    3b58:	e1c230b0 	strh	r3, [r2]
    3b5c:	e1c010b0 	strh	r1, [r0]
    3b60:	e12fff1e 	bx	lr
    3b64:	0020736a 	eoreq	r7, r0, sl, ror #6
    3b68:	e1d020b0 	ldrh	r2, [r0]
    3b6c:	e59f303c 	ldr	r3, [pc, #60]	; 0x3bb0
    3b70:	e2021c1e 	and	r1, r2, #7680	; 0x1e00
    3b74:	e7d3c4a1 	ldrb	ip, [r3, r1, lsr #9]
    3b78:	e1a01b82 	mov	r1, r2, lsl #23
    3b7c:	e1a02ba1 	mov	r2, r1, lsr #23
    3b80:	e003029c 	mul	r3, ip, r2
    3b84:	e2831001 	add	r1, r3, #1	; 0x1
    3b88:	e1a0c0c1 	mov	ip, r1, asr #1
    3b8c:	e59f1020 	ldr	r1, [pc, #32]	; 0x3bb4
    3b90:	e28c2004 	add	r2, ip, #4	; 0x4
    3b94:	e1a03802 	mov	r3, r2, lsl #16
    3b98:	e1d1c0b0 	ldrh	ip, [r1]
    3b9c:	e1a03823 	mov	r3, r3, lsr #16
    3ba0:	e083200c 	add	r2, r3, ip
    3ba4:	e1c120b0 	strh	r2, [r1]
    3ba8:	e1c030b0 	strh	r3, [r0]
    3bac:	e12fff1e 	bx	lr
    3bb0:	00207338 	eoreq	r7, r0, r8, lsr r3
    3bb4:	0020736a 	eoreq	r7, r0, sl, ror #6
    3bb8:	e1a0c00d 	mov	ip, sp
    3bbc:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    3bc0:	e1a04000 	mov	r4, r0
    3bc4:	e1d000b0 	ldrh	r0, [r0]
    3bc8:	e2003c1e 	and	r3, r0, #7680	; 0x1e00
    3bcc:	e1a054a3 	mov	r5, r3, lsr #9
    3bd0:	e24cb004 	sub	fp, ip, #4	; 0x4
    3bd4:	e1a00005 	mov	r0, r5
    3bd8:	ebfffe70 	bl	0x35a0
    3bdc:	e2506000 	subs	r6, r0, #0	; 0x0
    3be0:	e59f706c 	ldr	r7, [pc, #108]	; 0x3c54
    3be4:	e2841008 	add	r1, r4, #8	; 0x8
    3be8:	e2860008 	add	r0, r6, #8	; 0x8
    3bec:	0a000015 	beq	0x3c48
    3bf0:	e1d420b0 	ldrh	r2, [r4]
    3bf4:	e7d7c005 	ldrb	ip, [r7, r5]
    3bf8:	e1a03b82 	mov	r3, r2, lsl #23
    3bfc:	e1a0eba3 	mov	lr, r3, lsr #23
    3c00:	e0020e9c 	mul	r2, ip, lr
    3c04:	eb000356 	bl	0x4964
    3c08:	e1d410b0 	ldrh	r1, [r4]
    3c0c:	e2010c1e 	and	r0, r1, #7680	; 0x1e00
    3c10:	e7d7c4a0 	ldrb	ip, [r7, r0, lsr #9]
    3c14:	e1a02b81 	mov	r2, r1, lsl #23
    3c18:	e1a03ba2 	mov	r3, r2, lsr #23
    3c1c:	e000039c 	mul	r0, ip, r3
    3c20:	e2801001 	add	r1, r0, #1	; 0x1
    3c24:	e1a020c1 	mov	r2, r1, asr #1
    3c28:	e59f1028 	ldr	r1, [pc, #40]	; 0x3c58
    3c2c:	e282c004 	add	ip, r2, #4	; 0x4
    3c30:	e1d100b0 	ldrh	r0, [r1]
    3c34:	e1a0380c 	mov	r3, ip, lsl #16
    3c38:	e1a0c823 	mov	ip, r3, lsr #16
    3c3c:	e08c2000 	add	r2, ip, r0
    3c40:	e1c120b0 	strh	r2, [r1]
    3c44:	e1c4c0b0 	strh	ip, [r4]
    3c48:	e1a00006 	mov	r0, r6
    3c4c:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    3c50:	e12fff1e 	bx	lr
    3c54:	00207338 	eoreq	r7, r0, r8, lsr r3
    3c58:	0020736a 	eoreq	r7, r0, sl, ror #6
    3c5c:	e1a0c00d 	mov	ip, sp
    3c60:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    3c64:	e21270ff 	ands	r7, r2, #255	; 0xff
    3c68:	e20090ff 	and	r9, r0, #255	; 0xff
    3c6c:	e24cb004 	sub	fp, ip, #4	; 0x4
    3c70:	e1a05003 	mov	r5, r3
    3c74:	e20140ff 	and	r4, r1, #255	; 0xff
    3c78:	01a00007 	moveq	r0, r7
    3c7c:	0a00001c 	beq	0x3cf4
    3c80:	e3540001 	cmp	r4, #1	; 0x1
    3c84:	0a00001c 	beq	0x3cfc
    3c88:	e3a00000 	mov	r0, #0	; 0x0
    3c8c:	e5931000 	ldr	r1, [r3]
    3c90:	ebfffe42 	bl	0x35a0
    3c94:	e2506000 	subs	r6, r0, #0	; 0x0
    3c98:	0a000014 	beq	0x3cf0
    3c9c:	e5953000 	ldr	r3, [r5]
    3ca0:	e243c001 	sub	ip, r3, #1	; 0x1
    3ca4:	e37c0001 	cmn	ip, #1	; 0x1
    3ca8:	e585c000 	str	ip, [r5]
    3cac:	0a00000f 	beq	0x3cf0
    3cb0:	e2478001 	sub	r8, r7, #1	; 0x1
    3cb4:	e244a001 	sub	sl, r4, #1	; 0x1
    3cb8:	e2857004 	add	r7, r5, #4	; 0x4
    3cbc:	e20a10ff 	and	r1, sl, #255	; 0xff
    3cc0:	e1a00009 	mov	r0, r9
    3cc4:	e20820ff 	and	r2, r8, #255	; 0xff
    3cc8:	e1a03007 	mov	r3, r7
    3ccc:	e086410c 	add	r4, r6, ip, lsl #2
    3cd0:	ebffffe1 	bl	0x3c5c
    3cd4:	e5840008 	str	r0, [r4, #8]
    3cd8:	e5951000 	ldr	r1, [r5]
    3cdc:	e2410001 	sub	r0, r1, #1	; 0x1
    3ce0:	e3700001 	cmn	r0, #1	; 0x1
    3ce4:	e1a0c000 	mov	ip, r0
    3ce8:	e5850000 	str	r0, [r5]
    3cec:	1afffff2 	bne	0x3cbc
    3cf0:	e1a00006 	mov	r0, r6
    3cf4:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    3cf8:	e12fff1e 	bx	lr
    3cfc:	e5931000 	ldr	r1, [r3]
    3d00:	e1a00009 	mov	r0, r9
    3d04:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    3d08:	eafffe24 	b	0x35a0
    3d0c:	e2411001 	sub	r1, r1, #1	; 0x1
    3d10:	e20110ff 	and	r1, r1, #255	; 0xff
    3d14:	e35100ff 	cmp	r1, #255	; 0xff
    3d18:	e1a0c000 	mov	ip, r0
    3d1c:	e3a00000 	mov	r0, #0	; 0x0
    3d20:	012fff1e 	bxeq	lr
    3d24:	e2413001 	sub	r3, r1, #1	; 0x1
    3d28:	e20310ff 	and	r1, r3, #255	; 0xff
    3d2c:	e4dc2001 	ldrb	r2, [ip], #1
    3d30:	e35100ff 	cmp	r1, #255	; 0xff
    3d34:	e1820400 	orr	r0, r2, r0, lsl #8
    3d38:	1afffff9 	bne	0x3d24
    3d3c:	e12fff1e 	bx	lr
    3d40:	e201c0ff 	and	ip, r1, #255	; 0xff
    3d44:	e24c3001 	sub	r3, ip, #1	; 0x1
    3d48:	e20330ff 	and	r3, r3, #255	; 0xff
    3d4c:	e08c1000 	add	r1, ip, r0
    3d50:	e35300ff 	cmp	r3, #255	; 0xff
    3d54:	e2411001 	sub	r1, r1, #1	; 0x1
    3d58:	012fff1e 	bxeq	lr
    3d5c:	e2430001 	sub	r0, r3, #1	; 0x1
    3d60:	e20030ff 	and	r3, r0, #255	; 0xff
    3d64:	e35300ff 	cmp	r3, #255	; 0xff
    3d68:	e4412001 	strb	r2, [r1], #-1
    3d6c:	e1a02422 	mov	r2, r2, lsr #8
    3d70:	1afffff9 	bne	0x3d5c
    3d74:	e12fff1e 	bx	lr
    3d78:	e20110ff 	and	r1, r1, #255	; 0xff
    3d7c:	e3510001 	cmp	r1, #1	; 0x1
    3d80:	e52de004 	str	lr, [sp, #-4]!
    3d84:	e1a0e002 	mov	lr, r2
    3d88:	0a00000c 	beq	0x3dc0
    3d8c:	e3510002 	cmp	r1, #2	; 0x2
    3d90:	e1a0c000 	mov	ip, r0
    3d94:	0a00000c 	beq	0x3dcc
    3d98:	e4dc2001 	ldrb	r2, [ip], #1
    3d9c:	e5ce2003 	strb	r2, [lr, #3]
    3da0:	e5d01001 	ldrb	r1, [r0, #1]
    3da4:	e5ce1002 	strb	r1, [lr, #2]
    3da8:	e5dc0001 	ldrb	r0, [ip, #1]
    3dac:	e5ce0001 	strb	r0, [lr, #1]
    3db0:	e5dc3002 	ldrb	r3, [ip, #2]
    3db4:	e5ce3000 	strb	r3, [lr]
    3db8:	e49de004 	ldr	lr, [sp], #4
    3dbc:	e12fff1e 	bx	lr
    3dc0:	e1d030d0 	ldrsb	r3, [r0]
    3dc4:	e58e3000 	str	r3, [lr]
    3dc8:	eafffffa 	b	0x3db8
    3dcc:	e5d01001 	ldrb	r1, [r0, #1]
    3dd0:	e5d02000 	ldrb	r2, [r0]
    3dd4:	e1810402 	orr	r0, r1, r2, lsl #8
    3dd8:	e1a03800 	mov	r3, r0, lsl #16
    3ddc:	e1a03843 	mov	r3, r3, asr #16
    3de0:	e58e3000 	str	r3, [lr]
    3de4:	eafffff3 	b	0x3db8
    3de8:	e59f2010 	ldr	r2, [pc, #16]	; 0x3e00
    3dec:	e59f1010 	ldr	r1, [pc, #16]	; 0x3e04
    3df0:	e3a03000 	mov	r3, #0	; 0x0
    3df4:	e1c230b0 	strh	r3, [r2]
    3df8:	e1c130b0 	strh	r3, [r1]
    3dfc:	e12fff1e 	bx	lr
    3e00:	0020736a 	eoreq	r7, r0, sl, ror #6
    3e04:	00207368 	eoreq	r7, r0, r8, ror #6
    3e08:	e92d4030 	stmdb	sp!, {r4, r5, lr}
    3e0c:	e2804001 	add	r4, r0, #1	; 0x1
    3e10:	e3c40001 	bic	r0, r4, #1	; 0x1
    3e14:	e3c11001 	bic	r1, r1, #1	; 0x1
    3e18:	e060c001 	rsb	ip, r0, r1
    3e1c:	e59f403c 	ldr	r4, [pc, #60]	; 0x3e60
    3e20:	e59f503c 	ldr	r5, [pc, #60]	; 0x3e64
    3e24:	e24c2004 	sub	r2, ip, #4	; 0x4
    3e28:	e1d5e0b0 	ldrh	lr, [r5]
    3e2c:	e1d4c0b0 	ldrh	ip, [r4]
    3e30:	e1a03782 	mov	r3, r2, lsl #15
    3e34:	e1a03823 	mov	r3, r3, lsr #16
    3e38:	e59f2028 	ldr	r2, [pc, #40]	; 0x3e68
    3e3c:	e083e00e 	add	lr, r3, lr
    3e40:	e083c00c 	add	ip, r3, ip
    3e44:	e5820000 	str	r0, [r2]
    3e48:	e5801000 	str	r1, [r0]
    3e4c:	e1c030b4 	strh	r3, [r0, #4]
    3e50:	e1c4c0b0 	strh	ip, [r4]
    3e54:	e1c5e0b0 	strh	lr, [r5]
    3e58:	e8bd4030 	ldmia	sp!, {r4, r5, lr}
    3e5c:	e12fff1e 	bx	lr
    3e60:	00207368 	eoreq	r7, r0, r8, ror #6
    3e64:	0020736a 	eoreq	r7, r0, sl, ror #6
    3e68:	00207364 	eoreq	r7, r0, r4, ror #6
    3e6c:	e59f3008 	ldr	r3, [pc, #8]	; 0x3e7c
    3e70:	e1d300b0 	ldrh	r0, [r3]
    3e74:	e1a00080 	mov	r0, r0, lsl #1
    3e78:	e12fff1e 	bx	lr
    3e7c:	00207368 	eoreq	r7, r0, r8, ror #6
    3e80:	e59f3008 	ldr	r3, [pc, #8]	; 0x3e90
    3e84:	e1d300b0 	ldrh	r0, [r3]
    3e88:	e1a00080 	mov	r0, r0, lsl #1
    3e8c:	e12fff1e 	bx	lr
    3e90:	0020736a 	eoreq	r7, r0, sl, ror #6
    3e94:	e59f3004 	ldr	r3, [pc, #4]	; 0x3ea0
    3e98:	e5930000 	ldr	r0, [r3]
    3e9c:	e12fff1e 	bx	lr
    3ea0:	00207364 	eoreq	r7, r0, r4, ror #6
    3ea4:	e5d00000 	ldrb	r0, [r0]
    3ea8:	e12fff1e 	bx	lr
    3eac:	e92d40f0 	stmdb	sp!, {r4, r5, r6, r7, lr}
    3eb0:	e1a0e000 	mov	lr, r0
    3eb4:	e5dec007 	ldrb	ip, [lr, #7]
    3eb8:	e59f0080 	ldr	r0, [pc, #128]	; 0x3f40
    3ebc:	e5c0c000 	strb	ip, [r0]
    3ec0:	e5d05000 	ldrb	r5, [r0]
    3ec4:	e2452001 	sub	r2, r5, #1	; 0x1
    3ec8:	e20230ff 	and	r3, r2, #255	; 0xff
    3ecc:	e1a01801 	mov	r1, r1, lsl #16
    3ed0:	e35300ff 	cmp	r3, #255	; 0xff
    3ed4:	e1a05821 	mov	r5, r1, lsr #16
    3ed8:	e5c02000 	strb	r2, [r0]
    3edc:	0a000014 	beq	0x3f34
    3ee0:	e59f705c 	ldr	r7, [pc, #92]	; 0x3f44
    3ee4:	e59f605c 	ldr	r6, [pc, #92]	; 0x3f48
    3ee8:	e1a04000 	mov	r4, r0
    3eec:	e5de3002 	ldrb	r3, [lr, #2]
    3ef0:	e5de0003 	ldrb	r0, [lr, #3]
    3ef4:	e5d42000 	ldrb	r2, [r4]
    3ef8:	e596c000 	ldr	ip, [r6]
    3efc:	e1831400 	orr	r1, r3, r0, lsl #8
    3f00:	e0823082 	add	r3, r2, r2, lsl #1
    3f04:	e081100c 	add	r1, r1, ip
    3f08:	e1a03103 	mov	r3, r3, lsl #2
    3f0c:	e19100b3 	ldrh	r0, [r1, r3]
    3f10:	e2422001 	sub	r2, r2, #1	; 0x1
    3f14:	e1500005 	cmp	r0, r5
    3f18:	e0810003 	add	r0, r1, r3
    3f1c:	e202c0ff 	and	ip, r2, #255	; 0xff
    3f20:	e5870000 	str	r0, [r7]
    3f24:	0a000003 	beq	0x3f38
    3f28:	e35c00ff 	cmp	ip, #255	; 0xff
    3f2c:	e5c42000 	strb	r2, [r4]
    3f30:	1affffed 	bne	0x3eec
    3f34:	e3a00000 	mov	r0, #0	; 0x0
    3f38:	e8bd40f0 	ldmia	sp!, {r4, r5, r6, r7, lr}
    3f3c:	e12fff1e 	bx	lr
    3f40:	002073c0 	eoreq	r7, r0, r0, asr #7
    3f44:	00207370 	eoreq	r7, r0, r0, ror r3
    3f48:	00207424 	eoreq	r7, r0, r4, lsr #8
    3f4c:	e1a0c00d 	mov	ip, sp
    3f50:	e92dddf0 	stmdb	sp!, {r4, r5, r6, r7, r8, sl, fp, ip, lr, pc}
    3f54:	e59f71fc 	ldr	r7, [pc, #508]	; 0x4158
    3f58:	e1a05000 	mov	r5, r0
    3f5c:	e5d5300a 	ldrb	r3, [r5, #10]
    3f60:	e5d00008 	ldrb	r0, [r0, #8]
    3f64:	e5972000 	ldr	r2, [r7]
    3f68:	e59fa1ec 	ldr	sl, [pc, #492]	; 0x415c
    3f6c:	e24cb004 	sub	fp, ip, #4	; 0x4
    3f70:	e0422100 	sub	r2, r2, r0, lsl #2
    3f74:	e213c001 	ands	ip, r3, #1	; 0x1
    3f78:	e58a1000 	str	r1, [sl]
    3f7c:	e5872000 	str	r2, [r7]
    3f80:	1a000040 	bne	0x4088
    3f84:	e59f81d4 	ldr	r8, [pc, #468]	; 0x4160
    3f88:	e5984000 	ldr	r4, [r8]
    3f8c:	e5940014 	ldr	r0, [r4, #20]
    3f90:	e1d060b0 	ldrh	r6, [r0]
    3f94:	e1a01b86 	mov	r1, r6, lsl #23
    3f98:	e5d4601c 	ldrb	r6, [r4, #28]
    3f9c:	e1a03ba1 	mov	r3, r1, lsr #23
    3fa0:	e1560003 	cmp	r6, r3
    3fa4:	ba00000f 	blt	0x3fe8
    3fa8:	e083e083 	add	lr, r3, r3, lsl #1
    3fac:	e1a010ce 	mov	r1, lr, asr #1
    3fb0:	e35100ff 	cmp	r1, #255	; 0xff
    3fb4:	e1a0400c 	mov	r4, ip
    3fb8:	da000005 	ble	0x3fd4
    3fbc:	e59fc1a0 	ldr	ip, [pc, #416]	; 0x4164
    3fc0:	e59c0000 	ldr	r0, [ip]
    3fc4:	ebfffc1f 	bl	0x3048
    3fc8:	e1a00004 	mov	r0, r4
    3fcc:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    3fd0:	e12fff1e 	bx	lr
    3fd4:	ebfffef7 	bl	0x3bb8
    3fd8:	e2504000 	subs	r4, r0, #0	; 0x0
    3fdc:	0afffff6 	beq	0x3fbc
    3fe0:	e598c000 	ldr	ip, [r8]
    3fe4:	e58c4014 	str	r4, [ip, #20]
    3fe8:	e3560000 	cmp	r6, #0	; 0x0
    3fec:	05983000 	ldreq	r3, [r8]
    3ff0:	05932014 	ldreq	r2, [r3, #20]
    3ff4:	02824008 	addeq	r4, r2, #8	; 0x8
    3ff8:	1a00004e 	bne	0x4138
    3ffc:	e5986000 	ldr	r6, [r8]
    4000:	e5d6e01c 	ldrb	lr, [r6, #28]
    4004:	e28ec001 	add	ip, lr, #1	; 0x1
    4008:	e5c6c01c 	strb	ip, [r6, #28]
    400c:	e5970000 	ldr	r0, [r7]
    4010:	e5982000 	ldr	r2, [r8]
    4014:	e2806004 	add	r6, r0, #4	; 0x4
    4018:	e3a01000 	mov	r1, #0	; 0x0
    401c:	e5846008 	str	r6, [r4, #8]
    4020:	e5845000 	str	r5, [r4]
    4024:	e5841004 	str	r1, [r4, #4]
    4028:	e5924018 	ldr	r4, [r2, #24]
    402c:	e1d430b0 	ldrh	r3, [r4]
    4030:	e5d5e006 	ldrb	lr, [r5, #6]
    4034:	e1a0cb83 	mov	ip, r3, lsl #23
    4038:	e59f2128 	ldr	r2, [pc, #296]	; 0x4168
    403c:	e086110e 	add	r1, r6, lr, lsl #2
    4040:	e1a03bac 	mov	r3, ip, lsr #23
    4044:	e5d5e007 	ldrb	lr, [r5, #7]
    4048:	e2411004 	sub	r1, r1, #4	; 0x4
    404c:	e592c000 	ldr	ip, [r2]
    4050:	e0843103 	add	r3, r4, r3, lsl #2
    4054:	e1d520b4 	ldrh	r2, [r5, #4]
    4058:	e081e10e 	add	lr, r1, lr, lsl #2
    405c:	e2833008 	add	r3, r3, #8	; 0x8
    4060:	e59f5104 	ldr	r5, [pc, #260]	; 0x416c
    4064:	e082200c 	add	r2, r2, ip
    4068:	e15e0003 	cmp	lr, r3
    406c:	e5856000 	str	r6, [r5]
    4070:	e58a2000 	str	r2, [sl]
    4074:	e5871000 	str	r1, [r7]
    4078:	e59f60d8 	ldr	r6, [pc, #216]	; 0x4158
    407c:	2a000006 	bcs	0x409c
    4080:	e3a00001 	mov	r0, #1	; 0x1
    4084:	eaffffd0 	b	0x3fcc
    4088:	e1d500b0 	ldrh	r0, [r5]
    408c:	e2821004 	add	r1, r2, #4	; 0x4
    4090:	ebfff047 	bl	0x1b4
    4094:	e3a00000 	mov	r0, #0	; 0x0
    4098:	eaffffcb 	b	0x3fcc
    409c:	e064000e 	rsb	r0, r4, lr
    40a0:	e240100f 	sub	r1, r0, #15	; 0xf
    40a4:	e1a02121 	mov	r2, r1, lsr #2
    40a8:	e0823082 	add	r3, r2, r2, lsl #1
    40ac:	e1a00004 	mov	r0, r4
    40b0:	e1a010a3 	mov	r1, r3, lsr #1
    40b4:	ebfffebf 	bl	0x3bb8
    40b8:	e2504000 	subs	r4, r0, #0	; 0x0
    40bc:	0affffbe 	beq	0x3fbc
    40c0:	e59fe098 	ldr	lr, [pc, #152]	; 0x4160
    40c4:	e59e2000 	ldr	r2, [lr]
    40c8:	e5920018 	ldr	r0, [r2, #24]
    40cc:	e1d2c1dc 	ldrsb	ip, [r2, #28]
    40d0:	e5963000 	ldr	r3, [r6]
    40d4:	e5951000 	ldr	r1, [r5]
    40d8:	e0600004 	rsb	r0, r0, r4
    40dc:	e592e014 	ldr	lr, [r2, #20]
    40e0:	e0833000 	add	r3, r3, r0
    40e4:	e0812000 	add	r2, r1, r0
    40e8:	e25cc001 	subs	ip, ip, #1	; 0x1
    40ec:	e5863000 	str	r3, [r6]
    40f0:	e5852000 	str	r2, [r5]
    40f4:	e28ee008 	add	lr, lr, #8	; 0x8
    40f8:	4a00000a 	bmi	0x4128
    40fc:	e08c110c 	add	r1, ip, ip, lsl #2
    4100:	e08ee101 	add	lr, lr, r1, lsl #2
    4104:	e59e2008 	ldr	r2, [lr, #8]
    4108:	e59e1010 	ldr	r1, [lr, #16]
    410c:	e0823000 	add	r3, r2, r0
    4110:	e25cc001 	subs	ip, ip, #1	; 0x1
    4114:	e0812000 	add	r2, r1, r0
    4118:	e58e3008 	str	r3, [lr, #8]
    411c:	e58e2010 	str	r2, [lr, #16]
    4120:	e24ee014 	sub	lr, lr, #20	; 0x14
    4124:	5afffff6 	bpl	0x4104
    4128:	e598c000 	ldr	ip, [r8]
    412c:	e3a00001 	mov	r0, #1	; 0x1
    4130:	e58c4018 	str	r4, [ip, #24]
    4134:	eaffffa4 	b	0x3fcc
    4138:	e5982000 	ldr	r2, [r8]
    413c:	e5920014 	ldr	r0, [r2, #20]
    4140:	e0863106 	add	r3, r6, r6, lsl #2
    4144:	e0804103 	add	r4, r0, r3, lsl #2
    4148:	e244000c 	sub	r0, r4, #12	; 0xc
    414c:	ebfff8e6 	bl	0x24ec
    4150:	e2844008 	add	r4, r4, #8	; 0x8
    4154:	eaffffa8 	b	0x3ffc
    4158:	0020739c 	mlaeq	r0, ip, r3, r7
    415c:	002073b8 	streqh	r7, [r0], -r8
    4160:	002073ec 	eoreq	r7, r0, ip, ror #7
    4164:	00207418 	eoreq	r7, r0, r8, lsl r4
    4168:	00207424 	eoreq	r7, r0, r4, lsr #8
    416c:	002073b4 	streqh	r7, [r0], -r4
    4170:	e1a0c00d 	mov	ip, sp
    4174:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    4178:	e3500000 	cmp	r0, #0	; 0x0
    417c:	e24cb004 	sub	fp, ip, #4	; 0x4
    4180:	e24dd008 	sub	sp, sp, #8	; 0x8
    4184:	e1a01801 	mov	r1, r1, lsl #16
    4188:	e1a08821 	mov	r8, r1, lsr #16
    418c:	e50b002c 	str	r0, [fp, #-44]
    4190:	e50b2030 	str	r2, [fp, #-48]
    4194:	059f310c 	ldreq	r3, [pc, #268]	; 0x42a8
    4198:	0a00003d 	beq	0x4294
    419c:	e51b302c 	ldr	r3, [fp, #-44]
    41a0:	e59f9104 	ldr	r9, [pc, #260]	; 0x42ac
    41a4:	e5d36000 	ldrb	r6, [r3]
    41a8:	e59fa100 	ldr	sl, [pc, #256]	; 0x42b0
    41ac:	e59ae000 	ldr	lr, [sl]
    41b0:	e0862106 	add	r2, r6, r6, lsl #2
    41b4:	e08e1082 	add	r1, lr, r2, lsl #1
    41b8:	e281e010 	add	lr, r1, #16	; 0x10
    41bc:	e5de3007 	ldrb	r3, [lr, #7]
    41c0:	e59f10ec 	ldr	r1, [pc, #236]	; 0x42b4
    41c4:	e5c13000 	strb	r3, [r1]
    41c8:	e5d1c000 	ldrb	ip, [r1]
    41cc:	e24c2001 	sub	r2, ip, #1	; 0x1
    41d0:	e20200ff 	and	r0, r2, #255	; 0xff
    41d4:	e35000ff 	cmp	r0, #255	; 0xff
    41d8:	e589e000 	str	lr, [r9]
    41dc:	e5c12000 	strb	r2, [r1]
    41e0:	0a000013 	beq	0x4234
    41e4:	e59f70cc 	ldr	r7, [pc, #204]	; 0x42b8
    41e8:	e1a05001 	mov	r5, r1
    41ec:	e5de1002 	ldrb	r1, [lr, #2]
    41f0:	e5de4003 	ldrb	r4, [lr, #3]
    41f4:	e5d52000 	ldrb	r2, [r5]
    41f8:	e59ac000 	ldr	ip, [sl]
    41fc:	e1813404 	orr	r3, r1, r4, lsl #8
    4200:	e0820082 	add	r0, r2, r2, lsl #1
    4204:	e083100c 	add	r1, r3, ip
    4208:	e1a03100 	mov	r3, r0, lsl #2
    420c:	e19140b3 	ldrh	r4, [r1, r3]
    4210:	e2422001 	sub	r2, r2, #1	; 0x1
    4214:	e1540008 	cmp	r4, r8
    4218:	e0814003 	add	r4, r1, r3
    421c:	e202c0ff 	and	ip, r2, #255	; 0xff
    4220:	e5874000 	str	r4, [r7]
    4224:	0a000007 	beq	0x4248
    4228:	e35c00ff 	cmp	ip, #255	; 0xff
    422c:	e5c52000 	strb	r2, [r5]
    4230:	1affffed 	bne	0x41ec
    4234:	e3560000 	cmp	r6, #0	; 0x0
    4238:	0a000014 	beq	0x4290
    423c:	e599e000 	ldr	lr, [r9]
    4240:	e5de6008 	ldrb	r6, [lr, #8]
    4244:	eaffffd8 	b	0x41ac
    4248:	e3540000 	cmp	r4, #0	; 0x0
    424c:	0afffff8 	beq	0x4234
    4250:	e51b1030 	ldr	r1, [fp, #-48]
    4254:	e1a00004 	mov	r0, r4
    4258:	ebffff3b 	bl	0x3f4c
    425c:	e31000ff 	tst	r0, #255	; 0xff
    4260:	0a00000d 	beq	0x429c
    4264:	e5d4200a 	ldrb	r2, [r4, #10]
    4268:	e3120002 	tst	r2, #2	; 0x2
    426c:	0a00000a 	beq	0x429c
    4270:	ebfff892 	bl	0x24c0
    4274:	e51bc02c 	ldr	ip, [fp, #-44]
    4278:	e59f303c 	ldr	r3, [pc, #60]	; 0x42bc
    427c:	e580c004 	str	ip, [r0, #4]
    4280:	e1a0100c 	mov	r1, ip
    4284:	e5930000 	ldr	r0, [r3]
    4288:	ebfff914 	bl	0x26e0
    428c:	ea000002 	b	0x429c
    4290:	e59f3028 	ldr	r3, [pc, #40]	; 0x42c0
    4294:	e5930000 	ldr	r0, [r3]
    4298:	ebfffb6a 	bl	0x3048
    429c:	e24bd028 	sub	sp, fp, #40	; 0x28
    42a0:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    42a4:	e12fff1e 	bx	lr
    42a8:	00207400 	eoreq	r7, r0, r0, lsl #8
    42ac:	0020736c 	eoreq	r7, r0, ip, ror #6
    42b0:	00207424 	eoreq	r7, r0, r4, lsr #8
    42b4:	002073c0 	eoreq	r7, r0, r0, asr #7
    42b8:	00207370 	eoreq	r7, r0, r0, ror r3
    42bc:	002073ec 	eoreq	r7, r0, ip, ror #7
    42c0:	002073f4 	streqd	r7, [r0], -r4
    42c4:	e1a0c00d 	mov	ip, sp
    42c8:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    42cc:	e5d02009 	ldrb	r2, [r0, #9]
    42d0:	e3120001 	tst	r2, #1	; 0x1
    42d4:	e1a0e000 	mov	lr, r0
    42d8:	e24cb004 	sub	fp, ip, #4	; 0x4
    42dc:	e1a07001 	mov	r7, r1
    42e0:	13a00000 	movne	r0, #0	; 0x0
    42e4:	1a000027 	bne	0x4388
    42e8:	e3822001 	orr	r2, r2, #1	; 0x1
    42ec:	e5ce2009 	strb	r2, [lr, #9]
    42f0:	e5de0009 	ldrb	r0, [lr, #9]
    42f4:	e1a03120 	mov	r3, r0, lsr #2
    42f8:	e2130001 	ands	r0, r3, #1	; 0x1
    42fc:	0a000021 	beq	0x4388
    4300:	e59f1088 	ldr	r1, [pc, #136]	; 0x4390
    4304:	e5de2007 	ldrb	r2, [lr, #7]
    4308:	e5c12000 	strb	r2, [r1]
    430c:	e5d10000 	ldrb	r0, [r1]
    4310:	e240c001 	sub	ip, r0, #1	; 0x1
    4314:	e20c30ff 	and	r3, ip, #255	; 0xff
    4318:	e35300ff 	cmp	r3, #255	; 0xff
    431c:	e5c1c000 	strb	ip, [r1]
    4320:	0a000014 	beq	0x4378
    4324:	e59f6068 	ldr	r6, [pc, #104]	; 0x4394
    4328:	e59f5068 	ldr	r5, [pc, #104]	; 0x4398
    432c:	e1a04001 	mov	r4, r1
    4330:	e5de3002 	ldrb	r3, [lr, #2]
    4334:	e5de0003 	ldrb	r0, [lr, #3]
    4338:	e5d42000 	ldrb	r2, [r4]
    433c:	e595c000 	ldr	ip, [r5]
    4340:	e1831400 	orr	r1, r3, r0, lsl #8
    4344:	e0823082 	add	r3, r2, r2, lsl #1
    4348:	e081100c 	add	r1, r1, ip
    434c:	e1a03103 	mov	r3, r3, lsl #2
    4350:	e19100b3 	ldrh	r0, [r1, r3]
    4354:	e2422001 	sub	r2, r2, #1	; 0x1
    4358:	e3500003 	cmp	r0, #3	; 0x3
    435c:	e0810003 	add	r0, r1, r3
    4360:	e202c0ff 	and	ip, r2, #255	; 0xff
    4364:	e5860000 	str	r0, [r6]
    4368:	0a000003 	beq	0x437c
    436c:	e35c00ff 	cmp	ip, #255	; 0xff
    4370:	e5c42000 	strb	r2, [r4]
    4374:	1affffed 	bne	0x4330
    4378:	e3a00000 	mov	r0, #0	; 0x0
    437c:	e1a01007 	mov	r1, r7
    4380:	ebfffef1 	bl	0x3f4c
    4384:	e3a00001 	mov	r0, #1	; 0x1
    4388:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    438c:	e12fff1e 	bx	lr
    4390:	002073c0 	eoreq	r7, r0, r0, asr #7
    4394:	00207370 	eoreq	r7, r0, r0, ror r3
    4398:	00207424 	eoreq	r7, r0, r4, lsr #8
    439c:	e1a0c00d 	mov	ip, sp
    43a0:	e92dddf0 	stmdb	sp!, {r4, r5, r6, r7, r8, sl, fp, ip, lr, pc}
    43a4:	e59f5104 	ldr	r5, [pc, #260]	; 0x44b0
    43a8:	e20070ff 	and	r7, r0, #255	; 0xff
    43ac:	e5954000 	ldr	r4, [r5]
    43b0:	e0870107 	add	r0, r7, r7, lsl #2
    43b4:	e24cb004 	sub	fp, ip, #4	; 0x4
    43b8:	e084c080 	add	ip, r4, r0, lsl #1
    43bc:	e28c4010 	add	r4, ip, #16	; 0x10
    43c0:	e5d40009 	ldrb	r0, [r4, #9]
    43c4:	e3100001 	tst	r0, #1	; 0x1
    43c8:	e1a08003 	mov	r8, r3
    43cc:	e1a0a002 	mov	sl, r2
    43d0:	e20170ff 	and	r7, r1, #255	; 0xff
    43d4:	13a03000 	movne	r3, #0	; 0x0
    43d8:	1a000026 	bne	0x4478
    43dc:	e3802001 	orr	r2, r0, #1	; 0x1
    43e0:	e5c42009 	strb	r2, [r4, #9]
    43e4:	e5d41009 	ldrb	r1, [r4, #9]
    43e8:	e1a03121 	mov	r3, r1, lsr #2
    43ec:	e2133001 	ands	r3, r3, #1	; 0x1
    43f0:	0a000020 	beq	0x4478
    43f4:	e59f10b8 	ldr	r1, [pc, #184]	; 0x44b4
    43f8:	e5d43007 	ldrb	r3, [r4, #7]
    43fc:	e5c13000 	strb	r3, [r1]
    4400:	e5d10000 	ldrb	r0, [r1]
    4404:	e240e001 	sub	lr, r0, #1	; 0x1
    4408:	e20ec0ff 	and	ip, lr, #255	; 0xff
    440c:	e35c00ff 	cmp	ip, #255	; 0xff
    4410:	e5c1e000 	strb	lr, [r1]
    4414:	0a000013 	beq	0x4468
    4418:	e59f6098 	ldr	r6, [pc, #152]	; 0x44b8
    441c:	e1a0e001 	mov	lr, r1
    4420:	e5d43002 	ldrb	r3, [r4, #2]
    4424:	e5d40003 	ldrb	r0, [r4, #3]
    4428:	e5de2000 	ldrb	r2, [lr]
    442c:	e595c000 	ldr	ip, [r5]
    4430:	e1831400 	orr	r1, r3, r0, lsl #8
    4434:	e0823082 	add	r3, r2, r2, lsl #1
    4438:	e081100c 	add	r1, r1, ip
    443c:	e1a03103 	mov	r3, r3, lsl #2
    4440:	e19100b3 	ldrh	r0, [r1, r3]
    4444:	e2422001 	sub	r2, r2, #1	; 0x1
    4448:	e3500003 	cmp	r0, #3	; 0x3
    444c:	e0810003 	add	r0, r1, r3
    4450:	e202c0ff 	and	ip, r2, #255	; 0xff
    4454:	e5860000 	str	r0, [r6]
    4458:	0a000003 	beq	0x446c
    445c:	e35c00ff 	cmp	ip, #255	; 0xff
    4460:	e5ce2000 	strb	r2, [lr]
    4464:	1affffed 	bne	0x4420
    4468:	e3a00000 	mov	r0, #0	; 0x0
    446c:	e1a01008 	mov	r1, r8
    4470:	ebfffeb5 	bl	0x3f4c
    4474:	e3a03001 	mov	r3, #1	; 0x1
    4478:	e3530000 	cmp	r3, #0	; 0x0
    447c:	0a000001 	beq	0x4488
    4480:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    4484:	e12fff1e 	bx	lr
    4488:	e5d40002 	ldrb	r0, [r4, #2]
    448c:	e5d43003 	ldrb	r3, [r4, #3]
    4490:	e5952000 	ldr	r2, [r5]
    4494:	e180c403 	orr	ip, r0, r3, lsl #8
    4498:	e08c1002 	add	r1, ip, r2
    449c:	e0872087 	add	r2, r7, r7, lsl #1
    44a0:	e0810102 	add	r0, r1, r2, lsl #2
    44a4:	e1a0100a 	mov	r1, sl
    44a8:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    44ac:	eafffea6 	b	0x3f4c
    44b0:	00207424 	eoreq	r7, r0, r4, lsr #8
    44b4:	002073c0 	eoreq	r7, r0, r0, asr #7
    44b8:	00207370 	eoreq	r7, r0, r0, ror r3
    44bc:	e1a0c00d 	mov	ip, sp
    44c0:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    44c4:	e24cb004 	sub	fp, ip, #4	; 0x4
    44c8:	e20070ff 	and	r7, r0, #255	; 0xff
    44cc:	ebfff7fb 	bl	0x24c0
    44d0:	e5901004 	ldr	r1, [r0, #4]
    44d4:	e3510000 	cmp	r1, #0	; 0x0
    44d8:	e1a05000 	mov	r5, r0
    44dc:	059f4094 	ldreq	r4, [pc, #148]	; 0x4578
    44e0:	1a000020 	bne	0x4568
    44e4:	e5940000 	ldr	r0, [r4]
    44e8:	e1d011dc 	ldrsb	r1, [r0, #28]
    44ec:	e3510001 	cmp	r1, #1	; 0x1
    44f0:	e5d0301c 	ldrb	r3, [r0, #28]
    44f4:	0a000015 	beq	0x4550
    44f8:	e59f607c 	ldr	r6, [pc, #124]	; 0x457c
    44fc:	e2434001 	sub	r4, r3, #1	; 0x1
    4500:	e596c000 	ldr	ip, [r6]
    4504:	e5c0401c 	strb	r4, [r0, #28]
    4508:	e2450014 	sub	r0, r5, #20	; 0x14
    450c:	e04c4107 	sub	r4, ip, r7, lsl #2
    4510:	ebfff7fe 	bl	0x2510
    4514:	e2471001 	sub	r1, r7, #1	; 0x1
    4518:	e20100ff 	and	r0, r1, #255	; 0xff
    451c:	e35000ff 	cmp	r0, #255	; 0xff
    4520:	0a000008 	beq	0x4548
    4524:	e5961000 	ldr	r1, [r6]
    4528:	e240c001 	sub	ip, r0, #1	; 0x1
    452c:	e20c00ff 	and	r0, ip, #255	; 0xff
    4530:	e5b42004 	ldr	r2, [r4, #4]!
    4534:	e35000ff 	cmp	r0, #255	; 0xff
    4538:	e5812004 	str	r2, [r1, #4]
    453c:	e2811004 	add	r1, r1, #4	; 0x4
    4540:	1afffff8 	bne	0x4528
    4544:	e5861000 	str	r1, [r6]
    4548:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    454c:	e12fff1e 	bx	lr
    4550:	e59f3028 	ldr	r3, [pc, #40]	; 0x4580
    4554:	e59f2028 	ldr	r2, [pc, #40]	; 0x4584
    4558:	e5c0101f 	strb	r1, [r0, #31]
    455c:	e5c31000 	strb	r1, [r3]
    4560:	e5c21000 	strb	r1, [r2]
    4564:	eafffff7 	b	0x4548
    4568:	e59f4008 	ldr	r4, [pc, #8]	; 0x4578
    456c:	e5940000 	ldr	r0, [r4]
    4570:	ebfffa40 	bl	0x2e78
    4574:	eaffffda 	b	0x44e4
    4578:	002073ec 	eoreq	r7, r0, ip, ror #7
    457c:	0020739c 	mlaeq	r0, ip, r3, r7
    4580:	002073a2 	eoreq	r7, r0, r2, lsr #7
    4584:	002073a3 	eoreq	r7, r0, r3, lsr #7
    4588:	e3500000 	cmp	r0, #0	; 0x0
    458c:	e52de004 	str	lr, [sp, #-4]!
    4590:	e201c0ff 	and	ip, r1, #255	; 0xff
    4594:	0a000008 	beq	0x45bc
    4598:	e59fe048 	ldr	lr, [pc, #72]	; 0x45e8
    459c:	e59e1000 	ldr	r1, [lr]
    45a0:	e08c210c 	add	r2, ip, ip, lsl #2
    45a4:	e0813082 	add	r3, r1, r2, lsl #1
    45a8:	e5d31019 	ldrb	r1, [r3, #25]
    45ac:	e3110008 	tst	r1, #8	; 0x8
    45b0:	e5d00000 	ldrb	r0, [r0]
    45b4:	0a000007 	beq	0x45d8
    45b8:	e3a00001 	mov	r0, #1	; 0x1
    45bc:	e49de004 	ldr	lr, [sp], #4
    45c0:	e12fff1e 	bx	lr
    45c4:	e3500000 	cmp	r0, #0	; 0x0
    45c8:	0afffffb 	beq	0x45bc
    45cc:	e59e0000 	ldr	r0, [lr]
    45d0:	e0803082 	add	r3, r0, r2, lsl #1
    45d4:	e5d30018 	ldrb	r0, [r3, #24]
    45d8:	e150000c 	cmp	r0, ip
    45dc:	e0802100 	add	r2, r0, r0, lsl #2
    45e0:	1afffff7 	bne	0x45c4
    45e4:	eafffff3 	b	0x45b8
    45e8:	00207424 	eoreq	r7, r0, r4, lsr #8
    45ec:	e1a0c00d 	mov	ip, sp
    45f0:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
    45f4:	e24cb004 	sub	fp, ip, #4	; 0x4
    45f8:	ebffee84 	bl	0x10
    45fc:	e59f101c 	ldr	r1, [pc, #28]	; 0x4620
    4600:	e3a02000 	mov	r2, #0	; 0x0
    4604:	e2823001 	add	r3, r2, #1	; 0x1
    4608:	e7810102 	str	r0, [r1, r2, lsl #2]
    460c:	e20320ff 	and	r2, r3, #255	; 0xff
    4610:	e3520002 	cmp	r2, #2	; 0x2
    4614:	9afffffa 	bls	0x4604
    4618:	e89d6800 	ldmia	sp, {fp, sp, lr}
    461c:	e12fff1e 	bx	lr
    4620:	00207428 	eoreq	r7, r0, r8, lsr #8
    4624:	e1a0c00d 	mov	ip, sp
    4628:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
    462c:	e24cb004 	sub	fp, ip, #4	; 0x4
    4630:	ebffee76 	bl	0x10
    4634:	e59f5070 	ldr	r5, [pc, #112]	; 0x46ac
    4638:	e59fe070 	ldr	lr, [pc, #112]	; 0x46b0
    463c:	e1a04000 	mov	r4, r0
    4640:	e3a00000 	mov	r0, #0	; 0x0
    4644:	e080c100 	add	ip, r0, r0, lsl #2
    4648:	e06c320c 	rsb	r3, ip, ip, lsl #4
    464c:	e7952100 	ldr	r2, [r5, r0, lsl #2]
    4650:	e1a01103 	mov	r1, r3, lsl #2
    4654:	e0622004 	rsb	r2, r2, r4
    4658:	e2813f4b 	add	r3, r1, #300	; 0x12c
    465c:	e1520003 	cmp	r2, r3
    4660:	e280c001 	add	ip, r0, #1	; 0x1
    4664:	9a00000a 	bls	0x4694
    4668:	e1de10f4 	ldrsh	r1, [lr, #4]
    466c:	e59f2040 	ldr	r2, [pc, #64]	; 0x46b4
    4670:	e2811001 	add	r1, r1, #1	; 0x1
    4674:	e7854100 	str	r4, [r5, r0, lsl #2]
    4678:	e0c03192 	smull	r3, r0, r2, r1
    467c:	e1a03fc1 	mov	r3, r1, asr #31
    4680:	e06322c0 	rsb	r2, r3, r0, asr #5
    4684:	e0823102 	add	r3, r2, r2, lsl #2
    4688:	e0832103 	add	r2, r3, r3, lsl #2
    468c:	e0410102 	sub	r0, r1, r2, lsl #2
    4690:	e1ce00b4 	strh	r0, [lr, #4]
    4694:	e20c00ff 	and	r0, ip, #255	; 0xff
    4698:	e3500002 	cmp	r0, #2	; 0x2
    469c:	e28ee008 	add	lr, lr, #8	; 0x8
    46a0:	9affffe7 	bls	0x4644
    46a4:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
    46a8:	e12fff1e 	bx	lr
    46ac:	00207428 	eoreq	r7, r0, r8, lsr #8
    46b0:	00207374 	eoreq	r7, r0, r4, ror r3
    46b4:	51eb851f 	mvnpl	r8, pc, lsl r5
    46b8:	e3a03000 	mov	r3, #0	; 0x0
    46bc:	e1c130b0 	strh	r3, [r1]
    46c0:	e12fff1e 	bx	lr
    46c4:	e3a03000 	mov	r3, #0	; 0x0
    46c8:	e5c03000 	strb	r3, [r0]
    46cc:	e12fff1e 	bx	lr
    46d0:	e92d4010 	stmdb	sp!, {r4, lr}
    46d4:	e59fe060 	ldr	lr, [pc, #96]	; 0x473c
    46d8:	e1a04000 	mov	r4, r0
    46dc:	e89e000f 	ldmia	lr, {r0, r1, r2, r3}
    46e0:	e24dd010 	sub	sp, sp, #16	; 0x10
    46e4:	e88d000f 	stmia	sp, {r0, r1, r2, r3}
    46e8:	e28d3010 	add	r3, sp, #16	; 0x10
    46ec:	e3a0120a 	mov	r1, #-1610612736	; 0xa0000000
    46f0:	e083c104 	add	ip, r3, r4, lsl #2
    46f4:	e1a019c1 	mov	r1, r1, asr #19
    46f8:	e5910000 	ldr	r0, [r1]
    46fc:	e51c2010 	ldr	r2, [ip, #-16]
    4700:	e1803002 	orr	r3, r0, r2
    4704:	e5813000 	str	r3, [r1]
    4708:	e3e01ebe 	mvn	r1, #3040	; 0xbe0
    470c:	e511000f 	ldr	r0, [r1, #-15]
    4710:	e51c2010 	ldr	r2, [ip, #-16]
    4714:	e1803002 	orr	r3, r0, r2
    4718:	e501300f 	str	r3, [r1, #-15]
    471c:	e3e01d2f 	mvn	r1, #3008	; 0xbc0
    4720:	e51c2010 	ldr	r2, [ip, #-16]
    4724:	e511000f 	ldr	r0, [r1, #-15]
    4728:	e1803002 	orr	r3, r0, r2
    472c:	e501300f 	str	r3, [r1, #-15]
    4730:	e28dd010 	add	sp, sp, #16	; 0x10
    4734:	e8bd4010 	ldmia	sp!, {r4, lr}
    4738:	e12fff1e 	bx	lr
    473c:	002069f4 	streqd	r6, [r0], -r4
    4740:	e92d4010 	stmdb	sp!, {r4, lr}
    4744:	e59fe060 	ldr	lr, [pc, #96]	; 0x47ac
    4748:	e1a04000 	mov	r4, r0
    474c:	e89e000f 	ldmia	lr, {r0, r1, r2, r3}
    4750:	e24dd010 	sub	sp, sp, #16	; 0x10
    4754:	e88d000f 	stmia	sp, {r0, r1, r2, r3}
    4758:	e28d3010 	add	r3, sp, #16	; 0x10
    475c:	e3a0120a 	mov	r1, #-1610612736	; 0xa0000000
    4760:	e083c104 	add	ip, r3, r4, lsl #2
    4764:	e1a019c1 	mov	r1, r1, asr #19
    4768:	e5910000 	ldr	r0, [r1]
    476c:	e51c2010 	ldr	r2, [ip, #-16]
    4770:	e1803002 	orr	r3, r0, r2
    4774:	e5813000 	str	r3, [r1]
    4778:	e3e01ebe 	mvn	r1, #3040	; 0xbe0
    477c:	e511000f 	ldr	r0, [r1, #-15]
    4780:	e51c2010 	ldr	r2, [ip, #-16]
    4784:	e1803002 	orr	r3, r0, r2
    4788:	e501300f 	str	r3, [r1, #-15]
    478c:	e3e01d2f 	mvn	r1, #3008	; 0xbc0
    4790:	e51c2010 	ldr	r2, [ip, #-16]
    4794:	e511000b 	ldr	r0, [r1, #-11]
    4798:	e1803002 	orr	r3, r0, r2
    479c:	e501300b 	str	r3, [r1, #-11]
    47a0:	e28dd010 	add	sp, sp, #16	; 0x10
    47a4:	e8bd4010 	ldmia	sp!, {r4, lr}
    47a8:	e12fff1e 	bx	lr
    47ac:	002069f4 	streqd	r6, [r0], -r4
    47b0:	e59f1044 	ldr	r1, [pc, #68]	; 0x47fc
    47b4:	e3a03000 	mov	r3, #0	; 0x0
    47b8:	e59f2040 	ldr	r2, [pc, #64]	; 0x4800
    47bc:	e52de004 	str	lr, [sp, #-4]!
    47c0:	e59fc03c 	ldr	ip, [pc, #60]	; 0x4804
    47c4:	e1c130b0 	strh	r3, [r1]
    47c8:	e59fe038 	ldr	lr, [pc, #56]	; 0x4808
    47cc:	e5820000 	str	r0, [r2]
    47d0:	e1a00003 	mov	r0, r3
    47d4:	e2803001 	add	r3, r0, #1	; 0x1
    47d8:	e08c2180 	add	r2, ip, r0, lsl #3
    47dc:	e1a01080 	mov	r1, r0, lsl #1
    47e0:	e20300ff 	and	r0, r3, #255	; 0xff
    47e4:	e1d230b4 	ldrh	r3, [r2, #4]
    47e8:	e3500002 	cmp	r0, #2	; 0x2
    47ec:	e18130be 	strh	r3, [r1, lr]
    47f0:	9afffff7 	bls	0x47d4
    47f4:	e49de004 	ldr	lr, [sp], #4
    47f8:	e12fff1e 	bx	lr
    47fc:	00207440 	eoreq	r7, r0, r0, asr #8
    4800:	0020743c 	eoreq	r7, r0, ip, lsr r4
    4804:	00207374 	eoreq	r7, r0, r4, ror r3
    4808:	00207434 	eoreq	r7, r0, r4, lsr r4
    480c:	e1a0c00d 	mov	ip, sp
    4810:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
    4814:	e59f1130 	ldr	r1, [pc, #304]	; 0x494c
    4818:	e24cb004 	sub	fp, ip, #4	; 0x4
    481c:	e24dd008 	sub	sp, sp, #8	; 0x8
    4820:	e5d10000 	ldrb	r0, [r1]
    4824:	e2403001 	sub	r3, r0, #1	; 0x1
    4828:	e5c13000 	strb	r3, [r1]
    482c:	e5d12000 	ldrb	r2, [r1]
    4830:	e3a04000 	mov	r4, #0	; 0x0
    4834:	e3520000 	cmp	r2, #0	; 0x0
    4838:	e14b41b6 	strh	r4, [fp, #-22]
    483c:	e59fe10c 	ldr	lr, [pc, #268]	; 0x4950
    4840:	e59fc10c 	ldr	ip, [pc, #268]	; 0x4954
    4844:	1a00002e 	bne	0x4904
    4848:	e59f5108 	ldr	r5, [pc, #264]	; 0x4958
    484c:	e59f3108 	ldr	r3, [pc, #264]	; 0x495c
    4850:	e5950000 	ldr	r0, [r5]
    4854:	e5d32000 	ldrb	r2, [r3]
    4858:	e1500004 	cmp	r0, r4
    485c:	e5c12000 	strb	r2, [r1]
    4860:	0a000027 	beq	0x4904
    4864:	e5d01004 	ldrb	r1, [r0, #4]
    4868:	e1510004 	cmp	r1, r4
    486c:	1a000024 	bne	0x4904
    4870:	e3a00001 	mov	r0, #1	; 0x1
    4874:	e3a03801 	mov	r3, #65536	; 0x10000
    4878:	e1de20f0 	ldrsh	r2, [lr]
    487c:	e1a01083 	mov	r1, r3, lsl #1
    4880:	e1dc30f4 	ldrsh	r3, [ip, #4]
    4884:	e1520003 	cmp	r2, r3
    4888:	11844000 	orrne	r4, r4, r0
    488c:	e1dc20b4 	ldrh	r2, [ip, #4]
    4890:	e1a00821 	mov	r0, r1, lsr #16
    4894:	e1a03800 	mov	r3, r0, lsl #16
    4898:	11ce20b0 	strneh	r2, [lr]
    489c:	e3530807 	cmp	r3, #458752	; 0x70000
    48a0:	e28cc008 	add	ip, ip, #8	; 0x8
    48a4:	e28ee002 	add	lr, lr, #2	; 0x2
    48a8:	dafffff2 	ble	0x4878
    48ac:	e3a00a03 	mov	r0, #12288	; 0x3000
    48b0:	e24b1016 	sub	r1, fp, #22	; 0x16
    48b4:	ebffff7f 	bl	0x46b8
    48b8:	e15b31f6 	ldrsh	r3, [fp, #-22]
    48bc:	e1a01183 	mov	r1, r3, lsl #3
    48c0:	e14b11b6 	strh	r1, [fp, #-22]
    48c4:	e59fc094 	ldr	ip, [pc, #148]	; 0x4960
    48c8:	e15b21b6 	ldrh	r2, [fp, #-22]
    48cc:	e1dc00b0 	ldrh	r0, [ip]
    48d0:	e3a01000 	mov	r1, #0	; 0x0
    48d4:	e022e000 	eor	lr, r2, r0
    48d8:	e1cc20b0 	strh	r2, [ip]
    48dc:	e24b0017 	sub	r0, fp, #23	; 0x17
    48e0:	e18e4004 	orr	r4, lr, r4
    48e4:	ebffff76 	bl	0x46c4
    48e8:	e55bc017 	ldrb	ip, [fp, #-23]
    48ec:	e35c0000 	cmp	ip, #0	; 0x0
    48f0:	13843040 	orrne	r3, r4, #64	; 0x40
    48f4:	11a03803 	movne	r3, r3, lsl #16
    48f8:	11a04823 	movne	r4, r3, lsr #16
    48fc:	e3540000 	cmp	r4, #0	; 0x0
    4900:	1a000002 	bne	0x4910
    4904:	e24bd014 	sub	sp, fp, #20	; 0x14
    4908:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
    490c:	e12fff1e 	bx	lr
    4910:	e1a00804 	mov	r0, r4, lsl #16
    4914:	e1a02840 	mov	r2, r0, asr #16
    4918:	e3a03000 	mov	r3, #0	; 0x0
    491c:	e24b001a 	sub	r0, fp, #26	; 0x1a
    4920:	e3a01002 	mov	r1, #2	; 0x2
    4924:	e14b31ba 	strh	r3, [fp, #-26]
    4928:	ebfffd04 	bl	0x3d40
    492c:	e5950000 	ldr	r0, [r5]
    4930:	e15b21ba 	ldrh	r2, [fp, #-26]
    4934:	e1d0c0b8 	ldrh	ip, [r0, #8]
    4938:	e18ce002 	orr	lr, ip, r2
    493c:	e1c0e0b8 	strh	lr, [r0, #8]
    4940:	e3a01001 	mov	r1, #1	; 0x1
    4944:	ebfff8ee 	bl	0x2d04
    4948:	eaffffed 	b	0x4904
    494c:	0020743a 	eoreq	r7, r0, sl, lsr r4
    4950:	00207434 	eoreq	r7, r0, r4, lsr r4
    4954:	00207374 	eoreq	r7, r0, r4, ror r3
    4958:	0020743c 	eoreq	r7, r0, ip, lsr r4
    495c:	0020743b 	eoreq	r7, r0, fp, lsr r4
    4960:	00207440 	eoreq	r7, r0, r0, asr #8
    4964:	e352000f 	cmp	r2, #15	; 0xf
    4968:	e1a0c002 	mov	ip, r2
    496c:	e52de004 	str	lr, [sp, #-4]!
    4970:	e1a02000 	mov	r2, r0
    4974:	9a000016 	bls	0x49d4
    4978:	e1813000 	orr	r3, r1, r0
    497c:	e3130003 	tst	r3, #3	; 0x3
    4980:	1a000013 	bne	0x49d4
    4984:	e1a0e000 	mov	lr, r0
    4988:	e4913004 	ldr	r3, [r1], #4
    498c:	e48e3004 	str	r3, [lr], #4
    4990:	e4912004 	ldr	r2, [r1], #4
    4994:	e48e2004 	str	r2, [lr], #4
    4998:	e4913004 	ldr	r3, [r1], #4
    499c:	e48e3004 	str	r3, [lr], #4
    49a0:	e24cc010 	sub	ip, ip, #16	; 0x10
    49a4:	e4913004 	ldr	r3, [r1], #4
    49a8:	e35c000f 	cmp	ip, #15	; 0xf
    49ac:	e48e3004 	str	r3, [lr], #4
    49b0:	8afffff4 	bhi	0x4988
    49b4:	e35c0003 	cmp	ip, #3	; 0x3
    49b8:	9a000004 	bls	0x49d0
    49bc:	e4913004 	ldr	r3, [r1], #4
    49c0:	e24cc004 	sub	ip, ip, #4	; 0x4
    49c4:	e35c0003 	cmp	ip, #3	; 0x3
    49c8:	e48e3004 	str	r3, [lr], #4
    49cc:	8afffffa 	bhi	0x49bc
    49d0:	e1a0200e 	mov	r2, lr
    49d4:	e25cc001 	subs	ip, ip, #1	; 0x1
    49d8:	349df004 	ldrcc	pc, [sp], #4
    49dc:	e24cc001 	sub	ip, ip, #1	; 0x1
    49e0:	e4d13001 	ldrb	r3, [r1], #1
    49e4:	e37c0001 	cmn	ip, #1	; 0x1
    49e8:	e4c23001 	strb	r3, [r2], #1
    49ec:	1afffffa 	bne	0x49dc
    49f0:	e49df004 	ldr	pc, [sp], #4
    49f4:	00800000 	addeq	r0, r0, r0
    49f8:	10000000 	andne	r0, r0, r0
    49fc:	20000000 	andcs	r0, r0, r0
    4a00:	40000000 	andmi	r0, r0, r0
	...
    4b04:	01a8caf6 	streqd	ip, [r8, r6]!
    4b08:	01280176 	teqeq	r8, r6, ror r1
    4b0c:	00190027 	andeqs	r0, r9, r7, lsr #32
    4b10:	1b010833 	blne	0x46be4
    4b14:	01e00002 	mvneq	r0, r2
    4b18:	040004fc 	streq	r0, [r0], #-1276
    4b1c:	00120000 	andeqs	r0, r2, r0
    4b20:	04fc0210 	ldreqbt	r0, [ip], #528
    4b24:	00000a0d 	andeq	r0, r0, sp, lsl #20
    4b28:	02880004 	addeq	r0, r8, #4	; 0x4
    4b2c:	02010509 	andeq	r0, r1, #37748736	; 0x2400000
    4b30:	00040000 	andeq	r0, r4, r0
    4b34:	050a02a0 	streq	r0, [sl, #-672]
    4b38:	00000201 	andeq	r0, r0, r1, lsl #4
    4b3c:	02b80004 	adceqs	r0, r8, #4	; 0x4
    4b40:	0100050b 	tsteq	r0, fp, lsl #10
    4b44:	00040003 	andeq	r0, r4, r3
    4b48:	050b02c4 	streq	r0, [fp, #-708]
    4b4c:	00040100 	andeq	r0, r4, r0, lsl #2
    4b50:	02d00004 	sbceqs	r0, r0, #4	; 0x4
    4b54:	0100050b 	tsteq	r0, fp, lsl #10
    4b58:	00040004 	andeq	r0, r4, r4
    4b5c:	050b02dc 	streq	r0, [fp, #-732]
    4b60:	00040100 	andeq	r0, r4, r0, lsl #2
    4b64:	02e80004 	rsceq	r0, r8, #4	; 0x4
    4b68:	0100050b 	tsteq	r0, fp, lsl #10
    4b6c:	00040013 	andeq	r0, r4, r3, lsl r0
    4b70:	050b02f4 	streq	r0, [fp, #-756]
    4b74:	00130100 	andeqs	r0, r3, r0, lsl #2
    4b78:	03000004 	tsteq	r0, #4	; 0x4
    4b7c:	0100050b 	tsteq	r0, fp, lsl #10
    4b80:	00040014 	andeq	r0, r4, r4, lsl r0
    4b84:	050b030c 	streq	r0, [fp, #-780]
    4b88:	00130100 	andeqs	r0, r3, r0, lsl #2
    4b8c:	03180004 	tsteq	r8, #4	; 0x4
    4b90:	0100050b 	tsteq	r0, fp, lsl #10
    4b94:	00040013 	andeq	r0, r4, r3, lsl r0
    4b98:	050b0324 	streq	r0, [fp, #-804]
    4b9c:	00140100 	andeqs	r0, r4, r0, lsl #2
    4ba0:	03300004 	teqeq	r0, #4	; 0x4
    4ba4:	0100050b 	tsteq	r0, fp, lsl #10
    4ba8:	00040013 	andeq	r0, r4, r3, lsl r0
    4bac:	050b033c 	streq	r0, [fp, #-828]
    4bb0:	00130100 	andeqs	r0, r3, r0, lsl #2
    4bb4:	03480004 	cmpeq	r8, #4	; 0x4
    4bb8:	0100050b 	tsteq	r0, fp, lsl #10
    4bbc:	00020004 	andeq	r0, r2, r4
    4bc0:	050b0354 	streq	r0, [fp, #-852]
    4bc4:	00000200 	andeq	r0, r0, r0, lsl #4
    4bc8:	036c0002 	cmneq	ip, #2	; 0x2
    4bcc:	0300050b 	tsteq	r0, #46137344	; 0x2c00000
    4bd0:	00040000 	andeq	r0, r4, r0
    4bd4:	050b0390 	streq	r0, [fp, #-912]
    4bd8:	00140100 	andeqs	r0, r4, r0, lsl #2
    4bdc:	039c0004 	orreqs	r0, ip, #4	; 0x4
    4be0:	0200050b 	andeq	r0, r0, #46137344	; 0x2c00000
    4be4:	00090003 	andeq	r0, r9, r3
    4be8:	050b03b4 	streq	r0, [fp, #-948]
    4bec:	04000704 	streq	r0, [r0], #-1796
    4bf0:	04080002 	streq	r0, [r8], #-2
    4bf4:	0200050f 	andeq	r0, r0, #62914560	; 0x3c00000
    4bf8:	00020000 	andeq	r0, r2, r0
    4bfc:	050f0420 	streq	r0, [pc, #-1056]	; 0x47e4
    4c00:	08000100 	stmeqda	r0, {r8}
    4c04:	042c0002 	streqt	r0, [ip], #-2
    4c08:	0100050f 	tsteq	r0, pc, lsl #10
    4c0c:	00160800 	andeqs	r0, r6, r0, lsl #16
    4c10:	050f0438 	streq	r0, [pc, #-1080]	; 0x47e0
    4c14:	04010602 	streq	r0, [r1], #-1538
    4c18:	04800002 	streq	r0, [r0], #2
    4c1c:	02000511 	andeq	r0, r0, #71303168	; 0x4400000
    4c20:	00030000 	andeq	r0, r3, r0
    4c24:	05110498 	ldreq	r0, [r1, #-1176]
    4c28:	04000501 	streq	r0, [r0], #-1281
	...
    4c78:	a0000000 	andge	r0, r0, r0
    4c7c:	a008a004 	andge	sl, r8, r4
    4c80:	0010000c 	andeqs	r0, r0, ip
    4c84:	00180014 	andeqs	r0, r8, r4, lsl r0
    4c88:	0020001c 	eoreq	r0, r0, ip, lsl r0
    4c8c:	0028a024 	eoreq	sl, r8, r4, lsr #32
    4c90:	a030002c 	eorges	r0, r0, ip, lsr #32
    4c94:	90369034 	eorlss	r9, r6, r4, lsr r0
    4c98:	903a9038 	eorlss	r9, sl, r8, lsr r0
    4c9c:	903e903c 	eorlss	r9, lr, ip, lsr r0
    4ca0:	90429040 	subls	r9, r2, r0, asr #32
    4ca4:	90469044 	subls	r9, r6, r4, asr #32
    4ca8:	004a9048 	subeq	r9, sl, r8, asr #32
    4cac:	000007ff 	streqd	r0, [r0], -pc
    4cb0:	040a07ff 	streq	r0, [sl], #-2047
    4cb4:	040a0803 	streq	r0, [sl], #-2051
    4cb8:	040a0807 	streq	r0, [sl], #-2055
    4cbc:	040a080b 	streq	r0, [sl], #-2059
    4cc0:	040a080f 	streq	r0, [sl], #-2063
    4cc4:	040a0813 	streq	r0, [sl], #-2067
    4cc8:	040a0817 	streq	r0, [sl], #-2071
    4ccc:	040a081b 	streq	r0, [sl], #-2075
    4cd0:	040a081f 	streq	r0, [sl], #-2079
    4cd4:	040a0823 	streq	r0, [sl], #-2083
    4cd8:	040a0827 	streq	r0, [sl], #-2087
    4cdc:	040a082b 	streq	r0, [sl], #-2091
    4ce0:	040a082f 	streq	r0, [sl], #-2095
    4ce4:	04d40002 	ldreqb	r0, [r4], #2
    4ce8:	00010512 	andeq	r0, r1, r2, lsl r5
    4cec:	00000001 	andeq	r0, r0, r1
    4cf0:	00000007 	andeq	r0, r0, r7
    4cf4:	00000000 	andeq	r0, r0, r0
    4cf8:	00010003 	andeq	r0, r1, r3
    4cfc:	04d40032 	ldreqb	r0, [r4], #50
    4d00:	01010513 	tsteq	r1, r3, lsl r5
    4d04:	00000001 	andeq	r0, r0, r1
    4d08:	00000022 	andeq	r0, r0, r2, lsr #32
    4d0c:	00000000 	andeq	r0, r0, r0
    4d10:	00050001 	andeq	r0, r5, r1
    4d14:	04d40033 	ldreqb	r0, [r4], #51
    4d18:	02010516 	andeq	r0, r1, #92274688	; 0x5800000
    4d1c:	00000001 	andeq	r0, r0, r1
    4d20:	04d40002 	ldreqb	r0, [r4], #2
    4d24:	02010522 	andeq	r0, r1, #142606336	; 0x8800000
    4d28:	00000001 	andeq	r0, r0, r1
    4d2c:	04d40034 	ldreqb	r0, [r4], #52
    4d30:	02030529 	andeq	r0, r3, #171966464	; 0xa400000
    4d34:	00000002 	andeq	r0, r0, r2
    4d38:	00000001 	andeq	r0, r0, r1
    4d3c:	00000000 	andeq	r0, r0, r0
    4d40:	00000001 	andeq	r0, r0, r1
    4d44:	00000008 	andeq	r0, r0, r8
    4d48:	00000000 	andeq	r0, r0, r0
    4d4c:	00010001 	andeq	r0, r1, r1
    4d50:	0000000b 	andeq	r0, r0, fp
    4d54:	00000000 	andeq	r0, r0, r0
    4d58:	00050000 	andeq	r0, r5, r0
    4d5c:	0000000c 	andeq	r0, r0, ip
    4d60:	00000000 	andeq	r0, r0, r0
    4d64:	00010001 	andeq	r0, r1, r1
    4d68:	0000000d 	andeq	r0, r0, sp
    4d6c:	00000000 	andeq	r0, r0, r0
    4d70:	00010002 	andeq	r0, r1, r2
    4d74:	0000000e 	andeq	r0, r0, lr
    4d78:	00000000 	andeq	r0, r0, r0
    4d7c:	00010001 	andeq	r0, r1, r1
    4d80:	00000011 	andeq	r0, r0, r1, lsl r0
    4d84:	00000000 	andeq	r0, r0, r0
    4d88:	00010002 	andeq	r0, r1, r2
    4d8c:	04d40035 	ldreqb	r0, [r4], #53
    4d90:	0504054b 	streq	r0, [r4, #-1355]
    4d94:	00000004 	andeq	r0, r0, r4
    4d98:	04d40032 	ldreqb	r0, [r4], #50
    4d9c:	01010562 	tsteq	r1, r2, ror #10
    4da0:	00000001 	andeq	r0, r0, r1
    4da4:	04d40002 	ldreqb	r0, [r4], #2
    4da8:	02010564 	andeq	r0, r1, #419430400	; 0x19000000
    4dac:	00000001 	andeq	r0, r0, r1
    4db0:	04d40034 	ldreqb	r0, [r4], #52
    4db4:	0202056f 	andeq	r0, r2, #465567744	; 0x1bc00000
    4db8:	00000002 	andeq	r0, r0, r2
    4dbc:	04d40002 	ldreqb	r0, [r4], #2
    4dc0:	01010579 	tsteq	r1, r9, ror r5
    4dc4:	00000001 	andeq	r0, r0, r1
    4dc8:	04d40002 	ldreqb	r0, [r4], #2
    4dcc:	0101057e 	tsteq	r1, lr, ror r5
    4dd0:	00000001 	andeq	r0, r0, r1
    4dd4:	04d40002 	ldreqb	r0, [r4], #2
    4dd8:	01010583 	smlabbeq	r1, r3, r5, r0
    4ddc:	00000001 	andeq	r0, r0, r1
    4de0:	04d40002 	ldreqb	r0, [r4], #2
    4de4:	01010588 	smlabbeq	r1, r8, r5, r0
    4de8:	00000001 	andeq	r0, r0, r1
    4dec:	04d40002 	ldreqb	r0, [r4], #2
    4df0:	0101058d 	smlabbeq	r1, sp, r5, r0
    4df4:	00000001 	andeq	r0, r0, r1
    4df8:	04d40002 	ldreqb	r0, [r4], #2
    4dfc:	01010592 	streqb	r0, [r1, -r2]
    4e00:	00000001 	andeq	r0, r0, r1
    4e04:	04d40002 	ldreqb	r0, [r4], #2
    4e08:	01010597 	streqb	r0, [r1, -r7]
    4e0c:	00000001 	andeq	r0, r0, r1
    4e10:	04d40002 	ldreqb	r0, [r4], #2
    4e14:	0101059c 	streqb	r0, [r1, -ip]
    4e18:	00000001 	andeq	r0, r0, r1
    4e1c:	04d40002 	ldreqb	r0, [r4], #2
    4e20:	010105a1 	smlatbeq	r1, r1, r5, r0
    4e24:	00000001 	andeq	r0, r0, r1
    4e28:	04d40002 	ldreqb	r0, [r4], #2
    4e2c:	010105a6 	smlatbeq	r1, r6, r5, r0
    4e30:	00000001 	andeq	r0, r0, r1
    4e34:	04d40002 	ldreqb	r0, [r4], #2
    4e38:	010105ab 	smlatbeq	r1, fp, r5, r0
    4e3c:	00000001 	andeq	r0, r0, r1
    4e40:	04d40002 	ldreqb	r0, [r4], #2
    4e44:	010105b0 	streqh	r0, [r1, -r0]
    4e48:	00000001 	andeq	r0, r0, r1
    4e4c:	04d40002 	ldreqb	r0, [r4], #2
    4e50:	010105b5 	streqh	r0, [r1, -r5]
    4e54:	00000001 	andeq	r0, r0, r1
    4e58:	04d40002 	ldreqb	r0, [r4], #2
    4e5c:	010105ba 	streqh	r0, [r1, -sl]
    4e60:	00000001 	andeq	r0, r0, r1
    4e64:	04d40000 	ldreqb	r0, [r4]
    4e68:	010105bf 	streqh	r0, [r1, -pc]
    4e6c:	00040001 	andeq	r0, r4, r1
    4e70:	04d40002 	ldreqb	r0, [r4], #2
    4e74:	010105c6 	smlabteq	r1, r6, r5, r0
    4e78:	00000001 	andeq	r0, r0, r1
    4e7c:	04d40036 	ldreqb	r0, [r4], #54
    4e80:	050605cb 	streq	r0, [r6, #-1483]
    4e84:	00040005 	andeq	r0, r4, r5
    4e88:	04d40037 	ldreqb	r0, [r4], #55
    4e8c:	010005e8 	smlatteq	r0, r8, r5, r0
    4e90:	00040000 	andeq	r0, r4, r0
    4e94:	04d40002 	ldreqb	r0, [r4], #2
    4e98:	010105ec 	smlatteq	r1, ip, r5, r0
    4e9c:	00000001 	andeq	r0, r0, r1
    4ea0:	04d40002 	ldreqb	r0, [r4], #2
    4ea4:	010105f1 	streqd	r0, [r1, -r1]
    4ea8:	00000001 	andeq	r0, r0, r1
    4eac:	04d40034 	ldreqb	r0, [r4], #52
    4eb0:	020205f6 	andeq	r0, r2, #1031798784	; 0x3d800000
    4eb4:	00000002 	andeq	r0, r0, r2
    4eb8:	04d40003 	ldreqb	r0, [r4], #3
    4ebc:	040005fc 	streq	r0, [r0], #-1532
    4ec0:	00040000 	andeq	r0, r4, r0
    4ec4:	04d40038 	ldreqb	r0, [r4], #56
    4ec8:	03020637 	tsteq	r2, #57671680	; 0x3700000
    4ecc:	00000002 	andeq	r0, r0, r2
    4ed0:	04d40039 	ldreqb	r0, [r4], #57
    4ed4:	0301064e 	tsteq	r1, #81788928	; 0x4e00000
    4ed8:	00000001 	andeq	r0, r0, r1
    4edc:	04d4003a 	ldreqb	r0, [r4], #58
    4ee0:	0303065e 	tsteq	r3, #98566144	; 0x5e00000
    4ee4:	00000003 	andeq	r0, r0, r3
    4ee8:	00000024 	andeq	r0, r0, r4, lsr #32
    4eec:	00000000 	andeq	r0, r0, r0
    4ef0:	00050002 	andeq	r0, r5, r2
    4ef4:	00000025 	andeq	r0, r0, r5, lsr #32
    4ef8:	00000000 	andeq	r0, r0, r0
    4efc:	00050003 	andeq	r0, r5, r3
    4f00:	04d4003b 	ldreqb	r0, [r4], #59
    4f04:	04030671 	streq	r0, [r3], #-1649
    4f08:	00020001 	andeq	r0, r2, r1
    4f0c:	04d40002 	ldreqb	r0, [r4], #2
    4f10:	010106a1 	smlatbeq	r1, r1, r6, r0
    4f14:	00000001 	andeq	r0, r0, r1
    4f18:	04d40037 	ldreqb	r0, [r4], #55
    4f1c:	020006a6 	andeq	r0, r0, #174063616	; 0xa600000
    4f20:	00040000 	andeq	r0, r4, r0
    4f24:	0000003b 	andeq	r0, r0, fp, lsr r0
    4f28:	00000000 	andeq	r0, r0, r0
    4f2c:	00000001 	andeq	r0, r0, r1
    4f30:	0000003c 	andeq	r0, r0, ip, lsr r0
    4f34:	00000000 	andeq	r0, r0, r0
    4f38:	00000004 	andeq	r0, r0, r4
    4f3c:	04d40003 	ldreqb	r0, [r4], #3
    4f40:	020006ba 	andeq	r0, r0, #195035136	; 0xba00000
    4f44:	00040000 	andeq	r0, r4, r0
    4f48:	04d40002 	ldreqb	r0, [r4], #2
    4f4c:	030106c9 	tsteq	r1, #210763776	; 0xc900000
    4f50:	00000001 	andeq	r0, r0, r1
    4f54:	04d4003d 	ldreqb	r0, [r4], #61
    4f58:	020106d9 	andeq	r0, r1, #227540992	; 0xd900000
    4f5c:	00040200 	andeq	r0, r4, r0, lsl #4
    4f60:	04e4003e 	streqbt	r0, [r4], #62
    4f64:	04040718 	streq	r0, [r4], #-1816
    4f68:	00000003 	andeq	r0, r0, r3
    4f6c:	04e4003f 	streqbt	r0, [r4], #63
    4f70:	0303075f 	tsteq	r3, #24903680	; 0x17c0000
    4f74:	00000003 	andeq	r0, r0, r3
    4f78:	04e40001 	streqbt	r0, [r4], #1
    4f7c:	03030768 	tsteq	r3, #27262976	; 0x1a00000
    4f80:	00000101 	andeq	r0, r0, r1, lsl #2
    4f84:	04ec0002 	streqbt	r0, [ip], #2
    4f88:	0101079f 	streqb	r0, [r1, -pc]
    4f8c:	00000001 	andeq	r0, r0, r1
    4f90:	0000001b 	andeq	r0, r0, fp, lsl r0
    4f94:	00000000 	andeq	r0, r0, r0
    4f98:	00050002 	andeq	r0, r5, r2
    4f9c:	04ec0003 	streqbt	r0, [ip], #3
    4fa0:	030007a4 	tsteq	r0, #42991616	; 0x2900000
    4fa4:	00040000 	andeq	r0, r4, r0
    4fa8:	04ec0040 	streqbt	r0, [ip], #64
    4fac:	010207b0 	streqh	r0, [r2, -r0]
    4fb0:	00000002 	andeq	r0, r0, r2
    4fb4:	04ec0002 	streqbt	r0, [ip], #2
    4fb8:	010107b9 	streqh	r0, [r1, -r9]
    4fbc:	00000001 	andeq	r0, r0, r1
    4fc0:	04ec0041 	streqbt	r0, [ip], #65
    4fc4:	040507be 	streq	r0, [r5], #-1982
    4fc8:	00000203 	andeq	r0, r0, r3, lsl #4
    4fcc:	00000026 	andeq	r0, r0, r6, lsr #32
    4fd0:	00000000 	andeq	r0, r0, r0
    4fd4:	00010001 	andeq	r0, r1, r1
    4fd8:	00350006 	eoreqs	r0, r5, r6
    4fdc:	00030038 	andeq	r0, r3, r8, lsr r0
    4fe0:	003a0038 	eoreqs	r0, sl, r8, lsr r0
    4fe4:	00030038 	andeq	r0, r3, r8, lsr r0
    4fe8:	00330000 	eoreqs	r0, r3, r0
    4fec:	000d0033 	andeq	r0, sp, r3, lsr r0
    4ff0:	003d0006 	eoreqs	r0, sp, r6
    4ff4:	0003003e 	andeq	r0, r3, lr, lsr r0
    4ff8:	0040003e 	subeq	r0, r0, lr, lsr r0
    4ffc:	0003003e 	andeq	r0, r3, lr, lsr r0
    5000:	0a0a0a00 	beq	0x287808
    5004:	0808080a 	stmeqda	r8, {r1, r3, fp}
    5008:	08080808 	stmeqda	r8, {r3, fp}
    500c:	0a000000 	beq	0x5014
    5010:	0a0a0009 	beq	0x28503c
    5014:	12b10900 	adcnes	r0, r1, #0	; 0x0
    5018:	b42ab000 	strltt	fp, [sl]
    501c:	a4041b80 	strge	r1, [r4], #-2944
    5020:	ac040500 	cfstr32ge	mvfx0, [r4], {0}
    5024:	122aac03 	eorne	sl, sl, #768	; 0x300
    5028:	0201b700 	andeq	fp, r1, #0	; 0x0
    502c:	00b72ab1 	ldreqh	r2, [r7], r1
    5030:	0501b800 	streq	fp, [r1, #-2048]
    5034:	00c72c4d 	sbceq	r2, r7, sp, asr #24
    5038:	b6082a0b 	strlt	r2, [r8], -fp, lsl #20
    503c:	00a70d10 	adceq	r0, r7, r0, lsl sp
    5040:	b62c2a0b 	strltt	r2, [ip], -fp, lsl #20
    5044:	10b60c00 	adcnes	r0, r6, r0, lsl #24
    5048:	b52b2a0d 	strlt	r2, [fp, #-2573]!
    504c:	2ab11f00 	bcs	0xfec4cc54
    5050:	2a0000b7 	bcs	0x5334
    5054:	b505bc1d 	strlt	fp, [r5, #-3101]
    5058:	1c2b0400 	cfstrsne	mvf0, [fp]
    505c:	0400b42a 	streq	fp, [r0], #-1066
    5060:	12b81d03 	adcnes	r1, r8, #192	; 0xc0
    5064:	b02ab101 	eorlt	fp, sl, r1, lsl #2
    5068:	0000b72a 	andeq	fp, r0, sl, lsr #14
    506c:	b500122a 	strlt	r1, [r0, #-554]
    5070:	2ab10400 	bcs	0xfec46078
    5074:	2a0000b7 	bcs	0x5358
    5078:	0400b52b 	streq	fp, [r0], #-1323
    507c:	03b72ab1 	moveqs	r2, #724992	; 0xb1000
    5080:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    5084:	2ab10004 	bcs	0xfec4509c
    5088:	b10004b7 	strlth	r0, [r0, -r7]
    508c:	0004b72a 	andeq	fp, r4, sl, lsr #14
    5090:	13b72ab1 	movnes	r2, #724992	; 0xb1000
    5094:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    5098:	2ab10013 	bcs	0xfec450ec
    509c:	b10014b7 	strlth	r1, [r0, -r7]
    50a0:	0013b72a 	andeqs	fp, r3, sl, lsr #14
    50a4:	13b72ab1 	movnes	r2, #724992	; 0xb1000
    50a8:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    50ac:	2ab10014 	bcs	0xfec45104
    50b0:	b10013b7 	strlth	r1, [r0, -r7]
    50b4:	0013b72a 	andeqs	fp, r3, sl, lsr #14
    50b8:	04b72ab1 	ldreqt	r2, [r7], #2737
    50bc:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    50c0:	b2b10000 	adclts	r0, r1, #0	; 0x0
    50c4:	00b60515 	adceqs	r0, r6, r5, lsl r5
    50c8:	b72ab139 	undefined
    50cc:	03b10000 	moveqs	r0, #0	; 0x0
    50d0:	00a70536 	adceq	r0, r7, r6, lsr r5
    50d4:	05152c12 	ldreq	r2, [r5, #-3090]
    50d8:	152a601d 	strne	r6, [sl, #-29]!
    50dc:	34601b05 	strccbt	r1, [r0], #-2821
    50e0:	01058455 	tsteq	r5, r5, asr r4
    50e4:	04150515 	ldreq	r0, [r5], #-1301
    50e8:	b1edffa1 	mvnlt	pc, r1, lsr #31
    50ec:	b00116b8 	strlth	r1, [r1], -r8
    50f0:	0014b72a 	andeqs	fp, r4, sl, lsr #14
    50f4:	03b72ab1 	moveqs	r2, #724992	; 0xb1000
    50f8:	2b2ab100 	blcs	0xab1500
    50fc:	b10103b7 	strlth	r0, [r1, -r7]
    5100:	591500bb 	ldmpldb	r5, {r0, r1, r3, r4, r5, r7}
    5104:	0115b703 	tsteq	r5, r3, lsl #14
    5108:	bb0315b3 	bllt	0xca7dc
    510c:	04591500 	ldreqb	r1, [r9], #-1280
    5110:	b30115b7 	tstlt	r1, #767557632	; 0x2dc00000
    5114:	00bb0415 	adceqs	r0, fp, r5, lsl r4
    5118:	b7055915 	smladlt	r5, r5, r9, r5
    511c:	15b30115 	ldrne	r0, [r3, #277]!
    5120:	00bc0605 	adceqs	r0, ip, r5, lsl #12
    5124:	b2035900 	andlt	r5, r3, #0	; 0x0
    5128:	59530315 	ldmpldb	r3, {r0, r2, r4, r8, r9}^
    512c:	0415b204 	ldreq	fp, [r5], #-516
    5130:	b2055953 	andlt	r5, r5, #1359872	; 0x14c000
    5134:	b3530515 	cmplt	r3, #88080384	; 0x5400000
    5138:	2ab10615 	bcs	0xfec46994
    513c:	2a0000b7 	bcs	0x5420
    5140:	0890b503 	ldmeqia	r0, {r0, r1, r8, sl, ip, sp, pc}
    5144:	a0b51b2a 	adcges	r1, r5, sl, lsr #22
    5148:	11062a04 	tstne	r6, r4, lsl #20
    514c:	20b68000 	adccss	r8, r6, r0
    5150:	1911b13a 	ldmnedb	r1, {r1, r3, r4, r5, r8, ip, sp, pc}
    5154:	001011c4 	andeqs	r1, r0, r4, asr #3
    5158:	04a0b42a 	streqt	fp, [r0], #1066
    515c:	1ab89360 	bne	0xfee29ee4
    5160:	b42ab101 	strltt	fp, [sl], #-257
    5164:	041b04a0 	ldreq	r0, [fp], #-1184
    5168:	2a0515b8 	bcs	0x14a850
    516c:	1c04a0b4 	stcne	0, cr10, [r4], {180}
    5170:	0515b803 	ldreq	fp, [r5, #-2051]
    5174:	a0b42ab1 	ldrgeh	r2, [r4], r1
    5178:	15b80404 	ldrne	r0, [r8, #1028]!
    517c:	3d033c04 	stccc	12, cr3, [r3, #-16]
    5180:	2a1700a7 	bcs	0x5c5424
    5184:	1c0a00b4 	stcne	0, cr0, [sl], {180}
    5188:	b42a2a32 	strltt	r2, [sl], #-2610
    518c:	b61b0ea0 	ldrlt	r0, [fp], -r0, lsr #29
    5190:	00003c30 	andeq	r3, r0, r0, lsr ip
    5194:	1c010284 	sfmne	f0, 4, [r1], {132}
    5198:	0890b42a 	ldmeqia	r0, {r1, r3, r5, sl, ip, sp, pc}
    519c:	2ae7ffa1 	bcs	0xffa05028
    51a0:	0ea0b51b 	mcreq	5, 5, fp, cr0, cr11, {0}
    51a4:	00b72ab1 	ldreqh	r2, [r7], r1
    51a8:	16b2b100 	ldrnet	fp, [r2], r0, lsl #2
    51ac:	0d00c707 	stceq	7, cr12, [r0, #-28]
    51b0:	591600bb 	ldmpldb	r6, {r0, r1, r3, r4, r5, r7}
    51b4:	b30016b7 	tstlt	r0, #191889408	; 0xb700000
    51b8:	16b20716 	ssatne	r0, #19, r6, LSL #14
    51bc:	00bbb007 	adceqs	fp, fp, r7
    51c0:	19b75919 	ldmneib	r7!, {r0, r3, r4, r8, fp, ip, lr}
    51c4:	0819b301 	ldmeqda	r9, {r0, r8, r9, ip, sp, pc}
    51c8:	0c19b303 	ldceq	3, cr11, [r9], {3}
    51cc:	01b72ab1 	ldreqh	r2, [r7, r1]!
    51d0:	00bb2a01 	adceqs	r2, fp, r1, lsl #20
    51d4:	1bb7591b 	blne	0xfeddb648
    51d8:	2700b502 	strcs	fp, [r0, -r2, lsl #10]
    51dc:	0819b2b1 	ldmeqda	r9, {r0, r4, r5, r7, r9, ip, sp, pc}
    51e0:	b2c24b59 	sbclt	r4, r2, #91136	; 0x16400
    51e4:	00b60819 	adceqs	r0, r6, r9, lsl r8
    51e8:	27009a33 	smladxcs	r0, r3, sl, r9
    51ec:	0abc0710 	beq	0xfef06e34
    51f0:	100a19b3 	strneh	r1, [sl], -r3
    51f4:	0000bc07 	andeq	fp, r0, r7, lsl #24
    51f8:	b20b19b3 	andlt	r1, fp, #2932736	; 0x2cc000
    51fc:	b6040819 	undefined
    5200:	19b21110 	ldmneib	r2!, {r4, r8, ip}
    5204:	b60a1008 	strlt	r1, [sl], -r8
    5208:	19b20d10 	ldmneib	r2!, {r4, r8, sl, fp}
    520c:	0800b608 	stmeqda	r0, {r3, r9, sl, ip, sp, pc}
    5210:	00a7c32a 	adceq	ip, r7, sl, lsr #6
    5214:	bfc32a06 	swilt	0x00c32a06
    5218:	b00819b2 	strlth	r1, [r8], -r2
    521c:	a0b4592a 	adcges	r5, r4, sl, lsr #18
    5220:	b5801b23 	strlt	r1, [r0, #2851]
    5224:	3e0323a0 	cdpcc	3, 0, cr2, cr3, cr0, {5}
    5228:	b21200a7 	andlts	r0, r2, #167	; 0xa7
    522c:	321d0b19 	andccs	r0, sp, #25600	; 0x6400
    5230:	0600a62c 	streq	sl, [r0], -ip, lsr #12
    5234:	840d00a7 	strhi	r0, [sp], #-167
    5238:	b21d0103 	andlts	r0, sp, #-1073741824	; 0xc0000000
    523c:	ffa10c19 	swinv	0x00a10c19
    5240:	19b21ded 	ldmneib	r2!, {r0, r2, r3, r5, r6, r7, r8, sl, fp, ip}
    5244:	1900a00c 	stmnedb	r0, {r2, r3, sp, pc}
    5248:	b20a19b2 	andlt	r1, sl, #2916352	; 0x2c8000
    524c:	4f1b0c19 	swimi	0x001b0c19
    5250:	b20b19b2 	andlt	r1, fp, #2916352	; 0x2c8000
    5254:	04590c19 	ldreqb	r0, [r9], #-3097
    5258:	0c19b360 	ldceq	3, cr11, [r9], {96}
    525c:	b62a532c 	strltt	r5, [sl], -ip, lsr #6
    5260:	2ab10e00 	bcs	0xfec48a68
    5264:	2c781b04 	ldccsl	11, cr1, [r8], #-16
    5268:	b13e20b6 	ldrlth	r2, [lr, -r6]!
    526c:	2700b42a 	strcs	fp, [r0, -sl, lsr #8]
    5270:	23a0b42a 	movcs	fp, #704643072	; 0x2a000000
    5274:	4120b603 	teqmi	r0, r3, lsl #12
    5278:	a73d033c 	undefined
    527c:	b21b1a00 	andlts	r1, fp, #0	; 0x0
    5280:	2e1c0a19 	mrccs	10, 0, r0, cr12, cr9, {0}
    5284:	0d00997e 	stceq	9, cr9, [r0, #-504]
    5288:	1c0b19b2 	stcne	9, cr1, [fp], {178}
    528c:	3b00b632 	blcc	0x32b5c
    5290:	02840000 	addeq	r0, r4, #0	; 0x0
    5294:	19b21c01 	ldmneib	r2!, {r0, sl, fp, ip}
    5298:	e5ffa10c 	ldrb	sl, [pc, #268]!	; 0x53ac
    529c:	570400a7 	strpl	r0, [r4, -r7, lsr #1]
    52a0:	2accffa7 	bcs	0xff345144
    52a4:	b10000b7 	strlth	r0, [r0, -r7]
    52a8:	591b00bb 	ldmpldb	fp, {r0, r1, r3, r4, r5, r7}
    52ac:	011bb704 	tsteq	fp, r4, lsl #14
    52b0:	b1181bb3 	ldrlth	r1, [r8, -r3]
    52b4:	0000b72a 	andeq	fp, r0, sl, lsr #14
    52b8:	041bb72a 	ldreq	fp, [fp], #-1834
    52bc:	00b72ab1 	ldreqh	r2, [r7], r1
    52c0:	1bb2b100 	blne	0xfecb16c8
    52c4:	c24e5918 	subgt	r5, lr, #393216	; 0x60000
    52c8:	181bb21b 	ldmneda	fp, {r0, r1, r3, r4, r9, ip, sp, pc}
    52cc:	7e0490b4 	mcrvc	0, 0, r9, cr4, cr4, {5}
    52d0:	00a70436 	adceq	r0, r7, r6, lsr r4
    52d4:	181bb215 	ldmneda	fp, {r0, r2, r4, r9, ip, sp, pc}
    52d8:	20b6851c 	adccss	r8, r6, ip, lsl r5
    52dc:	1bb21b07 	blne	0xfec8bf00
    52e0:	0490b418 	ldreq	fp, [r0], #1048
    52e4:	1504367e 	strne	r3, [r4, #-1662]
    52e8:	ecff9904 	ldcl	9, cr9, [pc], #16
    52ec:	59181bb2 	ldmpldb	r8, {r1, r4, r5, r7, r8, r9, fp, ip}
    52f0:	1b0490b4 	blne	0x1295c8
    52f4:	937e8202 	cmnls	lr, #536870912	; 0x20000000
    52f8:	150490b5 	strne	r9, [r4, #-181]
    52fc:	acc32d04 	stcgel	13, cr2, [r3], {4}
    5300:	00bfc32d 	adceqs	ip, pc, sp, lsr #6
    5304:	00010000 	andeq	r0, r1, r0
    5308:	00050000 	andeq	r0, r5, r0
    530c:	000a0000 	andeq	r0, sl, r0
    5310:	00070000 	andeq	r0, r7, r0
    5314:	00020000 	andeq	r0, r2, r0
    5318:	00040000 	andeq	r0, r4, r0
    531c:	00080000 	andeq	r0, r8, r0
    5320:	00100000 	andeqs	r0, r0, r0
    5324:	00200000 	eoreq	r0, r0, r0
    5328:	00380000 	eoreqs	r0, r8, r0
    532c:	00030000 	andeq	r0, r3, r0
    5330:	00400000 	subeq	r0, r0, r0
    5334:	11060000 	tstne	r6, r0
    5338:	00001404 	andeq	r1, r0, r4, lsl #8
    533c:	08040201 	stmeqda	r4, {r0, r9}
    5340:	08040201 	stmeqda	r4, {r0, r9}
