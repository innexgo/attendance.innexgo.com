// Imports
const express = require('express');

const app = express();
const port = 8080;

// set up a route to redirect http to https
app.get('*', function(req, res) {
    res.redirect('https://' + req.headers.host + req.url);
})


app.listen(port, () => console.log(`App listening on port ${port}!`));
