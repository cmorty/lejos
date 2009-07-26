package java.lang;
import java.lang.annotation.Annotation;

import lejos.nxt.VM;

/**
 * Not fully functional. 
 */
public class Class<T>
{
    // Note the following fields are mapped on to read only flash entries
    // held within the VM. They should not be changed. New fields should not
    // be added to this class unless changes are also made to the VM.
    private short arrayDim;
    private short elementClass;
    private byte numFields;
    private byte numMethods;
    private byte parentClass;
    private byte flags;
    
    @SuppressWarnings("unchecked")
	public <U> Class<? extends U> asSubclass(Class<U> cls)
    {
    	if (!cls.isAssignableFrom(this))
    		throw new ClassCastException();
    	
    	return (Class<? extends U>)this;
    }
    
	@SuppressWarnings("unchecked")
	public T cast(Object o)
	{
		if (!this.isInstance(o))
			throw new ClassCastException();
			
		return (T)o;
	}
	
	/**
	 * Always return false.
	 * @return True if asserts are enabled false if they are disabled.
	 */
	public boolean desiredAssertionStatus()
	{
		return (VM.getVMOptions() & VM.VM_ASSERT) != 0;
	}
	
	/**
	 * @exception ClassNotFoundException Thrown always in TinyVM.
	 */
	@SuppressWarnings("unused")
	public static Class<?> forName (String aName)
		throws ClassNotFoundException
	{
		throw new ClassNotFoundException();
	}
	
	public Class<?> getComponentType()
	{
		if (!this.isArray())
			return null;
		
		return VM.getClass(this.elementClass & 0xFF);
	}
	
	public Class<?>[] getInterfaces()
	{
		//FIXME Andy
		throw new UnsupportedOperationException();
	}
		
	@SuppressWarnings("unchecked")
	public Class<? super T> getSuperclass()
	{
		if (0 != (flags & (VM.VMClass.C_INTERFACE | VM.VMClass.C_PRIMITIVE)) || this == Object.class)
			return null;
		
		return (Class<? super T>)VM.getClass(this.parentClass & 0xFF);
	}

	public boolean isAnnotation()
	{
		return this.isInterface() && Annotation.class != this && Annotation.class.isAssignableFrom(this);
	}	
	
	public boolean isArray()
	{
		return 0 != (flags & VM.VMClass.C_ARRAY);
	}	
	
	public boolean isAssignableFrom(Class<?> cls)
	{
		//FIXME Andy
		return VM.isAssignable(cls, this);
	}	
	
	public boolean isEnum()
	{
		return this.getSuperclass() == Enum.class;
	}	
	
	public boolean isInstance(Object obj)
	{
		if (obj == null)
			return false;
		
		return this.isAssignableFrom(obj.getClass());
	}
	
	public boolean isInterface()
	{
		return 0 != (flags & VM.VMClass.C_INTERFACE);
	}
	
	public boolean isPrimitive()
	{
		return 0 != (flags & VM.VMClass.C_PRIMITIVE);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (this.isInterface())
			sb.append("interface ");
		else
			sb.append("class ");
		
		//TODO classnumber instead of hash?
		sb.append(System.identityHashCode(this));
		
		return sb.toString();
	}
}
