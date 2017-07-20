package com.fatburner.fatburner;

/**
 * Created by sergeyteperchuk on 7/12/17.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ModalDialogNotSelectedDiet extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Опция не активна!")
                .setIcon(R.drawable.ic_information)
                .setMessage("Вам необходимо активировать опцию 'Следовать Диете' в меню настроек приложения.")
                .setPositiveButton("ОК", null)
                .create();
    }
}