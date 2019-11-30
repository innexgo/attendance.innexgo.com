package innexgo;

public class Offering {
  public long id;
  long courseId;
  long semesterId;

  // for jackson
  public Course course;
  public Semester semester;
}
