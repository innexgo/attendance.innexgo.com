package innexo;

public class Utils {
  public static boolean isBlank(String str) {
    return str == null || str.trim().equals("");
  }

  public static String escapeSQLString(String str) {
    // TODO
    return str == null ? null : str.replaceAll("[^a-zA-Z0-9]", "");
  }

  public static String unEscapeSQLString(String str) {
    // TODO
    return str == null ? null : str.replaceAll("[^a-zA-Z0-9]", "");
  }
}
