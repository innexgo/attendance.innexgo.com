"use strict"

$(document).ready(function(){
  //Bootstrap Popover - Alert Zones/Quick help for Card(s)
  $('[data-toggle="popover"]').popover({
    trigger : 'hover'
  });


  $('#reports-datetimepicker').datetimepicker({
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
    .sort((a, b) => (a.time > b.time) ? 1 : -1)
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
function loadClassSessionReports(date) {

  // First get periods that occured on that day
  // Then get courses for that year and teacher
  // For each period on that day, if there is a course with that period, add it to the table

  var apiKey = Cookies.getJSON('apiKey');
  var dayBegin = moment(date).startOf('day');
  var dayEnd = moment(date).endOf('day');


  if(moment().isBefore(dayEnd)) {
    dayEnd = moment();
  }

  // clear
  $('#reports-classes').empty();

  // Note this request is inclusive,
  // searching for end times that are after the beginning of the day, and initial times until the end of the day.
  request(`${apiUrl()}/period/?endTimeBegin=${dayBegin.valueOf()}&initialTimeEnd=${dayEnd.valueOf()}&apiKey=${apiKey.key}`,
    function(xhr) {
      let periods = JSON.parse(xhr.responseText);
      request(`${apiUrl()}/course/?year=${momentToAcademicYear(date)}&teacherId=${apiKey.user.id}&apiKey=${apiKey.key}`,
        function(xhr) {
          let courses = JSON.parse(xhr.responseText);
          for(let period of periods) {
            // for each course that has this period's id
            for(let course of courses.filter(c => c.period === period.period)) {
              $('#reports-classes').append(`
                  <tr>
                    <td>${period.period}</td>
                    <td>${linkRelative(course.subject, '/classprofile.html?courseId='+course.id+'&periodId='+period.id)}</td>
                  </tr>
                `)
            }
          }
        },
        // Failed to get courses
        function(xhr) {
          console.log('Failed to get courses from server.');
          giveAlert('Something went wrong while fetching courses from the server.', 'alert-danger', false);
        }
      );
    },
    // Failed to get periods
    function(xhr) {
      console.log('Failed to get periods from server.');
      giveAlert('Something went wrong while fetching periods from the server.', 'alert-danger', false);
    }
  );
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

function searchStudent(name) {
  let apiKey = Cookies.getJSON('apiKey');
  let validatedName = standardizeString(name);
  if(isEmpty(validatedName)) {
    giveAlert('Invalid Name', 'alert-danger', false);
    return;
  }

  // clear
  $('#reports-students').empty();

  request(`${apiUrl()}/student/?partialName=${validatedName}&apiKey=${apiKey.key}`,
    function(xhr) {
      let studentList = JSON.parse(xhr.responseText);
      for(let student of studentList) {
        $('#reports-students').append(`
            <tr>
              <td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
              <td>${student.id}</td>
              <td>${student.graduatingYear}</td>
            </tr>
          `);
      }
    },
    // Failure
    function(xhr) {
      console.log('Failed to get students from server.');
      giveAlert('Something went wrong while fetching students from the server.', 'alert-danger', false);
    }
  );
}
