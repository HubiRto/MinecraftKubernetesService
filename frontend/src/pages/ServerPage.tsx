import Navbar from "../components/Navbar.tsx";
import ControlPanel from "../components/ControlPanel.tsx";
import Terminal from "../components/terminal/Terminal.tsx";
import {LineChart} from "@mui/x-charts";

const uData = [4000, 3000, 2000, 2780, 1890, 2390, 3490];
const pData = [2400, 1398, 9800, 3908, 4800, 3800, 4300];
const xLabels = [
    'Page A',
    'Page B',
    'Page C',
    'Page D',
    'Page E',
    'Page F',
    'Page G',
];

const ServerPage: React.FC = () => {
    return (
        <div className="flex flex-col min-h-screen">
            <Navbar />
            <div className="flex flex-grow overflow-hidden mt-10">
                <div className="p-5 w-full">
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 h-full">
                        <div className="md:col-span-1 p-4 shadow-lg rounded-lg bg-white h-full">
                            <ControlPanel />
                        </div>
                        <div className="md:col-span-2 flex flex-col gap-4 h-full">
                            <div className="flex-1 p-4 shadow-lg rounded-lg bg-white">
                                <Terminal />
                            </div>
                            <div className="flex flex-col md:flex-row h-full">
                                <div className="w-full md:w-1/2 p-4 shadow-lg rounded-lg bg-white">
                                    <LineChart
                                        series={[
                                            { data: pData, label: 'pv' },
                                            { data: uData, label: 'uv' },
                                        ]}
                                        xAxis={[{ scaleType: 'point', data: xLabels }]}
                                    />
                                </div>
                                <div className="w-full md:w-1/2 p-4 shadow-lg rounded-lg bg-white">
                                    <LineChart
                                        series={[
                                            { data: pData, label: 'pv' },
                                            { data: uData, label: 'uv' },
                                        ]}
                                        xAxis={[{ scaleType: 'point', data: xLabels }]}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ServerPage;
