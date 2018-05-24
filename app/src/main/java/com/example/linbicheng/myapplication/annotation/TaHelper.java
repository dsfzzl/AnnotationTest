package com.example.linbicheng.myapplication.annotation;

import android.app.Activity;
import android.view.View;

import com.face.jfshare.annotationlib.ProxyInfo;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaHelper {
    /**
     * 用来缓存反射出来的类，节省每次都去反射引起的性能问题
     */
    static final Map<Class<?>, Constructor<?>> BINDINGS = new LinkedHashMap<>();
    public static void inject(Activity host) {
        inject(host,host.getWindow().getDecorView());
    }

    public static void inject(Object host, View view) {
        String name = host.getClass().getName() + ProxyInfo.ClassSuffix;
        Constructor<?> constructor = BINDINGS.get(name);
        try {
        if (constructor == null) {
            Class<?> aClass = Class.forName(name);
            constructor = aClass.getDeclaredConstructor(host.getClass(), View.class);
            BINDINGS.put(aClass,constructor);
        }
        constructor.setAccessible(true);
        constructor.newInstance(host,view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
