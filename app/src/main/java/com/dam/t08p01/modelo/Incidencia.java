package com.dam.t08p01.modelo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Incidencia implements Parcelable {

    public enum TIPO {RMI, RMA}

    //Si el estado es nulo, a la hora de filtrar se muestran todas
    public enum ESTADO {RESUELTA, NO_RESUELTA}

    /* Atributos **********************************************************************************/

    private String id; //PK
    private int idDpto;
    private String fecha;
    private String descripcion;
    private TIPO tipo;
    private boolean estado;
    private String resolucion;
    private Double latitud;
    private Double longitud;
    @Exclude
    private Bitmap imagen;

    /* Constructor **********************************************************************************/

    public Incidencia() {
        Date currDate = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
        id = "Inc-"+simpleDateFormat.format(currDate);

        simpleDateFormat.applyPattern("dd/MM/yyyy");
        fecha = simpleDateFormat.format(currDate);
    }

    /* Getters and Setters **********************************************************************************/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdDpto() {
        return idDpto;
    }

    public void setIdDpto(int idDpto) {
        this.idDpto = idDpto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFechaFormatFirebase(){
        String fechaTMP = fecha.replaceAll("/", "");
        return fechaTMP.substring(4) + fechaTMP.substring(2, 4) + fechaTMP.substring(0, 2);
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TIPO getTipo() {
        return tipo;
    }

    public void setTipo(TIPO tipo) {
        this.tipo = tipo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    @Exclude
    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    /* Parcelable **********************************************************************************/

    protected Incidencia(Parcel in) {
        id = in.readString();
        idDpto = in.readInt();
        fecha = in.readString();
        descripcion = in.readString();
        estado = in.readByte() != 0;
        resolucion = in.readString();
        if (in.readByte() == 0) {
            latitud = null;
        } else {
            latitud = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitud = null;
        } else {
            longitud = in.readDouble();
        }
        imagen = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Incidencia> CREATOR = new Creator<Incidencia>() {
        @Override
        public Incidencia createFromParcel(Parcel in) {
            return new Incidencia(in);
        }

        @Override
        public Incidencia[] newArray(int size) {
            return new Incidencia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(idDpto);
        dest.writeString(fecha);
        dest.writeString(descripcion);
        dest.writeByte((byte) (estado ? 1 : 0));
        dest.writeString(resolucion);
        if (latitud == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitud);
        }
        if (longitud == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitud);
        }
        dest.writeParcelable(imagen, flags);
    }
}
