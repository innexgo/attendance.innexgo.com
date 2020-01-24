"use strict"

let irregularityPage = 0;

async function loadPage(courseId, page) {
  let apiKey = Cookies.getJSON('apiKey');

  if(page == 0) {
    $('#courseprofile-irregularities-new').attr("disabled", true);
  } else {
    $('#courseprofile-irregularities-new').attr("disabled", false);
  }
  // Count of irregularities per page
  const c = 10;
  try {
    $('#courseprofile-irregularities').empty();

    let irregularities = (await fetchJson(`${apiUrl()}/irregularity/?apiKey=${apiKey.key}&courseId=${courseId}&offset=${c * page}&count=${c}`))
                          .sort((a, b) => (a > b) ? -1 : 1);
    if(irregularities.length == c) {
      $('#courseprofile-irregularities-old').attr("disabled", false);
      irregularities.forEach(irregularity => $('#courseprofile-irregularities').append(`
              <tr>
                <td>${linkRelative(irregularity.student.name, '/studentprofile.html?studentId='+irregularity.student.id)}</td>
                <td>${irregularity.type}</td>
                <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
              </tr>`));
    } else {
      // no more irregularity or something
      $('#courseprofile-irregularities-old').attr("disabled", true);
      if(irregularities.length == 0) {
        $('#courseprofile-irregularities')[0].innerHTML = "<b>No Irregularities</b>";
      }
    }
  } catch(err) {
    console.log(err);
    givePermError('Failed to connect to server.');
    return;
  }
}

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

    let students = (await fetchJson(`${apiUrl()}/schedule/?apiKey=${apiKey.key}&courseId=${courseId}&offset=0&count=${INT32_MAX}`))
      .map(schedule => schedule.student);
    document.getElementById('courseprofile-student-count').innerHTML = 'Number of students: ' + students.length;
    students.forEach(student => $('#courseprofile-students').append(`
          <tr>
            <td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
            <td>${student.id}</td>
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

  // Handle paging
  $('#courseprofile-irregularities-new').click(async function() {
    irregularityPage--;
    await loadPage(courseId, irregularityPage);
  });
  $('#courseprofile-irregularities-old').click(async function() {
    irregularityPage++;
    await loadPage(courseId, irregularityPage);
  });

  loadPage(courseId, irregularityPage)
})
