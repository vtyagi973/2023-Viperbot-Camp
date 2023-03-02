package org.firstinspires.ftc.teamcode.viperCamp.core;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class ViperBulkRead {
    private static final String TAG = "ViperBulkRead";
    private List<LynxModule> allHubs = null;
    private LynxModule.BulkCachingMode cachingMode = LynxModule.BulkCachingMode.OFF;

    public ViperBulkRead(HardwareMap hardwareMap) {
        ViperLogger.enter();
        // Get access to a list of Expansion Hub Modules to enable changing caching methods.
        if (hardwareMap != null) {
            allHubs = hardwareMap.getAll(LynxModule.class);
            setCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        ViperLogger.exit();
    }

    public LynxModule.BulkCachingMode getCachingMode() {
        return cachingMode;
    }

    public void setCachingMode(LynxModule.BulkCachingMode cachingMode) {
        ViperLogger.enter();
        if (allHubs != null && this.cachingMode != cachingMode) {
            for (LynxModule module : allHubs) {
                module.setBulkCachingMode(cachingMode);
            }

            this.cachingMode = cachingMode;
        }

        ViperLogger.exit();
    }

    public void clearBulkCache() {
        ViperLogger.enter();
        if (allHubs != null && this.cachingMode == LynxModule.BulkCachingMode.MANUAL) {
            for (LynxModule module : allHubs) {
                module.clearBulkCache();
            }
        }

        ViperLogger.exit();
    }
}

