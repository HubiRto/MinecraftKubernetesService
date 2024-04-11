import {useEffect, useState} from "react";

import Stomp from 'stompjs';

const BekaPage = () => {
    const [randomNumber, setRandomNumber] = useState(null);

    useEffect(() => {
        const socket = new WebSocket("ws://127.0.0.1:8080/randomNumber");
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            stompClient.subscribe('/topic/randomNumber', (message) => {
                setRandomNumber(JSON.parse(message.body));
            });
        });

        return () => {
            stompClient.disconnect(() => {});
        };
    }, []);

    const requestRandomNumber = () => {
        fetch('/randomNumber')
            .then(response => response.json())
            .then(data => setRandomNumber(data));
    };

    return (
        <div>
            <h1>Random Number Generator</h1>
            <button onClick={requestRandomNumber}>Generate Random Number</button>
            {randomNumber && <p>Random Number: {randomNumber}</p>}
        </div>
    );
};

export default BekaPage;