package lejos.nxt;
import java.util.Iterator;
import lejos.nxt.comm.RConsole;

/**
 * This class provides access to many of the internal structures of the leJOS
 * virtual machine. In particular it provides Java level access to the classes,
 * methods fields etc. that make up the currently executing program. These
 * structures are used by the VM to create the in memory program. They are
 * similar to the class file format used by a standard JVM, but with much of the
 * detail stripped away.
 * @author andy
 */
public class VM
{
    //The peek methods use the follow base address when accessing VM memory.
    private static final int ABSOLUTE = 0;
    private static final int THREADS = 1;
    private static final int HEAP = 2;
    private static final int IMAGE = 3;
    private static final int STATICS = 4;
    private static final int MEM = 5;

    // Offsets and masks to allow access to a standard Object header
    private static final int OBJ_HDR_SZ = 4;
    private static final int OBJ_FLAGS = 1;
    private static final int OBJ_ARRAY_LEN = 0;
    private static final int OBJ_ARRAY_LEN_ISBIGARRAY = 0xff;
    private static final int OBJ_CLASS = 0;
    private static final int OBJ_ARRAY_TYPE = 0xf;
    private static final int OBJ_BIGARRAY_LEN = 4;
    private static final int OBJ_BIGARRAY_CLASS = 6;
    private static final int OBJ_ARRAY = 0x40;
    private static final int SIMPLE_ARRAY_CLASS_BASE = 13;

    // Basic variable types used within the VM
    public static final int VM_OBJECT = 0;
    public static final int VM_STACKFRAME = 1;
    public static final int VM_CLASS = 2;
    public static final int VM_BOOLEAN = 4;
    public static final int VM_CHAR = 5;
    public static final int VM_FLOAT = 6;
    public static final int VM_DOUBLE = 7;
    public static final int VM_BYTE = 8;
    public static final int VM_SHORT = 9;
    public static final int VM_INT = 10;
    public static final int VM_LONG = 11;
    public static final int VM_VOID = 12;

    // The base address of the in memory program header
    private static final int IMAGE_BASE = memPeekInt(MEM, IMAGE*4);
    private int METHOD_BASE;
    private static final int METHOD_OFFSET = 4;
    private static VM theVM;

    // Singleton don't allow new from other classes
    private VM()
    {
        //IMAGE_BASE = memPeekInt(MEM, IMAGE*4);
        image = new VMImage(IMAGE_BASE);
        METHOD_BASE = memPeekShort(ABSOLUTE, IMAGE_BASE+IMAGE_HDR_LEN+METHOD_OFFSET) + IMAGE_BASE;
    }

    /**
     * Obtain access to the single instance of the VM class. This can then be
     * used to gain access to the more detailed information about the VM and
     * it's internal structures.
     * @return the VM object
     */
    public static VM getVM()
    {
        if (theVM == null)
            theVM = new VM();
        return theVM;
    }


    // Low level memory access functions

    /**
     * Return up to 4 bytes from a specified memory location.
     * @param base Base section of memory.
     * @param offset Offset (in bytes) of the location
     * @param len Number of bytes to return (1-4)
     * @return Memory location contents.
     */
    private static native int memPeek(int base, int offset, int len);

    /**
     * Copy the specified number of bytes from memory into the given object.
     * @param obj Object to copy to
     * @param objoffset Offset (in bytes) within the object
     * @param base Base section to copy from
     * @param offset Offset within the section
     * @param len Number of bytes to copy
     */
    private static native void memCopy(Object obj, int objoffset, int base, int offset, int len);

    /**
     * Return the address of the given objects first data field.
     * @param obj
     * @return the required address
     */
    private native static int getDataAddress (Object obj);

    /**
     * Return the address of the given object.
     * @param obj
     * @return the required address
     */
    private native static int getObjectAddress(Object obj);

    private native static Object memGetReference(int base, int offset);

    /**
     * Return a single byte from the specified memory location.
     * @param base
     * @param offset
     * @return byte value from memory
     */
    private static int memPeekByte(int base, int offset)
    {
        return memPeek(base, offset, 1) & 0xff;
    }

    /**
     * Return a 16 bit word from the specified memory location.
     * @param base
     * @param offset
     * @return short value from memory
     */
    private static int memPeekShort(int base, int offset)
    {
        return memPeek(base, offset, 2) & 0xffff;
    }

