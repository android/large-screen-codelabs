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

package com.google.codelab.android.camera.foldables.step1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.codelab.android.camera.foldables.step1.camera2.CameraViewFinderActor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val actor: CameraViewFinderActor) : ViewModel() {

    var cameraId: String = "0"

    /**Codelab: Add variables for rear display mode **/

    fun sendSurfaceUpdate() = viewModelScope.launch {
        actor.sendSurfaceRequest(cameraId)
    }

    fun toggleCamera() = viewModelScope.launch {
        cameraId = if (cameraId == "0") {
            "1"
        } else {
            "0"
        }
        actor.changeCameraTo(cameraId)
    }

    fun capture() {
        //TO DO
    }

    fun toggleAspectRatio() {
        actor.toggleAspectRatio()
    }

    /**Codelab: Add functions for rear display mode **/
}