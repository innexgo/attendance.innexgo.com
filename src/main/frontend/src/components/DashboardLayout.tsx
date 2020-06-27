import React from 'react';

// Bootstrap CSS & Js
import '../style/dashboard.scss';
import 'bootstrap/dist/js/bootstrap.js'
import 'popper.js/dist/popper.js'

import Footer from './Footer';
import DashboardHeader from './DashboardHeader';

interface DashboardLayoutProps {

}

class DashboardLayout extends React.Component<DashboardLayoutProps> {
  render() {
    return (
      <>
        <DashboardHeader sidebarCollapsed={false} />
        {this.props.children}
        <Footer />
      </>
    );
  }
}

export default DashboardLayout;
