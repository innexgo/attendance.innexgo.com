package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EncounterRowMapper implements RowMapper<Encounter> {
  @Override
  public Encounter mapRow(ResultSet row, int rowNum) throws SQLException {
    Encounter encounter = new Encounter();
    encounter.id = row.getInt("id");
    encounter.time = row.getLong("time");
    encounter.locationId = row.getInt("location_id");
    encounter.studentId = row.getInt("student_id");
    return encounter;
  }
}
