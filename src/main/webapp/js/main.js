function thisUrl(){
	return window.location;
}

function expandStudent(id) {
	var url = thisUrl().host+"students/"+id+"/";
	var request = new XMLHttpRequest();
	request.open('GET', url, true);
	request.onload = function () {
		if (request.status >= 200 && request.status < 400) {
			var data = JSON.parse(this.response);

		} else {
			console.log('error');
		}
	}
	request.send();
}

function addAllToTable() {
	var Http = new XMLHttpRequest();
	Http.open("GET", thisUrl().host + "/events/");
	Http.send();
	Http.onreadystatechange=(e)=>{
		console.log(Http.responseText);
		var data = JSON.parse(Http.responseText);
		for(var i = 0; i < data.length; i++) {
			addHistoryTableEntry(data[i].student.name, data[i].location.id, data[i].timestamp);
			console.log(data[i]);
		}
	} 
}

function addHistoryTableEntry(name, roomNumber, timestamp)
{
	document.getElementById("history-table").innerHTML =
		'<tr>' + 
		'<td><i class="fa fa-sign-in text-red large"></i></td>' +
		'<td class="yellow">' +
		'<strong> ' + name + ' </strong>' +
		'signed in to Room' +
		'<strong> ' + roomNumber + ' </strong>' + 
		'<td><i>'+ timestamp +'</i></td>' + 
		'</tr>' + document.getElementById("history-table").innerHTML;
}


addAllToTable();

//Get the Sidebar
var mySidebar = document.getElementById("mySidebar");

//Get the DIV with overlay effect
var overlayBg = document.getElementById("myOverlay");

//Toggle between showing and hiding the sidebar, and add overlay effect
function w3_open() {
	if (mySidebar.style.display === 'block') {
		mySidebar.style.display = 'none';
		overlayBg.style.display = "none";
	} else {
		mySidebar.style.display = 'block';
		overlayBg.style.display = "block";
	}
}

//Close the sidebar with the close button
function w3_close() {
	mySidebar.style.display = "none";
	overlayBg.style.display = "none";
}
