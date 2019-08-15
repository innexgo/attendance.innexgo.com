package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PeriodRowMapper implements RowMapper<Period> {

  @Override
  public Period mapRow(ResultSet row, int rowNum) throws SQLException {
    Period period = new Period();
    period.id = row.getInt("id");
    period.startTime = row.getLong("start_time");
    period.endTime = row.getLong("end_time");
    period.period = row.getInt("period");
    return period;
  }
}
