package org.windflume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.windflume.calibration.TrialResult;
import org.windflume.calibration.TrialStats;

class TrialStatsTest {

    @Test
    void tracksMaxMinMedianAndIgnoresInvalidTicks() {
        TrialStats s = new TrialStats();

        // TrialResult(sphereForce, testObjectForce, waterVelocity, testObjectCd)
        s.add(new TrialResult(1.0, 2.0, 0.4, 0.8));
        s.add(new TrialResult(1.5, 3.0, 0.6, 1.0));
        s.add(new TrialResult(1.2, 2.5, -1.0, -1.0));   // invalid tick

        assertEquals(0.6, s.getMaxVelocity(), 1e-9);
        assertEquals(0.4, s.getMinVelocity(), 1e-9);
        assertEquals(0.9, s.getMedianCd(), 1e-9);
        assertEquals(1.5, s.getMaxSphereForce(), 1e-9);
        assertEquals(3.0, s.getMaxTestObjectForce(), 1e-9);
        assertEquals(3, s.getDurationSeconds());
        assertEquals(-1.0, s.getCurrentVelocity(), 1e-9);
    }
}
