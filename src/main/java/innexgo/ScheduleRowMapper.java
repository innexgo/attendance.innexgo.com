package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ScheduleRowMapper implements RowMapper<Schedule> {

  @Override
  public Schedule mapRow(ResultSet row, int rowNum) throws SQLException {
    Schedule schedule = new Schedule();
    schedule.id = row.getLong("id");
    schedule.studentId = row.getLong("student_id");
    schedule.courseId = row.getLong("course_id");
    schedule.startTime = row.getLong("start_time");
    schedule.endTime = row.getLong("end_time");
    return schedule;
  }
}
