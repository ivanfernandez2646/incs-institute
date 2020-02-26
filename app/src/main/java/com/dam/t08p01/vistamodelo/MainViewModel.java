package com.dam.t08p01.vistamodelo;

import androidx.lifecycle.ViewModel;

import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Departamento;

public class MainViewModel extends ViewModel {

    private Departamento mLogin;

    /* ViewModel Main *****************************************************************************/

    public MainViewModel() {
        super();
        mLogin = null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cerramos AppDatabase
        AppDatabase.cerrarAppDatabase();
    }

    /* Getters & Setters Objetos Persistentes *****************************************************/

    public Departamento getLogin() {
        return mLogin;
    }

    public void setLogin(Departamento login) {
        mLogin = login;
    }

}
