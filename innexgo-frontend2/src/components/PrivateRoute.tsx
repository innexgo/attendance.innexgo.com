import React from 'react';
import { Route, RouteProps} from 'react-router-dom';

type

function PrivateRoute({ component: Component, ...rest}:RouteProps) {
  return(
    <Route {...rest} render={(props) => (
      <Component {...props} />
    )}
    />
  );
}

export default PrivateRoute;
