package com.dam.t08p01.vistamodelo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.FiltroIncs;
import com.dam.t08p01.modelo.Incidencia;
import com.dam.t08p01.repositorio.IncsRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncsViewModel extends AndroidViewModel {

    private LiveData<List<Incidencia>> mIncs;
    private IncsRepository mIncsRep;

    private Departamento mLogin;
    private Incidencia incidenciaAEliminar;
    private MutableLiveData<FiltroIncs> filtroIncs;
    private MutableLiveData<String> latitudLongitud;
    private MutableLiveData<Bitmap> imgIncd;

    public IncsViewModel(@NonNull Application application) {
        super(application);
        mIncsRep = IncsRepository.getInstance(application);
        filtroIncs = new MutableLiveData<>();
        latitudLongitud = new MutableLiveData<>();
        imgIncd = new MutableLiveData<>();
    }

    public LiveData<List<Incidencia>> getIncsME() {      // Multiple Events
        mIncs = mIncsRep.recuperarIncidenciasME(filtroIncs.getValue());
        return mIncs;
    }

    public LiveData<List<Incidencia>> getIncsSE() {      // Single Event
        mIncs = mIncsRep.recuperarIncidenciasSE(filtroIncs.getValue());
        return mIncs;
    }

    public void eliminarEventosGetIncsME() {
        mIncsRep.eliminarEventosGetIncsME();
    }

    public boolean altaIncidencia(Incidencia incd) {
        return mIncsRep.altaIncidencia(incd);
    }

    public boolean editarIncidencia(Incidencia incd) {
        return mIncsRep.editarIncidencia(incd);
    }

    public boolean bajaIncidencia(Incidencia incd) {
        return mIncsRep.bajaIncidencia(incd);
    }


    //Atributos para manejo de interfaz
    public void setLogin(Departamento mLogin) {
        this.mLogin = mLogin;
    }

    public Departamento getLogin() {
        return mLogin;
    }

    public Incidencia getIncidenciaAEliminar() {
        return incidenciaAEliminar;
    }

    public void setIncidenciaAEliminar(Incidencia incidenciaAEliminar) {
        this.incidenciaAEliminar = incidenciaAEliminar;
    }

    public MutableLiveData<FiltroIncs> getFiltroIncs() {
        return filtroIncs;
    }

    public MutableLiveData<String> getLatitudLongitud() {
        return latitudLongitud;
    }

    public MutableLiveData<Bitmap> getImgIncd() {
        return imgIncd;
    }
}
