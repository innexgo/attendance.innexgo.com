"use strict"

const beepup = new Audio('assets/beepup.wav');
const beepdown = new Audio('assets/beepdown.wav');
const error = new Audio('assets/error.wav');

// Page we're on
let recentActivityPage = 0;

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function () {
  $('[data-toggle="popover"]').popover({
    trigger: 'hover'
  });
});


async function manualEncounter(studentId) {
  console.log('submitting encounter ' + studentId)
  console.log(studentId);
  document.getElementById('manual-studentid').value = '';
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let course = Cookies.getJSON('courses').filter(c => c.period == period.number)[0];

  // Let's try to determine the location
  let locationId = Cookies.getJSON('default-locationid');
  if (course != null) {
    locationId = course.location.id;
  }

  if (String(studentId) == String(NaN)) {
    giveTempError('What you entered wasn\'t a valid ID');
    return;
  }

  if (locationId == null) {
    // If it's still null tell the user to set the default location
    giveTempError('Please set default location in order to manually sign in students when class is not in session.');
    error.play();
    return;
  }

  try {
    let session = await fetchJson(
      `${apiUrl()}/misc/attends/?studentId=${studentId}&locationId=${locationId}&manual=true&apiKey=${apiKey.key}`);

    if (session.complete) {
      giveTempInfo(`Sucessfully logged ${session.inEncounter.student.name} out of ${session.inEncounter.location.name}`);
      beepdown.play();
    } else {
      giveTempSuccess(`Sucessfully logged ${session.inEncounter.student.name} in to ${session.inEncounter.location.name}`);
      beepup.play();
    }
  } catch (err) {
    console.log(err);
    giveTempError('Something went wrong while trying to sign you in.');
    error.play();
  }
}

// Helper function to only be able to enter numbers into the manual-studentId
function manualEntryFunction(event) {
  event = (event) ? event : window.event;
  let charCode = (event.which) ? event.which : event.keyCode;

  if (charCode == 13) {
    // If enter pressed
    manualEncounter(parseInt($('#manual-studentid').val()));
    return false;
  } else if (charCode > 31 && (charCode < 48 || charCode > 57)) {
    // If not a number
    return false;
  } else {
    // If it is a number
    return true;
  }
}

