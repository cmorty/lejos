package js.tinyvm;

/**
 * Machine-generated file. Do not modify.
 */

public interface OpCodeInfo
{
   int OPCODE_ARGS[] =
   {
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 2, 1, 1, 1,
      1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
      2, 1, -1, -1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 4, 0, 2, 1, 2, 0, 0,
      2, 2, 0, 0, 1, 3, 2, 2, 4, 4, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1
   };

   String OPCODE_NAME[] =
   {
      "nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2",
      "iconst_3", "iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0",
      "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush",
      "ldc", "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload", "aload",
      "iload_0", "iload_1", "iload_2", "iload_3", "lload_0", "lload_1",
      "lload_2", "lload_3", "fload_0", "fload_1", "fload_2", "fload_3",
      "dload_0", "dload_1", "dload_2", "dload_3", "aload_0", "aload_1",
      "aload_2", "aload_3", "iaload", "laload", "faload", "daload", "aaload",
      "baload", "caload", "saload", "istore", "lstore", "fstore", "dstore",
      "astore", "istore_0", "istore_1", "istore_2", "istore_3", "lstore_0",
      "lstore_1", "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2",
      "fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0",
      "astore_1", "astore_2", "astore_3", "iastore", "lastore", "fastore",
      "dastore", "aastore", "bastore", "castore", "sastore", "pop", "pop2",
      "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1", "dup2_x2", "swap", "iadd",
      "ladd", "fadd", "dadd", "isub", "lsub", "fsub", "dsub", "imul", "lmul",
      "fmul", "dmul", "idiv", "ldiv", "fdiv", "ddiv", "irem", "lrem", "frem",
      "drem", "ineg", "lneg", "fneg", "dneg", "ishl", "lshl", "ishr", "lshr",
      "iushr", "lushr", "iand", "land", "ior", "lor", "ixor", "lxor", "iinc",
      "i2l", "i2f", "i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i",
      "d2l", "d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl",
      "dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle", "if_icmpeq",
      "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple",
      "if_acmpeq", "if_acmpne", "goto", "jsr", "ret", "tableswitch",
      "lookupswitch", "ireturn", "lreturn", "freturn", "dreturn", "areturn",
      "return", "getstatic", "putstatic", "getfield", "putfield",
      "invokevirtual", "invokespecial", "invokestatic", "invokeinterface",
      "xxxunusedxxx", "new", "newarray", "anewarray", "arraylength", "athrow",
      "checkcast", "instanceof", "monitorenter", "monitorexit", "wide",
      "multianewarray", "ifnull", "ifnonnull", "goto_w", "jsr_w", "breakpoint",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
      "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
   };

   ///////////////////////////
   // lejos nxj specific opcodes
   // codes from 203 to 228 are reserved for QUICK opcodes, what we are
   // doing here is equivalent so let's use them
   int OP_GETSTATIC_1 = 203;
   int OP_GETSTATIC_2 = 204;
   int OP_GETSTATIC_3 = 205;
   int OP_GETSTATIC_4 = 206;
   int OP_PUTSTATIC_1 = 207;
   int OP_PUTSTATIC_2 = 208;
   int OP_PUTSTATIC_3 = 209;
   int OP_PUTSTATIC_4 = 210; 
   int OP_LDC_1 = 211;
   int OP_LDC_2 = 212;
   int OP_LDC_3 = 213;
   int OP_LDC_4 = 214;
   int OP_GETFIELD_1 = 215;
   int OP_PUTFIELD_1 = 216;
}
