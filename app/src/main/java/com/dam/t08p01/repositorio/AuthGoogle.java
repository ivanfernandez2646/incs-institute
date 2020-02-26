package com.dam.t08p01.repositorio;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthGoogle {

    private static AuthGoogle authGoogle;
    private FirebaseAuth mAuth;

    private Departamento mLogin;

    public interface AuthGoogleInterface {
        void onSuccessSignInGoogle(FirebaseUser user);

        void onFailureSignInGoogle();

        void onSuccessSignUpGoogle(FirebaseUser user);

        void onFailureSignUpGoogle();
    }

    private AuthGoogle() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static AuthGoogle getInstance() {
        if (authGoogle == null) {
            authGoogle = new AuthGoogle();
        }
        return authGoogle;
    }

    public void crearUsuarioGoogleAuth(final Context context, final Departamento dpto) {
        final AuthGoogleInterface mListener = (AuthGoogleInterface) context;
        mAuth.createUserWithEmailAndPassword(dpto.getNombre() + "@echirinos.com", dpto.getClave())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mListener.onSuccessSignUpGoogle(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(((Activity) context).findViewById(android.R.id.content), task.getException().toString(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUsuarioGoogleAuth(Context context, Departamento dpto, Departamento dptoAntiguo) {
        signOut();
        inicarSesionEditDeleteIncd(context, dpto, dptoAntiguo);
    }

    public void deleteUsuarioGoogleAuth(Context context, Departamento dpto){
        signOut();
        inicarSesionEditDeleteIncd(context, null, dpto);
    }

    private void inicarSesionEditDeleteIncd(final Context context, final Departamento dpto, final Departamento dptoAntiguo) {
        mAuth.signInWithEmailAndPassword(dptoAntiguo.getNombre() + "@echirinos.com", dptoAntiguo.getClave())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            //Si el departamento es distinto de nulo quiere decir que es un Update, en cambio si es nulo quiere decir que hay que hacer un Delete
                            if(dpto != null){
                                user.updateEmail(dpto.getNombre()+"@echirinos.com");
                                user.updatePassword(dpto.getClave());
                            }else{
                                user.delete();
                            }
                            signOut();

                            DptosViewModel dptosVM = new ViewModelProvider((ViewModelStoreOwner) context).get(DptosViewModel.class);
                            iniciarSesionUsuarioGoogleAuth(context, dptosVM.getLogin());
                        } else {
                            // Ignore, it's impossible this fail
                        }
                    }
                });
    }

    public void iniciarSesionUsuarioGoogleAuth(Context context, Departamento dpto) {
        final AuthGoogleInterface mListener = (AuthGoogleInterface) context;
        mAuth.signInWithEmailAndPassword(dpto.getNombre() + "@echirinos.com", dpto.getClave())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mListener.onSuccessSignInGoogle(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            mListener.onFailureSignInGoogle();
                        }
                    }
                });
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}
