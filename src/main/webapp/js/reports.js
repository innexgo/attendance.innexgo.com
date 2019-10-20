"use strict"

$(document).ready(function(){
  //Bootstrap Popover - Alert Zones/Quick help for Card(s)
  $('[data-toggle="popover"]').popover({
    trigger : 'hover'
  });


  $('#reports-datetimepicker').datetimepicker({
    format: 'L'
  });

  loadCourseReports();
  loadClassSessionReports();
});

function loadClassSessionReports() {
  
}

function loadCourseReports() {
  // Get courses from cookie, sort them by period in order, and then append to end of table
  Cookies.getJSON('courses')
    .sort((a, b) => (a.time > b.time) ? 1 : -1)
    .forEach(course => console.log(course) + $('#reports-courses').append(
       `<tr>
          <td>${course.period}</td>
          <td>${linkRelative(course.subject, '/courseprofile.html?courseId='+course.id)}</td>
        </tr>`
    ));
}
