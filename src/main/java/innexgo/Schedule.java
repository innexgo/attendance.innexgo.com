package innexgo;

public class Schedule {
  public long id;
  long studentId;
  long courseId;

  // This is for dropping and inserting into classes
  // First period where student will attend course
  public long firstPeriodStartTime;
  // first period start time where student will not attend course
  public long lastPeriodStartTime;

  // for jackson
  public Student student;
  public Course course;
}
