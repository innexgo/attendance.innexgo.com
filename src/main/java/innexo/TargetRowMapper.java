package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TargetRowMapper implements RowMapper<Target> {
  @Override
  public Target mapRow(ResultSet row, int rowNum) throws SQLException {
    Target t = new Target();
    t.id = row.getInt("id");
    t.userId= row.getInt("user_id");
    t.locationId= row.getInt("location_id");
    t.name = row.getString("name");
    t.minTime = row.getTimestamp("min_time");
    t.maxTime = row.getTimestamp("max_time");
    return t;
  }
}
