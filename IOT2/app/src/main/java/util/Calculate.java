package util;

import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;

import threegpp.milenage.Milenage;
import threegpp.milenage.MilenageBufferFactory;
import threegpp.milenage.MilenageResult;
import threegpp.milenage.biginteger.BigIntegerBuffer;
import threegpp.milenage.biginteger.BigIntegerBufferFactory;
import threegpp.milenage.cipher.Ciphers;


/**
 * Created by cwsu on 2017/5/16.
 */

public class Calculate {
    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String sha3(String input) {

        SHA3.DigestSHA3 md = new SHA3.DigestSHA3(512);
        md.update(input.getBytes());
        return bytesToHex(md.digest()).toLowerCase();

    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    static String xor(String in, String out) {
        char[] keyarray = out.toCharArray(); //Can be any chars, and any length array
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            output.append((char) (in.charAt(i) ^ keyarray[i % keyarray.length]));
        }
        return output.toString();
    }

    public static String encryptDecrypt(String input, String key) {
        // do xor for encry and decry
        return xor(input, key);
    }

    public static String generateRandom() throws NoSuchAlgorithmException {
        SecureRandom number = SecureRandom.getInstance("SHA1PRNG");

        String randomString = "";
        for (int i = 0; i < 256; i++) {
            randomString += number.nextInt(10000) % 2;
        }

        return randomString;
    }

    static Map<Object, Object> generateAuthVector(String Ku) throws NoSuchAlgorithmException, DecoderException {


        Map<MilenageResult, byte[]> av = new HashMap<>();
        Map<Object, Object> result = new HashMap<>();
        Ku = checkKuLen(Ku);
        byte[] K = Hex.decodeHex(Ku.toCharArray());
        byte[] RAND = Hex.decodeHex("23553CBE9637A89D218AE64DAE47BF35".toCharArray());
        byte[] OP = Hex.decodeHex("CDC202D5123E20F62B6D676AC72CB318".toCharArray());
        byte[] SQN = Hex.decodeHex("FF9BB4D0B607".toCharArray());
        byte[] AMF = Hex.decodeHex("B9B9".toCharArray());

        MilenageBufferFactory<BigIntegerBuffer> bufferFactory = BigIntegerBufferFactory.getInstance();
        Cipher cipher = Ciphers.createRijndaelCipher(K);
        byte[] OPc = Milenage.calculateOPc(OP, cipher, bufferFactory);

        Milenage<BigIntegerBuffer> milenage = new Milenage<>(OPc, cipher, bufferFactory);

        try {
            av = milenage.calculateAll(RAND, SQN, AMF, Executors.newCachedThreadPool());

        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        result.put("av", av);
        result.put("RAND", RAND);
        result.put("SQN", SQN);
        result.put("AMF", AMF);

        return result;

    }

    private static String checkKuLen(String Ku) {

        int needLen = 32 - Ku.length();
        if (needLen > 0) {
            StringUtils.rightPad(Ku, needLen, '0');
        } else if (needLen < 0) {
            Ku = Ku.substring(0, 32);
        }

        return Ku;
    }


    public String f1_MAC_A(String K,String AMF,String SQN,String RAND){
        return sha3(K+"_"+AMF+"_"+SQN+"_"+RAND);
    }

    public String f2_XRES(String K,String RAND){
        return sha3(K+"_"+RAND);
    }

    public String f3_CK(String K,String RAND){
        return sha3(K+"_"+RAND);
    }

    public String f4_IK(String K,String RAND){
        return sha3(K+"_"+RAND);
    }

    public String f5_SQNAK(String K,String SQN,String RAND){
        return sha3(sha3(K+"_"+RAND)+"_"+SQN);
    }
}
