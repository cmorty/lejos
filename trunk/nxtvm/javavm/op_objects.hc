/**
 * This is included inside a switch statement.
 */

case OP_NEW:
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
  goto LABEL_ENGINELOOP;

case OP_GETSTATIC:
case OP_PUTSTATIC:
case OP_GETSTATIC_1:
case OP_PUTSTATIC_1:
case OP_GETSTATIC_2:
case OP_PUTSTATIC_2:
case OP_GETSTATIC_3:
case OP_PUTSTATIC_3:
  // Stack: +1 or +2 for GETSTATIC, -1 or -2 for PUTSTATIC
  {
    STATICFIELD fieldRecord;
    byte *fbase1 = null;
    unsigned int fieldType;
#if RECORD_REFERENCES
    byte isRef;
#endif
    unsigned int opcode;
    unsigned int fieldSize;
    unsigned int fldIdx;
    boolean wideWord;

    #if DEBUG_FIELDS
    printf ("---  GET/PUTSTATIC --- (%d, %d)\n", (int) pc[0], (int) pc[1]);
    #endif

    if (!is_initialized_idx (pc[0]))
    {
      SAVE_REGS();
      tempInt = dispatch_static_initializer (get_class_record (pc[0]), pc - 1);
      LOAD_REGS();
      if( tempInt)
        goto LABEL_ENGINELOOP;
    }

    opcode = pc[-1];
    fldIdx = pc[1];

    if( opcode >= OP_GETSTATIC_1)
      fldIdx |= (((opcode - OP_GETSTATIC_1) >> 1) + 1) << 8;

    fieldRecord = ((STATICFIELD *) get_static_fields_base())[fldIdx];

    fieldType = (fieldRecord >> 12) & 0x0F;
#if RECORD_REFERENCES
    isRef = (fieldType == T_REFERENCE);
#endif
    fieldSize = typeSize[fieldType];
    wideWord = false;
    if( fieldSize > 4)
    {
      fieldSize = 4;
      wideWord = true;
    }

    fbase1 = get_static_state_base() + get_static_field_offset (fieldRecord);

    #if DEBUG_FIELDS
    printf ("fieldSize  = %d\n", (int) fieldSize);
    printf ("fbase1  = %d\n", (int) fbase1);
    #endif

    if (opcode == OP_GETSTATIC || opcode == OP_GETSTATIC_1 || opcode == OP_GETSTATIC_2 || opcode == OP_GETSTATIC_3)
    {
#if RECORD_REFERENCES
      if (isRef)
        push_ref (get_word(fbase1, fieldSize));
      else
#endif
      {
        tempStackWord = get_word(fbase1, fieldSize);
        if( fieldType == T_CHAR)
          tempStackWord = (TWOBYTES) tempStackWord;
        push_word (tempStackWord);

        if (wideWord)
          push_word (get_word_4(fbase1 + 4));
      }
    }
    else
    {
      if (wideWord)
        store_word (fbase1 + 4, 4, pop_word());
#if RECORD_REFERENCES
      if (isRef)
        store_word (fbase1, fieldSize, pop_ref());
      else
#endif
      store_word (fbase1, fieldSize, pop_word());
    }

    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_GETFIELD:
  {
    byte *fbase2;
    unsigned int fieldType;
    unsigned int fieldSize;
    boolean wideWord;

    tempStackWord = get_top_ref();
    if (tempStackWord == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;

    fieldType = get_pgfield_type(pc[0]);
    fieldSize = typeSize[fieldType];
    fbase2 = ((byte *) word2ptr (tempStackWord)) + 
                get_pgfield_offset(pc[0], pc[1]);
    wideWord = false;
    if( fieldSize > 4)
    {
      fieldSize = 4;
      wideWord = true;
    }

    #ifdef DEBUG_FIELDS
    printf ("--- GETFIELD ---\n");
    printf ("fieldType: %d\n", (int) fieldType);
    printf ("fieldSize: %d\n", (int) fieldSize);
    printf ("wideWord: %d\n", (int) wideWord);
    printf ("reference: %d\n", (int) tempStackWord);
    printf ("stackTop: %d\n", (int) stackTop);
    #endif

    #ifdef DEBUG_FIELDS
    printf ("### get_field base=%d size=%d pushed=%d\n", (int) fbase2, (int) fieldSize, (int) tempStackWord);
    #endif

    #ifdef DEBUG_FIELDS
    printf ("### get_field base=%d size=%d pushed=%d\n", (int) fbase2, (int) fieldSize, (int) tempStackWord);
    #endif

#ifdef RECORD_REFERENCES
    if (fieldType == T_REFERENCE)
      set_top_ref (get_word(fbase2, fieldSize));
    else
#endif

    tempStackWord = get_word(fbase2, fieldSize);
    if( fieldType == T_CHAR)
      tempStackWord = (TWOBYTES) tempStackWord;
    set_top_word (tempStackWord);

    #ifdef DEBUG_FIELDS
    printf("Set top word done\n");
    if (wideWord)
    	printf("Wide word\n");
    #endif
    if (wideWord)
      push_word (get_word_4(fbase2 + 4));
    pc += 2;
  }
#ifdef DEBUG_FIELDS
	printf("Going home\n");
#endif
  goto LABEL_ENGINELOOP;

case OP_PUTFIELD:
  {
    byte *fbase3;
    unsigned int fieldType;
    unsigned int fieldSize;
    unsigned int offset;
    boolean wideWord;

    fieldType = get_pgfield_type(pc[0]);
    offset = get_pgfield_offset (pc[0], pc[1]); 
    fieldSize = typeSize[fieldType];
    wideWord = false;
    if( fieldSize > 4)
    {
      fieldSize = 4;
      wideWord = true;
      tempStackWord = get_ref_at (2);
    }
    else
      tempStackWord = get_ref_at (1);

    #ifdef DEBUG_FIELDS
    printf ("--- PUTFIELD ---\n");
    printf ("fieldType: %d\n", (int) fieldType);
    printf ("fieldSize: %d\n", (int) fieldSize);
    printf ("wideWord: %d\n", (int) wideWord);
    printf ("reference: %d\n", (int) tempStackWord);
    #endif

    if (tempStackWord == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fbase3 = ((byte *) word2ptr (tempStackWord)) + offset;
    if (wideWord)
      store_word (fbase3 + 4, 4, pop_word());

    #if 0
    printf ("### put_field base=%d size=%d stored=%d\n", (int) fbase3, (int) fieldSize, (int) get_top_word());
    #endif

#ifdef RECORD_REFERENCES
    if (fieldType == T_REFERENCE)
      store_word (fbase3, fieldSize, pop_ref());
    else
#endif
    store_word (fbase3, fieldSize, pop_word());
    just_pop_ref();
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

#if 1
case OP_GETFIELD_S1:
  {
    byte *fp;
    STACKWORD w = get_top_ref();
    if( w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    set_top_word( (JINT)(JBYTE)fp[0]);
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_GETFIELD_S2:
  {
    byte *fp;
    STACKWORD w = get_top_ref();
    if( w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    set_top_word( (JINT)(JSHORT) ((fp[0] << 8) | fp[1]));
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_GETFIELD_U2:
  {
    byte *fp;
    STACKWORD w = get_top_ref();
    if( w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    set_top_word( (JINT)(JCHAR) ((fp[0] << 8) | fp[1]));
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_GETFIELD_W4:
case OP_GETFIELD_A4:
  {
    byte *fp;
    STACKWORD w = get_top_ref();
    if( w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    set_top_word( get_word_4( fp));
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_PUTFIELD_S1:
  {
    byte *fp;
    STACKWORD w = get_ref_at (1);
    if (w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    fp[0] = (JBYTE) pop_word();
    just_pop_ref();
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_PUTFIELD_S2:
case OP_PUTFIELD_U2:
  {
    byte *fp;
    STACKWORD w = get_ref_at (1);
    if (w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    store_word (fp, 2, pop_word());
    just_pop_ref();
    pc += 2;
  }
  goto LABEL_ENGINELOOP;

case OP_PUTFIELD_W4:
case OP_PUTFIELD_A4:
  {
    byte *fp;
    STACKWORD w = get_ref_at (1);
    if (w == JNULL)
      goto LABEL_NULLPTR_EXCEPTION;
    fp = ((byte *) word2ptr( w)) + pc[0];
    store_word (fp, 4, pop_word());
    just_pop_ref();
    pc += 2;
  }
  goto LABEL_ENGINELOOP;
#endif

case OP_INSTANCEOF:
  // Stack: unchanged
  // Arguments: 2
  // Ignore hi byte
  set_top_word (instance_of (word2obj (get_top_ref()),  pc[1]));
  pc += 2;
  goto LABEL_ENGINELOOP;

case OP_CHECKCAST:
  // Stack: -1 +1 (same)
  // Arguments: 2
  // Ignore hi byte
  pc++;
  tempStackWord = get_top_ref();
  if (tempStackWord != JNULL && !instance_of (word2obj (tempStackWord), pc[0]))
  {
    thrownException = classCastException;
    goto LABEL_THROW_EXCEPTION;
  }
  pc++;
  goto LABEL_ENGINELOOP;

// Notes:
// - NEW, INSTANCEOF, CHECKCAST: 8 bits ignored, 8-bit class index
// - GETSTATIC and PUTSTATIC: 8-bit class index, 8-bit static field record index
// - GETSTATIC_x and PUTSTATIC_x: 8-bit class index, 8-bit static field record low byte of index
// - GETFIELD and PUTFIELD: 4-bit field type, 12-bit field data offset

/*end*/








