package com.project.ecorangeestimater.response


import com.project.trafficpulse.Response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @GET("user.php")
    fun userRegister(
        @Query("name") name: String,
        @Query("mobile") mobile: String,
        @Query("password") password: String,
        @Query("location") location: String,
        @Query("email") email: String,
        @Query("role") role: String,
        @Query("type") type: String,
    ): Call<CommonResponse>


    @FormUrlEncoded
    @POST("addEvStation.php")
    fun addEVStation(
        @Field("stationName") stationName: String,
        @Field("stationMobile") stationMobile: String,
        @Field("location") location: String,
        @Field("chargingpoints") chargingPoints: String,
        @Field("type") type: String,
        @Field("role") role: String,
        @Field("adapterTypes[]") adapterTypes: List<String>, // Retrofit handles multiple fields with the same name
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("login.php")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Call<CommonResponse>

    @GET("getEntries.php")
    fun getRole(
        @Query("role") role: String,
    ): Call<LoginResponse>

    @GET("evStation.php")
    fun getStationsEV(): Call<LoginResponse>


}
