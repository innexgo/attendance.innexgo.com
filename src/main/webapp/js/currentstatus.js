function currentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var period = Cookies.getJSON('period');
  var course = period == null ? null : Cookies.getJSON('courses').filter(c => c.period == period.period)[0];

  var table = document.getElementById('current-status-table');

  //bail if we dont have all of the necessary cookies
  if (apiKey == null || course == null || period == null) {
    console.log('lack necessary cookies to calculate current status');
    table.innerHTML = '';
    return;
  }

  // get students
  request(thisUrl() + '/student/' +
    '?courseId=' + course.id +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var students = JSON.parse(xhr.responseText);
      // get irregularities
      request(thisUrl() + '/irregularity/' +
        '?courseId=' + course.id +
        '&periodId=' + period.id +
        '&apiKey=' + apiKey.key,
        function (xhr) {
          var irregularities = JSON.parse(xhr.responseText).sort((a, b) => (a.time > b.time) ? 1 : -1);

          //blank table
          table.innerHTML = '';

          for (var i = 0; i < students.length; i++) {
            var text = '<span class="fa fa-check"></span>'
            var bgcolor = '#ccffcc';
            var fgcolor = '#00ff00';
            var student = students[i];

            var irregularity = irregularities.filter(i => i.student.id == student.id).pop();
            var type = irregularity == null ? null : irregularity.type;
            if (type == 'absent') {
              text = '<span class="fa fa-times"></span>';
              bgcolor = '#ffcccc';
              fgcolor = '#ff0000';
            } else if (type == 'tardy') {
              text = '<span class="fa fa-check"></span>';
              bgcolor = '#ffffcc';
              fgcolor = '#ffff00';
            } else if (type == 'left_early') {
              text = '<span class="fa fa-sign-out-alt"></span>';
              bgcolor = '#ccffff';
              fgcolor = '#00ffff';
            } else if (type == 'left_temporarily') {
              text = '<span class="fa fa-check"></span>';
              bgcolor = '#ccffff';
              fgcolor = '#00ffff';
            }

            // put values in table
            var newrow = table.insertRow(0);
            newrow.innerHTML =
              ('<td>' + student.name + '</td>' +
                '<td>' + student.id + '</td>' +
                '<td style="background-color:' + bgcolor + ';color:' + fgcolor + '">' + text + '</td>');
            newrow.className = 'id-' + student.id;
          }
        },
        //failure
        function (xhr) {
          return;
        }
      );
    },
    //failure
    function (xhr) {
      return;
    }
  );
}

$(document).ready(function () {
  currentStatus();
  setInterval(currentStatus, 5000);
})

