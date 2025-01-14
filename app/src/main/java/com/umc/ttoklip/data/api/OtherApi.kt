package com.umc.ttoklip.data.api

import com.umc.ttoklip.data.model.CommonResponse
import com.umc.ttoklip.data.model.ResponseBody
import com.umc.ttoklip.data.model.mypage.MyHoneyTipsResponse
import com.umc.ttoklip.data.model.news.ReportRequest
import com.umc.ttoklip.data.model.stranger.OtherUserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OtherApi {

    @GET("/api/v1/stranger")
    suspend fun getStrangeInfo(
        @Query("nickname") nickname : String,
    ): Response<ResponseBody<OtherUserInfoResponse>>

    @GET("/api/v1/stranger/honeytip/{userId}")
    suspend fun getStrangerTip(
        @Path("userId") userId : Int,
        @Query("page") page : Int,
    ): Response<ResponseBody<MyHoneyTipsResponse>>

    @POST("/api/v1/member/report")
    suspend fun postReportUserApi(
        @Query("nickName") nickname: String,
        @Body request : ReportRequest
    ): Response<ResponseBody<CommonResponse>>
}