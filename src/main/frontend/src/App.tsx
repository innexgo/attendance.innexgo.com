import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import PrivateRoute from './components/PrivateRoute';

import Home from './pages/Home';
import About from './pages/About';
import TermsOfService from './pages/TermsOfService';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Error from './pages/Error';
import { AuthContext } from "./context/auth";

function App() {
  // string stored at beginning
  const maybe_existingTokenJson = localStorage.getItem("tokens");

  const existingTokens = maybe_existingTokenJson == null
                      ? null
                      : JSON.parse(maybe_existingTokenJson) as ApiKey;

  const [authTokens, setAuthTokens] = React.useState(existingTokens);
  
  const setTokens = (data: ApiKey | null) => {
    localStorage.setItem("tokens", JSON.stringify(data));
    setAuthTokens(data);
  }

  return (
    <AuthContext.Provider value={{ authTokens, setAuthTokens: setTokens }}>
      <BrowserRouter>
        <div>
          <Switch>
            <Route path="/" exact component={Home}/>
            <Route path="/about" component={About}/>
            <Route path="/terms_of_service" component={TermsOfService}/>
            <Route path="/login" component = {Login} />
            <Route path="/dashboard" component={Dashboard}/>
            <Route path="/" component={Error}/>
          </Switch>
        </div>
      </BrowserRouter>
    </AuthContext.Provider>
  );

}

export default App;
