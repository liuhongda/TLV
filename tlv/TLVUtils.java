package com.xtc.sync.tlv;

import java.math.BigInteger;

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
		long value = 0;
		int length = bytes.length;
		//BigInteger转二进制字符串可自动把前面的0去掉
		value = Long.parseLong(new BigInteger(1, bytes).toString(2), 2);
		/*for (int i = 0; i < length; i++) {
			value |= (newbytes[i] & 0xFF) << 8 * (length - 1 - i);
		}*/
		return value;
	}

	/**
	 * int转成byte[]，高位在前低位在后
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] varIntToByteArray(long value) {
		int length = 4;
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
		return result;
	}
}
