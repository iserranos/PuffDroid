package com.openpuff.android.seguridad;

import android.support.annotation.NonNull;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.encoders.Base64;

import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Seguridad {

    @NonNull
    private static final String KEY_DERIVATION_ALGORITHM;
    @NonNull
    private static final String CIPHER_ALGORITHM;
    @NonNull
    private static final String DELIMITER;
    private static final int KEY_LENGTH;
    private static final int ITERATION_COUNT;
    private static final int SALT_LENGTH;
    @NonNull
    private static final SecureRandom random;

    static {
        Security.addProvider(new BouncyCastleProvider());
        KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
        CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
        DELIMITER = "]";
        KEY_LENGTH = 256;
        ITERATION_COUNT = 1000;
        SALT_LENGTH = 8;
        random = new SecureRandom();
    }

    public String encrypt(@NonNull byte[] plaintext, @NonNull String pass) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            byte[] salt = generateSalt();
            SecretKey key = getKey(salt, pass);

            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext);

            return String.format("%s%s%s%s%s",
                    new String(Base64.encode(salt)), DELIMITER, new String(
                            Base64.encode(iv)), DELIMITER, new String(
                            Base64.encode(cipherText)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public byte[] decrypt(@NonNull String ciphertext, @NonNull String pass) {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted text format");
        }
        try {
            byte[] salt = Base64.decode(fields[0]);
            byte[] iv = Base64.decode(fields[1]);
            byte[] cipherBytes = Base64.decode(fields[2]);

            SecretKey key = getKey(salt, pass);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);

            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*@NonNull
    byte[] generateCSRNPG(@NonNull String pass, int longitud) {
        Digest digest = new SHA512tDigest(pass.getBytes());
        DigestRandomGenerator drg = new DigestRandomGenerator(digest);
        byte[] random = new byte[longitud];
        drg.nextBytes(random);
        return random;
    }*/

    @NonNull
    private SecretKey getKey(@NonNull byte[] salt, @NonNull String password)
            throws Exception {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                    ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(
                    KEY_DERIVATION_ALGORITHM, "BC");
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Throwable e) {
            throw new Exception("Error while generating key", e);
        }
    }

    @NonNull
    private byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);

        return b;
    }

    @NonNull
    private byte[] generateSalt() {
        byte[] b = new byte[SALT_LENGTH];
        random.nextBytes(b);

        return b;
    }
}