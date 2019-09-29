"use strict"

function singleUploadStudent() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('need apiKey for singleUploadStudent');
    return;
  }

  var name = document.getElementById('adminmanagestudent-name').value;
  if(isEmpty(name)) {
    giveAlert('Please fill in student name.', 'alert-danger', false);
    return;
  }

  var studentId = document.getElementById('adminmanagestudent-studentid').value;
  if(isEmpty(studentId)) {
    giveAlert('Please fill in student ID.', 'alert-danger', false);
    return;
  }

  var graduatingYear = toGraduatingYear($('#adminmanagestudent-grade :selected'));
  if(isEmpty(graduatingYear)) {
    giveAlert('Please select student grade.', 'alert-danger', false);
    return;
  }


  request(thisUrl() + '/student/new/' +
    '?studentId='+encodeURI(studentId) +
    '&graduatingYear='+ encodeURI(graduatingYear) +
    '&name='+name+
    '&apiKey='+apiKey.key,
    function(xhr) {
      giveAlert('Student uploaded successfully.', 'alert-success', false);
    },
    function(xhr) {
      giveAlert('Student failed to upload.', 'alert-danger', false);
    }
  );
}



//gets new data from server and inserts it at the beginning
function batchUploadStudent() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('need apiKey for batchUploadStudent');
    return;
  }

  var url = thisUrl() + '/batchUploadStudent/' +
    '?apiKey=' + Cookies.getJSON('apiKey').key;

  var files = document.getElementById('adminmanagestudent-teacher-file').files;
  if(files.length == 0) {
    giveAlert('Please upload CSV file.', 'alert-danger', false);
    return;
  }
  for(var i = 0; i < files.length; i++) {
    var formData = new FormData();
    formData.append("file", files[0]);

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function() {
      if(xhr.readyState == 4) {
        if(xhr.status == 200) {
          giveAlert('Students loaded successfully.', 'alert-success', false);
        } else {
          giveAlert('An error occured while loading.', 'alert-danger', false);
        }
      }
    }

    xhr.open("POST", url);
    xhr.send(formData);
  }
}
