"use strict"

const beepup = new Audio('assets/beepup.wav');
const beepdown = new Audio('assets/beepdown.wav');
const error = new Audio('assets/error.wav');

async function submitEncounter(studentId) {
  console.log('submitting encounter ' + studentId)
  console.log(studentId);
  document.getElementById('manual-userid').value = '';
  let checkBox = document.getElementById('manual-type-toggle');
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let course = Cookies.getJSON('courses').filter(c => c.period == period.number)[0];

  // Let's try to determine the location
  let locationId = Cookies.getJSON('default-locationid');
  if(course != null) {
    locationId = course.locationId;
  }

  if (String(studentId) == String(NaN)) {
    giveTempError('What you entered wasn\'t a valid ID');
    return;
  }

  if(locationId == null) {
    // If it's still null tell the user to set the default location
    giveTempError('Please set default location in order to manually sign in students when class is not in session.');
    error.play();
    return;
  }

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
