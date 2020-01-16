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

public class IrregularityRowMapper implements RowMapper<Irregularity> {
  @Override
  public Irregularity mapRow(ResultSet row, int rowNum) throws SQLException {
    Irregularity irregularity = new Irregularity();
    irregularity.id = row.getLong("id");
    irregularity.studentId = row.getLong("student_id");
    irregularity.courseId = row.getLong("course_id");
    irregularity.periodStartTime = row.getLong("period_start_time");
    irregularity.type = row.getString("type");
    irregularity.time = row.getLong("time");
    irregularity.timeMissing = row.getLong("time_missing");
    return irregularity;
  }
}
