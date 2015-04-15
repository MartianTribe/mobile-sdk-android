/*
 *    Copyright 2015 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.appnexus.opensdk.mediatedviews;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.appnexus.opensdk.MediatedInterstitialAdView;
import com.appnexus.opensdk.MediatedInterstitialAdViewController;
import com.appnexus.opensdk.TargetingParameters;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyVideoAd;

import java.lang.ref.WeakReference;

/**
 * This class is the AdColony interstitial adaptor it provides the functionality needed to allow
 * an application using the App Nexus SDK to load an interstitial ad through the AdColony SDK. The
 * instantiation of this class is done in response from the AppNexus server for an interstitial
 * placement that is configured to use AdConoly to serve it. This class is never directly instantiated
 * by the developer.
 */
public class AdColonyInterstitial implements MediatedInterstitialAdView {
    String zoneId;
    WeakReference<Activity> weakActivity;
    AdColonyVideoAd ad;
    AdColonyListener listener;

    @Override
    public void requestAd(MediatedInterstitialAdViewController mIC, Activity activity, String parameter, String uid, TargetingParameters tp) {
        zoneId = uid;
        weakActivity = new WeakReference<Activity>(activity);
        listener = new AdColonyListener(mIC, this.getClass().getSimpleName());
        if (isReady()) {
            ad = new AdColonyVideoAd(zoneId).withListener(listener);
            if (mIC != null) {
                mIC.onAdLoaded();
            }
        } else {
            String zoneStatus = AdColony.statusForZone(zoneId);
            listener.onZoneStatusNotActive(zoneStatus, zoneId);
        }

    }

    @Override
    public void show() {
        if (isReady()) {
            Activity activity = weakActivity.get();
            if (activity != null) {
                // AdColony may rotate the screen
                // this line guarantees that after ad collapsed, activity restores to sensor detected rotation
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
            ad.show();
        }
    }

    @Override
    public boolean isReady() {
        return AdColony.statusForZone(zoneId).equals(AdColonySettings.ACTIVE);
    }

    @Override
    public void destroy() {
        listener = null;
        ad = null;
    }

    @Override
    public void onPause() {
        AdColony.pause();
    }

    @Override
    public void onResume() {
        Activity activity = this.weakActivity.get();
        if (activity != null) {
            AdColony.resume(activity);
        }
    }

    @Override
    public void onDestroy() {

    }
}
