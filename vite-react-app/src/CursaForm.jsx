import React, { useState, useEffect } from 'react';

export default function CursaForm({ addFunc, updateFunc, selectedCursa }) {
    const [destinatie, setDestinatie] = useState('');
    const [data, setData] = useState('');

    useEffect(() => {
        if (selectedCursa) {
            setDestinatie(selectedCursa.destinatie);
            setData(selectedCursa.data);
        } else {
            setDestinatie('');
            setData('');
        }
    }, [selectedCursa]);

    function handleSubmit(event) {
        event.preventDefault();
        const cursa = { destinatie, data };
        if (selectedCursa) {
            updateFunc({ ...cursa, id: selectedCursa.id }); // Pass the id of the selected cursa for update
        } else {
            addFunc(cursa);
        }
        setDestinatie('');
        setData('');
    }

    return (
        <form onSubmit={handleSubmit}>
            <label>
                Destinatie:
                <input type="text" value={destinatie} onChange={e => setDestinatie(e.target.value)} />
            </label><br />
            <label>
                Data:
                <input type="datetime-local" value={data} onChange={e => setData(e.target.value)} />
            </label><br />
            <input type="submit" value={selectedCursa ? "Update cursa" : "Add cursa"} />
        </form>
    );
}
