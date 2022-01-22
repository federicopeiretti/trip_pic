package educ.unito.myapplication

data class Favorites(
    var id : String,
    var user : String,
    var favorites: List<String>
) {

    override fun toString() : String = id

    val data : com.amplifyframework.datastore.generated.model.Favorites
        get() = com.amplifyframework.datastore.generated.model.Favorites
            .builder()
            .favoritesUserId(this.user)
            .post(this.favorites)
            .id(this.id)
            .build()

    companion object {
        fun from(favorite : com.amplifyframework.datastore.generated.model.Favorites) : Favorites {
            val result = Favorites(
                favorite.id,
                favorite.favoritesUserId,
                favorite.post
            )
            // some additional code will come here later
            return result
        }
    }

}