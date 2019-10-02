

//gets new data from server and inserts it at the beginning
function recentActivity() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');

  if(apiKey == null || course==null) {
    console.log('not enough cookies for recentActivity');
    return;
  }

				alert(name);
  var url = thisUrl()+'/session/?count=100' +
    '&courseId=' + course.id +
    '&apiKey='+ apiKey.key;

				var name = Document.getElementById("name").value;
				var id = Document.getElementById("id").value;
				var period = Document.getElementById("period").value;
				var teacher = Document.getElementById("teacher").value;
				var room = Document.getElementById("room").value;
				var startDate = Document.getElementById("startDate").value;
				var startTime = Document.getElementById("startTime").value;
				var endDate = Document.getElementById("endDate").value;
				var endTime = Document.getElementById("endTime").value;
				var count = Document.getElementById("count").value;
								

  request(url,
    function(xhr){
      // clear table
      var tableContent = document.getElementById('recentActivityContent');
      var apiKey = Cookies.getJSON('apiKey');
      table.innerHTML = '';

      var sessions = JSON.parse(xhr.responseText);
      //go backwards to maintain order
      for(var i = sessions.length-1; i >= 0; i--) {
        var session = sessions[i];
        table.insertRow(0).innerHTML=
          ('<tr>' +
            '<td>' + (sessions.length - i) + '</td>' +
            '<td>' + 
              '<a href="' + thisUrl() + 
                '/studentprofile.html/?apiKey=' + apiKey.key + 
                '&studentId=' + session.inEncounter.student.id+ '">'+
              session.inEncounter.student.name + '</a></td>' +
            '<td>' + session.inEncounter.student.id + '</td>' +
            '<td>' + session.course.period + '</td>' +
            '<td>' + session.course.teacher.name + '</td>' +
            '<td>' + '</td>' +
            '<td>' + session.course.location.name + '</td>' +
            '<td>' + session.inEncounter.time + '</td>' +
            '<td>' + session.outEncounter.time + '</td>' +
            '</tr>');
      }
    },
    function(xhr) {
      console.log(xhr);
    }
  );

}

