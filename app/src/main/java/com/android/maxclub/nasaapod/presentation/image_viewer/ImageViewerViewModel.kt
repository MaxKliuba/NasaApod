package com.android.maxclub.nasaapod.presentation.image_viewer

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.maxclub.nasaapod.data.util.ImageInfo
import com.android.maxclub.nasaapod.presentation.image_viewer.ImageViewerActivity.Companion.EXTRA_IMAGE_INFO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_BITMAP_SIZE = 100 * 1024 * 1024

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val imageInfo: ImageInfo? = savedStateHandle[EXTRA_IMAGE_INFO]

    private val _uiState = MutableStateFlow(
        imageInfo?.let { imageInfo ->
            ImageViewerUiState.UrlLoading(imageInfo.url)
        } ?: ImageViewerUiState.Error
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ImageViewerUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var bitmap: Bitmap? = null

    fun onEvent(event: ImageViewerEvent) {
        when (event) {
            is ImageViewerEvent.OnUrlLoaded -> {
                bitmap = event.bitmap
                _uiState.value = imageInfo?.hdUrl?.let { hdUrl ->
                    ImageViewerUiState.HdUrlLoading(event.bitmap, hdUrl)
                } ?: ImageViewerUiState.Success(event.bitmap, false)
            }
            is ImageViewerEvent.OnHdUrlLoaded -> {
                _uiState.value = if (event.bitmap.allocationByteCount < MAX_BITMAP_SIZE) {
                    ImageViewerUiState.Success(event.bitmap, true)
                } else {
                    bitmap?.let { bitmap ->
                        ImageViewerUiState.Success(bitmap, true)
                    } ?: ImageViewerUiState.Error
                }
                bitmap = event.bitmap
            }
            is ImageViewerEvent.OnUrlLoadingError -> {
                _uiState.value = ImageViewerUiState.Error
            }
            is ImageViewerEvent.OnHdUrlLoadingError -> {
                bitmap?.let { bitmap ->
                    _uiState.value = ImageViewerUiState.Success(bitmap, false)
                }
            }
            is ImageViewerEvent.OnSave -> {
                viewModelScope.launch {
                    bitmap.let { bitmap ->
                        if (bitmap != null && imageInfo != null) {
                            _uiEvent.emit(ImageViewerUiEvent.OnSave(bitmap, imageInfo))
                        } else {
                            _uiEvent.emit(ImageViewerUiEvent.OnShowSaveError)
                        }
                    }
                }
            }
        }
    }
}