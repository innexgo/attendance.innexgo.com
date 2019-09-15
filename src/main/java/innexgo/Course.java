package innexgo;

public class Course {
  public long id;
  long teacherId;
  long locationId;
  public int period;
  public String subject;
  public int year;

  // for jackson
  public User teacher;
  public Location location;
}
