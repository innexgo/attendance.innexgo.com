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

  public boolean existsById(int id) {
    String sql = "SELECT count(*) FROM irregularity WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Irregularity> query(
      Integer id,
      Integer studentId,
      Integer courseId,
      Integer periodId,
      Integer teacherId,
      String type,
      Long time,
      Long timeMissing) {
    String sql = "SELECT irr.id, irr.student_id, irr.course_id, irr.period_id, irr.type, irr.time, irr.time_missing"
       + " FROM irregularity irr"
       + (teacherId == null ? "" : " JOIN course crs ON crs.id = irr.course_id")
        + " WHERE 1=1 "
        + (id == null ? "" : " AND irr.id = " + id)
        + (studentId == null ? "" : " AND irr.student_id = " + studentId)
        + (courseId == null ? "" : " AND irr.course_id = " + courseId)
        + (periodId == null ? "" : " AND irr.period_id = " + periodId)
        + (teacherId == null ? "" : " AND crs.teacher_id = " + teacherId)
        + (type == null ? "" : " AND irr.type = " + Utils.escape(type))
        + (time == null ? "" : " AND irr.time = " + time)
        + (timeMissing == null ? "" : " AND irr.time_missing = " + timeMissing)
        + ";";

    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
