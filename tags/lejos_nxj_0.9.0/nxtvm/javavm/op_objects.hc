/**
 * This is included inside a switch statement.
 */

OPCODE(OP_NEW)
  // Stack: +1
  // Arguments: 2
  // Hi byte unused
  {
    byte *tempBytePtr;
    SAVE_REGS();
    tempBytePtr = (byte *) new_object_checked (pc[1], pc - 1);
    LOAD_REGS();
    if (tempBytePtr != JNULL)
    { 
      #if 0
      trace (-1, (short) pc[1], 1);
      trace (-1, (short) tempBytePtr, 2);
      trace (-1, get_class_index((Object *) tempBytePtr), 3);
      #endif
      push_ref (ptr2ref(tempBytePtr));
      pc += 2;
    }
  }
  DISPATCH_CHECKED;

OPCODE(OP_GETSTATIC)
  // Optimized version, for 4 byte values only
  // Stack + 1
  // Arguments 2
  if (!is_initialized_idx (pc[0]))
  {
    SAVE_REGS();
    tempInt = dispatch_static_initializer (get_class_record (pc[0]), pc - 1);
    LOAD_REGS();
    if(tempInt)
      DISPATCH_CHECKED;
  }
  push_word(*(((STACKWORD *)get_static_state_base()) + pc[1]));
  pc += 2;
  DISPATCH;

OPCODE(OP_PUTSTATIC)
  // Optimized version, for 4 byte values only
  // Stack + 1
  // Arguments 2
  if (!is_initialized_idx (pc[0]))
  {
    SAVE_REGS();
    tempInt = dispatch_static_initializer (get_class_record (pc[0]), pc - 1);
    LOAD_REGS();
    if(tempInt)
      DISPATCH_CHECKED;
  }
  *(((STACKWORD *)get_static_state_base()) + pc[1]) = pop_word();
  pc += 2;
  DISPATCH;

OPCODE(OP_GETSTATIC_1)
MULTI_OPCODE(OP_GETSTATIC_2)
MULTI_OPCODE(OP_GETSTATIC_3)
MULTI_OPCODE(OP_GETSTATIC_4)
  // Stack: +1 or +2 for GETSTATIC
  {
    if (!is_initialized_idx (pc[0]))
    {
      SAVE_REGS();
      tempInt = dispatch_static_initializer (get_class_record (pc[0]), pc - 1);
      LOAD_REGS();
      if(tempInt)
        DISPATCH_CHECKED;
    }
    tempStackWord = ((STATICFIELD *) get_static_fields_base())[((*(pc-1) - OP_GETSTATIC_1)*256 + pc[1])];
    tempWordPtr = (STACKWORD *)(get_static_state_base() + get_static_field_offset (tempStackWord));
    tempStackWord >>= 12;
    if (tempStackWord == T_LONG || tempStackWord == T_DOUBLE)
    {
      push_word(get_word_ns((byte *)(tempWordPtr), T_LONG));
      push_word(get_word_ns((byte *)(tempWordPtr+1), T_LONG));
    }
    else
    {
      push_word(get_word_ns((byte *) tempWordPtr, tempStackWord));
    }
    pc += 2;
  }
  DISPATCH;

OPCODE(OP_PUTSTATIC_1)
MULTI_OPCODE(OP_PUTSTATIC_2)
MULTI_OPCODE(OP_PUTSTATIC_3)
MULTI_OPCODE(OP_PUTSTATIC_4)
  // Stack: -1 or -2 for GETSTATIC
  {
    if (!is_initialized_idx (pc[0]))
    {
      SAVE_REGS();
      tempInt = dispatch_static_initializer (get_class_record (pc[0]), pc - 1);
      LOAD_REGS();
      if(tempInt)
        DISPATCH_CHECKED;
    }
    tempStackWord = ((STATICFIELD *) get_static_fields_base())[((*(pc-1) - OP_PUTSTATIC_1)*256 + pc[1])];
    tempWordPtr = (STACKWORD *)(get_static_state_base() + get_static_field_offset (tempStackWord));
    tempStackWord >>= 12;
    if (tempStackWord  == T_LONG || tempStackWord == T_DOUBLE)
    {
      store_word_ns((byte *)(tempWordPtr + 1), T_LONG, pop_word());
      store_word_ns((byte *)(tempWordPtr), T_LONG, pop_word());
    }
    else
      store_word_ns((byte *) tempWordPtr, tempStackWord, pop_word());
    pc += 2;
  }
  DISPATCH;;

