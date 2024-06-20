package com.nexlink.nexlinkmobileapp.view.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.nexlink.nexlinkmobileapp.R

fun alertConfirmDialog(
    context: Context,
    layoutInflater: LayoutInflater,
    onYesClicked: () -> Unit,
    title: String,
    message: String,
    icons: String? = null
) {
    val dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
    val dialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        .setView(dialogView)
        .create()

    val ivIcon = dialogView.findViewById<ImageView>(R.id.ivIcon)
    val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
    val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
    val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
    val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

    tvTitle.text = title
    tvMessage.text = message

    if (icons != null) {
        val icon = when (icons) {
            "warning" -> R.drawable.ic_alert_warning
            "error" -> R.drawable.ic_alert_danger
            "success" -> R.drawable.ic_alert_success
            "info" -> R.drawable.ic_alert_info
            else -> ivIcon.visibility = View.GONE
        }
        ivIcon.setImageResource(icon as Int)
        ivIcon.visibility = View.VISIBLE
    } else {
        ivIcon.visibility = View.GONE
    }

    btnNo.visibility = View.VISIBLE
    btnYes.visibility = View.VISIBLE
    btnOk.visibility = View.GONE

    btnNo.setOnClickListener {
        dialogBuilder.dismiss()
    }

    btnYes.setOnClickListener {
        onYesClicked()
        dialogBuilder.dismiss()
    }

    dialogBuilder.show()
}

fun alertInfoDialog(
    context: Context,
    layoutInflater: LayoutInflater,
    title: String,
    message: String,
    icons: String? = null
) {
    val dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
    val dialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        .setView(dialogView)
        .create()

    val ivIcon = dialogView.findViewById<ImageView>(R.id.ivIcon)
    val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
    val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
    val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
    val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

    tvTitle.text = title
    tvMessage.text = message

    // Mengatur ikon jika disediakan, atau menghilangkannya jika null
    if (icons != null) {
        val icon = when (icons) {
            "warning" -> R.drawable.ic_alert_warning
            "error" -> R.drawable.ic_alert_danger
            "success" -> R.drawable.ic_alert_success
            "info" -> R.drawable.ic_alert_info
            else -> ivIcon.visibility = View.GONE
        }

        ivIcon.setImageResource(icon as Int)
        ivIcon.visibility = View.VISIBLE
    } else {
        ivIcon.visibility = View.GONE
    }

    btnNo.visibility = View.GONE
    btnYes.visibility = View.GONE
    btnOk.visibility = View.VISIBLE

    btnOk.setOnClickListener {
        dialogBuilder.dismiss()
    }

    dialogBuilder.show()
}

fun alertInfoDialogWithEvent(
    context: Context,
    layoutInflater: LayoutInflater,
    onOkClicked: () -> Unit,
    title: String,
    message: String,
    icons: String? = null
) {
    val dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
    val dialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        .setView(dialogView)
        .create()

    val ivIcon = dialogView.findViewById<ImageView>(R.id.ivIcon)
    val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
    val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
    val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
    val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

    tvTitle.text = title
    tvMessage.text = message

    // Mengatur ikon jika disediakan, atau menghilangkannya jika null
    if (icons != null) {
        val icon = when (icons) {
            "warning" -> R.drawable.ic_alert_warning
            "error" -> R.drawable.ic_alert_danger
            "success" -> R.drawable.ic_alert_success
            "info" -> R.drawable.ic_alert_info
            else -> ivIcon.visibility = View.GONE
        }

        ivIcon.setImageResource(icon as Int)
        ivIcon.visibility = View.VISIBLE
    } else {
        ivIcon.visibility = View.GONE
    }

    btnNo.visibility = View.GONE
    btnYes.visibility = View.GONE
    btnOk.visibility = View.VISIBLE

    btnOk.setOnClickListener {
        onOkClicked()
        dialogBuilder.dismiss()
    }

    dialogBuilder.show()
}