    /**
     * Return a 32 bit word from the specified memory location.
     * @param base
     * @param offset
     * @return int value from memory
     */
    private static int memPeekInt(int base, int offset)
    {
        return memPeek(base, offset, 4);
    }

    // The flash structure for class data has a special header added to it to
    // allow it to be treated as a Java object. This is not the normal 4 byte
    // object header (because the second two bytes are used for locking which
    // would not work for a flash based object), it is a two byte header. We
    // need to allow for this header when moving around.
    private static final int CLASS_OBJ_HDR = 2;

    /**
     * This class is used to create a Java class from in memory data. This data
     * may not have a Java object header or may be of a different class to that
     * desired. The new object will contain a snapshot of the in memory data.
     * This snapshot can be updated (if required by calling the update() method.
     */
    static class VMClone
    {

        // Offset of the first cloned data field within the Java object. This
        // is after any red tape fields like the length and address. We also need
        // to allow for the extra "this" value.
        private static final int CLONE_OFFSET = 8;
        int length;
        int address;

        public void update()
        {
            memCopy(this, CLONE_OFFSET, ABSOLUTE, address, length);
        }

        VMClone()
        {
        }

        VMClone(int addr, int len)
        {
            address = addr;
            length = len;
            update();
        }
    }

    // The following allow access to in memory values
    private byte []rawBytes = new byte[8];
    private byte []swappedBytes = new byte[8];

    /**
     * Class that represents a value within the VM. The type field indicates the
     * basic type of the value and the other fields provide access to the actual
     * value.
     */
    public static class VMValue
    {
        // The comments are the offsets from the start of the data
        public int type;
        public Object objectVal; //4
        public int intVal; // 8
        public float floatVal; // 12
        public double doubleVal; // 16
        public long longVal; // 24
        public char charVal; // 32
        public short shortVal; // 34
        public byte byteVal; // 36
        public boolean booleanVal; // 37

        // Number of bytes for a basic type
        private static final int[] lengths = {4, 0, 0, 0, 1, 2, 4, 8, 1, 2, 4, 8};
        // Offset to the corresponding field in the VMValue Object
        private static final int[] offsets = {4, 0, 0, 0, 37, 32, 12, 16, 36, 34, 8, 24};

        VMValue(int typ, byte[] bytes)
        {
            type = typ;
            memCopy(this, offsets[typ], ABSOLUTE, getDataAddress(bytes), lengths[typ]);
        }

        VMValue(int typ, Object obj)
        {
            type = typ;
            objectVal = obj;
        }

    }

    // Provide access to the image header structure
    private static final int IMAGE_HDR_LEN = 20;
    /**
     * The image header for the currently active program.
     */
    public class VMImage extends VMClone
    {
        public short magicNumber;
        public short constantTableOffset;
        public short numConstants;
        public short staticFieldsOffset;
        public short staticStateOffset;
        public short staticStateLength;
        public short numStaticFields;
        public short entryClassesOffset;
        public byte numEntryClasses;
        public byte lastClass;
        public short runtimeOptions;

        VMImage(int addr)
        {
            super(addr, IMAGE_HDR_LEN);
        }

        /**
         * Return an object that can be used to access all of the available class
         * structures.
         * @return Class access object
         */
        public VMClasses getVMClasses()
        {
            return new VMClasses((int)lastClass + 1);
        }

        /**
         * Return an object that can be used to access all of the available
         * constant values.
         * @return Constant access object
         */
        public VMConstants getVMConstants()
        {
            return new VMConstants(constantTableOffset + IMAGE_BASE, numConstants);
        }

        /**
         * Return an object that can be used to access all of the static fields.
         * @return Field access object
         */
        public VMStaticFields getVMStaticFields()
        {
            return new VMStaticFields(staticFieldsOffset+IMAGE_BASE, numStaticFields);
        }

        /**
         * Get the base address for the current image, useful when converting
         * real address to relative ones.
         * @return
         */
        public int getImageBase()
        {
            return IMAGE_BASE;
        }
    }

    // Cached version of the image header.
    private VMImage image;

    /**
     * Return the image header for the currently running program
     * @return Image header.
     */
    public VMImage getImage()
    {
        return image;
    }


    /**
     * This class provides the ability to iterate through a series of in memory
     * structures and returns a Java accessible clone of the structure.
     * @param <E>
     */
    abstract class VMItems<E> implements Iterable<E>
    {
        int cnt;
        private class VMItemsIterator implements Iterator<E>
        {
            int next = 0;
            public boolean hasNext()
            {
                return (next < cnt);
            }

