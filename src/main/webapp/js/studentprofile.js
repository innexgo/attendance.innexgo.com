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

  return request(thisUrl() + '/irregularity/' +
    '?studentId=' + studentId +
    '&minTime=' + String(moment().subtract(14, 'd').format('X')) +
    '&maxTime=' + String(moment().unix()) +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var irregResponse = JSON.parse(xhr.responseText);
      var irreg = [];
      var i;
      for (i = 0; i < 14; i++) {
        irreg[i] = irregResponse.filter(i => i.time > moment().subtract(14 - d, 'd').valueOf() && i.time < moment().subtract(13 - d, 'd'))
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
    },
    function (xhr) {
      giveAlert('Failed to connect to server.', 'alert-danger');
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
      document.getElementById('studentprofile-id').innerHTML = studentResponse.id;
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
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
      var classTable = document.getElementById('studentprofile-courses');
      coursePeriods.forEach(function (course) {
        if (course != null) {
          var newrow = classTable.insertRow(0);
          newrow.innerHTML =
            ('<td>' + course.subject + '</td>' +
              '<td>' + course.teacher.name + '</td>' +
              '<td>' + course.location.name + '</td>');
        }
      });
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
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
  console.log(thisUrl())
  var chartOne = document.getElementById('chart-one');
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
