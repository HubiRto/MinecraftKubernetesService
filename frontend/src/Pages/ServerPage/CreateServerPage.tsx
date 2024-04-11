import React, {useState} from "react";
import axios from "axios";
import {useNavigate} from 'react-router-dom'

const CreateServerPage = () => {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');
    const [formData, setFormData] = useState({
        name: '',
        ram: '',
        disk: '',
        version: '',
        type: 'PAPER',
        port: 0
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://127.0.0.1:8080/api/v1/server/create', formData);
            if (response.status === 200) {
                navigate('/servers')
            } else {
                setErrorMessage('Błąd podczas tworzenia serwera');
            }
        } catch (error) {
            setErrorMessage('Błąd podczas tworzenia serwera v2');
        }
    };

    return (
        <div>
            {errorMessage && <div>{errorMessage}</div>}
            <form onSubmit={handleSubmit}>
                <label>
                    Nazwa:
                    <input type="text" name="name" value={formData.name} onChange={handleChange}/>
                </label>
                <br/>
                <label>
                    RAM:
                    <input type="text" name="ram" value={formData.ram} onChange={handleChange}/>
                </label>
                <br/>
                <label>
                    Dysk:
                    <input type="text" name="disk" value={formData.disk} onChange={handleChange}/>
                </label>
                <br/>
                <label>
                    Wersja:
                    <input type="text" name="version" value={formData.version} onChange={handleChange}/>
                </label>
                <br/>
                <label>
                    Typ:
                    <select name="type" value={formData.type} onChange={handleChange}>
                        <option value="PAPER">PAPER</option>
                        <option value="INNY_TYP">Inna Typ</option>
                    </select>
                </label>
                <br/>
                <label>
                    Port:
                    <input type="number" name="port" value={formData.port} onChange={handleChange}/>
                </label>
                <br/>
                <button type="submit">Utwórz serwer</button>
            </form>
        </div>
    );
};

export default CreateServerPage;