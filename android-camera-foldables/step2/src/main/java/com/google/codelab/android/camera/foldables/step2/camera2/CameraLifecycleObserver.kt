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

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject


class CameraLifecycleObserver @Inject constructor(
    private val actor: CameraViewFinderActor,
    private val hostActivity: AppCompatActivity,
) : DefaultLifecycleObserver {

    private lateinit var onSurfaceUpdate: () -> Unit

    init {
        hostActivity.lifecycle.addObserver(this)
    }

    fun attachTo(onSurfaceUpdate: () -> Unit) {
        this.onSurfaceUpdate = onSurfaceUpdate
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        openCameraWithPermissionCheck()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        actor.refreshCameraThreading()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        actor.detach()
        actor.safelyCloseCamera()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        hostActivity.lifecycle.removeObserver(this)
        super.onDestroy(owner)
    }

    private fun openCameraWithPermissionCheck() {
        if (ContextCompat.checkSelfPermission(
                hostActivity, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            actor.viewFinder!!.post { onSurfaceUpdate() }
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private val cameraPermissionRequest by lazy {
        hostActivity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            run {
                if (result) {
                    onSurfaceUpdate()
                } else {
                    hostActivity.finish()
                }
            }
        }
    }
}