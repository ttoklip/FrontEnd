package com.umc.ttoklip.presentation.honeytip.read

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.umc.ttoklip.R
import com.umc.ttoklip.TtoklipApplication
import com.umc.ttoklip.data.model.honeytip.ImageUrl
import com.umc.ttoklip.data.model.honeytip.request.ReportRequest
import com.umc.ttoklip.databinding.ActivityReadQuestionBinding
import com.umc.ttoklip.presentation.base.BaseActivity
import com.umc.ttoklip.presentation.honeytip.adapter.OnReadImageClickListener
import com.umc.ttoklip.presentation.honeytip.adapter.QuestionCommentRVA
import com.umc.ttoklip.presentation.honeytip.adapter.ReadImageRVA
import com.umc.ttoklip.presentation.dialog.DeleteDialogFragment
import com.umc.ttoklip.presentation.dialog.ReportDialogFragment
import com.umc.ttoklip.presentation.otheruser.OtherUserActivity
import com.umc.ttoklip.util.setOnSingleClickListener
import com.umc.ttoklip.util.showKeyboard
import com.umc.ttoklip.util.showToast
import com.umc.ttoklip.util.toReplyNicknameFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReadQuestionActivity :
    BaseActivity<ActivityReadQuestionBinding>(R.layout.activity_read_question),
    OnReadImageClickListener {
    private val viewModel: ReadHoneyTipViewModel by viewModels()

    private val commentRVA by lazy {
        QuestionCommentRVA(this, { id, name ->
            viewModel.replyCommentParentId.value = Pair(id, name.toReplyNicknameFormat())
            binding.commentEt.showKeyboard()
            binding.scrollV.viewTreeObserver.addOnGlobalLayoutListener {
                binding.scrollV.smoothScrollTo(0, binding.scrollV.getChildAt(0).bottom)
            }
        }, { id, myComment ->
            if (myComment) {
                val deleteDialog = DeleteDialogFragment()
                deleteDialog.setDialogClickListener(object :
                    DeleteDialogFragment.DialogClickListener {
                    override fun onClick() {
                        viewModel.deleteQuestionComment(
                            id,
                            postId
                        )
                    }
                })
                deleteDialog.show(supportFragmentManager, deleteDialog.tag)
            } else {
                val reportDialog = ReportDialogFragment()
                reportDialog.setDialogClickListener(object :
                    ReportDialogFragment.DialogClickListener {
                    override fun onClick(type: String, content: String) {
                        viewModel.postReportQuestionComment(
                            id,
                            ReportRequest(
                                content = content,
                                reportType = type
                            )
                        )
                    }
                })
                reportDialog.show(supportFragmentManager, reportDialog.tag)
            }
        },
            { id, likedByCurrentUser ->
                Log.d("likedByCurrentUser", likedByCurrentUser.toString())
                if (likedByCurrentUser) {
                    viewModel.disLikeQuestionComment(postId, id)
                } else {
                    viewModel.likeQuestionComment(postId, id)
                }
            })
    }

    private val imageAdapter: ReadImageRVA by lazy {
        ReadImageRVA(this, this@ReadQuestionActivity)
    }
    private var isShowMenu = false
    private var postId = 0

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.readEvent.collect {
                    handleEvent(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastEvent.collect { text ->
                    Toast.makeText(this@ReadQuestionActivity, text, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.questionComments.collect {
                    commentRVA.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.replyCommentParentId.collect { (id, name) ->
                    if (id == 0) {
                        binding.commentEt.setText("")
                    } else {
                        val spannableString = SpannableString(name).apply {
                            setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        this@ReadQuestionActivity,
                                        R.color.blue
                                    )
                                ),
                                0, name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                        binding.commentEt.setText(spannableString)
                        binding.commentEt.setSelection(name.length)
                    }
                }
            }
        }
    }

    private fun handleEvent(event: ReadHoneyTipViewModel.ReadEvent) {
        when (event) {
            is ReadHoneyTipViewModel.ReadEvent.ReadQuestionEvent -> {
                val question = event.inquireQuestionResponse
                with(binding) {
                    Glide.with(this@ReadQuestionActivity)
                        .load(question.writerProfileImageUrl)
                        .placeholder(R.drawable.ic_defeault_logo)
                        .into(profileImg)
                    titleTv.text = question.title
                    writerTv.text = question.writer
                    contentT.text = question.content
                    commitT.text = question.commentCount.toString()
                }
                if (question.imageUrls.isNotEmpty()) {
                    binding.imageRv.visibility = View.VISIBLE
                    imageAdapter.submitList(question.imageUrls)
                }
                val writer = TtoklipApplication.prefs.getString("nickname", "")
                if (question.writer != writer) {
                    showReportBtn()
                }
            }

            is ReadHoneyTipViewModel.ReadEvent.IncludeSwear -> {
                showToast(event.message)
            }

            else -> {}
        }
    }

    override fun initView() {
        binding.vm = viewModel
        postId = intent.getIntExtra("postId", 0)
        Log.d("read postid", postId.toString())
        binding.replyT.setOnSingleClickListener {
            viewModel.replyCommentParentId.value = Pair(0, "")
        }

        binding.commentEt.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && binding.commentEt.text.isEmpty()) {
                viewModel.replyCommentParentId.value = Pair(0, "")
                true
            } else {
                false
            }
        }

        binding.profileImg.setOnSingleClickListener {
            startActivity(OtherUserActivity.newIntent(this, binding.writerTv.text.toString()))
        }
        binding.commentRv.apply {
            adapter = commentRVA
            itemAnimator = null
        }
        binding.SendCardView.setOnSingleClickListener {
            viewModel.postQuestionComment(postId)
            binding.commentEt.setText("")
            viewModel.replyCommentParentId.value = Pair(0, "")
        }
        viewModel.inquireQuestion(postId)

        binding.backBtn.setOnSingleClickListener {
            finish()
        }

        initImageRVA()
        showReportDialog()
    }

    private fun initImageRVA() {
        binding.imageRv.adapter = imageAdapter
    }

    private fun showReportBtn() {
        binding.dotBtn.setOnSingleClickListener {
            if (!isShowMenu) {
                binding.reportBtn.bringToFront()
                binding.reportBtn.visibility = View.VISIBLE
                isShowMenu = true
            } else {
                binding.reportBtn.visibility = View.GONE
                isShowMenu = false
            }
        }
    }

    private fun showReportDialog() {
        binding.reportBtn.setOnSingleClickListener {
            val reportDialog = ReportDialogFragment()
            reportDialog.setDialogClickListener(object : ReportDialogFragment.DialogClickListener {


                override fun onClick(type: String, content: String) {
                    Log.d("report request", content.toString())
                    viewModel.reportQuestion(
                        postId,
                        ReportRequest(content = content, reportType = type)
                    )
                }
            })
            reportDialog.show(supportFragmentManager, reportDialog.tag)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isShowMenu && !isTouchInside(binding.dotBtn, ev?.x!!, ev?.y!!)) {
            if (!isTouchInside(binding.reportBtn, ev?.x!!, ev?.y!!)) {
                binding.reportBtn.visibility = View.GONE
                isShowMenu = false
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isTouchInside(view: View, x: Float, y: Float): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val realRight = location[0] + view.width
        val realBottom = location[1] + view.height
        return x >= location[0] && x <= realRight && y >= location[1] && y <= realBottom
    }

    override fun onClick(imageUrl: String, position: Int) {
        val images = imageAdapter.currentList.filterIsInstance<ImageUrl>().map { it.imageUrl }
            .toTypedArray()
        startActivity(ReadImageViewActivity.newIntent(this, images, position))
    }

    companion object {
        const val QUESTION = "postId"
        fun newIntent(context: Context, id: Int) =
            Intent(context, ReadQuestionActivity::class.java).apply {
                putExtra(QUESTION, id)
            }
    }
}