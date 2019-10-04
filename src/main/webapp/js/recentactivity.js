"use strict"

function getFromDocument(name, id) {
  var value = Document.getElementById(id).value;
  if (!isEmpty(value)) {
    return '&' + name + value;
  }
  return '';
};

//gets new data from server and inserts it at the beginning
function recentActivity() {
  var apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null || course == null) {
    console.log('not enough cookies for recentActivity');
    return;
  }

  alert(name);
  var url = thisUrl() + '/session/?count=100' +
    '&courseId=' + course.id +
    '&apiKey=' + apiKey.key +
    '&studentName' + getFromDocument('name') +
    '&studentId' + getFromDocument('id') +
    '&period' + getFromDocument('period') +
    '&teacherName' + getFromDocument('teacher') +
    '&room' + getFromDocument('room') +
    '&room' + getFromDocument('room') +
    '&count' + getFromDocument('count') +
    '&startTime' + $('#startTime').datetimepicker('viewDate').valueOf() +
    '&startTime' + $('#startTime').datetimepicker('viewDate').valueOf();

  request(url, function (xhr) {
    // clear table
    var table = document.getElementById('recentActivityContent');
    table.innerHTML = '';

    var sessions = JSON.parse(xhr.responseText);
    console.log(sessions)
    //go backwards to maintain order
    for (var i = sessions.length - 1; i >= 0; i--) {
      var session = sessions[i];
      table.insertRow(0).innerHTML =
        ('<tr>' +
          '<td>' + (sessions.length - i) + '</td>' +
          '<td>' + linkRelative(session.inEncounter.student.name, '/studentprofile.html/?studentId=' + session.inEncounter.student.id) + '</td>' +
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
    function (xhr) {
      console.log(xhr);
    }
  );
}

$(document).ready(function () {
  /* REQUIRED FOR TEMPUS DOMINUS ON PAIN OF DEATH BY RECURSION*/
  $('#start-time').val('');
  $('#end-time').val('');
  $('*[data-toggle="datetimepicker"]').removeClass('datetimepicker-input');

  // Add class back to Tempus Dominus date picker
  $(document).on('toggle change hide keydown keyup focus', '*[data-toggle="datetimepicker"]', function () {
    $(this).addClass('datetimepicker-input').datetimepicker({
      icons: {
        time: 'far fa-clock',
        date: 'far fa-calendar-alt',
        up: 'fas fa-arrow-up',
        down: 'fas fa-arrow-down',
        previous: 'fas fa-chevron-left',
        next: 'fas fa-chevron-right',
        today: 'far fa-calendar-check',
        clear: 'far fa-trash',
        close: 'far fa-times'
      },
      maxDate: moment().endOf('day')
    });
  });
});