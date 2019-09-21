

//gets new data from server and inserts it at the beginning
function recentActivity() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');

  if(apiKey == null || course == null) {
    console.log('not enough cookies for recentActivity');
    return;
  }

  var url = thisUrl()+'/session/?count=100' +
    '&courseId=' + course.id +
    '&apiKey='+ apiKey.key;
  request(url,
    function(xhr){
      // clear table
      var tableContent = document.getElementById('recentActivityContent');
      table.innerHTML = '';

      var sessions = JSON.parse(xhr.responseText);
      //go backwards to maintain order
      for(var i = sessions.length-1; i >= 0; i--) {
        var session = sessions[i];
        table.insertRow(0).innerHTML=
          ('<tr>' +
            '<td>' + (sessions.length - i) + '</td>' +
            '<td>' + session.inEncounter.student.name + '</td>' +
            '<td>' + session.inEncounter.student.id + '</td>' +
            '<td>' + session.course.period + '</td>' +
            '<td>' + session.course.teacher.name + '</td>' +
            '<td>' + '</td>' +
            '<td>' + session.course.location.name + '</td>' +
            '<td>' + session.inEncounter.time + '</td>' +
            '<td>' + session.outEncounter.time + '</td>' +
            '</tr>');
      }
    },
    function(xhr) {
      console.log(xhr);
    }
  );
}

$(document).ready(function () {
  recentActivity();
  setInterval(recentActivity, 5000);
})
