package innexgo;

public class Session {
  public int id;
  int inEncounterId;
  Integer outEncounterId; // nullable
  int courseId;
  public boolean complete;

  // Initialized by jackson during serialization
  public Encounter inEncounter;
  public Encounter outEncounter;
  public Course course;
}
