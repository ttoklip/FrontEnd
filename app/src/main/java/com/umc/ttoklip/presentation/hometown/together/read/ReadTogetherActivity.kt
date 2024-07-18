package com.umc.ttoklip.presentation.hometown.together.read

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.icu.text.DecimalFormat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.umc.ttoklip.R
import com.umc.ttoklip.TtoklipApplication
import com.umc.ttoklip.data.model.honeytip.ImageUrl
import com.umc.ttoklip.data.model.honeytip.request.ReportRequest
import com.umc.ttoklip.data.model.news.comment.NewsCommentResponse
import com.umc.ttoklip.data.model.town.CreateCommentRequest
import com.umc.ttoklip.databinding.ActivityReadTogetherBinding
import com.umc.ttoklip.presentation.base.BaseActivity
import com.umc.ttoklip.presentation.hometown.adapter.TownCommentAdapter
import com.umc.ttoklip.presentation.honeytip.ImageViewActivity
import com.umc.ttoklip.presentation.honeytip.adapter.OnReadImageClickListener
import com.umc.ttoklip.presentation.honeytip.adapter.ReadImageRVA
import com.umc.ttoklip.presentation.dialog.DeleteDialogFragment
import com.umc.ttoklip.presentation.dialog.FinishCartDialogFragment
import com.umc.ttoklip.presentation.dialog.ReportDialogFragment
import com.umc.ttoklip.presentation.hometown.dialog.TogetherDialog
import com.umc.ttoklip.presentation.honeytip.read.ReadHoneyTipActivity
import com.umc.ttoklip.presentation.news.adapter.CommentRVA
import com.umc.ttoklip.presentation.otheruser.OtherUserActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadTogetherActivity :
    BaseActivity<ActivityReadTogetherBinding>(R.layout.activity_read_together),
    OnReadImageClickListener{
    private val viewModel: ReadTogetherViewModel by viewModels<ReadTogetherViewModelImpl>()
    private val imageAdapter: ReadImageRVA by lazy {
        ReadImageRVA(this, this@ReadTogetherActivity)
    }
    private val commentRVA by lazy {
        CommentRVA({ id ->
            viewModel.replyCommentParentId.value = id
        }, { id, myComment ->
            Log.d("mycomment", myComment.toString())
            if (myComment) {
                val deleteDialog = DeleteDialogFragment()
                deleteDialog.setDialogClickListener(object :
                    DeleteDialogFragment.DialogClickListener {
                    override fun onClick() {
                        viewModel.deleteComment(id.toLong())
                    }
                })
                deleteDialog.show(supportFragmentManager, deleteDialog.tag)
            } else {
                val reportDialog = ReportDialogFragment()
                reportDialog.setDialogClickListener(object : ReportDialogFragment.DialogClickListener{
                    override fun onClick(type: String, content: String) {
                        viewModel.reportComment(
                            id.toLong(),
                            com.umc.ttoklip.data.model.town.ReportRequest(
                                content = content,
                                reportType = type
                            )
                        )
                    }
                })
                reportDialog.show(supportFragmentManager, reportDialog.tag)
            }
        }, { nick ->
            startActivity(OtherUserActivity.newIntent(this, nick))
        })
    }
    private var postId = 0L
    private var isWriter = false

    override fun initView() {
        binding.imageRv.apply {
            itemAnimator = null
            adapter = imageAdapter
        }
        binding.commentRv.adapter = commentRVA
        binding.replyT.setOnClickListener {
            viewModel.replyCommentParentId.value = 0
        }
        binding.vm = viewModel
        postId = intent.getLongExtra("postId", 0)
        viewModel.savePostId(postId)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.dotBtn.setOnClickListener {
            if (isWriter) {
                /*binding.editBtn.bringToFront()
                binding.editBtn.isVisible = binding.editBtn.isVisible.not()*/
            } else {
                binding.reportBtn.bringToFront()
                binding.reportBtn.isVisible = binding.reportBtn.isVisible.not()
            }
        }

        binding.reportBtn.setOnClickListener {
            val reportDialog = ReportDialogFragment()
            reportDialog.setDialogClickListener(object : ReportDialogFragment.DialogClickListener {
                override fun onClick(type: String, content: String) {
                    viewModel.reportPost(
                        com.umc.ttoklip.data.model.town.ReportRequest(
                            content = content,
                            reportType = type
                        )
                    )
                }

            })
            reportDialog.show(supportFragmentManager, reportDialog.tag)
        }


        binding.SendCardView.setOnClickListener {
            if (binding.commentEt.text.toString().isNotBlank()) {
                viewModel.createComment()
            }
            binding.commentEt.setText("")
            viewModel.replyCommentParentId.value = 0
        }

        binding.ownerCheckBtn.setOnClickListener {
            viewModel.fetchParticipants()
            /*val dialog = ParticipantDialogFragment()
            dialog.show(supportFragmentManager, dialog.tag)*/
        }

        binding.ownerJoinBtn.setOnClickListener {
            val finishCartDialog = FinishCartDialogFragment()
            finishCartDialog.setDialogClickListener(object : FinishCartDialogFragment.DialogClickListener{
                override fun onClick() {
                    viewModel.patchPostStatus("COMPLETED")
                }
            })
            finishCartDialog.show(supportFragmentManager, finishCartDialog.tag)
        }

        binding.joinBtn.setOnClickListener {
            val joinDialog = TogetherDialog()
            joinDialog.setDialogClickListener(object : TogetherDialog.TogetherDialogClickListener{
                override fun onClick() {
                    viewModel.setJoinState(true)
                    viewModel.joinTogether()
                }
            })
            joinDialog.show(supportFragmentManager, joinDialog.tag)
        }

        binding.joinCancelBtn.setOnClickListener {
            viewModel.setJoinState(false)
            viewModel.cancelTogether()
        }

    }

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.joinState.collect {
                    /*if (it.not()) {
                        binding.joinBtn.background =
                            getDrawable(R.drawable.rectangle_corner_10_strok_1_gray40)
//                            binding.joinBtn.text = getString(R.string.cancel_join)
                    } else {
                        binding.joinBtn.background =
                            getDrawable(R.drawable.yellow_btn_background)
//                            binding.joinBtn.text = getString(R.string.join_together)
                    }*/
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postId.collect {
                    viewModel.readTogether(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postContent.collect { response ->
                    with(binding) {
                        writerTv.text = response.writer
                        titleT.text = response.title
                        contentT.text = response.content
                        totalPriceTv.text = AMOUNT_FORMAT.format(response.totalPrice).toString()
                        maxMemberTv.text = response.partyMax.toString()
                        tradingPlaceTv.text = response.location
                        openChatLinkTv.text = response.chatUrl

                        imageAdapter.submitList(response.imageUrls.map { url ->
                            ImageUrl(
                                0,
                                imageUrl = url.imageUrl
                            )
                        })
                        val spannableAmount =
                            SpannableString(
                                getString(
                                    R.string.join_stat_format,
                                    response.partyCnt,
                                    response.partyMax
                                )
                            )
                        spannableAmount.setSpan(
                            ForegroundColorSpan(getColor(R.color.blue)),
                            AMOUNT_STRING_START,
                            response.partyCnt.toString().length + AMOUNT_STRING_LENGTH,
                            SPANNABLE_FLAG_ZERO
                        )
                        binding.currentJoinStatTv.text = spannableAmount
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.writer.collect {
                    val writer = TtoklipApplication.prefs.getString("nickname", "")
                    viewModel.checkWriter(writer)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isWriter.collect {
                    isWriter = it
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toast.collect {
                    Toast.makeText(this@ReadTogetherActivity, it, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.comments.collect {
                    val list = it.map { it ->
                        NewsCommentResponse(
                            it.commentContent,
                            it.commentId.toInt(),
                            it.parentId.toInt(),
                            it.writer,
                            it.writtenTime
                        )
                    }
                    commentRVA.submitList(list)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.replyCommentParentId.collect { id ->
                    if (id == 0) {
                        binding.replyT.text = ""
                    } else {
                        binding.replyT.text = "@${id}"
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.participants.collect{

                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.participants.collect{
                    if(it.cartMemberResponses.isNotEmpty()){
                        val dialog = ParticipantDialogFragment()
                        dialog.show(supportFragmentManager, dialog.tag)
                    }
                }
            }
        }
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        private const val AMOUNT_STRING_START = 0
        private const val SPANNABLE_FLAG_ZERO = 0
        private const val AMOUNT_STRING_LENGTH = 1
        private val AMOUNT_FORMAT = DecimalFormat("#,###")
        fun newIntent(context: Context, id: Long) =
            Intent(context, ReadTogetherActivity::class.java).apply {
                putExtra("postId", id)
            }
    }

    override fun onClick(imageUrl: String) {
        val images = imageAdapter.currentList.filterIsInstance<ImageUrl>().map { it.imageUrl }
            .toTypedArray()
        Log.d("images", images.toString())
        val intent = Intent(this, ImageViewActivity::class.java)
        intent.putExtra("images", images)
        startActivity(intent)
    }
}