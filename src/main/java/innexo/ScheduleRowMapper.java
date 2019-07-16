package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ScheduleRowMapper implements RowMapper<Schedule> {

  @Override
  public Schedule mapRow(ResultSet row, int rowNum) throws SQLException {
    Schedule schedule = new Schedule();
    schedule.id = row.getInt("id");
    schedule.userId = row.getInt("user_id");
    schedule.locationId = row.getInt("location_id");
    schedule.period = row.getInt("period");
    return schedule;
  }
}
