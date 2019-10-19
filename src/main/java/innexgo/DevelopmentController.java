package innexgo;

import java.time.*;
import java.util.*;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(value = {"/dev/"})
public class DevelopmentController {

  @Autowired ApiKeyService apiKeyService;
  @Autowired CardService cardService;
  @Autowired CourseService courseService;
  @Autowired EncounterService encounterService;
  @Autowired IrregularityService irregularityService;
  @Autowired LocationService locationService;
  @Autowired PeriodService periodService;
  @Autowired ScheduleService scheduleService;
  @Autowired SessionService sessionService;
  @Autowired StudentService studentService;
  @Autowired UserService userService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR =
      new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  /* TESTING */
  @RequestMapping("/populateTestingPeriods")
  public ResponseEntity<?> populateTestingPeriods() {
    LocalDate today = ZonedDateTime.now(Utils.TIMEZONE).toLocalDate();

    long minute = 1 * 60 * 1000;
    long initialTime = System.currentTimeMillis();
    for (int i = 3; i < 7; i++) {
      Period period = new Period();
      period.period = i;
      period.initialTime = initialTime;
      period.startTime = initialTime + minute;
      period.endTime = initialTime + minute * 3;
      initialTime += minute * 3;
      periodService.add(period);
    }
    return OK;
  }

  // deletes periods with a length of less than 10 min
  @RequestMapping("/deleteTestingPeriods")
  public ResponseEntity<?> deleteTestingPeriods() {
    long maxDuration = 10 * 60 * 1000;
    List<Period> periodList =
        periodService.query(
            null, // id
            null, // time
            null, // minDuration
            maxDuration, // maxDuration
            null, // initialTimeBegin
            null, // initialTimeEnd
            null, // startTimeBegin
            null, // startTimeEnd
            null, // endTimeBegin
            null, // endTimeEnd
            null, // period
            null, // courseId
            null // teacherId
            );
    for (Period period : periodList) {
      periodService.deleteById(period.id);
    }
    return OK;
  }

  @RequestMapping("/populatePeriods")
  public ResponseEntity<?> populatePeriods() {
    periodService.deleteAll();
    LocalDate sunday =
        ZonedDateTime.now(Utils.TIMEZONE).toLocalDate().plusWeeks(-1).with(DayOfWeek.SUNDAY);

    // get weekdays
    LocalDate monday = sunday.plusDays(1);
    LocalDate tuesday = sunday.plusDays(2);
    LocalDate wednesday = sunday.plusDays(3);
    LocalDate thursday = sunday.plusDays(4);
    LocalDate friday = sunday.plusDays(5);
    LocalDate saturday = sunday.plusDays(6);

    for (int week = 0; week < 10; week++) {
      // collab
      LocalDate thisMonday = monday.plusWeeks(week);
      addPeriod(thisMonday, 1, "6:00", "7:15", "7:55");
      addPeriod(thisMonday, 2, "7:55", "8:00", "8:40");
      addPeriod(thisMonday, 3, "8:40", "8:45", "9:25");
      addPeriod(thisMonday, 4, "9:25", "9:45", "10:25");
      addPeriod(thisMonday, 0, "10:25", "10:25", "10:55");
      addPeriod(thisMonday, 5, "10:55", "11:00", "11:40");
      addPeriod(thisMonday, 6, "11:40", "12:15", "12:55");
      addPeriod(thisMonday, 7, "12:55", "13:00", "13:40");

      // S Day
      LocalDate thisTuesday = tuesday.plusWeeks(week);
      addPeriod(thisTuesday, 1, "6:00", "7:15", "8:55");
      addPeriod(thisTuesday, 3, "8:55", "9:15", "10:55");
      addPeriod(thisTuesday, 5, "10:55", "11:15", "12:55");
      addPeriod(thisTuesday, 7, "12:55", "13:30", "15:10");

      LocalDate thisThursday = thursday.plusWeeks(week);
      addPeriod(thisThursday, 1, "6:00", "7:15", "8:55");
      addPeriod(thisThursday, 3, "8:55", "9:15", "10:55");
      addPeriod(thisThursday, 5, "10:55", "11:15", "12:55");
      addPeriod(thisThursday, 7, "12:55", "13:30", "15:10");

      // T Day
      LocalDate thisWednesday = wednesday.plusWeeks(week);
      addPeriod(thisWednesday, 2, "6:00", "8:00", "9:40");
      addPeriod(thisWednesday, 4, "9:40", "10:00", "11:40");
      addPeriod(thisWednesday, 0, "11:40", "11:40", "12:30");
      addPeriod(thisWednesday, 6, "12:30", "13:05", "14:45");

      LocalDate thisFriday = friday.plusWeeks(week);
      addPeriod(thisFriday, 2, "6:00", "8:00", "9:40");
      addPeriod(thisFriday, 4, "9:40", "10:00", "11:40");
      addPeriod(thisFriday, 0, "11:40", "11:40", "12:30");
      addPeriod(thisFriday, 6, "12:30", "13:05", "14:45");
    }
    return OK;
  }

  void addPeriod(LocalDate day, int p, String initialTime, String startTime, String endTime) {
    String[] initialComponents = initialTime.split(":");
    String[] startComponents = startTime.split(":");
    String[] endComponents = endTime.split(":");
    Period period = new Period();
    period.period = p;
    period.initialTime =
        day.atTime(
                Utils.parseInteger(initialComponents[0]), Utils.parseInteger(initialComponents[1]))
            .atZone(Utils.TIMEZONE)
            .toInstant()
            .toEpochMilli();
    period.startTime =
        day.atTime(Utils.parseInteger(startComponents[0]), Utils.parseInteger(startComponents[1]))
            .atZone(Utils.TIMEZONE)
            .toInstant()
            .toEpochMilli();
    period.endTime =
        day.atTime(Utils.parseInteger(endComponents[0]), Utils.parseInteger(endComponents[1]))
            .atZone(Utils.TIMEZONE)
            .toInstant()
            .toEpochMilli();
    periodService.add(period);
  }
}
