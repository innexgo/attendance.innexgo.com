const beepup = new Audio('assets/beepup.wav');
const beepdown = new Audio('assets/beepdown.wav');
const error = new Audio('assets/error.wav');

async function initializeLocationOptions() {
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let nextPeriod = Cookies.getJSON('nextPeriod');
  let courses = Cookies.getJSON('courses');

  try {
    let locations = await fetchJson(`${apiUrl()}/location/?apiKey=${apiKey.key}`);

    let locationSelect = $('#manual-locationid');

    // Add the options
    locationSelect.empty();
    locations.forEach(l => locationSelect.append(`<option value="${l.id}">${l.name}</option>`));

    // Course that is not null
    let currentCourse = courses.filter(c => c.period == period.number)[0];
    let nextPeriodCourse = courses.filter(c => c.period == period.number)[0];
    if(currentCourse != null || nextPeriodCourse  != null) {
      // Set auto selected location
      let selectedLocation = currentCourse == null ? nextPeriodCourse.location : currentCourse.location;
      locationSelect.prepend(`<option value="${selectedLocation.id}">(Default) ${selectedLocation.name}</option>`);
      locationSelect.val(selectedLocation.id);
    }
  } catch(err) {
    console.log(err);
    givePermError('Failed to load locations');
  }
}

async function submitEncounter(studentId) {
  console.log('submitting encounter ' + studentId)
  console.log(studentId);
  document.getElementById('manual-userid').value = '';
  let checkBox = document.getElementById('manual-type-toggle');
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let nextPeriod = Cookies.getJSON('nextPeriod');

  if (String(studentId) == String(NaN)) {
    giveTempError('What you entered wasn\'t a valid ID');
    return;
  }

  let locationId = $('#manual-locationid').val();

  try {
    let session = await fetchJson(
        `${apiUrl()}/misc/attends/?studentId=${studentId}&locationId=${locationId}&manual=true&apiKey=${apiKey.key}`);

    if(session.complete) {
        giveTempInfo(`Sucessfully logged ${session.inEncounter.student.name} out of ${session.inEncounter.location.name}`);
        beepdown.play();
    } else {
        giveTempSuccess(`Sucessfully logged ${session.inEncounter.student.name} in to ${session.inEncounter.location.name}`);
        beepup.play();
    }
  } catch(err) {
    console.log(err);
    giveTempError('Something went wrong while trying to sign you in.');
    error.play();
  }
}

$(document).ready(function () {
  // Initialize scanner selector
  var tbox = document.getElementById('manual-userid');
  $(document).scannerDetection(function (e, data) {
    console.log(e);
    if (!(document.activeElement === tbox)) {
      console.log('doing scanner')
        submitEncounter(parseInt(e));
    }
  });

  tbox.addEventListener('keydown', function (event) {
    if (event.keyCode === 13) {
      console.log('doing enter key')
      if (isEmpty(tbox.value)) {
        console.log('studentID is Empty')
      }
      else {
        submitEncounter(parseInt(tbox.value));
      }
    }
  });

  var button = document.getElementById('manual-submit');
  button.addEventListener('click', function (event) {
    console.log('doing button')
    if (isEmpty(tbox.value)) {
      console.log('studentID is Empty')
    }
    else {
      submitEncounter(parseInt(tbox.value));
    }
  });
});

// Periodically poll to check if course is valid
$(document).ready(async function() {
  while(true) {
    await initializeLocationOptions();
    let nextPeriod = Cookies.getJSON('nextPeriod');
    await sleep(nextPeriod.startTime - moment().valueOf());
  }
});
