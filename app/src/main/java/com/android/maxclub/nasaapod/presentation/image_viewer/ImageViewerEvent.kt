package com.android.maxclub.nasaapod.presentation.image_viewer

import android.graphics.Bitmap

sealed class ImageViewerEvent {
    data class OnUrlLoaded(val bitmap: Bitmap) : ImageViewerEvent()
    data class OnHdUrlLoaded(val bitmap: Bitmap) : ImageViewerEvent()
    object OnUrlLoadingError : ImageViewerEvent()
    object OnHdUrlLoadingError : ImageViewerEvent()
    object OnSave : ImageViewerEvent()
}