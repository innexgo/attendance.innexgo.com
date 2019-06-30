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
        "SELECT id, creator_id, creation_time, expiration_time, keydata FROM api_key WHERE id=?";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    ApiKey apiKey = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return apiKey;
  }

  public ApiKey getByKey(String key) {
    String sql =
        "SELECT id, creator_id, creation_time, expiration_time, keydata FROM api_key WHERE keydata=?";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    ApiKey apiKey = jdbcTemplate.queryForObject(sql, rowMapper, key);
    return apiKey;
  }

  public List<ApiKey> getAll() {
    String sql = "SELECT id, creator_id, creation_time, expiration_time, keydata FROM api_key";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(ApiKey apiKey) {
    // Add API key
    String sql =
        "INSERT INTO api_key (id, creator_id, creation_time, expiration_time, keydata) values (?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql,
        apiKey.id,
        apiKey.creatorId,
        apiKey.creationTime,
        apiKey.expirationTime,
        apiKey.keydata);

    // Fetch apiKey id
    sql =
        "SELECT id FROM api_key WHERE creator_id=? AND creation_time=? AND expiration_time=? AND keydata=?";
    int id =
        jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            apiKey.creatorId,
            apiKey.creationTime,
            apiKey.expirationTime,
            apiKey.keydata);

    // Set apiKey id
    apiKey.id = id;
  }

  public void update(ApiKey apiKey) {
    String sql =
        "UPDATE api_key SET id=?, creator_id=?, creation_time=?, expiration_time=?, key=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        apiKey.id,
        apiKey.creatorId,
        apiKey.creationTime,
        apiKey.expirationTime,
        apiKey.keydata);
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

  public boolean existsByKey(String key) {
    String sql = "SELECT count(*) FROM api_key WHERE keydata=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, key);
    if (count == 0) {
      return false;
    } else {
      return true;
    }
  }
}
