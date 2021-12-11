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
import pl.marchuck.imdbapiclient.imdb.Actor
import pl.marchuck.imdbapiclient.imdb.ImageItem

class ActorsAdapter : ListAdapter<Actor, ActorViewHolder>(ActorsDiffCallback()) {

    var customCellWidth: Int? = null
        set(value) {
            if (value != null) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPosterBinding.inflate(inflater, parent, false)
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(customCellWidth, getItem(position))
    }
}

private class ActorsDiffCallback : DiffUtil.ItemCallback<Actor>() {
    override fun areItemsTheSame(oldItem: Actor, newItem: Actor): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Actor, newItem: Actor): Boolean {
        return oldItem == newItem
    }
}

class ActorViewHolder(
    private val binding: ItemPosterBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var disposable: Disposable? = null

    fun bind(customCellWidth: Int?, item: Actor) {
        customCellWidth?.let { newWidth ->
            binding.root.layoutParams = FrameLayout.LayoutParams(newWidth, MATCH_PARENT)
        }
        disposable?.dispose()
        disposable = binding.cover.load(item.image)
        binding.title.text = item.name
    }
}
