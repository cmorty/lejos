package java.lang;
import lejos.nxt.VM;

/**
 * Not fully functional. 
 */
public class Class<T>
{
    // Note the following fields are mapped on to read only flash entries
    // held within the VM. They should not be changed. New fields should not
    // be added to this class unless changes are also made to the VM.
    public short arrayDim;
    public short elementClass;
    public byte numFields;
    public byte numMethods;
    public byte parentClass;
    public byte flags;
	/**
	 * @exception ClassNotFoundException Thrown always in TinyVM.
	 */
	@SuppressWarnings("unused")
	public static Class<?> forName (String aName)
		throws ClassNotFoundException
	{
		throw new ClassNotFoundException();
	}
	
	/**
	 * Always return false.
	 * @return false
	 */
	public boolean desiredAssertionStatus()
	{
		return (VM.getVMOptions() & VM.VM_ASSERT) != 0;
	}
	
	@SuppressWarnings("unchecked")
	public Class<? super T> getSuperclass()
	{
		//FIXME return null if this is a primitive type, void, an interface or Object.class   
		
		return (Class<? super T>)VM.getPrimitiveClass(this.parentClass & 0xFF);
	}
}
