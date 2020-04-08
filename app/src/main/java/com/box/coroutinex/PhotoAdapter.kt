package com.box.coroutinex

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.box.coroutinex.data.Hits
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.photo_item.view.*

class PhotoAdapter(var list: ArrayList<Hits>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    //딱히 효용이 없다. glide의 이미지가 완전히 로드가 끝나는 시점을 못 찾네.
    interface LoadingCheck {
        fun isLoadingEnd(boolean: Boolean)
    }

    var loadingCheck: LoadingCheck? = null

    //
    interface ItemClick {
        fun itemClickListener(url: String,position: Int)
    }

    var itemClick: ItemClick? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size


    private val requestOptions =
        RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).override(300,200)
            .placeholder(R.drawable.ic_launcher_foreground).fitCenter()

    private val requestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            loadingCheck?.isLoadingEnd(true)
            return false
        }
    }
    override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {

        holder.itemView.apply {
            Glide.with(context).load(list[position].webformatURL)
//                .apply(requestOptions)
                .placeholder(android.R.color.white)
                .listener(requestListener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(list[position].webformatWidth.toInt() / 2,list[position].webformatWidth.toInt())
                .fitCenter()
                .dontAnimate()
                .into(iamge_item)

            if (itemClick != null) {
                iamge_item.setOnClickListener {
                    itemClick!!.itemClickListener(list[position].largeImageURL!!,position)
                }
            }
        }
    }

    fun setHitsList(hitsList: ArrayList<Hits>) {
        list = hitsList
        notifyDataSetChanged()
    }

    fun addHitsList(hits: ArrayList<Hits>) {
        list.addAll(hits)
        Log.e("list size", list.size.toString())
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return list[position].id.toLong()
    }

}