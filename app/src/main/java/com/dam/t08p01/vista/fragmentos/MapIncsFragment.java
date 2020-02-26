package com.dam.t08p01.vista.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dam.t08p01.R;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapIncsFragment extends Fragment{

    private MapView mapView;
    private Button btAceptar;

    public MapIncsFragment() {
        // Required empty public constructor
    }

    private MapIncsFragInterface mListener;

    public interface MapIncsFragInterface{
        void onAceptarMapIncsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MapIncsFragInterface) {
            mListener = (MapIncsFragInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MapIncsFragInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_incs, container, false);

        mapView = v.findViewById(R.id.mapView);
        btAceptar = v.findViewById(R.id.btAceptar);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) requireActivity());
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Inits

        //Listeners
        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAceptarMapIncsFragment();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
