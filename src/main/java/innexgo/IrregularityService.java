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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class IrregularityService {
  @Autowired private JdbcTemplate jdbcTemplate;

  public Irregularity getById(long id) {
    String sql =
        "SELECT id, student_id, course_id, period_start_time, kind, time, time_missing FROM irregularity WHERE id=?";
    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    Irregularity irregularity = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return irregularity;
  }

  public List<Irregularity> getAll() {
    String sql =
        "SELECT id, student_id, course_id, period_start_time, kind, time, time_missing FROM irregularity";
    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Irregularity irregularity) {

    // Add irregularity
    String sql =
        "INSERT INTO irregularity (id, student_id, course_id, period_start_time, kind, time, time_missing) values (?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(
        sql,
        irregularity.id,
        irregularity.studentId,
        irregularity.courseId,
        irregularity.periodStartTime,
        irregularity.kind.name(),
        irregularity.time,
        irregularity.timeMissing);

    // Fetch irregularity id
    sql =
        "SELECT id FROM irregularity WHERE student_id=? AND course_id=? AND period_start_time=? AND kind=? AND time=? AND time_missing=?";
    long id =
        jdbcTemplate.queryForObject(
            sql,
            Long.class,
            irregularity.studentId,
            irregularity.courseId,
            irregularity.periodStartTime,
            irregularity.kind.name(),
            irregularity.time,
            irregularity.timeMissing);

    // Set irregularity id
    irregularity.id = id;
  }

  public void update(Irregularity irregularity) {
    String sql =
        "UPDATE irregularity SET id=?, student_id=?, course_id=?, period_start_time=?, kind=?, time=?, time_missing=? WHERE id=?";
    jdbcTemplate.update(
        sql,
        irregularity.id,
        irregularity.studentId,
        irregularity.courseId,
        irregularity.periodStartTime,
        irregularity.kind.name(),
        irregularity.time,
        irregularity.timeMissing,
        irregularity.id);
  }

  public Irregularity deleteById(long id) {
    Irregularity irregularity = getById(id);
    String sql = "DELETE FROM irregularity WHERE id=?";
    jdbcTemplate.update(sql, id);
    return irregularity;
  }

  public boolean existsById(long id) {
    String sql = "SELECT count(*) FROM irregularity WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != 0;
  }

  public List<Irregularity> query(
      Long id,
      Long studentId,
      Long courseId,
      Long periodStartTime,
      Long teacherId,
      IrregularityKind kind,
      Long time,
      Long minTime,
      Long maxTime,
      long offset,
      long count) {
    String sql =
        "SELECT irr.id, irr.student_id, irr.course_id, irr.period_start_time, irr.kind, irr.time, irr.time_missing"
            + " FROM irregularity irr"
            + (teacherId == null ? "" : " JOIN course crs ON crs.id = irr.course_id")
            + " WHERE 1=1 "
            + (id == null ? "" : " AND irr.id = " + id)
            + (studentId == null ? "" : " AND irr.student_id = " + studentId)
            + (courseId == null ? "" : " AND irr.course_id = " + courseId)
            + (periodStartTime == null ? "" : " AND irr.period_start_time = " + periodStartTime)
            + (teacherId == null ? "" : " AND crs.teacher_id = " + teacherId)
            + (kind == null ? "" : " AND irr.kind = " + Utils.toSQLString(kind))
            + (time == null ? "" : " AND irr.time = " + time)
            + (minTime == null ? "" : " AND irr.time >= " + minTime)
            + (maxTime == null ? "" : " AND irr.time <= " + maxTime)
            + (" ORDER BY irr.id")
            + (" LIMIT " + offset + " OFFSET "  + count)
            + ";";

    RowMapper<Irregularity> rowMapper = new IrregularityRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
