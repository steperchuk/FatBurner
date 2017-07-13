package com.fatburner.fatburner;

/**
 * Created by sergeyteperchuk on 7/12/17.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class ModalDialogSettings extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Внимание")
                .setIcon(R.drawable.ic_information)
                .setMessage("Ваши текущие настройки диеты и нормы воды будут сброшены если вы изменяли их. \n Вы уверены что хотите продолжить?")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                       //code here
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //
                                dialog.cancel();
                            }
                        }
                )
                .create();
    }
}