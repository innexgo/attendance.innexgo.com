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

public class Irregularity {

  static final String TYPE_ABSENT = "Absent";
  static final String TYPE_TARDY = "Tardy";
  static final String TYPE_LEFT_EARLY = "Left Early";
  static final String TYPE_LEFT_TEMPORARILY = "Left Temporarily";
  static final String TYPE_FORGOT_SIGN_OUT = "Forgot to Sign Out";

  public long id;
  long studentId;
  long courseId;
  long periodStartTime;
  public String type;
  public long time;
  public long timeMissing;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}
