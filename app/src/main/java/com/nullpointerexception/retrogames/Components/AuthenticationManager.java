package com.nullpointerexception.retrogames.Components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.R;

public class AuthenticationManager
{

    //Istanza di questa classe
    private static final AuthenticationManager ourInstance = new AuthenticationManager();
    //Utilizzato per accedere a questa classe
    public static AuthenticationManager get() { return ourInstance; }
    //Codice richiesta per la richiesta di accesso a Google
    private final int GOOGLE_SIGNIN_REQUEST = 10;
    private FirebaseAuth auth;  //Memorizza l'istanza dell'autent Firebase
    private Context context;    //Memorizza il contesto, utilizzato per alcune azioni di questa classe che lo richiedono
    private User currentUser;   //Utente loggato
    private LoginAttempt currentLoginAttempt; //Memorizza un'istanza di LoginAttempt quando è condivisa da più di un metodo

    /**
     * Costruttore privato per consentire una singola istanza di questa classe
     */
    private AuthenticationManager() {  }


    /**
     *  Inizializza i campi richiesti per usare questa classe
     * @param context contesto che verrà usato da questa classe
     * @return null se non ì èresente un utente loggato, altrimenti un istanza di loginAttempt
     */
    public LoginAttempt initialize(Activity context) {
        this.context = context;
        FirebaseApp.initializeApp(context);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null)
        {
            currentUser = new User(auth.getCurrentUser());

            //  Get user info
            SharedPreferences nicknameShared = context.getSharedPreferences(App.USER, Context.MODE_PRIVATE);
            currentUser.setNickname( nicknameShared.getString(App.NICKNAME, "-"));
        }
        else
            return null;

