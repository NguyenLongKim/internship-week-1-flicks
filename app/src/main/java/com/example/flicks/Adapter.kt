package com.example.flicks

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class Adapter(private var itemList: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var itemClickListener: ItemClickListener? = null
    private var basePosterUrl = "https://image.tmdb.org/t/p/"
    private val POPULAR = 1
    private val NORMAL = 0

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType==NORMAL){
            ViewHolderNormal(inflater.inflate(R.layout.item,parent,false))
        }else{
            ViewHolderPopular(inflater.inflate(R.layout.popular_item,parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType==NORMAL){
            configureViewHolderNormal(holder as ViewHolderNormal,position)
        }else{
            configureViewHolderPopular(holder as ViewHolderPopular,position)
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position].vote_average<8.0){
            NORMAL
        }else{
            POPULAR
        }
    }

    private fun configureViewHolderNormal(holder:ViewHolderNormal,position:Int){
        val currentItem = itemList[position]
        val context = holder.itemView.context
        var imageUrl = if (context.resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
            basePosterUrl+"original/"+currentItem.poster_path
        } else{
            basePosterUrl+"original/"+currentItem.backdrop_path
        }
        holder.progressBar.visibility=View.VISIBLE
        Glide.with(context)
            .load(imageUrl)
            .listener(object:RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    return false
                }

            })
            .into(holder.imageView)
        holder.textView1.text = currentItem.title
        holder.textView2.text = currentItem.overview
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(currentItem)
        }
    }

    private fun configureViewHolderPopular(holder:ViewHolderPopular,position:Int){
        val currentItem = itemList[position]
        val context = holder.itemView.context
        var imageUrl  = basePosterUrl+"original/"+currentItem.backdrop_path
        if (context.resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT) {
            holder.layoutImage.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }else{
            holder.textView1.text = currentItem.title
            holder.textView2.text = currentItem.overview
        }
        holder.imageViewOverLoad.isVisible=false
        holder.progressBar.visibility=View.VISIBLE
        Glide.with(context)
            .load(imageUrl)
            .listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    holder.imageViewOverLoad.isVisible=true
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility=View.GONE
                    holder.imageViewOverLoad.isVisible=true
                    return false
                }

            })
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(currentItem)
        }
    }

    class ViewHolderNormal(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.im)
        val progressBar:ProgressBar = itemView.findViewById(R.id.progressbar)
        val textView1: TextView = itemView.findViewById(R.id.tv1)
        val textView2: TextView = itemView.findViewById(R.id.tv2)
    }

    class ViewHolderPopular(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layoutImage:RelativeLayout = itemView.findViewById(R.id.layout_image)
        val imageView: ImageView = itemView.findViewById(R.id.im)
        val progressBar:ProgressBar = itemView.findViewById(R.id.progressbar)
        val imageViewOverLoad:ImageView = itemView.findViewById(R.id.im_overload)
        val textView1: TextView = itemView.findViewById(R.id.tv1)
        val textView2: TextView = itemView.findViewById(R.id.tv2)
    }
}