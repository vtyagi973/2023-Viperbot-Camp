package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class FalconBulkRead {
    private static final String TAG = "FalconBulkRead";
    private List<LynxModule> allHubs = null;
    private LynxModule.BulkCachingMode cachingMode = LynxModule.BulkCachingMode.OFF;

    public FalconBulkRead(HardwareMap hardwareMap) {
        FalconLogger.enter();
        // Get access to a list of Expansion Hub Modules to enable changing caching methods.
        if (hardwareMap != null) {
            allHubs = hardwareMap.getAll(LynxModule.class);
            setCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        FalconLogger.exit();
    }

    public LynxModule.BulkCachingMode getCachingMode() {
        return cachingMode;
    }

    public void setCachingMode(LynxModule.BulkCachingMode cachingMode) {
        FalconLogger.enter();
        if (allHubs != null && this.cachingMode != cachingMode) {
            for (LynxModule module : allHubs) {
                module.setBulkCachingMode(cachingMode);
            }

            this.cachingMode = cachingMode;
        }

        FalconLogger.exit();
    }

    public void clearBulkCache() {
        FalconLogger.enter();
        if (allHubs != null && this.cachingMode == LynxModule.BulkCachingMode.MANUAL) {
            for (LynxModule module : allHubs) {
                module.clearBulkCache();
            }
        }

        FalconLogger.exit();
    }
}

