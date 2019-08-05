package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StudentCacheRowMapper implements RowMapper<StudentCache> {

  @Override
  public StudentCache mapRow(ResultSet row, int rowNum) throws SQLException {
    StudentCache studentCache = new StudentCache();
    studentCache.id = row.getInt("id");
    studentCache.currentStatus = row.getInt("current_status");
    studentCache.lastLocationId = row.getString("last_location_id");
    return studentCache;
  }
}
