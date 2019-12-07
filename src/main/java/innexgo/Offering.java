package innexgo;

public class Offering {
  public long id;
  long courseId;
  long semesterStartTime;

  // for jackson
  public Course course;
  public Semester semester;
}
