package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class GradeRowMapper implements RowMapper<Grade> {

  @Override
  public Grade mapRow(ResultSet row, int rowNum) throws SQLException {
    Grade grade = new Grade();
    grade.id = row.getLong("id");
    grade.studentId = row.getLong("student_id");
    grade.semesterStartTime = row.getLong("semester_start_time");
    grade.number = row.getLong("number");
    return grade;
  }
}
