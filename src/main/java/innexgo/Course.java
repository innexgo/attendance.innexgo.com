package innexgo;

public class Course {
  public long id;
  long teacherId;
  long locationId;
  long semesterId;
  public int period;
  public String subject;

  // for jackson
  public User teacher;
  public Semester semester;
  public Location location;
}
