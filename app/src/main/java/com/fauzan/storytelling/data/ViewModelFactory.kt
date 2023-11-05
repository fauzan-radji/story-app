package com.fauzan.storytelling.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fauzan.storytelling.di.Injection
import com.fauzan.storytelling.ui.MainViewModel
import com.fauzan.storytelling.ui.add.AddViewModel
import com.fauzan.storytelling.ui.detail.DetailViewModel
import com.fauzan.storytelling.ui.home.HomeViewModel
import com.fauzan.storytelling.ui.login.LoginViewModel
import com.fauzan.storytelling.ui.maps.MapsViewModel
import com.fauzan.storytelling.ui.register.RegisterViewModel

class ViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(repository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(repository) as T
            modelClass.isAssignableFrom(AddViewModel::class.java) -> AddViewModel(repository) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(repository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context)).also { instance = it }
            }
        }
    }
}