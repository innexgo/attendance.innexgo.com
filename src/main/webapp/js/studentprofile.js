"use strict"

var student = null;
var studentIrregularities = null;

function loadStudentProfile() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  var searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('studentId')) {
    console.log('page not loaded with right params');
    return;
  }

  var studentId = searchParams.get('studentId');

  request(thisUrl() +  '/student/' +
    '?studentId='+studentId +
    '&apiKey='+apiKey.key,
    function(xhr) {
<<<<<<< HEAD
        var response = JSON.parse(xhr.responseText)[0];
        document.getElementById('studentprofile-name').innerHTML = response.name;
        document.getElementById('studentprofile-id').innerHTML = response.id;
=======
      students = JSON.parse(xhr.responseText);
      if(students.length  == 0) {
        giveAlert('No students found with this ID.', 'alert-danger');
        return;
      }
      student = students[0];
      request(thisUrl() + '/irregularity/' +
        '?studentId='+studentId +
        '&apiKey='+apiKey.key,
        function(xhr) {
          studentIrregularities = JSON.parse(xhr.responseText);
        },
        function(xhr) {
          //failure to load irregularity
          giveAlert('Failed to load irregularities.', 'alert-danger');
          return;
        }
      );
>>>>>>> 0fc3823afb5b8291f186182c63804d254960b923
    },
    function(xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
      return
    }
  );
<<<<<<< HEAD

  request(thisUrl() +  '/irregularity/' +
    '?studentId='+studentId +
    '&minTime='+String(moment().subtract(14,'d').format('X'))+
    '&maxTime='+String(moment().unix())+
    '&apiKey='+apiKey.key,
    function(xhr) {
        var response = JSON.parse(xhr.responseText);
        console.log(response);
    },
    function(xhr) {
      //failure
      // TODO send alert
      return
    }
  );
}
var dates = [];
var i;
for (i = 0; i < 14; i++) {
    dates.push(moment().subtract(14-i,'d').format('MM-DD-YYYY'));
};
$(document).ready(function() {
    console.log(thisUrl())
    var chartOne = document.getElementById('chart-one');
    var chartTwo = document.getElementById('chart-two');
    loadStudentProfile()
=======
}

$(document).ready(function() {
  loadStudentIrregularities();

  var chartOne = document.getElementById('chart-one');
  var chartTwo = document.getElementById('chart-two');
>>>>>>> 0fc3823afb5b8291f186182c63804d254960b923

    var dates = [];
    var i;
    for (i = 0; i < 2; i++) {
        dates[i] = String(moment().subtract(14-i,'d').format('MM/DD/YYYY'));
    };
    console.log(dates);
    var myChart = new Chart(chartTwo, {
        labels: ['aa'],
      type: 'line',
      data: {
          datasets: [{
              label: '# of Minutes',
              data: dates,
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
