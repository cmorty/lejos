package lejos.pc.tools;
import lejos.pc.comm.*;

import java.io.*; 


/**
 * connects to a NXT using either Bluetooth or USB and supplies input and output
 * data streams.
 * 
 * @author Roger Glassey revised 5/1/2008
 */

public class Connector
{
   DataInputStream dataIn;
   DataOutputStream dataOut;
   InputStream is;
   OutputStream os;
   NXTInfo[] _nxtInfo;

   /**
    * 
    * @param NXT
    *            can be the friendly name of the NXT or a 16 character address
    * @param useUSB
    * @return true if connection was made
    */
   public boolean connectTo(String NXT, boolean useUSB)
   {
      NXTComm nxtComm = null;
      
      if (useUSB)  
      {
         try { nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);}
         catch (Exception e ){}
         
         try {
             _nxtInfo = nxtComm.search(null, NXTCommFactory.USB);
         } catch (NXTCommException e) {
             System.out.println("Exception in search");
         }
         
         if (_nxtInfo.length == 0) {
             System.out.println("No NXT Found");
             System.exit(1);
         }

         try {
             nxtComm.open(_nxtInfo[0]);
         } catch (NXTCommException e) {
             System.out.println("Exception in open");
         }
         System.out.println("Opened USB connection");
      } else  //Bluetooth
      {  
		 try {
			 nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		 } catch (NXTCommException e) {
			System.err.println("Failed to load Bluetooth comms driver: " + e.getMessage());
			System.exit(1);
		 }
         if (NXT == null || NXT == " "||NXT.length()<1) 
         {
            System.out.println("searching for all");
            try{_nxtInfo = nxtComm.search(null, NXTCommFactory.BLUETOOTH);}
            catch (NXTCommException ex) { System.out.println("search Failed "+ex);}
         } else if (NXT.length() < 8) 
         {
            System.out.println("searching for " +NXT);
            try {_nxtInfo = nxtComm.search(NXT,  NXTCommFactory.BLUETOOTH);}
            catch (Exception ex) { System.out.println("search Failed "+ex);}

         } else 
         {
            _nxtInfo = new NXTInfo[1];
            _nxtInfo[0] = new NXTInfo("unknown ", NXT);// NXT is actually the address
         }
         if (_nxtInfo == null ||_nxtInfo.length == 0) 
         {
            System.out
            .println("NXT "+NXT+" not found: is BT adaper on? is NXT on? ");
            return false;
         }
         System.out.println("Connecting to " + _nxtInfo[0].name + " "
               + _nxtInfo[0].btDeviceAddress);
         boolean opened = false;
         try {opened = nxtComm.open(_nxtInfo[0]); }
         catch (NXTCommException ex) { System.out.println(ex);}
         if (!opened) 
         { 
            System.out.println("Failed to open " + _nxtInfo[0].name + " "
                  + _nxtInfo[0].btDeviceAddress);
            return false;
         }
         System.out.println("Connected to " + _nxtInfo[0].name);
      }
      is = nxtComm.getInputStream();
      dataIn = new DataInputStream(nxtComm.getInputStream());
      os = nxtComm.getOutputStream();
      dataOut = new DataOutputStream(os);
      return true;
   }

   /**
    * @return the InputStream for this connection;
    */
   public InputStream getInputStream() {  return is;}
    

   /**
    * @return the DataInputStream for this connection;
    */
   public DataInputStream getDataIn() { return dataIn;}


   /**
    * @return the OutputSteram for this connection;
    */
   public OutputStream getOutputStream() {     return os;}
 

   /**
    * @return the DataOutputStream for this connection
    */
   public DataOutputStream getDataOut() {  return dataOut;}

   
   public  NXTInfo[] getNXTInfo () { return _nxtInfo; }
   /**
    * earlier name for connectTo();
    * @param NXT
    *            can be the friendly name of the NXT or a 16 character address
    * @param useUSB
    * @return true if connection was made
    */
   public boolean startConnector(String NXT, boolean useUSB)
   {
      return connectTo(NXT,useUSB);
   }
}
