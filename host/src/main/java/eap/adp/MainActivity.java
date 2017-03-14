package eap.adp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {
    private static final String PLUGIN_APK_PATH = "/sdcard/adp/plugin.apk";
    private AssetManager pluginAssetManager = null;
    private Resources pluginResources = null;
    private ClassLoader pluginClassLoader = null;
    private Resources.Theme pluginTheme = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        initDynamicLoaderEnvironment();
    }

    private void initDynamicLoaderEnvironment() {
        String optimizedDirectory = this.getDir("dex", 0).getAbsolutePath();
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        this.pluginClassLoader = new DexClassLoader(PLUGIN_APK_PATH, optimizedDirectory, null, localClassLoader);

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, PLUGIN_APK_PATH);
            this.pluginAssetManager = assetManager;

            Resources superRes = super.getResources();
            this.pluginResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
            this.pluginTheme = this.pluginResources.newTheme();
            this.pluginTheme.setTo(super.getTheme());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public AssetManager getAssets() {
        return pluginAssetManager;
    }

    @Override
    public Resources getResources() {
        return pluginResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return pluginTheme;
    }

    @Override
    public ClassLoader getClassLoader() {
        return new ClassLoader(super.getClassLoader()) {
            @Override
            public Class<?> loadClass(String className) throws ClassNotFoundException {
                Class<?> clazz = null;
                clazz = pluginClassLoader.loadClass(className);
                if (clazz == null) {
                    clazz = getParent().loadClass(className);
                }
                // still not found
                if (clazz == null) {
                    throw new ClassNotFoundException(className);
                }

                return clazz;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        int viewId = generateViewId();
        frameLayout.setId(viewId);
        this.setContentView(frameLayout);
        // 固定的fragment，可以将配置以json格式存储在固定的class中，通过反射配置class获取具体的fragment path
        String pluginFragment = "eap.adp.plugin.PluginFragment";
        try {
            Class localClass = getClassLoader().loadClass(pluginFragment);
            Constructor localConstructor = localClass.getConstructor(new Class[]{});
            Object instance = localConstructor.newInstance(new Object[]{});
            Fragment fragment = (Fragment) instance;
            getFragmentManager().beginTransaction().replace(viewId, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public final static int generateViewId() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        } else {
            try {
                Class<View> clazz = View.class;
                Field field = clazz.getDeclaredField("sNextGeneratedId");
                field.setAccessible(true);
                AtomicInteger sNextGeneratedId = (AtomicInteger) field.get(null);
                for (; ; ) {
                    final int result = sNextGeneratedId.get();
                    // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                    int newValue = result + 1;
                    if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                    if (sNextGeneratedId.compareAndSet(result, newValue)) {
                        return result;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Create View Id Failed!!!!!");
    }
}
