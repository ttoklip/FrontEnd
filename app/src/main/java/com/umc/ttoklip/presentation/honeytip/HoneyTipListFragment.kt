package com.umc.ttoklip.presentation.honeytip

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.ttoklip.R
import com.umc.ttoklip.data.model.honeytip.HoneyTipCategory
import com.umc.ttoklip.data.model.honeytip.HoneyTipMainResponse
import com.umc.ttoklip.data.model.honeytip.HoneyTipResponse
import com.umc.ttoklip.data.model.honeytip.QuestionCategory
import com.umc.ttoklip.databinding.FragmentHoneyTipListBinding
import com.umc.ttoklip.presentation.base.BaseFragment
import com.umc.ttoklip.presentation.honeytip.adapter.HoneyTipListRVA
import com.umc.ttoklip.presentation.honeytip.adapter.HoneyTips
import com.umc.ttoklip.presentation.honeytip.adapter.OnItemClickListener
import com.umc.ttoklip.presentation.honeytip.read.ReadActivity
import com.umc.ttoklip.presentation.honeytip.write.WriteHoneyTipActivity
import kotlinx.coroutines.launch


class HoneyTipListFragment() :
    BaseFragment<FragmentHoneyTipListBinding>(R.layout.fragment_honey_tip_list),
    OnItemClickListener {
    private val honeyTipListRVA by lazy {
        HoneyTipListRVA(this)
    }
    private val viewModel: HoneyTipViewModel by activityViewModels()
    private var board = ""
    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.honeyTip.collect {
                    val intent = Intent(activity, ReadActivity::class.java)
                    intent.putExtra(BOARD, board)
                    intent.putExtra("honeyTip", it)
                    Log.d("HoneyTipListFragment", board)
                    startActivity(intent)
                }
            }
        }

        val honeyTipMainResponse = viewModel.honeyTipMain2.value

        viewModel.boardLiveData.observe(viewLifecycleOwner){
            board = it
            if(it == HONEY_TIP){
                submitHoneyTipList(honeyTipMainResponse!!)
            }
            else{
                submitQuestionList(honeyTipMainResponse!!)
            }
        }
    }

    private fun submitHoneyTipList(honeyTipMainResponse: HoneyTipMainResponse){
        viewModel.honeyTipCategory.observe(viewLifecycleOwner){ category->
            when(category){
                "집안일" -> {honeyTipListRVA.submitList(honeyTipMainResponse?.honeyTipCategory?.housework)
                Log.d("뭐고", honeyTipMainResponse?.honeyTipCategory?.housework.toString())}
                "레시피" -> {honeyTipListRVA.submitList(honeyTipMainResponse?.honeyTipCategory?.cooking)
                Log.d("왜 작동 안해", honeyTipMainResponse?.honeyTipCategory?.cooking.toString())}
                "안전한 생활" -> honeyTipListRVA.submitList(honeyTipMainResponse?.honeyTipCategory?.safeLiving)
                else -> honeyTipListRVA.submitList(honeyTipMainResponse?.honeyTipCategory?.welfarePolicy)
            }
        }
    }

    private fun submitQuestionList(honeyTipMainResponse: HoneyTipMainResponse){
        viewModel.questionCategory.observe(viewLifecycleOwner){ category->
            when(category){
                "집안일" -> honeyTipListRVA.submitList(honeyTipMainResponse?.questionCategory?.housework)
                "레시피" -> honeyTipListRVA.submitList(honeyTipMainResponse?.questionCategory?.cooking)
                "안전한 생활" -> honeyTipListRVA.submitList(honeyTipMainResponse?.questionCategory?.safeLiving)
                else -> honeyTipListRVA.submitList(honeyTipMainResponse?.questionCategory?.welfarePolicy)
            }
        }
    }

    override fun initView() {
        initRV()
        /*viewModel.boardLiveData.observe(this) {
            board = it
            Log.d("HoneyTipListFragment change board", it)
        }*/
    }

    private fun initRV() {
        /*val honeyTipList = listOf(
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
            HoneyTips("똑똑이", "음식물 쓰레기 냄새 방지!!", "집에 가끔씩이지만 나타나는 바퀴벌레, 잘못 처리하면 알깐다고도...", "1일전", 0),
        )*/
        //val honeyTipList = mutableListOf<HoneyTipResponse>()

        binding.rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        binding.rv.adapter = honeyTipListRVA
        //honeyTipListRVA.submitList(honeyTipList)
    }

    override fun onClick(honeyTipResponse: HoneyTipResponse) {
        viewModel.inquireHoneyTip(honeyTipResponse.honeyTipId)
    }
}