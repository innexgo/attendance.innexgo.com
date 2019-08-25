package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class CourseService {
  @Autowired private JdbcTemplate jdbcTemplate;

  public Course getById(int id) {
    String sql = "SELECT id, teacher_id, location_id, period, subject, year FROM course WHERE id=?";
    RowMapper<Course> rowMapper = new CourseRowMapper();
    Course course = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return course;
  }

  public List<Course> getAll() {
    String sql = "SELECT id, teacher_id, location_id, period, subject, year FROM course";
    RowMapper<Course> rowMapper = new CourseRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Course course) {
    // Add course
    String sql =
        "INSERT INTO course (id, teacher_id, location_id, period, subject, year) values (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, course.id, course.teacherId, course.locationId, course.period, course.subject);

    // Fetch course id
    sql =
        "SELECT id FROM course WHERE teacher_id=? AND location_id=? AND period=? AND subject=? AND year=?";
    int id =
        jdbcTemplate.queryForObject(
            sql, Integer.class, course.teacherId, course.locationId, course.period, course.subject);

    // Set course id
    course.id = id;
  }

  public void update(Course course) {
    String sql =
        "UPDATE course SET id=?, teacher_id=?, location_id=? period=?, subject=?, year=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        course.id,
        course.teacherId,
        course.locationId,
        course.period,
        course.subject,
        course.year,
        course.id);
  }

  public Course delete(int id) {
    Course course = getById(id);
    String sql = "DELETE FROM course WHERE id=?";
    jdbcTemplate.update(sql, id);
    return course;
  }

  public boolean existsById(int id) {
    String sql = "SELECT count(*) FROM course WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Course> query(
      Integer id,
      Integer teacherId,
      Integer locationId,
      Integer studentId,
      Long time,
      String subject,
      Integer year) {
    String sql =
        "SELECT c.id, c.teacher_id, c.location_id, c.period, c.subject, c.year FROM course c"
            + (studentId == null ? "" : " JOIN schedule sc ON c.id = sc.course_id ")
            + " WHERE 1=1 "
            + (id == null ? "" : " AND c.id = " + id)
            + (teacherId == null ? "" : " AND c.teacher_id = " + teacherId)
            + (locationId == null ? "" : " AND c.location_id = " + locationId)
            + (year == null ? "" : " AND c.year = " + year)
            + (studentId == null ? "" : " AND sc.student_id = " + studentId)
            + (time == null ? "" : " AND 
            + (subject == null ? "" : " AND c.subject = " + Utils.escape(subject))
            + ";";

    RowMapper<Course> rowMapper = new CourseRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
