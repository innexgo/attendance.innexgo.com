"use strict"


// this is how far back we look to make the graph
const earliest_date_offset = 14;

let student = null;


function getIrregularities(chartName) {


  let chartTwo = document.getElementById('chart-two');

  var dates = [];
  for (let i = 0; i < 14; i++) {
    dates[i] = String(moment().subtract(14 - i, 'd').format('MMM Do'));
  }

  var myChartTwo = new Chart(chartTwo, {

    type: 'bar',
    data: {
      labels: dates,
      datasets: [{
        data: [],
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






  let apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('studentId')) {
    console.log('page not loaded with right params');
    return;
  }

  let studentId = searchParams.get('studentId');

  try {
    let minTime = moment().subtract(14, 'd').valueOf();
    let irregularities = await fetchJson(`${apiUrl()}/irregularity/?studentId=${studentId}&apiKey=${apiKey.key}&minTime=${minTime}`);
    // Now we must construct an array of 
    let 



  request(apiUrl() + '/irregularity/' +
    '?studentId=' + studentId +
    '&minTime=' + moment().subtract(14, 'd').format('X') +
    '&maxTime=' + moment().unix() +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var irregResponse = JSON.parse(xhr.responseText);

      /* Plot Chart */
      var irreg = [];
      const arrSum = arr => arr.reduce((a, b) => a + b, 0)
      for (var d = 0; d < 14; d++) {
        var msMissing = [];
        irregResponse.filter(i => i.time > moment().subtract(14 - d, 'd').valueOf() && i.time < moment().subtract(13 - d, 'd')).forEach((entry) => msMissing.push(entry.timeMissing));
        irreg[d] = msMissing.length == 0 ? 0 : (arrSum(msMissing) / 60000).toFixed(5);
      }
      chartName.data.datasets.forEach((dataset) => {
        dataset.data.pop();
      });
      chartName.data.datasets.forEach((dataset) => {
        irreg.forEach(function (number) {
          dataset.data.push(number);
        })
      });
      chartName.update();

      /* Get Total Absences and Tardies */
      var totalAbsence = 0;
      var totalTardy = 0;
      irregResponse.forEach(function (entry) {
        if (entry.type == 'tardy') {
          totalTardy++;
        }
        else if (entry.type == 'absent') {
          totalAbsence++;
        };
      });
      document.getElementById('studentprofile-total-tardies').innerHTML = 'Total Tardies: ' + totalTardy;
      document.getElementById('studentprofile-total-absences').innerHTML = 'Total Absences: ' + totalAbsence;
    },
    function (xhr) {
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return;
    }
  );
}

async function initialize() {
  let apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
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
    givePermError('Student Id Invali

  request(apiUrl() + '/student/' +
    '?studentId=' + studentId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      let studentResponse = JSON.parse(xhr.responseText)[0];
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return
    }
  );

  request(apiUrl() + '/course/' +
    '?studentId=' + studentId +
    '&year=' + currentAcademicYear()+
    '&apiKey=' + apiKey.key,
    function (xhr) {
      let studentCourses = JSON.parse(xhr.responseText);
      studentCourses
        .sort((a, b) => (a.period > b.period) ? 1 : -1) // Sort in order
        .forEach(course => $('#studentprofile-courses').append(`
            <tr>
              <td>${course.period}</td>
              <td>${linkRelative(course.subject,'/courseprofile.html?courseId='+course.id)}</td>
              <td>${linkRelative(course.teacher.name, '/userprofile.html?userId='+course.teacher.id)}</td>
              <td>${linkRelative(course.location.name, '/locationprofile.html?locationId='+course.location.id)}</td>
            </tr>
          `)); // Append row
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return;
    }
  );


  request(`${apiUrl()}/irregularity/?studentId=${studentId}&apiKey=${apiKey.key}`,
    function(xhr) {
      let studentIrregularities = JSON.parse(xhr.responseText);
      studentIrregularities
        .sort((a,b) => (a.time > b.time) ? -1 : 1) // sort by time descending
        .forEach(irregularity => $('#studentprofile-irregularities').append(`
            <tr>
              <td>${irregularity.course.period}</td>
              <td>${linkRelative(irregularity.course.subject,'/courseprofile.html?courseId='+irregularity.course.id)}</td>
              <td>${linkRelative(irregularity.course.teacher.name, '/userprofile.html?userId='+irregularity.course.teacher.id)}</td>
              <td>${linkRelative(irregularity.course.location.name, '/locationprofile.html?locationId='+irregularity.course.location.id)}</td>
              <td>${irregularity.type}</td>
              <td>${moment(irregularity.time).format('MMM Do, YYYY')}</td>
            </tr>
          `)); // Append row
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return;
    }
  );
}

$(document).ready(function () {
  initialize();
  makeChart();
});

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});
