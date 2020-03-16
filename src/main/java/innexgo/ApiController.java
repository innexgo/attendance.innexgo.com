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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @Autowired InnexgoService innexgoService;
  @Autowired SchedulerService schedulerService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);

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
      @RequestParam(value = "userEmail", defaultValue = "") String email,
      @RequestParam("expirationTime") Long expirationTime,
      @RequestParam("userPassword") String password) {
    // if they gave a username instead of a userId
    if (userId == -1 && !Utils.isEmpty(email)) {
      // if the email is registered
      if (userService.existsByEmail(email)) {
        // get email
        userId = userService.getByEmail(email).id;
      }
    }
    // Ensure user exists
    if(!userService.existsById(userId)) {
      return Errors.USER_NONEXISTENT.getResponse();
    }
    // Ensure password is valid
    User u = userService.getById(userId);
    if (!Utils.matchesPassword(password, u.passwordHash)) {
      return Errors.PASSWORD_INCORRECT.getResponse();
    }
    // now actually make apiKey
    ApiKey apiKey = new ApiKey();
    apiKey.userId = userId;
    apiKey.creationTime = System.currentTimeMillis();
    apiKey.expirationTime = expirationTime;
    apiKey.key = Utils.generateKey();
    apiKey.keyHash = Utils.encodeApiKey(apiKey.key);
    apiKeyService.add(apiKey);
    return new ResponseEntity<>(innexgoService.fillApiKey(apiKey), HttpStatus.OK);
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
      @RequestParam("periodNumber") Long period,
      @RequestParam("courseSubject") String subject,
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
    if(!locationService.existsById(locationId)) {
      return Errors.LOCATION_NONEXISTENT.getResponse();
    }
    if(!userService.existsById(teacherId)) {
      return Errors.USER_NONEXISTENT.getResponse();
    }
    if(Utils.isEmpty(subject)) {
      return Errors.COURSE_SUBJECT_EMPTY.getResponse();
    }
    Course course = new Course();
    course.teacherId = teacherId;
    course.locationId = locationId;
    course.period = period;
    course.subject = subject;
    courseService.add(course);
    // return the innexgoService.filled course on success
    return new ResponseEntity<>(innexgoService.fillCourse(course), HttpStatus.OK);
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
    if(!innexgoService.isTrusted(apiKey)) {
      return Errors.MUST_BE_USER.getResponse();
    }
    if(!locationService.existsById(locationId)) {
      return Errors.LOCATION_NONEXISTENT.getResponse();
    }
    if(!studentService.existsById(studentId)) {
      return Errors.STUDENT_NONEXISTENT.getResponse();
    }
    Encounter encounter = new Encounter();
    encounter.locationId = locationId;
    encounter.studentId = studentId;
    encounter.time = System.currentTimeMillis();
    encounter.type = manual
      ? Encounter.MANUAL_ENCOUNTER
      : Encounter.DEFAULT_ENCOUNTER;
    encounterService.add(encounter);
    return new ResponseEntity<>(innexgoService.fillEncounter(encounter), HttpStatus.OK);
  }

  @RequestMapping("/grade/new/")
  public ResponseEntity<?> newGrade(
      @RequestParam("studentId") Long studentId,
      @RequestParam("semesterStartTime") Long semesterStartTime,
      @RequestParam("gradeNumber") Long number,
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
    if(!studentService.existsById(studentId)) {
      return Errors.STUDENT_NONEXISTENT.getResponse();
    }
    if(gradeService.existsByStudentIdSemesterStartTime(studentId, semesterStartTime)) {
      return Errors.GRADE_EXISTENT.getResponse();
    }

    Grade grade = new Grade();
    grade.studentId = studentId;
    grade.semesterStartTime = semesterStartTime;
    grade.number = number;
    gradeService.add(grade);
    return new ResponseEntity<>(innexgoService.fillGrade(grade), HttpStatus.OK);
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
      @RequestParam("locationName") String name,
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
    if(locationService.existsById(locationId)) {
      return Errors.LOCATION_EXISTENT.getResponse();
    }
    if(Utils.isEmpty(name)) {
      return Errors.LOCATION_NAME_EMPTY.getResponse();
    }
    Location location = new Location();
    location.id = locationId;
    location.name = name;
    locationService.add(location);
    return new ResponseEntity<>(innexgoService.fillLocation(location), HttpStatus.OK);
  }

  @RequestMapping("/offering/new/")
  public ResponseEntity<?> newOffering(
      @RequestParam("semesterStartTime") Long semesterStartTime,
      @RequestParam("courseId") Long courseId,
      @RequestParam("apiKey") String apiKey) {
    if(!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
    if(!semesterService.existsByStartTime(semesterStartTime)) {
      return Errors.SEMESTER_NONEXISTENT.getResponse();
    }
    if(!courseService.existsById(courseId)) {
      return Errors.COURSE_NONEXISTENT.getResponse();
    }
    if(offeringService.getOfferingBySemesterStartTimeCourseId(
              semesterStartTime,
              courseId)
            != null) {
      return Errors.OFFERING_EXISTENT.getResponse();
    }
    Offering offering = new Offering();
    offering.semesterStartTime = semesterStartTime;
    offering.courseId = courseId;
    offeringService.add(offering);
    return new ResponseEntity<>(innexgoService.fillOffering(offering), HttpStatus.OK);
  }

  @RequestMapping("/period/new/")
  public ResponseEntity<?> newPeriod(
      @RequestParam("periodStartTime") Long startTime,
      @RequestParam("periodNumber") Long number,
      @RequestParam("periodType") String type,
      @RequestParam("apiKey") String apiKey) {
    if (!innexgoService.isAdministrator(apiKey)) {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
    if(!Arrays.asList(PeriodType.values()).contains(type)) {
      return Errors.INVALID_PERIOD_TYPE.getResponse();
    }
    Period pr = new Period();
    pr.startTime = startTime;
    pr.number = number;
    pr.type = PeriodType.valueOf(type);
    periodService.add(pr);
    innexgoService.restartInsertAbsences();
    return new ResponseEntity<>(innexgoService.fillPeriod(pr), HttpStatus.OK);
  }

  @RequestMapping("/schedule/new/")
  public ResponseEntity<?> newSchedule(
      @RequestParam("studentId") Long studentId,
      @RequestParam("courseId") Long courseId,
      @RequestParam(value="scheduleStartTime", defaultValue="-1") Long startTime,
      @RequestParam(value="scheduleEndTime", defaultValue="-1") Long endTime,
      @RequestParam("apiKey") String apiKey) {
    if (innexgoService.isAdministrator(apiKey)) {
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
        return new ResponseEntity<>(innexgoService.fillSchedule(schedule), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
  }

  @RequestMapping("/student/new/")
  public ResponseEntity<?> newStudent(
      @RequestParam("studentId") Long studentId,
      @RequestParam("studentName") String name,
      @RequestParam("apiKey") String apiKey) {
    if (innexgoService.isAdministrator(apiKey)) {
      if (!studentService.existsById(studentId)
          && !Utils.isEmpty(name)) {
        Student student = new Student();
        student.id = studentId;
        student.name = name.toUpperCase();
        studentService.add(student);
        return new ResponseEntity<>(innexgoService.fillStudent(student), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
  }

  @RequestMapping("/user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userName") String name,
      @RequestParam("userEmail") String email,
      @RequestParam("userPassword") String password,
      @RequestParam("userRing") Integer ring,
      @RequestParam("apiKey") String apiKey) {
    if (innexgoService.isAdministrator(apiKey)) {
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
        return new ResponseEntity<>(innexgoService.fillUser(u), HttpStatus.OK);
      } else {
        return BAD_REQUEST;
      }
    } else {
      return Errors.MUST_BE_ADMIN.getResponse();
    }
  }

  // This method updates the password for same user only
  @RequestMapping("/user/updatePassword/")
  public ResponseEntity<?> updatePassword(
      @RequestParam("userId") Long userId,
      @RequestParam("userOldPassword") String oldPassword,
      @RequestParam("userNewPassword") String newPassword) {
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
    return new ResponseEntity<>(innexgoService.fillUser(user), HttpStatus.OK);
  }

  @RequestMapping("/apiKey/delete/")
  public ResponseEntity<?> deleteApiKey(
      @RequestParam("apiKeyId") Long apiKeyId,
      @RequestParam("apiKey") String apiKey) {
    if (innexgoService.isAdministrator(apiKey)) {
      if (apiKeyService.existsById(apiKeyId)) {
        return new ResponseEntity<>(innexgoService.fillApiKey(apiKeyService.deleteById(apiKeyId)), HttpStatus.OK);
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
    if (innexgoService.isAdministrator(apiKey)) {
      if (gradeService.existsById(gradeId)) {
        return new ResponseEntity<>(
            innexgoService.fillGrade(gradeService.deleteById(gradeId)), HttpStatus.OK);
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
    if (innexgoService.isAdministrator(apiKey)) {
      if (irregularityService.existsById(irregularityId)) {
        return new ResponseEntity<>(
            innexgoService.fillIrregularity(irregularityService.deleteById(irregularityId)), HttpStatus.OK);
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
    if (innexgoService.isAdministrator(apiKey)) {
      if (scheduleService.existsById(scheduleId)) {
        return new ResponseEntity<>(
            innexgoService.fillSchedule(scheduleService.deleteById(scheduleId)), HttpStatus.OK);
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
        && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<ApiKey> list =
        apiKeyService
        .query(
            Utils.parseLong(allRequestParam.get("apiKeyId")),
            Utils.parseLong(allRequestParam.get("userId")),
            Utils.parseLong(allRequestParam.get("apiKeyMinCreationTime")),
            Utils.parseLong(allRequestParam.get("apiKeyMaxCreationTime")),
            allRequestParam.containsKey("apiKeyData")
              ? Utils.encodeApiKey(allRequestParam.get("apiKeyData"))
              : null,
            offset,
            count)
        .stream()
        .map(x -> innexgoService.fillApiKey(x))
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
    if (innexgoService.isTrusted(apiKey)) {
      List<Course> els =
        courseService
        .query(
            Utils.parseLong(allRequestParam.get("courseId")),
            Utils.parseLong(allRequestParam.get("userId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("periodNumber")),
            allRequestParam.get("courseSubject"),
            Utils.parseLong(allRequestParam.get("semesterStartTime")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillCourse(x))
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
    if (innexgoService.isTrusted(apiKey)) {
      List<Encounter> els =
        encounterService
        .query(
            Utils.parseLong(allRequestParam.get("encounterId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("encounterMinTime")),
            Utils.parseLong(allRequestParam.get("encounterMaxTime")),
            allRequestParam.get("type"),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillEncounter(x))
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
    if (innexgoService.isTrusted(apiKey)) {
      List<Grade> els =
        gradeService
        .query(
            Utils.parseLong(allRequestParam.get("gradeId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("semesterStartTime")),
            Utils.parseLong(allRequestParam.get("periodNumber")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillGrade(x))
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
    if (innexgoService.isTrusted(apiKey)) {
      List<Irregularity> els =
        irregularityService
        .query(
            Utils.parseLong(allRequestParam.get("irregularityId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("courseId")),
            Utils.parseLong(allRequestParam.get("periodStartTime")),
            Utils.parseLong(allRequestParam.get("userId")),
            allRequestParam.get("irregularityType"),
            Utils.parseLong(allRequestParam.get("irregularityTime")),
            Utils.parseLong(allRequestParam.get("irregularityMinTime")),
            Utils.parseLong(allRequestParam.get("irregularityMaxTime")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillIrregularity(x))
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
    if (allRequestParam.containsKey("apiKey") && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<Location> list =
        locationService
        .query(
            Utils.parseLong(allRequestParam.get("locationId")),
            allRequestParam.get("locationName"),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillLocation(x))
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
    if (innexgoService.isTrusted(apiKey)) {
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
        .map(x -> innexgoService.fillOffering(x))
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
    if (innexgoService.isTrusted(apiKey)) {
      List<Period> els =
        periodService
        .query(
            Utils.parseLong(allRequestParam.get("periodStartTime")),
            Utils.parseLong(allRequestParam.get("periodNumber")),
            allRequestParam.get("periodType"),
            Utils.parseLong(allRequestParam.get("minPeriodStartTime")),
            Utils.parseLong(allRequestParam.get("maxPeriodStartTime")),
            Utils.parseBoolean(allRequestParam.get("periodTemp")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillPeriod(x))
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
    if (allRequestParam.containsKey("apiKey") && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<Schedule> list =
        scheduleService
        .query(
            Utils.parseLong(allRequestParam.get("scheduleId")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("courseId")),
            Utils.parseBoolean(allRequestParam.get("scheduleHasStart")),
            Utils.parseBoolean(allRequestParam.get("scheduleHasEnd")),
            Utils.parseLong(allRequestParam.get("userId")),
            Utils.parseLong(allRequestParam.get("scheduleTime")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("periodNumber")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillSchedule(x))
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
    if (allRequestParam.containsKey("apiKey") && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<Semester> list =
        semesterService
        .query(
            Utils.parseLong(allRequestParam.get("semesterStartTime")),
            Utils.parseLong(allRequestParam.get("semesterYear")),
            allRequestParam.get("semesterType"),
            Utils.parseLong(allRequestParam.get("minSemesterStartTime")),
            Utils.parseLong(allRequestParam.get("maxSemesterStartTime")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillSemester(x))
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
    if (allRequestParam.containsKey("apiKey") && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<Session> list =
        sessionService
        .query(
            Utils.parseLong(allRequestParam.get("sessionId")),
            Utils.parseLong(allRequestParam.get("inEncounterId")),
            Utils.parseLong(allRequestParam.get("outEncounterId")),
            Utils.parseLong(allRequestParam.get("anyEncounterId")),
            Utils.parseBoolean(allRequestParam.get("sessionComplete")),
            Utils.parseLong(allRequestParam.get("studentId")),
            Utils.parseLong(allRequestParam.get("locationId")),
            Utils.parseLong(allRequestParam.get("sessionIncludesTime")),
            Utils.parseLong(allRequestParam.get("inTimeBegin")),
            Utils.parseLong(allRequestParam.get("inTimeEnd")),
            Utils.parseLong(allRequestParam.get("outTimeBegin")),
            Utils.parseLong(allRequestParam.get("outTimeEnd")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillSession(x))
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
        && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<Student> list =
        studentService
        .query(
            Utils.parseLong(allRequestParam.get("studentId")),
            allRequestParam.get("studentName"),
            allRequestParam.get("studentNamePartial"),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillStudent(x))
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
        && innexgoService.isTrusted(allRequestParam.get("apiKey"))) {
      List<User> list =
        userService
        .query(
            Utils.parseLong(allRequestParam.get("userId")),
            allRequestParam.get("userName"),
            allRequestParam.get("userEmail"),
            Utils.parseInteger(allRequestParam.get("userRing")),
            offset,
            count
          )
        .stream()
        .map(x -> innexgoService.fillUser(x))
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
    if(!innexgoService.isTrusted(apiKey)) {
      return UNAUTHORIZED;
    }

    if(!locationService.existsById(locationId) || !studentService.existsById(studentId))
    {
      return BAD_REQUEST;
    }

    Session s = innexgoService.attends(studentId, locationId, manual);

    // return the innexgoService.filled encounter on success
    return new ResponseEntity<>(innexgoService.fillSession(s), HttpStatus.OK);
  }

  @RequestMapping("/misc/validate/")
  public ResponseEntity<?> validateTrusted(@RequestParam("apiKey") String apiKey) {
    return innexgoService.isTrusted(apiKey) ? OK : UNAUTHORIZED;
  }

  @RequestMapping("/misc/getSemesterByTime/")
  public ResponseEntity<?> getSemesterByTime(
      @RequestParam("time") Long time,
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isTrusted(apiKey)) {
      return new ResponseEntity<>(semesterService.getByTime(time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/misc/getPeriodByTime/")
  public ResponseEntity<?> currentPeriod(
      @RequestParam("time") Long time,
      @RequestParam("apiKey") String apiKey) {
    if(innexgoService.isTrusted(apiKey)) {
      return new ResponseEntity<>(periodService.getByTime(time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }

  @RequestMapping("/misc/nextPeriod/")
  public ResponseEntity<?> nextPeriod(@RequestParam("apiKey") String apiKey) {
    if(innexgoService.isTrusted(apiKey)) {
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
    if(innexgoService.isTrusted(apiKey)) {
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
    if(innexgoService.isTrusted(apiKey)) {
      return new ResponseEntity<>(studentService.present(locationId, time), HttpStatus.OK);
    } else {
      return UNAUTHORIZED;
    }
  }
}
