package innexgo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class SessionService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Session getById(long id) {
    String sql =
        "SELECT id, student_id, in_encounter_id, out_encounter_id, complete FROM session WHERE id=?";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    Session session = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return session;
  }

  public List<Session> getAll() {
    String sql =
        "SELECT id, student_id, in_encounter_id, out_encounter_id, complete FROM session";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Session session) {
    // Add session
    String sql =
        "INSERT INTO session (id, student_id, in_encounter_id, out_encounter_id, complete) values (?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql,
        session.id,
        session.studentId,
        session.inEncounterId,
        session.outEncounterId,
        session.complete);

    // Fetch session id
    sql =
        "SELECT id FROM session WHERE student_id=? AND in_encounter_id=? AND complete=?";
    long id =
        jdbcTemplate.queryForObject(
            sql,
            Long.class,
            session.studentId,
            session.inEncounterId,
            session.complete);

    // Set session id
    session.id = id;
  }

  public void update(Session session) {
    String sql =
        "UPDATE session SET id=?, student_id=?, in_encounter_id=?, out_encounter_id=?, complete=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        session.id,
        session.studentId,
        session.inEncounterId,
        session.outEncounterId,
        session.complete,
        session.id);
  }

  public Session delete(long id) {
    Session session = getById(id);
    String sql = "DELETE FROM session WHERE id=?";
    jdbcTemplate.update(sql, id);
    return session;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM session WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Session> query(
      Long id,
      Long inEncounterId,
      Long outEncounterId,
      Long anyEncounterId,
      Long periodId,
      Long period,
      Long courseId,
      Boolean complete,
      Long locationId,
      Long studentId,
      Long teacherId,
      Long time,
      Long inTimeBegin,
      Long inTimeEnd,
      Long outTimeBegin,
      Long outTimeEnd,
      Long count) {

    boolean courseUnused = courseId == null && teacherId == null;
    boolean periodUnused = courseUnused && periodId == null && period == null;

    String sql =
        "SELECT DISTINCT ses.id, ses.student_id, ses.in_encounter_id, ses.out_encounter_id, ses.complete"
            + " FROM session ses"
            + " INNER JOIN encounter inen ON ses.in_encounter_id = inen.id"
            + " LEFT JOIN encounter outen ON (ses.complete AND ses.out_encounter_id = outen.id)"
            + (periodUnused ? "" : " INNER JOIN period per ON (per.start_time <= IFNULL(outen.time, UNIX_TIMESTAMP()*1000) AND per.end_time >= inen.time)")
            + (courseUnused ? "" : " INNER JOIN course crs ON crs.period = per.period")
            + " WHERE 1 = 1 "
            + (id == null ? "" : " AND ses.id = " + id)
            + (inEncounterId == null ? "" : " AND ses.in_encounter_id = " + inEncounterId)
            + (outEncounterId == null ? "" : " AND ses.out_encounter_id = " + outEncounterId)
            + (anyEncounterId == null ? "" : " AND (ses.in_encounter_id =" + anyEncounterId + " OR ses.out_encounter_id = " + anyEncounterId + ")")
            + (complete == null ? "" : " AND ses.complete = " + complete)
            + (courseId == null ? "" : " AND ses.course_id = " + courseId)
            + (studentId == null ? "" : " AND ses.student_id = " + studentId)
            + (teacherId == null ? "" : " AND crs.teacher_id = " + teacherId)
            + (locationId == null ? "" : " AND inen.location_id = " + locationId)
            + (time == null ? "" : " AND " + time + " BETWEEN inen.time AND outen.time")
            + (inTimeBegin == null ? "" : " AND inen.time >= " + inTimeBegin)
            + (inTimeEnd == null ? "" : " AND inen.time <= " + inTimeEnd)
            + (outTimeBegin == null ? "" : " AND outen.time >= " + outTimeBegin)
            + (outTimeEnd == null ? "" : " AND outen.time <= " + outTimeEnd)
            + (" ORDER BY IFNULL(outen.time, inen.time) DESC")
            + (count == null ? "" : " LIMIT " + count)
            + ";";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