        currentLoginAttempt = new LoginAttempt();
        return currentLoginAttempt;
    }

    /**
     *      Prova ad accedere con un account google
     *
     *      @param email    email dell'utente
     *      @param password password dell'utente
     *
     *      @return Un istanza di loginAttempt
     */
    public LoginAttempt login(String email, String password) {
        final LoginAttempt loginAttempt = new LoginAttempt();

        //  Non consentire l'accesso se prima si è disconnessi con l'account corrente.
        if(currentUser != null)
            logout();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        //  Check if user exists and if it's verified
                        if(auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified())
                        {
                            //  Set current user
                            currentUser = new User(auth.getCurrentUser());

                            //  Call the callback method, if set, with positive result
                            if(loginAttempt.getOnLoginResultListener() != null)
                                loginAttempt.getOnLoginResultListener().onLoginResult(true);
                        }
                        else if(loginAttempt.getOnLoginResultListener() != null)
                            loginAttempt.getOnLoginResultListener().onLoginResult(false);
                    }
                    else {
                        // Call the callback method, if set, with negative result
                        if(loginAttempt.getOnLoginResultListener() != null)
                            loginAttempt.getOnLoginResultListener().onLoginResult(false);
                    }
                });

        return loginAttempt;
    }

    /**
     * Tenta di ottenere un UI di un account con le credenziali specificate.
     *
     * @param email    email dell'utente
     * @param password password dell'utente
     * @return Un'istanza di LoginAttempt con consente di aggiungere
     *  un metodo di callback per questo tentativo specificato.
     */
    public LoginAttempt getUIdOf(String email, String password) {
        final LoginAttempt loginAttempt = new LoginAttempt();

        // Non consentire l'accesso se prima si è disconnessi con l'account corrente.
        if(currentUser != null)
            logout();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        //  Check if user exists and if it's verified
                        if(auth.getCurrentUser() != null)
                        {
                            String uid = auth.getUid();

                            //  Call the callback method, if set, with positive result
                            if(loginAttempt.getOnUidListener() != null)
                                loginAttempt.getOnUidListener().onIdObtained(uid);
                        }
                        else
                            //  Call the callback method, if set, with negative result
                            if(loginAttempt.getOnUidListener() != null)
                                loginAttempt.getOnUidListener().onError();
                    }
                    else
                    {
                        //  Call the callback method, if set, with negative result
                        if(loginAttempt.getOnUidListener() != null)
                            loginAttempt.getOnUidListener().onError();
                    }

                });

        return loginAttempt;
    }

    /**
     *  Mostra il dialog di Google sign-in.

     * @param activity Activity che gestirà l'intent lanciato da questo metodo.
     * @return Un'istanza di LoginAttempt con consente di aggiungere un metodo di callback per questo dato tentativo.
     */
    public LoginAttempt requestLoginWithGoogle(Activity activity) {
        currentLoginAttempt = new LoginAttempt();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.server_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient;
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, GOOGLE_SIGNIN_REQUEST);

        return currentLoginAttempt;
    }

    /**
     * Gestire il risultato dell'intenzione avviata con il metodo requestLoginWithGoogle(...):
     *      Imposta l'utente corrente con i dati dell'account Google dell'utente ha appena effettuato l'accesso.
     *
     *      @param requestCode  Request code provided by onActivityResult(...)
     *      @param data         Intent provided by onActivityResult(...)
     */
    public void loginWithGoogle(int requestCode, Intent data) {
        //  Check if got a null intent
        if(data == null)
            return;

        if(requestCode == GOOGLE_SIGNIN_REQUEST)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)
                {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential)
                            .addOnCompleteListener((Activity) context, task1 -> {
                                if (task1.isSuccessful())
                                {
                                    if(auth.getCurrentUser() != null)
                                        currentUser = new User(auth.getCurrentUser());

                                    // Call the callback method, if set, with positive result
                                    if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(true);

                                    currentLoginAttempt = null;
                                }
                                else
                                {
                                    if (currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);

                                    currentLoginAttempt = null;
                                }
                            });
                }
                else
                {
                    if (currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                        currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);

                    currentLoginAttempt = null;
                }
            }
            catch (ApiException e)
            {
                Log.e("Error", e.toString());

                if(currentLoginAttempt != null && currentLoginAttempt.getOnLoginResultListener() != null)
                    currentLoginAttempt.getOnLoginResultListener().onLoginResult(false);

                currentLoginAttempt = null;
            }
        }
    }

    /**
     * Ritorna l'utente attualmente loggato
     * @return ritorna l'utente attualmente loggato
     */
    public User getUserLogged()
    {
        return currentUser;
    }

    /**
     *      @return vero se l'utente è loggato, altrimenti falso
     */
    public boolean isUserLogged() {
        if(auth == null)
            return false;

        return auth.getCurrentUser() != null;
    }

    /**
     * Permette di far uscire l'utente
     */
    public void logout() {
        auth.signOut();
        currentUser = null;
    }

    /**
     *  Crea un nuovo account con email e password su firebase
     *
     *  @param email email dell'utente
     *  @param password password dell'utente
     *  @param onCompleteListener gestisce le azioni differenti
     */
    public void createFirebaseUser(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, onCompleteListener);
    }

    /**
     *     Invia l'email di conferma
     *
     *     @param onCompleteListener gestisce le azioni different
     */
    public void sendVerificationEmail(OnCompleteListener<Void> onCompleteListener) {
        if (auth.getCurrentUser() != null)
            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(onCompleteListener);
    }

    /**
     *      LoginAttempt
     *
     *      Classe che consente di impostare i metodi di callback per un tentativo di accesso.
     */
    public static class LoginAttempt {

        //Archivia l'implementazione fornita di OnLoginResultListener
        private OnLoginResultListener onLoginResultListener;
        //Archivia l'implementazione fornita di OnUIDListener
        private OnUIDListener onUidListener;


        public interface OnLoginResultListener {
            /**
             *      Metodo di callback chiamato dopo un tentativo di accesso.
             *      @param result vero se l'utente è loggato, altrimenti falso
             */
            void onLoginResult(boolean result);
        }

        public interface OnUIDListener {
            /**
             *       Metodo di callback chiamato dopo un tentativo di accesso.
             *      @param uid ID utente fornito da FireBase
             */
            void onIdObtained(String uid);

            /**     Metodo di callback chiamato dopo un tentativo di accesso non riuscito. */
            void onError();
        }


        /**
         *      Aggiungere un'implementazione per il metodo chiamato dopo un risultato di accesso.
         *      @param onLoginResultListener implementazione del metodo
         * */
        public void addOnLoginResultListener(OnLoginResultListener onLoginResultListener) {
            this.onLoginResultListener = onLoginResultListener;
        }

        OnLoginResultListener getOnLoginResultListener() {
            return onLoginResultListener; }

        /**
         *      Aggiungere un'implementazione per il metodo chiamato dopo un risultato di accesso.
         *      @param onUidListener implementazione del metodo.
         * */
        public void addOnUidListener(OnUIDListener onUidListener) {
            this.onUidListener = onUidListener;
        }

        OnUIDListener getOnUidListener() { return onUidListener; }

    }
}
