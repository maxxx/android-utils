package ru.maxdestroyer.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt
{

	public static String hexEncodeFormatted(byte[] input)
    {
        if (input == null || input.length == 0)
        {
            return "";
        }

        int inputLength = input.length;
        StringBuilder output = new StringBuilder(inputLength * 2);

        for (int i = 0; i < inputLength; i++)
        {
            int next = input[i] & 0xff;
            /*if (next < 0x10)
            {
                output.append("0");
            }*/

            output.append("0x" + Integer.toHexString(next) + ", ");
        }

        return output.toString();
    }
	
	public static String hexEncode(byte[] input)
    {
        if (input == null || input.length == 0)
        {
            return "";
        }

        int inputLength = input.length;
        StringBuilder output = new StringBuilder(inputLength * 2);

        for (int i = 0; i < inputLength; i++)
        {
            int next = input[i] & 0xff;
            /*if (next < 0x10)
            {
                output.append("0");
            }*/

            output.append(Integer.toHexString(next));
        }

        return output.toString();
    }
	
	// 10, 0 -> 010
	public static String hexEncodeReverse(byte[] input)
    {
        if (input == null || input.length == 0)
        {
            return "";
        }

        int inputLength = input.length;
        StringBuilder output = new StringBuilder(inputLength * 2);

        for (int i = inputLength-1; i >= 0; i--)
        {
            int next = input[i];
//            if (next < 0x10)
//            {
//                output.append("0");
//            }

            output.append(Integer.toString(next));
        }

        return output.toString();
    }
	
	public static byte[] encryptAES(byte[] in, byte[] key)
	{
		String CIPHER_ALGORITHM = "AES/CBC/NoPadding"; // output размером как инпут
		String SECRET_KEY_ALGORITHM = "AES";
		byte[] encryptedData = new byte[in.length];
		byte[] iv = new byte[16];
		for (byte i = 0; i < 16; i++)
			iv[i] = i;
		
		try
		{
			SecretKeySpec secretKey = new SecretKeySpec(key, SECRET_KEY_ALGORITHM);

			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			
			encryptedData = cipher.doFinal(in);
		} catch (Exception e)
		{
			Util.LOG(e.toString());
			e.printStackTrace();
		}
		
		return encryptedData;
	}
	
	public static byte[] decryptAES(byte[] in, byte[] key)
	{
		String CIPHER_ALGORITHM = "AES/CBC/NoPadding"; // output размером как инпут
		String SECRET_KEY_ALGORITHM = "AES";
		byte[] decryptedData = new byte[in.length];
		byte[] iv = new byte[16];
		for (byte i = 0; i < 16; i++)
			iv[i] = i;
		
		try
		{
			SecretKeySpec secretKey = new SecretKeySpec(key, SECRET_KEY_ALGORITHM);

			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			
			decryptedData = cipher.doFinal(in);
		} catch (Exception e)
		{
			Util.LOG(e.toString());
			e.printStackTrace();
		}
		
		return decryptedData;
	}
	
}
