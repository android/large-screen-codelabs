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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.area.WindowAreaCapability
import androidx.window.area.WindowAreaController
import androidx.window.area.WindowAreaInfo
import androidx.window.area.WindowAreaSession
import androidx.window.area.WindowAreaSessionCallback
import androidx.window.core.ExperimentalWindowApi
import com.google.codelab.android.camera.foldables.databinding.ActivityMainBinding
import com.google.codelab.android.camera.foldables.step2.camera2.CameraLifecycleObserver
import com.google.codelab.android.camera.foldables.step2.camera2.CameraViewFinderActor
import com.google.codelab.android.camera.foldables.step2.utils.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
    private lateinit var windowAreaController: WindowAreaController
    private lateinit var displayExecutor: Executor
    private var rearDisplaySession: WindowAreaSession? = null
    private var rearDisplayWindowAreaInfo: WindowAreaInfo? = null
    private var rearDisplayStatus: WindowAreaCapability.Status =
        WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED
    private val rearDisplayOperation = WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA

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
        binding.rearDisplay.setOnClickListener { toggleRearDisplayMode() }

        /**Codelab: initialize WindowAreaController*/
        displayExecutor = ContextCompat.getMainExecutor(this)
        windowAreaController = WindowAreaController.getOrCreate()

        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowAreaController.windowAreaInfos
                    .map { info -> info.firstOrNull { it.type == WindowAreaInfo.Type.TYPE_REAR_FACING } }
                    .onEach { info -> rearDisplayWindowAreaInfo = info }
                    .map { it?.getCapability(rearDisplayOperation)?.status ?: WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED }
                    .distinctUntilChanged()
                    .collect {
                        rearDisplayStatus = it
                        updateUI()
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /**Codelab: start tracking window layout info **/
        lifecycleScope.launch {
            foldingStateActor.checkFoldingState(this@MainActivity, binding.viewFinder)
        }
    }

    /** Codelab: add the code to manage rear display sessions **/
    private fun toggleRearDisplayMode() {
        if(rearDisplayStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE) {
            if(rearDisplaySession == null) {
                rearDisplaySession = rearDisplayWindowAreaInfo?.getActiveSession(
                    rearDisplayOperation
                )
            }
            rearDisplaySession?.close()
        } else {
            rearDisplayWindowAreaInfo?.token?.let { token ->
                windowAreaController.transferActivityToWindowArea(
                    token = token,
                    activity = this,
                    executor = displayExecutor,
                    windowAreaSessionCallback = this
                )
            }
        }
    }

    private fun updateUI() {
        if(rearDisplaySession != null) {
            binding.rearDisplay.isEnabled = true
            // A session is already active, clicking on the button will disable it
        } else {
            when(rearDisplayStatus) {
                WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED -> {
                    binding.rearDisplay.isEnabled = false
                    // RearDisplay Mode is not supported on this device"
                }
                WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNAVAILABLE -> {
                    binding.rearDisplay.isEnabled = false
                    // RearDisplay Mode is not currently available
                }
                WindowAreaCapability.Status.WINDOW_AREA_STATUS_AVAILABLE -> {
                    binding.rearDisplay.isEnabled = true
                    // You can enable RearDisplay Mode
                }
                WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE -> {
                    binding.rearDisplay.isEnabled = true
                    // You can disable RearDisplay Mode
                }
                else -> {
                    binding.rearDisplay.isEnabled = false
                    // RearDisplay status is unknown
                }
            }
        }
    }

    override fun onSessionEnded(t: Throwable?) {
        if(t != null) {
            Log.d("Something was broken: ${t.message}")
        }else{
            Log.d("rear session ended")
        }
    }

    override fun onSessionStarted(session: WindowAreaSession) {
        Log.d("rear session started [session=$session]")
    }
    //endregion
}
