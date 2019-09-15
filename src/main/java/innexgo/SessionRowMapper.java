package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SessionRowMapper implements RowMapper<Session> {
  @Override
  public Session mapRow(ResultSet row, int rowNum) throws SQLException {
    Session session = new Session();
    session.id = row.getLong("id");
    session.studentId = row.getLong("student_id");
    session.courseId = row.getLong("course_id");
    session.inEncounterId = row.getLong("in_encounter_id");
    session.outEncounterId = row.getLong("out_encounter_id");
    session.hasOut = row.getBoolean("has_out");
    session.complete = row.getBoolean("complete");
    return session;
  }
}
