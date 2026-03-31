/*
 * Copyright (c) 2026 PRISM Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.features.login.impl.screens.onboarding.classic

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.prism.android.features.login.impl.BuildConfig
import io.prism.android.libraries.core.log.logger.LoggerTag
import io.prism.android.libraries.di.annotations.AppCoroutineScope
import io.prism.android.libraries.di.annotations.ApplicationContext
import io.prism.android.libraries.prism.api.core.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

interface PRISMClassicConnection {
    fun start()
    fun stop()
    fun requestData()
    val stateFlow: StateFlow<PRISMClassicConnectionState>
}

sealed interface PRISMClassicConnectionState {
    object Idle : PRISMClassicConnectionState
    object PRISMClassicNotFound : PRISMClassicConnectionState
    object PRISMClassicReadyNoSession : PRISMClassicConnectionState
    data class PRISMClassicReady(
        val userId: UserId,
        val secrets: String,
    ) : PRISMClassicConnectionState

    data class Error(val error: String) : PRISMClassicConnectionState
}

private val loggerTag = LoggerTag("ECConnection")

@ContributesBinding(AppScope::class)
class DefaultPRISMClassicConnection(
    @ApplicationContext
    private val context: Context,
    @AppCoroutineScope
    private val coroutineScope: CoroutineScope,
) : PRISMClassicConnection {
    // Messenger for communicating with the service.
    private var messenger: Messenger? = null

    // Target we publish for external service to send messages to IncomingHandler.
    private val incomingMessenger: Messenger = Messenger(IncomingHandler())

    // Flag indicating whether we have called bind on the service.
    private var bound: Boolean = false

    /**
     * Class for interacting with the main interface of the service.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Timber.tag(loggerTag.value).d("onServiceConnected")
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service. We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            messenger = Messenger(service)
            bound = true
            // Request the data as soon as possible
            requestData()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Timber.tag(loggerTag.value).d("onServiceDisconnected")
            // This is called when the connection with the service has been
            // unexpectedly disconnected&mdash;that is, its process crashed.
            messenger = null
            bound = false
        }
    }

    override fun start() {
        Timber.tag(loggerTag.value).w("start()")
        coroutineScope.launch {
            // Establish a connection with the service. We use an explicit
            // class name because there is no reason to be able to let other
            // applications replace our component.
            try {
                val intentService = Intent()
                intentService.setComponent(getPRISMClassicComponent())
                if (context.bindService(intentService, serviceConnection, BIND_AUTO_CREATE)) {
                    Timber.tag(loggerTag.value).d("Binding returned true")
                } else {
                    // This happen when the app is not installed
                    Timber.tag(loggerTag.value).d("Binding returned false")
                    mutableStateFlow.emit(PRISMClassicConnectionState.PRISMClassicNotFound)
                }
            } catch (e: SecurityException) {
                Timber.tag(loggerTag.value).e(e, "Can't bind to Service")
                mutableStateFlow.emit(PRISMClassicConnectionState.Error(e.localizedMessage.orEmpty()))
            }
        }
    }

    override fun stop() {
        Timber.tag(loggerTag.value).w("stop(): Unbinding (bound=$bound)")
        if (bound) {
            // Detach our existing connection.
            context.unbindService(serviceConnection)
            bound = false
        }
        coroutineScope.launch {
            mutableStateFlow.emit(PRISMClassicConnectionState.Idle)
        }
    }

    override fun requestData() {
        Timber.tag(loggerTag.value).w("requestData()")
        coroutineScope.launch {
            val finalMessenger = messenger
            if (finalMessenger == null) {
                Timber.tag(loggerTag.value).w("The messenger is null, can't request data")
                mutableStateFlow.emit(PRISMClassicConnectionState.Error("The messenger is null, can't request data"))
            } else {
                try {
                    // Get the data
                    val msg = Message.obtain(null, MSG_GET_DATA)
                    msg.replyTo = incomingMessenger
                    finalMessenger.send(msg)
                } catch (e: RemoteException) {
                    // In this case the service has crashed before we could even
                    // do anything with it; we can count on soon being
                    // disconnected (and then reconnected if it can be restarted)
                    // so there is no need to do anything here.
                    Timber.tag(loggerTag.value).e(e, "RemoteException")
                    mutableStateFlow.emit(PRISMClassicConnectionState.Error(e.localizedMessage.orEmpty()))
                }
            }
        }
    }

    private val mutableStateFlow = MutableStateFlow<PRISMClassicConnectionState>(PRISMClassicConnectionState.Idle)
    override val stateFlow = mutableStateFlow.asStateFlow()

    /**
     * Handler of incoming messages from service.
     */
    @Suppress("DEPRECATION")
    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Timber.tag(loggerTag.value).d("IncomingHandler handling message ${msg.what}")
            when (msg.what) {
                MSG_GET_DATA -> {
                    // The data must be extracted from the bundle before we launch the coroutine, else the bundle will be emptied
                    val state = msg.data.toPRISMClassicConnectionState()
                    emitPRISMClassicState(state)
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }

    private fun emitPRISMClassicState(state: PRISMClassicConnectionState) = coroutineScope.launch {
        when (state) {
            is PRISMClassicConnectionState.Error -> {
                Timber.tag(loggerTag.value).w("Received error from PRISM Classic: %s", state.error)
                mutableStateFlow.emit(state)
            }
            is PRISMClassicConnectionState.PRISMClassicReady -> {
                Timber.tag(loggerTag.value).d("Received userId from PRISM Classic: %s", state.userId)
                mutableStateFlow.emit(state)
            }
            PRISMClassicConnectionState.PRISMClassicReadyNoSession -> {
                Timber.tag(loggerTag.value).d("Received no session from PRISM Classic")
                mutableStateFlow.emit(state)
            }
            else -> {
                // Should not happen
                Timber.tag(loggerTag.value).w("Received unexpected state from PRISM Classic: %s", state)
                mutableStateFlow.emit(PRISMClassicConnectionState.Idle)
            }
        }
    }

    private fun getPRISMClassicComponent() = ComponentName(
        BuildConfig.prismClassicPackage,
        PRISM_CLASSIC_SERVICE_FULL_CLASS_NAME,
    )

    private fun Bundle?.toPRISMClassicConnectionState(): PRISMClassicConnectionState {
        return if (this == null) {
            PRISMClassicConnectionState.Error("No data received from PRISM Classic")
        } else {
            val error = getString(KEY_ERROR_STR)
            if (error != null) {
                PRISMClassicConnectionState.Error(error)
            } else {
                val userId = getString(KEY_USER_ID_STR)?.takeIf { it.isNotEmpty() }?.let(::UserId)
                if (userId != null) {
                    val secrets = getString(KEY_SECRETS_STR)?.takeIf { it.isNotEmpty() }
                    if (secrets == null) {
                        PRISMClassicConnectionState.Error("No secrets received from PRISM Classic")
                    } else {
                        PRISMClassicConnectionState.PRISMClassicReady(userId, secrets)
                    }
                } else {
                    PRISMClassicConnectionState.PRISMClassicReadyNoSession
                }
            }
        }
    }

    // Everything in this companion object must match what is defined in PRISM Classic
    private companion object {
        const val PRISM_CLASSIC_SERVICE_FULL_CLASS_NAME = "uk.fathertkt.prism.features.importer.ImporterService"

        // Command to the service to get the data.
        const val MSG_GET_DATA = 1

        // Keys for the bundle returned from the service
        const val KEY_ERROR_STR = "error"
        const val KEY_USER_ID_STR = "userId"

        /**
         * Key to extract the secrets from the bundle, as a Json string.
         * Json will have this format:
         * {
         *   "cross_signing" : {
         *     "master_key" : "z8RUxnaAGu___REDACTED___k+BQL9o",
         *     "user_signing_key" : "baJHzA___REDACTED___xMLbSUAXw9QUzqms",
         *     "self_signing_key" : "DU0CvLtR2G/___REDACTED___dV/MONNq4nsQhM"
         *   },
         *   "backup" : {
         *     "algorithm" : "m.megolm_backup.v1.curve25519-aes-sha2",
         *     "key" : "VzncmQ+UOV___REDACTED___patxDz7m0Nc",
         *     "backup_version" : "1"
         *   }
         * }
         */
        const val KEY_SECRETS_STR = "secrets"
    }
}
