//gets new data from server and inserts it at the beginning
function recentActivity() {
  var apiKey = Cookies.getJSON('apiKey');
  var url = thisUrl()+'/encounter/?count=100' +
    '&managerId=' + apiKey.user.id +
    '&apiKey='+ apiKey.key;
  request(url,
    function(xhr){
      // clear table
      var table = document.getElementById('recent-activity-table');
      table.innerHTML = '';

      var encounters = JSON.parse(xhr.responseText);
      //go backwards to maintain order
      for(var i = encounters.length-1; i >= 0; i--) {
        var encounter = encounters[i];
        table.insertRow(0).innerHTML=
          ('<tr>' +
            '<td>' + encounter.user.name + '</td>' +
            '<td>' + encounter.type + '</td>' +
            '<td>' + moment.unix(encounter.time).fromNow() + '</td>' +
            '<td>' + encounter.location.name + '</td>' +
            '</tr>');
      }
    },
    function(xhr) {
      console.log(xhr);
    }
  );
}



$(document).ready(function () {
  recentActivity();
  setInterval(recentActivity, 5000);
})
