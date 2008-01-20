/**
 * This is included inside a switch statement.
 */

case OP_ILOAD:
case OP_FLOAD:
  push_word (get_local_word(*pc++));
  goto LABEL_ENGINEFASTLOOP;

case OP_ALOAD:
  // Arguments: 1
  // Stack: +1
  push_ref (get_local_ref(*pc++));
  goto LABEL_ENGINEFASTLOOP;

case OP_ILOAD_0:
case OP_FLOAD_0:
  push_word (get_local_word(0));
  goto LABEL_ENGINEFASTLOOP;

case OP_ILOAD_1:
case OP_FLOAD_1:
  push_word (get_local_word(1));
  goto LABEL_ENGINEFASTLOOP;

case OP_ILOAD_2:
case OP_FLOAD_2:
  push_word (get_local_word(2));
  goto LABEL_ENGINEFASTLOOP;

case OP_ILOAD_3:
case OP_FLOAD_3:
  push_word (get_local_word(3));
  goto LABEL_ENGINEFASTLOOP;

case OP_ALOAD_0:
  push_ref (get_local_ref(0));
  goto LABEL_ENGINEFASTLOOP;

case OP_ALOAD_1:
  push_ref (get_local_ref(1));
  goto LABEL_ENGINEFASTLOOP;

case OP_ALOAD_2:
  push_ref (get_local_ref(2));
  goto LABEL_ENGINEFASTLOOP;

case OP_ALOAD_3:
  push_ref (get_local_ref(3));
  goto LABEL_ENGINEFASTLOOP;

case OP_LLOAD:
case OP_DLOAD:
  // Arguments: 1
  // Stack: +2
  push_word (get_local_word(*pc));
  push_word (get_local_word((*pc)+1));
  pc++;
  goto LABEL_ENGINELOOP;

case OP_LLOAD_0:
case OP_LLOAD_1:
case OP_LLOAD_2:
case OP_LLOAD_3:
  // Arguments: 0
  // Stack: +2
  tempInt = *(pc-1) - OP_LLOAD_0;
  goto LABEL_DLOAD_COMPLETE; // below
  //push_word (get_local_word(tempInt++));
  //push_word (get_local_word(tempInt));
  //goto LABEL_ENGINELOOP;

case OP_DLOAD_0:
case OP_DLOAD_1:
case OP_DLOAD_2:
case OP_DLOAD_3:
  // Arguments: 0
  // Stack: +2
  tempInt = *(pc-1) - OP_DLOAD_0;
 LABEL_DLOAD_COMPLETE:
  push_word (get_local_word(tempInt++));
  push_word (get_local_word(tempInt));
  goto LABEL_ENGINELOOP;

case OP_ISTORE:
case OP_FSTORE:
  // Arguments: 1
  // Stack: -1
  set_local_word(*pc++, pop_word());
  goto LABEL_ENGINEFASTLOOP;  

case OP_ASTORE:
  // Arguments: 1
  // Stack: -1
  set_local_ref(*pc++, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ISTORE_0:
case OP_FSTORE_0:
  set_local_word(0, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ISTORE_1:
case OP_FSTORE_1:
  set_local_word(1, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ISTORE_2:
case OP_FSTORE_2:
  set_local_word(2, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ISTORE_3:
case OP_FSTORE_3:
  set_local_word(3, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ASTORE_0:
  set_local_ref(0, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ASTORE_1:
  set_local_ref(1, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ASTORE_2:
  set_local_ref(2, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_ASTORE_3:
  set_local_ref(3, pop_word());
  goto LABEL_ENGINEFASTLOOP;

case OP_LSTORE:
case OP_DSTORE:
  // Arguments: 1
  // Stack: -1
  set_local_word ((*pc)+1, pop_word());
  set_local_word (*pc++, pop_word());
  goto LABEL_ENGINELOOP;

case OP_LSTORE_0:
case OP_LSTORE_1:
case OP_LSTORE_2:
case OP_LSTORE_3:
  tempInt = *(pc-1) - OP_LSTORE_0;
  goto LABEL_DSTORE_END;
  //set_local_word (tempInt+1, pop_word());
  //set_local_word (tempInt, pop_word());
  //goto LABEL_ENGINELOOP;

case OP_DSTORE_0:
case OP_DSTORE_1:
case OP_DSTORE_2:
case OP_DSTORE_3:
  tempInt = *(pc-1) - OP_DSTORE_0;
 LABEL_DSTORE_END:
  set_local_word (tempInt+1, pop_word());
  set_local_word (tempInt, pop_word());
  goto LABEL_ENGINELOOP;

case OP_IINC:
  // Arguments: 2
  // Stack: +0
  inc_local_word (pc[0], byte2jint(pc[1]));
  pc += 2;
  goto LABEL_ENGINEFASTLOOP;

case OP_WIDE:
  if( pc[0] == OP_IINC && pc[1] == 0)
  {
    // Arguments: 4
    // Stack: +0
    inc_local_word (pc[2], (JSHORT) (TWOBYTES) (((TWOBYTES) pc[3] << 8) | pc[4]));
    pc += 5;
  }
  else
  {
    thrownException = noSuchMethodError;
    goto LABEL_THROW_EXCEPTION;
  }
  goto LABEL_ENGINEFASTLOOP;

// Notes:
// - OP_WIDE other then OP_IINC is unexpected in TinyVM and CompactVM.

/*end*/







