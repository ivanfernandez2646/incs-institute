package com.dam.t08p01.modelo;

import java.text.SimpleDateFormat;

public class FiltroIncs {


    private String idDptoFiltro;
    private Incidencia.ESTADO estadoIncidenciaFiltro;
    private String fechaIncidenciaFiltro;

    public FiltroIncs() {
    }

    public String getIdDptoFiltro() {
        return idDptoFiltro;
    }

    public void setIdDptoFiltro(String idDptoFiltro) {
        this.idDptoFiltro = idDptoFiltro;
    }

    public Incidencia.ESTADO getEstadoIncidenciaFiltro() {
        return estadoIncidenciaFiltro;
    }

    public void setEstadoIncidenciaFiltro(Incidencia.ESTADO estadoIncidenciaFiltro) {
        this.estadoIncidenciaFiltro = estadoIncidenciaFiltro;
    }

    public String getFechaIncidenciaFiltro() {
        return fechaIncidenciaFiltro;
    }

    public String getFechaIncidenciaFiltroNotFormatted() {
        String fechaWithoutFormat = fechaIncidenciaFiltro.substring(6) + fechaIncidenciaFiltro.substring(3, 5) + fechaIncidenciaFiltro.substring(0, 2);
        return fechaWithoutFormat;
    }

    public void setFechaIncidenciaFiltro(String fechaIncidenciaFiltro) {
        this.fechaIncidenciaFiltro = fechaIncidenciaFiltro;
    }
}
