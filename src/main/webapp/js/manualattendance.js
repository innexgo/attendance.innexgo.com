const beepup = new Audio('assets/beepup.wav');
const beepdown = new Audio('assets/beepdown.wav');
const error = new Audio('assets/error.wav');

let inferred_location_id = null;

async function submitEncounter(studentId) {
  console.log('submitting encounter ' + studentId)
  console.log(studentId);
  document.getElementById('manual-userid').value = '';
  let checkBox = document.getElementById('manual-type-toggle');
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let nextPeriod = Cookies.getJSON('nextPeriod');
  let courses = Cookies.getJSON('courses');
  let course = courses.filter(c => c.period == period.number)[0];

  if (course == null) {
    let dialog = bootbox.dialog({
      title: 'A custom dialog with init',
      message: '<p><i class="fa fa-spin fa-spinner"></i> Loading...</p>'
    });

    dialog.init(function(){
      setTimeout(function(){
        dialog.find('.bootbox-body').html('I was loaded after the dialog was shown!');
      }, 3000);
    });
    giveTempError('No class at the moment to sign into.');
    return;
  }

  if (String(studentId) == String(NaN)) {
    giveTempError('What you entered wasn\'t a valid ID');
    return;
  }

  fetch(`${apiUrl()}/misc/attends/?studentId=${studentId}&locationId=${course.location.id}&manual=true&apiKey=${apiKey.key}`)
    .then(response => parseResponse(response))
    .then(function(session) {
      // If the session is complete, then it is logging out
      if(session.complete) {
          giveTempInfo(`Sucessfully logged ${encounter.student.name} out of ${encounter.location.name}`);
          beepdown.play();
      } else {
          giveTempSuccess(`Sucessfully logged ${encounter.student.name} in to ${encounter.location.name}`);
          beepup.play();
      }
    })
    .catch(function(err) {
      giveTempError('Something went wrong while trying to sign you in.');
      error.play();
    })
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

