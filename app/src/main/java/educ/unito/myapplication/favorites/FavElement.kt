package educ.unito.myapplication.favorites

import androidx.compose.ui.graphics.painter.Painter
import educ.unito.myapplication.Post

data class FavElement(
    var user : String,
    var post : Post
) {

}