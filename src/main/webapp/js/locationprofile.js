"use strict"

/* globals moment getLocalJson fetchJson linkRelative apiUrl giveTempError givePermError sleep INT32_MAX */

// Forever runs and updates currentStatus
async function currentStatus(locationId) {
  let apiKey = getLocalJson('apiKey');
  let table = document.getElementById('current-status-table');
  for (; ;) {
    let period = getLocalJson('period');

    let time = moment().valueOf();

    //bail if we dont have all of the necessary cookies
    if (apiKey == null || period == null) {
      console.log('lack necessary cookies to calculate current status');
      table.innerHTML = '';
      return;
    }

    try {
      let students = await fetchJson(`${apiUrl()}/misc/present/?locationId=${locationId}&time=${time}&apiKey=${apiKey.key}`);
      let text = '<span class="fa fa-check"></span>'
      let bgcolor = '#ccffcc';
      let fgcolor = '#00ff00';
      // Clear table
      table.innerHTML = '';
      // Students
      if (students.length == 0) {
        table.innerHTML = `<b>No Students Currently at Location</b>`;
      } else {
        students.forEach(student => $('#current-status-table').append(
          `<tr><td>${linkRelative(student.name, '/studentprofile.html?studentId=' + student.id)}</td>
                   <td>${student.id}</td>
                   <td style="background-color:${bgcolor};color:${fgcolor}">${text}</td></tr>`)
        );
      }
    } catch (err) {
      console.log(err);
      giveTempError('Failed to correctly fetch student data, try refreshing');
    }
    // Wait 5 seconds before updating again
    await sleep(5000);
  }
}


async function loadData() {
  let apiKey = getLocalJson('apiKey');
  if (apiKey == null) {
    console.log('Not signed in');
    return;
  }

  let semester = getLocalJson('semester');
  if (semester == null) {
    console.log('No semester, bailing');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('locationId')) {
    givePermError('Page loaded with invalid parameters.');
    return;
  }

  var locationId = searchParams.get('locationId');

  try {
    let location = (await fetchJson(`${apiUrl()}/location/?locationId=${locationId}&offset=0&count=1&apiKey=${apiKey.key}`))[0]
    if (location == null) {
      throw new Error('Location Id undefined in database!');
    }
    document.getElementById('location-name').innerHTML = location.name;
  } catch (err) {
    console.log(err);
    givePermError('Page loaded with invalid location id.');
  }


  currentStatus(locationId);

  try {
    // One liner time
    (await fetchJson(`${apiUrl()}/course/?locationId=${locationId}&semesterStartTime=${semester.startTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
      .sort((a, b) => (a.period > b.period) ? 1 : -1)
      .forEach(course => $('#location-courses').append(`
            <tr>
              <td>${course.period}</td>
              <td>${linkRelative(course.subject, '/courseprofile.html?courseId=' + course.id)}</td>
              <td>${linkRelative(course.teacher.name, '/userprofile.html?userId=' + course.teacher.id)}</td>
            </tr>`));
  } catch (err) {
    console.log(err);
    givePermError('Error fetching courses.');
  }
}

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function () {
  $('[data-toggle="popover"]').popover({
    trigger: 'hover'
  });
});

$(document).ready(function () {
  loadData();
})

