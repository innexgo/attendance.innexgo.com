function thisUrl(){
	return window.location;
}

function baseUrl(url) {
	return url.protocol + "//" + url.host + "/" + url.pathname.split('/')[1];
}

function expandStudent(id) {
	var url = baseUrl(thisUrl())+"students/"+id+"/";
	var request = new XMLHttpRequest();
	request.open('GET', url, true);
	request.onload = function () {
		if (request.status >= 200 && request.status < 400) {
			var data = JSON.parse(this.response);
			document.getElementById('studentdisplay').innerHTML = 
				'<p>Student ID: ' + data.id + '</p>' + 
				'<p>Name: ' + data.name + '</p>';
		} else {
			console.log('error');
		}
	}
	request.send();
}

expandStudent(1);

