package com.xtc.sync.tlv;

import java.util.ArrayList;
import java.util.List;

/**
 * TLV解码实现
 * 
 * Created by lhd on 2015/09/26.
 * 
 */
public class TLVDecoder {

	/**
	 * 解析TLV字节数组
	 * 
	 * @param tlvBytes
	 * @return
	 */
	public static TLVDecodeResult decode(byte[] tlvBytes) throws Exception {
		List<TLVDecodeResult> list = new ArrayList<>();
		TLVDecodeResult result = null;
		try {
			result = decodeImpl(tlvBytes, list);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 递归逐个解析TLV
	 * 
	 * @param tlvBytes
	 * @param list
	 * @return
	 */
	private static TLVDecodeResult decodeImpl(byte[] tlvBytes,
											  List<TLVDecodeResult> list) {
		if (tlvBytes == null || tlvBytes.length == 0) {
			return null;
		}
		// 截取Tag
		int tagBytesSize = getTagBytesSize(tlvBytes);
		byte[] tagBytes = new byte[tagBytesSize];
		System.arraycopy(tlvBytes, 0, tagBytes, 0, tagBytesSize);

		// System.out.println("info:"+tlvBytes.length+" "+tagBytesSize);

		// 截取Length
		int lengthBytesSize = getLengthBytesSize(tlvBytes, tagBytesSize);
		byte[] lengthBytes = new byte[lengthBytesSize];
		System.arraycopy(tlvBytes, tagBytesSize, lengthBytes, 0,
				lengthBytesSize);
		
		// 截取Value
		int valueBytesSize = decodeLength(lengthBytes);
		byte[] valueBytes = new byte[valueBytesSize];
		System.arraycopy(tlvBytes, tagBytesSize + lengthBytesSize, valueBytes,
				0, valueBytesSize);
		
		// 解析数据
		TLVDecodeResult result = decodeFirstTLV(tagBytes, lengthBytes,
				valueBytes);
		if (result != null)
			list.add(result);

		int totalSize = tlvBytes.length;
		int firstTLVSize = tagBytesSize + lengthBytesSize + valueBytesSize;
		if (totalSize > firstTLVSize) {// 父V中有多个子TLV结构体
			decodeSecondTLV(tlvBytes, firstTLVSize, list);
		}
		return result;
	}

	/**
	 * 解析同级V中的第一个TLV
	 * 
	 * @param tagBytes
	 * @param lengthBytes
	 * @param valueBytes
	 * @return
	 */
	private static TLVDecodeResult decodeFirstTLV(byte[] tagBytes,
												  byte[] lengthBytes, byte[] valueBytes) {
		int dataType = decodeDataType(tagBytes);
		TLVDecodeResult result = new TLVDecodeResult();
		result.setFrameType(decodeFrameType(tagBytes));
		result.setDataType(dataType);
		result.setTagValue(decodeTagValue(tagBytes));
		result.setLength(decodeLength(lengthBytes));
		if (dataType == TLVEncoder.ConstructedData) {
			List<TLVDecodeResult> childList = new ArrayList<TLVDecodeResult>();
			decodeImpl(valueBytes, childList);
			result.setValue(childList);
		} else {
			result.setValue(valueBytes);
		}

		/*
		 * if (result.getTagValue() == 0) {
		 * System.err.println("RID:"+result.getIntValue()); }
		 */

		return result;
	}

	/**
	 * 解析同级V中的第二个TLV
	 * 
	 * @param tlvBytes
	 * @param firstTLVSize
	 * @param list
	 */
	private static TLVDecodeResult decodeSecondTLV(byte[] tlvBytes,
												   int firstTLVSize, List<TLVDecodeResult> list) {
		int totalSize = tlvBytes.length;
		byte[] nextBytes = new byte[totalSize - firstTLVSize];
		System.arraycopy(tlvBytes, firstTLVSize, nextBytes, 0, totalSize
				- firstTLVSize);
		TLVDecodeResult result = decodeImpl(nextBytes, list);

		return result;
	}

	/**
	 * 获取到全部的TLV数量
	 * 
	 * @param tlvBytes
	 * @return
	 */
	public static int getTLVSize(byte[] tlvBytes) {
		int size = 0;
		// 截取Tag
		int tagBytesSize = getTagBytesSize(tlvBytes);
		byte[] tagBytes = new byte[tagBytesSize];
		System.arraycopy(tlvBytes, 0, tagBytes, 0, tagBytesSize);
		// 截取Length
		int lengthBytesSize = getLengthBytesSize(tlvBytes, tagBytesSize);
		byte[] lengthBytes = new byte[lengthBytesSize];
		System.arraycopy(tlvBytes, tagBytesSize, lengthBytes, 0,
				lengthBytesSize);
		// 截取Value
		int valueBytesSize = decodeLength(lengthBytes);
		byte[] valueBytes = new byte[valueBytesSize];
		System.arraycopy(tlvBytes, tagBytesSize + lengthBytesSize, valueBytes,
				0, valueBytesSize);
		size++;
		// 解析数据
		int dataType = decodeDataType(tagBytes);
		TLVDecodeResult result = new TLVDecodeResult();
		result.setFrameType(decodeFrameType(tagBytes));
		result.setDataType(dataType);
		result.setTagValue(decodeTagValue(tagBytes));
		result.setLength(decodeLength(lengthBytes));
		if (dataType == TLVEncoder.ConstructedData) {
			size = size + getTLVSize(valueBytes);
		} else {
			// size++;
		}
		int totalSize = tlvBytes.length;
		int firstTLVSize = tagBytesSize + lengthBytesSize + valueBytesSize;
		if (totalSize > firstTLVSize) {
			byte[] nextBytes = new byte[totalSize - firstTLVSize];
			System.arraycopy(tlvBytes, firstTLVSize, nextBytes, 0, totalSize
					- firstTLVSize);
			size = size + getTLVSize(nextBytes);
		}
		return size;
	}

	/**
	 * 获取Tag占用的字节数
	 * 
	 * @param tlvBytes
	 * @return
	 */
	public static int getTagBytesSize(byte[] tlvBytes) {
		int length = 0;
		for (byte b : tlvBytes) {
			length++;
			int test = b & 0x80;
			if (test == 0) {
				return length;
			}
		}
		return 0;
	}

	/**
	 * 获取Length占用的字节数
	 * 
	 * @param tlvBytes
	 * @return
	 */
	public static int getLengthBytesSize(byte[] tlvBytes, int offset) {
		int size = 0;
		for (int i = offset;i < tlvBytes.length;i++) {
			size++;
			int test = tlvBytes[i] & 0x80;
			if (test == 0x00) {
				return size;
			}
		}
		return 0;
	}
	
	/**
	 * 解析TLV的Tag中的frameType
	 * 
	 * @param tagBytes
	 * @return
	 */
	public static int decodeFrameType(byte[] tagBytes) {
		return TLVEncoder.PrivateFrame & tagBytes[0];
	}

	/**
	 * 解析TLV的Tag中的dataType
	 * 
	 * @param tagBytes
	 * @return
	 */
	public static int decodeDataType(byte[] tagBytes) {
		return TLVEncoder.ConstructedData & tagBytes[0];
	}

	/**
	 * 解析TLV的Tag中的tagValue
	 * 
	 * @param tagBytes
	 * @return
	 */
	public static int decodeTagValue(byte[] tagBytes) {
		int tagValue = 0x80 & tagBytes[0];
		int result = 0;
		if (tagValue != 0x80) {
			result = tagBytes[0] & 0x1f;
		} else {
			//高位到低位解析
//			result = decodeTagValueFromHighToLowBit(tagBytes);
			//低位到高位解析
			result = decodeValueFromLowToHighBit(tagBytes);
		}
		// System.out.println(result);
		return result;
	}

	/**
	 * 从高位到低位解析tagValue
	 * @param bytes
	 * @return
	 */
	private static int decodeValueFromHighToLowBit(byte[] bytes) {
		int result = 0;
		for (int i = 1; i < bytes.length; i++) {
			result |= (0x7f & bytes[i]) << 7 * (bytes.length - i - 1);
		}
		return result;
	}
	
	/**
	 * 从低位到高位解析tagValue
	 * @param bytes
	 * @return
	 */
	private static int decodeValueFromLowToHighBit(byte[] bytes) {
		int result = 0;
		for (int i = 1; i < bytes.length; i++) {
			result |= (0x7f & bytes[i]) << 7 * (i - 1);
		}
		return result;
	}
	
	/**
	 * 解析TLV中的Length
	 * 
	 * @param lengthBytes
	 * @return
	 */
	public static int decodeLength(byte[] lengthBytes) {
		int result = 0;
		int len = 0x80 & lengthBytes[0];
		if (len != 0x80) {
			result = (int) TLVUtils.byteArrayToLong(lengthBytes);
		} else {
			result |= 0x7f & lengthBytes[0];
			for (int i = 1; i < lengthBytes.length; i++) {
				result |= (0x7f & lengthBytes[i]) << 7 * (lengthBytes.length - i);
			}
		}
		return result;
	}
}
