function currentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');

  // get students
  var getStudentListUrl = thisUrl() + '/student/' +
    '?courseId=' + course.id +
    '&apiKey=' + apiKey.key;
  request(getStudentListUrl,
    function(xhr) {
      // TODO decide how to represent people in a schedule/ who teaches who

      // select people who have a student permission
      var studentschedules = JSON.parse(xhr.responseText);

      // now we request all the encounters which occur at this location since the period started
      var getEncounterListUrl = thisUrl() + '/encounter/' +
        '?type=in' +
        '&locationId=' + schedule.location.id +
        '&managerId=' + schedule.user.id +
        '&minDate=' + getPeriodStart(schedule.period) + //TODO what if someone signs in early
        '&apiKey=' + apiKey.key;
      request(getEncounterListUrl,
        // success
        function(xhr) {
          var table = document.getElementById('current-status-table');
          //blank table
          table.innerHTML='';

          // now we must compare to check if each one of these works
          var studentencounters = JSON.parse(xhr.responseText);

          for(var i = 0; i < studentschedules.length; i++) {
            var schedstudent = studentschedules[i];
            var text = '<span class="fa fa-times"></span>';
            var bgcolor = '#ffcccc';
            var fgcolor = '#ff0000';
            // if we can find it
            if(studentencounters.filter(e=>e.user.id==schedstudent.user.id).length > 0) {
              text =  '<span class="fa fa-check"></span>'
              bgcolor = '#ccffcc';
              fgcolor = '#00ff00';
            }

            table.insertRow(0).innerHTML=
              ('<tr>' +
              '<td>' + schedstudent.user.name + '</td>' +
              '<td>' + schedstudent.user.id + '</td>' +
              '<td style="background-color:'+bgcolor+';color:'+fgcolor+'">' + text + '</td>' +
              '</tr>');
          }
        },
        //failure
        function(xhr) {
          return;
        }
      );
    },
    //failure
    function(xhr) {
      return;
    }
  );
}

$(document).ready(function() {
  currentStatus();
  setInterval(currentStatus, 5000);
})

