/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.user.editprofile

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.prism.android.libraries.androidutils.file.TemporaryUriDeleter
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.architecture.Presenter
import io.prism.android.libraries.architecture.runCatchingUpdatingState
import io.prism.android.libraries.core.extensions.runCatchingExceptions
import io.prism.android.libraries.core.mimetype.MimeTypes
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.ui.media.AvatarAction
import io.prism.android.libraries.mediapickers.api.PickerProvider
import io.prism.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.prism.android.libraries.mediaupload.api.MediaPreProcessor
import io.prism.android.libraries.permissions.api.PermissionsEvent
import io.prism.android.libraries.permissions.api.PermissionsPresenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class EditUserProfilePresenter(
    @Assisted private val prismUser: PRISMUser,
    @Assisted private val navigator: EditUserProfileNavigator,
    private val prismClient: PRISMClient,
    private val mediaPickerProvider: PickerProvider,
    private val mediaPreProcessor: MediaPreProcessor,
    private val temporaryUriDeleter: TemporaryUriDeleter,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
    permissionsPresenterFactory: PermissionsPresenter.Factory,
) : Presenter<EditUserProfileState> {
    private val cameraPermissionPresenter: PermissionsPresenter = permissionsPresenterFactory.create(android.Manifest.permission.CAMERA)
    private var pendingPermissionRequest = false

    @AssistedFactory
    interface Factory {
        fun create(
            prismUser: PRISMUser,
            navigator: EditUserProfileNavigator,
        ): EditUserProfilePresenter
    }

    @Composable
    override fun present(): EditUserProfileState {
        val cameraPermissionState = cameraPermissionPresenter.present()
        var userAvatarUri by rememberSaveable { mutableStateOf(prismUser.avatarUrl) }
        var userDisplayName by rememberSaveable { mutableStateOf(prismUser.displayName) }
        val cameraPhotoPicker = mediaPickerProvider.registerCameraPhotoPicker(
            onResult = { uri ->
                if (uri != null) {
                    temporaryUriDeleter.delete(userAvatarUri?.toUri())
                    userAvatarUri = uri.toString()
                }
            }
        )
        val galleryImagePicker = mediaPickerProvider.registerGalleryImagePicker(
            onResult = { uri ->
                if (uri != null) {
                    temporaryUriDeleter.delete(userAvatarUri?.toUri())
                    userAvatarUri = uri.toString()
                }
            }
        )

        val avatarActions by remember(userAvatarUri) {
            derivedStateOf {
                listOfNotNull(
                    AvatarAction.TakePhoto,
                    AvatarAction.ChoosePhoto,
                    AvatarAction.Remove.takeIf { userAvatarUri != null },
                ).toImmutableList()
            }
        }

        LaunchedEffect(cameraPermissionState.permissionGranted) {
            if (cameraPermissionState.permissionGranted && pendingPermissionRequest) {
                pendingPermissionRequest = false
                cameraPhotoPicker.launch()
            }
        }

        val saveAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val localCoroutineScope = rememberCoroutineScope()

        val canSave = remember(userDisplayName, userAvatarUri) {
            val hasProfileChanged = hasDisplayNameChanged(userDisplayName, prismUser) ||
                hasAvatarUrlChanged(userAvatarUri, prismUser)
            !userDisplayName.isNullOrBlank() && hasProfileChanged
        }

        fun handleEvent(event: EditUserProfileEvent) {
            when (event) {
                is EditUserProfileEvent.Save -> localCoroutineScope.saveChanges(
                    name = userDisplayName,
                    avatarUri = userAvatarUri?.toUri(),
                    currentUser = prismUser,
                    action = saveAction,
                )
                is EditUserProfileEvent.HandleAvatarAction -> {
                    when (event.action) {
                        AvatarAction.ChoosePhoto -> galleryImagePicker.launch()
                        AvatarAction.TakePhoto -> if (cameraPermissionState.permissionGranted) {
                            cameraPhotoPicker.launch()
                        } else {
                            pendingPermissionRequest = true
                            cameraPermissionState.eventSink(PermissionsEvent.RequestPermissions)
                        }
                        AvatarAction.Remove -> {
                            temporaryUriDeleter.delete(userAvatarUri?.toUri())
                            userAvatarUri = null
                        }
                    }
                }
                is EditUserProfileEvent.UpdateDisplayName -> userDisplayName = event.name
                EditUserProfileEvent.Exit -> {
                    when (saveAction.value) {
                        is AsyncAction.Confirming -> {
                            // Close the dialog right now
                            saveAction.value = AsyncAction.Uninitialized
                            navigator.close()
                        }
                        AsyncAction.Loading -> Unit
                        is AsyncAction.Failure,
                        is AsyncAction.Success -> {
                            // Should not happen
                        }
                        AsyncAction.Uninitialized -> {
                            if (canSave) {
                                saveAction.value = AsyncAction.ConfirmingCancellation
                            } else {
                                navigator.close()
                            }
                        }
                    }
                }
                EditUserProfileEvent.CloseDialog -> saveAction.value = AsyncAction.Uninitialized
            }
        }

        return EditUserProfileState(
            userId = prismUser.userId,
            displayName = userDisplayName.orEmpty(),
            userAvatarUrl = userAvatarUri,
            avatarActions = avatarActions,
            saveButtonEnabled = canSave && saveAction.value !is AsyncAction.Loading,
            saveAction = saveAction.value,
            cameraPermissionState = cameraPermissionState,
            eventSink = ::handleEvent,
        )
    }

    private fun hasDisplayNameChanged(name: String?, currentUser: PRISMUser) =
        name?.trim() != currentUser.displayName?.trim()

    private fun hasAvatarUrlChanged(avatarUri: String?, currentUser: PRISMUser) =
        avatarUri?.trim() != currentUser.avatarUrl?.trim()

    private fun CoroutineScope.saveChanges(
        name: String?,
        avatarUri: Uri?,
        currentUser: PRISMUser,
        action: MutableState<AsyncAction<Unit>>,
    ) = launch {
        val results = mutableListOf<Result<Unit>>()
        suspend {
            if (!name.isNullOrEmpty() && name.trim() != currentUser.displayName.orEmpty().trim()) {
                results.add(prismClient.setDisplayName(name).onFailure {
                    Timber.e(it, "Failed to set user's display name")
                })
            }
            if (avatarUri?.toString()?.trim() != currentUser.avatarUrl?.trim()) {
                results.add(updateAvatar(avatarUri).onFailure {
                    Timber.e(it, "Failed to update user's avatar")
                })
            }
            if (results.all { it.isSuccess }) Unit else results.first { it.isFailure }.getOrThrow()
        }.runCatchingUpdatingState(action)
    }

    private suspend fun updateAvatar(avatarUri: Uri?): Result<Unit> {
        return runCatchingExceptions {
            if (avatarUri != null) {
                val preprocessed = mediaPreProcessor.process(
                    uri = avatarUri,
                    mimeType = MimeTypes.Jpeg,
                    deleteOriginal = false,
                    mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
                ).getOrThrow()
                prismClient.uploadAvatar(MimeTypes.Jpeg, preprocessed.file.readBytes()).getOrThrow()
            } else {
                prismClient.removeAvatar().getOrThrow()
            }
        }.onFailure { Timber.e(it, "Unable to update avatar") }
    }
}
