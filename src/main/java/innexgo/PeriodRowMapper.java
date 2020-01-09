package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PeriodRowMapper implements RowMapper<Period> {

  @Override
  public Period mapRow(ResultSet row, int rowNum) throws SQLException {
    Period period = new Period();
    period.startTime = row.getLong("start_time");
    period.number = row.getLong("number");
    period.type = row.getString("type");
    period.temp = row.getBoolean("temp");
    return period;
  }
}
