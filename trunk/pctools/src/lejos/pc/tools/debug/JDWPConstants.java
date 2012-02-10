/*
 * Copyright 1990-2007 Sun Microsystems, Inc. All Rights Reserved. DO NOT ALTER
 * OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 only, as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2 for
 * more details (a copy is included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this work; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara, CA
 * 95054 or visit www.sun.com if you need additional information or have any
 * questions.
 */

package lejos.pc.tools.debug;

public interface JDWPConstants {

	/** ID sizes */
	int SIZEOF_OBJECT_ID = 4;
	int SIZEOF_CLASS_ID = 1;
	int SIZEOF_FIELD_ID = 2;
	int SIZEOF_METHOD_ID = 2;
	int SIZEOF_FRAME_ID = 1;

	/** Command Sets. */
	public static final int CSET_VIRTUAL_MACHINE = 1;
	public static final int CSET_REFERENCE_TYPE = 2;
	public static final int CSET_CLASS_TYPE = 3;
	public static final int CSET_ARRAY_TYPE = 4;
	public static final int CSET_INTERFACE_TYPE = 5;
	public static final int CSET_METHOD = 6;
	public static final int CSET_FIELD = 8;
	public static final int CSET_OBJECT_REFERENCE = 9;
	public static final int CSET_STRING_REFERENCE = 10;
	public static final int CSET_THREAD_REFERENCE = 11;
	public static final int CSET_THREAD_GROUP_REFERENCE = 12;
	public static final int CSET_ARRAY_REFERENCE = 13;
	public static final int CSET_CLASS_LOADER_REFERENCE = 14;
	public static final int CSET_EVENT_REQUEST = 15;
	public static final int CSET_STACK_FRAME = 16;
	public static final int CSET_CLASS_OBJECT_REFERENCE = 17;
	public static final int CSET_EVENT = 64;
	public static final int CSET_NXT = 65;

	/** Commands NXTProxyRequest. */
	public static final int NXT_HANDSHAKE = 1;
	public static final int NXT_STEP_LINE_INFO=2;

	/** Commands VirtualMachine. */
	public static final int VM_VERSION = 1;
	public static final int VM_CLASSES_BY_SIGNATURE = 2;
	public static final int VM_ALL_CLASSES = 3;
	public static final int VM_ALL_THREADS = 4;
	public static final int VM_TOP_LEVEL_THREAD_GROUPS = 5;
	public static final int VM_DISPOSE = 6;
	public static final int VM_ID_SIZES = 7;
	public static final int VM_SUSPEND = 8;
	public static final int VM_RESUME = 9;
	public static final int VM_EXIT = 10;
	public static final int VM_CREATE_STRING = 11;
	public static final int VM_CAPABILITIES = 12;
	public static final int VM_CLASS_PATHS = 13;
	public static final int VM_DISPOSE_OBJECTS = 14;
	public static final int VM_HOLD_EVENTS = 15;
	public static final int VM_RELEASE_EVENTS = 16;
	public static final int VM_CAPABILITIES_NEW = 17;
	public static final int VM_REDEFINE_CLASSES = 18;
	public static final int VM_SET_DEFAULT_STRATUM = 19;
	public static final int VM_ALL_CLASSES_WITH_GENERIC = 20;
	public static final int VM_INSTANCE_COUNTS = 21;

	/** Commands ReferenceType. */
	public static final int RT_SIGNATURE = 1;
	public static final int RT_CLASS_LOADER = 2;
	public static final int RT_MODIFIERS = 3;
	public static final int RT_FIELDS = 4;
	public static final int RT_METHODS = 5;
	public static final int RT_GET_VALUES = 6;
	public static final int RT_SOURCE_FILE = 7;
	public static final int RT_NESTED_TYPES = 8;
	public static final int RT_STATUS = 9;
	public static final int RT_INTERFACES = 10;
	public static final int RT_CLASS_OBJECT = 11;
	public static final int RT_SOURCE_DEBUG_EXTENSION = 12;
	public static final int RT_SIGNATURE_WITH_GENERIC = 13;
	public static final int RT_FIELDS_WITH_GENERIC = 14;
	public static final int RT_METHODS_WITH_GENERIC = 15;
	public static final int RT_INSTANCES = 16;
	public static final int RT_CLASS_VERSION = 17;
	public static final int RT_CONSTANT_POOL = 18;