            public E next()
            {
                return get(next++);
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        }

        public Iterator<E> iterator()
        {
            return new VMItemsIterator();
        }

        abstract public E get(int entry);

        VMItems(int cnt)
        {
            this.cnt = cnt;
        }

    }

    // Provide access to the static field data.
    private static final int FIELD_LEN = 2;
    /**
     * This class can be used to gain access to all of the static fields.
     */
    public class VMStaticFields extends VMItems<VMValue>
    {
        int baseAddr;


        /**
         * Return a VMValue object for the specified static field number
         * @param item
         * @return VMBoject for this item
         */
        public VMValue get(int item)
        {
            if (item >= cnt) return null;
            int addr = baseAddr + item*FIELD_LEN;
            int rec = memPeekShort(ABSOLUTE, addr);
            int typ = (rec >> 12) & 0xf;
            int offset = rec & 0xfff;
            memCopy(swappedBytes, 0, STATICS, offset, VMValue.lengths[typ]);
            return new VMValue(typ, swappedBytes);
        }

        VMStaticFields(int base, int cnt)
        {
            super(cnt);
            baseAddr = base;
        }

    }

    // Provide access to constant values
    private static final int CONSTANT_LEN = 4;
    /**
     * This class allows access to all of the constant values.
     */
    public class VMConstants extends VMItems<VMValue>
    {
        int baseAddr;

        /**
         * Return a VMValue object for the specified constant table entry.
         * @param item
         * @return VMValue object for the constant.
         */
        public VMValue get(int item)
        {
            if (item >= cnt) return null;
            int addr = baseAddr + item*CONSTANT_LEN;
            int offset = memPeekShort(ABSOLUTE, addr) + IMAGE_BASE;
            int typ = memPeekByte(ABSOLUTE, addr+2);
            int len = memPeekByte(ABSOLUTE, addr+3);
            if (typ == VM_OBJECT)
            {
                // Must be a string constant
                char chars[] = new char[len];
                for(int i = 0; i < len; i++)
                    chars[i] = (char)memPeekByte(ABSOLUTE, offset+i);
                return new VMValue(typ, new String(chars));
            }
            len = VMValue.lengths[typ];
            memCopy(rawBytes, 0, ABSOLUTE, offset, len);
            for(int i = 0; i < len; i++)
                swappedBytes[i] = rawBytes[(len-1) - i];
            return new VMValue(typ, swappedBytes);
        }

        VMConstants(int base, int cnt)
        {
            super(cnt);
            baseAddr = base;
        }
    }


    // Provide access to internal exception data
    private static final int EXCEPTION_LEN = 7;
    /**
     * An exception record
     */
    public class VMException extends VMClone
    {
        public short start;
        public short end;
        public short handler;
        public byte classIndex;

        VMException(int addr)
        {
            super(addr, EXCEPTION_LEN);
        }

    }

    /**
     * Class to provide access to a series of exception records
     */
    public class VMExceptions extends VMItems<VMException>
    {
        int baseAddr;

        VMExceptions(int baseAddr, int cnt)
        {
            super(cnt);
            this.baseAddr = baseAddr;
        }

        public VMException get(int item)
        {
            return new VMException(baseAddr + item*((EXCEPTION_LEN+1)&~1));
        }
    }

    // Provide access to internal method data
    private static final int METHOD_LEN = 11;
    /**
     * Provide access to information about a method
     */
    public class VMMethod extends VMClone
    {
        public short signature;
        public short exceptionTable;
        public short codeOffset;
        public byte numLocals;
        public byte maxOperands;
        public byte numParameters;
        public byte numExceptionHandlers;
        public byte mflags;

        // Flag values
        public static final byte M_NATIVE = 1;
        public static final byte M_SYNCHRONIZED = 2;
        public static final byte M_STATIC = 4;

        VMMethod(int addr)
        {
            super(addr, METHOD_LEN);
        }

        /**
         * Return access to the exception records for this method.
         * @return the VMExceptions object
         */
        public VMExceptions getVMExceptions()
        {
            return new VMExceptions(exceptionTable + IMAGE_BASE, numExceptionHandlers);
        }

        public int getMethodNumber()
        {
            return (address - METHOD_BASE)/((METHOD_LEN + 1)&~1);
        }
    }

