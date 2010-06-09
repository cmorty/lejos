/**
 * This is included inside a switch statement.
 */

OPCODE(OP_BIPUSH)
  // Stack size: +1
  // Arguments: 1
  // TBD: check negatives
  push_word ((JBYTE) (*pc++));
  DISPATCH;

OPCODE(OP_SIPUSH)
  // Stack size: +1
  // Arguments: 2
  #if 0
  printf ("  OP_SIPUSH args: %d, %d (%d)\n", (int) pc[0], (int) pc[1], (int) pc[2]);
  #endif
  push_word ((JSHORT) (((TWOBYTES) pc[0] << 8) | pc[1]));
  pc += 2;
  DISPATCH;

OPCODE(OP_LDC)
  // Stack size: +1
  // Arguments: 1
  // Optimized version for ints/floats only.
  push_word(*(((STACKWORD *)get_constant_values_base()) + pc[0]));
  pc++;
  DISPATCH;

OPCODE(OP_LDC_1)
OPCODE(OP_LDC_2)
MULTI_OPCODE(OP_LDC_3)
MULTI_OPCODE(OP_LDC_4)
  tempConstRec = get_constant_record ((*(pc-1) - OP_LDC_1)*256 + *pc);
  // Stack size: +1
  // Arguments: 1
  switch (tempConstRec->constantType)
  {
    case JAVA_LANG_STRING:
    case AC:
      // Optimized and none optimized strings
      SAVE_REGS();
      tempWordPtr = (void *) new_string (tempConstRec, pc - 1);
      LOAD_REGS();
      if (tempWordPtr == JNULL)
        DISPATCH_CHECKED;
      push_ref (ptr2word (tempWordPtr));
      break;
    case T_CLASS:
      push_word(ptr2ref(get_class_record(*get_constant_ptr(tempConstRec))));
      break;
    case T_INT:
    case T_FLOAT:
      push_word(*(STACKWORD *)get_constant_ptr(tempConstRec));
      break;
    #ifdef VERIFY
    default:
      assert (false, INTERPRETER0);
    #endif
  }
  pc++;
  DISPATCH;

OPCODE(OP_LDC2_W)
  // Stack size: +2
  // Arguments: 2
  {
    tempConstRec = get_constant_record (((TWOBYTES) pc[0] << 8) | pc[1]);
    tempWordPtr = (STACKWORD *)get_constant_ptr (tempConstRec);
    push_word(*tempWordPtr++);
    push_word(*tempWordPtr);
    pc += 2;
  }
  DISPATCH;

OPCODE(OP_ACONST_NULL)
  // Stack size: +1
  // Arguments: 0
  push_ref (JNULL);
  DISPATCH;

OPCODE(OP_ICONST_M1)
  push_word (-1);
  DISPATCH;

OPCODE(OP_ICONST_0)
  push_word (0);
  DISPATCH;

OPCODE(OP_ICONST_1)
  push_word (1);
  DISPATCH;

OPCODE(OP_ICONST_2)
  push_word (2);
  DISPATCH;

OPCODE(OP_ICONST_3)
  push_word (3);
  DISPATCH;

OPCODE(OP_ICONST_4)
  push_word (4);
  DISPATCH;

OPCODE(OP_ICONST_5)
  push_word (5);
  DISPATCH;

OPCODE(OP_LCONST_0)
OPCODE(OP_LCONST_1)
  // Stack size: +2
  // Arguments: 0
  push_word (0);
  push_word (*(pc-1) - OP_LCONST_0);
  DISPATCH;

OPCODE(OP_DCONST_0)
  push_word (0);
  // Fall through!
OPCODE(OP_FCONST_0)
  push_word (0);
  DISPATCH;  

OPCODE(OP_POP2)
  // Stack size: -2
  // Arguments: 0
  just_pop_word();
  // Fall through
OPCODE(OP_POP)
  // Stack size: -1
  // Arguments: 0
  just_pop_word();
  DISPATCH;

OPCODE(OP_DUP)
  // Stack size: +1
  // Arguments: 0
  dup();
  DISPATCH;

OPCODE(OP_DUP2)
  // Stack size: +2
  // Arguments: 0
  dup2();
  DISPATCH;

OPCODE(OP_DUP_X1)
  // Stack size: +1
  // Arguments: 0
  dup_x1();
  DISPATCH;

OPCODE(OP_DUP2_X1)
  // Stack size: +2
  // Arguments: 0
  dup2_x1();
  DISPATCH;

OPCODE(OP_DUP_X2)
  // Stack size: +1
  // Arguments: 0
  dup_x2();
  DISPATCH;

OPCODE(OP_DUP2_X2)
  // Stack size: +2
  // Arguments: 0
  dup2_x2();
  DISPATCH;

OPCODE(OP_SWAP)
  swap(); 
  DISPATCH;

#if FP_ARITHMETIC
  
OPCODE(OP_FCONST_1)
  push_word (jfloat2word((JFLOAT) 1.0));
  DISPATCH;

OPCODE(OP_FCONST_2)
  push_word (jfloat2word((JFLOAT) 2.0));
  DISPATCH;

OPCODE(OP_DCONST_1)
  // Stack size: +2
  // Arguments: 0
  d1.dnum = 1.0;
  push_jdouble(&d1);
  DISPATCH;

#endif // FP_ARITHMETIC

  
// Notes)
// - LDC_W should not occur in TinyVM or CompactVM.
// - Arguments of LDC and LDC2_W are postprocessed.
// - NOP is in op_skip.hc.

/*end*/







