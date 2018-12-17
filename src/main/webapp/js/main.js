"use strict"
function thisUrl(){
	return window.location.protocol  + "//" + window.location.host;
}

function timeSince(date) {
	var seconds = Math.floor((new Date().getTime() - Date.parse(date)) / 1000);
	var interval = Math.floor(seconds / 31536000);

	if (interval > 1) {
		return interval + " years";
	}
	interval = Math.floor(seconds / 2592000);
	if (interval > 1) {
		return interval + " months";
	}
	interval = Math.floor(seconds / 86400);
	if (interval > 1) {
		return interval + " days";
	}
	interval = Math.floor(seconds / 3600);
	if (interval > 1) {
		return interval + " hours";
	}
	interval = Math.floor(seconds / 60);
	if (interval > 1) {
		return interval + " minutes";
	}
	return Math.floor(seconds) + " seconds";
}

function request(url, functionOnLoad, functionOnError) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', url, true);
	xhr.onload = function() {
		if (xhr.readyState == 4 && xhr.status == 200) {
			functionOnLoad(xhr);
		} else if(xhr.readyStat == 4 && xhr.status != 200) {
			functionOnError(xhr);
		}
	};
	xhr.send();
}

function addHistoryTableEntry(isSignIn, color, studentName, placeName, timestamp)
{
	var timeAgoText = timeSince(timestamp) + ' ago';
	var textIconClass = isSignIn ? 'fa fa-sign-in text-blue large' : 'fa fa-sign-out text-red large';
	var tableId = isSignIn ? "sign-in-table" : "sign-out-table";
	var signInOrSignOutText = isSignIn ? 'in to' : 'out of';
	document.getElementById(tableId).innerHTML =
		'<tr>' + 
		'<td><i class="'+textIconClass+'"></i></td>' +
		'<td class="' + color + '">' +
		'<strong> ' + studentName + ' </strong>' +
		'signed ' + signInOrSignOutText+ ' <b>' + placeName + '</b> ' + 
		'<td><i>' + timeAgoText +'</i></td>' + 
		'</tr>' + document.getElementById(tableId).innerHTML;
}

function clearHistoryTables()
{
	document.getElementById('sign-in-table').innerHTML = '';
	document.getElementById('sign-out-table').innerHTML = '';
}

function updateHistoryTables() {
	request(thisUrl()+'/events/', 
			function(xhr){
		var events = JSON.parse(xhr.responseText);
		for(var i = 0; i < events.length; i++) {
			addHistoryTableEntry(events[i].type == "sign-in", 'yellow', events[i].student.name, events[i].location.name, events[i].time);
		}
	},
	function(xhr) 
	{
		console.log(xhr.responseText);
	}
	);
}

function toggleSignInOrOut() {
	var icon = document.getElementById("sign-in-or-out-icon");
	var checkBox = document.getElementById("sign-in-or-out-checkbox");
	icon.innerHTML = checkBox.checked ? '<i class="fa fa-sign-in xxxlarge"></i>' : '<i class="fa fa-sign-out xxxlarge"></i>';
}

function newEvent(studentId, locationId, type) {
	request(thisUrl()+'/events/new/?studentId='+studentId+'&locationId='+locationId+'&type='+type,
			function(xhr){}, 
			function(xhr)
			{
				console.log(xhr.responseText);
			});
}


function submitEvent() {
	var textBox = document.getElementById("student-id-textbox");
	var checkBox = document.getElementById("sign-in-or-out-checkbox");
	newEvent(textBox.value, 1, checkBox.checked ? "sign-out" : "sign-in");
	clearHistoryTables();
	updateHistoryTables();
}

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

setTimeout(function(){
	clearHistoryTables();
	updateHistoryTables();
}, 1000);

updateHistoryTables();
