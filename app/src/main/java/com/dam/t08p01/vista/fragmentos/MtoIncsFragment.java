package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.Incidencia;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.dam.t08p01.vistamodelo.IncsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MtoIncsFragment extends Fragment {

    private TextView tvCabecera, tvLatitudRes, tvLongitudRes;
    private Spinner spDptoId;
    private EditText etDptoNombre, etId, etFecha, etDescripcion, etResolucion;
    private RadioButton rbTipoRMA, rbTipoRMI;
    private CheckBox cbEstado;
    private Button btCancelar, btAceptar;


    private int mOp;    // Operación a realizar
    private Incidencia mIncidencia;

    public static final int OP_ELIMINAR = 1;
    public static final int OP_EDITAR = 2;
    public static final int OP_CREAR = 3;

    public static final String TAG = "MtoIncsFragment";
    private MtoIncsFragInterface mListener;

    private DptosViewModel dptosVM;

    private Bitmap imgIncd;

    public interface MtoIncsFragInterface {
        void onCancelarMtoIncsFrag();

        void onAceptarMtoIncsFrag(int op, Incidencia inc);
    }

    public MtoIncsFragment() {
        // Required empty public constructor
    }

    public static MtoIncsFragment newInstance(Bundle arguments) {
        MtoIncsFragment frag = new MtoIncsFragment();
        if (arguments != null) {
            frag.setArguments(arguments);
        }
        return frag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MtoIncsFragInterface) {
            mListener = (MtoIncsFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MtoIncsFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOp = getArguments().getInt("op");
            mIncidencia = (getArguments().getParcelable("incd"));
            setHasOptionsMenu(true);
        } else {
            mOp = -1;
            mIncidencia = null;
        }


        //Inits Spinner
        dptosVM = new ViewModelProvider(requireActivity()).get(DptosViewModel.class);
        dptosVM.setIdDptoSpinner(mIncidencia.getIdDpto());
        dptosVM.recuperarNombreDepartamento().observe(this, new Observer<Departamento>() {
            @Override
            public void onChanged(Departamento departamento) {
                etDptoNombre.setText(departamento.getNombre());
            }
        });

        // Inits LocationGoogle
        IncsViewModel incsVM = new ViewModelProvider(requireActivity()).get(IncsViewModel.class);
        incsVM.getLatitudLongitud().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //LLega a "" cuando estamos creando un usuario, en vez de editarlo
                if(!s.equals("")){
                    String[] res = s.split(":::");
                    if(res.length == 2){
                        tvLatitudRes.setText(res[0]);
                        tvLongitudRes.setText(res[1]);
                    }
                }
            }
        });

        //Inits ImgObserver
        incsVM.getImgIncd().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                imgIncd = bitmap;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mOp == OP_CREAR){
            menu.findItem(R.id.menuItemCamera).setVisible(true);
            menu.findItem(R.id.menuItemLocation).setVisible(true);
        }else if(mOp == OP_EDITAR){
            menu.findItem(R.id.menuItemCamera).setVisible(false);
            menu.findItem(R.id.menuItemLocation).setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mto_incs, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mto_incs, container, false);

        // FindViewByIds
        tvCabecera = v.findViewById(R.id.tvIncCabecera);
        tvLatitudRes = v.findViewById(R.id.tvLatitudRes);
        tvLongitudRes = v.findViewById(R.id.tvLongitudRes);
        spDptoId = v.findViewById(R.id.spDptoId);
        etDptoNombre = v.findViewById(R.id.etIncDptoNombre);
        etId = v.findViewById(R.id.etIncId);
        etFecha = v.findViewById(R.id.etIncFecha);
        etDescripcion = v.findViewById(R.id.etIncDescripcion);
        rbTipoRMA = v.findViewById(R.id.rbIncTipoRMA);
        rbTipoRMI = v.findViewById(R.id.rbIncTipoRMI);
        cbEstado = v.findViewById(R.id.cbIncEstado);
        etResolucion = v.findViewById(R.id.etIncResolucion);
        btCancelar = v.findViewById(R.id.btCancelar);
        btAceptar = v.findViewById(R.id.btAceptar);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //La utilizo a la hora de setear ids y nombres de departamento automáticamente
        List<Integer> idsDepartamentos = new ArrayList<>();
        for (Departamento dpto : dptosVM.getDptosSE().getValue()) {
            idsDepartamentos.add(dpto.getId());
        }

        //Init Spinner
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, idsDepartamentos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDptoId.setAdapter(adapter);

        // Inits
        if (mOp != -1) {                    // MtoIncsFragment requiere una operación válida!!
            btCancelar.setEnabled(true);
            btAceptar.setEnabled(true);
            if (mIncidencia.getIdDpto() == 0) {
                spDptoId.setEnabled(true);
            } else {
                spDptoId.setEnabled(false);
            }
            etId.setEnabled(false);
            etFecha.setEnabled(false);
            etResolucion.setEnabled(false);

            switch (mOp) {
                case OP_CREAR:
                    tvCabecera.setText(getString(R.string.tv_Inc_Cabecera_Crear));


                    int posicionDptoAdapterCrear = adapter.getPosition(mIncidencia.getIdDpto());
                    spDptoId.setSelection(posicionDptoAdapterCrear);

                    etId.setText(mIncidencia.getId());
                    etFecha.setText(mIncidencia.getFecha());
                    break;

                case OP_EDITAR:
                    tvCabecera.setText(getString(R.string.tv_Inc_Cabecera_Editar));
                    spDptoId.setEnabled(false);

                    int posicionDptoAdapterEditar = adapter.getPosition(mIncidencia.getIdDpto());
                    spDptoId.setSelection(posicionDptoAdapterEditar);

                    etId.setText(mIncidencia.getId());
                    etFecha.setText(mIncidencia.getFecha());
                    etDescripcion.setText(mIncidencia.getDescripcion());

                    if(mIncidencia.getLatitud() != null){
                        tvLatitudRes.setText(String.valueOf(mIncidencia.getLatitud()));
                    }

                    if(mIncidencia.getLongitud() != null){
                        tvLongitudRes.setText(String.valueOf(mIncidencia.getLongitud()));
                    }


                    if (mIncidencia.getTipo().equals(Incidencia.TIPO.RMA)) {
                        rbTipoRMA.setChecked(true);
                    } else if (mIncidencia.getTipo().equals(Incidencia.TIPO.RMI)) {
                        rbTipoRMI.setChecked(true);
                    }

                    cbEstado.setChecked(mIncidencia.isEstado());
                    etResolucion.setText(mIncidencia.getResolucion());
                    etResolucion.setEnabled(true);
                    break;
            }

            // Listeners
            btCancelar.setOnClickListener(btCancelar_OnClickListener);
            btAceptar.setOnClickListener(btAceptar_OnClickListener);
            spDptoId.setOnItemSelectedListener(spDptoId_OnItemClickListener);

        } else {
            btCancelar.setEnabled(false);
            btAceptar.setEnabled(false);
            spDptoId.setOnItemSelectedListener(spDptoId_OnItemClickListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private View.OnClickListener btCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mListener != null) {
                mListener.onCancelarMtoIncsFrag();
            }
        }
    };

    private View.OnClickListener btAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mListener != null) {
                if (!etDescripcion.getText().toString().equals("")) {

                    mIncidencia.setIdDpto(Integer.parseInt(spDptoId.getSelectedItem().toString()));
                    mIncidencia.setId(etId.getText().toString());
                    mIncidencia.setFecha(etFecha.getText().toString());
                    mIncidencia.setDescripcion(etDescripcion.getText().toString());

                    if(!tvLatitudRes.getText().toString().equals("")){
                        mIncidencia.setLatitud(Double.parseDouble(tvLatitudRes.getText().toString()));
                    }

                    if(!tvLongitudRes.getText().toString().equals("")){
                        mIncidencia.setLongitud(Double.parseDouble(tvLongitudRes.getText().toString()));
                    }

                    if (rbTipoRMA.isChecked()) {
                        mIncidencia.setTipo(Incidencia.TIPO.RMA);
                    } else if (rbTipoRMI.isChecked()) {
                        mIncidencia.setTipo(Incidencia.TIPO.RMI);
                    }

                    mIncidencia.setEstado(cbEstado.isChecked());
                    mIncidencia.setResolucion(etResolucion.getText().toString());

                    mIncidencia.setImagen(imgIncd);

                    mListener.onAceptarMtoIncsFrag(mOp, mIncidencia);
                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.msg_FaltanDatosObligatorios, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    };

    private Spinner.OnItemSelectedListener spDptoId_OnItemClickListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dptosVM.setIdDptoSpinner(Integer.parseInt(spDptoId.getSelectedItem().toString()));
            dptosVM.recuperarNombreDepartamento();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
