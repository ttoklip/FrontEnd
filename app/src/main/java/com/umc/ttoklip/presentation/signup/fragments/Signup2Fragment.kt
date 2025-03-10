package com.umc.ttoklip.presentation.signup.fragments

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.umc.ttoklip.R
import com.umc.ttoklip.databinding.FragmentSignup2Binding
import com.umc.ttoklip.presentation.base.BaseFragment
import com.umc.ttoklip.presentation.signup.SignupActivity
import com.umc.ttoklip.presentation.signup.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class Signup2Fragment: BaseFragment<FragmentSignup2Binding>(R.layout.fragment_signup2) {

    private lateinit var vm: SignupViewModel

    val num="([0-9].*)"
    val symbol="([-_.;!?@#,$%^&*].*)"
    val eng="([a-z].*)"
    val pattern_num= Pattern.compile(num)
    val pattern_symbol= Pattern.compile(symbol)
    val pattern_eng= Pattern.compile(eng)

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.iddupcheckbtn.collect{
                        if(it){
                            delay(200)
                            vm.idok.collect {
                                if (it) {
                                    binding.signup2IdnotokTv.visibility=View.GONE
                                    binding.signup2IdokTv.visibility= View.VISIBLE
                                } else {
                                    binding.signup2IdnotokTv.visibility=View.VISIBLE
                                    binding.signup2IdokTv.visibility= View.GONE
                                }
                                nextOk()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        vm= ViewModelProvider(requireActivity()).get(SignupViewModel::class.java)
        Log.i("type test",vm.signupType.value)

        val activity=activity as SignupActivity
        activity?.setProg(2)

        initValue()

        binding.signup2IdcheckButton.setOnClickListener {
            //아이디 중복 확인 체크 로직
            vm.checkId(binding.signup2IdEt.text.toString())
            vm.idDupCheckOk()
        }
        binding.signup2PwEt.addTextChangedListener {
            pwcheck(it.toString())
            if(it.toString().length>=8) {
                vm.pwSizeOk.value=true
                binding.signupPwsizeNotokTv.visibility=View.GONE
            }
            else {
                vm.pwSizeOk.value=false
                binding.signupPwsizeNotokTv.visibility=View.VISIBLE
            }
        }
        binding.signup2PwcheckEt.addTextChangedListener {
            if(it.toString().equals(binding.signup2PwEt.text.toString())){
                vm.pwok.value=true
                binding.signupPwokTv.visibility=View.VISIBLE
                binding.signupPwnotokTv.visibility=View.GONE
            }else{
                vm.pwok.value=false
                binding.signupPwokTv.visibility=View.GONE
                binding.signupPwnotokTv.visibility=View.VISIBLE
            }
            nextOk()
        }
        binding.signup2NextBtn.setOnClickListener {
            if(vm.idok.value&&vm.pwok.value&&vm.pwSizeOk.value){
                vm.pw.value=binding.signup2PwEt.text.toString()
                vm.repw.value=binding.signup2PwcheckEt.text.toString()
                findNavController().navigate(R.id.action_signup2_fragment_to_signup3_fragment)
            }
        }
    }

    private fun initValue() {
        binding.signup2IdEt.setText(vm.email.value)
        binding.signup2PwEt.setText(vm.pw.value)
        binding.signup2PwcheckEt.setText(vm.repw.value)
        pwcheck(vm.pw.value)
        nextOk()
    }

    private fun pwcheck(pw: String){
        val matcher_num=pattern_num.matcher(pw)
        val matcher_symbol=pattern_symbol.matcher(pw)
        val matcher_eng=pattern_eng.matcher(pw)
        if(matcher_eng.find()){
            binding.signup2PwengIv.setImageResource(R.drawable.item_pwcheck_on)
            binding.signup2PwengTv.setTextColor(ContextCompat.getColor(requireActivity(),R.color.black))
        }else{
            binding.signup2PwengIv.setImageResource(R.drawable.item_pwcheck_off)
            binding.signup2PwengTv.setTextColor(ContextCompat.getColor(requireActivity(),R.color.gray60))
        }
        if(matcher_symbol.find()){
            binding.signup2PwspeIv.setImageResource(R.drawable.item_pwcheck_on)
            binding.signup2PwspeTv.setTextColor(ContextCompat.getColor(requireActivity(),R.color.black))
        }else{
            binding.signup2PwspeIv.setImageResource(R.drawable.item_pwcheck_off)
            binding.signup2PwspeTv.setTextColor(ContextCompat.getColor(requireActivity(),R.color.gray60))
        }
        if(matcher_num.find()){
            binding.signup2PwnumIv.setImageResource(R.drawable.item_pwcheck_on)
            binding.signup2PwnumTv.setTextColor(ContextCompat.getColor(requireActivity(),R.color.black))
        }else{
            binding.signup2PwnumIv.setImageResource(R.drawable.item_pwcheck_off)
            binding.signup2PwnumTv.setTextColor(ContextCompat.getColor(requireActivity(),R.color.gray60))
        }
    }

    private fun nextOk(){
        if(vm.idok.value&&vm.pwok.value){
            binding.signup2NextBtn.isClickable=true
            binding.signup2NextBtn.setBackgroundResource(R.drawable.rectangle_corner_10_yellow)
            binding.signup2NextBtn.setTextAppearance(R.style.TextAppearance_App_16sp_700)
        }else{
            binding.signup2NextBtn.isClickable=false
            binding.signup2NextBtn.setBackgroundResource(R.drawable.rectangle_corner_10_strok_1_black)
            binding.signup2NextBtn.setTextAppearance(R.style.TextAppearance_App_16sp_500)
        }
    }
}