/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codelab.android.camera.foldables.step2.camera2

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.camera.viewfinder.CameraViewfinder
import androidx.camera.viewfinder.ViewfinderSurfaceRequest
import androidx.camera.viewfinder.populateFromCharacteristics
import com.google.codelab.android.camera.foldables.step2.utils.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Semaphore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class CameraViewFinderActor @Inject constructor(
    private val cameraManager: CameraManager
) {

    private var cameraViewfinder: CameraViewfinder? = null
    private lateinit var characteristics: CameraCharacteristics
    private var viewfinderSurfaceRequest: ViewfinderSurfaceRequest? = null
    private lateinit var session: CameraCaptureSession
    private lateinit var camera: CameraDevice

    private lateinit var cameraThread: HandlerThread
    private lateinit var cameraHandler: Handler

    private val cameraOpenCloseLock = Semaphore(1)

    internal val viewFinder
        get() = cameraViewfinder

    fun attachTo(cameraViewfinder: CameraViewfinder) {
        this.cameraViewfinder = cameraViewfinder
    }

    fun detach() {
        cameraViewfinder = null
    }

    fun refreshCameraThreading() {
        cameraThread = HandlerThread(HANDLER_THREAD_NAME).apply { start() }
        cameraHandler = Handler(cameraThread.looper)
    }

    fun safelyCloseCamera() {
        closeCamera()
        cameraThread.quitSafely()
        viewfinderSurfaceRequest?.markSurfaceSafeToRelease()
    }

    suspend fun changeCameraTo(cameraId: String) {
        closeCamera()
        sendSurfaceRequest(cameraId)
    }

    fun toggleAspectRatio() {
        checkNotNull(cameraViewfinder)

        if (cameraViewfinder!!.scaleType == CameraViewfinder.ScaleType.FIT_CENTER) {
            cameraViewfinder!!.scaleType = CameraViewfinder.ScaleType.FILL_CENTER
        } else {
            cameraViewfinder!!.scaleType = CameraViewfinder.ScaleType.FIT_CENTER
        }

    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            if (::session.isInitialized) {
                session.close()
            }

            if (::camera.isInitialized) {
                camera.close()
            }
        } catch (t: Throwable) {
            Log.e("Error closing camera", t)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    suspend fun sendSurfaceRequest(cameraId: String) {
        checkNotNull(cameraViewfinder)

        setUpCameraOutputs(cameraId)?.let {
            val surface = cameraViewfinder?.requestSurfaceAsync(it)?.await()
            if (surface != null) {
                cameraViewfinder!!.scaleType = CameraViewfinder.ScaleType.FIT_CENTER
                initializeCamera(surface, cameraId)
            } else {
                Log.e("Surface request error")
            }
        }
    }

    private fun setUpCameraOutputs(cameraId: String): ViewfinderSurfaceRequest? {
        try {
            characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
            ) ?: return null
            val largest = Collections.max(
                listOf(*map.getOutputSizes(ImageFormat.JPEG)),
                compareBy { it.height * it.width }
            )
            viewfinderSurfaceRequest = ViewfinderSurfaceRequest.Builder(largest)
                .populateFromCharacteristics(characteristics)
                .build()
            return viewfinderSurfaceRequest

        } catch (e: CameraAccessException) {
            Log.e(e.toString())
        }
        return null
    }

    private suspend fun initializeCamera(surface: Surface, cameraId: String) =
        withContext(Dispatchers.IO) {
            cameraOpenCloseLock.acquire()
            camera = openCamera(cameraManager, cameraId, cameraHandler)
            val targets = listOf(surface)

            try {
                session = createCaptureSession(camera, targets, cameraHandler)
                val captureRequest = camera.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW
                ).apply { addTarget(surface) }
                session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
            } catch (e: Exception) {
                Log.e("createCaptureSession exception: ${e.message}", e)
            }
        }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        try {
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    cameraOpenCloseLock.release()
                    cont.resume(device)
                }

                override fun onDisconnected(device: CameraDevice) {
                    Log.w("Camera $cameraId has been disconnected")
                    cameraOpenCloseLock.release()
                }

                override fun onError(device: CameraDevice, error: Int) {
                    Log.e("Camera $cameraId error: ($error)")
                }
            }, handler)
        } catch (e: Exception) {
            Log.e("openCamera exception: ${e.message}")
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)
            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    companion object {
        private const val HANDLER_THREAD_NAME = "HANDLER_THREAD"
    }
}