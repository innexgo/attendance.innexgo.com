"use strict"


// this is how far back we look to make the graph
const chartStartDaysAgo = 14;

let student = null;
let irregularities = null;
let grades = null;

function makeChart() {

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

async function getCourses(initialSemesterTime) {

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
  } catch(err) {
    console.log(err);
    givePermError('Page loaded with invalid student id.');
  }
  
  try {
    grades = await fetchJson(`${apiUrl()}/grades/?studentId=${studentId}&apiKey=${apiKey.key}`);

    let currentGrade = grades.filter(




  try {
    irregularities = await fetchJson(`${apiUrl()}/irregularity/?studentId=${studentId}&apiKey=${apiKey.key}`);

    irregularities.sort((a,b) => (a.time > b.time) ? -1 : 1) // sort by time descending
           .forEach(irregularity => $('#studentprofile-irregularities').append(`
            <tr>
              <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
              <td>${linkRelative(irregularity.course.subject,'/courseprofile.html?courseId='+irregularity.course.id)}</td>
              <td>${irregularity.type}</td>
            </tr>
          `)); // Append row
  } catch(err) {
    console.log(err);
    givePermError('Failed to load irregularities.');
  }
}

$(document).ready(async function () {
  await initialize();
  makeChart();
});

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});
