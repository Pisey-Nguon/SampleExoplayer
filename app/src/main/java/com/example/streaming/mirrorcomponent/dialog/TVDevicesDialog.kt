package com.example.streaming.mirrorcomponent.dialog

import android.R
import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.connectsdk.device.ConnectableDevice
import com.connectsdk.device.DevicePicker


class TVDevicesDialog() {
    var mTV: ConnectableDevice? = null
    companion object{
        private lateinit var activity: AppCompatActivity
        fun instance(activity: AppCompatActivity):TVDevicesDialog{
            this.activity = activity
            return TVDevicesDialog()
        }
    }
    var dp: DevicePicker? = null
    var dialog: AlertDialog? = null
    var pairingAlertDialog: AlertDialog? = null
    var pairingCodeDialog: AlertDialog? = null
    fun showDevices(itemClickListener: OnItemClickListener) {
        dp = DevicePicker(activity)
        dialog = dp?.getPickerDialog("Cast to") { parent, view, position, id ->
            itemClickListener.onConnectTV(
                parent = parent,
                positionDevices = position
            )
        }
        dialog?.show()

    }

    fun showConfirmPairCode(){
        pairingAlertDialog = AlertDialog.Builder(activity)
            .setTitle("Pairing with TV")
            .setMessage("Please confirm the connection on your TV")
            .setPositiveButton("Okay", null)
            .setNegativeButton("Cancel") { dialog, which ->
                dp?.cancelPicker()

            }
            .create()
    }

    fun showEnterPairCode(itemClickListener: OnItemClickListener){
        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        pairingCodeDialog = AlertDialog.Builder(activity)
            .setTitle("Enter Pairing Code on TV")
            .setView(input)
            .setPositiveButton(R.string.ok) { arg0, arg1 ->
                imm.hideSoftInputFromWindow(input.windowToken, 0)
                val value = input.text.toString().trim { it <= ' ' }
                itemClickListener.onEnterPairingCode(value = value)
            }
            .setNegativeButton(R.string.cancel) { dialog, whichButton ->
                dp?.cancelPicker()
                imm.hideSoftInputFromWindow(input.windowToken, 0)
                itemClickListener.onNotConfirmParingCode()
            }
            .create()

        pairingCodeDialog?.show()
    }

    fun dismissConfirmPairCode(){
        if (pairingCodeDialog?.isShowing == true){
            pairingCodeDialog?.dismiss()
        }
    }

    fun dismissEnterPairCode(){
        if (pairingAlertDialog?.isShowing == true){
            pairingAlertDialog?.dismiss()
        }
    }

    interface OnItemClickListener{
        fun onConnectTV(parent: AdapterView<*>?, positionDevices: Int)
        fun onEnterPairingCode(value: String)
        fun onNotConfirmParingCode()
    }
}