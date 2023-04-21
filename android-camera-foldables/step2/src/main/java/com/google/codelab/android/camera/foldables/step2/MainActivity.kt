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

package com.google.codelab.android.camera.foldables.step2

import android.hardware.camera2.*
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.window.area.WindowAreaController
import androidx.window.area.WindowAreaSession
import androidx.window.area.WindowAreaSessionCallback
import androidx.window.core.ExperimentalWindowApi
import androidx.window.java.area.WindowAreaControllerJavaAdapter
import com.google.codelab.android.camera.foldables.databinding.ActivityMainBinding
import com.google.codelab.android.camera.foldables.step2.camera2.CameraLifecycleObserver
import com.google.codelab.android.camera.foldables.step2.camera2.CameraViewFinderActor
import com.google.codelab.android.camera.foldables.step2.utils.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject

@OptIn(ExperimentalWindowApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), WindowAreaSessionCallback {


    private lateinit var binding: ActivityMainBinding

    private val viewModel: CameraViewModel by viewModels()

    @Inject
    lateinit var cameraLifecycleObserver: CameraLifecycleObserver

    @Inject
    lateinit var foldingStateActor: FoldingStateActor

    @Inject
    lateinit var actor: CameraViewFinderActor

    /**Codelab: Add variables for rear display mode **/
    private lateinit var windowAreaController: WindowAreaControllerJavaAdapter
    private lateinit var displayExecutor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actor.attachTo(binding.viewFinder)
        cameraLifecycleObserver.attachTo { run { viewModel.sendSurfaceUpdate() } }

        val windowParams: WindowManager.LayoutParams = window.attributes
        windowParams.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT
        window.attributes = windowParams

        binding.toggle.setOnClickListener { viewModel.toggleCamera() }
        binding.capture.setOnClickListener { viewModel.capture() }
        binding.aspectRatio.setOnClickListener { viewModel.toggleAspectRatio() }
        binding.rearDisplay.setOnClickListener { startRearDisplayMode() }

        /**Codelab: initialize WindowInfoTracker**/
        displayExecutor = ContextCompat.getMainExecutor(this)
        windowAreaController = WindowAreaControllerJavaAdapter(WindowAreaController.getOrCreate())
    }

    override fun onResume() {
        super.onResume()

        /**Codelab: start tracking window layout info **/
        lifecycleScope.launch {
            foldingStateActor.checkFoldingState(this@MainActivity, binding.viewFinder)
        }
    }

    /** Codelab: add the code to manage rear display sessions **/

    private fun startRearDisplayMode() {
        if (viewModel.rearDisplaySession != null) {
            viewModel.rearDisplaySession?.close()
        } else {
            windowAreaController.startRearDisplayModeSession(
                this,
                displayExecutor,
                this
            )
        }
    }

    override fun onSessionEnded() {
        viewModel.updateSession(null)
        Log.d("rear session onSessionEnded")
    }

    override fun onSessionStarted(session: WindowAreaSession) {
        viewModel.updateSession(session)
        Log.d("rear session onSessionStarted [session=${viewModel.rearDisplaySession}]")
    }
    //endregion
}
