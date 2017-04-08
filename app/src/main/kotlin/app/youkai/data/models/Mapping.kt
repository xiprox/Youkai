package app.youkai.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.jasminb.jsonapi.Links
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.RelationshipLinks
import com.github.jasminb.jsonapi.annotations.Type

@Type("mappings") @JsonIgnoreProperties(ignoreUnknown = true)
class Mapping : BaseJsonModel(JsonType("mappings")) {

    companion object FieldNames {
        val EXTERNAL_SITE = "externalSite"
        val EXTERNAL_ID = "externalId"
        val MEDIA = "media"
    }

    var externalSite: String? = null

    var externalId: String? = null

    //TODO: Fix for polymorphism.
    @Relationship("media")
    var media: Media? = null

    @RelationshipLinks("media")
    var mediaLinks: Links? = null

}
