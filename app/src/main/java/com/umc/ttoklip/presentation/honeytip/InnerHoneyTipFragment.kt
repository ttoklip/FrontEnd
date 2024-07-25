package com.umc.ttoklip.presentation.honeytip

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.umc.ttoklip.R
import com.umc.ttoklip.databinding.FragmentInnerHoneyTipBinding
import com.umc.ttoklip.presentation.base.BaseFragment
import com.umc.ttoklip.presentation.honeytip.adapter.CategoryVPA
import com.umc.ttoklip.presentation.honeytip.adapter.DailyPopularHoneyTipsVPA
import com.umc.ttoklip.presentation.honeytip.read.ReadHoneyTipActivity
import com.umc.ttoklip.presentation.honeytip.write.WriteHoneyTipActivity
import com.umc.ttoklip.util.PageDecoration
import kotlinx.coroutines.launch

class InnerHoneyTipFragment: BaseFragment<FragmentInnerHoneyTipBinding>(R.layout.fragment_inner_honey_tip) {
    private val viewModel: HoneyTipViewModel by viewModels(
        ownerProducer = {requireParentFragment()}
    )
    private val popularHoneyTipsVPA by lazy {
        DailyPopularHoneyTipsVPA{
            startActivity(ReadHoneyTipActivity.newIntent(requireContext(), it.id))
        }
    }
    private var category = WriteHoneyTipActivity.Category.HOUSEWORK.toString()
    override fun initObserver() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.topFiveQuestions.collect{
                    popularHoneyTipsVPA.submitList(it)
                }
            }
        }
    }

    override fun initView() {
        initCategoryViewPager()
        initPopularHoneyTipsViewPager(65, 30)
        binding.scrollV.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if ((!v.canScrollVertically(1))) {
                Log.d("end", "end")
                viewModel.getHoneyTipPage(category)
                /*when(category){
                    WriteHoneyTipActivity.Category.HOUSEWORK.toString() -> viewModel.getHouseHoneyTipPage()
                    WriteHoneyTipActivity.Category.RECIPE.toString() -> viewModel.getRecipeHoneyTipPage()
                    WriteHoneyTipActivity.Category.SAFE_LIVING.toString() -> viewModel.getSafeLivingHoneyTipPage()
                    WriteHoneyTipActivity.Category.WELFARE_POLICY.toString() -> viewModel.getWelFareHoneyTipPage()
                }*/
            }
        }

        binding.categoryTablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                category = tabTextToCategory(tab?.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun initPopularHoneyTipsViewPager(previewWidth: Int, itemMargin: Int) {
        val decoMargin = previewWidth + itemMargin
        val pageTransX = decoMargin + previewWidth
        val decoration = PageDecoration(decoMargin)
        binding.popularHoneyTipsVp.apply {
            offscreenPageLimit = 1
            addItemDecoration(decoration)
            adapter = popularHoneyTipsVPA
            binding.indicator.attachTo(this)
            setPageTransformer { page, position ->
                page.translationX = position * -pageTransX
                when {
                    position < -1 -> {
                        page.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.item_daily_popular_honey_tip_external_background)
                    }

                    position <= 0.5 && position >= -0.5 -> {
                        page.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.item_daily_popular_honey_tip_background)
                    }

                    else -> {
                        page.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.item_daily_popular_honey_tip_external_background)
                    }
                }
            }
        }
    }

    private fun initCategoryViewPager() {
        val tabTitles = listOf("집안일", "레시피", "안전한생활", "복지\u00b7정책")
        binding.categoryVp.adapter = CategoryVPA(this, HONEY_TIPS, tabTitles.size)
        TabLayoutMediator(binding.categoryTablayout, binding.categoryVp) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun tabTextToCategory(string: kotlin.String): String {
        return when (string) {
            "집안일" -> WriteHoneyTipActivity.Category.HOUSEWORK.toString()
            "레시피" -> WriteHoneyTipActivity.Category.RECIPE.toString()
            "안전한생활" -> WriteHoneyTipActivity.Category.SAFE_LIVING.toString()
            else -> WriteHoneyTipActivity.Category.WELFARE_POLICY.toString()
        }
    }
}