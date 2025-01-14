package com.umc.ttoklip.presentation.signup

import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.umc.ttoklip.R
import com.umc.ttoklip.TtoklipApplication
import com.umc.ttoklip.databinding.ActivitySignupBinding
import com.umc.ttoklip.presentation.base.BaseActivity
import com.umc.ttoklip.presentation.login.LoginActivity
import com.umc.ttoklip.presentation.signup.fragments.TermViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupActivity:BaseActivity<ActivitySignupBinding>(R.layout.activity_signup) {

    private lateinit var navHostFragment:NavHostFragment
    private lateinit var vm:SignupViewModel
    private lateinit var termVM:TermViewModel

    override fun initView() {
        vm=ViewModelProvider(this).get(SignupViewModel::class.java)
        termVM=ViewModelProvider(this).get(TermViewModel::class.java)
        termVM.getTerm()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.signup_frm)as NavHostFragment
        var navController=navHostFragment.findNavController()
        val graphInflater=navController.navInflater
        val navGraph=graphInflater.inflate(R.navigation.signup_graph)

        val loginWay=intent.getStringExtra("loginWay")?:""
        val startDestination=determineStartDestination(loginWay)
        navGraph.setStartDestination(startDestination)
        navController.graph=navGraph

        binding.signupBackIb.setOnClickListener {
            if(navHostFragment.childFragmentManager.backStackEntryCount==0){
                val loginactivity=LoginActivity.loginActivity!!
                loginactivity.cancelLogin()
                finish()
            }else{
                navController.popBackStack()
            }
        }
        binding.signupCancelIb.setOnClickListener {
            binding.signupBackIb.visibility= View.VISIBLE
            binding.signupCancelIb.visibility= View.INVISIBLE
            navController.popBackStack()
        }
    }

    override fun onBackPressed() {
        if(navHostFragment.childFragmentManager.backStackEntryCount==0){
            val loginactivity=LoginActivity.loginActivity!!
            loginactivity.cancelLogin()
            finish()
        }else{
            super.onBackPressed()
        }
    }

    fun termBack(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.signup_frm)as NavHostFragment
        var navController=navHostFragment.findNavController()
        binding.signupBackIb.visibility= View.VISIBLE
        binding.signupCancelIb.visibility= View.INVISIBLE
        navController.popBackStack()
    }

    override fun initObserver()=Unit

    fun setProg(step:Int){
        binding.signupProgressbar.progress=step
    }
    fun updateButtonForTerm(){
        binding.signupBackIb.visibility= View.INVISIBLE
        binding.signupCancelIb.visibility= View.VISIBLE
    }

    private fun determineStartDestination(loginWay:String): Int {
        return if(loginWay=="local"){
            vm.signupType.value="local"
            R.id.signup1_fragment
        }else{
            vm.signupType.value="sns"
            R.id.signup3_fragment
        }
    }

    companion object{
        var signupActivity: SignupActivity?=null
    }
}
