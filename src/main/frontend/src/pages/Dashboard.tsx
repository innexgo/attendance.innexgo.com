import React from 'react';
import DashboardLayout from '../components/DashboardLayout';
import Utility from '../components/Utility';
import { Container, Popover, CardDeck } from 'react-bootstrap';
import { Async } from 'react-async';
import { fetchApi } from '../utils/utils';
import moment from 'moment';

interface UpcomingClassesProps {
  // list of courses that will be shown upcoming
  courses: Course[]
  // relevant periods
  periods: Period[]
}

// provides a list of some upcoming classes
function UpcomingClasses(props: UpcomingClassesProps) {
  enum TemporalState {
    PAST,
    PRESENT,
    FUTURE
  }

  type CourseInstance = Course & Period & {
    temporalState: TemporalState
  }

  const now = Date.now();
  const upcomingCourseInstances = props.periods
    // ensure it is sorted by time
    .sort((a, b) => a.startTime - b.startTime)
    // join course
    .flatMap((p: Period, index: number, arr: Period[]): CourseInstance[] => {
      let temporalState: TemporalState;
      if (index < arr.length - 1) {
        // the next chronological period
        const np = arr[index + 1];
        // if it is not the last chronological period
        if (p.startTime < now) {
          if (np.startTime < now) {
            temporalState = TemporalState.PAST;
          } else {
            temporalState = TemporalState.PRESENT;
          }
        } else {
          temporalState = TemporalState.FUTURE;
        }
      } else {
        // means it is the last chronological period presented to us
        if (p.startTime < now) {
          temporalState = TemporalState.PRESENT
        } else {
          temporalState = TemporalState.FUTURE;
        }
      }
      // a list of all the courses that made it
      return props.courses
        .filter((c: Course) => c.period === p.numbering)
        .map((c: Course): CourseInstance => {
          const foo: CourseInstance = {
            ...c,
            ...p,
            temporalState
          };
          return foo;
        })
        ;
    });

  return (
    <table>
      {
        upcomingCourseInstances.map((x) =>
          <tr>
            <td>{x.temporalState}</td>
            <td>{x.subject}</td>
            <td>{moment(x.startTime).format()}</td>
          </tr>
        )
      }
    </table>);
}


function Dashboard(props: AuthenticatedComponentProps) {

  const informationTooltip = <Popover id="information-tooltip">
    When you're not teaching a class, your current classroom is unknown.
    This means that the system doesn't know which classroom to display data for.
    By setting a default location here, you can use the apps on this page,
    such as Current Status, Manual Attendance, and Recent Activity even when you don't have a class active.
    Note that the default location is overriden when there is a course in session.
  </Popover>;

  const loadData = async (apiKey: ApiKey):Promise<UpcomingClassesProps> => {
    const semester = await fetchApi("misc/getSemesterByTime/?" + new URLSearchParams([
      ["time", `${Date.now()}`],
      ["apiKey", apiKey.key]
    ])) as Semester;

    const periods = await fetchApi("misc/getPeriodIntersectingTime/?" + new URLSearchParams([
      ["minStartTime", `${moment().startOf('day').valueOf()}`],
      ["maxStartTime", `${moment().endOf('day').valueOf()}`],
      ["apiKey", apiKey.key]
    ])) as Period[];

    const courses = await fetchApi("course/?" + new URLSearchParams([
      ["semesterStartTime", `${semester.startTime}`],
      ["userId", `${apiKey.user.id}`],
      ["offset", "0"],
      ["count", "1000"]
    ])) as Course[];

    return {
      periods,
      courses,
    }
  };

  return (
    <DashboardLayout {...props} >
      <Container fluid className="py-3 px-3">
        <CardDeck>
          <Utility title="Welcome" overlay={informationTooltip}>
            <Async promise={loadData(props.apiKey)}>
              <Async.Pending>Loading...</Async.Pending>
              <Async.Fulfilled>
                {data => <UpcomingClasses {...(data as UpcomingClassesProps)} />}
              </Async.Fulfilled>
              <Async.Rejected>{error => `Something went wrong: ${error.message}`}</Async.Rejected>
            </Async>
          </Utility>
        </CardDeck>
      </Container>
    </DashboardLayout >
  )
}

export default Dashboard;

