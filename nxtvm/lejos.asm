
nxt_lejos.bin:     file format binary

Disassembly of section .data:

00000000 <.data>:
       0:	e3a0d821 	mov	sp, #2162688	; 0x210000
       4:	e92d4000 	stmdb	sp!, {lr}
       8:	eb000059 	bl	0x174
       c:	e8bd8000 	ldmia	sp!, {pc}
      10:	e59f3004 	ldr	r3, [pc, #4]	; 0x1c
      14:	e5930000 	ldr	r0, [r3]
      18:	e12fff1e 	bx	lr
      1c:	002078d0 	ldreqd	r7, [r0], -r0
      20:	e12fff1e 	bx	lr
      24:	e12fff1e 	bx	lr
      28:	e12fff1e 	bx	lr
      2c:	e1a0c00d 	mov	ip, sp
      30:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
      34:	e59f3110 	ldr	r3, [pc, #272]	; 0x14c
      38:	e59f6110 	ldr	r6, [pc, #272]	; 0x150
      3c:	e59f2110 	ldr	r2, [pc, #272]	; 0x154
      40:	e3a01001 	mov	r1, #1	; 0x1
      44:	e5c31000 	strb	r1, [r3]
      48:	e5965000 	ldr	r5, [r6]
      4c:	e5c21000 	strb	r1, [r2]
      50:	e59f3100 	ldr	r3, [pc, #256]	; 0x158
      54:	e1d500b6 	ldrh	r0, [r5, #6]
      58:	e3a04000 	mov	r4, #0	; 0x0
      5c:	e24cb004 	sub	fp, ip, #4	; 0x4
      60:	e0800005 	add	r0, r0, r5
      64:	e1d510b8 	ldrh	r1, [r5, #8]
      68:	e5834000 	str	r4, [r3]
      6c:	eb000ca1 	bl	0x32f8
      70:	e5962000 	ldr	r2, [r6]
      74:	e0843104 	add	r3, r4, r4, lsl #2
      78:	e0822083 	add	r2, r2, r3, lsl #1
      7c:	e2822010 	add	r2, r2, #16	; 0x10
      80:	e5d23009 	ldrb	r3, [r2, #9]
      84:	e3c33001 	bic	r3, r3, #1	; 0x1
      88:	e5c23009 	strb	r3, [r2, #9]
      8c:	e2841001 	add	r1, r4, #1	; 0x1
      90:	e5d5300f 	ldrb	r3, [r5, #15]
      94:	e20140ff 	and	r4, r1, #255	; 0xff
      98:	e1530004 	cmp	r3, r4
      9c:	2afffff3 	bcs	0x70
      a0:	eb000cee 	bl	0x3460
      a4:	e59f30b0 	ldr	r3, [pc, #176]	; 0x15c
      a8:	e3a02982 	mov	r2, #2129920	; 0x208000
      ac:	e2822a02 	add	r2, r2, #8192	; 0x2000
      b0:	e3a01983 	mov	r1, #2146304	; 0x20c000
      b4:	e1a00002 	mov	r0, r2
      b8:	e1a05001 	mov	r5, r1
      bc:	e5832000 	str	r2, [r3]
      c0:	eb000cee 	bl	0x3480
      c4:	e2855a03 	add	r5, r5, #12288	; 0x3000
      c8:	eb000c48 	bl	0x31f0
      cc:	e3a00001 	mov	r0, #1	; 0x1
      d0:	e5c50000 	strb	r0, [r5]
      d4:	eb000de3 	bl	0x3868
      d8:	e59f3080 	ldr	r3, [pc, #128]	; 0x160
      dc:	e59f1080 	ldr	r1, [pc, #128]	; 0x164
      e0:	e5830000 	str	r0, [r3]
      e4:	e59f207c 	ldr	r2, [pc, #124]	; 0x168
      e8:	e59f307c 	ldr	r3, [pc, #124]	; 0x16c
      ec:	e3a04000 	mov	r4, #0	; 0x0
      f0:	e5c24000 	strb	r4, [r2]
      f4:	e5834000 	str	r4, [r3]
      f8:	e5814024 	str	r4, [r1, #36]
      fc:	e5814000 	str	r4, [r1]
     100:	e5814004 	str	r4, [r1, #4]
     104:	e5814008 	str	r4, [r1, #8]
     108:	e581400c 	str	r4, [r1, #12]
     10c:	e5814010 	str	r4, [r1, #16]
     110:	e5814014 	str	r4, [r1, #20]
     114:	e5814018 	str	r4, [r1, #24]
     118:	e581401c 	str	r4, [r1, #28]
     11c:	e5814020 	str	r4, [r1, #32]
     120:	eb0009c0 	bl	0x2828
     124:	e1500004 	cmp	r0, r4
     128:	0a000004 	beq	0x140
     12c:	e59f303c 	ldr	r3, [pc, #60]	; 0x170
     130:	e3a02003 	mov	r2, #3	; 0x3
     134:	e5c34000 	strb	r4, [r3]
     138:	e5c52000 	strb	r2, [r5]
     13c:	eb0001ca 	bl	0x86c
     140:	e24bd018 	sub	sp, fp, #24	; 0x18
     144:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
     148:	e12fff1e 	bx	lr
     14c:	002079c6 	eoreq	r7, r0, r6, asr #19
     150:	002079b0 	streqh	r7, [r0], -r0
     154:	002079c7 	eoreq	r7, r0, r7, asr #19
     158:	002079c8 	eoreq	r7, r0, r8, asr #19
     15c:	0020791c 	eoreq	r7, r0, ip, lsl r9
     160:	00207920 	eoreq	r7, r0, r0, lsr #18
     164:	00207950 	eoreq	r7, r0, r0, asr r9
     168:	0020797c 	eoreq	r7, r0, ip, ror r9
     16c:	00207978 	eoreq	r7, r0, r8, ror r9
     170:	0020797d 	eoreq	r7, r0, sp, ror r9
     174:	e1a0c00d 	mov	ip, sp
     178:	e92dd810 	stmdb	sp!, {r4, fp, ip, lr, pc}
     17c:	e24cb004 	sub	fp, ip, #4	; 0x4
     180:	eb0012dc 	bl	0x4cf8
     184:	e59f102c 	ldr	r1, [pc, #44]	; 0x1b8
     188:	e59f302c 	ldr	r3, [pc, #44]	; 0x1bc
     18c:	e3a02983 	mov	r2, #2146304	; 0x20c000
     190:	e2822a03 	add	r2, r2, #12288	; 0x3000
     194:	e3a04000 	mov	r4, #0	; 0x0
     198:	e5831000 	str	r1, [r3]
     19c:	e5824004 	str	r4, [r2, #4]
     1a0:	e5c24000 	strb	r4, [r2]
     1a4:	ebffffa0 	bl	0x2c
     1a8:	e1a00004 	mov	r0, r4
     1ac:	e24bd010 	sub	sp, fp, #16	; 0x10
     1b0:	e89d6810 	ldmia	sp, {r4, fp, sp, lr}
     1b4:	e12fff1e 	bx	lr
     1b8:	00207090 	mlaeq	r0, r0, r0, r7
     1bc:	002079b0 	streqh	r7, [r0], -r0
     1c0:	e1a00800 	mov	r0, r0, lsl #16
     1c4:	e1a00820 	mov	r0, r0, lsr #16
     1c8:	e3a03983 	mov	r3, #2146304	; 0x20c000
     1cc:	e1a0c00d 	mov	ip, sp
     1d0:	e2833a03 	add	r3, r3, #12288	; 0x3000
     1d4:	e3a02063 	mov	r2, #99	; 0x63
     1d8:	e2400004 	sub	r0, r0, #4	; 0x4
     1dc:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
     1e0:	e24cb004 	sub	fp, ip, #4	; 0x4
     1e4:	e5c32000 	strb	r2, [r3]
     1e8:	e1a04001 	mov	r4, r1
     1ec:	e350002d 	cmp	r0, #45	; 0x2d
     1f0:	979ff100 	ldrls	pc, [pc, r0, lsl #2]
     1f4:	ea000101 	b	0x600
     1f8:	00202500 	eoreq	r2, r0, r0, lsl #10
     1fc:	002024f0 	streqd	r2, [r0], -r0
     200:	00202570 	eoreq	r2, r0, r0, ror r5
     204:	00202310 	eoreq	r2, r0, r0, lsl r3
     208:	00202490 	mlaeq	r0, r0, r4, r2
     20c:	0020249c 	mlaeq	r0, ip, r4, r2
     210:	002024b4 	streqh	r2, [r0], -r4
     214:	00202430 	eoreq	r2, r0, r0, lsr r4
     218:	00202450 	eoreq	r2, r0, r0, asr r4
     21c:	00202350 	eoreq	r2, r0, r0, asr r3
     220:	00202370 	eoreq	r2, r0, r0, ror r3
     224:	00202380 	eoreq	r2, r0, r0, lsl #7
     228:	002023bc 	streqh	r2, [r0], -ip
     22c:	00202320 	eoreq	r2, r0, r0, lsr #6
     230:	00202330 	eoreq	r2, r0, r0, lsr r3
     234:	00202304 	eoreq	r2, r0, r4, lsl #6
     238:	00202304 	eoreq	r2, r0, r4, lsl #6
     23c:	002023e4 	eoreq	r2, r0, r4, ror #7
     240:	00202414 	eoreq	r2, r0, r4, lsl r4
     244:	00202510 	eoreq	r2, r0, r0, lsl r5
     248:	00202540 	eoreq	r2, r0, r0, asr #10
     24c:	00202600 	eoreq	r2, r0, r0, lsl #12
     250:	002022dc 	ldreqd	r2, [r0], -ip
     254:	002022b0 	streqh	r2, [r0], -r0
     258:	002022dc 	ldreqd	r2, [r0], -ip
     25c:	002022dc 	ldreqd	r2, [r0], -ip
     260:	002022dc 	ldreqd	r2, [r0], -ip
     264:	002022e8 	eoreq	r2, r0, r8, ror #5
     268:	002022dc 	ldreqd	r2, [r0], -ip
     26c:	002022dc 	ldreqd	r2, [r0], -ip
     270:	00202580 	eoreq	r2, r0, r0, lsl #11
     274:	002022dc 	ldreqd	r2, [r0], -ip
     278:	002025a0 	eoreq	r2, r0, r0, lsr #11
     27c:	002025c8 	eoreq	r2, r0, r8, asr #11
     280:	002025f4 	streqd	r2, [r0], -r4
     284:	00202600 	eoreq	r2, r0, r0, lsl #12
     288:	00202610 	eoreq	r2, r0, r0, lsl r6
     28c:	0020262c 	eoreq	r2, r0, ip, lsr #12
     290:	00202640 	eoreq	r2, r0, r0, asr #12
     294:	00202640 	eoreq	r2, r0, r0, asr #12
     298:	002022dc 	ldreqd	r2, [r0], -ip
     29c:	002022dc 	ldreqd	r2, [r0], -ip
     2a0:	002022e8 	eoreq	r2, r0, r8, ror #5
     2a4:	002022e8 	eoreq	r2, r0, r8, ror #5
     2a8:	002022e8 	eoreq	r2, r0, r8, ror #5
     2ac:	00202470 	eoreq	r2, r0, r0, ror r4
     2b0:	e3a03d65 	mov	r3, #6464	; 0x1940
     2b4:	e5912000 	ldr	r2, [r1]
     2b8:	e2833006 	add	r3, r3, #6	; 0x6
     2bc:	e1520003 	cmp	r2, r3
     2c0:	0a0000e8 	beq	0x668
     2c4:	e3a03d67 	mov	r3, #6592	; 0x19c0
     2c8:	e2833004 	add	r3, r3, #4	; 0x4
     2cc:	e1520003 	cmp	r2, r3
     2d0:	05940004 	ldreq	r0, [r4, #4]
     2d4:	02400a01 	subeq	r0, r0, #4096	; 0x1000
     2d8:	0b001233 	bleq	0x4bac
     2dc:	e24bd014 	sub	sp, fp, #20	; 0x14
     2e0:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
     2e4:	e12fff1e 	bx	lr
     2e8:	e59f138c 	ldr	r1, [pc, #908]	; 0x67c
     2ec:	e5910000 	ldr	r0, [r1]
     2f0:	e3a02000 	mov	r2, #0	; 0x0
     2f4:	e2803004 	add	r3, r0, #4	; 0x4
     2f8:	e5813000 	str	r3, [r1]
     2fc:	e5802004 	str	r2, [r0, #4]
     300:	eafffff5 	b	0x2dc
     304:	e5910000 	ldr	r0, [r1]
     308:	eb0008d2 	bl	0x2658
     30c:	eafffff2 	b	0x2dc
     310:	e5911008 	ldr	r1, [r1, #8]
     314:	e5940000 	ldr	r0, [r4]
     318:	eb000916 	bl	0x2778
     31c:	eaffffee 	b	0x2dc
     320:	e5912004 	ldr	r2, [r1, #4]
     324:	e5913000 	ldr	r3, [r1]
     328:	e5c32022 	strb	r2, [r3, #34]
     32c:	eaffffea 	b	0x2dc
     330:	e59fc344 	ldr	ip, [pc, #836]	; 0x67c
     334:	e5912000 	ldr	r2, [r1]
     338:	e59c1000 	ldr	r1, [ip]
     33c:	e1d202d2 	ldrsb	r0, [r2, #34]
     340:	e2813004 	add	r3, r1, #4	; 0x4
     344:	e58c3000 	str	r3, [ip]
     348:	e5810004 	str	r0, [r1, #4]
     34c:	eaffffe2 	b	0x2dc
     350:	e5911004 	ldr	r1, [r1, #4]
     354:	e2413001 	sub	r3, r1, #1	; 0x1
     358:	e3530009 	cmp	r3, #9	; 0x9
     35c:	9a0000be 	bls	0x65c
     360:	e59f3318 	ldr	r3, [pc, #792]	; 0x680
     364:	e5930000 	ldr	r0, [r3]
     368:	eb000b1f 	bl	0x2fec
     36c:	eaffffda 	b	0x2dc
     370:	e5912000 	ldr	r2, [r1]
     374:	e3a03001 	mov	r3, #1	; 0x1
     378:	e5c23021 	strb	r3, [r2, #33]
     37c:	eaffffd6 	b	0x2dc
     380:	e59f32fc 	ldr	r3, [pc, #764]	; 0x684
     384:	e5931000 	ldr	r1, [r3]
     388:	e3a03000 	mov	r3, #0	; 0x0
     38c:	e1d122d1 	ldrsb	r2, [r1, #33]
     390:	e59f02e4 	ldr	r0, [pc, #740]	; 0x67c
     394:	e5c13021 	strb	r3, [r1, #33]
     398:	e1520003 	cmp	r2, r3
     39c:	e5901000 	ldr	r1, [r0]
     3a0:	01a02003 	moveq	r2, r3
     3a4:	13a02401 	movne	r2, #16777216	; 0x1000000
     3a8:	e1a02c42 	mov	r2, r2, asr #24
     3ac:	e2813004 	add	r3, r1, #4	; 0x4
     3b0:	e5803000 	str	r3, [r0]
     3b4:	e5812004 	str	r2, [r1, #4]
     3b8:	eaffffc7 	b	0x2dc
     3bc:	e5913000 	ldr	r3, [r1]
     3c0:	e59f02b4 	ldr	r0, [pc, #692]	; 0x67c
     3c4:	e1d322d1 	ldrsb	r2, [r3, #33]
     3c8:	e5901000 	ldr	r1, [r0]
     3cc:	e2522000 	subs	r2, r2, #0	; 0x0
     3d0:	13a02001 	movne	r2, #1	; 0x1
     3d4:	e2813004 	add	r3, r1, #4	; 0x4
     3d8:	e5803000 	str	r3, [r0]
     3dc:	e5812004 	str	r2, [r1, #4]
     3e0:	eaffffbd 	b	0x2dc
     3e4:	e59f4290 	ldr	r4, [pc, #656]	; 0x67c
     3e8:	e5943000 	ldr	r3, [r4]
     3ec:	e3a02000 	mov	r2, #0	; 0x0
     3f0:	e5832004 	str	r2, [r3, #4]
     3f4:	e2833004 	add	r3, r3, #4	; 0x4
     3f8:	e5843000 	str	r3, [r4]
     3fc:	ebffff03 	bl	0x10
     400:	e5942000 	ldr	r2, [r4]
     404:	e2823004 	add	r3, r2, #4	; 0x4
     408:	e5843000 	str	r3, [r4]
     40c:	e5820004 	str	r0, [r2, #4]
     410:	eaffffb1 	b	0x2dc
     414:	e59f326c 	ldr	r3, [pc, #620]	; 0x688
     418:	e3a02001 	mov	r2, #1	; 0x1
     41c:	e5c32000 	strb	r2, [r3]
     420:	e59f3264 	ldr	r3, [pc, #612]	; 0x68c
     424:	e3a01002 	mov	r1, #2	; 0x2
     428:	e5c31000 	strb	r1, [r3]
     42c:	eaffffaa 	b	0x2dc
     430:	e59f0244 	ldr	r0, [pc, #580]	; 0x67c
     434:	e59f3248 	ldr	r3, [pc, #584]	; 0x684
     438:	e590c000 	ldr	ip, [r0]
     43c:	e5931000 	ldr	r1, [r3]
     440:	e28c2004 	add	r2, ip, #4	; 0x4
     444:	e5802000 	str	r2, [r0]
     448:	e58c1004 	str	r1, [ip, #4]
     44c:	eaffffa2 	b	0x2dc
     450:	e59fc224 	ldr	ip, [pc, #548]	; 0x67c
     454:	e5912000 	ldr	r2, [r1]
     458:	e59c1000 	ldr	r1, [ip]
     45c:	e1d202d0 	ldrsb	r0, [r2, #32]
     460:	e2813004 	add	r3, r1, #4	; 0x4
     464:	e58c3000 	str	r3, [ip]
     468:	e5810004 	str	r0, [r1, #4]
     46c:	eaffff9a 	b	0x2dc
     470:	e59f0204 	ldr	r0, [pc, #516]	; 0x67c
     474:	e59f3214 	ldr	r3, [pc, #532]	; 0x690
     478:	e590c000 	ldr	ip, [r0]
     47c:	e5931000 	ldr	r1, [r3]
     480:	e28c2004 	add	r2, ip, #4	; 0x4
     484:	e5802000 	str	r2, [r0]
     488:	e58c1004 	str	r1, [ip, #4]
     48c:	eaffff92 	b	0x2dc
     490:	e5910000 	ldr	r0, [r1]
     494:	eb0008e3 	bl	0x2828
     498:	eaffff8f 	b	0x2dc
     49c:	e59f21e8 	ldr	r2, [pc, #488]	; 0x68c
     4a0:	e59f11e0 	ldr	r1, [pc, #480]	; 0x688
     4a4:	e3a03001 	mov	r3, #1	; 0x1
     4a8:	e5c23000 	strb	r3, [r2]
     4ac:	e5c13000 	strb	r3, [r1]
     4b0:	eaffff89 	b	0x2dc
     4b4:	e59f11c8 	ldr	r1, [pc, #456]	; 0x684
     4b8:	e5912000 	ldr	r2, [r1]
     4bc:	e3a03006 	mov	r3, #6	; 0x6
     4c0:	e5944004 	ldr	r4, [r4, #4]
     4c4:	e5c2301f 	strb	r3, [r2, #31]
     4c8:	e5915000 	ldr	r5, [r1]
     4cc:	ebfffecf 	bl	0x10
     4d0:	e59f31b4 	ldr	r3, [pc, #436]	; 0x68c
     4d4:	e59f21ac 	ldr	r2, [pc, #428]	; 0x688
     4d8:	e3a01001 	mov	r1, #1	; 0x1
     4dc:	e0844000 	add	r4, r4, r0
     4e0:	e5854010 	str	r4, [r5, #16]
     4e4:	e5c31000 	strb	r1, [r3]
     4e8:	e5c21000 	strb	r1, [r2]
     4ec:	eaffff7a 	b	0x2dc
     4f0:	e5910000 	ldr	r0, [r1]
     4f4:	e3a01001 	mov	r1, #1	; 0x1
     4f8:	eb000938 	bl	0x29e0
     4fc:	eaffff76 	b	0x2dc
     500:	e5910000 	ldr	r0, [r1]
     504:	e3a01000 	mov	r1, #0	; 0x0
     508:	eb000934 	bl	0x29e0
     50c:	eaffff72 	b	0x2dc
     510:	e59f4164 	ldr	r4, [pc, #356]	; 0x67c
     514:	e5943000 	ldr	r3, [r4]
     518:	e3a02000 	mov	r2, #0	; 0x0
     51c:	e5832004 	str	r2, [r3, #4]
     520:	e2833004 	add	r3, r3, #4	; 0x4
     524:	e5843000 	str	r3, [r4]
     528:	eb000bfb 	bl	0x351c
     52c:	e5942000 	ldr	r2, [r4]
     530:	e2823004 	add	r3, r2, #4	; 0x4
     534:	e5843000 	str	r3, [r4]
     538:	e5820004 	str	r0, [r2, #4]
     53c:	eaffff66 	b	0x2dc
     540:	e59f4134 	ldr	r4, [pc, #308]	; 0x67c
     544:	e5943000 	ldr	r3, [r4]
     548:	e3a02000 	mov	r2, #0	; 0x0
     54c:	e5832004 	str	r2, [r3, #4]
     550:	e2833004 	add	r3, r3, #4	; 0x4
     554:	e5843000 	str	r3, [r4]
     558:	eb000bea 	bl	0x3508
     55c:	e5942000 	ldr	r2, [r4]
     560:	e2823004 	add	r3, r2, #4	; 0x4
     564:	e5843000 	str	r3, [r4]
     568:	e5820004 	str	r0, [r2, #4]
     56c:	eaffff5a 	b	0x2dc
     570:	e5910000 	ldr	r0, [r1]
     574:	e3a01000 	mov	r1, #0	; 0x0
     578:	eb00087e 	bl	0x2778
     57c:	eaffff56 	b	0x2dc
     580:	e59f00f4 	ldr	r0, [pc, #244]	; 0x67c
     584:	e5912000 	ldr	r2, [r1]
     588:	e5901000 	ldr	r1, [r0]
     58c:	e2822008 	add	r2, r2, #8	; 0x8
     590:	e2813004 	add	r3, r1, #4	; 0x4
     594:	e5803000 	str	r3, [r0]
     598:	e5812004 	str	r2, [r1, #4]
     59c:	eaffff4e 	b	0x2dc
     5a0:	e5912000 	ldr	r2, [r1]
     5a4:	e59fc0d0 	ldr	ip, [pc, #208]	; 0x67c
     5a8:	e59f30e4 	ldr	r3, [pc, #228]	; 0x694
     5ac:	e59c1000 	ldr	r1, [ip]
     5b0:	e0833182 	add	r3, r3, r2, lsl #3
     5b4:	e1d300f4 	ldrsh	r0, [r3, #4]
     5b8:	e2812004 	add	r2, r1, #4	; 0x4
     5bc:	e58c2000 	str	r2, [ip]
     5c0:	e5810004 	str	r0, [r1, #4]
     5c4:	eaffff44 	b	0x2dc
     5c8:	e5d13008 	ldrb	r3, [r1, #8]
     5cc:	e3530002 	cmp	r3, #2	; 0x2
     5d0:	0a000001 	beq	0x5dc
     5d4:	e3530003 	cmp	r3, #3	; 0x3
     5d8:	1affff3f 	bne	0x2dc
     5dc:	e5942000 	ldr	r2, [r4]
     5e0:	e59f30ac 	ldr	r3, [pc, #172]	; 0x694
     5e4:	e1d440b4 	ldrh	r4, [r4, #4]
     5e8:	e0833182 	add	r3, r3, r2, lsl #3
     5ec:	e1c340b4 	strh	r4, [r3, #4]
     5f0:	eaffff39 	b	0x2dc
     5f4:	e5910000 	ldr	r0, [r1]
     5f8:	eb0011ca 	bl	0x4d28
     5fc:	eaffff36 	b	0x2dc
     600:	e59f3090 	ldr	r3, [pc, #144]	; 0x698
     604:	e5930000 	ldr	r0, [r3]
     608:	eb000a77 	bl	0x2fec
     60c:	eaffff32 	b	0x2dc
     610:	e5913004 	ldr	r3, [r1, #4]
     614:	e3530000 	cmp	r3, #0	; 0x0
     618:	1affff2f 	bne	0x2dc
     61c:	e59f3078 	ldr	r3, [pc, #120]	; 0x69c
     620:	e5930000 	ldr	r0, [r3]
     624:	eb000a70 	bl	0x2fec
     628:	eaffff2b 	b	0x2dc
     62c:	e5912008 	ldr	r2, [r1, #8]
     630:	e5913004 	ldr	r3, [r1, #4]
     634:	e1530002 	cmp	r3, r2
     638:	1afffff7 	bne	0x61c
     63c:	eaffff26 	b	0x2dc
     640:	e59f2034 	ldr	r2, [pc, #52]	; 0x67c
     644:	e5921000 	ldr	r1, [r2]
     648:	e5940000 	ldr	r0, [r4]
     64c:	e2813004 	add	r3, r1, #4	; 0x4
     650:	e5823000 	str	r3, [r2]
     654:	e5810004 	str	r0, [r1, #4]
     658:	eaffff1f 	b	0x2dc
     65c:	e5940000 	ldr	r0, [r4]
     660:	eb0008b4 	bl	0x2938
     664:	eaffff1c 	b	0x2dc
     668:	e5910004 	ldr	r0, [r1, #4]
     66c:	e2400a01 	sub	r0, r0, #4096	; 0x1000
     670:	eb001136 	bl	0x4b50
     674:	e5942000 	ldr	r2, [r4]
     678:	eaffff11 	b	0x2c4
     67c:	00207928 	eoreq	r7, r0, r8, lsr #18
     680:	00207994 	mlaeq	r0, r4, r9, r7
     684:	00207978 	eoreq	r7, r0, r8, ror r9
     688:	0020792f 	eoreq	r7, r0, pc, lsr #18
     68c:	0020792e 	eoreq	r7, r0, lr, lsr #18
     690:	0020791c 	eoreq	r7, r0, ip, lsl r9
     694:	00207900 	eoreq	r7, r0, r0, lsl #18
     698:	00207980 	eoreq	r7, r0, r0, lsl #19
     69c:	002079a0 	eoreq	r7, r0, r0, lsr #19
     6a0:	e59fc034 	ldr	ip, [pc, #52]	; 0x6dc
     6a4:	e31000ff 	tst	r0, #255	; 0xff
     6a8:	059c3000 	ldreq	r3, [ip]
     6ac:	02833002 	addeq	r3, r3, #2	; 0x2
     6b0:	058c3000 	streq	r3, [ip]
     6b4:	012fff1e 	bxeq	lr
     6b8:	e59c3000 	ldr	r3, [ip]
     6bc:	e5d31000 	ldrb	r1, [r3]
     6c0:	e5d32001 	ldrb	r2, [r3, #1]
     6c4:	e1822401 	orr	r2, r2, r1, lsl #8
     6c8:	e1a02802 	mov	r2, r2, lsl #16
     6cc:	e0833842 	add	r3, r3, r2, asr #16
     6d0:	e2433001 	sub	r3, r3, #1	; 0x1
     6d4:	e58c3000 	str	r3, [ip]
     6d8:	e12fff1e 	bx	lr
     6dc:	00207944 	eoreq	r7, r0, r4, asr #18
     6e0:	e59fc01c 	ldr	ip, [pc, #28]	; 0x704
     6e4:	e59c1000 	ldr	r1, [ip]
     6e8:	e1a02001 	mov	r2, r1
     6ec:	e4120004 	ldr	r0, [r2], #-4
     6f0:	e5113004 	ldr	r3, [r1, #-4]
     6f4:	e0603003 	rsb	r3, r0, r3
     6f8:	e58c2000 	str	r2, [ip]
     6fc:	e5013004 	str	r3, [r1, #-4]
     700:	e12fff1e 	bx	lr
     704:	00207928 	eoreq	r7, r0, r8, lsr #18
     708:	e59f0090 	ldr	r0, [pc, #144]	; 0x7a0
     70c:	e5903000 	ldr	r3, [r0]
     710:	e1a02003 	mov	r2, r3
     714:	e1a0c00d 	mov	ip, sp
     718:	e4121004 	ldr	r1, [r2], #-4
     71c:	e92dd810 	stmdb	sp!, {r4, fp, ip, lr, pc}
     720:	e5134004 	ldr	r4, [r3, #-4]
     724:	e1a01801 	mov	r1, r1, lsl #16
     728:	e5802000 	str	r2, [r0]
     72c:	e59f3070 	ldr	r3, [pc, #112]	; 0x7a4
     730:	e59f2070 	ldr	r2, [pc, #112]	; 0x7a8
     734:	e1a01821 	mov	r1, r1, lsr #16
     738:	e3540000 	cmp	r4, #0	; 0x0
     73c:	e24cb004 	sub	fp, ip, #4	; 0x4
     740:	e1c310b0 	strh	r1, [r3]
     744:	e1a0c801 	mov	ip, r1, lsl #16
     748:	e5824000 	str	r4, [r2]
     74c:	0a00000e 	beq	0x78c
     750:	e1b0284c 	movs	r2, ip, asr #16
     754:	4a000005 	bmi	0x770
     758:	e1d430b0 	ldrh	r3, [r4]
     75c:	e1a03b83 	mov	r3, r3, lsl #23
     760:	e1a03ba3 	mov	r3, r3, lsr #23
     764:	e1520003 	cmp	r2, r3
     768:	e3a00001 	mov	r0, #1	; 0x1
     76c:	ba000003 	blt	0x780
     770:	e59f3034 	ldr	r3, [pc, #52]	; 0x7ac
     774:	e5930000 	ldr	r0, [r3]
     778:	eb000a1b 	bl	0x2fec
     77c:	e3a00000 	mov	r0, #0	; 0x0
     780:	e24bd010 	sub	sp, fp, #16	; 0x10
     784:	e89d6810 	ldmia	sp, {r4, fp, sp, lr}
     788:	e12fff1e 	bx	lr
     78c:	e59f301c 	ldr	r3, [pc, #28]	; 0x7b0
     790:	e5930000 	ldr	r0, [r3]
     794:	eb000a14 	bl	0x2fec
     798:	e1a00004 	mov	r0, r4
     79c:	eafffff7 	b	0x780
     7a0:	00207928 	eoreq	r7, r0, r8, lsr #18
     7a4:	0020792c 	eoreq	r7, r0, ip, lsr #18
     7a8:	00207930 	eoreq	r7, r0, r0, lsr r9
     7ac:	0020799c 	mlaeq	r0, ip, r9, r7
     7b0:	0020798c 	eoreq	r7, r0, ip, lsl #19
     7b4:	e1a0c00d 	mov	ip, sp
     7b8:	e92dd810 	stmdb	sp!, {r4, fp, ip, lr, pc}
     7bc:	e59fe094 	ldr	lr, [pc, #148]	; 0x858
     7c0:	e59e2000 	ldr	r2, [lr]
     7c4:	e24cb004 	sub	fp, ip, #4	; 0x4
     7c8:	e1a0c002 	mov	ip, r2
     7cc:	e41c3004 	ldr	r3, [ip], #-4
     7d0:	e5124004 	ldr	r4, [r2, #-4]
     7d4:	e1a03803 	mov	r3, r3, lsl #16
     7d8:	e59f207c 	ldr	r2, [pc, #124]	; 0x85c
     7dc:	e59f107c 	ldr	r1, [pc, #124]	; 0x860
     7e0:	e1a03823 	mov	r3, r3, lsr #16
     7e4:	e3540000 	cmp	r4, #0	; 0x0
     7e8:	e1a00803 	mov	r0, r3, lsl #16
     7ec:	e1c230b0 	strh	r3, [r2]
     7f0:	e5814000 	str	r4, [r1]
     7f4:	e58ec000 	str	ip, [lr]
     7f8:	0a000011 	beq	0x844
     7fc:	e1b01840 	movs	r1, r0, asr #16
     800:	4a00000a 	bmi	0x830
     804:	e1d430b0 	ldrh	r3, [r4]
     808:	e1a03b83 	mov	r3, r3, lsl #23
     80c:	e1a03ba3 	mov	r3, r3, lsr #23
     810:	e24c2004 	sub	r2, ip, #4	; 0x4
     814:	e1510003 	cmp	r1, r3
     818:	e3a00001 	mov	r0, #1	; 0x1
     81c:	b58e2000 	strlt	r2, [lr]
     820:	aa000002 	bge	0x830
     824:	e24bd010 	sub	sp, fp, #16	; 0x10
     828:	e89d6810 	ldmia	sp, {r4, fp, sp, lr}
     82c:	e12fff1e 	bx	lr
     830:	e59f302c 	ldr	r3, [pc, #44]	; 0x864
     834:	e5930000 	ldr	r0, [r3]
     838:	eb0009eb 	bl	0x2fec
     83c:	e3a00000 	mov	r0, #0	; 0x0
     840:	eafffff7 	b	0x824
     844:	e59f301c 	ldr	r3, [pc, #28]	; 0x868
     848:	e5930000 	ldr	r0, [r3]
     84c:	eb0009e6 	bl	0x2fec
     850:	e1a00004 	mov	r0, r4
     854:	eafffff2 	b	0x824
     858:	00207928 	eoreq	r7, r0, r8, lsr #18
     85c:	0020792c 	eoreq	r7, r0, ip, lsr #18
     860:	00207930 	eoreq	r7, r0, r0, lsr r9
     864:	0020799c 	mlaeq	r0, ip, r9, r7
     868:	0020798c 	eoreq	r7, r0, ip, lsl #19
     86c:	e1a0c00d 	mov	ip, sp
     870:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
     874:	e59fafa8 	ldr	sl, [pc, #4008]	; 0x1824
     878:	e59f8fa8 	ldr	r8, [pc, #4008]	; 0x1828
     87c:	e24cb004 	sub	fp, ip, #4	; 0x4
     880:	e3a03001 	mov	r3, #1	; 0x1
     884:	e24dd008 	sub	sp, sp, #8	; 0x8
     888:	e3a00000 	mov	r0, #0	; 0x0
     88c:	e5c83000 	strb	r3, [r8]
     890:	e5ca3000 	strb	r3, [sl]
     894:	e50b0030 	str	r0, [fp, #-48]
     898:	e3a07014 	mov	r7, #20	; 0x14
     89c:	e3a03001 	mov	r3, #1	; 0x1
     8a0:	e5ca3000 	strb	r3, [sl]
     8a4:	e5da3000 	ldrb	r3, [sl]
     8a8:	e3530000 	cmp	r3, #0	; 0x0
     8ac:	e59f5f70 	ldr	r5, [pc, #3952]	; 0x1824
     8b0:	0a00001f 	beq	0x934
     8b4:	e3a03000 	mov	r3, #0	; 0x0
     8b8:	e5c53000 	strb	r3, [r5]
     8bc:	e5d84000 	ldrb	r4, [r8]
     8c0:	e5c83000 	strb	r3, [r8]
     8c4:	ebfffdd1 	bl	0x10
     8c8:	e59f2f5c 	ldr	r2, [pc, #3932]	; 0x182c
     8cc:	e5923000 	ldr	r3, [r2]
     8d0:	e2833003 	add	r3, r3, #3	; 0x3
     8d4:	e1500003 	cmp	r0, r3
     8d8:	e59f6f48 	ldr	r6, [pc, #3912]	; 0x1828
     8dc:	aa0000ed 	bge	0xc98
     8e0:	e3540002 	cmp	r4, #2	; 0x2
     8e4:	0a0000f0 	beq	0xcac
     8e8:	e3540000 	cmp	r4, #0	; 0x0
     8ec:	02473001 	subeq	r3, r7, #1	; 0x1
     8f0:	020370ff 	andeq	r7, r3, #255	; 0xff
     8f4:	e3570000 	cmp	r7, #0	; 0x0
     8f8:	13540001 	cmpne	r4, #1	; 0x1
     8fc:	0a0000ed 	beq	0xcb8
     900:	e59f3f38 	ldr	r3, [pc, #3896]	; 0x1840
     904:	e5932000 	ldr	r2, [r3]
     908:	e3520000 	cmp	r2, #0	; 0x0
     90c:	1affffe4 	bne	0x8a4
     910:	e5d63000 	ldrb	r3, [r6]
     914:	e3530000 	cmp	r3, #0	; 0x0
     918:	02833001 	addeq	r3, r3, #1	; 0x1
     91c:	05c53000 	streqb	r3, [r5]
     920:	05c63000 	streqb	r3, [r6]
     924:	e5da3000 	ldrb	r3, [sl]
     928:	e3530000 	cmp	r3, #0	; 0x0
     92c:	e59f5ef0 	ldr	r5, [pc, #3824]	; 0x1824
     930:	1affffdf 	bne	0x8b4
     934:	e59f5ef4 	ldr	r5, [pc, #3828]	; 0x1830
     938:	e51b2030 	ldr	r2, [fp, #-48]
     93c:	e5951000 	ldr	r1, [r5]
     940:	e2833983 	add	r3, r3, #2146304	; 0x20c000
     944:	e2833a03 	add	r3, r3, #12288	; 0x3000
     948:	e2822001 	add	r2, r2, #1	; 0x1
     94c:	e50b2030 	str	r2, [fp, #-48]
     950:	e5831004 	str	r1, [r3, #4]
     954:	e5d10000 	ldrb	r0, [r1]
     958:	e35200c9 	cmp	r2, #201	; 0xc9
     95c:	e5c30000 	strb	r0, [r3]
     960:	0a0000d1 	beq	0xcac
     964:	e2814001 	add	r4, r1, #1	; 0x1
     968:	e5854000 	str	r4, [r5]
     96c:	e35000c7 	cmp	r0, #199	; 0xc7
     970:	979ff100 	ldrls	pc, [pc, r0, lsl #2]
     974:	ea00038a 	b	0x17a4
     978:	0020289c 	mlaeq	r0, ip, r8, r2
     97c:	00203588 	eoreq	r3, r0, r8, lsl #11
     980:	002035a4 	eoreq	r3, r0, r4, lsr #11
     984:	002035a4 	eoreq	r3, r0, r4, lsr #11
     988:	002035a4 	eoreq	r3, r0, r4, lsr #11
     98c:	002035a4 	eoreq	r3, r0, r4, lsr #11
     990:	002035a4 	eoreq	r3, r0, r4, lsr #11
     994:	002035a4 	eoreq	r3, r0, r4, lsr #11
     998:	002035a4 	eoreq	r3, r0, r4, lsr #11
     99c:	002035c4 	eoreq	r3, r0, r4, asr #11
     9a0:	002035c4 	eoreq	r3, r0, r4, asr #11
     9a4:	00202ce0 	eoreq	r2, r0, r0, ror #25
     9a8:	002037a4 	eoreq	r3, r0, r4, lsr #15
     9ac:	002037a4 	eoreq	r3, r0, r4, lsr #15
     9b0:	00202cc8 	eoreq	r2, r0, r8, asr #25
     9b4:	002037a4 	eoreq	r3, r0, r4, lsr #15
     9b8:	00203e94 	mlaeq	r0, r4, lr, r3
     9bc:	00203eb4 	streqh	r3, [r0], -r4
     9c0:	002033f4 	streqd	r3, [r0], -r4
     9c4:	002037a4 	eoreq	r3, r0, r4, lsr #15
     9c8:	00203cf4 	streqd	r3, [r0], -r4
     9cc:	00203c74 	eoreq	r3, r0, r4, ror ip
     9d0:	00203ca4 	eoreq	r3, r0, r4, lsr #25
     9d4:	00203c74 	eoreq	r3, r0, r4, ror ip
     9d8:	00203ca4 	eoreq	r3, r0, r4, lsr #25
     9dc:	00203b54 	eoreq	r3, r0, r4, asr fp
     9e0:	00204030 	eoreq	r4, r0, r0, lsr r0
     9e4:	00204030 	eoreq	r4, r0, r0, lsr r0
     9e8:	00204030 	eoreq	r4, r0, r0, lsr r0
     9ec:	00204030 	eoreq	r4, r0, r0, lsr r0
     9f0:	0020405c 	eoreq	r4, r0, ip, asr r0
     9f4:	0020405c 	eoreq	r4, r0, ip, asr r0
     9f8:	0020405c 	eoreq	r4, r0, ip, asr r0
     9fc:	0020405c 	eoreq	r4, r0, ip, asr r0
     a00:	00204070 	eoreq	r4, r0, r0, ror r0
     a04:	00204070 	eoreq	r4, r0, r0, ror r0
     a08:	00204070 	eoreq	r4, r0, r0, ror r0
     a0c:	00204070 	eoreq	r4, r0, r0, ror r0
     a10:	002037b4 	streqh	r3, [r0], -r4
     a14:	002037b4 	streqh	r3, [r0], -r4
     a18:	002037b4 	streqh	r3, [r0], -r4
     a1c:	002037b4 	streqh	r3, [r0], -r4
     a20:	00203634 	eoreq	r3, r0, r4, lsr r6
     a24:	00203634 	eoreq	r3, r0, r4, lsr r6
     a28:	00203634 	eoreq	r3, r0, r4, lsr r6
     a2c:	00203634 	eoreq	r3, r0, r4, lsr r6
     a30:	00203660 	eoreq	r3, r0, r0, ror #12
     a34:	002036d0 	ldreqd	r3, [r0], -r0
     a38:	00203660 	eoreq	r3, r0, r0, ror #12
     a3c:	002036d0 	ldreqd	r3, [r0], -r0
     a40:	00203734 	eoreq	r3, r0, r4, lsr r7
     a44:	00203b84 	eoreq	r3, r0, r4, lsl #23
     a48:	00203be8 	eoreq	r3, r0, r8, ror #23
     a4c:	00203be8 	eoreq	r3, r0, r8, ror #23
     a50:	00203c4c 	eoreq	r3, r0, ip, asr #24
     a54:	00203978 	eoreq	r3, r0, r8, ror r9
     a58:	00203c4c 	eoreq	r3, r0, ip, asr #24
     a5c:	00203978 	eoreq	r3, r0, r8, ror r9
     a60:	002039cc 	eoreq	r3, r0, ip, asr #19
     a64:	002039f4 	streqd	r3, [r0], -r4
     a68:	002039f4 	streqd	r3, [r0], -r4
     a6c:	002039f4 	streqd	r3, [r0], -r4
     a70:	002039f4 	streqd	r3, [r0], -r4
     a74:	00203a20 	eoreq	r3, r0, r0, lsr #20
     a78:	00203a20 	eoreq	r3, r0, r0, lsr #20
     a7c:	00203a20 	eoreq	r3, r0, r0, lsr #20
     a80:	00203a20 	eoreq	r3, r0, r0, lsr #20
     a84:	00203a74 	eoreq	r3, r0, r4, ror sl
     a88:	00203a74 	eoreq	r3, r0, r4, ror sl
     a8c:	00203a74 	eoreq	r3, r0, r4, ror sl
     a90:	00203a74 	eoreq	r3, r0, r4, ror sl
     a94:	00203aa0 	eoreq	r3, r0, r0, lsr #21
     a98:	00203aa0 	eoreq	r3, r0, r0, lsr #21
     a9c:	00203aa0 	eoreq	r3, r0, r0, lsr #21
     aa0:	00203aa0 	eoreq	r3, r0, r0, lsr #21
     aa4:	00203ab4 	streqh	r3, [r0], -r4
     aa8:	00203ab4 	streqh	r3, [r0], -r4
     aac:	00203ab4 	streqh	r3, [r0], -r4
     ab0:	00203ab4 	streqh	r3, [r0], -r4
     ab4:	00203ae0 	eoreq	r3, r0, r0, ror #21
     ab8:	00203fb8 	streqh	r3, [r0], -r8
     abc:	00203ae0 	eoreq	r3, r0, r0, ror #21
     ac0:	00203fb8 	streqh	r3, [r0], -r8
     ac4:	00203e74 	eoreq	r3, r0, r4, ror lr
     ac8:	00203dcc 	eoreq	r3, r0, ip, asr #27
     acc:	00203870 	eoreq	r3, r0, r0, ror r8
     ad0:	00203870 	eoreq	r3, r0, r0, ror r8
     ad4:	00202d0c 	eoreq	r2, r0, ip, lsl #26
     ad8:	00202cfc 	streqd	r2, [r0], -ip
     adc:	002034d4 	ldreqd	r3, [r0], -r4
     ae0:	002034f0 	streqd	r3, [r0], -r0
     ae4:	00203e40 	eoreq	r3, r0, r0, asr #28
     ae8:	002038e4 	eoreq	r3, r0, r4, ror #17
     aec:	00203904 	eoreq	r3, r0, r4, lsl #18
     af0:	00203940 	eoreq	r3, r0, r0, asr #18
     af4:	0020305c 	eoreq	r3, r0, ip, asr r0
     af8:	00202d34 	eoreq	r2, r0, r4, lsr sp
     afc:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b00:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b04:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b08:	00202d20 	eoreq	r2, r0, r0, lsr #26
     b0c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b10:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b14:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b18:	00203eec 	eoreq	r3, r0, ip, ror #29
     b1c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b20:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b24:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b28:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b2c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b30:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b34:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b38:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b3c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b40:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b44:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b48:	00203fa0 	eoreq	r3, r0, r0, lsr #31
     b4c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b50:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b54:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b58:	002041b8 	streqh	r4, [r0], -r8
     b5c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b60:	00204154 	eoreq	r4, r0, r4, asr r1
     b64:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b68:	00204184 	eoreq	r4, r0, r4, lsl #3
     b6c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b70:	002040a0 	eoreq	r4, r0, r0, lsr #1
     b74:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b78:	002040cc 	eoreq	r4, r0, ip, asr #1
     b7c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b80:	002040fc 	streqd	r4, [r0], -ip
     b84:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b88:	00204128 	eoreq	r4, r0, r8, lsr #2
     b8c:	00203600 	eoreq	r3, r0, r0, lsl #12
     b90:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b94:	002037a4 	eoreq	r3, r0, r4, lsr #15
     b98:	0020301c 	eoreq	r3, r0, ip, lsl r0
     b9c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     ba0:	002037a4 	eoreq	r3, r0, r4, lsr #15
     ba4:	002037a4 	eoreq	r3, r0, r4, lsr #15
     ba8:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bac:	00203da4 	eoreq	r3, r0, r4, lsr #27
     bb0:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bb4:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bb8:	0020301c 	eoreq	r3, r0, ip, lsl r0
     bbc:	00203040 	eoreq	r3, r0, r0, asr #32
     bc0:	00203084 	eoreq	r3, r0, r4, lsl #1
     bc4:	00203084 	eoreq	r3, r0, r4, lsl #1
     bc8:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bcc:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bd0:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bd4:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bd8:	002037a4 	eoreq	r3, r0, r4, lsr #15
     bdc:	00202f68 	eoreq	r2, r0, r8, ror #30
     be0:	00202fd4 	ldreqd	r2, [r0], -r4
     be4:	00202d80 	eoreq	r2, r0, r0, lsl #27
     be8:	00202de8 	eoreq	r2, r0, r8, ror #27
     bec:	00202e54 	eoreq	r2, r0, r4, asr lr
     bf0:	00202ec0 	eoreq	r2, r0, r0, asr #29
     bf4:	00202f44 	eoreq	r2, r0, r4, asr #30
     bf8:	00202fb0 	streqh	r2, [r0], -r0
     bfc:	00202d60 	eoreq	r2, r0, r0, ror #26
     c00:	00202dc8 	eoreq	r2, r0, r8, asr #27
     c04:	00202e30 	eoreq	r2, r0, r0, lsr lr
     c08:	00202e9c 	mlaeq	r0, ip, lr, r2
     c0c:	00202f44 	eoreq	r2, r0, r4, asr #30
     c10:	00202fb0 	streqh	r2, [r0], -r0
     c14:	00202f20 	eoreq	r2, r0, r0, lsr #30
     c18:	00202f08 	eoreq	r2, r0, r8, lsl #30
     c1c:	002030a0 	eoreq	r3, r0, r0, lsr #1
     c20:	002037a4 	eoreq	r3, r0, r4, lsr #15
     c24:	002037a4 	eoreq	r3, r0, r4, lsr #15
     c28:	002030b8 	streqh	r3, [r0], -r8
     c2c:	002030b8 	streqh	r3, [r0], -r8
     c30:	002030b8 	streqh	r3, [r0], -r8
     c34:	002030b8 	streqh	r3, [r0], -r8
     c38:	002030b8 	streqh	r3, [r0], -r8
     c3c:	002030e0 	eoreq	r3, r0, r0, ror #1
     c40:	002030ec 	eoreq	r3, r0, ip, ror #1
     c44:	002030ec 	eoreq	r3, r0, ip, ror #1
     c48:	002031c4 	eoreq	r3, r0, r4, asr #3
     c4c:	00203248 	eoreq	r3, r0, r8, asr #4
     c50:	002032ec 	eoreq	r3, r0, ip, ror #5
     c54:	0020331c 	eoreq	r3, r0, ip, lsl r3
     c58:	0020331c 	eoreq	r3, r0, ip, lsl r3
     c5c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     c60:	002037a4 	eoreq	r3, r0, r4, lsr #15
     c64:	00203334 	eoreq	r3, r0, r4, lsr r3
     c68:	00203374 	eoreq	r3, r0, r4, ror r3
     c6c:	002037a4 	eoreq	r3, r0, r4, lsr #15
     c70:	0020339c 	mlaeq	r0, ip, r3, r3
     c74:	002033c4 	eoreq	r3, r0, r4, asr #7
     c78:	00203f6c 	eoreq	r3, r0, ip, ror #30
     c7c:	00203f1c 	eoreq	r3, r0, ip, lsl pc
     c80:	00203f4c 	eoreq	r3, r0, ip, asr #30
     c84:	00203804 	eoreq	r3, r0, r4, lsl #16
     c88:	002037a4 	eoreq	r3, r0, r4, lsr #15
     c8c:	00203520 	eoreq	r3, r0, r0, lsr #10
     c90:	00202f68 	eoreq	r2, r0, r8, ror #30
     c94:	00202fd4 	ldreqd	r2, [r0], -r4
     c98:	e5820000 	str	r0, [r2]
     c9c:	eb000fd9 	bl	0x4c08
     ca0:	eb001032 	bl	0x4d70
     ca4:	e3540002 	cmp	r4, #2	; 0x2
     ca8:	1affff0e 	bne	0x8e8
     cac:	e24bd028 	sub	sp, fp, #40	; 0x28
     cb0:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
     cb4:	e12fff1e 	bx	lr
     cb8:	eb00078d 	bl	0x2af4
     cbc:	ebfffcd8 	bl	0x24
     cc0:	e3a07014 	mov	r7, #20	; 0x14
     cc4:	eaffff0d 	b	0x900
     cc8:	e59feb9c 	ldr	lr, [pc, #2972]	; 0x186c
     ccc:	e59e3000 	ldr	r3, [lr]
     cd0:	e3a02000 	mov	r2, #0	; 0x0
     cd4:	e2831004 	add	r1, r3, #4	; 0x4
     cd8:	e5832004 	str	r2, [r3, #4]
     cdc:	e58e1000 	str	r1, [lr]
     ce0:	e59f0b84 	ldr	r0, [pc, #2948]	; 0x186c
     ce4:	e5903000 	ldr	r3, [r0]
     ce8:	e3a01000 	mov	r1, #0	; 0x0
     cec:	e2832004 	add	r2, r3, #4	; 0x4
     cf0:	e5802000 	str	r2, [r0]
     cf4:	e5831004 	str	r1, [r3, #4]
     cf8:	eafffee7 	b	0x89c
     cfc:	e59f1b68 	ldr	r1, [pc, #2920]	; 0x186c
     d00:	e5913000 	ldr	r3, [r1]
     d04:	e2433004 	sub	r3, r3, #4	; 0x4
     d08:	e5813000 	str	r3, [r1]
     d0c:	e59f2b58 	ldr	r2, [pc, #2904]	; 0x186c
     d10:	e5923000 	ldr	r3, [r2]
     d14:	e2433004 	sub	r3, r3, #4	; 0x4
     d18:	e5823000 	str	r3, [r2]
     d1c:	eafffede 	b	0x89c
     d20:	e59fcb44 	ldr	ip, [pc, #2884]	; 0x186c
     d24:	e59c2000 	ldr	r2, [ip]
     d28:	e5923000 	ldr	r3, [r2]
     d2c:	e2633000 	rsb	r3, r3, #0	; 0x0
     d30:	e5823000 	str	r3, [r2]
     d34:	e59feb30 	ldr	lr, [pc, #2864]	; 0x186c
     d38:	e59e1000 	ldr	r1, [lr]
     d3c:	e1a02001 	mov	r2, r1
     d40:	e4120004 	ldr	r0, [r2], #-4
     d44:	e59f3b1c 	ldr	r3, [pc, #2844]	; 0x1868
     d48:	e5830000 	str	r0, [r3]
     d4c:	e5113004 	ldr	r3, [r1, #-4]
     d50:	e0833000 	add	r3, r3, r0
     d54:	e58e2000 	str	r2, [lr]
     d58:	e5013004 	str	r3, [r1, #-4]
     d5c:	eafffece 	b	0x89c
     d60:	e59feb04 	ldr	lr, [pc, #2820]	; 0x186c
     d64:	e59e2000 	ldr	r2, [lr]
     d68:	e1a01002 	mov	r1, r2
     d6c:	e4110004 	ldr	r0, [r1], #-4
     d70:	e5123004 	ldr	r3, [r2, #-4]
     d74:	e0603003 	rsb	r3, r0, r3
     d78:	e5023004 	str	r3, [r2, #-4]
     d7c:	e58e1000 	str	r1, [lr]
     d80:	e59f0ae4 	ldr	r0, [pc, #2788]	; 0x186c
     d84:	e5903000 	ldr	r3, [r0]
     d88:	e4132004 	ldr	r2, [r3], #-4
     d8c:	e3520000 	cmp	r2, #0	; 0x0
     d90:	e5803000 	str	r3, [r0]
     d94:	a5953000 	ldrge	r3, [r5]
     d98:	a2833002 	addge	r3, r3, #2	; 0x2
     d9c:	a5853000 	strge	r3, [r5]
     da0:	aafffebd 	bge	0x89c
     da4:	e5953000 	ldr	r3, [r5]
     da8:	e5d31000 	ldrb	r1, [r3]
     dac:	e5d32001 	ldrb	r2, [r3, #1]
     db0:	e1822401 	orr	r2, r2, r1, lsl #8
     db4:	e1a02802 	mov	r2, r2, lsl #16
     db8:	e0833842 	add	r3, r3, r2, asr #16
     dbc:	e2433001 	sub	r3, r3, #1	; 0x1
     dc0:	e5853000 	str	r3, [r5]
     dc4:	eafffeb4 	b	0x89c
     dc8:	e59fea9c 	ldr	lr, [pc, #2716]	; 0x186c
     dcc:	e59e2000 	ldr	r2, [lr]
     dd0:	e1a01002 	mov	r1, r2
     dd4:	e4110004 	ldr	r0, [r1], #-4
     dd8:	e5123004 	ldr	r3, [r2, #-4]
     ddc:	e0603003 	rsb	r3, r0, r3
     de0:	e5023004 	str	r3, [r2, #-4]
     de4:	e58e1000 	str	r1, [lr]
     de8:	e59f0a7c 	ldr	r0, [pc, #2684]	; 0x186c
     dec:	e5903000 	ldr	r3, [r0]
     df0:	e4132004 	ldr	r2, [r3], #-4
     df4:	e3520000 	cmp	r2, #0	; 0x0
     df8:	e5803000 	str	r3, [r0]
     dfc:	b5953000 	ldrlt	r3, [r5]
     e00:	b2833002 	addlt	r3, r3, #2	; 0x2
     e04:	b5853000 	strlt	r3, [r5]
     e08:	bafffea3 	blt	0x89c
     e0c:	e5953000 	ldr	r3, [r5]
     e10:	e5d31000 	ldrb	r1, [r3]
     e14:	e5d32001 	ldrb	r2, [r3, #1]
     e18:	e1822401 	orr	r2, r2, r1, lsl #8
     e1c:	e1a02802 	mov	r2, r2, lsl #16
     e20:	e0833842 	add	r3, r3, r2, asr #16
     e24:	e2433001 	sub	r3, r3, #1	; 0x1
     e28:	e5853000 	str	r3, [r5]
     e2c:	eafffe9a 	b	0x89c
     e30:	e59f1a34 	ldr	r1, [pc, #2612]	; 0x186c
     e34:	e5912000 	ldr	r2, [r1]
     e38:	e1a01002 	mov	r1, r2
     e3c:	e4110004 	ldr	r0, [r1], #-4
     e40:	e5123004 	ldr	r3, [r2, #-4]
     e44:	e0603003 	rsb	r3, r0, r3
     e48:	e5023004 	str	r3, [r2, #-4]
     e4c:	e59f2a18 	ldr	r2, [pc, #2584]	; 0x186c
     e50:	e5821000 	str	r1, [r2]
     e54:	e59fca10 	ldr	ip, [pc, #2576]	; 0x186c
     e58:	e59c3000 	ldr	r3, [ip]
     e5c:	e4132004 	ldr	r2, [r3], #-4
     e60:	e3520000 	cmp	r2, #0	; 0x0
     e64:	e58c3000 	str	r3, [ip]
     e68:	d5953000 	ldrle	r3, [r5]
     e6c:	d2833002 	addle	r3, r3, #2	; 0x2
     e70:	d5853000 	strle	r3, [r5]
     e74:	dafffe88 	ble	0x89c
     e78:	e5953000 	ldr	r3, [r5]
     e7c:	e5d31000 	ldrb	r1, [r3]
     e80:	e5d32001 	ldrb	r2, [r3, #1]
     e84:	e1822401 	orr	r2, r2, r1, lsl #8
     e88:	e1a02802 	mov	r2, r2, lsl #16
     e8c:	e0833842 	add	r3, r3, r2, asr #16
     e90:	e2433001 	sub	r3, r3, #1	; 0x1
     e94:	e5853000 	str	r3, [r5]
     e98:	eafffe7f 	b	0x89c
     e9c:	e59f19c8 	ldr	r1, [pc, #2504]	; 0x186c
     ea0:	e5912000 	ldr	r2, [r1]
     ea4:	e1a01002 	mov	r1, r2
     ea8:	e4110004 	ldr	r0, [r1], #-4
     eac:	e5123004 	ldr	r3, [r2, #-4]
     eb0:	e0603003 	rsb	r3, r0, r3
     eb4:	e5023004 	str	r3, [r2, #-4]
     eb8:	e59f29ac 	ldr	r2, [pc, #2476]	; 0x186c
     ebc:	e5821000 	str	r1, [r2]
     ec0:	e59fc9a4 	ldr	ip, [pc, #2468]	; 0x186c
     ec4:	e59c3000 	ldr	r3, [ip]
     ec8:	e4132004 	ldr	r2, [r3], #-4
     ecc:	e3520000 	cmp	r2, #0	; 0x0
     ed0:	e58c3000 	str	r3, [ip]
     ed4:	c5953000 	ldrgt	r3, [r5]
     ed8:	c2833002 	addgt	r3, r3, #2	; 0x2
     edc:	c5853000 	strgt	r3, [r5]
     ee0:	cafffe6d 	bgt	0x89c
     ee4:	e5953000 	ldr	r3, [r5]
     ee8:	e5d31000 	ldrb	r1, [r3]
     eec:	e5d32001 	ldrb	r2, [r3, #1]
     ef0:	e1822401 	orr	r2, r2, r1, lsl #8
     ef4:	e1a02802 	mov	r2, r2, lsl #16
     ef8:	e0833842 	add	r3, r3, r2, asr #16
     efc:	e2433001 	sub	r3, r3, #1	; 0x1
     f00:	e5853000 	str	r3, [r5]
     f04:	eafffe64 	b	0x89c
     f08:	e59fe95c 	ldr	lr, [pc, #2396]	; 0x186c
     f0c:	e59e3000 	ldr	r3, [lr]
     f10:	e2841002 	add	r1, r4, #2	; 0x2
     f14:	e2832004 	add	r2, r3, #4	; 0x4
     f18:	e58e2000 	str	r2, [lr]
     f1c:	e5831004 	str	r1, [r3, #4]
     f20:	e5953000 	ldr	r3, [r5]
     f24:	e5d31000 	ldrb	r1, [r3]
     f28:	e5d32001 	ldrb	r2, [r3, #1]
     f2c:	e1822401 	orr	r2, r2, r1, lsl #8
     f30:	e1a02802 	mov	r2, r2, lsl #16
     f34:	e0833842 	add	r3, r3, r2, asr #16
     f38:	e2433001 	sub	r3, r3, #1	; 0x1
     f3c:	e5853000 	str	r3, [r5]
     f40:	eafffe55 	b	0x89c
     f44:	e59f3920 	ldr	r3, [pc, #2336]	; 0x186c
     f48:	e5932000 	ldr	r2, [r3]
     f4c:	e1a01002 	mov	r1, r2
     f50:	e4110004 	ldr	r0, [r1], #-4
     f54:	e5123004 	ldr	r3, [r2, #-4]
     f58:	e59fc90c 	ldr	ip, [pc, #2316]	; 0x186c
     f5c:	e0603003 	rsb	r3, r0, r3
     f60:	e5023004 	str	r3, [r2, #-4]
     f64:	e58c1000 	str	r1, [ip]
     f68:	e59fe8fc 	ldr	lr, [pc, #2300]	; 0x186c
     f6c:	e59e3000 	ldr	r3, [lr]
     f70:	e4132004 	ldr	r2, [r3], #-4
     f74:	e3520000 	cmp	r2, #0	; 0x0
     f78:	e58e3000 	str	r3, [lr]
     f7c:	15953000 	ldrne	r3, [r5]
     f80:	12833002 	addne	r3, r3, #2	; 0x2
     f84:	15853000 	strne	r3, [r5]
     f88:	1afffe43 	bne	0x89c
     f8c:	e5953000 	ldr	r3, [r5]
     f90:	e5d31000 	ldrb	r1, [r3]
     f94:	e5d32001 	ldrb	r2, [r3, #1]
     f98:	e1822401 	orr	r2, r2, r1, lsl #8
     f9c:	e1a02802 	mov	r2, r2, lsl #16
     fa0:	e0833842 	add	r3, r3, r2, asr #16
     fa4:	e2433001 	sub	r3, r3, #1	; 0x1
     fa8:	e5853000 	str	r3, [r5]
     fac:	eafffe3a 	b	0x89c
     fb0:	e59f08b4 	ldr	r0, [pc, #2228]	; 0x186c
     fb4:	e5902000 	ldr	r2, [r0]
     fb8:	e1a01002 	mov	r1, r2
     fbc:	e4110004 	ldr	r0, [r1], #-4
     fc0:	e5123004 	ldr	r3, [r2, #-4]
     fc4:	e0603003 	rsb	r3, r0, r3
     fc8:	e5023004 	str	r3, [r2, #-4]
     fcc:	e59f2898 	ldr	r2, [pc, #2200]	; 0x186c
     fd0:	e5821000 	str	r1, [r2]
     fd4:	e59fc890 	ldr	ip, [pc, #2192]	; 0x186c
     fd8:	e59c3000 	ldr	r3, [ip]
     fdc:	e4132004 	ldr	r2, [r3], #-4
     fe0:	e3520000 	cmp	r2, #0	; 0x0
     fe4:	e58c3000 	str	r3, [ip]
     fe8:	05953000 	ldreq	r3, [r5]
     fec:	02833002 	addeq	r3, r3, #2	; 0x2
     ff0:	05853000 	streq	r3, [r5]
     ff4:	0afffe28 	beq	0x89c
     ff8:	e5953000 	ldr	r3, [r5]
     ffc:	e5d31000 	ldrb	r1, [r3]
    1000:	e5d32001 	ldrb	r2, [r3, #1]
    1004:	e1822401 	orr	r2, r2, r1, lsl #8
    1008:	e1a02802 	mov	r2, r2, lsl #16
    100c:	e0833842 	add	r3, r3, r2, asr #16
    1010:	e2433001 	sub	r3, r3, #1	; 0x1
    1014:	e5853000 	str	r3, [r5]
    1018:	eafffe1f 	b	0x89c
    101c:	e59f0848 	ldr	r0, [pc, #2120]	; 0x186c
    1020:	e5903000 	ldr	r3, [r0]
    1024:	e1a02003 	mov	r2, r3
    1028:	e4121004 	ldr	r1, [r2], #-4
    102c:	e5802000 	str	r2, [r0]
    1030:	e59f2830 	ldr	r2, [pc, #2096]	; 0x1868
    1034:	e5821000 	str	r1, [r2]
    1038:	e5031004 	str	r1, [r3, #-4]
    103c:	eafffe16 	b	0x89c
    1040:	e59f0824 	ldr	r0, [pc, #2084]	; 0x186c
    1044:	e5902000 	ldr	r2, [r0]
    1048:	e5923000 	ldr	r3, [r2]
    104c:	e1a03c03 	mov	r3, r3, lsl #24
    1050:	e1a03c43 	mov	r3, r3, asr #24
    1054:	e5823000 	str	r3, [r2]
    1058:	eafffe0f 	b	0x89c
    105c:	e59fe808 	ldr	lr, [pc, #2056]	; 0x186c
    1060:	e59e1000 	ldr	r1, [lr]
    1064:	e59f07fc 	ldr	r0, [pc, #2044]	; 0x1868
    1068:	e5913000 	ldr	r3, [r1]
    106c:	e5803000 	str	r3, [r0]
    1070:	e5112004 	ldr	r2, [r1, #-4]
    1074:	e5812000 	str	r2, [r1]
    1078:	e5903000 	ldr	r3, [r0]
    107c:	e5013004 	str	r3, [r1, #-4]
    1080:	eafffe05 	b	0x89c
    1084:	e59f17e0 	ldr	r1, [pc, #2016]	; 0x186c
    1088:	e5912000 	ldr	r2, [r1]
    108c:	e5923000 	ldr	r3, [r2]
    1090:	e1a03803 	mov	r3, r3, lsl #16
    1094:	e1a03843 	mov	r3, r3, asr #16
    1098:	e5823000 	str	r3, [r2]
    109c:	eafffdfe 	b	0x89c
    10a0:	e59f07ac 	ldr	r0, [pc, #1964]	; 0x1854
    10a4:	e5d43000 	ldrb	r3, [r4]
    10a8:	e5902000 	ldr	r2, [r0]
    10ac:	e7921103 	ldr	r1, [r2, r3, lsl #2]
    10b0:	e5851000 	str	r1, [r5]
    10b4:	eafffdf8 	b	0x89c
    10b8:	e5540001 	ldrb	r0, [r4, #-1]
    10bc:	e24000ac 	sub	r0, r0, #172	; 0xac
    10c0:	e1a03fa0 	mov	r3, r0, lsr #31
    10c4:	e0800003 	add	r0, r0, r3
    10c8:	e2000001 	and	r0, r0, #1	; 0x1
    10cc:	e0630000 	rsb	r0, r3, r0
    10d0:	e2800001 	add	r0, r0, #1	; 0x1
    10d4:	e20000ff 	and	r0, r0, #255	; 0xff
    10d8:	eb000cfa 	bl	0x44c8
    10dc:	eafffdee 	b	0x89c
    10e0:	e3a00000 	mov	r0, #0	; 0x0
    10e4:	eb000cf7 	bl	0x44c8
    10e8:	eafffdeb 	b	0x89c
    10ec:	e59f676c 	ldr	r6, [pc, #1900]	; 0x1860
    10f0:	e4543001 	ldrb	r3, [r4], #-1
    10f4:	e5960000 	ldr	r0, [r6]
    10f8:	e0833103 	add	r3, r3, r3, lsl #2
    10fc:	e0800083 	add	r0, r0, r3, lsl #1
    1100:	e2800010 	add	r0, r0, #16	; 0x10
    1104:	e1a01004 	mov	r1, r4
    1108:	eb000db3 	bl	0x47dc
    110c:	e250e000 	subs	lr, r0, #0	; 0x0
    1110:	1afffde1 	bne	0x89c
    1114:	e5960000 	ldr	r0, [r6]
    1118:	e595c000 	ldr	ip, [r5]
    111c:	e1d030b4 	ldrh	r3, [r0, #4]
    1120:	e5dc2001 	ldrb	r2, [ip, #1]
    1124:	e0833082 	add	r3, r3, r2, lsl #1
    1128:	e19330b0 	ldrh	r3, [r3, r0]
    112c:	e59f2700 	ldr	r2, [pc, #1792]	; 0x1834
    1130:	e7d21623 	ldrb	r1, [r2, r3, lsr #12]
    1134:	e3510004 	cmp	r1, #4	; 0x4
    1138:	83a0e001 	movhi	lr, #1	; 0x1
    113c:	850be02c 	strhi	lr, [fp, #-44]
    1140:	950be02c 	strls	lr, [fp, #-44]
    1144:	91a09001 	movls	r9, r1
    1148:	e1d020b6 	ldrh	r2, [r0, #6]
    114c:	e55c1001 	ldrb	r1, [ip, #-1]
    1150:	e1a03a03 	mov	r3, r3, lsl #20
    1154:	83a09004 	movhi	r9, #4	; 0x4
    1158:	e0802002 	add	r2, r0, r2
    115c:	e1a03a23 	mov	r3, r3, lsr #20
    1160:	e35100b2 	cmp	r1, #178	; 0xb2
    1164:	e0824003 	add	r4, r2, r3
    1168:	0a0004c1 	beq	0x2474
    116c:	e51be02c 	ldr	lr, [fp, #-44]
    1170:	e35e0000 	cmp	lr, #0	; 0x0
    1174:	0a000007 	beq	0x1198
    1178:	e59f06ec 	ldr	r0, [pc, #1772]	; 0x186c
    117c:	e5903000 	ldr	r3, [r0]
    1180:	e59fc6e4 	ldr	ip, [pc, #1764]	; 0x186c
    1184:	e4132004 	ldr	r2, [r3], #-4
    1188:	e2840004 	add	r0, r4, #4	; 0x4
    118c:	e3a01004 	mov	r1, #4	; 0x4
    1190:	e58c3000 	str	r3, [ip]
    1194:	eb000888 	bl	0x33bc
    1198:	e59fe6cc 	ldr	lr, [pc, #1740]	; 0x186c
    119c:	e59e3000 	ldr	r3, [lr]
    11a0:	e4132004 	ldr	r2, [r3], #-4
    11a4:	e1a00004 	mov	r0, r4
    11a8:	e1a01009 	mov	r1, r9
    11ac:	e58e3000 	str	r3, [lr]
    11b0:	eb000881 	bl	0x33bc
    11b4:	e5953000 	ldr	r3, [r5]
    11b8:	e2833002 	add	r3, r3, #2	; 0x2
    11bc:	e5853000 	str	r3, [r5]
    11c0:	eafffdb5 	b	0x89c
    11c4:	e59f06a0 	ldr	r0, [pc, #1696]	; 0x186c
    11c8:	e5903000 	ldr	r3, [r0]
    11cc:	e593c000 	ldr	ip, [r3]
    11d0:	e59f9690 	ldr	r9, [pc, #1680]	; 0x1868
    11d4:	e35c0000 	cmp	ip, #0	; 0x0
    11d8:	e589c000 	str	ip, [r9]
    11dc:	0a000470 	beq	0x23a4
    11e0:	e5951000 	ldr	r1, [r5]
    11e4:	e59f2648 	ldr	r2, [pc, #1608]	; 0x1834
    11e8:	e5d13000 	ldrb	r3, [r1]
    11ec:	e5d10001 	ldrb	r0, [r1, #1]
    11f0:	e7d21223 	ldrb	r1, [r2, r3, lsr #4]
    11f4:	e203300f 	and	r3, r3, #15	; 0xf
    11f8:	e1800403 	orr	r0, r0, r3, lsl #8
    11fc:	e3510004 	cmp	r1, #4	; 0x4
    1200:	93a04000 	movls	r4, #0	; 0x0
    1204:	83a04001 	movhi	r4, #1	; 0x1
    1208:	e3540000 	cmp	r4, #0	; 0x0
    120c:	e080600c 	add	r6, r0, ip
    1210:	13a01004 	movne	r1, #4	; 0x4
    1214:	e1a02009 	mov	r2, r9
    1218:	e1a00006 	mov	r0, r6
    121c:	eb000873 	bl	0x33f0
    1220:	e59f3644 	ldr	r3, [pc, #1604]	; 0x186c
    1224:	e5932000 	ldr	r2, [r3]
    1228:	e5993000 	ldr	r3, [r9]
    122c:	e3540000 	cmp	r4, #0	; 0x0
    1230:	e5823000 	str	r3, [r2]
    1234:	1a000470 	bne	0x23fc
    1238:	e5953000 	ldr	r3, [r5]
    123c:	e2833002 	add	r3, r3, #2	; 0x2
    1240:	e5853000 	str	r3, [r5]
    1244:	eafffd94 	b	0x89c
    1248:	e5d42000 	ldrb	r2, [r4]
    124c:	e59f35e0 	ldr	r3, [pc, #1504]	; 0x1834
    1250:	e7d34222 	ldrb	r4, [r3, r2, lsr #4]
    1254:	e3540004 	cmp	r4, #4	; 0x4
    1258:	93a0c000 	movls	ip, #0	; 0x0
    125c:	83a0c001 	movhi	ip, #1	; 0x1
    1260:	e35c0000 	cmp	ip, #0	; 0x0
    1264:	1a0003e0 	bne	0x21ec
    1268:	e59fe5fc 	ldr	lr, [pc, #1532]	; 0x186c
    126c:	e59e3000 	ldr	r3, [lr]
    1270:	e3a02004 	mov	r2, #4	; 0x4
    1274:	e0623003 	rsb	r3, r2, r3
    1278:	e5930000 	ldr	r0, [r3]
    127c:	e59f15e4 	ldr	r1, [pc, #1508]	; 0x1868
    1280:	e3500000 	cmp	r0, #0	; 0x0
    1284:	e5810000 	str	r0, [r1]
    1288:	0a0003f3 	beq	0x225c
    128c:	e5953000 	ldr	r3, [r5]
    1290:	e5d32000 	ldrb	r2, [r3]
    1294:	e5d31001 	ldrb	r1, [r3, #1]
    1298:	e202200f 	and	r2, r2, #15	; 0xf
    129c:	e1811402 	orr	r1, r1, r2, lsl #8
    12a0:	e35c0000 	cmp	ip, #0	; 0x0
    12a4:	e0815000 	add	r5, r1, r0
    12a8:	e59f6580 	ldr	r6, [pc, #1408]	; 0x1830
    12ac:	1a000468 	bne	0x2454
    12b0:	e59fe5b4 	ldr	lr, [pc, #1460]	; 0x186c
    12b4:	e59e3000 	ldr	r3, [lr]
    12b8:	e4132004 	ldr	r2, [r3], #-4
    12bc:	e1a00005 	mov	r0, r5
    12c0:	e58e3000 	str	r3, [lr]
    12c4:	e1a01004 	mov	r1, r4
    12c8:	eb00083b 	bl	0x33bc
    12cc:	e59f0598 	ldr	r0, [pc, #1432]	; 0x186c
    12d0:	e5962000 	ldr	r2, [r6]
    12d4:	e5903000 	ldr	r3, [r0]
    12d8:	e2822002 	add	r2, r2, #2	; 0x2
    12dc:	e2433004 	sub	r3, r3, #4	; 0x4
    12e0:	e5803000 	str	r3, [r0]
    12e4:	e5862000 	str	r2, [r6]
    12e8:	eafffd6b 	b	0x89c
    12ec:	e59f2578 	ldr	r2, [pc, #1400]	; 0x186c
    12f0:	e5d41000 	ldrb	r1, [r4]
    12f4:	e5923000 	ldr	r3, [r2]
    12f8:	e5d40001 	ldrb	r0, [r4, #1]
    12fc:	e1a02221 	mov	r2, r1, lsr #4
    1300:	e0433102 	sub	r3, r3, r2, lsl #2
    1304:	e201100f 	and	r1, r1, #15	; 0xf
    1308:	e1801401 	orr	r1, r0, r1, lsl #8
    130c:	e2842002 	add	r2, r4, #2	; 0x2
    1310:	e5930000 	ldr	r0, [r3]
    1314:	eb000d7c 	bl	0x490c
    1318:	eafffd5f 	b	0x89c
    131c:	e2443001 	sub	r3, r4, #1	; 0x1
    1320:	e5d40000 	ldrb	r0, [r4]
    1324:	e5d41001 	ldrb	r1, [r4, #1]
    1328:	e2842002 	add	r2, r4, #2	; 0x2
    132c:	eb000dc6 	bl	0x4a4c
    1330:	eafffd59 	b	0x89c
    1334:	e2441001 	sub	r1, r4, #1	; 0x1
    1338:	e5d40001 	ldrb	r0, [r4, #1]
    133c:	eb000bbf 	bl	0x4240
    1340:	e59f2500 	ldr	r2, [pc, #1280]	; 0x1848
    1344:	e3500000 	cmp	r0, #0	; 0x0
    1348:	e5820000 	str	r0, [r2]
    134c:	0afffd52 	beq	0x89c
    1350:	e59fc514 	ldr	ip, [pc, #1300]	; 0x186c
    1354:	e5953000 	ldr	r3, [r5]
    1358:	e59c1000 	ldr	r1, [ip]
    135c:	e2833002 	add	r3, r3, #2	; 0x2
    1360:	e2812004 	add	r2, r1, #4	; 0x4
    1364:	e58c2000 	str	r2, [ip]
    1368:	e5810004 	str	r0, [r1, #4]
    136c:	e5853000 	str	r3, [r5]
    1370:	eafffd49 	b	0x89c
    1374:	e59fc4f0 	ldr	ip, [pc, #1264]	; 0x186c
    1378:	e4d40001 	ldrb	r0, [r4], #1
    137c:	e59c3000 	ldr	r3, [ip]
    1380:	e5854000 	str	r4, [r5]
    1384:	e5931000 	ldr	r1, [r3]
    1388:	eb0009a9 	bl	0x3a34
    138c:	e59fe4d8 	ldr	lr, [pc, #1240]	; 0x186c
    1390:	e59e3000 	ldr	r3, [lr]
    1394:	e5830000 	str	r0, [r3]
    1398:	eafffd3f 	b	0x89c
    139c:	e59fe4c8 	ldr	lr, [pc, #1224]	; 0x186c
    13a0:	e59e0000 	ldr	r0, [lr]
    13a4:	e5903000 	ldr	r3, [r0]
    13a8:	e3530000 	cmp	r3, #0	; 0x0
    13ac:	0a0003cc 	beq	0x22e4
    13b0:	e1d330b0 	ldrh	r3, [r3]
    13b4:	e1a03b83 	mov	r3, r3, lsl #23
    13b8:	e1a03ba3 	mov	r3, r3, lsr #23
    13bc:	e5803000 	str	r3, [r0]
    13c0:	eafffd35 	b	0x89c
    13c4:	e59f14a0 	ldr	r1, [pc, #1184]	; 0x186c
    13c8:	e5913000 	ldr	r3, [r1]
    13cc:	e4130004 	ldr	r0, [r3], #-4
    13d0:	e59f2490 	ldr	r2, [pc, #1168]	; 0x1868
    13d4:	e3500000 	cmp	r0, #0	; 0x0
    13d8:	e5813000 	str	r3, [r1]
    13dc:	e5820000 	str	r0, [r2]
    13e0:	1a000395 	bne	0x223c
    13e4:	e59f3470 	ldr	r3, [pc, #1136]	; 0x185c
    13e8:	e5930000 	ldr	r0, [r3]
    13ec:	eb0006fe 	bl	0x2fec
    13f0:	eafffd29 	b	0x89c
    13f4:	e59f1464 	ldr	r1, [pc, #1124]	; 0x1860
    13f8:	e5910000 	ldr	r0, [r1]
    13fc:	e1a02004 	mov	r2, r4
    1400:	e1d030b2 	ldrh	r3, [r0, #2]
    1404:	e4d21001 	ldrb	r1, [r2], #1
    1408:	e0833000 	add	r3, r3, r0
    140c:	e0836101 	add	r6, r3, r1, lsl #2
    1410:	e5d69002 	ldrb	r9, [r6, #2]
    1414:	e59f3420 	ldr	r3, [pc, #1056]	; 0x183c
    1418:	e3590006 	cmp	r9, #6	; 0x6
    141c:	e5852000 	str	r2, [r5]
    1420:	e5836000 	str	r6, [r3]
    1424:	0a000375 	beq	0x2200
    1428:	e359000a 	cmp	r9, #10	; 0xa
    142c:	0a000373 	beq	0x2200
    1430:	e3590000 	cmp	r9, #0	; 0x0
    1434:	1afffd18 	bne	0x89c
    1438:	e2441001 	sub	r1, r4, #1	; 0x1
    143c:	e3a00002 	mov	r0, #2	; 0x2
    1440:	eb000b7e 	bl	0x4240
    1444:	e2504000 	subs	r4, r0, #0	; 0x0
    1448:	0a0003fd 	beq	0x2444
    144c:	e3a00005 	mov	r0, #5	; 0x5
    1450:	e5d61003 	ldrb	r1, [r6, #3]
    1454:	eb000976 	bl	0x3a34
    1458:	e2505000 	subs	r5, r0, #0	; 0x0
    145c:	0a0003f1 	beq	0x2428
    1460:	e2840008 	add	r0, r4, #8	; 0x8
    1464:	e3a01004 	mov	r1, #4	; 0x4
    1468:	e1a02005 	mov	r2, r5
    146c:	eb0007d2 	bl	0x33bc
    1470:	e5d63003 	ldrb	r3, [r6, #3]
    1474:	e3530000 	cmp	r3, #0	; 0x0
    1478:	0a00000d 	beq	0x14b4
    147c:	e59f33dc 	ldr	r3, [pc, #988]	; 0x1860
    1480:	e5930000 	ldr	r0, [r3]
    1484:	e1a0c009 	mov	ip, r9
    1488:	e1d630b0 	ldrh	r3, [r6]
    148c:	e083300c 	add	r3, r3, ip
    1490:	e7d31000 	ldrb	r1, [r3, r0]
    1494:	e085208c 	add	r2, r5, ip, lsl #1
    1498:	e1c210b8 	strh	r1, [r2, #8]
    149c:	e28c3001 	add	r3, ip, #1	; 0x1
    14a0:	e1a03803 	mov	r3, r3, lsl #16
    14a4:	e5d62003 	ldrb	r2, [r6, #3]
    14a8:	e1a0c823 	mov	ip, r3, lsr #16
    14ac:	e152000c 	cmp	r2, ip
    14b0:	8afffff4 	bhi	0x1488
    14b4:	e59fc3b0 	ldr	ip, [pc, #944]	; 0x186c
    14b8:	e59c3000 	ldr	r3, [ip]
    14bc:	e2832004 	add	r2, r3, #4	; 0x4
    14c0:	e58c2000 	str	r2, [ip]
    14c4:	e59f2398 	ldr	r2, [pc, #920]	; 0x1864
    14c8:	e5834004 	str	r4, [r3, #4]
    14cc:	e5824000 	str	r4, [r2]
    14d0:	eafffcf1 	b	0x89c
    14d4:	e59fc390 	ldr	ip, [pc, #912]	; 0x186c
    14d8:	e59c3000 	ldr	r3, [ip]
    14dc:	e2831004 	add	r1, r3, #4	; 0x4
    14e0:	e5112004 	ldr	r2, [r1, #-4]
    14e4:	e58c1000 	str	r1, [ip]
    14e8:	e5832004 	str	r2, [r3, #4]
    14ec:	eafffcea 	b	0x89c
    14f0:	e59f1374 	ldr	r1, [pc, #884]	; 0x186c
    14f4:	e5910000 	ldr	r0, [r1]
    14f8:	e2801004 	add	r1, r0, #4	; 0x4
    14fc:	e5113004 	ldr	r3, [r1, #-4]
    1500:	e5803004 	str	r3, [r0, #4]
    1504:	e5112008 	ldr	r2, [r1, #-8]
    1508:	e5012004 	str	r2, [r1, #-4]
    150c:	e59f2358 	ldr	r2, [pc, #856]	; 0x186c
    1510:	e5903004 	ldr	r3, [r0, #4]
    1514:	e5821000 	str	r1, [r2]
    1518:	e5013008 	str	r3, [r1, #-8]
    151c:	eafffcde 	b	0x89c
    1520:	e5d42002 	ldrb	r2, [r4, #2]
    1524:	e59f0340 	ldr	r0, [pc, #832]	; 0x186c
    1528:	e59f4320 	ldr	r4, [pc, #800]	; 0x1850
    152c:	e2422001 	sub	r2, r2, #1	; 0x1
    1530:	e20220ff 	and	r2, r2, #255	; 0xff
    1534:	e595c000 	ldr	ip, [r5]
    1538:	e5c42000 	strb	r2, [r4]
    153c:	e5903000 	ldr	r3, [r0]
    1540:	e5dc1001 	ldrb	r1, [ip, #1]
    1544:	e0433102 	sub	r3, r3, r2, lsl #2
    1548:	e5dc0000 	ldrb	r0, [ip]
    154c:	e5dc2002 	ldrb	r2, [ip, #2]
    1550:	eb0009de 	bl	0x3cd0
    1554:	e59f3310 	ldr	r3, [pc, #784]	; 0x186c
    1558:	e5d41000 	ldrb	r1, [r4]
    155c:	e5932000 	ldr	r2, [r3]
    1560:	e5953000 	ldr	r3, [r5]
    1564:	e59fc300 	ldr	ip, [pc, #768]	; 0x186c
    1568:	e59fe2d8 	ldr	lr, [pc, #728]	; 0x1848
    156c:	e0422101 	sub	r2, r2, r1, lsl #2
    1570:	e2833003 	add	r3, r3, #3	; 0x3
    1574:	e5820000 	str	r0, [r2]
    1578:	e5853000 	str	r3, [r5]
    157c:	e58c2000 	str	r2, [ip]
    1580:	e58e0000 	str	r0, [lr]
    1584:	eafffcc4 	b	0x89c
    1588:	e59fe2dc 	ldr	lr, [pc, #732]	; 0x186c
    158c:	e59e3000 	ldr	r3, [lr]
    1590:	e3a01000 	mov	r1, #0	; 0x0
    1594:	e2832004 	add	r2, r3, #4	; 0x4
    1598:	e58e2000 	str	r2, [lr]
    159c:	e5831004 	str	r1, [r3, #4]
    15a0:	eafffcbd 	b	0x89c
    15a4:	e59f02c0 	ldr	r0, [pc, #704]	; 0x186c
    15a8:	e5543001 	ldrb	r3, [r4, #-1]
    15ac:	e5901000 	ldr	r1, [r0]
    15b0:	e2433003 	sub	r3, r3, #3	; 0x3
    15b4:	e2812004 	add	r2, r1, #4	; 0x4
    15b8:	e5802000 	str	r2, [r0]
    15bc:	e5813004 	str	r3, [r1, #4]
    15c0:	eafffcb5 	b	0x89c
    15c4:	e59f12a0 	ldr	r1, [pc, #672]	; 0x186c
    15c8:	e5912000 	ldr	r2, [r1]
    15cc:	e3a03000 	mov	r3, #0	; 0x0
    15d0:	e2821004 	add	r1, r2, #4	; 0x4
    15d4:	e5823004 	str	r3, [r2, #4]
    15d8:	e59f228c 	ldr	r2, [pc, #652]	; 0x186c
    15dc:	e5953000 	ldr	r3, [r5]
    15e0:	e5821000 	str	r1, [r2]
    15e4:	e5532001 	ldrb	r2, [r3, #-1]
    15e8:	e59fc27c 	ldr	ip, [pc, #636]	; 0x186c
    15ec:	e2422009 	sub	r2, r2, #9	; 0x9
    15f0:	e2813004 	add	r3, r1, #4	; 0x4
    15f4:	e58c3000 	str	r3, [ip]
    15f8:	e5812004 	str	r2, [r1, #4]
    15fc:	eafffca6 	b	0x89c
    1600:	e59f3264 	ldr	r3, [pc, #612]	; 0x186c
    1604:	e5931000 	ldr	r1, [r3]
    1608:	e59fc258 	ldr	ip, [pc, #600]	; 0x1868
    160c:	e5910000 	ldr	r0, [r1]
    1610:	e1a02001 	mov	r2, r1
    1614:	e3a03000 	mov	r3, #0	; 0x0
    1618:	e58c0000 	str	r0, [ip]
    161c:	e4823004 	str	r3, [r2], #4
    1620:	e59fe244 	ldr	lr, [pc, #580]	; 0x186c
    1624:	e59c0000 	ldr	r0, [ip]
    1628:	e58e2000 	str	r2, [lr]
    162c:	e5810004 	str	r0, [r1, #4]
    1630:	eafffc99 	b	0x89c
    1634:	e59fc218 	ldr	ip, [pc, #536]	; 0x1854
    1638:	e5542001 	ldrb	r2, [r4, #-1]
    163c:	e59fe228 	ldr	lr, [pc, #552]	; 0x186c
    1640:	e59c3000 	ldr	r3, [ip]
    1644:	e59e1000 	ldr	r1, [lr]
    1648:	e0833102 	add	r3, r3, r2, lsl #2
    164c:	e51300a8 	ldr	r0, [r3, #-168]
    1650:	e2812004 	add	r2, r1, #4	; 0x4
    1654:	e58e2000 	str	r2, [lr]
    1658:	e5810004 	str	r0, [r1, #4]
    165c:	eafffc8e 	b	0x89c
    1660:	e59fc204 	ldr	ip, [pc, #516]	; 0x186c
    1664:	e59c0000 	ldr	r0, [ip]
    1668:	e1a03000 	mov	r3, r0
    166c:	e4132004 	ldr	r2, [r3], #-4
    1670:	e59fc1cc 	ldr	ip, [pc, #460]	; 0x1844
    1674:	e5101004 	ldr	r1, [r0, #-4]
    1678:	e59fe1ec 	ldr	lr, [pc, #492]	; 0x186c
    167c:	e1cc20b0 	strh	r2, [ip]
    1680:	e59f21c0 	ldr	r2, [pc, #448]	; 0x1848
    1684:	e3510000 	cmp	r1, #0	; 0x0
    1688:	e58e3000 	str	r3, [lr]
    168c:	e5821000 	str	r1, [r2]
    1690:	0a000347 	beq	0x23b4
    1694:	e1dc20f0 	ldrsh	r2, [ip]
    1698:	e3520000 	cmp	r2, #0	; 0x0
    169c:	ba000007 	blt	0x16c0
    16a0:	e1d130b0 	ldrh	r3, [r1]
    16a4:	e1a03b83 	mov	r3, r3, lsl #23
    16a8:	e1a03ba3 	mov	r3, r3, lsr #23
    16ac:	e1520003 	cmp	r2, r3
    16b0:	b0813102 	addlt	r3, r1, r2, lsl #2
    16b4:	b5932008 	ldrlt	r2, [r3, #8]
    16b8:	b5002004 	strlt	r2, [r0, #-4]
    16bc:	bafffc76 	blt	0x89c
    16c0:	e59f3184 	ldr	r3, [pc, #388]	; 0x184c
    16c4:	e5930000 	ldr	r0, [r3]
    16c8:	eb000647 	bl	0x2fec
    16cc:	eafffc72 	b	0x89c
    16d0:	e59fc194 	ldr	ip, [pc, #404]	; 0x186c
    16d4:	e59c0000 	ldr	r0, [ip]
    16d8:	e1a03000 	mov	r3, r0
    16dc:	e4132004 	ldr	r2, [r3], #-4
    16e0:	e59fe15c 	ldr	lr, [pc, #348]	; 0x1844
    16e4:	e510c004 	ldr	ip, [r0, #-4]
    16e8:	e59f117c 	ldr	r1, [pc, #380]	; 0x186c
    16ec:	e1ce20b0 	strh	r2, [lr]
    16f0:	e59f2150 	ldr	r2, [pc, #336]	; 0x1848
    16f4:	e35c0000 	cmp	ip, #0	; 0x0
    16f8:	e5813000 	str	r3, [r1]
    16fc:	e582c000 	str	ip, [r2]
    1700:	0a000323 	beq	0x2394
    1704:	e1de20f0 	ldrsh	r2, [lr]
    1708:	e3520000 	cmp	r2, #0	; 0x0
    170c:	ba000004 	blt	0x1724
    1710:	e1dc30b0 	ldrh	r3, [ip]
    1714:	e1a03b83 	mov	r3, r3, lsl #23
    1718:	e1a03ba3 	mov	r3, r3, lsr #23
    171c:	e1520003 	cmp	r2, r3
    1720:	ba000327 	blt	0x23c4
    1724:	e59f3120 	ldr	r3, [pc, #288]	; 0x184c
    1728:	e5930000 	ldr	r0, [r3]
    172c:	eb00062e 	bl	0x2fec
    1730:	eafffc59 	b	0x89c
    1734:	e59f1130 	ldr	r1, [pc, #304]	; 0x186c
    1738:	e5910000 	ldr	r0, [r1]
    173c:	e1a03000 	mov	r3, r0
    1740:	e4132004 	ldr	r2, [r3], #-4
    1744:	e59fc0f8 	ldr	ip, [pc, #248]	; 0x1844
    1748:	e5101004 	ldr	r1, [r0, #-4]
    174c:	e59fe118 	ldr	lr, [pc, #280]	; 0x186c
    1750:	e1cc20b0 	strh	r2, [ip]
    1754:	e59f20ec 	ldr	r2, [pc, #236]	; 0x1848
    1758:	e3510000 	cmp	r1, #0	; 0x0
    175c:	e58e3000 	str	r3, [lr]
    1760:	e5821000 	str	r1, [r2]
    1764:	0a000306 	beq	0x2384
    1768:	e1dc20f0 	ldrsh	r2, [ip]
    176c:	e3520000 	cmp	r2, #0	; 0x0
    1770:	ba000007 	blt	0x1794
    1774:	e1d130b0 	ldrh	r3, [r1]
    1778:	e1a03b83 	mov	r3, r3, lsl #23
    177c:	e1a03ba3 	mov	r3, r3, lsr #23
    1780:	e1520003 	cmp	r2, r3
    1784:	b0813102 	addlt	r3, r1, r2, lsl #2
    1788:	b5932008 	ldrlt	r2, [r3, #8]
    178c:	b5002004 	strlt	r2, [r0, #-4]
    1790:	bafffc41 	blt	0x89c
    1794:	e59f30b0 	ldr	r3, [pc, #176]	; 0x184c
    1798:	e5930000 	ldr	r0, [r3]
    179c:	eb000612 	bl	0x2fec
    17a0:	eafffc3d 	b	0x89c
    17a4:	e59f308c 	ldr	r3, [pc, #140]	; 0x1838
    17a8:	e5930000 	ldr	r0, [r3]
    17ac:	eb00060e 	bl	0x2fec
    17b0:	eafffd3d 	b	0xcac
    17b4:	e5543001 	ldrb	r3, [r4, #-1]
    17b8:	e59f4090 	ldr	r4, [pc, #144]	; 0x1850
    17bc:	e2433026 	sub	r3, r3, #38	; 0x26
    17c0:	e5c43000 	strb	r3, [r4]
    17c4:	e59f1088 	ldr	r1, [pc, #136]	; 0x1854
    17c8:	e5d43000 	ldrb	r3, [r4]
    17cc:	e5910000 	ldr	r0, [r1]
    17d0:	e59fc094 	ldr	ip, [pc, #148]	; 0x186c
    17d4:	e7901103 	ldr	r1, [r0, r3, lsl #2]
    17d8:	e59c2000 	ldr	r2, [ip]
    17dc:	e2833001 	add	r3, r3, #1	; 0x1
    17e0:	e5c43000 	strb	r3, [r4]
    17e4:	e5821004 	str	r1, [r2, #4]
    17e8:	e5d43000 	ldrb	r3, [r4]
    17ec:	e2822004 	add	r2, r2, #4	; 0x4
    17f0:	e7901103 	ldr	r1, [r0, r3, lsl #2]
    17f4:	e2823004 	add	r3, r2, #4	; 0x4
    17f8:	e58c3000 	str	r3, [ip]
    17fc:	e5821004 	str	r1, [r2, #4]
    1800:	eafffc25 	b	0x89c
    1804:	e59fe060 	ldr	lr, [pc, #96]	; 0x186c
    1808:	e59e2000 	ldr	r2, [lr]
    180c:	e59f302c 	ldr	r3, [pc, #44]	; 0x1840
    1810:	e4121004 	ldr	r1, [r2], #-4
    1814:	e5930000 	ldr	r0, [r3]
    1818:	e58e2000 	str	r2, [lr]
    181c:	eb000385 	bl	0x2638
    1820:	eafffc1d 	b	0x89c
    1824:	0020792f 	eoreq	r7, r0, pc, lsr #18
    1828:	0020792e 	eoreq	r7, r0, lr, lsr #18
    182c:	00207924 	eoreq	r7, r0, r4, lsr #18
    1830:	00207944 	eoreq	r7, r0, r4, asr #18
    1834:	002078c4 	eoreq	r7, r0, r4, asr #17
    1838:	00207980 	eoreq	r7, r0, r0, lsl #19
    183c:	00207934 	eoreq	r7, r0, r4, lsr r9
    1840:	00207978 	eoreq	r7, r0, r8, ror r9
    1844:	0020792c 	eoreq	r7, r0, ip, lsr #18
    1848:	00207930 	eoreq	r7, r0, r0, lsr r9
    184c:	0020799c 	mlaeq	r0, ip, r9, r7
    1850:	0020794c 	eoreq	r7, r0, ip, asr #18
    1854:	00207940 	eoreq	r7, r0, r0, asr #18
    1858:	00207988 	eoreq	r7, r0, r8, lsl #19
    185c:	0020798c 	eoreq	r7, r0, ip, lsl #19
    1860:	002079b0 	streqh	r7, [r0], -r0
    1864:	00207938 	eoreq	r7, r0, r8, lsr r9
    1868:	00207948 	eoreq	r7, r0, r8, asr #18
    186c:	00207928 	eoreq	r7, r0, r8, lsr #18
    1870:	e51fe00c 	ldr	lr, [pc, #-12]	; 0x186c
    1874:	e59e3000 	ldr	r3, [lr]
    1878:	e1a02003 	mov	r2, r3
    187c:	e412e004 	ldr	lr, [r2], #-4
    1880:	e51f0020 	ldr	r0, [pc, #-32]	; 0x1868
    1884:	e580e000 	str	lr, [r0]
    1888:	e5131004 	ldr	r1, [r3, #-4]
    188c:	e51f3050 	ldr	r3, [pc, #-80]	; 0x1844
    1890:	e5120004 	ldr	r0, [r2, #-4]
    1894:	e242c004 	sub	ip, r2, #4	; 0x4
    1898:	e1c310b0 	strh	r1, [r3]
    189c:	e51f205c 	ldr	r2, [pc, #-92]	; 0x1848
    18a0:	e51f103c 	ldr	r1, [pc, #-60]	; 0x186c
    18a4:	e3500000 	cmp	r0, #0	; 0x0
    18a8:	e581c000 	str	ip, [r1]
    18ac:	e5820000 	str	r0, [r2]
    18b0:	0a0002a9 	beq	0x235c
    18b4:	e1d320f0 	ldrsh	r2, [r3]
    18b8:	e3520000 	cmp	r2, #0	; 0x0
    18bc:	ba000004 	blt	0x18d4
    18c0:	e1d030b0 	ldrh	r3, [r0]
    18c4:	e1a03b83 	mov	r3, r3, lsl #23
    18c8:	e1a03ba3 	mov	r3, r3, lsr #23
    18cc:	e1520003 	cmp	r2, r3
    18d0:	ba0002a5 	blt	0x236c
    18d4:	e51f3090 	ldr	r3, [pc, #-144]	; 0x184c
    18d8:	e5930000 	ldr	r0, [r3]
    18dc:	eb0005c2 	bl	0x2fec
    18e0:	eafffbed 	b	0x89c
    18e4:	e51fe080 	ldr	lr, [pc, #-128]	; 0x186c
    18e8:	e59e3000 	ldr	r3, [lr]
    18ec:	e5132004 	ldr	r2, [r3, #-4]
    18f0:	e5931000 	ldr	r1, [r3]
    18f4:	e5832004 	str	r2, [r3, #4]
    18f8:	e5a31008 	str	r1, [r3, #8]!
    18fc:	e58e3000 	str	r3, [lr]
    1900:	eafffbe5 	b	0x89c
    1904:	e51f30a0 	ldr	r3, [pc, #-160]	; 0x186c
    1908:	e5931000 	ldr	r1, [r3]
    190c:	e2813008 	add	r3, r1, #8	; 0x8
    1910:	e5132008 	ldr	r2, [r3, #-8]
    1914:	e5812008 	str	r2, [r1, #8]
    1918:	e513000c 	ldr	r0, [r3, #-12]
    191c:	e5132010 	ldr	r2, [r3, #-16]
    1920:	e5030004 	str	r0, [r3, #-4]
    1924:	e5032008 	str	r2, [r3, #-8]
    1928:	e51fc0c4 	ldr	ip, [pc, #-196]	; 0x186c
    192c:	e5912008 	ldr	r2, [r1, #8]
    1930:	e58c3000 	str	r3, [ip]
    1934:	e5030010 	str	r0, [r3, #-16]
    1938:	e503200c 	str	r2, [r3, #-12]
    193c:	eafffbd6 	b	0x89c
    1940:	e51f10dc 	ldr	r1, [pc, #-220]	; 0x186c
    1944:	e5910000 	ldr	r0, [r1]
    1948:	e2803008 	add	r3, r0, #8	; 0x8
    194c:	e5132008 	ldr	r2, [r3, #-8]
    1950:	e5802008 	str	r2, [r0, #8]
    1954:	e2431014 	sub	r1, r3, #20	; 0x14
    1958:	e8911006 	ldmia	r1, {r1, r2, ip}
    195c:	e9031006 	stmdb	r3, {r1, r2, ip}
    1960:	e5902008 	ldr	r2, [r0, #8]
    1964:	e503c014 	str	ip, [r3, #-20]
    1968:	e51fc104 	ldr	ip, [pc, #-260]	; 0x186c
    196c:	e5032010 	str	r2, [r3, #-16]
    1970:	e58c3000 	str	r3, [ip]
    1974:	eafffbc8 	b	0x89c
    1978:	e51fe114 	ldr	lr, [pc, #-276]	; 0x186c
    197c:	e59ec000 	ldr	ip, [lr]
    1980:	e5d43000 	ldrb	r3, [r4]
    1984:	e51f0138 	ldr	r0, [pc, #-312]	; 0x1854
    1988:	e1a0200c 	mov	r2, ip
    198c:	e4121004 	ldr	r1, [r2], #-4
    1990:	e590e000 	ldr	lr, [r0]
    1994:	e2833001 	add	r3, r3, #1	; 0x1
    1998:	e20330ff 	and	r3, r3, #255	; 0xff
    199c:	e78e1103 	str	r1, [lr, r3, lsl #2]
    19a0:	e51f113c 	ldr	r1, [pc, #-316]	; 0x186c
    19a4:	e5953000 	ldr	r3, [r5]
    19a8:	e5812000 	str	r2, [r1]
    19ac:	e4d31001 	ldrb	r1, [r3], #1
    19b0:	e51c0004 	ldr	r0, [ip, #-4]
    19b4:	e5853000 	str	r3, [r5]
    19b8:	e51f3154 	ldr	r3, [pc, #-340]	; 0x186c
    19bc:	e2422004 	sub	r2, r2, #4	; 0x4
    19c0:	e78e0101 	str	r0, [lr, r1, lsl #2]
    19c4:	e5832000 	str	r2, [r3]
    19c8:	eafffbb3 	b	0x89c
    19cc:	e51fe168 	ldr	lr, [pc, #-360]	; 0x186c
    19d0:	e51fc184 	ldr	ip, [pc, #-388]	; 0x1854
    19d4:	e59e2000 	ldr	r2, [lr]
    19d8:	e4d40001 	ldrb	r0, [r4], #1
    19dc:	e4121004 	ldr	r1, [r2], #-4
    19e0:	e59c3000 	ldr	r3, [ip]
    19e4:	e5854000 	str	r4, [r5]
    19e8:	e7831100 	str	r1, [r3, r0, lsl #2]
    19ec:	e58e2000 	str	r2, [lr]
    19f0:	eafffba9 	b	0x89c
    19f4:	e51fe190 	ldr	lr, [pc, #-400]	; 0x186c
    19f8:	e5543001 	ldrb	r3, [r4, #-1]
    19fc:	e59e2000 	ldr	r2, [lr]
    1a00:	e51fc1b4 	ldr	ip, [pc, #-436]	; 0x1854
    1a04:	e4120004 	ldr	r0, [r2], #-4
    1a08:	e243303b 	sub	r3, r3, #59	; 0x3b
    1a0c:	e59c1000 	ldr	r1, [ip]
    1a10:	e20330ff 	and	r3, r3, #255	; 0xff
    1a14:	e7810103 	str	r0, [r1, r3, lsl #2]
    1a18:	e58e2000 	str	r2, [lr]
    1a1c:	eafffb9e 	b	0x89c
    1a20:	e5543001 	ldrb	r3, [r4, #-1]
    1a24:	e51f41dc 	ldr	r4, [pc, #-476]	; 0x1850
    1a28:	e243303f 	sub	r3, r3, #63	; 0x3f
    1a2c:	e5c43000 	strb	r3, [r4]
    1a30:	e51fc1cc 	ldr	ip, [pc, #-460]	; 0x186c
    1a34:	e59ce000 	ldr	lr, [ip]
    1a38:	e5d43000 	ldrb	r3, [r4]
    1a3c:	e51f01f0 	ldr	r0, [pc, #-496]	; 0x1854
    1a40:	e1a0200e 	mov	r2, lr
    1a44:	e4121004 	ldr	r1, [r2], #-4
    1a48:	e590c000 	ldr	ip, [r0]
    1a4c:	e2833001 	add	r3, r3, #1	; 0x1
    1a50:	e20330ff 	and	r3, r3, #255	; 0xff
    1a54:	e78c1103 	str	r1, [ip, r3, lsl #2]
    1a58:	e51f11f4 	ldr	r1, [pc, #-500]	; 0x186c
    1a5c:	e5d40000 	ldrb	r0, [r4]
    1a60:	e51e3004 	ldr	r3, [lr, #-4]
    1a64:	e2422004 	sub	r2, r2, #4	; 0x4
    1a68:	e78c3100 	str	r3, [ip, r0, lsl #2]
    1a6c:	e5812000 	str	r2, [r1]
    1a70:	eafffb89 	b	0x89c
    1a74:	e51fe210 	ldr	lr, [pc, #-528]	; 0x186c
    1a78:	e5543001 	ldrb	r3, [r4, #-1]
    1a7c:	e59e2000 	ldr	r2, [lr]
    1a80:	e51fc234 	ldr	ip, [pc, #-564]	; 0x1854
    1a84:	e4120004 	ldr	r0, [r2], #-4
    1a88:	e2433043 	sub	r3, r3, #67	; 0x43
    1a8c:	e59c1000 	ldr	r1, [ip]
    1a90:	e20330ff 	and	r3, r3, #255	; 0xff
    1a94:	e7810103 	str	r0, [r1, r3, lsl #2]
    1a98:	e58e2000 	str	r2, [lr]
    1a9c:	eafffb7e 	b	0x89c
    1aa0:	e5543001 	ldrb	r3, [r4, #-1]
    1aa4:	e51f425c 	ldr	r4, [pc, #-604]	; 0x1850
    1aa8:	e2433047 	sub	r3, r3, #71	; 0x47
    1aac:	e5c43000 	strb	r3, [r4]
    1ab0:	eaffffde 	b	0x1a30
    1ab4:	e51fe250 	ldr	lr, [pc, #-592]	; 0x186c
    1ab8:	e5543001 	ldrb	r3, [r4, #-1]
    1abc:	e59e2000 	ldr	r2, [lr]
    1ac0:	e51fc274 	ldr	ip, [pc, #-628]	; 0x1854
    1ac4:	e4120004 	ldr	r0, [r2], #-4
    1ac8:	e243304b 	sub	r3, r3, #75	; 0x4b
    1acc:	e59c1000 	ldr	r1, [ip]
    1ad0:	e20330ff 	and	r3, r3, #255	; 0xff
    1ad4:	e7810103 	str	r0, [r1, r3, lsl #2]
    1ad8:	e58e2000 	str	r2, [lr]
    1adc:	eafffb6e 	b	0x89c
    1ae0:	e51f127c 	ldr	r1, [pc, #-636]	; 0x186c
    1ae4:	e5913000 	ldr	r3, [r1]
    1ae8:	e1a02003 	mov	r2, r3
    1aec:	e412e004 	ldr	lr, [r2], #-4
    1af0:	e51fc290 	ldr	ip, [pc, #-656]	; 0x1868
    1af4:	e58ce000 	str	lr, [ip]
    1af8:	e5131004 	ldr	r1, [r3, #-4]
    1afc:	e51f32c0 	ldr	r3, [pc, #-704]	; 0x1844
    1b00:	e5120004 	ldr	r0, [r2, #-4]
    1b04:	e242c004 	sub	ip, r2, #4	; 0x4
    1b08:	e1c310b0 	strh	r1, [r3]
    1b0c:	e51f22cc 	ldr	r2, [pc, #-716]	; 0x1848
    1b10:	e51f12ac 	ldr	r1, [pc, #-684]	; 0x186c
    1b14:	e3500000 	cmp	r0, #0	; 0x0
    1b18:	e581c000 	str	ip, [r1]
    1b1c:	e5820000 	str	r0, [r2]
    1b20:	0a000203 	beq	0x2334
    1b24:	e1d320f0 	ldrsh	r2, [r3]
    1b28:	e3520000 	cmp	r2, #0	; 0x0
    1b2c:	ba000004 	blt	0x1b44
    1b30:	e1d030b0 	ldrh	r3, [r0]
    1b34:	e1a03b83 	mov	r3, r3, lsl #23
    1b38:	e1a03ba3 	mov	r3, r3, lsr #23
    1b3c:	e1520003 	cmp	r2, r3
    1b40:	ba0001ff 	blt	0x2344
    1b44:	e51f3300 	ldr	r3, [pc, #-768]	; 0x184c
    1b48:	e5930000 	ldr	r0, [r3]
    1b4c:	eb000526 	bl	0x2fec
    1b50:	eafffb51 	b	0x89c
    1b54:	e51f0308 	ldr	r0, [pc, #-776]	; 0x1854
    1b58:	e51f22f4 	ldr	r2, [pc, #-756]	; 0x186c
    1b5c:	e4d41001 	ldrb	r1, [r4], #1
    1b60:	e5903000 	ldr	r3, [r0]
    1b64:	e5920000 	ldr	r0, [r2]
    1b68:	e793c101 	ldr	ip, [r3, r1, lsl #2]
    1b6c:	e51f3308 	ldr	r3, [pc, #-776]	; 0x186c
    1b70:	e2802004 	add	r2, r0, #4	; 0x4
    1b74:	e5854000 	str	r4, [r5]
    1b78:	e5832000 	str	r2, [r3]
    1b7c:	e580c004 	str	ip, [r0, #4]
    1b80:	eafffb45 	b	0x89c
    1b84:	e51fc320 	ldr	ip, [pc, #-800]	; 0x186c
    1b88:	e59c0000 	ldr	r0, [ip]
    1b8c:	e1a03000 	mov	r3, r0
    1b90:	e4132004 	ldr	r2, [r3], #-4
    1b94:	e51fc358 	ldr	ip, [pc, #-856]	; 0x1844
    1b98:	e5101004 	ldr	r1, [r0, #-4]
    1b9c:	e51fe338 	ldr	lr, [pc, #-824]	; 0x186c
    1ba0:	e1cc20b0 	strh	r2, [ip]
    1ba4:	e51f2364 	ldr	r2, [pc, #-868]	; 0x1848
    1ba8:	e3510000 	cmp	r1, #0	; 0x0
    1bac:	e58e3000 	str	r3, [lr]
    1bb0:	e5821000 	str	r1, [r2]
    1bb4:	0a0001d6 	beq	0x2314
    1bb8:	e1dc20f0 	ldrsh	r2, [ip]
    1bbc:	e3520000 	cmp	r2, #0	; 0x0
    1bc0:	ba000004 	blt	0x1bd8
    1bc4:	e1d130b0 	ldrh	r3, [r1]
    1bc8:	e1a03b83 	mov	r3, r3, lsl #23
    1bcc:	e1a03ba3 	mov	r3, r3, lsr #23
    1bd0:	e1520003 	cmp	r2, r3
    1bd4:	ba0001d2 	blt	0x2324
    1bd8:	e51f3394 	ldr	r3, [pc, #-916]	; 0x184c
    1bdc:	e5930000 	ldr	r0, [r3]
    1be0:	eb000501 	bl	0x2fec
    1be4:	eafffb2c 	b	0x89c
    1be8:	e51fc384 	ldr	ip, [pc, #-900]	; 0x186c
    1bec:	e59c0000 	ldr	r0, [ip]
    1bf0:	e1a03000 	mov	r3, r0
    1bf4:	e4132004 	ldr	r2, [r3], #-4
    1bf8:	e51fc3bc 	ldr	ip, [pc, #-956]	; 0x1844
    1bfc:	e5101004 	ldr	r1, [r0, #-4]
    1c00:	e51fe39c 	ldr	lr, [pc, #-924]	; 0x186c
    1c04:	e1cc20b0 	strh	r2, [ip]
    1c08:	e51f23c8 	ldr	r2, [pc, #-968]	; 0x1848
    1c0c:	e3510000 	cmp	r1, #0	; 0x0
    1c10:	e58e3000 	str	r3, [lr]
    1c14:	e5821000 	str	r1, [r2]
    1c18:	0a0001b5 	beq	0x22f4
    1c1c:	e1dc20f0 	ldrsh	r2, [ip]
    1c20:	e3520000 	cmp	r2, #0	; 0x0
    1c24:	ba000004 	blt	0x1c3c
    1c28:	e1d130b0 	ldrh	r3, [r1]
    1c2c:	e1a03b83 	mov	r3, r3, lsl #23
    1c30:	e1a03ba3 	mov	r3, r3, lsr #23
    1c34:	e1520003 	cmp	r2, r3
    1c38:	ba0001b1 	blt	0x2304
    1c3c:	e51f33f8 	ldr	r3, [pc, #-1016]	; 0x184c
    1c40:	e5930000 	ldr	r0, [r3]
    1c44:	eb0004e8 	bl	0x2fec
    1c48:	eafffb13 	b	0x89c
    1c4c:	e51fe3e8 	ldr	lr, [pc, #-1000]	; 0x186c
    1c50:	e51fc404 	ldr	ip, [pc, #-1028]	; 0x1854
    1c54:	e59e2000 	ldr	r2, [lr]
    1c58:	e4d40001 	ldrb	r0, [r4], #1
    1c5c:	e4121004 	ldr	r1, [r2], #-4
    1c60:	e59c3000 	ldr	r3, [ip]
    1c64:	e5854000 	str	r4, [r5]
    1c68:	e7831100 	str	r1, [r3, r0, lsl #2]
    1c6c:	e58e2000 	str	r2, [lr]
    1c70:	eafffb09 	b	0x89c
    1c74:	e51f2428 	ldr	r2, [pc, #-1064]	; 0x1854
    1c78:	e51fc414 	ldr	ip, [pc, #-1044]	; 0x186c
    1c7c:	e4d41001 	ldrb	r1, [r4], #1
    1c80:	e5923000 	ldr	r3, [r2]
    1c84:	e59c0000 	ldr	r0, [ip]
    1c88:	e51fe424 	ldr	lr, [pc, #-1060]	; 0x186c
    1c8c:	e793c101 	ldr	ip, [r3, r1, lsl #2]
    1c90:	e2802004 	add	r2, r0, #4	; 0x4
    1c94:	e5854000 	str	r4, [r5]
    1c98:	e58e2000 	str	r2, [lr]
    1c9c:	e580c004 	str	ip, [r0, #4]
    1ca0:	eafffafd 	b	0x89c
    1ca4:	e51f0458 	ldr	r0, [pc, #-1112]	; 0x1854
    1ca8:	e5d4c000 	ldrb	ip, [r4]
    1cac:	e5902000 	ldr	r2, [r0]
    1cb0:	e51f144c 	ldr	r1, [pc, #-1100]	; 0x186c
    1cb4:	e5913000 	ldr	r3, [r1]
    1cb8:	e792110c 	ldr	r1, [r2, ip, lsl #2]
    1cbc:	e2830004 	add	r0, r3, #4	; 0x4
    1cc0:	e5831004 	str	r1, [r3, #4]
    1cc4:	e51f3460 	ldr	r3, [pc, #-1120]	; 0x186c
    1cc8:	e5951000 	ldr	r1, [r5]
    1ccc:	e5830000 	str	r0, [r3]
    1cd0:	e4d13001 	ldrb	r3, [r1], #1
    1cd4:	e0822103 	add	r2, r2, r3, lsl #2
    1cd8:	e592c004 	ldr	ip, [r2, #4]
    1cdc:	e51fe478 	ldr	lr, [pc, #-1144]	; 0x186c
    1ce0:	e2803004 	add	r3, r0, #4	; 0x4
    1ce4:	e58e3000 	str	r3, [lr]
    1ce8:	e580c004 	str	ip, [r0, #4]
    1cec:	e5851000 	str	r1, [r5]
    1cf0:	eafffae9 	b	0x89c
    1cf4:	e51f349c 	ldr	r3, [pc, #-1180]	; 0x1860
    1cf8:	e5931000 	ldr	r1, [r3]
    1cfc:	e5d42001 	ldrb	r2, [r4, #1]
    1d00:	e5d43000 	ldrb	r3, [r4]
    1d04:	e1d1c0b2 	ldrh	ip, [r1, #2]
    1d08:	e1822403 	orr	r2, r2, r3, lsl #8
    1d0c:	e08cc001 	add	ip, ip, r1
    1d10:	e1a02102 	mov	r2, r2, lsl #2
    1d14:	e51f34e0 	ldr	r3, [pc, #-1248]	; 0x183c
    1d18:	e19c40b2 	ldrh	r4, [ip, r2]
    1d1c:	e08cc002 	add	ip, ip, r2
    1d20:	e583c000 	str	ip, [r3]
    1d24:	e51f34e4 	ldr	r3, [pc, #-1252]	; 0x1848
    1d28:	e0844001 	add	r4, r4, r1
    1d2c:	e1a00004 	mov	r0, r4
    1d30:	e3a01004 	mov	r1, #4	; 0x4
    1d34:	e51f24d4 	ldr	r2, [pc, #-1236]	; 0x1868
    1d38:	e5834000 	str	r4, [r3]
    1d3c:	eb0005ab 	bl	0x33f0
    1d40:	e51fc4dc 	ldr	ip, [pc, #-1244]	; 0x186c
    1d44:	e51fe4e4 	ldr	lr, [pc, #-1252]	; 0x1868
    1d48:	e51f1508 	ldr	r1, [pc, #-1288]	; 0x1848
    1d4c:	e59c3000 	ldr	r3, [ip]
    1d50:	e59e2000 	ldr	r2, [lr]
    1d54:	e5910000 	ldr	r0, [r1]
    1d58:	e5832004 	str	r2, [r3, #4]
    1d5c:	e2833004 	add	r3, r3, #4	; 0x4
    1d60:	e58c3000 	str	r3, [ip]
    1d64:	e2800004 	add	r0, r0, #4	; 0x4
    1d68:	e3a01004 	mov	r1, #4	; 0x4
    1d6c:	e1a0200e 	mov	r2, lr
    1d70:	eb00059e 	bl	0x33f0
    1d74:	e51f2510 	ldr	r2, [pc, #-1296]	; 0x186c
    1d78:	e51f3518 	ldr	r3, [pc, #-1304]	; 0x1868
    1d7c:	e5921000 	ldr	r1, [r2]
    1d80:	e5952000 	ldr	r2, [r5]
    1d84:	e5930000 	ldr	r0, [r3]
    1d88:	e51fc524 	ldr	ip, [pc, #-1316]	; 0x186c
    1d8c:	e2822002 	add	r2, r2, #2	; 0x2
    1d90:	e2813004 	add	r3, r1, #4	; 0x4
    1d94:	e58c3000 	str	r3, [ip]
    1d98:	e5810004 	str	r0, [r1, #4]
    1d9c:	e5852000 	str	r2, [r5]
    1da0:	eafffabd 	b	0x89c
    1da4:	e51f2540 	ldr	r2, [pc, #-1344]	; 0x186c
    1da8:	e5923000 	ldr	r3, [r2]
    1dac:	e1a02003 	mov	r2, r3
    1db0:	e4921004 	ldr	r1, [r2], #4
    1db4:	e51fc550 	ldr	ip, [pc, #-1360]	; 0x186c
    1db8:	e51fe558 	ldr	lr, [pc, #-1368]	; 0x1868
    1dbc:	e58c2000 	str	r2, [ip]
    1dc0:	e58e1000 	str	r1, [lr]
    1dc4:	e5831004 	str	r1, [r3, #4]
    1dc8:	eafffab3 	b	0x89c
    1dcc:	e51fe568 	ldr	lr, [pc, #-1384]	; 0x186c
    1dd0:	e59e3000 	ldr	r3, [lr]
    1dd4:	e1a02003 	mov	r2, r3
    1dd8:	e412e004 	ldr	lr, [r2], #-4
    1ddc:	e51f057c 	ldr	r0, [pc, #-1404]	; 0x1868
    1de0:	e580e000 	str	lr, [r0]
    1de4:	e5131004 	ldr	r1, [r3, #-4]
    1de8:	e51f35ac 	ldr	r3, [pc, #-1452]	; 0x1844
    1dec:	e5120004 	ldr	r0, [r2, #-4]
    1df0:	e242c004 	sub	ip, r2, #4	; 0x4
    1df4:	e1c310b0 	strh	r1, [r3]
    1df8:	e51f25b8 	ldr	r2, [pc, #-1464]	; 0x1848
    1dfc:	e51f1598 	ldr	r1, [pc, #-1432]	; 0x186c
    1e00:	e3500000 	cmp	r0, #0	; 0x0
    1e04:	e581c000 	str	ip, [r1]
    1e08:	e5820000 	str	r0, [r2]
    1e0c:	0a000130 	beq	0x22d4
    1e10:	e1d320f0 	ldrsh	r2, [r3]
    1e14:	e3520000 	cmp	r2, #0	; 0x0
    1e18:	ba000004 	blt	0x1e30
    1e1c:	e1d030b0 	ldrh	r3, [r0]
    1e20:	e1a03b83 	mov	r3, r3, lsl #23
    1e24:	e1a03ba3 	mov	r3, r3, lsr #23
    1e28:	e1520003 	cmp	r2, r3
    1e2c:	ba000104 	blt	0x2244
    1e30:	e51f35ec 	ldr	r3, [pc, #-1516]	; 0x184c
    1e34:	e5930000 	ldr	r0, [r3]
    1e38:	eb00046b 	bl	0x2fec
    1e3c:	eafffa96 	b	0x89c
    1e40:	e51fe5dc 	ldr	lr, [pc, #-1500]	; 0x186c
    1e44:	e59e1000 	ldr	r1, [lr]
    1e48:	e2813004 	add	r3, r1, #4	; 0x4
    1e4c:	e5132004 	ldr	r2, [r3, #-4]
    1e50:	e5812004 	str	r2, [r1, #4]
    1e54:	e5130008 	ldr	r0, [r3, #-8]
    1e58:	e513c00c 	ldr	ip, [r3, #-12]
    1e5c:	e5030004 	str	r0, [r3, #-4]
    1e60:	e503c008 	str	ip, [r3, #-8]
    1e64:	e5912004 	ldr	r2, [r1, #4]
    1e68:	e58e3000 	str	r3, [lr]
    1e6c:	e503200c 	str	r2, [r3, #-12]
    1e70:	eafffa89 	b	0x89c
    1e74:	e51fe610 	ldr	lr, [pc, #-1552]	; 0x186c
    1e78:	e59e3000 	ldr	r3, [lr]
    1e7c:	e4132004 	ldr	r2, [r3], #-4
    1e80:	e51f0620 	ldr	r0, [pc, #-1568]	; 0x1868
    1e84:	e2433008 	sub	r3, r3, #8	; 0x8
    1e88:	e58e3000 	str	r3, [lr]
    1e8c:	e5802000 	str	r2, [r0]
    1e90:	eafffa81 	b	0x89c
    1e94:	e51fc630 	ldr	ip, [pc, #-1584]	; 0x186c
    1e98:	e59c3000 	ldr	r3, [ip]
    1e9c:	e0d410d1 	ldrsb	r1, [r4], #1
    1ea0:	e2832004 	add	r2, r3, #4	; 0x4
    1ea4:	e5854000 	str	r4, [r5]
    1ea8:	e58c2000 	str	r2, [ip]
    1eac:	e5831004 	str	r1, [r3, #4]
    1eb0:	eafffa79 	b	0x89c
    1eb4:	e5d42000 	ldrb	r2, [r4]
    1eb8:	e5d43001 	ldrb	r3, [r4, #1]
    1ebc:	e51fe658 	ldr	lr, [pc, #-1624]	; 0x186c
    1ec0:	e1833402 	orr	r3, r3, r2, lsl #8
    1ec4:	e59e0000 	ldr	r0, [lr]
    1ec8:	e5951000 	ldr	r1, [r5]
    1ecc:	e1a03803 	mov	r3, r3, lsl #16
    1ed0:	e1a03843 	mov	r3, r3, asr #16
    1ed4:	e2811002 	add	r1, r1, #2	; 0x2
    1ed8:	e2802004 	add	r2, r0, #4	; 0x4
    1edc:	e58e2000 	str	r2, [lr]
    1ee0:	e5803004 	str	r3, [r0, #4]
    1ee4:	e5851000 	str	r1, [r5]
    1ee8:	eafffa6b 	b	0x89c
    1eec:	e51fc688 	ldr	ip, [pc, #-1672]	; 0x186c
    1ef0:	e59c1000 	ldr	r1, [ip]
    1ef4:	e1a02001 	mov	r2, r1
    1ef8:	e4120004 	ldr	r0, [r2], #-4
    1efc:	e51fe69c 	ldr	lr, [pc, #-1692]	; 0x1868
    1f00:	e58e0000 	str	r0, [lr]
    1f04:	e5113004 	ldr	r3, [r1, #-4]
    1f08:	e00c0390 	mul	ip, r0, r3
    1f0c:	e51f06a8 	ldr	r0, [pc, #-1704]	; 0x186c
    1f10:	e501c004 	str	ip, [r1, #-4]
    1f14:	e5802000 	str	r2, [r0]
    1f18:	eafffa5f 	b	0x89c
    1f1c:	e51f16b8 	ldr	r1, [pc, #-1720]	; 0x186c
    1f20:	e5913000 	ldr	r3, [r1]
    1f24:	e5d41001 	ldrb	r1, [r4, #1]
    1f28:	e5930000 	ldr	r0, [r3]
    1f2c:	eb000a5c 	bl	0x48a4
    1f30:	e51fc6cc 	ldr	ip, [pc, #-1740]	; 0x186c
    1f34:	e5953000 	ldr	r3, [r5]
    1f38:	e59c2000 	ldr	r2, [ip]
    1f3c:	e2833002 	add	r3, r3, #2	; 0x2
    1f40:	e5820000 	str	r0, [r2]
    1f44:	e5853000 	str	r3, [r5]
    1f48:	eafffa53 	b	0x89c
    1f4c:	e51fc6e8 	ldr	ip, [pc, #-1768]	; 0x186c
    1f50:	e59c2000 	ldr	r2, [ip]
    1f54:	e51f371c 	ldr	r3, [pc, #-1820]	; 0x1840
    1f58:	e4121004 	ldr	r1, [r2], #-4
    1f5c:	e5930000 	ldr	r0, [r3]
    1f60:	e58c2000 	str	r2, [ip]
    1f64:	eb0001df 	bl	0x26e8
    1f68:	eafffa4b 	b	0x89c
    1f6c:	e51fe708 	ldr	lr, [pc, #-1800]	; 0x186c
    1f70:	e59e3000 	ldr	r3, [lr]
    1f74:	e5930000 	ldr	r0, [r3]
    1f78:	e51f1718 	ldr	r1, [pc, #-1816]	; 0x1868
    1f7c:	e2843001 	add	r3, r4, #1	; 0x1
    1f80:	e3500000 	cmp	r0, #0	; 0x0
    1f84:	e5853000 	str	r3, [r5]
    1f88:	e5810000 	str	r0, [r1]
    1f8c:	1a0000b6 	bne	0x226c
    1f90:	e5953000 	ldr	r3, [r5]
    1f94:	e2833001 	add	r3, r3, #1	; 0x1
    1f98:	e5853000 	str	r3, [r5]
    1f9c:	eafffa3e 	b	0x89c
    1fa0:	e51f173c 	ldr	r1, [pc, #-1852]	; 0x186c
    1fa4:	e5912000 	ldr	r2, [r1]
    1fa8:	e5923000 	ldr	r3, [r2]
    1fac:	e2633000 	rsb	r3, r3, #0	; 0x0
    1fb0:	e5823000 	str	r3, [r2]
    1fb4:	eafffa38 	b	0x89c
    1fb8:	e51fe754 	ldr	lr, [pc, #-1876]	; 0x186c
    1fbc:	e59e3000 	ldr	r3, [lr]
    1fc0:	e51f0760 	ldr	r0, [pc, #-1888]	; 0x1868
    1fc4:	e5135004 	ldr	r5, [r3, #-4]
    1fc8:	e4134004 	ldr	r4, [r3], #-4
    1fcc:	e5805000 	str	r5, [r0]
    1fd0:	e2432004 	sub	r2, r3, #4	; 0x4
    1fd4:	e5131004 	ldr	r1, [r3, #-4]
    1fd8:	e51fe79c 	ldr	lr, [pc, #-1948]	; 0x1844
    1fdc:	e512c004 	ldr	ip, [r2, #-4]
    1fe0:	e2420004 	sub	r0, r2, #4	; 0x4
    1fe4:	e1ce10b0 	strh	r1, [lr]
    1fe8:	e51f27a8 	ldr	r2, [pc, #-1960]	; 0x1848
    1fec:	e51f1788 	ldr	r1, [pc, #-1928]	; 0x186c
    1ff0:	e35c0000 	cmp	ip, #0	; 0x0
    1ff4:	e5810000 	str	r0, [r1]
    1ff8:	e582c000 	str	ip, [r2]
    1ffc:	0a0000a2 	beq	0x228c
    2000:	e1de20f0 	ldrsh	r2, [lr]
    2004:	e3520000 	cmp	r2, #0	; 0x0
    2008:	ba000004 	blt	0x2020
    200c:	e1dc30b0 	ldrh	r3, [ip]
    2010:	e1a03b83 	mov	r3, r3, lsl #23
    2014:	e1a03ba3 	mov	r3, r3, lsr #23
    2018:	e1520003 	cmp	r2, r3
    201c:	ba00009e 	blt	0x229c
    2020:	e51f37dc 	ldr	r3, [pc, #-2012]	; 0x184c
    2024:	e5930000 	ldr	r0, [r3]
    2028:	eb0003ef 	bl	0x2fec
    202c:	eafffa1a 	b	0x89c
    2030:	e51fc7e4 	ldr	ip, [pc, #-2020]	; 0x1854
    2034:	e5542001 	ldrb	r2, [r4, #-1]
    2038:	e51fe7d4 	ldr	lr, [pc, #-2004]	; 0x186c
    203c:	e59c3000 	ldr	r3, [ip]
    2040:	e59e1000 	ldr	r1, [lr]
    2044:	e0833102 	add	r3, r3, r2, lsl #2
    2048:	e5130068 	ldr	r0, [r3, #-104]
    204c:	e2812004 	add	r2, r1, #4	; 0x4
    2050:	e58e2000 	str	r2, [lr]
    2054:	e5810004 	str	r0, [r1, #4]
    2058:	eafffa0f 	b	0x89c
    205c:	e5543001 	ldrb	r3, [r4, #-1]
    2060:	e51f4818 	ldr	r4, [pc, #-2072]	; 0x1850
    2064:	e243301e 	sub	r3, r3, #30	; 0x1e
    2068:	e5c43000 	strb	r3, [r4]
    206c:	eafffdd4 	b	0x17c4
    2070:	e51f0824 	ldr	r0, [pc, #-2084]	; 0x1854
    2074:	e5542001 	ldrb	r2, [r4, #-1]
    2078:	e5903000 	ldr	r3, [r0]
    207c:	e0833102 	add	r3, r3, r2, lsl #2
    2080:	e51f281c 	ldr	r2, [pc, #-2076]	; 0x186c
    2084:	e5921000 	ldr	r1, [r2]
    2088:	e5130088 	ldr	r0, [r3, #-136]
    208c:	e51f3828 	ldr	r3, [pc, #-2088]	; 0x186c
    2090:	e2812004 	add	r2, r1, #4	; 0x4
    2094:	e5832000 	str	r2, [r3]
    2098:	e5810004 	str	r0, [r1, #4]
    209c:	eafff9fe 	b	0x89c
    20a0:	e51fc83c 	ldr	ip, [pc, #-2108]	; 0x186c
    20a4:	e59c1000 	ldr	r1, [ip]
    20a8:	e1a02001 	mov	r2, r1
    20ac:	e4120004 	ldr	r0, [r2], #-4
    20b0:	e51fe850 	ldr	lr, [pc, #-2128]	; 0x1868
    20b4:	e58e0000 	str	r0, [lr]
    20b8:	e5113004 	ldr	r3, [r1, #-4]
    20bc:	e0033000 	and	r3, r3, r0
    20c0:	e58c2000 	str	r2, [ip]
    20c4:	e5013004 	str	r3, [r1, #-4]
    20c8:	eafff9f3 	b	0x89c
    20cc:	e51f0868 	ldr	r0, [pc, #-2152]	; 0x186c
    20d0:	e5901000 	ldr	r1, [r0]
    20d4:	e1a02001 	mov	r2, r1
    20d8:	e4120004 	ldr	r0, [r2], #-4
    20dc:	e51f387c 	ldr	r3, [pc, #-2172]	; 0x1868
    20e0:	e5830000 	str	r0, [r3]
    20e4:	e5113004 	ldr	r3, [r1, #-4]
    20e8:	e51fc884 	ldr	ip, [pc, #-2180]	; 0x186c
    20ec:	e1833000 	orr	r3, r3, r0
    20f0:	e58c2000 	str	r2, [ip]
    20f4:	e5013004 	str	r3, [r1, #-4]
    20f8:	eafff9e7 	b	0x89c
    20fc:	e51fe898 	ldr	lr, [pc, #-2200]	; 0x186c
    2100:	e59e1000 	ldr	r1, [lr]
    2104:	e1a02001 	mov	r2, r1
    2108:	e4120004 	ldr	r0, [r2], #-4
    210c:	e51f38ac 	ldr	r3, [pc, #-2220]	; 0x1868
    2110:	e5830000 	str	r0, [r3]
    2114:	e5113004 	ldr	r3, [r1, #-4]
    2118:	e0233000 	eor	r3, r3, r0
    211c:	e58e2000 	str	r2, [lr]
    2120:	e5013004 	str	r3, [r1, #-4]
    2124:	eafff9dc 	b	0x89c
    2128:	e51f28dc 	ldr	r2, [pc, #-2268]	; 0x1854
    212c:	e5d40000 	ldrb	r0, [r4]
    2130:	e592c000 	ldr	ip, [r2]
    2134:	e1d410d1 	ldrsb	r1, [r4, #1]
    2138:	e79c3100 	ldr	r3, [ip, r0, lsl #2]
    213c:	e5952000 	ldr	r2, [r5]
    2140:	e0833001 	add	r3, r3, r1
    2144:	e2822002 	add	r2, r2, #2	; 0x2
    2148:	e78c3100 	str	r3, [ip, r0, lsl #2]
    214c:	e5852000 	str	r2, [r5]
    2150:	eafff9d1 	b	0x89c
    2154:	e51fc8f0 	ldr	ip, [pc, #-2288]	; 0x186c
    2158:	e59c0000 	ldr	r0, [ip]
    215c:	e1a01000 	mov	r1, r0
    2160:	e4113004 	ldr	r3, [r1], #-4
    2164:	e51fe904 	ldr	lr, [pc, #-2308]	; 0x1868
    2168:	e58e3000 	str	r3, [lr]
    216c:	e5102004 	ldr	r2, [r0, #-4]
    2170:	e203301f 	and	r3, r3, #31	; 0x1f
    2174:	e1a02352 	mov	r2, r2, asr r3
    2178:	e58c1000 	str	r1, [ip]
    217c:	e5002004 	str	r2, [r0, #-4]
    2180:	eafff9c5 	b	0x89c
    2184:	e51f1920 	ldr	r1, [pc, #-2336]	; 0x186c
    2188:	e5910000 	ldr	r0, [r1]
    218c:	e1a01000 	mov	r1, r0
    2190:	e4113004 	ldr	r3, [r1], #-4
    2194:	e51f2934 	ldr	r2, [pc, #-2356]	; 0x1868
    2198:	e5823000 	str	r3, [r2]
    219c:	e5102004 	ldr	r2, [r0, #-4]
    21a0:	e203301f 	and	r3, r3, #31	; 0x1f
    21a4:	e1a02332 	mov	r2, r2, lsr r3
    21a8:	e51f3944 	ldr	r3, [pc, #-2372]	; 0x186c
    21ac:	e5002004 	str	r2, [r0, #-4]
    21b0:	e5831000 	str	r1, [r3]
    21b4:	eafff9b8 	b	0x89c
    21b8:	e51f1954 	ldr	r1, [pc, #-2388]	; 0x186c
    21bc:	e5910000 	ldr	r0, [r1]
    21c0:	e1a01000 	mov	r1, r0
    21c4:	e4113004 	ldr	r3, [r1], #-4
    21c8:	e51f2968 	ldr	r2, [pc, #-2408]	; 0x1868
    21cc:	e5823000 	str	r3, [r2]
    21d0:	e5102004 	ldr	r2, [r0, #-4]
    21d4:	e203301f 	and	r3, r3, #31	; 0x1f
    21d8:	e1a02312 	mov	r2, r2, lsl r3
    21dc:	e51f3978 	ldr	r3, [pc, #-2424]	; 0x186c
    21e0:	e5002004 	str	r2, [r0, #-4]
    21e4:	e5831000 	str	r1, [r3]
    21e8:	eafff9ab 	b	0x89c
    21ec:	e51fe988 	ldr	lr, [pc, #-2440]	; 0x186c
    21f0:	e59e3000 	ldr	r3, [lr]
    21f4:	e3a02008 	mov	r2, #8	; 0x8
    21f8:	e3a04004 	mov	r4, #4	; 0x4
    21fc:	eafffc1c 	b	0x1274
    2200:	e51fc9a8 	ldr	ip, [pc, #-2472]	; 0x1860
    2204:	e1d630b0 	ldrh	r3, [r6]
    2208:	e59c0000 	ldr	r0, [ip]
    220c:	e3a01004 	mov	r1, #4	; 0x4
    2210:	e0830000 	add	r0, r3, r0
    2214:	e51f29b4 	ldr	r2, [pc, #-2484]	; 0x1868
    2218:	eb000474 	bl	0x33f0
    221c:	e51fe9b8 	ldr	lr, [pc, #-2488]	; 0x186c
    2220:	e51f09c0 	ldr	r0, [pc, #-2496]	; 0x1868
    2224:	e59e2000 	ldr	r2, [lr]
    2228:	e5901000 	ldr	r1, [r0]
    222c:	e2823004 	add	r3, r2, #4	; 0x4
    2230:	e58e3000 	str	r3, [lr]
    2234:	e5821004 	str	r1, [r2, #4]
    2238:	eafff997 	b	0x89c
    223c:	eb00036a 	bl	0x2fec
    2240:	eafff995 	b	0x89c
    2244:	e24c3004 	sub	r3, ip, #4	; 0x4
    2248:	e51fc9e4 	ldr	ip, [pc, #-2532]	; 0x186c
    224c:	e0822000 	add	r2, r2, r0
    2250:	e58c3000 	str	r3, [ip]
    2254:	e5c2e008 	strb	lr, [r2, #8]
    2258:	eafff98f 	b	0x89c
    225c:	e51f2a08 	ldr	r2, [pc, #-2568]	; 0x185c
    2260:	e5920000 	ldr	r0, [r2]
    2264:	eb000360 	bl	0x2fec
    2268:	eafff98b 	b	0x89c
    226c:	e5d41001 	ldrb	r1, [r4, #1]
    2270:	eb00098b 	bl	0x48a4
    2274:	e3500000 	cmp	r0, #0	; 0x0
    2278:	1affff44 	bne	0x1f90
    227c:	e51f3a2c 	ldr	r3, [pc, #-2604]	; 0x1858
    2280:	e5930000 	ldr	r0, [r3]
    2284:	eb000358 	bl	0x2fec
    2288:	eaffff40 	b	0x1f90
    228c:	e51f3a38 	ldr	r3, [pc, #-2616]	; 0x185c
    2290:	e5930000 	ldr	r0, [r3]
    2294:	eb000354 	bl	0x2fec
    2298:	eafff97f 	b	0x89c
    229c:	e1de30f0 	ldrsh	r3, [lr]
    22a0:	e1a03883 	mov	r3, r3, lsl #17
    22a4:	e2831801 	add	r1, r3, #65536	; 0x10000
    22a8:	e1a01821 	mov	r1, r1, lsr #16
    22ac:	e1a02801 	mov	r2, r1, lsl #16
    22b0:	e08c2742 	add	r2, ip, r2, asr #14
    22b4:	e08c3743 	add	r3, ip, r3, asr #14
    22b8:	e51fca54 	ldr	ip, [pc, #-2644]	; 0x186c
    22bc:	e2400004 	sub	r0, r0, #4	; 0x4
    22c0:	e5835008 	str	r5, [r3, #8]
    22c4:	e58c0000 	str	r0, [ip]
    22c8:	e5824008 	str	r4, [r2, #8]
    22cc:	e1ce10b0 	strh	r1, [lr]
    22d0:	eafff971 	b	0x89c
    22d4:	e51f3a80 	ldr	r3, [pc, #-2688]	; 0x185c
    22d8:	e5930000 	ldr	r0, [r3]
    22dc:	eb000342 	bl	0x2fec
    22e0:	eafff96d 	b	0x89c
    22e4:	e51f1a90 	ldr	r1, [pc, #-2704]	; 0x185c
    22e8:	e5910000 	ldr	r0, [r1]
    22ec:	eb00033e 	bl	0x2fec
    22f0:	eafff969 	b	0x89c
    22f4:	e51f3aa0 	ldr	r3, [pc, #-2720]	; 0x185c
    22f8:	e5930000 	ldr	r0, [r3]
    22fc:	eb00033a 	bl	0x2fec
    2300:	eafff965 	b	0x89c
    2304:	e0813082 	add	r3, r1, r2, lsl #1
    2308:	e1d320f8 	ldrsh	r2, [r3, #8]
    230c:	e5002004 	str	r2, [r0, #-4]
    2310:	eafff961 	b	0x89c
    2314:	e51f3ac0 	ldr	r3, [pc, #-2752]	; 0x185c
    2318:	e5930000 	ldr	r0, [r3]
    231c:	eb000332 	bl	0x2fec
    2320:	eafff95d 	b	0x89c
    2324:	e0823001 	add	r3, r2, r1
    2328:	e1d320d8 	ldrsb	r2, [r3, #8]
    232c:	e5002004 	str	r2, [r0, #-4]
    2330:	eafff959 	b	0x89c
    2334:	e51f3ae0 	ldr	r3, [pc, #-2784]	; 0x185c
    2338:	e5930000 	ldr	r0, [r3]
    233c:	eb00032a 	bl	0x2fec
    2340:	eafff955 	b	0x89c
    2344:	e24c3004 	sub	r3, ip, #4	; 0x4
    2348:	e51fcae4 	ldr	ip, [pc, #-2788]	; 0x186c
    234c:	e0802102 	add	r2, r0, r2, lsl #2
    2350:	e58c3000 	str	r3, [ip]
    2354:	e582e008 	str	lr, [r2, #8]
    2358:	eafff94f 	b	0x89c
    235c:	e51f3b08 	ldr	r3, [pc, #-2824]	; 0x185c
    2360:	e5930000 	ldr	r0, [r3]
    2364:	eb000320 	bl	0x2fec
    2368:	eafff94b 	b	0x89c
    236c:	e24c3004 	sub	r3, ip, #4	; 0x4
    2370:	e51fcb0c 	ldr	ip, [pc, #-2828]	; 0x186c
    2374:	e0802082 	add	r2, r0, r2, lsl #1
    2378:	e58c3000 	str	r3, [ip]
    237c:	e1c2e0b8 	strh	lr, [r2, #8]
    2380:	eafff945 	b	0x89c
    2384:	e51f3b30 	ldr	r3, [pc, #-2864]	; 0x185c
    2388:	e5930000 	ldr	r0, [r3]
    238c:	eb000316 	bl	0x2fec
    2390:	eafff941 	b	0x89c
    2394:	e51f3b40 	ldr	r3, [pc, #-2880]	; 0x185c
    2398:	e5930000 	ldr	r0, [r3]
    239c:	eb000312 	bl	0x2fec
    23a0:	eafff93d 	b	0x89c
    23a4:	e51f1b50 	ldr	r1, [pc, #-2896]	; 0x185c
    23a8:	e5910000 	ldr	r0, [r1]
    23ac:	eb00030e 	bl	0x2fec
    23b0:	eafff939 	b	0x89c
    23b4:	e51f3b60 	ldr	r3, [pc, #-2912]	; 0x185c
    23b8:	e5930000 	ldr	r0, [r3]
    23bc:	eb00030a 	bl	0x2fec
    23c0:	eafff935 	b	0x89c
    23c4:	e1a02882 	mov	r2, r2, lsl #17
    23c8:	e1a03822 	mov	r3, r2, lsr #16
    23cc:	e2833001 	add	r3, r3, #1	; 0x1
    23d0:	e08c2742 	add	r2, ip, r2, asr #14
    23d4:	e1ce30b0 	strh	r3, [lr]
    23d8:	e5921008 	ldr	r1, [r2, #8]
    23dc:	e1de30f0 	ldrsh	r3, [lr]
    23e0:	e5001004 	str	r1, [r0, #-4]
    23e4:	e08c3103 	add	r3, ip, r3, lsl #2
    23e8:	e5932008 	ldr	r2, [r3, #8]
    23ec:	e51fcb88 	ldr	ip, [pc, #-2952]	; 0x186c
    23f0:	e5802000 	str	r2, [r0]
    23f4:	e58c0000 	str	r0, [ip]
    23f8:	eafff927 	b	0x89c
    23fc:	e3a01004 	mov	r1, #4	; 0x4
    2400:	e1a02009 	mov	r2, r9
    2404:	e2860004 	add	r0, r6, #4	; 0x4
    2408:	eb0003f8 	bl	0x33f0
    240c:	e51fcba8 	ldr	ip, [pc, #-2984]	; 0x186c
    2410:	e59c3000 	ldr	r3, [ip]
    2414:	e5991000 	ldr	r1, [r9]
    2418:	e2832004 	add	r2, r3, #4	; 0x4
    241c:	e58c2000 	str	r2, [ip]
    2420:	e5831004 	str	r1, [r3, #4]
    2424:	eafffb83 	b	0x1238
    2428:	e51f2bd0 	ldr	r2, [pc, #-3024]	; 0x1860
    242c:	e5923000 	ldr	r3, [r2]
    2430:	e5d32025 	ldrb	r2, [r3, #37]
    2434:	e5d31024 	ldrb	r1, [r3, #36]
    2438:	e1a00004 	mov	r0, r4
    243c:	e1811402 	orr	r1, r1, r2, lsl #8
    2440:	eb000427 	bl	0x34e4
    2444:	e51f3be8 	ldr	r3, [pc, #-3048]	; 0x1864
    2448:	e3a02000 	mov	r2, #0	; 0x0
    244c:	e5832000 	str	r2, [r3]
    2450:	eafff911 	b	0x89c
    2454:	e51fcbf0 	ldr	ip, [pc, #-3056]	; 0x186c
    2458:	e59c3000 	ldr	r3, [ip]
    245c:	e4132004 	ldr	r2, [r3], #-4
    2460:	e2850004 	add	r0, r5, #4	; 0x4
    2464:	e3a01004 	mov	r1, #4	; 0x4
    2468:	e58c3000 	str	r3, [ip]
    246c:	eb0003d2 	bl	0x33bc
    2470:	eafffb8e 	b	0x12b0
    2474:	e51f6c14 	ldr	r6, [pc, #-3092]	; 0x1868
    2478:	e1a01009 	mov	r1, r9
    247c:	e1a00004 	mov	r0, r4
    2480:	e1a02006 	mov	r2, r6
    2484:	eb0003d9 	bl	0x33f0
    2488:	e51f0c24 	ldr	r0, [pc, #-3108]	; 0x186c
    248c:	e51b202c 	ldr	r2, [fp, #-44]
    2490:	e5903000 	ldr	r3, [r0]
    2494:	e5961000 	ldr	r1, [r6]
    2498:	e3520000 	cmp	r2, #0	; 0x0
    249c:	e2832004 	add	r2, r3, #4	; 0x4
    24a0:	e5831004 	str	r1, [r3, #4]
    24a4:	e5802000 	str	r2, [r0]
    24a8:	0afffb41 	beq	0x11b4
    24ac:	e3a01004 	mov	r1, #4	; 0x4
    24b0:	e1a02006 	mov	r2, r6
    24b4:	e2840004 	add	r0, r4, #4	; 0x4
    24b8:	eb0003cc 	bl	0x33f0
    24bc:	e51fcc58 	ldr	ip, [pc, #-3160]	; 0x186c
    24c0:	e59c3000 	ldr	r3, [ip]
    24c4:	e5961000 	ldr	r1, [r6]
    24c8:	e2832004 	add	r2, r3, #4	; 0x4
    24cc:	e58c2000 	str	r2, [ip]
    24d0:	e5831004 	str	r1, [r3, #4]
    24d4:	eafffb36 	b	0x11b4
    24d8:	e59f2020 	ldr	r2, [pc, #32]	; 0x2500
    24dc:	e5922000 	ldr	r2, [r2]
    24e0:	e5d2301c 	ldrb	r3, [r2, #28]
    24e4:	e3530000 	cmp	r3, #0	; 0x0
    24e8:	e1a00003 	mov	r0, r3
    24ec:	e0831103 	add	r1, r3, r3, lsl #2
    24f0:	15923014 	ldrne	r3, [r2, #20]
    24f4:	10833101 	addne	r3, r3, r1, lsl #2
    24f8:	1243000c 	subne	r0, r3, #12	; 0xc
    24fc:	e12fff1e 	bx	lr
    2500:	00207978 	eoreq	r7, r0, r8, ror r9
    2504:	e59f3014 	ldr	r3, [pc, #20]	; 0x2520
    2508:	e59f2014 	ldr	r2, [pc, #20]	; 0x2524
    250c:	e5931000 	ldr	r1, [r3]
    2510:	e5923000 	ldr	r3, [r2]
    2514:	e580300c 	str	r3, [r0, #12]
    2518:	e5801010 	str	r1, [r0, #16]
    251c:	e12fff1e 	bx	lr
    2520:	00207928 	eoreq	r7, r0, r8, lsr #18
    2524:	00207944 	eoreq	r7, r0, r4, asr #18
    2528:	e52de004 	str	lr, [sp, #-4]!
    252c:	e59f3024 	ldr	r3, [pc, #36]	; 0x2558
    2530:	e590100c 	ldr	r1, [r0, #12]
    2534:	e590e008 	ldr	lr, [r0, #8]
    2538:	e590c010 	ldr	ip, [r0, #16]
    253c:	e5831000 	str	r1, [r3]
    2540:	e59f2014 	ldr	r2, [pc, #20]	; 0x255c
    2544:	e59f3014 	ldr	r3, [pc, #20]	; 0x2560
    2548:	e582c000 	str	ip, [r2]
    254c:	e583e000 	str	lr, [r3]
    2550:	e49de004 	ldr	lr, [sp], #4
    2554:	e12fff1e 	bx	lr
    2558:	00207944 	eoreq	r7, r0, r4, asr #18
    255c:	00207928 	eoreq	r7, r0, r8, lsr #18
    2560:	00207940 	eoreq	r7, r0, r0, asr #18
    2564:	e92d4030 	stmdb	sp!, {r4, r5, lr}
    2568:	e59fe0c0 	ldr	lr, [pc, #192]	; 0x2630
    256c:	e20150ff 	and	r5, r1, #255	; 0xff
    2570:	e59ec024 	ldr	ip, [lr, #36]
    2574:	e35c0000 	cmp	ip, #0	; 0x0
    2578:	e1a0200c 	mov	r2, ip
    257c:	0a000014 	beq	0x25d4
    2580:	e3550000 	cmp	r5, #0	; 0x0
    2584:	0a000018 	beq	0x25ec
    2588:	e3a01004 	mov	r1, #4	; 0x4
    258c:	e3a04002 	mov	r4, #2	; 0x2
    2590:	ea000001 	b	0x259c
    2594:	e152000c 	cmp	r2, ip
    2598:	0a00000d 	beq	0x25d4
    259c:	e5922008 	ldr	r2, [r2, #8]
    25a0:	e1d231df 	ldrsb	r3, [r2, #31]
    25a4:	e3530005 	cmp	r3, #5	; 0x5
    25a8:	1afffff9 	bne	0x2594
    25ac:	e592300c 	ldr	r3, [r2, #12]
    25b0:	e1530000 	cmp	r3, r0
    25b4:	1afffff6 	bne	0x2594
    25b8:	e1d232d1 	ldrsb	r3, [r2, #33]
    25bc:	e3530000 	cmp	r3, #0	; 0x0
    25c0:	15c24021 	strneb	r4, [r2, #33]
    25c4:	e5c2101f 	strb	r1, [r2, #31]
    25c8:	e59ec024 	ldr	ip, [lr, #36]
    25cc:	e152000c 	cmp	r2, ip
    25d0:	1afffff1 	bne	0x259c
    25d4:	e59f3058 	ldr	r3, [pc, #88]	; 0x2634
    25d8:	e24ee004 	sub	lr, lr, #4	; 0x4
    25dc:	e15e0003 	cmp	lr, r3
    25e0:	1affffe2 	bne	0x2570
    25e4:	e8bd4030 	ldmia	sp!, {r4, r5, lr}
    25e8:	e12fff1e 	bx	lr
    25ec:	e5922008 	ldr	r2, [r2, #8]
    25f0:	e1d231df 	ldrsb	r3, [r2, #31]
    25f4:	e3530005 	cmp	r3, #5	; 0x5
    25f8:	0a000002 	beq	0x2608
    25fc:	e152000c 	cmp	r2, ip
    2600:	1afffff9 	bne	0x25ec
    2604:	eafffff2 	b	0x25d4
    2608:	e592300c 	ldr	r3, [r2, #12]
    260c:	e1530000 	cmp	r3, r0
    2610:	1afffff9 	bne	0x25fc
    2614:	e1d232d1 	ldrsb	r3, [r2, #33]
    2618:	e3530000 	cmp	r3, #0	; 0x0
    261c:	13a03002 	movne	r3, #2	; 0x2
    2620:	15c23021 	strneb	r3, [r2, #33]
    2624:	e3a03004 	mov	r3, #4	; 0x4
    2628:	e5c2301f 	strb	r3, [r2, #31]
    262c:	eaffffec 	b	0x25e4
    2630:	00207950 	eoreq	r7, r0, r0, asr r9
    2634:	00207928 	eoreq	r7, r0, r8, lsr #18
    2638:	e3510000 	cmp	r1, #0	; 0x0
    263c:	012fff1e 	bxeq	lr
    2640:	e5d13004 	ldrb	r3, [r1, #4]
    2644:	e2433001 	sub	r3, r3, #1	; 0x1
    2648:	e21330ff 	ands	r3, r3, #255	; 0xff
    264c:	05c13005 	streqb	r3, [r1, #5]
    2650:	e5c13004 	strb	r3, [r1, #4]
    2654:	e12fff1e 	bx	lr
    2658:	e12fff1e 	bx	lr
    265c:	e5d03020 	ldrb	r3, [r0, #32]
    2660:	e2433001 	sub	r3, r3, #1	; 0x1
    2664:	e59f2044 	ldr	r2, [pc, #68]	; 0x26b0
    2668:	e20330ff 	and	r3, r3, #255	; 0xff
    266c:	e1a03103 	mov	r3, r3, lsl #2
    2670:	e793c002 	ldr	ip, [r3, r2]
    2674:	e59c1008 	ldr	r1, [ip, #8]
    2678:	e1500001 	cmp	r0, r1
    267c:	e0832002 	add	r2, r3, r2
    2680:	0a000003 	beq	0x2694
    2684:	e1a0c001 	mov	ip, r1
    2688:	e5911008 	ldr	r1, [r1, #8]
    268c:	e1500001 	cmp	r0, r1
    2690:	1afffffb 	bne	0x2684
    2694:	e150000c 	cmp	r0, ip
    2698:	15903008 	ldrne	r3, [r0, #8]
    269c:	03a03000 	moveq	r3, #0	; 0x0
    26a0:	05823000 	streq	r3, [r2]
    26a4:	1582c000 	strne	ip, [r2]
    26a8:	158c3008 	strne	r3, [ip, #8]
    26ac:	e12fff1e 	bx	lr
    26b0:	00207950 	eoreq	r7, r0, r0, asr r9
    26b4:	e5d03020 	ldrb	r3, [r0, #32]
    26b8:	e59f2024 	ldr	r2, [pc, #36]	; 0x26e4
    26bc:	e2433001 	sub	r3, r3, #1	; 0x1
    26c0:	e20330ff 	and	r3, r3, #255	; 0xff
    26c4:	e7921103 	ldr	r1, [r2, r3, lsl #2]
    26c8:	e3510000 	cmp	r1, #0	; 0x0
    26cc:	e7820103 	str	r0, [r2, r3, lsl #2]
    26d0:	15913008 	ldrne	r3, [r1, #8]
    26d4:	15803008 	strne	r3, [r0, #8]
    26d8:	05800008 	streq	r0, [r0, #8]
    26dc:	15810008 	strne	r0, [r1, #8]
    26e0:	e12fff1e 	bx	lr
    26e4:	00207950 	eoreq	r7, r0, r0, asr r9
    26e8:	e1a0c00d 	mov	ip, sp
    26ec:	e3510000 	cmp	r1, #0	; 0x0
    26f0:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
    26f4:	e24cb004 	sub	fp, ip, #4	; 0x4
    26f8:	0a000017 	beq	0x275c
    26fc:	e5d13004 	ldrb	r3, [r1, #4]
    2700:	e3530000 	cmp	r3, #0	; 0x0
    2704:	e283e001 	add	lr, r3, #1	; 0x1
    2708:	1a000005 	bne	0x2724
    270c:	e5d0301e 	ldrb	r3, [r0, #30]
    2710:	e5c1e004 	strb	lr, [r1, #4]
    2714:	e5c13005 	strb	r3, [r1, #5]
    2718:	e24bd00c 	sub	sp, fp, #12	; 0xc
    271c:	e89d6800 	ldmia	sp, {fp, sp, lr}
    2720:	e12fff1e 	bx	lr
    2724:	e5d12005 	ldrb	r2, [r1, #5]
    2728:	e1d031de 	ldrsb	r3, [r0, #30]
    272c:	e1530002 	cmp	r3, r2
    2730:	e3a0c001 	mov	ip, #1	; 0x1
    2734:	0afffff4 	beq	0x270c
    2738:	e3a03004 	mov	r3, #4	; 0x4
    273c:	e5c0301f 	strb	r3, [r0, #31]
    2740:	e59f2024 	ldr	r2, [pc, #36]	; 0x276c
    2744:	e59f3024 	ldr	r3, [pc, #36]	; 0x2770
    2748:	e580100c 	str	r1, [r0, #12]
    274c:	e5c0c01d 	strb	ip, [r0, #29]
    2750:	e5c3c000 	strb	ip, [r3]
    2754:	e5c2c000 	strb	ip, [r2]
    2758:	eaffffee 	b	0x2718
    275c:	e59f3010 	ldr	r3, [pc, #16]	; 0x2774
    2760:	e5930000 	ldr	r0, [r3]
    2764:	eb000220 	bl	0x2fec
    2768:	eaffffea 	b	0x2718
    276c:	0020792f 	eoreq	r7, r0, pc, lsr #18
    2770:	0020792e 	eoreq	r7, r0, lr, lsr #18
    2774:	0020798c 	eoreq	r7, r0, ip, lsl #19
    2778:	e1a0c00d 	mov	ip, sp
    277c:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    2780:	e59fe090 	ldr	lr, [pc, #144]	; 0x2818
    2784:	e24cb004 	sub	fp, ip, #4	; 0x4
    2788:	e59ec000 	ldr	ip, [lr]
    278c:	e5d02005 	ldrb	r2, [r0, #5]
    2790:	e1dc31de 	ldrsb	r3, [ip, #30]
    2794:	e1530002 	cmp	r3, r2
    2798:	e1a05000 	mov	r5, r0
    279c:	e1a06001 	mov	r6, r1
    27a0:	0a000005 	beq	0x27bc
    27a4:	e59f3070 	ldr	r3, [pc, #112]	; 0x281c
    27a8:	e5930000 	ldr	r0, [r3]
    27ac:	eb00020e 	bl	0x2fec
    27b0:	e24bd018 	sub	sp, fp, #24	; 0x18
    27b4:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    27b8:	e12fff1e 	bx	lr
    27bc:	e3a03005 	mov	r3, #5	; 0x5
    27c0:	e5cc301f 	strb	r3, [ip, #31]
    27c4:	e5d02004 	ldrb	r2, [r0, #4]
    27c8:	e59e3000 	ldr	r3, [lr]
    27cc:	e5c3201d 	strb	r2, [r3, #29]
    27d0:	e59e4000 	ldr	r4, [lr]
    27d4:	e3510000 	cmp	r1, #0	; 0x0
    27d8:	e584000c 	str	r0, [r4, #12]
    27dc:	05841010 	streq	r1, [r4, #16]
    27e0:	1a000008 	bne	0x2808
    27e4:	e3a02000 	mov	r2, #0	; 0x0
    27e8:	e5c52004 	strb	r2, [r5, #4]
    27ec:	e5c52005 	strb	r2, [r5, #5]
    27f0:	e59f3028 	ldr	r3, [pc, #40]	; 0x2820
    27f4:	e59f2028 	ldr	r2, [pc, #40]	; 0x2824
    27f8:	e3a01001 	mov	r1, #1	; 0x1
    27fc:	e5c31000 	strb	r1, [r3]
    2800:	e5c21000 	strb	r1, [r2]
    2804:	eaffffe9 	b	0x27b0
    2808:	ebfff600 	bl	0x10
    280c:	e0860000 	add	r0, r6, r0
    2810:	e5840010 	str	r0, [r4, #16]
    2814:	eafffff2 	b	0x27e4
    2818:	00207978 	eoreq	r7, r0, r8, ror r9
    281c:	002079ac 	eoreq	r7, r0, ip, lsr #19
    2820:	0020792e 	eoreq	r7, r0, lr, lsr #18
    2824:	0020792f 	eoreq	r7, r0, pc, lsr #18
    2828:	e1a0c00d 	mov	ip, sp
    282c:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    2830:	e59f60f0 	ldr	r6, [pc, #240]	; 0x2928
    2834:	e5d63000 	ldrb	r3, [r6]
    2838:	e2833001 	add	r3, r3, #1	; 0x1
    283c:	e5c0301e 	strb	r3, [r0, #30]
    2840:	e59f70e4 	ldr	r7, [pc, #228]	; 0x292c
    2844:	e5973000 	ldr	r3, [r7]
    2848:	e1d051df 	ldrsb	r5, [r0, #31]
    284c:	e3530000 	cmp	r3, #0	; 0x0
    2850:	02833005 	addeq	r3, r3, #5	; 0x5
    2854:	05c03020 	streqb	r3, [r0, #32]
    2858:	e3550000 	cmp	r5, #0	; 0x0
    285c:	e24cb004 	sub	fp, ip, #4	; 0x4
    2860:	e1a04000 	mov	r4, r0
    2864:	0a000006 	beq	0x2884
    2868:	e59f30c0 	ldr	r3, [pc, #192]	; 0x2930
    286c:	e5930000 	ldr	r0, [r3]
    2870:	eb0001dd 	bl	0x2fec
    2874:	e3a00000 	mov	r0, #0	; 0x0
    2878:	e24bd01c 	sub	sp, fp, #28	; 0x1c
    287c:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    2880:	e12fff1e 	bx	lr
    2884:	e3a00001 	mov	r0, #1	; 0x1
    2888:	e3a01004 	mov	r1, #4	; 0x4
    288c:	eb000468 	bl	0x3a34
    2890:	e3500000 	cmp	r0, #0	; 0x0
    2894:	e5840014 	str	r0, [r4, #20]
    2898:	01a00005 	moveq	r0, r5
    289c:	0afffff5 	beq	0x2878
    28a0:	e3a0000a 	mov	r0, #10	; 0xa
    28a4:	e1a01000 	mov	r1, r0
    28a8:	eb000461 	bl	0x3a34
    28ac:	e3500000 	cmp	r0, #0	; 0x0
    28b0:	e5840018 	str	r0, [r4, #24]
    28b4:	0a000016 	beq	0x2914
    28b8:	e5d63000 	ldrb	r3, [r6]
    28bc:	e2833001 	add	r3, r3, #1	; 0x1
    28c0:	e5c63000 	strb	r3, [r6]
    28c4:	e3a03002 	mov	r3, #2	; 0x2
    28c8:	e5c4301f 	strb	r3, [r4, #31]
    28cc:	e5c4501c 	strb	r5, [r4, #28]
    28d0:	e5972000 	ldr	r2, [r7]
    28d4:	e3520000 	cmp	r2, #0	; 0x0
    28d8:	05874000 	streq	r4, [r7]
    28dc:	e5d43020 	ldrb	r3, [r4, #32]
    28e0:	e59f204c 	ldr	r2, [pc, #76]	; 0x2934
    28e4:	e2433001 	sub	r3, r3, #1	; 0x1
    28e8:	e20330ff 	and	r3, r3, #255	; 0xff
    28ec:	e7921103 	ldr	r1, [r2, r3, lsl #2]
    28f0:	e3510000 	cmp	r1, #0	; 0x0
    28f4:	e7824103 	str	r4, [r2, r3, lsl #2]
    28f8:	15913008 	ldrne	r3, [r1, #8]
    28fc:	03a00001 	moveq	r0, #1	; 0x1
    2900:	13a00001 	movne	r0, #1	; 0x1
    2904:	15843008 	strne	r3, [r4, #8]
    2908:	05844008 	streq	r4, [r4, #8]
    290c:	15814008 	strne	r4, [r1, #8]
    2910:	eaffffd8 	b	0x2878
    2914:	e5940014 	ldr	r0, [r4, #20]
    2918:	eb000308 	bl	0x3540
    291c:	e1a00005 	mov	r0, r5
    2920:	e5845014 	str	r5, [r4, #20]
    2924:	eaffffd3 	b	0x2878
    2928:	0020797c 	eoreq	r7, r0, ip, ror r9
    292c:	00207978 	eoreq	r7, r0, r8, ror r9
    2930:	00207990 	mlaeq	r0, r0, r9, r7
    2934:	00207950 	eoreq	r7, r0, r0, asr r9
    2938:	e5d02020 	ldrb	r2, [r0, #32]
    293c:	e1a03c02 	mov	r3, r2, lsl #24
    2940:	e1510c43 	cmp	r1, r3, asr #24
    2944:	e92d4010 	stmdb	sp!, {r4, lr}
    2948:	0a000021 	beq	0x29d4
    294c:	e1d031df 	ldrsb	r3, [r0, #31]
    2950:	e3530000 	cmp	r3, #0	; 0x0
    2954:	05c01020 	streqb	r1, [r0, #32]
    2958:	0a00001d 	beq	0x29d4
    295c:	e2423001 	sub	r3, r2, #1	; 0x1
    2960:	e59fe074 	ldr	lr, [pc, #116]	; 0x29dc
    2964:	e20330ff 	and	r3, r3, #255	; 0xff
    2968:	e1a03103 	mov	r3, r3, lsl #2
    296c:	e793c00e 	ldr	ip, [r3, lr]
    2970:	e59c2008 	ldr	r2, [ip, #8]
    2974:	e1500002 	cmp	r0, r2
    2978:	e083400e 	add	r4, r3, lr
    297c:	0a000003 	beq	0x2990
    2980:	e1a0c002 	mov	ip, r2
    2984:	e5922008 	ldr	r2, [r2, #8]
    2988:	e1500002 	cmp	r0, r2
    298c:	1afffffb 	bne	0x2980
    2990:	e150000c 	cmp	r0, ip
    2994:	15903008 	ldrne	r3, [r0, #8]
    2998:	03a03000 	moveq	r3, #0	; 0x0
    299c:	158c3008 	strne	r3, [ip, #8]
    29a0:	05843000 	streq	r3, [r4]
    29a4:	e20130ff 	and	r3, r1, #255	; 0xff
    29a8:	1584c000 	strne	ip, [r4]
    29ac:	e5c03020 	strb	r3, [r0, #32]
    29b0:	e2433001 	sub	r3, r3, #1	; 0x1
    29b4:	e20330ff 	and	r3, r3, #255	; 0xff
    29b8:	e79e2103 	ldr	r2, [lr, r3, lsl #2]
    29bc:	e3520000 	cmp	r2, #0	; 0x0
    29c0:	e78e0103 	str	r0, [lr, r3, lsl #2]
    29c4:	15923008 	ldrne	r3, [r2, #8]
    29c8:	05800008 	streq	r0, [r0, #8]
    29cc:	15803008 	strne	r3, [r0, #8]
    29d0:	15820008 	strne	r0, [r2, #8]
    29d4:	e8bd4010 	ldmia	sp!, {r4, lr}
    29d8:	e12fff1e 	bx	lr
    29dc:	00207950 	eoreq	r7, r0, r0, asr r9
    29e0:	e1a0c00d 	mov	ip, sp
    29e4:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
    29e8:	e59f30f4 	ldr	r3, [pc, #244]	; 0x2ae4
    29ec:	e5932000 	ldr	r2, [r3]
    29f0:	e1a0e000 	mov	lr, r0
    29f4:	e5de3005 	ldrb	r3, [lr, #5]
    29f8:	e1d201de 	ldrsb	r0, [r2, #30]
    29fc:	e1500003 	cmp	r0, r3
    2a00:	e24cb004 	sub	fp, ip, #4	; 0x4
    2a04:	0a000005 	beq	0x2a20
    2a08:	e59f30d8 	ldr	r3, [pc, #216]	; 0x2ae8
    2a0c:	e5930000 	ldr	r0, [r3]
    2a10:	eb000175 	bl	0x2fec
    2a14:	e24bd014 	sub	sp, fp, #20	; 0x14
    2a18:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
    2a1c:	e12fff1e 	bx	lr
    2a20:	e59fc0c4 	ldr	ip, [pc, #196]	; 0x2aec
    2a24:	e20150ff 	and	r5, r1, #255	; 0xff
    2a28:	e59c0024 	ldr	r0, [ip, #36]
    2a2c:	e3500000 	cmp	r0, #0	; 0x0
    2a30:	e1a02000 	mov	r2, r0
    2a34:	0a000014 	beq	0x2a8c
    2a38:	e3550000 	cmp	r5, #0	; 0x0
    2a3c:	0a000017 	beq	0x2aa0
    2a40:	e3a01004 	mov	r1, #4	; 0x4
    2a44:	e3a04002 	mov	r4, #2	; 0x2
    2a48:	ea000001 	b	0x2a54
    2a4c:	e1520000 	cmp	r2, r0
    2a50:	0a00000d 	beq	0x2a8c
    2a54:	e5922008 	ldr	r2, [r2, #8]
    2a58:	e1d231df 	ldrsb	r3, [r2, #31]
    2a5c:	e3530005 	cmp	r3, #5	; 0x5
    2a60:	1afffff9 	bne	0x2a4c
    2a64:	e592300c 	ldr	r3, [r2, #12]
    2a68:	e153000e 	cmp	r3, lr
    2a6c:	1afffff6 	bne	0x2a4c
    2a70:	e1d232d1 	ldrsb	r3, [r2, #33]
    2a74:	e3530000 	cmp	r3, #0	; 0x0
    2a78:	15c24021 	strneb	r4, [r2, #33]
    2a7c:	e5c2101f 	strb	r1, [r2, #31]
    2a80:	e59c0024 	ldr	r0, [ip, #36]
    2a84:	e1520000 	cmp	r2, r0
    2a88:	1afffff1 	bne	0x2a54
    2a8c:	e59f305c 	ldr	r3, [pc, #92]	; 0x2af0
    2a90:	e24cc004 	sub	ip, ip, #4	; 0x4
    2a94:	e15c0003 	cmp	ip, r3
    2a98:	1affffe2 	bne	0x2a28
    2a9c:	eaffffdc 	b	0x2a14
    2aa0:	e5922008 	ldr	r2, [r2, #8]
    2aa4:	e1d231df 	ldrsb	r3, [r2, #31]
    2aa8:	e3530005 	cmp	r3, #5	; 0x5
    2aac:	0a000002 	beq	0x2abc
    2ab0:	e1520000 	cmp	r2, r0
    2ab4:	1afffff9 	bne	0x2aa0
    2ab8:	eafffff3 	b	0x2a8c
    2abc:	e592300c 	ldr	r3, [r2, #12]
    2ac0:	e153000e 	cmp	r3, lr
    2ac4:	1afffff9 	bne	0x2ab0
    2ac8:	e1d232d1 	ldrsb	r3, [r2, #33]
    2acc:	e3530000 	cmp	r3, #0	; 0x0
    2ad0:	13a03002 	movne	r3, #2	; 0x2
    2ad4:	15c23021 	strneb	r3, [r2, #33]
    2ad8:	e3a03004 	mov	r3, #4	; 0x4
    2adc:	e5c2301f 	strb	r3, [r2, #31]
    2ae0:	eaffffcb 	b	0x2a14
    2ae4:	00207978 	eoreq	r7, r0, r8, ror r9
    2ae8:	002079ac 	eoreq	r7, r0, ip, lsr #19
    2aec:	00207950 	eoreq	r7, r0, r0, asr r9
    2af0:	00207928 	eoreq	r7, r0, r8, lsr #18
    2af4:	e1a0c00d 	mov	ip, sp
    2af8:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    2afc:	e24cb004 	sub	fp, ip, #4	; 0x4
    2b00:	e24dd008 	sub	sp, sp, #8	; 0x8
    2b04:	e59fa4b4 	ldr	sl, [pc, #1204]	; 0x2fc0
    2b08:	e59a1000 	ldr	r1, [sl]
    2b0c:	e3510000 	cmp	r1, #0	; 0x0
    2b10:	0a000005 	beq	0x2b2c
    2b14:	e1d131df 	ldrsb	r3, [r1, #31]
    2b18:	e3530001 	cmp	r3, #1	; 0x1
    2b1c:	0a000109 	beq	0x2f48
    2b20:	e5d1201c 	ldrb	r2, [r1, #28]
    2b24:	e3520000 	cmp	r2, #0	; 0x0
    2b28:	1a0000f5 	bne	0x2f04
    2b2c:	e59f0490 	ldr	r0, [pc, #1168]	; 0x2fc4
    2b30:	e3a03000 	mov	r3, #0	; 0x0
    2b34:	e3a02009 	mov	r2, #9	; 0x9
    2b38:	e58a3000 	str	r3, [sl]
    2b3c:	e50b002c 	str	r0, [fp, #-44]
    2b40:	e50b2030 	str	r2, [fp, #-48]
    2b44:	e1a08003 	mov	r8, r3
    2b48:	e51b302c 	ldr	r3, [fp, #-44]
    2b4c:	e5937024 	ldr	r7, [r3, #36]
    2b50:	e3570000 	cmp	r7, #0	; 0x0
    2b54:	0a000036 	beq	0x2c34
    2b58:	e1a00007 	mov	r0, r7
    2b5c:	e5905008 	ldr	r5, [r0, #8]
    2b60:	e1d531df 	ldrsb	r3, [r5, #31]
    2b64:	e2433002 	sub	r3, r3, #2	; 0x2
    2b68:	e3530004 	cmp	r3, #4	; 0x4
    2b6c:	979ff103 	ldrls	pc, [pc, r3, lsl #2]
    2b70:	ea000026 	b	0x2c10
    2b74:	00204b88 	eoreq	r4, r0, r8, lsl #23
    2b78:	00204c10 	eoreq	r4, r0, r0, lsl ip
    2b7c:	00204cf8 	streqd	r4, [r0], -r8
    2b80:	00204cc8 	eoreq	r4, r0, r8, asr #25
    2b84:	00204d7c 	eoreq	r4, r0, ip, ror sp
    2b88:	e59a6000 	ldr	r6, [sl]
    2b8c:	e3560000 	cmp	r6, #0	; 0x0
    2b90:	1a000021 	bne	0x2c1c
    2b94:	e59f3424 	ldr	r3, [pc, #1060]	; 0x2fc0
    2b98:	e5952018 	ldr	r2, [r5, #24]
    2b9c:	e59f9424 	ldr	r9, [pc, #1060]	; 0x2fc8
    2ba0:	e2822008 	add	r2, r2, #8	; 0x8
    2ba4:	e5835000 	str	r5, [r3]
    2ba8:	e3a03003 	mov	r3, #3	; 0x3
    2bac:	e5892000 	str	r2, [r9]
    2bb0:	e5c5301f 	strb	r3, [r5, #31]
    2bb4:	e59f2410 	ldr	r2, [pc, #1040]	; 0x2fcc
    2bb8:	e5923000 	ldr	r3, [r2]
    2bbc:	e1550003 	cmp	r5, r3
    2bc0:	0a0000ae 	beq	0x2e80
    2bc4:	e5993000 	ldr	r3, [r9]
    2bc8:	e1a02006 	mov	r2, r6
    2bcc:	e5835000 	str	r5, [r3]
    2bd0:	e1a00005 	mov	r0, r5
    2bd4:	e3a01001 	mov	r1, #1	; 0x1
    2bd8:	eb00074b 	bl	0x490c
    2bdc:	e59a3000 	ldr	r3, [sl]
    2be0:	e5d3201c 	ldrb	r2, [r3, #28]
    2be4:	e3520000 	cmp	r2, #0	; 0x0
    2be8:	01a01002 	moveq	r1, r2
    2bec:	15933014 	ldrne	r3, [r3, #20]
    2bf0:	10822102 	addne	r2, r2, r2, lsl #2
    2bf4:	10833102 	addne	r3, r3, r2, lsl #2
    2bf8:	1243100c 	subne	r1, r3, #12	; 0xc
    2bfc:	e59f03cc 	ldr	r0, [pc, #972]	; 0x2fd0
    2c00:	e5992000 	ldr	r2, [r9]
    2c04:	e5903000 	ldr	r3, [r0]
    2c08:	e5812010 	str	r2, [r1, #16]
    2c0c:	e581300c 	str	r3, [r1, #12]
    2c10:	e59a3000 	ldr	r3, [sl]
    2c14:	e3530000 	cmp	r3, #0	; 0x0
    2c18:	0a000051 	beq	0x2d64
    2c1c:	e1d532d2 	ldrsb	r3, [r5, #34]
    2c20:	e3530000 	cmp	r3, #0	; 0x0
    2c24:	03a08001 	moveq	r8, #1	; 0x1
    2c28:	e1570005 	cmp	r7, r5
    2c2c:	e1a00005 	mov	r0, r5
    2c30:	1affffc9 	bne	0x2b5c
    2c34:	e24b0030 	sub	r0, fp, #48	; 0x30
    2c38:	e8900005 	ldmia	r0, {r0, r2}
    2c3c:	e2403001 	sub	r3, r0, #1	; 0x1
    2c40:	e1a03803 	mov	r3, r3, lsl #16
    2c44:	e3730801 	cmn	r3, #65536	; 0x10000
    2c48:	e2422004 	sub	r2, r2, #4	; 0x4
    2c4c:	e1a03823 	mov	r3, r3, lsr #16
    2c50:	e50b202c 	str	r2, [fp, #-44]
    2c54:	e50b3030 	str	r3, [fp, #-48]
    2c58:	1affffba 	bne	0x2b48
    2c5c:	e3580000 	cmp	r8, #0	; 0x0
    2c60:	0a00009c 	beq	0x2ed8
    2c64:	e59a3000 	ldr	r3, [sl]
    2c68:	e3530000 	cmp	r3, #0	; 0x0
    2c6c:	0a000013 	beq	0x2cc0
    2c70:	e5d3201c 	ldrb	r2, [r3, #28]
    2c74:	e3520000 	cmp	r2, #0	; 0x0
    2c78:	15933014 	ldrne	r3, [r3, #20]
    2c7c:	10822102 	addne	r2, r2, r2, lsl #2
    2c80:	10833102 	addne	r3, r3, r2, lsl #2
    2c84:	1243100c 	subne	r1, r3, #12	; 0xc
    2c88:	e59a3000 	ldr	r3, [sl]
    2c8c:	01a01002 	moveq	r1, r2
    2c90:	e591c008 	ldr	ip, [r1, #8]
    2c94:	e1d322d1 	ldrsb	r2, [r3, #33]
    2c98:	e5910010 	ldr	r0, [r1, #16]
    2c9c:	e59f332c 	ldr	r3, [pc, #812]	; 0x2fd0
    2ca0:	e591100c 	ldr	r1, [r1, #12]
    2ca4:	e3520002 	cmp	r2, #2	; 0x2
    2ca8:	e5831000 	str	r1, [r3]
    2cac:	e59f2314 	ldr	r2, [pc, #788]	; 0x2fc8
    2cb0:	e59f331c 	ldr	r3, [pc, #796]	; 0x2fd4
    2cb4:	e5820000 	str	r0, [r2]
    2cb8:	e583c000 	str	ip, [r3]
    2cbc:	0a00009c 	beq	0x2f34
    2cc0:	e3a00001 	mov	r0, #1	; 0x1
    2cc4:	ea00008b 	b	0x2ef8
    2cc8:	e5953010 	ldr	r3, [r5, #16]
    2ccc:	e3530000 	cmp	r3, #0	; 0x0
    2cd0:	da000003 	ble	0x2ce4
    2cd4:	ebfff4cd 	bl	0x10
    2cd8:	e5953010 	ldr	r3, [r5, #16]
    2cdc:	e1500003 	cmp	r0, r3
    2ce0:	2a000004 	bcs	0x2cf8
    2ce4:	e1d532d1 	ldrsb	r3, [r5, #33]
    2ce8:	e3530000 	cmp	r3, #0	; 0x0
    2cec:	0affffc7 	beq	0x2c10
    2cf0:	e3a03002 	mov	r3, #2	; 0x2
    2cf4:	e5c53021 	strb	r3, [r5, #33]
    2cf8:	e595c00c 	ldr	ip, [r5, #12]
    2cfc:	e5dc0005 	ldrb	r0, [ip, #5]
    2d00:	e3500000 	cmp	r0, #0	; 0x0
    2d04:	1a000027 	bne	0x2da8
    2d08:	e5dc2004 	ldrb	r2, [ip, #4]
    2d0c:	e3520000 	cmp	r2, #0	; 0x0
    2d10:	0a00004d 	beq	0x2e4c
    2d14:	e1d531de 	ldrsb	r3, [r5, #30]
    2d18:	e3530000 	cmp	r3, #0	; 0x0
    2d1c:	0a00004a 	beq	0x2e4c
    2d20:	e3a03004 	mov	r3, #4	; 0x4
    2d24:	e5c5301f 	strb	r3, [r5, #31]
    2d28:	e59f22a8 	ldr	r2, [pc, #680]	; 0x2fd8
    2d2c:	e59f32a8 	ldr	r3, [pc, #680]	; 0x2fdc
    2d30:	e3a01001 	mov	r1, #1	; 0x1
    2d34:	e5c5101d 	strb	r1, [r5, #29]
    2d38:	e5c31000 	strb	r1, [r3]
    2d3c:	e5c21000 	strb	r1, [r2]
    2d40:	e5d5301d 	ldrb	r3, [r5, #29]
    2d44:	e3a02003 	mov	r2, #3	; 0x3
    2d48:	e5cc3004 	strb	r3, [ip, #4]
    2d4c:	e5c5201f 	strb	r2, [r5, #31]
    2d50:	e3a03000 	mov	r3, #0	; 0x0
    2d54:	e585300c 	str	r3, [r5, #12]
    2d58:	e59a3000 	ldr	r3, [sl]
    2d5c:	e3530000 	cmp	r3, #0	; 0x0
    2d60:	1affffad 	bne	0x2c1c
    2d64:	e1d531df 	ldrsb	r3, [r5, #31]
    2d68:	e3530003 	cmp	r3, #3	; 0x3
    2d6c:	051b202c 	ldreq	r2, [fp, #-44]
    2d70:	058a5000 	streq	r5, [sl]
    2d74:	05825024 	streq	r5, [r2, #36]
    2d78:	eaffffa7 	b	0x2c1c
    2d7c:	e1d532d1 	ldrsb	r3, [r5, #33]
    2d80:	e3530000 	cmp	r3, #0	; 0x0
    2d84:	0a000035 	beq	0x2e60
    2d88:	e3530000 	cmp	r3, #0	; 0x0
    2d8c:	e3a03003 	mov	r3, #3	; 0x3
    2d90:	e5c5301f 	strb	r3, [r5, #31]
    2d94:	12433001 	subne	r3, r3, #1	; 0x1
    2d98:	15c53021 	strneb	r3, [r5, #33]
    2d9c:	e3a03000 	mov	r3, #0	; 0x0
    2da0:	e5853010 	str	r3, [r5, #16]
    2da4:	eaffff99 	b	0x2c10
    2da8:	e59a3000 	ldr	r3, [sl]
    2dac:	e3530000 	cmp	r3, #0	; 0x0
    2db0:	1affff99 	bne	0x2c1c
    2db4:	e1d541de 	ldrsb	r4, [r5, #30]
    2db8:	e1540000 	cmp	r4, r0
    2dbc:	0affffe8 	beq	0x2d64
    2dc0:	e59fc1fc 	ldr	ip, [pc, #508]	; 0x2fc4
    2dc4:	e24ce028 	sub	lr, ip, #40	; 0x28
    2dc8:	e59c1024 	ldr	r1, [ip, #36]
    2dcc:	e3510000 	cmp	r1, #0	; 0x0
    2dd0:	0a000014 	beq	0x2e28
    2dd4:	e1a02001 	mov	r2, r1
    2dd8:	ea000001 	b	0x2de4
    2ddc:	e1510002 	cmp	r1, r2
    2de0:	0a000010 	beq	0x2e28
    2de4:	e5922008 	ldr	r2, [r2, #8]
    2de8:	e1d231de 	ldrsb	r3, [r2, #30]
    2dec:	e1530000 	cmp	r3, r0
    2df0:	1afffff9 	bne	0x2ddc
    2df4:	e5d2301f 	ldrb	r3, [r2, #31]
    2df8:	e1a03c03 	mov	r3, r3, lsl #24
    2dfc:	e1a03c43 	mov	r3, r3, asr #24
    2e00:	e3530003 	cmp	r3, #3	; 0x3
    2e04:	0a00001b 	beq	0x2e78
    2e08:	e3530004 	cmp	r3, #4	; 0x4
    2e0c:	1afffff2 	bne	0x2ddc
    2e10:	e592300c 	ldr	r3, [r2, #12]
    2e14:	e5d30005 	ldrb	r0, [r3, #5]
    2e18:	e3500000 	cmp	r0, #0	; 0x0
    2e1c:	1affffe5 	bne	0x2db8
    2e20:	e1510002 	cmp	r1, r2
    2e24:	1affffee 	bne	0x2de4
    2e28:	e24cc004 	sub	ip, ip, #4	; 0x4
    2e2c:	e15c000e 	cmp	ip, lr
    2e30:	1affffe4 	bne	0x2dc8
    2e34:	e1d531df 	ldrsb	r3, [r5, #31]
    2e38:	e3530003 	cmp	r3, #3	; 0x3
    2e3c:	051b202c 	ldreq	r2, [fp, #-44]
    2e40:	058a5000 	streq	r5, [sl]
    2e44:	05825024 	streq	r5, [r2, #36]
    2e48:	eaffff73 	b	0x2c1c
    2e4c:	e5d5301e 	ldrb	r3, [r5, #30]
    2e50:	e2822001 	add	r2, r2, #1	; 0x1
    2e54:	e5cc3005 	strb	r3, [ip, #5]
    2e58:	e5cc2004 	strb	r2, [ip, #4]
    2e5c:	eaffffb7 	b	0x2d40
    2e60:	ebfff46a 	bl	0x10
    2e64:	e5953010 	ldr	r3, [r5, #16]
    2e68:	e1500003 	cmp	r0, r3
    2e6c:	3affff67 	bcc	0x2c10
    2e70:	e1d532d1 	ldrsb	r3, [r5, #33]
    2e74:	eaffffc3 	b	0x2d88
    2e78:	e58a2000 	str	r2, [sl]
    2e7c:	eaffff66 	b	0x2c1c
    2e80:	e59f3158 	ldr	r3, [pc, #344]	; 0x2fe0
    2e84:	e59f1158 	ldr	r1, [pc, #344]	; 0x2fe4
    2e88:	e5934000 	ldr	r4, [r3]
    2e8c:	e5d10000 	ldrb	r0, [r1]
    2e90:	e1d420bc 	ldrh	r2, [r4, #12]
    2e94:	e0822000 	add	r2, r2, r0
    2e98:	e7d23004 	ldrb	r3, [r2, r4]
    2e9c:	e0833103 	add	r3, r3, r3, lsl #2
    2ea0:	e5992000 	ldr	r2, [r9]
    2ea4:	e0844083 	add	r4, r4, r3, lsl #1
    2ea8:	e2844010 	add	r4, r4, #16	; 0x10
    2eac:	e5826000 	str	r6, [r2]
    2eb0:	e1a01006 	mov	r1, r6
    2eb4:	e1a00004 	mov	r0, r4
    2eb8:	eb00055e 	bl	0x4438
    2ebc:	e1a01006 	mov	r1, r6
    2ec0:	eb0005b5 	bl	0x459c
    2ec4:	e59f2104 	ldr	r2, [pc, #260]	; 0x2fd0
    2ec8:	e1a00004 	mov	r0, r4
    2ecc:	e5921000 	ldr	r1, [r2]
    2ed0:	eb000641 	bl	0x47dc
    2ed4:	eaffff40 	b	0x2bdc
    2ed8:	e59f30f8 	ldr	r3, [pc, #248]	; 0x2fd8
    2edc:	e3a02001 	mov	r2, #1	; 0x1
    2ee0:	e5c32000 	strb	r2, [r3]
    2ee4:	e59f30f0 	ldr	r3, [pc, #240]	; 0x2fdc
    2ee8:	e3a01002 	mov	r1, #2	; 0x2
    2eec:	e5c31000 	strb	r1, [r3]
    2ef0:	e58a8000 	str	r8, [sl]
    2ef4:	e1a00008 	mov	r0, r8
    2ef8:	e24bd028 	sub	sp, fp, #40	; 0x28
    2efc:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    2f00:	e12fff1e 	bx	lr
    2f04:	e5913014 	ldr	r3, [r1, #20]
    2f08:	e0822102 	add	r2, r2, r2, lsl #2
    2f0c:	e0833102 	add	r3, r3, r2, lsl #2
    2f10:	e253000c 	subs	r0, r3, #12	; 0xc
    2f14:	0affff04 	beq	0x2b2c
    2f18:	e59f30a8 	ldr	r3, [pc, #168]	; 0x2fc8
    2f1c:	e5931000 	ldr	r1, [r3]
    2f20:	e59f30a8 	ldr	r3, [pc, #168]	; 0x2fd0
    2f24:	e5932000 	ldr	r2, [r3]
    2f28:	e5801010 	str	r1, [r0, #16]
    2f2c:	e580200c 	str	r2, [r0, #12]
    2f30:	eafffefd 	b	0x2b2c
    2f34:	e59f30ac 	ldr	r3, [pc, #172]	; 0x2fe8
    2f38:	e5930000 	ldr	r0, [r3]
    2f3c:	eb00002a 	bl	0x2fec
    2f40:	e3a00001 	mov	r0, #1	; 0x1
    2f44:	eaffffeb 	b	0x2ef8
    2f48:	e5910018 	ldr	r0, [r1, #24]
    2f4c:	eb00017b 	bl	0x3540
    2f50:	e59a3000 	ldr	r3, [sl]
    2f54:	e5930014 	ldr	r0, [r3, #20]
    2f58:	eb000178 	bl	0x3540
    2f5c:	e59ae000 	ldr	lr, [sl]
    2f60:	e5de3020 	ldrb	r3, [lr, #32]
    2f64:	e2433001 	sub	r3, r3, #1	; 0x1
    2f68:	e59f1054 	ldr	r1, [pc, #84]	; 0x2fc4
    2f6c:	e20330ff 	and	r3, r3, #255	; 0xff
    2f70:	e1a03103 	mov	r3, r3, lsl #2
    2f74:	e793c001 	ldr	ip, [r3, r1]
    2f78:	e59c0008 	ldr	r0, [ip, #8]
    2f7c:	e3a02000 	mov	r2, #0	; 0x0
    2f80:	e150000e 	cmp	r0, lr
    2f84:	e58e2018 	str	r2, [lr, #24]
    2f88:	e58e2014 	str	r2, [lr, #20]
    2f8c:	e0832001 	add	r2, r3, r1
    2f90:	0a000003 	beq	0x2fa4
    2f94:	e1a0c000 	mov	ip, r0
    2f98:	e5900008 	ldr	r0, [r0, #8]
    2f9c:	e15e0000 	cmp	lr, r0
    2fa0:	1afffffb 	bne	0x2f94
    2fa4:	e15c000e 	cmp	ip, lr
    2fa8:	159e3008 	ldrne	r3, [lr, #8]
    2fac:	03a03000 	moveq	r3, #0	; 0x0
    2fb0:	05823000 	streq	r3, [r2]
    2fb4:	1582c000 	strne	ip, [r2]
    2fb8:	158c3008 	strne	r3, [ip, #8]
    2fbc:	eafffeda 	b	0x2b2c
    2fc0:	00207978 	eoreq	r7, r0, r8, ror r9
    2fc4:	00207950 	eoreq	r7, r0, r0, asr r9
    2fc8:	00207928 	eoreq	r7, r0, r8, lsr #18
    2fcc:	00207920 	eoreq	r7, r0, r0, lsr #18
    2fd0:	00207944 	eoreq	r7, r0, r4, asr #18
    2fd4:	00207940 	eoreq	r7, r0, r0, asr #18
    2fd8:	0020792f 	eoreq	r7, r0, pc, lsr #18
    2fdc:	0020792e 	eoreq	r7, r0, lr, lsr #18
    2fe0:	002079b0 	streqh	r7, [r0], -r0
    2fe4:	0020797d 	eoreq	r7, r0, sp, ror r9
    2fe8:	002079a8 	eoreq	r7, r0, r8, lsr #19
    2fec:	e1a0c00d 	mov	ip, sp
    2ff0:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    2ff4:	e59f31c4 	ldr	r3, [pc, #452]	; 0x31c0
    2ff8:	e24cb004 	sub	fp, ip, #4	; 0x4
    2ffc:	e24dd004 	sub	sp, sp, #4	; 0x4
    3000:	e5931000 	ldr	r1, [r3]
    3004:	e3510000 	cmp	r1, #0	; 0x0
    3008:	e1a06000 	mov	r6, r0
    300c:	0a000054 	beq	0x3164
    3010:	e59f31ac 	ldr	r3, [pc, #428]	; 0x31c4
    3014:	e5932000 	ldr	r2, [r3]
    3018:	e1520000 	cmp	r2, r0
    301c:	03a03000 	moveq	r3, #0	; 0x0
    3020:	05c13021 	streqb	r3, [r1, #33]
    3024:	e59fe19c 	ldr	lr, [pc, #412]	; 0x31c8
    3028:	e59fa19c 	ldr	sl, [pc, #412]	; 0x31cc
    302c:	e59e2000 	ldr	r2, [lr]
    3030:	e59f1198 	ldr	r1, [pc, #408]	; 0x31d0
    3034:	e3a03000 	mov	r3, #0	; 0x0
    3038:	e5812000 	str	r2, [r1]
    303c:	e58a3000 	str	r3, [sl]
    3040:	e59f918c 	ldr	r9, [pc, #396]	; 0x31d4
    3044:	e59f418c 	ldr	r4, [pc, #396]	; 0x31d8
    3048:	e59f718c 	ldr	r7, [pc, #396]	; 0x31dc
    304c:	e59f518c 	ldr	r5, [pc, #396]	; 0x31e0
    3050:	ebfffd20 	bl	0x24d8
    3054:	e59a3000 	ldr	r3, [sl]
    3058:	e3530000 	cmp	r3, #0	; 0x0
    305c:	e59f3180 	ldr	r3, [pc, #384]	; 0x31e4
    3060:	e5902000 	ldr	r2, [r0]
    3064:	e5830000 	str	r0, [r3]
    3068:	059f315c 	ldreq	r3, [pc, #348]	; 0x31cc
    306c:	e5892000 	str	r2, [r9]
    3070:	05832000 	streq	r2, [r3]
    3074:	e59fe16c 	ldr	lr, [pc, #364]	; 0x31e8
    3078:	e5d20009 	ldrb	r0, [r2, #9]
    307c:	e59ec000 	ldr	ip, [lr]
    3080:	e59fe140 	ldr	lr, [pc, #320]	; 0x31c8
    3084:	e1d230b4 	ldrh	r3, [r2, #4]
    3088:	e2400001 	sub	r0, r0, #1	; 0x1
    308c:	e1d220b2 	ldrh	r2, [r2, #2]
    3090:	e59e1000 	ldr	r1, [lr]
    3094:	e20000ff 	and	r0, r0, #255	; 0xff
    3098:	e08c3003 	add	r3, ip, r3
    309c:	e0631001 	rsb	r1, r3, r1
    30a0:	e082200c 	add	r2, r2, ip
    30a4:	e35000ff 	cmp	r0, #255	; 0xff
    30a8:	e5842000 	str	r2, [r4]
    30ac:	e1c710b0 	strh	r1, [r7]
    30b0:	e5c50000 	strb	r0, [r5]
    30b4:	0a00002d 	beq	0x3170
    30b8:	e5942000 	ldr	r2, [r4]
    30bc:	ea000008 	b	0x30e4
    30c0:	e5d53000 	ldrb	r3, [r5]
    30c4:	e5942000 	ldr	r2, [r4]
    30c8:	e2433001 	sub	r3, r3, #1	; 0x1
    30cc:	e20330ff 	and	r3, r3, #255	; 0xff
    30d0:	e2822008 	add	r2, r2, #8	; 0x8
    30d4:	e35300ff 	cmp	r3, #255	; 0xff
    30d8:	e5842000 	str	r2, [r4]
    30dc:	e5c53000 	strb	r3, [r5]
    30e0:	0a000022 	beq	0x3170
    30e4:	e1d710b0 	ldrh	r1, [r7]
    30e8:	e1d230b0 	ldrh	r3, [r2]
    30ec:	e1530001 	cmp	r3, r1
    30f0:	8afffff2 	bhi	0x30c0
    30f4:	e1d230b2 	ldrh	r3, [r2, #2]
    30f8:	e1530001 	cmp	r3, r1
    30fc:	e1a00006 	mov	r0, r6
    3100:	3affffee 	bcc	0x30c0
    3104:	e5d21006 	ldrb	r1, [r2, #6]
    3108:	eb0005e5 	bl	0x48a4
    310c:	e3500000 	cmp	r0, #0	; 0x0
    3110:	0affffea 	beq	0x30c0
    3114:	e59f30bc 	ldr	r3, [pc, #188]	; 0x31d8
    3118:	e59f10c4 	ldr	r1, [pc, #196]	; 0x31e4
    311c:	e5990000 	ldr	r0, [r9]
    3120:	e5934000 	ldr	r4, [r3]
    3124:	e59f30bc 	ldr	r3, [pc, #188]	; 0x31e8
    3128:	e5915000 	ldr	r5, [r1]
    312c:	e5d02006 	ldrb	r2, [r0, #6]
    3130:	e1d010b4 	ldrh	r1, [r0, #4]
    3134:	e593c000 	ldr	ip, [r3]
    3138:	e595e008 	ldr	lr, [r5, #8]
    313c:	e1d430b4 	ldrh	r3, [r4, #4]
    3140:	e1a02102 	mov	r2, r2, lsl #2
    3144:	e081100c 	add	r1, r1, ip
    3148:	e0811003 	add	r1, r1, r3
    314c:	e082000e 	add	r0, r2, lr
    3150:	e782600e 	str	r6, [r2, lr]
    3154:	e59f3090 	ldr	r3, [pc, #144]	; 0x31ec
    3158:	e59fe068 	ldr	lr, [pc, #104]	; 0x31c8
    315c:	e5830000 	str	r0, [r3]
    3160:	e58e1000 	str	r1, [lr]
    3164:	e24bd028 	sub	sp, fp, #40	; 0x28
    3168:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    316c:	e12fff1e 	bx	lr
    3170:	e59f1048 	ldr	r1, [pc, #72]	; 0x31c0
    3174:	e5918000 	ldr	r8, [r1]
    3178:	e3a00000 	mov	r0, #0	; 0x0
    317c:	eb0004d1 	bl	0x44c8
    3180:	e1d831df 	ldrsb	r3, [r8, #31]
    3184:	e3530001 	cmp	r3, #1	; 0x1
    3188:	1affffb0 	bne	0x3050
    318c:	e1a00006 	mov	r0, r6
    3190:	eb0004a6 	bl	0x4430
    3194:	e3500010 	cmp	r0, #16	; 0x10
    3198:	0afffff1 	beq	0x3164
    319c:	e59fe02c 	ldr	lr, [pc, #44]	; 0x31d0
    31a0:	e59ec000 	ldr	ip, [lr]
    31a4:	e59a2000 	ldr	r2, [sl]
    31a8:	e5993000 	ldr	r3, [r9]
    31ac:	e1a00006 	mov	r0, r6
    31b0:	e1a01008 	mov	r1, r8
    31b4:	e58dc000 	str	ip, [sp]
    31b8:	ebfff398 	bl	0x20
    31bc:	eaffffe8 	b	0x3164
    31c0:	00207978 	eoreq	r7, r0, r8, ror r9
    31c4:	002079a8 	eoreq	r7, r0, r8, lsr #19
    31c8:	00207944 	eoreq	r7, r0, r4, asr #18
    31cc:	002078d4 	ldreqd	r7, [r0], -r4
    31d0:	002078ec 	eoreq	r7, r0, ip, ror #17
    31d4:	002078d8 	ldreqd	r7, [r0], -r8
    31d8:	002078e4 	eoreq	r7, r0, r4, ror #17
    31dc:	002078dc 	ldreqd	r7, [r0], -ip
    31e0:	002078e8 	eoreq	r7, r0, r8, ror #17
    31e4:	002078e0 	eoreq	r7, r0, r0, ror #17
    31e8:	002079b0 	streqh	r7, [r0], -r0
    31ec:	00207928 	eoreq	r7, r0, r8, lsr #18
    31f0:	e1a0c00d 	mov	ip, sp
    31f4:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
    31f8:	e3a00005 	mov	r0, #5	; 0x5
    31fc:	e24cb004 	sub	fp, ip, #4	; 0x4
    3200:	eb000198 	bl	0x3868
    3204:	e59f30bc 	ldr	r3, [pc, #188]	; 0x32c8
    3208:	e5830000 	str	r0, [r3]
    320c:	e3a00006 	mov	r0, #6	; 0x6
    3210:	eb000194 	bl	0x3868
    3214:	e59f30b0 	ldr	r3, [pc, #176]	; 0x32cc
    3218:	e5830000 	str	r0, [r3]
    321c:	e3a00007 	mov	r0, #7	; 0x7
    3220:	eb000190 	bl	0x3868
    3224:	e59f30a4 	ldr	r3, [pc, #164]	; 0x32d0
    3228:	e5830000 	str	r0, [r3]
    322c:	e3a00008 	mov	r0, #8	; 0x8
    3230:	eb00018c 	bl	0x3868
    3234:	e59f3098 	ldr	r3, [pc, #152]	; 0x32d4
    3238:	e5830000 	str	r0, [r3]
    323c:	e3a00009 	mov	r0, #9	; 0x9
    3240:	eb000188 	bl	0x3868
    3244:	e59f308c 	ldr	r3, [pc, #140]	; 0x32d8
    3248:	e5830000 	str	r0, [r3]
    324c:	e3a0000a 	mov	r0, #10	; 0xa
    3250:	eb000184 	bl	0x3868
    3254:	e59f3080 	ldr	r3, [pc, #128]	; 0x32dc
    3258:	e5830000 	str	r0, [r3]
    325c:	e3a0000b 	mov	r0, #11	; 0xb
    3260:	eb000180 	bl	0x3868
    3264:	e59f3074 	ldr	r3, [pc, #116]	; 0x32e0
    3268:	e5830000 	str	r0, [r3]
    326c:	e3a0000c 	mov	r0, #12	; 0xc
    3270:	eb00017c 	bl	0x3868
    3274:	e59f3068 	ldr	r3, [pc, #104]	; 0x32e4
    3278:	e5830000 	str	r0, [r3]
    327c:	e3a0000d 	mov	r0, #13	; 0xd
    3280:	eb000178 	bl	0x3868
    3284:	e59f305c 	ldr	r3, [pc, #92]	; 0x32e8
    3288:	e5830000 	str	r0, [r3]
    328c:	e3a0000e 	mov	r0, #14	; 0xe
    3290:	eb000174 	bl	0x3868
    3294:	e59f3050 	ldr	r3, [pc, #80]	; 0x32ec
    3298:	e5830000 	str	r0, [r3]
    329c:	e3a0000f 	mov	r0, #15	; 0xf
    32a0:	eb000170 	bl	0x3868
    32a4:	e59f3044 	ldr	r3, [pc, #68]	; 0x32f0
    32a8:	e5830000 	str	r0, [r3]
    32ac:	e3a00004 	mov	r0, #4	; 0x4
    32b0:	eb00016c 	bl	0x3868
    32b4:	e59f3038 	ldr	r3, [pc, #56]	; 0x32f4
    32b8:	e5830000 	str	r0, [r3]
    32bc:	e24bd00c 	sub	sp, fp, #12	; 0xc
    32c0:	e89d6800 	ldmia	sp, {fp, sp, lr}
    32c4:	e12fff1e 	bx	lr
    32c8:	00207984 	eoreq	r7, r0, r4, lsl #19
    32cc:	00207980 	eoreq	r7, r0, r0, lsl #19
    32d0:	002079a4 	eoreq	r7, r0, r4, lsr #19
    32d4:	0020798c 	eoreq	r7, r0, ip, lsl #19
    32d8:	00207988 	eoreq	r7, r0, r8, lsl #19
    32dc:	00207998 	mlaeq	r0, r8, r9, r7
    32e0:	0020799c 	mlaeq	r0, ip, r9, r7
    32e4:	00207994 	mlaeq	r0, r4, r9, r7
    32e8:	002079a8 	eoreq	r7, r0, r8, lsr #19
    32ec:	00207990 	mlaeq	r0, r0, r9, r7
    32f0:	002079ac 	eoreq	r7, r0, ip, lsr #19
    32f4:	002079a0 	eoreq	r7, r0, r0, lsr #19
    32f8:	e1a01801 	mov	r1, r1, lsl #16
    32fc:	e1b01821 	movs	r1, r1, lsr #16
    3300:	012fff1e 	bxeq	lr
    3304:	e2413001 	sub	r3, r1, #1	; 0x1
    3308:	e1a03803 	mov	r3, r3, lsl #16
    330c:	e1b01823 	movs	r1, r3, lsr #16
    3310:	e3a03000 	mov	r3, #0	; 0x0
    3314:	e0c030b2 	strh	r3, [r0], #2
    3318:	1afffff9 	bne	0x3304
    331c:	e12fff1e 	bx	lr
    3320:	e59f3028 	ldr	r3, [pc, #40]	; 0x3350
    3324:	e20110ff 	and	r1, r1, #255	; 0xff
    3328:	e7d32001 	ldrb	r2, [r3, r1]
    332c:	e1a00800 	mov	r0, r0, lsl #16
    3330:	e1a00820 	mov	r0, r0, lsr #16
    3334:	e0000092 	mul	r0, r2, r0
    3338:	e2800001 	add	r0, r0, #1	; 0x1
    333c:	e1a000c0 	mov	r0, r0, asr #1
    3340:	e2800004 	add	r0, r0, #4	; 0x4
    3344:	e1a00800 	mov	r0, r0, lsl #16
    3348:	e1a00820 	mov	r0, r0, lsr #16
    334c:	e12fff1e 	bx	lr
    3350:	002078c4 	eoreq	r7, r0, r4, asr #17
    3354:	e1d010b0 	ldrh	r1, [r0]
    3358:	e59f202c 	ldr	r2, [pc, #44]	; 0x338c
    335c:	e2010c1e 	and	r0, r1, #7680	; 0x1e00
    3360:	e3a03f7f 	mov	r3, #508	; 0x1fc
    3364:	e7d2c4a0 	ldrb	ip, [r2, r0, lsr #9]
    3368:	e2833003 	add	r3, r3, #3	; 0x3
    336c:	e0011003 	and	r1, r1, r3
    3370:	e000019c 	mul	r0, ip, r1
    3374:	e2800001 	add	r0, r0, #1	; 0x1
    3378:	e1a000c0 	mov	r0, r0, asr #1
    337c:	e2800004 	add	r0, r0, #4	; 0x4
    3380:	e1a00800 	mov	r0, r0, lsl #16
    3384:	e1a00820 	mov	r0, r0, lsr #16
    3388:	e12fff1e 	bx	lr
    338c:	002078c4 	eoreq	r7, r0, r4, asr #17
    3390:	e21110ff 	ands	r1, r1, #255	; 0xff
    3394:	01a0c001 	moveq	ip, r1
    3398:	0a000005 	beq	0x33b4
    339c:	e3a0c000 	mov	ip, #0	; 0x0
    33a0:	e2413001 	sub	r3, r1, #1	; 0x1
    33a4:	e4d02001 	ldrb	r2, [r0], #1
    33a8:	e21310ff 	ands	r1, r3, #255	; 0xff
    33ac:	e182c40c 	orr	ip, r2, ip, lsl #8
    33b0:	1afffffa 	bne	0x33a0
    33b4:	e1a0000c 	mov	r0, ip
    33b8:	e12fff1e 	bx	lr
    33bc:	e20110ff 	and	r1, r1, #255	; 0xff
    33c0:	e2413001 	sub	r3, r1, #1	; 0x1
    33c4:	e20330ff 	and	r3, r3, #255	; 0xff
    33c8:	e35300ff 	cmp	r3, #255	; 0xff
    33cc:	012fff1e 	bxeq	lr
    33d0:	e0810000 	add	r0, r1, r0
    33d4:	e0633000 	rsb	r3, r3, r0
    33d8:	e2433001 	sub	r3, r3, #1	; 0x1
    33dc:	e5602001 	strb	r2, [r0, #-1]!
    33e0:	e1500003 	cmp	r0, r3
    33e4:	e1a02422 	mov	r2, r2, lsr #8
    33e8:	1afffffb 	bne	0x33dc
    33ec:	e12fff1e 	bx	lr
    33f0:	e20110ff 	and	r1, r1, #255	; 0xff
    33f4:	e3510001 	cmp	r1, #1	; 0x1
    33f8:	e52de004 	str	lr, [sp, #-4]!
    33fc:	e1a0e002 	mov	lr, r2
    3400:	0a00000c 	beq	0x3438
    3404:	e3510002 	cmp	r1, #2	; 0x2
    3408:	e1a0c000 	mov	ip, r0
    340c:	0a00000c 	beq	0x3444
    3410:	e4dc3001 	ldrb	r3, [ip], #1
    3414:	e5c23003 	strb	r3, [r2, #3]
    3418:	e5d02001 	ldrb	r2, [r0, #1]
    341c:	e5ce2002 	strb	r2, [lr, #2]
    3420:	e5dc3001 	ldrb	r3, [ip, #1]
    3424:	e5ce3001 	strb	r3, [lr, #1]
    3428:	e5dc2002 	ldrb	r2, [ip, #2]
    342c:	e5ce2000 	strb	r2, [lr]
    3430:	e49de004 	ldr	lr, [sp], #4
    3434:	e12fff1e 	bx	lr
    3438:	e1d030d0 	ldrsb	r3, [r0]
    343c:	e5823000 	str	r3, [r2]
    3440:	eafffffa 	b	0x3430
    3444:	e5d03001 	ldrb	r3, [r0, #1]
    3448:	e5d02000 	ldrb	r2, [r0]
    344c:	e1833402 	orr	r3, r3, r2, lsl #8
    3450:	e1a03803 	mov	r3, r3, lsl #16
    3454:	e1a03843 	mov	r3, r3, asr #16
    3458:	e58e3000 	str	r3, [lr]
    345c:	eafffff3 	b	0x3430
    3460:	e59f2010 	ldr	r2, [pc, #16]	; 0x3478
    3464:	e59f1010 	ldr	r1, [pc, #16]	; 0x347c
    3468:	e3a03000 	mov	r3, #0	; 0x0
    346c:	e1c230b0 	strh	r3, [r2]
    3470:	e1c130b0 	strh	r3, [r1]
    3474:	e12fff1e 	bx	lr
    3478:	002078f6 	streqd	r7, [r0], -r6
    347c:	002078f4 	streqd	r7, [r0], -r4
    3480:	e2800001 	add	r0, r0, #1	; 0x1
    3484:	e3c00001 	bic	r0, r0, #1	; 0x1
    3488:	e1a03000 	mov	r3, r0
    348c:	e3c11001 	bic	r1, r1, #1	; 0x1
    3490:	e4831004 	str	r1, [r3], #4
    3494:	e92d4010 	stmdb	sp!, {r4, lr}
    3498:	e59fe038 	ldr	lr, [pc, #56]	; 0x34d8
    349c:	e59f4038 	ldr	r4, [pc, #56]	; 0x34dc
    34a0:	e0631001 	rsb	r1, r3, r1
    34a4:	e1de20b0 	ldrh	r2, [lr]
    34a8:	e1d4c0b0 	ldrh	ip, [r4]
    34ac:	e1a01781 	mov	r1, r1, lsl #15
    34b0:	e1a01821 	mov	r1, r1, lsr #16
    34b4:	e59f3024 	ldr	r3, [pc, #36]	; 0x34e0
    34b8:	e081c00c 	add	ip, r1, ip
    34bc:	e0812002 	add	r2, r1, r2
    34c0:	e5830000 	str	r0, [r3]
    34c4:	e1c010b4 	strh	r1, [r0, #4]
    34c8:	e1ce20b0 	strh	r2, [lr]
    34cc:	e1c4c0b0 	strh	ip, [r4]
    34d0:	e8bd4010 	ldmia	sp!, {r4, lr}
    34d4:	e12fff1e 	bx	lr
    34d8:	002078f4 	streqd	r7, [r0], -r4
    34dc:	002078f6 	streqd	r7, [r0], -r6
    34e0:	002078f0 	streqd	r7, [r0], -r0
    34e4:	e59f2018 	ldr	r2, [pc, #24]	; 0x3504
    34e8:	e1a01801 	mov	r1, r1, lsl #16
    34ec:	e1d230b0 	ldrh	r3, [r2]
    34f0:	e1a01821 	mov	r1, r1, lsr #16
    34f4:	e0813003 	add	r3, r1, r3
    34f8:	e1c230b0 	strh	r3, [r2]
    34fc:	e1c010b0 	strh	r1, [r0]
    3500:	e12fff1e 	bx	lr
    3504:	002078f6 	streqd	r7, [r0], -r6
    3508:	e59f3008 	ldr	r3, [pc, #8]	; 0x3518
    350c:	e1d300b0 	ldrh	r0, [r3]
    3510:	e1a00080 	mov	r0, r0, lsl #1
    3514:	e12fff1e 	bx	lr
    3518:	002078f4 	streqd	r7, [r0], -r4
    351c:	e59f3008 	ldr	r3, [pc, #8]	; 0x352c
    3520:	e1d300b0 	ldrh	r0, [r3]
    3524:	e1a00080 	mov	r0, r0, lsl #1
    3528:	e12fff1e 	bx	lr
    352c:	002078f6 	streqd	r7, [r0], -r6
    3530:	e59f3004 	ldr	r3, [pc, #4]	; 0x353c
    3534:	e5930000 	ldr	r0, [r3]
    3538:	e12fff1e 	bx	lr
    353c:	002078f0 	streqd	r7, [r0], -r0
    3540:	e52de004 	str	lr, [sp, #-4]!
    3544:	e1d010b0 	ldrh	r1, [r0]
    3548:	e59f2044 	ldr	r2, [pc, #68]	; 0x3594
    354c:	e201cc1e 	and	ip, r1, #7680	; 0x1e00
    3550:	e3a03f7f 	mov	r3, #508	; 0x1fc
    3554:	e7d2e4ac 	ldrb	lr, [r2, ip, lsr #9]
    3558:	e2833003 	add	r3, r3, #3	; 0x3
    355c:	e0011003 	and	r1, r1, r3
    3560:	e002019e 	mul	r2, lr, r1
    3564:	e2822001 	add	r2, r2, #1	; 0x1
    3568:	e59f1028 	ldr	r1, [pc, #40]	; 0x3598
    356c:	e1a020c2 	mov	r2, r2, asr #1
    3570:	e2822004 	add	r2, r2, #4	; 0x4
    3574:	e1d130b0 	ldrh	r3, [r1]
    3578:	e1a02802 	mov	r2, r2, lsl #16
    357c:	e1a02822 	mov	r2, r2, lsr #16
    3580:	e0823003 	add	r3, r2, r3
    3584:	e1c130b0 	strh	r3, [r1]
    3588:	e1c020b0 	strh	r2, [r0]
    358c:	e49de004 	ldr	lr, [sp], #4
    3590:	e12fff1e 	bx	lr
    3594:	002078c4 	eoreq	r7, r0, r4, asr #17
    3598:	002078f6 	streqd	r7, [r0], -r6
    359c:	e59f3138 	ldr	r3, [pc, #312]	; 0x36dc
    35a0:	e5932000 	ldr	r2, [r3]
    35a4:	e92d4070 	stmdb	sp!, {r4, r5, r6, lr}
    35a8:	e5924000 	ldr	r4, [r2]
    35ac:	e282c004 	add	ip, r2, #4	; 0x4
    35b0:	e1a00800 	mov	r0, r0, lsl #16
    35b4:	e15c0004 	cmp	ip, r4
    35b8:	e1a00820 	mov	r0, r0, lsr #16
    35bc:	2a00001f 	bcs	0x3640
    35c0:	e59f3118 	ldr	r3, [pc, #280]	; 0x36e0
    35c4:	e59f6118 	ldr	r6, [pc, #280]	; 0x36e4
    35c8:	e5935000 	ldr	r5, [r3]
    35cc:	ea000004 	b	0x35e4
    35d0:	e1510000 	cmp	r1, r0
    35d4:	2a00001d 	bcs	0x3650
    35d8:	e08cc081 	add	ip, ip, r1, lsl #1
    35dc:	e154000c 	cmp	r4, ip
    35e0:	9a000016 	bls	0x3640
    35e4:	e1dc10b0 	ldrh	r1, [ip]
    35e8:	e3110902 	tst	r1, #32768	; 0x8000
    35ec:	0afffff7 	beq	0x35d0
    35f0:	e3110901 	tst	r1, #16384	; 0x4000
    35f4:	e2012c1e 	and	r2, r1, #7680	; 0x1e00
    35f8:	17d624a2 	ldrneb	r2, [r6, r2, lsr #9]
    35fc:	e1a0eb81 	mov	lr, r1, lsl #23
    3600:	e20130ff 	and	r3, r1, #255	; 0xff
    3604:	e1a0ebae 	mov	lr, lr, lsr #23
    3608:	e0833103 	add	r3, r3, r3, lsl #2
    360c:	e0853083 	add	r3, r5, r3, lsl #1
    3610:	10030e92 	mulne	r3, r2, lr
    3614:	12833001 	addne	r3, r3, #1	; 0x1
    3618:	11a030c3 	movne	r3, r3, asr #1
    361c:	12833004 	addne	r3, r3, #4	; 0x4
    3620:	05d32011 	ldreqb	r2, [r3, #17]
    3624:	05d33010 	ldreqb	r3, [r3, #16]
    3628:	11a03803 	movne	r3, r3, lsl #16
    362c:	11a03823 	movne	r3, r3, lsr #16
    3630:	01833402 	orreq	r3, r3, r2, lsl #8
    3634:	e08cc083 	add	ip, ip, r3, lsl #1
    3638:	e154000c 	cmp	r4, ip
    363c:	8affffe8 	bhi	0x35e4
    3640:	e3a0c000 	mov	ip, #0	; 0x0
    3644:	e1a0000c 	mov	r0, ip
    3648:	e8bd4070 	ldmia	sp!, {r4, r5, r6, lr}
    364c:	e12fff1e 	bx	lr
    3650:	e1a03081 	mov	r3, r1, lsl #1
    3654:	e19320bc 	ldrh	r2, [r3, ip]
    3658:	e083300c 	add	r3, r3, ip
    365c:	e1540003 	cmp	r4, r3
    3660:	83a03000 	movhi	r3, #0	; 0x0
    3664:	93a03001 	movls	r3, #1	; 0x1
    3668:	e19337a2 	orrs	r3, r3, r2, lsr #15
    366c:	e1a0e00c 	mov	lr, ip
    3670:	1a00000b 	bne	0x36a4
    3674:	e0813002 	add	r3, r1, r2
    3678:	e1a03803 	mov	r3, r3, lsl #16
    367c:	e1a01823 	mov	r1, r3, lsr #16
    3680:	e1cc10b0 	strh	r1, [ip]
    3684:	e1a02081 	mov	r2, r1, lsl #1
    3688:	e082300c 	add	r3, r2, ip
    368c:	e19220bc 	ldrh	r2, [r2, ip]
    3690:	e1530004 	cmp	r3, r4
    3694:	33a03000 	movcc	r3, #0	; 0x0
    3698:	23a03001 	movcs	r3, #1	; 0x1
    369c:	e19337a2 	orrs	r3, r3, r2, lsr #15
    36a0:	0afffff3 	beq	0x3674
    36a4:	e1500001 	cmp	r0, r1
    36a8:	2a000006 	bcs	0x36c8
    36ac:	e0603001 	rsb	r3, r0, r1
    36b0:	e1a03803 	mov	r3, r3, lsl #16
    36b4:	e1a03823 	mov	r3, r3, lsr #16
    36b8:	e1a02083 	mov	r2, r3, lsl #1
    36bc:	e1cc30b0 	strh	r3, [ip]
    36c0:	e18200be 	strh	r0, [r2, lr]
    36c4:	e082c00c 	add	ip, r2, ip
    36c8:	e59f2018 	ldr	r2, [pc, #24]	; 0x36e8
    36cc:	e1d230b0 	ldrh	r3, [r2]
    36d0:	e0603003 	rsb	r3, r0, r3
    36d4:	e1c230b0 	strh	r3, [r2]
    36d8:	eaffffd9 	b	0x3644
    36dc:	002078f0 	streqd	r7, [r0], -r0
    36e0:	002079b0 	streqh	r7, [r0], -r0
    36e4:	002078c4 	eoreq	r7, r0, r4, asr #17
    36e8:	002078f6 	streqd	r7, [r0], -r6
    36ec:	e59f3160 	ldr	r3, [pc, #352]	; 0x3854
    36f0:	e5932000 	ldr	r2, [r3]
    36f4:	e1a0c00d 	mov	ip, sp
    36f8:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    36fc:	e5924000 	ldr	r4, [r2]
    3700:	e24cb004 	sub	fp, ip, #4	; 0x4
    3704:	e282c004 	add	ip, r2, #4	; 0x4
    3708:	e1a00800 	mov	r0, r0, lsl #16
    370c:	e15c0004 	cmp	ip, r4
    3710:	e1a00820 	mov	r0, r0, lsr #16
    3714:	2a00001f 	bcs	0x3798
    3718:	e59f3138 	ldr	r3, [pc, #312]	; 0x3858
    371c:	e59f6138 	ldr	r6, [pc, #312]	; 0x385c
    3720:	e5935000 	ldr	r5, [r3]
    3724:	ea000004 	b	0x373c
    3728:	e1500001 	cmp	r0, r1
    372c:	9a000020 	bls	0x37b4
    3730:	e08cc081 	add	ip, ip, r1, lsl #1
    3734:	e154000c 	cmp	r4, ip
    3738:	9a000016 	bls	0x3798
    373c:	e1dc10b0 	ldrh	r1, [ip]
    3740:	e3110902 	tst	r1, #32768	; 0x8000
    3744:	0afffff7 	beq	0x3728
    3748:	e3110901 	tst	r1, #16384	; 0x4000
    374c:	e2012c1e 	and	r2, r1, #7680	; 0x1e00
    3750:	17d624a2 	ldrneb	r2, [r6, r2, lsr #9]
    3754:	e1a0eb81 	mov	lr, r1, lsl #23
    3758:	e20130ff 	and	r3, r1, #255	; 0xff
    375c:	e1a0ebae 	mov	lr, lr, lsr #23
    3760:	e0833103 	add	r3, r3, r3, lsl #2
    3764:	e0853083 	add	r3, r5, r3, lsl #1
    3768:	10030e92 	mulne	r3, r2, lr
    376c:	12833001 	addne	r3, r3, #1	; 0x1
    3770:	11a030c3 	movne	r3, r3, asr #1
    3774:	12833004 	addne	r3, r3, #4	; 0x4
    3778:	05d32011 	ldreqb	r2, [r3, #17]
    377c:	05d33010 	ldreqb	r3, [r3, #16]
    3780:	11a03803 	movne	r3, r3, lsl #16
    3784:	11a03823 	movne	r3, r3, lsr #16
    3788:	01833402 	orreq	r3, r3, r2, lsl #8
    378c:	e08cc083 	add	ip, ip, r3, lsl #1
    3790:	e154000c 	cmp	r4, ip
    3794:	8affffe8 	bhi	0x373c
    3798:	e59f30c0 	ldr	r3, [pc, #192]	; 0x3860
    379c:	e5930000 	ldr	r0, [r3]
    37a0:	ebfffe11 	bl	0x2fec
    37a4:	e3a00000 	mov	r0, #0	; 0x0
    37a8:	e24bd018 	sub	sp, fp, #24	; 0x18
    37ac:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    37b0:	e12fff1e 	bx	lr
    37b4:	e1a03081 	mov	r3, r1, lsl #1
    37b8:	e19320bc 	ldrh	r2, [r3, ip]
    37bc:	e083300c 	add	r3, r3, ip
    37c0:	e1540003 	cmp	r4, r3
    37c4:	83a03000 	movhi	r3, #0	; 0x0
    37c8:	93a03001 	movls	r3, #1	; 0x1
    37cc:	e19337a2 	orrs	r3, r3, r2, lsr #15
    37d0:	e1a0e00c 	mov	lr, ip
    37d4:	1a00000b 	bne	0x3808
    37d8:	e0813002 	add	r3, r1, r2
    37dc:	e1a03803 	mov	r3, r3, lsl #16
    37e0:	e1a01823 	mov	r1, r3, lsr #16
    37e4:	e1cc10b0 	strh	r1, [ip]
    37e8:	e1a02081 	mov	r2, r1, lsl #1
    37ec:	e082300c 	add	r3, r2, ip
    37f0:	e19220bc 	ldrh	r2, [r2, ip]
    37f4:	e1530004 	cmp	r3, r4
    37f8:	33a03000 	movcc	r3, #0	; 0x0
    37fc:	23a03001 	movcs	r3, #1	; 0x1
    3800:	e19337a2 	orrs	r3, r3, r2, lsr #15
    3804:	0afffff3 	beq	0x37d8
    3808:	e1500001 	cmp	r0, r1
    380c:	2a000006 	bcs	0x382c
    3810:	e0603001 	rsb	r3, r0, r1
    3814:	e1a03803 	mov	r3, r3, lsl #16
    3818:	e1a03823 	mov	r3, r3, lsr #16
    381c:	e1a02083 	mov	r2, r3, lsl #1
    3820:	e1cc30b0 	strh	r3, [ip]
    3824:	e18200be 	strh	r0, [r2, lr]
    3828:	e082c00c 	add	ip, r2, ip
    382c:	e59f3030 	ldr	r3, [pc, #48]	; 0x3864
    3830:	e1d320b0 	ldrh	r2, [r3]
    3834:	e3a01000 	mov	r1, #0	; 0x0
    3838:	e0602002 	rsb	r2, r0, r2
    383c:	e1a0000c 	mov	r0, ip
    3840:	e1c320b0 	strh	r2, [r3]
    3844:	e1cc10b0 	strh	r1, [ip]
    3848:	e5cc1004 	strb	r1, [ip, #4]
    384c:	e5cc1005 	strb	r1, [ip, #5]
    3850:	eaffffd4 	b	0x37a8
    3854:	002078f0 	streqd	r7, [r0], -r0
    3858:	002079b0 	streqh	r7, [r0], -r0
    385c:	002078c4 	eoreq	r7, r0, r4, asr #17
    3860:	00207984 	eoreq	r7, r0, r4, lsl #19
    3864:	002078f6 	streqd	r7, [r0], -r6
    3868:	e1a0c00d 	mov	ip, sp
    386c:	e92dddf0 	stmdb	sp!, {r4, r5, r6, r7, r8, sl, fp, ip, lr, pc}
    3870:	e59f31a8 	ldr	r3, [pc, #424]	; 0x3a20
    3874:	e59f21a8 	ldr	r2, [pc, #424]	; 0x3a24
    3878:	e24cb004 	sub	fp, ip, #4	; 0x4
    387c:	e5926000 	ldr	r6, [r2]
    3880:	e593c000 	ldr	ip, [r3]
    3884:	e20070ff 	and	r7, r0, #255	; 0xff
    3888:	e0873107 	add	r3, r7, r7, lsl #2
    388c:	e0863083 	add	r3, r6, r3, lsl #1
    3890:	e59ce000 	ldr	lr, [ip]
    3894:	e5d31011 	ldrb	r1, [r3, #17]
    3898:	e5d32010 	ldrb	r2, [r3, #16]
    389c:	e28cc004 	add	ip, ip, #4	; 0x4
    38a0:	e15c000e 	cmp	ip, lr
    38a4:	e1825401 	orr	r5, r2, r1, lsl #8
    38a8:	e1824401 	orr	r4, r2, r1, lsl #8
    38ac:	2a000020 	bcs	0x3934
    38b0:	e2443004 	sub	r3, r4, #4	; 0x4
    38b4:	e1a03803 	mov	r3, r3, lsl #16
    38b8:	e59f8168 	ldr	r8, [pc, #360]	; 0x3a28
    38bc:	e1a0a823 	mov	sl, r3, lsr #16
    38c0:	ea000004 	b	0x38d8
    38c4:	e1550001 	cmp	r5, r1
    38c8:	9a000021 	bls	0x3954
    38cc:	e08cc081 	add	ip, ip, r1, lsl #1
    38d0:	e15e000c 	cmp	lr, ip
    38d4:	9a000016 	bls	0x3934
    38d8:	e1dc10b0 	ldrh	r1, [ip]
    38dc:	e3110902 	tst	r1, #32768	; 0x8000
    38e0:	0afffff7 	beq	0x38c4
    38e4:	e3110901 	tst	r1, #16384	; 0x4000
    38e8:	e2012c1e 	and	r2, r1, #7680	; 0x1e00
    38ec:	17d824a2 	ldrneb	r2, [r8, r2, lsr #9]
    38f0:	e1a00b81 	mov	r0, r1, lsl #23
    38f4:	e20130ff 	and	r3, r1, #255	; 0xff
    38f8:	e1a00ba0 	mov	r0, r0, lsr #23
    38fc:	e0833103 	add	r3, r3, r3, lsl #2
    3900:	e0863083 	add	r3, r6, r3, lsl #1
    3904:	10030092 	mulne	r3, r2, r0
    3908:	12833001 	addne	r3, r3, #1	; 0x1
    390c:	11a030c3 	movne	r3, r3, asr #1
    3910:	12833004 	addne	r3, r3, #4	; 0x4
    3914:	05d32011 	ldreqb	r2, [r3, #17]
    3918:	05d33010 	ldreqb	r3, [r3, #16]
    391c:	11a03803 	movne	r3, r3, lsl #16
    3920:	11a03823 	movne	r3, r3, lsr #16
    3924:	01833402 	orreq	r3, r3, r2, lsl #8
    3928:	e08cc083 	add	ip, ip, r3, lsl #1
    392c:	e15e000c 	cmp	lr, ip
    3930:	8affffe8 	bhi	0x38d8
    3934:	e59f30f0 	ldr	r3, [pc, #240]	; 0x3a2c
    3938:	e5930000 	ldr	r0, [r3]
    393c:	ebfffdaa 	bl	0x2fec
    3940:	e3a0c000 	mov	ip, #0	; 0x0
    3944:	e1a0000c 	mov	r0, ip
    3948:	e24bd024 	sub	sp, fp, #36	; 0x24
    394c:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    3950:	e12fff1e 	bx	lr
    3954:	e1a03081 	mov	r3, r1, lsl #1
    3958:	e19320bc 	ldrh	r2, [r3, ip]
    395c:	e083300c 	add	r3, r3, ip
    3960:	e15e0003 	cmp	lr, r3
    3964:	83a03000 	movhi	r3, #0	; 0x0
    3968:	93a03001 	movls	r3, #1	; 0x1
    396c:	e19337a2 	orrs	r3, r3, r2, lsr #15
    3970:	e1a0000c 	mov	r0, ip
    3974:	1a00000b 	bne	0x39a8
    3978:	e0813002 	add	r3, r1, r2
    397c:	e1a03803 	mov	r3, r3, lsl #16
    3980:	e1a01823 	mov	r1, r3, lsr #16
    3984:	e1cc10b0 	strh	r1, [ip]
    3988:	e1a02081 	mov	r2, r1, lsl #1
    398c:	e082300c 	add	r3, r2, ip
    3990:	e19220bc 	ldrh	r2, [r2, ip]
    3994:	e153000e 	cmp	r3, lr
    3998:	33a03000 	movcc	r3, #0	; 0x0
    399c:	23a03001 	movcs	r3, #1	; 0x1
    39a0:	e19337a2 	orrs	r3, r3, r2, lsr #15
    39a4:	0afffff3 	beq	0x3978
    39a8:	e1550001 	cmp	r5, r1
    39ac:	2a000006 	bcs	0x39cc
    39b0:	e0653001 	rsb	r3, r5, r1
    39b4:	e1a03803 	mov	r3, r3, lsl #16
    39b8:	e1a03823 	mov	r3, r3, lsr #16
    39bc:	e1a02083 	mov	r2, r3, lsl #1
    39c0:	e1cc30b0 	strh	r3, [ip]
    39c4:	e18250b0 	strh	r5, [r2, r0]
    39c8:	e082c00c 	add	ip, r2, ip
    39cc:	e59f005c 	ldr	r0, [pc, #92]	; 0x3a30
    39d0:	e1d030b0 	ldrh	r3, [r0]
    39d4:	e3a02000 	mov	r2, #0	; 0x0
    39d8:	e35a0000 	cmp	sl, #0	; 0x0
    39dc:	e0653003 	rsb	r3, r5, r3
    39e0:	e3871902 	orr	r1, r7, #32768	; 0x8000
    39e4:	e1c030b0 	strh	r3, [r0]
    39e8:	e5cc2005 	strb	r2, [ip, #5]
    39ec:	e5cc2004 	strb	r2, [ip, #4]
    39f0:	e1cc10b0 	strh	r1, [ip]
    39f4:	11a0200c 	movne	r2, ip
    39f8:	0affffd1 	beq	0x3944
    39fc:	e2443001 	sub	r3, r4, #1	; 0x1
    3a00:	e1a03803 	mov	r3, r3, lsl #16
    3a04:	e1a04823 	mov	r4, r3, lsr #16
    3a08:	e3540004 	cmp	r4, #4	; 0x4
    3a0c:	e3a03000 	mov	r3, #0	; 0x0
    3a10:	e1c230b8 	strh	r3, [r2, #8]
    3a14:	e2822002 	add	r2, r2, #2	; 0x2
    3a18:	1afffff7 	bne	0x39fc
    3a1c:	eaffffc8 	b	0x3944
    3a20:	002078f0 	streqd	r7, [r0], -r0
    3a24:	002079b0 	streqh	r7, [r0], -r0
    3a28:	002078c4 	eoreq	r7, r0, r4, asr #17
    3a2c:	00207984 	eoreq	r7, r0, r4, lsl #19
    3a30:	002078f6 	streqd	r7, [r0], -r6
    3a34:	e1a0c00d 	mov	ip, sp
    3a38:	e3510c02 	cmp	r1, #512	; 0x200
    3a3c:	e92dd9f0 	stmdb	sp!, {r4, r5, r6, r7, r8, fp, ip, lr, pc}
    3a40:	e24cb004 	sub	fp, ip, #4	; 0x4
    3a44:	2a000035 	bcs	0x3b20
    3a48:	e59f61c4 	ldr	r6, [pc, #452]	; 0x3c14
    3a4c:	e20050ff 	and	r5, r0, #255	; 0xff
    3a50:	e1a02801 	mov	r2, r1, lsl #16
    3a54:	e7d61005 	ldrb	r1, [r6, r5]
    3a58:	e1a0c822 	mov	ip, r2, lsr #16
    3a5c:	e0030c91 	mul	r3, r1, ip
    3a60:	e59f21b0 	ldr	r2, [pc, #432]	; 0x3c18
    3a64:	e5921000 	ldr	r1, [r2]
    3a68:	e2833001 	add	r3, r3, #1	; 0x1
    3a6c:	e1a030c3 	mov	r3, r3, asr #1
    3a70:	e591e000 	ldr	lr, [r1]
    3a74:	e2833004 	add	r3, r3, #4	; 0x4
    3a78:	e2810004 	add	r0, r1, #4	; 0x4
    3a7c:	e1a03803 	mov	r3, r3, lsl #16
    3a80:	e150000e 	cmp	r0, lr
    3a84:	e1a04823 	mov	r4, r3, lsr #16
    3a88:	e1a03823 	mov	r3, r3, lsr #16
    3a8c:	2a000023 	bcs	0x3b20
    3a90:	e59f2184 	ldr	r2, [pc, #388]	; 0x3c1c
    3a94:	e1a03803 	mov	r3, r3, lsl #16
    3a98:	e18c1485 	orr	r1, ip, r5, lsl #9
    3a9c:	e2433805 	sub	r3, r3, #327680	; 0x50000
    3aa0:	e5925000 	ldr	r5, [r2]
    3aa4:	e3818903 	orr	r8, r1, #49152	; 0xc000
    3aa8:	e1a07823 	mov	r7, r3, lsr #16
    3aac:	ea000004 	b	0x3ac4
    3ab0:	e1540001 	cmp	r4, r1
    3ab4:	9a000020 	bls	0x3b3c
    3ab8:	e0800081 	add	r0, r0, r1, lsl #1
    3abc:	e15e0000 	cmp	lr, r0
    3ac0:	9a000016 	bls	0x3b20
    3ac4:	e1d010b0 	ldrh	r1, [r0]
    3ac8:	e3110902 	tst	r1, #32768	; 0x8000
    3acc:	0afffff7 	beq	0x3ab0
    3ad0:	e3110901 	tst	r1, #16384	; 0x4000
    3ad4:	e2012c1e 	and	r2, r1, #7680	; 0x1e00
    3ad8:	17d624a2 	ldrneb	r2, [r6, r2, lsr #9]
    3adc:	e1a0cb81 	mov	ip, r1, lsl #23
    3ae0:	e20130ff 	and	r3, r1, #255	; 0xff
    3ae4:	e1a0cbac 	mov	ip, ip, lsr #23
    3ae8:	e0833103 	add	r3, r3, r3, lsl #2
    3aec:	e0853083 	add	r3, r5, r3, lsl #1
    3af0:	10030c92 	mulne	r3, r2, ip
    3af4:	12833001 	addne	r3, r3, #1	; 0x1
    3af8:	11a030c3 	movne	r3, r3, asr #1
    3afc:	12833004 	addne	r3, r3, #4	; 0x4
    3b00:	05d32011 	ldreqb	r2, [r3, #17]
    3b04:	05d33010 	ldreqb	r3, [r3, #16]
    3b08:	11a03803 	movne	r3, r3, lsl #16
    3b0c:	11a03823 	movne	r3, r3, lsr #16
    3b10:	01833402 	orreq	r3, r3, r2, lsl #8
    3b14:	e0800083 	add	r0, r0, r3, lsl #1
    3b18:	e15e0000 	cmp	lr, r0
    3b1c:	8affffe8 	bhi	0x3ac4
    3b20:	e59f30f8 	ldr	r3, [pc, #248]	; 0x3c20
    3b24:	e5930000 	ldr	r0, [r3]
    3b28:	ebfffd2f 	bl	0x2fec
    3b2c:	e3a00000 	mov	r0, #0	; 0x0
    3b30:	e24bd020 	sub	sp, fp, #32	; 0x20
    3b34:	e89d69f0 	ldmia	sp, {r4, r5, r6, r7, r8, fp, sp, lr}
    3b38:	e12fff1e 	bx	lr
    3b3c:	e1a03081 	mov	r3, r1, lsl #1
    3b40:	e19320b0 	ldrh	r2, [r3, r0]
    3b44:	e0833000 	add	r3, r3, r0
    3b48:	e15e0003 	cmp	lr, r3
    3b4c:	83a03000 	movhi	r3, #0	; 0x0
    3b50:	93a03001 	movls	r3, #1	; 0x1
    3b54:	e19337a2 	orrs	r3, r3, r2, lsr #15
    3b58:	e1a0c000 	mov	ip, r0
    3b5c:	1a00000b 	bne	0x3b90
    3b60:	e0813002 	add	r3, r1, r2
    3b64:	e1a03803 	mov	r3, r3, lsl #16
    3b68:	e1a01823 	mov	r1, r3, lsr #16
    3b6c:	e1c010b0 	strh	r1, [r0]
    3b70:	e1a02081 	mov	r2, r1, lsl #1
    3b74:	e0823000 	add	r3, r2, r0
    3b78:	e19220b0 	ldrh	r2, [r2, r0]
    3b7c:	e153000e 	cmp	r3, lr
    3b80:	33a03000 	movcc	r3, #0	; 0x0
    3b84:	23a03001 	movcs	r3, #1	; 0x1
    3b88:	e19337a2 	orrs	r3, r3, r2, lsr #15
    3b8c:	0afffff3 	beq	0x3b60
    3b90:	e1510004 	cmp	r1, r4
    3b94:	9a000006 	bls	0x3bb4
    3b98:	e0643001 	rsb	r3, r4, r1
    3b9c:	e1a03803 	mov	r3, r3, lsl #16
    3ba0:	e1a03823 	mov	r3, r3, lsr #16
    3ba4:	e1a02083 	mov	r2, r3, lsl #1
    3ba8:	e1c030b0 	strh	r3, [r0]
    3bac:	e18240bc 	strh	r4, [r2, ip]
    3bb0:	e0820000 	add	r0, r2, r0
    3bb4:	e59f3068 	ldr	r3, [pc, #104]	; 0x3c24
    3bb8:	e3a0e801 	mov	lr, #65536	; 0x10000
    3bbc:	e1d320b0 	ldrh	r2, [r3]
    3bc0:	e24ee001 	sub	lr, lr, #1	; 0x1
    3bc4:	e3a01000 	mov	r1, #0	; 0x0
    3bc8:	e0642002 	rsb	r2, r4, r2
    3bcc:	e157000e 	cmp	r7, lr
    3bd0:	e1c320b0 	strh	r2, [r3]
    3bd4:	e1a0c007 	mov	ip, r7
    3bd8:	e5c01005 	strb	r1, [r0, #5]
    3bdc:	e5c01004 	strb	r1, [r0, #4]
    3be0:	e1c080b0 	strh	r8, [r0]
    3be4:	11a0100e 	movne	r1, lr
    3be8:	11a02000 	movne	r2, r0
    3bec:	0affffcf 	beq	0x3b30
    3bf0:	e24c3001 	sub	r3, ip, #1	; 0x1
    3bf4:	e1a03803 	mov	r3, r3, lsl #16
    3bf8:	e1a0c823 	mov	ip, r3, lsr #16
    3bfc:	e15c0001 	cmp	ip, r1
    3c00:	e3a03000 	mov	r3, #0	; 0x0
    3c04:	e1c230b8 	strh	r3, [r2, #8]
    3c08:	e2822002 	add	r2, r2, #2	; 0x2
    3c0c:	1afffff7 	bne	0x3bf0
    3c10:	eaffffc6 	b	0x3b30
    3c14:	002078c4 	eoreq	r7, r0, r4, asr #17
    3c18:	002078f0 	streqd	r7, [r0], -r0
    3c1c:	002079b0 	streqh	r7, [r0], -r0
    3c20:	00207984 	eoreq	r7, r0, r4, lsl #19
    3c24:	002078f6 	streqd	r7, [r0], -r6
    3c28:	e1a0c00d 	mov	ip, sp
    3c2c:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    3c30:	e1d030b0 	ldrh	r3, [r0]
    3c34:	e2033c1e 	and	r3, r3, #7680	; 0x1e00
    3c38:	e1a054a3 	mov	r5, r3, lsr #9
    3c3c:	e1a04000 	mov	r4, r0
    3c40:	e24cb004 	sub	fp, ip, #4	; 0x4
    3c44:	e1a00005 	mov	r0, r5
    3c48:	ebffff79 	bl	0x3a34
    3c4c:	e2506000 	subs	r6, r0, #0	; 0x0
    3c50:	e59f7070 	ldr	r7, [pc, #112]	; 0x3cc8
    3c54:	e2841008 	add	r1, r4, #8	; 0x8
    3c58:	e2860008 	add	r0, r6, #8	; 0x8
    3c5c:	0a000015 	beq	0x3cb8
    3c60:	e1d430b0 	ldrh	r3, [r4]
    3c64:	e7d7c005 	ldrb	ip, [r7, r5]
    3c68:	e1a03b83 	mov	r3, r3, lsl #23
    3c6c:	e1a03ba3 	mov	r3, r3, lsr #23
    3c70:	e002039c 	mul	r2, ip, r3
    3c74:	eb000495 	bl	0x4ed0
    3c78:	e1d420b0 	ldrh	r2, [r4]
    3c7c:	e2023c1e 	and	r3, r2, #7680	; 0x1e00
    3c80:	e7d714a3 	ldrb	r1, [r7, r3, lsr #9]
    3c84:	e1a02b82 	mov	r2, r2, lsl #23
    3c88:	e1a02ba2 	mov	r2, r2, lsr #23
    3c8c:	e0030291 	mul	r3, r1, r2
    3c90:	e2833001 	add	r3, r3, #1	; 0x1
    3c94:	e59f1030 	ldr	r1, [pc, #48]	; 0x3ccc
    3c98:	e1a030c3 	mov	r3, r3, asr #1
    3c9c:	e2833004 	add	r3, r3, #4	; 0x4
    3ca0:	e1d120b0 	ldrh	r2, [r1]
    3ca4:	e1a03803 	mov	r3, r3, lsl #16
    3ca8:	e1a03823 	mov	r3, r3, lsr #16
    3cac:	e0832002 	add	r2, r3, r2
    3cb0:	e1c120b0 	strh	r2, [r1]
    3cb4:	e1c430b0 	strh	r3, [r4]
    3cb8:	e1a00006 	mov	r0, r6
    3cbc:	e24bd01c 	sub	sp, fp, #28	; 0x1c
    3cc0:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    3cc4:	e12fff1e 	bx	lr
    3cc8:	002078c4 	eoreq	r7, r0, r4, asr #17
    3ccc:	002078f6 	streqd	r7, [r0], -r6
    3cd0:	e1a0c00d 	mov	ip, sp
    3cd4:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    3cd8:	e24cb004 	sub	fp, ip, #4	; 0x4
    3cdc:	e24dd094 	sub	sp, sp, #148	; 0x94
    3ce0:	e21250ff 	ands	r5, r2, #255	; 0xff
    3ce4:	e20140ff 	and	r4, r1, #255	; 0xff
    3ce8:	e20010ff 	and	r1, r0, #255	; 0xff
    3cec:	e1a06000 	mov	r6, r0
    3cf0:	e50b3088 	str	r3, [fp, #-136]
    3cf4:	e50b108c 	str	r1, [fp, #-140]
    3cf8:	050b5084 	streq	r5, [fp, #-132]
    3cfc:	0a000123 	beq	0x4190
    3d00:	e3540001 	cmp	r4, #1	; 0x1
    3d04:	1a000005 	bne	0x3d20
    3d08:	e24b008c 	sub	r0, fp, #140	; 0x8c
    3d0c:	e8900005 	ldmia	r0, {r0, r2}
    3d10:	e5921000 	ldr	r1, [r2]
    3d14:	e24bd028 	sub	sp, fp, #40	; 0x28
    3d18:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    3d1c:	eaffff44 	b	0x3a34
    3d20:	e51b3088 	ldr	r3, [fp, #-136]
    3d24:	e3a00000 	mov	r0, #0	; 0x0
    3d28:	e5931000 	ldr	r1, [r3]
    3d2c:	ebffff40 	bl	0x3a34
    3d30:	e3500000 	cmp	r0, #0	; 0x0
    3d34:	e50b0084 	str	r0, [fp, #-132]
    3d38:	0a000114 	beq	0x4190
    3d3c:	e51b1088 	ldr	r1, [fp, #-136]
    3d40:	e5913000 	ldr	r3, [r1]
    3d44:	e2433001 	sub	r3, r3, #1	; 0x1
    3d48:	e3730001 	cmn	r3, #1	; 0x1
    3d4c:	e50b303c 	str	r3, [fp, #-60]
    3d50:	e5813000 	str	r3, [r1]
    3d54:	0a00010d 	beq	0x4190
    3d58:	e2452001 	sub	r2, r5, #1	; 0x1
    3d5c:	e20220ff 	and	r2, r2, #255	; 0xff
    3d60:	e50b2068 	str	r2, [fp, #-104]
    3d64:	e2422001 	sub	r2, r2, #1	; 0x1
    3d68:	e20220ff 	and	r2, r2, #255	; 0xff
    3d6c:	e50b2060 	str	r2, [fp, #-96]
    3d70:	e2422001 	sub	r2, r2, #1	; 0x1
    3d74:	e20220ff 	and	r2, r2, #255	; 0xff
    3d78:	e2443001 	sub	r3, r4, #1	; 0x1
    3d7c:	e50b2058 	str	r2, [fp, #-88]
    3d80:	e2422001 	sub	r2, r2, #1	; 0x1
    3d84:	e20330ff 	and	r3, r3, #255	; 0xff
    3d88:	e20220ff 	and	r2, r2, #255	; 0xff
    3d8c:	e50b306c 	str	r3, [fp, #-108]
    3d90:	e50b2050 	str	r2, [fp, #-80]
    3d94:	e2433001 	sub	r3, r3, #1	; 0x1
    3d98:	e2422001 	sub	r2, r2, #1	; 0x1
    3d9c:	e20330ff 	and	r3, r3, #255	; 0xff
    3da0:	e20220ff 	and	r2, r2, #255	; 0xff
    3da4:	e50b2048 	str	r2, [fp, #-72]
    3da8:	e50b3064 	str	r3, [fp, #-100]
    3dac:	e2433001 	sub	r3, r3, #1	; 0x1
    3db0:	e2812004 	add	r2, r1, #4	; 0x4
    3db4:	e20330ff 	and	r3, r3, #255	; 0xff
    3db8:	e51b1048 	ldr	r1, [fp, #-72]
    3dbc:	e50b305c 	str	r3, [fp, #-92]
    3dc0:	e2433001 	sub	r3, r3, #1	; 0x1
    3dc4:	e20330ff 	and	r3, r3, #255	; 0xff
    3dc8:	e50b2070 	str	r2, [fp, #-112]
    3dcc:	e2412001 	sub	r2, r1, #1	; 0x1
    3dd0:	e20220ff 	and	r2, r2, #255	; 0xff
    3dd4:	e50b3054 	str	r3, [fp, #-84]
    3dd8:	e2433001 	sub	r3, r3, #1	; 0x1
    3ddc:	e20330ff 	and	r3, r3, #255	; 0xff
    3de0:	e50b2040 	str	r2, [fp, #-64]
    3de4:	e51b2070 	ldr	r2, [fp, #-112]
    3de8:	e50b304c 	str	r3, [fp, #-76]
    3dec:	e2433001 	sub	r3, r3, #1	; 0x1
    3df0:	e20330ff 	and	r3, r3, #255	; 0xff
    3df4:	e20660ff 	and	r6, r6, #255	; 0xff
    3df8:	e2822004 	add	r2, r2, #4	; 0x4
    3dfc:	e50b3044 	str	r3, [fp, #-68]
    3e00:	e50b60ac 	str	r6, [fp, #-172]
    3e04:	e50b20a8 	str	r2, [fp, #-168]
    3e08:	e51b3068 	ldr	r3, [fp, #-104]
    3e0c:	e3530000 	cmp	r3, #0	; 0x0
    3e10:	0a0000fe 	beq	0x4210
    3e14:	e51b106c 	ldr	r1, [fp, #-108]
    3e18:	e3510001 	cmp	r1, #1	; 0x1
    3e1c:	0a000102 	beq	0x422c
    3e20:	e51b3070 	ldr	r3, [fp, #-112]
    3e24:	e3a00000 	mov	r0, #0	; 0x0
    3e28:	e5931000 	ldr	r1, [r3]
    3e2c:	ebffff00 	bl	0x3a34
    3e30:	e3500000 	cmp	r0, #0	; 0x0
    3e34:	e50b0080 	str	r0, [fp, #-128]
    3e38:	0a0000f4 	beq	0x4210
    3e3c:	e51b1070 	ldr	r1, [fp, #-112]
    3e40:	e5913000 	ldr	r3, [r1]
    3e44:	e2433001 	sub	r3, r3, #1	; 0x1
    3e48:	e3730001 	cmn	r3, #1	; 0x1
    3e4c:	e50b3038 	str	r3, [fp, #-56]
    3e50:	e5813000 	str	r3, [r1]
    3e54:	0a0000c2 	beq	0x4164
    3e58:	e51b20a8 	ldr	r2, [fp, #-168]
    3e5c:	e51b30ac 	ldr	r3, [fp, #-172]
    3e60:	e2822004 	add	r2, r2, #4	; 0x4
    3e64:	e20330ff 	and	r3, r3, #255	; 0xff
    3e68:	e50b20a0 	str	r2, [fp, #-160]
    3e6c:	e50b30a4 	str	r3, [fp, #-164]
    3e70:	e51b1060 	ldr	r1, [fp, #-96]
    3e74:	e3510000 	cmp	r1, #0	; 0x0
    3e78:	0a0000dd 	beq	0x41f4
    3e7c:	e51b2064 	ldr	r2, [fp, #-100]
    3e80:	e3520001 	cmp	r2, #1	; 0x1
    3e84:	0a0000e3 	beq	0x4218
    3e88:	e51b20a8 	ldr	r2, [fp, #-168]
    3e8c:	e3a00000 	mov	r0, #0	; 0x0
    3e90:	e5921000 	ldr	r1, [r2]
    3e94:	ebfffee6 	bl	0x3a34
    3e98:	e3500000 	cmp	r0, #0	; 0x0
    3e9c:	e50b007c 	str	r0, [fp, #-124]
    3ea0:	0a0000d3 	beq	0x41f4
    3ea4:	e51b10a8 	ldr	r1, [fp, #-168]
    3ea8:	e5913000 	ldr	r3, [r1]
    3eac:	e2433001 	sub	r3, r3, #1	; 0x1
    3eb0:	e3730001 	cmn	r3, #1	; 0x1
    3eb4:	e50b3034 	str	r3, [fp, #-52]
    3eb8:	e5813000 	str	r3, [r1]
    3ebc:	0a00009c 	beq	0x4134
    3ec0:	e51b20a0 	ldr	r2, [fp, #-160]
    3ec4:	e51b30a4 	ldr	r3, [fp, #-164]
    3ec8:	e2822004 	add	r2, r2, #4	; 0x4
    3ecc:	e20330ff 	and	r3, r3, #255	; 0xff
    3ed0:	e50b2098 	str	r2, [fp, #-152]
    3ed4:	e50b309c 	str	r3, [fp, #-156]
    3ed8:	e51b1058 	ldr	r1, [fp, #-88]
    3edc:	e3510000 	cmp	r1, #0	; 0x0
    3ee0:	0a0000bc 	beq	0x41d8
    3ee4:	e51b205c 	ldr	r2, [fp, #-92]
    3ee8:	e3520001 	cmp	r2, #1	; 0x1
    3eec:	0a0000c2 	beq	0x41fc
    3ef0:	e51b20a0 	ldr	r2, [fp, #-160]
    3ef4:	e3a00000 	mov	r0, #0	; 0x0
    3ef8:	e5921000 	ldr	r1, [r2]
    3efc:	ebfffecc 	bl	0x3a34
    3f00:	e3500000 	cmp	r0, #0	; 0x0
    3f04:	e50b0078 	str	r0, [fp, #-120]
    3f08:	0a0000b2 	beq	0x41d8
    3f0c:	e51b10a0 	ldr	r1, [fp, #-160]
    3f10:	e5913000 	ldr	r3, [r1]
    3f14:	e2433001 	sub	r3, r3, #1	; 0x1
    3f18:	e3730001 	cmn	r3, #1	; 0x1
    3f1c:	e50b3030 	str	r3, [fp, #-48]
    3f20:	e5813000 	str	r3, [r1]
    3f24:	0a000076 	beq	0x4104
    3f28:	e51b2098 	ldr	r2, [fp, #-152]
    3f2c:	e51b309c 	ldr	r3, [fp, #-156]
    3f30:	e2828004 	add	r8, r2, #4	; 0x4
    3f34:	e24b1044 	sub	r1, fp, #68	; 0x44
    3f38:	e8910006 	ldmia	r1, {r1, r2}
    3f3c:	e20330ff 	and	r3, r3, #255	; 0xff
    3f40:	e2411001 	sub	r1, r1, #1	; 0x1
    3f44:	e2422001 	sub	r2, r2, #1	; 0x1
    3f48:	e50b3094 	str	r3, [fp, #-148]
    3f4c:	e50b10b8 	str	r1, [fp, #-184]
    3f50:	e50b20bc 	str	r2, [fp, #-188]
    3f54:	e51b3050 	ldr	r3, [fp, #-80]
    3f58:	e3530000 	cmp	r3, #0	; 0x0
    3f5c:	0a000097 	beq	0x41c0
    3f60:	e51b1054 	ldr	r1, [fp, #-84]
    3f64:	e3510001 	cmp	r1, #1	; 0x1
    3f68:	0a00009c 	beq	0x41e0
    3f6c:	e51b3098 	ldr	r3, [fp, #-152]
    3f70:	e3a00000 	mov	r0, #0	; 0x0
    3f74:	e5931000 	ldr	r1, [r3]
    3f78:	ebfffead 	bl	0x3a34
    3f7c:	e3500000 	cmp	r0, #0	; 0x0
    3f80:	e50b0074 	str	r0, [fp, #-116]
    3f84:	0a00008d 	beq	0x41c0
    3f88:	e51b1098 	ldr	r1, [fp, #-152]
    3f8c:	e5913000 	ldr	r3, [r1]
    3f90:	e2433001 	sub	r3, r3, #1	; 0x1
    3f94:	e3730001 	cmn	r3, #1	; 0x1
    3f98:	e50b302c 	str	r3, [fp, #-44]
    3f9c:	e5813000 	str	r3, [r1]
    3fa0:	0a00004b 	beq	0x40d4
    3fa4:	e51b2094 	ldr	r2, [fp, #-148]
    3fa8:	e24b10bc 	sub	r1, fp, #188	; 0xbc
    3fac:	e891000a 	ldmia	r1, {r1, r3}
    3fb0:	e20220ff 	and	r2, r2, #255	; 0xff
    3fb4:	e20330ff 	and	r3, r3, #255	; 0xff
    3fb8:	e20110ff 	and	r1, r1, #255	; 0xff
    3fbc:	e50b2090 	str	r2, [fp, #-144]
    3fc0:	e50b30b0 	str	r3, [fp, #-176]
    3fc4:	e50b10b4 	str	r1, [fp, #-180]
    3fc8:	e2886004 	add	r6, r8, #4	; 0x4
    3fcc:	e51b2048 	ldr	r2, [fp, #-72]
    3fd0:	e3520000 	cmp	r2, #0	; 0x0
    3fd4:	0a000073 	beq	0x41a8
    3fd8:	e51b304c 	ldr	r3, [fp, #-76]
    3fdc:	e3530001 	cmp	r3, #1	; 0x1
    3fe0:	0a000078 	beq	0x41c8
    3fe4:	e3a00000 	mov	r0, #0	; 0x0
    3fe8:	e5981000 	ldr	r1, [r8]
    3fec:	ebfffe90 	bl	0x3a34
    3ff0:	e2509000 	subs	r9, r0, #0	; 0x0
    3ff4:	0a00006b 	beq	0x41a8
    3ff8:	e5983000 	ldr	r3, [r8]
    3ffc:	e2437001 	sub	r7, r3, #1	; 0x1
    4000:	e3770001 	cmn	r7, #1	; 0x1
    4004:	e5887000 	str	r7, [r8]
    4008:	0a000024 	beq	0x40a0
    400c:	e286a004 	add	sl, r6, #4	; 0x4
    4010:	e51b1040 	ldr	r1, [fp, #-64]
    4014:	e3510000 	cmp	r1, #0	; 0x0
    4018:	0a000060 	beq	0x41a0
    401c:	e51b2044 	ldr	r2, [fp, #-68]
    4020:	e3520001 	cmp	r2, #1	; 0x1
    4024:	0a000061 	beq	0x41b0
    4028:	e3a00000 	mov	r0, #0	; 0x0
    402c:	e5961000 	ldr	r1, [r6]
    4030:	ebfffe7f 	bl	0x3a34
    4034:	e2505000 	subs	r5, r0, #0	; 0x0
    4038:	0a000058 	beq	0x41a0
    403c:	e5963000 	ldr	r3, [r6]
    4040:	e2434001 	sub	r4, r3, #1	; 0x1
    4044:	e3740001 	cmn	r4, #1	; 0x1
    4048:	e5864000 	str	r4, [r6]
    404c:	0a00000b 	beq	0x4080
    4050:	e51b20b4 	ldr	r2, [fp, #-180]
    4054:	e1a0300a 	mov	r3, sl
    4058:	e51b0090 	ldr	r0, [fp, #-144]
    405c:	e51b10b0 	ldr	r1, [fp, #-176]
    4060:	ebffff1a 	bl	0x3cd0
    4064:	e0853104 	add	r3, r5, r4, lsl #2
    4068:	e5830008 	str	r0, [r3, #8]
    406c:	e5962000 	ldr	r2, [r6]
    4070:	e2424001 	sub	r4, r2, #1	; 0x1
    4074:	e3740001 	cmn	r4, #1	; 0x1
    4078:	e5864000 	str	r4, [r6]
    407c:	1afffff3 	bne	0x4050
    4080:	e1a00005 	mov	r0, r5
    4084:	e0893107 	add	r3, r9, r7, lsl #2
    4088:	e5830008 	str	r0, [r3, #8]
    408c:	e5982000 	ldr	r2, [r8]
    4090:	e2427001 	sub	r7, r2, #1	; 0x1
    4094:	e3770001 	cmn	r7, #1	; 0x1
    4098:	e5887000 	str	r7, [r8]
    409c:	1affffdb 	bne	0x4010
    40a0:	e1a00009 	mov	r0, r9
    40a4:	e51b202c 	ldr	r2, [fp, #-44]
    40a8:	e51b1074 	ldr	r1, [fp, #-116]
    40ac:	e0813102 	add	r3, r1, r2, lsl #2
    40b0:	e5830008 	str	r0, [r3, #8]
    40b4:	e51b3098 	ldr	r3, [fp, #-152]
    40b8:	e5932000 	ldr	r2, [r3]
    40bc:	e2422001 	sub	r2, r2, #1	; 0x1
    40c0:	e3720001 	cmn	r2, #1	; 0x1
    40c4:	e50b202c 	str	r2, [fp, #-44]
    40c8:	e5832000 	str	r2, [r3]
    40cc:	1affffbe 	bne	0x3fcc
    40d0:	e1a00001 	mov	r0, r1
    40d4:	e51b2030 	ldr	r2, [fp, #-48]
    40d8:	e51b1078 	ldr	r1, [fp, #-120]
    40dc:	e0813102 	add	r3, r1, r2, lsl #2
    40e0:	e5830008 	str	r0, [r3, #8]
    40e4:	e51b30a0 	ldr	r3, [fp, #-160]
    40e8:	e5932000 	ldr	r2, [r3]
    40ec:	e2422001 	sub	r2, r2, #1	; 0x1
    40f0:	e3720001 	cmn	r2, #1	; 0x1
    40f4:	e50b2030 	str	r2, [fp, #-48]
    40f8:	e5832000 	str	r2, [r3]
    40fc:	1affff94 	bne	0x3f54
    4100:	e1a00001 	mov	r0, r1
    4104:	e51b2034 	ldr	r2, [fp, #-52]
    4108:	e51b107c 	ldr	r1, [fp, #-124]
    410c:	e0813102 	add	r3, r1, r2, lsl #2
    4110:	e5830008 	str	r0, [r3, #8]
    4114:	e51b30a8 	ldr	r3, [fp, #-168]
    4118:	e5932000 	ldr	r2, [r3]
    411c:	e2422001 	sub	r2, r2, #1	; 0x1
    4120:	e3720001 	cmn	r2, #1	; 0x1
    4124:	e50b2034 	str	r2, [fp, #-52]
    4128:	e5832000 	str	r2, [r3]
    412c:	1affff69 	bne	0x3ed8
    4130:	e1a00001 	mov	r0, r1
    4134:	e51b2038 	ldr	r2, [fp, #-56]
    4138:	e51b1080 	ldr	r1, [fp, #-128]
    413c:	e0813102 	add	r3, r1, r2, lsl #2
    4140:	e5830008 	str	r0, [r3, #8]
    4144:	e51b3070 	ldr	r3, [fp, #-112]
    4148:	e5932000 	ldr	r2, [r3]
    414c:	e2422001 	sub	r2, r2, #1	; 0x1
    4150:	e3720001 	cmn	r2, #1	; 0x1
    4154:	e50b2038 	str	r2, [fp, #-56]
    4158:	e5832000 	str	r2, [r3]
    415c:	1affff43 	bne	0x3e70
    4160:	e1a00001 	mov	r0, r1
    4164:	e51b203c 	ldr	r2, [fp, #-60]
    4168:	e51b1084 	ldr	r1, [fp, #-132]
    416c:	e0813102 	add	r3, r1, r2, lsl #2
    4170:	e5830008 	str	r0, [r3, #8]
    4174:	e51b3088 	ldr	r3, [fp, #-136]
    4178:	e5932000 	ldr	r2, [r3]
    417c:	e2422001 	sub	r2, r2, #1	; 0x1
    4180:	e3720001 	cmn	r2, #1	; 0x1
    4184:	e50b203c 	str	r2, [fp, #-60]
    4188:	e5832000 	str	r2, [r3]
    418c:	1affff1d 	bne	0x3e08
    4190:	e51b0084 	ldr	r0, [fp, #-132]
    4194:	e24bd028 	sub	sp, fp, #40	; 0x28
    4198:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    419c:	e12fff1e 	bx	lr
    41a0:	e3a00000 	mov	r0, #0	; 0x0
    41a4:	eaffffb6 	b	0x4084
    41a8:	e3a00000 	mov	r0, #0	; 0x0
    41ac:	eaffffbc 	b	0x40a4
    41b0:	e51b0090 	ldr	r0, [fp, #-144]
    41b4:	e5961000 	ldr	r1, [r6]
    41b8:	ebfffe1d 	bl	0x3a34
    41bc:	eaffffb0 	b	0x4084
    41c0:	e3a00000 	mov	r0, #0	; 0x0
    41c4:	eaffffc2 	b	0x40d4
    41c8:	e51b0094 	ldr	r0, [fp, #-148]
    41cc:	e5981000 	ldr	r1, [r8]
    41d0:	ebfffe17 	bl	0x3a34
    41d4:	eaffffb2 	b	0x40a4
    41d8:	e3a00000 	mov	r0, #0	; 0x0
    41dc:	eaffffc8 	b	0x4104
    41e0:	e24b009c 	sub	r0, fp, #156	; 0x9c
    41e4:	e8900005 	ldmia	r0, {r0, r2}
    41e8:	e5921000 	ldr	r1, [r2]
    41ec:	ebfffe10 	bl	0x3a34
    41f0:	eaffffb7 	b	0x40d4
    41f4:	e3a00000 	mov	r0, #0	; 0x0
    41f8:	eaffffcd 	b	0x4134
    41fc:	e24b00a4 	sub	r0, fp, #164	; 0xa4
    4200:	e8900009 	ldmia	r0, {r0, r3}
    4204:	e5931000 	ldr	r1, [r3]
    4208:	ebfffe09 	bl	0x3a34
    420c:	eaffffbc 	b	0x4104
    4210:	e3a00000 	mov	r0, #0	; 0x0
    4214:	eaffffd2 	b	0x4164
    4218:	e24b00ac 	sub	r0, fp, #172	; 0xac
    421c:	e8900009 	ldmia	r0, {r0, r3}
    4220:	e5931000 	ldr	r1, [r3]
    4224:	ebfffe02 	bl	0x3a34
    4228:	eaffffc1 	b	0x4134
    422c:	e51b2070 	ldr	r2, [fp, #-112]
    4230:	e51b008c 	ldr	r0, [fp, #-140]
    4234:	e5921000 	ldr	r1, [r2]
    4238:	ebfffdfd 	bl	0x3a34
    423c:	eaffffc8 	b	0x4164
    4240:	e1a0c00d 	mov	ip, sp
    4244:	e92dddf0 	stmdb	sp!, {r4, r5, r6, r7, r8, sl, fp, ip, lr, pc}
    4248:	e59f51cc 	ldr	r5, [pc, #460]	; 0x441c
    424c:	e20060ff 	and	r6, r0, #255	; 0xff
    4250:	e0863106 	add	r3, r6, r6, lsl #2
    4254:	e5950000 	ldr	r0, [r5]
    4258:	e1a04083 	mov	r4, r3, lsl #1
    425c:	e0840000 	add	r0, r4, r0
    4260:	e24cb004 	sub	fp, ip, #4	; 0x4
    4264:	e2800010 	add	r0, r0, #16	; 0x10
    4268:	eb00015b 	bl	0x47dc
    426c:	e3500000 	cmp	r0, #0	; 0x0
    4270:	1a00002f 	bne	0x4334
    4274:	e59f31a4 	ldr	r3, [pc, #420]	; 0x4420
    4278:	e5957000 	ldr	r7, [r5]
    427c:	e5931000 	ldr	r1, [r3]
    4280:	e0842007 	add	r2, r4, r7
    4284:	e591e000 	ldr	lr, [r1]
    4288:	e5d20011 	ldrb	r0, [r2, #17]
    428c:	e5d23010 	ldrb	r3, [r2, #16]
    4290:	e2811004 	add	r1, r1, #4	; 0x4
    4294:	e151000e 	cmp	r1, lr
    4298:	e1835400 	orr	r5, r3, r0, lsl #8
    429c:	e1834400 	orr	r4, r3, r0, lsl #8
    42a0:	2a000020 	bcs	0x4328
    42a4:	e2443004 	sub	r3, r4, #4	; 0x4
    42a8:	e1a03803 	mov	r3, r3, lsl #16
    42ac:	e59f8170 	ldr	r8, [pc, #368]	; 0x4424
    42b0:	e1a0a823 	mov	sl, r3, lsr #16
    42b4:	ea000004 	b	0x42cc
    42b8:	e1550000 	cmp	r5, r0
    42bc:	9a000021 	bls	0x4348
    42c0:	e0811080 	add	r1, r1, r0, lsl #1
    42c4:	e15e0001 	cmp	lr, r1
    42c8:	9a000016 	bls	0x4328
    42cc:	e1d100b0 	ldrh	r0, [r1]
    42d0:	e3100902 	tst	r0, #32768	; 0x8000
    42d4:	0afffff7 	beq	0x42b8
    42d8:	e3100901 	tst	r0, #16384	; 0x4000
    42dc:	e2002c1e 	and	r2, r0, #7680	; 0x1e00
    42e0:	17d824a2 	ldrneb	r2, [r8, r2, lsr #9]
    42e4:	e1a0cb80 	mov	ip, r0, lsl #23
    42e8:	e20030ff 	and	r3, r0, #255	; 0xff
    42ec:	e1a0cbac 	mov	ip, ip, lsr #23
    42f0:	e0833103 	add	r3, r3, r3, lsl #2
    42f4:	e0873083 	add	r3, r7, r3, lsl #1
    42f8:	10030c92 	mulne	r3, r2, ip
    42fc:	12833001 	addne	r3, r3, #1	; 0x1
    4300:	11a030c3 	movne	r3, r3, asr #1
    4304:	12833004 	addne	r3, r3, #4	; 0x4
    4308:	05d32011 	ldreqb	r2, [r3, #17]
    430c:	05d33010 	ldreqb	r3, [r3, #16]
    4310:	11a03803 	movne	r3, r3, lsl #16
    4314:	11a03823 	movne	r3, r3, lsr #16
    4318:	01833402 	orreq	r3, r3, r2, lsl #8
    431c:	e0811083 	add	r1, r1, r3, lsl #1
    4320:	e15e0001 	cmp	lr, r1
    4324:	8affffe8 	bhi	0x42cc
    4328:	e59f30f8 	ldr	r3, [pc, #248]	; 0x4428
    432c:	e5930000 	ldr	r0, [r3]
    4330:	ebfffb2d 	bl	0x2fec
    4334:	e3a0c000 	mov	ip, #0	; 0x0
    4338:	e1a0000c 	mov	r0, ip
    433c:	e24bd024 	sub	sp, fp, #36	; 0x24
    4340:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    4344:	e12fff1e 	bx	lr
    4348:	e1a03080 	mov	r3, r0, lsl #1
    434c:	e19320b1 	ldrh	r2, [r3, r1]
    4350:	e0833001 	add	r3, r3, r1
    4354:	e15e0003 	cmp	lr, r3
    4358:	83a03000 	movhi	r3, #0	; 0x0
    435c:	93a03001 	movls	r3, #1	; 0x1
    4360:	e19337a2 	orrs	r3, r3, r2, lsr #15
    4364:	e1a0c001 	mov	ip, r1
    4368:	1a00000b 	bne	0x439c
    436c:	e0803002 	add	r3, r0, r2
    4370:	e1a03803 	mov	r3, r3, lsl #16
    4374:	e1a00823 	mov	r0, r3, lsr #16
    4378:	e1c100b0 	strh	r0, [r1]
    437c:	e1a02080 	mov	r2, r0, lsl #1
    4380:	e0823001 	add	r3, r2, r1
    4384:	e19220b1 	ldrh	r2, [r2, r1]
    4388:	e153000e 	cmp	r3, lr
    438c:	33a03000 	movcc	r3, #0	; 0x0
    4390:	23a03001 	movcs	r3, #1	; 0x1
    4394:	e19337a2 	orrs	r3, r3, r2, lsr #15
    4398:	0afffff3 	beq	0x436c
    439c:	e1550000 	cmp	r5, r0
    43a0:	3a000015 	bcc	0x43fc
    43a4:	e59f0080 	ldr	r0, [pc, #128]	; 0x442c
    43a8:	e1d030b0 	ldrh	r3, [r0]
    43ac:	e1a0c001 	mov	ip, r1
    43b0:	e3a02000 	mov	r2, #0	; 0x0
    43b4:	e35a0000 	cmp	sl, #0	; 0x0
    43b8:	e0653003 	rsb	r3, r5, r3
    43bc:	e3861902 	orr	r1, r6, #32768	; 0x8000
    43c0:	e1c030b0 	strh	r3, [r0]
    43c4:	e5cc2005 	strb	r2, [ip, #5]
    43c8:	e5cc2004 	strb	r2, [ip, #4]
    43cc:	e1cc10b0 	strh	r1, [ip]
    43d0:	11a0200c 	movne	r2, ip
    43d4:	0affffd7 	beq	0x4338
    43d8:	e2443001 	sub	r3, r4, #1	; 0x1
    43dc:	e1a03803 	mov	r3, r3, lsl #16
    43e0:	e1a04823 	mov	r4, r3, lsr #16
    43e4:	e3540004 	cmp	r4, #4	; 0x4
    43e8:	e3a03000 	mov	r3, #0	; 0x0
    43ec:	e1c230b8 	strh	r3, [r2, #8]
    43f0:	e2822002 	add	r2, r2, #2	; 0x2
    43f4:	1afffff7 	bne	0x43d8
    43f8:	eaffffce 	b	0x4338
    43fc:	e0653000 	rsb	r3, r5, r0
    4400:	e1a03803 	mov	r3, r3, lsl #16
    4404:	e1a03823 	mov	r3, r3, lsr #16
    4408:	e1a02083 	mov	r2, r3, lsl #1
    440c:	e1c130b0 	strh	r3, [r1]
    4410:	e0821001 	add	r1, r2, r1
    4414:	e18250bc 	strh	r5, [r2, ip]
    4418:	eaffffe1 	b	0x43a4
    441c:	002079b0 	streqh	r7, [r0], -r0
    4420:	002078f0 	streqd	r7, [r0], -r0
    4424:	002078c4 	eoreq	r7, r0, r4, asr #17
    4428:	00207984 	eoreq	r7, r0, r4, lsl #19
    442c:	002078f6 	streqd	r7, [r0], -r6
    4430:	e5d00000 	ldrb	r0, [r0]
    4434:	e12fff1e 	bx	lr
    4438:	e92d4070 	stmdb	sp!, {r4, r5, r6, lr}
    443c:	e59f3078 	ldr	r3, [pc, #120]	; 0x44bc
    4440:	e59f6078 	ldr	r6, [pc, #120]	; 0x44c0
    4444:	e5935000 	ldr	r5, [r3]
    4448:	e59f4074 	ldr	r4, [pc, #116]	; 0x44c4
    444c:	e5d03007 	ldrb	r3, [r0, #7]
    4450:	e1a01801 	mov	r1, r1, lsl #16
    4454:	e596e000 	ldr	lr, [r6]
    4458:	e5c43000 	strb	r3, [r4]
    445c:	e1a01821 	mov	r1, r1, lsr #16
    4460:	e5d43000 	ldrb	r3, [r4]
    4464:	e2433001 	sub	r3, r3, #1	; 0x1
    4468:	e20330ff 	and	r3, r3, #255	; 0xff
    446c:	e0832083 	add	r2, r3, r3, lsl #1
    4470:	e35300ff 	cmp	r3, #255	; 0xff
    4474:	e1a0c102 	mov	ip, r2, lsl #2
    4478:	e5c43000 	strb	r3, [r4]
    447c:	0a00000b 	beq	0x44b0
    4480:	e5d02003 	ldrb	r2, [r0, #3]
    4484:	e5d03002 	ldrb	r3, [r0, #2]
    4488:	e1833402 	orr	r3, r3, r2, lsl #8
    448c:	e0833005 	add	r3, r3, r5
    4490:	e19320bc 	ldrh	r2, [r3, ip]
    4494:	e1520001 	cmp	r2, r1
    4498:	e083e00c 	add	lr, r3, ip
    449c:	1affffef 	bne	0x4460
    44a0:	e586e000 	str	lr, [r6]
    44a4:	e1a0000e 	mov	r0, lr
    44a8:	e8bd4070 	ldmia	sp!, {r4, r5, r6, lr}
    44ac:	e12fff1e 	bx	lr
    44b0:	e586e000 	str	lr, [r6]
    44b4:	e3a0e000 	mov	lr, #0	; 0x0
    44b8:	eafffff9 	b	0x44a4
    44bc:	002079b0 	streqh	r7, [r0], -r0
    44c0:	002078fc 	streqd	r7, [r0], -ip
    44c4:	0020794c 	eoreq	r7, r0, ip, asr #18
    44c8:	e1a0c00d 	mov	ip, sp
    44cc:	e92dd8f0 	stmdb	sp!, {r4, r5, r6, r7, fp, ip, lr, pc}
    44d0:	e24cb004 	sub	fp, ip, #4	; 0x4
    44d4:	e20040ff 	and	r4, r0, #255	; 0xff
    44d8:	ebfff7fe 	bl	0x24d8
    44dc:	e5901004 	ldr	r1, [r0, #4]
    44e0:	e3510000 	cmp	r1, #0	; 0x0
    44e4:	e1a06000 	mov	r6, r0
    44e8:	059f509c 	ldreq	r5, [pc, #156]	; 0x458c
    44ec:	159f5098 	ldrne	r5, [pc, #152]	; 0x458c
    44f0:	15950000 	ldrne	r0, [r5]
    44f4:	1bfff84f 	blne	0x2638
    44f8:	e5952000 	ldr	r2, [r5]
    44fc:	e5d2001c 	ldrb	r0, [r2, #28]
    4500:	e1a03c00 	mov	r3, r0, lsl #24
    4504:	e1a01c43 	mov	r1, r3, asr #24
    4508:	e3510001 	cmp	r1, #1	; 0x1
    450c:	0a000018 	beq	0x4574
    4510:	e59f7078 	ldr	r7, [pc, #120]	; 0x4590
    4514:	e2403001 	sub	r3, r0, #1	; 0x1
    4518:	e5975000 	ldr	r5, [r7]
    451c:	e2460014 	sub	r0, r6, #20	; 0x14
    4520:	e5c2301c 	strb	r3, [r2, #28]
    4524:	ebfff7ff 	bl	0x2528
    4528:	e2442001 	sub	r2, r4, #1	; 0x1
    452c:	e202e0ff 	and	lr, r2, #255	; 0xff
    4530:	e35e00ff 	cmp	lr, #255	; 0xff
    4534:	1597c000 	ldrne	ip, [r7]
    4538:	10450104 	subne	r0, r5, r4, lsl #2
    453c:	11a0100c 	movne	r1, ip
    4540:	1a000003 	bne	0x4554
    4544:	e24bd01c 	sub	sp, fp, #28	; 0x1c
    4548:	e89d68f0 	ldmia	sp, {r4, r5, r6, r7, fp, sp, lr}
    454c:	e12fff1e 	bx	lr
    4550:	e2442001 	sub	r2, r4, #1	; 0x1
    4554:	e5b03004 	ldr	r3, [r0, #4]!
    4558:	e21240ff 	ands	r4, r2, #255	; 0xff
    455c:	e5a13004 	str	r3, [r1, #4]!
    4560:	1afffffa 	bne	0x4550
    4564:	e08c310e 	add	r3, ip, lr, lsl #2
    4568:	e2833004 	add	r3, r3, #4	; 0x4
    456c:	e5873000 	str	r3, [r7]
    4570:	eafffff3 	b	0x4544
    4574:	e5c2101f 	strb	r1, [r2, #31]
    4578:	e59f3014 	ldr	r3, [pc, #20]	; 0x4594
    457c:	e59f2014 	ldr	r2, [pc, #20]	; 0x4598
    4580:	e5c31000 	strb	r1, [r3]
    4584:	e5c21000 	strb	r1, [r2]
    4588:	eaffffed 	b	0x4544
    458c:	00207978 	eoreq	r7, r0, r8, ror r9
    4590:	00207928 	eoreq	r7, r0, r8, lsr #18
    4594:	0020792e 	eoreq	r7, r0, lr, lsr #18
    4598:	0020792f 	eoreq	r7, r0, pc, lsr #18
    459c:	e1a0c00d 	mov	ip, sp
    45a0:	e92ddff0 	stmdb	sp!, {r4, r5, r6, r7, r8, r9, sl, fp, ip, lr, pc}
    45a4:	e59f8218 	ldr	r8, [pc, #536]	; 0x47c4
    45a8:	e1a05000 	mov	r5, r0
    45ac:	e5d5300a 	ldrb	r3, [r5, #10]
    45b0:	e5d00008 	ldrb	r0, [r0, #8]
    45b4:	e5982000 	ldr	r2, [r8]
    45b8:	e59f9208 	ldr	r9, [pc, #520]	; 0x47c8
    45bc:	e0422100 	sub	r2, r2, r0, lsl #2
    45c0:	e213a001 	ands	sl, r3, #1	; 0x1
    45c4:	e24cb004 	sub	fp, ip, #4	; 0x4
    45c8:	e5891000 	str	r1, [r9]
    45cc:	e5882000 	str	r2, [r8]
    45d0:	1a00003c 	bne	0x46c8
    45d4:	e59f71f0 	ldr	r7, [pc, #496]	; 0x47cc
    45d8:	e5973000 	ldr	r3, [r7]
    45dc:	e5930014 	ldr	r0, [r3, #20]
    45e0:	e1d020b0 	ldrh	r2, [r0]
    45e4:	e5d3401c 	ldrb	r4, [r3, #28]
    45e8:	e1a02b82 	mov	r2, r2, lsl #23
    45ec:	e1a02ba2 	mov	r2, r2, lsr #23
    45f0:	e20460ff 	and	r6, r4, #255	; 0xff
    45f4:	e1560002 	cmp	r6, r2
    45f8:	aa000027 	bge	0x469c
    45fc:	e3540000 	cmp	r4, #0	; 0x0
    4600:	1a00005b 	bne	0x4774
    4604:	e5973000 	ldr	r3, [r7]
    4608:	e5932014 	ldr	r2, [r3, #20]
    460c:	e2824008 	add	r4, r2, #8	; 0x8
    4610:	e1a02003 	mov	r2, r3
    4614:	e5d2301c 	ldrb	r3, [r2, #28]
    4618:	e2833001 	add	r3, r3, #1	; 0x1
    461c:	e5c2301c 	strb	r3, [r2, #28]
    4620:	e5980000 	ldr	r0, [r8]
    4624:	e5972000 	ldr	r2, [r7]
    4628:	e2800004 	add	r0, r0, #4	; 0x4
    462c:	e3a03000 	mov	r3, #0	; 0x0
    4630:	e5840008 	str	r0, [r4, #8]
    4634:	e5845000 	str	r5, [r4]
    4638:	e5843004 	str	r3, [r4, #4]
    463c:	e5924018 	ldr	r4, [r2, #24]
    4640:	e1d430b0 	ldrh	r3, [r4]
    4644:	e5d51006 	ldrb	r1, [r5, #6]
    4648:	e59f2180 	ldr	r2, [pc, #384]	; 0x47d0
    464c:	e1a03b83 	mov	r3, r3, lsl #23
    4650:	e5d5e007 	ldrb	lr, [r5, #7]
    4654:	e0801101 	add	r1, r0, r1, lsl #2
    4658:	e1a03ba3 	mov	r3, r3, lsr #23
    465c:	e2411004 	sub	r1, r1, #4	; 0x4
    4660:	e592c000 	ldr	ip, [r2]
    4664:	e0843103 	add	r3, r4, r3, lsl #2
    4668:	e1d520b4 	ldrh	r2, [r5, #4]
    466c:	e081e10e 	add	lr, r1, lr, lsl #2
    4670:	e2833008 	add	r3, r3, #8	; 0x8
    4674:	e59f5158 	ldr	r5, [pc, #344]	; 0x47d4
    4678:	e082200c 	add	r2, r2, ip
    467c:	e15e0003 	cmp	lr, r3
    4680:	e5892000 	str	r2, [r9]
    4684:	e5881000 	str	r1, [r8]
    4688:	e5850000 	str	r0, [r5]
    468c:	e59f7130 	ldr	r7, [pc, #304]	; 0x47c4
    4690:	2a000011 	bcs	0x46dc
    4694:	e3a00001 	mov	r0, #1	; 0x1
    4698:	ea000007 	b	0x46bc
    469c:	e0823082 	add	r3, r2, r2, lsl #1
    46a0:	e1a010c3 	mov	r1, r3, asr #1
    46a4:	e35100ff 	cmp	r1, #255	; 0xff
    46a8:	da00003a 	ble	0x4798
    46ac:	e59f3124 	ldr	r3, [pc, #292]	; 0x47d8
    46b0:	e5930000 	ldr	r0, [r3]
    46b4:	ebfffa4c 	bl	0x2fec
    46b8:	e1a0000a 	mov	r0, sl
    46bc:	e24bd028 	sub	sp, fp, #40	; 0x28
    46c0:	e89d6ff0 	ldmia	sp, {r4, r5, r6, r7, r8, r9, sl, fp, sp, lr}
    46c4:	e12fff1e 	bx	lr
    46c8:	e1d500b0 	ldrh	r0, [r5]
    46cc:	e2821004 	add	r1, r2, #4	; 0x4
    46d0:	ebffeeba 	bl	0x1c0
    46d4:	e3a00000 	mov	r0, #0	; 0x0
    46d8:	eafffff7 	b	0x46bc
    46dc:	e064100e 	rsb	r1, r4, lr
    46e0:	e241100f 	sub	r1, r1, #15	; 0xf
    46e4:	e1a01121 	mov	r1, r1, lsr #2
    46e8:	e0811081 	add	r1, r1, r1, lsl #1
    46ec:	e1a00004 	mov	r0, r4
    46f0:	e1a010a1 	mov	r1, r1, lsr #1
    46f4:	ebfffd4b 	bl	0x3c28
    46f8:	e2506000 	subs	r6, r0, #0	; 0x0
    46fc:	0a00002b 	beq	0x47b0
    4700:	e59f30c4 	ldr	r3, [pc, #196]	; 0x47cc
    4704:	e5934000 	ldr	r4, [r3]
    4708:	e5941018 	ldr	r1, [r4, #24]
    470c:	e1d4e1dc 	ldrsb	lr, [r4, #28]
    4710:	e5973000 	ldr	r3, [r7]
    4714:	e5952000 	ldr	r2, [r5]
    4718:	e061c006 	rsb	ip, r1, r6
    471c:	e08c2002 	add	r2, ip, r2
    4720:	e083300c 	add	r3, r3, ip
    4724:	e25e0001 	subs	r0, lr, #1	; 0x1
    4728:	e5852000 	str	r2, [r5]
    472c:	e5873000 	str	r3, [r7]
    4730:	e5942014 	ldr	r2, [r4, #20]
    4734:	4a00000b 	bmi	0x4768
    4738:	e08e310e 	add	r3, lr, lr, lsl #2
    473c:	e0821103 	add	r1, r2, r3, lsl #2
    4740:	e5113004 	ldr	r3, [r1, #-4]
    4744:	e5912004 	ldr	r2, [r1, #4]
    4748:	e2400001 	sub	r0, r0, #1	; 0x1
    474c:	e083300c 	add	r3, r3, ip
    4750:	e082200c 	add	r2, r2, ip
    4754:	e3700001 	cmn	r0, #1	; 0x1
    4758:	e5013004 	str	r3, [r1, #-4]
    475c:	e5812004 	str	r2, [r1, #4]
    4760:	e2411014 	sub	r1, r1, #20	; 0x14
    4764:	1afffff5 	bne	0x4740
    4768:	e3a00001 	mov	r0, #1	; 0x1
    476c:	e5846018 	str	r6, [r4, #24]
    4770:	eaffffd1 	b	0x46bc
    4774:	e5973000 	ldr	r3, [r7]
    4778:	e5934014 	ldr	r4, [r3, #20]
    477c:	e0862106 	add	r2, r6, r6, lsl #2
    4780:	e0844102 	add	r4, r4, r2, lsl #2
    4784:	e244000c 	sub	r0, r4, #12	; 0xc
    4788:	ebfff75d 	bl	0x2504
    478c:	e5972000 	ldr	r2, [r7]
    4790:	e2844008 	add	r4, r4, #8	; 0x8
    4794:	eaffff9e 	b	0x4614
    4798:	ebfffd22 	bl	0x3c28
    479c:	e3500000 	cmp	r0, #0	; 0x0
    47a0:	15973000 	ldrne	r3, [r7]
    47a4:	15830014 	strne	r0, [r3, #20]
    47a8:	1affff93 	bne	0x45fc
    47ac:	eaffffbe 	b	0x46ac
    47b0:	e59f3020 	ldr	r3, [pc, #32]	; 0x47d8
    47b4:	e5930000 	ldr	r0, [r3]
    47b8:	ebfffa0b 	bl	0x2fec
    47bc:	e1a00006 	mov	r0, r6
    47c0:	eaffffbd 	b	0x46bc
    47c4:	00207928 	eoreq	r7, r0, r8, lsr #18
    47c8:	00207944 	eoreq	r7, r0, r4, asr #18
    47cc:	00207978 	eoreq	r7, r0, r8, ror r9
    47d0:	002079b0 	streqh	r7, [r0], -r0
    47d4:	00207940 	eoreq	r7, r0, r0, asr #18
    47d8:	002079a4 	eoreq	r7, r0, r4, lsr #19
    47dc:	e1a0c00d 	mov	ip, sp
    47e0:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    47e4:	e5d03009 	ldrb	r3, [r0, #9]
    47e8:	e3130001 	tst	r3, #1	; 0x1
    47ec:	e24cb004 	sub	fp, ip, #4	; 0x4
    47f0:	e1a06001 	mov	r6, r1
    47f4:	1a000020 	bne	0x487c
    47f8:	e3833001 	orr	r3, r3, #1	; 0x1
    47fc:	e3130004 	tst	r3, #4	; 0x4
    4800:	e5c03009 	strb	r3, [r0, #9]
    4804:	0a00001c 	beq	0x487c
    4808:	e59f3088 	ldr	r3, [pc, #136]	; 0x4898
    480c:	e59f5088 	ldr	r5, [pc, #136]	; 0x489c
    4810:	e5934000 	ldr	r4, [r3]
    4814:	e59f1084 	ldr	r1, [pc, #132]	; 0x48a0
    4818:	e5d03007 	ldrb	r3, [r0, #7]
    481c:	e595e000 	ldr	lr, [r5]
    4820:	e5c13000 	strb	r3, [r1]
    4824:	e5d13000 	ldrb	r3, [r1]
    4828:	e2433001 	sub	r3, r3, #1	; 0x1
    482c:	e20330ff 	and	r3, r3, #255	; 0xff
    4830:	e0832083 	add	r2, r3, r3, lsl #1
    4834:	e35300ff 	cmp	r3, #255	; 0xff
    4838:	e1a0c102 	mov	ip, r2, lsl #2
    483c:	e5c13000 	strb	r3, [r1]
    4840:	0a000011 	beq	0x488c
    4844:	e5d02003 	ldrb	r2, [r0, #3]
    4848:	e5d03002 	ldrb	r3, [r0, #2]
    484c:	e1833402 	orr	r3, r3, r2, lsl #8
    4850:	e0833004 	add	r3, r3, r4
    4854:	e19320bc 	ldrh	r2, [r3, ip]
    4858:	e3520003 	cmp	r2, #3	; 0x3
    485c:	e083e00c 	add	lr, r3, ip
    4860:	1affffef 	bne	0x4824
    4864:	e585e000 	str	lr, [r5]
    4868:	e1a0000e 	mov	r0, lr
    486c:	e1a01006 	mov	r1, r6
    4870:	ebffff49 	bl	0x459c
    4874:	e3a00001 	mov	r0, #1	; 0x1
    4878:	ea000000 	b	0x4880
    487c:	e3a00000 	mov	r0, #0	; 0x0
    4880:	e24bd018 	sub	sp, fp, #24	; 0x18
    4884:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    4888:	e12fff1e 	bx	lr
    488c:	e585e000 	str	lr, [r5]
    4890:	e3a0e000 	mov	lr, #0	; 0x0
    4894:	eafffff3 	b	0x4868
    4898:	002079b0 	streqh	r7, [r0], -r0
    489c:	002078fc 	streqd	r7, [r0], -ip
    48a0:	0020794c 	eoreq	r7, r0, ip, asr #18
    48a4:	e3500000 	cmp	r0, #0	; 0x0
    48a8:	e20110ff 	and	r1, r1, #255	; 0xff
    48ac:	0a000011 	beq	0x48f8
    48b0:	e59f3050 	ldr	r3, [pc, #80]	; 0x4908
    48b4:	e593c000 	ldr	ip, [r3]
    48b8:	e0812101 	add	r2, r1, r1, lsl #2
    48bc:	e08c2082 	add	r2, ip, r2, lsl #1
    48c0:	e5d23019 	ldrb	r3, [r2, #25]
    48c4:	e3130008 	tst	r3, #8	; 0x8
    48c8:	e5d00000 	ldrb	r0, [r0]
    48cc:	1a00000b 	bne	0x4900
    48d0:	e1500001 	cmp	r0, r1
    48d4:	1a000005 	bne	0x48f0
    48d8:	ea000008 	b	0x4900
    48dc:	e0803100 	add	r3, r0, r0, lsl #2
    48e0:	e08c3083 	add	r3, ip, r3, lsl #1
    48e4:	e5d30018 	ldrb	r0, [r3, #24]
    48e8:	e1510000 	cmp	r1, r0
    48ec:	0a000003 	beq	0x4900
    48f0:	e3500000 	cmp	r0, #0	; 0x0
    48f4:	1afffff8 	bne	0x48dc
    48f8:	e3a00000 	mov	r0, #0	; 0x0
    48fc:	e12fff1e 	bx	lr
    4900:	e3a00001 	mov	r0, #1	; 0x1
    4904:	e12fff1e 	bx	lr
    4908:	002079b0 	streqh	r7, [r0], -r0
    490c:	e1a0c00d 	mov	ip, sp
    4910:	e92dddf0 	stmdb	sp!, {r4, r5, r6, r7, r8, sl, fp, ip, lr, pc}
    4914:	e1a01801 	mov	r1, r1, lsl #16
    4918:	e2507000 	subs	r7, r0, #0	; 0x0
    491c:	e24cb004 	sub	fp, ip, #4	; 0x4
    4920:	e1a0a002 	mov	sl, r2
    4924:	e1a01821 	mov	r1, r1, lsr #16
    4928:	0a000035 	beq	0x4a04
    492c:	e59f80fc 	ldr	r8, [pc, #252]	; 0x4a30
    4930:	e59f30fc 	ldr	r3, [pc, #252]	; 0x4a34
    4934:	e1a02801 	mov	r2, r1, lsl #16
    4938:	e5d76000 	ldrb	r6, [r7]
    493c:	e5931000 	ldr	r1, [r3]
    4940:	e5984000 	ldr	r4, [r8]
    4944:	e59fe0ec 	ldr	lr, [pc, #236]	; 0x4a38
    4948:	e1a05822 	mov	r5, r2, lsr #16
    494c:	e0863106 	add	r3, r6, r6, lsl #2
    4950:	e0813083 	add	r3, r1, r3, lsl #1
    4954:	e283c010 	add	ip, r3, #16	; 0x10
    4958:	e5dc2007 	ldrb	r2, [ip, #7]
    495c:	e5ce2000 	strb	r2, [lr]
    4960:	e5de3000 	ldrb	r3, [lr]
    4964:	e2433001 	sub	r3, r3, #1	; 0x1
    4968:	e20330ff 	and	r3, r3, #255	; 0xff
    496c:	e0832083 	add	r2, r3, r3, lsl #1
    4970:	e35300ff 	cmp	r3, #255	; 0xff
    4974:	e1a00102 	mov	r0, r2, lsl #2
    4978:	e5ce3000 	strb	r3, [lr]
    497c:	0a00001c 	beq	0x49f4
    4980:	e5dc2003 	ldrb	r2, [ip, #3]
    4984:	e5dc3002 	ldrb	r3, [ip, #2]
    4988:	e1833402 	orr	r3, r3, r2, lsl #8
    498c:	e0833001 	add	r3, r3, r1
    4990:	e19320b0 	ldrh	r2, [r3, r0]
    4994:	e1520005 	cmp	r2, r5
    4998:	e0834000 	add	r4, r3, r0
    499c:	1affffef 	bne	0x4960
    49a0:	e59f3094 	ldr	r3, [pc, #148]	; 0x4a3c
    49a4:	e1a0100a 	mov	r1, sl
    49a8:	e1a00004 	mov	r0, r4
    49ac:	e583c000 	str	ip, [r3]
    49b0:	e5884000 	str	r4, [r8]
    49b4:	ebfffef8 	bl	0x459c
    49b8:	e3500000 	cmp	r0, #0	; 0x0
    49bc:	0a000002 	beq	0x49cc
    49c0:	e5d4300a 	ldrb	r3, [r4, #10]
    49c4:	e3130002 	tst	r3, #2	; 0x2
    49c8:	1a000002 	bne	0x49d8
    49cc:	e24bd024 	sub	sp, fp, #36	; 0x24
    49d0:	e89d6df0 	ldmia	sp, {r4, r5, r6, r7, r8, sl, fp, sp, lr}
    49d4:	e12fff1e 	bx	lr
    49d8:	ebfff6be 	bl	0x24d8
    49dc:	e59f305c 	ldr	r3, [pc, #92]	; 0x4a40
    49e0:	e5807004 	str	r7, [r0, #4]
    49e4:	e1a01007 	mov	r1, r7
    49e8:	e5930000 	ldr	r0, [r3]
    49ec:	ebfff73d 	bl	0x26e8
    49f0:	eafffff5 	b	0x49cc
    49f4:	e3560000 	cmp	r6, #0	; 0x0
    49f8:	0a000005 	beq	0x4a14
    49fc:	e5dc6008 	ldrb	r6, [ip, #8]
    4a00:	eaffffd1 	b	0x494c
    4a04:	e59f3038 	ldr	r3, [pc, #56]	; 0x4a44
    4a08:	e5930000 	ldr	r0, [r3]
    4a0c:	ebfff976 	bl	0x2fec
    4a10:	eaffffed 	b	0x49cc
    4a14:	e59f302c 	ldr	r3, [pc, #44]	; 0x4a48
    4a18:	e5930000 	ldr	r0, [r3]
    4a1c:	e59f3018 	ldr	r3, [pc, #24]	; 0x4a3c
    4a20:	e583c000 	str	ip, [r3]
    4a24:	e5884000 	str	r4, [r8]
    4a28:	ebfff96f 	bl	0x2fec
    4a2c:	eaffffe6 	b	0x49cc
    4a30:	002078fc 	streqd	r7, [r0], -ip
    4a34:	002079b0 	streqh	r7, [r0], -r0
    4a38:	0020794c 	eoreq	r7, r0, ip, asr #18
    4a3c:	002078f8 	streqd	r7, [r0], -r8
    4a40:	00207978 	eoreq	r7, r0, r8, ror r9
    4a44:	0020798c 	eoreq	r7, r0, ip, lsl #19
    4a48:	00207980 	eoreq	r7, r0, r0, lsl #19
    4a4c:	e92d4070 	stmdb	sp!, {r4, r5, r6, lr}
    4a50:	e59fe0d4 	ldr	lr, [pc, #212]	; 0x4b2c
    4a54:	e20000ff 	and	r0, r0, #255	; 0xff
    4a58:	e59ec000 	ldr	ip, [lr]
    4a5c:	e0800100 	add	r0, r0, r0, lsl #2
    4a60:	e08cc080 	add	ip, ip, r0, lsl #1
    4a64:	e28cc010 	add	ip, ip, #16	; 0x10
    4a68:	e5dc0009 	ldrb	r0, [ip, #9]
    4a6c:	e3100001 	tst	r0, #1	; 0x1
    4a70:	e1a04002 	mov	r4, r2
    4a74:	e1a06003 	mov	r6, r3
    4a78:	e20110ff 	and	r1, r1, #255	; 0xff
    4a7c:	1a00001d 	bne	0x4af8
    4a80:	e3803001 	orr	r3, r0, #1	; 0x1
    4a84:	e3130004 	tst	r3, #4	; 0x4
    4a88:	e5cc3009 	strb	r3, [ip, #9]
    4a8c:	0a000019 	beq	0x4af8
    4a90:	e59f5098 	ldr	r5, [pc, #152]	; 0x4b30
    4a94:	e59e4000 	ldr	r4, [lr]
    4a98:	e5dc3007 	ldrb	r3, [ip, #7]
    4a9c:	e59fe090 	ldr	lr, [pc, #144]	; 0x4b34
    4aa0:	e5950000 	ldr	r0, [r5]
    4aa4:	e5ce3000 	strb	r3, [lr]
    4aa8:	e5de3000 	ldrb	r3, [lr]
    4aac:	e2433001 	sub	r3, r3, #1	; 0x1
    4ab0:	e20330ff 	and	r3, r3, #255	; 0xff
    4ab4:	e0832083 	add	r2, r3, r3, lsl #1
    4ab8:	e35300ff 	cmp	r3, #255	; 0xff
    4abc:	e1a01102 	mov	r1, r2, lsl #2
    4ac0:	e5ce3000 	strb	r3, [lr]
    4ac4:	0a000015 	beq	0x4b20
    4ac8:	e5dc2003 	ldrb	r2, [ip, #3]
    4acc:	e5dc3002 	ldrb	r3, [ip, #2]
    4ad0:	e1833402 	orr	r3, r3, r2, lsl #8
    4ad4:	e0833004 	add	r3, r3, r4
    4ad8:	e19320b1 	ldrh	r2, [r3, r1]
    4adc:	e3520003 	cmp	r2, #3	; 0x3
    4ae0:	e0830001 	add	r0, r3, r1
    4ae4:	1affffef 	bne	0x4aa8
    4ae8:	e5850000 	str	r0, [r5]
    4aec:	e1a01006 	mov	r1, r6
    4af0:	e8bd4070 	ldmia	sp!, {r4, r5, r6, lr}
    4af4:	eafffea8 	b	0x459c
    4af8:	e5dc3003 	ldrb	r3, [ip, #3]
    4afc:	e5dc0002 	ldrb	r0, [ip, #2]
    4b00:	e59e2000 	ldr	r2, [lr]
    4b04:	e1800403 	orr	r0, r0, r3, lsl #8
    4b08:	e0800002 	add	r0, r0, r2
    4b0c:	e0813081 	add	r3, r1, r1, lsl #1
    4b10:	e0800103 	add	r0, r0, r3, lsl #2
    4b14:	e1a01004 	mov	r1, r4
    4b18:	e8bd4070 	ldmia	sp!, {r4, r5, r6, lr}
    4b1c:	eafffe9e 	b	0x459c
    4b20:	e5850000 	str	r0, [r5]
    4b24:	e3a00000 	mov	r0, #0	; 0x0
    4b28:	eaffffef 	b	0x4aec
    4b2c:	002079b0 	streqh	r7, [r0], -r0
    4b30:	002078fc 	streqd	r7, [r0], -ip
    4b34:	0020794c 	eoreq	r7, r0, ip, asr #18
    4b38:	e3a03000 	mov	r3, #0	; 0x0
    4b3c:	e1c130b0 	strh	r3, [r1]
    4b40:	e12fff1e 	bx	lr
    4b44:	e3a03000 	mov	r3, #0	; 0x0
    4b48:	e5c03000 	strb	r3, [r0]
    4b4c:	e12fff1e 	bx	lr
    4b50:	e92d4010 	stmdb	sp!, {r4, lr}
    4b54:	e59fe04c 	ldr	lr, [pc, #76]	; 0x4ba8
    4b58:	e1a04000 	mov	r4, r0
    4b5c:	e89e000f 	ldmia	lr, {r0, r1, r2, r3}
    4b60:	e24dd010 	sub	sp, sp, #16	; 0x10
    4b64:	e88d000f 	stmia	sp, {r0, r1, r2, r3}
    4b68:	e28d3010 	add	r3, sp, #16	; 0x10
    4b6c:	e3e01000 	mvn	r1, #0	; 0x0
    4b70:	e0832104 	add	r2, r3, r4, lsl #2
    4b74:	e5120010 	ldr	r0, [r2, #-16]
    4b78:	e5113bff 	ldr	r3, [r1, #-3071]
    4b7c:	e1833000 	orr	r3, r3, r0
    4b80:	e5013bff 	str	r3, [r1, #-3071]
    4b84:	e5112bef 	ldr	r2, [r1, #-3055]
    4b88:	e1802002 	orr	r2, r0, r2
    4b8c:	e5012bef 	str	r2, [r1, #-3055]
    4b90:	e5113bcf 	ldr	r3, [r1, #-3023]
    4b94:	e1800003 	orr	r0, r0, r3
    4b98:	e5010bcf 	str	r0, [r1, #-3023]
    4b9c:	e28dd010 	add	sp, sp, #16	; 0x10
    4ba0:	e8bd4010 	ldmia	sp!, {r4, lr}
    4ba4:	e12fff1e 	bx	lr
    4ba8:	00206f80 	eoreq	r6, r0, r0, lsl #31
    4bac:	e92d4010 	stmdb	sp!, {r4, lr}
    4bb0:	e59fe04c 	ldr	lr, [pc, #76]	; 0x4c04
    4bb4:	e1a04000 	mov	r4, r0
    4bb8:	e89e000f 	ldmia	lr, {r0, r1, r2, r3}
    4bbc:	e24dd010 	sub	sp, sp, #16	; 0x10
    4bc0:	e88d000f 	stmia	sp, {r0, r1, r2, r3}
    4bc4:	e28d3010 	add	r3, sp, #16	; 0x10
    4bc8:	e3e01000 	mvn	r1, #0	; 0x0
    4bcc:	e0832104 	add	r2, r3, r4, lsl #2
    4bd0:	e5120010 	ldr	r0, [r2, #-16]
    4bd4:	e5113bff 	ldr	r3, [r1, #-3071]
    4bd8:	e1833000 	orr	r3, r3, r0
    4bdc:	e5013bff 	str	r3, [r1, #-3071]
    4be0:	e5112bef 	ldr	r2, [r1, #-3055]
    4be4:	e1802002 	orr	r2, r0, r2
    4be8:	e5012bef 	str	r2, [r1, #-3055]
    4bec:	e5113bcb 	ldr	r3, [r1, #-3019]
    4bf0:	e1800003 	orr	r0, r0, r3
    4bf4:	e5010bcb 	str	r0, [r1, #-3019]
    4bf8:	e28dd010 	add	sp, sp, #16	; 0x10
    4bfc:	e8bd4010 	ldmia	sp!, {r4, lr}
    4c00:	e12fff1e 	bx	lr
    4c04:	00206f70 	eoreq	r6, r0, r0, ror pc
    4c08:	e1a0c00d 	mov	ip, sp
    4c0c:	e92dd870 	stmdb	sp!, {r4, r5, r6, fp, ip, lr, pc}
    4c10:	e24cb004 	sub	fp, ip, #4	; 0x4
    4c14:	ebffecfd 	bl	0x10
    4c18:	e59fe0cc 	ldr	lr, [pc, #204]	; 0x4cec
    4c1c:	e59e3000 	ldr	r3, [lr]
    4c20:	e59f60c8 	ldr	r6, [pc, #200]	; 0x4cf0
    4c24:	e0633000 	rsb	r3, r3, r0
    4c28:	e3530f4b 	cmp	r3, #300	; 0x12c
    4c2c:	e1a0c000 	mov	ip, r0
    4c30:	e1a04006 	mov	r4, r6
    4c34:	e1a05006 	mov	r5, r6
    4c38:	9a00000a 	bls	0x4c68
    4c3c:	e1d610f4 	ldrsh	r1, [r6, #4]
    4c40:	e59f20ac 	ldr	r2, [pc, #172]	; 0x4cf4
    4c44:	e2811001 	add	r1, r1, #1	; 0x1
    4c48:	e0c03192 	smull	r3, r0, r2, r1
    4c4c:	e1a03fc1 	mov	r3, r1, asr #31
    4c50:	e06332c0 	rsb	r3, r3, r0, asr #5
    4c54:	e0833103 	add	r3, r3, r3, lsl #2
    4c58:	e0833103 	add	r3, r3, r3, lsl #2
    4c5c:	e0411103 	sub	r1, r1, r3, lsl #2
    4c60:	e1c610b4 	strh	r1, [r6, #4]
    4c64:	e58ec000 	str	ip, [lr]
    4c68:	e59e3004 	ldr	r3, [lr, #4]
    4c6c:	e063300c 	rsb	r3, r3, ip
    4c70:	e3530f96 	cmp	r3, #600	; 0x258
    4c74:	9a00000a 	bls	0x4ca4
    4c78:	e1d510fc 	ldrsh	r1, [r5, #12]
    4c7c:	e59f2070 	ldr	r2, [pc, #112]	; 0x4cf4
    4c80:	e2811001 	add	r1, r1, #1	; 0x1
    4c84:	e0c03192 	smull	r3, r0, r2, r1
    4c88:	e1a03fc1 	mov	r3, r1, asr #31
    4c8c:	e06332c0 	rsb	r3, r3, r0, asr #5
    4c90:	e0833103 	add	r3, r3, r3, lsl #2
    4c94:	e0833103 	add	r3, r3, r3, lsl #2
    4c98:	e0411103 	sub	r1, r1, r3, lsl #2
    4c9c:	e58ec004 	str	ip, [lr, #4]
    4ca0:	e1c510bc 	strh	r1, [r5, #12]
    4ca4:	e59e3008 	ldr	r3, [lr, #8]
    4ca8:	e063300c 	rsb	r3, r3, ip
    4cac:	e3530fe1 	cmp	r3, #900	; 0x384
    4cb0:	9a00000a 	bls	0x4ce0
    4cb4:	e1d611f4 	ldrsh	r1, [r6, #20]
    4cb8:	e59f2034 	ldr	r2, [pc, #52]	; 0x4cf4
    4cbc:	e2811001 	add	r1, r1, #1	; 0x1
    4cc0:	e0c03192 	smull	r3, r0, r2, r1
    4cc4:	e1a03fc1 	mov	r3, r1, asr #31
    4cc8:	e06332c0 	rsb	r3, r3, r0, asr #5
    4ccc:	e0833103 	add	r3, r3, r3, lsl #2
    4cd0:	e0833103 	add	r3, r3, r3, lsl #2
    4cd4:	e0411103 	sub	r1, r1, r3, lsl #2
    4cd8:	e58ec008 	str	ip, [lr, #8]
    4cdc:	e1c611b4 	strh	r1, [r6, #20]
    4ce0:	e24bd018 	sub	sp, fp, #24	; 0x18
    4ce4:	e89d6870 	ldmia	sp, {r4, r5, r6, fp, sp, lr}
    4ce8:	e12fff1e 	bx	lr
    4cec:	002079b4 	streqh	r7, [r0], -r4
    4cf0:	00207900 	eoreq	r7, r0, r0, lsl #18
    4cf4:	51eb851f 	mvnpl	r8, pc, lsl r5
    4cf8:	e1a0c00d 	mov	ip, sp
    4cfc:	e92dd800 	stmdb	sp!, {fp, ip, lr, pc}
    4d00:	e24cb004 	sub	fp, ip, #4	; 0x4
    4d04:	ebffecc1 	bl	0x10
    4d08:	e59f3014 	ldr	r3, [pc, #20]	; 0x4d24
    4d0c:	e5830008 	str	r0, [r3, #8]
    4d10:	e5830000 	str	r0, [r3]
    4d14:	e5830004 	str	r0, [r3, #4]
    4d18:	e24bd00c 	sub	sp, fp, #12	; 0xc
    4d1c:	e89d6800 	ldmia	sp, {fp, sp, lr}
    4d20:	e12fff1e 	bx	lr
    4d24:	002079b4 	streqh	r7, [r0], -r4
    4d28:	e59f2030 	ldr	r2, [pc, #48]	; 0x4d60
    4d2c:	e59f3030 	ldr	r3, [pc, #48]	; 0x4d64
    4d30:	e59f1030 	ldr	r1, [pc, #48]	; 0x4d68
    4d34:	e5830000 	str	r0, [r3]
    4d38:	e1d201b4 	ldrh	r0, [r2, #20]
    4d3c:	e59f3028 	ldr	r3, [pc, #40]	; 0x4d6c
    4d40:	e1c100b4 	strh	r0, [r1, #4]
    4d44:	e3a00000 	mov	r0, #0	; 0x0
    4d48:	e1c300b0 	strh	r0, [r3]
    4d4c:	e1d230b4 	ldrh	r3, [r2, #4]
    4d50:	e1c130b0 	strh	r3, [r1]
    4d54:	e1d220bc 	ldrh	r2, [r2, #12]
    4d58:	e1c120b2 	strh	r2, [r1, #2]
    4d5c:	e12fff1e 	bx	lr
    4d60:	00207900 	eoreq	r7, r0, r0, lsl #18
    4d64:	002079c8 	eoreq	r7, r0, r8, asr #19
    4d68:	002079c0 	eoreq	r7, r0, r0, asr #19
    4d6c:	002079cc 	eoreq	r7, r0, ip, asr #19
    4d70:	e1a0c00d 	mov	ip, sp
    4d74:	e92dd830 	stmdb	sp!, {r4, r5, fp, ip, lr, pc}
    4d78:	e24cb004 	sub	fp, ip, #4	; 0x4
    4d7c:	e24dd008 	sub	sp, sp, #8	; 0x8
    4d80:	e59fc130 	ldr	ip, [pc, #304]	; 0x4eb8
    4d84:	e5dc3000 	ldrb	r3, [ip]
    4d88:	e2433001 	sub	r3, r3, #1	; 0x1
    4d8c:	e20310ff 	and	r1, r3, #255	; 0xff
    4d90:	e3510000 	cmp	r1, #0	; 0x0
    4d94:	e5cc1000 	strb	r1, [ip]
    4d98:	1a000043 	bne	0x4eac
    4d9c:	e59f5118 	ldr	r5, [pc, #280]	; 0x4ebc
    4da0:	e59f3118 	ldr	r3, [pc, #280]	; 0x4ec0
    4da4:	e5950000 	ldr	r0, [r5]
    4da8:	e5d32000 	ldrb	r2, [r3]
    4dac:	e3500000 	cmp	r0, #0	; 0x0
    4db0:	e5cc2000 	strb	r2, [ip]
    4db4:	0a00003c 	beq	0x4eac
    4db8:	e5d00004 	ldrb	r0, [r0, #4]
    4dbc:	e3500000 	cmp	r0, #0	; 0x0
    4dc0:	e14b11b8 	strh	r1, [fp, #-24]
    4dc4:	1a000038 	bne	0x4eac
    4dc8:	e59fc0f4 	ldr	ip, [pc, #244]	; 0x4ec4
    4dcc:	e59f10f4 	ldr	r1, [pc, #244]	; 0x4ec8
    4dd0:	e1dc20b4 	ldrh	r2, [ip, #4]
    4dd4:	e1d130b0 	ldrh	r3, [r1]
    4dd8:	e1520003 	cmp	r2, r3
    4ddc:	11c120b0 	strneh	r2, [r1]
    4de0:	e1d130b2 	ldrh	r3, [r1, #2]
    4de4:	e1dc20bc 	ldrh	r2, [ip, #12]
    4de8:	e59f10d8 	ldr	r1, [pc, #216]	; 0x4ec8
    4dec:	01a04000 	moveq	r4, r0
    4df0:	13a04001 	movne	r4, #1	; 0x1
    4df4:	e1520003 	cmp	r2, r3
    4df8:	11c120b2 	strneh	r2, [r1, #2]
    4dfc:	e59f30c0 	ldr	r3, [pc, #192]	; 0x4ec4
    4e00:	e1d120b4 	ldrh	r2, [r1, #4]
    4e04:	e1d331b4 	ldrh	r3, [r3, #20]
    4e08:	13844002 	orrne	r4, r4, #2	; 0x2
    4e0c:	e1530002 	cmp	r3, r2
    4e10:	11c130b4 	strneh	r3, [r1, #4]
    4e14:	e3a00a03 	mov	r0, #12288	; 0x3000
    4e18:	e24b1018 	sub	r1, fp, #24	; 0x18
    4e1c:	13844004 	orrne	r4, r4, #4	; 0x4
    4e20:	ebffff44 	bl	0x4b38
    4e24:	e15b31f8 	ldrsh	r3, [fp, #-24]
    4e28:	e59fc09c 	ldr	ip, [pc, #156]	; 0x4ecc
    4e2c:	e1a03983 	mov	r3, r3, lsl #19
    4e30:	e1dc20b0 	ldrh	r2, [ip]
    4e34:	e1a03823 	mov	r3, r3, lsr #16
    4e38:	e0232002 	eor	r2, r3, r2
    4e3c:	e1cc30b0 	strh	r3, [ip]
    4e40:	e24b0015 	sub	r0, fp, #21	; 0x15
    4e44:	e3a01000 	mov	r1, #0	; 0x0
    4e48:	e1844002 	orr	r4, r4, r2
    4e4c:	e14b31b8 	strh	r3, [fp, #-24]
    4e50:	ebffff3b 	bl	0x4b44
    4e54:	e55b3015 	ldrb	r3, [fp, #-21]
    4e58:	e3530000 	cmp	r3, #0	; 0x0
    4e5c:	13843040 	orrne	r3, r4, #64	; 0x40
    4e60:	11a03803 	movne	r3, r3, lsl #16
    4e64:	11a04823 	movne	r4, r3, lsr #16
    4e68:	1a000001 	bne	0x4e74
    4e6c:	e3540000 	cmp	r4, #0	; 0x0
    4e70:	0a00000d 	beq	0x4eac
    4e74:	e1a02804 	mov	r2, r4, lsl #16
    4e78:	e3a03000 	mov	r3, #0	; 0x0
    4e7c:	e1a02842 	mov	r2, r2, asr #16
    4e80:	e24b001a 	sub	r0, fp, #26	; 0x1a
    4e84:	e3a01002 	mov	r1, #2	; 0x2
    4e88:	e14b31ba 	strh	r3, [fp, #-26]
    4e8c:	ebfff94a 	bl	0x33bc
    4e90:	e5950000 	ldr	r0, [r5]
    4e94:	e15b21ba 	ldrh	r2, [fp, #-26]
    4e98:	e1d030b8 	ldrh	r3, [r0, #8]
    4e9c:	e1833002 	orr	r3, r3, r2
    4ea0:	e1c030b8 	strh	r3, [r0, #8]
    4ea4:	e3a01001 	mov	r1, #1	; 0x1
    4ea8:	ebfff5ad 	bl	0x2564
    4eac:	e24bd014 	sub	sp, fp, #20	; 0x14
    4eb0:	e89d6830 	ldmia	sp, {r4, r5, fp, sp, lr}
    4eb4:	e12fff1e 	bx	lr
    4eb8:	002079c6 	eoreq	r7, r0, r6, asr #19
    4ebc:	002079c8 	eoreq	r7, r0, r8, asr #19
    4ec0:	002079c7 	eoreq	r7, r0, r7, asr #19
    4ec4:	00207900 	eoreq	r7, r0, r0, lsl #18
    4ec8:	002079c0 	eoreq	r7, r0, r0, asr #19
    4ecc:	002079cc 	eoreq	r7, r0, ip, asr #19
    4ed0:	e352000f 	cmp	r2, #15	; 0xf
    4ed4:	e52de004 	str	lr, [sp, #-4]!
    4ed8:	e1a0c000 	mov	ip, r0
    4edc:	e1a0e002 	mov	lr, r2
    4ee0:	9a000002 	bls	0x4ef0
    4ee4:	e1813000 	orr	r3, r1, r0
    4ee8:	e3130003 	tst	r3, #3	; 0x3
    4eec:	0a000008 	beq	0x4f14
    4ef0:	e35e0000 	cmp	lr, #0	; 0x0
    4ef4:	049df004 	ldreq	pc, [sp], #4
    4ef8:	e3a02000 	mov	r2, #0	; 0x0
    4efc:	e4d13001 	ldrb	r3, [r1], #1
    4f00:	e7c2300c 	strb	r3, [r2, ip]
    4f04:	e2822001 	add	r2, r2, #1	; 0x1
    4f08:	e152000e 	cmp	r2, lr
    4f0c:	1afffffa 	bne	0x4efc
    4f10:	e49df004 	ldr	pc, [sp], #4
    4f14:	e5913000 	ldr	r3, [r1]
    4f18:	e58c3000 	str	r3, [ip]
    4f1c:	e5912004 	ldr	r2, [r1, #4]
    4f20:	e58c2004 	str	r2, [ip, #4]
    4f24:	e5913008 	ldr	r3, [r1, #8]
    4f28:	e58c3008 	str	r3, [ip, #8]
    4f2c:	e24ee010 	sub	lr, lr, #16	; 0x10
    4f30:	e591300c 	ldr	r3, [r1, #12]
    4f34:	e35e000f 	cmp	lr, #15	; 0xf
    4f38:	e58c300c 	str	r3, [ip, #12]
    4f3c:	e2811010 	add	r1, r1, #16	; 0x10
    4f40:	e28cc010 	add	ip, ip, #16	; 0x10
    4f44:	8afffff2 	bhi	0x4f14
    4f48:	e35e0003 	cmp	lr, #3	; 0x3
    4f4c:	9affffe7 	bls	0x4ef0
    4f50:	e24ee004 	sub	lr, lr, #4	; 0x4
    4f54:	e4913004 	ldr	r3, [r1], #4
    4f58:	e35e0003 	cmp	lr, #3	; 0x3
    4f5c:	e48c3004 	str	r3, [ip], #4
    4f60:	8afffffa 	bhi	0x4f50
    4f64:	e35e0000 	cmp	lr, #0	; 0x0
    4f68:	1affffe2 	bne	0x4ef8
    4f6c:	e49df004 	ldr	pc, [sp], #4
    4f70:	00800000 	addeq	r0, r0, r0
    4f74:	10000000 	andne	r0, r0, r0
    4f78:	20000000 	andcs	r0, r0, r0
    4f7c:	40000000 	andmi	r0, r0, r0
    4f80:	00800000 	addeq	r0, r0, r0
    4f84:	10000000 	andne	r0, r0, r0
    4f88:	20000000 	andcs	r0, r0, r0
    4f8c:	40000000 	andmi	r0, r0, r0
	...
    5090:	01a8caf6 	streqd	ip, [r8, r6]!
    5094:	01280176 	teqeq	r8, r6, ror r1
    5098:	00190027 	andeqs	r0, r9, r7, lsr #32
    509c:	1b010833 	blne	0x47170
    50a0:	01e00002 	mvneq	r0, r2
    50a4:	040004fc 	streq	r0, [r0], #-1276
    50a8:	00120000 	andeqs	r0, r2, r0
    50ac:	04fc0210 	ldreqbt	r0, [ip], #528
    50b0:	00000a0d 	andeq	r0, r0, sp, lsl #20
    50b4:	02880004 	addeq	r0, r8, #4	; 0x4
    50b8:	02010509 	andeq	r0, r1, #37748736	; 0x2400000
    50bc:	00040000 	andeq	r0, r4, r0
    50c0:	050a02a0 	streq	r0, [sl, #-672]
    50c4:	00000201 	andeq	r0, r0, r1, lsl #4
    50c8:	02b80004 	adceqs	r0, r8, #4	; 0x4
    50cc:	0100050b 	tsteq	r0, fp, lsl #10
    50d0:	00040003 	andeq	r0, r4, r3
    50d4:	050b02c4 	streq	r0, [fp, #-708]
    50d8:	00040100 	andeq	r0, r4, r0, lsl #2
    50dc:	02d00004 	sbceqs	r0, r0, #4	; 0x4
    50e0:	0100050b 	tsteq	r0, fp, lsl #10
    50e4:	00040004 	andeq	r0, r4, r4
    50e8:	050b02dc 	streq	r0, [fp, #-732]
    50ec:	00040100 	andeq	r0, r4, r0, lsl #2
    50f0:	02e80004 	rsceq	r0, r8, #4	; 0x4
    50f4:	0100050b 	tsteq	r0, fp, lsl #10
    50f8:	00040013 	andeq	r0, r4, r3, lsl r0
    50fc:	050b02f4 	streq	r0, [fp, #-756]
    5100:	00130100 	andeqs	r0, r3, r0, lsl #2
    5104:	03000004 	movweq	r0, #4	; 0x4
    5108:	0100050b 	tsteq	r0, fp, lsl #10
    510c:	00040014 	andeq	r0, r4, r4, lsl r0
    5110:	050b030c 	streq	r0, [fp, #-780]
    5114:	00130100 	andeqs	r0, r3, r0, lsl #2
    5118:	03180004 	tsteq	r8, #4	; 0x4
    511c:	0100050b 	tsteq	r0, fp, lsl #10
    5120:	00040013 	andeq	r0, r4, r3, lsl r0
    5124:	050b0324 	streq	r0, [fp, #-804]
    5128:	00140100 	andeqs	r0, r4, r0, lsl #2
    512c:	03300004 	teqeq	r0, #4	; 0x4
    5130:	0100050b 	tsteq	r0, fp, lsl #10
    5134:	00040013 	andeq	r0, r4, r3, lsl r0
    5138:	050b033c 	streq	r0, [fp, #-828]
    513c:	00130100 	andeqs	r0, r3, r0, lsl #2
    5140:	03480004 	movteq	r0, #32772	; 0x8004
    5144:	0100050b 	tsteq	r0, fp, lsl #10
    5148:	00020004 	andeq	r0, r2, r4
    514c:	050b0354 	streq	r0, [fp, #-852]
    5150:	00000200 	andeq	r0, r0, r0, lsl #4
    5154:	036c0002 	cmneq	ip, #2	; 0x2
    5158:	0300050b 	movweq	r0, #1291	; 0x50b
    515c:	00040000 	andeq	r0, r4, r0
    5160:	050b0390 	streq	r0, [fp, #-912]
    5164:	00140100 	andeqs	r0, r4, r0, lsl #2
    5168:	039c0004 	orreqs	r0, ip, #4	; 0x4
    516c:	0200050b 	andeq	r0, r0, #46137344	; 0x2c00000
    5170:	00090003 	andeq	r0, r9, r3
    5174:	050b03b4 	streq	r0, [fp, #-948]
    5178:	04000704 	streq	r0, [r0], #-1796
    517c:	04080002 	streq	r0, [r8], #-2
    5180:	0200050f 	andeq	r0, r0, #62914560	; 0x3c00000
    5184:	00020000 	andeq	r0, r2, r0
    5188:	050f0420 	streq	r0, [pc, #-1056]	; 0x4d70
    518c:	08000100 	stmeqda	r0, {r8}
    5190:	042c0002 	streqt	r0, [ip], #-2
    5194:	0100050f 	tsteq	r0, pc, lsl #10
    5198:	00160800 	andeqs	r0, r6, r0, lsl #16
    519c:	050f0438 	streq	r0, [pc, #-1080]	; 0x4d6c
    51a0:	04010602 	streq	r0, [r1], #-1538
    51a4:	04800002 	streq	r0, [r0], #2
    51a8:	02000511 	andeq	r0, r0, #71303168	; 0x4400000
    51ac:	00030000 	andeq	r0, r3, r0
    51b0:	05110498 	ldreq	r0, [r1, #-1176]
    51b4:	04000501 	streq	r0, [r0], #-1281
	...
    5204:	a0000000 	andge	r0, r0, r0
    5208:	a008a004 	andge	sl, r8, r4
    520c:	0010000c 	andeqs	r0, r0, ip
    5210:	00180014 	andeqs	r0, r8, r4, lsl r0
    5214:	0020001c 	eoreq	r0, r0, ip, lsl r0
    5218:	0028a024 	eoreq	sl, r8, r4, lsr #32
    521c:	a030002c 	eorges	r0, r0, ip, lsr #32
    5220:	90369034 	eorlss	r9, r6, r4, lsr r0
    5224:	903a9038 	eorlss	r9, sl, r8, lsr r0
    5228:	903e903c 	eorlss	r9, lr, ip, lsr r0
    522c:	90429040 	subls	r9, r2, r0, asr #32
    5230:	90469044 	subls	r9, r6, r4, asr #32
    5234:	004a9048 	subeq	r9, sl, r8, asr #32
    5238:	000007ff 	streqd	r0, [r0], -pc
    523c:	040a07ff 	streq	r0, [sl], #-2047
    5240:	040a0803 	streq	r0, [sl], #-2051
    5244:	040a0807 	streq	r0, [sl], #-2055
    5248:	040a080b 	streq	r0, [sl], #-2059
    524c:	040a080f 	streq	r0, [sl], #-2063
    5250:	040a0813 	streq	r0, [sl], #-2067
    5254:	040a0817 	streq	r0, [sl], #-2071
    5258:	040a081b 	streq	r0, [sl], #-2075
    525c:	040a081f 	streq	r0, [sl], #-2079
    5260:	040a0823 	streq	r0, [sl], #-2083
    5264:	040a0827 	streq	r0, [sl], #-2087
    5268:	040a082b 	streq	r0, [sl], #-2091
    526c:	040a082f 	streq	r0, [sl], #-2095
    5270:	04d40002 	ldreqb	r0, [r4], #2
    5274:	00010512 	andeq	r0, r1, r2, lsl r5
    5278:	00000001 	andeq	r0, r0, r1
    527c:	00000007 	andeq	r0, r0, r7
    5280:	00000000 	andeq	r0, r0, r0
    5284:	00010003 	andeq	r0, r1, r3
    5288:	04d40032 	ldreqb	r0, [r4], #50
    528c:	01010513 	tsteq	r1, r3, lsl r5
    5290:	00000001 	andeq	r0, r0, r1
    5294:	00000022 	andeq	r0, r0, r2, lsr #32
    5298:	00000000 	andeq	r0, r0, r0
    529c:	00050001 	andeq	r0, r5, r1
    52a0:	04d40033 	ldreqb	r0, [r4], #51
    52a4:	02010516 	andeq	r0, r1, #92274688	; 0x5800000
    52a8:	00000001 	andeq	r0, r0, r1
    52ac:	04d40002 	ldreqb	r0, [r4], #2
    52b0:	02010522 	andeq	r0, r1, #142606336	; 0x8800000
    52b4:	00000001 	andeq	r0, r0, r1
    52b8:	04d40034 	ldreqb	r0, [r4], #52
    52bc:	02030529 	andeq	r0, r3, #171966464	; 0xa400000
    52c0:	00000002 	andeq	r0, r0, r2
    52c4:	00000001 	andeq	r0, r0, r1
    52c8:	00000000 	andeq	r0, r0, r0
    52cc:	00000001 	andeq	r0, r0, r1
    52d0:	00000008 	andeq	r0, r0, r8
    52d4:	00000000 	andeq	r0, r0, r0
    52d8:	00010001 	andeq	r0, r1, r1
    52dc:	0000000b 	andeq	r0, r0, fp
    52e0:	00000000 	andeq	r0, r0, r0
    52e4:	00050000 	andeq	r0, r5, r0
    52e8:	0000000c 	andeq	r0, r0, ip
    52ec:	00000000 	andeq	r0, r0, r0
    52f0:	00010001 	andeq	r0, r1, r1
    52f4:	0000000d 	andeq	r0, r0, sp
    52f8:	00000000 	andeq	r0, r0, r0
    52fc:	00010002 	andeq	r0, r1, r2
    5300:	0000000e 	andeq	r0, r0, lr
    5304:	00000000 	andeq	r0, r0, r0
    5308:	00010001 	andeq	r0, r1, r1
    530c:	00000011 	andeq	r0, r0, r1, lsl r0
    5310:	00000000 	andeq	r0, r0, r0
    5314:	00010002 	andeq	r0, r1, r2
    5318:	04d40035 	ldreqb	r0, [r4], #53
    531c:	0504054b 	streq	r0, [r4, #-1355]
    5320:	00000004 	andeq	r0, r0, r4
    5324:	04d40032 	ldreqb	r0, [r4], #50
    5328:	01010562 	tsteq	r1, r2, ror #10
    532c:	00000001 	andeq	r0, r0, r1
    5330:	04d40002 	ldreqb	r0, [r4], #2
    5334:	02010564 	andeq	r0, r1, #419430400	; 0x19000000
    5338:	00000001 	andeq	r0, r0, r1
    533c:	04d40034 	ldreqb	r0, [r4], #52
    5340:	0202056f 	andeq	r0, r2, #465567744	; 0x1bc00000
    5344:	00000002 	andeq	r0, r0, r2
    5348:	04d40002 	ldreqb	r0, [r4], #2
    534c:	01010579 	tsteq	r1, r9, ror r5
    5350:	00000001 	andeq	r0, r0, r1
    5354:	04d40002 	ldreqb	r0, [r4], #2
    5358:	0101057e 	tsteq	r1, lr, ror r5
    535c:	00000001 	andeq	r0, r0, r1
    5360:	04d40002 	ldreqb	r0, [r4], #2
    5364:	01010583 	smlabbeq	r1, r3, r5, r0
    5368:	00000001 	andeq	r0, r0, r1
    536c:	04d40002 	ldreqb	r0, [r4], #2
    5370:	01010588 	smlabbeq	r1, r8, r5, r0
    5374:	00000001 	andeq	r0, r0, r1
    5378:	04d40002 	ldreqb	r0, [r4], #2
    537c:	0101058d 	smlabbeq	r1, sp, r5, r0
    5380:	00000001 	andeq	r0, r0, r1
    5384:	04d40002 	ldreqb	r0, [r4], #2
    5388:	01010592 	streqb	r0, [r1, -r2]
    538c:	00000001 	andeq	r0, r0, r1
    5390:	04d40002 	ldreqb	r0, [r4], #2
    5394:	01010597 	streqb	r0, [r1, -r7]
    5398:	00000001 	andeq	r0, r0, r1
    539c:	04d40002 	ldreqb	r0, [r4], #2
    53a0:	0101059c 	streqb	r0, [r1, -ip]
    53a4:	00000001 	andeq	r0, r0, r1
    53a8:	04d40002 	ldreqb	r0, [r4], #2
    53ac:	010105a1 	smlatbeq	r1, r1, r5, r0
    53b0:	00000001 	andeq	r0, r0, r1
    53b4:	04d40002 	ldreqb	r0, [r4], #2
    53b8:	010105a6 	smlatbeq	r1, r6, r5, r0
    53bc:	00000001 	andeq	r0, r0, r1
    53c0:	04d40002 	ldreqb	r0, [r4], #2
    53c4:	010105ab 	smlatbeq	r1, fp, r5, r0
    53c8:	00000001 	andeq	r0, r0, r1
    53cc:	04d40002 	ldreqb	r0, [r4], #2
    53d0:	010105b0 	streqh	r0, [r1, -r0]
    53d4:	00000001 	andeq	r0, r0, r1
    53d8:	04d40002 	ldreqb	r0, [r4], #2
    53dc:	010105b5 	streqh	r0, [r1, -r5]
    53e0:	00000001 	andeq	r0, r0, r1
    53e4:	04d40002 	ldreqb	r0, [r4], #2
    53e8:	010105ba 	streqh	r0, [r1, -sl]
    53ec:	00000001 	andeq	r0, r0, r1
    53f0:	04d40000 	ldreqb	r0, [r4]
    53f4:	010105bf 	streqh	r0, [r1, -pc]
    53f8:	00040001 	andeq	r0, r4, r1
    53fc:	04d40002 	ldreqb	r0, [r4], #2
    5400:	010105c6 	smlabteq	r1, r6, r5, r0
    5404:	00000001 	andeq	r0, r0, r1
    5408:	04d40036 	ldreqb	r0, [r4], #54
    540c:	050605cb 	streq	r0, [r6, #-1483]
    5410:	00040005 	andeq	r0, r4, r5
    5414:	04d40037 	ldreqb	r0, [r4], #55
    5418:	010005e8 	smlatteq	r0, r8, r5, r0
    541c:	00040000 	andeq	r0, r4, r0
    5420:	04d40002 	ldreqb	r0, [r4], #2
    5424:	010105ec 	smlatteq	r1, ip, r5, r0
    5428:	00000001 	andeq	r0, r0, r1
    542c:	04d40002 	ldreqb	r0, [r4], #2
    5430:	010105f1 	streqd	r0, [r1, -r1]
    5434:	00000001 	andeq	r0, r0, r1
    5438:	04d40034 	ldreqb	r0, [r4], #52
    543c:	020205f6 	andeq	r0, r2, #1031798784	; 0x3d800000
    5440:	00000002 	andeq	r0, r0, r2
    5444:	04d40003 	ldreqb	r0, [r4], #3
    5448:	040005fc 	streq	r0, [r0], #-1532
    544c:	00040000 	andeq	r0, r4, r0
    5450:	04d40038 	ldreqb	r0, [r4], #56
    5454:	03020637 	movweq	r0, #9783	; 0x2637
    5458:	00000002 	andeq	r0, r0, r2
    545c:	04d40039 	ldreqb	r0, [r4], #57
    5460:	0301064e 	movweq	r0, #5710	; 0x164e
    5464:	00000001 	andeq	r0, r0, r1
    5468:	04d4003a 	ldreqb	r0, [r4], #58
    546c:	0303065e 	movweq	r0, #13918	; 0x365e
    5470:	00000003 	andeq	r0, r0, r3
    5474:	00000024 	andeq	r0, r0, r4, lsr #32
    5478:	00000000 	andeq	r0, r0, r0
    547c:	00050002 	andeq	r0, r5, r2
    5480:	00000025 	andeq	r0, r0, r5, lsr #32
    5484:	00000000 	andeq	r0, r0, r0
    5488:	00050003 	andeq	r0, r5, r3
    548c:	04d4003b 	ldreqb	r0, [r4], #59
    5490:	04030671 	streq	r0, [r3], #-1649
    5494:	00020001 	andeq	r0, r2, r1
    5498:	04d40002 	ldreqb	r0, [r4], #2
    549c:	010106a1 	smlatbeq	r1, r1, r6, r0
    54a0:	00000001 	andeq	r0, r0, r1
    54a4:	04d40037 	ldreqb	r0, [r4], #55
    54a8:	020006a6 	andeq	r0, r0, #174063616	; 0xa600000
    54ac:	00040000 	andeq	r0, r4, r0
    54b0:	0000003b 	andeq	r0, r0, fp, lsr r0
    54b4:	00000000 	andeq	r0, r0, r0
    54b8:	00000001 	andeq	r0, r0, r1
    54bc:	0000003c 	andeq	r0, r0, ip, lsr r0
    54c0:	00000000 	andeq	r0, r0, r0
    54c4:	00000004 	andeq	r0, r0, r4
    54c8:	04d40003 	ldreqb	r0, [r4], #3
    54cc:	020006ba 	andeq	r0, r0, #195035136	; 0xba00000
    54d0:	00040000 	andeq	r0, r4, r0
    54d4:	04d40002 	ldreqb	r0, [r4], #2
    54d8:	030106c9 	movweq	r0, #5833	; 0x16c9
    54dc:	00000001 	andeq	r0, r0, r1
    54e0:	04d4003d 	ldreqb	r0, [r4], #61
    54e4:	020106d9 	andeq	r0, r1, #227540992	; 0xd900000
    54e8:	00040200 	andeq	r0, r4, r0, lsl #4
    54ec:	04e4003e 	streqbt	r0, [r4], #62
    54f0:	04040718 	streq	r0, [r4], #-1816
    54f4:	00000003 	andeq	r0, r0, r3
    54f8:	04e4003f 	streqbt	r0, [r4], #63
    54fc:	0303075f 	movweq	r0, #14175	; 0x375f
    5500:	00000003 	andeq	r0, r0, r3
    5504:	04e40001 	streqbt	r0, [r4], #1
    5508:	03030768 	movweq	r0, #14184	; 0x3768
    550c:	00000101 	andeq	r0, r0, r1, lsl #2
    5510:	04ec0002 	streqbt	r0, [ip], #2
    5514:	0101079f 	streqb	r0, [r1, -pc]
    5518:	00000001 	andeq	r0, r0, r1
    551c:	0000001b 	andeq	r0, r0, fp, lsl r0
    5520:	00000000 	andeq	r0, r0, r0
    5524:	00050002 	andeq	r0, r5, r2
    5528:	04ec0003 	streqbt	r0, [ip], #3
    552c:	030007a4 	movweq	r0, #1956	; 0x7a4
    5530:	00040000 	andeq	r0, r4, r0
    5534:	04ec0040 	streqbt	r0, [ip], #64
    5538:	010207b0 	streqh	r0, [r2, -r0]
    553c:	00000002 	andeq	r0, r0, r2
    5540:	04ec0002 	streqbt	r0, [ip], #2
    5544:	010107b9 	streqh	r0, [r1, -r9]
    5548:	00000001 	andeq	r0, r0, r1
    554c:	04ec0041 	streqbt	r0, [ip], #65
    5550:	040507be 	streq	r0, [r5], #-1982
    5554:	00000203 	andeq	r0, r0, r3, lsl #4
    5558:	00000026 	andeq	r0, r0, r6, lsr #32
    555c:	00000000 	andeq	r0, r0, r0
    5560:	00010001 	andeq	r0, r1, r1
    5564:	00350006 	eoreqs	r0, r5, r6
    5568:	00030038 	andeq	r0, r3, r8, lsr r0
    556c:	003a0038 	eoreqs	r0, sl, r8, lsr r0
    5570:	00030038 	andeq	r0, r3, r8, lsr r0
    5574:	00330000 	eoreqs	r0, r3, r0
    5578:	000d0033 	andeq	r0, sp, r3, lsr r0
    557c:	003d0006 	eoreqs	r0, sp, r6
    5580:	0003003e 	andeq	r0, r3, lr, lsr r0
    5584:	0040003e 	subeq	r0, r0, lr, lsr r0
    5588:	0003003e 	andeq	r0, r3, lr, lsr r0
    558c:	0a0a0a00 	beq	0x287d94
    5590:	0808080a 	stmeqda	r8, {r1, r3, fp}
    5594:	08080808 	stmeqda	r8, {r3, fp}
    5598:	0a000000 	beq	0x55a0
    559c:	0a0a0009 	beq	0x2855c8
    55a0:	12b10900 	adcnes	r0, r1, #0	; 0x0
    55a4:	b42ab000 	strltt	fp, [sl]
    55a8:	a4041b80 	strge	r1, [r4], #-2944
    55ac:	ac040500 	cfstr32ge	mvfx0, [r4], {0}
    55b0:	122aac03 	eorne	sl, sl, #768	; 0x300
    55b4:	0201b700 	andeq	fp, r1, #0	; 0x0
    55b8:	00b72ab1 	ldreqht	r2, [r7], r1
    55bc:	0501b800 	streq	fp, [r1, #-2048]
    55c0:	00c72c4d 	sbceq	r2, r7, sp, asr #24
    55c4:	b6082a0b 	strlt	r2, [r8], -fp, lsl #20
    55c8:	00a70d10 	adceq	r0, r7, r0, lsl sp
    55cc:	b62c2a0b 	strltt	r2, [ip], -fp, lsl #20
    55d0:	10b60c00 	adcnes	r0, r6, r0, lsl #24
    55d4:	b52b2a0d 	strlt	r2, [fp, #-2573]!
    55d8:	2ab11f00 	bcs	0xfec4d1e0
    55dc:	2a0000b7 	bcs	0x58c0
    55e0:	b505bc1d 	strlt	fp, [r5, #-3101]
    55e4:	1c2b0400 	cfstrsne	mvf0, [fp]
    55e8:	0400b42a 	streq	fp, [r0], #-1066
    55ec:	12b81d03 	adcnes	r1, r8, #192	; 0xc0
    55f0:	b02ab101 	eorlt	fp, sl, r1, lsl #2
    55f4:	0000b72a 	andeq	fp, r0, sl, lsr #14
    55f8:	b500122a 	strlt	r1, [r0, #-554]
    55fc:	2ab10400 	bcs	0xfec46604
    5600:	2a0000b7 	bcs	0x58e4
    5604:	0400b52b 	streq	fp, [r0], #-1323
    5608:	03b72ab1 	moveqs	r2, #724992	; 0xb1000
    560c:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    5610:	2ab10004 	bcs	0xfec45628
    5614:	b10004b7 	strlth	r0, [r0, -r7]
    5618:	0004b72a 	andeq	fp, r4, sl, lsr #14
    561c:	13b72ab1 	movnes	r2, #724992	; 0xb1000
    5620:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    5624:	2ab10013 	bcs	0xfec45678
    5628:	b10014b7 	strlth	r1, [r0, -r7]
    562c:	0013b72a 	andeqs	fp, r3, sl, lsr #14
    5630:	13b72ab1 	movnes	r2, #724992	; 0xb1000
    5634:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    5638:	2ab10014 	bcs	0xfec45690
    563c:	b10013b7 	strlth	r1, [r0, -r7]
    5640:	0013b72a 	andeqs	fp, r3, sl, lsr #14
    5644:	04b72ab1 	ldreqt	r2, [r7], #2737
    5648:	b72ab100 	strlt	fp, [sl, -r0, lsl #2]!
    564c:	b2b10000 	adclts	r0, r1, #0	; 0x0
    5650:	00b60515 	adceqs	r0, r6, r5, lsl r5
    5654:	b72ab139 	undefined
    5658:	03b10000 	moveqs	r0, #0	; 0x0
    565c:	00a70536 	adceq	r0, r7, r6, lsr r5
    5660:	05152c12 	ldreq	r2, [r5, #-3090]
    5664:	152a601d 	strne	r6, [sl, #-29]!
    5668:	34601b05 	strccbt	r1, [r0], #-2821
    566c:	01058455 	tsteq	r5, r5, asr r4
    5670:	04150515 	ldreq	r0, [r5], #-1301
    5674:	b1edffa1 	mvnlt	pc, r1, lsr #31
    5678:	b00116b8 	strlth	r1, [r1], -r8
    567c:	0014b72a 	andeqs	fp, r4, sl, lsr #14
    5680:	03b72ab1 	moveqs	r2, #724992	; 0xb1000
    5684:	2b2ab100 	blcs	0xab1a8c
    5688:	b10103b7 	strlth	r0, [r1, -r7]
    568c:	591500bb 	ldmpldb	r5, {r0, r1, r3, r4, r5, r7}
    5690:	0115b703 	tsteq	r5, r3, lsl #14
    5694:	bb0315b3 	bllt	0xcad68
    5698:	04591500 	ldreqb	r1, [r9], #-1280
    569c:	b30115b7 	movwlt	r1, #5559	; 0x15b7
    56a0:	00bb0415 	adceqs	r0, fp, r5, lsl r4
    56a4:	b7055915 	smladlt	r5, r5, r9, r5
    56a8:	15b30115 	ldrne	r0, [r3, #277]!
    56ac:	00bc0605 	adceqs	r0, ip, r5, lsl #12
    56b0:	b2035900 	andlt	r5, r3, #0	; 0x0
    56b4:	59530315 	ldmpldb	r3, {r0, r2, r4, r8, r9}^
    56b8:	0415b204 	ldreq	fp, [r5], #-516
    56bc:	b2055953 	andlt	r5, r5, #1359872	; 0x14c000
    56c0:	b3530515 	cmplt	r3, #88080384	; 0x5400000
    56c4:	2ab10615 	bcs	0xfec46f20
    56c8:	2a0000b7 	bcs	0x59ac
    56cc:	0890b503 	ldmeqia	r0, {r0, r1, r8, sl, ip, sp, pc}
    56d0:	a0b51b2a 	adcges	r1, r5, sl, lsr #22
    56d4:	11062a04 	tstne	r6, r4, lsl #20
    56d8:	20b68000 	adccss	r8, r6, r0
    56dc:	1911b13a 	ldmnedb	r1, {r1, r3, r4, r5, r8, ip, sp, pc}
    56e0:	001011c4 	andeqs	r1, r0, r4, asr #3
    56e4:	04a0b42a 	streqt	fp, [r0], #1066
    56e8:	1ab89360 	bne	0xfee2a470
    56ec:	b42ab101 	strltt	fp, [sl], #-257
    56f0:	041b04a0 	ldreq	r0, [fp], #-1184
    56f4:	2a0515b8 	bcs	0x14addc
    56f8:	1c04a0b4 	stcne	0, cr10, [r4], {180}
    56fc:	0515b803 	ldreq	fp, [r5, #-2051]
    5700:	a0b42ab1 	ldrgeht	r2, [r4], r1
    5704:	15b80404 	ldrne	r0, [r8, #1028]!
    5708:	3d033c04 	stccc	12, cr3, [r3, #-16]
    570c:	2a1700a7 	bcs	0x5c59b0
    5710:	1c0a00b4 	stcne	0, cr0, [sl], {180}
    5714:	b42a2a32 	strltt	r2, [sl], #-2610
    5718:	b61b0ea0 	ldrlt	r0, [fp], -r0, lsr #29
    571c:	00003c30 	andeq	r3, r0, r0, lsr ip
    5720:	1c010284 	sfmne	f0, 4, [r1], {132}
    5724:	0890b42a 	ldmeqia	r0, {r1, r3, r5, sl, ip, sp, pc}
    5728:	2ae7ffa1 	bcs	0xffa055b4
    572c:	0ea0b51b 	mcreq	5, 5, fp, cr0, cr11, {0}
    5730:	00b72ab1 	ldreqht	r2, [r7], r1
    5734:	16b2b100 	ldrnet	fp, [r2], r0, lsl #2
    5738:	0d00c707 	stceq	7, cr12, [r0, #-28]
    573c:	591600bb 	ldmpldb	r6, {r0, r1, r3, r4, r5, r7}
    5740:	b30016b7 	movwlt	r1, #1719	; 0x6b7
    5744:	16b20716 	ssatne	r0, #19, r6, LSL #14
    5748:	00bbb007 	adceqs	fp, fp, r7
    574c:	19b75919 	ldmneib	r7!, {r0, r3, r4, r8, fp, ip, lr}
    5750:	0819b301 	ldmeqda	r9, {r0, r8, r9, ip, sp, pc}
    5754:	0c19b303 	ldceq	3, cr11, [r9], {3}
    5758:	01b72ab1 	ldreqh	r2, [r7, r1]!
    575c:	00bb2a01 	adceqs	r2, fp, r1, lsl #20
    5760:	1bb7591b 	blne	0xfeddbbd4
    5764:	2700b502 	strcs	fp, [r0, -r2, lsl #10]
    5768:	0819b2b1 	ldmeqda	r9, {r0, r4, r5, r7, r9, ip, sp, pc}
    576c:	b2c24b59 	sbclt	r4, r2, #91136	; 0x16400
    5770:	00b60819 	adceqs	r0, r6, r9, lsl r8
    5774:	27009a33 	smladxcs	r0, r3, sl, r9
    5778:	0abc0710 	beq	0xfef073c0
    577c:	100a19b3 	strneh	r1, [sl], -r3
    5780:	0000bc07 	andeq	fp, r0, r7, lsl #24
    5784:	b20b19b3 	andlt	r1, fp, #2932736	; 0x2cc000
    5788:	b6040819 	undefined
    578c:	19b21110 	ldmneib	r2!, {r4, r8, ip}
    5790:	b60a1008 	strlt	r1, [sl], -r8
    5794:	19b20d10 	ldmneib	r2!, {r4, r8, sl, fp}
    5798:	0800b608 	stmeqda	r0, {r3, r9, sl, ip, sp, pc}
    579c:	00a7c32a 	adceq	ip, r7, sl, lsr #6
    57a0:	bfc32a06 	svclt	0x00c32a06
    57a4:	b00819b2 	strlth	r1, [r8], -r2
    57a8:	a0b4592a 	adcges	r5, r4, sl, lsr #18
    57ac:	b5801b23 	strlt	r1, [r0, #2851]
    57b0:	3e0323a0 	cdpcc	3, 0, cr2, cr3, cr0, {5}
    57b4:	b21200a7 	andlts	r0, r2, #167	; 0xa7
    57b8:	321d0b19 	andccs	r0, sp, #25600	; 0x6400
    57bc:	0600a62c 	streq	sl, [r0], -ip, lsr #12
    57c0:	840d00a7 	strhi	r0, [sp], #-167
    57c4:	b21d0103 	andlts	r0, sp, #-1073741824	; 0xc0000000
    57c8:	ffa10c19 	undefined instruction 0xffa10c19
    57cc:	19b21ded 	ldmneib	r2!, {r0, r2, r3, r5, r6, r7, r8, sl, fp, ip}
    57d0:	1900a00c 	stmnedb	r0, {r2, r3, sp, pc}
    57d4:	b20a19b2 	andlt	r1, sl, #2916352	; 0x2c8000
    57d8:	4f1b0c19 	svcmi	0x001b0c19
    57dc:	b20b19b2 	andlt	r1, fp, #2916352	; 0x2c8000
    57e0:	04590c19 	ldreqb	r0, [r9], #-3097
    57e4:	0c19b360 	ldceq	3, cr11, [r9], {96}
    57e8:	b62a532c 	strltt	r5, [sl], -ip, lsr #6
    57ec:	2ab10e00 	bcs	0xfec48ff4
    57f0:	2c781b04 	ldccsl	11, cr1, [r8], #-16
    57f4:	b13e20b6 	ldrlth	r2, [lr, -r6]!
    57f8:	2700b42a 	strcs	fp, [r0, -sl, lsr #8]
    57fc:	23a0b42a 	movcs	fp, #704643072	; 0x2a000000
    5800:	4120b603 	teqmi	r0, r3, lsl #12
    5804:	a73d033c 	undefined
    5808:	b21b1a00 	andlts	r1, fp, #0	; 0x0
    580c:	2e1c0a19 	mrccs	10, 0, r0, cr12, cr9, {0}
    5810:	0d00997e 	stceq	9, cr9, [r0, #-504]
    5814:	1c0b19b2 	stcne	9, cr1, [fp], {178}
    5818:	3b00b632 	blcc	0x330e8
    581c:	02840000 	addeq	r0, r4, #0	; 0x0
    5820:	19b21c01 	ldmneib	r2!, {r0, sl, fp, ip}
    5824:	e5ffa10c 	ldrb	sl, [pc, #268]!	; 0x5938
    5828:	570400a7 	strpl	r0, [r4, -r7, lsr #1]
    582c:	2accffa7 	bcs	0xff3456d0
    5830:	b10000b7 	strlth	r0, [r0, -r7]
    5834:	591b00bb 	ldmpldb	fp, {r0, r1, r3, r4, r5, r7}
    5838:	011bb704 	tsteq	fp, r4, lsl #14
    583c:	b1181bb3 	ldrlth	r1, [r8, -r3]
    5840:	0000b72a 	andeq	fp, r0, sl, lsr #14
    5844:	041bb72a 	ldreq	fp, [fp], #-1834
    5848:	00b72ab1 	ldreqht	r2, [r7], r1
    584c:	1bb2b100 	blne	0xfecb1c54
    5850:	c24e5918 	subgt	r5, lr, #393216	; 0x60000
    5854:	181bb21b 	ldmneda	fp, {r0, r1, r3, r4, r9, ip, sp, pc}
    5858:	7e0490b4 	mcrvc	0, 0, r9, cr4, cr4, {5}
    585c:	00a70436 	adceq	r0, r7, r6, lsr r4
    5860:	181bb215 	ldmneda	fp, {r0, r2, r4, r9, ip, sp, pc}
    5864:	20b6851c 	adccss	r8, r6, ip, lsl r5
    5868:	1bb21b07 	blne	0xfec8c48c
    586c:	0490b418 	ldreq	fp, [r0], #1048
    5870:	1504367e 	strne	r3, [r4, #-1662]
    5874:	ecff9904 	ldcl	9, cr9, [pc], #16
    5878:	59181bb2 	ldmpldb	r8, {r1, r4, r5, r7, r8, r9, fp, ip}
    587c:	1b0490b4 	blne	0x129b54
    5880:	937e8202 	cmnls	lr, #536870912	; 0x20000000
    5884:	150490b5 	strne	r9, [r4, #-181]
    5888:	acc32d04 	stcgel	13, cr2, [r3], {4}
    588c:	00bfc32d 	adceqs	ip, pc, sp, lsr #6
    5890:	00010000 	andeq	r0, r1, r0
    5894:	00050000 	andeq	r0, r5, r0
    5898:	000a0000 	andeq	r0, sl, r0
    589c:	00070000 	andeq	r0, r7, r0
    58a0:	00020000 	andeq	r0, r2, r0
    58a4:	00040000 	andeq	r0, r4, r0
    58a8:	00080000 	andeq	r0, r8, r0
    58ac:	00100000 	andeqs	r0, r0, r0
    58b0:	00200000 	eoreq	r0, r0, r0
    58b4:	00380000 	eoreqs	r0, r8, r0
    58b8:	00030000 	andeq	r0, r3, r0
    58bc:	00400000 	subeq	r0, r0, r0
    58c0:	11060000 	tstne	r6, r0
    58c4:	00001404 	andeq	r1, r0, r4, lsl #8
    58c8:	08040201 	stmeqda	r4, {r0, r9}
    58cc:	08040201 	stmeqda	r4, {r0, r9}
