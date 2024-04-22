import React, {useState} from "react";
import {Link} from "react-router-dom";
import {FaFacebookF, FaGithub, FaGoogle, FaLock} from "react-icons/fa";
import {IoEye, IoEyeOff, IoPerson} from "react-icons/io5";
import {CiMail} from "react-icons/ci";

const Register: React.FC = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [errors, setErrors] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [showPassword, setShowPassword] = useState({password: false, confirmPassword: false});

    const validateInput = (name: string, value: string): string => {
        switch (name) {
            case 'firstName':
            case 'lastName':
                if (!value) return 'To pole jest wymagane';
                if (value.length < 3) return 'To pole musi zawierać min. 3 znaki';
                return '';
            case 'email':
                if (!value) return 'Email jest wymagany';
                if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(value)) return 'Niepoprawny adres email';
                return '';
            case 'password':
            case 'confirmPassword':
                if (!value) return 'Hasło jest wymagane';
                if (value.length < 6) return 'Hasło musi zawierać min. 6 znaków';
                if (name === 'confirmPassword' && value !== formData.password) return 'Hasła nie są takie same';
                return '';
            default:
                return '';
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData(prev => ({...prev, [name]: value}));
        setErrors(prev => ({...prev, [name]: validateInput(name, value)}));
    };

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        let valid = true;
        Object.keys(formData).forEach(key => {
            const error = validateInput(key, formData[key as keyof typeof formData]);
            if (error) valid = false;
            setErrors(prev => ({...prev, [key]: error}));
        });
        if (valid) {
            console.log('Form data submitted:', formData);
            // Implement submission logic here
        }
    };

    const toggleShowPassword = (field: 'password' | 'confirmPassword') => {
        setShowPassword(prev => ({...prev, [field]: !prev[field]}));
    };


    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white shadow-md rounded-lg w-full max-w-md">
                <h1 className="text-center text-3xl font-bold text-gray-900">Rejestracja</h1>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="flex gap-4">
                        <div className="flex-1 min-w-0 relative">
                            <IoPerson className="absolute left-3 top-3 text-gray-500 z-20"/>
                            <input id="firstName" name="firstName" type="text" autoComplete="given-name"
                                   className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                   placeholder="Imię" value={formData.firstName} onChange={handleChange}/>
                            {errors.firstName && <p className="text-red-500 text-xs mt-1">{errors.firstName}</p>}
                        </div>
                        <div className="flex-1 min-w-0 relative">
                            <IoPerson className="absolute left-3 top-3 text-gray-500 z-20"/>
                            <input id="lastName" name="lastName" type="text" autoComplete="family-name"
                                   className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                   placeholder="Nazwisko" value={formData.lastName} onChange={handleChange}/>
                            {errors.lastName && <p className="text-red-500 text-xs mt-1">{errors.lastName}</p>}
                        </div>
                    </div>
                    <div className="relative">
                        <CiMail className="absolute left-3 top-3 text-gray-500 z-20"/>
                        <input id="email" name="email" type="text" autoComplete="email"
                               className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                               placeholder="Adres Email" value={formData.email} onChange={handleChange}/>
                        {errors.email && <p className="text-red-500 text-xs italic mt-1">{errors.email}</p>}
                    </div>
                    <div className="flex gap-4">
                        <div className="flex-1 min-w-0 relative">
                            <FaLock className="absolute left-3 top-3 text-gray-500 z-20"/>
                            <input id="password" name="password" type={showPassword.password ? 'text' : 'password'}
                                   autoComplete="new-password"
                                   className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                   placeholder="Hasło" value={formData.password} onChange={handleChange}/>
                            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
                            <IoEyeOff
                                className={`absolute right-3 top-3 text-gray-500 cursor-pointer z-20 ${showPassword.password ? 'hidden' : 'block'}`}
                                onClick={() => toggleShowPassword('password')}/>
                            <IoEye
                                className={`absolute right-3 top-3 text-gray-500 cursor-pointer z-20 ${showPassword.password ? 'block' : 'hidden'}`}
                                onClick={() => toggleShowPassword('password')}/>
                        </div>
                        <div className="flex-1 min-w-0 relative">
                            <FaLock className="absolute left-3 top-3 text-gray-500 z-20"/>
                            <input id="confirmPassword" name="confirmPassword"
                                   type={showPassword.confirmPassword ? 'text' : 'password'} autoComplete="new-password"
                                   className="appearance-none rounded-md relative block w-full px-10 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                   placeholder="Powórz hasło" value={formData.confirmPassword}
                                   onChange={handleChange}/>
                            {errors.confirmPassword &&
                                <p className="text-red-500 text-xs mt-1">{errors.confirmPassword}</p>}
                            <IoEyeOff
                                className={`absolute right-3 top-3 text-gray-500 cursor-pointer z-20 ${showPassword.confirmPassword ? 'hidden' : 'block'}`}
                                onClick={() => toggleShowPassword('confirmPassword')}/>
                            <IoEye
                                className={`absolute right-3 top-3 text-gray-500 cursor-pointer z-20 ${showPassword.confirmPassword ? 'block' : 'hidden'}`}
                                onClick={() => toggleShowPassword('confirmPassword')}/>
                        </div>
                    </div>
                    <button type="submit"
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                        Stwórz konto
                    </button>
                    <div className="text-sm text-center">
                        <span>Masz już konto? </span>
                        <Link to="/auth/login" className="font-medium text-indigo-600 hover:text-indigo-500">Zaloguj
                            się</Link>
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

export default Register;
