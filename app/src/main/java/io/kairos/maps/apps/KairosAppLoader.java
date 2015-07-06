package io.kairos.maps.apps;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for creating the list of all apps available in the system.
 */
public class KairosAppLoader {
    private static final String TAG = KairosAppLoader.class.toString();

    // This will ultimately be fetched from resource files.
    private String[] appClassNames = {
        "io.kairos.maps.apps.calling.CallingApp",
        "io.kairos.maps.apps.texting.TextingApp",
        "io.kairos.maps.apps.navigation.NavigationApp",
        "io.kairos.maps.apps.music.MusicApp"
    };

    private Map<String, KairosApp> appsMap;

    private KairosAppLoader() {
        appsMap = new HashMap<String, KairosApp>();

        initAppsMap();
    }

    private static KairosAppLoader instance = new KairosAppLoader();

    public static KairosAppLoader instance() {
        return instance;
    }

    private void initAppsMap() {
        Log.d(TAG, "Loading Kairos Apps");
        Class<KairosApp> kairosAppClass = KairosApp.class;

        for (String appName : appClassNames) {
            try {
                KairosApp kairosApp = kairosAppClass.cast(Class.forName(appName).newInstance());
                appsMap.put(appName, kairosApp);
            } catch (InstantiationException e) {
                Log.e(TAG, "Error getting app " + appName, e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Error getting app " + appName, e);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Error getting app " + appName, e);
            }
        }
    }

    public List<KairosApp> getKairosApps() {
        return new ArrayList<KairosApp>(appsMap.values());
    }

    public KairosApp getKairosApp(String key) {
        return appsMap.get(key);
    }
}