// Forever runs and updates locationOptions
async function locationOptions() {
  let apiKey = Cookies.getJSON('apiKey');
  while (true) {
    try {
      let locations = await fetchJson(`${apiUrl()}/location/?offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
      let locationSelect = $('#overview-locationid');

      // Add the options
      locationSelect.empty();
      locations.forEach(l => locationSelect.append(`<option value="${l.id}">${l.name}</option>`));

      // Set preselected option
      let defaultLocation = Cookies.get('default-locationid');
      if (defaultLocation == null) {
        // Preselect Disabled option
        locationSelect.prepend(
          `<option selected hidden disabled value="null">Select</option>`
        );
        locationSelect.val("null");
      } else {
        locationSelect.val(defaultLocation);
      }

      // On change, reload thing
      locationSelect.change(async function () {
        let selectedValue = $('#overview-locationid').val();
        if (selectedValue != null) {
          Cookies.set('default-locationid', selectedValue);
        } else {
          console.log('Can\'t set the locationId');
        }
      });

    } catch (err) {
      console.log(err);
      givePermError('Failed to load locations.');
    }
    let nextPeriod = Cookies.getJSON('nextPeriod');
    await sleep(nextPeriod.startTime - moment().valueOf());
  }
}

// Loads data in reverse chronological order into the page
async function recentActivityLoadPage(page) {
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let course = Cookies.getJSON('courses').filter(c => c.period == period.number)[0];
  let locationId = course != null ? course.location.id : Cookies.getJSON('default-locationid');
  if (location == null) {
    table.innerHTML = '<b>No Default Location Loaded</b>';
    return;
  }

  if (page == 0) {
    $('#recentactivity-events-new').attr("disabled", true);
  } else {
    $('#recentactivity-events-new').attr("disabled", false);
  }
  // Count of irregularities per page
  const c = 5;
  let sessions = await fetchJson(`${apiUrl()}/session/?apiKey=${apiKey.key}&locationId=${locationId}&offset=${c * page}&count=${c}`);

  // Clear table
  $('#recentactivity-events').empty();
  sessions.map(s => {
    // Convert the session into encounters, but keep the info about whether it was an in or out
    if (s.complete) {
      return [{encounter: s.inEncounter, in: true},
      {encounter: s.outEncounter, in: false}];
    } else {
      return {encounter: s.inEncounter, in: true};
    }
  })
    .flat() // Flatten the nested arrays
    .sort((a, b) => b.encounter.time - a.encounter.time) // Sort in Reverse Chronological order
    .forEach(e => $('#recentactivity-events').append(`
            <tr>
              <td>${linkRelative(e.encounter.student.name, '/studentprofile.html?studentId=' + e.encounter.student.id)}</td>
              <td>${e.encounter.student.id}</td>
              <td>${moment(e.encounter.time).fromNow()}</td>
              <td> <h4 style="color:${e.in
        ? '#66ff66'     // green if siginin
        : e.encounter.type == 'virtual'
          ? '#6666ff' // blue if virtual signout
          : '#ff6666' // red if signout
      }" class="fa ${e.in ? 'fa-sign-in-alt' : 'fa-sign-out-alt'}"></h4></td>
            </tr>`));

  if (sessions.length == c) {
    $('#recentactivity-events-old').attr("disabled", false);
  } else {
    // no more irregularity or something
    $('#recentactivity-events-old').attr("disabled", true);
    if (sessions.length == 0) {
      $('#recentactivity-events')[0].innerHTML = "<b>No Recent Activity</b>";
    }
  }
}

async function recentActivity() {
  let apiKey = Cookies.getJSON('apiKey');
  let table = $('recentactivity-events')[0];

  // Handle paging
  $('#recentactivity-events-new').click(async function () {
    recentActivityPage--;
    await recentActivityLoadPage(recentActivityPage);
  });
  $('#recentactivity-events-old').click(async function () {
    recentActivityPage++;
    await recentActivityLoadPage(recentActivityPage);
  });

  while (true) {
    try {
      recentActivityLoadPage(recentActivityPage);
    } catch (err) {
      console.log(err);
      giveTempError('Failed to fetch recent activity data.');
    }
    await sleep(5000);
  }
}

// Forever runs and updates currentStatus
async function currentStatus() {
  let apiKey = Cookies.getJSON('apiKey');
  let table = document.getElementById('current-status-table');
  while (true) {
    let period = Cookies.getJSON('period');
    let courses = Cookies.getJSON('courses').sort((a, b) => (a.period > b.period ? -1 : 1));
    let course = period == null ? null : courses.filter(c => c.period == period.number)[0];

    let time = moment().valueOf();

    //bail if we dont have all of the necessary cookies
    if (apiKey == null || period == null) {
      console.log('lack necessary cookies to calculate current status');
      table.innerHTML = '';
      return;
    }

    if (course != null) {
      try {
        let students = await fetchJson(`${apiUrl()}/misc/registeredForCourse/?courseId=${course.id}&time=${time}&apiKey=${apiKey.key}`);
        let irregularities = await fetchJson(`${apiUrl()}/irregularity/?courseId=${course.id}&periodStartTime=${period.startTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
        let presentStudents = await fetchJson(`${apiUrl()}/misc/present/?locationId=${course.location.id}&time=${time}&apiKey=${apiKey.key}`);

        // This statement is brute force. Oof to those who must read it
        // Is list of all present students who are not in students
        let visitors = presentStudents.filter(v => students.filter(s => s.id == v.id).length == 0)
        let presentEnrolledStudents = presentStudents.filter(v => students.filter(s => s.id == v.id).length != 0)

        $('#current-status-percent-attendance')[0].innerHTML = `${presentEnrolledStudents.length}/${students.length}`;

        table.innerHTML = '';

        students.sort((a, b) => (a.name > b.name) ? 1 : -1)

        visitors.forEach(v => $('#current-status-table').append(
          `<tr><td>${linkRelative(v.name, '/studentprofile.html?studentId=' + v.id)}</td>
            <td>${v.id}</td>
            <td style="background-color:#ffccff;color:#cc00cc"><span class="fa fa-check"></span></td></tr>`));

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
          $('#current-status-table').append(
            `<tr>
                <td>${linkRelative(student.name, '/studentprofile.html?studentId=' + student.id)}</td>
                <td>${student.id}</td>
                <td style="background-color:${bgcolor};color:${fgcolor}">${text}</td>
             <tr>`);
        }
        if (table.innerHTML == '') {
          table.innerHTML = `<b>No Students Currently in Classroom</b>`;
        }
      } catch (err) {
        console.log(err);
        giveTempError('Failed to get current status.');
      }
    } else {
      // Clear attendance
      document.getElementById('current-status-percent-attendance').innerHTML = 'N/A';
      let locationId = Cookies.getJSON('default-locationid');
      if (locationId != null) {
        try {
          let students = await fetchJson(`${apiUrl()}/misc/present/?locationId=${locationId}&time=${time}&apiKey=${apiKey.key}`);
          let text = '<span class="fa fa-check"></span>'
          let bgcolor = '#ccffcc';
          let fgcolor = '#00ff00';
          // Clear table
          table.innerHTML = '';
          // Students
          if (students.length == 0) {
            table.innerHTML = `<b>No Students Currently in Classroom</b>`;
          } else {
            students.forEach(student => $('#current-status-table').append(
              `<td>${linkRelative(student.name, '/studentprofile.html?studentId=' + student.id)}</td>
                   <td>${student.id}</td>
                   <td style="background-color:${bgcolor};color:${fgcolor}">${text}</td>`)
            );
          }
        } catch (err) {
          console.log(err);
          giveTempError('Failed to correctly fetch student data, try refreshing');
        }
      }
    }
    // Wait 5 seconds before updating again
    await sleep(5000);
  }
}

$(document).ready(async function () {

  let manualStudentId = document.getElementById('manual-studentid');

  // Initialize scanner selector
  $(document).scannerDetection(function (e, data) {
    console.log(e);
    if (!(document.activeElement === manualStudentId)) {
      console.log('doing scanner')
      manualEncounter(parseInt(e));
    }
  });

  let manualSubmit = document.getElementById('manual-submit');
  manualSubmit.addEventListener('click', function (event) {
    if (isEmpty(manualStudentId.value)) {
      console.log('studentID is Empty')
    }
    else {
      manualEncounter(parseInt($('#manual-studentid').val()));
    }
  });

  // Just double check...
  await userInfo();

  // Start long running threads
  locationOptions();
  currentStatus();
  recentActivity();
});

