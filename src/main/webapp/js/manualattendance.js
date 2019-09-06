var beepup = new Audio('assets/beepup.wav');
var beepdown = new Audio('assets/beepdown.wav');
var error = new Audio('assets/error.wav');

function submitEncounter(studentId) {
  console.log('submitting encounter ' + String(studentId))
  console.log(String(studentId));
  document.getElementById('manual-userid').value = '';
  var checkBox = document.getElementById('manual-type-toggle');
  var apiKey = Cookies.getJSON('apiKey');
  var period = Cookies.getJSON('period');
  var course = Cookies.getJSON('courses').filter(c => c.period == period.period)[0];

  if (String(studentId) == String(NaN)) {
    giveAlert('What you entered wasn\'t a valid ID', 'alert-danger');
    return;
  }

  if (course == null) {
    giveAlert('No class at the moment to sign into.', 'alert-danger');
    return;
  }

  var addEncounterUrl = thisUrl() + '/encounter/new/' +
    '?studentId=' + studentId +
    '&locationId=' + course.location.id +
    '&courseId=' + course.id +
    '&apiKey=' + apiKey.key;

  request(addEncounterUrl,
    //success
    function (xhr) {
      var encounter = JSON.parse(xhr.responseText);

      //now check if it was a sign in or a sign out

      var getSessionUrl = thisUrl() + '/session/' +
        '?inEncounterId=' + encounter.id +
        '&apiKey=' + apiKey.key;

      request(getSessionUrl,
        //success
        function (xhr) {
          var sessionList = JSON.parse(xhr.responseText);
          if (sessionList.length != 0) {
            giveAlert('Sucessfully logged ' + encounter.student.name + ' in to ' + encounter.location.name, 'alert-success');
            beepup.play();
          } else {
            giveAlert('Sucessfully logged ' + encounter.student.name + ' out of ' + encounter.location.name, 'alert-success');
            beepdown.play();
          }
        },
        //failure
        function (xhr) {
          giveAlert('Something went wrong while finalizing sign in.', 'alert-danger');
          error.play();
        }
      );
    },
    function (xhr) {
      giveAlert('Something went wrong while trying to sign you in.', 'alert-danger');
      error.play();
    }
  );
}

$(document).ready(function () {
  // Initialize scanner selector
  var tbox = document.getElementById('manual-userid');
  $(document).scannerDetection(function (e, data) {
    console.log(e);
    if (!(document.activeElement === tbox)) {
      console.log('doing scanner')
      if (isEmpty(tbox.value)) {
        console.log('studentID is Empty')
      }
      else {
        submitEncounter(parseInt(e));
      }
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

