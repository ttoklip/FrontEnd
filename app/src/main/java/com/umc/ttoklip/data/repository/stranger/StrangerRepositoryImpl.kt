package com.umc.ttoklip.data.repository.stranger

import com.umc.ttoklip.data.api.OtherApi
import com.umc.ttoklip.data.model.ResponseBody
import com.umc.ttoklip.data.model.stranger.OtherUserInfoResponse
import com.umc.ttoklip.module.NetworkResult
import com.umc.ttoklip.module.handleApi
import javax.inject.Inject

class StrangerRepositoryImpl @Inject constructor(
    private val api : OtherApi
) : StrangerRepository {

    override suspend fun getStranger(nick: String): NetworkResult<OtherUserInfoResponse> {
        return handleApi({api.getStrangeInfo(nick)}) {response: ResponseBody<OtherUserInfoResponse> ->response.result}
    }


}