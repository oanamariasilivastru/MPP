
import {CURSE_BASE_URL} from './consts';

function status(response) {
    console.log('response status '+response.status);
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
}

function json(response) {
    return response.json()
}

export function GetCursa(){
    let headers = new Headers();
    headers.append('Accept', 'application/json');
    let myInit = { method: 'GET',
        headers: headers,
        mode: 'cors'
    };
    let request = new Request(CURSE_BASE_URL, myInit);

    console.log('Inainte de fetch GET pentru '+CURSE_BASE_URL)

    return fetch(request)
        .then(status)
        .then(json)
        .then(data=> {
            console.log('Request succeeded with JSON response', data);
            return data;
        }).catch(error=>{
            console.log('Request failed', error);
            return Promise.reject(error);
        });

}

export function DeleteCursa(username){
    console.log('inainte de fetch delete')
    let myHeaders = new Headers();
    myHeaders.append("Accept", "application/json");

    let antet = { method: 'DELETE',
        headers: myHeaders,
        mode: 'cors'
    };

    const userDelUrl=CURSE_BASE_URL+'/'+username;
    console.log('URL pentru delete   '+userDelUrl)
    return fetch(userDelUrl,antet)
        .then(status)
        .then(response=>{
            console.log('Delete status '+response.status);
            return response.text();
        }).catch(e=>{
            console.log('error '+e);
            return Promise.reject(e);
        });

}


export function AddCursa(cursa) {
    console.log('inainte de fetch post', JSON.stringify(cursa));

    let myHeaders = new Headers();
    myHeaders.append("Accept", "application/json");
    myHeaders.append("Content-Type", "application/json");

    let antet = {
        method: 'POST',
        headers: myHeaders,
        mode: 'cors',
        body: JSON.stringify(cursa)
    };

    return fetch(CURSE_BASE_URL, antet)
        .then(response => {
            console.log('Response status:', response.status);
            if (response.status >= 200 && response.status < 300) {
                return response.json();
            } else {
                return response.text().then(text => {
                    console.error('Error response:', text);
                    throw new Error(response.statusText || response.status);
                });
            }
        })
        .catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}

export function UpdateCursa(cursa) {
    console.log('inainte de fetch put pentru actualizare', JSON.stringify(cursa));

    let myHeaders = new Headers();
    myHeaders.append("Accept", "application/json");
    myHeaders.append("Content-Type", "application/json");

    let antet = {
        method: 'PUT', // sau 'PATCH', în funcție de cerințele API-ului
        headers: myHeaders,
        mode: 'cors',
        body: JSON.stringify(cursa)
    };

    const updateUrl = CURSE_BASE_URL + '/' + cursa.id; // presupunând că este necesară ID-ul cursului pentru actualizare
    console.log('URL pentru actualizare:', updateUrl);

    return fetch(updateUrl, antet)
        .then(response => {
            console.log('Response status:', response.status);
            if (response.ok) { // Verificăm dacă răspunsul este OK
                // Verificăm dacă răspunsul este gol sau nu
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    return response.json(); // Returnăm datele JSON dacă există
                } else {
                    return null; // Returnăm null dacă nu există date
                }
            } else {
                return response.text().then(text => {
                    console.error('Error response:', text);
                    throw new Error(response.statusText || response.status);
                });
            }
        })
        .catch(error => {
            console.log('Request failed', error);
            return Promise.reject(error);
        });
}



