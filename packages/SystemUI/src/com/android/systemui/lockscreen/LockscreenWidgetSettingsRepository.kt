/*
 * Copyright (C) 2025 The AxionAOSP Project
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
package com.android.systemui.lockscreen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import androidx.core.content.ContextCompat
import android.provider.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers

data class WidgetSettings(
    val settings: String,
    val isEnabled: Boolean,
    val isNight: Boolean,
    val theme: Int
)

class LockscreenWidgetSettingsRepository(
    private val context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver

    val settings: WidgetSettings get() {
        val settings = Settings.System.getStringForUser(
            contentResolver,
            "lockscreen_widgets_extras",
            UserHandle.USER_CURRENT
        ) ?: ""

        val isEnabled = Settings.System.getIntForUser(
            contentResolver,
            "lockscreen_widgets_enabled",
            0,
            UserHandle.USER_CURRENT
        ) == 1

        val isNight = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        val darkColorActive = ContextCompat.getColor(context, LsWidgetsRes.COLOR_BG_ADARK)
        val lightColorActive = ContextCompat.getColor(context, LsWidgetsRes.COLOR_BG_ALIGHT)

        val theme = 31 * darkColorActive + lightColorActive

        return WidgetSettings(settings, isEnabled, isNight, theme)
    }
}
