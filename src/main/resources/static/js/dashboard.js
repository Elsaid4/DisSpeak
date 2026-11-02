// Porta la lista dei messaggi in fondo, così si vede l'ultimo messaggio.
// Quando collegherete il WebSocket, richiamate la stessa funzione a ogni messaggio ricevuto.
function scrollToLatest() {
    const list = document.getElementById("messageList");
    if (list) {
        list.scrollTop = list.scrollHeight;
    }
}

document.addEventListener("DOMContentLoaded", scrollToLatest);
