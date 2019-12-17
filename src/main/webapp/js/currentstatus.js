function currentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var period = Cookies.getJSON('period');
  var course = period == null ? null : Cookies.getJSON('courses').filter(c => c.period == period.number)[0];

  var table = document.getElementById('current-status-table');

  //bail if we dont have all of the necessary cookies
  if (apiKey == null || course == null || period == null) {
    console.log('lack necessary cookies to calculate current status');
    table.innerHTML = '';
    return;
  }

  if(course.type != 'Class Period') {
    fetch(`${apiUrl()}/misc/registeredForCours/?courseId=${course.id}&apiKey=${apiKey.key}`)
      .then(parseResponse)
      .then(function (students) {
        fetch(`${apiUrl()}/irregularity/?courseId=${course.id}&periodId=${period.id}&apiKey=${apiKey.key}`)
          .then(parseResponse)
          .then(function(irregularities) {
              table.innerHTML = '';
              students.sort((a, b) => (a.name > b.name) ? 1 : -1)
              for (let i = 0; i < students.length; i++) {
                let text = '<span class="fa fa-check"></span>'
                let bgcolor = '#ccffcc';
                let fgcolor = '#00ff00';
                let student = students[i];

                var irregularity = irregularities.filter(irr => irr.student.id == student.id).pop();
                console.log(irregularity);
                console.log(student);
                let type = irregularity == null ? null : irregularity.type;
                if (type == 'Absent') {
                  text = '<span class="fa fa-times"></span>';
                  bgcolor = '#ffcccc';
                  fgcolor = '#ff0000';
                } else if (type == 'Tardy') {
                  text = '<span class="fa fa-check"></span>';
                  bgcolor = '#ffffcc';
                  fgcolor = '#cccc00';
                } else if (type == 'Left Early') {
                  text = '<span class="fa fa-times"></span>';
                  bgcolor = '#ccffff';
                  fgcolor = '#00cccc';
                } else if (type == 'Left Temporarily') {
                  text = '<span class="fa fa-check"></span>';
                  bgcolor = '#ccffff';
                  fgcolor = '#00cccc';
                }

                // put values in table
                let newrow = table.insertRow(0);
                newrow.innerHTML =
                  `<td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
                   <td>${student.id}</td>
                   <td style="background-color:${bgcolor};color:${fgcolor}">${text}</td>`;
                newrow.id = 'id-' + student.id;
              }
          })
          .catch(function(err) {
            givePermError('Failed to correctly fetch irregularity data, try refreshing');
          });
      })
      .catch(function(err) {
            givePermError('Failed to correctly fetch course data, try refreshing');
      });
  }
}

$(document).ready(function () {
  currentStatus();
  setInterval(currentStatus, 5000);
})

