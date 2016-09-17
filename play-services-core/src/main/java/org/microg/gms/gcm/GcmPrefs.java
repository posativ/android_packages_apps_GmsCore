/*
 * Copyright 2013-2016 microG Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.gms.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GcmPrefs implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREF_GCM_HEARTBEAT = "gcm_heartbeat_interval";
    private static final String PREF_GCM_LOG = "gcm_full_log";
    private static final String PREF_LAST_PERSISTENT_ID = "gcm_last_persistent_id";
    private static GcmPrefs INSTANCE;

    public static GcmPrefs get(Context context) {
        if (INSTANCE == null) {
            if (context == null) return new GcmPrefs(null);
            INSTANCE = new GcmPrefs(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private int heartbeatMs = 300000;
    private boolean gcmLogEnabled = true;
    private String lastPersistedId = "";

    private SharedPreferences defaultPreferences;

    public GcmPrefs(Context context) {
        if (context != null) {
            defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            defaultPreferences.registerOnSharedPreferenceChangeListener(this);
            update();
        }
    }

    public void update() {
        heartbeatMs = Integer.parseInt(defaultPreferences.getString(PREF_GCM_HEARTBEAT, "300")) * 1000;
        gcmLogEnabled = defaultPreferences.getBoolean(PREF_GCM_LOG, true);
        lastPersistedId = defaultPreferences.getString(PREF_LAST_PERSISTENT_ID, "");
    }

    public int getHeartbeatMs() {
        return heartbeatMs;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        update();
    }

    public boolean isGcmLogEnabled() {
        return gcmLogEnabled;
    }

    public List<String> getLastPersistedIds() {
        if (lastPersistedId.isEmpty()) return Collections.emptyList();
        return Arrays.asList(lastPersistedId.split("\\|"));
    }

    public void extendLastPersistedId(String id) {
        if (!lastPersistedId.isEmpty()) lastPersistedId += "|";
        lastPersistedId += id;
        defaultPreferences.edit().putString(PREF_LAST_PERSISTENT_ID, lastPersistedId).apply();
    }

    public void clearLastPersistedId() {
        lastPersistedId = "";
        defaultPreferences.edit().putString(PREF_LAST_PERSISTENT_ID, lastPersistedId).apply();
    }
}
