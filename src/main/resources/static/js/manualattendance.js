var beepup = new Audio('assets/beepup.wav');
var beepdown = new Audio('assets/beepdown.wav');

function submitEncounter(studentId) {
  var checkBox = document.getElementById('manual-type-toggle');
  var course = Cookies.getJSON('course');
  var apiKey = Cookies.getJSON('apiKey');

  if(course == null) {
    //TODO actually give this error
    console.log('no class at the moment to sign into');
    return;
  }

  var addEncounterUrl = thisUrl() + '/encounter/new/' +
    '?studentId=' + studentId +
    '&locationId=' + course.location.id +
    '&courseId=' + course.id +
    '&apiKey=' + apiKey.key;

  request(addEncounterUrl,
    //success
    function(xhr) {
      var encounter = JSON.parse(xhr.responseText);

      //now check if it was a sign in or a sign out

      var getSessionUrl = thisUrl() + '/session/' +
        '?outEncounterId=' + encounter.id +
        '&apiKey=' + apiKey.key;

      request(getSessionUrl,
        //success
        function(xhr) {
          var sessionList = JSON.parse(xhr.responseText);
          if(sessionList.length != 0) {
            beepup.play();
          } else {
            beepdown.play();
          }
        },
        //failure
        function(xhr) {}
      );
    },
    function(xhr) {}
  );
}

$(document).ready(function () {
  // Initialize scanner selector
  $(document).scannerDetection(function(e, data) {
    submitEncounter(e);
  });

  var tbox = document.getElementById('manual-userid');
  tbox.addEventListener('keydown', function(event) {
    if (event.keyCode === 13) {
      submitEncounter(parseInt(tbox.value));
    }
  });

  var button = document.getElementById('manual-submit');
  button.addEventListener('click', function(event) {
    submitEncounter(parseInt(tbox.value));
  });
});
