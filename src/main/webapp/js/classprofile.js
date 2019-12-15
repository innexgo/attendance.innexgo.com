"use strict"

function loadClassProfile() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  let table = document.getElementById('classprofile-table');

  let searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('courseId') || !searchParams.has('periodId')) {
    console.log('page not loaded with right params');
    return;
  }

  let courseId = searchParams.get('courseId');
  let periodStartTime = searchParams.get('periodStartTime');

  fetch(`${apiUrl()}/misc/registeredForCourse/?courseId=${courseId}&time=${periodStartTime}&apiKey=${apiKey.key}`)
    .then(parseResponse)
    .then(function(students) {
      fetch(`${apiUrl()}/irregularity/?courseId=${courseId}&periodId=${periodId}&apiKey=${apiKey.key}`)
        .then(parseResponse)
        .then(function(irregularities) {
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
        })
        .catch(function(err) {
          givePermErr('Failed to get data for irregularities');
        });
    })
    .catch(function(err) {
      givePermErr('Failed to get data for students');
    });
}

async function loadClassText() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  var table = document.getElementById('classprofile-table');
  var searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('courseId') || !searchParams.has('periodStartTime')) {
    console.log('page not loaded with right params');
    return;
  }

  var courseId = searchParams.get('courseId');
  var periodStartTime = searchParams.get('periodStartTime');

  var text = document.getElementById('classprofile-text');


  fetch(`${apiUrl()}/course/?courseId=${courseId}&apiKey=${apiKey.key}`)
    .then(parseResponse)
    .then(function(courses) {
      let course = courses[0];
      fetch(`${apiUrl()}/period/?periodStartTime=${periodStartTime}&apiKey=${apiKey.key}`)
        .then(parseResponse)
        .then(function(periods) {
          let period = periods[0];
          text.innerHTML = 'View students who attended ' + ordinal_suffix_of(course.period) +
            ' period ' + course.subject + ' ('+course.teacher.name+') on ' +
            moment(period.startTime).format('dddd, MMMM Do YYYY') + '.';
        })
        .catch(function(err) {
          givePermError('Could not get period information');
        })
    })
    .catch(function(err) {
      givePermError('Could not get course information');
    })
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
