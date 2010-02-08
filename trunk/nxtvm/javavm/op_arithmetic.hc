/**
 * This is included inside a switch statement.
 */

OPCODE(OP_ISUB)
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) - word2jint(tempStackWord));
  DISPATCH;

OPCODE(OP_IADD)
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) + word2jint(tempStackWord));
  DISPATCH;

OPCODE(OP_IMUL)
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) * word2jint(tempStackWord));
  DISPATCH;

OPCODE(OP_IDIV)
OPCODE(OP_IREM)
  tempInt = word2jint(pop_word());
  if (tempInt == 0)
    goto LABEL_THROW_ARITHMETIC_EXCEPTION;
  just_set_top_word ((*(pc-1) == OP_IDIV) ? word2jint(get_top_word()) / tempInt :
                                            word2jint(get_top_word()) % tempInt);
  DISPATCH;

OPCODE(OP_INEG)
  just_set_top_word (-word2jint(get_top_word()));
  DISPATCH;

#if FP_ARITHMETIC

OPCODE(OP_FSUB)
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) - 
                     word2jfloat(tempStackWord)));
  DISPATCH;

OPCODE(OP_FADD)
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) + 
                     word2jfloat(tempStackWord)));
  DISPATCH;

OPCODE(OP_FMUL)
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) * 
                     word2jfloat(tempStackWord)));
  DISPATCH;

OPCODE(OP_FDIV)
  // TBD: no division by zero?
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) / 
                     word2jfloat(tempStackWord)));
  DISPATCH;

OPCODE(OP_FNEG)
  just_set_top_word (jfloat2word(-word2jfloat(get_top_word())));
  DISPATCH;

OPCODE(OP_FREM)
  tempStackWord = pop_word();
  just_set_top_word(jfloat2word((float)__ieee754_fmod(word2jfloat(get_top_word()), word2jfloat(tempStackWord))));
  DISPATCH;

OPCODE(OP_DNEG)
  pop_jdouble(&d1);
  d1.dnum = -d1.dnum;
  push_jdouble(&d1);
  DISPATCH;

OPCODE(OP_DSUB)
  pop_jdouble(&d1);
  pop_jdouble(&d2);
  d2.dnum -= d1.dnum;
  push_jdouble(&d2);
  DISPATCH;

OPCODE(OP_DADD)
  pop_jdouble(&d1);
  pop_jdouble(&d2);
  d2.dnum += d1.dnum;
  push_jdouble(&d2);
  DISPATCH;

OPCODE(OP_DMUL)
  pop_jdouble(&d1);
  pop_jdouble(&d2);
  d2.dnum *= d1.dnum;
  push_jdouble(&d2);
  DISPATCH;

OPCODE(OP_DDIV)
  pop_jdouble(&d1);
  pop_jdouble(&d2);
  d2.dnum /= d1.dnum;
  push_jdouble(&d2);
  DISPATCH;

OPCODE(OP_DREM)
  pop_jdouble(&d1);
  pop_jdouble(&d2);
  d2.dnum = __ieee754_fmod(d2.dnum, d1.dnum);
  push_jdouble(&d2);
  DISPATCH;
#endif // FP_ARITHMETIC

#if LONG_ARITHMETIC
OPCODE(OP_LNEG)
  pop_jlong(&l1);
  l1.lnum = -l1.lnum;
  push_jlong(&l1);
  DISPATCH;

OPCODE(OP_LADD)
  pop_jlong(&l1);
  pop_jlong(&l2);
  l2.lnum += l1.lnum;
  push_jlong(&l2);
  DISPATCH;

OPCODE(OP_LSUB)
  pop_jlong(&l1);
  pop_jlong(&l2);
  l2.lnum -= l1.lnum;
  push_jlong(&l2);
  DISPATCH;

OPCODE(OP_LMUL)
  pop_jlong(&l1);
  pop_jlong(&l2);
  l2.lnum *= l1.lnum;
  push_jlong(&l2);
  DISPATCH;

OPCODE(OP_LDIV)
  pop_jlong(&l1);
  pop_jlong(&l2);
  if (l1.lnum == 0)
    goto LABEL_THROW_ARITHMETIC_EXCEPTION;
  l2.lnum /= l1.lnum;
  push_jlong(&l2);
  DISPATCH;

OPCODE(OP_LREM)
  pop_jlong(&l1);
  pop_jlong(&l2);
  if (l1.lnum == 0)
    goto LABEL_THROW_ARITHMETIC_EXCEPTION;
  l2.lnum %= l1.lnum;
  push_jlong(&l2);
  DISPATCH;
#endif

// Notes)
// - Not supported) LADD, LSUB, LMUL, LREM, FREM, DREM
// - Operations on doubles are truncated to low float

/*end*/
