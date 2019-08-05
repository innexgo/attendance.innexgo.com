
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
    String sql = "SELECT id, current_status, last_location_id, last_location_time FROM student_cache WHERE id=?";
    RowMapper<StudentCache> rowMapper = new StudentCacheRowMapper();
    StudentCache StudentCache = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return StudentCache;
  }

  public List<StudentCache> getAll() {
    String sql = "SELECT id, current_status, last_location_id, last_location_time FROM student_cache";
    RowMapper<StudentCache> rowMapper = new StudentCacheRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(StudentCache studentCache) {
    // Add StudentCache
    String sql = "INSERT INTO student_cache (id, current_status, last_location_id, last_location_time) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, studentCache.id, studentCache.currentStatus, studentCache.lastLocationId, studentCache.lastLocationTime);
    // note that student cache does not have auto_increment, so we dont have to do a search to refind it
  }

  public void update(StudentCache studentCache) {
    String sql = "UPDATE student_cache SET id=?, current_status=?, last_location_id=?, last_location_time=? WHERE id=?";
    jdbcTemplate.update(
        sql, studentCache.id, studentCache.currentStatus, studentCache.lastLocationId, studentCache.lastLocationTime, studentCache.id);
  }

  public StudentCache delete(int id) {
    StudentCache studentCache = getById(id);
    String sql = "DELETE FROM student_cache WHERE id=?";
    jdbcTemplate.update(sql, id);
    return studentCache;
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM student_cache WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }
}
