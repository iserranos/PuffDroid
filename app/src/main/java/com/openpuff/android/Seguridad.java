package com.openpuff.android;

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

class Seguridad {

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

    String encrypt(@NonNull String plaintext, @NonNull String pass) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            byte[] salt = generateSalt();
            SecretKey key = getKey(salt, pass);

            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            return String.format("%s%s%s%s%s",
                    new String(Base64.encode(salt)), DELIMITER, new String(
                            Base64.encode(iv)), DELIMITER, new String(
                            Base64.encode(cipherText)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    String decrypt(@NonNull String ciphertext, @NonNull String password) {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted text format");
        }
        try {
            byte[] salt = Base64.decode(fields[0]);
            byte[] iv = Base64.decode(fields[1]);
            byte[] cipherBytes = Base64.decode(fields[2]);

            SecretKey key = getKey(salt, password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);

            return new String(plaintext, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    private SecretKey getKey(byte[] salt, @NonNull String password)
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

/*import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.generators.HKDFBytesGenerator;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.HKDFParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Seguridad {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    protected static String generarMensaje(String mensaje, String pass1, String pass2, String pass3) {
        ArrayList<byte[]> submensaje = new ArrayList<byte[]>();

        int contador = 0;
        while (mensaje.length() > 16) {
            submensaje.add(mensaje.substring(contador, contador + 16).getBytes());
            contador += 16;
            mensaje = mensaje.substring(contador, contador + 16);
        }
        String pass1KDF = generarKDF(pass1);
        String pass2KDF = generarKDF(pass2);
        String pass3KDF = generarKDF(pass3);

        String semilla = generarIV(pass2KDF);

        try {
            String mensajeCifrado = encrypt(submensaje, pass1KDF.getBytes(), semilla.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected static String descubrirMensaje(String oculto, String pass1, String pass2, String pass3) {
        ArrayList<byte[]> submensaje = new ArrayList<byte[]>();

        int contador = 0;
        while (oculto.length() > 16) {
            submensaje.add(oculto.substring(contador, contador + 16).getBytes());
            contador += 16;
        }

        String pass1KDF = generarKDF(pass1);
        String pass2KDF = generarKDF(pass2);
        String pass3KDF = generarKDF(pass3);

        String semilla = generarIV(pass2KDF);

        try {
            String mensajeCifrado = decrypt(submensaje, pass1KDF.getBytes(), semilla.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data)
            throws Exception {
        int minSize = cipher.getOutputSize(data.length);
        byte[] outBuf = new byte[minSize];
        int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
        int length2 = cipher.doFinal(outBuf, length1);
        int actualLength = length1 + length2;
        byte[] result = new byte[actualLength];
        System.arraycopy(outBuf, 0, result, 0, result.length);
        return result;
    }

    private static String decrypt(ArrayList<byte[]> cipher, byte[] key, byte[] iv) throws Exception {
        PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(
                new AESEngine()));
        CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
        aes.init(false, ivAndKey);
        Iterator<byte[]> iterador = cipher.iterator();
        String resultado = "";
        while (iterador.hasNext()) {
            resultado += cipherData(aes, iterador.next());
        }
        return resultado;
    }

    private static String encrypt(ArrayList<byte[]> plain, byte[] key, byte[] iv) throws Exception {
        PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(
                new AESEngine()));
        CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
        aes.init(true, ivAndKey);
        Iterator<byte[]> iterador = plain.iterator();
        String resultado = "";
        while (iterador.hasNext()) {
            resultado += cipherData(aes, iterador.next());
        }
        return resultado;
    }

    private static String cifrar(String pass1KDF, String encString, String semilla) throws Throwable {
        // AES algorithm with CBC cipher and PKCS5 padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

        PBEKeySpec pbeEKeySpec = new PBEKeySpec(pass1KDF.toCharArray(), generateSalt(), 50, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
        SecretKeySpec secretKey = new SecretKeySpec(keyFactory.generateSecret(pbeEKeySpec).getEncoded(), "AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(semilla.getBytes()));
        return new String(cipher.doFinal(encString.getBytes()));
    }

    private static String generarKDF(String pass) {
        byte[] data = new byte[32];

        //Single-Step KDF specification using SHA256
        Digest digest = new SHA256Digest();
        HKDFBytesGenerator kDF1BytesGenerator = new HKDFBytesGenerator(digest);
        kDF1BytesGenerator.init(new HKDFParameters(pass.getBytes(), generateSalt(), null));
        kDF1BytesGenerator.generateBytes(data, 0, 8);
        return new String(data);
    }

    private static String generarIV(String inicializador) {

        byte[] keyStart = inicializador.getBytes();
        byte[] semilla = null;

        KeyGenerator kgen = null;
        SecureRandom sr = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (sr != null) {
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            semilla = skey.getEncoded();
        }
        String seed = "";
        if (semilla != null) {
            for (byte aSemilla : semilla) {
                seed += Byte.toString(aSemilla);
            }
        }
        return seed;
    }

    private static byte[] generateSalt() {
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ignored) {
        }
        byte[] salt = new byte[32];
        if (random != null) {
            random.nextBytes(salt);
        }

        return salt;
    }
}*/
