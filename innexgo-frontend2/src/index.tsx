import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import AuthState from './components/AuthState';
import PrivateRoute from './components/PrivateRoute';

import Home from './pages/Home';
import About from './pages/About';
import TermsOfService from './pages/TermsOfService';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Error from './pages/Error';


ReactDOM.render(
  <React.StrictMode>
    <AuthState>
      <BrowserRouter>
        <div>
          <Switch>
            <Route path="/" exact>
              <Home />
            </Route>
            <Route path="/about" >
              <About />
            </Route>
            <Route path="/terms_of_service" >
              <TermsOfService />
            </Route>
            <Route path="/login" >
              <Login />
            </Route>
            <PrivateRoute path="/Dashboard">
              <Dashboard />
            </PrivateRoute>
            <Route>
              <Error />
            </Route>
          </Switch>
        </div>
      </BrowserRouter>
    </AuthState>
  </React.StrictMode>,
  document.getElementById('root')
);
