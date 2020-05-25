"use strict"

/*
 global Cookies moment
 readableTimestamp apiUrl fetchJson linkRelative isEmpty sleep INT32_MAX
 giveTempError giveTempInfo giveTempSuccess givePermError
 */

const beepup = new Audio('assets/beepup.wav');
const beepdown = new Audio('assets/beepdown.wav');
const error = new Audio('assets/error.wav');

function enableHover() {
  $('[data-toggle="popover"]').popover({
    trigger: 'hover'
  });
}

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(enableHover);


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
  for(;;) {
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
async function recentActivityLoadPage() {
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let course = Cookies.getJSON('courses').filter(c => c.period == period.number)[0];
  let locationId = course != null ? course.location.id : Cookies.getJSON('default-locationid');

  if (location == null) {
    $('#recentactivity-events')[0].innerHTML = '<b>No Default Location Loaded</b>';
    return;
  }

  // Count of sessions per page
  const c = 10;
  let sessions = await fetchJson(`${apiUrl()}/session/?apiKey=${apiKey.key}&locationId=${locationId}&offset=0&count=${c}`);

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
              <td>${readableTimestamp(e.encounter.time)}</td>
              <td>${e.encounter.type == 'virtual'
                  ? 'Out (Assumed)'
                  : e.in
                      ? 'In'
                      : 'Out'}
              </td>
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
  for(;;) {
    try {
      recentActivityLoadPage();
    } catch (err) {
      console.log(err);
      giveTempError('Failed to fetch recent activity data.');
    }
    await sleep(5000);
  }
}

// Forever runs and updates currentStatus
async function currentStatus() {

  const makeEntry = (student, bgcolor, fgcolor, faClass, toolTip) =>
      `<tr>
        <td>${linkRelative(student.name, '/studentprofile.html?studentId=' + student.id)}</td>
        <td>${student.id}</td>
        <td title="${toolTip}" style="background-color:${bgcolor};color:${fgcolor}">
            <span class="fa ${faClass}"></span>
        </td>
      </tr>`;

  let apiKey = Cookies.getJSON('apiKey');
  let table = $('#current-status-table');
  for (;;) {
    let period = Cookies.getJSON('period');
    let courses = Cookies.getJSON('courses').sort((a, b) => (a.period > b.period ? -1 : 1));
    let course = period == null ? null : courses.filter(c => c.period == period.number)[0];

    let time = moment().valueOf();

    //bail if we dont have all of the necessary cookies
    if (apiKey == null || period == null) {
      console.log('lack necessary cookies to calculate current status');
      table[0].innerHTML = '';
      return;
    }

    if (course != null) {
      try {
        let irregularities = (await fetchJson(`${apiUrl()}/irregularity/?courseId=${course.id}&periodStartTime=${period.startTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
          .filter(i => i.type != 'Forgot to Sign Out');

        let presentStudents = await fetchJson(`${apiUrl()}/misc/present/?locationId=${course.location.id}&time=${time}&apiKey=${apiKey.key}`);
        let registeredStudents = await fetchJson(`${apiUrl()}/misc/registeredForCourse/?courseId=${course.id}&time=${time}&apiKey=${apiKey.key}`);

        // This statement is brute force. Oof to those who must read it
        // Is list of all present students who are not in students
        let visitors = presentStudents.filter(v => registeredStudents.filter(s => s.id == v.id).length == 0)
        let presentRegistered = presentStudents.filter(v => registeredStudents.filter(s => s.id == v.id).length > 0)

        $('#current-status-percent-attendance')[0].innerHTML = `${presentRegistered.length}/${registeredStudents.length}`;

        table[0].innerHTML = '';

        visitors
          .sort((a, b) => (a.name > b.name) ? 1 : -1)
          .forEach(v => table.append(makeEntry(v, '#ffccff', '#cc00cc', 'fa-check', 'This student is not scheduled to be in your classroom right now.')));

        if(registeredStudents.length == 0) {
          table[0].innerHTML = `<b>No Students Currently in Classroom</b>`;
        } else {
          registeredStudents
            .sort((a, b) => (a.name > b.name) ? 1 : -1)
            .forEach(student => {
              let irregularity = irregularities.filter(irr => irr.student.id == student.id).pop();
              if (irregularity != null) {
                console.log(irregularity.type);
                switch (irregularity.type) {
                  case 'Absent': {
                    table.append(makeEntry(student, '#ffcccc', '#ff0000', 'fa-times', 'Student was supposed to attend, but hasn\'t signed in yet.'));
                    break;
                  }
                  case 'Tardy': {
                    table.append(makeEntry(student, '#ffffcc', '#cccc00', 'fa-check', 'Student was supposed to attend and signed in late.'));
                    break;
                  }
                  case 'Left Early': {
                    table.append(makeEntry(student, '#ccffff', '#00cccc', 'fa-times', 'This student was supposed to attend, but has signed out of your classroom early.'));
                    break;
                  }
                  case 'Left Temporarily': {
                    table.append(makeEntry(student, '#ccffff', '#00cccc', 'fa-check', 'This student left in the middle of class, but has now signed back in.'));
                    break;
                  }
                }
              } else {
                table.append(makeEntry(student, '#ccffcc', '#00ff00', 'fa-check', 'Student is present.'));
              }
            });
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

  // Start long running threads
  locationOptions();
  currentStatus();
  recentActivity();
});

