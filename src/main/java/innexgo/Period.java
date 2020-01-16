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

public class Period {
  public static final String PASSING_PERIOD = "Passing Period";
  public static final String CLASS_PERIOD = "Class Period";
  public static final String BREAK_PERIOD = "Break";
  public static final String LUNCH_PERIOD = "Lunch";
  public static final String TUTORIAL_PERIOD = "Tutorial Period";
  public static final String NO_PERIOD = "No School In Session";

  // Primary Index is startTime
  public long startTime;
  public long number;
  public String type; // Must be one of the above defined strings
  // If it's a test. (For testing purposes)
  boolean temp;
}