	/** Commands ClassType. */
	public static final int CT_SUPERCLASS = 1;
	public static final int CT_SET_VALUES = 2;
	public static final int CT_INVOKE_METHOD = 3;
	public static final int CT_NEW_INSTANCE = 4;

	/** Commands ArrayType. */
	public static final int AT_NEW_INSTANCE = 1;

	/** Commands Method. */
	public static final int M_LINE_TABLE = 1;
	public static final int M_VARIABLE_TABLE = 2;
	public static final int M_BYTECODES = 3;
	public static final int M_OBSOLETE = 4;
	public static final int M_VARIABLE_TABLE_WITH_GENERIC = 5;

	/** Commands ObjectReference. */
	public static final int OR_REFERENCE_TYPE = 1;
	public static final int OR_GET_VALUES = 2;
	public static final int OR_SET_VALUES = 3;
	public static final int OR_MONITOR_INFO = 5;
	public static final int OR_INVOKE_METHOD = 6;
	public static final int OR_DISABLE_COLLECTION = 7;
	public static final int OR_ENABLE_COLLECTION = 8;
	public static final int OR_IS_COLLECTED = 9;
	public static final int OR_REFERRING_OBJECTS = 10;

	/** Commands StringReference. */
	public static final int SR_VALUE = 1;

	/** Commands ThreadReference. */
	public static final int TR_NAME = 1;
	public static final int TR_SUSPEND = 2;
	public static final int TR_RESUME = 3;
	public static final int TR_STATUS = 4;
	public static final int TR_THREAD_GROUP = 5;
	public static final int TR_FRAMES = 6;
	public static final int TR_FRAME_COUNT = 7;
	public static final int TR_OWNED_MONITORS = 8;
	public static final int TR_CURRENT_CONTENDED_MONITOR = 9;
	public static final int TR_STOP = 10;
	public static final int TR_INTERRUPT = 11;
	public static final int TR_SUSPEND_COUNT = 12;
	public static final int TR_OWNED_MONITOR_STACK_DEPTH = 13;
	public static final int TR_FORCE_EARLY_RETURN = 14;

	/** Commands ThreadGroupReference. */
	public static final int TGR_NAME = 1;
	public static final int TGR_PARENT = 2;
	public static final int TGR_CHILDREN = 3;

	/** Commands ArrayReference. */
	public static final int AR_LENGTH = 1;
	public static final int AR_GET_VALUES = 2;
	public static final int AR_SET_VALUES = 3;

	/** Commands ClassLoaderReference. */
	public static final int CLR_VISIBLE_CLASSES = 1;

	/** Commands EventRequest. */
	public static final int ER_SET = 1;
	public static final int ER_CLEAR = 2;
	public static final int ER_CLEAR_ALL_BREAKPOINTS = 3;

	/** Commands StackFrame. */
	public static final int SF_GET_VALUES = 1;
	public static final int SF_SET_VALUES = 2;
	public static final int SF_THIS_OBJECT = 3;
	public static final int SF_POP_FRAME = 4;

	/** Commands ClassObjectReference. */
	public static final int COR_REFLECTED_TYPE = 1;

	/** Commands Event. */
	public static final int E_COMPOSITE = 100;

