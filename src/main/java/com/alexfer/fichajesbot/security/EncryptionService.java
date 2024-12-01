package com.alexfer.fichajesbot.security;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

  private final SecretKey secretKey;
  private final String transformation;

  public EncryptionService(
      @Value("${encryption.key}") String encodedKey,
      @Value("${encryption.transformation}") String transformation
  ) {
    byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
    this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    this.transformation = transformation;
  }

  public String encrypt(String plainText) {
    if (plainText == null) return null;
    return Try.of(() -> {
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
      return Base64.getEncoder().encodeToString(encryptedBytes);
    }).getOrElse((String) null);
  }

  public String decrypt(String encryptedText) {
    if (encryptedText == null) return null;
    return Try.of(() -> {
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
      return new String(decryptedBytes);
    }).getOrElse((String) null);
  }

}
