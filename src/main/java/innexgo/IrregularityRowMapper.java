package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class IrregularityRowMapper implements RowMapper<Irregularity> {
  @Override
  public Irregularity mapRow(ResultSet row, int rowNum) throws SQLException {
    Irregularity irregularity = new Irregularity();
    irregularity.id = row.getInt("id");
    irregularity.studentId = row.getInt("student_id");
    irregularity.courseId = row.getInt("course_id");
    irregularity.periodId = row.getInt("period_id");
    irregularity.type = row.getString("type");
    irregularity.time = row.getLong("time");
    irregularity.timeMissing = row.getLong("time_missing");
    return irregularity;
  }
}
