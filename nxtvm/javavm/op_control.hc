/**
 * This is included inside a switch statement.
 */

case OP_IF_ICMPEQ:
case OP_IF_ACMPEQ:
  // Arguments: 2
  // Stack: -2
  do_isub();
  // Fall through!
case OP_IFEQ:
case OP_IFNULL:
  // Arguments: 2
  // Stack: -1
  pc = do_goto (pc, pop_word() == 0);
  goto LABEL_ENGINELOOP;

case OP_IF_ICMPNE:
case OP_IF_ACMPNE:
  do_isub();
  // Fall through!
case OP_IFNE:
case OP_IFNONNULL:
  pc = do_goto (pc, pop_word() != 0);
  goto LABEL_ENGINELOOP;

case OP_IF_ICMPLT:
  do_isub();
  // Fall through!
case OP_IFLT:
  pc = do_goto (pc, pop_jint() < 0);
  goto LABEL_ENGINELOOP;

case OP_IF_ICMPLE:
  do_isub();
  // Fall through!
case OP_IFLE:
  pc = do_goto (pc, pop_jint() <= 0);
  goto LABEL_ENGINELOOP;

case OP_IF_ICMPGE:
  do_isub();
  // Fall through!
case OP_IFGE:
  pc = do_goto (pc, pop_jint() >= 0);
  goto LABEL_ENGINELOOP;

case OP_IF_ICMPGT:
  do_isub();
  // Fall through!
case OP_IFGT:
  pc = do_goto (pc, pop_jint() > 0);
  goto LABEL_ENGINELOOP;

case OP_JSR:
  // Arguments: 2
  // Stack: +1
  push_word (ptr2word (pc + 2));
  // Fall through!
case OP_GOTO:
  // Arguments: 2
  // Stack: +0
  pc = do_goto (pc, true);
  // No pc increment!
  goto LABEL_ENGINELOOP;

case OP_RET:
  // Arguments: 1
  // Stack: +0
  pc = word2ptr (get_local_word (pc[0]));
  #if DEBUG_BYTECODE
  printf ("\n  OP_RET: returning to %d\n", (int) pc);
  #endif
  // No pc increment!
  goto LABEL_ENGINELOOP;

#if FP_ARITHMETIC

case OP_DCMPL:
case OP_DCMPG:
  // TBD: no distinction between opcodes
  {
    STACKWORD wrd1;
    STACKWORD wrd2;

    wrd2 = pop_word();
    just_pop_word();
    wrd1 = pop_word();
    just_pop_word();
    push_word( do_fcmp (word2jfloat(wrd1), word2jfloat (wrd2), 0));
  }
  goto LABEL_ENGINELOOP;

case OP_FCMPL:
case OP_FCMPG:
  // TBD: no distinction between opcodes
  tempStackWord = pop_word();
  push_word( do_fcmp (word2jfloat(pop_word()), word2jfloat(tempStackWord), 0));
  goto LABEL_ENGINELOOP;
  
#endif // FP_ARITHMETIC

#if 0
  
case OP_LCMP:
  // Arguments: 0
  // Stack: -4 + 1
  {
    JLONG l1, l2;
    JINT c;

    pop_jlong (&l2);
    pop_jlong (&l1);
    c = jlong_compare (l1, l2);
    push_word ((c == 0) ? 0 : ((c < 0) ? -1 : +1));
  }
  goto LABEL_ENGINELOOP;    

#endif // 0

case OP_LOOKUPSWITCH:
  {
    // padding removed while linking
    int off, npairs, idx, idx8;
    byte *from, *to;

    off = get_word_4( pc);
    npairs = get_word_4( pc + 4);

    idx = pop_word();
    idx8 = (byte) idx;

    for( from = pc + 8, to = from + npairs * 8; from < to; from += 8)
    {
       if( from[ 3] == idx8) // fast compare of low byte of match value
        if( get_word_4( from) == idx)
        {
          off = get_word_4( from + 4);
          break;
        }
    }

    pc += off - 1;
  }
  goto LABEL_ENGINELOOP;    

case OP_TABLESWITCH:
  {
    // padding removed while linking
    int off, low, hig, idx;

    off = get_word_4( pc);
    low = get_word_4( pc + 4);
    hig = get_word_4( pc + 8);

    idx = pop_word();
    if( idx >= low && idx <= hig)
      off = get_word_4( pc + 12 + ((idx - low) << 2));

    pc += off - 1;
  }
  goto LABEL_ENGINELOOP;    

// Notes:
// - Not supported: GOTO_W, JSR_W, LCMP

/*end*/







