import React from 'react';
import { LayoutSidebar } from 'react-bootstrap-icons';


interface DashboardHeaderProps {
    sidebarCollapsed: boolean;
}

interface DashboardHeaderState {
    sidebarCollapsed: boolean;
}

class DashboardHeader extends React.Component<DashboardHeaderProps, DashboardHeaderState> {

    constructor(props:DashboardHeaderProps) {
        super(props);
        this.state = {
          sidebarCollapsed: props.sidebarCollapsed
        };
    }

    render() {
        return <p>hi</p>
    }
}

export default DashboardHeader;