    /**
     * Provide access to a series of method records
     */
    public class VMMethods extends VMItems<VMMethod>
    {
        int baseAddr;

        VMMethods(int baseAddr, int cnt)
        {
            super(cnt);
            this.baseAddr = baseAddr;
        }

        /**
         * Return access to a specific method.
         * @param item
         * @return the VMMethod object
         */
        public VMMethod get(int item)
        {
            return new VMMethod(baseAddr + item*((METHOD_LEN+1)&~1));
        }

    }


    // Provide access to the internal class data
    // This is the size of data within the structure it does not include
    // the special object header. We need to add the size of the special header
    // when moving from item to item.
    private static final int CLASS_LEN = 10;
    /**
     * Provide access to the internal class data
     */
    public class VMClass extends VMClone
    {
        public short size;
        public short arrayDim;
        public short elementClass;
        public byte numFields;
        public byte numMethods;
        public byte parentClass;
        public byte flags;

        // Class flags
        public static final byte C_ARRAY = 2;
        public static final byte C_HASCLINIT = 4;
        public static final byte C_INTERFACE = 8;
        public static final byte C_NOREFS = 0x10;

        // The following are not part of the internal structure
        private int clsNo;

        VMClass(int addr)
        {
            super(addr+CLASS_OBJ_HDR, CLASS_LEN);
            clsNo = (addr - IMAGE_BASE - IMAGE_HDR_LEN)/((CLASS_LEN+CLASS_OBJ_HDR +1) & ~1);
        }

        /**
         * Return access to the methods for this class
         * @return the VMMethods object
         */
        public VMMethods getMethods()
        {
            return new VMMethods(arrayDim + IMAGE_BASE, numMethods);
        }

        /**
         * Return the class number of this class
         * @return the class number
         */
        public int getClassNo()
        {
            return clsNo;
        }

        /**
         * Return a Java Class object for this class.
         * @return Java Class object
         */
        public Class getJavaClass()
        {
            //return classFactory.makeRef(getClassAddress(clsNo));
            return (Class) memGetReference(ABSOLUTE, getClassAddress(clsNo));
        }
    }

    /**
     * Provide access to a series of class records
     */
    public class VMClasses extends VMItems<VMClass>
    {
        VMClasses(int cnt)
        {
            super(cnt);
        }

        /**
         * return a specific class object
         * @param item
         * @return the VMClass object
         */
        public VMClass get(int item)
        {
            if (item >= cnt) return null;
            return new VMClass(getClassAddress(item));
        }

    }

    public class VMFields extends VMItems<VMValue>
    {
        private Object obj;
        private int fieldBase;
        private int fieldTable;

        VMFields(Object obj)
        {
            super(0);
            VMClass cls = getVMClass(obj);
            if (isArray(obj)) return;
            this.obj = obj;
            fieldBase = getDataAddress(obj);
            cnt = cls.numFields;
            fieldTable = cls.elementClass + IMAGE_BASE;
        }

        public VMValue get(int item)
        {
            if (item >= cnt) return null;
            int offset = fieldBase;
            int itemData = fieldTable;
            int typ = memPeekByte(ABSOLUTE, itemData++);
            while (item-- > 0)
            {
                offset += VMValue.lengths[typ];
                typ = memPeekByte(ABSOLUTE, itemData++);
            }
            memCopy(swappedBytes, 0, ABSOLUTE, offset, VMValue.lengths[typ]);
            return new VMValue(typ, swappedBytes);
        }
    }


    public class VMElements extends VMItems<VMValue>
    {
        private Object obj;
        private int arrayBase;
        private int typ;

        VMElements(Object obj)
        {
            super(0);
            int addr = getObjectAddress(obj);
            int hdr = memPeekByte(ABSOLUTE, addr+OBJ_FLAGS);
            if ((hdr & OBJ_ARRAY) == 0) return;
            typ = (hdr & OBJ_ARRAY_TYPE);
            cnt = memPeekByte(ABSOLUTE, addr+OBJ_ARRAY_LEN);
            if (cnt == OBJ_ARRAY_LEN_ISBIGARRAY) cnt = memPeekShort(ABSOLUTE, addr + OBJ_BIGARRAY_LEN);
            this.obj = obj;
            arrayBase = getDataAddress(obj);
        }

