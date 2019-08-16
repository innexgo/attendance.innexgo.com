package innexgo;

public class Course {
  public int id;
  int teacherId;
  int locationId;
  public int period;
  public String subject;
  public int year;

  // for jackson
  public User teacher;
  public Location location;
}
