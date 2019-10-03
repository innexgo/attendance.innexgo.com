"use strict"

function loadData() {
  var apiKey = Cookies.getJSON('apiKey');
  var searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('userId')) {
    giveAlert('No user query');
    return;
  }

  var userId = searchParams.get('userId');
  const position = {
    0:'Administrator',
    1:'Teacher'
  }
  request(thisUrl() + '/user/' +
    '?userId=' + userId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var userResponse = JSON.parse(xhr.responseText)[0];
      document.getElementById('user-name').innerHTML = userResponse.name;
      document.getElementById('user-email').innerHTML = 'Email: ' + userResponse.email;
      document.getElementById('user-position').innerHTML = position[userResponse.ring];
      if (userResponse.ring == 0){
      var element = document.getElementById('user-courses-table');
      element.parentNode.removeChild(element);
  }
    },
    function (xhr) {
      //failure
      giveAlert('Failed to connect to server.', 'alert-danger');
      return
    }
  );

  request(thisUrl() + '/course/' +
    '?teacherId=' + userId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var teacherCourses = JSON.parse(xhr.responseText);
      var coursePeriods = [];
      for (var p = 1; p <= 7; p++) {
        var currentPeriodList = teacherCourses.filter(c => c.period == p);
        coursePeriods.push(currentPeriodList.length == 0 ? null : currentPeriodList[0])
      }
      var classTable = document.getElementById('user-courses');
      coursePeriods.sort(function (a, b) {
        if (a != null && b != null) {
          return b.period-a.period;
        } else {return -1};
      });
      coursePeriods.forEach(function (course) {
        if (course != null) {
          var newrow = classTable.insertRow(0);
          newrow.innerHTML =
            ('<td>' + course.period + '</td>' +
             '<td>' + linkRelative(course.subject, '/courseprofile.html?courseId='+course.id) + '</td>' +
             '<td>' + linkRelative(course.location.name, '/locationprofile.html?locationId='+location.id) + '</td>');
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
