package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ScheduleRowMapper implements RowMapper<Schedule> {

  @Override
  public Schedule mapRow(ResultSet row, int rowNum) throws SQLException {
    Schedule schedule = new Schedule();
    schedule.id = row.getInt("id");
    schedule.studentId = row.getInt("student_id");
    schedule.courseId = row.getInt("course_id");
    return schedule;
  }
}
