import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaSignOutAlt, FaBars } from 'react-icons/fa';

const Navbar: React.FC = () => {
    const [isOpen, setIsOpen] = useState(false);
    const token = localStorage.getItem("jwtToken");
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("jwtToken");
        navigate('/auth/login');
    };

    return (
        <nav className="bg-gray-800 text-white p-2.5 fixed top-0 left-0 right-0 z-10">
            <div className="flex justify-between items-center w-full">
                <div className="flex items-center">
                    <span className="text-xl font-bold mr-4">Pomoku</span>
                    <div className="hidden sm:flex sm:space-x-4">
                        {token ? (
                            <>
                                <button onClick={() => navigate('/')}
                                        className="hover:bg-gray-700 px-3 py-2 rounded text-sm font-medium">Home
                                </button>
                                <button onClick={() => navigate('/servers')}
                                        className="hover:bg-gray-700 px-3 py-2 rounded text-sm font-medium">Servers
                                </button>
                            </>
                        ) : (
                            <>
                                <button onClick={() => navigate('/auth/login')}
                                        className="hover:bg-gray-700 px-3 py-2 rounded text-sm font-medium">Login
                                </button>
                                <button onClick={() => navigate('/auth/register')}
                                        className="hover:bg-gray-700 px-3 py-2 rounded text-sm font-medium">Register
                                </button>
                            </>
                        )}
                    </div>
                </div>
                {token && (
                    <button onClick={handleLogout} className="hover:bg-gray-700 p-2 rounded sm:block hidden">
                        <FaSignOutAlt className="text-xl"/>
                    </button>
                )}
                <button
                    className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white sm:hidden"
                    onClick={() => setIsOpen(!isOpen)}>
                    <FaBars className="block h-6 w-6"/>
                </button>
            </div>
            {/* Mobile menu */}
            {isOpen && (
                <div className="sm:hidden">
                    <div className="px-2 pt-2 pb-3 space-y-1">
                        {token ? (
                            <>
                                <button onClick={() => navigate('/')}
                                        className="hover:bg-gray-700 block px-3 py-2 rounded text-base font-medium">Home
                                </button>
                                <button onClick={() => navigate('/servers')}
                                        className="hover:bg-gray-700 block px-3 py-2 rounded text-base font-medium">Servers
                                </button>
                                <button onClick={handleLogout}
                                        className="hover:bg-gray-700 block px-3 py-2 rounded text-base font-medium">
                                    <FaSignOutAlt className="text-lg"/>
                                </button>
                            </>
                        ) : (
                            <>
                                <button onClick={() => navigate('/auth/login')}
                                        className="hover:bg-gray-700 block px-3 py-2 rounded text-base font-medium">Login
                                </button>
                                <button onClick={() => navigate('/auth/register')}
                                        className="hover:bg-gray-700 block px-3 py-2 rounded text-base font-medium">Register
                                </button>
                            </>
                        )}
                    </div>
                </div>
            )}
        </nav>
    );
};

export default Navbar;
