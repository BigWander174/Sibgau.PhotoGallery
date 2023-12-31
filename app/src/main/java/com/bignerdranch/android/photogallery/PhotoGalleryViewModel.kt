package com.sample.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.bignerdranch.android.photogallery.FlickFetchr
import com.bignerdranch.android.photogallery.GalleryItem
import com.bignerdranch.android.photogallery.Item
import com.bignerdranch.android.photogallery.QueryPreferences

class PhotoGalleryViewModel(private val app: Application
) : AndroidViewModel(app) {
    private val galleryRepository = GalleryRepository.get()
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    val itemLiveData: LiveData<List<Item>> = galleryRepository.getPhotos()

    private val flickrFetchr = FlickFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
        galleryItemLiveData = mutableSearchTerm.switchMap { searchTerm ->
            if (searchTerm.isBlank()) {
                flickrFetchr.fetchPhotos()
            } else {
                flickrFetchr.searchPhotos(searchTerm)
            }
        }
    }
    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }
    fun showDatabaseGallery(){
        galleryRepository.getPhotos()
    }
    fun deletephotos(){
        galleryRepository.deleteAllPhotos()
    }

    fun addPhoto(photo: GalleryItem) {
        galleryRepository.addPhoto(photo)
    }
}
