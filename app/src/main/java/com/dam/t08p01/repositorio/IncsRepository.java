package com.dam.t08p01.repositorio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.FiltroIncs;
import com.dam.t08p01.modelo.Incidencia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IncsRepository {

    /* Singleton **********************************************************************************/

    private static IncsRepository mRepo = null;

    private MutableLiveData<List<Incidencia>> mIncs;
    private AppDatabase mAppDB;
    private FiltroIncs filtroIncs;

    private IncsRepository(Application application) {
        mIncs = new MutableLiveData<>();
        mAppDB = AppDatabase.getAppDatabase(application);
    }

    public static IncsRepository getInstance(Application application) {
        if (mRepo == null) {
            mRepo = new IncsRepository(application);
        }
        return mRepo;
    }

    /* Métodos Lógica Incs ***********************************************************************/

    public LiveData<List<Incidencia>> recuperarIncidenciasSE(FiltroIncs filtroIncs) {
        this.filtroIncs = filtroIncs;
        mAppDB.getRefFB().child("Incs").child(filtroIncs.getIdDptoFiltro()).orderByKey().startAt(filtroIncs.getFechaIncidenciaFiltroNotFormatted()).addListenerForSingleValueEvent(incs_ValueEventListener);
        return mIncs;
    }

    public LiveData<List<Incidencia>> recuperarIncidenciasME(FiltroIncs filtroIncs) {
        this.filtroIncs = filtroIncs;
        mAppDB.getRefFB().child("Incs").child(filtroIncs.getIdDptoFiltro()).orderByKey().startAt(filtroIncs.getFechaIncidenciaFiltroNotFormatted()).addValueEventListener(incs_ValueEventListener);
        return mIncs;
    }

    private ValueEventListener incs_ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Incidencia> tIncs = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot childChild : child.getChildren()) {
                        Incidencia inc = childChild.getValue(Incidencia.class);
                        if (filtroIncs.getEstadoIncidenciaFiltro() != null) {
                            if (filtroIncs.getEstadoIncidenciaFiltro() == Incidencia.ESTADO.RESUELTA) {
                                if (inc.isEstado()) {
                                    tIncs.add(inc);
                                }
                            } else if (filtroIncs.getEstadoIncidenciaFiltro() == Incidencia.ESTADO.NO_RESUELTA) {
                                if (!inc.isEstado()) {
                                    tIncs.add(inc);
                                }
                            }
                        } else {
                            tIncs.add(inc);
                        }
                    }
                }
            }
            mIncs.setValue(tIncs);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            ;
        }
    };

    public void eliminarEventosGetIncsME() {
        mAppDB.getRefFB().child("Incs").removeEventListener(incs_ValueEventListener);
    }

    public boolean altaIncidencia(final Incidencia incd) {
        // Comprobamos previamente la existencia!!
        mAppDB.getRefFB()
                .child("Incs")
                .child(String.valueOf(incd.getIdDpto()))
                .child(incd.getFecha().replaceAll("/", ""))
                .child(incd.getId().substring(4)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Incidencia tmpInc = dataSnapshot.getValue(Incidencia.class);
                if (tmpInc == null) {
                    mAppDB.getRefFB().child("Incs")
                            .child(String.valueOf(incd.getIdDpto()))
                            .child(incd.getFechaFormatFirebase())
                            .child(incd.getId().substring(4)).setValue(incd);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ;
            }
        });
        return true;
    }

    public boolean editarIncidencia(Incidencia incd) {
        mAppDB.getRefFB().child("Incs")
                .child(String.valueOf(incd.getIdDpto()))
                .child(incd.getFechaFormatFirebase())
                .child(incd.getId().substring(4)).setValue(incd);
        return true;
    }

    public boolean bajaIncidencia(Incidencia incd) {
        mAppDB.getRefFB().child("Incs")
                .child(String.valueOf(incd.getIdDpto()))
                .child(incd.getFechaFormatFirebase())
                .child(incd.getId().substring(4)).removeValue();
        mAppDB.getRefSG().child("Incs")
                .child(String.valueOf(incd.getIdDpto()))
                .child(incd.getFechaFormatFirebase())
                .child(incd.getId().substring(4)).delete();
        return true;
    }
}
