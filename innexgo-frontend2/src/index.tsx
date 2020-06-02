import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import Home from './components/Home';
import About from './components/About';
import TermsOfService from './components/TermsOfService';
import CookiePolicy from './components/CookiePolicy';
import Error from './components/Error';

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
