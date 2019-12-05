package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class StudentService {

  @Autowired private PeriodService periodService;
  @Autowired private CourseService courseService;
  @Autowired private JdbcTemplate jdbcTemplate;

  public Student getById(long id) {
    String sql = "SELECT id, tags, initial_semester_id, initial_semester_id FROM student WHERE id=?";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    Student student = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return student;
  }

  public List<Student> getAll() {
    String sql = "SELECT id, name, tags, initial_semester_id FROM student";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Student student) {
    // Add student
    String sql = "INSERT INTO student(id, name, tags, initial_semester_id) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, student.id, student.name, student.tags, student.initialSemesterId);
  }

  public void update(Student student) {
    String sql = "UPDATE student SET id=?, name=?, tags=?, initial_semester_id=? WHERE id=?";
    jdbcTemplate.update(
        sql, student.id, student.name, student.tags, student.initialSemesterId, student.id);
  }

  public Student deleteById(long id) {
    Student student = getById(id);
    String sql = "DELETE FROM student WHERE id=?";
    jdbcTemplate.update(sql, id);
    return student;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM student WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  // Restrict students by
  public List<Student> query(
      Long id, // Exact match to id
      String name, // Exact match to name
      String partialName, // Partial match to name
      String tags, // Exact match to tags
      String partialTags, // Partial match to tags
      Long initialSemesterId // Exact match to initial semester id
      ) {
    String sql =
        "SELECT st.id, st.name, st.tags, st.initial_semester_id FROM student st"
            + " WHERE 1=1 "
            + (id == null ? "" : " AND st.id = " + id)
            + (name == null ? "" : " AND st.name = " + Utils.escape(name))
            + (partialName == null ? "" : " AND st.name LIKE " + Utils.escape("%"+partialName+"%"))
            + (tags == null ? "" : " AND st.tags = " + Utils.escape(tags))
            + (partialTags == null ? "" : " AND st.tags LIKE " + Utils.escape("%"+partialTags+"%"))
            + (initial_semester_id == null ? "" : " AND st.initial_semester_id = " + initial_semester_id)
            + ";";

    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  // find students who are present at the location that they are supposed to be in at this time
  public List<Student> present(long courseId, long time) {
    Course course = courseService.getById(courseId);
    Period period = periodService.getByTime(time);

    // Do some sanity checks to ensure that the time's period is the same period as the course
    if(period.number != course.period) {
      // empty list return
      return new ArrayList<Student>();
    }

    // Find the sessions that have a start date before the period start date.
    // If they have an end date it must be after the period start date
    // find students who are in this list

    String sql =
              " SELECT DISTINCT st.id, st.name, st.tags, st.initial_semester_id"
            + " FROM student st"
            + " INNER JOIN session ses ON ses.student_id = st.id"
            + " INNER JOIN encounter inen ON ses.in_encounter_id = inen.id"
            + " LEFT JOIN encounter outen ON ses.complete AND ses.out_encounter_id = outen.id"
            + " WHERE 1 = 1 "
            + (" AND inen.location_id = " + course.locationId)
            + (" AND inen.time < " + period.startTime)
            + (" AND outen.time IS NULL OR outen.time > " + period.startTime)
            + " ;";

    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  // Find students who are supposed to be here
  public List<Student> registeredForCourse(long courseId, long time) {
    Course course = courseService.getById(courseId);
    Period period = periodService.getByTime(time);

    // Do some sanity checks to ensure that the time's period is the same period as the course
    if(period.number != course.period) {
      // empty list return
      return new ArrayList<Student>();
    }

    // Find schedules which have the specified courseId
    // From these select schedules that are still active
    // Return the students of these schedules
    String sql =
        " SELECT DISTINCT st.id, st.name, st.tags, st.initial_semester_id"
      + " FROM student st"
      + " INNER JOIN schedule sc ON st.id = sc.student_id"
      + " INNER JOIN courses cs ON cs.id = sc.course_id "
      + " WHERE 1 == 1"
      + " AND cs.id = " + course.id
      + " AND " + time + " >= sc.first_period_start_time AND " + time + " < sc.last_period_start_time "
      + " ;";
    RowMapper<Student> rowMapper = new StudentRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
