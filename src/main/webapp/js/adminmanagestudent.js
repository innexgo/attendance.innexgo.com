"use strict"

//gets new data from server and inserts it at the beginning
function batchUploadStudent() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('need apiKey for recentActivity');
    return;
  }

  var url = thisUrl() + '/batchUploadStudent/' +
    '?apiKey=' + Cookies.getJSON('apiKey').key;

  var files = document.getElementById('adminmanagestudent-teacher-file').files;
  for(var i = 0; i < files.length; i++) {
    var formData = new FormData();
    formData.append("file", files[0]);

    var xhr = new XMLHttpRequest();

   xhr.onreadystatechange = function() {
     if(xhr.readyState == 4) {
       if(xhr.status == 200) {
        giveAlert('Students loaded successfully.', 'alert-success');
       } else {
        giveAlert('An error occured while loading.', 'alert-danger');
       }
     }
   }

    xhr.open("POST", url);
    xhr.send(formData);
  }
}
