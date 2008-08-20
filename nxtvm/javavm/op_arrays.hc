/**
 * This is included inside a switch statement.
 */

case OP_NEWARRAY:
  // Stack size: unchanged
  // Arguments: 1
  SAVE_REGS();
  tempStackWord = obj2ref(new_primitive_array (*pc, get_top_word()));
  LOAD_REGS();
  // Do not modify the stack if an exception has been thrown
  if (tempStackWord != JNULL)
  {
    pc++;
    set_top_ref(tempStackWord);
  }
  // Exceptions are taken care of
  goto LABEL_ENGINELOOP;

case OP_MULTIANEWARRAY:
  // Stack size: -N + 1
  // Arguments: 3
  {
    byte *tempBytePtr;
    tempInt = pc[2] - 1;
    SAVE_REGS();
    tempBytePtr = (byte *) new_multi_array (pc[0], pc[1], pc[2], get_stack_ptr() - tempInt);
    LOAD_REGS();
    // Must not modify either the stack or the pc if an exception has been thrown
    if (tempBytePtr != JNULL)
    {
      pop_words (tempInt);
      pc += 3;
      set_top_ref (ptr2ref (tempBytePtr));
    }
  }
  goto LABEL_ENGINELOOP;

LABEL_NULLPTR_EXCEPTION:
  thrownException = nullPointerException;
  goto LABEL_THROW_EXCEPTION;

LABEL_ARRAY_EXCEPTION:
  if ( tempInt == -1)
    goto LABEL_NULLPTR_EXCEPTION;
  thrownException = arrayIndexOutOfBoundsException;
  goto LABEL_THROW_EXCEPTION;
 
case OP_AALOAD:
  // Stack size: -2 + 1
  // Arguments: 0
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  // arrayStart set by call above
  set_top_ref (word_array_ptr(arrayStart)[tempInt]);
  goto LABEL_ENGINELOOP;

case OP_IALOAD:
case OP_FALOAD:
  // Stack size: -2 + 1
  // Arguments: 0
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (word_array_ptr(arrayStart)[tempInt]);
  goto LABEL_ENGINELOOP;

case OP_CALOAD:
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (jchar_array_ptr(arrayStart)[tempInt]);
  goto LABEL_ENGINELOOP;

case OP_SALOAD:
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (jshort_array_ptr(arrayStart)[tempInt]);
  goto LABEL_ENGINELOOP;

case OP_BALOAD:
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  set_top_word (jbyte_array_ptr(arrayStart)[tempInt]);
  goto LABEL_ENGINELOOP;

case OP_LALOAD:
case OP_DALOAD:
  // Stack size: -2 + 2
  // Arguments: 0
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  tempInt *= 2;
  set_top_word (word_array_ptr(arrayStart)[tempInt++]);
  push_word (word_array_ptr(arrayStart)[tempInt]);
  goto LABEL_ENGINELOOP;

case OP_AASTORE:
  // Stack size: -3
  tempStackWord = pop_ref();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  ref_array_ptr(arrayStart)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;

case OP_IASTORE:
case OP_FASTORE:
  // Stack size: -3
  tempStackWord = pop_word();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  jint_array_ptr(arrayStart)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;

case OP_CASTORE:
case OP_SASTORE:
  // Stack size: -3
  tempStackWord = pop_word();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  jshort_array_ptr(arrayStart)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;

case OP_BASTORE:
  // Stack size: -3
  tempStackWord = pop_word();
  tempInt = array_helper( pc, stackTop);
  just_pop_word();
  if( tempInt < 0)
    goto LABEL_ARRAY_EXCEPTION;
  just_pop_ref();
  jbyte_array_ptr(arrayStart)[tempInt] = tempStackWord;
  goto LABEL_ENGINELOOP;

case OP_DASTORE:
case OP_LASTORE:
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
  goto LABEL_ENGINELOOP;

case OP_ARRAYLENGTH:
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
  goto LABEL_ENGINELOOP;


// Notes:
// * OP_ANEWARRAY is changed to OP_NEWARRAY of data type 0, plus a NOP.

/*end*/







