package com.xtc.sync.tlv;

/**
 * TLV编码实现
 * <p/>
 * Created by lhd on 2015/09/26.
 */
public class TLVEncoder {

    /**
     * 基本数据类型
     */
    public static final int PrimitiveFrame = 0x00;

    /**
     * 私有类型
     */
    public static final int PrivateFrame = 0x40;

    /**
     * 基本类型数据编码
     */
    public static final int PrimitiveData = 0x00;

    /**
     * TLV类型数据编码
     */
    public static final int ConstructedData = 0x20;

    /**
     * TLV格式编码
     *
     * @param frameType
     * @param dataType
     * @param tagValue
     * @param value
     * @return
     */
    public static TLVEncodeResult encode(int frameType, int dataType,
                                         int tagValue, byte[] value) {
        byte[] tagBytes = encodeTag(frameType, dataType, tagValue);
        // System.out.println("tag:"+new BigInteger(1, tagBytes).toString(2));

        byte[] lengthBytes = encodeLength(value == null ? 0 : value.length);
//		System.out.println("length:" + value.length);
//		System.out.println("lengthBytes:" + new BigInteger(1, lengthBytes).toString(2));

        TLVEncodeResult result = new TLVEncodeResult();
        result.setTagBytes(tagBytes);
        result.setTagSize(tagBytes.length);
        result.setLengthBytes(lengthBytes);
        result.setLengthSize(lengthBytes.length);
        result.setValueBytes(value);
        result.setValueSize(value == null ? 0 : value.length);
        return result;
    }

    /**
     * TLV格式编码
     *
     * @param frameType
     * @param dataType
     * @param tagValue
     * @param value
     * @return
     */
    public static TLVEncodeResult encode(int frameType, int dataType,
                                         int tagValue, String value) {
        if (value != null)
            return encode(frameType, dataType, tagValue, value.getBytes());
        else
            return encode(frameType, dataType, tagValue, (byte[]) null);
    }

    public static TLVEncodeResult encode(int frameType, int dataType,
                                         int tagValue, long value) {
        return encode(frameType, dataType, tagValue,
                TLVUtils.longToByteArray(value));
    }

    /**
     * <p>
     * 生成 Tag ByteArray
     * </p>
     * <p>
     * <b>其中 tagValue <= 2097151，超过之后编码的结果是错误的</b>
     * </p>
     *
     * @param tagValue  Tag 值，即协议中定义的交易类型 或 基本数据类型
     * @param frameType TLV类型，Tag首字节最左两bit为00：基本类型，01：私有类型(自定义类型)
     * @param dataType  数据类型，Tag首字节第5位为0：基本数据类型，1：结构类型(TLV类型，即TLV的V为一个TLV结构)
     * @return Tag ByteArray
     */
    public static byte[] encodeTag(int frameType, int dataType, int tagValue) {
        int result = frameType | dataType | tagValue;
        int digit = 0;
        if (tagValue >= 0x1f) {
            result = frameType | dataType | 0x80;
            digit = (int) computeTagDigit(tagValue);
            result <<= 8 * digit;
            //高位到低位
//			rawTag = encodeTagValueFromHighToLowBit(rawTag, digit, tagValue);
            //低位到高位
            result = encodeValueFromLowToHighBit(result, digit, tagValue);
        }
        return intToByteArrayForTag(result, digit);
    }

    /**
     * 从高位到低位对tagValue进行编码
     *
     * @param result
     * @param digit
     * @param value
     * @return
     */
    private static int encodeValueFromHighToLowBit(int result, int digit, int value) {
        //高位到低位
        for (int i = digit - 1; i > 0; i--) {
            result |= ((value >> i * 7 & 0x7f) | 0x80) << i * 8;
        }
        result |= value & 0x7f;
        return result;
    }

    /**
     * 从低位到高位对tagValue进行编码
     *
     * @param result 形参，int类型的只传值进来，因此必须反馈编码结果
     * @param digit
     * @param value
     * @return
     */
    private static int encodeValueFromLowToHighBit(int result, int digit, int value) {
        //低位到高位
        for (int i = 0; i < digit - 1; i++) {
            result |= ((value >> i * 7 & 0x7f) | 0x80) << (digit - 1 - i) * 8;
//			System.out.println("tag:"+Integer.toBinaryString(((tagValue >> i * 7 & 0x7f) | 0x80) << (digit - 1 - i) * 8));
//			System.out.println("rawTag:" + Integer.toBinaryString(rawTag));
        }
//		System.out.println("high:" + Integer.toBinaryString((tagValue >> (digit - 1) * 7) & 0x7f));
        result |= (value >> (digit - 1) * 7) & 0x7f;
        return result;
    }

    private static byte[] intToByteArrayForTag(int value, int digit) {
        byte[] result = new byte[digit + 1];
        int length = result.length;
        result[0] = (byte) ((value >> (8 * digit)) & 0xff);
        for (int i = 0; i < length; i++) {
            //这里有两种方法来实现高位到低位或者低位到高位的编码，具体选择哪种待评估
//			if (i == length -1) {
//				result[i] = (byte) ((value >> (8 * (i - 1))) & 0x7f);
//			} else {
//				result[i] = (byte) ((value >> (8 * (i - 1))) & 0xff | 0x80);
//			}
            result[i] = (byte) ((value >> (8 * (digit - i))) & 0xff);//高位到低位
        }
        return result;
    }

    private static byte[] intToByteArrayForLength(int value, int digit) {
        byte[] result = new byte[digit];
        int length = result.length;
        result[0] = (byte) ((value >> (8 * (digit - 1))) & 0xff);
        for (int i = 1; i < length; i++) {
            //这里有两种方法来实现高位到低位或者低位到高位的编码，具体选择哪种待评估
//			if (i == length -1) {
//				result[i] = (byte) ((value >> (8 * (i - 1))) & 0x7f);
//			} else {
//				result[i] = (byte) ((value >> (8 * (i - 1))) & 0xff | 0x80);
//			}
            result[i] = (byte) ((value >> (8 * (digit - i - 1))) & 0xff);//高位到低位
        }
        return result;
    }

    /**
     * 对数计算换底公式
     *
     * @param value
     * @param base
     * @return
     */
    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * 计算Tag字节数,推导出来的计算公式
     *
     * @param value
     * @return
     */
    private static double computeTagDigit(double value) {
        if (value < 0x1f) {
            throw new IllegalArgumentException(
                    "the tag value must not less than 31.");
        }
        return Math.ceil(log(value + 1, 128));
    }

    /**
     * 生成Length的byte数组
     *
     * @param length
     * @return
     */
    public static byte[] encodeLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException(
                    "the length must not less than 0.");
        }
        if (length < 128) {
            byte[] lengthBytes = new byte[1];
            lengthBytes[0] = (byte) (0x7f & length);
            return lengthBytes;
        } else {
            int digit = (int) computeLengthDigit(length);
            int result = 0;
            result = encodeValueFromLowToHighBit(result, digit, length);
            return intToByteArrayForLength(result, digit);
        }
    }

    /**
     * 计算length的字节数,推导出来的计算公式
     *
     * @param length
     * @return
     */
    private static double computeLengthDigit(int length) {
        return Math.ceil(log(length + 1, 128));
    }
}
