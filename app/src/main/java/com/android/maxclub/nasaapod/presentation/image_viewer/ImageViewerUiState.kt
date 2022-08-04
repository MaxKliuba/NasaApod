package com.android.maxclub.nasaapod.presentation.image_viewer

import android.graphics.Bitmap

sealed class ImageViewerUiState {
    data class UrlLoading(val url: String) : ImageViewerUiState()
    data class HdUrlLoading(val bitmap: Bitmap, val hdUrl: String) : ImageViewerUiState()
    data class Success(val bitmap: Bitmap, val isHd: Boolean) : ImageViewerUiState()
    object Error : ImageViewerUiState()
}