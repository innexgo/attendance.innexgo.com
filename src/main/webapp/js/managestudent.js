"use strict"

var courseList;

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
  formData.append('file', fileinput.files[0]);

  var xhr = new XMLHttpRequest();
  xhr.open('POST', url);
  xhr.send(formData);
}

function updateClassList() {
  var selectCourseDropdown = document.getElementById('managestudent-select-course');
  var teacherViewClassTable = document.getElementById('managestudent-teacher-viewclass-table');
  var apiKey = Cookies.getJSON('apiKey');
  request(
    thisUrl() + '/student/' +
    '?courseId=' + courseList[selectCourseDropdown.selectedIndex].id +
    '&apiKey=' + apiKey.key,
    //success
    function(xhr) {
      var students = JSON.parse(xhr.responseText);
      for(var student in students) {
        teacherViewClassTable.innerHTML +=
          '<tr>' +
            '<td>' + student.name + '</td>' +
            '<td>' + student.id + '</td>' +
            '<td>' + student.graduatingYear + '</td>' +
            '<td>' + student.tags + '</td>' +
          '</td>';
      }
    },
    function(xhr) {}
  );
}


$(document).ready(function() {
  var apiKey = Cookies.getJSON('apiKey');

  // populate course list
  var selectCourseDropdown = document.getElementById('managestudent-select-course');

  request(
    thisUrl() + '/course/' +
    '?teacherId=' + apiKey.user.id +
    '&apiKey=' + apiKey.key,
    function(xhr) {
      courseList = JSON.parse(xhr.responseText);
      for(var i =0; i < courseList.length; i++) {
        var course = courseList[i];
        selectCourseDropdown.innerHTML +=
          '<option>' + course.period + ' : ' + course.subject + '</option>';
      }
    },
    //failure
    function(xhr) {}
  );
});






