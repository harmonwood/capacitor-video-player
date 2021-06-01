package com.jeep.plugin.capacitor.capacitorvideoplayer.Notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NotificationCenter {

    //static reference for singleton
    private static NotificationCenter _instance;

    private HashMap<String, ArrayList<MyRunnable>> registeredObjects;

    //default c'tor for singleton
    private NotificationCenter() {
        registeredObjects = new HashMap<String, ArrayList<MyRunnable>>();
    }

    //returning the reference
    public static synchronized NotificationCenter defaultCenter() {
        if (_instance == null) _instance = new NotificationCenter();
        return _instance;
    }

    public synchronized void addMethodForNotification(String notificationName, MyRunnable r) {
        ArrayList<MyRunnable> list = registeredObjects.get(notificationName);
        if (list == null) {
            list = new ArrayList<MyRunnable>();
            registeredObjects.put(notificationName, list);
        }
        list.add(r);
    }

    public synchronized void removeMethodForNotification(String notificationName, MyRunnable r) {
        ArrayList<MyRunnable> list = registeredObjects.get(notificationName);
        if (list != null) {
            list.remove(r);
        }
    }

    public synchronized void removeAllNotifications() {
        for (Iterator<Map.Entry<String, ArrayList<MyRunnable>>> entry = registeredObjects.entrySet().iterator(); entry.hasNext();) {
            Map.Entry<String, ArrayList<MyRunnable>> e = entry.next();
            String key = e.getKey();
            ArrayList<MyRunnable> value = e.getValue();
            removeMethodForNotification(key, value.get(0));
            entry.remove();
        }
    }

    public synchronized void postNotification(String notificationName, Map<String, Object> _info) {
        ArrayList<MyRunnable> list = registeredObjects.get(notificationName);
        if (list != null) {
            for (MyRunnable r : list) {
                r.setInfo(_info);
                r.run();
            }
        }
    }
}
