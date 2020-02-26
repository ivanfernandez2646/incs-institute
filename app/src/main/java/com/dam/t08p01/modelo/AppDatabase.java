package com.dam.t08p01.modelo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.dam.t08p01.repositorio.AuthGoogle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AppDatabase {

    private static volatile AppDatabase db = null;  // Singleton
    private static FirebaseDatabase dbFB = null;
    private static DatabaseReference refFB = null;
    private static FirebaseStorage fbSG = null;
    private static StorageReference refSG = null;

    private AppDatabase() {
        // Patrón Singleton
    }

    public DatabaseReference getRefFB() {
        return refFB;
    }

    public StorageReference getRefSG() {
        return refSG;
    }

    public static AppDatabase getAppDatabase(Context context) {
        if (db == null) {
            synchronized (AppDatabase.class) {
                if (db == null) {
                    db = new AppDatabase();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                    String nombreBD = pref.getString(context.getResources().getString(R.string.Firebase_name_key), "");
                    if (!nombreBD.equals("")) {
                        dbFB = FirebaseDatabase.getInstance();
                        refFB = dbFB.getReference().child(nombreBD);
                        fbSG = FirebaseStorage.getInstance();
                        refSG = fbSG.getReference().child(nombreBD);

                        // Creamos Dpto 0 admin (si no existe ya)
                        Departamento dpto = new Departamento();
                        dpto.setId(0);
                        dpto.setNombre("admin");
                        dpto.setClave("aaaaaa");
                        refFB.child("Dptos").child(String.valueOf(dpto.getId())).setValue(dpto);
                    }
                }
            }
        }
        return db;
    }

    public static boolean cerrarAppDatabase() {
        if (db != null) {
            dbFB = null;
            fbSG = null;
            db = null;
            return true;
        }
        return false;
    }

}
