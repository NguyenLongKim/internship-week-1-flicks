package com.example.flicks

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var itemList:List<Item>
    private lateinit var adapter:Adapter
    private lateinit var recyclerView:RecyclerView
    private val BASE_URL = "https://api.themoviedb.org"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService:MovieDatabaseApi=retrofit.create(MovieDatabaseApi::class.java)
    private var pageCount=0

    private val itemClickListener = object : ItemClickListener {
        override fun onClick(item: Item) {
            Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity,VideoActivity::class.java)
            apiService.getTrailers(item.id,"a07e22bc18f5cb106bfe4cc1f83ad8ed")
                .enqueue(object:Callback<Trailers>{
                    override fun onResponse(call: Call<Trailers>, response: Response<Trailers>) {
                        intent.putExtra("video_key", response.body()?.youtube?.get(0)?.source)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Trailers>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

    private val endlessScrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1)) {
                loadMovies(10)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (savedInstanceState!=null){
            itemList= savedInstanceState.get("itemList") as List<Item>
            adapter= Adapter(itemList)
            recyclerView.adapter=adapter
            adapter.setItemClickListener(itemClickListener)
            recyclerView.addOnScrollListener(endlessScrollListener)
        }else{
            loadMovies(10)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("itemList",itemList as java.util.ArrayList<out Parcelable>)
    }

    private fun loadMovies(size: Int){
        apiService
            .getMovies("a07e22bc18f5cb106bfe4cc1f83ad8ed",++pageCount)
            .enqueue(object: Callback<Movies> {
                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    val newItems = response.body()?.results
                    if (this@MainActivity::adapter.isInitialized) {
                        val positionStart = itemList.size
                        (itemList as ArrayList).addAll(newItems as ArrayList)
                        adapter.notifyItemRangeInserted(positionStart,newItems.size)
                    }else{
                        itemList = newItems!!
                        adapter = Adapter(itemList)
                        recyclerView.adapter=adapter
                        adapter.setItemClickListener(itemClickListener)
                        recyclerView.addOnScrollListener(endlessScrollListener)
                    }
                }
                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    Log.d("Main","load failed")
                }
            })
    }
}