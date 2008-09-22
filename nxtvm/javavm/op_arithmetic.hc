/**
 * This is included inside a switch statement.
 */

case OP_ISUB:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) - word2jint(tempStackWord));
  goto LABEL_ENGINEFASTLOOP;

case OP_IADD:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) + word2jint(tempStackWord));
  goto LABEL_ENGINEFASTLOOP;

case OP_IMUL:
  // Arguments: 0
  // Stack: -2 +1
  tempStackWord = pop_word();
  just_set_top_word (word2jint(get_top_word()) * word2jint(tempStackWord));
  goto LABEL_ENGINEFASTLOOP;

case OP_IDIV:
case OP_IREM:
  tempInt = word2jint(pop_word());
  if (tempInt == 0)
  {
    thrownException = arithmeticException;
    goto LABEL_THROW_EXCEPTION;
  }
  just_set_top_word ((*(pc-1) == OP_IDIV) ? word2jint(get_top_word()) / tempInt :
                                            word2jint(get_top_word()) % tempInt);
  goto LABEL_ENGINEFASTLOOP;

case OP_INEG:
  just_set_top_word (-word2jint(get_top_word()));
  goto LABEL_ENGINEFASTLOOP;

#if FP_ARITHMETIC

case OP_FSUB:
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) - 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINEFASTLOOP;

case OP_FADD:
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) + 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINEFASTLOOP;

case OP_FMUL:
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) * 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINEFASTLOOP;

case OP_FDIV:
  // TBD: no division by zero?
  tempStackWord = pop_word();
  just_set_top_word (jfloat2word(word2jfloat(get_top_word()) / 
                     word2jfloat(tempStackWord)));
  goto LABEL_ENGINEFASTLOOP;

case OP_FNEG:
  just_set_top_word (jfloat2word(-word2jfloat(get_top_word())));
  goto LABEL_ENGINEFASTLOOP;

case OP_DNEG:
  {
    JDOUBLE d;
    pop_jdouble(&d);
    d.dnum = -d.dnum;
    push_jdouble(&d);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_DSUB:
  {
    JDOUBLE d1, d2;
    pop_jdouble(&d1);
    pop_jdouble(&d2);
    d2.dnum -= d1.dnum;
    push_jdouble(&d2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_DADD:
  {
    JDOUBLE d1, d2;
    pop_jdouble(&d1);
    pop_jdouble(&d2);
    d2.dnum += d1.dnum;
    push_jdouble(&d2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_DMUL:
  {
    JDOUBLE d1, d2;
    pop_jdouble(&d1);
    pop_jdouble(&d2);
    d2.dnum *= d1.dnum;
    push_jdouble(&d2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_DDIV:
  {
    JDOUBLE d1, d2;
    pop_jdouble(&d1);
    pop_jdouble(&d2);
    d2.dnum /= d1.dnum;
    push_jdouble(&d2);
    goto LABEL_ENGINEFASTLOOP;
  }

#endif // FP_ARITHMETIC

case OP_LNEG:
  {
    JLONG l;
    pop_jlong(&l);
    l.lnum = -l.lnum;
    push_jlong(&l);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_LADD:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    l2.lnum += l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_LSUB:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    l2.lnum -= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_LMUL:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    l2.lnum *= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_LDIV:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    if (l1.lnum == 0)
    {
      thrownException = arithmeticException;
      goto LABEL_THROW_EXCEPTION;
    }
    l2.lnum /= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINEFASTLOOP;
  }

case OP_LREM:
  {
    JLONG l1, l2;
    pop_jlong(&l1);
    pop_jlong(&l2);
    if (l1.lnum == 0)
    {
      thrownException = arithmeticException;
      goto LABEL_THROW_EXCEPTION;
    }
    l2.lnum %= l1.lnum;
    push_jlong(&l2);
    goto LABEL_ENGINEFASTLOOP;
  }
// Notes:
// - Not supported: LADD, LSUB, LMUL, LREM, FREM, DREM
// - Operations on doubles are truncated to low float

/*end*/







