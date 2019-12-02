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

  public Period getByTime(long time) {
    String sql = "SELECT time, number, type FROM period WHERE time=?";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    Period period = jdbcTemplate.queryForObject(sql, rowMapper, time);
    return period;
  }

  public List<Period> getAll() {
    String sql = "SELECT time, number, type FROM period";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Period period) {
    // Add period
    String sql =
        "INSERT INTO period (time, number, type) values (?, ?, ?)";
    jdbcTemplate.update(
        sql, period.time, period.number, period.type);
  }

  public void update(Period period) {
    String sql =
        "UPDATE period SET time=?, number=?, type=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        period.time,
        period.number,
        period.type);
  }

  public Period deleteByTime(long time) {
    Period period = getByTime(time);
    String sql = "DELETE FROM period WHERE time=?";
    jdbcTemplate.update(sql, time);
    return period;
  }

  public void deleteAll() {
    String sql = "TRUNCATE period";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsByTime(long time) {
    String sql = "SELECT count(*) FROM period WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, time);
    return count != 0;
  }

  public List<Period> query(
      Long time,
      Long number,
      String type,
      Long containsTime,
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
