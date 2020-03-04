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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = { "/dev/" })
public class DevelopmentController {

  @Autowired ApiKeyService apiKeyService;
  @Autowired CourseService courseService;
  @Autowired EncounterService encounterService;
  @Autowired GradeService gradeService;
  @Autowired IrregularityService irregularityService;
  @Autowired LocationService locationService;
  @Autowired PeriodService periodService;
  @Autowired ScheduleService scheduleService;
  @Autowired SemesterService semesterService;
  @Autowired SessionService sessionService;
  @Autowired StudentService studentService;
  @Autowired UserService userService;

  @Autowired
  InnexgoService innexgoService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  static final String rootEmail = "root@example.com";

  @RequestMapping("/signEveryoneOut/")
  public ResponseEntity<?> signEveryoneOut(
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isAdministrator(apiKey)) {
      innexgoService.signEveryoneOut();
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/issueAbsences/")
  public ResponseEntity<?> issueAbsences(
      @RequestParam("periodStartTime") Long periodStartTime,
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isAdministrator(apiKey)) {
      innexgoService.issueAbsences(periodStartTime);
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/initializeRoot/")
  public ResponseEntity<?> populateUsers() {
    if (userService.getAll().size() == 0) {
      User user = new User();
      user.name = "root";
      user.email = rootEmail;
      user.passwordHash = Utils.encodePassword("1234");
      user.ring = User.ADMINISTRATOR;
      userService.add(user);

      // Create apiKey
      ApiKey apiKey = new ApiKey();
      apiKey.userId = user.id;
      apiKey.creationTime = System.currentTimeMillis();
      apiKey.expirationTime = Long.MAX_VALUE;
      apiKey.key = "testlmao";
      apiKey.keyHash = Utils.encodeApiKey(apiKey.key);
      apiKeyService.add(apiKey);
      return new ResponseEntity<>(apiKey, HttpStatus.OK);
    }
    return BAD_REQUEST;
  }


  /* TESTING */
  @RequestMapping("/populateTestingPeriods/")
  public ResponseEntity<?> populateTestingPeriods() {
    long minute = 1 * 60 * 1000;
    long initialTime = System.currentTimeMillis() - 3 * minute;
    for (int i = 1; i < 7; i++) {
      Period period = new Period();
      period.startTime = initialTime + minute * i * 3;
      period.number = i;
      period.type = Period.CLASS_PERIOD;
      period.temp = true;
      periodService.add(period);
    }
    return OK;
  }

  // deletes periods with a length of less than 4 min
  @RequestMapping("/deleteTestingPeriods/")
  public ResponseEntity<?> deleteTestingPeriods(
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isAdministrator(apiKey)) {
      List<Period> periodList = periodService.query(null, // Long startTime,
          null, // Long number,
          null, // String type,
          null, // Long minStartTime,
          null, // Long maxStartTime,
          true, // Boolean temp,
          0, // long offset,
          Long.MAX_VALUE // long count
      );

      for (Period currentPeriod : periodList) {
        // delete irregularities, as they reference periods
        List<Irregularity> irregularities = irregularityService.query(

            null, // Long id,
            null, // Long studentId,
            null, // Long courseId,
            currentPeriod.startTime, // Long periodStartTime,
            null, // Long teacherId,
            null, // String type,
            null, // Long time,
            null, // Long minTime,
            null, // Long maxTime,
            0, // long offset
            Long.MAX_VALUE // long count
        );

        for (Irregularity irregularity : irregularities) {
          irregularityService.deleteById(irregularity.id);
        }
        periodService.deleteByStartTime(currentPeriod.startTime);
      }
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/populatePeriods/")
  public ResponseEntity<?> populatePeriods(
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isAdministrator(apiKey)) {

      periodService.deleteAll();
      LocalDate sunday = ZonedDateTime.now(Utils.TIMEZONE).toLocalDate().plusWeeks(-1).with(DayOfWeek.SUNDAY);

      // get weekdays
      LocalDate monday = sunday.plusDays(1);
      LocalDate tuesday = sunday.plusDays(2);
      LocalDate wednesday = sunday.plusDays(3);
      LocalDate thursday = sunday.plusDays(4);
      LocalDate friday = sunday.plusDays(5);

      for (int week = 0; week < 10; week++) {
        // collab
        LocalDate thisMonday = monday.plusWeeks(week);

        addPeriod(thisMonday, 101, Period.PASSING_PERIOD, "7:10");
        addPeriod(thisMonday, 1, Period.CLASS_PERIOD, "7:15");
        addPeriod(thisMonday, 102, Period.PASSING_PERIOD, "7:55");
        addPeriod(thisMonday, 2, Period.CLASS_PERIOD, "8:00");
        addPeriod(thisMonday, 103, Period.PASSING_PERIOD, "8:40");
        addPeriod(thisMonday, 3, Period.CLASS_PERIOD, "8:45");
        addPeriod(thisMonday, 204, Period.BREAK_PERIOD, "9:25");
        addPeriod(thisMonday, 104, Period.PASSING_PERIOD, "9:40");
        addPeriod(thisMonday, 4, Period.CLASS_PERIOD, "9:45");
        addPeriod(thisMonday, 300, Period.TUTORIAL_PERIOD, "10:25");
        addPeriod(thisMonday, 105, Period.PASSING_PERIOD, "10:55");
        addPeriod(thisMonday, 5, Period.CLASS_PERIOD, "11:00");
        addPeriod(thisMonday, 106, Period.LUNCH_PERIOD, "11:40");
        addPeriod(thisMonday, 106, Period.PASSING_PERIOD, "12:10");
        addPeriod(thisMonday, 6, Period.CLASS_PERIOD, "12:15");
        addPeriod(thisMonday, 107, Period.PASSING_PERIOD, "12:55");
        addPeriod(thisMonday, 7, Period.CLASS_PERIOD, "13:00");
        addPeriod(thisMonday, 0, Period.NO_PERIOD, "13:40");

        // S Day
        LocalDate thisTuesday = tuesday.plusWeeks(week);

        addPeriod(thisTuesday, 101, Period.PASSING_PERIOD, "7:10");
        addPeriod(thisTuesday, 1, Period.CLASS_PERIOD, "7:15");
        addPeriod(thisTuesday, 203, Period.BREAK_PERIOD, "8:55");
        addPeriod(thisTuesday, 103, Period.PASSING_PERIOD, "9:10");
        addPeriod(thisTuesday, 3, Period.CLASS_PERIOD, "9:15");
        addPeriod(thisTuesday, 205, Period.BREAK_PERIOD, "10:55");
        addPeriod(thisTuesday, 105, Period.PASSING_PERIOD, "11:10");
        addPeriod(thisTuesday, 5, Period.CLASS_PERIOD, "11:15");
        addPeriod(thisTuesday, 207, Period.LUNCH_PERIOD, "12:55");
        addPeriod(thisTuesday, 107, Period.PASSING_PERIOD, "13:25");
        addPeriod(thisTuesday, 7, Period.CLASS_PERIOD, "13:30");
        addPeriod(thisTuesday, 0, Period.NO_PERIOD, "15:10");

        LocalDate thisThursday = thursday.plusWeeks(week);

        addPeriod(thisThursday, 101, Period.PASSING_PERIOD, "7:10");
        addPeriod(thisThursday, 1, Period.CLASS_PERIOD, "7:15");
        addPeriod(thisThursday, 203, Period.BREAK_PERIOD, "8:55");
        addPeriod(thisThursday, 103, Period.PASSING_PERIOD, "9:10");
        addPeriod(thisThursday, 3, Period.CLASS_PERIOD, "9:15");
        addPeriod(thisThursday, 205, Period.BREAK_PERIOD, "10:55");
        addPeriod(thisThursday, 105, Period.PASSING_PERIOD, "11:10");
        addPeriod(thisThursday, 5, Period.CLASS_PERIOD, "11:15");
        addPeriod(thisThursday, 207, Period.LUNCH_PERIOD, "12:55");
        addPeriod(thisThursday, 107, Period.PASSING_PERIOD, "13:25");
        addPeriod(thisThursday, 7, Period.CLASS_PERIOD, "13:30");
        addPeriod(thisThursday, 0, Period.NO_PERIOD, "15:10");

        // T Day
        LocalDate thisWednesday = wednesday.plusWeeks(week);

        addPeriod(thisWednesday, 102, Period.PASSING_PERIOD, "7:55");
        addPeriod(thisWednesday, 2, Period.CLASS_PERIOD, "8:00");
        addPeriod(thisWednesday, 204, Period.BREAK_PERIOD, "9:40");
        addPeriod(thisWednesday, 104, Period.PASSING_PERIOD, "9:55");
        addPeriod(thisWednesday, 4, Period.CLASS_PERIOD, "10:00");
        addPeriod(thisWednesday, 300, Period.TUTORIAL_PERIOD, "11:40");
        addPeriod(thisWednesday, 206, Period.LUNCH_PERIOD, "12:30");
        addPeriod(thisWednesday, 106, Period.PASSING_PERIOD, "13:00");
        addPeriod(thisWednesday, 6, Period.CLASS_PERIOD, "13:05");
        addPeriod(thisWednesday, 0, Period.NO_PERIOD, "14:45");

        LocalDate thisFriday = friday.plusWeeks(week);

        addPeriod(thisFriday, 102, Period.PASSING_PERIOD, "7:55");
        addPeriod(thisFriday, 2, Period.CLASS_PERIOD, "8:00");
        addPeriod(thisFriday, 204, Period.BREAK_PERIOD, "9:40");
        addPeriod(thisFriday, 104, Period.PASSING_PERIOD, "9:55");
        addPeriod(thisFriday, 4, Period.CLASS_PERIOD, "10:00");
        addPeriod(thisFriday, 300, Period.TUTORIAL_PERIOD, "11:40");
        addPeriod(thisFriday, 206, Period.LUNCH_PERIOD, "12:30");
        addPeriod(thisFriday, 106, Period.PASSING_PERIOD, "13:00");
        addPeriod(thisFriday, 6, Period.CLASS_PERIOD, "13:05");
        addPeriod(thisFriday, 0, Period.NO_PERIOD, "14:45");
      }
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  void addPeriod(LocalDate day, int number, String type, String startTime) {
    String[] startComponents = startTime.split(":");
    Period period = new Period();
    period.number = number;
    period.type = type;
    period.startTime = day.atTime(Utils.parseInteger(startComponents[0]), Utils.parseInteger(startComponents[1]))
        .atZone(Utils.TIMEZONE).toInstant().toEpochMilli();
    periodService.add(period);
  }

  @RequestMapping("/populateSemesters")
  public ResponseEntity<?> populateSemesters(
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isAdministrator(apiKey)) {
      semesterService.deleteAll();
      for (int year = 2018; year < 2023; year++) {
        addSemester(Semester.SPRING_SEMESTER, LocalDate.of(year, 1, 1));
        addSemester(Semester.SUMMER_SEMESTER, LocalDate.of(year, 6, 1));
        addSemester(Semester.FALL_SEMESTER, LocalDate.of(year, 8, 1));
      }
      return OK;
    } else {
      return UNAUTHORIZED;
    }
  }

  void addSemester(String type, LocalDate date) {
    Semester semester = new Semester();
    semester.startTime = date.atTime(0, 0).atZone(Utils.TIMEZONE).toInstant().toEpochMilli();
    semester.year = date.getYear();
    semester.type = type;

    semesterService.add(semester);
  }
}
