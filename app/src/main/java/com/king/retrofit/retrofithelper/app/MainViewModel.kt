package com.king.retrofit.retrofithelper.app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import java.lang.Exception
import kotlin.math.log

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class MainViewModel : ViewModel() {

    val liveDataStatus by lazy { MutableLiveData<Boolean>() }

    val liveData by lazy { MutableLiveData<String>() }

    fun getRequest1(){
        launch{
            val response = Repository.getRequest1().await()
            liveData.value = response
        }
    }

    fun getRequest2(){
        launch{
            val response = Repository.getRequest2().await()
            liveData.value = response
        }
    }

    fun getRequest3(){
        launch{
            val response = Repository.getRequest3().await()
            liveData.value = response
        }
    }

    fun getRequest4(){
        launch{
            val response = Repository.getRequest4().await()
            liveData.value = response
        }
    }

    fun getRequest5(){
        launch{
            val response = Repository.getRequest5().await()
            liveData.value = response
        }
    }

    fun download(){
        launch {
            val result = withContext(Dispatchers.IO){
                val response = Repository.download()
                val inputStream = response.execute().body()?.byteStream()
                inputStream?.let {
                    var buffer = ByteArray(1024)
                    while (it.read(buffer) != -1){

                    }
                    true
                } ?: false
            }
            Log.d(Constants.TAG, "result: $result")
        }
    }

    private fun launch(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            liveDataStatus.value = true
            block()
        }catch (e: Exception){
            e.printStackTrace()
            liveData.value = e.message
        }
        liveDataStatus.value = false
    }

}