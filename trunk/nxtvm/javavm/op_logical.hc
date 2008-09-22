/**
 * This is included inside a switch statement.
 */

case OP_ISHL:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) << (tempStackWord & 0x1F));
  goto LABEL_ENGINELOOP;

case OP_ISHR:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) >> (tempStackWord & 0x1F));
  goto LABEL_ENGINELOOP;

case OP_IUSHR:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() >> (tempStackWord & 0x1F));
  goto LABEL_ENGINELOOP;

case OP_IAND:
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() & tempStackWord);
  goto LABEL_ENGINELOOP;

case OP_IOR:
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() | tempStackWord);
  goto LABEL_ENGINELOOP;

case OP_IXOR:
  tempStackWord = pop_word();
  just_set_top_word (get_top_word() ^ tempStackWord);
  goto LABEL_ENGINELOOP;

case OP_LSHL:
  {
    // Arguments: 0
    // Stack: -3 +1
    JLONG l;
    tempStackWord = pop_word();
    pop_jlong(&l);
    l.lnum <<= (tempStackWord & 0x3F);
    push_jlong(&l);
    goto LABEL_ENGINELOOP;
  }

case OP_LSHR:
  {
    // Arguments: 0
    // Stack: -3 +1
    JLONG l;
    tempStackWord = pop_word();
    pop_jlong(&l);
    l.lnum >>= (tempStackWord & 0x3F);
    push_jlong(&l);
    goto LABEL_ENGINELOOP;
  }

case OP_LUSHR:
  {
    // Arguments: 0
    // Stack: -3 +1
    JLONG l;
    tempStackWord = pop_word();
    pop_jlong(&l);
    l.lnum = (LLONG)(((ULLONG)l.lnum) >> (tempStackWord & 0x3F));
    push_jlong(&l);
    goto LABEL_ENGINELOOP;
  }

case OP_LAND:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    l2.lnum &= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINELOOP;
  }

case OP_LOR:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    l2.lnum |= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINELOOP;
  }

case OP_LXOR:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    l2.lnum ^= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINELOOP;
  }

// Notes:
// - Not supported: LSHL, LSHR, LAND, LOR, LXOR

/*end*/







