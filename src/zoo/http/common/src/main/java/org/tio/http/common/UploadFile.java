package org.tio.http.common;

/**
 *
 * @author tanyaowu
 * 2017年7月26日 下午3:12:56
 */
public class UploadFile {

	private String	name	= null;
	private int		size	= -1;

	private byte[] data = null;
	//    private File file = null;

	/**
	 *
	 */
	public UploadFile() {

	}

	public byte[] getData() {
		return data;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	//    public File getFile()
	//    {
	//        return file;
	//    }
	//
	//    public void setFile(File file)
	//    {
	//        this.file = file;
	//    }

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
