import React from 'react';
import {get,set} from 'local-storage';

type AuthStateType = {
  apiKey: ApiKey | null
};

class AuthState extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
        apiKey: get<ApiKey | null>("apiKey")
    }
  }

  render() {
    return (this.props.children);
  }

}

export default AuthState;
