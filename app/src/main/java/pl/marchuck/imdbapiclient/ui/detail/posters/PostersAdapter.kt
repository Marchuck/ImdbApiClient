package pl.marchuck.imdbapiclient.ui.detail.posters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.Disposable
import pl.marchuck.imdbapiclient.databinding.ItemPosterBinding
import pl.marchuck.imdbapiclient.imdb.ImageItem

class PosterAdapter : ListAdapter<ImageItem, PosterViewHolder>(PosterDiffCallback()) {

    var customCellWidth: Int? = null
        set(value) {
            if (value != null) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPosterBinding.inflate(inflater, parent, false)
        return PosterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(customCellWidth, getItem(position))
    }
}

private class PosterDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }
}

class PosterViewHolder(
    private val binding: ItemPosterBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var disposable: Disposable? = null

    fun bind(customCellWidth: Int?, item: ImageItem) {
        customCellWidth?.let { newWidth ->
            binding.root.layoutParams = FrameLayout.LayoutParams(newWidth, MATCH_PARENT)
        }
        disposable?.dispose()
        disposable = binding.cover.load(item.image)
        binding.title.text = item.title
    }
}
