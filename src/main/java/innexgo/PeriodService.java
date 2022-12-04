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
public class PeriodService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Period getByStartTime(long startTime) {
    String sql = "SELECT start_time, numbering, kind, temp FROM period WHERE start_time=?";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    Period period = jdbcTemplate.queryForObject(sql, rowMapper, startTime);
    return period;
  }

  public List<Period> getAll() {
    String sql = "SELECT start_time, numbering, kind, temp FROM period";
    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }

  public void add(Period period) {
    // Add period
    String sql = "INSERT INTO period (start_time, numbering, kind, temp) values (?, ?, ?::period_kind, ?)";
    jdbcTemplate.update(
        sql, period.startTime, period.numbering, period.kind.name(), period.temp);
  }

  public void update(Period period) {
    String sql = "UPDATE period SET start_time=?, numbering=?, kind=?, temp=? WHERE start_time=?";
    jdbcTemplate.update(
        sql,
        period.startTime,
        period.numbering,
        period.kind.name(),
        period.temp);
  }

  public Period deleteByStartTime(long startTime) {
    Period period = getByStartTime(startTime);
    String sql = "DELETE FROM period WHERE start_time=?";
    jdbcTemplate.update(sql, startTime);
    return period;
  }

  public void deleteAll() {
    String sql = "DELETE FROM period";
    jdbcTemplate.update(sql);
    return;
  }

  public boolean existsByStartTime(long startTime) {
    String sql = "SELECT count(*) FROM period WHERE start_time=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, startTime);
    return count != 0;
  }

  public Period getCurrent() {
    return getByTime(System.currentTimeMillis());
  }

  public Period getByTime(long time) {
    List<Period> currentPeriods = query(
        null, // Long startTime
        null, // Long numbering
        null, // String kind
        null, // Long minStartTime
        time, // Long maxStartTime
        null, // Boolean temp
        0, // offset
        Long.MAX_VALUE // count
    );
    return (currentPeriods.size() != 0 ? currentPeriods.get(currentPeriods.size() - 1) : null);
  }

  public Period getNextByTime(long time) {
    List<Period> currentPeriods = query(
        null, // Long startTime
        null, // Long numbering
        null, // String kind
        time, // Long minStartTime
        null, // Long maxStartTime
        null, // Boolean temp
        0, // offset
        1 // count
    );
    return (currentPeriods.size() != 0 ? currentPeriods.get(0) : null);
  }

  public List<Period> getIntersectingTime(long minSearchTime, long maxSearchTime) {
    List<Period> pds = query(
        null, // Long startTime,
        null, // Long numbering,
        null, // PeriodKind kind,
        minSearchTime, // Long minStartTime,
        maxSearchTime, // Long maxStartTime,
        null, // Boolean temp,
        0, // long offset,
        Integer.MAX_VALUE // long count
    );

    Period previous = getByTime(minSearchTime - 1);
    if (pds != null) {
      pds.add(previous);
    }

    return pds;
  }

  public List<Period> query(
      Long startTime,
      Long numbering,
      PeriodKind kind,
      Long minStartTime,
      Long maxStartTime,
      Boolean temp,
      long offset,
      long count) {
    String sql = "SELECT prd.start_time, prd.numbering, prd.kind, prd.temp FROM period prd"
        + " WHERE 1=1"
        + (startTime == null ? "" : " AND prd.start_time = " + startTime)
        + (numbering == null ? "" : " AND prd.numbering = " + numbering)
        + (kind == null ? "" : " AND prd.kind = " + Utils.escape(kind.name()))
        + (minStartTime == null ? "" : " AND prd.start_time >= " + minStartTime)
        + (maxStartTime == null ? "" : " AND prd.start_time <= " + maxStartTime)
        + (temp == null ? "" : " AND prd.temp = " + temp)
        + (" ORDER BY prd.start_time")
        + (" LIMIT " + count + " OFFSET " + offset)
        + ";";

    RowMapper<Period> rowMapper = new PeriodRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }
}
