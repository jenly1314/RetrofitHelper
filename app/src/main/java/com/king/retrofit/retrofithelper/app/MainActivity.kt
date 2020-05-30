package com.king.retrofit.retrofithelper.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.king.retrofit.retrofithelper.DomainName
import com.king.retrofit.retrofithelper.RetrofitHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val loadingFragment by lazy { LoadingFragment() }

    private val viewModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
    }

    private fun initData(){
        viewModel.liveDataStatus.observe(this, Observer {
            loading(it)
        })

        viewModel.liveData.observe(this, Observer {
            tvResponse.text = it
        })

        tvResponse.movementMethod = ScrollingMovementMethod.getInstance()

        etUrl.setText("https://baidu.com")
    }

    private fun loading(isLoading: Boolean){
        if(isLoading){
            loadingFragment.show(supportFragmentManager,"loading")
        }else{
            loadingFragment.dismiss()
        }
    }


    fun onClick(v: View){
        when(v.id){
            //原始BaseUrl请求
            R.id.btn1 -> {
                viewModel.getRequest1()
            }
            //切换到GitHub Url
            R.id.btn2 -> {
                viewModel.getRequest2()
            }
            //切换到Google Url
            R.id.btn3 -> {
                viewModel.getRequest3()
            }
            //动态设置BaseUrl
            R.id.btn4 -> {

                if(TextUtils.isEmpty(etUrl.text)){
                    return
                }
                val dynamicUrl = etUrl.text.toString()
                //动态改变 BaseUrl，这里只改变标记为Constants.DOMAIN_DYNAMIC的接口
                RetrofitHelper.getInstance().putDomain(Constants.DOMAIN_DYNAMIC,dynamicUrl)
                //通过setBaseUrl可以动态改变全局的 BaseUrl，优先级比domainName低，谨慎使用
//                RetrofitHelper.getInstance().setBaseUrl(dynamicUrl)
                viewModel.getRequest4()
            }
        }
    }

}
