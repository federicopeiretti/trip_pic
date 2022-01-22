package educ.unito.myapplication

data class Friend(
    var id : String,
    var user : String,
    var friends: List<String>
) {

    override fun toString() : String = id

    val data : com.amplifyframework.datastore.generated.model.Friend
        get() = com.amplifyframework.datastore.generated.model.Friend
            .builder()
            .friendUserId(this.user)
            .friends(this.friends)
            .build()

    companion object {
        fun from(friend : com.amplifyframework.datastore.generated.model.Friend) : Friend {
            val result = Friend(
                friend.id,
                friend.friendUserId,
                friend.friends
            )
            // some additional code will come here later
            return result
        }
    }

}