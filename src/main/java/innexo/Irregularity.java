package innexo;

public class Irregularity {
  public int id;
  int studentId;
  int courseId;
  int periodId;
  public int type;
  public int timeMissing;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}
