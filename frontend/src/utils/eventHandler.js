import { toast } from 'react-toastify';

export const handleEventResponse = (responseData, navigate, contextData = {}) => {

    const internalCode = responseData.internalCode || responseData.message;
    const { data, message } = responseData;

    switch(internalCode){

        case 'POSTED_SUCCESSFULLY':
            toast.success("Evento postato con successo!")
            return null;

        case 'DELETED_SUCCESSFULLY':
            toast.success("Evento eliminato con successo!")
            return null;

        case 'EDITED_SUCCESSFULLY':
            toast.success("Evento modificato con successo!")
            return null;

        case 'JOINED_SUCCESSFULLY':
            toast.success("Ora partecipi all'evento")
            return null;

        case 'CANT_JOIN_EVENT':
            toast.error("Non puoi unirti ad un evento già iniziato")
            return null;

        case 'UNJOINED_SUCCESSFULLY':
            toast.success("Non stai più partecipando all'evento")
            return null;

        case 'VIEW_SUCCESSFULLY':
            return null;

        case 'RETRIEVE_SUCCESSFULLY':
            return null;

        case 'EVENT_NOT_FOUND':
            toast.error("Evento non trovato")
            return null;

        case 'ALREADY_JOINED':
            toast.error("Stai già partecipando all'evento")
            return null;

        case 'CITY_NOT_VALID':
            toast.error("Città inserita non valida")
            return null;

        case 'EVENT_ALREADY_EXISTS':
            toast.error("L'evento è gia presente sulla piattaforma")
            return null;

        case 'CANT_UNJOIN_EVENT':
            toast.error("Impossibile rimuovere iscrizione all'evento")
            return null;

        case 'ALREADY_NOT_JOINED':
            toast.error("Non sei nella lista dei partecipanti")
            return null;

        case 'USER_NOT_FOUND':
            return null;

        case 'HOST_NOT_FOUND':
            return null;

        case 'ACCOUNT_NOT_FOUND':
            return null;

        case 'DB_CONSTRAINS_VIOLATED':
            return null;

        default:
            console.warn("Codice non gestito:", internalCode);
            return internalCode;
    }
};