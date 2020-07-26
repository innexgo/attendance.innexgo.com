/* global moment sleep L apiUrl fetchJson givePermSuccess givePermError */

// the map
let instruction2Map = null;

// The {latitude, longitude, timestamp} groups
let instruction2Points = null;

// list of all instruction2Markers
let instruction2Markers = [];

let exclusionZones = [];

/**
 * loads the map
 */
function loadInstruction2Map() {
  instruction2Map = L.map('instruction2-map');
  instruction2Map.setView([0, 0], 2);

  const osm = L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: ('Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
      '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
      'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>'),
    maxZoom: 18,
    minZoom: 2,
    id: 'mapbox/streets-v11',
    tileSize: 512,
    zoomOffset: -1,
    accessToken: 'pk.eyJ1IjoicGltcGFsZSIsImEiOiJjazhkbzk4NTIwdHkzM21vMWFiNHI' +
      'zZ3BiIn0.nLv4P71SFh4TIANuwJ8I9A',
  });

  osm.addTo(instruction2Map);

  const drawnItems = L.featureGroup().addTo(instruction2Map);

  instruction2Map.addControl(new L.Control.Draw({
    position: 'topright',
    edit: {
      featureGroup: drawnItems,
      edit: false,
      poly: {
        allowIntersection: false,
      },
    },
    draw: {
      featureGroup: drawnItems,
      polyline: false,
      polygon: false,
      circle: false,
      marker: false,
      rectangle: true,
      circlemarker: false,
    },
  }));

  function genExclusionZones() {
    exclusionZones = [];
    drawnItems.eachLayer((l) => {
      exclusionZones.push(l.getBounds());
    })
  }

  instruction2Map.on(L.Draw.Event.CREATED, async function (event) {
    if (!rendering) {
      drawnItems.addLayer(event.layer);
      genExclusionZones();
      await renderInstruction2Map();
    }
  });

  instruction2Map.on(L.Draw.Event.DELETED, async function (event) {
    if (!rendering) {
      drawnItems.removeLayer(event.layer);
      genExclusionZones();
      await renderInstruction2Map();
    }
  });
}

function addInstruction2Marker(latlng, html) {
  let marker = new L.Marker(latlng);
  instruction2Map.addLayer(marker);
  if (html != null) {
    marker.bindPopup(html);
  }
  instruction2Markers.push(marker);
}

let rendering = false;

function getValidPoints() {
  return instruction2Points
    .filter((loc) => {
      for (const box of exclusionZones) {
        if (box.contains([loc.latitude, loc.longitude])) {
          return false
        }
      }
      return true;
    });
}

/**
 * Renders the instruction2Markers on the map, making sure to ignore areas that are covered
 * with a block. Also ignores the areas outside of the given time range Domain: [minT, maxT)
 */
async function renderInstruction2Map() {
  rendering = true;
  // clean map
  for (let i = 0; i < instruction2Markers.length; i++) {
    instruction2Map.removeLayer(instruction2Markers[i]);
  }
  instruction2Markers = [];

  $('#instruction2-map-progress-div').show();

  // Calculate the points that fit within these places
  const renderable_points = getValidPoints();
  // get the length
  const renderable_points_length = renderable_points.length;

  $('#instruction2-counter').html(`${(renderable_points_length / 10e3).toFixed(2)}/10.00 Megabytes Used`)
  if (renderable_points_length / 10e3 < 10) {
    $('#instruction2-confirm').prop('disabled', false);
  } else {
    $('#instruction2-confirm').prop('disabled', true);
  }

  let lastlatlng = null
  for (let i = 0; i < renderable_points_length; i++) {
    // get current location
    let loc = renderable_points[i]
    const latlng = [loc.latitude, loc.longitude]
    if (lastlatlng != null) {
      if (Math.hypot(latlng[0] - lastlatlng[0], latlng[1] - lastlatlng[1]) < 0.001) {
        continue;
      }
      lastlatlng = latlng;
    } else {
      lastlatlng = latlng;
    }

    await sleep(1);
    $('#instruction2-map-progress').css('width', `${(i * 100.0) / renderable_points_length}%`);
    addInstruction2Marker(latlng, moment(loc.timestamp).format('MMM D, hh:ss a'));
  }

  $('#instruction2-map-progress-div').hide();
  $('#instruction2-map-progress').css('width', '0%');
  rendering = false;
}

let datePicked = false;
let emailValid = false;
let fileValid = false;

/**
 * when the file handler loads a file, we process it
 */