        public VMValue get(int item)
        {
            if (item >= cnt) return null;
            int offset = arrayBase + item*VMValue.lengths[typ];
            memCopy(swappedBytes, 0, ABSOLUTE, offset, VMValue.lengths[typ]);
            return new VMValue(typ, swappedBytes);
        }

        public int length()
        {
            return cnt;
        }
    }

    // NOTE: Some of the following methods shouldr really be in VMClasses, but
    // it just makes things a little cleaner to access them if they are here!

    /**
     * Return the address of the specified class number
     * @param clsNo
     * @return the address of the object
     */
    private static int getClassAddress(int clsNo)
    {
        return IMAGE_BASE + IMAGE_HDR_LEN + clsNo*((CLASS_LEN+CLASS_OBJ_HDR +1) & ~1);
    }

    /**
     * Return the class number of the class for the specified object
     * @param obj Object to obtain the class number for.
     * @return The requested class number
     */
    private int getClassNo(Object obj)
    {
        // First we het the address of the object
        int addr = getObjectAddress(obj);
        // Now we read the flag bytes in the object header
        int hdr = memPeekByte(ABSOLUTE, addr+OBJ_FLAGS);
        int cls;
        // If the object is an array we must deal with it in a special way
        if ((hdr & OBJ_ARRAY) != 0)
        {
            // Only BIGARRAYS have full class data. For other arrays we use
            // the basic type information to return an array class.
            if (memPeekByte(ABSOLUTE, addr+OBJ_ARRAY_LEN) == OBJ_ARRAY_LEN_ISBIGARRAY)
                cls = memPeekByte(ABSOLUTE, addr+OBJ_BIGARRAY_CLASS);
            else
                cls = (hdr & OBJ_ARRAY_TYPE) + SIMPLE_ARRAY_CLASS_BASE;
        }
        else
            // All other objects have a class, so read it
            cls = memPeekByte(ABSOLUTE, addr+OBJ_CLASS);
        return cls;
    }



    /**
     * Return the Class object for the provided object.
     * Note: The actual object returned actually resides in flash rom and is
     * part of the leJOS loader. It is not possible to extend this class or
     * modify the contents.
     * @param obj
     * @return the Class object
     */
    public Class getClass(Object obj)
    {
        //return classFactory.makeRef(getClassAddress(getClassNo(obj)));
        return (Class) memGetReference(ABSOLUTE, getClassAddress(getClassNo(obj)));
    }

    /**
     * Return a VMClass object for the provided object.
     * Note: The object returned is actually a copy of the in flash object.
     * @param obj
     * @return the VMClass object
     */
    public VMClass getVMClass(Object obj)
    {
        return new VMClass(getClassAddress(getClassNo(obj)));
    }

    public static Class getPrimitiveClass(int clsNo)
    {
        return (Class) memGetReference(ABSOLUTE, getClassAddress(clsNo));
    }

    /**
     * Return a VMClass object for the provided class object.
     * Note: The object returned is actually a copy of the in flash object.
     * @param cls
     * @return the VMClass object
     */
    public VMClass getVMClass(Class cls)
    {
        return new VMClass(getObjectAddress(cls));
    }

    /**
     * Return a VMClass object for the provided class number.
     * Note: The object returned is actually a copy of the in flash object.
     * @param clsNo
     * @return the VMClass object
     */
    public VMClass getVMClass(int clsNo)
    {
        if (clsNo > image.lastClass) return null;
        return new VMClass(getClassAddress(clsNo));
    }

    /**
     * Return data about the specified method number
     * @param methodNo
     * @return Method object
     */
    public VMMethod getMethod(int methodNo)
    {
        return new VMMethod(METHOD_BASE + methodNo*((METHOD_LEN + 1) & ~1));
    }

    /**
     * Return true if the specified object is an array
     * @param obj
     * @return true iff the specified object is an array
     */
    public boolean isArray(Object obj)
    {
        if (obj == null) return false;
        return (memPeekByte(ABSOLUTE, getObjectAddress(obj)+OBJ_FLAGS) & OBJ_ARRAY) != 0;
    }

    /**
     * Provide access to the fields of an object
     * @param obj
     * @return fields object
     */
    public VMFields getFields(Object obj)
    {
        if (obj == null) return null;
        return new VMFields(obj);
    }

    public VMElements getElements(Object obj)
    {
        if (obj == null) return null;
        return new VMElements(obj);
    }


