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

package com.google.codelab.android.camera.foldables.step1.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    fun provideCameraService(context: Application) =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
}

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideAppCompatActivity(activity: Activity) =
        activity as AppCompatActivity
}