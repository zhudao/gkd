package li.gkd.app.priv

import li.gkd.app.logging.LogUtils
import priv.kit.core.Privilege
import priv.kit.core.PrivilegeConfig

object PrivilegeOwnerLifecycle {
    private const val FOLLOW_DEATH_DELAY_MILLIS = 10 * 60 * 1000L
    private const val APP_RESTART_PASSIVE_TIMEOUT_MILLIS = 5_000L
    private var automatorEnabled = false
    private var updateInstallationActive = false

    @Synchronized
    fun configure(enableAutomator: Boolean) {
        automatorEnabled = enableAutomator
        applyConfiguration()
    }

    @Synchronized
    fun setUpdateInstallationActive(active: Boolean) {
        updateInstallationActive = active
        applyConfiguration()
    }

    private fun applyConfiguration() {
        PrivilegeConfig.configure(
            followDeathDelayMillis =
                if (automatorEnabled || updateInstallationActive) FOLLOW_DEATH_DELAY_MILLIS else 0L,
            // Only the install-success callback may relaunch the app during an update.
            activeReconnectOnOwnerDeath = automatorEnabled && !updateInstallationActive,
        )
    }

    fun prepareAppRestart() {
        runCatching {
            Privilege.prepareOwnerRestart(APP_RESTART_PASSIVE_TIMEOUT_MILLIS)
        }
            .onFailure { error ->
                LogUtils.e("prepare owner restart failed", error)
            }
    }
}
