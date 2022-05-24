package com.esaudev.memorygame.ui.components

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.esaudev.memorygame.R

class GameLostDialogFragment : DialogFragment() {

    var onRestartClick: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.game_lost_title)
                    .setPositiveButton(R.string.game_option_restart,
                            DialogInterface.OnClickListener { dialog, id ->
                                // START THE GAME!
                                onRestartClick.invoke()
                                dialog.dismiss()
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}