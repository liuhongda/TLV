package com.xtc.sync.tlv;

/**
 * TLV编码int和byte[]转换方式
 *
 * Created by lhd on 2015/09/26.
 *
 */
public class TLVUtils {

	/**
	 * 字节数组转int,适合转高位在前低位在后的byte[]
	 * 
	 * @param bytes
	 * @return
	 */
	public static long byteArrayToLong(byte[] bytes) {
		/*long value = 0;
		int length = bytes.length;
		//BigInteger转二进制字符串可自动把前面的0去掉
		value = Long.parseLong(new BigInteger(1, bytes).toString(2), 2);
		return value;*/
		long result = 0;
		int len = bytes.length;
		if (len == 1) {
            byte ch = (byte) (bytes[0] & 0xff);
			result = ch;
		} else if (len == 2) {
			int ch1 = bytes[0] & 0xff;
			int ch2 = bytes[1] & 0xff;
			result = (short) ((ch1 << 8) | (ch2 << 0));
		} else if (len == 4) {
			int ch1 = bytes[0] & 0xff;
			int ch2 = bytes[1] & 0xff;
			int ch3 = bytes[2] & 0xff;
			int ch4 = bytes[3] & 0xff;
			result = (int) ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | (ch4 << 0));
		} else if (len == 8) {
			long ch1 = bytes[0] & 0xff;
			long ch2 = bytes[1] & 0xff;
			long ch3 = bytes[2] & 0xff;
			long ch4 = bytes[3] & 0xff;
			long ch5 = bytes[4] & 0xff;
			long ch6 = bytes[5] & 0xff;
			long ch7 = bytes[6] & 0xff;
			long ch8 = bytes[7] & 0xff;
			result = (ch1 << 56) | (ch2 << 48) | (ch3 << 40) | (ch4 << 32) | (ch5 << 24) | (ch6 << 16) | (ch7 << 8) | (ch8 << 0);
		} else {
			throw new IllegalArgumentException("the length of byte array is uncorrected.");
		}
		return result;
	}

	/**
	 * int转byte[]，高位在前低位在后
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] longToByteArray(long value) {
		/*int length = 4;
        if (value < Math.pow(2, 8 * 1)) {
            length = 1;
        } else if (value < Math.pow(2, 8 * 2)) {
            length = 2;
        } else if (value < Math.pow(2, 8 * 4)) {
            length = 4;
        } else if (value < Math.pow(2, 8 * 8)) {
            length = 8;
        }
		byte[] result = new byte[length];
		// 由高位到低位
		for (int i = 0; i < length; i++) {
			result[i] = (byte) ((value >> (8 * (length - 1 - i))) & 0xff);
		}
		return result;*/
		Long l = new Long(value);
		byte[] valueBytes = null;
		if (l == l.byteValue()) {
			valueBytes = toBytes(value, 1);
		} else if (l == l.shortValue()) {
			valueBytes = toBytes(value, 2);
		} else if (l == l.intValue()) {
			valueBytes = toBytes(value, 4);
		} else if (l == l.longValue()) {
			valueBytes = toBytes(value, 8);
		} else {
			throw new IllegalArgumentException("the value [" + value + "] is too large.");
		}
		return valueBytes;
	}

	private static byte[] toBytes(long value, int len) {
		byte[] valueBytes = new byte[len];
		for (int i = 0;i < len;i++) {
			valueBytes[i] = (byte) (value >>> 8 * (len - i - 1));
		}
		return valueBytes;
	}
}
