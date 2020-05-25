"use strict"

/*
 global Cookies moment
 apiUrl fetchJson linkRelative INT32_MAX ordinal_suffix_of
 giveTempSuccess givePermError
 */

let course = null;
let period = null;


async function loadClassProfile() {
  let apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  if(course == null || period == null) {
    console.log('Page not loaded properly!');
    return;
  }

  let table = document.getElementById('classprofile-table');

  try {
    let students = await fetchJson(`${apiUrl()}/misc/registeredForCourse/?courseId=${course.id}&time=${period.startTime}&apiKey=${apiKey.key}`);
    let irregularities = await fetchJson(`${apiUrl()}/irregularity/?courseId=${course.id}&periodStartTime=${period.startTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

    table.innerHTML = '';

    for (var i = 0; i < students.length; i++) {
      let text = '<span class="fa fa-check"></span>'
      let bgcolor = '#ccffcc';
      let fgcolor = '#00ff00';
      let student = students[i];

      let irregularity = irregularities.filter(i => i.student.id == student.id).pop();
      let type = irregularity == null ? null : irregularity.type;
      if (type == 'Absent') {
        text = '<span class="fa fa-times"></span>';
        bgcolor = '#ffcccc';
        fgcolor = '#ff0000';
      } else if (type == 'Tardy') {
        text = '<span class="fa fa-check"></span>';
        bgcolor = '#ffffcc';
        fgcolor = '#ffff00';
      } else if (type == 'Left Early') {
        text = '<span class="fa fa-sign-out-alt"></span>';
        bgcolor = '#ccffff';
        fgcolor = '#00ffff';
      } else if (type == 'Left Temporarily') {
        text = '<span class="fa fa-check"></span>';
        bgcolor = '#ccffff';
        fgcolor = '#00ffff';
      }

      // put values in table
      var newrow = table.insertRow(0);
      newrow.innerHTML =
        ('<td>' + linkRelative(student.name, '/studentreport.html?studentId='+student.id)+ '</td>' +
          '<td>' + student.id + '</td>' +
          '<td style="background-color:' + bgcolor + ';color:' + fgcolor + '">' + text + '</td>');
      newrow.className = 'id-' + student.id;
    }
  } catch(err) {
    console.log(err);
    givePermError('Failed to get data for students');
  }
}

async function initialize() {
  let apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  let semester = Cookies.getJSON('semester');
  if(semester == null) {
    console.log('No semester');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('courseId') || !searchParams.has('periodStartTime')) {
    console.log('Page loaded with invalid parameters.');
    return;
  }

  let courseId = searchParams.get('courseId');
  let periodStartTime = searchParams.get('periodStartTime');

  let text = document.getElementById('classprofile-text');

  try {
    let courses = await fetchJson(`${apiUrl()}/course/?courseId=${courseId}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    let periods = await fetchJson(`${apiUrl()}/period/?periodStartTime=${periodStartTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

    course = courses[0];
    period = periods[0];

    text.innerHTML = 'View students who attended ' +
      linkRelative(course.subject, '/coursereport.html?courseId='+course.id) +
      ' ('+linkRelative(course.teacher.name, '/userprofile.html?userId='+course.teacher.id)+') on ' +
      moment(period.startTime).format('dddd, MMMM Do YYYY') + ' ' + ordinal_suffix_of(course.period) + ' period.';
  } catch(err) {
    console.log(err);
    givePermError('Could not get course information');
  }
}


$(document).ready(async function () {
  await initialize();
  await loadClassProfile();
})

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
    trigger : 'hover'
  });
});

$(document).ready(function(){

  var ctxD = document.getElementById("doughnutChart").getContext('2d');
  var myLineChart = new Chart(ctxD, {
    type: 'doughnut',
    data: {
      labels: ["# Absence","# Late", "# Present"],
      datasets: [{
        data: [1,5,30],
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
      labels: ["00:10", "00:20", "00:30", "00:40", "00:50", "00:60", "01:00", "01:10", "01:20", "01:30"],
      datasets: [{
        label: "# of Students Leaving",
        data: [0, 0, 1, 2, 0, 0, 3, 2, 1, 1],
        backgroundColor: [
'rgba(105, 0, 132, .2)',
        ],
        borderColor: [
'rgba(200, 99, 132, .7)',
        ],
        borderWidth: 2
      },
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
      labels: ["00:10", "00:20", "00:30", "00:40", "00:50", "00:60", "01:00", "01:10", "01:20", "01:30"],
      datasets: [{
        label: "# of Students Re-Entering",
        data: [0, 3, 2, 1, 4, 0, 1, 0, 0, 2],
        backgroundColor: [
'rgba(0, 137, 132, .2)',
        ],
        borderColor: [
'rgba(0, 10, 130, .7)',
        ],
        borderWidth: 2
      },
      ]
    },
    options: {
      responsive: true
    }
  });
});
