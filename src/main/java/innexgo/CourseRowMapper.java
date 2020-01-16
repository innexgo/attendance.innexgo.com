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

public class CourseRowMapper implements RowMapper<Course> {

  @Override
  public Course mapRow(ResultSet row, int rowNum) throws SQLException {
    Course course = new Course();
    course.id = row.getLong("id");
    course.teacherId = row.getLong("teacher_id");
    course.locationId = row.getLong("location_id");
    course.period = row.getLong("period");
    course.subject = row.getString("subject");
    return course;
  }
}
