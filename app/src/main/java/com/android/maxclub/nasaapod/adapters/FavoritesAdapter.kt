package com.android.maxclub.nasaapod.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.maxclub.nasaapod.R
import com.android.maxclub.nasaapod.data.FavoriteApod
import com.android.maxclub.nasaapod.databinding.ListItemFavoriteApodBinding
import com.android.maxclub.nasaapod.utils.formatDate
import com.squareup.picasso.Picasso
import java.util.*

class FavoritesAdapter(
    diffCallback: FavoritesDiffCallback,
    private val onClick: (FavoriteApod) -> Unit,
) :
    ListAdapter<FavoriteApod, FavoritesAdapter.FavoritesViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder =
        FavoritesViewHolder(
            ListItemFavoriteApodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick,
        )

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) =
        holder.bind(getItem(position))

    class FavoritesViewHolder(
        private val binding: ListItemFavoriteApodBinding,
        onClick: (FavoriteApod) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var currentFavoriteApod: FavoriteApod

        init {
            binding.root.setOnClickListener {
                onClick(currentFavoriteApod)
            }
        }

        fun bind(favoriteApod: FavoriteApod) {
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
}

class FavoritesDiffCallback : DiffUtil.ItemCallback<FavoriteApod>() {
    override fun areItemsTheSame(oldItem: FavoriteApod, newItem: FavoriteApod): Boolean =
        oldItem.date == newItem.date

    override fun areContentsTheSame(oldItem: FavoriteApod, newItem: FavoriteApod): Boolean =
        oldItem == newItem
}