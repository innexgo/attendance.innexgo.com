package innexgo;

public class Irregularity {
  public long id;
  long studentId;
  long courseId;
  long periodId;
  public String type;
  public long time;
  public long timeMissing;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}
