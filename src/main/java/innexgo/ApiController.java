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

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.csv.*;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.ByteArrayOutputStream;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api"})
public class ApiController {

  Logger logger = LoggerFactory.getLogger(ApiController.class);

  @Autowired ApiKeyService apiKeyService;
  @Autowired CourseService courseService;
  @Autowired EncounterService encounterService;
  @Autowired GradeService gradeService;
  @Autowired IrregularityService irregularityService;
  @Autowired LocationService locationService;
  @Autowired OfferingService offeringService;
  @Autowired PeriodService periodService;
  @Autowired ScheduleService scheduleService;
  @Autowired SemesterService semesterService;
  @Autowired SessionService sessionService;
  @Autowired StudentService studentService;
  @Autowired UserService userService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR =
    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

  ResponseEntity<?> pdfToResponse(PDDocument pdf) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
          pdf.save(bos);
        } catch(Exception e){
          e.printStackTrace();
          return INTERNAL_SERVER_ERROR;
        }

        byte[] contents = bos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
  }


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
   * Fills in jackson objects (Student, Course, inEncounter, and outEncounter) for Session
   *
   *
   * @param session - Session object
   * @return Session object with filled jackson objects
   */
  Session fillSession(Session session) {
    session.inEncounter = fillEncounter(encounterService.getById(session.inEncounterId));
    if (session.complete) {
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
   * Each day at midnight, log everyone out and give a "forgot to sign out" irregularity if they had
   * a session open
   */
  @Scheduled(fixedDelay = 5000)
  public void signOutAtMidnight() {
    System.out.println("Starting sign out at midnight process");
    while (true) {
      try {
        ZonedDateTime now = ZonedDateTime.now(Utils.TIMEZONE);
        ZonedDateTime tomorrowStart = now.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        long millisTillMidnight = Duration.between(now, tomorrowStart).toMillis();
        logger.info("Next mass sign out at: " + millisTillMidnight / 1000);
        Thread.sleep(millisTillMidnight);
      } catch (Exception e) {
        e.printStackTrace();
      }

      logger.info("Signing everyone out");
      // get list of open sessions
      List<Session> openSessionList =
        sessionService.query(
            null,           //  Long id
            null,           //  Long inEncounterId
            null,           //  Long outEncounterId
            null,           //  Long anyEncounterId
            false,          //  Boolean complete
            null,           //  Long studentId
            null,           //  Long locationId
            null,           //  Long time
            null,           //  Long inTimeBegin
            null,           //  Long inTimeEnd
            null,           //  Long outTimeBegin
            null,           //  Long outTimeEnd
            0,              //  long offset
            Long.MAX_VALUE  //  long count
            );

      for (Session openSession : openSessionList) {
        // Virtually close session by generating a fake (virtual) encounter and insert it in.
        // We know they must have somehow left from here

        // grab old encounter
        Encounter inEncounter = encounterService.getById(openSession.inEncounterId);
        // make a virtual out encounter
        Encounter outEncounter = new Encounter();
        outEncounter.locationId = inEncounter.locationId;
        outEncounter.studentId = inEncounter.studentId;
        outEncounter.time = System.currentTimeMillis();
        outEncounter.type = Encounter.VIRTUAL_ENCOUNTER;
        encounterService.add(outEncounter);

        // now close session
        openSession.outEncounterId = outEncounter.id;
        openSession.complete = true;
        sessionService.update(openSession);

        // After the person signed out the first time, they might have been logged in at a bunch of classes afterwards
        // We give them the forgot to sign out irregularity at the course which they signed in to
        // period and course are that of the first period with a course that the session intersected
        List<Period> intersectedPeriods =
          periodService.query(
              null,                                                // Long startTime
              null,                                                // Long number
              Period.CLASS_PERIOD,                                 // String type
              periodService.getByTime(inEncounter.time).startTime, // Long minStartTime
              outEncounter.time,                                   // Long maxStartTime
              0,                                                   //  long offset
              Long.MAX_VALUE                                       //  long count
              );

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
          forgotToSignOut.type = Irregularity.TYPE_FORGOT_SIGN_OUT;
          forgotToSignOut.time = irregPeriod.startTime;
          forgotToSignOut.timeMissing = 0;
          irregularityService.add(forgotToSignOut);
        }
      }
    }
  }

  /**
   * For all the courses at the current time, create irregularities { If the student has not signed
   * in yet before or during the period { generate an absent irregularity } }
   */
  @Scheduled(fixedDelay = 5000)
  public void insertAbsences() {
    logger.info("INNEXGO: Starting insert absences process");
    // the list of periods that havent started yet
    List<Period> periodList =
      periodService.query(
          null,                       // Long startTime,
          null,                       // Long number,
          null,                       // String type,
          System.currentTimeMillis(), // Long minStartTime,
          null,                        // Long maxStartTime
          0,
          Long.MAX_VALUE
          );

    for (int i = 0; i < periodList.size(); i++) {
      Period period = periodList.get(i);
      // wait till we are at the right time
      try {
        long timeToSleep = Math.max(0, period.startTime - System.currentTimeMillis());
        logger.info("Inserting absences in: " +  timeToSleep / 1000 + " seconds");
        Thread.sleep(timeToSleep);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      logger.info("Period " + period.startTime + " started, inserting absences");
      // get courses at this period
      List<Course> courseList = courseService.getByPeriodStartTime(period.startTime);

      // for all courses at this time
      for (Course course : courseList) {
        // subtract present students from all students taking the course
        List<Student> absentees = studentService.registeredForCourse(course.id, period.startTime);
        absentees.removeAll(studentService.present(course.locationId, period.startTime));

        // mark all students not there as absent
        for (Student student : absentees) {
          // Check if already absent. if not, don't add
          boolean alreadyAbsent = irregularityService.query(
              null,                     //  Long id
              student.id,               //  Long studentId
              null,                     //  Long courseId
              period.startTime,         //  Long periodStartTime
              null,                     //  Long teacherId
              Irregularity.TYPE_ABSENT, //  String type
              null,                     //  Long time
              null,                     //  Long minTime
              null,                     //  Long maxTime
              null,                     //  Long timeMissing
              0,                        //  long count
              Long.MAX_VALUE            //  long count
              ).size() > 0;
          // if not already absent
          if (!alreadyAbsent) {
            Irregularity irregularity = new Irregularity();
            irregularity.studentId = student.id;
            irregularity.courseId = course.id;
            irregularity.periodStartTime = period.startTime;
            irregularity.type = Irregularity.TYPE_ABSENT;
            irregularity.time = period.startTime;
            irregularity.timeMissing = periodService
              .getNextByTime(period.startTime+1)
              .startTime - period.startTime;
            irregularityService.add(irregularity);
          }
        }
      }
    }
  }

  /**
   * Create a new apiKey for a User
   *
   * @param userId the id of the User
   * @param email email of the User
   * @param expirationTime time in milliseconds since 1970 when this key is due to expire
   * @param password User password
   * @return ResponseEntity with ApiKey of User and HttpStatus.OK code if successful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is unauthorized
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process is unsuccessful
   */
  @RequestMapping("/apiKey/new/")
  public ResponseEntity<?> newApiKey(
      @RequestParam(value = "userId", defaultValue = "-1") Long userId,
      @RequestParam(value = "email", defaultValue = "") String email,
      @RequestParam("expirationTime") Long expirationTime,
      @RequestParam("password") String password) {

      // if they gave a username instead of a userId
      if (userId == -1 && !Utils.isEmpty(email)) {

        // if the email is registered
        if (userService.existsByEmail(email)) {
          // get email
          userId = userService.getByEmail(email).id;
        }
      }

      // now actually make apiKey
      if (userService.existsById(userId)) {
        User u = userService.getById(userId);
        if (Utils.matchesPassword(password, u.passwordHash)) {
          ApiKey apiKey = new ApiKey();
          apiKey.userId = userId;
          apiKey.creationTime = System.currentTimeMillis();
          apiKey.expirationTime = expirationTime;
          apiKey.key = Utils.generateKey();
          apiKey.keyHash = Utils.encodeApiKey(apiKey.key);
          apiKeyService.add(apiKey);
          return new ResponseEntity<>(fillApiKey(apiKey), HttpStatus.OK);
        } else {
          return UNAUTHORIZED;
        }
      }
      return BAD_REQUEST;
      }


  /**
   * Create a new course and can only be done by an administrator
   *
   * @param teacherId - id of teacher that normally teaches the class
   * @param locationId - id of location where class is normally held
   * @param period - period of the class (usually 1-7)
   * @param subject - Subject of class, Ex. "Math 3"
   * @param apiKey - of User creating new Course
   * @return ResponseEntity with a Course and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not an administrator
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if the process if unsuccessful
   */
  @RequestMapping("/course/new/")
  public ResponseEntity<?> newCourse(
      @RequestParam("userId") Long teacherId,
      @RequestParam("locationId") Long locationId,
      @RequestParam("period") Long period,
      @RequestParam("subject") String subject,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (!Utils.isEmpty(subject)
          && locationService.existsById(locationId)
          && userService.existsById(teacherId)) {
        Course course = new Course();
        course.teacherId = teacherId;
        course.locationId = locationId;
        course.period = period;
        course.subject = subject;
        courseService.add(course);
        // return the filled course on success
        return new ResponseEntity<>(fillCourse(course), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  /**
   * Creates a new encounter and can be done by a trusted user
   *
   * @param studentId - student id number
   * @param locationId - location id of the location where this course is normally taught
   * @param courseId - course id
   * @param apiKey - apiKey of the user creating the encounter
   * @return ResponseEntity with Encounter and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if process is unsuccessful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not trusted
   */
  @RequestMapping("/encounter/new/")
  public ResponseEntity<?> newEncounter(
      @RequestParam("studentId") Long studentId,
      @RequestParam("locationId") Long locationId,
      @RequestParam("manual") Boolean manual,
      @RequestParam("apiKey") String apiKey) {
    if (isTrusted(apiKey)) {
      if (locationService.existsById(locationId)
          && studentService.existsById(studentId)) {
        Encounter encounter = new Encounter();
        encounter.locationId = locationId;
        encounter.studentId = studentId;
        encounter.time = System.currentTimeMillis();
        encounter.type = manual
          ? Encounter.MANUAL_ENCOUNTER
          : Encounter.DEFAULT_ENCOUNTER;
        encounterService.add(encounter);
        return new ResponseEntity<>(fillEncounter(encounter), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/grade/new/")
  public ResponseEntity<?> newGrade(
      @RequestParam("studentId") Long studentId,
      @RequestParam("semesterStartTime") Long semesterStartTime,
      @RequestParam("number") Long number,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (studentService.existsById(studentId)
          && semesterService.existsByStartTime(semesterStartTime)
          && !gradeService.existsByStudentIdSemesterStartTime(studentId, semesterStartTime)) {
        Grade grade = new Grade();
        grade.studentId = studentId;
        grade.semesterStartTime = semesterStartTime;
        grade.number = number;
        gradeService.add(grade);
        return new ResponseEntity<>(fillGrade(grade), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  /**
   * Creates a new location and can only be done by an Administrator
   *
   * @param name name of location, eg. "Room 105"
   * @param apiKey - apiKey of the User creating the location
   * @return ResponseEntity with location and HttpStatus.OK
   * @throws ResponseEntity with HttpStatus.BAD_REQUEST if process if unsuccessful
   * @throws ResponseEntity with HttpStatus.UNAUTHORIZED if the User is not an administrator
   */
  @RequestMapping("/location/new/")
  public ResponseEntity<?> newLocation(
      @RequestParam("locationId") Long locationId,
      @RequestParam("name") String name,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (!locationService.existsById(locationId)
          && !Utils.isEmpty(name)) {
        Location location = new Location();
        location.id = locationId;
        location.name = name;
        locationService.add(location);
        return new ResponseEntity<>(fillLocation(location), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/offering/new/")
  public ResponseEntity<?> newOffering(
      @RequestParam("semesterStartTime") Long semesterStartTime,
      @RequestParam("courseId") Long courseId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (semesterService.existsByStartTime(semesterStartTime)
          && courseService.existsById(courseId)
          && offeringService.getOfferingBySemesterStartTimeCourseId(
              semesterStartTime,
              courseId)
            == null) {
        Offering offering = new Offering();
        offering.semesterStartTime = semesterStartTime;
        offering.courseId = courseId;
        offeringService.add(offering);
        return new ResponseEntity<>(fillOffering(offering), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/period/new/")
  public ResponseEntity<?> newPeriod(
      @RequestParam("startTime") Long startTime,
      @RequestParam("number") Long number,
      @RequestParam("type") String type,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      // TODO verify that type is one of the predefined types
      Period pr = new Period();
      pr.startTime = startTime;
      pr.number = number;
      pr.type = type;
      periodService.add(pr);
      return new ResponseEntity<>(fillPeriod(pr), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/schedule/new/")
  public ResponseEntity<?> newSchedule(
      @RequestParam("studentId") Long studentId,
      @RequestParam("courseId") Long courseId,
      @RequestParam(value="startTime", defaultValue="-1") Long startTime,
      @RequestParam(value="endTime", defaultValue="-1") Long endTime,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (studentService.existsById(studentId)
          && courseService.existsById(courseId)
          && scheduleService.getScheduleByStudentIdCourseId(studentId, courseId) == null) {
        Schedule schedule = new Schedule();
        schedule.studentId = studentId;
        schedule.courseId = courseId;
        schedule.hasStart = startTime >= 0;
        if(schedule.hasStart) {
          schedule.startTime = startTime;
        }
        schedule.hasEnd = endTime >= 0;
        if(schedule.hasEnd) {
          schedule.endTime = endTime;
        }
        scheduleService.add(schedule);
        return new ResponseEntity<>(fillSchedule(schedule), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/student/new/")
  public ResponseEntity<?> newStudent(
      @RequestParam("studentId") Long studentId,
      @RequestParam("name") String name,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (!studentService.existsById(studentId)
          && !Utils.isEmpty(name)) {
        Student student = new Student();
        student.id = studentId;
        student.name = name.toUpperCase();
        studentService.add(student);
        return new ResponseEntity<>(fillStudent(student), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userName") String name,
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam("ring") Integer ring,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (!Utils.isEmpty(name)
          && !Utils.isEmpty(password)
          && !Utils.isEmpty(email)
          && !userService.existsByEmail(email)) {
        User u = new User();
        u.name = name;
        u.email = email;
        u.passwordHash = Utils.encodePassword(password);
        u.ring = ring;
        userService.add(u);
        return new ResponseEntity<>(fillUser(u), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  // This method updates the password for same user only
  @RequestMapping("/user/updatePassword/")
  public ResponseEntity<?> updatePassword(
      @RequestParam("userId") Long userId,
      @RequestParam("oldPassword") String oldPassword,
      @RequestParam("newPassword") String newPassword) {
    if(!userService.existsById(userId)) {
      return BAD_REQUEST;
    }

    User user = userService.getById(userId);

    if(!Utils.isEmpty(oldPassword)
        && Utils.matchesPassword(oldPassword, user.passwordHash)) {
      return UNAUTHORIZED;
    }

    if(Utils.isEmpty(newPassword)) {
      return BAD_REQUEST;
    }

    user.passwordHash = Utils.encodePassword(newPassword);
    userService.update(user);
    return new ResponseEntity<>(fillUser(user), HttpStatus.OK);
  }

  @RequestMapping("/apiKey/delete/")
  public ResponseEntity<?> deleteApiKey(
      @RequestParam("apiKeyId") Long apiKeyId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (apiKeyService.existsById(apiKeyId)) {
        return new ResponseEntity<>(fillApiKey(apiKeyService.deleteById(apiKeyId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/grade/delete/")
  public ResponseEntity<?> deleteGrade(
      @RequestParam("gradeId") Integer gradeId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (gradeService.existsById(gradeId)) {
        return new ResponseEntity<>(
            fillGrade(gradeService.deleteById(gradeId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }


  @RequestMapping("/irregularity/delete/")
  public ResponseEntity<?> deleteIrregularity(
      @RequestParam("irregularityId") Long irregularityId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (irregularityService.existsById(irregularityId)) {
        return new ResponseEntity<>(
            fillIrregularity(irregularityService.deleteById(irregularityId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/schedule/delete/")
  public ResponseEntity<?> deleteSchedule(
      @RequestParam("scheduleId") Integer scheduleId,
      @RequestParam("apiKey") String apiKey) {
    if (isAdministrator(apiKey)) {
      if (scheduleService.existsById(scheduleId)) {
        return new ResponseEntity<>(
            fillSchedule(scheduleService.deleteById(scheduleId)), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/apiKey/")
  public ResponseEntity<?> viewApiKey(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey")
        && isTrusted(allRequestParam.get("apiKey"))) {
      List<ApiKey> list =
        apiKeyService
        .query(
            Utils.parseLong(allRequestParam.get("apiKeyId")),
            Utils.parseLong(allRequestParam.get("userId")),
            Utils.parseLong(allRequestParam.get("minCreationTime")),
            Utils.parseLong(allRequestParam.get("maxCreationTime")),
            allRequestParam.containsKey("apiKeyData")
              ? Utils.encodeApiKey(allRequestParam.get("apiKeyData"))
              : null,
            offset,
            count)
        .stream()
        .map(x -> fillApiKey(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/course/")
  public ResponseEntity<?> viewCourse(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Course> els =
        courseService
        .query(
            Utils.parseLong(allRequestParam.get("courseId")),
            Utils.parseLong(allRequestParam.get("teacherId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("period")),
            allRequestParam.get("subject"),
            Utils.parseLong(allRequestParam.get("semesterStartTime")),
            offset,
            count
          )
        .stream()
        .map(x -> fillCourse(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/encounter/")
  public ResponseEntity<?> viewEncounter(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Encounter> els =
        encounterService
        .query(
            Utils.parseLong(allRequestParam.get("encounterId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("minTime")),
            Utils.parseLong(allRequestParam.get("maxTime")),
            allRequestParam.get("type"),
            offset,
            count
          )
        .stream()
        .map(x -> fillEncounter(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/grade/")
  public ResponseEntity<?> viewGrade(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Grade> els =
        gradeService
        .query(
            Utils.parseLong(allRequestParam.get("gradeId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("semesterStartTime")),
            Utils.parseLong(allRequestParam.get("number")),
            offset,
            count
          )
        .stream()
        .map(x -> fillGrade(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/irregularity/")
  public ResponseEntity<?> viewIrregularity(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Irregularity> els =
        irregularityService
        .query(
            Utils.parseLong(allRequestParam.get("irregularityId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("courseId")),
            Utils.parseLong(allRequestParam.get("periodStartTime")),
            Utils.parseLong(allRequestParam.get("teacherId")),
            allRequestParam.get("type"),
            Utils.parseLong(allRequestParam.get("time")),
            Utils.parseLong(allRequestParam.get("minTime")),
            Utils.parseLong(allRequestParam.get("maxTime")),
            Utils.parseLong(allRequestParam.get("timeMissing")),
            offset,
            count
          )
        .stream()
        .map(x -> fillIrregularity(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/location/")
  public ResponseEntity<?> viewLocation(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Location> list =
        locationService
        .query(
            Utils.parseLong(allRequestParam.get("locationId")),
            allRequestParam.get("name"),
            offset,
            count
          )
        .stream()
        .map(x -> fillLocation(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }


  @RequestMapping("/offering/")
  public ResponseEntity<?> viewOffering(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Offering> els =
        offeringService
        .query(
            Utils.parseLong(allRequestParam.get("offeringId")),
            Utils.parseLong(allRequestParam.get("semesterStartTime")),
            Utils.parseLong(allRequestParam.get("courseId")),
            offset,
            count
          )
        .stream()
        .map(x -> fillOffering(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/period/")
  public ResponseEntity<?> viewPeriod(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    String apiKey = allRequestParam.get("apiKey");
    if (isTrusted(apiKey)) {
      List<Period> els =
        periodService
        .query(
            Utils.parseLong(allRequestParam.get("startTime")),
            Utils.parseLong(allRequestParam.get("number")),
            allRequestParam.get("number"),
            Utils.parseLong(allRequestParam.get("minStartTime")),
            Utils.parseLong(allRequestParam.get("maxStartTime")),
            offset,
            count
          )
        .stream()
        .map(x -> fillPeriod(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(els, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/schedule/")
  public ResponseEntity<?> viewSchedule(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Schedule> list =
        scheduleService
        .query(
            Utils.parseLong(allRequestParam.get("scheduleId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("courseId")),
            Utils.parseBoolean(allRequestParam.get("hasStart")),
            Utils.parseLong(allRequestParam.get("startTime")),
            Utils.parseBoolean(allRequestParam.get("hasEnd")),
            Utils.parseLong(allRequestParam.get("endTime")),
            Utils.parseLong(allRequestParam.get("teacherId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("period")),
            offset,
            count
          )
        .stream()
        .map(x -> fillSchedule(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/semester/")
  public ResponseEntity<?> viewSemester(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Semester> list =
        semesterService
        .query(
            Utils.parseLong(allRequestParam.get("startTime")),
            Utils.parseLong(allRequestParam.get("year")),
            allRequestParam.get("type"),
            Utils.parseLong(allRequestParam.get("minStartTime")),
            Utils.parseLong(allRequestParam.get("maxStartTime")),
            offset,
            count
          )
        .stream()
        .map(x -> fillSemester(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/session/")
  public ResponseEntity<?> viewSession(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey") && isTrusted(allRequestParam.get("apiKey"))) {
      List<Session> list =
        sessionService
        .query(
            Utils.parseLong(allRequestParam.get("sessionId")),
            Utils.parseLong(allRequestParam.get("inEncounterId")),
            Utils.parseLong(allRequestParam.get("outEncounterId")),
            Utils.parseLong(allRequestParam.get("anyEncounterId")),
            Utils.parseBoolean(allRequestParam.get("complete")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("time")),
            Utils.parseLong(allRequestParam.get("inTimeBegin")),
            Utils.parseLong(allRequestParam.get("inTimeEnd")),
            Utils.parseLong(allRequestParam.get("outTimeBegin")),
            Utils.parseLong(allRequestParam.get("outTimeEnd")),
            offset,
            count
          )
        .stream()
        .map(x -> fillSession(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/student/")
  public ResponseEntity<?> viewStudent(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey")
        && isTrusted(allRequestParam.get("apiKey"))) {
      List<Student> list =
        studentService
        .query(
            Utils.parseLong(allRequestParam.get("studentId")),
            allRequestParam.get("name"),
            allRequestParam.get("partialName"),
            offset,
            count
          )
        .stream()
        .map(x -> fillStudent(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/user/")
  public ResponseEntity<?> viewUser(
      @RequestParam("offset") Long offset,
      @RequestParam("count") Long count,
      @RequestParam Map<String, String> allRequestParam) {
    if (allRequestParam.containsKey("apiKey")
        && isTrusted(allRequestParam.get("apiKey"))) {
      List<User> list =
        userService
        .query(
            Utils.parseLong(allRequestParam.get("userId")),
            allRequestParam.get("name"),
            allRequestParam.get("email"),
            Utils.parseInteger(allRequestParam.get("ring")),
            offset,
            count
          )
        .stream()
        .map(x -> fillUser(x))
        .collect(Collectors.toList());
      return new ResponseEntity<>(list, HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  /* SPECIAL METHODS */
  @RequestMapping("/misc/attends/")
  public ResponseEntity<?> attends(
      @RequestParam("studentId") Long studentId,
      @RequestParam("locationId") Long locationId,
      @RequestParam("manual") Boolean manual,
      @RequestParam("apiKey") String apiKey
    )
  {
    if(!isTrusted(apiKey)) {
      return UNAUTHORIZED;
    }

    if(!locationService.existsById(locationId) || !studentService.existsById(studentId))
    {
      return BAD_REQUEST;
    }

    Session returnableSession = null;

    // Get this student
    Student student = studentService.getById(studentId);

    // Put the encounter in the database
    Encounter encounter = new Encounter();
    encounter.locationId = locationId;
    encounter.studentId = student.id;
    encounter.time = System.currentTimeMillis();
    encounter.type = manual
      ? Encounter.MANUAL_ENCOUNTER
      : Encounter.DEFAULT_ENCOUNTER;
    encounterService.add(encounter);


    // if school is currently going on, represents the current period
    Period currentPeriod = periodService.getCurrent();

    // if there is currently a course going on, represents the current course
    List<Course> currentCourses = courseService.getByPeriodStartTime(currentPeriod.startTime);
    currentCourses.removeIf(cs -> cs.locationId != locationId);

    Course currentCourse = currentCourses.isEmpty() ? null : currentCourses.get(0);

    // Get incomplete sessions
    List<Session> openSessionList =
      sessionService.query(
          null,           //  Long id
          null,           //  Long inEncounterId
          null,           //  Long outEncounterId
          null,           //  Long anyEncounterId
          false,          //  Boolean complete
          student.id,     //  Long studentId
          null,           //  Long locationId
          null,           //  Long time
          null,           //  Long inTimeBegin
          null,           //  Long inTimeEnd
          null,           //  Long outTimeBegin
          null,           //  Long outTimeEnd
          0,              //  long count
          Long.MAX_VALUE  //  long count
          );

    // If the encounter will be used to start a new session since there are no open ones
    boolean usedToClose = false;

    for (Session openSession : openSessionList) {
      Encounter inEncounter = encounterService.getById(openSession.inEncounterId);
      // if it's at the same location
      if (locationId == inEncounter.locationId) {
        // Then close this session naturally
        openSession.outEncounterId = encounter.id;
        openSession.complete = true;
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
          irregularity.type = Irregularity.TYPE_LEFT_EARLY;
          irregularity.time = encounter.time;
          irregularity.timeMissing = periodService
            .getNextByTime(encounter.time)
            .startTime - encounter.time;
          irregularityService.add(irregularity);
        }
      } else {
        // its not at the same location as the beginning
        // Virtually close session by generating a fake (virtual) encounter and insert it in.
        // We know they must have somehow left from here

        // make a virtual out encounter
        Encounter outEncounter = new Encounter();
        outEncounter.locationId = inEncounter.locationId;
        outEncounter.studentId = inEncounter.studentId;
        outEncounter.time = encounter.time;
        outEncounter.type = Encounter.VIRTUAL_ENCOUNTER;
        encounterService.add(outEncounter);

        // now close session
        openSession.outEncounterId = outEncounter.id;
        openSession.complete = true;
        sessionService.update(openSession);


        // After the person signed out the first time, they might have been logged in at a bunch of classes afterwards
        // We give them the forgot to sign out irregularity at the course which they signed in to
        // period and course are that of the first period with a course that the session intersected
        List<Period> intersectedPeriods =
          periodService.query(
              null,                                                // Long startTime
              null,                                                // Long number
              Period.CLASS_PERIOD,                                 // String type
              periodService.getByTime(encounter.time).startTime,   // Long minStartTime
              outEncounter.time,                                    // Long maxStartTime
              0,
              Long.MAX_VALUE
              );

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
          forgotToSignOut.type = Irregularity.TYPE_FORGOT_SIGN_OUT;
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
      session.complete = false;
      session.inEncounterId = encounter.id;
      session.outEncounterId = 0;
      sessionService.add(session);

      // This is the session we give back
      returnableSession = session;

      if (currentCourse != null) {
        // now we check if they arent there, and fix it
        List<Irregularity> irregularities =
          irregularityService.query(
              null,                    //  Long id
              studentId,               //  Long studentId
              currentCourse.id,        //  Long courseId
              currentPeriod.startTime, //  Long periodStartTime
              null,                    //  Long teacherId
              null,                    //  String type
              null,                    //  Long time
              null,                    //  Long minTime
              null,                    //  Long maxTime
              null,                    //  Long timeMissing
              0,                       //  long offset
              Long.MAX_VALUE           //  long count
              );

        for (Irregularity irregularity : irregularities) {
          if (irregularity.type.equals(Irregularity.TYPE_ABSENT)) {
            // if there is absence, convert it to a tardy or delete it
            if (System.currentTimeMillis() > currentPeriod.startTime) {
              irregularity.type = Irregularity.TYPE_TARDY;
              irregularity.timeMissing = encounter.time - currentPeriod.startTime;
              irregularityService.update(irregularity);
            } else {
              // if they're present before the startTime
              irregularityService.deleteById(irregularity.id);
            }
          } else if (irregularity.type.equals(Irregularity.TYPE_LEFT_EARLY)) {
            // if there is a leftEarly, convert it to a leftTemporarily
            irregularity.type = Irregularity.TYPE_LEFT_TEMPORARILY;
            irregularity.timeMissing = encounter.time - irregularity.time;
            irregularityService.update(irregularity);
          }
        }
      }
    }
    // return the filled encounter on success
    return new ResponseEntity<>(fillSession(returnableSession), HttpStatus.OK);
  }

  @RequestMapping("/misc/validate/")
  public ResponseEntity<?> validateTrusted(@RequestParam("apiKey") String apiKey) {
    return isTrusted(apiKey) ? OK : UNAUTHORIZED;
  }

  @RequestMapping("/misc/getSemesterByTime/")
  public ResponseEntity<?> getSemesterByTime(
      @RequestParam("time") Long time,
      @RequestParam("apiKey") String apiKey) {
    if(isTrusted(apiKey)) {
      return new ResponseEntity<>(semesterService.getByTime(time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/misc/getPeriodByTime/")
  public ResponseEntity<?> currentPeriod(
      @RequestParam("time") Long time,
      @RequestParam("apiKey") String apiKey) {
    if(isTrusted(apiKey)) {
      return new ResponseEntity<>(periodService.getByTime(time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/misc/nextPeriod/")
  public ResponseEntity<?> nextPeriod(@RequestParam("apiKey") String apiKey) {
    if(isTrusted(apiKey)) {
      return new ResponseEntity<>(periodService.getNextByTime(System.currentTimeMillis()), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/misc/registeredForCourse/")
  public ResponseEntity<?> registeredForCourse(
      @RequestParam("courseId") Long courseId,
      @RequestParam("time") Long time,
      @RequestParam("apiKey") String apiKey) {
    if(isTrusted(apiKey)) {
      return new ResponseEntity<>(studentService.registeredForCourse(courseId, time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/misc/present/")
  public ResponseEntity<?> present(
      @RequestParam("locationId") Long locationId,
      @RequestParam("time") Long time,
      @RequestParam("apiKey") String apiKey) {
    if(isTrusted(apiKey)) {
      return new ResponseEntity<>(studentService.present(locationId, time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }
}
