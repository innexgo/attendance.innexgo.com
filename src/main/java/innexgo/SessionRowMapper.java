package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SessionRowMapper implements RowMapper<Session> {
  @Override
  public Session mapRow(ResultSet row, int rowNum) throws SQLException {
    Session session = new Session();
    session.id = row.getInt("id");
    session.studentId = row.getInt("student_id");
    session.courseId = row.getInt("course_id");
    session.inEncounterId = row.getInt("in_encounter_id");
    session.outEncounterId = row.getInt("out_encounter_id");
    session.complete = row.getBoolean("complete");
    return session;
  }
}
