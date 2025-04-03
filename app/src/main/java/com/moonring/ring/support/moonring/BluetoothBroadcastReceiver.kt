package com.moonring.ring.support.moonring

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (BluetoothDevice.ACTION_NAME_CHANGED == action) {
//            val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            val deviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)
            Log.d("Bluetooth", "Device name changed: $deviceName")
        }
    }
}
