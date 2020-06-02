import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import Home from './pages/Home';
import About from './pages/About';
import TermsOfService from './pages/TermsOfService';
import CookiePolicy from './pages/CookiePolicy';
import Error from './pages/Error';

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <div>
        <Switch>
          <Route path="/" component={Home} exact />
          <Route path="/about" component={About} />
          <Route path="/terms_of_service" component={TermsOfService} />
          <Route path="/cookie_policy" component={CookiePolicy} />
          <Route component={Error} />
        </Switch>
      </div>
    </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
);
