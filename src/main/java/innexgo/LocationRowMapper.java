package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class LocationRowMapper implements RowMapper<Location> {

  @Override
  public Location mapRow(ResultSet row, int rowNum) throws SQLException {
    Location loc = new Location();
    loc.id = row.getLong("id");
    loc.name = row.getString("name");
    return loc;
  }
}
