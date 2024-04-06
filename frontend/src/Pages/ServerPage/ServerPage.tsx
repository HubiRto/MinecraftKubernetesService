import {useEffect, useState} from "react";
import axios from "axios";
import * as Console from "console";

const ServerPage = () => {
    const [servers, setServers] = useState([]);

    useEffect(() => {
        const fetchServers = async () => {
            try {
                const response = await axios.get('http://127.0.0.1:8080/api/v1/server/allNames');
                console.log(response.data)
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
                {servers.map((serverName, index) => (
                    <li key={index}>{serverName}</li>
                ))}
            </ul>
        </div>
    );
};

export default ServerPage;