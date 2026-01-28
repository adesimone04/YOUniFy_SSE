import { toast } from 'react-toastify';

export const handleMatchResponse = (responseData, navigate, contextData = {}) => {

    const internalCode = responseData.internalCode || responseData.message;
    const { data, message } = responseData;

    switch(internalCode) {

        case 'MATCH_SENT_SUCCESSFULLY':
            toast.success("Match inviato con successo!");
            return null;

        case 'MATCH_REACTED_SUCCESSFULLY':
            toast.success("Risposta inviata con successo!");
            return null;

        case 'MATCH_DELETED_SUCCESSFULLY':
            toast.success("Match eliminato con successo!");
            return null;

        case 'VIEW_SUCCESSFULLY':
            return null;

        case 'RETRIEVED_SUCCESSFULLY':
            return null;

        case 'HOST_NOT_FOUND':
            toast.warn("La notifica di matching non è stata inviata");
            return null;

        case 'ACCOUNT_NOT_FOUND':
            toast.warn("La notifica di matching non è stata inviata");
            return null;

        case 'USER_NOT_FOUND':
            toast.warn("La notifica di matching non è stata inviata");
            return null;

        case 'MATCH_NOT_FOUND':
            toast.error("Match non trovato");
            return null;

        case 'USER_NOT_FOUND_FOR_MATCH':
            toast.error("Utente non trovato per il matching");
            return null;

        case 'WRONG_SENDER':
            toast.error("Non hai i permessi per questa operazione");
            return null;

        case 'UNIVERSITY_NOT_VALID':
            toast.error("Università non valida");
            return null;

        case 'UNIVERSITY_OR_CDL_NOT_VALID':
            toast.error("Università o corso di laurea non validi");
            return null;

        case 'DB_CONSTRAINS_VIOLATED':
            toast.error("Operazione non valida");
            return null;


        default:
            console.warn("Codice non gestito:", internalCode);
            return internalCode;
    }
};
