package com.dam.t08p01.vista;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.AppDatabase;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.FiltroIncs;
import com.dam.t08p01.modelo.Incidencia;
import com.dam.t08p01.repositorio.LocGoogle;
import com.dam.t08p01.vista.dialogos.DlgConfirmacion;
import com.dam.t08p01.vista.dialogos.DlgSeleccionFecha;
import com.dam.t08p01.vista.fragmentos.BusIncsFragment;
import com.dam.t08p01.vista.fragmentos.FiltroIncsFragment;
import com.dam.t08p01.vista.fragmentos.MapIncsFragment;
import com.dam.t08p01.vista.fragmentos.MtoIncsFragment;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.dam.t08p01.vistamodelo.IncsViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncsActivity extends AppCompatActivity implements BusIncsFragment.BusIncsFragInterface,
        MtoIncsFragment.MtoIncsFragInterface,
        DlgConfirmacion.DlgConfirmacionListener,
        FiltroIncsFragment.FiltroIncsFragInterface,
        DlgSeleccionFecha.DlgSeleccionFechaListener,
        OnMapReadyCallback,
        MapIncsFragment.MapIncsFragInterface {

    private NavController mNavC;

    private IncsViewModel mIncsVM;
    private Departamento mLogin;

    private LocationCallback locationCallback;

    public static final int REQUEST_IMAGE_CAPTURE = 3;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // FindViewByIds

        // Inits
        mNavC = Navigation.findNavController(this, R.id.navhostfrag_incs);
        mIncsVM = new ViewModelProvider(this).get(IncsViewModel.class);

        // Recuperamos el dpto login
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            if (b != null) {
                mLogin = b.getParcelable("login");
                mIncsVM.setLogin(mLogin);      // Guardamos el login en el ViewModel

                if (savedInstanceState == null) {
                    FiltroIncs filtroIncs = new FiltroIncs();
                    filtroIncs.setIdDptoFiltro(String.valueOf(mLogin.getId()));
                    filtroIncs.setEstadoIncidenciaFiltro(null);
                    Date currDate = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                    simpleDateFormat.applyPattern("dd/MM/yyyy");
                    filtroIncs.setFechaIncidenciaFiltro(simpleDateFormat.format(currDate));
                    mIncsVM.getFiltroIncs().setValue(filtroIncs);
                }
            }
        }
        if (mLogin == null) {
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_NoLogin, Snackbar.LENGTH_SHORT).show();
            finish();
            return;
        }

        //LocationCallBack
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    double lastLatitude = locationResult.getLastLocation().getLatitude();
                    double lastLongitude = locationResult.getLastLocation().getLongitude();
                    mIncsVM.getLatitudLongitud().setValue(lastLatitude + ":::" + lastLongitude);
                    Snackbar.make(findViewById(android.R.id.content), String.format(getString(R.string.msg_Loc_LatLong), lastLatitude, lastLongitude), Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCrearBusIncsFrag() {
        // Lanzamos MtoIncsFragment
        Bundle bundle = new Bundle();
        bundle.putInt("op", MtoIncsFragment.OP_CREAR);
        Incidencia incidencia = new Incidencia();
        mIncsVM.getLatitudLongitud().setValue("");
        mIncsVM.getImgIncd().setValue(null);
        incidencia.setIdDpto(mLogin.getId());
        bundle.putParcelable("incd", incidencia);
        mNavC.navigate(R.id.action_busIncsFragment_to_mtoIncsFragment, bundle);
    }

    @Override
    public void onEditarBusIncsFrag(Incidencia incd) {
        Bundle bundle = new Bundle();
        mIncsVM.getLatitudLongitud().setValue((incd.getLatitud() == null ? "" : incd.getLatitud()) + ":::" + (incd.getLongitud() == null ? "" : incd.getLongitud()));
        bundle.putInt("op", MtoIncsFragment.OP_EDITAR);
        bundle.putParcelable("incd", incd);
        mNavC.navigate(R.id.action_busIncsFragment_to_mtoIncsFragment, bundle);
    }

    @Override
    public void onEliminarBusIncsFrag(Incidencia incd) {
        mIncsVM.setIncidenciaAEliminar(incd);
        // Lanzamos DlgConfirmacion
        Bundle bundle = new Bundle();
        bundle.putInt("titulo", R.string.app_name);
        bundle.putInt("mensaje", R.string.msg_DlgConfirmacion_Eliminar);
        mNavC.navigate(R.id.action_global_dlgConfirmacionIncs, bundle);
    }

    @Override
    public void onCameraBusIncsFrag(final Incidencia incd) {
        //Ponemos a descargar la imagen
        final long ONE_MEGABYTE = 1024 * 1024;
        AppDatabase.getAppDatabase(this).getRefSG()
                .child("Incs")
                .child(String.valueOf(incd.getIdDpto()))
                .child(incd.getFechaFormatFirebase())
                .child(incd.getId().substring(4))
                .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                mIncsVM.getImgIncd().setValue(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                mNavC.navigate(R.id.action_global_dlgImagenIncs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                mIncsVM.getImgIncd().setValue(null);
                mNavC.navigate(R.id.action_global_dlgImagenIncs);
            }
        });
    }

    @Override
    public void onCancelarMtoIncsFrag() {
        // Cerramos MtoIncsFragment
        LocGoogle locGoogle = LocGoogle.getInstance();
        locGoogle.stopLocationUpdates(locationCallback);
        mIncsVM.getImgIncd().setValue(null);
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarMtoIncsFrag(int op, Incidencia inc) {
        switch (op) {
            case MtoIncsFragment.OP_CREAR:
                if (mIncsVM.altaIncidencia(inc)) {
                    Bitmap img = inc.getImagen();

                    if (img != null) {
                        img = img.createScaledBitmap(img, 500, 350, false);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = AppDatabase.getAppDatabase(this).getRefSG()
                                .child("Incs")
                                .child(String.valueOf(inc.getIdDpto()))
                                .child(inc.getFechaFormatFirebase())
                                .child(inc.getId().substring(4))
                                .putBytes(data);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaIncidenciaKO, Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaIncidenciaOK, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaIncidenciaOK, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.msg_AltaIncidenciaKO, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case MtoIncsFragment.OP_EDITAR:
                if (mIncsVM.editarIncidencia(inc)) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.msg_EditarIncidenciaOK, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.msg_EditarIncidenciaKO, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case MtoIncsFragment.OP_ELIMINAR:
                break;
        }
        LocGoogle locGoogle = LocGoogle.getInstance();
        locGoogle.stopLocationUpdates(locationCallback);
        // Cerramos MtoIncsFragment
        mNavC.navigateUp();
    }

    //Diálogo Listeners
    @Override
    public void onDlgConfirmacionPositiveClick(DialogFragment dialog) {
        if (mIncsVM.bajaIncidencia(mIncsVM.getIncidenciaAEliminar())) {
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_BajaIncidenciaOK, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_BajaIncidenciaKO, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDlgConfirmacionNegativeClick(DialogFragment dialog) {
        ;
    }


    //ItemSelected Menu ---------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFiltroIncs:
                mNavC.navigate(R.id.action_busIncsFragment_to_filtroIncsFragment);
                break;
            case R.id.menuItemCamera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case R.id.menuItemLocation:
                LocGoogle locGoogle = LocGoogle.getInstance();
                locGoogle.initLocGoogle(this, locationCallback);
                break;
            case R.id.menuMapaIncs:
                mNavC.navigate(R.id.action_busIncsFragment_to_mapIncsFragment);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFiltroFechaFiltroIncsFrag() {
        mNavC.navigate(R.id.action_global_dlgSeleccionFecha);
    }

    //FiltroIncs ---------------------------------------------------------------------------------------------------------------
    @Override
    public void onCancelarFiltroIncsFrag() {
        mNavC.navigateUp();
    }

    @Override
    public void onAceptarFiltroIncsFrag(FiltroIncs filtroIncs) {
        mIncsVM.getFiltroIncs().setValue(filtroIncs);
        mIncsVM.getIncsME();
        mNavC.navigateUp();
    }

    @Override
    public void onDlgSeleccionFechaClick(DialogFragment dialog, String fecha) {
        FiltroIncs filtroIncsTMP = mIncsVM.getFiltroIncs().getValue();
        filtroIncsTMP.setFechaIncidenciaFiltro(fecha);
        mIncsVM.getFiltroIncs().setValue(filtroIncsTMP);
    }

    @Override
    public void onDlgSeleccionFechaCancel(DialogFragment dialog) {
        ;
    }


    //Permisos CallBack ---------------------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocGoogle.PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocGoogle locGoogle = LocGoogle.getInstance();
                    locGoogle.startLocationUpdates(locationCallback);
                }
            }
        }
    }

    //Nos llega el resultado de chequear las settings establecidas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocGoogle.REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    LocGoogle locGoogle = LocGoogle.getInstance();
                    locGoogle.startLocationUpdates(locationCallback);
                }
            }
            break;
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mIncsVM.getImgIncd().setValue(imageBitmap);
                }
            }
        }
    }

    //Maps ----------------------------------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(final GoogleMap googleMap) {


        //Seteamos el Tipo de Mapa
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String tipoMapa = pref.getString(this.getApplicationContext().getResources().getString(R.string.Map_tipo_key),
                pref.getString(this.getApplicationContext().getResources().getString(R.string.Map_tipo_default), ""));

        switch (tipoMapa) {
            case "Normal":
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "Híbrido":
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "Satélite":
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "Tierra":
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

        //Creamos los marcadores con sus textos y snippets
        final DptosViewModel dptosVM = new ViewModelProvider(this).get(DptosViewModel.class);
        final List<Departamento> departamentoList = dptosVM.getDptosSE().getValue();
        mIncsVM.getIncsSE().observe(this, new Observer<List<Incidencia>>() {
            @Override
            public void onChanged(List<Incidencia> incidencias) {
                googleMap.clear();
                boolean establecerLimites = false;
                LatLngBounds.Builder boundsMap = new LatLngBounds.Builder();

                for (final Incidencia incd : incidencias) {

                    String nombreDpto = "";

                    for (Departamento departamento : departamentoList) {
                        if (departamento.getId() == incd.getIdDpto()) {
                            nombreDpto = departamento.getNombre();
                        }
                    }

                    if (incd.getLatitud() != null && incd.getLongitud() != null) {
                        establecerLimites = true;
                        LatLng latLng = new LatLng(incd.getLatitud(), incd.getLongitud());
                        boundsMap = boundsMap.include(latLng);
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(incd.getFecha() + " " + nombreDpto)
                                .snippet(incd.getId().substring(4) + " " + (incd.isEstado() ? Incidencia.ESTADO.RESUELTA : Incidencia.ESTADO.NO_RESUELTA))
                                .icon(BitmapDescriptorFactory.defaultMarker((incd.isEstado() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED))));
                    }
                }


                if (establecerLimites) {
                    //Límites GoogleMaps
                    googleMap.setLatLngBoundsForCameraTarget(boundsMap.build());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsMap.build(), 125));
                }

                //Activamos el zoom tanto para gestos, como los botones
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
            }
        });
    }

    //CallBack Cerrar Fragment Mapa
    @Override
    public void onAceptarMapIncsFragment() {
        mNavC.navigateUp();
    }
}
