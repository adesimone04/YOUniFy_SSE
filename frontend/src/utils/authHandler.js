import { toast } from 'react-toastify';

export const handleAuthResponse = (responseData, navigate, contextData = {}) => {

    const internalCode = responseData.internalCode || responseData.message;
    const { data, message } = responseData;

    // 2. ESTRAZIONE DATI UTILI (ID, Scadenza per 2FA)
    const accountId =
        data?.accountID || data?.accountId ||
        data?.ID || data?.id ||
        responseData.accountID || responseData.accountId;

    const expiryDate = data?.expiryDate || responseData.expiryDate;

    switch (internalCode) {

        // ============================================================
        // SUCCESSO & NAVIGAZIONE
        // ============================================================

        // LOGIN / 2FA_CONFIRMED -> Vai alla Home (Thread)
        case 'LOGIN':

            if (accountId && accountId.toUpperCase().startsWith('H')) {

                localStorage.setItem('isHost', 'true');
            }
            else {
                localStorage.setItem('isHost', 'false');
            }

            localStorage.setItem('user_is_logged_in', 'true');
            navigate('/thread');
            return null;

        case '2FA_CONFIRMED':
            navigate('/login');
            return null;

        // LOGOUT -> Vai alla Landing
        case 'LOGOUT':
            localStorage.clear();
            navigate('/');
            return null;

        // REGISTRAZIONE OK -> Vai alla pagina 2FA
        case 'USER_REGISTERED':
        case 'HOST_REGISTERED':
        case '2FA_SENT': // Login che richiede 2FA

            navigate('/2fa', {
                state: {
                    email: contextData.email,
                    name: contextData.name,
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
            toast.error("Utente non valido");
            navigate('/login');
            return null;

        // Refresh Token Errors -> Logout forzato
        case 'WRONG_REFRESH_TOKEN':
        case 'TOKEN_NOT_FOUND':
        case 'TOKEN_REVOKED':
        case 'TOKEN_EXPIRED':
            toast.info("Sessione scaduta. Accedi di nuovo.");
            localStorage.clear();
            navigate('/login');
            return null;

        // ============================================================
        // REDIRECT ALLA REGISTRAZIONE (Casi Critici)
        // ============================================================

        case 'ACCOUNT_OR_2FA_NOT_FOUND':
            toast.error("Account non trovato. Registrati per continuare.");
            navigate('/auth');
            return null;

        // ============================================================
        // ERRORI GESTITI "IN LOCO" (Restituiscono il codice)
        // ============================================================
        // Questi casi ritornano il codice così AuthPage o TwoFactorAuth
        // possono mostrare il Toast specifico o pulire i campi.

        // Login Page
        case 'BAD_CREDENTIALS':
        case 'ACCOUNT_NOT_FOUND':

        // 2FA Page
        case 'MAX_ATTEMPTS':            // 2FA Page farà il resend automatico
        case 'TRY_2FA_AGAIN':           // 2FA Page pulisce input
        case '2FA_CODE_EXPIRED':        // 2FA Page farà resend
        case 'INVALID_2FA_CODE':        // 2FA Page mostra errore

        // Auth Page (SignUp)
        case 'PASSWORD_NOT_SAFE':
        case 'PEC_ADDRESS_NOT_VALID':
        case 'EMAIL_NOT_VALID':
        case 'PEC_ADDRESS_DOESNT_MATCH_VATCODE':
        case 'OPEN_API_ERROR':
            return internalCode;

        case 'DATA_NOT_VALID':
            toast.error("Alcuni dati inseriti non sono validi.");
            return null;

        default:
            console.warn("Codice non gestito:", internalCode);
            return internalCode;
    }
};