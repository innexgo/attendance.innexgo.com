"use strict"

//gets new data from server and inserts it at the beginning
function getRecentActivity() {
  var apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('apiKey not present. Sending to login page.');
    window.location.replace(staticUrl() + '/login.html');
    return;
  }

  request(apiUrl() +
    '/session/' +
    '?apiKey=' + apiKey.key +
    '&teacherId=' + apiKey.user.id +
    '&count=' + '100',
    function (xhr) {
    var table = document.getElementById('response-table-body');
    table.innerHTML = '';
    var sessionList = JSON.parse(xhr.responseText);
    //go backwards to maintain order
    var accumulator = [];
    sessionList.reduce((accumulator, currentValue) => {
      accumulator += currentValue.inEncounter;
      if (currentValue.hasOut) {
        accumulator += currentValue.outEncounter;
      }
      return accumulator;
    }, 0);
    sessionList.sort((a,b) => {
      var valueA = a.hasOut ? a.outEncounter.time : a.inEncounter.time;
      var valueB = b.hasOut ? b.outEncounter.time : b.inEncounter.time;
      return valueA - valueB;
    });
    console.log(sessionList);
    sessionList.forEach((session, index) => {
      var outEncounterTime = (session.outEncounter === null) ? '' : moment(session.outEncounter.time, 'x').format('L LTS');
      table.insertRow(0).innerHTML =
        ('<tr>' +
          '<td>' + (index+1) + '</td>' +
          '<td>' + linkRelative(session.inEncounter.student.name, '/studentprofile.html/?studentId=' + session.inEncounter.student.id) + '</td>' +
          '<td>' + session.inEncounter.student.id + '</td>' +
          '<td>' + session.course.period + '</td>' +
          '<td>' + linkRelative(session.course.teacher.name, '/userprofile.html/?userId=' + session.course.teacher.id) + '</td>' +
          '<td>' + session.course.location.name + '</td>' +
          '<td>' + moment(session.inEncounter.time, 'x').format('L LTS') + '</td>' +
          '<td>' + outEncounterTime + '</td>' +
          '</tr>');
    }
    )},
    function (xhr) {
      console.log(xhr);
    }
  );
}

$(document).ready(function () {
  setInterval(getRecentActivity(), 5000);
});

//Bootstrap Popover - Alert Zones/Qucik Helps for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});
