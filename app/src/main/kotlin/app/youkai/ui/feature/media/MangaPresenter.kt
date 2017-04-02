package app.youkai.ui.feature.media

import android.util.Log
import app.youkai.App
import app.youkai.App.Companion.context
import app.youkai.R
import app.youkai.data.models.Anime
import app.youkai.data.models.Manga
import app.youkai.data.service.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Top-level media presenter that takes care of manga-specific functions.
 */
class MangaPresenter : BaseMediaPresenter() {

    override fun loadMedia(mediaId: String) {
        Api.manga(mediaId)
                .include(
                        "genres",
                        "castings.person",
                        /*"mediaRelationships.destination",*/
                        "reviews.user"
                )
                .get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        { m ->
                            run {
                                if (m != null) {
                                    setMedia(m)
                                }
                            }
                        },
                        // onError
                        { e ->
                            throw e
                            // TODO: Handle
                        },
                        // onComplete
                        {
                            Log.wtf("CCCCCCC", "")
                        }
                )
    }

    override fun setType() {
        view?.setType(
                when ((media as Manga?)?.mangaType) {
                    "manga" -> context.getString(R.string.media_type_manga_manga)
                    "novel" -> context.getString(R.string.media_type_manga_novel)
                    "manhua" -> context.getString(R.string.media_type_manga_manhua)
                    "oneshot" -> context.getString(R.string.media_type_manga_oneshot)
                    "doujin" -> context.getString(R.string.media_type_manga_doujin)
                    else -> "?"
                }
        )
    }
}