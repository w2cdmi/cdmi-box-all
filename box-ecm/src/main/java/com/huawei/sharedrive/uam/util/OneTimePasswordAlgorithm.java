package com.huawei.sharedrive.uam.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class contains static methods that are used to calculate the One-Time
 * Password (OTP) using JCE to provide the HMAC-SHA-1.
 *
 * @author Loren Hart
 * @version 1.0
 */
public class OneTimePasswordAlgorithm {
	private OneTimePasswordAlgorithm() {
	}

	// These are used to calculate the check-sum digits.
	// 0 1 2 3 4 5 6 7 8 9
	private static final int[] doubleDigits = { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 };

	/**
	 * Calculates the checksum using the credit card algorithm. This algorithm
	 * has the advantage that it detects any single mistyped digit and any
	 * single transposition of adjacent digits.
	 *
	 * @param num
	 *            the number to calculate the checksum for
	 * @param digits
	 *            number of significant places in the number
	 *
	 * @return the checksum of num
	 */
	public static int calcChecksum(long num, int digits) {
		boolean doubleDigit = true;
		int total = 0;
		while (0 < digits--) {
			int digit = (int) (num % 10);
			num /= 10;
			if (doubleDigit) {
				digit = doubleDigits[digit];
			}
			total += digit;
			doubleDigit = !doubleDigit;
		}
		int result = total % 10;
		if (result > 0) {
			result = 10 - result;
		}
		return result;
	}

	/**
	 * This method uses the JCE to provide the HMAC-SHA-1
	 * 
	 * 
	 * 
	 * algorithm. HMAC computes a Hashed Message Authentication Code and in this
	 * case SHA1 is the hash algorithm used.
	 *
	 * @param keyBytes
	 *            the bytes to use for the HMAC-SHA-1 key
	 * @param text
	 *            the message or text to be authenticated.
	 *
	 * @throws NoSuchAlgorithmException
	 *             if no provider makes either HmacSHA1 or HMAC-SHA-1 digest
	 *             algorithms available.
	 * @throws InvalidKeyException
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 *
	 */

	public static byte[] hmac_sha1(byte[] keyBytes, byte[] text) throws NoSuchAlgorithmException, InvalidKeyException {
		// try {
		Mac hmacSha1;
		try {
			hmacSha1 = Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException nsae) {
			hmacSha1 = Mac.getInstance("HMAC-SHA-1");
		}
		SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
		hmacSha1.init(macKey);
		return hmacSha1.doFinal(text);
		// } catch (GeneralSecurityException gse) {
		// throw new UndeclaredThrowableException(gse);
		// }
	}

	private static final int[] DIGITS_POWER
	// 0 1 2 3 4 5 6 7 8
			= { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };

