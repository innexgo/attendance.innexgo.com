package innexgo;

public class Encounter {
  public long id;
  public long time;
  long locationId;
  long studentId;

  // Initialized by jackson during serialization
  public Student student;
  public Location location;
}
