package innexo;

public class Utils {
	public static String valString(String str) {
		return str == null ? null : str.replaceAll("[^a-zA-Z0-9]", "");
	}
}
