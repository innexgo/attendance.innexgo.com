"use strict"

var courseIrregularities = null;
var course= null;

function loadStudentIrregularities() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not signed in');
    return;
  }

  var searchParams = new URLSearchParams(window.location.search);

  if(!searchParams.has('courseId')) {
    console.log('page not loaded with right params');
    return;
  }

  var courseId = searchParams.get('courseId');

  request(thisUrl() +  '/student/' +
    '?courseId='+courseId +
    '&apiKey='apiKey.key,
    function(xhr) {
      courseIrregularities = JSON.parse(xhr.responseText);
    },
    function(xhr) {
      //failure
      // TODO send alert
      return
    }
  );
}



