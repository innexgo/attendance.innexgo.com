"use strict"

//gets new data from server and inserts it at the beginning
function batchSetSchedule() {
  var apiKey = Cookies.getJSON('apiKey');

  var fileinput = document.getElementById('managestudent-teacher-file');
  var courseinput = document.getElementById('managestudent-teacher-courseselection');

  if(apiKey == null) {
    console.log('no perms');
    return;
  }

  var url = thisUrl() + '/batchSetSchedule/' +
    '?courseId=' + courseinput.value +
    '&apiKey=' + Cookies.getJSON('apiKey').key;

  var formData = new FormData();
  formData.append("file", fileinput.files[0]);

  var xhr = new XMLHttpRequest();
  xhr.open("POST", url);
  xhr.send(formData);
}
