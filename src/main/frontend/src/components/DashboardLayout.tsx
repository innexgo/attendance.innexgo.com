import React from 'react';
import { Link } from 'react-router-dom';
import { Icon, Gear, House, BoxArrowRight, BarChart, Search, People, LayoutSidebar } from 'react-bootstrap-icons';

// Bootstrap CSS & Js
import '../style/dashboard.scss';
import 'bootstrap/dist/js/bootstrap.js'
import 'popper.js/dist/popper.js'

import Footer from './Footer';

interface SidebarEntryProps {
  label: string,
  icon: Icon,
  href: string,
  collapsed: boolean,
}
function SidebarEntry(props: React.PropsWithChildren<SidebarEntryProps>) {
  const style = {
    padding: ".5rem 1.25rem",
    color: "#fff"
  }

  const iconStyle = {
    width: "2vw",
    height: "2vw",
    display: "inline-block",
    borderRadius: "0.5vw",
    color: "#fefefe",
    background: "#990000ff",
    padding: "0.2vw",
    margin: "0.1vw"
  };

	const Icon = props.icon;
  if (props.collapsed) {
    return <Link style={style} className="nav-item nav-link" to={props.href}>
			<Icon style={iconStyle} />
    </Link>
  } else {
    return <Link style={style} className="nav-item nav-link" to={props.href}>
			<Icon style={iconStyle} /> {props.label}
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


    const collapsed = this.state.sidebarCollapsed;

    const width = collapsed ? "10px" : "20vw";

    const sidebarStyle = {
      height: "100%",
      width: width,
      zIndex: 1,
      position: "fixed" as const,
      top: 0,
      left: 0,
      overflowX: "hidden" as const,
      overflowY: "hidden" as const,
      margin: "0%"
    };

    const sidebarBottom = {
 position: 'absolute' as const,
  bottom: 0,
  right: 0,
  left: 0,
    };

    return (
      <nav className="bg-dark text-light" style={sidebarStyle}>
        <SidebarEntry label="Dashboard" href="/dashboard" collapsed={collapsed} icon={House} />
        <SidebarEntry label="Find Student" href="/dashboard" collapsed={collapsed} icon={Search} />
        <SidebarEntry label="My Classes" href="/classes" collapsed={collapsed} icon={People} />
        <SidebarEntry label="Reports" href="/reports" collapsed={collapsed} icon={BarChart} />
        <div style={sidebarBottom}>
        	<SidebarEntry label="Settings" href="/settings" collapsed={collapsed} icon={Gear} /> 
        	<SidebarEntry label="Log Out" href="/logout" collapsed={collapsed} icon={BoxArrowRight} /> 
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
