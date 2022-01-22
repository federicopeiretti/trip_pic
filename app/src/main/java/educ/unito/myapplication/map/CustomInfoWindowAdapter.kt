package educ.unito.myapplication.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import educ.unito.myapplication.PostMarkerItem
import educ.unito.myapplication.R


import android.view.LayoutInflater

import android.widget.ImageView

import android.net.Uri
import java.net.URL
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy


class CustomInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private var context : Context = context

    fun CustomInfoWindowAdapter(context: Context) {
        this.context = context
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getInfoWindow(
        marker: Marker
    ) : View? {
        val post : PostMarkerItem = marker.tag as PostMarkerItem
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fragment_map_pin, null)

        //val imgFile = File(post.getImagePath())
        //val filePath = getURLForResource(R.drawable.torino)

        //val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        val myImage = view.findViewById<ImageView>(R.id.image_post_marker)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val inputStream = URL(post.getImagePath()).openStream()
        val image = BitmapFactory.decodeStream(inputStream)
        myImage.setImageBitmap(image)
        //myImage.setImageBitmap(myBitmap)
        //myImage.setImageResource(R.drawable.torino)
        //myImage.setImageDrawable(Drawable.createFromPath(post.getImagePath()))
        return view
    }

    override fun getInfoContents(marker : Marker) : View? {
        return null
    }
}


fun getURLForResource(resourceId: Int): String? {
    //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
    return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resourceId).toString()
}