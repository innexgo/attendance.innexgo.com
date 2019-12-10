package innexgo;

public class Encounter {
  public long id; // Id of encounter
  public long time; // time in milliseconds since 1970 that this encounter occured
  public boolean virtual; // Whether the encounter was automatically generated
  long locationId; // where
  long studentId; // who

  // Initialized by jackson during serialization
  public Student student;
  public Location location;
}
