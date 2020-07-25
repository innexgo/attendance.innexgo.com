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
    grade.numbering = row.getLong("numbering");
    return grade;
  }
}
