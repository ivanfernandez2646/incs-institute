package com.dam.t08p01.vista.adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.t08p01.R;
import com.dam.t08p01.modelo.Incidencia;

import java.util.List;

public class AdaptadorIncs extends RecyclerView.Adapter<AdaptadorIncs.IncdVH> {

    private List<Incidencia> mDatos;
    private int mItemPos;
    private View.OnClickListener mListenerClick;
    private View.OnLongClickListener mListenerOnLongClick;

    public AdaptadorIncs() {
        mDatos = null;
        mItemPos = -1;
        mListenerClick = null;
        mListenerOnLongClick = null;
    }

    public void setDatos(List<Incidencia> mDatos) {
        this.mDatos = mDatos;
    }

    public int getItemPos() {
        return mItemPos;
    }

    public void setItemPos(int mItemPos) {
        this.mItemPos = mItemPos;
    }

    public void setOnClickListener(View.OnClickListener mListener) {
        this.mListenerClick = mListener;
    }

    public void setListenerOnLongClick(View.OnLongClickListener mListenerOnLongClick) {
        this.mListenerOnLongClick = mListenerOnLongClick;
    }

    public Incidencia getItem(int pos) {
        return mDatos.get(pos);
    }

    @NonNull
    @Override
    public IncdVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_rv_incs, parent, false);
        return new IncdVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IncdVH holder, int position) {
        if (mDatos != null) {
            holder.setItem(mDatos.get(position));
            holder.itemView.setBackgroundColor((mItemPos == position)
                    ? holder.itemView.getContext().getResources().getColor(R.color.colorPrimary)
                    : Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        if (mDatos != null) {
            return mDatos.size();
        } else {
            return 0;
        }
    }

    public class IncdVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvIncDptoIdFecha, tvIncTipoEstado, tvIncDescripcion;
        private View mItemView;

        public IncdVH(@NonNull View itemView) {
            super(itemView);
            tvIncDptoIdFecha = itemView.findViewById(R.id.tvIncDptoIdFecha);
            tvIncTipoEstado = itemView.findViewById(R.id.tvIncTipoEstado);
            tvIncDescripcion = itemView.findViewById(R.id.tvIncDescripcion);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mItemView = itemView;
        }

        public void setItem(Incidencia inc){
            tvIncDptoIdFecha.setText(String.format(mItemView.getContext().getResources().getString(R.string.msg_Inc_DptoIdFecha),inc.getIdDpto(),inc.getId(),inc.getFecha()));
            tvIncTipoEstado.setText(String.format(mItemView.getContext().getResources().getString(R.string.msg_Inc_TipoEstado),inc.getTipo(),(inc.isEstado() ? Incidencia.ESTADO.RESUELTA : Incidencia.ESTADO.NO_RESUELTA)));
            tvIncDescripcion.setText(String.format(mItemView.getContext().getResources().getString(R.string.msg_Inc_Descripcion),inc.getDescripcion()));
        }


        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            notifyItemChanged(mItemPos);
            mItemPos = (mItemPos == pos) ? -1 : pos;
            notifyItemChanged(mItemPos);
            if (mListenerClick != null)
                mListenerClick.onClick(v);
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getLayoutPosition();
            notifyItemChanged(mItemPos);
            mItemPos = pos;
            notifyItemChanged(mItemPos);
            if (mListenerOnLongClick != null)
                mListenerOnLongClick.onLongClick(v);
            return true;
        }
    }
}
