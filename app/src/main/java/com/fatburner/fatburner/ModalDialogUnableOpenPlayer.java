package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ModalDialogUnableOpenPlayer extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Не получается окртыть")
                .setIcon(R.drawable.ic_warning_smal)
                .setMessage("Не получается окртыть плеер на вашем устройстве.")
                .setPositiveButton("ОК", null)
                .create();
    }
}
