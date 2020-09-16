import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import AuthenticatedRoute from './components/AuthenticatedRoute';

import Home from './pages/Home';
import About from './pages/About';
import TermsOfService from './pages/TermsOfService';
import Dashboard from './pages/Dashboard';
import Error from './pages/Error';

function getPreexistingApiKey() {
  const preexistingApiKeyString = localStorage.getItem("apiKey");
  if (preexistingApiKeyString == null) {
    return null;
  } else {
    try {
      // TODO validate here
      return JSON.parse(preexistingApiKeyString) as ApiKey;
    } catch (e) {
      // try to clean up a bad config
      localStorage.setItem("apiKey", JSON.stringify(null));
      return null;
    }
  }
}

function App() {
  const preexistingApiKey = getPreexistingApiKey();

  const [apiKey, setApiKeyState] = React.useState(preexistingApiKey);

  const setApiKeyStateAndStorage = (data: ApiKey | null) => {
    localStorage.setItem("apiKey", JSON.stringify(data));
    setApiKeyState(data);
  }

  return (
    <BrowserRouter>
      <Switch>
        <Route path="/" exact component={Home} />
        <Route path="/about" component={About} />
        <Route path="/terms_of_service" component={TermsOfService} />
        <AuthenticatedRoute
          path="/dashboard"
          apiKey={apiKey}
          setApiKey={setApiKeyStateAndStorage}
          component={Dashboard}
        />
        <Route path="/" component={Error} />
      </Switch>
    </BrowserRouter>
  );
}

export default App;
