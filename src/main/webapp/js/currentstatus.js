function currentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var period = Cookies.getJSON('period');
  var course = period == null ? null : Cookies.getJSON('courses').filter(c => c.period == period.period)[0];

  var table = document.getElementById('current-status-table');

  //bail if we dont have all of the necessary cookies
  if(apiKey == null || course == null || period == null) {
    console.log('lack necessary cookies to calculate current status');
    table.innerHTML='';
    return;
  }


  // get students

  // get irregularities
  request(thisUrl() + '/irregularity/' +
    '?courseId=' + course.id +
    '&periodId=' + period.id +
    '&apiKey=' + apiKey.key,
    function(xhr) {
      var irregularities = JSON.parse(xhr.responseText);

      //blank table
      table.innerHTML='';

      for(var i = 0; i < irregularities.length; i++) {
        var status = statuses[i].type;
        var student = irregularities[i].student;
        var text = '<span class="fa fa-times"></span>';
        var bgcolor = '#ffcccc';
        var fgcolor = '#ff0000';
        // if we can find it
        if(status == 'present') {
          text =  '<span class="fa fa-check"></span>'
          bgcolor = '#ccffcc';
          fgcolor = '#00ff00';
        } else if (status == 'tardy') {
          text =  '<span class="fa fa-check"></span>'
          bgcolor = '#ffffcc';
          fgcolor = '#ffff00';
        } else if (status == 'absent') {
          text =  '<span class="fa fa-times"></span>'
          bgcolor = '#ffcccc';
          fgcolor = '#ff0000';
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
}

$(document).ready(function() {
  currentStatus();
  setInterval(currentStatus, 5000);
})

