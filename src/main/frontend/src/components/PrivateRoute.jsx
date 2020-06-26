import React from "react";
import { Route, Redirect } from "react-router-dom";
import { useAuth } from "../context/auth";

function PrivateRoute({ component: Component, ...rest }) {
  const {authTokens, }  = useAuth();
  const isAuthenticated = authTokens?.expirationTime > Date.now() ?? false;

  return (
    <Route
      {...rest}
      render={props =>
        isAuthenticated ? (
          <Component {...props} />
        ) : (
          <Redirect to={{ pathname: "/login", forward: window.location }} />
        )
      }
    />
  );
}

export default PrivateRoute;