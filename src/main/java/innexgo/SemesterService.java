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
public class SemesterService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Semester getByStartTime(long startTime) {
    String sql = "SELECT start_time, year, kind FROM semester WHERE start_time=?";
    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    Semester semester = jdbcTemplate.queryForObject(sql, rowMapper, startTime);
    return semester;
  }

  public List<Semester> getAll() {
    String sql = "SELECT start_time, year, kind FROM semester";
    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Semester semester) {
    // Add semester
    String sql = "INSERT INTO semester (start_time, year, kind) values (?, ?, ?)";
    jdbcTemplate.update(
        sql, semester.startTime, semester.year, semester.kind.name());
  }

  public void update(Semester semester) {
    String sql = "UPDATE semester SET start_time=?, year=?, kind=? WHERE start_time=?";
    jdbcTemplate.update(
        sql,
        semester.startTime,
        semester.year,
        semester.kind.name(),
        semester.startTime);
  }

  public Semester deleteByStartTime(long startTime) {
    Semester semester = getByStartTime(startTime);
    String sql = "DELETE FROM semester WHERE start_time=?";
    jdbcTemplate.update(sql, startTime);
    return semester;
  }

  public void deleteAll() {
    String sql = "TRUNCATE semester";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsByStartTime(long startTime) {
    String sql = "SELECT count(*) FROM semester WHERE start_time=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, startTime);
    return count != 0;
  }

  public Semester getCurrent() {
    return getByTime(System.currentTimeMillis());
  }

  public Semester getByTime(Long time) {
    List<Semester> currentSemesters = query(
        null, // Long startTime
        null, // Long year
        null, // Long kind
        null, // Long minStartTime
        time, // Long maxStartTime
        0, // long offset
        Long.MAX_VALUE // long count
    );
    System.out.println(currentSemesters);
    // Get the most recent of the ones that start before now
    return (currentSemesters.size() != 0
        ? currentSemesters.get(currentSemesters.size() - 1)
        : null);
  }

  public List<Semester> query(
      Long startTime,
      Long year,
      SemesterKind kind,
      Long minStartTime,
      Long maxStartTime,
      long offset,
      long count
  ) {

    String sql = "SELECT se.start_time, se.year, se.kind FROM semester se"
        + " WHERE 1=1 "
        + (startTime == null ? "" : " AND se.start_time = " + startTime)
        + (year == null ? "" : " AND se.year = " + year)
        + (kind == null ? "" : " AND se.kind = " + kind.name())
        + (minStartTime == null ? "" : " AND se.start_time >= " + minStartTime)
        + (maxStartTime == null ? "" : " AND se.start_time <= " + maxStartTime)
        + (" ORDER BY se.start_time")
        + (" LIMIT " + count + " OFFSET " + offset)
        + ";";

    RowMapper<Semester> rowMapper = new SemesterRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
