package com.dam.t08p01.vista;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.repositorio.AuthGoogle;
import com.dam.t08p01.vista.dialogos.DlgConfirmacion;
import com.dam.t08p01.vista.fragmentos.BusDptosFragment;
import com.dam.t08p01.vista.fragmentos.MtoDptosFragment;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class DptosActivity extends AppCompatActivity
        implements BusDptosFragment.BusDptosFragInterface,
        MtoDptosFragment.MtoDptosFragInterface,
        DlgConfirmacion.DlgConfirmacionListener,
        AuthGoogle.AuthGoogleInterface {

    private NavController mNavC;

    private DptosViewModel mDptosVM;
    private Departamento mLogin;

    private boolean editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dptos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // FindViewByIds

        // Inits
        mNavC = Navigation.findNavController(this, R.id.navhostfrag_dptos);
        mDptosVM = new ViewModelProvider(this).get(DptosViewModel.class);

        // Recuperamos el dpto login
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            if (b != null) {
                mLogin = b.getParcelable("login");
                mDptosVM.setLogin(mLogin);      // Guardamos el login en el ViewModel
            }
        }
        if (mLogin == null) {
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_NoLogin, Snackbar.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Listeners

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCrearBusDptosFrag() {
        // Lanzamos MtoDptosFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoDptosFragment.OP_CREAR);
        mNavC.navigate(R.id.action_busDptosFragment_to_mtoDptosFragment, bundle);
    }

    @Override
    public void onEditarBusDptosFrag(Departamento dpto) {
        // Lanzamos MtoDptosFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoDptosFragment.OP_EDITAR);
        bundle.putParcelable("dpto", dpto);
        mNavC.navigate(R.id.action_busDptosFragment_to_mtoDptosFragment, bundle);
    }

    @Override
    public void onEliminarBusDptosFrag(Departamento dpto) {
        if (dpto.getId() == 0) return;  // no se puede borrar el admin!!
        // Guardamos el dpto a eliminar en el ViewModel
        mDptosVM.setDptoAEliminar(dpto);
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_Eliminar);
        mNavC.navigate(R.id.action_global_dlgConfirmacionDptos, bundle);
    }

    @Override
    public void onCancelarMtoDptosFrag() {
        // Cerramos MtoDptosFragment
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarMtoDptosFrag(int op, Departamento dpto, Departamento dptoAntiguo) {
        switch (op) {
            case MtoDptosFragment.OP_CREAR:
                if (mDptosVM.altaDepartamento(dpto)) {
                    AuthGoogle.getInstance().crearUsuarioGoogleAuth(this, dpto);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaDepartamentoKO, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case MtoDptosFragment.OP_EDITAR:
                editar = true;
                if (mDptosVM.editarDepartamento(dpto)) {
                    AuthGoogle.getInstance().updateUsuarioGoogleAuth(this, dpto, dptoAntiguo);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.msg_EditarDepartamentoKO, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case MtoDptosFragment.OP_ELIMINAR:
                break;
        }

        // Cerramos MtoDptosFragment
        mNavC.navigateUp();
    }

    @Override
    public void onDlgConfirmacionPositiveClick(DialogFragment dialog) {
        // Recuperamos el dpto a eliminar del ViewModel
        editar = false;
        Departamento dptoAEliminar = mDptosVM.getDptoAEliminar();
        if (mDptosVM.bajaDepartamento(dptoAEliminar)) {
            AuthGoogle.getInstance().deleteUsuarioGoogleAuth(this, dptoAEliminar);
        }
    }

    @Override
    public void onDlgConfirmacionNegativeClick(DialogFragment dialog) {
        // Eliminamos el dpto a eliminar del ViewModel
        mDptosVM.setDptoAEliminar(null);
    }

    //Relacionado con la Autenticación

    //Este se llama al editar un Dpto, si todo va bien se vuelve a iniciar sesión con el Admin lo que indicaría que la edición es correcta
    @Override
    public void onSuccessSignInGoogle(FirebaseUser user) {
        if(editar){
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_EditarDepartamentoOK, Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_BajaDepartamentoOK, Snackbar.LENGTH_SHORT).show();

            // Eliminamos el dpto a eliminar del ViewModel
            mDptosVM.setDptoAEliminar(null);
        }
    }

    @Override
    public void onFailureSignInGoogle() {
        if(editar){
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_EditarDepartamentoKO, Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_BajaDepartamentoKO, Snackbar.LENGTH_SHORT).show();
        }
    }

    //Este es llamado al crear un Dpto
    @Override
    public void onSuccessSignUpGoogle(FirebaseUser user) {
        Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaDepartamentoOK, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFailureSignUpGoogle() {
        Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaDepartamentoOK, Snackbar.LENGTH_SHORT).show();
    }
}
