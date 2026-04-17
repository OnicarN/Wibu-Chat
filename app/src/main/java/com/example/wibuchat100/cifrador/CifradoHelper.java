package com.example.wibuchat100.cifrador;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.MessageDigest;
import java.util.Arrays;

public class CifradoHelper {

    // Derivamos una clave AES-256 a partir del chatId
    private static SecretKeySpec generarClave(String chatId) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(chatId.getBytes("UTF-8"));
        return new SecretKeySpec(Arrays.copyOf(hash, 32), "AES");
    }

    public static String cifrar(String texto, String chatId) {
        try {
            SecretKeySpec clave = generarClave(chatId);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // IV fijo derivado del chatId (simplificación válida para este proyecto)
            byte[] iv = Arrays.copyOf(
                    MessageDigest.getInstance("MD5").digest(chatId.getBytes("UTF-8")), 16);
            cipher.init(Cipher.ENCRYPT_MODE, clave, new IvParameterSpec(iv));
            byte[] cifrado = cipher.doFinal(texto.getBytes("UTF-8"));
            return Base64.encodeToString(cifrado, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return texto; // si falla, devuelve el texto original
        }
    }

    public static String descifrar(String textoCifrado, String chatId) {
        try {
            SecretKeySpec clave = generarClave(chatId);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = Arrays.copyOf(
                    MessageDigest.getInstance("MD5").digest(chatId.getBytes("UTF-8")), 16);
            cipher.init(Cipher.DECRYPT_MODE, clave, new IvParameterSpec(iv));
            byte[] descifrado = cipher.doFinal(Base64.decode(textoCifrado, Base64.NO_WRAP));
            return new String(descifrado, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return textoCifrado; // si falla, devuelve el texto tal cual
        }
    }
}