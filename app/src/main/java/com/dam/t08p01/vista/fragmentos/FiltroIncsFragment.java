package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.Departamento;
import com.dam.t08p01.modelo.FiltroIncs;
import com.dam.t08p01.modelo.Incidencia;
import com.dam.t08p01.vistamodelo.DptosViewModel;
import com.dam.t08p01.vistamodelo.IncsViewModel;

import java.util.ArrayList;
import java.util.List;

public class FiltroIncsFragment extends Fragment {


    private Spinner spFiltroDptos;
    private RadioButton rbFiltroEstadoTodas, rbFiltroEstadoResueltas, rbFiltroEstadoNoResultas;
    private EditText etFiltroFecha;
    private Button btFiltroFecha, btCancelar, btAceptar;

    public static final String TAG = "BusDptosFragment";
    private FiltroIncsFragInterface mListener;

    public FiltroIncsFragment() {
        // Required empty public constructor
    }

    public interface FiltroIncsFragInterface{
        void onFiltroFechaFiltroIncsFrag();

        void onCancelarFiltroIncsFrag();

        void onAceptarFiltroIncsFrag(FiltroIncs filtroIncs);
    }

    public static FiltroIncsFragment newInstance(Bundle arguments) {
        FiltroIncsFragment frag = new FiltroIncsFragment();
        if (arguments != null) {
            frag.setArguments(arguments);
        }
        return frag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FiltroIncsFragInterface) {
            mListener = (FiltroIncsFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FiltroIncsFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inits Observe Fecha
        IncsViewModel incsVM = new ViewModelProvider(requireActivity()).get(IncsViewModel.class);
        incsVM.getFiltroIncs().observe(this, new Observer<FiltroIncs>() {
            @Override
            public void onChanged(FiltroIncs filtroIncs) {
                etFiltroFecha.setText(filtroIncs.getFechaIncidenciaFiltro());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filtro_incs, container, false);

        // FindViewByIds
        spFiltroDptos = v.findViewById(R.id.spFiltroDptos);
        rbFiltroEstadoTodas = v.findViewById(R.id.rbFiltroEstadoTodas);
        rbFiltroEstadoResueltas = v.findViewById(R.id.rbFiltroEstadoResueltas);
        rbFiltroEstadoNoResultas = v.findViewById(R.id.rbFiltroEstadoNoResueltas);
        etFiltroFecha = v.findViewById(R.id.etFiltroFecha);
        btFiltroFecha = v.findViewById(R.id.btFiltroFecha);
        btCancelar = v.findViewById(R.id.btCancelar);
        btAceptar = v.findViewById(R.id.btAceptar);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Seteamos los valores del spinner
        DptosViewModel mDptosVM = new ViewModelProvider(this).get(DptosViewModel.class);
        IncsViewModel mIncsVM = new ViewModelProvider(requireActivity()).get(IncsViewModel.class);
        List<Departamento> idsDepartamentos = new ArrayList<>();
        if(mIncsVM.getLogin().getId() == 0){
            for (Departamento dpto : mDptosVM.getDptosSE().getValue()) {
                idsDepartamentos.add(dpto);
            }
        }else{
            idsDepartamentos.add(mIncsVM.getLogin());
            spFiltroDptos.setEnabled(false);
        }

        //Init Spinner
        ArrayAdapter<Departamento> adapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, idsDepartamentos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroDptos.setAdapter(adapter);

        //Seteamos los valores del filtro, a los asociados en el ViewModel
        FiltroIncs filtroActual = mIncsVM.getFiltroIncs().getValue();
        for (int i = 0; i < idsDepartamentos.size(); i++) {
            if(idsDepartamentos.get(i).getId() == Integer.parseInt(filtroActual.getIdDptoFiltro())){
                spFiltroDptos.setSelection(i);
                break;
            }
        }
        if(filtroActual.getEstadoIncidenciaFiltro() != null){
            if(filtroActual.getEstadoIncidenciaFiltro() == Incidencia.ESTADO.RESUELTA){
                rbFiltroEstadoResueltas.setChecked(true);
            }else if(filtroActual.getEstadoIncidenciaFiltro() == Incidencia.ESTADO.NO_RESUELTA){
                rbFiltroEstadoNoResultas.setChecked(true);
            }
        }else{
            rbFiltroEstadoTodas.setChecked(true);
        }
        etFiltroFecha.setText(filtroActual.getFechaIncidenciaFiltro());


        //Listeners
        btFiltroFecha.setOnClickListener(btFiltroFecha_OnClickListener);
        btCancelar.setOnClickListener(btCancelar_OnClickListener);
        btAceptar.setOnClickListener(btAceptar_OnClickListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private View.OnClickListener btFiltroFecha_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onFiltroFechaFiltroIncsFrag();
        }
    };

    private View.OnClickListener btCancelar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onCancelarFiltroIncsFrag();
        }
    };

    private View.OnClickListener btAceptar_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FiltroIncs filtroIncs = new FiltroIncs();
            filtroIncs.setIdDptoFiltro(String.valueOf(((Departamento)spFiltroDptos.getSelectedItem()).getId()));

            if(rbFiltroEstadoTodas.isChecked()){
                filtroIncs.setEstadoIncidenciaFiltro(null);
            }else if(rbFiltroEstadoResueltas.isChecked()){
                filtroIncs.setEstadoIncidenciaFiltro(Incidencia.ESTADO.RESUELTA);
            }else if(rbFiltroEstadoNoResultas.isChecked()){
                filtroIncs.setEstadoIncidenciaFiltro(Incidencia.ESTADO.NO_RESUELTA);
            }
            filtroIncs.setFechaIncidenciaFiltro(etFiltroFecha.getText().toString());

            mListener.onAceptarFiltroIncsFrag(filtroIncs);
        }
    };
}
