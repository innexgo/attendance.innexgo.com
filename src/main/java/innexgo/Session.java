package innexgo;

public class Session {
  public long id;
  long studentId;
  long inEncounterId;
  long outEncounterId;
  public boolean complete;

  // Initialized by jackson during serialization
  public Student student;
  public Encounter inEncounter;
  public Encounter outEncounter;
}
