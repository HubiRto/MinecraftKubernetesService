import Navbar from "../components/Navbar.tsx";
import Login from "../components/Login.tsx";


const LoginPage: React.FC = () => {
    return (
        <div className="flex flex-col min-h-screen">
            <Navbar/>
            <div className="flex-grow">
                <Login/>
            </div>
        </div>
    );
};

export default LoginPage;
