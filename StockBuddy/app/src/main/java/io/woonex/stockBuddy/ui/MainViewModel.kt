package io.woonex.stockBuddy.ui


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import io.woonex.stockBuddy.api.RedditApi
//import io.woonex.stockBuddy.api.RedditPost
//import io.woonex.stockBuddy.api.RedditPostRepository
import io.woonex.stockBuddy.databinding.ActionBarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var title = MutableLiveData<String>()
    private var actionBarBinding : ActionBarBinding? = null

    fun observeTitle(): LiveData<String> {
        return title
    }
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    /////////////////////////
    // Action bar
    fun initActionBarBinding(it: ActionBarBinding) {
        actionBarBinding = it
    }
    fun hideActionBarFavorites() {
        actionBarBinding?.actionFavorite?.visibility = View.GONE
    }
    fun showActionBarFavorites() {
        actionBarBinding?.actionFavorite?.visibility = View.VISIBLE
    }
}