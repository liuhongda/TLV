package com.xtc.sync.tlv;

import java.util.Arrays;

/**
 * TLV编码结果
 *
 * Created by lhd on 2015/09/26.
 *
 */
public class TLVEncodeResult {

	private int tagSize;

	private int lengthSize;

	private int valueSize;

	private byte[] tagBytes;

	private byte[] lengthBytes;

	private byte[] valueBytes;

	/**
	 * 将Tag,Length,Value的字节数组按顺序组合成TLV字节数组形式
	 * 
	 * @return
	 */
	public byte[] toByteArray() {
		byte[] newBytes = new byte[tagSize + lengthSize + valueSize];
		System.arraycopy(tagBytes, 0, newBytes, 0, tagSize);
		System.arraycopy(lengthBytes, 0, newBytes, tagSize, lengthSize);
		if (valueBytes != null) {
			System.arraycopy(valueBytes, 0, newBytes, tagSize + lengthSize,
					valueSize);
		}
		return newBytes;
	}

	public int getTagSize() {
		return tagSize;
	}

	public void setTagSize(int tagSize) {
		this.tagSize = tagSize;
	}

	public int getLengthSize() {
		return lengthSize;
	}

	public void setLengthSize(int lengthSize) {
		this.lengthSize = lengthSize;
	}

	public int getValueSize() {
		return valueSize;
	}

	public void setValueSize(int valueSize) {
		this.valueSize = valueSize;
	}

	public byte[] getTagBytes() {
		return tagBytes;
	}

	public void setTagBytes(byte[] tagBytes) {
		this.tagBytes = tagBytes;
	}

	public byte[] getLengthBytes() {
		return lengthBytes;
	}

	public void setLengthBytes(byte[] lengthBytes) {
		this.lengthBytes = lengthBytes;
	}

	public byte[] getValueBytes() {
		return valueBytes;
	}

	public void setValueBytes(byte[] valueBytes) {
		this.valueBytes = valueBytes;
	}

	@Override
	public String toString() {
		return "TLVEncodeResult [tagSize=" + tagSize + ", lengthSize="
				+ lengthSize + ", valueSize=" + valueSize + ", tagBytes="
				+ Arrays.toString(tagBytes) + ", lengthBytes="
				+ Arrays.toString(lengthBytes) + ", valueBytes="
				+ Arrays.toString(valueBytes) + "]";
	}
}
