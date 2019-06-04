/*
 *    Copyright 2018 APPNEXUS INC
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

package com.appnexus.opensdk.shadows;

import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.appnexus.opensdk.util.TestUtil;
import com.appnexus.opensdk.utils.Clog;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

@Implements(value = WebView.class, callThroughByDefault = true)
public class ShadowCustomWebView extends ShadowWebView {

    private WebView webView;
    public static boolean simulateRendererScriptSuccess = false;

    /*
    * This makes it possible for onAdLoaded on Banner to be called
     */
    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        if(webView == null) {
            webView = new WebView(RuntimeEnvironment.application);
        }
        if (simulateRendererScriptSuccess) {
            this.getWebViewClient().shouldOverrideUrlLoading(webView, "nativerenderer://success");
            simulateRendererScriptSuccess = false;
        }
        Clog.d(TestUtil.testLogTag, "ShadowCustomWebView loadDataWithBaseURL");
        this.getWebViewClient().onPageFinished(webView,baseUrl);
    }

}
