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

  public List<Schedule> getAll() {
    String sql = "SELECT user_id, location_id, period FROM schedule";
    RowMapper<Schedule> rowMapper = new ScheduleRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Schedule schedule) {
    // Add schedule
    String sql = "INSERT INTO schedule (user_id, location_id, period) values (?, ?, ?)";
    jdbcTemplate.update(sql, schedule.userId, schedule.locationId, schedule.period);
  }

  public Schedule delete(Schedule schedule) {
    Schedule schedule = getById(id);
    String sql = "DELETE FROM schedule WHERE user_id=? AND location_id=? AND period=?";
    jdbcTemplate.update(sql, schedule.userId, schedule.locationId, schedule.period);
    return schedule;
  }

  public Schedule query(Integer userId, Integer locationId, Integer period) {
    String sql =
        "SELECT user_id, location_id, period FROM schedule WHERE 1=1 "
            + (userId == null ? "" : " AND user_id = " + userId)
            + (locationId == null ? "" : "AND location_id = " + locationId)
            + (period == null ? "" : "AND period = " + period)
            + ";";
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
