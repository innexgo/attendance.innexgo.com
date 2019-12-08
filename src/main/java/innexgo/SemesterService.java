package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class SemesterService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Semester getByStartTime(long startTime) {
    String sql = "SELECT start_time, year, type FROM semester WHERE start_time=?";
    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    Semester semester = jdbcTemplate.queryForObject(sql, rowMapper, startTime);
    return semester;
  }

  public List<Semester> getAll() {
    String sql = "SELECT start_time, year, type FROM semester";
    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Semester semester) {
    // Add semester
    String sql =
        "INSERT INTO semester (start_time, year, type) values (?, ?, ?)";
    jdbcTemplate.update(
        sql, semester.startTime, semester.year, semester.type);
  }

  public void update(Semester semester) {
    String sql =
        "UPDATE semester SET start_time=?, year=?, type=? WHERE start_time=?";
    jdbcTemplate.update(
        sql,
        semester.startTime,
        semester.year,
        semester.type,
        semester.startTime);
  }

  public Semester deleteByStartTime(long startTime) {
    Semester semester = getByStartTime(startTime);
    String sql = "DELETE FROM semester WHERE start_time=?";
    jdbcTemplate.update(sql, startTime);
    return semester;
  }

  public void deleteAll() {
    String sql = "TRUNCATE semester";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsByStartTime(long startTime) {
    String sql = "SELECT count(*) FROM semester WHERE start_time=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, startTime);
    return count != 0;
  }

  public Semester getCurrent() {
    return getByTime(System.currentTimeMillis());
  }

  public Semester getByTime(Long time) {
    List<Semester> currentSemesters =  query(
      null, // Long startTime
      null, // Long year
      null, // Long type
      null, // Long minStartTime
      time  // Long maxStartTime
    );
    return (currentSemesters.size() != 0 ? currentSemesters.get(0) : null);
  }

  public List<Semester> query(
      Long startTime,
      Long year,
      String type,
      Long minStartTime,
      Long maxStartTime
      ) {

    String sql = "SELECT se.start_time, se.year, se.type FROM semester se"
            + " WHERE 1=1 "
            + (startTime == null ? "" : " AND se.start_time = " + startTime)
            + (year == null ? "" : " AND se.year = " + year)
            + (type == null ? "" : " AND se.type = " + Utils.escape(type))
            + (minStartTime == null ? "" : " AND se.start_time >= " + minStartTime)
            + (maxStartTime == null ? "" : " AND se.start_time <= " + maxStartTime)
            + " ORDER BY se.start_time"
            + ";";

    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
