"use strict"

function loadDataOne() {
  var apiKey = Cookies.getJSON('apiKey');
  var searchParams = new URLSearchParams(window.location.search);
  if (!searchParams.has('courseId')) {
    giveAlert('No user query');
    return;
  }
  var courseId = searchParams.get('courseId');

  request(thisUrl() + '/course/' +
    '?apiKey=' + apiKey.key +
    '&courseId=' + courseId,
    function (xhr) {
      var courseResponse = JSON.parse(xhr.responseText)[0];
      document.getElementById('course-name').innerHTML = courseResponse.subject;
      document.getElementById('course-teacher').innerHTML = 'Teacher: ' + linkRelative(courseResponse.teacher.name, '/userprofile.html?userId=' + courseResponse.teacher.id);
      document.getElementById('course-period').innerHTML = 'Period: ' + courseResponse.period;
      document.getElementById('course-room').innerHTML = linkRelative(courseResponse.location.name, '/locationprofile.html?locationId='+courseResponse.location.id);
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
      return;
    }
  );
};

function loadGraph(chartName) {
  var apiKey = Cookies.getJSON('apiKey');
  var searchParams = new URLSearchParams(window.location.search);
  if (!searchParams.has('courseId')) {
    giveAlert('No user query');
    return;
  }
  var courseId = searchParams.get('courseId');

  request(thisUrl() + '/student/' +
    '?apiKey=' + apiKey.key +
    '&courseId=' + courseId,
    function (xhr) {
      var students = JSON.parse(xhr.responseText);
      var table = document.getElementById('course-students');
      // sort alphabetically
      students.sort(function(a, b) {
        var nameA = a.name.toUpperCase();
        var nameB = b.name.toUpperCase();
        if (nameA > nameB) {
          return -1;
        }
        if (nameA < nameB) {
          return 1;
        }
        return 0;
      });
      document.getElementById('course-student-count').innerHTML = 'Total Students: ' + students.length;

      for (var i = 0; i < students.length; i++) {
        var newrow = table.insertRow(0);
        var student = students[i];
        newrow.innerHTML =
          ('<td>' + linkRelative(student.name, '/studentprofile.html?studentId='+student.id) + '</td>' +
            '<td>' + student.id + '</td>' +
            '<td>' + student.graduatingYear + '</td>')
        newrow.id = 'id-' + student.id;
      }
    },
    function (xhr) {
      giveAlert('Failed to connect to server.', 'alert-danger');
      return;
    });

    request(thisUrl() + '/irregularity/' +
    '?apiKey=' + apiKey.key +
    '&courseId=' + courseId,
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
      document.getElementById('course-total-tardies').innerHTML = 'Total Tardies: ' + totalTardy;
      document.getElementById('course-total-absences').innerHTML = 'Total Absences: ' + totalAbsence;
    },
    //failure
    function (xhr) {
      giveAlert('Failed to connect to server.', 'alert-danger');
      return;
    }
  );
};


$(document).ready(function() {
  loadDataOne();
  var chartOne = document.getElementById('chart-one');
  var dates = [];
  var i;
  for (i = 0; i < 14; i++) {
    dates[i] = String(moment().subtract(14 - i, 'd').format('MM/DD/YYYY'));
  };

  var myChartOne = new Chart(chartOne, {
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
  loadGraph(myChartOne);
})
