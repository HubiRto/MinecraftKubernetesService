import React, {useState} from "react";
import {FaFacebookF, FaGithub, FaGoogle} from "react-icons/fa";
import {IoEye, IoEyeOff} from "react-icons/io5";
import {CiMail} from "react-icons/ci";
import {RiLockPasswordLine} from "react-icons/ri";
import {Link, useNavigate} from "react-router-dom";
import {LoginRequest} from "../interfaces/LoginRequest.ts";
import axios from "axios";
import {LoginResponse} from "../interfaces/LoginResponse.ts";

const API_URL: string = 'http://127.0.0.1:8080/api/v1/auth';

const Login: React.FC = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState<LoginRequest>({
        email: '',
        password: ''
    });
    const [showPassword, setShowPassword] = useState(false);
    const [errors, setErrors] = useState({
        email: '',
        password: ''
    });

    const validateEmail = (email: string) => {
        if (!email) {
            return 'Email jest wymagany';
        } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(email)) {
            return 'Niepoprawny adres email';
        }
        return '';
    };

    const validatePassword = (password: string) => {
        if (!password) {
            return 'Hasło jest wymagane';
        }
        return '';
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value, type, checked} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
        let error = '';
        if (name === 'email') {
            error = validateEmail(value);
        } else if (name === 'password') {
            error = validatePassword(value);
        }
        setErrors(prev => ({
            ...prev,
            [name]: error
        }));
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const emailError = validateEmail(formData.email);
        const passwordError = validatePassword(formData.password);
        if (!emailError && !passwordError) {
            await axios.post<LoginResponse>(`${API_URL}/login`, formData)
                .then(function (response) {
                    localStorage.setItem("jwtToken", response.data.token);
                    navigate("/");
                })
                .catch(function (error) {
                    console.log('ERROR: ' + error);
                })
        } else {
            setErrors({email: emailError, password: passwordError});
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white shadow-md rounded-lg w-full max-w-md">
                <h1 className="text-center text-3xl font-bold text-gray-900">Logowanie</h1>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="relative">
                        <CiMail className="absolute left-3 top-3 text-gray-500 z-20"/>
                        <input id="email" name="email" type="email" autoComplete="email"
                               className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                               placeholder="Adres Email" value={formData.email} onChange={handleChange}/>
                        {errors.email && <p className="text-red-500 text-xs italic mt-1">{errors.email}</p>}
                    </div>
                    <div className="relative">
                        <RiLockPasswordLine className="absolute left-3 top-3 text-gray-500 z-20"/>
                        <input id="password" name="password" type={showPassword ? 'text' : 'password'}
                               autoComplete="current-password"
                               className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                               placeholder="Hasło" value={formData.password} onChange={handleChange}/>
                        {showPassword ? <IoEyeOff className="absolute right-3 top-3 text-gray-500 cursor-pointer z-10"
                                                  onClick={() => setShowPassword(false)}/>
                            : <IoEye className="absolute right-3 top-3 text-gray-500 cursor-pointer z-10"
                                     onClick={() => setShowPassword(true)}/>}
                        {errors.password && <p className="text-red-500 text-xs italic mt-1">{errors.password}</p>}
                    </div>
                    <div className="flex items-center justify-between">
                        <div className="flex items-center">
                            <input id="remember-me" name="rememberMe" type="checkbox"
                                   className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                                   onChange={handleChange}/>
                            <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">Zapamiętaj
                                mnie</label>
                        </div>
                        <div className="text-sm">
                            <a href="#" className="font-medium text-indigo-600 hover:text-indigo-500">Zapomniałeś
                                hasła?</a>
                        </div>
                    </div>
                    <button type="submit"
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                        Zaloguj
                    </button>
                    <div className="text-sm text-center">
                        <span>Nie masz jeszcze konta? </span>
                        <Link to="/auth/register" className="font-medium text-indigo-600 hover:text-indigo-500">Stwórz
                            je</Link>
                    </div>
                    <div className="flex items-center justify-center space-x-3 mt-6">
                        <FaGoogle className="text-2xl text-red-500 cursor-pointer"/>
                        <FaFacebookF className="text-2xl text-blue-600 cursor-pointer"/>
                        <FaGithub className="text-2xl text-gray-800 cursor-pointer"/>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;