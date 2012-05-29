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
  tempStackWord = obj2ref(new_single_array(ALJAVA_LANG_OBJECT + *pc, (JINT)get_top_word()));
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

OPCODE(OP_AALOAD)
  // Stack size: -2 + 1
  // Arguments: 0
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(REFERENCE));
  just_pop_word();
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  set_top_ref (*tempWordPtr);
  DISPATCH;

OPCODE(OP_IALOAD)
OPCODE(OP_FALOAD)
  // Stack size: -2 + 1
  // Arguments: 0
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(STACKWORD));
  just_pop_word();
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  set_top_word (*tempWordPtr);
  DISPATCH;

OPCODE(OP_CALOAD)
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JCHAR));
  just_pop_word();
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  set_top_word (*(JCHAR *)tempWordPtr);
  DISPATCH;

OPCODE(OP_SALOAD)
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JSHORT));
  just_pop_word();
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  set_top_word (*(JSHORT *)tempWordPtr);
  DISPATCH;

OPCODE(OP_BALOAD)
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JBYTE));
  just_pop_word();
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  set_top_word (*(JBYTE *)tempWordPtr);
  DISPATCH;

OPCODE(OP_LALOAD)
OPCODE(OP_DALOAD)
  // Stack size: -2 + 2
  // Arguments: 0
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JLONG));
  just_pop_word();
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  set_top_word (*tempWordPtr++);
  push_word (*tempWordPtr);
  DISPATCH;

OPCODE(OP_AASTORE)
  // Stack size: -3
  tempStackWord = pop_ref();
  tempWordPtr = (STACKWORD *)get_ref_at(1);
  if (type_checks_enabled() && tempStackWord != JNULL && !is_assignable(get_class_index(ref2obj(tempStackWord)), get_element_class(get_class_record(get_class_index((Object *)tempWordPtr)))))
  {
    thrownException = JAVA_LANG_ARRAYSTOREEXCEPTION;
    goto LABEL_THROW_EXCEPTION;
  }
  update_array((Object *) tempWordPtr);
  tempWordPtr = array_helper(get_top_word(), (Object *)tempWordPtr, sizeof(REFERENCE));
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  *tempWordPtr = tempStackWord;
  pop_words(2);
  DISPATCH;

OPCODE(OP_IASTORE)
OPCODE(OP_FASTORE)
  // Stack size: -3
  tempStackWord = pop_word();
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(STACKWORD));
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  *tempWordPtr = tempStackWord;
  pop_words(2);
  DISPATCH;

OPCODE(OP_CASTORE)
OPCODE(OP_SASTORE)
  // Stack size: -3
  tempStackWord = pop_word();
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JCHAR));
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  *(JSHORT *)tempWordPtr = tempStackWord;
  pop_words(2);
  DISPATCH;

OPCODE(OP_BASTORE)
  // Stack size: -3
  tempStackWord = pop_word();
  tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JBYTE));
  if(tempWordPtr == NULL)
    goto LABEL_THROW_EXCEPTION;
  *(JBYTE *)tempWordPtr = tempStackWord;
  pop_words(2);
  DISPATCH;

OPCODE(OP_DASTORE)
OPCODE(OP_LASTORE)
  // Stack size: -4
  {
    STACKWORD tempStackWord2;

    tempStackWord2 = pop_word();
    tempStackWord = pop_word();
    tempWordPtr = array_helper(get_top_word(), (Object *)get_word_at(1), sizeof(JLONG));
    if(tempWordPtr == NULL)
      goto LABEL_THROW_EXCEPTION;
    *tempWordPtr++ = tempStackWord;
    *tempWordPtr = tempStackWord2;
    pop_words(2);
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
      goto LABEL_THROW_NULLPTR_EXCEPTION;
    set_top_word (get_array_length (word2obj (tempRef)));
  }
  DISPATCH;

/*end*/
