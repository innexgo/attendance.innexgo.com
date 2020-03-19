"use strict"

/* global Cookies fetchJson apiUrl linkRelative givePermError INT32_MAX */

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

$(document).ready(function(){

  var ctx = document.getElementById("doughnutChart").getContext('2d');
  var ctxD = document.getElementById("doughnutChart").getContext('2d');
  var myLineChart = new Chart(ctxD, {
    type: 'doughnut',
    data: {
      labels: ["% Absence","% Present"],
      datasets: [{
        data: [1/100, 99/100],
        backgroundColor: ["#F7464A", "#DCEDC1"],
        hoverBackgroundColor: ["#FF5A5E","#E6F2D3"]
      }]
    },
    options: {
      responsive: true
    }
  });

  var ctxL = document.getElementById("lineChart").getContext('2d');
  var myLineChart = new Chart(ctxL, {
    type: 'line',
    data: {
      labels: ["August", "September", "October", "November", "December", "January", "February", "March", "April", "May"],
      datasets: [{
          label: "Average Cumulative Absence Record",
          data: [1, 1, 3, 4, 6, 6, 8, 9, 14, 17],
          backgroundColor: [
'rgba(0, 137, 132, .2)',
          ],
          borderColor: [
'rgba(0, 10, 130, .7)',
          ],
          borderWidth: 2
        }
      ]
    },
    options: {
      responsive: true
    }
  });

});
