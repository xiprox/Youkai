package app.youkai.ui.feature.media.view

import android.view.View
import app.youkai.App
import app.youkai.R
import app.youkai.data.models.MediaRelationship
import app.youkai.util.color
import app.youkai.util.ext.getLayoutInflater
import app.youkai.util.ext.loadWithPalette
import kotlinx.android.synthetic.main.item_media_related.view.*

/**
 * A simple helper class to allow easy item creation behind the scenes.
 */
object RelatedMediaItemCreator {

    /**
     * Inflates a new related media item, sets its data to a given data, and returns the resulting view.
     */
    fun new(data: MediaRelationship): View {
        val view = App.context.getLayoutInflater().inflate(R.layout.item_media_related, null, false)
        val url = data.source?.posterImage?.tiny
        view.poster.loadWithPalette(url, {
            view.card.setBackgroundColor(it.darkMutedSwatch?.rgb ?: color(R.color.primary))
            view.title.setTextColor(it.darkMutedSwatch?.titleTextColor ?: color(android.R.color.white))
            view.type.setTextColor(it.darkMutedSwatch?.titleTextColor ?: color(android.R.color.white))
        })
        view.poster.setImageURI(data.source?.posterImage?.medium)

        view.title.text = data.source?.canonicalTitle
        view.type.text = data.role

        return view
    }
}