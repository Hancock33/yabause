/*  Copyright 2019 devMiyax(smiyaxdev@gmail.com)

    This file is part of YabaSanshiro.

    YabaSanshiro is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    YabaSanshiro is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with YabaSanshiro; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
*/
package org.uoyabause.android.phone

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
// import net.nend.android.NendAdView
import org.devmiyax.yabasanshiro.BuildConfig
import org.devmiyax.yabasanshiro.R
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.ads.AdListener
import org.uoyabause.android.BillingViewModel

class GameSelectActivityPhone : AppCompatActivity() {
    lateinit var frg_: GameSelectFragmentPhone
    var adView: AdView? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark))
        window.setNavigationBarColor(getResources().getColor(R.color.black_opaque))

        val frame = FrameLayout(this)
        frame.id = CONTENT_VIEW_ID
        setContentView(
            frame, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        if (savedInstanceState == null) {
            frg_ = GameSelectFragmentPhone()

            val ft = supportFragmentManager.beginTransaction()
            ft.add(CONTENT_VIEW_ID, frg_).commit()
        } else {
            frg_ =
                supportFragmentManager.findFragmentById(CONTENT_VIEW_ID) as GameSelectFragmentPhone
        }

        if (BuildConfig.BUILD_TYPE != "pro") {
            val prefs =
                getSharedPreferences("private", Context.MODE_PRIVATE)
            val hasDonated = prefs.getBoolean("donated", false)
            if (hasDonated == false) {
                try {
                    MobileAds.initialize(this)
                    adView = AdView(this)
                    adView!!.adUnitId = this.getString(R.string.banner_ad_unit_id)
                    adView!!.setAdSize(AdSize.BANNER)
                    val adRequest = AdRequest.Builder().build()

                    val params = FrameLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    frame.addView(adView, params)
                    adView!!.bringToFront()
                    adView!!.invalidate()
                    ViewCompat.setTranslationZ(adView!!, 90f)
                    adView!!.loadAd(adRequest)

                    adView!!.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            // mAdView.getHeight() returns 0 since the ad UI didn't load
                            adView!!.viewTreeObserver.addOnGlobalLayoutListener(object :
                                OnGlobalLayoutListener {
                                override fun onGlobalLayout() {
                                    adView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    frg_.onAdViewIsShown(adView!!.getHeight())
                                }
                            })
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        frg_.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Pass the event to ActionBarDrawerToggle, if it returns
        val rtn = frg_.onOptionsItemSelected(item)
        if (rtn == true) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        adView?.destroy()
    }

    companion object {
        const val CONTENT_VIEW_ID = 10101010
    }
}
