package com.kp.skey;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordGenerator {

	private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQESTUVWXYZ";
	private static final int UPPER_CASE_LEN = UPPER_CASE.length();
	private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
	private static final int LOWER_CASE_LEN = LOWER_CASE.length();
	private static final String NUMERAL = "0123456789";
	private static final int NUMERAL_LEN = NUMERAL.length();
	public static String symbol = "!@#$%(){}?";
	private static int passwordSize;

	private static String seedText = "Just a default for now.";
	private static final String salt = "twoallbeefpattiesspecialsaucelettucecheesepicklesonasesameseedbun";
	private static long seed;

	public PasswordGenerator(String pgseedtext) {
		// This is a salt that distinguishes passwords for the same domains
		// each machine should use a unique pgseedtext
		seedText = pgseedtext;
	}

	public static boolean containsCorrectCharacterCategories(String passkey, int numericals, int symbols, int uppercase, int lowercase) {
		int uppersCount = 0;
		int lowersCount = 0;
		int numericalsCount = 0;
		int symbolsCount = 0;


		for (int i = 0; i < passkey.length() ; i++) {
			char character = passkey.charAt(i);
			if(UPPER_CASE.contains(String.valueOf(character))) {
				uppersCount++;
			}

			if(LOWER_CASE.contains(String.valueOf(character))) {
				lowersCount++;
			}

			if(NUMERAL.contains(String.valueOf(character))) {
				numericalsCount++;
			}

			if(symbol.contains(String.valueOf(character))) {
				symbolsCount++;
			}
		}

		if (numericals == numericalsCount && symbols == symbolsCount && lowercase == lowersCount && uppercase == uppersCount) {
			return true;
		}
		return false;
	}


	public static String generate(String domaintext, Boolean numericals,
								  Boolean symbols, Boolean upperCase,
								  Boolean lowerCase, int size, long timestamp)
			throws NoSuchAlgorithmException {

		if (size < 4)
			size = 4;
		if (size > 16)
			size = 16;

		passwordSize = size;

		MessageDigest md = MessageDigest.getInstance("MD5");
		String fullSeedText = timestamp + domaintext + salt;
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

		StringBuilder buffer = new StringBuilder(passwordSize);
        int counter = 0;
		while(buffer.length() < passwordSize) {
			if (buffer.length() < 4 && counter <= 4){
                if (upperCase && buffer.length() < passwordSize) {
                    buffer.append(UPPER_CASE.charAt(rng.nextInt(UPPER_CASE_LEN - 1)));
                }
                if (lowerCase && buffer.length() < passwordSize) {
                    buffer.append(LOWER_CASE.charAt(rng.nextInt(LOWER_CASE_LEN - 1)));
                }
                if (numericals && buffer.length() < passwordSize) {
                    buffer.append(NUMERAL.charAt(rng.nextInt(NUMERAL_LEN - 1)));
                }
                if (symbols && buffer.length() < passwordSize) {
                    buffer.append(symbol.charAt(rng.nextInt(symbol.length() - 1)));
                }
            }
            else {
                int index = rng.nextInt(4);
                if (index == 0 && buffer.length() < passwordSize) {
                    if (upperCase) {
                        buffer.append(UPPER_CASE.charAt(rng.nextInt(UPPER_CASE_LEN - 1)));
                    } else {
                        continue;
                    }

                }
                if (index == 1 && buffer.length() < passwordSize) {
                    if (lowerCase) {
                        buffer.append(LOWER_CASE.charAt(rng.nextInt(LOWER_CASE_LEN - 1)));
                    } else {
                        continue;
                    }

                }
                if (index == 2 && buffer.length() < passwordSize) {
                    if (numericals) {
                        buffer.append(NUMERAL.charAt(rng.nextInt(NUMERAL_LEN - 1)));
                    } else {
                        continue;
                    }

                }
                if (index == 3 && buffer.length() < passwordSize) {
                    if (symbols) {
                        buffer.append(symbol.charAt(rng.nextInt(symbol.length() - 1)));
                    } else {
                        continue;
                    }

                }

            }
            counter++;

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
