package educ.unito.myapplication


data class User (
    var id : String,
    var username : String,
    var description : String,
    var location : String,
    var image : String,
    var locationName: String? = null,
    var locationLat: Double? = null,
    var locationLng: Double? = null,
    var imagePath : String? = null
) {
    override fun toString() : String = username

    val data : com.amplifyframework.datastore.generated.model.User
        get() = com.amplifyframework.datastore.generated.model.User
            .builder()
            .user(this.username)
            .description(this.description)
            .userLocationId(this.location)
            .id(this.id)
            .image(this.image)
            .build()


    companion object {
        fun from(user : com.amplifyframework.datastore.generated.model.User) : User {
            val result = User(
                user.id,
                user.user,
                user.description,
                user.userLocationId,
                user.image,
                user.location.name,
                user.location.lat,
                user.location.lng
            )
            // some additional code will come here later
            return result
        }
    }
}