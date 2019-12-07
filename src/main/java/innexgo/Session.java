package innexgo;

public class Session {
  public long id;
  // Id of encounter (Use this to determine student, location, etc)
  long inEncounterId;
  public boolean complete;
  // This is UNDEFINED unless complete is true
  long outEncounterId;

  // Initialized by jackson during serialization
  public Encounter inEncounter;
  public Encounter outEncounter;
}
