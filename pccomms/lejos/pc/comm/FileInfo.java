package lejos.pc.comm;

public class FileInfo {
	public String fileName;
	public byte fileHandle;
	public int fileSize;
	public byte status;
	
	public FileInfo(String fileName) {
		this.fileName = fileName;
	}	
}
