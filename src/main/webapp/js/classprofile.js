"use strict"


function loadClassProfile() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  var table = document.getElementById('classprofile-table');

  var searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('courseId') || !searchParams.has('periodId')) {
    console.log('page not loaded with right params');
    return;
  }

  var courseId = searchParams.get('courseId');
  var periodId = searchParams.get('periodId');

  // get students
  request(apiUrl() + '/student/' +
    '?courseId=' + courseId +
    '&apiKey=' + apiKey.key,
    function (xhr) {
      var students = JSON.parse(xhr.responseText);
      // get irregularities
      request(apiUrl() + '/irregularity/' +
        '?courseId=' + courseId +
        '&periodId=' + periodId +
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
            if (type == 'Absent') {
              text = '<span class="fa fa-times"></span>';
              bgcolor = '#ffcccc';
              fgcolor = '#ff0000';
            } else if (type == 'Tardy') {
              text = '<span class="fa fa-check"></span>';
              bgcolor = '#ffffcc';
              fgcolor = '#ffff00';
            } else if (type == 'Left Early') {
              text = '<span class="fa fa-sign-out-alt"></span>';
              bgcolor = '#ccffff';
              fgcolor = '#00ffff';
            } else if (type == 'Left Temporarily') {
              text = '<span class="fa fa-check"></span>';
              bgcolor = '#ccffff';
              fgcolor = '#00ffff';
            }

            // put values in table
            var newrow = table.insertRow(0);
            newrow.innerHTML =
              ('<td>' + linkRelative(student.name, '/studentprofile.html?studentId='+student.id)+ '</td>' +
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

function loadClassText() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  var table = document.getElementById('classprofile-table');

  var searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('courseId') || !searchParams.has('periodId')) {
    console.log('page not loaded with right params');
    return;
  }

  var courseId = searchParams.get('courseId');
  var periodId = searchParams.get('periodId');

  var text = document.getElementById('classprofile-text');

  request(apiUrl() + '/course/' +
    '?courseId='+courseId +
    '&apiKey='+apiKey.key,
    function(xhr) {
      var courses = JSON.parse(xhr.responseText);
      // if there are no courses with this idea
      if(courses.length < 1)
      {
        console.log('failed to get course');
        return
      }

      var course = courses[0];

      // now get the period
      request(apiUrl() + '/period/' +
        '?apiKey='+apiKey.key +
        '&periodId='+periodId,
        function(xhr) {
          var periods = JSON.parse(xhr.responseText);

          if(periods.length < 1) {
            console.log('failed to get periods');
            return
          }

          var period = periods[0];

          text.innerHTML = 'View students who attended ' + ordinal_suffix_of(course.period) +
            ' period ' + course.subject + ' ('+course.teacher.name+') on ' +
            moment(period.startTime).format('dddd, MMMM Do YYYY') + '.';
        },
        function(xhr) {
          giveAlert('Failed to get period information.', 'alert-danger', true);
          return;
        }
      );
    },
    function(xhr) {
      giveAlert('Failed to get course information.', 'alert-danger', true);
      return;
    }
  );
}


$(document).ready(function () {
  loadClassText();
  loadClassProfile();
})

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});
