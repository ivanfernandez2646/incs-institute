package com.dam.t08p01.vista.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.dam.t08p01.R;
import com.dam.t08p01.vistamodelo.IncsViewModel;

public class DlgImagenIncs extends DialogFragment {

    private Context appContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            final IncsViewModel mIncsVM = new ViewModelProvider(requireActivity()).get(IncsViewModel.class);

            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.msg_DlgImagen_Incidencia);
            builder.setView(inflater.inflate(R.layout.dialog_img_incs, null));
            builder.setPositiveButton(R.string.bt_Aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mIncsVM.getImgIncd().setValue(null);
                }
            });

            final AlertDialog dialogo = builder.create();
            dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Bitmap bitmap = mIncsVM.getImgIncd().getValue();
                    ImageView image = dialogo.findViewById(R.id.ivFotoIncs);
                    if(bitmap != null){
                        image.setImageBitmap(bitmap);
                    }else{
                        image.setImageDrawable(appContext.getResources().getDrawable(R.drawable.ic_logo_echirinosv4));
                    }
                }
            });
            return dialogo;
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (appContext == null)
            appContext = context.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