async function instruction1() {
  // reset
  datePicked = false;
  emailValid = false;
  fileValid = false;
  $('#instruction1-confirm').prop('disabled', true);
  $('#instruction1-email').val('');
  $('#instruction1-daterange').val('');


  function conditionallyEnableButton() {
    if (datePicked == true && emailValid == true && fileValid == true) {
      $('#instruction1-confirm').prop('disabled', false);
    } else {
      $('#instruction1-confirm').prop('disabled', true);
    }
  }

  let mindate = null;
  let maxdate = null;
  let file = null;
  let email = null;


  $('#instruction1-daterange').daterangepicker({
    autoUpdateInput: false,
    minDate: moment('2020-01-01').toDate(),
    maxDate: moment().toDate(),
    opens: 'right',
    locale: {
      cancelLabel: 'Clear'
    }
  });

  // Clear
  $('#instruction1-daterange').on('apply.daterangepicker', function (ev, picker) {
    $(this).val(moment(picker.startDate).format('MM/DD/YYYY') + ' - ' + picker.endDate.format('MM/DD/YYYY'));
    datePicked = true;
    mindate = picker.startDate.valueOf();
    maxdate = picker.endDate.valueOf();
    conditionallyEnableButton();
  });

  $('#instruction1-daterange').on('cancel.daterangepicker', function (ev, picker) {
    $(this).val('');
    datePicked = false;
    conditionallyEnableButton();
  });

  $('#instruction1-email').change(function () {
    const elem = $(this);
    email = elem.val();
    emailValid = elem[0].validity.valid;
    if (emailValid) {
      $('#instruction1-email-error').hide();
    } else {
      $('#instruction1-email-error').show();
    }
    conditionallyEnableButton();
  });

  // when a file is uploaded
  $('#instruction1-file').change(async function () {
    // Set filename
    const fileName = $(this).val().split("\\").pop();
    $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    file = this.files[0];

    function validateObj(obj) {
      if (!Array.isArray(obj.locations)) {
        return false;
      }
      for (const loc of obj.locations) {
        if (typeof loc.latitudeE7 != 'number') {
          return false;
        }
        if (typeof loc.latitudeE7 != 'number') {
          return false;
        }
        if (typeof loc.latitudeE7 != 'number') {
          return false;
        }
      }
      return true;
    }

    // Attempt to parse file
    try {
      fileValid = validateObj(JSON.parse(await file.text()));
    } catch (e) {
      fileValid = false;
    }

    if (fileValid) {
      $('#instruction1-file-error').hide();
    } else {
      $('#instruction1-file-error').show();
    }

    // enable button if necessary
    conditionallyEnableButton();
  });

  // Submit button
  $('#instruction1-confirm').button().click(async function () {
    $('#instruction1-confirm').prop('disabled', true);

    // 14 days before symptoms
    const day = 24 * 60 * 60 * 1000
    const infect_start = mindate - 14 * day;

    // Whichever comes last: 10 days after the start date or 3 days after the end of symptoms
    const infect_end = Math.max(mindate + 10 * day, maxdate + 3 * day);

    instruction2Points = JSON.parse(await file.text()).locations
      .filter((loc) => loc.timestampMs >= infect_start && loc.timestampMs < infect_end)
      .map((loc) => ({
        latitude: loc.latitudeE7 * 10e-8,
        longitude: loc.longitudeE7 * 10e-8,
        timestamp: parseInt(loc.timestampMs),
      }));

    await instruction2(email, infect_start);
  });
}

/**
 * we initialize the methods for the user to begin excluding data
 */
async function instruction2(email, infect_start) {
  loadInstruction2Map();

  $('#instruction1-div').hide();
  $('#instruction2-div').show();

  instruction2Map.invalidateSize(true);

  // corona didn't really get started till 2020
  await renderInstruction2Map();

  $('#instruction2-confirm').prop('disabled', false);
  $('#instruction2-confirm').button().click(async function () {
    $('#instruction2-wait').show();
    try {
      const ret = await fetchJson(`${apiUrl()}/uploadlocations/`, {
        method: 'post',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email,
          infect_start: infect_start,
          locs: getValidPoints(),
        })
      });
      givePermSuccess('Upload Successful');
    } catch (e) {
      console.log(e);
      let errObj = JSON.parse(e.message);
      let errStr = 'Upload Failed: ';
      switch(errObj.code) {
        case 409: {
          errStr += 'This IP has already uploaded data';
          break;
        }
        case 422: {
          errStr += 'Malformed Request (Usually due to email)';
          break;
        }
        default: {
          errStr += 'Unknown Error (Try again later)';
          break;
        }
      }
      givePermError(errStr);
    }
    $('#instruction2-wait').hide();
  });
}

$(document).ready(async function () {
  // begin the process
  await instruction1();
});
