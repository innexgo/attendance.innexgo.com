import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Home from './components/Home';
import About from './components/About';
import TermsOfService from './components/TermsOfService';
import CookiePolicy from './components/CookiePolicy';
import Error from './components/Error';

// Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  return (
    <BrowserRouter>
      <div>
        <Switch>
          <Route path="/about" component={About} />
          <Route path="/terms_of_service" component={TermsOfService} />
          <Route path="/cookie_policy" component={CookiePolicy} />
          <Route path="/" component={Home} exact />
          <Route component={Error} />
        </Switch>
      </div>
    </BrowserRouter>
  );
}

export default App;
