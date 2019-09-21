"use strict"

var student = null;
var studentIrregularities = null;

function loadStudentIrregularities() {
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
    },
    function(xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
      return
    }
  );
}

$(document).ready(function() {
  loadStudentIrregularities();

  var chartOne = document.getElementById('chart-one');
  var chartTwo = document.getElementById('chart-two');

  var myChart = new Chart(chartTwo, {
      type: 'bar',
      data: {
          labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
          datasets: [{
              label: '# of Votes',
              data: [12, 19, 3, 5, 2, 3],
              backgroundColor: [
                  'rgba(255, 99, 132, 0.2)',
                  'rgba(54, 162, 235, 0.2)',
                  'rgba(255, 206, 86, 0.2)',
                  'rgba(75, 192, 192, 0.2)',
                  'rgba(153, 102, 255, 0.2)',
                  'rgba(255, 159, 64, 0.2)'
              ],
              borderColor: [
                  'rgba(255, 99, 132, 1)',
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
