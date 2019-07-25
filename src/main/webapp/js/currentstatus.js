function currentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');


  // get students
  var getStudentListUrl = thisUrl() + '/student/' +
    '?courseId=' + course.id +
    '&apiKey=' + apiKey.key;
  request(getStudentListUrl,
    function(xhr) {
      // select people who have a student permission
      var students = JSON.parse(xhr.responseText);

      // now we get the min and max time of the current period
      var getTimeUrl = thisUrl() + '/period/' +
        '?minTime=' + moment().unix() +
        '&maxTime=' + moment().unix() +
        '&apiKey=' + apiKey.key;

      request(getTimeUrl,
        //success
        function(xhr) {
          var periods = JSON.parse(xhr.responseText);
          if

      // now we request all the encounters where this was the course and happened between 
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

