"use strict"

//gets new data from server and inserts it at the beginning
function getRecentActivity() {
  let apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    return;
  }

  let table = document.getElementById('response-table-body');
  fetch(`${apiUrl()}/session/?apiKey=${apiKey.key}&count=100`)
    .then(parseResponse)
    .then(function(sessions) {
      // Sort list of sessions
      sessions.sort((a,b) => {
        let valueA = a.hasOut ? a.outEncounter.time : a.inEncounter.time;
        let valueB = b.hasOut ? b.outEncounter.time : b.inEncounter.time;
        return valueA - valueB;
      });
      // Clear table
      table.innerHTML = '';
      // Add sessions
      sessions.forEach((session, index) => {
        let outEncounterTime = (session.outEncounter === null) ? 'N/A' : moment(session.outEncounter.time, 'x').fromNow();
        table.insertRow(0).innerHTML = `
                <tr>
                  <td>${linkRelative(session.inEncounter.student.name, '/studentprofile.html?studentId=' + session.inEncounter.student.id)}</td>
                  <td>${session.inEncounter.student.id}</td>
                  <td>${linkRelative(session.inEncounter.location.name, '/locationprofile.html?locationId='+session.inEncounter.location.id)}</td>
                  <td>${moment(session.inEncounter.time, 'x').fromNow()}</td>
                  <td>${outEncounterTime}</td>
                </tr>`;
      });
    })
    .catch(function(err) {
      console.log(err);
      giveTempError('Could not fetch necessary information.');
    });
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
