"use strict"
$(document).ready(function(){

  var ctx = document.getElementById("doughnutChart").getContext('2d');
  var ctxD = document.getElementById("doughnutChart").getContext('2d');
  var myLineChart = new Chart(ctxD, {
    type: 'doughnut',
    data: {
      labels: ["% Absence","% Late", "% Present"],
      datasets: [{
        data: [13,10,76],
        backgroundColor: ["#F7464A","#ffff99","#DCEDC1"],
        hoverBackgroundColor: ["#FF5A5E","#ffffe5","#E6F2D3"]
      }]
    },
    options: {
      responsive: true,
      legend: {
        position: 'right',
        labels: {
          padding: 20,
          boxWidth: 10
        }
      },
    }
  });

  var ctxL = document.getElementById("lineChart").getContext('2d');
  var myLineChart = new Chart(ctxL, {
    type: 'line',
    data: {
      labels: ["August", "September", "October", "November", "December", "January", "February", "March", "April", "May"],
      datasets: [{
        label: "Student's Cumulative Absence Record",
        data: [1, 2, 5, 5, 8, 14, 15, 16, 19, 24],
        backgroundColor: [
'rgba(105, 0, 132, .2)',
        ],
        borderColor: [
'rgba(200, 99, 132, .7)',
        ],
        borderWidth: 2
      },
        {
          label: "Average Cumulative Absence Record",
          data: [1, 1, 3, 4, 6, 6, 8, 9, 14, 17],
          backgroundColor: [
'rgba(0, 137, 132, .2)',
          ],
          borderColor: [
'rgba(0, 10, 130, .7)',
          ],
          borderWidth: 2
        }
      ]
    },
    options: {
      responsive: true
    }
  });

});
