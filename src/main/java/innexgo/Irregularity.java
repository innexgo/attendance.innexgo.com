package innexgo;

public class Irregularity {
  public int id;
  int studentId;
  int courseId;
  int periodId;
  public String type;
  public int time;
  public int timeMissing;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}
