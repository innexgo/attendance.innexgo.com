"use strict"


// this is how far back we look to make the graph
const chartStartDaysAgo = 14;

let student = null;
let irregularities = null;


async function makeChart(irregularities) {

  let apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  if (student == null || irregularities == null) {
    console.log('Student or irregularities null');
    return;
  }

  try {
    let dates = Array.from(
              {length: chartStartDaysAgo},
              (x, i) => moment().subtract(chartStartDaysAgo - i, 'd'));

    let studentChart = new Chart(document.getElementById('studentprofile-chart'), {
      type: 'bar',
      data: {
        labels: dates.map(d => d.format('MMM Do')),
        datasets: [{
          data: dates.map( d => irregularities.filter(i => i.time > moment(d).startOf('day').valueOf() && i.time < moment(d).endOf('day'))
                                              .map(i => i.timeMissing/1000.0)
                                              .reduce((a, b) => a + b, 0)),
          label: 'Minutes tardy or absent',
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          xAxes: [{
            scaleLabel: {
              display: true,
              labelString: 'Date'
            }
          }],
          yAxes: [{
            ticks: {
              beginAtZero: true
            },
            scaleLabel: {
              display: true,
              labelString: 'Minutes'
            }
          }]
        }
      }
    });
  } catch(err) {
    console.log(err);
    givePermError('Failed to load student chart');
  }
}

async function loadCourses(studentId, initialSemesterTime) {
  let apiKey = Cookies.getJSON('apiKey');
  let courseTable = $('#studentprofile-courses')
  // Clear table
  courseTable.empty();
  // Repopulate table
  (await fetchJson(`${apiUrl()}/course/?studentId=${studentId}&semesterStartTime=${initialSemesterTime}&apiKey=${apiKey.key}`))
    .sort((a, b) => (a.period > b.period) ? 1 : -1) // Sort in order
    .forEach(course => courseTable.append(`
            <tr>
              <td>${course.period}</td>
              <td>${linkRelative(course.subject,'/courseprofile.html?courseId='+course.id)}</td>
              <td>${linkRelative(course.teacher.name, '/userprofile.html?userId='+course.teacher.id)}</td>
            </tr>
          `))
}

async function loadIrregularityPage(studentId, minTime, maxTime) {
  let apiKey = Cookies.getJSON('apiKey');
  let irregularityTable = $('#studentprofile-irregularities');
  (await fetchJson(`${apiUrl()}/irregularity/?studentId=${studentId}&minTime=${minTime}&maxTime=${maxTime}&apiKey=${apiKey.key}`))
    .sort((a,b) => (a.time > b.time) ? -1 : 1) // sort by time descending
    .forEach(irregularity => irregularityTable.append(`
            <tr>
              <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
              <td>${linkRelative(irregularity.course.subject,'/courseprofile.html?courseId='+irregularity.course.id)}</td>
              <td>${irregularity.type}</td>
            </tr>
          `)); // Append row
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
    student = (await fetchJson(`${apiUrl()}/student/?studentId=${studentId}&apiKey=${apiKey.key}`))[0];

    if(student == null) {
      throw new Error('Student Id invalid');
    }

    document.getElementById('studentprofile-name').innerHTML = student.name;
    document.getElementById('studentprofile-id').innerHTML = 'ID: ' + student.id;

    // Load semester chooser options
    try {
      let grades = await fetchJson(`${apiUrl()}/grade/?studentId=${studentId}&apiKey=${apiKey.key}`);

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

  } catch(err) {
    console.log(err);
    givePermError('Page loaded with invalid student id.');
  }
}

$(document).ready(async function () {
  await Promise.all([
    initialize(),
    makeChart()]);
});

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});

