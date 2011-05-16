/**
 * This is included inside a switch statement.
 */

OPCODE(OP_INVOKEVIRTUAL)
  // Stack: (see method)
  // Arguments: 2
  // Note: pc is updated by dispatch method
  SAVE_REGS();
  dispatch_virtual (word2obj (get_ref_at (pc[0] >> 4)), 
      (TWOBYTES) pc[1] | ((TWOBYTES)(pc[0] & 0x0F) << 8), pc + 2);
  LOAD_REGS();
  DISPATCH_CHECKED;

OPCODE(OP_INVOKESPECIAL)
OPCODE(OP_INVOKESTATIC)
  // Stack: (see method)
  // Arguments: 2
  // Note: pc is updated by dispatch method
  SAVE_REGS();
  dispatch_special_checked (pc[0], pc[1], pc + 2, pc - 1);
  LOAD_REGS();
  DISPATCH_CHECKED;

OPCODE(OP_IRETURN)
OPCODE(OP_FRETURN)
OPCODE(OP_ARETURN)
  // Stack: 1 word copied up
  // Arguments: 0
  SAVE_REGS();
  do_return (1);
  LOAD_REGS();
  DISPATCH_CHECKED;

OPCODE(OP_LRETURN)
OPCODE(OP_DRETURN)
  // Stack: 2 words copied up
  // Arguments: 0
  SAVE_REGS();
  do_return (2);
  LOAD_REGS();
  DISPATCH_CHECKED;

OPCODE(OP_RETURN)
  // Stack: unchanged
  // Arguments: 0
  SAVE_REGS();
  do_return (0);
  LOAD_REGS();
  DISPATCH_CHECKED;


// Notes:
// * INVOKEINTERFACE cannot occur because it's replaced
//   by INVOKEVIRTUAL and a couple of NOOPs.

/*end*/








