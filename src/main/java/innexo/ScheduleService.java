package innexo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class ScheduleService {

  @Autowired private JdbcTemplate jdbcTemplate;

  public Schedule getById(int id) {
    String sql = "SELECT id, user_id, location_id, period FROM schedule WHERE id=?";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    Schedule schedule = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return schedule;
  }

  public List<Schedule> getAll() {
    String sql = "SELECT id, user_id, location_id, period FROM schedule";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Schedule schedule) {
    // Add schedule
    String sql = "INSERT INTO schedule (id, user_id, location_id, period) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, schedule.id, schedule.userId, schedule.locationId, schedule.period);

    // Fetch schedule id
    sql = "SELECT id FROM schedule WHERE user_id=? AND location_id=? AND period=?";
    int id = jdbcTemplate.queryForObject(sql, Integer.class, schedule.userId, schedule.locationId, schedule.period);
    schedule.id = id;
  }

  public void update(Schedule schedule) {
    String sql = "UPDATE schedule SET id=?, user_id=?, location_id=?, period=? WHERE id=?";
    jdbcTemplate.update(sql, schedule.id, schedule.userId, schedule.locationId, schedule.period, schedule.id);
  }

  public Schedule delete(int id) {
    Schedule schedule = getById(id);
    String sql = "DELETE FROM schedule WHERE id=?";
    jdbcTemplate.update(sql, id);
    return schedule;
  }

  public List<Schedule> query(Integer scheduleId, Integer userId, Integer locationId, Integer period) {
    String sql =
        "SELECT id, user_id, location_id, period FROM schedule WHERE 1=1 "
            + (userId == null ? "" : " AND user_id = " + userId)
            + (locationId == null ? "" : "AND location_id = " + locationId)
            + (period == null ? "" : "AND period = " + period)
            + ";";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM schedule WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }
}
