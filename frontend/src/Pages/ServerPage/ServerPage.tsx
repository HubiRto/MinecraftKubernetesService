import React, {useEffect, useState} from "react";
import axios from "axios";
import {ServerBasicData} from "../../Models/ServerBasicData";
import {Link} from "react-router-dom";

const ServerPage: React.FC = () => {
    const [servers, setServers] = useState([] as ServerBasicData[]);

    useEffect(() => {
        const fetchServers = async () => {
            try {
                const response = await axios.get<ServerBasicData[]>('http://127.0.0.1:8080/api/v1/server/allNames');
                setServers(response.data);
            } catch (error) {
                console.error('Błąd pobierania serwerów:', error);
            }
        };
        fetchServers();
    }, []);

    return (
        <div>
            <h2>Lista Serwerów:</h2>
            <ul>
                {servers.map(server => (
                    <li key={server.id}>{server.name} - <Link to={`/servers/info/${server.id}`}>{server.id}</Link></li>
                ))}
            </ul>
            <Link to="/servers/create">
                <button>Add server</button>
            </Link>
        </div>
    );
};

export default ServerPage;