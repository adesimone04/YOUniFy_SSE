import { toast } from 'react-toastify';

export const handleProfileResponse = (responseData, navigate, contextData = {}) => {
    const internalCode = responseData.internalCode || responseData.message;
    const { data, message } = responseData;

    switch(internalCode) {

        // ========================================
        // SUCCESS CASES
        // ========================================

        // Visualizzazione profilo (silenzioso)
        case 'VIEW_SUCCESSFULLY':
            return null;

        // Modifica profilo riuscita
        case 'EDIT_SUCCESSFULLY':
            toast.success('Profilo modificato con successo!');
            return null;

        // Cambio password/università riuscito
        case 'CHANGE_SUCCESSFULLY':
            toast.success('Modifica effettuata con successo!');
            return null;

        // 2FA inviato per cambio università
        case '2FA_CHANGE_SENT':

            const accountId = data?.ID;
            const email = data?.accountEmail;
            const name = data?.accountName;
            const expiryDate = data?.expiryDate;
            const university = data?.university;

            if (accountId) {
                localStorage.setItem('2fa_accountId', accountId);
            }

            navigate('/2fa', {
                state: {
                    email: email,
                    accountId: accountId,
                    name: name,
                    expiryDate: expiryDate,
                    university: university
                }
            });
            return null;


        // 2FA confermato con successo
        case '2FA_CHANGE_CONFIRMED':
            toast.success('Università cambiata con successo! Benvenuto nella tua nuova sede universitaria!');
            navigate('/profile');
            return null;

        // ========================================
        // ERROR CASES - VALIDAZIONE DATI
        // ========================================

        // Dati generici non validi
        case 'DATA_NOT_VALID':
            toast.error('Alcuni dati inseriti non sono validi. Controlla i campi e riprova.');
            return null;

        // ========================================
        // ERROR CASES - 2FA
        // ========================================

        // Codice 2FA scaduto
        case '2FA_CODE_EXPIRED':
            toast.error('Il codice 2FA è scaduto. Richiedi un nuovo codice.');
            return null;

        // Codice 2FA errato (con tentativi rimanenti)
        case 'TRY_2FA_AGAIN':
            const attempts = message?.match(/\d+/)?.[0] || '?';
            toast.warning(`Codice errato. ${attempts} tentativi rimasti.`);
            return null;

        // Massimo numero di tentativi 2FA raggiunto
        case 'MAX_ATTEMPTS':
            toast.error('Hai raggiunto il numero massimo di tentativi (3). Richiedi un nuovo codice.');
            return null;

        // 2FA o account non trovato
        case 'ACCOUNT_OR_2FA_NOT_FOUND':
            toast.error('Account o codice 2FA non trovato. Verifica i dati inseriti.');
            return null;

        // ========================================
        // ERROR CASES - PASSWORD
        // ========================================

        // Password uguale a quella corrente
        case 'PASS_NOT_CHANGED':
            toast.error('La nuova password è uguale a quella attuale. Inseriscine una diversa.');
            return null;

        // ========================================
        // ERROR CASES - ACCOUNT
        // ========================================

        // Account non trovato
        case 'ACCOUNT_NOT_FOUND':
            toast.error('Account non trovato. Verifica le credenziali.');
            return null;

        // Utente non trovato
        case 'USER_NOT_FOUND':
            toast.error('Utente non trovato.');
            return null;

        // Host non trovato
        case 'HOST_NOT_FOUND':
            toast.error('Attività non trovata.');
            return null;

        // ========================================
        // ERROR CASES - DATABASE
        // ========================================

        // Violazione vincoli database
        case 'DB_CONSTRAINS_VIOLATED':
            toast.error('Operazione non valida. Alcuni dati violano i vincoli del sistema.');
            return null;

        // ========================================
        // DEFAULT
        // ========================================

        default:
            console.warn('Codice non gestito in profileHandler:', internalCode);
            if (message) {
                toast.info(message);
            }
            return internalCode;
    }
};
