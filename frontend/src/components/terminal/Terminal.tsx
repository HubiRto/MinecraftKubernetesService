import {useEffect, useRef, useState} from "react";
import './Terminal.css'
import {useParams} from "react-router-dom";
import axios from "axios";
import {CompatClient, Stomp, StompSubscription} from "@stomp/stompjs";
import SockJS from 'sockjs-client';

let count = 0;
const Terminal: React.FC = () => {
    count++;
    console.log("component render number: ", count);


    const [logs, setLogs] = useState<string[]>([]);
    const [input, setInput] = useState('');
    const bottomRef = useRef<HTMLDivElement>(null);
    const {id} = useParams<{ id: string }>();

    const [stompClient, setStompClient] = useState<CompatClient | null>(null);
    const [subscription, setSubscription] = useState<StompSubscription>();

    const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter' && input.trim() !== '') {
            setInput('');
        }
    };

    const removePrefixFromLogs = (logs: string[]): string[] => {
        const regex = /\[([^[\]]+)]/g;
        return logs.map(log => log.replace(regex, (match, p1, offset) => {
            if (offset === 0) {
                return match;
            } else {
                return `[${p1.slice(-4)}]`;
            }
        }));
    }

    const colorizeLog = (log: string): string => {
        const regexSquare = /\[([^[\]]+)]\s*\[([^[\]]+)]\s*:\s*(.*)/g;
        const regexCircle = /\((.*?)\)/g;

        if (regexSquare.test(log)) {
            log = log.replace(regexCircle, (_match, group) => {
                return `(<span style="color: #ff7bd8">${group}</span>)`;
            });

            log = log.replace(regexSquare, (_match, p1, p2, p3) => {
                switch (p2) {
                    case 'WARN':
                        return `[<span style="color: #baffc9">${p1}</span>] [<span style="color: #ffdfba;">${p2}</span>]: <span style="color: #ffdfba">${p3}</span>`;
                    case 'INFO':
                        return `[<span style="color: #baffc9">${p1}</span>] [<span style="color: #bae1ff;">${p2}</span>]: ${p3}`;
                    default:
                        return `[<span style="color: #baffc9">${p1}</span>] [${p2}]: ${p3}`;
                }
            });
        }
        return log;
    }

    const closeWebSocket = () => {
        if (stompClient && subscription) {
            subscription.unsubscribe(); // Unsubscribe
            stompClient.disconnect(() => {
                console.log('Disconnected from WebSocket');
            });
            setStompClient(null);
            setSubscription(undefined);
        }
    };

    // Connect to WebSocket
    const connectWebSocket = (id: string | undefined) => {
        if (id && (!stompClient || !subscription)) {
            closeWebSocket(); // Ensure we are not leaking connections
            const socket = new SockJS('http://localhost:8080/ws', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
                }
            });
            const client = Stomp.over(socket);
            client.reconnect_delay = 5000;

            client.connect({}, () => {
                console.log('Connected to WebSocket');
                const sub = client.subscribe(`/user/${id}/server/logs`, (message) => {
                    console.log('Received message from WebSocket:', message.body);
                    const newLogs = JSON.parse(message.body);
                    setLogs(prevLogs => [...prevLogs, ...newLogs.map((log: string) => colorizeLog(log))]);
                });
                setSubscription(sub);
            });
            setStompClient(client);
        }
    };

    useEffect(() => {
        axios.get<string[]>(`http://localhost:8080/api/v1/server/logs/${id}`, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
            }
        })
            .then(response => {
                setLogs(removePrefixFromLogs(response.data).map((i) => {
                    return colorizeLog(i)
                }));
                console.log("Odpowiedz z REST API")
                connectWebSocket(id);
            })
            .catch(error => console.error('Failed to fetch logs:', error));

        console.log("ID przed żądaniem:", id);

        return () => closeWebSocket();
    }, [id]);

    useEffect(() => {
        if (bottomRef.current) {
            bottomRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [logs]);

    return (
        <div
            className="grid grid-rows-1 grid-cols-1 max-w-4xl max-w-full bg-gray-900 rounded-lg shadow-lg overflow-hidden">
            <div className="flex items-center justify-between bg-gray-800 p-3">
                <div className="flex space-x-2">
                    <div className="w-3 h-3 rounded-full bg-red-500"></div>
                    <div className="w-3 h-3 rounded-full bg-yellow-400"></div>
                    <div className="w-3 h-3 rounded-full bg-green-500"></div>
                </div>
                <div className="text-white text-sm">Terminal</div>
            </div>
            <div className="p-4 text-white flex flex-col space-y-2 overflow-y-auto h-80 terminal-scrollbar">
                {logs.map((log, index) => (
                    <div key={index} className="whitespace-pre-wrap break-words"
                         dangerouslySetInnerHTML={{__html: log}}/>
                ))}
                <div ref={bottomRef}></div>
            </div>
            <div className="flex bg-gray-800 p-3">
                <input
                    className="flex-1 bg-transparent text-white outline-none placeholder-gray-400"
                    placeholder="Type your command here..."
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyPress={handleKeyPress}
                />
            </div>
        </div>
    );
};

export default Terminal;