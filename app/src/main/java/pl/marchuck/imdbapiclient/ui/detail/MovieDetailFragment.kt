package pl.marchuck.imdbapiclient.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearSnapHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.marchuck.imdbapiclient.R
import pl.marchuck.imdbapiclient.common.autoCleared
import pl.marchuck.imdbapiclient.databinding.FragmentMovieDetailBinding
import pl.marchuck.imdbapiclient.databinding.ItemToolbarBinding
import pl.marchuck.imdbapiclient.imdb.Actor
import pl.marchuck.imdbapiclient.imdb.ImageItem
import pl.marchuck.imdbapiclient.imdb.JobInfo
import pl.marchuck.imdbapiclient.imdb.TrailerResponse
import pl.marchuck.imdbapiclient.ui.detail.posters.ActorsAdapter
import pl.marchuck.imdbapiclient.ui.detail.posters.PosterAdapter
import kotlin.math.roundToInt

class MovieDetailFragment : Fragment() {

    private var binding by autoCleared<FragmentMovieDetailBinding>()

    private val viewModel by viewModel<MovieDetailViewModel>()

    private val movieDurationFormatter by lazy { MovieDetailFormatter(resources) }

    private val actorsAdapter = ActorsAdapter()
    private val postersAdapter = PosterAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieId = resolveMovieId(requireArguments())
        val movieName = resolveMovieName(requireArguments())

        configureToolbar(binding.toolbar, movieName)

        binding.root.doOnPreDraw {
            val totalWidth = it.measuredWidth
            configurePostersRecyclerView(totalWidth)
            configureActorsRecyclerView(totalWidth)
            viewModel.viewState.asLiveData().observe(viewLifecycleOwner, ::renderViewState)
            viewModel.initialize(movieId)
        }
    }

    private fun configurePostersRecyclerView(totalWidth: Int) {
        val cellWidth = (totalWidth.toFloat() / 1.2f).roundToInt()
        val horizontalPadding = (totalWidth - cellWidth) / 2
        binding.postersRecyclerView.updatePadding(
            left = horizontalPadding,
            right = horizontalPadding
        )
        binding.postersRecyclerView.adapter = postersAdapter.apply {
            this.customCellWidth = cellWidth
        }
        LinearSnapHelper().attachToRecyclerView(binding.postersRecyclerView)
    }

    private fun configureActorsRecyclerView(totalWidth: Int) {
        val cellWidth = (totalWidth.toFloat() / 2.2f).roundToInt()
        val horizontalPadding = (totalWidth - cellWidth) / 2
        binding.actorsRecyclerView.updatePadding(
            left = horizontalPadding,
            right = horizontalPadding
        )
        binding.actorsRecyclerView.adapter = actorsAdapter.apply {
            this.customCellWidth = cellWidth
        }
        LinearSnapHelper().attachToRecyclerView(binding.actorsRecyclerView)
    }

    private fun configureToolbar(toolbar: ItemToolbarBinding, movieName: String) {
        toolbar.title.updatePadding(left = 0)
        toolbar.title.text = movieName
        toolbar.buttonBack.isVisible = true
        toolbar.buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun renderViewState(state: MovieResponseState) = when (state) {
        MovieResponseState.FailedToFetch -> {
            binding.contentRoot.isVisible = false
            binding.progress.isVisible = false
            binding.noResults.isVisible = true
            binding.noResultsText.setText(R.string.imdb__something_went_wrong)
        }
        MovieResponseState.ApiLimit -> {
            binding.contentRoot.isVisible = false
            binding.progress.isVisible = false
            binding.noResultsText.setText(R.string.imdb__search_api_limit)
        }
        MovieResponseState.Loading -> {
            binding.contentRoot.isVisible = false
            binding.progress.isVisible = true
        }
        is MovieResponseState.Ready -> {
            binding.progress.isVisible = false

            binding.contentRoot.isVisible = true
            renderMetadata(
                state.response.fullCast.directors,
                state.response.fullCast.writers,
                state.response.movieDurationInMinutes,
                state.response.rating,
                state.response.plot,
            )
            renderImages(state.response.images?.items.orEmpty())
            renderCast(state.response.fullCast.actors)
            renderTrailerIfAny(state.response.trailer)
        }
    }

    private fun renderTrailerIfAny(trailer: TrailerResponse?) {
        if (trailer?.videoUrl == null) {
            binding.trailerLabel.isVisible = false
            binding.playerView.isVisible = false
        } else {
            binding.trailerLabel.isVisible = true
            binding.playerView.isVisible = true
            binding.playerView.setup(
                requireActivity(),
                trailer.videoUrl,
                trailer.thumbnailUrl,
            )
        }
    }

    private fun renderMetadata(
        directors: JobInfo,
        writers: JobInfo,
        durationInMinutes: String,
        rating: String,
        plot: String
    ) {
        val ratingText = if (rating.isNotBlank()) "<b>IMDB rating</b>: ${rating}<br/>" else ""

        val contentTextHtml = "${movieDurationFormatter.formatDuration(durationInMinutes)}<br/>" +
                ratingText +
                "${movieDurationFormatter.formatJobInfo(directors.job, directors.items)}<br/>" +
                "${movieDurationFormatter.formatJobInfo(writers.job, writers.items)}<br/>" +
                "<b>Plot</b><br/>" + plot
        binding.ratingLabel.text =
            HtmlCompat.fromHtml(contentTextHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun renderImages(images: List<ImageItem>) {
        binding.postersRecyclerView.isVisible = images.isNotEmpty()
        postersAdapter.submitList(images)
    }

    private fun renderCast(cast: List<Actor>) {
        binding.actorsRecyclerView.isVisible = cast.isNotEmpty()
        actorsAdapter.submitList(cast)
    }

    companion object {

        private const val KEY_MOVIE_ID = "KEY_MOVIE_ID"
        private const val KEY_MOVIE_NAME = "KEY_MOVIE_NAME"

        @JvmStatic
        private fun resolveMovieId(bundle: Bundle): String {
            return requireNotNull(bundle.getString(KEY_MOVIE_ID)) {
                "got nullable KEY_MOVIE_ID"
            }
        }

        @JvmStatic
        private fun resolveMovieName(bundle: Bundle): String {
            return bundle.getString(KEY_MOVIE_NAME).orEmpty()
        }

        @JvmStatic
        fun newInstance(movieId: String, movieName: String): MovieDetailFragment {
            return MovieDetailFragment().apply {
                arguments = bundleOf(
                    KEY_MOVIE_ID to movieId,
                    KEY_MOVIE_NAME to movieName
                )
            }
        }
    }
}
