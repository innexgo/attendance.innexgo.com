package innexgo;

public class Period {
  public static final String PASSING_PERIOD = "Passing";
  public static final String CLASS_PERIOD = "Class";
  public static final String BREAK_PERIOD = "Break";
  public static final String LUNCH_PERIOD = "Lunch";
  public static final String TUTORIAL_PERIOD = "Tutorial";

  // Primary Index is startTime
  public long startTime;
  public long number;
  public String type;
}
