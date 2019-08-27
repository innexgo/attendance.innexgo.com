"use strict"

//gets new data from server and inserts it at the beginning
function batchUploadStudent() {
  var apiKey = Cookies.getJSON('apiKey');

  if(apiKey == null) {
    console.log('not enough cookies for recentActivity');
    return;
  }

  var url = thisUrl() + '/batchUploadStudent/' +
    '?apiKey=' + Cookies.getJSON('apiKey').key;

  var formData = new FormData();
  formData.append("file", document.getElementById("adminmanage-file").files[0]);

  var xhr = new XMLHttpRequest();
  xhr.open("POST", url);
  xhr.send(formData);
}
