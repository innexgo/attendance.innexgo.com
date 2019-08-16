package innexgo;

public class Session {
  public int id;
  int inEncounterId;
  int outEncounterId;
  int courseId;
  public boolean complete;

  // Initialized by jackson during serialization
  public Encounter inEncounter;
  public Encounter outEncounter;
  public Course course;
}
