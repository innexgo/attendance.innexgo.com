
package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CourseRowMapper implements RowMapper<Course> {

  @Override
  public Course mapRow(ResultSet row, int rowNum) throws SQLException {
    Course course = new Course();
    course.id = row.getInt("id");
    course.teacherId = row.getInt("teacher_id");
    course.locationId = row.getInt("location_id");
    course.period = row.getInt("period");
    course.subject = row.getString("subject");
    return course;
  }
}
