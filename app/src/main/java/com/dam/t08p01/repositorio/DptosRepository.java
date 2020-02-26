package com.dam.t08p01.repositorio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Departamento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DptosRepository {

    /* Singleton **********************************************************************************/

    private static DptosRepository mRepo = null;

    private MutableLiveData<List<Departamento>> mDptos;
    private MutableLiveData<Departamento> dptoSpinnerSeleccionado;
    private AppDatabase mAppDB;

    private DptosRepository(Application application) {
        mDptos = new MutableLiveData<>();
        dptoSpinnerSeleccionado = new MutableLiveData<>();
        mAppDB = AppDatabase.getAppDatabase(application);
    }

    public static DptosRepository getInstance(Application application) {
        if (mRepo == null) {
            mRepo = new DptosRepository(application);
        }
        return mRepo;
    }

    /* Métodos Lógica Dptos ***********************************************************************/

    public LiveData<List<Departamento>> recuperarDepartamentosSE() {
        mAppDB.getRefFB().child("Dptos").orderByKey().addListenerForSingleValueEvent(dptosSE_ValueEventListener);
        return mDptos;
    }

    private ValueEventListener dptosSE_ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Departamento> tDptos = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Departamento dpto = ds.getValue(Departamento.class);
                    tDptos.add(dpto);
                }
            }
            mDptos.setValue(tDptos);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            ;
        }
    };

    public LiveData<List<Departamento>> recuperarDepartamentosME() {
        mAppDB.getRefFB().child("Dptos").addValueEventListener(dptosME_ValueEventListener);
        return mDptos;
    }

    public void eliminarEventosGetDptosME() {
        mAppDB.getRefFB().child("Dptos").removeEventListener(dptosME_ValueEventListener);
    }

    private ValueEventListener dptosME_ValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<Departamento> tDptos = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Departamento dpto = ds.getValue(Departamento.class);
                    tDptos.add(dpto);
                }
            }
            mDptos.setValue(tDptos);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            ;
        }
    };

    private boolean existeDepartamento(Departamento dpto) {
        return false;
    }

    public boolean altaDepartamento(final Departamento dpto) {
        // Comprobamos previamente la existencia!!
        mAppDB.getRefFB().child("Dptos").child(String.valueOf(dpto.getId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Departamento tmpDpto = dataSnapshot.getValue(Departamento.class);
                if (tmpDpto == null) {
                    mAppDB.getRefFB().child("Dptos").child(String.valueOf(dpto.getId())).setValue(dpto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ;
            }
        });
        return true;
    }

    public boolean editarDepartamento(Departamento dpto) {
        mAppDB.getRefFB().child("Dptos").child(String.valueOf(dpto.getId())).setValue(dpto);
        return true;
    }

    public boolean bajaDepartamento(Departamento dpto) {
        mAppDB.getRefFB().child("Dptos").child(String.valueOf(dpto.getId())).removeValue();
        mAppDB.getRefFB().child("Incs").child(String.valueOf(dpto.getId())).removeValue();
        return true;
    }

    public LiveData<Departamento> recuperarNombreDepartamento(final int idDpto) {
        mAppDB.getRefFB().child("Dptos").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.getKey().equals(String.valueOf(idDpto))){
                            dptoSpinnerSeleccionado.setValue(ds.getValue(Departamento.class));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return dptoSpinnerSeleccionado;
    }
}
