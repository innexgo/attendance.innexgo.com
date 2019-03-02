package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TargetRowMapper implements RowMapper<Target> {
  @Override
  public Target mapRow(ResultSet row, int rowNum) throws SQLException {
    Target t = new Target();
    t.id = row.getInt("id");
    t.organizerId= row.getInt("organizer_id");
    t.name = row.getString("name");
    return t;
  }
}
