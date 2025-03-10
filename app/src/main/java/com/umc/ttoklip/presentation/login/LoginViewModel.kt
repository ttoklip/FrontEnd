package com.umc.ttoklip.presentation.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.ttoklip.TtoklipApplication
import com.umc.ttoklip.data.model.login.LoginLocalRequest
import com.umc.ttoklip.data.model.login.LoginRequest
import com.umc.ttoklip.data.repository.login.LoginRepositoryImpl
import com.umc.ttoklip.module.onFail
import com.umc.ttoklip.module.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepositoryImpl
) : ViewModel() {

    var pwshow=MutableStateFlow<Boolean>(false)

    private val _isSocialLogin=MutableStateFlow<Boolean>(false)
    val isSocialLogin:StateFlow<Boolean>
        get() = _isSocialLogin
    private val _isFirstLogin = MutableStateFlow<Boolean>(true)
    val isFirstLogin: StateFlow<Boolean>
        get() = _isFirstLogin
    private val _isLogin=MutableStateFlow<Boolean>(false)
    val isLogin:StateFlow<Boolean>
        get() = _isLogin

    fun setIsSocialLogin(isSL:Boolean){
        _isSocialLogin.value=isSL
    }

    fun postLocalLogin(request: LoginLocalRequest, context: Context){
        viewModelScope.launch {
            loginRepository.postLoginLocal(request)
                .onSuccess {
                    TtoklipApplication.prefs.setString("jwt",it.jwtToken)
                    TtoklipApplication.prefs.setBoolean("isFirstLogin",it.ifFirstLogin)
                    _isFirstLogin.emit(it.ifFirstLogin)
                    _isLogin.emit(true)
                }.onFail {
                    Toast.makeText(context,"이메일 또는 비밀번호가 알맞지 않아요.", Toast.LENGTH_LONG).show()
                    Log.d("LOGIN-API", "local login 실패")
                }
        }
    }

    fun postLogin(request: LoginRequest) {
        viewModelScope.launch {
            loginRepository.postLogin(request)
                .onSuccess {
                    TtoklipApplication.prefs.setString("jwt",it.jwtToken)
                    TtoklipApplication.prefs.setBoolean("isFirstLogin",it.ifFirstLogin)
                    _isFirstLogin.emit(it.ifFirstLogin)
                    _isLogin.emit(true)
                }.onFail {
                    Log.d("LOGIN-API", "login 실패")
                }
        }
    }

    fun initIsLogin(){
        _isLogin.value=false
    }
}