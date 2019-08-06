package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PeriodService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Period getById(int id) {
    String sql = "SELECT id, start_time, end_time, period FROM period WHERE id=?";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return period;
  }

  public List<Period> getAll() {
    String sql = "SELECT id, start_time, end_time, period FROM period";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Period period) {
    // Add period
    String sql = "INSERT INTO period (id, start_time, end_time, period) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, period.id, period.startTime, period.endTime, period.period);

    // Fetch period id
    sql = "SELECT id FROM period WHERE start_time=? AND end_time=? AND period=?";
    int id =
        jdbcTemplate.queryForObject(
            sql, Integer.class, period.startTime, period.endTime, period.period);

    // Set period id
    period.id = id;
  }

  public void update(Period period) {
    String sql = "UPDATE period SET id=?, start_time=?, end_time=?, period=? WHERE id=?";
    jdbcTemplate.update(sql, period.id, period.startTime, period.endTime, period.period, period.id);
  }

  public Period delete(int id) {
    Period period = getById(id);
    String sql = "DELETE FROM period WHERE id=?";
    jdbcTemplate.update(sql, id);
    return period;
  }

  public boolean existsById(int id) {
    String sql = "SELECT count(*) FROM period WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Period> query(
      Integer id,
      Integer minTime, // selects periods that end after this date
      Integer maxTime, // select periods that start before this date
      Integer period,
      Integer courseId, // all periods that have this course
      Integer teacherId // all periods where this teacher teaches a coruse
      ) {

    String sql =
        "SELECT p.id, p.start_time, p.end_time, p.period FROM period p"
            + (courseId == null || teacherId == null
                ? ""
                : " JOIN course c ON c.period = p.period ")
            + " WHERE 1=1 "
            + (id == null ? "" : " AND p.id = " + id)
            + (minTime == null ? "" : " AND p.end_time >= " + minTime)
            + (maxTime == null ? "" : " AND p.start_time < " + maxTime)
            + (period == null ? "" : " AND p.period = " + period)
            + (courseId == null ? "" : " AND c.course_id = " + courseId)
            + (teacherId == null ? "" : " AND c.teacher_id = " + teacherId)
            + ";";

    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
