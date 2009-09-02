/**
 * This is included inside a switch statement.
 */

OPCODE(OP_ANEWARRAY)
  // Stack size: unchanged
  // Arguments: 1
  tempInt = (pc[0] << 8) | pc[1];
  SAVE_REGS();
  tempStackWord = obj2ref(new_single_array (tempInt, (JINT)get_top_word()));
  LOAD_REGS();
  // Do not modify the stack if an exception has been thrown
  if (tempStackWord != JNULL)
  {
    pc += 2;
    set_top_ref(tempStackWord);
  }
  // Exceptions are taken care of
  DISPATCH_CHECKED;

OPCODE(OP_NEWARRAY)
  // Stack size: unchanged
  // Arguments: 1
  SAVE_REGS();
  tempStackWord = obj2ref(new_primitive_array (*pc, (JINT)get_top_word()));
  LOAD_REGS();
  // Do not modify the stack if an exception has been thrown
  if (tempStackWord != JNULL)
  {
    pc++;
    set_top_ref(tempStackWord);
  }
  // Exceptions are taken care of
  DISPATCH_CHECKED;

OPCODE(OP_MULTIANEWARRAY)
  // Stack size: -N + 1
  // Arguments: 3
  {
    int cls = (pc[0] << 8) | pc[1];
    tempInt = pc[2] - 1;
    SAVE_REGS();
    tempStackWord = obj2ref(new_multi_array (cls, pc[2], get_stack_ptr() - tempInt));
    LOAD_REGS();
    // Must not modify either the stack or the pc if an exception has been thrown
    if (tempStackWord != JNULL)
    {
      pop_words (tempInt);
      pc += 3;
      set_top_ref (tempStackWord);
    }
  }
  DISPATCH_CHECKED;

LABEL_NULLPTR_EXCEPTION:
  thrownException = nullPointerException;
  goto LABEL_THROW_EXCEPTION;

LABEL_ARRAY_EXCEPTION:
  if ( tempInt == -1)
    goto LABEL_NULLPTR_EXCEPTION;
  thrownException = arrayIndexOutOfBoundsException;
  goto LABEL_THROW_EXCEPTION;
 
OPCODE(OP_AALOAD)
  // Stack size: -2 + 1
  // Arguments: 0
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  // arrayStart set by call above
  set_top_ref (word_array_ptr(arrayStart)[tempInt]);
  DISPATCH;

OPCODE(OP_IALOAD)
OPCODE(OP_FALOAD)
  // Stack size: -2 + 1
  // Arguments: 0
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (word_array_ptr(arrayStart)[tempInt]);
  DISPATCH;

OPCODE(OP_CALOAD)
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (jchar_array_ptr(arrayStart)[tempInt]);
  DISPATCH;

OPCODE(OP_SALOAD)
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (jshort_array_ptr(arrayStart)[tempInt]);
  DISPATCH;

OPCODE(OP_BALOAD)
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (jbyte_array_ptr(arrayStart)[tempInt]);
  DISPATCH;

OPCODE(OP_LALOAD)
OPCODE(OP_DALOAD)
  // Stack size: -2 + 2
  // Arguments: 0
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  tempInt *= 2;
  set_top_word (word_array_ptr(arrayStart)[tempInt++]);
  push_word (word_array_ptr(arrayStart)[tempInt]);
  DISPATCH;

OPCODE(OP_AASTORE)
  // Stack size: -3
  tempStackWord = pop_ref();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  tempWordPtr = (STACKWORD *)pop_ref();
  if (type_checks_enabled() && tempStackWord != JNULL && !is_assignable(get_class_index(ref2obj(tempStackWord)), get_element_class(get_class_record(get_class_index((Object *)tempWordPtr)))))
  {
    thrownException = arrayStoreException;
    goto LABEL_THROW_EXCEPTION;
  }
  update_array((Object *) tempWordPtr);
  ref_array_ptr(arrayStart)[tempInt] = tempStackWord;
  DISPATCH;

OPCODE(OP_IASTORE)
OPCODE(OP_FASTORE)
  // Stack size: -3
  tempStackWord = pop_word();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  jint_array_ptr(arrayStart)[tempInt] = tempStackWord;
  DISPATCH;

OPCODE(OP_CASTORE)
OPCODE(OP_SASTORE)
  // Stack size: -3
  tempStackWord = pop_word();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  jshort_array_ptr(arrayStart)[tempInt] = tempStackWord;
  DISPATCH;

OPCODE(OP_BASTORE)
  // Stack size: -3
  tempStackWord = pop_word();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  jbyte_array_ptr(arrayStart)[tempInt] = tempStackWord;
  DISPATCH;

OPCODE(OP_DASTORE)
OPCODE(OP_LASTORE)
  // Stack size: -4
  {
    STACKWORD tempStackWord2;

    tempStackWord2 = pop_word();
    tempStackWord = pop_word();
    tempInt = array_helper( pc, stackTop);
    just_pop_word();
    if( tempInt < 0)
      goto LABEL_ARRAY_EXCEPTION;
    just_pop_ref();
    tempInt *= 2;
    jint_array_ptr(arrayStart)[tempInt++] = tempStackWord;
    jint_array_ptr(arrayStart)[tempInt] = tempStackWord2;
  }
  DISPATCH;

OPCODE(OP_ARRAYLENGTH)
  // Stack size: -1 + 1
  // Arguments: 0
  {
    REFERENCE tempRef;

    tempRef = get_top_ref();
    
    //printf ("ARRAYLENGTH for %d\n", (int) tempRef); 
    
    if (tempRef == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    set_top_word (get_array_length (word2obj (tempRef)));
  }
  DISPATCH;


/*end*/







