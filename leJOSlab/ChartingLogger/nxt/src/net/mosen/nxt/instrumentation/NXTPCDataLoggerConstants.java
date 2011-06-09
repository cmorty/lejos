package net.mosen.nxt.instrumentation;

public interface NXTPCDataLoggerConstants {
    public final static byte ATTENTION1 = 127;
    public final static byte ATTENTION2 = -128;
    
    public final static byte COMMAND_ITEMSPERLINE = 0;
    public final static byte COMMAND_DATATYPE = 1;    
    public final static byte    DT_INTEGER = 0;
    public final static byte    DT_LONG = 1;
    public final static byte    DT_FLOAT = 2;
    public final static byte    DT_DOUBLE = 3;
    public final static byte    DT_STRING = 4;
    public final static byte    DT_BYTE = 5;
}
