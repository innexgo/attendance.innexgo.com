"use strict"

async function loadCourseProfile(courseId) {
  try {
    let apiKey = Cookies.getJSON('apiKey');
    let course = (await fetchJson(`${apiUrl()}/course/?apiKey=${apiKey.key}&courseId=${courseId}&offset=0&count=1`))[0];
    if(course == null) {
      givePermError('Course query specifies invalid course id.');
      return;
    }

    document.getElementById('courseprofile-name').innerHTML = course.subject;
    document.getElementById('courseprofile-teacher').innerHTML = 'Teacher: ' + linkRelative(course.teacher.name, '/userprofile.html?userId=' + course.teacher.id);
    document.getElementById('courseprofile-period').innerHTML = 'Period: ' + course.period;
    document.getElementById('courseprofile-location').innerHTML = linkRelative(course.location.name, '/locationprofile.html?locationId='+course.location.id);

    let schedules = await fetchJson(`${apiUrl()}/schedule/?apiKey=${apiKey.key}&courseId=${courseId}&scheduleTime=${Date.now()}&offset=0&count=${INT32_MAX}`);

    document.getElementById('courseprofile-student-count').innerHTML = 'Number of students: ' + schedules.length;

    schedules.forEach(schedule => $('#courseprofile-students').append(`
          <tr>
            <td>${linkRelative(schedule.student.name, '/studentprofile.html?studentId='+schedule.student.id)}</td>
            <td>${schedule.student.id}</td>
          </tr>`));
  } catch(err) {
    console.log(err);
    givePermError('Failed to connect to server.');
    return;
  }
}

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
    trigger : 'hover'
  });
});

$(document).ready(function() {
  let apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    givePermError('You are not signed in.', );
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('courseId')) {
    givePermError('No course query in URL.', );
    return;
  }

  let courseId = searchParams.get('courseId');


  loadCourseProfile(courseId);
})
