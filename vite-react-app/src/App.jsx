import React, { useEffect, useState } from 'react';
import CursaTable from './CursaTable.jsx';
import './App.css';
import CursaForm from "./CursaForm.jsx";
import { GetCursa, DeleteCursa, AddCursa, UpdateCursa } from './utils/rest-calls';

export default function CursaApp() {
    const [curse, setCurse] = useState([]); // Initialize with an empty array
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [selectedCursa, setSelectedCursa] = useState(null);

    function addFunc(cursa) {
        console.log('Inside addFunc:', cursa);
        AddCursa(cursa)
            .then(() => {
                setSuccessMessage('Cursa adăugată cu succes!');
                return GetCursa();
            })
            .then(curse => setCurse(curse))
            .catch(error => {
                console.log('Eroare la adăugare:', error);
                setError('Eroare la adăugare.');
            });
    }

    function updateFunc(cursa) {
        console.log('Inside updateFunc:', cursa);
        UpdateCursa(cursa)
            .then(() => {
                setSuccessMessage('Cursa actualizată cu succes!');
                return GetCursa();
            })
            .then(curse => setCurse(curse))
            .catch(error => {
                console.log('Eroare la actualizare:', error);
                setError('Eroare la actualizare.');
            });
    }

    function deleteFunc(cursaId) {
        console.log('Inside deleteFunc:', cursaId);
        DeleteCursa(cursaId)
            .then(() => {
                setSuccessMessage('Cursa ștearsă cu succes!');
                return GetCursa();
            })
            .then(curse => setCurse(curse))
            .catch(error => {
                console.log('Eroare la ștergere:', error);
                setError('Eroare la ștergere.');
            });
    }

    useEffect(() => {
        console.log('Inside useEffect');
        GetCursa()
            .then(curse => setCurse(curse))
            .catch(error => {
                console.log('Eroare la preluare curse:', error);
                setError('Eroare la preluare curse.');
            });
    }, []);

    return (
        <div className="CursaApp">
            <h1>New Cursa Management App</h1>
            {error && <p className="error">{error}</p>}
            {successMessage && <p className="success">{successMessage}</p>}
            <CursaForm addFunc={addFunc} updateFunc={updateFunc} selectedCursa={selectedCursa} />
            <br />
            <br />
            <CursaTable cursuri={curse} deleteFunc={deleteFunc} onSelect={setSelectedCursa} />
        </div>
    );
}
