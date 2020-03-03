/*
 * Innexgo Website
 * Copyright (C) 2020 Innexgo LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package innexgo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Utils {

  public static final ZoneId TIMEZONE = ZoneId.of("America/Los_Angeles");

  // note that we use bcrypt for passwords
  static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
  static final Base64.Decoder base64Decoder = Base64.getUrlDecoder();

  /**
   * Parses string for Integer
   *
   * @param str - string to be parsed
   * @return Integer parsed from str; null if the string is null or cannot be parsed
   */
  public static Integer parseInteger(String str) {
    if (str == null) {
      return null;
    } else {
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
  /**
   * Parses string for Long
   *
   * @param str - string to be parsed
   * @return Long parsed from str; null if string is null or cannot be parsed
   */
  public static Long parseLong(String str) {
    if (str == null) {
      return null;
    } else {
      try {
        return Long.parseLong(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }

  /**
   * Parses string for Boolean
   *
   * @param str - string to be parsed
   * @return Boolean parsed from str; null if string is null or cannot be parsed
   */
  public static Boolean parseBoolean(String str) {
    if (str == null) {
      return null;
    } else {
      try {
        return Boolean.parseBoolean(str);
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }

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

  // create 128 bit key
  public static String generateKey() {
    return Long.toHexString(new Random().nextLong()) + Long.toHexString(new Random().nextLong());
  }

  public static boolean isEmpty(String str) {
    return str == null || str == "";
  }

  public static String escape(String str) {
    return "\'" + escapeSQLString(str) + "\'";
  }

  public static String escapeSQLString(String str) {
    return str.replaceAll("\'", "\'\'");
  }

  public static String unEscapeSQLString(String str) {
    return str.replaceAll("\'\'", "\'");
  }

  public static int getCurrentGraduatingYear() {
    LocalDateTime time = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
    int currentYear = time.getYear();
    // if its the fall/winter
    if (time.getMonth().getValue() >= 7) {
      currentYear++;
    }
    return currentYear;
  }
}
