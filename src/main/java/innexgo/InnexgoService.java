package innexgo;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InnexgoService {

  @Autowired
  ApiKeyService apiKeyService;
  @Autowired
  CourseService courseService;
  @Autowired
  EncounterService encounterService;
  @Autowired
  GradeService gradeService;
  @Autowired
  IrregularityService irregularityService;
  @Autowired
  LocationService locationService;
  @Autowired
  PeriodService periodService;
  @Autowired
  ScheduleService scheduleService;
  @Autowired
  SemesterService semesterService;
  @Autowired
  SessionService sessionService;
  @Autowired
  StudentService studentService;
  @Autowired
  UserService userService;

  @Autowired
  SchedulerService schedulerService;

  Logger logger = LoggerFactory.getLogger(InnexgoService.class);

  /**
   * Fills in jackson objects (User) for ApiKey
   *
   * @param apiKey - ApiKey object
   * @return apiKey with filled jackson objects
   */
  ApiKey fillApiKey(ApiKey apiKey) {
    apiKey.user = fillUser(userService.getById(apiKey.userId));
    return apiKey;
  }

  /**
   * Fills in jackson objects(Teacher and Location) in Course
   *
   * @param course - Course object
   * @return Course object with filled jackson objects
   */
  Course fillCourse(Course course) {
    course.teacher = fillUser(userService.getById(course.teacherId));
    course.location = fillLocation(locationService.getById(course.locationId));
    return course;
  }

  /**
   * Fills in jackson objects(Location and Student) in Encounter
   *
   * @param encounter - Encounter object
   * @return Encounter object with filled jackson objects
   */
  Encounter fillEncounter(Encounter encounter) {
    encounter.location = fillLocation(locationService.getById(encounter.locationId));
    encounter.student = fillStudent(studentService.getById(encounter.studentId));
    return encounter;
  }

  /**
   * Fills in jackson objects(Semester and Student) in Grade
   *
   * @param grade - Grade object
   * @return Grade object with filled jackson objects
   */
  Grade fillGrade(Grade grade) {
    grade.student = fillStudent(studentService.getById(grade.studentId));
    grade.semester = fillSemester(semesterService.getByStartTime(grade.semesterStartTime));
    return grade;
  }

  /**
   * Fills in jackson objects(Course, Period, and Student) in Irregularity
   *
   * @param irregularity - Irregularity object
   * @return Irregularity object with filled jackson objects
   */
  Irregularity fillIrregularity(Irregularity irregularity) {
    irregularity.course = fillCourse(courseService.getById(irregularity.courseId));
    irregularity.period = fillPeriod(periodService.getByStartTime(irregularity.periodStartTime));
    irregularity.student = fillStudent(studentService.getById(irregularity.studentId));
    return irregularity;
  }

  /**
   * Fills in jackson objects (none at the moment) for Location
   *
   * @param location - Location object
   * @return Location object with filled jackson objects
   */
  Location fillLocation(Location location) {
    return location;
  }

  /**
   * Fills in jackson objects(Course, Semester) for Offering
   *
   * @param offering - Offering object
   * @return Offering object with filled jackson objects
   */
  Offering fillOffering(Offering offering) {
    offering.course = fillCourse(courseService.getById(offering.courseId));
    offering.semester = fillSemester(semesterService.getByStartTime(offering.semesterStartTime));
    return offering;
  }

  /**
   * Fills in jackson objects (none at the moment) for Period
   *
   * @param period - Period object
   * @return Period object with filled jackson objects
   */
  Period fillPeriod(Period period) {
    return period;
  }

  /**
   * Fills in jackson objects(Student, Course) for Schedule
   *
   * @param schedule - Schedule object
   * @return Schedule object with filled jackson objects
   */
  Schedule fillSchedule(Schedule schedule) {
    schedule.student = fillStudent(studentService.getById(schedule.studentId));
    schedule.course = fillCourse(courseService.getById(schedule.courseId));
    return schedule;
  }

  /**
   * Fills in jackson objects (none at the moment) for Semester
   *
   * @param semester - Semester object
   * @return Semester object with filled jackson objects
   */
  Semester fillSemester(Semester semester) {
    return semester;
  }

  /**
   * Fills in jackson objects (Student, Course, inEncounter, and outEncounter) for
   * Session
   *
   *
   * @param session - Session object
   * @return Session object with filled jackson objects
   */
  Session fillSession(Session session) {
    session.inEncounter = fillEncounter(encounterService.getById(session.inEncounterId));
    if (session.outEncounterId != null) {
      session.outEncounter = fillEncounter(encounterService.getById(session.outEncounterId));
    }
    return session;
  }

  /**
   * Fills in jackson objects (none at the moment) for Student
   *
   * @param student - Student object
   * @return Student object with filled jackson objects
   */
  Student fillStudent(Student student) {
    return student;
  }

  /**
   * Fills in jackson objects (none at the moment) for User
   *
   * @param user - User object
   * @return User object with filled jackson objects
   */
  User fillUser(User user) {
    return user;
  }

  /**
   * Returns a user if valid
   *
   * @param key - apikey code of the User
   * @return User or null if invalid
   */
  User getUserIfValid(String key) {
    String hash = Utils.encodeApiKey(key);
    if (apiKeyService.existsByKeyHash(hash)) {
      ApiKey apiKey = apiKeyService.getByKeyHash(hash);
      if (apiKey.expirationTime > System.currentTimeMillis()) {
        if (userService.existsById(apiKey.userId)) {
          return userService.getById(apiKey.userId);
        }
      }
    }
    return null;
  }

  /**
   * Checks if a user is an administrator
   *
   * @param key - apikey code of a User
   * @return true if administrator; false if not administrator or invalid
   */
  boolean isAdministrator(String key) {
    if (key == null) {
      return false;
    }
    User user = getUserIfValid(key);
    return user != null && (user.ring == User.ADMINISTRATOR);
  }

  /**
   * Checks whether a User is trusted
   *
   * @param key - apikey code of User
   * @return true if User is trusted; false if User not trusted
   */
  boolean isTrusted(String key) {
    if (key == null) {
      return false;
    }
    User user = getUserIfValid(key);
    return user != null && (user.ring <= User.TEACHER);
  }

  /**
   * Each day at midnight, log everyone out and give a "forgot to sign out"
   * irregularity if they had
   * a session open
   */
  @Scheduled(fixedDelay = 5000)
  public void signOutAtMidnight() {
    logger.info("Starting sign out at midnight process");
    while (true) {
      try {
        ZonedDateTime now = ZonedDateTime.now(Utils.TIMEZONE);
        ZonedDateTime tomorrowStart = now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        long millisTillMidnight = Duration.between(now, tomorrowStart).toMillis();
        logger.info("Next mass sign out at: " + millisTillMidnight / 1000);
        Thread.sleep(millisTillMidnight);
      } catch (Exception e) {
        // We will bail pretty regularly whenever someone updates the course and decides
        // to kill
        return;
      }
      signEveryoneOut();
    }
  }

  public void signEveryoneOut() {
    long currentTime = System.currentTimeMillis();

    logger.info("Signing everyone out");
    // get list of open sessions
    List<Session> openSessionList = sessionService.query(
        null, // Long id
        null, // Long inEncounterId
        null, // Long outEncounterId
        null, // Long anyEncounterId
        false, // Boolean complete
        null, // Long studentId
        null, // Long locationId
        null, // Long time
        null, // Long inTimeBegin
        null, // Long inTimeEnd
        null, // Long outTimeBegin
        null, // Long outTimeEnd
        0, // long offset
        Long.MAX_VALUE // long count
    );

    for (Session openSession : openSessionList) {
      // Virtually close session by generating a fake (virtual) encounter and insert
      // it in.
      // We know they must have somehow left from here

      // grab old encounter
      Encounter inEncounter = encounterService.getById(openSession.inEncounterId);
      // make a virtual out encounter
      Encounter outEncounter = new Encounter();
      outEncounter.locationId = inEncounter.locationId;
      outEncounter.studentId = inEncounter.studentId;
      outEncounter.time = currentTime;
      outEncounter.kind = EncounterKind.VIRTUAL;
      encounterService.add(outEncounter);

      // now close session
      openSession.outEncounterId = outEncounter.id;
      sessionService.update(openSession);

      // After the person signed out the first time, they might have been logged in at
      // a bunch of classes afterwards
      // We give them the forgot to sign out irregularity at the course which they
      // signed in to
      // period and course are that of the first period with a course that the session
      // intersected
      List<Period> intersectedPeriods = periodService.query(
          null, // Long startTime
          null, // Long numbering
          PeriodKind.CLASS, // String kind
          periodService.getByTime(inEncounter.time).startTime, // Long minStartTime
          outEncounter.time, // Long maxStartTime
          null, // Boolean temp
          0, // long offset
          Long.MAX_VALUE // long count
      );

      // Find first period with a course at this location
      for (Period irregPeriod : intersectedPeriods) {
        List<Course> courses = courseService.query(
            null, // Long id,
            null, // Long teacherId,
            inEncounter.locationId, // Long locationId,
            null, // Long studentId,
            irregPeriod.numbering, // Long periodNumber,
            null, // String subject,
            semesterService.getCurrent().startTime, // Long semesterStartTime,
            0, // long offset,
            Long.MAX_VALUE // long count
        );
        // Give forgot to sign out error to each course at this period at this location
        for (Course irregCourse : courses) {
          Irregularity forgotToSignOut = new Irregularity();
          forgotToSignOut.studentId = inEncounter.studentId;
          forgotToSignOut.courseId = irregCourse.id;
          forgotToSignOut.periodStartTime = irregPeriod.startTime;
          forgotToSignOut.kind = IrregularityKind.FORGOT_SIGN_OUT;
          forgotToSignOut.time = currentTime;
          forgotToSignOut.timeMissing = 0;
          irregularityService.add(forgotToSignOut);
          break;
        }
        // We only want to issue this forgot to sign out error to the FIRST courses that
        // would have intersected
        // Thus, we break if a course was found
        if (courses.size() > 0) {
          break;
        }
      }
    }

  }

  public void restartInsertAbsences() {
    schedulerService.restartTask("insertAbsences");
  }

  /**
   * For all the courses at the current time, create irregularities { If the
   * student has not signed
   * in yet before or during the period { generate an absent irregularity } }
   */
  @Scheduled(fixedDelay = 5000)
  public void insertAbsences() {
    logger.info("INNEXGO: Starting insert absences process");
    // the list of periods that havent started yet
    List<Period> periodList = periodService.query(
        null, // Long startTime,
        null, // Long numbering,
        null, // String kind,
        System.currentTimeMillis(), // Long minStartTime,
        null, // Long maxStartTime,
        null, // Boolean temp
        0,
        Long.MAX_VALUE);
    for (int i = 0; i < periodList.size(); i++) {
      Period period = periodList.get(i);
      // wait till we are at the right time
      try {
        long timeToSleep = Math.max(0, period.startTime - System.currentTimeMillis());
        logger.info("Inserting absences in: " + timeToSleep / 1000 + " seconds");
        Thread.sleep(timeToSleep);
      } catch (InterruptedException e) {
        logger.info("insertAbsences thread INTERRUPTED, bailing");
        return;
      }
      issueAbsences(period.startTime);
    }
  }

  public void issueAbsences(long periodStartTime) {
    logger.info("Period " + periodStartTime + " started, inserting absences");
    // get courses at this period
    List<Course> courseList = courseService.getByPeriodStartTime(periodStartTime);

    // for all courses at this time
    for (Course course : courseList) {
      // subtract present students from all students taking the course
      Set<Long> presentStudentIds = studentService.present(course.locationId, periodStartTime)
          .stream()
          .map(s -> s.id)
          .collect(Collectors.toSet());

      List<Student> absentees = studentService.registeredForCourse(course.id, periodStartTime)
          .stream()
          .filter(s -> !presentStudentIds.contains(s.id))
          .collect(Collectors.toList());

      // mark all students not there as absent
      for (Student student : absentees) {
        // Check if already absent. if not, don't add
        boolean alreadyAbsent = irregularityService.query(
            null, // Long id
            student.id, // Long studentId
            course.id, // Long courseId
            periodStartTime, // Long periodStartTime
            null, // Long teacherId
            IrregularityKind.ABSENT, // String kind
            null, // Long time
            null, // Long minTime
            null, // Long maxTime
            0, // long count
            Long.MAX_VALUE // long count
        ).size() > 0;
        // if not already absent
        if (!alreadyAbsent) {
          Irregularity irregularity = new Irregularity();
          irregularity.studentId = student.id;
          irregularity.courseId = course.id;
          irregularity.periodStartTime = periodStartTime;
          irregularity.kind = IrregularityKind.ABSENT;
          irregularity.time = periodStartTime;
          irregularity.timeMissing = periodService
              .getNextByTime(periodStartTime + 1).startTime - periodStartTime;
          irregularityService.add(irregularity);
        }
      }
    }

  }

  public Session attends(long studentId, long locationId, boolean manual) {
    Session returnableSession = null;

    // Get this student
    Student student = studentService.getById(studentId);

    // Put the encounter in the database
    Encounter encounter = new Encounter();
    encounter.locationId = locationId;
    encounter.studentId = student.id;
    encounter.time = System.currentTimeMillis();
    encounter.kind = manual
        ? EncounterKind.MANUAL
        : EncounterKind.DEFAULT;
    encounterService.add(encounter);

    // if school is currently going on, represents the current period
    Period currentPeriod = periodService.getCurrent();
    Semester currentSemester = semesterService.getCurrent();

    // if there is currently a course going on, represents the current course
    List<Course> currentCourses = courseService.query(
        null, // Long id
        null, // Long teacherId
        locationId, // Long locationId
        studentId, // Long studentId
        currentPeriod.numbering, // Long period
        null, // String subject
        currentSemester.startTime, // Long semesterStartTime
        0, // long offset
        1 // long count
    );

    Course currentCourse = currentCourses.isEmpty() ? null : currentCourses.get(0);

    // Get incomplete sessions
    List<Session> openSessionList = sessionService.query(
        null, // Long id
        null, // Long inEncounterId
        null, // Long outEncounterId
        null, // Long anyEncounterId
        false, // Boolean complete
        student.id, // Long studentId
        null, // Long locationId
        null, // Long time
        null, // Long inTimeBegin
        null, // Long inTimeEnd
        null, // Long outTimeBegin
        null, // Long outTimeEnd
        0, // long count
        Long.MAX_VALUE // long count
    );

    // If the encounter will be used to start a new session since there are no open
    // ones
    boolean usedToClose = false;

    for (Session openSession : openSessionList) {
      Encounter inEncounter = encounterService.getById(openSession.inEncounterId);
      // if it's at the same location
      if (locationId == inEncounter.locationId) {
        // Then close this session naturally
        openSession.outEncounterId = encounter.id;
        sessionService.update(openSession);

        // Will result in the last open session being the one returned
        returnableSession = openSession;

        usedToClose = true;

        // if it is in the middle of class, add a leaveEarly irregularity
        if (currentCourse != null) {
          Irregularity irregularity = new Irregularity();
          irregularity.studentId = student.id;
          irregularity.courseId = currentCourse.id;
          irregularity.periodStartTime = currentPeriod.startTime;
          // if before the period has actually started, make absent instead of left early
          irregularity.kind = IrregularityKind.LEAVE_NORETURN;
          irregularity.time = encounter.time;
          Period next = periodService.getNextByTime(encounter.time);
          if (next != null) {
            irregularity.timeMissing = next.startTime - encounter.time;
          } else {
            irregularity.timeMissing = 0;
          }
          irregularityService.add(irregularity);
        }
      } else {
        // its not at the same location as the beginning
        // Virtually close session by generating a fake (virtual) encounter and insert
        // it in.
        // We know they must have somehow left from here

        // make a virtual out encounter
        Encounter outEncounter = new Encounter();
        outEncounter.locationId = inEncounter.locationId;
        outEncounter.studentId = inEncounter.studentId;
        outEncounter.time = encounter.time;
        outEncounter.kind = EncounterKind.VIRTUAL;
        encounterService.add(outEncounter);

        // now close session
        openSession.outEncounterId = outEncounter.id;
        sessionService.update(openSession);

        // After the person signed out the first time, they might have been logged in at
        // a bunch of classes afterwards
        // We give them the forgot to sign out irregularity at the course which they
        // signed in to
        // period and course are that of the first period with a course that the session
        // intersected
        List<Period> intersectedPeriods = periodService.query(
            null, // Long startTime
            null, // Long numbering
            PeriodKind.CLASS, // String kind
            periodService.getByTime(encounter.time).startTime, // Long minStartTime
            outEncounter.time, // Long maxStartTime
            null, // Boolean temp
            0,
            Long.MAX_VALUE);

        // Find first period with a course at this location
        Period irregPeriod = null;
        Course irregCourse = null;
        for (Period period : intersectedPeriods) {
          List<Course> courses = courseService.getByPeriodStartTime(period.startTime);
          if (courses.size() > 0) {
            irregPeriod = period;
            irregCourse = courses.get(0);
          }
        }

        if (irregPeriod != null && irregCourse != null) {
          // Now add irregularity about forgetting to sign out
          Irregularity forgotToSignOut = new Irregularity();
          forgotToSignOut.studentId = inEncounter.studentId;
          forgotToSignOut.courseId = irregCourse.id;
          forgotToSignOut.periodStartTime = irregPeriod.startTime;
          forgotToSignOut.kind = IrregularityKind.FORGOT_SIGN_OUT;
          forgotToSignOut.time = irregPeriod.startTime;
          forgotToSignOut.timeMissing = 0;
          irregularityService.add(forgotToSignOut);
        }
      }
    }

    // If the encounter wasn't used to close, we must make a new one
    if (!usedToClose) {
      // make new open session
      Session session = new Session();
      session.inEncounterId = encounter.id;
      session.outEncounterId = null;
      sessionService.add(session);

      // This is the session we give back
      returnableSession = session;

      if (currentCourse != null) {
        // now we check if they arent there, and fix it
        List<Irregularity> irregularities = irregularityService.query(
            null, // Long id
            studentId, // Long studentId
            currentCourse.id, // Long courseId
            currentPeriod.startTime, // Long periodStartTime
            null, // Long teacherId
            null, // String kind
            null, // Long time
            null, // Long minTime
            null, // Long maxTime
            0, // long offset
            Long.MAX_VALUE // long count
        );

        for (Irregularity irregularity : irregularities) {
          if (irregularity.kind.equals(IrregularityKind.ABSENT)) {
            // if there is absence, convert it to a tardy or delete it
            if (System.currentTimeMillis() > currentPeriod.startTime) {
              irregularity.kind = IrregularityKind.TARDY;
              irregularity.timeMissing = encounter.time - currentPeriod.startTime;
              irregularity.time = encounter.time;
              irregularityService.update(irregularity);
            } else {
              // if they're present before the startTime
              irregularityService.deleteById(irregularity.id);
            }
          } else if (irregularity.kind.equals(IrregularityKind.LEAVE_NORETURN)) {
            // if there is a leftEarly, convert it to a leftTemporarily
            irregularity.kind = IrregularityKind.LEAVE_RETURN;
            irregularity.timeMissing = encounter.time - irregularity.time;
            irregularityService.update(irregularity);
          }
        }
      }
    }
    return returnableSession;
  }
}
