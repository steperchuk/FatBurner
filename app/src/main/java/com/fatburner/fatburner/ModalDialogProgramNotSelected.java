package com.fatburner.fatburner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ModalDialogProgramNotSelected extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Программа тренировок не выбрана!")
                .setIcon(R.drawable.ic_information)
                .setMessage("Вам необходимо выбрать программу.")
                .setPositiveButton("ОК", null)
                .create();
    }
}
