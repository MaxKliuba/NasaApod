package com.android.maxclub.nasaapod.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.databinding.ListItemNewFavoriteApodBinding
import com.android.maxclub.nasaapod.databinding.ListItemOldFavoriteApodBinding
import com.android.maxclub.nasaapod.utils.formatDate
import com.squareup.picasso.Picasso
import java.util.*

class FavoritesAdapter(
    diffCallback: FavoritesDiffCallback,
    private val onClick: (FavoriteApod) -> Unit,
) :
    ListAdapter<FavoriteApod, FavoritesAdapter.FavoriteApodViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteApodViewHolder =
        when (viewType) {
            NEW_ITEM -> NewFavoriteApodViewHolder(
                ListItemNewFavoriteApodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClick,
            )
            else -> OldFavoriteApodViewHolder(
                ListItemOldFavoriteApodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClick,
            )
        }

    override fun submitList(list: List<FavoriteApod>?) {
        super.submitList(
            list?.sortedByDescending { it.position }
        )
    }

    override fun onBindViewHolder(holder: FavoriteApodViewHolder, position: Int) =
        holder.bind(getItem(position))

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).isNew) NEW_ITEM else OLD_ITEM

    abstract class FavoriteApodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        protected lateinit var currentFavoriteApod: FavoriteApod

        abstract fun bind(favoriteApod: FavoriteApod)
    }

    class NewFavoriteApodViewHolder(
        private val binding: ListItemNewFavoriteApodBinding,
        onClick: (FavoriteApod) -> Unit,
    ) : FavoriteApodViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClick(currentFavoriteApod)
            }
        }

        override fun bind(favoriteApod: FavoriteApod) {
            currentFavoriteApod = favoriteApod
            binding.apply {
                val context = root.context
                Picasso.get()
                    .load(favoriteApod.url)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_image_error_placeholder_24)
                    .placeholder(R.drawable.ic_image_placeholder_24)
                    .into(imageView)
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
            }
        }
    }

    class OldFavoriteApodViewHolder(
        private val binding: ListItemOldFavoriteApodBinding,
        onClick: (FavoriteApod) -> Unit,
    ) : FavoriteApodViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClick(currentFavoriteApod)
            }
        }

        override fun bind(favoriteApod: FavoriteApod) {
            currentFavoriteApod = favoriteApod
            binding.apply {
                val context = root.context
                Picasso.get()
                    .load(favoriteApod.url)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_image_error_placeholder_24)
                    .placeholder(R.drawable.ic_image_placeholder_24)
                    .into(imageView)
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
            }
        }
    }

    companion object {
        private const val NEW_ITEM = 0
        private const val OLD_ITEM = 1
    }
}

class FavoritesDiffCallback : DiffUtil.ItemCallback<FavoriteApod>() {
    override fun areItemsTheSame(oldItem: FavoriteApod, newItem: FavoriteApod): Boolean =
        oldItem.date == newItem.date

    override fun areContentsTheSame(oldItem: FavoriteApod, newItem: FavoriteApod): Boolean =
        oldItem == newItem
}