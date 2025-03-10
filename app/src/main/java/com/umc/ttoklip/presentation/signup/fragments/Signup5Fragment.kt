package com.umc.ttoklip.presentation.signup.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.umc.ttoklip.R
import com.umc.ttoklip.databinding.FragmentSignup5Binding
import com.umc.ttoklip.presentation.MainActivity
import com.umc.ttoklip.presentation.base.BaseFragment
import com.umc.ttoklip.presentation.login.LoginActivity
import com.umc.ttoklip.presentation.signup.location.DirectLocationActivity
import com.umc.ttoklip.presentation.signup.location.LocationActivity
import com.umc.ttoklip.presentation.signup.SignupActivity
import com.umc.ttoklip.presentation.signup.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Signup5Fragment: BaseFragment<FragmentSignup5Binding>(R.layout.fragment_signup5) {

    private val viewModel: SignupViewModel by activityViewModels()
    private lateinit var range: String

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorData.collect {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun initView() {
        val activity=activity as SignupActivity
        activity.setProg(5)

        range = getString(R.string.range_500m)
        binding.rangeSettingExplainTv.text =
            getString(R.string.range_setting_format, range)

        binding.range500mTv.setOnClickListener { setRange500m(activity) }
        binding.range1kmTv.setOnClickListener { setRange1km(activity) }
        binding.range15kmTv.setOnClickListener { setRange15km(activity) }

        binding.rangeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress <= RANGE_500M_PROGRESS) { setRange500m(activity)
                } else if (progress <= RANGE_1KM_PROGRESS) { setRange1km(activity)
                } else { setRange15km(activity)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    if (it.progress <= RANGE_500M_PROGRESS) {
                        it.progress = RANGE_500M_PROGRESS
                        setRange500m(activity)
                    } else if (it.progress <= RANGE_1KM_PROGRESS) {
                        it.progress = RANGE_1KM_PROGRESS
                        setRange1km(activity)
                    } else {
                        it.progress = RANGE_15kM_PROGRESS
                        setRange15km(activity)
                    }
                }
            }

        })

        binding.signup5LocationBtn.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("nickname",viewModel.nickname.value)
            bundle.putStringArrayList("interest",viewModel.categories.value)
            bundle.putString("imageUri",viewModel.profileImage.value)
            bundle.putInt("independentCareerYear",viewModel.independenctYear.value)
            bundle.putInt("independentCareerMonth",viewModel.independenctMonth.value)
            val intent=Intent(activity, LocationActivity::class.java)
            intent.putExtra("userInfo",bundle)
            startActivity(intent)
        }
        binding.signup5LocationDirectEt.setOnClickListener {
            startActivity(Intent(activity, DirectLocationActivity::class.java))
        }
        binding.signup5NextBtn.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            val loginActivity=LoginActivity.loginActivity
            loginActivity?.finish()
            activity.finish()
        }
    }

    private fun setRange15km(activity: SignupActivity) {
        range = getString(R.string.range_1_5km)
        binding.rangeBar.progress = RANGE_15kM_PROGRESS
        binding.range500mTv.setTextColor(ContextCompat.getColor(activity,R.color.gray40))
        binding.range1kmTv.setTextColor(ContextCompat.getColor(activity,R.color.gray40))
        binding.range15kmTv.setTextColor(ContextCompat.getColor(activity,R.color.black))
        binding.rangeSettingExplainTv.text = getString(R.string.range_setting_format, range)
    }

    private fun setRange1km(activity: SignupActivity) {
        range = getString(R.string.range_1km)
        binding.rangeBar.progress = RANGE_1KM_PROGRESS
        binding.range500mTv.setTextColor(ContextCompat.getColor(activity,R.color.gray40))
        binding.range1kmTv.setTextColor(ContextCompat.getColor(activity,R.color.black))
        binding.range15kmTv.setTextColor(ContextCompat.getColor(activity,R.color.gray40))
        binding.rangeSettingExplainTv.text = getString(R.string.range_setting_format, range)
    }

    private fun setRange500m(activity: SignupActivity) {
        range = getString(R.string.range_500m)
        binding.rangeBar.progress = RANGE_500M_PROGRESS
        binding.range500mTv.setTextColor(ContextCompat.getColor(activity,R.color.black))
        binding.range1kmTv.setTextColor(ContextCompat.getColor(activity,R.color.gray40))
        binding.range15kmTv.setTextColor(ContextCompat.getColor(activity,R.color.gray40))
        binding.rangeSettingExplainTv.text = getString(R.string.range_setting_format, range)
    }

    companion object {
        private const val RANGE_500M_PROGRESS = 33
        private const val RANGE_1KM_PROGRESS = 67
        private const val RANGE_15kM_PROGRESS = 100
    }
}