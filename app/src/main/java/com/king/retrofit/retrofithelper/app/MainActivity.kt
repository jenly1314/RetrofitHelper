package com.king.retrofit.retrofithelper.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.king.retrofit.retrofithelper.RetrofitHelper
import com.king.retrofit.retrofithelper.app.databinding.ActivityMainBinding
import com.king.retrofit.retrofithelper.listener.ProgressListener
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val loadingFragment by lazy { LoadingFragment() }

    private val viewModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MainViewModel::class.java) }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    
    private var isDownload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initData()
    }

    private fun initData(){
        viewModel.liveDataStatus.observe(this) {
            loading(it)
        }

        viewModel.liveData.observe(this) {
            binding.tvResponse.text = it
            if(isDownload){
                isDownload = false
            }
        }

        binding.tvResponse.movementMethod = ScrollingMovementMethod.getInstance()

        binding.etUrl.setText(Constants.BAIDU_BASE_URL)
        binding.etUrl.setSelection(binding.etUrl.length())
        // 添加响应进度监听
        RetrofitHelper.getInstance().addResponseListener( "${Constants.BAIDU_BASE_URL}/index.html", object: ProgressListener{
            override fun onProgress(currentBytes: Long, contentLength: Long, completed: Boolean) {
                Log.d(Constants.TAG, "onProgress: ${currentBytes}/${contentLength} - ${currentBytes.toFloat().div(contentLength).times(100F)}% - $completed")
            }

            override fun onException(e: Exception) {
                Log.e(Constants.TAG, e.message)
            }
        })

        // 添加响应进度监听
        RetrofitHelper.getInstance().addResponseListener(Constants.RESPONSE_PROGRESS_1, object: ProgressListener{
            override fun onProgress(currentBytes: Long, contentLength: Long, completed: Boolean) {
                Log.d(Constants.TAG, "${Constants.RESPONSE_PROGRESS_1}: ${currentBytes}/${contentLength} - ${currentBytes.toFloat().div(contentLength).times(100F)}% - $completed")
            }

            override fun onException(e: Exception) {
                Log.e(Constants.TAG, e.message)
            }
        })

        // 添加响应进度监听
        RetrofitHelper.getInstance().addResponseListener(Constants.RESPONSE_PROGRESS_2, object: ProgressListener{
            override fun onProgress(currentBytes: Long, contentLength: Long, completed: Boolean) {
                val progress = if(contentLength > 0){
                    currentBytes.toFloat().div(contentLength).times(100F).toInt()
                }else {
                    0
                }
                Log.d(Constants.TAG, "${Constants.RESPONSE_PROGRESS_2}: ${currentBytes}/${contentLength} - ${currentBytes.toFloat().div(contentLength).times(100F)}% - $completed")
                binding.progressBar.progress = progress
                if(completed){
                    isDownload = false
                    binding.tvResponse.text = "下载完成"
                }else{
                    binding.tvResponse.text = "进度：${currentBytes}/${contentLength} - 进度百分比: ${progress}%"
                }
            }

            override fun onException(e: Exception) {
                Log.e(Constants.TAG, e.message)
                isDownload = false
                binding.tvResponse.text = e.message
            }
        })

    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            loadingFragment.show(supportFragmentManager,"loading")
        }else{
            loadingFragment.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        RetrofitHelper.getInstance().clearListener()
    }


    fun onClick(v: View){
        when(v.id){
            //原始BaseUrl请求
            R.id.btn1 -> viewModel.getRequest1()
            //切换到GitHub Url
            R.id.btn2 -> viewModel.getRequest2()
            //切换到Google Url
            R.id.btn3 -> viewModel.getRequest3()
            //切换到BaiDu Url
            R.id.btn4 -> viewModel.getRequest4()
            //动态设置BaseUrl
            R.id.btn5 -> {

                if(TextUtils.isEmpty(binding.etUrl.text)){
                    return
                }
                val dynamicUrl = binding.etUrl.text.toString()
                //动态改变 BaseUrl，这里只改变标记为Constants.DOMAIN_DYNAMIC的接口
                RetrofitHelper.getInstance().putDomain(Constants.DOMAIN_DYNAMIC,dynamicUrl)
                //通过setBaseUrl可以动态改变全局的 BaseUrl，优先级比domainName低，谨慎使用
//                RetrofitHelper.getInstance().setBaseUrl(dynamicUrl)
                viewModel.getRequest5()
            }
            R.id.btn6 -> {
                if(!isDownload){
                    isDownload = true
                    Log.d(Constants.TAG, "download...")
                    viewModel.download()
                }
            }
        }
    }

}
