package javax.microedition.io;
import java.io.*;

public interface StreamConnection {
	
	public void close() throws IOException;

	public InputStream openInputStream() throws IOException;
	
	public DataInputStream openDataInputStream() throws IOException;
	
	public OutputStream openOutputStream() throws IOException;
	
	public DataOutputStream openDataOutputStream() throws IOException;
}
