package net.mosen.nxt;

public class chksumTest {
    public chksumTest() {
    }
 
    public static void main(String[] args) {
        chksumTest chksumTest = new chksumTest();
        chksumTest.runIt();
    }
    private void hexByteOut(String desc, int value) {
        value = value & 0xffffff;
        final int BITS=32;
        StringBuilder sb1 = new StringBuilder(BITS-BITS/8-1);
        int pow=0;
        for (int i=BITS-1;i>=0;i--) {
            pow= (int)Math.pow(2,i);
            if((pow&value)==pow) sb1.append("1"); else sb1.append("0");
           if(i%8==0)sb1.append(" ");
        }
        //long BinValReprsntn = Long.valueOf(strBinary);
        System.out.format("%3$7s: %1$ 9d 0x%1$0" + BITS/4 + "x: %2$s\n" , value, sb1.toString(), desc);
    }
    
    private int to8Bits(int val) {
        return val&0xff;
    }
    private void runIt(){
        System.out.println("Begin");
        int[] ba =  {0xff,0,0,0};
        int randomXOR = (int)(Math.random()*255);
        int XORMASK = 0xff;
        hexByteOut("test", (byte)0xff);
        hexByteOut("test", to8Bits((byte)0xff));
//        hexByteOut("test", to8Bits(~(45+~45)));
//        
//        hexByteOut("XORmSK", XORMASK);
        
        hexByteOut("rand",randomXOR);
        int ATTN = to8Bits(255 + 128);
        hexByteOut("ATTN", ATTN);
        
        int theSum = to8Bits(ATTN+randomXOR);
        hexByteOut("sum",theSum);
        
        int twosComp = to8Bits(~theSum);
        hexByteOut("2comp",twosComp);
        hexByteOut("sum2",to8Bits(theSum+twosComp));
        for (int i=0;i<1;i++){
            hexByteOut("chk"+i,to8Bits(~(theSum+i+twosComp)));
        }
        
        hexByteOut("test", 255);
        hexByteOut("test", -255);
        hexByteOut("test", -255-1);
        hexByteOut("test", ~255);
        hexByteOut("test", ~-255);
        
        
//        int theXOR = to8Bits(theSum^XORMASK);
//        hexByteOut("XOR",theXOR);
//        hexByteOut("sum2",(to8Bits(theSum+theXOR)));
//        for (int i=0;i<1;i++){
//            hexByteOut("chk"+i,(to8Bits(theSum+i+theXOR)^XORMASK));
//        }
        
 
        System.out.println("------------");
        
 
       
    }
}
