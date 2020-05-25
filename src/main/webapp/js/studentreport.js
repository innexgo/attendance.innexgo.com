"use strict"

/* global
  moment Cookies
  fetchJson apiUrl linkRelative INT32_MAX
  giveTempError givePermError
*/

async function loadCourses(studentId, initialSemesterTime) {
  let apiKey = Cookies.getJSON('apiKey');
  let courseTable = $('#studentprofile-courses')
  // Clear table
  courseTable.empty();
  // Repopulate table
  (await fetchJson(`${apiUrl()}/course/?studentId=${studentId}&semesterStartTime=${initialSemesterTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
    .sort((a, b) => (a.period > b.period) ? 1 : -1) // Sort in order
    .forEach(course => courseTable.append(`
            <tr>
              <td>${course.period}</td>
              <td> ${linkRelative(course.subject,'/coursereport.html?courseId='+course.id)}</td>
              <td>${linkRelative(course.teacher.name, '/userprofile.html?userId='+course.teacher.id)}</td>
              <td>${linkRelative(course.location.name, '/locationreport.html?locationId='+course.location.id)}</td>
            </tr>
          `))
}

async function loadIrregularityPage(studentId, minTime, maxTime) {
  let apiKey = Cookies.getJSON('apiKey');
  let irregularityTable = $('#studentprofile-irregularities');
  (await fetchJson(`${apiUrl()}/irregularity/?studentId=${studentId}&irregularityMinTime=${minTime}&irregularityMaxTime=${maxTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
    .sort((a,b) => (a.time > b.time) ? -1 : 1) // sort by time descending
    .forEach(irregularity => irregularityTable.append(`
            <tr>
              <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
              <td>${moment(irregularity.time).format('h:mm A')}</td>
              <td>${linkRelative(irregularity.course.subject,'/coursereport.html?courseId='+irregularity.course.id)}</td>
              <td>${irregularity.type}</td>
            </tr>
          `)); // Append row
}

async function loadCurrentLocation(studentId) {
  let apiKey = Cookies.getJSON('apiKey');
  let mostRecentSession = (await fetchJson(`${apiUrl()}/session/?studentId=${studentId}&apiKey=${apiKey.key}&offset=0&count=1`))[0];
  if(mostRecentSession != null) {
    $('#studentprofile-currentlocation-signedin')[0].innerHTML = mostRecentSession.complete ? "No, not still here" : "Yes";
    let time = mostRecentSession.complete ? mostRecentSession.outEncounter.time : mostRecentSession.inEncounter.time;
    $('#studentprofile-currentlocation-time')[0].innerHTML = moment(time).format('h:mm A');
    $('#studentprofile-currentlocation-date')[0].innerHTML = moment(time).format('dddd, MMM Do, YYYY');
    let loc = mostRecentSession.inEncounter.location;
    $('#studentprofile-currentlocation-location')[0].innerHTML = linkRelative(loc.name, '/locationreport.html?locationId='+loc.id);
  }
}

async function initialize() {
  let apiKey = Cookies.getJSON('apiKey');
  if (apiKey == null) {
    console.log('not signed in');
    return;
  }


  let semester = Cookies.getJSON('semester');
  if(semester == null) {
    console.log('No semester');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('studentId')) {
    console.log('Page not loaded with right params');
    givePermError('Page loaded with invalid parameters.');
    return;
  }

  let studentId = searchParams.get('studentId');

  try {
    let student = (await fetchJson(`${apiUrl()}/student/?studentId=${studentId}&offset=0&count=1&apiKey=${apiKey.key}`))[0];

    if(student == null) {
      throw new Error('Student Id invalid');
    }

    document.getElementById('studentprofile-name').innerHTML = student.name;
    document.getElementById('studentprofile-id').innerHTML = 'ID: ' + student.id;
    // Load semester chooser options
    try {
      let grades = await fetchJson(`${apiUrl()}/grade/?studentId=${studentId}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

      let gradeSelect = $('#studentprofile-grades');

      // Add grades to chooser
      grades
        .sort((a,b) => (a.semester.startTime < b.semester.startTime) ? -1 : 1)
        .forEach(g => gradeSelect.append(
          `<option value="${g.semester.startTime}">
            ${moment(g.semester.startTime).year()} - ${g.semester.type}
           </option>`
        ));
      // On change, reload thing
      gradeSelect.change(async function() {
        let selectedValue = $('#studentprofile-grades').val();
        try {
          await loadCourses(studentId, selectedValue);
        } catch(err) {
          console.log(err);
          giveTempError('Failed to load courses.');
        }
      });

      // Now figure out which grade to load initially
      let currentGrade = grades.filter(g => g.semester.startTime == semester.startTime)[0];
      console.log(grades);
      if(currentGrade != null) {
        // Set the grade to the current number
        $('#studentprofile-grade')[0].innerHTML += currentGrade.number;
        // Select the current grade
        gradeSelect.val(currentGrade.semester.startTime);
        // Load the current courses
        try {
          await loadCourses(studentId, currentGrade.semester.startTime);
        } catch(err) {
          console.log(err);
          givePermError('Failed to load courses.');
        }

      } else {
        gradeSelect.prepend(
          `<option selected hidden disabled value="null">Select Semester</option>`
        );
        gradeSelect.val("null");

        $('#studentprofile-grade')[0].innerHTML += 'N/A (Not Enrolled)';
        $('#studentprofile-courses')[0].innerHTML = 'Student Not Enrolled';
      }
    } catch(err) {
      console.log(err);
      givePermError('Failed to load grades.');
    }

    try {
      await loadIrregularityPage(studentId, 0, moment().valueOf())
    } catch(err) {
      console.log(err);
      givePermError('Failed to load irregularities.');
    }

    try {
      await loadCurrentLocation(studentId);
    } catch(err) {
      console.log(err);
      givePermError('Failed to load most recent student location.');
    }

  } catch(err) {
    console.log(err);
    givePermError('Page loaded with invalid student id.');
  }
}
$(document).ready(async function () {
    await initialize();
});

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});

$(document).ready(function(){

  var ctx = document.getElementById("doughnutChart").getContext('2d');
  var ctxD = document.getElementById("doughnutChart").getContext('2d');
  var myLineChart = new Chart(ctxD, {
    type: 'doughnut',
    data: {
      labels: ["% Absence","% Late", "% Present"],
      datasets: [{
        data: [9,10,81],
        backgroundColor: ["#F7464A","#ffff99","#DCEDC1"],
        hoverBackgroundColor: ["#FF5A5E","#ffffe5","#E6F2D3"]
      }]
    },
    options: {
      responsive: true,
      legend: {
        labels: {
          padding: 20,
          boxWidth: 10
        }
      },
    }
  });

  var ctxB = document.getElementById("barChart").getContext('2d');
  var myBarChart = new Chart(ctxB, {
    type: 'bar',
    data: {
      labels: ["Period 1", "Period 2", "Period 3", "Period 4", "Period 5", "Period 6"],
      datasets: [{
        label: '# of Absences',
        data: [3, 4, 4, 5, 5, 5],
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)',
          'rgba(54, 162, 235, 0.2)',
          'rgba(255, 206, 86, 0.2)',
          'rgba(75, 192, 192, 0.2)',
          'rgba(153, 102, 255, 0.2)',
          'rgba(255, 159, 64, 0.2)'
        ],
        borderColor: [
          'rgba(255,99,132,1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
          'rgba(255, 159, 64, 1)'
        ],
        borderWidth: 1
      }]
    },
    options: {
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: true
          }
        }]
      }
    }
  });
});
