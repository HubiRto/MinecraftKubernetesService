import Navbar from "../components/Navbar.tsx";
import React from "react";
import Register from "../components/Register.tsx";


const RegisterPage: React.FC = () => {
    return (
        <div className="flex flex-col min-h-screen">
            <Navbar/>
            <div className="flex-grow">
                <Register/>
            </div>
        </div>
    );
};

export default RegisterPage;
