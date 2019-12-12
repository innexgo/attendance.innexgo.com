package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EncounterRowMapper implements RowMapper<Encounter> {
  @Override
  public Encounter mapRow(ResultSet row, int rowNum) throws SQLException {
    Encounter encounter = new Encounter();
    encounter.id = row.getLong("id");
    encounter.time = row.getLong("time");
    encounter.locationId = row.getLong("location_id");
    encounter.studentId = row.getLong("student_id");
    encounter.type = row.getString("type");
    return encounter;
  }
}
