package com.umc.ttoklip.presentation.honeytip.honeytiplist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.umc.ttoklip.R
import com.umc.ttoklip.data.model.honeytip.HoneyTipMain
import com.umc.ttoklip.databinding.FragmentHoneyTipListBinding
import com.umc.ttoklip.presentation.honeytip.HoneyTipViewModel
import com.umc.ttoklip.presentation.honeytip.adapter.HoneyTipListRVA
import com.umc.ttoklip.presentation.honeytip.adapter.OnItemClickListener
import com.umc.ttoklip.presentation.honeytip.read.ReadHoneyTipActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HouseWorkHoneyTipListFragment : Fragment(),
    OnItemClickListener {
    private val honeyTipListRVA by lazy {
        HoneyTipListRVA(this.requireContext(),this)
    }
    private val viewModel: HoneyTipViewModel by viewModels(
        ownerProducer = { requireParentFragment().requireParentFragment() }
    )

    lateinit var binding: FragmentHoneyTipListBinding
    private var list = listOf<HoneyTipMain>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_honey_tip_list,
            null,
            false
        )
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("House start", "start")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.houseworkHoneyTip.collect {
                    if(it.size==0){

                    }
                    honeyTipListRVA.submitList(it)
                    list = it
                    /*if(honeyTipListRVA.currentList.size != it.size) {
                        honeyTipListRVA.submitList(it)
                    }*/
                }
            }
        }

        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.honeyTipPaging.collect {
                    Log.d("it", it.toString())
                    totalPage = it.totalPage
                    isLast = it.isLast
                }
            }
        }*/
    }

    fun initView() {
        initRV()
        /*binding.sv.viewTreeObserver.addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
            val view: View? = binding.sv.getChildAt(binding.sv.childCount - 1)
            if (view != null) {
                val diff: Int = view.bottom - (binding.sv.height + binding.sv
                    .scrollY)
                if (diff == 0) {
                    Log.d("end", "end")
                    viewModel.getHoneyTipByCategory()
                }
            }
        })*/
        /*binding.sv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if ((!v.canScrollVertically(1))) {
                Log.d("end", "end")
                viewModel.getHoneyTipByCategory()
            }
        }*/
    }

    private fun initRV() {
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        binding.rv.adapter = honeyTipListRVA
    }

    override fun onClick(honeyTip: HoneyTipMain) {
        startActivity(ReadHoneyTipActivity.newIntent(requireContext(), honeyTip.id))
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onPause() {
        super.onPause()
        Log.d("pause", "pause")
        viewModel.resetHoneyTipList("HOUSEWORK")
    }

}