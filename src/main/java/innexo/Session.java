package innexo;

public class Session {
  public int id;
  int inEncounterId;
  int outEncounterId;
  int courseId;

  // Initialized by jackson during serialization
  public Encounter inEncounter;
  public Encounter outEncounter;
  public Course course;
  public Location location;
}
