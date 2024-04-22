import Navbar from "../components/Navbar.tsx";


const HomePage: React.FC = () => {
    return (
        <div className="flex flex-col min-h-screen">
            <Navbar/>
            <p>HOME PAGE</p>
        </div>
    );
};

export default HomePage;
