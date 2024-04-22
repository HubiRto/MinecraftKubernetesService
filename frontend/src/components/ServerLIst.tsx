import React, {useEffect, useState} from 'react';
import {Server} from "../interfaces/Server.ts";
import {useNavigate} from "react-router-dom";
import axios from "axios";

const ServerList: React.FC = () => {
    const [servers, setServers] = useState<Server[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        axios.get<Server[]>('http://localhost:8080/api/v1/server/all', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
            }
        })
            .then(response => {
                setServers(response.data);
                setIsLoading(false);
            })
            .catch(error => {
                console.error('Error fetching data: ', error);
                setIsLoading(false);
            });
    }, []);

    const handleServerClick = (serverId: string) => {
        navigate(`/servers/info/${serverId}`);
    };

    return (
        <div className="container mx-auto mt-20">
            <h1 className="text-xl font-bold mb-5">Lista Serwerów</h1>
            {isLoading ? <p>Ładowanie danych...</p> :
                servers ? (
                    <ul>
                        {servers.map(server => (
                            <li key={server.id} className="mb-4 p-5 shadow-md"
                                onClick={() => handleServerClick(server.id)}>
                                <h2 className="text-lg font-semibold">{server.name}</h2>
                                <p>Status: {server.status}</p>
                                <p>IP Address: {server.ipAddress}</p>
                            </li>
                        ))}
                    </ul>
                ) : <p className="text-red-600">Brak serwerów</p>
            }
        </div>
    );
};

export default ServerList;
