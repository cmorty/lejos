/**
 * This is included inside a switch statement.
 */

OPCODE(OP_I2B)
  just_set_top_word ((JBYTE) word2jint(get_top_word()));
  DISPATCH;

OPCODE(OP_I2S)
  just_set_top_word ((JSHORT) word2jint(get_top_word()));
  DISPATCH;

OPCODE(OP_I2C)
  just_set_top_word ((JCHAR) word2jint(get_top_word()));
  DISPATCH;

OPCODE(OP_F2D)
  // Arguments: 0
  // Stack: -1 +2
  d1.dnum = (double)word2jfloat(pop_word());
  push_jdouble(&d1);
  DISPATCH;

OPCODE(OP_D2F)
  // Arguments: 0
  // Stack: -2 +1
  pop_jdouble(&d1);
  push_word(jfloat2word((float) d1.dnum));
  DISPATCH;

OPCODE(OP_L2I)
  // Arguments: 0
  // Stack: -2 +1
  pop_jlong(&l1);
  push_word((JINT) l1.lnum);
  DISPATCH;

OPCODE(OP_I2L)
  l1.lnum = (LLONG) pop_jint();
  push_jlong(&l1);
  DISPATCH;

#if FP_ARITHMETIC

OPCODE(OP_I2F)
  // Arguments: 0
  // Stack: -1 +1
  just_set_top_word (jfloat2word ((JFLOAT) word2jint(get_top_word())));
  DISPATCH;

OPCODE(OP_I2D)
  // Arguments: 0
  // Stack: -1 +2
  d1.dnum = (double) (int) pop_word();
  push_jdouble(&d1);
  DISPATCH;

OPCODE(OP_F2I)
  // Arguments: 0
  // Stack: -1 +1
  just_set_top_word ((JINT) word2jfloat(get_top_word()));
  DISPATCH;

OPCODE(OP_D2I)
  // Arguments: 0
  // Stack: -2 +1  
  pop_jdouble(&d1);
  push_word((JINT) d1.dnum);
  DISPATCH;

OPCODE(OP_L2F)
  pop_jlong(&l1);
  push_word (jfloat2word ((JFLOAT) l1.lnum));
  DISPATCH;

OPCODE(OP_L2D)
  pop_jlong(&l1);
  d1.dnum = (double) l1.lnum;
  push_jdouble(&d1);
  DISPATCH;

OPCODE(OP_F2L)
  tempStackWord = pop_word();
  l1.lnum = (LLONG)word2jfloat(tempStackWord);
  push_jlong(&l1);
  DISPATCH;

OPCODE(OP_D2L)
  pop_jdouble(&d1);
  l1.lnum = (LLONG)d1.dnum;
  push_jlong(&l1);
  DISPATCH;

#endif

/*end*/


