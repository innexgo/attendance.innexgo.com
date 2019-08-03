package innexo;

public class Irregularity {
  public int id;
  int studentId;
  int courseId;
  int periodId;
  public boolean tardy;
  public Integer secondsLate;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}
