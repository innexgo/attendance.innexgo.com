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
  public long id;
  long studentId;
  long courseId;
  long periodStartTime;
  public IrregularityType type;
  public long time;
  public long timeMissing;

  // for jackson
  public Student student;
  public Course course;
  public Period period;
}

enum IrregularityType {
  ABSENT,
  TARDY,
  LEAVE_NORETURN,
  LEAVE_RETURN,
  FORGET_SIGNOUT;

  public static boolean contains(String str) {
    for(IrregularityType irregularityType : IrregularityType.values()) {
      if(irregularityType.name().equals(str)) {
        return true;
      }
    }
    return false;
  }
}
