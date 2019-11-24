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

  public Semester getById(long id) {
    String sql = "SELECT id, start_time, end_time FROM semester WHERE id=?";
    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    Semester semester = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return semester;
  }

  public List<Semester> getAll() {
    String sql = "SELECT id, start_time, end_time FROM semester";
    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Semester semester) {
    // Add semester
    String sql =
        "INSERT INTO semester (id, start_time, end_time) values (?, ?, ?)";
    jdbcTemplate.update(
        sql, semester.id, semester.startTime, semester.endTime);

    // Fetch semester id
    sql = "SELECT id FROM semester WHERE start_time=? AND end_time=?";
    long id = jdbcTemplate.queryForObject(sql, Long.class, semester.startTime, semester.endTime);

    // Set semester id
    semester.id = id;
  }

  public void update(Semester semester) {
    String sql =
        "UPDATE semester SET id=?, start_time=?, end_time=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        semester.id,
        semester.startTime,
        semester.endTime,
        semester.id);
  }

  public Semester deleteById(long id) {
    Semester semester = getById(id);
    String sql = "DELETE FROM semester WHERE id=?";
    jdbcTemplate.update(sql, id);
    return semester;
  }

  public void deleteAll() {
    String sql = "TRUNCATE semester";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM semester WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public Semester getCurrentSemester() {
    List<Semester> currentSemesters =  query(
      null, // Long id
      System.currentTimeMillis(), // Long time
      null, // Long startTimeBegin
      null, // Long startTimeEnd
      null, // Long endTimeBegin
      null  // Long endTimeEnd
    );
    return (currentSemesters.size() != 0 ? currentSemesters.get(0) : null);
  }

  public List<Semester> query(
      Long id,
      Long time,
      Long startTimeBegin,
      Long startTimeEnd,
      Long endTimeBegin,
      Long endTimeEnd) {

    String sql =
        "SELECT se.id, se.start_time, se.end_time FROM semester se"
            + " WHERE 1=1 "
            + (id == null ? "" : " AND se.id = " + id)
            + (time == null ? "" : " AND " + time + " BETWEEN se.initial_time AND se.end_time")
            + (startTimeBegin == null ? "" : " AND se.start_time >= " + startTimeBegin)
            + (startTimeEnd == null ? "" : " AND se.start_time <= " + startTimeEnd)
            + (endTimeBegin == null ? "" : " AND se.end_time >= " + endTimeBegin)
            + (endTimeEnd == null ? "" : " AND se.end_time <= " + endTimeEnd)
            + " ORDER BY se.start_time"
            + ";";

    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
