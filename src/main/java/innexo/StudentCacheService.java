
package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class StudentCacheService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public StudentCache getById(int id) {
    String sql = "SELECT id, current_status, last_location_id FROM student_cache WHERE id=?";
    RowMapper<StudentCache> rowMapper = new StudentCacheRowMapper();
    StudentCache StudentCache = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return StudentCache;
  }

  public List<StudentCache> getAll() {
    String sql = "SELECT id, current_status, last_location_id FROM StudentCache";
    RowMapper<StudentCache> rowMapper = new StudentCacheRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(StudentCache StudentCache) {
    // Add StudentCache
    String sql = "INSERT INTO student_cache (id, current_status, last_location_id) values (?, ?, ?)";
    jdbcTemplate.update(sql, StudentCache.id, StudentCache.currentStatus, StudentCache.lastLocationId);

    // Fetch StudentCache id
    sql = "SELECT id FROM student_cache WHERE current_status=? AND name=?";
    int id =
        jdbcTemplate.queryForObject(
            sql, Integer.class, StudentCache.currentStatus, StudentCache.lastLocationId);

    // Set StudentCache id
    StudentCache.id = id;
  }

  public void update(StudentCache StudentCache) {
    String sql = "UPDATE student_cache SET id=?, current_status=?, lastLocationId=? WHERE id=?";
    jdbcTemplate.update(
        sql, StudentCache.id, StudentCache.currentStatus, StudentCache.lastLocationId, StudentCache.id);
  }

  public StudentCache delete(int id) {
    StudentCache StudentCache = getById(id);
    String sql = "DELETE FROM student_cache WHERE id=?";
    jdbcTemplate.update(sql, id);
    return StudentCache;
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM student_cache WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }
}
