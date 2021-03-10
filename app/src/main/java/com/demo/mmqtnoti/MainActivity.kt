package com.demo.mmqtnoti

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import com.demo.mmqtnoti.notification.ConnectionStatus
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private var viewModel: MainViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = MainViewModel((application as PushApplication).messageGateway)
        observeState()
    }

    private fun observeState() {
        viewModel?.connectionStatus?.observe(this, Observer {

            when (it) {
                is ConnectionStatus.Connected -> showConnectedState()
                is ConnectionStatus.Disconnected -> showDisconnectedState()
                is ConnectionStatus.Connecting -> showConnectingState()
            }
        })
    }

    private fun showConnectingState() {
        showConnectingTextStatus()
        showConnectingLoadingOnStatus()
        hideStatusIcon()
        setControlButtonForConnectingState()
    }

    private fun showConnectedState() {
        showConnectedTextStatus()
        hideConnectingLoadingOnStatus()
        setControlButtonForConnectedState()
        showConnectedIcon()
    }

    private fun showDisconnectedState() {
        showDisconnectedTextStatus()
        hideConnectingLoadingOnStatus()
        showDisconnectedIcon()
        setControlButtonForDisconnectedState()
    }

    private fun setControlButtonForDisconnectedState() {
        findViewById<Button>(R.id.btn_connect).apply {
            changeBackgroundDrawableTint(this, R.color.green_500)
            text = getString(R.string.connect)
            setOnClickListener {
                viewModel?.connect()
            }
        }
    }

    private fun setControlButtonForConnectedState() {
        findViewById<Button>(R.id.btn_connect).apply {
            text = getString(R.string.disconnect)
            changeBackgroundDrawableTint(this, R.color.red_300)
            setOnClickListener {
                viewModel?.disconnect()
            }
        }
    }

    private fun changeBackgroundDrawableTint(view: View, @ColorRes color: Int) {
        val drawable = view.background
        DrawableCompat.setTint(drawable, resources.getColor(color))
        view.background = drawable
    }

    private fun setControlButtonForConnectingState() {
        findViewById<Button>(R.id.btn_connect).apply {
            text = getString(R.string.cancel)
            changeBackgroundDrawableTint(this, R.color.gray_800)
            setOnClickListener {
                viewModel?.disconnect()
                Log.d("MainActivity", "onDisconnect")
            }
        }
    }


    private fun hideStatusIcon() {
        findViewById<ImageView>(R.id.imv_status)
            .visibility = View.INVISIBLE
    }

    private fun showImageViewIfHidden(imv: ImageView) {
        if (imv.visibility == View.GONE) {
            imv.visibility = View.VISIBLE
        }
    }

    private fun showDisconnectedIcon() {
        findViewById<ImageView>(R.id.imv_status)
            .apply {
                setImageResource(R.drawable.ic_warning)
                visibility = View.VISIBLE
                showImageViewIfHidden(this)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
                imageTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.red_300)
            }
    }

    private fun showConnectedIcon() {
        findViewById<ImageView>(R.id.imv_status)
            .apply {
                setImageResource(R.drawable.ic_ok_checked)
                visibility = View.VISIBLE
                showImageViewIfHidden(this)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
                imageTintList =
                    ContextCompat.getColorStateList(this@MainActivity, R.color.green_500)
            }
    }

    private fun showConnectingLoadingOnStatus() {
        findViewById<ProgressBar>(R.id.pb_connection_status)
            .visibility = View.VISIBLE
    }

    private fun hideConnectingLoadingOnStatus() {
        findViewById<ProgressBar>(R.id.pb_connection_status)
            .visibility = View.GONE
    }

    private fun showConnectingTextStatus() {
        findViewById<TextView>(R.id.tv_progress)
            .text = StringBuilder()
            .append(getString(R.string.connection_status_suffix))
            .append(" ")
            .append(getString(R.string.status_connecting))
            .toString()
    }

    private fun showConnectedTextStatus() {
        findViewById<TextView>(R.id.tv_progress)
            .text = StringBuilder()
            .append(getString(R.string.connection_status_suffix))
            .append(" ")
            .append(getString(R.string.status_connected))
            .toString()
    }

    private fun showDisconnectedTextStatus() {
        findViewById<TextView>(R.id.tv_progress)
            .text = StringBuilder()
            .append(getString(R.string.connection_status_suffix))
            .append(" ")
            .append(getString(R.string.disconnect))
            .toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel = null
    }

}