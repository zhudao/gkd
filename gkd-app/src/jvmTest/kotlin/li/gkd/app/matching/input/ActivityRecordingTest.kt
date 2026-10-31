package li.gkd.app.matching.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ActivityRecordingTest {
    private val launcher = ForegroundIdentity("launcher", "Home")
    private val app = ForegroundIdentity("gkd", "Main")
    private val systemUi = ForegroundIdentity("com.android.systemui")
    private val policy = MatchTimingPolicy()

    @Test
    fun locallyStoppedServiceRecordsTasksRegardlessOfShadeWithoutConfirmingNodes() {
        // Regression from diagnostic 34223533: the last nodes were SystemUI when
        // scoped automation stopped; subsequent launcher/GKD tasks never got nodes.
        val tracker = ForegroundTracker(systemUi)
        tracker.observeSurface(true, 0)
        val belowShade = tracker.observe(ForegroundSource.TaskStack, launcher, policy, 1)
        assertEquals(launcher, belowShade.recordedActivity(nodesAvailable = false))
        assertEquals(launcher, tracker.observeSurface(false, 2).recordedActivity(false))
        val opened = tracker.observe(ForegroundSource.TaskStack, app, policy, 3)
        assertEquals(app, opened.recordedActivity(false))
        assertEquals(systemUi, opened.confirmed)
        assertNotNull(opened.pending)
        assertEquals(app, tracker.observeSurface(true, 4).recordedActivity(false))
        assertEquals(app, tracker.observeSurface(false, 5).recordedActivity(false))
        assertEquals(app, tracker.snapshot().recordedActivity(false))
        tracker.observeSurface(true, 6)
        val switchedBelowShade = tracker.observe(ForegroundSource.TaskStack, launcher, policy, 7)
        assertEquals(launcher, switchedBelowShade.recordedActivity(false))
        assertEquals(launcher, tracker.observeSurface(false, 8).recordedActivity(false))
    }

    @Test
    fun stoppingAndRestartingNodeServiceDoesNotAuthorizePendingTask() {
        val tracker = ForegroundTracker(systemUi)
        tracker.observeSurface(false, 0)
        val pending = tracker.observe(ForegroundSource.TaskStack, launcher, policy, 1)
        assertEquals(launcher, pending.recordedActivity(false))
        assertEquals(systemUi, pending.recordedActivity(true))
        val confirmed = tracker.observe(ForegroundSource.Nodes, launcher, policy, 2)
        assertEquals(launcher, confirmed.recordedActivity(true))
        assertNull(confirmed.pending)
    }

    @Test
    fun absentSurfaceListenerStillRecordsTasksButNeverInventsOneFromOldNodes() {
        val tracker = ForegroundTracker(launcher)
        assertNull(tracker.snapshot().recordedActivity(false))
        tracker.observe(ForegroundSource.TaskStack, launcher, policy, 0)
        tracker.observeSurface(true, 1)
        val unavailable = tracker.observeSurface(null, 2)
        assertEquals(launcher, unavailable.recordedActivity(false))
        assertEquals(launcher, unavailable.recordedActivity(true))
    }
}
