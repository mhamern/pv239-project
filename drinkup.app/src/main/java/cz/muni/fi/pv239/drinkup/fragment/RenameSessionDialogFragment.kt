package cz.muni.fi.pv239.drinkup.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import cz.muni.fi.pv239.drinkup.R
//
//class RenameSessionDialogFragment: DialogFragment() {
//     override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
//        return activity?.let {
//            val myContext = context
//            // Use the Builder class for convenient dialog construction
//            val builder = AlertDialog.Builder(it)
//            builder.setMessage("message")
//                    .setPositiveButton("fire",
//                            DialogInterface.OnClickListener { dialog, id ->
//                                // FIRE ZE MISSILES!
//                            })
//                    .setNegativeButton(R.string.cancel,
//                            DialogInterface.OnClickListener { dialog, id ->
//                                // User cancelled the dialog
//                            })
//            // Create the AlertDialog object and return it
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
//    }
//}