package com.umc.ttoklip.presentation.home

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.umc.ttoklip.R
import com.umc.ttoklip.TtoklipApplication
import com.umc.ttoklip.data.model.honeytip.HoneyTipMain
import com.umc.ttoklip.data.model.town.Togethers
import com.umc.ttoklip.databinding.FragmentHomeBinding
import com.umc.ttoklip.presentation.MainActivity
import com.umc.ttoklip.presentation.alarm.AlarmActivity
import com.umc.ttoklip.presentation.base.BaseFragment
import com.umc.ttoklip.presentation.home.adapter.HomeTipRVA
import com.umc.ttoklip.presentation.hometown.communication.CommunicationActivity
import com.umc.ttoklip.presentation.hometown.together.read.ReadTogetherActivity
import com.umc.ttoklip.presentation.hometown.together.TogetherActivity
import com.umc.ttoklip.presentation.honeytip.adapter.OnItemClickListener
import com.umc.ttoklip.presentation.honeytip.read.ReadHoneyTipActivity
import com.umc.ttoklip.presentation.login.LoginActivity
import com.umc.ttoklip.presentation.mypage.adapter.OnTogetherItemClickListener
import com.umc.ttoklip.presentation.mypage.adapter.TransactionAdapter
import com.umc.ttoklip.presentation.news.adapter.NewsRVA
import com.umc.ttoklip.presentation.news.detail.ArticleActivity
import com.umc.ttoklip.presentation.search2.SearchActivity2
import com.umc.ttoklip.util.TtoklipFirebaseMessagingService
import com.umc.ttoklip.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home),
    OnItemClickListener, OnTogetherItemClickListener {


    private val viewModel: HomeViewModel by viewModels<HomeViewModelImpl>()
    private val newsRVA by lazy {
        NewsRVA(onClick = { news ->
            startActivity(ArticleActivity.newIntent(requireContext(), news.newsletterId))
        })
    }
    private val townRVA by lazy {
        TransactionAdapter(requireContext(), this)
    }
    private val tipRVA by lazy {
        HomeTipRVA(this,this)
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activityBus.collect {
                    when (it) {
                        HomeViewModel.ActivityEventBus.SEARCH -> {
                            startActivity(SearchActivity2.newIntent(requireContext()))
                        }

                        HomeViewModel.ActivityEventBus.NEWS_DETAIL -> {
                            (requireActivity() as MainActivity).goNews()
                        }

                        HomeViewModel.ActivityEventBus.TIP_DETAIL -> {
                            (requireActivity() as MainActivity).goTip()
                        }

                        HomeViewModel.ActivityEventBus.ALARM -> {
                            startActivity(AlarmActivity.newIntent(requireContext()))
                        }

                        HomeViewModel.ActivityEventBus.GROUP_BUY_DETAIL -> {
                            (requireActivity() as MainActivity).goTown()
                        }

                        else -> {}
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainData.collect {
                    newsRVA.submitList(it.newsLetters)
                    tipRVA.submitList(it.honeyTips)
                    townRVA.submitList(it.carts)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorData.collect {
                    if(it.isNotEmpty()) showToast(it)
                }
            }
        }
    }

    override fun initView() {
        binding.vm = viewModel
        binding.tipRV.adapter = tipRVA
        viewModel.getMain(::expirationToken)
        Log.d("엑세스", "${TtoklipApplication.prefs.getString("jwt", "")}")

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.patchFCM(TtoklipFirebaseMessagingService().getFirebaseToken())
        }

        binding.chatImg.setOnClickListener {
            //(requireActivity() as MainActivity).goTown()
            //동내 페이지 점검
            val intent = Intent(requireContext(), CommunicationActivity::class.java)
            startActivity(intent)
        }
        binding.groupButImg.setOnClickListener {
            //(requireActivity() as MainActivity).goTown()
            //동내 페이지 점검
            val intent = Intent(requireContext(), TogetherActivity::class.java)
            startActivity(intent)
        }

        binding.newsRV.adapter = newsRVA
        binding.groupBuyRV.adapter = townRVA

    }

    override fun onClick(honeyTip: HoneyTipMain) {
        val intent = Intent(activity, ReadHoneyTipActivity::class.java)
        intent.putExtra("postId", honeyTip.id)
        Log.d("Clicked honeyTip", honeyTip.toString())
        Log.d("postId", honeyTip.id.toString())
        startActivity(intent)
    }

    override fun onClick(together: Togethers) {
        val intent = Intent(requireContext(), ReadTogetherActivity::class.java)
        intent.putExtra("postId", together.id)
        startActivity(intent)
    }

    private fun expirationToken(){
        TtoklipApplication.prefs.removeString("jwt")
        startActivity(Intent(requireContext(),LoginActivity::class.java))
        activity?.finish()
    }
}