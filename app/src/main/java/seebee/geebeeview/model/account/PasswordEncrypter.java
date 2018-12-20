package seebee.geebeeview.model.account;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * Created by Joy on 6/28/2017.
 *
 * Edited by Candy 12/20/2018
 *      changed approach due to deprecation of Crypto in new Android Studio
 *      Implementation taken from http://www.code2learn.com/2011/06/encryption-and-decryption-of-data-using.html
 */

public class PasswordEncrypter {

    private static final String ALGO = "AES";
    private static final String keyString = "GeeBeeViewKey";
    private static final byte[] keyValue = new byte[]
                    {'G', 'e', 'e', 'b', 'e', 'e',
                    'S', 'e', 'c', 'r', 'e', 't',
                    'K', 'e', 'y'}; // Can be anything, just used to seed


    private Key key;
    private Cipher cipher;
    //private SecretKeySpec sks;

    // New key implementation
    private SecretKey secretKey;

    // Key generator
    private static Key generateKey() throws Exception {

        byte[] keyByte = keyString.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyByte = sha.digest(keyByte);
        keyByte = Arrays.copyOf(keyByte, 16); // use only first 128 bit

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, ALGO);
        // Key key = new SecretKeySpec(keyValue, ALGO);
        return secretKeySpec;
    }


    public PasswordEncrypter() {
        try {
            this.key = generateKey();
            this.cipher = Cipher.getInstance(ALGO);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "AES secret key spec error");
        }
    }

    public byte[] encryptPassword(String password) {
        String encryptedValue = "";
        byte[] encVal = null;
        try {
            this.key = generateKey();
//            Log.d(TAG, "Key generated");

            this.cipher = Cipher.getInstance(ALGO);
//            Log.d(TAG, "Cipher instance algo retrieved");

            this.cipher.init(Cipher.ENCRYPT_MODE, key);
//            Log.d(TAG, "Cipher encrypt mode initialized password "+password);

            encVal = this.cipher.doFinal(password.getBytes());
//            Log.d(TAG, "Password encrypted bytes retrieved");

            encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
//            Log.d(TAG, "Encypted value generated "+encryptedValue);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "AES encryption error");
        }

//        Log.d(TAG, "Returning ecrypted value "+encryptedValue);

        byte[] encryptedBytes = Base64.decode(encryptedValue, Base64.DEFAULT);
        return encryptedBytes;
    }

    public String decodePassword(byte[] encodedBytes) {

        String decryptedValue = "";

        String encodedBytesBase64 = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        byte[] decodedBytes = Base64.decode(encodedBytesBase64, Base64.DEFAULT);


//        Log.d(TAG, "Decoder received base64 "+encodedBytesBase64);
        try {
            String decodedString = new String(decodedBytes, "UTF-8");
//            Log.d(TAG, "Decoded string received "+decodedString);

            this.key = generateKey();
//            Log.d(TAG, "Decoder key generated");

            this.cipher = Cipher.getInstance(ALGO);
//            Log.d(TAG, "Decoder cipher instance retrieved");

            this.cipher.init(Cipher.DECRYPT_MODE, key);
//            Log.d(TAG, "Decoder cipher initialized");


            byte[] decryptedByteValue = this.cipher.doFinal(encodedBytes);
//            Log.d(TAG, "Decoder doFinal 1");

            decryptedValue = new String(decryptedByteValue);
//            Log.d(TAG, "Decrypted Value "+decryptedValue);

        } catch (InvalidKeyException e) {
            Log.d(TAG, "ERROR invalid key exception");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d(TAG, "ERROR decryption exception");
            e.printStackTrace();
        }

        return decryptedValue;
    }

}
