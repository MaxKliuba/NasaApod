package com.android.maxclub.nasaapod.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.data.MediaType
import com.android.maxclub.nasaapod.databinding.ListItemFavoriteApodBinding
import com.android.maxclub.nasaapod.utils.formatDate
import com.squareup.picasso.Picasso
import java.util.*

class FavoritesAdapter(
    diffCallback: FavoritesDiffCallback,
    private val onClick: (FavoriteApod) -> Unit,
) :
    ListAdapter<FavoriteApod, FavoritesAdapter.FavoriteApodViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteApodViewHolder =
        FavoriteApodViewHolder(
            ListItemFavoriteApodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick,
        )

    override fun submitList(list: List<FavoriteApod>?) {
        super.submitList(
            list?.sortedByDescending { it.position }
        )
    }

    override fun onBindViewHolder(holder: FavoriteApodViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onBindViewHolder(
        holder: FavoriteApodViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == true) {
            // Nothing
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class FavoriteApodViewHolder(
        private val binding: ListItemFavoriteApodBinding,
        onClick: (FavoriteApod) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var currentFavoriteApod: FavoriteApod
        var isDragging: Boolean = false
            set(value) {
                field = value
                binding.dragIndicator.isVisible = value
            }

        init {
            binding.root.setOnClickListener {
                onClick(currentFavoriteApod)
            }
        }

        fun bind(favoriteApod: FavoriteApod) {
            currentFavoriteApod = favoriteApod
            binding.apply {
                val context = root.context
                when (favoriteApod.mediaType) {
                    MediaType.IMAGE -> Picasso.get()
                        .load(favoriteApod.url)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_image_placeholder_24)
                        .error(R.drawable.ic_error_image_placeholder_24)
                        .into(imageView)
                    MediaType.VIDEO -> imageView.setImageResource(R.drawable.ic_video_placeholder_24)
                    MediaType.UNKNOWN -> imageView.setImageResource(R.drawable.ic_unknow_media_placeholder_24)
                }
                titleTextView.text = favoriteApod.title
                dateTextView.text = formatDate(
                    favoriteApod.date,
                    context.getString(R.string.date_format_pattern),
                    Locale(
                        context.getString(R.string.language),
                        context.getString(R.string.country)
                    )
                )
                copyrightTextView.text = favoriteApod.copyright?.let { copyright ->
                    context.getString(R.string.copyright_placeholder, copyright)
                }
                newItemLabel.isVisible = favoriteApod.isNew
            }
        }
    }
}

class FavoritesDiffCallback : DiffUtil.ItemCallback<FavoriteApod>() {
    override fun areItemsTheSame(oldItem: FavoriteApod, newItem: FavoriteApod): Boolean =
        oldItem.date == newItem.date

    override fun areContentsTheSame(oldItem: FavoriteApod, newItem: FavoriteApod): Boolean =
        oldItem == newItem

    override fun getChangePayload(oldItem: FavoriteApod, newItem: FavoriteApod): Any? =
        if (oldItem.position != newItem.position) true else null
}