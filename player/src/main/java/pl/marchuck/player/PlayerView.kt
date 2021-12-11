package pl.marchuck.player

import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.startActivity
import coil.load
import pl.marchuck.player.databinding.ViewPlayerBinding

class PlayerView(context: Context, attributeSet: AttributeSet?) :
    FrameLayout(context, attributeSet) {

    private val binding = ViewPlayerBinding.inflate(LayoutInflater.from(context), this)

    fun setup(callingContext: Context, url: String, thumbnail: String) {
        //todo: setup embedded player
        binding.videoViewThumbnail.load(thumbnail)
        binding.videoViewPlay.setOnClickListener {
            val title = resources.getString(R.string.imdb__watch_trailer)
            val targetIntent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
            val intent = createChooser(targetIntent, title)
            startActivity(callingContext, intent, null)
        }
    }
}