OPCODE(OP_GETFIELD)
  {
    // Optimized version for int/float/reference
    tempStackWord = get_top_ref();
    if (tempStackWord == JNULL)
      goto LABEL_THROW_NULLPTR_EXCEPTION;

    tempWordPtr = (STACKWORD *)(((byte *) word2ptr (tempStackWord)) + 
                get_pgfield_offset(pc[0], pc[1]));
    set_top_ref(*tempWordPtr);
    pc += 2;
  }
  DISPATCH;

OPCODE(OP_PUTFIELD)
  {
    // Optimized version for int/float/reference
    tempStackWord = get_ref_at(1);
    if (tempStackWord == JNULL)
      goto LABEL_THROW_NULLPTR_EXCEPTION;

    tempWordPtr = (STACKWORD *)(((byte *) word2ptr (tempStackWord)) + 
                get_pgfield_offset(pc[0], pc[1]));
    if (get_pgfield_type(pc[0]) == T_REFERENCE)
      update_object((Object *) tempStackWord);
    *tempWordPtr = pop_word();
    just_pop_ref();
    pc += 2;
  }
  DISPATCH;

OPCODE(OP_GETFIELD_1)
  {
    tempStackWord = get_top_ref();
    if (tempStackWord == JNULL)
      goto LABEL_THROW_NULLPTR_EXCEPTION;

    tempWordPtr = (STACKWORD *)(((byte *) word2ptr (tempStackWord)) + 
                get_pgfield_offset(pc[0], pc[1]));
    tempStackWord = get_pgfield_type(pc[0]);
    if (tempStackWord == T_LONG || tempStackWord == T_DOUBLE)
    {
      set_top_ref(get_word_ns((byte *)tempWordPtr, T_LONG));
      push_word(get_word_ns((byte *)(tempWordPtr+1), T_LONG));
    }
    else
      set_top_ref(get_word_ns((byte *)tempWordPtr, tempStackWord));
    pc += 2;
  }
  DISPATCH;

OPCODE(OP_PUTFIELD_1)
  {
    unsigned int fieldType;
    unsigned int offset;

    offset = get_pgfield_offset(pc[0], pc[1]);
    fieldType = get_pgfield_type(pc[0]);
    if (fieldType == T_LONG || fieldType == T_DOUBLE)
    {
      tempStackWord = get_ref_at (2);
      if (tempStackWord == JNULL)
        goto LABEL_THROW_NULLPTR_EXCEPTION;
      tempWordPtr = (STACKWORD *)(((byte *) word2ptr (tempStackWord)) + offset);
      store_word_ns((byte *)(tempWordPtr + 1), T_LONG, pop_word());
      store_word_ns((byte *)(tempWordPtr), T_LONG, pop_word());
    }
    else
    {
      tempStackWord = get_ref_at (1);
      if (tempStackWord == JNULL)
        goto LABEL_THROW_NULLPTR_EXCEPTION;
      tempWordPtr = (STACKWORD *)(((byte *) word2ptr (tempStackWord)) + offset);
      if (fieldType == T_REFERENCE)
        update_object((Object *) tempStackWord);
      store_word_ns((byte *)tempWordPtr, fieldType, pop_word());
    }
    just_pop_ref();
    pc += 2;
  }
  DISPATCH;

OPCODE(OP_INSTANCEOF)
  // Stack: unchanged
  // Arguments: 2
  // Ignore hi byte
  set_top_word (instance_of (word2obj (get_top_ref()),  pc[1]|(pc[0] << 8)));
  pc += 2;
  DISPATCH;

OPCODE(OP_CHECKCAST)
  // Stack: -1 +1 (same)
  // Arguments: 2
  // Ignore hi byte
  tempStackWord = get_top_ref();
  if (tempStackWord != JNULL && !instance_of (word2obj (tempStackWord), pc[1] | (pc[0] << 8)))
  {
    thrownException = JAVA_LANG_CLASSCASTEXCEPTION;
    goto LABEL_THROW_EXCEPTION;
  }
  pc += 2;
  DISPATCH;

// Notes:
// - NEW, INSTANCEOF, CHECKCAST: 8 bits ignored, 8-bit class index
// - GETSTATIC and PUTSTATIC: 8-bit class index, 8-bit static field record index
// - GETSTATIC_x and PUTSTATIC_x: 8-bit class index, 8-bit static field record low byte of index
// - GETFIELD and PUTFIELD: 4-bit field type, 12-bit field data offset

/*end*/
