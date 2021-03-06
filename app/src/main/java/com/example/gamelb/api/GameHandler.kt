package com.example.gamelb.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gamelb.api.models.Game
import com.example.gamelb.api.models.GameResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameHandler(url: String) {

    val retrofit  = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val games: MutableLiveData<List<Game>> = MutableLiveData<List<Game>>()
    val upcomingGames: MutableLiveData<List<Game>> = MutableLiveData<List<Game>>()
    val nextUrl: MutableLiveData<String> = MutableLiveData("")
    val upcomingNextUrl: MutableLiveData<String> = MutableLiveData("")

    fun getAllGames(api_key: String, page: Int?){
        val service = retrofit.create(GameService::class.java)
        val call = service.getGameData(api_key,page)
        call.enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.code() == 200) {
                    val gameResponse = response.body()!!
                    nextUrl.postValue(gameResponse.next)
                    games.postValue(listOf(games.value.orEmpty(),gameResponse.results).flatten())
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                Log.e("GameHandler","Something went wrong")
            }
        })
    }

    fun getUpcomingGames(api_key: String,dates: String, page: Int?){
        val service = retrofit.create(GameService::class.java)
        val call = service.getGamesInDateRange(api_key,dates,page)
        call.enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.code() == 200) {
                    val gameResponse = response.body()!!
                    upcomingNextUrl.postValue(gameResponse.next)
                    upcomingGames.postValue(listOf(upcomingGames.value.orEmpty(),gameResponse.results).flatten())
                }
            }
            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                Log.e("GameHandler","Something went wrong")
            }
        })
    }
}