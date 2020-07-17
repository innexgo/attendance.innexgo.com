import React from 'react';
import { Link } from 'react-router-dom';
import { Icon, Gear, House, BoxArrowRight, BarChart, Search, People, LayoutSidebar } from 'react-bootstrap-icons';

// Bootstrap CSS & Js
import '../style/dashboard.scss';
import 'bootstrap/dist/js/bootstrap.js'
import 'popper.js/dist/popper.js'

interface SidebarEntryProps {
  label: string,
  icon: Icon,
  href: string,
  collapsed: boolean,
}
function SidebarEntry(props: React.PropsWithChildren<SidebarEntryProps>) {
  const style = {
    color: "#fff"
  }

  const iconStyle = {
    width: "3rem",
    height: "3rem",
    display: "inline-block",
    borderRadius: "0.5rem",
    padding: "0.2rem",
    margin: "0.1rem"
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
}

interface DashboardHeaderState {
  sidebarCollapsed: boolean;
}

class DashboardLayout extends React.Component<DashboardHeaderProps, DashboardHeaderState> {

  constructor(props: DashboardHeaderProps) {
    super(props);
    this.state = {
      sidebarCollapsed: true
    };
  }

  toggleSidebar = (_: React.MouseEvent) => {
    this.setState({ sidebarCollapsed: !this.state.sidebarCollapsed });
  }

  render() {


    const collapsed = this.state.sidebarCollapsed;

    const width = collapsed ? "5rem" : "20rem";

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

    const nonSidebarStyle = {
      marginLeft: width
    }

    return (
      <div>
        <nav className="bg-dark text-light" style={sidebarStyle}>
          <button type="button" className="btn btn-light" onClick={this.toggleSidebar}>
            Button
		      </button>
          <SidebarEntry label="Dashboard" href="/dashboard" collapsed={collapsed} icon={House} />
          <SidebarEntry label="Find Student" href="/dashboard" collapsed={collapsed} icon={Search} />
          <SidebarEntry label="My Classes" href="/classes" collapsed={collapsed} icon={People} />
          <SidebarEntry label="Reports" href="/reports" collapsed={collapsed} icon={BarChart} />
          <div style={sidebarBottom}>
            <SidebarEntry label="Settings" href="/settings" collapsed={collapsed} icon={Gear} />
            <SidebarEntry label="Log Out" href="/logout" collapsed={collapsed} icon={BoxArrowRight} />
          </div>
        </nav>
        <div style={nonSidebarStyle}>
          {this.props.children}
        </div>
      </div>
    )
  }
}

export default DashboardLayout;
