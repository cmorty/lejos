/**
 * This is included inside a switch statement.
 */

OPCODE(OP_ATHROW)
  tempStackWord = pop_ref();
  if (tempStackWord == JNULL)
    goto LABEL_THROW_NULLPTR_EXCEPTION;
  SAVE_REGS();
  throw_exception((Throwable *)word2obj(tempStackWord), true);
  LOAD_REGS();
  DISPATCH_CHECKED;

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

OPCODE(OP_BREAKPOINT)
  tempInt = check_breakpoint(current_stackframe()->methodRecord, pc-1);
  if (tempInt >= 0)
  {
    DISPATCH_OPCODE(tempInt);
  }
  else
  {
    pc--;
  }
  DISPATCH_CHECKED;


/*end*/
