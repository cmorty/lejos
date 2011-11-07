package java.io;

public interface Closeable extends AutoCloseable {
	@Override
	void close() throws IOException;
}
