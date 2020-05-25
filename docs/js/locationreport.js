"use strict"

/* globals moment Cookies fetchJson linkRelative apiUrl giveTempError givePermError sleep INT32_MAX */

// Forever runs and updates currentStatus
async function currentStatus(locationId) {
  const makeEntry = (student, bgcolor, fgcolor, faClass, toolTip) =>
    `<tr>
        <td>${linkRelative(student.name, '/studentreport.html?studentId=' + student.id)}</td>
        <td>${student.id}</td>
        <td title="${toolTip}" style="background-color:${bgcolor};color:${fgcolor}">
            <span class="fa ${faClass}"></span>
        </td>
      </tr>`;

  let apiKey = Cookies.getJSON('apiKey');
  let table = $('#current-status-table');
  for (; ;) {
    let period = Cookies.getJSON('period');

    let time = moment().valueOf();

    //bail if we dont have all of the necessary cookies
    if (apiKey == null || period == null) {
      console.log('lack necessary cookies to calculate current status');
      table.innerHTML = '';
      return;
    }

    try {
      // Clear table
      table.innerHTML = '';
      // Students
      try {
        let students = await fetchJson(`${apiUrl()}/misc/present/?locationId=${locationId}&time=${time}&apiKey=${apiKey.key}`);
        // Clear table
        table[0].innerHTML = '';
        // Students
        if (students.length == 0) {
          table[0].innerHTML = `<b>No Students Currently in Classroom</b>`;
        } else {
          students.forEach(student => table.append(makeEntry(student, '#ccffcc', '#00ff00', 'fa-check', 'You don\'t have a scheduled class at the moment, but this student is signed into your classroom.')));
        }
      } catch (err) {
        console.log(err);
        giveTempError('Failed to correctly fetch student data, try refreshing');
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
  let apiKey = Cookies.getJSON('apiKey');
  if (apiKey == null) {
    console.log('Not signed in');
    return;
  }

  let semester = Cookies.getJSON('semester');
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
              <td>${linkRelative(course.subject, '/coursereport.html?courseId=' + course.id)}</td>
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

$(document).ready(function(){

  var ctxL = document.getElementById("lineChart2").getContext('2d');
  var myLineChart = new Chart(ctxL, {
    type: 'line',
    data: {
      labels: ["August", "September", "October", "November", "December", "January", "February", "March", "April", "May"],
      datasets: [{
        label: "Period 1",
        data: [7.2, 5.4, 4.5, 4.8, 4.5, 4.9, 7.1, 6.8, 7.5, 8.5],
        backgroundColor: [
          'rgb(0, 0, 0, .0)',
        ],
        borderColor: [
          'rgba(200, 99, 132, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Period 2",
        data: [3.5, 2.1, 1.5, 1.9, 2.3, 2.5, 2.1, 3.1, 3.4, 4.1],
        backgroundColor: [
            'rgb(0, 0, 0, .0)',
        ],
        borderColor: [
          'rgba(0, 10, 130, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Period 3",
        data: [6.4, 7.1, 5.2, 6.8, 7.1, 7.3, 7.4, 7.8, 8.3, 8.4],
        backgroundColor: [
          'rgb(0, 0, 0, .0)',
        ],
        borderColor: [
          'rgb(127, 255, 0, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Period 4",
        data: [6.7, 7.2, 6.8, 7.3, 7.6, 8.1, 8.2, 8.8, 9.0, 10.2],
        backgroundColor: [
          'rgb(0, 0, 0, .0)',
        ],
        borderColor: [
          'rgb(188, 143, 143, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Period 5",
        data: [1.2, 1.4, 1.8, 1.2, 1.7, 1.9, 2.0, 2.4, 2.2, 2.8],
        backgroundColor: [
          'rgb(0, 0, 0, .0)',
        ],
        borderColor: [
          'rgb(192, 192, 192, .7)',
        ],
        borderWidth: 2
      },
      {
        label: "Period 6",
        data: [5.6, 5.8, 6.7, 15.2, 7.2, 7.1, 8.1, 8.2, 9.1, 10.1],
        backgroundColor: [
          'rgb(0, 0, 0, .0)',
        ],
        borderColor: [
          'rgb(75, 0, 130, .7)',
        ],
        borderWidth: 2
      }
    ]
  },

    options: {
      responsive: true
    }
  });

});
