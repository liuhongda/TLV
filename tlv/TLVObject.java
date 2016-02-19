package com.xtc.sync.tlv;

import com.xtc.sync.util.SyncLogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * TLV编码构建对象
 *
 * Created by lhd on 2015/09/26.
 *
 */
public class TLVObject {

	private ByteArrayOutputStream baos;

	public TLVObject() {
		baos = new ByteArrayOutputStream();
	}

	public TLVObject put(int tagValue, long value) {
		writeValue(tagValue, TLVUtils.varIntToByteArray(value));
		return this;
	}

	public TLVObject put(int tagValue, String value) {
		if (value != null)
			writeValue(tagValue, value.getBytes());
		else
			writeValue(tagValue, null);
		return this;
	}

	public TLVObject put(int tagValue, byte[] value) {
		writeValue(tagValue, value);
		return this;
	}

	public TLVObject put(int tagValue, TLVObject tlvObject) {
		writeTLV(tagValue, tlvObject);
		return this;
	}

	private void writeValue(int tagValue, byte[] value) {
		TLVEncodeResult result = TLVEncoder.encode(TLVEncoder.PrimitiveFrame, TLVEncoder.PrimitiveData, tagValue, value);
		try {
			baos.write(result.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeTLV(int tagValue, TLVObject tlvObject) {
		if (tlvObject != null && tlvObject.size() > 0) {
			TLVEncodeResult result = TLVEncoder.encode(TLVEncoder.PrimitiveFrame, TLVEncoder.ConstructedData, tagValue,
					tlvObject.toByteArray());
			try {
				baos.write(result.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int size() {
		return baos.size();
	}

	public byte[] toByteArray() {
		return baos.toByteArray();
	}

	public String toBinaryString() {
		return new BigInteger(1, baos.toByteArray()).toString(2);
	}

	@Override
	public String toString() {
		String result = null;
		try {
			result =  TLVDecoder.decode(baos.toByteArray()).toString();
		} catch (Exception e) {
			SyncLogUtil.e(e);
		}
		return result;
	}
}
