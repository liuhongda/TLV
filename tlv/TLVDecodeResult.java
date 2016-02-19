package com.xtc.sync.tlv;

import java.util.Arrays;
import java.util.List;

/**
 * TLV解码结果
 *
 * Created by lhd on 2015/09/26.
 *
 */
public class TLVDecodeResult {

	private int frameType; 

	private int dataType;

	private int tagValue;

	private int length;

	/**
	 * <p>
	 * TLV中的Value的具体值，只有可能存在两种类型：
	 * </p>
	 * <p>
	 * 1.{@link #dataType}=0x20的时候是List<{@link TLVDecodeResult}>类型
	 * </p>
	 * <p>
	 * 2.{@link #dataType}=0x00的时候是byte[]类型
	 * </p>
	 */
	private Object value;

	public TLVDecodeResult getResultByTagValue(int tagValue) {
		TLVDecodeResult result = null;
		if (dataType == TLVEncoder.ConstructedData) {
			List<TLVDecodeResult> list = (List<TLVDecodeResult>) value;
			for (TLVDecodeResult r : list) {
				if (r.getTagValue() == tagValue) {
					result = r;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取int类型的值
	 * 
	 * @return
	 */
	public int getIntValue() {
		int intValue = 0;
		if (value instanceof byte[]) {
			intValue = (int) TLVUtils.byteArrayToLong((byte[]) value);
		}
		return intValue;
	}

	public long getLongValue() {
		long longValue = 0L;
		if (value instanceof byte[]) {
			longValue = TLVUtils.byteArrayToLong((byte[]) value);
		}
		return longValue;
	}

	/**
	 * 获取String类型的值
	 * 
	 * @return
	 */
	public String getStringValue() {
		String strValue = null;
		if (value instanceof byte[]) {
			strValue = new String((byte[]) value);
		}
		return strValue;
	}

	public int getFrameType() {
		return frameType;
	}

	public void setFrameType(int frameType) {
		this.frameType = frameType;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getTagValue() {
		return tagValue;
	}

	public void setTagValue(int tagValue) {
		this.tagValue = tagValue;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String v = null;
		if (dataType == TLVEncoder.ConstructedData) {
			v = value.toString();
		} else {
			v = Arrays.toString((byte[]) value);
		}
		return "TLVDecodeResult [frameType=" + frameType + ", dataType="
				+ dataType + ", tagValue=" + tagValue + ", length=" + length
				+ ", value=" + v + "]";
	}

}
