import React, {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';

interface RequireAuthProps {
    children: JSX.Element;
}

export const RequireAuth: React.FC<RequireAuthProps> = ({children}) => {
    const navigate = useNavigate();
    const token = localStorage.getItem('jwtToken');

    useEffect(() => {
        if (!token) {
            navigate('/auth/login');
        }
    }, [token, navigate]);

    return token ? children : null;
};