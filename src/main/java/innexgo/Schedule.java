package innexgo;

public class Schedule {
  public long id;
  long studentId;
  long courseId;

  // This is for dropping and inserting into classes
  // If there is a nonzero start time
  public boolean hasStart;
  // Only defined if hasStart true
  public long startTime;
  // If there is a noninfinity end time
  public boolean hasEnd;
  // Only defined if hasEnd true
  public long endTime;

  // for jackson
  public Student student;
  public Course course;
}
