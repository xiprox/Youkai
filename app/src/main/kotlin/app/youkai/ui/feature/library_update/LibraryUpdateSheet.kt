package app.youkai.ui.feature.library_update

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import app.youkai.R
import app.youkai.data.models.Anime
import app.youkai.data.models.JsonType
import app.youkai.data.models.Manga
import app.youkai.data.models.Status
import app.youkai.progressview.ProgressView
import app.youkai.ui.feature.login.LoginActivity
import app.youkai.util.ext.getColorCompat
import app.youkai.util.ext.removeAllAndAdd
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import com.jakewharton.rxbinding2.widget.textChangeEvents
import kotlinx.android.synthetic.main.library_update.*
import kotlinx.android.synthetic.main.library_update.view.*
import kotlinx.android.synthetic.main.library_update_progress_anime.*
import kotlinx.android.synthetic.main.library_update_progress_anime.view.*
import kotlinx.android.synthetic.main.library_update_progress_manga.*
import kotlinx.android.synthetic.main.library_update_progress_manga.view.*

/**
 * Was intended to be
 * class LibraryUpdateSheet : MvpBottomSheetFragment<LibraryUpdateView, BaseLibraryUpdatePresenter>(), LibraryUpdateView
 * but could not get MvpBottomSheetFragment to work.
 */
class LibraryUpdateSheet : BottomSheetDialogFragment(), LibraryUpdateView {

    companion object {
        val ARGUMENT_LIBRARY_ENTRY_ID = "libraryEntryId"
        val ARGUMENT_ANIME_ID = "animeId"
        val ARGUMENT_MANGA_ID = "mangaId"
    }

    private var mediaType: JsonType? = null
    private var presenter: BaseLibraryUpdatePresenter? = null
    private var statusResolver: StatusResolver? = null
    private var aestheticsDelegate: AestheticsDelegate? = null

    /**
     * Lifecycle methods
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPresenterForArguments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.library_update, container, false)
        aestheticsDelegate = AestheticsDelegate(v, context, resources)
        setMediaTypeForArguments(v)
        setViewListeners(v)
        return v
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aestheticsDelegate?.applyLightColors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // manually calling presenter method
        presenter?.detachView(retainInstance = false)
        aestheticsDelegate = null
    }

    override fun onPause() {
        /**
         * TODO: change behaviour
         * Save when:
         *  - dismissed
         *  - screen off
         *  - switched app
         *  -
         */
        presenter?.postUpdate()

