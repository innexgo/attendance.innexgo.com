package innexgo;

public class Encounter {
  public int id;
  public long time;
  int locationId;
  int studentId;

  // Initialized by jackson during serialization
  public Student student;
  public Course course;
  public Location location;
}
