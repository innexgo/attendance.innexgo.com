package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ApiKeyService {
  @Autowired private JdbcTemplate jdbcTemplate;

  public ApiKey getById(int id) {
    String sql =
        "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key WHERE id=?";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    ApiKey apiKey = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return apiKey;
  }

  public ApiKey getByKeyHash(String keyHash) {
    String sql =
        "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key WHERE key_hash=?";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    ApiKey apiKey = jdbcTemplate.queryForObject(sql, rowMapper, keyHash);
    return apiKey;
  }

  public List<ApiKey> getAll() {
    String sql = "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(ApiKey apiKey) {
    // Add API key
    String sql =
        "INSERT INTO api_key (id, user_id, creation_time, expiration_time, key_hash) values (?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, apiKey.id, apiKey.userId, apiKey.creationTime, apiKey.expirationTime, apiKey.keyHash);

    // Fetch apiKey id
    sql =
        "SELECT id FROM api_key WHERE user_id=? AND creation_time=? AND expiration_time=? AND key_hash=?";
    int id =
        jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            apiKey.userId,
            apiKey.creationTime,
            apiKey.expirationTime,
            apiKey.keyHash);

    // Set apiKey id
    apiKey.id = id;
  }

  public void update(ApiKey apiKey) {
    String sql =
        "UPDATE api_key SET id=?, user_id=?, creation_time=?, expiration_time=?, key=? WHERE id=?";
    jdbcTemplate.update(
        sql, apiKey.id, apiKey.userId, apiKey.creationTime, apiKey.expirationTime, apiKey.keyHash);
  }

  public ApiKey delete(int id) {
    ApiKey apiKey = getById(id);
    String sql = "DELETE FROM api_key WHERE id=?";
    jdbcTemplate.update(sql, id);
    return apiKey;
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM api_key WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    if (count == 0) {
      return false;
    } else {
      return true;
    }
  }

  public boolean existsByKeyHash(String keyHash) {
    String sql = "SELECT count(*) FROM api_key WHERE key_hash=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, keyHash);
    return count != 0;
  }

  public List<ApiKey> query(
      Integer id,
      Integer userId,
      Integer minCreationTime,
      Integer maxCreationTime,
      String keyHash) {
    String sql =
        "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key WHERE 1=1"
            + (id == null ? "" : " AND id=" + id)
            + (userId == null ? "" : " AND user_id =" + userId)
            + (minCreationTime == null ? "" : " AND creation_time >= " + minCreationTime)
            + (maxCreationTime == null ? "" : " AND creation_time <= " + maxCreationTime)
            + (keyHash == null ? "" : " AND key_hash = \'" + Utils.escapeSQLString(keyHash) + "\'")
            + ";";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
