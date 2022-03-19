package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.lang.StringBuilder;
import java.security.NoSuchAlgorithmException;

public class Hashing{

    // empty constructor
    public Hashing(){

    }

    public String SHA1FromBytes(byte[] data) {
        // create digest with SHA1. I think 256 would be be better but need to check specifications 
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error in hashing algorithm, SHA1 wasn't found.");
            e.printStackTrace();
        }
        byte[] hash  = digest.digest(data); 
        // get the hash number
        BigInteger hashInt = new BigInteger(1, hash);
        // build a string with the hash
        StringBuilder hashMessage = new StringBuilder(hashInt.toString(16)); 
        // pad with leading zeros
            while (hashMessage.length() < 32) 
            { 
                hashMessage.insert(0, '0'); 
            } 
        // return the tostring with padding 
        return hashMessage.toString(); 
} 
 
}