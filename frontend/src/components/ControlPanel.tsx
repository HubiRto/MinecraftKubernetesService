import {FaNetworkWired, FaPlay, FaRedo, FaServer, FaStop} from "react-icons/fa";
import React from "react";

const ControlPanel: React.FC = () => {
    return (
        <div className="p-4 bg-gray-200 h-full rounded-lg shadow-lg">
            <div className="flex mb-4">
                <button className="flex-grow flex justify-center items-center bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                    <FaPlay className="mr-2" /> Start
                </button>
                <button className="flex-grow flex justify-center items-center bg-yellow-500 hover:bg-yellow-700 text-white font-bold py-2 px-4 rounded mx-2">
                    <FaRedo className="mr-2" /> Restart
                </button>
                <button className="flex-grow flex justify-center items-center bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
                    <FaStop className="mr-2" /> Stop
                </button>
            </div>
            <div className="space-y-2">
                <div className="bg-white p-4 rounded-lg shadow flex items-center">
                    <FaServer className="text-lg mr-2" />
                    <div>
                        <h4 className="font-bold text-lg">Server Information</h4>
                        <p><strong>IP Address:</strong> 192.168.1.1</p>
                        <p><strong>Status:</strong> Running</p>
                        <p><strong>More Info:</strong> Other details...</p>
                    </div>
                </div>
                <div className="bg-white p-4 rounded-lg shadow flex items-center">
                    <FaNetworkWired className="text-lg mr-2" />
                    <div>
                        <h4 className="font-bold text-lg">Network Stats</h4>
                        <p><strong>Inbound Traffic:</strong> 120 Mb/s</p>
                        <p><strong>Outbound Traffic:</strong> 150 Mb/s</p>
                    </div>
                </div>
                {/* Dodatkowe bloki z informacjami można dodać tutaj */}
            </div>
        </div>
    );
};

export default ControlPanel;