package innexo;

import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Utils {

  // note that we use bcrypt for passwords
  static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  static MessageDigest md = getDigester();
  static Base64.Encoder base64Encoder = Base64.getUrlEncoder();
  static Base64.Decoder base64Decoder = Base64.getUrlDecoder();

  static MessageDigest getDigester() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  public static String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  public static boolean matchesPassword(String password, String hash) {
    return passwordEncoder.matches(password, hash);
  }

  public static String encodeApiKey(String key) {
    return base64Encoder.encodeToString(md.digest(key.getBytes()));
  }

  public static boolean matchesApiKey(String key, String hash) {
    return hash.equals(encodeApiKey(key));
  }

  public static boolean isBlank(String str) {
    return str == null || str.trim().equals("");
  }

  public static String escapeSQLString(String str) {
    return str.replaceAll("\'", "\'\'");
  }

  public static String unEscapeSQLString(String str) {
    return str.replaceAll("\'\'", "\'");
  }
}
