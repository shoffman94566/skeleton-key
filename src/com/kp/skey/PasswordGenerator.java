package com.kp.skey;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordGenerator {

	private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQESTUVWXYZ";
	private static final int UPPER_CASE_LEN = UPPER_CASE.length();
	public static int upperMin = 2;
	public static int upperMax = 5;
	private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
	private static final int LOWER_CASE_LEN = LOWER_CASE.length();
	private static final String NUMERAL = "0123456789";
	private static final int NUMERAL_LEN = NUMERAL.length();
	public static int numeralMin = 2;
	public static int numeralMax = 4;
	public static String symbol = "!@#$%(){}?";
	private static int symbol_len = symbol.length();
	public static int symbolMin = 2;
	public static int symbolMax = 3;
	public static int passwordMin = 5;
	public static int passwordMax = 24;
	private static int passwordSize;

	private static String seedText = "Just a default for now.";
	private static final String salt = "twoallbeefpattiesspecialsaucelettucecheesepicklesonasesameseedbun";
	private static long seed;

	public PasswordGenerator(String pgseedtext) {
		// This is a salt that distinguishes passwords for the same domains
		// each machine should use a unique pgseedtext
		seedText = pgseedtext;
	}

	public static String generate(String domaintext, Boolean numericals,
			Boolean symbols, Boolean alpha, int size)
			throws NoSuchAlgorithmException {

		if (size < 5)
			size = 5;
		if (size > 24)
			size = 24;

		passwordSize = size;

		MessageDigest md = MessageDigest.getInstance("MD5");
		String fullSeedText = System.currentTimeMillis() + domaintext + salt;
		md.update(fullSeedText.getBytes());
		byte[] digest = md.digest();
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		String hash = sb.toString();

		// conver hash to long
		// seed = Long.parseLong(hash, 16);
		seed = new BigInteger(hash, 16).longValue();
		// seed = parseUnsignedHex(hash);

		// seed random number generator
		Random rng = new Random(seed);

		// select length of our password
		// if (passwordMin == passwordMax) {
		// passwordSize = passwordMin;
		// } else {
		// passwordSize = passwordMin + rng.nextInt(passwordMax - passwordMin);
		// }

		// collect characters for the password
		int numUpper = 0, numNumeral = 0, numSymbol = 0, numLower = 0;
		if (alpha) {
			numUpper = upperMin + rng.nextInt(upperMax - upperMin);
		}
		if (numericals) {
			numNumeral = numeralMin + rng.nextInt(numeralMax - numeralMin);
		}
		if (symbols) {
			numSymbol = symbolMin + rng.nextInt(symbolMax - symbolMin);
		}
		numLower = passwordSize - numUpper - numNumeral - numSymbol;

		StringBuilder buffer = new StringBuilder(passwordSize);

		for (int i = 0; i < numUpper; i++) {
			buffer.append(UPPER_CASE.charAt(rng.nextInt(UPPER_CASE_LEN - 1)));
		}
		for (int i = 0; i < numLower; i++) {
			buffer.append(LOWER_CASE.charAt(rng.nextInt(LOWER_CASE_LEN - 1)));
		}
		for (int i = 0; i < numNumeral; i++) {
			buffer.append(NUMERAL.charAt(rng.nextInt(NUMERAL_LEN - 1)));
		}
		for (int i = 0; i < numSymbol; i++) {
			buffer.append(symbol.charAt(rng.nextInt(symbol.length() - 1)));
		}

		// append the characters to the password in random order

		StringBuilder password = new StringBuilder(passwordSize);

		for (int i = 0; i < passwordSize; i++) {
			int len = buffer.length();
			int charpos = rng.nextInt(len);
			password.append(buffer.charAt(charpos));
			buffer = buffer.deleteCharAt(charpos);
		}

		// done!

		return password.toString();

	}

}
