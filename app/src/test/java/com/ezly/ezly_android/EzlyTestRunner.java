package com.ezly.ezly_android;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

import java.io.File;

/**
 * Created by Johnnie on 26/12/17.
 */

public class EzlyTestRunner extends RobolectricTestRunner {
    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory by default. Use the {@link Config} annotation to configure.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public EzlyTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String appRoot = "app/src/main/";
        String manifestPath =  appRoot + "AndroidManifest.xml";
        String resDir = appRoot + "res";
        String assetsDir = appRoot + "assets";
        String packageName = "com.ezly.ezly_android";

        File f = new File(appRoot);

        AndroidManifest manifest = createAppManifest(Fs.fileFromPath(manifestPath),
                Fs.fileFromPath(resDir),
                Fs.fileFromPath(assetsDir),
                packageName);

        // Robolectric is already going to look in the  'app' dir ...
        // so no need to add to package name
        return manifest;
    }
}
