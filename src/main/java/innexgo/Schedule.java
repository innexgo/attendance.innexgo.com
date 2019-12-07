package innexgo;

public class Schedule {
  public long id;
  long studentId;
  long courseId;

  // This is for dropping and inserting into classes
  // Should be set to the natural boundaries of the semester if not used
  public long startTime;
  public long endTime;

  // for jackson
  public Student student;
  public Course course;
}
