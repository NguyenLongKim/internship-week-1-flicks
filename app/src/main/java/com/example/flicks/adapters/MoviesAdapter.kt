package com.example.flicks.adapters

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.flicks.R
import com.example.flicks.databinding.ItemBinding
import com.example.flicks.databinding.PopularItemBinding
import com.example.flicks.listeners.ItemClickListener
import com.example.flicks.models.Item

class MoviesAdapter(private var itemList: List<Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val baseImageUrl = "https://image.tmdb.org/t/p/original"
        private const val POPULAR = 1
        private const val NORMAL = 0
    }

    private var itemClickListener: ItemClickListener? = null

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == NORMAL) {
            ViewHolderNormal(inflater.inflate(R.layout.item, parent, false))
        } else {
            ViewHolderPopular(inflater.inflate(R.layout.popular_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == NORMAL) {
            configureViewHolderNormal(holder as ViewHolderNormal, position)
        } else {
            configureViewHolderPopular(holder as ViewHolderPopular, position)
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position].vote_average < 8.0) {
            NORMAL
        } else {
            POPULAR
        }
    }

    private fun configureViewHolderNormal(holder: ViewHolderNormal, position: Int) {
        val currentItem = itemList[position]
        holder.binding.tvTitle.text = currentItem.title
        holder.binding.tvOverview.text = currentItem.overview
        val imagePath =
            baseImageUrl + if (holder.itemView.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                currentItem.poster_path
            } else {
                currentItem.backdrop_path
            }
        holder.binding.progressBar.visibility = View.VISIBLE
        Glide.with(holder.itemView.context)
            .load(imagePath)
            .transform(CenterInside(), RoundedCorners(16))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.progressBar.visibility = View.GONE
                    holder.binding.ivImage.setImageResource(R.drawable.ic_default_image)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(holder.binding.ivImage)
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(itemList[position])
        }
    }


    private fun configureViewHolderPopular(holder: ViewHolderPopular, position: Int) {
        val currentItem = itemList[position]
        if (holder.itemView.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            holder.binding.lnText.visibility = View.GONE
        } else {
            holder.binding.lnText.visibility = View.VISIBLE
            holder.binding.tvTitle.text = currentItem.title
            holder.binding.tvOverview.text = currentItem.overview
        }
        val imagePath = baseImageUrl + currentItem.backdrop_path
        holder.binding.progressBar.visibility = View.VISIBLE
        holder.binding.ivOverplay.visibility = View.GONE
        Glide.with(holder.itemView.context)
            .load(imagePath)
            .transform(CenterInside(), RoundedCorners(16))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.progressBar.visibility = View.GONE
                    holder.binding.ivOverplay.visibility = View.VISIBLE
                    holder.binding.ivImage.setImageResource(R.drawable.ic_default_image)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.progressBar.visibility = View.GONE
                    holder.binding.ivOverplay.visibility = View.VISIBLE
                    return false
                }
            }).into(holder.binding.ivImage)
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(itemList[position])
        }
    }

    class ViewHolderNormal(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemBinding = ItemBinding.bind(itemView)
    }

    class ViewHolderPopular(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: PopularItemBinding = PopularItemBinding.bind(itemView)
    }
}