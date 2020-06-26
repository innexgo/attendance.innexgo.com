import React from 'react';

// Bootstrap CSS & JS
import '../style/external.scss'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.js'

import Footer from './Footer';
import Header from './Header';

interface ExternalLayoutProps {
    fixed: boolean;
    transparentTop: boolean;
}

class ExternalLayout extends React.Component<ExternalLayoutProps> {
  render() {
    return (
      <div>
        <Header fixed={this.props.fixed} transparentTop={this.props.transparentTop} />
        <div className="content">
          {this.props.children}
        </div>
        <Footer />
      </div>
    )
  }
}

export default ExternalLayout;