	// hexadecimal to binary table
	static byte[] hex2bin_table = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x00 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x10 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x20 */
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, /* 0x30 */
			0000, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0000, /* 0x40 */
			0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x50 */
			0000, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0000, /* 0x60 */
			0000, 0000, 0000, 0000, 0000, 0000, 0000, 0000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x70 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x80 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0x90 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0xa0 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0xb0 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0xc0 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0xd0 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, /* 0xe0 */
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 /* 0xf0 */
	};

	/**
	 * hexadecimal to binary 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
	 */
	public static byte[] hexStringToBytesEx(String strHexs) {
		if (strHexs == null || strHexs.length() % 2 != 0) {
			return null;
		}

		int len = strHexs.length() / 2;
		byte[] ret = new byte[len];
		for (int i = 0; i < len; i++) {
			ret[i] = (byte) (hex2bin_table[strHexs.charAt(i * 2)] << 4 | hex2bin_table[strHexs.charAt(i * 2 + 1)]);
		}

		return ret;
	}

	/**
	 * This method generates an OTP value for the given set of parameters.
	 *
	 * @param secret
	 *            the shared secret
	 * @param movingFactor
	 *            the counter, time, or other value that changes on a per use
	 *            basis.
	 * @param codeDigits
	 *            the number of digits in the OTP, not including the checksum,
	 *            if any.
	 * @param addChecksum
	 *            a flag that indicates if a checksum digit
	 * 
	 * 
	 * 
	 * 
	 *            should be appended to the OTP.
	 * @param truncationOffset
	 *            the offset into the MAC result to begin truncation. If this
	 *            value is out of the range of 0 ... 15, then dynamic truncation
	 *            will be used. Dynamic truncation is when the last 4 bits of
	 *            the last byte of the MAC are used to determine the start
	 *            offset.
	 * @throws NoSuchAlgorithmException
	 *             if no provider makes either HmacSHA1 or HMAC-SHA-1 digest
	 *             algorithms available.
	 * @throws InvalidKeyException
	 *             The secret provided was not a valid HMAC-SHA-1 key.
	 *
	 * @return A numeric String in base 10 that includes {@link codeDigits}
	 *         digits plus the optional checksum digit if requested.
	 */
	public static void main(String[] args) {

//		System.out.println("the next 100 OTP is ");
//		byte[] k = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
//		byte[] k1 = { 3, 1, 3, 2, 3, 3, 3, 4, 3, 5, 3, 6, 3, 7, 3, 8, 3, 9, 3, 0, 3, 1, 3, 2, 3, 3, 3, 4, 3, 5, 3, 6, 3,
//				7, 3, 8, 3, 9, 3, 0 };
//
//		// String k2 = "3132333435363738393031323334353637383930";
//		String k2 = "99A3B7A37ED5DA2F0465336A89AE223EF35C41B9";
//
//		byte[] bkey = hexStringToBytesEx(k2);
//		// byte[] bkey = {0x61, 0xA7-256, 0x40, 0x34, 0xA9-256, 0x9A-256, 0x17,
//		// 0xD7-256, 0xE5-256, 0xB4-256, 0xA3-256, 0xF6-256, 0x8A-256, 0xA1-256,
//		// 0x3E, 0xBA-256, 0xE6-256, 0x7E, 0xCD-256, 0xD6-256};
//		try {
//			for (int a = 0; a < 100; a++) {
//				String otpString = generateOTP(bkey, a, 6, false, -1);
//				/*
//				 * if (otpString.equals("131999")) {
//				 * System.out.println("Fount: " + a); break; } else
//				 */ {
//					System.out.println(otpString);
//				}
//			}
//		} catch (InvalidKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	static public String generateOTP(){
		long movingFactor = System.currentTimeMillis();
		int codeDigits=6;
		boolean addChecksum=false;
		int truncationOffset=-1;
		// String k2 = "3132333435363738393031323334353637383930";
		
		String k2 = "99A3B7A37ED5DA2F0465336A89AE223EF35C41B9";

		byte[] bkey = hexStringToBytesEx(k2);
		// put movingFactor value into text byte array
		String result = null;
		int digits = false ? (codeDigits + 1) : codeDigits;
		byte[] text = new byte[8];
		for (int i = text.length - 1; i >= 0; i--) {
			text[i] = (byte) (movingFactor & 0xff);
			movingFactor >>= 8;
			// System.out.println(movingFactor);
		}

		// compute hmac hash
		byte[] hash = null;
		try {
			hash = hmac_sha1(bkey, text);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// put selected bytes into result int
		int offset = hash[hash.length - 1] & 0xf;
		if ((0 <= truncationOffset) && (truncationOffset < (hash.length - 4))) {
			offset = truncationOffset;
		}
		int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

		int otp = binary % DIGITS_POWER[codeDigits];
		if (addChecksum) {
			otp = (otp * 10) + calcChecksum(otp, codeDigits);
		}
		result = Integer.toString(otp);
		while (result.length() < digits) {
			result = "0" + result;
		}
		return result;

	}

	public String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();

	}

	/**
	 * 24. * Convert hex string to byte[] 25. * 26. * @param hexString 27.
	 * * @return 28.
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		// hexString = hexString.toUpperCase(); //����Ǵ�д��ʽ
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
		// return (byte) "0123456789ABCDEF".indexOf(c);
	}
}