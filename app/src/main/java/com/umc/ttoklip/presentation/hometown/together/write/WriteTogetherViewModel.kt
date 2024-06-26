package com.umc.ttoklip.presentation.hometown.together.write

import com.umc.ttoklip.presentation.honeytip.adapter.Image
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface WriteTogetherViewModel {
    val totalPrice: StateFlow<Long>
    val totalMember: StateFlow<Long>
    val title: StateFlow<String>
    val dealPlace: StateFlow<String>
    val openLink: StateFlow<String>
    val content: StateFlow<String>
    val extraUrl: StateFlow<String>
    val doneButtonActivated: StateFlow<Boolean>
    val doneWriteTogether: StateFlow<Boolean>
    val closePage: StateFlow<Boolean>
    val images: StateFlow<List<Image>>
    val postId: StateFlow<Long>
    val address: StateFlow<String>
    val addressDetail: StateFlow<String>

    fun setTotalPrice(totalPrice: Long)
    fun setTotalMember(totalMember: Long)

    fun setAddress(address: String)

    fun setAddressDetail(addressDetail: String)
    fun addImages(images: List<Image>)
    fun checkDone()
    fun doneButtonClick()
    fun writeTogether()
}