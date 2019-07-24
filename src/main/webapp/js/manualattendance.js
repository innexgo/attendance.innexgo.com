function submitEncounter(studentId) {
  var checkBox = document.getElementById('manual-direction-toggle');

  var url = thisUrl()+
    '/encounter/new/?userId='+ studentId +
    '&locationId='+ Cookies.getJSON('schedule').location.id +
    '&type='+ (checkBox.checked ? 'out' : 'in') +
    '&apiKey='+Cookies.getJSON('apiKey').key;
  request(url,
    function(xhr){},
    function(xhr){});
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
