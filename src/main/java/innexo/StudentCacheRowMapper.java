package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StudentCacheRowMapper implements RowMapper<StudentCache> {

  @Override
  public StudentCache mapRow(ResultSet row, int rowNum) throws SQLException {
    StudentCache studentCache = new StudentCache();
    studentCache.id = row.getInt("id");
    studentCache.currentStatus = row.getString("current_status");
    studentCache.lastLocationId = row.getInt("last_location_id");
    studentCache.lastLocationTime = row.getInt("last_location_time");
    return studentCache;
  }
}
