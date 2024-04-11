import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import {createBrowserRouter, RouterProvider} from 'react-router-dom';
import ServerPage from "./Pages/ServerPage/ServerPage";
import NotFoundPage from "./Pages/NotFoundPage";
import CreateServerPage from "./Pages/ServerPage/CreateServerPage";
import ServerInfoPage from "./Pages/ServerPage/ServerInfoPage";
import BekaPage from "./Pages/ServerPage/BekaPage";


const router = createBrowserRouter([
    {
        path: '/servers',
        element: <ServerPage/>,
        errorElement: <NotFoundPage/>
    },
    {
        path: '/servers/create',
        element: <CreateServerPage/>
    },
    {
        path: 'servers/info/:id',
        element: <ServerInfoPage/>
    },
    {
        path: 'servers/beka',
        element: <BekaPage/>
    },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <RouterProvider router={router}/>
    </React.StrictMode>,
);
