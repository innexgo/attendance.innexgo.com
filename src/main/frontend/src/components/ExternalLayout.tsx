import React from 'react';

// Bootstrap CSS & JS
import '../style/external.scss'
import 'bootstrap/dist/js/bootstrap.js'
import 'popper.js/dist/popper.js'

import Footer from './Footer';
import ExternalHeader from './ExternalHeader';

interface ExternalLayoutProps {
    fixed: boolean;
    transparentTop: boolean;
}

class ExternalLayout extends React.Component<ExternalLayoutProps> {
  render() {
    return (
      <>
        <ExternalHeader fixed={this.props.fixed} transparentTop={this.props.transparentTop} />
        {this.props.children}
        <Footer />
      </>
    )
  }
}

export default ExternalLayout;
