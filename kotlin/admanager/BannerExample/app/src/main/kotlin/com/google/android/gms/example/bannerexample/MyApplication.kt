package com.google.android.gms.example.bannerexample

import android.app.Application
import org.prebid.mobile.Host
import org.prebid.mobile.PrebidMobile

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PrebidMobile.setPrebidServerAccountId(Constants.PBS_ACCOUNT_ID)
        PrebidMobile.setPrebidServerHost(Host.APPNEXUS)
        PrebidMobile.setShareGeoLocation(true)
        PrebidMobile.setApplicationContext(applicationContext)
    }
}