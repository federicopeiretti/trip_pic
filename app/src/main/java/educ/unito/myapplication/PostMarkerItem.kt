package educ.unito.myapplication

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


class PostMarkerItem(lat : Double, lng : Double, title : String, imagePath : String, user : String) : ClusterItem {

    private var mPosition = LatLng(lat, lng)
    private var mTitle = title
    private var mSnippet: String? = null
    private var imagePath = imagePath
    private var user = user

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getSnippet(): String? {
        return mSnippet
    }

    fun getImagePath() : String {
        return imagePath
    }

    fun getUser() : String {
        return user
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    fun setSnippet(snippet: String) {
        mSnippet = snippet
    }

    fun setImagePath(imagePath : String) {
        this.imagePath = imagePath
    }

    fun setUser(user : String) {
        this.user = user
    }

    fun setPosition(lat : Double, lng : Double) {
        this.mPosition = LatLng(lat,lng)
    }


}