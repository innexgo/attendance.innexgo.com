import React from 'react';
import { Link } from 'react-router-dom';
import { House, LayoutSidebar } from 'react-bootstrap-icons';

// Bootstrap CSS & Js
import '../style/dashboard.scss';
import 'bootstrap/dist/js/bootstrap.js'
import 'popper.js/dist/popper.js'

import Footer from './Footer';

interface SidebarEntryProps {
  name: string,
  href: string,
  collapsed: boolean,
}
function SidebarEntry(props: React.PropsWithChildren<SidebarEntryProps>) {
  const style = {
    padding: ".5rem 1.25rem"
    color:
  }
  if (props.collapsed) {
    return <Link style={style} className="nav-item nav-link font-weight-bold" to={props.href}>
      {props.children} {props.name}
    </Link>
  } else {
    return <Link style={style} className="nav-item nav-link font-weight-bold" to={props.href}>
      {props.children} {props.name}
    </Link>
  }
}


interface DashboardHeaderProps {
  sidebarCollapsed: boolean;
}

interface DashboardHeaderState {
  sidebarCollapsed: boolean;
}

class DashboardHeader extends React.Component<DashboardHeaderProps, DashboardHeaderState> {

  constructor(props: DashboardHeaderProps) {
    super(props);
    this.state = {
      sidebarCollapsed: props.sidebarCollapsed
    };
  }


  render() {
    const collapsedSidebarStyle = {
      height: "100%",
      width: "100px",
      zIndex: 1,
      position: "fixed",
      top: 0,
      left: 0,
      overflowX: "hidden",
      overflowY: "hidden",
      margin: "0%"
    };

    const expandedSidebarStyle = {
      height: "100%",
      width: "10vw",
      zIndex: 1,
      position: "fixed" as const,
      top: 0,
      left: 0,
      overflowX: "hidden" as const,
      overflowY: "hidden" as const,
      margin: "0%"
    };

    const collapsed = this.state.sidebarCollapsed;

    return (
      <nav className="bg-dark text-light" style={expandedSidebarStyle}>
        <SidebarEntry name="Dashboard" href="/dashboard" collapsed={collapsed}> <House /> </SidebarEntry>
        <SidebarEntry name="Reports" href="/dashboard" collapsed={collapsed}> <House /> </SidebarEntry>
        <SidebarEntry name="Find Student" href="/dashboard" collapsed={collapsed}> <House /> </SidebarEntry>
        <SidebarEntry name="Attendance Irregularities" href="/dashboard" collapsed={collapsed}> <House /> </SidebarEntry>
        <SidebarEntry name="Statistics" href="/dashboard" collapsed={collapsed}> <House /> </SidebarEntry>
        <div className="sidebar-bottom">
          <a className="sidebar-item text-light bg-dark" href="settings.html">
            <i className="fa fa-cog"></i> Settings
          	</a>
          <a className="sidebar-item text-light bg-dark" href="index.html">
            <i className="fa fa-sign-out-alt"></i> Sign out
          	</a>
        </div>
      </nav>
    )
  }
}
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
