"use strict"

var student = null;
var studentIrregularities = null;

function getIrregularities(chartName) {
  var apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  var searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('studentId')) {
    console.log('page not loaded with right params');
    return;
  }

  var studentId = searchParams.get('studentId');

  request(thisUrl() + '/irregularity/' +
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

function loadStudentProfile() {
  var apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  var searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('studentId')) {
    console.log('page not loaded with right params');
    return;
  }

  var studentId = searchParams.get('studentId');

  request(thisUrl() + '/student/' +
    '?studentId=' + studentId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var studentResponse = JSON.parse(xhr.responseText)[0];
      document.getElementById('studentprofile-name').innerHTML = studentResponse.name;
      document.getElementById('studentprofile-id').innerHTML = 'ID: ' + studentResponse.id;
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return
    }
  );

  request(thisUrl() + '/course/' +
    '?studentId=' + studentId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var studentCourses = JSON.parse(xhr.responseText);
      var coursePeriods = [];
      for (var p = 1; p <= 7; p++) {
        var currentPeriodList = studentCourses.filter(c => c.period == p);
        coursePeriods.push(currentPeriodList.length == 0 ? null : currentPeriodList[0])
      }
      // sort in reverse order by period
      coursePeriods.sort((a, b) => b.period-a.period);

      var classTable = document.getElementById('studentprofile-courses');
      courseperiods.foreach(function (course) {
        if (course != null) {
          var newrow = classtable.insertrow(0);
          newrow.innerhtml =
            ('<td>' + course.period + '</td>' +
             '<td>' + relativeLink(course.subject,'/coursereport.html?courseId=' + course.id) + '</td>' +
             '<td>' + relativeLink(course.teacher.name, '/userprofile.html?userId='+course.teacher.id)+'</td>' +
             '<td>' + relativeLink(course.location.name, '/locationreport.html?locationId='+course.location.id)+ '</td>');
        }
      });
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger', true);
      return;
    }
  );
}

var dates = [];
var i;
for (i = 0; i < 14; i++) {
  dates.push(moment().subtract(14 - i, 'd').format('MM-DD-YYYY'));
};

$(document).ready(function () {
  //var chartOne = document.getElementById('chart-one');
  var chartTwo = document.getElementById('chart-two');

  var dates = [];
  var i;
  for (i = 0; i < 14; i++) {
    dates[i] = String(moment().subtract(14 - i, 'd').format('MM/DD/YYYY'));
  };

  var myChartTwo = new Chart(chartTwo, {

    type: 'line',
    data: {
      labels: dates,
      datasets: [{
        label: '# of Minutes',
        data: [],
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
  loadStudentProfile();
  getIrregularities(myChartTwo);
});
