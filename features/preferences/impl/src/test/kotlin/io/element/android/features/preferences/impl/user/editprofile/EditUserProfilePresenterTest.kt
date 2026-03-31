/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.preferences.impl.user.editprofile

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import io.prism.android.libraries.androidutils.file.TemporaryUriDeleter
import io.prism.android.libraries.architecture.AsyncAction
import io.prism.android.libraries.prism.api.PRISMClient
import io.prism.android.libraries.prism.api.user.PRISMUser
import io.prism.android.libraries.prism.test.AN_AVATAR_URL
import io.prism.android.libraries.prism.test.A_USER_ID
import io.prism.android.libraries.prism.test.FakePRISMClient
import io.prism.android.libraries.prism.ui.components.aPRISMUser
import io.prism.android.libraries.prism.ui.media.AvatarAction
import io.prism.android.libraries.mediapickers.test.FakePickerProvider
import io.prism.android.libraries.mediaupload.api.MediaUploadInfo
import io.prism.android.libraries.mediaupload.test.FakeMediaOptimizationConfigProvider
import io.prism.android.libraries.mediaupload.test.FakeMediaPreProcessor
import io.prism.android.libraries.permissions.api.PermissionsPresenter
import io.prism.android.libraries.permissions.test.FakePermissionsPresenter
import io.prism.android.libraries.permissions.test.FakePermissionsPresenterFactory
import io.prism.android.tests.testutils.WarmUpRule
import io.prism.android.tests.testutils.consumeItemsUntilPredicate
import io.prism.android.tests.testutils.consumeItemsUntilTimeout
import io.prism.android.tests.testutils.fake.FakeTemporaryUriDeleter
import io.prism.android.tests.testutils.lambda.lambdaRecorder
import io.prism.android.tests.testutils.lambda.value
import io.prism.android.tests.testutils.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class EditUserProfilePresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    private lateinit var fakePickerProvider: FakePickerProvider
    private lateinit var fakeMediaPreProcessor: FakeMediaPreProcessor

    private val userAvatarUri: Uri = mockk()
    private val anotherAvatarUri: Uri = mockk()

    @Before
    fun setup() {
        fakePickerProvider = FakePickerProvider()
        fakeMediaPreProcessor = FakeMediaPreProcessor()
        mockkStatic(Uri::class)

        every { Uri.parse(AN_AVATAR_URL) } returns userAvatarUri
        every { userAvatarUri.toString() } returns AN_AVATAR_URL
        every { Uri.parse(ANOTHER_AVATAR_URL) } returns anotherAvatarUri
        every { anotherAvatarUri.toString() } returns ANOTHER_AVATAR_URL
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun createEditUserProfilePresenter(
        prismClient: PRISMClient = FakePRISMClient(),
        navigator: EditUserProfileNavigator = FakeEditUserProfileNavigator(),
        prismUser: PRISMUser = aPRISMUser(),
        permissionsPresenter: PermissionsPresenter = FakePermissionsPresenter(),
        temporaryUriDeleter: TemporaryUriDeleter = FakeTemporaryUriDeleter(),
        mediaOptimizationConfigProvider: FakeMediaOptimizationConfigProvider = FakeMediaOptimizationConfigProvider(),
    ): EditUserProfilePresenter {
        return EditUserProfilePresenter(
            prismClient = prismClient,
            navigator = navigator,
            prismUser = prismUser,
            mediaPickerProvider = fakePickerProvider,
            mediaPreProcessor = fakeMediaPreProcessor,
            temporaryUriDeleter = temporaryUriDeleter,
            permissionsPresenterFactory = FakePermissionsPresenterFactory(permissionsPresenter),
            mediaOptimizationConfigProvider = mediaOptimizationConfigProvider,
        )
    }

    @Test
    fun `present - initial state is created from user info`() = runTest {
        val user = aPRISMUser(avatarUrl = AN_AVATAR_URL)
        val presenter = createEditUserProfilePresenter(prismUser = user)
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.userId).isEqualTo(user.userId)
            assertThat(initialState.displayName).isEqualTo(user.displayName)
            assertThat(initialState.userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            assertThat(initialState.avatarActions).containsExactly(
                AvatarAction.ChoosePhoto,
                AvatarAction.TakePhoto,
                AvatarAction.Remove
            )
            assertThat(initialState.saveButtonEnabled).isFalse()
            assertThat(initialState.saveAction).isInstanceOf(AsyncAction.Uninitialized::class.java)
        }
    }

    @Test
    fun `present - exit invokes the expected callback`() = runTest {
        val user = aPRISMUser(avatarUrl = AN_AVATAR_URL)
        val closeLambda = lambdaRecorder<Unit> {}
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            navigator = FakeEditUserProfileNavigator(closeLambda),
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(EditUserProfileEvent.Exit)
            closeLambda.assertions().isCalledOnce()
        }
    }

    @Test
    fun `present - exit without unsaved changes`() = runTest {
        val user = aPRISMUser(avatarUrl = AN_AVATAR_URL)
        val closeLambda = lambdaRecorder<Unit> {}
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            navigator = FakeEditUserProfileNavigator(closeLambda),
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("New name"))
            val withUpdatedName = awaitItem()
            withUpdatedName.eventSink(EditUserProfileEvent.Exit)
            val withConfirmation = awaitItem()
            assertThat(withConfirmation.saveAction).isEqualTo(AsyncAction.ConfirmingCancellation)
            // Cancel
            withConfirmation.eventSink(EditUserProfileEvent.CloseDialog)
            val afterCancel = awaitItem()
            assertThat(afterCancel.saveAction).isEqualTo(AsyncAction.Uninitialized)
            // Try again and confirm
            afterCancel.eventSink(EditUserProfileEvent.Exit)
            val withConfirmation2 = awaitItem()
            assertThat(withConfirmation2.saveAction).isEqualTo(AsyncAction.ConfirmingCancellation)
            closeLambda.assertions().isNeverCalled()
            withConfirmation2.eventSink(EditUserProfileEvent.Exit)
            // Dialog is closed
            val finalState = awaitItem()
            assertThat(finalState.saveAction).isEqualTo(AsyncAction.Uninitialized)
            closeLambda.assertions().isCalledOnce()
        }
    }

    @Test
    fun `present - updates state in response to changes`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = { assertThat(it).isEqualTo(userAvatarUri) }
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.displayName).isEqualTo("Name")
            assertThat(initialState.userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("Name II"))
            awaitItem().apply {
                assertThat(displayName).isEqualTo("Name II")
                assertThat(userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            }
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("Name III"))
            awaitItem().apply {
                assertThat(displayName).isEqualTo("Name III")
                assertThat(userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            }
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.Remove))
            awaitItem().apply {
                assertThat(displayName).isEqualTo("Name III")
                assertThat(userAvatarUrl).isNull()
            }
        }
    }

    @Test
    fun `present - obtains avatar uris from gallery`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        fakePickerProvider.givenResult(anotherAvatarUri)
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = { assertThat(it).isEqualTo(userAvatarUri) }
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.ChoosePhoto))
            awaitItem().apply {
                assertThat(userAvatarUrl).isEqualTo(ANOTHER_AVATAR_URL)
            }
        }
    }

    @Test
    fun `present - obtains avatar uris from camera`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        fakePickerProvider.givenResult(anotherAvatarUri)
        val fakePermissionsPresenter = FakePermissionsPresenter()
        val deleteCallback = lambdaRecorder<Uri?, Unit> {}
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            permissionsPresenter = fakePermissionsPresenter,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = deleteCallback,
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            assertThat(initialState.cameraPermissionState.permissionGranted).isFalse()
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.TakePhoto))
            val stateWithAskingPermission = awaitItem()
            assertThat(stateWithAskingPermission.cameraPermissionState.showDialog).isTrue()
            fakePermissionsPresenter.setPermissionGranted()
            val stateWithPermission = awaitItem()
            assertThat(stateWithPermission.cameraPermissionState.permissionGranted).isTrue()
            val stateWithNewAvatar = awaitItem()
            assertThat(stateWithNewAvatar.userAvatarUrl).isEqualTo(ANOTHER_AVATAR_URL)
            // Do it again, no permission is requested
            fakePickerProvider.givenResult(userAvatarUri)
            stateWithNewAvatar.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.TakePhoto))
            val stateWithNewAvatar2 = awaitItem()
            assertThat(stateWithNewAvatar2.userAvatarUrl).isEqualTo(AN_AVATAR_URL)
            deleteCallback.assertions().isCalledExactly(2).withSequence(
                listOf(value(userAvatarUri)),
                listOf(value(anotherAvatarUri)),
            )
        }
    }

    @Test
    fun `present - updates save button state`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        fakePickerProvider.givenResult(userAvatarUri)
        val deleteCallback = lambdaRecorder<Uri?, Unit> {}
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = deleteCallback
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.saveButtonEnabled).isFalse()
            // Once a change is made, the save button is enabled
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("Name II"))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isTrue()
            }
            // If it's reverted then the save disables again
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("Name"))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isFalse()
            }
            // Make a change...
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.Remove))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isTrue()
            }
            // Revert it...
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.ChoosePhoto))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isFalse()
            }
            deleteCallback.assertions().isCalledExactly(2).withSequence(
                listOf(value(userAvatarUri)),
                listOf(value(null)),
            )
        }
    }

    @Test
    fun `present - updates save button state when initial values are null`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = null)
        fakePickerProvider.givenResult(userAvatarUri)
        val deleteCallback = lambdaRecorder<Uri?, Unit> {}
        val presenter = createEditUserProfilePresenter(
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = deleteCallback
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            assertThat(initialState.saveButtonEnabled).isFalse()
            // Once a change is made, the save button is enabled
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("Name II"))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isTrue()
            }
            // If it's reverted then the save disables again
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("Name"))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isFalse()
            }
            // Make a change...
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.ChoosePhoto))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isTrue()
            }
            // Revert it...
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.Remove))
            awaitItem().apply {
                assertThat(saveButtonEnabled).isFalse()
            }
            deleteCallback.assertions().isCalledExactly(2).withSequence(
                listOf(value(null)),
                listOf(value(userAvatarUri)),
            )
        }
    }

    @Test
    fun `present - save changes room details if different`() = runTest {
        val prismClient = FakePRISMClient()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val presenter = createEditUserProfilePresenter(
            prismClient = prismClient,
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = { assertThat(it).isEqualTo(userAvatarUri) }
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("New name"))
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.Remove))
            initialState.eventSink(EditUserProfileEvent.Save)
            consumeItemsUntilPredicate { prismClient.setDisplayNameCalled && prismClient.removeAvatarCalled && !prismClient.uploadAvatarCalled }
            assertThat(prismClient.setDisplayNameCalled).isTrue()
            assertThat(prismClient.removeAvatarCalled).isTrue()
            assertThat(prismClient.uploadAvatarCalled).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - save does not change room details if they're the same trimmed`() = runTest {
        val prismClient = FakePRISMClient()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val presenter = createEditUserProfilePresenter(
            prismClient = prismClient,
            prismUser = user
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("   Name   "))
            initialState.eventSink(EditUserProfileEvent.Save)
            consumeItemsUntilTimeout()
            assertThat(prismClient.setDisplayNameCalled).isFalse()
            assertThat(prismClient.uploadAvatarCalled).isFalse()
            assertThat(prismClient.removeAvatarCalled).isFalse()
        }
    }

    @Test
    fun `present - save does not change name if it's now empty`() = runTest {
        val prismClient = FakePRISMClient()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val presenter = createEditUserProfilePresenter(
            prismClient = prismClient,
            prismUser = user
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(EditUserProfileEvent.UpdateDisplayName(""))
            initialState.eventSink(EditUserProfileEvent.Save)
            assertThat(prismClient.setDisplayNameCalled).isFalse()
            assertThat(prismClient.uploadAvatarCalled).isFalse()
            assertThat(prismClient.removeAvatarCalled).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `present - save processes and sets avatar when processor returns successfully`() = runTest {
        val prismClient = FakePRISMClient()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val tmpFile = givenPickerReturnsFile()
        val presenter = createEditUserProfilePresenter(
            prismClient = prismClient,
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = { assertThat(it).isEqualTo(userAvatarUri) }
            ),
        )
        try {
            presenter.test {
                val initialState = awaitItem()
                initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.ChoosePhoto))
                initialState.eventSink(EditUserProfileEvent.Save)
                consumeItemsUntilPredicate { prismClient.uploadAvatarCalled }
                assertThat(prismClient.uploadAvatarCalled).isTrue()
            }
        } finally {
            tmpFile.delete()
        }
    }

    @Test
    fun `present - save does not set avatar data if processor fails`() = runTest {
        val prismClient = FakePRISMClient()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val presenter = createEditUserProfilePresenter(
            prismClient = prismClient,
            prismUser = user,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = { assertThat(it).isEqualTo(userAvatarUri) }
            ),
        )
        fakePickerProvider.givenResult(anotherAvatarUri)
        fakeMediaPreProcessor.givenResult(Result.failure(RuntimeException("Oh no")))
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(EditUserProfileEvent.HandleAvatarAction(AvatarAction.ChoosePhoto))
            initialState.eventSink(EditUserProfileEvent.Save)
            skipItems(2)
            assertThat(prismClient.uploadAvatarCalled).isFalse()
            assertThat(awaitItem().saveAction).isInstanceOf(AsyncAction.Failure::class.java)
        }
    }

    @Test
    fun `present - sets save action to failure if name update fails`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val prismClient = FakePRISMClient().apply {
            givenSetDisplayNameResult(Result.failure(RuntimeException("!")))
        }
        saveAndAssertFailure(user, prismClient, EditUserProfileEvent.UpdateDisplayName("New name"))
    }

    @Test
    fun `present - sets save action to failure if removing avatar fails`() = runTest {
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val prismClient = FakePRISMClient().apply {
            givenRemoveAvatarResult(Result.failure(RuntimeException("!")))
        }
        saveAndAssertFailure(user, prismClient, EditUserProfileEvent.HandleAvatarAction(AvatarAction.Remove))
    }

    @Test
    fun `present - sets save action to failure if setting avatar fails`() = runTest {
        val tmpFile = givenPickerReturnsFile()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val prismClient = FakePRISMClient().apply {
            givenUploadAvatarResult(Result.failure(RuntimeException("!")))
        }
        try {
            saveAndAssertFailure(user, prismClient, EditUserProfileEvent.HandleAvatarAction(AvatarAction.ChoosePhoto))
        } finally {
            tmpFile.delete()
        }
    }

    @Test
    fun `present - CloseDialog resets save action state`() = runTest {
        val tmpFile = givenPickerReturnsFile()
        val user = aPRISMUser(id = A_USER_ID.value, displayName = "Name", avatarUrl = AN_AVATAR_URL)
        val prismClient = FakePRISMClient().apply {
            givenSetDisplayNameResult(Result.failure(RuntimeException("!")))
        }
        val presenter = createEditUserProfilePresenter(prismUser = user, prismClient = prismClient)
        try {
            presenter.test {
                val initialState = awaitItem()
                initialState.eventSink(EditUserProfileEvent.UpdateDisplayName("foo"))
                initialState.eventSink(EditUserProfileEvent.Save)
                skipItems(2)
                assertThat(awaitItem().saveAction).isInstanceOf(AsyncAction.Failure::class.java)
                initialState.eventSink(EditUserProfileEvent.CloseDialog)
                assertThat(awaitItem().saveAction).isInstanceOf(AsyncAction.Uninitialized::class.java)
            }
        } finally {
            tmpFile.delete()
        }
    }

    private suspend fun saveAndAssertFailure(prismUser: PRISMUser, prismClient: PRISMClient, event: EditUserProfileEvent) {
        val presenter = createEditUserProfilePresenter(
            prismUser = prismUser,
            prismClient = prismClient,
            temporaryUriDeleter = FakeTemporaryUriDeleter(
                deleteLambda = { assertThat(it).isEqualTo(userAvatarUri) }
            ),
        )
        presenter.test {
            val initialState = awaitItem()
            initialState.eventSink(event)
            initialState.eventSink(EditUserProfileEvent.Save)
            skipItems(1)
            assertThat(awaitItem().saveAction).isInstanceOf(AsyncAction.Loading::class.java)
            assertThat(awaitItem().saveAction).isInstanceOf(AsyncAction.Failure::class.java)
        }
    }

    private fun givenPickerReturnsFile(): File {
        val file = File.createTempFile("test", "jpg")
        fakePickerProvider.givenResult(anotherAvatarUri)
        fakeMediaPreProcessor.givenResult(
            Result.success(
                MediaUploadInfo.AnyFile(
                    file = file,
                    fileInfo = mockk(),
                )
            )
        )
        return file
    }

    companion object {
        private const val ANOTHER_AVATAR_URL = "example://camera/foo.jpg"
    }
}
