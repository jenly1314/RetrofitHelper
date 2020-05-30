package com.king.retrofit.retrofithelper.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.await
import java.lang.Exception

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val liveDataStatus by lazy { MutableLiveData<Boolean>() }

    val liveData by lazy { MutableLiveData<String>() }

    fun getRequest1(){
        launch{
            val response  = Repository.getRequest1().await()
            liveData.value = response
        }
    }

    fun getRequest2(){
        launch{
            val response  = Repository.getRequest2().await()
            liveData.value = response
        }
    }

    fun getRequest3(){
        launch{
            val response  = Repository.getRequest3().await()
            liveData.value = response
        }
    }

    fun getRequest4(){
        launch{
            val response  = Repository.getRequest4().await()
            liveData.value = response
        }
    }


    private fun launch(block: suspend () -> Unit){
        launch(block,{
            it.printStackTrace()
            liveData.value = it.message
        })
    }

    private fun launch(block: suspend () -> Unit,error: suspend (Throwable) -> Unit) = viewModelScope.launch {
        try {
            liveDataStatus.value = true
            block()
        }catch (e: Exception){
            error(e)
        }
        liveDataStatus.value = false
    }

}