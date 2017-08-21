package app.youkai.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type

@JsonIgnoreProperties(ignoreUnknown = true)
@Type("media")
//TODO: rename to Media
open class BaseMedia(type: JsonType = JsonType("media")) : BaseJsonModel(type) {

    companion object FieldNames {
        val SLUG = "slug"
        val SYNOPSIS = "synopsis"
        val COVER_IMAGE_TOP_OFFSET = "coverImageTopOffset"
        val TITLES = "titles"
        val CANONICAL_TITLE = "canonicalTitle"
        val ABBREVIATED_TITLES = "abbreviatedTitles"
        val AVERAGE_RATING = "averageRating"
        val RATING_FREQUENCIES = "ratingFrequencies"
        val FAVORITES_COUNT = "favoritesCount"
        val START_DATE = "startDate"
        val END_DATE = "endDate"
        val POPULARITY_RANK = "popularityRank"
        val RATING_RANK = "ratingRank"
        val AGE_RATING = "ageRating"
        val AGE_RATING_GUIDE = "ageRatingGuide"
        val POSTER_IMAGE = "posterImage"
        val COVER_IMAGE = "coverImage"
        val SUBTYPE = "subtype"
        val GENRES = "genres"
        val CATEGORIES = "categories"
        val CASTINGS = "castings"
        val INSTALLMENTS = "installments"
        val MAPPINGS = "mappings"
        val MEDIA_RELATIONSHIPS = "mediaRelationships"
        val REVIEWS = "reviews"
    }

    var slug: String? = null

    var synopsis: String? = null

    var coverImageTopOffset: Int? = null

    var titles: Titles? = null

    var canonicalTitle: String? = null

    var abbreviatedTitles: List<String>? = null

    var averageRating: Float? = null

    var ratingFrequencies: Map<String, String>? = null

    var favoritesCount: Int? = null

    var startDate: String? = null

    var endDate: String? = null

    var popularityRank: Int? = null

    var ratingRank: Int? = null

    var ageRating: String? = null

    var ageRatingGuide: String? = null

    var posterImage: PosterImage? = null

    var coverImage: CoverImage? = null

    var subtype: String? = null

    /**
     * Heads up: you probably want [categories] rather than [genres].
     */
    @Relationship("genres")
    var genres: List<Genre>? = null

    @RelationshipLinks("genres")
    var genreLinks: Links? = null

    @Relationship("categories")
    var categories: List<Category>? = null

    @RelationshipLinks("categories")
    var categoryLinks: Links? = null

    @Relationship("castings")
    var castings: List<Casting>? = null

    @RelationshipLinks("castings")
    var castingLinks: Links? = null

    @Relationship("installments")
    var installments: List<Installment>? = null

    @RelationshipLinks("installments")
    var installmentLinks: Links? = null

    @Relationship("mappings")
    var mappings: List<Mapping>? = null

    @RelationshipLinks("mappings")
    var mappingLinks: Links? = null

    @Relationship("mediaRelationships")
    var medias: List<MediaRelationship>? = null

    @RelationshipLinks("mediaRelationships")
    var mediaLinks: Links? = null

    @Relationship("reviews")
    var reviews: List<Review>? = null

    @RelationshipLinks("reviews")
    var reviewLinks: Links? = null

}
