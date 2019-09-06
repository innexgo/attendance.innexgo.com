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

  var courseId = courseList.filter(c => c.period==$("#managestudent-teacher-courseselection :selected").val())[0].id;

  var url = thisUrl() + '/batchSetSchedule/' +
    '?courseId=' + courseId +
    '&apiKey=' + Cookies.getJSON('apiKey').key;

  var formData = new FormData();
  formData.append('file', fileinput.files[0]);

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
  xhr.open('POST', url);
  xhr.send(formData);
}

function updateClassList() {
  var teacherViewClassTable = document.getElementById('managestudent-teacher-viewclass-table');
  var courseId = courseList.filter(c => c.period==$("#managestudent-teacher-courseselection :selected").val())[0].id;
  var apiKey = Cookies.getJSON('apiKey');
  request(
    thisUrl() + '/student/' +
    '?courseId=' + courseId +
    '&apiKey=' + apiKey.key,
    //success
    function(xhr) {
      var students = JSON.parse(xhr.responseText);
      for(var i = 0; i < students.length; i++) {
        teacherViewClassTable.innerHTML +=
          '<tr>' +
            '<td>' + students[i].name + '</td>' +
            '<td>' + students[i].id + '</td>' +
            '<td>' + students[i].graduatingYear + '</td>' +
            '<td>' + students[i].tags + '</td>' +
          '</td>';
      }
    },
    function(xhr) {}
  );
}


$(document).ready(function() {
  var apiKey = Cookies.getJSON('apiKey');

  // populate course list
  var selectCourseDropdownUploadStudent = document.getElementById('managestudent-teacher-courseselection');
  var selectCourseDropdownClassList = document.getElementById('managestudent-select-course');

  request(
    thisUrl() + '/course/' +
    '?teacherId=' + apiKey.user.id +
    '&apiKey=' + apiKey.key,
    function(xhr) {
      courseList = JSON.parse(xhr.responseText);
      for(var i =0; i < courseList.length; i++) {
        var course = courseList[i];
        selectCourseDropdownUploadStudent.innerHTML +=
          '<option>' + ordinal_suffix_of(course.period) + ' Period </option>';
        selectCourseDropdownClassList.innerHTML +=
          '<option>' + ordinal_suffix_of(course.period) + ' Period </option>';
      }
    },
    //failure
    function(xhr) {}
  );
});






