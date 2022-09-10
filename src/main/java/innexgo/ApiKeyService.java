/*
 * Innexgo Website
 * Copyright (C) 2020 Innexgo LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package innexgo;

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

  public ApiKey getById(long id) {
    String sql =
        "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key WHERE id=?";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    ApiKey apiKey = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return apiKey;
  }

  // Gets the last created key with the keyhash
  public ApiKey getByKeyHash(String keyHash) {
    String sql =
        "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key WHERE key_hash=? ORDER BY creation_time DESC";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    List<ApiKey> apiKeys = jdbcTemplate.query(sql, rowMapper, keyHash);
    return apiKeys.size() > 0 ? apiKeys.get(0) : null;
  }

  public List<ApiKey> getAll() {
    String sql = "SELECT id, user_id, creation_time, expiration_time, key_hash FROM api_key";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(ApiKey apiKey) {
    // Add API key
    String sql =
        "INSERT INTO api_key (user_id, creation_time, expiration_time, key_hash) values (?, ?, ?, ?)";
    jdbcTemplate.update(
        sql, apiKey.userId, apiKey.creationTime, apiKey.expirationTime, apiKey.keyHash);

    // Fetch apiKey id
    sql =
        "SELECT id FROM api_key WHERE user_id=? AND creation_time=? AND expiration_time=? AND key_hash=?";
    long id =
        jdbcTemplate.queryForObject(
            sql,
            Long.class,
            apiKey.userId,
            apiKey.creationTime,
            apiKey.expirationTime,
            apiKey.keyHash);

    // Set apiKey id
    apiKey.id = id;
  }

  public void update(ApiKey apiKey) {
    String sql =
        "UPDATE api_key user_id=?, creation_time=?, expiration_time=?, key=? WHERE id=?";
    jdbcTemplate.update(
        sql, apiKey.userId, apiKey.creationTime, apiKey.expirationTime, apiKey.keyHash);
  }

  public ApiKey deleteById(long id) {
    ApiKey apiKey = getById(id);
    String sql = "DELETE FROM api_key WHERE id=?";
    jdbcTemplate.update(sql, id);
    return apiKey;
  }

  public boolean existsById(long id) {
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
    long count = jdbcTemplate.queryForObject(sql, Long.class, keyHash);
    return count != 0;
  }

  public List<ApiKey> query(
      Long id, Long userId, Long minCreationTime, Long maxCreationTime, String keyHash, long offset, long count) {
    String sql =
        "SELECT a.id, a.user_id, a.creation_time, a.expiration_time, a.key_hash FROM api_key a WHERE 1=1"
            + (id == null ? "" : " AND a.id=" + id)
            + (userId == null ? "" : " AND a.user_id =" + userId)
            + (minCreationTime == null ? "" : " AND a.creation_time >= " + minCreationTime)
            + (maxCreationTime == null ? "" : " AND a.creation_time <= " + maxCreationTime)
            + (keyHash == null ? "" : " AND a.key_hash = " + Utils.escape(keyHash))
            + (" ORDER BY a.id")
            + (" LIMIT " + offset + " OFFSET "  + count)
            + ";";
    RowMapper<ApiKey> rowMapper = new ApiKeyRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
