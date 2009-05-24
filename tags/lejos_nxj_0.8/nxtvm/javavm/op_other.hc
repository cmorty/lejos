/**
 * This is included inside a switch statement.
 */

OPCODE(OP_ATHROW)
  tempStackWord = pop_ref();
  if (tempStackWord == JNULL)
    goto LABEL_NULLPTR_EXCEPTION;
  thrownException = word2obj (tempStackWord);
  goto LABEL_THROW_EXCEPTION;

OPCODE(OP_MONITORENTER)
  {
    Object *obj = word2obj(get_top_ref());
    SAVE_REGS();
    enter_monitor (currentThread, obj);
    LOAD_REGS();
    just_pop_ref();
  }
  DISPATCH_CHECKED;

OPCODE(OP_MONITOREXIT)
  {
    Object *obj = word2obj(get_top_ref());
    SAVE_REGS();
    exit_monitor (currentThread, obj);
    LOAD_REGS();
    just_pop_ref();
  }
  DISPATCH_CHECKED;

LABEL_THROW_EXCEPTION:
  SAVE_REGS();
  throw_exception( thrownException);
  LOAD_REGS();
  DISPATCH_CHECKED;

// Notes:
// - Not supported: BREAKPOINT

/*end*/


