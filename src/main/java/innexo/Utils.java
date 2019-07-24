package innexo;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Random;
import java.security.NoSuchAlgorithmException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Utils {

  // note that we use bcrypt for passwords
  static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
  static final Base64.Decoder base64Decoder = Base64.getUrlDecoder();

  static MessageDigest getDigester() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  public static boolean matchesPassword(String password, String hash) {
    return passwordEncoder.matches(password, hash);
  }

  public static String encodeApiKey(String key) {
    return base64Encoder.encodeToString(getDigester().digest(key.getBytes()));
  }

  public static boolean matchesApiKey(String key, String hash) {
    return hash.equals(encodeApiKey(key));
  }

  public static String generateKey() {
    return Long.toHexString(new Random().nextLong());
  }

  public static boolean isEmpty(String str) {
    return str == null || str == "";
  }

  public static String escapeSQLString(String str) {
    return str.replaceAll("\'", "\'\'");
  }

  public static String unEscapeSQLString(String str) {
    return str.replaceAll("\'\'", "\'");
  }
}
