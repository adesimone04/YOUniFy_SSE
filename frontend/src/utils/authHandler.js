import { toast } from 'react-toastify';

export const handleAuthResponse = (responseData, navigate, contextData = {}) => {
    // 1. NORMALIZZAZIONE CODICE
    // A volte il backend manda il codice in 'internalCode', a volte è il 'message' stesso (es. HOST_REGISTERED)
    // Se internalCode è null, usiamo message come codice.
    const internalCode = responseData.internalCode || responseData.message;
    const { data, message } = responseData;

    // 2. ESTRAZIONE DATI UTILI (ID, Scadenza per 2FA)
    const accountId =
        data?.accountID ||
        data?.accountId ||
        data?.ID ||
        data?.id ||
        responseData.accountID ||
        responseData.accountId;

    const expiryDate = data?.expiryDate || responseData.expiryDate;

    // 3. SWITCH CASE BASATO SULLA TUA LISTA
    switch (internalCode) {

        // ============================================================
        // SUCCESSO & NAVIGAZIONE
        // ============================================================

        // LOGIN / 2FA_CONFIRMED -> Vai alla Home (Thread)
        case 'LOGIN':
        case '2FA_CONFIRMED':
            // Opzionale: toast.success("Benvenuto!");
            navigate('/');
            return null;

        // LOGOUT -> Vai alla Landing
        case 'LOGOUT':
            navigate('/');
            return null;

        // REGISTRAZIONE OK -> Vai alla pagina 2FA
        case 'USER_REGISTERED':
        case 'HOST_REGISTERED':
        case '2FA_SENT': // Login che richiede 2FA

            navigate('/2fa', {
                state: {
                    email: contextData.email,   // Passato dalla pagina precedente
                    name: contextData.name,     // Passato dalla pagina precedente
                    expiryDate: expiryDate,
                    accountId: accountId
                }
            });
            return null;

        // ============================================================
        // REDIRECT AL LOGIN (Errori Sessione/Account)
        // ============================================================

        case 'ALREADY_REGISTERED':
            toast.warning("Utente già registrato. Effettua il login.");
            navigate('/login');
            return null;

        case 'USER_NOT_ABILITATED_FOR_TOKEN':
            toast.error("Utente non abilitato.");
            navigate('/login'); // O Landing
            return null;

        // Refresh Token Errors -> Logout forzato
        case 'WRONG_REFRESH_TOKEN':
        case 'TOKEN_NOT_FOUND':
        case 'TOKEN_REVOKED':
        case 'TOKEN_EXPIRED':
            toast.info("Sessione scaduta. Accedi di nuovo.");
            navigate('/login');
            return null;

        // ============================================================
        // REDIRECT ALLA REGISTRAZIONE (Casi Critici)
        // ============================================================

        case 'ACCOUNT_OR_2FA_NOT_FOUND':
        case 'ACCOUNT_NOT_FOUND_2FA': // Caso Generate2FAService
            toast.error("Account non trovato. Registrati per continuare.");
            navigate('/auth');
            return null;

        // ============================================================
        // ERRORI GESTITI "IN LOCO" (Restituiscono il codice)
        // ============================================================
        // Questi casi ritornano il codice così AuthPage o TwoFactorAuth
        // possono mostrare il Toast specifico o pulire i campi.

        // Login Page
        case 'BAD_CREDENTIALS':         // Login mostra scritta rossa
        case 'ACCOUNT_NOT_FOUND':       // Login mostra scritta rossa

        // 2FA Page (TwoFactorAuth.jsx gestisce la logica di questi codici)
        case 'MAX_ATTEMPTS':            // 2FA Page farà il resend automatico
        case 'TRY_2FA_AGAIN':           // 2FA Page pulisce input
        case '2FA_CODE_EXPIRED':        // 2FA Page farà resend
        case 'INVALID_2FA_CODE':        // 2FA Page mostra errore
        case 'MAX_ATTEMPTS_REACHED':    // Alias spesso usato

        // Auth Page (Registrazione)
        case 'PASSWORD_NOT_SAFE':
        case 'PEC_ADDRESS_NOT_VALID':
        case 'EMAIL_NOT_VALID':
        case 'PEC_ADDRESS_DOESNT_MATCH_VATCODE':
            return internalCode; // Restituisce la stringa, la pagina visualizzerà il Toast errore

        // ============================================================
        // DEFAULT
        // ============================================================
        default:
            console.warn("Codice non gestito:", internalCode);
            // Se è un errore generico (status 400/500), ritornalo comunque per mostrare un toast generico
            return internalCode;
    }
};