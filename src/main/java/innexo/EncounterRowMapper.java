package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EncounterRowMapper implements RowMapper<Encounter> {
	@Override
	public Encounter mapRow(ResultSet row, int rowNum) throws SQLException {
		Encounter e = new Encounter ();
    e.id = row.getInt("id");
    e.time = row.getTimestamp("time");
    e.locationId = row.getInt("location_id");
    e.userId = row.getInt("user_id");
    e.type = row.getString("type");
		return e;
  }
}
