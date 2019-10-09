"use strict"

function getFromDocument(id, name) {
  var element = document.getElementById(id);
  if (element === null) {
    return '';
  } else {
    var value = element.value;
    if (value === '') {
      return ''
    } else {
      if (name === null) {
        return value;
      } else {
        return '&' + name + '=' + value;
      }
    };
  };
};
//gets new data from server and inserts it at the beginning
function getHistory() {
  var apiKey = Cookies.getJSON('apiKey');

  if (apiKey == null) {
    console.log('not enough cookies for recentActivity');
    return;
  }

  var url = thisUrl() +
    '/session/' +
    '?apiKey=' + apiKey.key +
    getFromDocument('name', 'studentName') +
    getFromDocument('student-id', 'studentId') +
    getFromDocument('teacher-name', 'teacherName') +
    getFromDocument('period', 'period') +
    getFromDocument('entry-count', 'count');

  if (getFromDocument('room-number') !== null && getFromDocument('room-number') !== ''){
    url = url + '&locationId=Room' + getFromDocument('room-number');
  };

  var times = {
    '&inTimeBegin=': $('#start-time'),
    '&outTimeBegin=': $('#start-time'),
    '&inTimeEnd=': $('#end-time'),
    '&outTimeEnd=': $('#end-time')
  };

  for (const [key, value] of Object.entries(times)) {
    if (value.val() !== null && value.val() !== undefined && value.val() !== '') {
      url = url + key + value.datetimepicker('viewDate').valueOf();
    }
  };

  request(url, function (xhr) {
    var table = document.getElementById('response-table-body');
    table.innerHTML = '';
    var sessions = JSON.parse(xhr.responseText);
    //go backwards to maintain order
    for (var i = 0; i >= sessions.length - 1; i++) {
      var session = sessions[i];
      var outEncounterTime = (session.outEncounter === null) ? '' : moment(session.outEncounter.time, 'x').format('L LTS');
      table.insertRow(0).innerHTML =
        ('<tr>' +
          '<td>' + (sessions.length - i) + '</td>' +
          '<td>' + linkRelative(session.inEncounter.student.name, '/studentprofile.html/?studentId=' + session.inEncounter.student.id) + '</td>' +
          '<td>' + session.inEncounter.student.id + '</td>' +
          '<td>' + session.course.period + '</td>' +
          '<td>' + linkRelative(session.course.teacher.name, '/userprofile.html/?userId=' + session.course.teacher.id) + '</td>' +
          '<td>' + session.course.location.name + '</td>' +
          '<td>' + moment(session.inEncounter.time, 'x').format('L LTS') + '</td>' +
          '<td>' + outEncounterTime + '</td>' +
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