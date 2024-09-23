import React from 'react';
import './App.css';

function CursaRow({ cursa, deleteFunc, onSelect }) {
    function handleDelete() {
        console.log('Delete button for ' + cursa.id);
        deleteFunc(cursa.id);
    }

    function handleSelect() {
        console.log('Selecting cursa:', cursa);
        onSelect(cursa); // Transmit the selected cursa to the parent component
    }

    return (
        <tr>
            <td>{cursa.id}</td>
            <td>{cursa.destinatie}</td>
            <td>{cursa.data}</td>
            <td>
                <button onClick={handleDelete}>Delete</button>
                <button onClick={handleSelect}>Select</button> {/* Add a select button */}
            </td>
        </tr>
    );
}

export default function CursaTable({ cursuri, deleteFunc, onSelect }) {
    console.log("In CursaTable");
    console.log(cursuri);
    let rows = [];
    cursuri.forEach(function (cursa) {
        rows.push(<CursaRow cursa={cursa} key={cursa.id} deleteFunc={deleteFunc} onSelect={onSelect} />);
    });
    return (
        <div className="CursaTable">
            <table className="center">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Destinatie</th>
                    <th>Data</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>{rows}</tbody>
            </table>
        </div>
    );
}
