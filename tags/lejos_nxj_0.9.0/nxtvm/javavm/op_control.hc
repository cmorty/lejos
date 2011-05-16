/**
 * This is included inside a switch statement.
 */

OPCODE(OP_IF_ICMPEQ)
OPCODE(OP_IF_ACMPEQ)
  // Arguments: 2
  // Stack: -2
  tempStackWord = pop_word();
  set_top_word (word2jint(get_top_word()) - word2jint(tempStackWord));
  // Fall through!
OPCODE(OP_IFEQ)
OPCODE(OP_IFNULL)
  // Arguments: 2
  // Stack: -1
  pc = do_goto (pc, pop_word() == 0);
  DISPATCH_CHECKED;

OPCODE(OP_IF_ICMPNE)
OPCODE(OP_IF_ACMPNE)
  tempStackWord = pop_word();
  set_top_word (word2jint(get_top_word()) - word2jint(tempStackWord));
  // Fall through!
OPCODE(OP_IFNE)
OPCODE(OP_IFNONNULL)
  pc = do_goto (pc, pop_word() != 0);
  DISPATCH_CHECKED;

OPCODE(OP_IFLT)
  tempStackWord = 0;
  goto SKIP_OP_IF_ICMPLT;
OPCODE(OP_IF_ICMPLT)
  tempStackWord = pop_word();
SKIP_OP_IF_ICMPLT:
  pc = do_goto (pc, pop_jint() < word2jint(tempStackWord));
  DISPATCH_CHECKED;

OPCODE(OP_IFLE)
  tempStackWord = 0;
  goto SKIP_OP_IF_ICMPLE;
OPCODE(OP_IF_ICMPLE)
  tempStackWord = pop_word();
SKIP_OP_IF_ICMPLE:
  pc = do_goto (pc, pop_jint() <= word2jint(tempStackWord));
  DISPATCH_CHECKED;

OPCODE(OP_IFGE)
  tempStackWord = 0;
  goto SKIP_OP_IF_ICMPGE;
OPCODE(OP_IF_ICMPGE)
  tempStackWord = pop_word();
SKIP_OP_IF_ICMPGE:
  pc = do_goto (pc, pop_jint() >= word2jint(tempStackWord));
  DISPATCH_CHECKED;

OPCODE(OP_IFGT)
  tempStackWord = 0;
  goto SKIP_OP_IF_ICMPGT;
OPCODE(OP_IF_ICMPGT)
  tempStackWord = pop_word();
SKIP_OP_IF_ICMPGT:
  pc = do_goto (pc, pop_jint() > word2jint(tempStackWord));
  DISPATCH_CHECKED;

OPCODE(OP_JSR)
  // Arguments: 2
  // Stack: +1
  push_word (ptr2word (pc + 2));
  // Fall through!
OPCODE(OP_GOTO)
  // Arguments: 2
  // Stack: +0
  pc = do_goto (pc, true);
  // No pc increment!
  DISPATCH_CHECKED;

OPCODE(OP_RET)
  // Arguments: 1
  // Stack: +0
  pc = word2ptr (get_local_word (pc[0]));
  #if DEBUG_BYTECODE
  printf ("\n  OP_RET: returning to %d\n", (int) pc);
  #endif
  // No pc increment!
  DISPATCH_CHECKED;

#if FP_ARITHMETIC

OPCODE(OP_DCMPL)
  pop_jdouble(&d2);
  pop_jdouble(&d1);
  push_word( do_dcmp (d1.dnum, d2.dnum, -1));
  DISPATCH;

OPCODE(OP_DCMPG)
  pop_jdouble(&d2);
  pop_jdouble(&d1);
  push_word( do_dcmp (d1.dnum, d2.dnum, 1));
  DISPATCH;

OPCODE(OP_FCMPL)
  tempStackWord = pop_word();
  just_set_top_word( do_fcmp (word2jfloat(get_top_word()), word2jfloat(tempStackWord), -1));
  DISPATCH;

OPCODE(OP_FCMPG)
  // TBD: no distinction between opcodes
  tempStackWord = pop_word();
  just_set_top_word( do_fcmp (word2jfloat(get_top_word()), word2jfloat(tempStackWord), 1));
  DISPATCH;
  
#endif // FP_ARITHMETIC

#if LONG_ARITHMETIC
  
OPCODE(OP_LCMP)
  // Arguments: 0
  // Stack: -4 + 1
  pop_jlong (&l2);
  pop_jlong (&l1);
  push_word (do_lcmp(l1.lnum, l2.lnum, 0));
  DISPATCH;    

#endif

OPCODE(OP_LOOKUPSWITCH)
  {
    // padding removed while linking
    int off, npairs, idx, idx8;
    byte *from, *to;

    off = get_word_4_swp( pc);
    npairs = get_word_4_swp( pc + 4);

    idx = pop_word();
    idx8 = (byte) idx;

    for( from = pc + 8, to = from + npairs * 8; from < to; from += 8)
    {
       if( from[ 3] == idx8) // fast compare of low byte of match value
        if( get_word_4_swp( from) == idx)
        {
          off = get_word_4_swp( from + 4);
          break;
        }
    }

    pc += off - 1;
  }
  DISPATCH_CHECKED;    

OPCODE(OP_TABLESWITCH)
  {
    // padding removed while linking
    int off, low, hig, idx;

    off = get_word_4_swp( pc);
    low = get_word_4_swp( pc + 4);
    hig = get_word_4_swp( pc + 8);

    idx = pop_word();
    if( idx >= low && idx <= hig)
      off = get_word_4_swp( pc + 12 + ((idx - low) << 2));

    pc += off - 1;
  }
  DISPATCH_CHECKED;    

// Notes:
// - Not supported: GOTO_W, JSR_W, LCMP

/*end*/







