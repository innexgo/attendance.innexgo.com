package innexgo;

public class Session {
  public long id;
  long studentId;
  long courseId;
  long inEncounterId;
  long outEncounterId;
  public boolean hasOut;
  public boolean complete;

  // Initialized by jackson during serialization
  public Student student;
  public Encounter inEncounter;
  public Encounter outEncounter;
  public Course course;
}
