package innexgo;

public class Encounter {

  public static final String VIRTUAL_ENCOUNTER = "virtual";
  public static final String MANUAL_ENCOUNTER = "manual";
  public static final String DEFAULT_ENCOUNTER = "default";

  public long id; // Id of encounter
  public long time; // time in milliseconds since 1970 that this encounter occured
  long locationId; // where
  long studentId; // who
  public String type; // What kind of sign in (manual, virtual, card, etc.)

  // Initialized by jackson during serialization
  public Student student;
  public Location location;
}
