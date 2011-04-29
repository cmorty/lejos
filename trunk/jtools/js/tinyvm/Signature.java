package js.tinyvm;

import org.apache.bcel.Constants;

public class Signature
{
	String name;
	String descriptor;

   public Signature (String aName, String aDescriptor)
   {
	   name = aName;
	   descriptor = aDescriptor;
   }

   public Signature (String aSignature)
   {
	   int i = aSignature.indexOf('(');
	   if (i < 0)
		   throw new RuntimeException("illegal signature");
	   
	   name = aSignature.substring(0, i);
	   descriptor = aSignature.substring(i);
   }
   
   public boolean isInitializer()
   {
	   return this.isStaticInitializer() || this.isConstructor();
   }
   
   public boolean isStaticInitializer()
   {
	   return this.name.equals(Constants.STATIC_INITIALIZER_NAME);
   }
   
   public boolean isConstructor()
   {
	   return this.name.equals(Constants.CONSTRUCTOR_NAME);
   }
   
   public int hashCode ()
   {
      return name.hashCode() ^ descriptor.hashCode();
   }

   public boolean equals (Object aOther)
   {
      if (!(aOther instanceof Signature))
         return false;
      Signature pSig = (Signature) aOther;
      return pSig.name.equals(name) && pSig.descriptor.equals(descriptor);
   }

   public String getImage ()
   {
      return name + descriptor;
   }
   
   public String getName()
   {
	   return this.name;
   }
   
   public String getDescriptor()
   {
	   return this.descriptor;
   }

   public String toString ()
   {
      return getImage();
   }
}