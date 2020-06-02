package com.couponmanager.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;

public class CryptoHelper {

	public static final String DEFAULTKEY="sunkakaocodework";
	
	/**
	 * AES 암호화
	 * 
	 * @param srcData
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] AESCTR_Encrypt(byte[] srcData, byte[] key)
			throws Exception {

		return AESCTR(Cipher.ENCRYPT_MODE, srcData, key);
	}
	
	public static String AESCTR_Encode_Default(String encryptedTarget,String encodeType)
			throws Exception {

		byte[] encriptyByteArrray=AESCTR_Encrypt(encryptedTarget.getBytes(encodeType),CryptoHelper.DEFAULTKEY.getBytes());
		return new String(Base64.encode(encriptyByteArrray), "utf-8");
	}
	/**
	 * AES 복호화
	 * 
	 * @param encryptedData
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] AESCTR_Decrypt(byte[] encryptedData, byte[] key)
			throws Exception {

		return AESCTR(Cipher.DECRYPT_MODE, encryptedData, key);
	}
	
	public static String AESCTR_Decode_Default(String encryptedTarget)throws Exception {
		byte[] targetStr=Base64.decode(encryptedTarget.getBytes());
		byte[] encriptyByteArrray=AESCTR_Decrypt(targetStr,CryptoHelper.DEFAULTKEY.getBytes());
		return new String(encriptyByteArrray,"UTF-8");
	}

	/**
	 * AES 처리 (내부적으로 공통의 method를 호출함)
	 * @param mode
	 * @param value
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static byte[] AESCTR(int mode, byte[] value, byte[] key)
			throws Exception {

		byte[] iv = new byte[16];
		SecretKey secureKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(mode, secureKey, new IvParameterSpec(iv));

		byte[] result = cipher.doFinal(value);
		return result;
	}
}
