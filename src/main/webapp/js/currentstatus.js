async function currentStatus() {
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let courses = Cookies.getJSON('courses').sort((a, b) => (a.period > b.period ? -1 : 1));
  let course = period == null ? null : courses.filter(c => c.period == period.number)[0];

  let table = document.getElementById('current-status-table');
  let time = moment().valueOf();

  //bail if we dont have all of the necessary cookies
  if (apiKey == null || period == null) {
    console.log('lack necessary cookies to calculate current status');
    table.innerHTML = '';
    return;
  }

  if(course != null) {
    try {
      let students = await fetchJson(`${apiUrl()}/misc/registeredForCourse/?courseId=${course.id}&time=${time}&apiKey=${apiKey.key}`);
      let irregularities = await fetchJson(`${apiUrl()}/irregularity/?courseId=${course.id}&periodId=${period.id}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}&offset=0&count=${INT32_MAX}`);

      table.innerHTML = '';
      students.sort((a, b) => (a.name > b.name) ? -1 : 1)
      for (let i = 0; i < students.length; i++) {
        let text = '<span class="fa fa-check"></span>'
        let bgcolor = '#ccffcc';
        let fgcolor = '#00ff00';
        let student = students[i];

        let irregularity = irregularities.filter(irr => irr.student.id == student.id).pop();
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
    } catch(err) {
      console.log(err);
      giveTempError('Failed to get current status.');
    }
  } else {
    let locationId = Cookies.getJSON('default-locationid');
    if(locationId != null) {
      try {
        let students = await fetchJson(`${apiUrl()}/misc/present/?locationId=${locationId}&time=${time}&apiKey=${apiKey.key}`);
        let text = '<span class="fa fa-check"></span>'
        let bgcolor = '#ccffcc';
        let fgcolor = '#00ff00';
        // Clear table
        table.innerHTML = '';
        // Students
        students.forEach(student => $('#current-status-table').append(
              `<td>${linkRelative(student.name, '/studentprofile.html?studentId='+student.id)}</td>
               <td>${student.id}</td>
               <td style="background-color:${bgcolor};color:${fgcolor}">${text}</td>`)
        );
      } catch(err) {
        console.log(err);
        giveTempError('Failed to correctly fetch student data, try refreshing');
      }
    }
  }
}

$(document).ready(async function () {
  while(true) {
    await currentStatus();
    await sleep(5000);
  }
})

