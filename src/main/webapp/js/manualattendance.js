

function submitEncounter(studentId) {
  var checkBox = document.getElementById('manual-direction-toggle');
  var course = Cookies.getJSON('course');
  var apiKey = Cookies.getJSON('apiKey');

  if(course == null) {
    //TODO actually give this error
    console.log('no class at the moment to sign into');
    return;
  }


  // get the last encounter
  var getLastEncounterUrl = thisUrl() + '/encounter/' +
    '?studentId=' + encodeURIComponent(studentId) +
    '&count=1' +
    '&apiKey='+Cookies.getJSON('apiKey').key;

  request(getLastEncounterUrl,
    //success
    function(xhr) {
      var direction = 'in';
      //TODO play sound on correct sign in

      var lastEncounter = JSON.parse(xhr.responseText)[0];
      console.log(lastEncounter);
      // if it's null, there was no last encounter, so the direction is in
      if(lastEncounter == null) {
        direction = 'in';
      } else {
        //if the last encounter was at the same place
        if(lastEncounter.location.id == course.location.id) {
          // whatever the opposite was
          direction = lastEncounter.direction == 'in' ? 'out' : 'in';
        } else {
          if(lastEncounter.direction == 'in') {
            // infer a sign out at the last location
            var addOldSignOut = thisUrl()+'/encounter/new/' +
              '?studentId=' + studentId +
              '&locationId=' + lastEncounter.location.id +
              (lastEncounter.course == null
                ? ''
                : '&courseId=' + lastEncounter.course.id) +
              '&type=in' +
              '&apiKey='+apiKey.key;
            request(url,
              function(xhr){},
              function(xhr){});
            // direction = in
            direction = 'in';
          } else {
            direction = 'in';
          }
        }
      }

      var addEncounterUrl = thisUrl() + '/encounter/new/' +
        '?studentId=' + studentId +
        '&locationId=' + course.location.id +
        '&courseId=' + course.id +
        '&type=' + direction +
        '&apiKey=' + apiKey.key;
      request(url,
        function(xhr){},
        function(xhr){});
    },
    //failure
    function(xhr) {
      //TODO explain error
      return;
    }
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
