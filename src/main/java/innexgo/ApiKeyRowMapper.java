package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ApiKeyRowMapper implements RowMapper<ApiKey> {
  @Override
  public ApiKey mapRow(ResultSet row, int rowNum) throws SQLException {
    ApiKey apiKey = new ApiKey();
    apiKey.id = row.getLong("id");
    apiKey.userId = row.getLong("user_id");
    apiKey.creationTime = row.getLong("creation_time");
    apiKey.expirationTime = row.getLong("expiration_time");
    apiKey.keyHash = row.getString("key_hash");
    return apiKey;
  }
}
