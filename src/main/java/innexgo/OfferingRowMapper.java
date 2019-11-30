package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class OfferingRowMapper implements RowMapper<Offering> {

  @Override
  public Offering mapRow(ResultSet row, int rowNum) throws SQLException {
    Offering offering = new Offering();
    offering.id = row.getLong("id");
    offering.courseId = row.getLong("course_id");
    offering.semesterId = row.getLong("semester_id");
    return offering;
  }
}
