package com.ahmed.myflickr.viewmodels

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.myflickr.repository.FlickrRepository
import com.ahmed.myflickr.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class FlickrViewModel @Inject constructor(private val repository: FlickrRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FlickrUiState())
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        observeSearchQuery()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    private fun observeSearchQuery() {
        _query
            .debounce(300L)
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .flatMapLatest { query ->
                repository.searchImages(query)
                    .onStart { emit(Resource.Loading) }
            }
            .onEach { result ->
                _uiState.value = when (result) {
                    is Resource.Success -> FlickrUiState(images = result.data, isLoading = false)
                    is Resource.Error -> FlickrUiState(isLoading = false) // could show error here
                    is Resource.Loading -> FlickrUiState(isLoading = true)
                }
            }
            .launchIn(viewModelScope)
    }
}

@Parcelize
data class FlickrImage(
    val title: String,
    val description: String,
    val author: String,
    val published: String,
    val media: Media
) : Parcelable

@Parcelize
data class Media(val m: String) : Parcelable

data class FlickrUiState(
    val images: List<FlickrImage> = emptyList(),
    val isLoading: Boolean = false,
    val selectedImage: FlickrImage? = null
)