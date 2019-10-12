"use strict"

function loadData() {
  var apiKey = Cookies.getJSON('apiKey');
  var searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('locationId')) {
    giveAlert('No user query');
    return;
  }
  var locationId = searchParams.get('locationId');

  request(apiUrl() + '/course/' +
    '?locationId=' + locationId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var locationData = JSON.parse(xhr.responseText);
      locationData.sort(function (a, b) {
        if (a != null && b != null) {
          return b.period-a.period;
        } else {return -1};
      });
      document.getElementById('location-name').innerHTML = locationData[0].location.name;
      var classTable = document.getElementById('location-courses');
      locationData.forEach(function (course) {
        if (course != null) {
          var newrow = classTable.insertRow(0);
          newrow.innerHTML =
            ('<td>' + course.period + '</td>' +
             '<td>' + linkRelative(course.subject, '/courseprofile.html?courseId='+course.id) + '</td>' +
             '<td>' + linkRelative(course.teacher.name, '/userprofile.html?userId='+course.teacher.id) + '</td>');
        }
      });
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
      return;
    }
  );
};

$(document).ready(function() {
  loadData();
})
