package innexgo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StudentRowMapper implements RowMapper<Student> {

  @Override
  public Student mapRow(ResultSet row, int rowNum) throws SQLException {
    Student student = new Student();
    student.id = row.getLong("id");
    student.name = row.getString("name");
    student.tags = row.getString("tags");
    student.initialSemesterStartTime = row.getLong("initial_semester_start_time");
    return student;
  }
}
