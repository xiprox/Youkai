package app.youkai.ui.feature.library_update.view

import android.content.Context
import android.view.View
import app.youkai.R
import app.youkai.progressview.ProgressView
import app.youkai.ui.feature.library_update.BaseLibraryUpdatePresenter
import app.youkai.ui.feature.library_update.MangaLibraryUpdatePresenter
import app.youkai.ui.feature.library_update.MangaStatusResolver
import app.youkai.util.ext.removeAllAndAdd
import kotlinx.android.synthetic.main.library_update.view.*
import kotlinx.android.synthetic.main.library_update_progress_manga.view.*

class MangaLibraryUpdateView(presenter: BaseLibraryUpdatePresenter, rootView: View, context: Context)
    : BaseLibraryUpdateView(presenter, rootView, context) {

    init {
        statusResolver = MangaStatusResolver(context)
        setStatusSpinner(R.array.manga_statuses)
        setStatusSpinnerListener(statusResolver)
        setProgressViews()
    }

    fun setChapterProgress(progress: Int) {
        if (rootView.progressContainer.findViewById<ProgressView>(R.id.chaptersProgressView) != null) rootView.chaptersProgressView.progress = progress
        else throw IllegalArgumentException("No chaptersProgressView was inflated.")
    }

    fun setMaxChapters(max: Int) {
        if (rootView.progressContainer.findViewById<ProgressView>(R.id.chaptersProgressView) != null) rootView.chaptersProgressView.max = max
        else throw IllegalArgumentException("No chaptersProgressView was inflated.")
    }

    fun setVolumeProgress(progress: Int) {
        if (rootView.progressContainer.findViewById<ProgressView>(R.id.volumesProgressView) != null) rootView.volumesProgressView.progress = progress
        else throw IllegalArgumentException("No volumesProgressView was inflated.")
    }

    fun setMaxVolumes(max: Int) {
        if (rootView.progressContainer.findViewById<ProgressView>(R.id.volumesProgressView) != null) rootView.volumesProgressView.max = max
        else throw IllegalArgumentException("No volumesProgressView was inflated.")
    }

    override fun setProgressViews() {
        val layout: Int = R.layout.library_update_progress_manga
        val chapters: ProgressView? = rootView.chaptersProgressView
        val volumes: ProgressView? = rootView.volumesProgressView
        container.removeAllAndAdd(layoutInflater, layout)
        // set listeners
        if (presenter is MangaLibraryUpdatePresenter) {
            chapters?.setListener { presenter.setProgress(chapters.progress) }
            volumes?.setListener { presenter.setVolumesProgress(volumes.progress) }
        }
    }

}