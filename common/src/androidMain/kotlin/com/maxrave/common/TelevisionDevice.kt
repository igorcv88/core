package com.maxrave.common

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration

fun Context.isTelevisionDevice(): Boolean =
    packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
        resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
