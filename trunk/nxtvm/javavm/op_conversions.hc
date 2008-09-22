/**
 * This is included inside a switch statement.
 */

case OP_I2B:
  just_set_top_word ((JBYTE) word2jint(get_top_word()));
  goto LABEL_ENGINELOOP;

case OP_I2S:
  just_set_top_word ((JSHORT) word2jint(get_top_word()));
  goto LABEL_ENGINELOOP;   

case OP_I2C:
  just_set_top_word ((JCHAR) word2jint(get_top_word()));
  goto LABEL_ENGINELOOP;   

case OP_F2D:
  {
    // Arguments: 0
    // Stack: -1 +2
    JDOUBLE d;
    d.dnum = (double)word2jfloat(pop_word());
    push_jdouble(&d);
    goto LABEL_ENGINELOOP;
  }

case OP_D2F:
  {
    // Arguments: 0
    // Stack: -2 +1
    JDOUBLE d;
    pop_jdouble(&d);
    push_word(jfloat2word((float) d.dnum));
    goto LABEL_ENGINELOOP;
  }
case OP_L2I:
  {
    // Arguments: 0
    // Stack: -2 +1
    JLONG l;
    pop_jlong(&l);
    push_word((JINT) l.lnum);
    goto LABEL_ENGINELOOP;
  }

case OP_I2L:
  {
    JLONG l;
    l.lnum = (LLONG) pop_jint();
    push_jlong(&l);
    goto LABEL_ENGINELOOP;
  }

#if FP_ARITHMETIC

case OP_I2F:
  // Arguments: 0
  // Stack: -1 +1
  just_set_top_word (jfloat2word ((JFLOAT) word2jint(get_top_word())));
  goto LABEL_ENGINELOOP;

case OP_I2D:
  {
    // Arguments: 0
    // Stack: -1 +2
    JDOUBLE d;
    d.dnum = (double) (int) pop_word();
    push_jdouble(&d);
    goto LABEL_ENGINELOOP;
  }

case OP_F2I:
  // Arguments: 0
  // Stack: -1 +1
  just_set_top_word ((JINT) word2jfloat(get_top_word()));
  goto LABEL_ENGINELOOP;

case OP_D2I:
  {
    // Arguments: 0
    // Stack: -2 +1  
    JDOUBLE d;
    pop_jdouble(&d);
    push_word((JINT) d.dnum);
    goto LABEL_ENGINELOOP;
  }

case OP_L2F:
  {
    JLONG l;
    pop_jlong(&l);
    push_word (jfloat2word ((JFLOAT) l.lnum));
    goto LABEL_ENGINELOOP;
  }

case OP_L2D:
  {
    JDOUBLE d;
    JLONG l;
    pop_jlong(&l);
    d.dnum = (double) l.lnum;
    push_jdouble(&d);
    goto LABEL_ENGINELOOP;
  }

case OP_F2L:
  {
    JLONG l;
    tempStackWord = pop_word();
    l.lnum = (LLONG)word2jfloat(tempStackWord);
    push_jlong(&l);
    goto LABEL_ENGINELOOP;
  }

case OP_D2L:
  {
    JDOUBLE d;
    JLONG l;
    pop_jdouble(&d);
    l.lnum = (LLONG)d.dnum;
    push_jlong(&l);
    goto LABEL_ENGINELOOP;
  }

#endif

/*end*/


