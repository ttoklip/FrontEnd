package com.umc.ttoklip.data.api

import com.umc.ttoklip.data.model.ResponseBody
import com.umc.ttoklip.data.model.mypage.UserStreetResponse
import com.umc.ttoklip.data.model.news.detail.NewsDetailResponse
import com.umc.ttoklip.data.model.search.NewsSearchResponse
import com.umc.ttoklip.data.model.search.TipSearchResponse
import com.umc.ttoklip.data.model.search.TogetherSearchResponse
import com.umc.ttoklip.data.model.search.TownSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Search2Api {

    @GET("/api/v1/search/newsletter")
    suspend fun getSearchNewsApi(
        @Query("title") title : String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Response<ResponseBody<NewsSearchResponse>>

    @GET("/api/v1/search/honeytip")
    suspend fun getSearchTipApi(
        @Query("title") title : String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Response<ResponseBody<TipSearchResponse>>


    @GET("/api/v1/search/community")
    suspend fun getSearchTownApi(
        @Query("title") title : String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Response<ResponseBody<TownSearchResponse>>

    @GET("/api/v1/search/cart")
    suspend fun getSearchCartApi(
        @Query("title") title : String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Response<ResponseBody<TogetherSearchResponse>>

    @GET("/api/v1/member/street")
    suspend fun getStreet(): Response<ResponseBody<UserStreetResponse>>


}