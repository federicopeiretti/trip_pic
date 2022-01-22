package educ.unito.myapplication

data class Location (
    var id : String,
    var lat : Double,
    var lng : Double,
    var name : String
) {
    override fun toString() : String = name

    val data : com.amplifyframework.datastore.generated.model.Location
        get() = com.amplifyframework.datastore.generated.model.Location
            .builder()
            .lat(this.lat)
            .lng(this.lng)
            .name(this.name)
            .id(this.id)
            .build()

    companion object {
        fun from(location : com.amplifyframework.datastore.generated.model.Location) : Location {
            val result = Location(
                location.id,
                location.lat,
                location.lng,
                location.name
            )
            // some additional code will come here later
            return result
        }
    }
}