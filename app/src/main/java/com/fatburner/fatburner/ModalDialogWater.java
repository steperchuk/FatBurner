package com.fatburner.fatburner;

/**
 * Created by sergeyteperchuk on 7/12/17.
 */

import android.app.AlertDialog;
        import android.app.Dialog;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.DialogFragment;

public class ModalDialogWater extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Цель достигнута")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("Вы потребили достаточное количество воды.")
                .setPositiveButton("OK", null)
                .create();
    }
}