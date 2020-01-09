"use strict"

async function initializeOverview() {
  let apiKey = Cookies.getJSON('apiKey');
}

async function initializeLocationOptions() {
  let apiKey = Cookies.getJSON('apiKey');

  try {
    let locations = await fetchJson(`${apiUrl()}/location/?apiKey=${apiKey.key}`);
    let locationSelect = $('#overview-locationid');

    // Add the options
    locationSelect.empty();
    locations.forEach(l => locationSelect.append(`<option value="${l.id}">${l.name}</option>`));

    // Preselect Disabled option
    locationSelect.prepend(
      `<option selected hidden disabled value="null">Select Default Location</option>`
    );
    locationSelect.val("null");

    // On change, reload thing
    locationSelect.change(async function() {
      let selectedValue = $('#overview-locationid').val();
      if(selectedValue != null) {
        Cookies.set('default-locationid', selectedValue);
      } else {
        console.log('Can\'t set the locationId');
      }
    });

  } catch(err) {
    console.log(err);
    givePermError('Failed to load locations');
  }
}

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});

$(document).ready(async function() {
  await initializeOverview();
  await initializeLocationOptions();
});

