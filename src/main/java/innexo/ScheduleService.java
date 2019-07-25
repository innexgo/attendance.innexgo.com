package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ScheduleService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Schedule getById(int id) {
    String sql = "SELECT id, student_id, course_id FROM schedule WHERE id=?";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    Schedule schedule = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return schedule;
  }

  public List<Schedule> getAll() {
    String sql = "SELECT id, student_id, course_id FROM schedule";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Schedule schedule) {
    // Add schedule
    String sql = "INSERT INTO schedule (id, student_id, course_id) values (?, ?, ?)";
    jdbcTemplate.update(sql, schedule.id, schedule.studentId, schedule.courseId);

    // Fetch schedule id
    sql = "SELECT id FROM schedule WHERE student_id=? AND course_id=?";
    int id =
        jdbcTemplate.queryForObject(
            sql, Integer.class, schedule.studentId, schedule.courseId);
    schedule.id = id;
  }

  public void update(Schedule schedule) {
    String sql = "UPDATE schedule SET id=?, student_id=?, course_id=? WHERE id=?";
    jdbcTemplate.update(
        sql, schedule.id, schedule.studentId, schedule.courseId, schedule.id);
  }

  public Schedule delete(int id) {
    Schedule schedule = getById(id);
    String sql = "DELETE FROM schedule WHERE id=?";
    jdbcTemplate.update(sql, id);
    return schedule;
  }

  public List<Schedule> query(
      Integer scheduleId,
      Integer studentId,
      Integer courseId,
      Integer teacherId,
      Integer locationId,
      Integer period) {
    String sql =
      "SELECT s.id, s.student_id, s.course_id FROM schedule s" +
      " JOIN course c ON s.course_id = c.id" +
      " WHERE 1=1 " +
      (scheduleId == null ? "" : " AND s.id = " + scheduleId) +
      (studentId  == null ? "" : " AND s.student_id = " + studentId) +
      (courseId   == null ? "" : " AND s.course_id = " + courseId) +
      (teacherId  == null ? "" : " AND c.teacher_id = " + teacherId) +
      (locationId == null ? "" : " AND c.location_id = " + locationId) +
      (period     == null ? "" : " AND c.period = " + period) +
      ";";

    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM schedule WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }
}
