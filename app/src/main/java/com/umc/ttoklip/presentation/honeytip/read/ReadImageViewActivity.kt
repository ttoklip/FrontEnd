package com.umc.ttoklip.presentation.honeytip.read

import android.content.Context
import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.umc.ttoklip.R
import com.umc.ttoklip.databinding.ActivityImageViewBinding
import com.umc.ttoklip.presentation.base.BaseActivity
import com.umc.ttoklip.presentation.honeytip.adapter.ReadImageVPA
import com.umc.ttoklip.util.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadImageViewActivity: BaseActivity<ActivityImageViewBinding>(R.layout.activity_image_view) {
    private val images by lazy {
        (intent.getStringArrayExtra("images") ?: emptyArray()).toMutableList()
    }

    private val index by lazy {
        intent.getIntExtra("position", 0)
    }
    override fun initView() {
        initVPA()

        binding.currentTv.text = "${index+1}/"
        binding.totalTv.text = images.size.toString()

        binding.closeBtn.setOnSingleClickListener {
            finish()
        }
    }

    private fun initVPA(){
        val readImageVPA = ReadImageVPA(this)
        readImageVPA.submitList(images)
        binding.vp.apply {
            adapter = readImageVPA
            currentItem = index
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    binding.currentTv.text = "${position+1}/"
                }
            })
        }
    }

    override fun initObserver() {

    }

    companion object {
        fun newIntent(context: Context, images: Array<String>, position: Int) =
            Intent(context, ReadImageViewActivity::class.java).apply {
                putExtra("images", images)
                putExtra("position", position)
            }
    }
}