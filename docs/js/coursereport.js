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
    document.getElementById('courseprofile-location').innerHTML = linkRelative(course.location.name, '/locationreport.html?locationId='+course.location.id);

    let schedules = await fetchJson(`${apiUrl()}/schedule/?apiKey=${apiKey.key}&courseId=${courseId}&scheduleTime=${Date.now()}&offset=0&count=${INT32_MAX}`);

    document.getElementById('courseprofile-student-count').innerHTML = 'Number of students: ' + schedules.length;

    schedules.forEach(schedule => $('#courseprofile-students').append(`
          <tr>
            <td>${linkRelative(schedule.student.name, '/studentreport.html?studentId='+schedule.student.id)}</td>
            <td>${schedule.student.id}</td>
            <td></td>
            <td></td>
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
      labels: ["% Absence","% Late", "% Present"],
      datasets: [{
        data: [5.50,4.25,90.25],
        backgroundColor: ["#F7464A","#ffff99","#DCEDC1"],
        hoverBackgroundColor: ["#FF5A5E","#ffffe5","#E6F2D3"]
      }]
    },
    options: {
      responsive: true,
      legend: {
        position: 'right',
        labels: {
          padding: 20,
          boxWidth: 10
        }
      },
    }
  });

  var ctxL = document.getElementById("lineChart").getContext('2d');
  var myLineChart = new Chart(ctxL, {
    type: 'line',
    data: {
      labels: ["August", "September", "October", "November", "December", "January", "February", "March", "April", "May"],
      datasets: [{
        label: "Cumulative Absences in Course",
        data: [7, 22, 39, 51, 72, 86, 111, 139, 170, 212],
        backgroundColor: [
          'rgba(105, 0, 132, .2)',
        ],
        borderColor: [
          'rgba(200, 99, 132, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Average Cumulative Absences in All Courses by Instructor",
        data: [5, 26, 43, 67, 86, 107, 130, 157, 185, 220],
        backgroundColor: [
          'rgba(0, 137, 132, .2)',
        ],
        borderColor: [
          'rgba(0, 10, 130, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Average Cumulative Absences in Department",
        data: [12, 37, 66, 97, 125, 152, 186, 221, 260, 306],
        backgroundColor: [
          'rgb(0, 100, 0, .2)',
        ],
        borderColor: [
          'rgb(127, 255, 0, .7)',
        ],
        borderWidth: 2
      }
    ]
  },

    options: {
      responsive: true
    }
  });

  var ctxL = document.getElementById("lineChart2").getContext('2d');
  var myLineChart = new Chart(ctxL, {
    type: 'line',
    data: {
      labels: ["August", "September", "October", "November", "December", "January", "February", "March", "April", "May"],
      datasets: [{
        label: "Absences in Course",
        data: [7, 15, 17, 12, 21, 14, 25, 28, 31, 42],
        backgroundColor: [
          'rgba(105, 0, 132, .2)',
        ],
        borderColor: [
          'rgba(200, 99, 132, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Average # of Absences in All Courses by Instructor",
        data: [5, 21, 17, 24, 19, 21, 23, 27, 28, 35],
        backgroundColor: [
          'rgba(0, 137, 132, .2)',
        ],
        borderColor: [
          'rgba(0, 10, 130, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Average # of Absences in Department",
        data: [12, 25, 29, 31, 28, 27, 34, 35, 39, 46],
        backgroundColor: [
          'rgb(0, 100, 0, .2)',
        ],
        borderColor: [
          'rgb(127, 255, 0, .7)',
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
