package innexgo;

public class Schedule {
  public long id;
  long studentId;
  long courseId;

  // This is for dropping and inserting into classes
  // Normally should be set to the first and last period of the course
  public long firstPeriodId;
  public long lastPeriodId;

  // for jackson
  public Student student;
  public Course course;
}
