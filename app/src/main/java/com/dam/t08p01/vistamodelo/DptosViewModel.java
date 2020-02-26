package com.dam.t08p01.vistamodelo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.repositorio.DptosRepository;

import java.util.List;

public class DptosViewModel extends AndroidViewModel {

    /* ViewModel Dptos ****************************************************************************/

    private DptosRepository mDptosRep;
    private LiveData<List<Departamento>> mDptos;
    private int idDptoSpinner;

    private Departamento mLogin;
    private Departamento mDptoAEliminar;

    public DptosViewModel(@NonNull Application application) {
        super(application);
        mDptosRep = DptosRepository.getInstance(application);
        mLogin = null;
        mDptoAEliminar = null;
    }

    /* Métodos Mantenimiento Departamentos ********************************************************/

    public LiveData<List<Departamento>> getDptosME() {      // Multiple Events
        mDptos = mDptosRep.recuperarDepartamentosME();
        return mDptos;
    }

    public LiveData<List<Departamento>> getDptosSE() {      // Single Event
        mDptos = mDptosRep.recuperarDepartamentosSE();
        return mDptos;
    }

    public void eliminarEventosGetDptosME() {
        mDptosRep.eliminarEventosGetDptosME();
    }

    public boolean altaDepartamento(Departamento dpto) {
        return mDptosRep.altaDepartamento(dpto);
    }

    public boolean editarDepartamento(Departamento dpto) {
        return mDptosRep.editarDepartamento(dpto);
    }

    public boolean bajaDepartamento(Departamento dpto) {
        return mDptosRep.bajaDepartamento(dpto);
    }

    //Utilizado para el RecyclerView de BuscadorIncds
    public LiveData<Departamento> recuperarNombreDepartamento(){
        return mDptosRep.recuperarNombreDepartamento(idDptoSpinner);
    }

    /* Getters & Setters Objetos Persistentes *****************************************************/

    public Departamento getLogin() {
        return mLogin;
    }

    public void setLogin(Departamento login) {
        mLogin = login;
    }

    public Departamento getDptoAEliminar() {
        return mDptoAEliminar;
    }

    public void setDptoAEliminar(Departamento dptoAEliminar) {
        mDptoAEliminar = dptoAEliminar;
    }

    public int getIdDptoSpinner() {
        return idDptoSpinner;
    }

    public void setIdDptoSpinner(int idDptoSpinner) {
        this.idDptoSpinner = idDptoSpinner;
    }
}
