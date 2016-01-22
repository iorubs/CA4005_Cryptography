import java.security.SecureRandom;
import java.security.MessageDigest;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.io.FileOutputStream;

import java.math.BigInteger;

class Assi1
{
    static private String geoffsKey = "c406136c640a665900a9df4df63a84fc855927b729a3a106fb3f379e8e4190ebba442f67b93402e535b18a5777e6490e67dbee954bb02175e43b6481e7563d3f9ff338f07950d1553ee6c343d3f8148f71b4d2df8da7efb39f846ac07c865201fbb35ea4d71dc5f858d9d41aaa856d50dc2d2732582f80e7d38c32aba87ba9";
    
    static private String byteToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (byte b : bytes)
        {
        	if(count == 4)
        	{
            	sb.append(String.format(" %02X", b));
            	count = 0;
            }
            else
                sb.append(String.format("%02X", b));

            count++;
        }
        
        return sb.toString();
    }

    static private byte[] utf8Encoder(String password)
    {
        try
        {
            return password.getBytes("UTF-8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    static private byte[] random128bit()
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16]; // 16*8=128 bits
        random.nextBytes(bytes);

        return bytes;
    }

    static private byte[] appendPasswordAndSalt(byte[] password, byte[] salt)
    {
        byte[] tmp = new byte[password.length + salt.length];

        for(int i=0; i<tmp.length/2; i++)
        {
            tmp[i] = password[i];
            tmp[i+password.length] = salt[i];
        }

        return tmp;
    }

    static private byte[] sha256(byte[] passAndSalt)
    {
        byte[] digest = new byte[passAndSalt.length];
        digest = md.digest(passAndSalt);

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            for(int i=1; i<200; i++)
                digest = md.digest(digest);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return digest;
    }

    private static byte[] readFileToByteArray(String file_name)
    {
        try
        {
            Path file_path = Paths.get(file_name);
            return Files.readAllBytes(file_path);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] addPadding(byte[] file, int blockSizeInBits)
    {
        int blockSizeInBytes = blockSizeInBits/8;
        int leftOver = file.length%blockSizeInBytes;

        byte[] padded_array = new byte[file.length + (blockSizeInBytes - leftOver)];

        padded_array[file.length] = (byte)0x80;


        for(int i=0; i<file.length; i++)
        {
            padded_array[i]=file[i];
        }

        return padded_array;
    }

    private static byte[] encrypt_AES_CBC(byte[] hashed_pass, byte[] iv, byte[] file) {
        try
        {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            SecretKeySpec keySpec = new SecretKeySpec(hashed_pass, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted_file = cipher.doFinal(file);
            
            return encrypted_file;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static BigInteger modularExp(BigInteger p, String exponent, BigInteger modulus)
    {
        BigInteger encrypted = BigInteger.ONE;

        for(int i=0; i<exponent.length(); i++)
        {
            if(exponent.charAt(i) == '1')
                encrypted = encrypted.multiply(p).mod(modulus);
            p = p.multiply(p).mod(modulus);
        }

        return encrypted;
    }

    public static void main(String [] args)
    {
        String password = "5tr0ngP@ss1$345&";
        byte[] encoded_pass = utf8Encoder(password);
        System.out.println("Pass = " + password);

        byte[] salt = random128bit();
        System.out.println();
        System.out.println("Salt = " + byteToHex(salt));

        byte[] hashed_pass = sha256(appendPasswordAndSalt(encoded_pass, salt));

        byte[] iv = random128bit();
        System.out.println();
        System.out.println("IV   = " + byteToHex(iv));

        byte[] file = readFileToByteArray("Assi1.zip");

        byte[] padded_file = addPadding(file, 128);

        byte[] encrypted_file = encrypt_AES_CBC(hashed_pass, iv, padded_file);

        System.out.println();
        System.out.println("AES_encrypted_file:");
        System.out.println(byteToHex(encrypted_file));

        BigInteger p = new BigInteger(encoded_pass);
        BigInteger e = new BigInteger("65537", 10);
        BigInteger n = new BigInteger(geoffsKey, 16);

        byte[] rsa_encrypted = modularExp(p, Integer.toBinaryString(65537), n).toByteArray();
        System.out.println();
        System.out.println("RSA_encrypted_pass:");
        System.out.println(byteToHex(rsa_encrypted));
        System.out.println();
    }
}