	/** Tag Constants. */
	public static final byte NULL_TAG = 91; // Used for tagged null values.
	public static final byte ARRAY_TAG = 91; // '[' - an array object (objectID
												// size).
	public static final byte BYTE_TAG = 66; // 'B' - a byte value (1 byte).
	public static final byte CHAR_TAG = 67; // 'C' - a character value (2
											// bytes).
	public static final byte OBJECT_TAG = 76; // 'L' - an object (objectID
												// size).
	public static final byte FLOAT_TAG = 70; // 'F' - a float value (4 bytes).
	public static final byte DOUBLE_TAG = 68; // 'D' - a double value (8 bytes).
	public static final byte INT_TAG = 73; // 'I' - an int value (4 bytes).
	public static final byte LONG_TAG = 74; // 'J' - a long value (8 bytes).
	public static final byte SHORT_TAG = 83; // 'S' - a short value (2 bytes).
	public static final byte VOID_TAG = 86; // 'V' - a void value (no bytes).
	public static final byte BOOLEAN_TAG = 90; // 'Z' - a boolean value (1
												// byte).
	public static final byte STRING_TAG = 115; // 's' - a String object
												// (objectID size).
	public static final byte THREAD_TAG = 116; // 't' - a Thread object
												// (objectID size).
	public static final byte THREAD_GROUP_TAG = 103; // 'g' - a ThreadGroup
														// object (objectID
														// size).
	public static final byte CLASS_LOADER_TAG = 108; // 'l' - a ClassLoader
														// object (objectID
														// size).
	public static final byte CLASS_OBJECT_TAG = 99; // 'c' - a class object
													// object (objectID size).

	/** TypeTag Constants. */
	public static final byte TYPE_TAG_CLASS = 1; // ReferenceType is a class.
	public static final byte TYPE_TAG_INTERFACE = 2; // ReferenceType is an
														// interface.
	public static final byte TYPE_TAG_ARRAY = 3; // ReferenceType is an array.

	/** Constants for EventKind. */
	public static final byte EVENT_SINGLE_STEP = 1;
	public static final byte EVENT_BREAKPOINT = 2;
	public static final byte EVENT_FRAME_POP = 3;
	public static final byte EVENT_EXCEPTION = 4;
	public static final byte EVENT_USER_DEFINED = 5;
	public static final byte EVENT_THREAD_START = 6;
	public static final byte EVENT_THREAD_END = 7;
	public static final byte EVENT_CLASS_PREPARE = 8;
	public static final byte EVENT_CLASS_UNLOAD = 9;
	public static final byte EVENT_CLASS_LOAD = 10;
	public static final byte EVENT_FIELD_ACCESS = 20;
	public static final byte EVENT_FIELD_MODIFICATION = 21;
	public static final byte EVENT_EXCEPTION_CATCH = 30;
	public static final byte EVENT_METHOD_ENTRY = 40;
	public static final byte EVENT_METHOD_EXIT = 41;
	public static final byte EVENT_METHOD_EXIT_WITH_RETURN_VALUE = 42;
	public static final byte EVENT_MONITOR_CONTENDED_ENTER = 43;
	public static final byte EVENT_MONITOR_CONTENDED_ENTERED = 44;
	public static final byte EVENT_MONITOR_WAIT = 45;
	public static final byte EVENT_MONITOR_WAITED = 46;
	public static final byte EVENT_VM_INIT = 90;
	public static final byte EVENT_VM_DEATH = 99;
	public static final byte EVENT_VM_DISCONNECTED = 100; // Never sent by
															// across JDWP.
	public static final byte EVENT_VM_START = EVENT_VM_INIT;
	public static final byte EVENT_THREAD_DEATH = EVENT_THREAD_END;

