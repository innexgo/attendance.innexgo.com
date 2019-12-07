package innexgo;

public class Irregularity {

  static final String TYPE_ABSENT = "Absent";
  static final String TYPE_TARDY = "Tardy";
  static final String TYPE_LEFT_EARLY = "Left Early";
  static final String TYPE_LEFT_TEMPORARILY = "Left Temporarily";
  static final String TYPE_FORGOT_SIGN_OUT = "Forgot to Sign Out";

  public long id;
  long studentId;
  long courseId;
  long periodStartTime;
  public String type;
  public long time;
  public long timeMissing;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}
