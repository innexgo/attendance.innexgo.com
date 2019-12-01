package innexgo;

public class Course {
  public long id;
  long teacherId;
  long locationId;
  public long period;
  public String subject;

  // for jackson
  public User teacher;
  public Location location;
}
