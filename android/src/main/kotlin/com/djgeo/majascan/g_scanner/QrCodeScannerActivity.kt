package com.djgeo.majascan.g_scanner


import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.djgeo.majascan.R

class QrCodeScannerActivity : AppCompatActivity() {

    companion object {

        val REQUEST_CAMERA = 1

        //bundle key
        val BUNDLE_SCAN_CALLBACK = "BUNDLE_SCAN_CALLBACK"

        //key from flutter
        val FLASHLIGHT = "FLASHLIGHT"
        val TITLE = "TITLE"
        val TITLE_COLOR = "TITLE_COLOR"
        val BAR_COLOR = "BAR_COLOR"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.noActionbarTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_g_scanner)

        goToScanFragment()

        //鎖定螢幕
        val orientation = resources.configuration.orientation
        Log.d("QrCodeScannerActivity", "QrCodeScannerActivity:$orientation")
        requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }

    fun goToWebviewFragment(url: String, webTitle: String) {
        val fm = supportFragmentManager
        val webViewFragment = WebViewFragment.newInstance(url, webTitle)
        fm?.beginTransaction()?.replace(R.id.fragment_container, webViewFragment)?.addToBackStack(WebViewFragment::class.java.simpleName)?.commitAllowingStateLoss()
    }

    fun goToScanFragment() {
        val intent = intent
        val fm = supportFragmentManager

        //是否有閃光燈按鈕
        var hasFlashLight = true
        if ("0" == intent.getStringExtra(FLASHLIGHT)) {
            hasFlashLight = false
        }

        //toolbar顏色
        var toolbarColor = 0
        val stringBarColor = intent.getStringExtra(BAR_COLOR)
        if (!TextUtils.isEmpty(stringBarColor) && stringBarColor.indexOf("#") == 0) {
            try {
                toolbarColor = Color.parseColor(stringBarColor)
            } catch (e: Exception) {
                Log.e("QrCodeScannerActivity", "parse color code error:$e")
            }

        }

        var titleColor = 0
        val stringTitleColor = intent.getStringExtra(TITLE_COLOR)
        if (!TextUtils.isEmpty(stringTitleColor) && stringTitleColor.indexOf("#") == 0) {
            try {
                titleColor = Color.parseColor(stringTitleColor)
            } catch (e: Exception) {
                Log.e("QrCodeScannerActivity", "parse color code error:$e")
            }

        }

        val scanFragment = ScanFragment.newInstance(
                intent.getStringExtra(TITLE),
                hasFlashLight,
                toolbarColor,
                titleColor
        )

        fm?.beginTransaction()?.replace(R.id.fragment_container, scanFragment, ScanFragment::class.java.simpleName)?.commitAllowingStateLoss()
    }

    fun receiveAndSetResult(result: String) {
        val intent = Intent()
        intent.putExtra(BUNDLE_SCAN_CALLBACK, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        //攔截back鍵事件，如果是在ScanFragment就強制finish();
        if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            val fm = supportFragmentManager
            if (fm != null) {
                val fragment = fm.findFragmentByTag(ScanFragment::class.java.simpleName)
                if (fragment is ScanFragment) {
                    finish()
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val fm = supportFragmentManager
        if (fm != null) {
            val fragment = fm.findFragmentByTag(ScanFragment::class.java.simpleName)
            if (fragment is ScanFragment) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }
}