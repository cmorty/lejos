/**
 * This is included inside a switch statement.
 */

OPCODE(OP_ILOAD)
OPCODE(OP_FLOAD)
  push_word (get_local_word(*pc++));
  DISPATCH;

OPCODE(OP_ALOAD)
  // Arguments: 1
  // Stack: +1
  push_ref (get_local_ref(*pc++));
  DISPATCH;

OPCODE(OP_ILOAD_0)
OPCODE(OP_FLOAD_0)
  push_word (get_local_word(0));
  DISPATCH;

OPCODE(OP_ILOAD_1)
OPCODE(OP_FLOAD_1)
  push_word (get_local_word(1));
  DISPATCH;

OPCODE(OP_ILOAD_2)
OPCODE(OP_FLOAD_2)
  push_word (get_local_word(2));
  DISPATCH;

OPCODE(OP_ILOAD_3)
OPCODE(OP_FLOAD_3)
  push_word (get_local_word(3));
  DISPATCH;

OPCODE(OP_ALOAD_0)
  push_ref (get_local_ref(0));
  DISPATCH;

OPCODE(OP_ALOAD_1)
  push_ref (get_local_ref(1));
  DISPATCH;

OPCODE(OP_ALOAD_2)
  push_ref (get_local_ref(2));
  DISPATCH;

OPCODE(OP_ALOAD_3)
  push_ref (get_local_ref(3));
  DISPATCH;

OPCODE(OP_LLOAD)
OPCODE(OP_DLOAD)
  // Arguments: 1
  // Stack: +2
  push_word (get_local_word(*pc));
  push_word (get_local_word((*pc)+1));
  pc++;
  DISPATCH;

OPCODE(OP_LLOAD_0)
OPCODE(OP_LLOAD_1)
OPCODE(OP_LLOAD_2)
OPCODE(OP_LLOAD_3)
  // Arguments: 0
  // Stack: +2
  tempInt = *(pc-1) - OP_LLOAD_0;
  push_word (get_local_word(tempInt++));
  push_word (get_local_word(tempInt));
  DISPATCH;

OPCODE(OP_DLOAD_0)
OPCODE(OP_DLOAD_1)
OPCODE(OP_DLOAD_2)
OPCODE(OP_DLOAD_3)
  // Arguments: 0
  // Stack: +2
  tempInt = *(pc-1) - OP_DLOAD_0;
  push_word (get_local_word(tempInt++));
  push_word (get_local_word(tempInt));
  DISPATCH;

OPCODE(OP_ISTORE)
OPCODE(OP_FSTORE)
  // Arguments: 1
  // Stack: -1
  set_local_word(*pc++, pop_word());
  DISPATCH;

OPCODE(OP_ASTORE)
  // Arguments: 1
  // Stack: -1
  set_local_ref(*pc++, pop_word());
  DISPATCH;

OPCODE(OP_ISTORE_0)
OPCODE(OP_FSTORE_0)
  set_local_word(0, pop_word());
  DISPATCH;

OPCODE(OP_ISTORE_1)
OPCODE(OP_FSTORE_1)
  set_local_word(1, pop_word());
  DISPATCH;

OPCODE(OP_ISTORE_2)
OPCODE(OP_FSTORE_2)
  set_local_word(2, pop_word());
  DISPATCH;

OPCODE(OP_ISTORE_3)
OPCODE(OP_FSTORE_3)
  set_local_word(3, pop_word());
  DISPATCH;

OPCODE(OP_ASTORE_0)
  set_local_ref(0, pop_word());
  DISPATCH;

OPCODE(OP_ASTORE_1)
  set_local_ref(1, pop_word());
  DISPATCH;

OPCODE(OP_ASTORE_2)
  set_local_ref(2, pop_word());
  DISPATCH;

OPCODE(OP_ASTORE_3)
  set_local_ref(3, pop_word());
  DISPATCH;

OPCODE(OP_LSTORE)
OPCODE(OP_DSTORE)
  // Arguments: 1
  // Stack: -1
  set_local_word ((*pc)+1, pop_word());
  set_local_word (*pc++, pop_word());
  DISPATCH;

OPCODE(OP_LSTORE_0)
OPCODE(OP_LSTORE_1)
OPCODE(OP_LSTORE_2)
OPCODE(OP_LSTORE_3)
  tempInt = *(pc-1) - OP_LSTORE_0;
  set_local_word (tempInt+1, pop_word());
  set_local_word (tempInt, pop_word());
  DISPATCH;

OPCODE(OP_DSTORE_0)
OPCODE(OP_DSTORE_1)
OPCODE(OP_DSTORE_2)
OPCODE(OP_DSTORE_3)
  tempInt = *(pc-1) - OP_DSTORE_0;
  set_local_word (tempInt+1, pop_word());
  set_local_word (tempInt, pop_word());
  DISPATCH;

OPCODE(OP_IINC)
  // Arguments: 2
  // Stack: +0
  inc_local_word (pc[0], byte2jint(pc[1]));
  pc += 2;
  DISPATCH;

OPCODE(OP_WIDE)
  if( pc[0] == OP_IINC && pc[1] == 0)
  {
    // Arguments: 4
    // Stack: +0
    inc_local_word (pc[2], (JSHORT) (TWOBYTES) (((TWOBYTES) pc[3] << 8) | pc[4]));
    pc += 5;
  }
  else
  {
    thrownException = JAVA_LANG_NOSUCHMETHODERROR;
    goto LABEL_THROW_EXCEPTION;
  }
  DISPATCH;

// Notes:
// - OP_WIDE other then OP_IINC is unexpected in TinyVM and CompactVM.

/*end*/







