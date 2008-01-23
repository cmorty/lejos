package javax.bluetooth;
/**
 * An NMEA sentence consists of (in order) a '$' character, some 
 * header information, many data fields seperated by ',' a '*' symbol
 * at the end, and a final hexidecimal checksum number (two chars = one byte).
 * e.g. $GPGGA,140819.000,4433.2984,N,08056.3959,W,6,00,50.0,168.7,M,-36.0,M,,0000*53
 * The first two chars (GP) are the talker id
 * The next 3 characters (GGA) are the sentence id
 * @author BB
 *
 */
public class NMEASentence {

}
