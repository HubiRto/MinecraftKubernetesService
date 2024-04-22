import Navbar from "../components/Navbar.tsx";
import React from "react";
import ServerList from "../components/ServerLIst.tsx";

const ServerListPage: React.FC = () => {

    return (
        <div className="flex flex-col min-h-screen">
            <Navbar/>
            <ServerList/>
        </div>
    );
};

export default ServerListPage;
