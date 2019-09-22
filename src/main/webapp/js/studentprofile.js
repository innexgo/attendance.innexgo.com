"use strict"

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
        var response = JSON.parse(xhr.responseText)[0];
        document.getElementById('studentprofile-name').innerHTML = response.name;
        document.getElementById('studentprofile-id').innerHTML = response.id;
    },
    function(xhr) {
      //failure
      // TODO send alert
      return
    }
  );

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
