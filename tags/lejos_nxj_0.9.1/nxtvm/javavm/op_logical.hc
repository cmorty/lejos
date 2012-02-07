/**
 * This is included inside a switch statement.
 */

OPCODE(OP_ISHL)
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) << (tempStackWord & 0x1F));
  DISPATCH;

OPCODE(OP_ISHR)
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) >> (tempStackWord & 0x1F));
  DISPATCH;

OPCODE(OP_IUSHR)
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() >> (tempStackWord & 0x1F));
  DISPATCH;

OPCODE(OP_IAND)
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() & tempStackWord);
  DISPATCH;

OPCODE(OP_IOR)
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() | tempStackWord);
  DISPATCH;

OPCODE(OP_IXOR)
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() ^ tempStackWord);
  DISPATCH;

#if LONG_ARITHMETIC
OPCODE(OP_LSHL)
  // Arguments) 0
  // Stack) -3 +1
  tempStackWord = pop_word();
  pop_jlong(&l1);
  l1.lnum <<= (tempStackWord & 0x3F);
  push_jlong(&l1);
  DISPATCH;

OPCODE(OP_LSHR)
  // Arguments) 0
  // Stack) -3 +1
  tempStackWord = pop_word();
  pop_jlong(&l1);
  l1.lnum >>= (tempStackWord & 0x3F);
  push_jlong(&l1);
  DISPATCH;

OPCODE(OP_LUSHR)
  // Arguments) 0
  // Stack) -3 +1
  tempStackWord = pop_word();
  pop_jlong(&l1);
  l1.lnum = (LLONG)(((ULLONG)l1.lnum) >> (tempStackWord & 0x3F));
  push_jlong(&l1);
  DISPATCH;

OPCODE(OP_LAND)
  pop_jlong(&l1);
  pop_jlong(&l2);
  l2.lnum &= l1.lnum;
  push_jlong(&l2);
  DISPATCH;

OPCODE(OP_LOR)
  pop_jlong(&l1);
  pop_jlong(&l2);
  l2.lnum |= l1.lnum;
  push_jlong(&l2);
  DISPATCH;

OPCODE(OP_LXOR)
  pop_jlong(&l1);
  pop_jlong(&l2);
  l2.lnum ^= l1.lnum;
  push_jlong(&l2);
  DISPATCH;

#endif

/*end*/