        super.onPause()
    }

    /**
     * Media type configuration
     */
    private fun setPresenterForArguments() {
        if (arguments.containsKey(ARGUMENT_LIBRARY_ENTRY_ID)) {
            presenter = BaseLibraryUpdatePresenter()
            presenter!!.getEntryById(arguments.getString(ARGUMENT_LIBRARY_ENTRY_ID))
        } else if (arguments.containsKey(ARGUMENT_ANIME_ID)) {
            //TODO: mediaType
            presenter = AnimeLibraryUpdatePresenter()
            (presenter as AnimeLibraryUpdatePresenter).getEntryByAnime(arguments.getString(ARGUMENT_ANIME_ID))
        } else if (arguments.containsKey(ARGUMENT_MANGA_ID)) {
            presenter = MangaLibraryUpdatePresenter()
            (presenter as MangaLibraryUpdatePresenter).getEntryByManga(arguments.getString(ARGUMENT_MANGA_ID))
        } else throw IllegalArgumentException("Cannot create a LibraryUpdateSheet without any relevant arguments.")
        presenter!!.attachView(this)
    }

    private fun setMediaTypeForArguments(rootView: View?) {
        if (arguments.containsKey(ARGUMENT_ANIME_ID)) {
            //TODO: mediaType
            setMediaType(JsonType("anime"), rootView)
        } else if (arguments.containsKey(ARGUMENT_MANGA_ID)) {
            setMediaType(JsonType("manga"), rootView)
        } else throw IllegalArgumentException("Cannot create a LibraryUpdateSheet without any relevant arguments.")
    }


    private fun mediaTypeIsAnime(mediaType: JsonType? = null): Boolean {
        val type: JsonType = mediaType ?: this.mediaType ?: return false
        return type.type == Anime().type.type
    }

    private fun mediaTypeIsManga(mediaType: JsonType? = null): Boolean {
        val type: JsonType = mediaType ?: this.mediaType ?: return false
        return type.type == Manga().type.type
    }
    /**
     * Use directly if calling this before [LibraryUpdateSheet.onCreateView] has finished.
     * [rootView] used
     */
    private fun setMediaType(mediaType: JsonType, rootView: View? = null) {
        if (!mediaTypeIsAnime(mediaType) && !mediaTypeIsManga(mediaType))
            throw IllegalArgumentException("Media type " + mediaType.type + " is unrecognised.")
        this.mediaType = mediaType
        setStatusResolverForMediaType(mediaType)
        replacePresenterForMediaType()
        configureViewForMediaType(rootView ?: view ?: throw IllegalStateException("No view to configure."))
    }

    /**
     * This exists so that the exposed interface remains simple and without View dependencies.
     */
    override fun setMediaType(mediaType: JsonType) = setMediaType(mediaType, view)

    private fun setStatusResolverForMediaType(mediaType: JsonType) {
        if (mediaTypeIsAnime()) {
            statusResolver = AnimeStatusResolver()
            statusResolver!!.init(context)
        } else if (mediaTypeIsManga()) {
            statusResolver = MangaStatusResolver()
            statusResolver!!.init(context)
        } else throw IllegalArgumentException("No status resolver for media type: " + mediaType)
    }

    private fun configureViewForMediaType(v: View) {
        setProgressViewForMediaType(v)
        setStatusSpinnerForMediaType(v)
    }

    private fun replacePresenterForMediaType() {
        val newPresenter: BaseLibraryUpdatePresenter =
                if (mediaTypeIsAnime()) AnimeLibraryUpdatePresenter()
                else if (mediaTypeIsManga()) MangaLibraryUpdatePresenter()
                else throw IllegalArgumentException("No library entry presenter for type: " + mediaType!!.type)
        if (presenter != null) newPresenter.libraryEntry = presenter!!.libraryEntry
        presenter = newPresenter
        presenter!!.attachView(this)
    }

    private fun setProgressViewForMediaType(rootView: View) {
        val layout: Int =
                if (mediaTypeIsAnime())
                    R.layout.library_update_progress_anime
                else if (mediaTypeIsManga())
                    R.layout.library_update_progress_manga
                else throw IllegalArgumentException("No progress layout for media type: " + mediaType!!.type)

        val container: ViewGroup =
                if (rootView.progressContainer != null) rootView.progressContainer
                else throw NullPointerException("No container to inflate progress views into.")

        val episodes: ProgressView? =
                if (rootView.episodesProgressView != null) rootView.episodesProgressView
                else null

        val chapters: ProgressView? =
                if (rootView.chaptersProgressView != null) rootView.chaptersProgressView
                else null

        val volumes: ProgressView? =
                if (rootView.volumesProgressView != null) rootView.volumesProgressView
                else null

        container.removeAllAndAdd(activity.layoutInflater, layout)

        // set listeners
        if (presenter is AnimeLibraryUpdatePresenter) {
            episodes?.setListener { (presenter as AnimeLibraryUpdatePresenter).setProgress(episodes.progress) }
        } else if (presenter is MangaLibraryUpdatePresenter) {
            chapters?.setListener { presenter!!.setProgress(chapters.progress) }
            volumes?.setListener { (presenter as MangaLibraryUpdatePresenter).setVolumesProgress(volumes.progress) }
        } else throw IllegalStateException("Presenter $presenter is unsuitable.")
    }

    private fun setStatusSpinnerForMediaType(rootView: View) {
        val statusAdapter = ArrayAdapter.createFromResource(
                context,
                if (mediaTypeIsAnime()) R.array.anime_statuses
                else if (mediaTypeIsManga()) R.array.manga_statuses
                else throw IllegalArgumentException("Cannot set status spinner for media type: " + mediaType?.type),
                R.layout.simple_spinner_item
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rootView.statusSpinner.adapter = statusAdapter
    }

    /**
     * View behaviour and data
     */
    private fun setViewListeners(rootView: View) {
        rootView.privacySwitch.setOnCheckedChangeListener { _, isPrivate ->
            presenter!!.setPrivate(isPrivate)
            setPrivate(isPrivate)
        }
        rootView.statusSpinner.itemSelections()
                .skipInitialValue()
                .doOnNext {
                    if (privacySwitch.isChecked) aestheticsDelegate?.setSpinnerSelectedDark()
                    else aestheticsDelegate?.setSpinnerSelectedLight()
                }
                .filter { rootView.statusSpinner.adapter != null }
                .map { rootView.statusSpinner.adapter.getItem(it).toString() }
                .doOnNext { if (statusResolver == null) throw IllegalStateException("Cannot set a status without a statusResolver") }
                .map { statusResolver!!.getItemStatus(it) }
                .filter { presenter != null }
                .doOnNext { presenter!!.setStatus(it) }
                .subscribe()
        //TODO: handle rating preferences
        rootView.ratingBar.setOnRatingChangeListener { _, rating -> presenter!!.setRating(rating) }
        rootView.notesInputEdit.textChangeEvents()
                .skipInitialValue()
                .subscribe { t -> presenter!!.setNotes(t.text().toString()) }
        rootView.removeButton.clicks().subscribe{ _ -> showRemovalConfirmationDialog() }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setPrivate(isPrivate: Boolean) {
        privacySwitch.isChecked = isPrivate
        aestheticsDelegate?.setPrivateBackground(isPrivate)
    }


    private fun showRemovalConfirmationDialog() {
        val dialog = AlertDialog.Builder(context, R.style.LibraryUpdateDialog)
                .setTitle(R.string.delete_confirmation_title)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(
                        R.string.delete_confirmation_positive,
                        { _, _ ->
                            presenter!!.removeLibraryEntry()
                        }
                )
                .setNeutralButton(
                        android.R.string.cancel,
                        { _, _ ->
                            //do nothing
                        }
                )
                .create()
        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(context.getColorCompat(R.color.text_gray_light))
    }

    override fun setStatus(status: Status) {
        this.statusSpinner.setSelection(statusResolver!!.getItemPosition(status))
    }

    override fun setReconsumedCount(reconsumedCount: Int) {
        reconsumedProgressView.progress = reconsumedCount
    }

    override fun setRating(rating: Float) {
        ratingBar.rating = rating
    }

    override fun setNotes(notes: String) {
        notesInputEdit.setText(notes)
    }

    override fun setEpisodeProgress(progress: Int) {
        if (mediaTypeIsAnime()) {
            if (progressContainer.findViewById<ProgressView>(R.id.episodesProgressView) != null) episodesProgressView.progress = progress
            else throw IllegalArgumentException("No episodesProgressView was inflated.")
        } else throw IllegalArgumentException("Cannot set episode progress for media type: " + mediaType!!.type)
    }

    override fun setMaxEpisodes(max: Int) {
        if (mediaTypeIsAnime()) {
            if (progressContainer.findViewById<ProgressView>(R.id.episodesProgressView) != null) episodesProgressView.max = max
            else throw IllegalArgumentException("No episodesProgressView was inflated.")
        } else throw IllegalArgumentException("Cannot set max episodes for media type: " + mediaType!!.type)
    }

    override fun setChapterProgress(progress: Int) {
        if (mediaTypeIsManga()) {
            if (progressContainer.findViewById<ProgressView>(R.id.chaptersProgressView) != null) chaptersProgressView.progress = progress
            else throw IllegalArgumentException("No chaptersProgressView was inflated.")
        } else throw IllegalArgumentException("Cannot set chapter progress for media type: " + mediaType!!.type)
    }

    override fun setMaxChapters(max: Int) {
        if (mediaTypeIsManga()) {
            if (progressContainer.findViewById<ProgressView>(R.id.chaptersProgressView) != null) chaptersProgressView.max = max
            else throw IllegalArgumentException("No chaptersProgressView was inflated.")
        } else throw IllegalArgumentException("Cannot set max chapters for media type: " + mediaType!!.type)
    }

    override fun setVolumeProgress(progress: Int) {
        if (mediaTypeIsManga()) {
            if (progressContainer.findViewById<ProgressView>(R.id.volumesProgressView) != null) volumesProgressView.progress = progress
            else throw IllegalArgumentException("No volumesProgressView was inflated.")
        } else throw IllegalArgumentException("Cannot set volume progress for media type: " + mediaType!!.type)
    }

    override fun setMaxVolumes(max: Int) {
        if (mediaTypeIsManga()) {
            if (progressContainer.findViewById<ProgressView>(R.id.volumesProgressView) != null) volumesProgressView.max = max
            else throw IllegalArgumentException("No volumesProgressView was inflated.")
        } else throw IllegalArgumentException("Cannot set max volumes for media type: " + mediaType!!.type)
    }

    /**
     * Login boilerplate
     */
    override fun sendToLogin()
            = startActivityForResult(Intent(context, LoginActivity::class.java), LoginActivity.START_FOR_ACCESS_TOKEN)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginActivity.START_FOR_ACCESS_TOKEN && resultCode == LoginActivity.RESULT_OK) {
            presenter?.postUpdate()
        }
    }

}