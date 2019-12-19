"use strict"

async function loadCourseProfile() {
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

  try {
    let course = (await fetchJson(`${apiUrl()}/course/?apiKey=${apiKey.key}&courseId=${courseId}`))[0];
    if(course == null) {
      throw new Error('Course nonexistent!');
    }

    document.getElementById('courseprofile-name').innerHTML = course.subject;
    document.getElementById('courseprofile-teacher').innerHTML = 'Teacher: ' + linkRelative(course.teacher.name, '/userprofile.html?userId=' + course.teacher.id);
    document.getElementById('courseprofile-period').innerHTML = 'Period: ' + course.period;
    document.getElementById('courseprofile-location').innerHTML = linkRelative(course.location.name, '/locationprofile.html?locationId='+course.location.id);

    let irregularities = await fetchJson(`${apiUrl()}/irregularity/?apiKey=${apiKey.key}&courseId=${course.id}&count=100`);
    irregularities.forEach(irregularity => $('#courseprofile-irregularities').append(`
          <tr>
            <td>${linkRelative(irregularity.student.name, '/studentprofile.html?studentId='+irregularity.student.id)}</td>
            <td>${irregularity.type}</td>
            <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
          </tr>`));

    let students = (await fetchJson(`${apiUrl()}/schedule/?apiKey=${apiKey.key}&courseId=${courseId}`))
                    .map(schedule => schedule.student);
    document.getElementById('courseprofile-student-count').innerHTML = 'Number of students: ' + students.length;
    students.forEach(student => $('#courseprofile-students').append(`
          <tr>
            <td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
            <td>${student.id}</td>
            <td>${student.graduatingYear}</td>
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
  loadCourseProfile();
})
