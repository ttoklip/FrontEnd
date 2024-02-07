package com.umc.ttoklip.data.repository.honeytip

import com.umc.ttoklip.data.model.CreateHoneyTipResponse
import com.umc.ttoklip.data.model.ResponseBody
import com.umc.ttoklip.module.NetworkResult
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface HoneyTipRepository {
    suspend fun createHoneyTip(request: RequestBody, uri: List<MultipartBody.Part>): NetworkResult<CreateHoneyTipResponse>
}