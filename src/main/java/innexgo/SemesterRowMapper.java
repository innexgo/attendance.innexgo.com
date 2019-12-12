package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SemesterRowMapper implements RowMapper<Semester> {

  @Override
  public Semester mapRow(ResultSet row, int rowNum) throws SQLException {
    Semester semester = new Semester();
    semester.startTime = row.getLong("start_time");
    semester.year = row.getLong("year");
    semester.type = row.getString("type");
    return semester;
  }
}
