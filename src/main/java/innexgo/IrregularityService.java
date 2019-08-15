package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class IrregularityService {
  @Autowired private JdbcTemplate jdbcTemplate;

  static final int ADMINISTRATOR = 0;
  static final int TEACHER = 1;

  public Irregularity getById(int id) {
    String sql =
        "SELECT id, student_id, course_id, period_id, type, time, time_missing FROM irregularity WHERE id=?";
    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    Irregularity irregularity = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return irregularity;
  }

  public List<Irregularity> getAll() {
    String sql =
        "SELECT id, student_id, course_id, period_id, type, time, time_missing FROM irregularity";
    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Irregularity irregularity) {
    // Add irregularity
    String sql =
        "INSERT INTO irregularity (id, student_id, course_id, period_id, type, time, time_missing) values (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql,
        irregularity.id,
        irregularity.studentId,
        irregularity.courseId,
        irregularity.periodId,
        irregularity.type,
        irregularity.time,
        irregularity.timeMissing);

    // Fetch irregularity id
    sql =
        "SELECT id FROM irregularity WHERE student_id=? AND course_id=? AND period_id=? AND type=? AND time=? AND time_missing=?";
    int id =
        jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            irregularity.studentId,
            irregularity.courseId,
            irregularity.periodId,
            irregularity.type,
            irregularity.time,
            irregularity.timeMissing);

    // Set irregularity id
    irregularity.id = id;
  }

  public void update(Irregularity irregularity) {
    String sql =
        "UPDATE irregularity SET id=?, student_id=?, course_id=?, period_id=?, type=?, time=?, time_missing=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        irregularity.studentId,
        irregularity.courseId,
        irregularity.periodId,
        irregularity.type,
        irregularity.time,
        irregularity.timeMissing);
  }

  public Irregularity delete(int id) {
    Irregularity irregularity = getById(id);
    String sql = "DELETE FROM irregularity WHERE id=?";
    jdbcTemplate.update(sql, id);
    return irregularity;
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM irregularity WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Irregularity> query(
      Integer id,
      Integer studentId,
      Integer courseId,
      Integer periodId,
      String type,
      Long time,
      Long timeMissing,
      Integer teacherId) {
    String sql =
        "SELECT u.id, u.student_id, u.course_id, u.period_id, u.type, u.time, u.time_missing FROM irregularity u"
            + " WHERE 1=1 "
            + (id == null ? "" : " AND u.id = " + id)
            + (name == null ? "" : " AND u.name = " + Utils.escape(name))
            + (email == null ? "" : " AND u.email = " + Utils.escape(email))
            + (ring == null ? "" : " AND u.ring = " + ring)
            + ";";

    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
