package com.android.maxclub.nasaapod.presentation.favorites_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.domain.model.MediaType
import com.android.maxclub.nasaapod.databinding.ListItemFavoriteApodBinding
import com.android.maxclub.nasaapod.domain.model.FavoriteApod
import com.android.maxclub.nasaapod.presentation.favorites_list.FavoritesDiffCallback.Companion.POSITION_CHANGE_PAYLOAD
import com.android.maxclub.nasaapod.util.formatDate
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

    @SuppressLint("NotifyDataSetChanged")
    override fun submitList(list: List<FavoriteApod>?) {
        super.submitList(list)
        if (list?.size == 1) {
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: FavoriteApodViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun onBindViewHolder(
        holder: FavoriteApodViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.first() == POSITION_CHANGE_PAYLOAD) {
            holder.bindData(getItem(position))
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

        fun bindData(favoriteApod: FavoriteApod) {
            currentFavoriteApod = favoriteApod
        }

        fun bind(favoriteApod: FavoriteApod) {
            bindData(favoriteApod)
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
        if (oldItem.position != newItem.position) POSITION_CHANGE_PAYLOAD else null

    companion object {
        const val POSITION_CHANGE_PAYLOAD = 0
    }
}