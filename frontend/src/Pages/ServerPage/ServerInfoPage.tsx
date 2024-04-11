import {useCallback, useEffect, useState} from "react";
import axios from "axios";
import {ServerData} from "../../Models/ServerData";
import {useNavigate, useParams} from "react-router-dom";
import {ServerStatus} from "../../Models/ServerStatus";

import Stomp from 'stompjs';
import SockJS from 'sockjs-client'
import useWebSocket from "react-use-websocket";

const ServerInfoPage = () => {
    const [server, setServer] = useState<ServerData | null>(null);
    const {id} = useParams<{ id: string }>();
    const navigate = useNavigate();

    const { sendMessage, lastMessage, readyState } = useWebSocket(
        `wss://localhost:8080/ws`,
        {
            shouldReconnect: () => true
        }
    );

    useEffect(() => {
        if (readyState === WebSocket.OPEN) {
            sendMessage(JSON.stringify({ id }));
        }
    }, [id, sendMessage, readyState]);

    useEffect(() => {
        if (lastMessage) {
            console.log(lastMessage);
        }
    }, [lastMessage]);


    //
    // const fetchServerDetails = async () => {
    //     try {
    //         // console.log(id)
    //         const response = await axios.get<ServerData>(`http://127.0.0.1:8080/api/v1/server/info/${id}`);
    //         setServer(response.data);
    //     } catch (error) {
    //         console.error('Błąd pobierania szczegółów serwera:', error);
    //     }
    // };
    //
    // fetchServerDetails();
    // return () => {
    //     client.disconnect(() => {});
    // };

    const handleDelete = async () => {
        try {
            const confirmDelete = window.confirm('Czy na pewno chcesz usunąć serwer?');
            if (confirmDelete) {
                await axios.delete(`http://127.0.0.1:8080/api/v1/server/remove/${id}`);
                navigate("/servers")
            }
        } catch (error) {
            console.error('Błąd podczas usuwania serwera:', error);
        }
    };

    const getTextColor = (status: ServerStatus) => {
        // console.log(server)
        switch (status) {
            case ServerStatus.DISABLE:
                return 'red';
            case ServerStatus.RUNNING:
                return 'green';
            case ServerStatus.START:
                return 'orange';
            default:
                return 'blue'
        }
    };

    return (
        <div>
            {server ? (
                <div>
                    <p>ID: {server.id}</p>
                    <p>Nazwa: {server.name}</p>
                    <p>Typ: {server.type}</p>
                    <p>Ram: {server.ram}</p>
                    <p>Dysk: {server.disk}</p>
                    <p>Wersja: {server.version}</p>
                    <p>Status: <span style={{backgroundColor: getTextColor(server.status)}}>{server.status}</span></p>
                    <p>Port: {server.port}</p>
                    <p>Ścieżka: {server.path}</p>
                    <button onClick={handleDelete}>Usuń serwer</button>
                </div>
            ) : (
                <p>Ładowanie danych...</p>
            )}
        </div>
    );
};

export default ServerInfoPage;