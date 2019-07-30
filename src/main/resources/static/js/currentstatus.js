function currentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');
  var period = Cookies.getJSON('period');

  //bail if we dont have all of the necessary cookies
  if(apiKey == null || course == null || period == null) {
    console.log('lack necessary cookies to calculate current status');
    return;
  }

  // get students
  var getStudentListUrl = thisUrl() + '/student/' +
    '?courseId=' + course.id +
    '&apiKey=' + apiKey.key;
  request(getStudentListUrl,
    function(xhr) {
      // select people who have a student permission
      var students = JSON.parse(xhr.responseText);

      // now we must get the encounters within this time range and at this course
      var getEncounterListUrl = thisUrl() + '/encounter/' +
        '?type=in' +
        '&courseId=' + course.id +
        '&minTime' + period.startTime +
        '&maxTime' + period.endTime +
        '&apiKey=' + apiKey.key;
      request(getEncounterListUrl,
        // success
        function(xhr) {
          var table = document.getElementById('current-status-table');
          //blank table
          table.innerHTML='';

          // now we must compare to check if each one of these works
          var studentencounters = JSON.parse(xhr.responseText);

          for(var i = 0; i < students.length; i++) {
            var student = students[i];
            var text = '<span class="fa fa-times"></span>';
            var bgcolor = '#ffcccc';
            var fgcolor = '#ff0000';
            // if we can find it
            if(studentencounters.filter(e => e.student.id==student.id).length > 0) {
              text =  '<span class="fa fa-check"></span>'
              bgcolor = '#ccffcc';
              fgcolor = '#00ff00';
            }

            table.insertRow(0).innerHTML=
              ('<tr>' +
              '<td>' + student.name + '</td>' +
              '<td>' + student.id + '</td>' +
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

