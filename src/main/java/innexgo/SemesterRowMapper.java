package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SemesterRowMapper implements RowMapper<Semester> {

  @Override
  public Semester mapRow(ResultSet row, int rowNum) throws SQLException {
    Semester semester = new Semester();
    semester.id = row.getLong("id");
    semester.startTime = row.getLong("start_time");
    semester.endTime = row.getLong("end_time");
    return semester;
  }
}
