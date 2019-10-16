function openSidebar() {
  document.getElementById('sidebar').style.width = '20%';
  //document.getElementById('overlay').style.display = 'block'
}

function closeSidebar() {
  document.getElementById('sidebar').style.width = '0%';
  //document.getElementById('overlay').style.display = 'none';
}

$(document).ready(function () {
  var apiKey = Cookies.getJSON('apiKey');

  document.getElementById('info-name').innerHTML = linkRelative(apiKey.user.name, '/userprofile.html?userId=' + apiKey.user.id);

  if (apiKey.user.ring == 0) {
    document.getElementById('my-overview').href = '/adminoverview.html';
    document.getElementById('my-managestudent').href = '/adminmanagestudent.html';
    document.getElementById('reports').href = '/adminreports.html';
  } else {
    document.getElementById('my-overview').href = '/overview.html';
    document.getElementById('my-managestudent').href = '/managestudent.html';
    document.getElementById('reports').href = '/reports.html';
  }

  var sidebarItems = $('.sidebar-item').addClass('list-group-item');
  sidebarItems.not('.sidebar-info-list').addClass('list-group-item-action');

  loadSidebar();
});


function loadSidebar() {
  var prefs = Cookies.getJSON('prefs');
  var sidebar = prefs.sidebarStyle;
  var colour = prefs.colourTheme;

  switch (sidebar) {
    case 'collapsed':
      $('#sidebar').addClass('sidebar-collapsable');
      break;

    case 'fixed':
      $('#sidebar').addClass('sidebar-fixed');
      $('#not-sidebar').css('padding-left', '20%');
      $('.sidebar-button').remove();
      document.getElementById('overlay').remove();
      break;
  };

  switch (colour) {
    case 'dark':
      var brandImage = document.createElement('img');
      brandImage.src = '/img/innexgo_logo.png';

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(' Innexgo');

      $('.navbar-palette').addClass('text-light').addClass('bg-dark');
      break;

    case 'light':
      var brandImage = document.createElement('img');
      brandImage.src = '/img/innexgo_logo_dark.png';

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(' Innexgo');

      $('.navbar-palette').addClass('text-dark').addClass('sidebar-light');
      break;

    case 'blue':
      var brandImage = document.createElement('img');
      brandImage.src = '/img/innexgo_logo.png';

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(' Innexgo');

      $('.navbar-palette').addClass('text-light').addClass('sidebar-blue');
      break;

    case 'default':
      var brandImage = document.createElement('img');
      brandImage.src = '/img/innexgo_logo.png';

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(' Innexgo');

      $('.navbar-palette').addClass('text-light');
      break;
  };
};

function displayInfo() {
  if (Cookies.getJSON('period') == null) {
    userInfo();
  }
  var apiKey = Cookies.getJSON('apiKey');
  var period = Cookies.getJSON('period');
  var course = period == null ? null : Cookies.getJSON('courses').filter(c => c.period == period.period)[0];


  document.getElementById('info-time').innerHTML = moment().format('dddd, MMMM D');

  document.getElementById('info-period').innerHTML =
    (period == null
      ? ''
      : (period.startTime > Date.now()
        ? 'Waiting for Period ' + period.period
        : 'Period ' + period.period
      )
    );

  document.getElementById('info-location').innerHTML =
    course == null ? '' : course.location.name;
}

$(document).ready(function () {
  displayInfo();
  var period = Cookies.getJSON('period');
  setInterval(function () {
    if (period == null) {
      displayInfo();
    }
    else if (period.endTime - moment().valueOf() < 0) {
      displayInfo();
    };
  }, 1000);
})
