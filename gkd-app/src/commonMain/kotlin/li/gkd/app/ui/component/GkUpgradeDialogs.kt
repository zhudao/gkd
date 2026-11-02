package li.gkd.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import li.gkd.app.error.displayMessage
import li.gkd.app.platform.PackageInstallCapability
import li.gkd.app.resources.*
import org.jetbrains.compose.resources.stringResource

sealed interface UpdateInstallState {
    data object Idle : UpdateInstallState

    data object Active : UpdateInstallState

    data object Success : UpdateInstallState

    data class Failure(val exception: Exception) : UpdateInstallState
}

sealed interface UpdateDownloadState {
    data class Loading(val progress: Float) : UpdateDownloadState

    data class Failure(val exception: Exception) : UpdateDownloadState

    data object Success : UpdateDownloadState
}

@Composable
fun GkUpgradeDialogs(
    text: String?,
    allowIgnore: Boolean,
    downloadStatus: UpdateDownloadState?,
    onDownload: () -> Unit,
    onDismissVersion: () -> Unit,
    onIgnore: () -> Unit,
    onCancelDownload: () -> Unit,
    onDismissDownload: () -> Unit,
    onInstall: () -> Unit,
    installCapability: PackageInstallCapability,
    installState: UpdateInstallState,
    downloadedVersion: String?,
    onInternalInstall: () -> Unit,
) {
    if (text != null) {
        val scrollState = rememberScrollState()
        GkAlertDialog(
            title = {
                Text(text = stringResource(Res.string.update_new_version))
            },
            text = {
                Text(
                    text = text,
                    modifier =
                        Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(scrollState),
                )
            },
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        onDownload()
                    },
                ) {
                    Text(text = stringResource(Res.string.update_download))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissVersion() }) {
                    Text(text = stringResource(Res.string.action_cancel))
                }
                if (allowIgnore) {
                    TextButton(
                        onClick = {
                            onIgnore()
                        },
                    ) {
                        Text(text = stringResource(Res.string.action_ignore))
                    }
                }
            },
        )
    }
    downloadStatus?.let { downloadStatusVal ->
        when (downloadStatusVal) {
            is UpdateDownloadState.Loading -> {
                GkAlertDialog(
                    title = { Text(text = stringResource(Res.string.image_downloading)) },
                    text = {
                        LinearProgressIndicator(
                            progress = { downloadStatusVal.progress },
                        )
                    },
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onCancelDownload()
                            },
                        ) {
                            Text(text = stringResource(Res.string.download_abort))
                        }
                    },
                )
            }

            is UpdateDownloadState.Failure -> {
                GkAlertDialog(
                    title = { Text(text = stringResource(Res.string.download_failed)) },
                    text = {
                        Text(
                            text =
                                downloadStatusVal.exception.let {
                                    it.message ?: it.toString()
                                },
                        )
                    },
                    onDismissRequest = { onDismissDownload() },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDismissDownload()
                            },
                        ) {
                            Text(text = stringResource(Res.string.action_close))
                        }
                    },
                )
            }

            UpdateDownloadState.Success -> {
                val installing = installState is UpdateInstallState.Active
                val installed = installState == UpdateInstallState.Success
                GkAlertDialog(
                    title = {
                        Text(
                            text =
                                stringResource(
                                    when (installState) {
                                        UpdateInstallState.Success -> Res.string.update_installed
                                        is UpdateInstallState.Failure ->
                                            Res.string.update_install_failed
                                        UpdateInstallState.Idle,
                                        UpdateInstallState.Active -> Res.string.download_complete
                                    },
                                ),
                        )
                    },
                    text = {
                        Column(
                            modifier =
                                Modifier.heightIn(max = 400.dp)
                                    .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                text =
                                    buildString {
                                        downloadedVersion?.let { append("v$it\n\n") }
                                        append(
                                            when (installState) {
                                                UpdateInstallState.Success ->
                                                    stringResource(
                                                        Res.string.update_installed_description,
                                                    )
                                                is UpdateInstallState.Failure ->
                                                    installState.exception.displayMessage()
                                                UpdateInstallState.Idle,
                                                UpdateInstallState.Active ->
                                                    stringResource(
                                                        Res.string.update_ready_to_install,
                                                    )
                                            },
                                        )
                                    },
                            )
                        }
                    },
                    onDismissRequest = { if (!installing) onDismissDownload() },
                    dismissButton = {
                        TextButton(
                            enabled = !installing,
                            onClick = {
                                onDismissDownload()
                            },
                        ) {
                            Text(text = stringResource(Res.string.action_close))
                        }
                    },
                    confirmButton = {
                        if (!installed) {
                            TextButton(onClick = onInstall, enabled = !installing) {
                                Text(
                                    text =
                                        stringResource(
                                            if (
                                                installCapability ==
                                                    PackageInstallCapability.Available
                                            )
                                                Res.string.update_system_install
                                            else Res.string.action_install,
                                        ),
                                )
                            }
                            if (installCapability == PackageInstallCapability.Available) {
                                TextButton(onClick = onInternalInstall, enabled = !installing) {
                                    Text(
                                        text =
                                            stringResource(
                                                if (installing) Res.string.update_installing
                                                else Res.string.action_install,
                                            ),
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}
