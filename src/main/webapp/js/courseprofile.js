"use strict"

function loadCourseProfile() {
  let apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('courseId')) {
    giveAlert('No course query', );
    return;
  }

  let courseId = searchParams.get('courseId');

  request(`${apiUrl()}/course/?apiKey=${apiKey.key}&courseId=${courseId}`,
    function (xhr) {
      let course = JSON.parse(xhr.responseText)[0];
      document.getElementById('courseprofile-name').innerHTML = course.subject;
      document.getElementById('courseprofile-teacher').innerHTML = 'Teacher: ' + linkRelative(course.teacher.name, '/userprofile.html?userId=' + course.teacher.id);
      document.getElementById('courseprofile-period').innerHTML = 'Period: ' + course.period;
      document.getElementById('courseprofile-location').innerHTML = linkRelative(course.location.name, '/locationprofile.html?locationId='+course.location.id);

      request(`${apiUrl()}/irregularity/?apiKey=${apiKey.key}&courseId=${course.id}&count=100`,
        function(xhr) {
          let irregularities = JSON.parse(xhr.responseText);
          irregularities.forEach(irregularity => $('#courseprofile-irregularities').append(`
            <tr>
              <td>${linkRelative(irregularity.student.name, '/studentprofile.html?studentId='+irregularity.student.id)}</td>
              <td>${irregularity.type}</td>
              <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
            </tr>`));
        },
        function (xhr) {
          //failure
          giveAlert('Failed to connect to server.', 'alert-danger', true);
          return;
        }
      );

      request(`${apiUrl()}/student/?apiKey=${apiKey.key}&courseId=${courseId}`,
        function(xhr) {
          let studentList = JSON.parse(xhr.responseText);
          document.getElementById('courseprofile-student-count').innerHTML = 'Number of students: ' + studentList.length;
          studentList.forEach(student => $('#courseprofile-students').append(`
          <tr>
            <td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
            <td>${student.id}</td>
            <td>${student.graduatingYear}</td>
          </tr>`));
        },
        // failure
        function() {
          giveAlert('Failed to connect to server.', 'alert-danger', true);
          return;
        }
      );
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return;
    }
  );
}

$(document).ready(function() {
  loadCourseProfile();
})


//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});