    private static final int STACKFRAME_LEN = 20;
    public class VMStackFrame extends VMClone
    {
        public int methodRecord;
        public Object monitor;
        public int localsBase;
        public int pc;
        public int stackTop;

        VMStackFrame(int addr)
        {
            super(addr, STACKFRAME_LEN);
        }

        public VMMethod getVMMethod()
        {
            return new VMMethod(methodRecord);
        }
    }

    public class VMStackFrames extends VMItems<VMStackFrame>
    {
        int base;
        int size;
        VMStackFrames(Object stackFrame, int size)
        {
            super(size);
            base = getDataAddress(stackFrame);
            this.size = size;
        }

        public VMStackFrame get(int item)
        {
            int offset = base + (size - item - 1) * STACKFRAME_LEN;
            return new VMStackFrame(offset);
        }
    }

    // Provide access to the internal Thread data.
    private static final int THREAD_LEN = 31;
    /**
     * Internal version of a thread structure
     */
    public class VMThread extends VMClone
    {
        public Thread nextThread;
        public Object waitingOn;
        public int sync;
        public int sleepUntil;
        public Object stackFrameArray;
        public Object stackArray;
        public byte stackFrameArraySize;
        public byte monitorCount;
        public byte threadId;
        public byte state;
        public byte priority;
        public byte interrupted;
        public byte daemon;
        // The following is not part of the VM internal structure
        private Thread thread;

        VMThread(int addr)
        {
            super(addr, THREAD_LEN);
            thread = (Thread)memGetReference(ABSOLUTE, addr - OBJ_HDR_SZ);
        }

        /**
         * Return a Java Thread object for this thread.
         * @return Java Thread object
         */
        public Thread getJavaThread()
        {
            return thread;
        }

        public VMStackFrames getStackFrames()
        {
            return new VMStackFrames(stackFrameArray, stackFrameArraySize);
        }

        public VMStackFrames getStackFrames(int frameCnt)
        {
             return new VMStackFrames(stackFrameArray, frameCnt);

        }
    }

    /**
     * Provide access to a series of internal thread records
     */
    public class VMThreads implements Iterable<VMThread>
    {
        private class VMThreadIterator implements Iterator<VMThread>
        {
            int nextPriority = Thread.MAX_PRIORITY-1;
            int first = 0;
            int nextThread = 0;

            private void findNext()
            {
                if (nextThread != 0)
                    nextThread = memPeekInt(ABSOLUTE, nextThread + OBJ_HDR_SZ);
                if (nextThread == first)
                {
                    first = 0;
                    while (nextPriority >= 0 && first == 0)
                    {
                        first = memPeekInt(THREADS, nextPriority*4);
                        nextPriority--;
                    }
                    nextThread = first;
                }
            }

            public boolean hasNext()
            {
                return nextThread != 0;
            }

            public VMThread next()
            {
                VMThread ret = new VMThread(nextThread+OBJ_HDR_SZ);
                findNext();
                return ret;
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            VMThreadIterator()
            {
                findNext();
            }
        }

        public Iterator<VMThread> iterator()
        {
            return new VMThreadIterator();
        }
    }


    /**
     * Returns access to all of the current internal thread objects
     * @return the VMThreads object
     */
    public VMThreads getVMThreads()
    {
        return new VMThreads();
    }

    public VMThread getVMThread(Thread thread)
    {
        return new VMThread(getDataAddress(thread));
    }

    /**
     * Suspend a thread. This places the specified thread into a suspended
     * state. If thread is null all threads except for the current thread will
     * be suspended.
     * @param thread
     */
    public native static final void suspendThread(Object thread);

    /**
     * Resume a thread. A suspended thread will be resumed to it's previous
     * state. If thread is null all suspended threads will be resumed.
     * @param thread
     */
    public native static final void resumeThread(Object thread);

    /**
     * leJOS allows several "programs" to be linked into a single nxj file
     * the system by default will start execution of program 0. This function
     * allows other programs to be called.
     * @param progNo program number to call
     */
    public native static void executeProgram(int progNo);

    // Flags used to control the Virtual Machine.
    public static final int VM_TYPECHECKS = 1;
    public static final int VM_ASSERT = 2;

     /**
      * Control the run time operation of the leJOS Virtual Machine.
      * @param options Bit flags.
      */
     public static native void setVMOptions(int options);

     /**
      * Return the currently operating Virtual Machine options.
      * @return the options
      */
     public static native int getVMOptions();

}
