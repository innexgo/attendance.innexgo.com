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

public class Schedule {
  public long id;
  long studentId;
  long courseId;

  // This is for dropping and inserting into classes
  // If there is a nonzero start time
  public boolean hasStart;
  // Only defined if hasStart true
  public long startTime;
  // If there is a noninfinity end time
  public boolean hasEnd;
  // Only defined if hasEnd true
  public long endTime;

  // for jackson
  public Student student;
  public Course course;
}
