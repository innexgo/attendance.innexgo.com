"use strict"

/* global
  Cookies moment
  apiUrl isEmpty standardizeString
  fetchJson linkRelative INT32_MAX
  giveTempError givePermError giveTempSuccess
  */

$(document).ready(async function () {
  //Bootstrap Popover - Alert Zones/Quick help for Card(s)
  $('[data-toggle="popover"]').popover({
    trigger: 'hover'
  });


  $('#reports-datetimepicker').datetimepicker({
    defaultDate: moment(),
    format: 'L',
    keepInvalid: false,
    change: (date, oldDate) => loadClassSessionReports(date),
    maxDate: moment()
  });
  $('#reports-datetimepicker').on('change.datetimepicker', function (e) {
    loadClassSessionReports(e.date);
  });

  // Get courses from cookie, sort them by period in order, and then append to end of table
  Cookies.getJSON('courses')
    .sort((a, b) => (a.period > b.period) ? 1 : -1)
    .forEach(course => $('#reports-courses').append(`
        <tr>
          <td>${course.period}</td>
          <td>${linkRelative(course.subject, '/coursereport.html?courseId=' + course.id)}</td>
        </tr>
      `)
    );
  await loadClassSessionReports(moment());
  try {
    await loadStudentClassPeriodReports();
  } catch {
    givePermError('Failed to load student class period reports');
  }
});

async function loadStudentClassPeriodReports() {
  const apiKey = Cookies.getJSON('apiKey');
  const courses = Cookies.getJSON('courses').sort((a, b) => (a.period > b.period) ? 1 : -1);

  async function calcReport(course) {
    let schedules = await fetchJson(`${apiUrl()}/schedule/?apiKey=${apiKey.key}&courseId=${course.id
      }&scheduleTime=${Date.now()}&offset=0&count=${INT32_MAX}`);

    let table = `<table>`;
    for(let i = 0; i < schedules.length; i++) {
      let s = schedules[i];

      if(i % 4 == 0) {
        table += `<tr>`;
      }
      table += `<td class="px-3">${linkRelative(s.student.name, `/studentclassperiodreport.html?scheduleId=${s.id}`)}</td>`
      if(i % 4 == 3) {
        table += `</tr>`;
      }
    }
    return table;
  }

  for (let cid = 0; cid < courses.length; cid++) {
    // cid == course index
    const course = courses[cid];
    $('#reports-studentclassperiod-accordion').append(`
      <div class="card">
        <div class="card-header" id="reports-heading-${cid}">
          <h2 class="mb-0">
            <button class="btn btn-link" type="button" data-toggle="collapse" data-target="#reports-acc-${cid}">
              <table>
                <tr>
                  <td class="px-3">${course.period}</td>
                  <td class="px-3">${course.subject}</td>
                </tr>
              </table>
            </button>
          </h2>
        </div>
        <div id="reports-acc-${cid}" class="collapse" data-parent="#reports-studentclassperiod-accordion">
          <div class="card-body">
            ${ await calcReport(course) }
          </div>
        </div>
      </div>`);
  }

}

// Populates tables with links to class session reports for that day
async function loadClassSessionReports(date) {

  // First get periods that occured on that day
  // Then get courses for that year and teacher
  // For each period on that day, if there is a course with that period, add it to the table

  let apiKey = Cookies.getJSON('apiKey');
  let dayBegin = moment(date).startOf('day');
  let dayEnd = moment(date).endOf('day');


  if (moment().isBefore(dayEnd)) {
    dayEnd = moment();
  }

  // clear
  $('#reports-classes').empty();

  try {
    let semester = await fetchJson(`${apiUrl()}/misc/getSemesterByTime/?time=${date.valueOf()}&apiKey=${apiKey.key}`);

    if (semester == null) {
      console.log('No semester, so assuming no school!');
      return;
    }

    // Note this request is inclusive,
    // searching for end times that are after the beginning of the day, and initial times until the end of the day.
    let periods = await fetchJson(`${apiUrl()}/period/?minPeriodStartTime=${dayBegin.valueOf()}&maxPeriodStartTime=${dayEnd.valueOf()}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    let courses = await fetchJson(`${apiUrl()}/course/?semesterStartTime=${semester.startTime}&userId=${apiKey.user.id}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

    for (let period of periods) {
      // for each course that has this period's id
      for (let course of courses.filter(c => c.period === period.number)) {
        $('#reports-classes').append(`
                  <tr>
                    <td>${period.number}</td>
                    <td>${linkRelative(course.subject, '/classsessionreport.html?courseId=' + course.id + '&periodStartTime=' + period.startTime)}</td>
                  </tr>`)
      }
    }
  } catch (err) {
    console.log(err);
    giveTempError('Something went wrong while fetching courses from the server.');
  }
}

// Search event listeners
$(document).ready(function () {
  let tbox = document.getElementById('reports-student-search');
  tbox.addEventListener('keydown', function (event) {
    if (event.keyCode === 13) {
      console.log('doing enter key');
      if (isEmpty(tbox.value)) {
        console.log('Name is Empty');
      }
      else {
        searchStudent(tbox.value);
      }
    }
  });

  let button = document.getElementById('reports-student-submit');
  button.addEventListener('click', function (event) {
    if (!isEmpty(tbox.value)) {
      console.log('doing button');
      searchStudent(tbox.value);
      tbox.value = '';
    } else {
      console.log('button pressed but name is empty');
    }
  });
});

async function searchStudent(name) {
  let apiKey = Cookies.getJSON('apiKey');
  let validatedName = standardizeString(name);
  if (isEmpty(validatedName)) {
    giveTempError('Invalid Name');
    return;
  }

  // clear
  $('#reports-students').empty();

  try {
    let studentList = await fetchJson(`${apiUrl()}/student/?studentNamePartial=${validatedName}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    studentList.forEach(student => $('#reports-students').append(`
        <tr>
          <td>${linkRelative(student.name, '/studentreport.html?studentId=' + student.id)}</td>
          <td>${student.id}</td>
        </tr>`));
  } catch (err) {
    console.log(err);
    giveTempError('Something went wrong while fetching students from the server.');
  }
}

$(document).ready(function () {

});
