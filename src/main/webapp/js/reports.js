"use strict"

$(document).ready(function(){
  //Bootstrap Popover - Alert Zones/Quick help for Card(s)
  $('[data-toggle="popover"]').popover({
    trigger : 'hover'
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
          <td>${linkRelative(course.subject, '/courseprofile.html?courseId='+course.id)}</td>
        </tr>
      `)
    );
  loadClassSessionReports(moment());
});

// Populates tables with links to class session reports for that day
async function loadClassSessionReports(date) {

  // First get periods that occured on that day
  // Then get courses for that year and teacher
  // For each period on that day, if there is a course with that period, add it to the table

  let apiKey = Cookies.getJSON('apiKey');
  let dayBegin = moment(date).startOf('day');
  let dayEnd = moment(date).endOf('day');


  if(moment().isBefore(dayEnd)) {
    dayEnd = moment();
  }

  // clear
  $('#reports-classes').empty();

  try {
    let semester = await fetchJson(`${apiUrl()}/misc/getSemesterByTime/?time=${date.valueOf()}&apiKey=${apiKey.key}`);

    if(semester == null) {
      console.log('No semester, so assuming no school!');
      return;
    }

    // Note this request is inclusive,
    // searching for end times that are after the beginning of the day, and initial times until the end of the day.
    let periods = await fetchJson(`${apiUrl()}/period/?minStartTime=${dayBegin.valueOf()}&maxStartTime=${dayEnd.valueOf()}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    let courses = await fetchJson(`${apiUrl()}/course/?semesterStartTime=${semester.startTime}&teacherId=${apiKey.user.id}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

    for(let period of periods) {
      // for each course that has this period's id
      for(let course of courses.filter(c => c.period === period.number)) {
        $('#reports-classes').append(`
                  <tr>
                    <td>${period.number}</td>
                    <td>${linkRelative(course.subject, '/classprofile.html?courseId='+course.id+'&periodStartTime='+period.startTime)}</td>
                  </tr>`)
      }
    }
  } catch(err) {
    console.log(err);
    giveAlert('Something went wrong while fetching courses from the server.', 'alert-danger', false);
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
  button.addEventListener('click', function(event) {
    if(!isEmpty(tbox.value)) {
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
  if(isEmpty(validatedName)) {
    giveAlert('Invalid Name', 'alert-danger', false);
    return;
  }

  // clear
  $('#reports-students').empty();

  try {
    let studentList = await fetchJson(`${apiUrl()}/student/?partialName=${validatedName}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    for(let student of studentList) {
      $('#reports-students').append(`
              <tr>
                <td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
                <td>${student.id}</td>
              </tr>
            `);
    }
  } catch(err) {
    console.log(err);
    giveTempError('Something went wrong while fetching students from the server.');
  }
}
