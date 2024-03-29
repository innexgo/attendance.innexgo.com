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
  @Autowired InnexgoService innexgoService;

  static final String rootEmail = "root@example.com";

  @RequestMapping("/signEveryoneOut")
  public ResponseEntity<?> signEveryoneOut(
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ROOT.getResponse();
    }
    innexgoService.signEveryoneOut();
    return Errors.OK.getResponse();
  }

  @RequestMapping("/issueAbsences")
  public ResponseEntity<?> issueAbsences(
      @RequestParam("periodStartTime") Long periodStartTime,
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ROOT.getResponse();
    }
    innexgoService.issueAbsences(periodStartTime);
    return Errors.OK.getResponse();
  }

  @RequestMapping("/initializeRoot")
  public ResponseEntity<?> populateUsers() {
    if (userService.getAll().size() != 0) {
      return Errors.DATABASE_INITIALIZED.getResponse();
    }
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


  /* TESTING */
  @RequestMapping("/populateTestingPeriods")
  public ResponseEntity<?> populateTestingPeriods(
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ROOT.getResponse();
    }

    long minute = 1 * 60 * 1000;
    long initialTime = System.currentTimeMillis() - 3 * minute;
    for (int i = 1; i < 7; i++) {
      Period passingPeriod = new Period();
      passingPeriod.startTime = initialTime + minute * i * 3;
      passingPeriod.numbering = 100+i;
      passingPeriod.kind = PeriodKind.PASSING;
      passingPeriod.temp = true;
      periodService.add(passingPeriod);

      Period period = new Period();
      period.startTime = initialTime + minute * i * 3 + minute;
      period.numbering = i;
      period.kind = PeriodKind.CLASS;
      period.temp = true;
      periodService.add(period);
    }

    Period period = new Period();
    period.startTime = initialTime + minute * 8 * 3 + minute;
    period.numbering = 0;
    period.kind = PeriodKind.NONE;
    period.temp = true;
    periodService.add(period);

    // We need to restart this so that it works properly
    innexgoService.restartInsertAbsences();
    return Errors.OK.getResponse();
  }

  // deletes periods with a length of less than 4 min
  @RequestMapping("/deleteTestingPeriods")
  public ResponseEntity<?> deleteTestingPeriods(
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ROOT.getResponse();
    }
    List<Period> periodList = periodService.query(
        null, // Long startTime,
        null, // Long numbering,
        null, // String kind,
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
          null, // String kind,
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
    // We need to restart this so that it works properly
    innexgoService.restartInsertAbsences();
    return Errors.OK.getResponse();
  }

  @RequestMapping("/populatePeriods")
  public ResponseEntity<?> populatePeriods(
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ROOT.getResponse();
    }

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

      addPeriod(thisMonday, 101, PeriodKind.PASSING, "8:25");
      addPeriod(thisMonday, 1, PeriodKind.CLASS, "8:30");
      addPeriod(thisMonday, 102, PeriodKind.PASSING, "9:20");
      addPeriod(thisMonday, 2, PeriodKind.CLASS, "9:25");
      addPeriod(thisMonday, 204, PeriodKind.BREAK, "10:20");
      addPeriod(thisMonday, 103, PeriodKind.PASSING, "10:35");
      addPeriod(thisMonday, 3, PeriodKind.CLASS, "10:40");
      addPeriod(thisMonday, 104, PeriodKind.PASSING, "11:30");
      addPeriod(thisMonday, 4, PeriodKind.CLASS, "11:35");
      addPeriod(thisMonday, 106, PeriodKind.LUNCH, "12:25");
      addPeriod(thisMonday, 105, PeriodKind.PASSING, "12:55");
      addPeriod(thisMonday, 5, PeriodKind.CLASS, "13:00");
      addPeriod(thisMonday, 106, PeriodKind.PASSING, "13:50");
      addPeriod(thisMonday, 6, PeriodKind.CLASS, "13:55");
      addPeriod(thisMonday, 107, PeriodKind.PASSING, "14:45");
      addPeriod(thisMonday, 7, PeriodKind.CLASS, "14:50");
      addPeriod(thisMonday, 0, PeriodKind.NONE, "15:40");

      // S Day
      LocalDate thisTuesday = tuesday.plusWeeks(week);

      addPeriod(thisTuesday, 101, PeriodKind.PASSING, "8:25");
      addPeriod(thisTuesday, 1, PeriodKind.CLASS, "8:30");
      addPeriod(thisTuesday, 203, PeriodKind.BREAK, "10:05");
      addPeriod(thisTuesday, 103, PeriodKind.PASSING, "10:20");
      addPeriod(thisTuesday, 3, PeriodKind.CLASS, "10:25");
      addPeriod(thisTuesday, 205, PeriodKind.LUNCH, "12:00");
      addPeriod(thisTuesday, 105, PeriodKind.PASSING, "12:30");
      addPeriod(thisTuesday, 5, PeriodKind.CLASS, "12:35");
      addPeriod(thisTuesday, 207, PeriodKind.BREAK, "14:10");
      addPeriod(thisTuesday, 107, PeriodKind.PASSING, "14:25");
      addPeriod(thisTuesday, 7, PeriodKind.CLASS, "14:30");
      addPeriod(thisTuesday, 0, PeriodKind.NONE, "16:05");

      LocalDate thisThursday = thursday.plusWeeks(week);

      addPeriod(thisThursday, 101, PeriodKind.PASSING, "8:25");
      addPeriod(thisThursday, 1, PeriodKind.CLASS, "8:30");
      addPeriod(thisThursday, 203, PeriodKind.BREAK, "10:05");
      addPeriod(thisThursday, 103, PeriodKind.PASSING, "10:20");
      addPeriod(thisThursday, 3, PeriodKind.CLASS, "10:25");
      addPeriod(thisThursday, 205, PeriodKind.LUNCH, "12:00");
      addPeriod(thisThursday, 105, PeriodKind.PASSING, "12:30");
      addPeriod(thisThursday, 5, PeriodKind.CLASS, "12:35");
      addPeriod(thisThursday, 207, PeriodKind.BREAK, "14:10");
      addPeriod(thisThursday, 107, PeriodKind.PASSING, "14:25");
      addPeriod(thisThursday, 7, PeriodKind.CLASS, "14:30");
      addPeriod(thisThursday, 0, PeriodKind.NONE, "16:05");

      // T Day
      LocalDate thisWednesday = wednesday.plusWeeks(week);

      addPeriod(thisWednesday, 102, PeriodKind.PASSING, "8:25");
      addPeriod(thisWednesday, 2, PeriodKind.CLASS, "8:30");
      addPeriod(thisWednesday, 204, PeriodKind.BREAK, "10:05");
      addPeriod(thisWednesday, 104, PeriodKind.PASSING, "10:20");
      addPeriod(thisWednesday, 4, PeriodKind.CLASS, "10:25");
      addPeriod(thisWednesday, 300, PeriodKind.TUTORIAL, "12:00");
      addPeriod(thisWednesday, 206, PeriodKind.LUNCH, "12:50");
      addPeriod(thisWednesday, 106, PeriodKind.PASSING, "13:20");
      addPeriod(thisWednesday, 6, PeriodKind.CLASS, "13:25");
      addPeriod(thisWednesday, 0, PeriodKind.NONE, "15:00");

      LocalDate thisFriday = friday.plusWeeks(week);

      addPeriod(thisFriday, 102, PeriodKind.PASSING, "8:25");
      addPeriod(thisFriday, 2, PeriodKind.CLASS, "8:30");
      addPeriod(thisFriday, 204, PeriodKind.BREAK, "10:05");
      addPeriod(thisFriday, 104, PeriodKind.PASSING, "10:20");
      addPeriod(thisFriday, 4, PeriodKind.CLASS, "10:25");
      addPeriod(thisFriday, 300, PeriodKind.TUTORIAL, "12:00");
      addPeriod(thisFriday, 206, PeriodKind.LUNCH, "12:50");
      addPeriod(thisFriday, 106, PeriodKind.PASSING, "13:20");
      addPeriod(thisFriday, 6, PeriodKind.CLASS, "13:25");
      addPeriod(thisFriday, 0, PeriodKind.NONE, "15:00");
    }
    // We need to restart this so that it works properly
    innexgoService.restartInsertAbsences();
    return Errors.OK.getResponse();
  }

  void addPeriod(LocalDate day, int numbering, PeriodKind kind, String startTime) {
    String[] startComponents = startTime.split(":");
    Period period = new Period();
    period.numbering = numbering;
    period.kind = kind;
    period.startTime = day.atTime(Utils.parseInteger(startComponents[0]), Utils.parseInteger(startComponents[1]))
        .atZone(Utils.TIMEZONE).toInstant().toEpochMilli();
    periodService.add(period);
  }

  @RequestMapping("/populateSemesters")
  public ResponseEntity<?> populateSemesters(
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ROOT.getResponse();
    }
    semesterService.deleteAll();
    for (int year = 2018; year < 2028; year++) {
      addSemester(SemesterKind.SPRING, LocalDate.of(year, 1, 1));
      addSemester(SemesterKind.SUMMER, LocalDate.of(year, 6, 1));
      addSemester(SemesterKind.FALL, LocalDate.of(year, 8, 1));
    }
    return Errors.OK.getResponse();
  }

  void addSemester(SemesterKind kind, LocalDate date) {
    Semester semester = new Semester();
    semester.startTime = date.atTime(0, 0).atZone(Utils.TIMEZONE).toInstant().toEpochMilli();
    semester.year = date.getYear();
    semester.kind = kind;

    semesterService.add(semester);
  }
}
