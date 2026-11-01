package li.gkd.app.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import li.gkd.db.ActionLog
import li.songe.codeorigin.nameOf

enum class RouteTransition {
    Standard,
    Editor,
}

fun RouteTransition.toMetadata(): Map<String, Any> =
    when (this) {
        RouteTransition.Standard -> emptyMap()
        RouteTransition.Editor -> GkNavigationTransitions.editor
    }

@Serializable
sealed interface AppRoute : NavKey {
    val transition: RouteTransition
        get() = RouteTransition.Standard
}

val AppRoute.logName: String
    get() =
        when (this) {
            A11YScopeAppListRoute -> nameOf<A11YScopeAppListRoute>()
            AutomationScopeAppListRoute -> nameOf<AutomationScopeAppListRoute>()
            is AppConfigRoute -> nameOf<AppConfigRoute>()
            BlockA11yAppListRoute -> nameOf<BlockA11yAppListRoute>()
            CrashReportRoute -> nameOf<CrashReportRoute>()
            EditBlockAppListRoute -> nameOf<EditBlockAppListRoute>()
            is ImagePreviewRoute -> nameOf<ImagePreviewRoute>()
            PrivilegeServiceRoute -> nameOf<PrivilegeServiceRoute>()
            is WebViewRoute -> nameOf<WebViewRoute>()
            ActionToastRoute -> nameOf<ActionToastRoute>()
            BlockA11ySetupRoute -> nameOf<BlockA11ySetupRoute>()
            HomeRoute -> nameOf<HomeRoute>()
            NotificationTextRoute -> nameOf<NotificationTextRoute>()
            A11yEventLogRoute -> nameOf<A11yEventLogRoute>()
            is ActionLogRoute -> nameOf<ActionLogRoute>()
            ActivityLogRoute -> nameOf<ActivityLogRoute>()
            AboutRoute -> nameOf<AboutRoute>()
            AdvancedPageRoute -> nameOf<AdvancedPageRoute>()
            DebugSettingsRoute -> nameOf<DebugSettingsRoute>()
            KeepAliveRoute -> nameOf<KeepAliveRoute>()
            MatchDebugRoute -> nameOf<MatchDebugRoute>()
            ExternalCallsRoute -> nameOf<ExternalCallsRoute>()
            QuickSettingsTilesRoute -> nameOf<QuickSettingsTilesRoute>()
            WorkModeRoute -> nameOf<WorkModeRoute>()
            SnapshotPageRoute -> nameOf<SnapshotPageRoute>()
            is SnapshotPreviewRoute -> nameOf<SnapshotPreviewRoute>()
            SnapshotSettingsRoute -> nameOf<SnapshotSettingsRoute>()
            is CategoryEditorRoute -> nameOf<CategoryEditorRoute>()
            is RuleExcludeEditorRoute -> nameOf<RuleExcludeEditorRoute>()
            is SubsAppGroupListRoute -> nameOf<SubsAppGroupListRoute>()
            is SubsAppListRoute -> nameOf<SubsAppListRoute>()
            is SubsCategoryGroupRoute -> nameOf<SubsCategoryGroupRoute>()
            is SubsCategoryRoute -> nameOf<SubsCategoryRoute>()
            is SubsGlobalGroupExcludeRoute -> nameOf<SubsGlobalGroupExcludeRoute>()
            is SubsGlobalGroupListRoute -> nameOf<SubsGlobalGroupListRoute>()
            is UpsertRuleGroupRoute -> nameOf<UpsertRuleGroupRoute>()
        }

@Serializable data object A11YScopeAppListRoute : AppRoute

@Serializable data object AutomationScopeAppListRoute : AppRoute

@Serializable
data class AppConfigRoute(
    val appId: String,
    val focusLog: ActionLog? = null,
) : AppRoute

@Serializable data object BlockA11yAppListRoute : AppRoute

@Serializable data object CrashReportRoute : AppRoute

@Serializable
data object EditBlockAppListRoute : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable
data class ImagePreviewRoute(
    val title: String? = null,
    val items: List<ImagePreviewItem> = emptyList(),
) : AppRoute

@Serializable data object PrivilegeServiceRoute : AppRoute

@Serializable data class WebViewRoute(val initUrl: String) : AppRoute

@Serializable
data object ActionToastRoute : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable
data object BlockA11ySetupRoute : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable data object HomeRoute : AppRoute

@Serializable
data object NotificationTextRoute : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable data object A11yEventLogRoute : AppRoute

@Serializable
data class ActionLogRoute(
    val subsId: Long? = null,
    val appId: String? = null,
) : AppRoute

@Serializable data object ActivityLogRoute : AppRoute

@Serializable data object AboutRoute : AppRoute

@Serializable data object AdvancedPageRoute : AppRoute

@Serializable data object DebugSettingsRoute : AppRoute

@Serializable data object KeepAliveRoute : AppRoute

@Serializable data object MatchDebugRoute : AppRoute

@Serializable data object ExternalCallsRoute : AppRoute

@Serializable data object QuickSettingsTilesRoute : AppRoute

@Serializable data object WorkModeRoute : AppRoute

@Serializable data object SnapshotPageRoute : AppRoute

@Serializable
data class SnapshotPreviewRoute(
    val snapshotId: Long,
    val snapshotIds: List<Long>,
) : AppRoute

@Serializable data object SnapshotSettingsRoute : AppRoute

@Serializable
data class CategoryEditorRoute(val subsId: Long, val categoryKey: Int? = null) : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable
data class RuleExcludeEditorRoute(
    val subsId: Long,
    val groupKey: Int,
    val appId: String? = null,
) : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable
data class SubsAppGroupListRoute(
    val subsItemId: Long,
    val appId: String,
    val focusGroupKey: Int? = null,
) : AppRoute

@Serializable data class SubsAppListRoute(val subsItemId: Long) : AppRoute

@Serializable data class SubsCategoryGroupRoute(val subsId: Long, val categoryKey: Int?) : AppRoute

@Serializable data class SubsCategoryRoute(val subsItemId: Long) : AppRoute

@Serializable
data class SubsGlobalGroupExcludeRoute(val subsItemId: Long, val groupKey: Int) : AppRoute

@Serializable
data class SubsGlobalGroupListRoute(val subsItemId: Long, val focusGroupKey: Int? = null) : AppRoute

@Serializable
data class UpsertRuleGroupRoute(
    val subsId: Long,
    val groupKey: Int? = null,
    val appId: String? = null,
    val forward: Boolean = false,
) : AppRoute {
    override val transition
        get() = RouteTransition.Editor
}

@Serializable
data class ImagePreviewItem(
    val uri: String,
    val title: String? = null,
    val titles: List<String> = emptyList(),
)
