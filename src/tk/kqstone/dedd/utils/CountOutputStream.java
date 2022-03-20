package tk.kqstone.dedd.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountOutputStream extends FilterOutputStream {

	private long count;

	private WriteByteListener writeByteListener;

	public CountOutputStream(OutputStream out) {
		super(out);
		count = 0;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		count++;
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
		sendCount();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
		sendCount();
	}

	public void addWriteByteListener(WriteByteListener l) {
		this.writeByteListener = l;
	}

	private void sendCount() {
		if (writeByteListener != null)
			writeByteListener.byteWrited(count);
	}

}
