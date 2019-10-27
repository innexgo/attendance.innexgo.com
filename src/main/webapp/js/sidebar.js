function displayInfo() {
  var period = Cookies.getJSON('period');
  var course = period == null ? null : Cookies.getJSON('courses').filter(c => c.period == period.period)[0];


  document.getElementById('info-time').innerHTML = moment().format('dddd (MM/DD/YYYY)');

  document.getElementById('info-period').innerHTML =
    (period == null
      ? ''
      : (period.startTime > Date.now()
        ? 'Waiting for Period ' + period.period
        : 'Period ' + period.period
      )
    );

  if(course != null) {
    document.getElementById('info-location').innerHTML =
      linkRelative(course.location.name, '/locationprofile.html?locationId=' + course.location.id);
  } else {
    document.getElementById('info-location').innerHTML = '';
  }
}

$(document).ready(function () {
  var apiKey = Cookies.getJSON('apiKey');
  var prefs = Cookies.getJSON('prefs');

  // Set name
  document.getElementById('info-name').innerHTML = linkRelative(apiKey.user.name, '/userprofile.html?userId=' + apiKey.user.id);

  // Set links in the document dependent on user permissions
  if (apiKey.user.ring == 0) {
    document.getElementById('my-overview').href = '/adminoverview.html';
  } else {
    document.getElementById('my-overview').href = '/overview.html';
  }

  // Add sidebar tags
  var sidebarItems = $('.sidebar-item').addClass('list-group-item');
  sidebarItems.not('.sidebar-info-list').addClass('list-group-item-action');

  // Now setup color
  var sidebar = prefs.sidebarStyle;
  var color = prefs.colorScheme;

  switch (color) {
    case 'dark':
      var brandImage = document.createElement('img');
      brandImage.src = '/img/innexgo_logo.png';

      $('.navbar-palette').addClass('text-light').addClass('bg-dark');
      break;
     case 'default':
      var brandImage = document.createElement('img');
      brandImage.src = '/img/innexgo_logo.png';

      $('.navbar-palette').addClass('text-light').addClass('bg-dark');
      break;
  };

  displayInfo();

  var period = Cookies.getJSON('period');
  setInterval(function () {
    if (period == null) {
      displayInfo();
    } else if (period.endTime - moment().valueOf() < 0) {
      userInfo();
      displayInfo();
    }
  }, 10000);
})