	/** Modifier bit flag: Is synthetic. see MODIFIER_ACC_SYNTHETIC. */
	public static final int MODIFIER_SYNTHETIC = 0xf0000000;
	/** Modifier bit flag: Is public; may be accessed from outside its package. */
	public static final int MODIFIER_ACC_PUBLIC = 0x0001;
	/** Modifier bit flag: Is private; usable only within the defining class. */
	public static final int MODIFIER_ACC_PRIVATE = 0x0002;
	/** Modifier bit flag: Is protected; may be accessed within subclasses. */
	public static final int MODIFIER_ACC_PROTECTED = 0x0004;
	/** Modifier bit flag: Is static. */
	public static final int MODIFIER_ACC_STATIC = 0x0008;
	/** Modifier bit flag: Is final; no overriding is allowed. */
	public static final int MODIFIER_ACC_FINAL = 0x0010;
	/** Modifier bit flag: Is synchronized; wrap use in monitor lock. */
	public static final int MODIFIER_ACC_SYNCHRONIZED = 0x0020;
	/** Modifier bit flag: Treat superclass methods specially in invokespecial. */
	public static final int MODIFIER_ACC_SUPER = 0x0020;
	/**
	 * Modifier bit flag: Is bridge; the method is a synthetic method created to
	 * support generic types.
	 */
	public static final int MODIFIER_ACC_BRIDGE = 0x0040;
	/** Modifier bit flag: Is volitile; cannot be reached. */
	public static final int MODIFIER_ACC_VOLITILE = 0x0040;
	/**
	 * Modifier bit flag: Is transient; not written or read by a persistent
	 * object manager.
	 */
	public static final int MODIFIER_ACC_TRANSIENT = 0x0080;
	/**
	 * Modifier bit flag: Is varargs; the method has been declared with variable
	 * number of arguments.
	 */
	public static final int MODIFIER_ACC_VARARGS = 0x0080;
	/**
	 * Modifier bit flag: Is enum; the field hold an element of an enumerated
	 * type.
	 */
	public static final int MODIFIER_ACC_ENUM = 0x0100;
	/** Modifier bit flag: Is native; implemented in a language other than Java. */
	public static final int MODIFIER_ACC_NATIVE = 0x0100;
	/** Modifier bit flag: Is abstract; no implementation is provided. */
	public static final int MODIFIER_ACC_ABSTRACT = 0x0400;
	/**
	 * Modifier bit flag: Is strict; the method floating-point mode is FP-strict
	 */
	public static final int MODIFIER_ACC_STRICT = 0x0800;
	/** Modifier bit flag: Is synthetic. see MODIFIER_SYNTHETIC. */
	public static final int MODIFIER_ACC_SYNTHETIC = 0x1000;

	public static final short NONE = 0;
	public static final short INVALID_THREAD = 10;
	public static final short INVALID_THREAD_GROUP = 11;
	public static final short INVALID_PRIORITY = 12;
	public static final short THREAD_NOT_SUSPENDED = 13;
	public static final short THREAD_SUSPENDED = 14;
	public static final short THREAD_NOT_ALIVE = 15;
	public static final short INVALID_OBJECT = 20;
	public static final short INVALID_CLASS = 21;
	public static final short CLASS_NOT_PREPARED = 22;
	public static final short INVALID_METHODID = 23;
	public static final short INVALID_LOCATION = 24;
	public static final short INVALID_FIELDID = 25;
	public static final short INVALID_FRAMEID = 30;
	public static final short NO_MORE_FRAMES = 31;
	public static final short OPAQUE_FRAME = 32;
	public static final short NOT_CURRENT_FRAME = 33;
	public static final short TYPE_MISMATCH = 34;
	public static final short INVALID_SLOT = 35;
	public static final short DUPLICATE = 40;
	public static final short NOT_FOUND = 41;
	public static final short INVALID_MONITOR = 50;
	public static final short NOT_MONITOR_OWNER = 51;
	public static final short INTERRUPT = 52;
	public static final short INVALID_CLASS_FORMAT = 60;
	public static final short CIRCULAR_CLASS_DEFINITION = 61;
	public static final short FAILS_VERIFICATION = 62;
	public static final short ADD_METHOD_NOT_IMPLEMENTED = 63;
	public static final short SCHEMA_CHANGE_NOT_IMPLEMENTED = 64;
	public static final short INVALID_TYPESTATE = 65;
	public static final short HIERARCHY_CHANGE_NOT_IMPLEMENTED = 66;
	public static final short DELETE_METHOD_NOT_IMPLEMENTED = 67;
	public static final short UNSUPPORTED_VERSION = 68;
	public static final short NAMES_DONT_MATCH = 69;
	public static final short CLASS_MODIFIERS_CHANGE_NOT_IMPLEMENTED = 70;
	public static final short METHOD_MODIFIERS_CHANGE_NOT_IMPLEMENTED = 71;
	public static final short NOT_IMPLEMENTED = 99;
	public static final short NULL_POINTER = 100;
	public static final short ABSENT_INFORMATION = 101;
	public static final short INVALID_EVENT_TYPE = 102;
	public static final short ILLEGAL_ARGUMENT = 103;
	public static final short OUT_OF_MEMORY = 110;
	public static final short ACCESS_DENIED = 111;
	public static final short VM_DEAD = 112;
	public static final short INTERNAL = 113;
	public static final short UNATTACHED_THREAD = 115;
	public static final short INVALID_TAG = 500;
	public static final short ALREADY_INVOKING = 502;
	public static final short INVALID_INDEX = 503;
	public static final short INVALID_LENGTH = 504;
	public static final short INVALID_STRING = 506;
	public static final short INVALID_CLASS_LOADER = 507;
	public static final short INVALID_ARRAY = 508;
	public static final short TRANSPORT_LOAD = 509;
	public static final short TRANSPORT_INIT = 510;
	public static final short NATIVE_METHOD = 511;
	public static final short INVALID_COUNT = 512;

