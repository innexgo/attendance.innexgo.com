import React from 'react';
import { Link } from 'react-router-dom';
import { SvgIconComponent, Settings, Home, ExitToApp, BarChart, Search, People, Menu } from '@material-ui/icons';

// Bootstrap CSS & Js
import '../style/dashboard.scss';
import 'bootstrap/dist/js/bootstrap.js'
import 'popper.js/dist/popper.js'

  const iconStyle = {
    width: "2rem",
    height: "2rem",
  };

interface SidebarEntryProps {
  label: string,
  icon: SvgIconComponent,
  href: string,
  collapsed: boolean,
}

function SidebarEntry(props: React.PropsWithChildren<SidebarEntryProps>) {
  const style = {
    color: "#fff"
  }


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

    const widthrem = collapsed ? 4 : 15;

    const sidebarStyle = {
      height: "100%",
      width: `${widthrem}rem`,
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
      marginLeft: `${widthrem}rem`
    }

    return (
      <div>
        <nav className="bg-dark text-light" style={sidebarStyle}>
					<div>
            <button type="button" className="btn btn-lg text-light float-right" onClick={this.toggleSidebar}>
              <Menu style={iconStyle}/>
            </button>
          </div>

          <SidebarEntry label="Dashboard" href="/dashboard" collapsed={collapsed} icon={Home} />
          <SidebarEntry label="Find Student" href="/findstudent" collapsed={collapsed} icon={Search} />
          <SidebarEntry label="My Classes" href="/classes" collapsed={collapsed} icon={People} />
          <SidebarEntry label="Reports" href="/reports" collapsed={collapsed} icon={BarChart} />
          <div style={sidebarBottom}>
            <SidebarEntry label="Settings" href="/settings" collapsed={collapsed} icon={Settings} />
            <SidebarEntry label="Log Out" href="/logout" collapsed={collapsed} icon={ExitToApp} />
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
