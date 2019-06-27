package innexo;

public class Utils {
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
