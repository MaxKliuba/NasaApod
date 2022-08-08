package com.android.maxclub.nasaapod.presentation.image_viewer

import android.graphics.Bitmap
import com.android.maxclub.nasaapod.domain.model.ImageInfo

sealed class ImageViewerUiEvent {
    data class OnSave(val bitmap: Bitmap, val imageInfo: ImageInfo) : ImageViewerUiEvent()
    object OnShowSaveError : ImageViewerUiEvent()
}