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

  @Autowired private EncounterService encounterService;
  @Autowired private SessionService sessionService;

  @Autowired private JdbcTemplate jdbcTemplate;

  public Period getByStartTime(long startTime) {
    String sql = "SELECT start_time, number, type FROM period WHERE start_time=?";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    Period period = jdbcTemplate.queryForObject(sql, rowMapper, startTime);
    return period;
  }

  public List<Period> getAll() {
    String sql = "SELECT start_time, number, type FROM period";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Period period) {
    // Add period
    String sql =
        "INSERT INTO period (start_time, number, type) values (?, ?, ?)";
    jdbcTemplate.update(
        sql, period.startTime, period.number, period.type);
  }

  public void update(Period period) {
    String sql =
        "UPDATE period SET start_time=?, number=?, type=? WHERE start_time=?";
    jdbcTemplate.update(
        sql,
        period.startTime,
        period.number,
        period.type);
  }

  public Period deleteByStartTime(long startTime) {
    Period period = getByStartTime(startTime);
    String sql = "DELETE FROM period WHERE start_time=?";
    jdbcTemplate.update(sql, startTime);
    return period;
  }

  public void deleteAll() {
    String sql = "TRUNCATE period";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsByStartTime(long startTime) {
    String sql = "SELECT count(*) FROM period WHERE start_time=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, startTime);
    return count != 0;
  }

  public Period getCurrent() {
    return getByTime(System.currentTimeMillis());
  }


  public Period getByTime(long time) {
    List<Period> currentPeriods = query(
      null, // Long startTime
      null, // Long number
      null, // String type
      null, // Long minStartTime
      time  // Long maxStartTime
    );
    return (currentPeriods.size() != 0 ? currentPeriods.get(currentPeriods.size()-1) : null);
  }

  public Period getNextByTime(long time) {
    List<Period> currentPeriods = query(
      null, // Long startTime
      null, // Long number
      null, // String type
      time, // Long minStartTime
      null  // Long maxStartTime
    );
    return (currentPeriods.size() != 0 ? currentPeriods.get(0) : null);
  }


  public List<Period> query(
      Long startTime,
      Long number,
      String type,
      Long minStartTime,
      Long maxStartTime
    ) {

    String sql = "SELECT p.start_time, p.number, p.type FROM period pr"
            + " WHERE 1=1"
            + (startTime == null ? "" : " AND pr.start_time = " + startTime)
            + (number == null ? "" : " AND pr.number = " + number)
            + (type == null ? "" : " AND pr.type = " + Utils.escape(type))
            + (minStartTime == null ? "" : " AND pr.start_time >= " + minStartTime)
            + (maxStartTime == null ? "" : " AND pr.start_time <= " + maxStartTime)
            + " ORDER BY pr.start_time"
            + ";";

    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
