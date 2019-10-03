/*
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.bannerexample

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import kotlinx.android.synthetic.main.activity_my.*
import org.prebid.mobile.*
import org.prebid.mobile.addendum.AdViewUtils
import org.prebid.mobile.addendum.PbFindSizeError

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
class MyActivity : AppCompatActivity() {
    internal var refreshCount: Int = 0
    internal var adUnit: AdUnit? = null
    lateinit var resultCode: ResultCode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        createDFPBanner(Constants.AD_SIZE_320x50)

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        val adRequest = PublisherAdRequest.Builder().build()

        // Start loading the ad in the background.
        ad_view.loadAd(adRequest)
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        ad_view.destroy()
        super.onDestroy()
    }

    internal fun createDFPBanner(size: String) {
        val container = findViewById<RelativeLayout>(R.id.container)
        container.removeView(ad_view)

        val wAndH = size.split("x".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val width = Integer.valueOf(wAndH[0])
        val height = Integer.valueOf(wAndH[1])

        val dfpAdView = PublisherAdView(this)
        dfpAdView.setAdSizes(AdSize.BANNER)
        adUnit = BannerAdUnit(Constants.PBS_CONFIG_ID_320x50_APPNEXUS, width, height)

        dfpAdView.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()

                AdViewUtils.findPrebidCreativeSize(dfpAdView, object : AdViewUtils.PbFindSizeListener {
                    override fun success(width: Int, height: Int) {
                        dfpAdView.setAdSizes(AdSize(width, height))

                    }

                    override fun failure(error: PbFindSizeError) {
                        Log.d("MyTag", "error: $error")
                    }
                })

            }
        })

        dfpAdView.setAdSizes(AdSize(width, height))
        dfpAdView.adUnitId = Constants.GAM_ADUNIT_ID_320x50
        container.addView(dfpAdView)

        val builder = PublisherAdRequest.Builder()
        val request = builder.build()
        loadRequest(request, dfpAdView)
    }

    internal fun loadRequest(request: PublisherAdRequest, adView: PublisherAdView) {
        //region PrebidMobile Mobile API 1.0 usage
        val millis = intent.getIntExtra(Constants.AUTO_REFRESH_NAME, 0)
        adUnit!!.setAutoRefreshPeriodMillis(millis)
        adUnit!!.fetchDemand(request, object : OnCompleteListener {
            override fun onComplete(resultCode: ResultCode) {
                this@MyActivity.resultCode = resultCode
                adView.loadAd(request)
                refreshCount++
            }
        })
    }
}