	/** ClassStatus Constants. */
	public static final int JDWP_CLASS_STATUS_VERIFIED = 1;
	public static final int JDWP_CLASS_STATUS_PREPARED = 2;
	public static final int JDWP_CLASS_STATUS_INITIALIZED = 4;
	public static final int JDWP_CLASS_STATUS_ERROR = 8;

	public static final int JDWP_THREAD_STATUS_UNKNOWN = -1;
	public static final int JDWP_THREAD_STATUS_ZOMBIE = 0;
	public static final int JDWP_THREAD_STATUS_RUNNING = 1;
	public static final int JDWP_THREAD_STATUS_SLEEPING = 2;
	public static final int JDWP_THREAD_STATUS_MONITOR = 3;
	public static final int JDWP_THREAD_STATUS_WAIT = 4;
	public static final int JDWP_THREAD_STATUS_NOT_STARTED = 5;

	final static int ONLY_THREADGROUP_ID = 0xffffffe0;
	final static String THREADGROUP_NAME = "LeJOS_NXJ_System";

	final static String VMcmds[][] = {
			{ "" }, // make the list 1-based
			{ "Virtual Machine", "Version", "ClassBySignature", "AllClasses", "AllThreads", "TopLevelThreadGroups", "Dispose", "IDSizes",
					"Suspend", "Resume", "Exit", "CreateString", "Capabilities", "ClassPaths", "DisposeObjects", "HoldEvents", "ReleaseEvents" },
			{ "ReferenceType", "Signature", "ClassLoader", "Modifiers", "Fields", "Methods", "GetValues", "SourceFile", "NestedTypes", "Status",
					"Interfaces", "ClassObject" },
			{ "ClassType", "Superclass", "SetValues", "InvokeMethod", "NewInstance" },
			{ "ArrayType", "NewInstance" },
			{ "InterfaceType", "" },
			{ "Method", "LineTable", "VariableTable", "Bytecodes" },
			{ "UNUSED", "UNUSED" },
			{ "Field", "" },
			{ "ObjectReference", "ReferenceType", "GetValues", "SetValues", "UNUSED", "MonitorInfo", "InvokeMethod", "DisableCollection",
					"EnableCollection", "IsCollected" },
			{ "StringReference", "Value" },
			{ "ThreadReference", "Name", "Suspend", "Resume", "Status", "ThreadGroup", "Frames", "FrameCount", "OwnedMonitors",
					"CurrentCountendedMonitor", "Stop", "Interrupt", "SuspendCount" }, { "ThreadGroupReference", "Name", "Parent", "Children" },
			{ "ArrayReference", "Length", "GetValues", "SetValues" }, { "ClassLoaderReference", "VisibleClasses" },
			{ "EventRequest", "Set", "Clear", "ClearAllBreakpoints" }, { "StackFrame", "GetValues", "SetValues", "ThisObject" },
			{ "ClassObjectReference", "ReflectedType" }, };

	final static String DBGcmds[][] = { { "Event", "Composite" },

	};

	final static String VENcmds[][] = { { "Vender-Specific", "UNKNOWN" } };
}
