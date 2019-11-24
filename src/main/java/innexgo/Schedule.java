package innexgo;

public class Schedule {
  public long id;
  long studentId;
  long courseId;

  // This is for dropping and inserting into classes
  public long startTime;
  public long endTime;

  // for jackson
  public Student student;
  public Course course;
}
