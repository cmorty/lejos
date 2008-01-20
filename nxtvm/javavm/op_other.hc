/**
 * This is included inside a switch statement.
 */

case OP_ATHROW:
  tempStackWord = pop_ref();
  if (tempStackWord == JNULL)
    goto LABEL_NULLPTR_EXCEPTION;
  thrownException = word2obj (tempStackWord);
  goto LABEL_THROW_EXCEPTION;

case OP_MONITORENTER:
  {
    Object *obj = word2obj(pop_ref());
    SAVE_REGS();
    enter_monitor (currentThread, obj);
    LOAD_REGS();
  }
  goto LABEL_ENGINELOOP;

case OP_MONITOREXIT:
  {
    Object *obj = word2obj(pop_ref());
    SAVE_REGS();
    exit_monitor (currentThread, obj);
    LOAD_REGS();
  }
  goto LABEL_ENGINELOOP;

LABEL_THROW_EXCEPTION:
  SAVE_REGS();
  throw_exception( thrownException);
  LOAD_REGS();
  goto LABEL_ENGINELOOP;

// Notes:
// - Not supported: BREAKPOINT

/*end*/


