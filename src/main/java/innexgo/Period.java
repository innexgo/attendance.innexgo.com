package innexgo;

public class Period {
  public static final String PASSING_PERIOD = "Passing Period";
  public static final String CLASS_PERIOD = "Class Period";
  public static final String BREAK_PERIOD = "Break";
  public static final String LUNCH_PERIOD = "Lunch";
  public static final String TUTORIAL_PERIOD = "Tutorial Period";
  public static final String NO_PERIOD = "No School In Session";

  // Primary Index is startTime
  public long startTime;
  public long number;
  public String type; // Must be one of the above defined strings
  // If it's a test. (For testing purposes)
  boolean temp;
}
