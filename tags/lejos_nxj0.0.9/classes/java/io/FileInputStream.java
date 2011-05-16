package java.io;

import lejos.nxt.Flash;

/**
 * Reads a stream of bytes from a file.
 * This stream uses an internal Buffer of 256 bytes.
 * 
 * @author Brian Bagnall
 * @author Sven Köhler
 */
public class FileInputStream extends InputStream
{
	private int offset;
	private int file_limit;
	private int page_limit;
	private int first_page;
	private byte[] buff;
	
	public FileInputStream(File f) throws FileNotFoundException
	{
		if (!f.exists())
			throw new FileNotFoundException();
		
		this.buff = new byte[Flash.BYTES_PER_PAGE];
		
		this.offset = 0;
		this.file_limit = f.file_length;
		this.page_limit = 0;
		this.first_page = f.page_location;
	}
	
	@Override
	public int available() throws IOException
	{
		return this.file_limit - this.offset;
	}
	
	private void buffPage()
	{
		if (this.offset < this.page_limit)
			return;
		
		int pnum = this.offset / Flash.BYTES_PER_PAGE;
		Flash.readPage(this.buff, this.first_page + pnum);
		this.page_limit = (pnum + 1) * Flash.BYTES_PER_PAGE;
	}

	@Override
	public int read() throws IOException
	{
		if (this.offset >= this.file_limit)
			return -1;

		this.buffPage();
				
		return buff[this.offset++ % Flash.BYTES_PER_PAGE] & 0xFF;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (this.offset >= this.file_limit)
			return -1;

		int favail = this.file_limit - this.offset;
		if (len > favail)
			len = favail;

		int offorig = off;
		while (len > 0)
		{
			this.buffPage();
			
			int pavail = this.page_limit - this.offset;
			if (pavail > len)
				pavail = len;
			
			System.arraycopy(this.buff, this.offset % Flash.BYTES_PER_PAGE, b, off, pavail);
			this.offset += pavail;
			off += pavail;
			len -= pavail;
		}
		return off - offorig;
	}

	@Override
	public long skip(long n) throws IOException
	{
		if (n <= 0)
			return 0;
		
		int avail = this.file_limit - this.offset;
		if (avail > n)
			avail = (int)n;
		
		this.offset += avail;
		
		return n - avail;
	}
}