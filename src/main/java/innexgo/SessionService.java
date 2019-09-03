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

  public Session getById(int id) {
    String sql =
        "SELECT id, student_id, in_encounter_id, out_encounter_id, course_id, complete FROM session WHERE id=?";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    Session session = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return session;
  }

  public List<Session> getAll() {
    String sql =
        "SELECT id, student_id, in_encounter_id, out_encounter_id, course_id, complete FROM session";
    RowMapper<Session> rowMapper = new SessionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Session session) {
    // Add session
    String sql =
        "INSERT INTO session (id, student_id, in_encounter_id, out_encounter_id, course_id, complete) values (?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql,
        session.id,
        session.studentId,
        session.inEncounterId,
        session.outEncounterId,
        session.courseId,
        session.complete);

    // Fetch session id
    sql =
        "SELECT id FROM session WHERE student_id=? AND in_encounter_id=? AND out_encounter_id=? AND course_id=? AND complete=?";
    int id =
        jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            session.studentId,
            session.inEncounterId,
            session.outEncounterId,
            session.courseId,
            session.complete);

    // Set session id
    session.id = id;
  }

  public void update(Session session) {
    String sql =
        "UPDATE session SET id=?, student_id=?, in_encounter_id=?, out_encounter_id=?, course_id=?, complete=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        session.id,
        session.studentId,
        session.inEncounterId,
        session.outEncounterId,
        session.courseId,
        session.complete,
        session.id);
  }

  public Session delete(int id) {
    Session session = getById(id);
    String sql = "DELETE FROM session WHERE id=?";
    jdbcTemplate.update(sql, id);
    return session;
  }

  public boolean existsById(int id) {
    String sql = "SELECT count(*) FROM session WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Session> query(
      Integer id,
      Integer inEncounterId,
      Integer outEncounterId,
      Integer anyEncounterId,
      Integer courseId,
      Boolean complete,
      Integer locationId,
      Integer studentId,
      Long time,
      Long inTimeBegin,
      Long inTimeEnd,
      Long outTimeBegin,
      Long outTimeEnd) {

    boolean outEncounterUnused =
        outEncounterId == null && outTimeBegin == null && outTimeEnd == null && time == null;

    String sql =
        "SELECT ses.id, ses.student_id, ses.in_encounter_id, ses.out_encounter_id, ses.course_id, ses.complete"
            + " FROM session ses"
            + " JOIN encounter inen ON ses.in_encounter_id = inen.id"
            + (outEncounterUnused ? "" : " JOIN encounter outen ON ses.out_encounter_id = outen.id")
            + " WHERE 1=1 "
            + (id == null ? "" : " AND ses.id = " + id)
            + (inEncounterId == null ? "" : " AND ses.in_encounter_id = " + inEncounterId)
            + (outEncounterId == null ? "" : " AND ses.out_encounter_id = " + outEncounterId)
            + (anyEncounterId == null
                ? ""
                : " AND (ses.in_encounter_id ="
                    + anyEncounterId
                    + " OR ses.out_encounter_id = "
                    + anyEncounterId
                    + ")")
            + (complete == null ? "" : " AND ses.complete = " + complete)
            + (courseId == null ? "" : " AND ses.course_id = " + courseId)
            + (studentId == null ? "" : " AND ses.student_id = " + studentId)
            + (locationId == null ? "" : " AND inen.location_id = " + locationId)
            + (time == null ? "" : " AND " + time + " BETWEEN inen.time AND outen.time")
            + (inTimeBegin == null ? "" : " AND inen.time >= " + inTimeBegin)
            + (inTimeEnd == null ? "" : " AND inen.time <= " + inTimeEnd)
            + (outTimeBegin == null ? "" : " AND outen.time >= " + outTimeBegin)
            + (outTimeEnd == null ? "" : " AND outen.time <= " + outTimeEnd)
            + ";";

    RowMapper<Session> rowMapper = new SessionRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
