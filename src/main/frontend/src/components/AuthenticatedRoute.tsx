import React from "react";
import Login from "../components/Login";
import { RouteProps } from "react-router";
import { Route } from "react-router-dom";


interface AuthenticatedRouteProps extends Omit<RouteProps, 'component'> {
  component: React.ComponentType<AuthenticatedComponentProps>
	apiKey: ApiKey | null,
	setApiKey: (data:ApiKey|null)=>void
}

function AuthenticatedRoute({
    component:AuthenticatedComponent,
    apiKey,
    setApiKey,
    ...rest
  }: AuthenticatedRouteProps) {

	const apiKeyExpirationTime = apiKey?.expirationTime;
  const isAuthenticated = apiKeyExpirationTime && apiKeyExpirationTime > Date.now() ;

  return (
    <Route {...rest} >
      {isAuthenticated
          ? <AuthenticatedComponent apiKey={apiKey!} setApiKey={setApiKey} />
          : <Login setApiKey={setApiKey}/> }
    </Route>
  );
}

export default AuthenticatedRoute;
