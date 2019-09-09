package innexgo;

public class Session {
  public int id;
  int studentId;
  int courseId;
  int inEncounterId;
  int outEncounterId;
  public boolean hasOut;
  public boolean complete;

  // Initialized by jackson during serialization
  public Student student;
  public Encounter inEncounter;
  public Encounter outEncounter;
  public Course course;
}
