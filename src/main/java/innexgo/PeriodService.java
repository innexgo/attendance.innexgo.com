package innexgo;

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

  public Period getById(long id) {
    String sql = "SELECT id, initial_time, start_time, end_time, period FROM period WHERE id=?";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return period;
  }

  public List<Period> getAll() {
    String sql = "SELECT id, initial_time, start_time, end_time, period FROM period";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Period period) {
    // Add period
    String sql =
        "INSERT INTO period (id, initial_time, start_time, end_time, period) values (?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, period.id, period.initialTime, period.startTime, period.endTime, period.period);

    // Fetch period id
    sql = "SELECT id FROM period WHERE initial_time=? AND start_time=? AND end_time=? AND period=?";
    long id =
        jdbcTemplate.queryForObject(
            sql, Long.class, period.initialTime, period.startTime, period.endTime, period.period);

    // Set period id
    period.id = id;
  }

  public void update(Period period) {
    String sql =
        "UPDATE period SET id=?, initial_time=?, start_time=?, end_time=?, period=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        period.id,
        period.initialTime,
        period.startTime,
        period.endTime,
        period.period,
        period.id);
  }

  public Period deleteById(long id) {
    Period period = getById(id);
    String sql = "DELETE FROM period WHERE id=?";
    jdbcTemplate.update(sql, id);
    return period;
  }

  public void deleteAll() {
    String sql = "TRUNCATE period";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM period WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Period> query(
      Long id,
      Long time,
      Long minDuration,
      Long maxDuration,
      Long initialTimeBegin,
      Long initialTimeEnd,
      Long startTimeBegin,
      Long startTimeEnd,
      Long endTimeBegin,
      Long endTimeEnd,
      Long period,
      Long courseId, // all periods that have this course
      Long teacherId // all periods where this teacher teaches a coruse
      ) {

    String sql =
        "SELECT p.id, p.initial_time, p.start_time, p.end_time, p.period FROM period p"
            + (courseId == null || teacherId == null
                ? ""
                : " JOIN course c ON c.period = p.period ")
            + " WHERE 1=1 "
            + (id == null ? "" : " AND p.id = " + id)
            + (time == null ? "" : " AND " + time + " BETWEEN p.initial_time AND p.end_time")
            + (minDuration == null ? "" : " AND " + minDuration + " <= p.end_time - p.start_time")
            + (maxDuration == null ? "" : " AND " + maxDuration + " >= p.end_time - p.start_time")
            + (startTimeBegin == null ? "" : " AND p.start_time >= " + startTimeBegin)
            + (startTimeEnd == null ? "" : " AND p.start_time <= " + startTimeEnd)
            + (initialTimeBegin == null ? "" : " AND p.initial_time >= " + initialTimeBegin)
            + (initialTimeEnd == null ? "" : " AND p.initial_time <= " + initialTimeEnd)
            + (endTimeBegin == null ? "" : " AND p.end_time >= " + endTimeBegin)
            + (endTimeEnd == null ? "" : " AND p.end_time <= " + endTimeEnd)
            + (period == null ? "" : " AND p.period = " + period)
            + (courseId == null ? "" : " AND c.course_id = " + courseId)
            + (teacherId == null ? "" : " AND c.teacher_id = " + teacherId)
            + " ORDER BY p.start_time"
            + ";";

    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
