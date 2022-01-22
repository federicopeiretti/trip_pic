package educ.unito.myapplication.favorites

import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Observer
import coil.compose.rememberImagePainter
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import educ.unito.myapplication.*
import educ.unito.myapplication.R
import educ.unito.myapplication.profile.PostPopup
import java.io.InputStream
import java.util.*

@ExperimentalFoundationApi
@Composable
fun FavouriteScreen(){

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    )
    Column {
        // this is how we call
        // function adding whole UI
        /*Text(
            text = "VIAGGI SALVATI",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(10.dp)
        )*/
        Spacer(modifier = Modifier.width(50.dp))


        var currentUser = "mario.rossi"


        Log.i(
            "SearchScreen",
            "Amplify.Auth.currentUser : ${Amplify.Auth.currentUser}"
        )

        if (Amplify.Auth.currentUser != null)
            currentUser = Amplify.Auth.currentUser.username


        if (currentUser == "mario.rossi") {
            FavouriteList(
                listOf(
                    Post(
                        id = "1",
                        owner = "serena.verdi",
                        ownerName = "serena.verdi",
                        location = "Sydney",
                        locationName = "Sydney",
                        title = "Sydney",
                        description = "Descrizione",
                        imageKey = "serena.verdi_1",
                        imageUrl = "android.resource://educ.unito.myapplication/drawable/sydney"
                    ),
                    Post(
                        id = "1",
                        owner = "giovanni.bianchi",
                        ownerName = "giovanni.bianchi",
                        location = "Napoli",
                        locationName = "Napoli",
                        title = "Napoli",
                        description = "Descrizione",
                        imageKey = "giovanni.bianchi_1",
                        imageUrl = "android.resource://educ.unito.myapplication/drawable/napoli"
                    )
                ),
                currentUser
            )
        }
        else {
            FavouriteList(FriendData.allFriendsPosts, currentUser)
        }

    }
}




@Composable
fun FavouriteList(posts : List<Post>, currentUser : String){

    var isEmpty by remember{mutableStateOf<Boolean>(true)}

    LazyColumn{
        items(posts){
                post ->
                    if (currentUser == "mario.rossi") {
                        FavouriteCard(post)
                        isEmpty = false
                    }
                    else {
                        if (FavoritesData.userFavorites.contains(post.id)) {
                            FavouriteCard(post)
                            isEmpty = false
                        }
                    }
        }
    }

    if (isEmpty){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.W900, color = Color(0xFF4552B8))
                        ) {
                            append("Non hai ancora aggiunto post alla tua lista dei Preferiti.")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun FavouriteCard (favorite : Post) {
    val context = LocalContext.current

    val openDialog = remember {
        mutableStateOf(false)
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        ) {
            Image(
                painter = rememberImagePainter(data = favorite.imageUrl),
                contentDescription = favorite.title,
                modifier = Modifier
                    .size(width = 400.dp, height = 160.dp)
                    .background(Color.LightGray)
                    .clickable {
                        openDialog.value = true
                    },
                contentScale = ContentScale.Crop,
            )

            if (openDialog.value) {
                Dialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    content = {
                        PostPopup(post = favorite)
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .absolutePadding(top=5.dp, bottom=0.dp, right=0.dp, left=5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(
                        text = favorite.ownerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                Column() {
                    Icon(
                        // icona cliccabile per gestire la rimozione del post dai preferiti
                        // TODO spostare l'icona sulla destra
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                        tint = Color.Black,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .absolutePadding(right=10.dp, top=5.dp)
                            .clickable {
                                Toast
                                    .makeText(context, "Image clicked", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .absolutePadding(top=1.dp, bottom=0.dp, right=0.dp, left=5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.LocationOn,
                    contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = favorite.locationName,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(2.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .absolutePadding(top=0.dp, bottom=5.dp, right=0.dp, left=5.dp)
            ) {
                Text(
                    text = favorite.description,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(2.dp)
                )
            }

        }
    }
}


@Composable
fun FavouritePostPopup(post: Post) {

    val context = LocalContext.current

    // Colonna esterna in cui sono contenuti tutti gli elementi del popup
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(8.dp)
    ) {

        //User
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .absolutePadding(top = 0.dp, bottom = 0.dp, right = 0.dp, left = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.ownerName,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(2.dp)
            )
            Icon(
                Icons.Rounded.Share,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {

                        var imageUrl = "/storage/emulated/0/Android/data/educ.unito.myapplication/drawable/sydney.png"
                        var intent = Intent(Intent.ACTION_SEND)
                        var path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageUrl, "", null)
                        var screenshotUri = Uri.parse(path)
                        intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                        intent.setType("image/*")
                        context.startActivity(Intent.createChooser(intent, null))


                        //var uri = Uri.parse(post.imageUrl)
                        //var stream = context.getContentResolver().openInputStream(uri) as InputStream

                        //val sendIntent: Intent = Intent().apply {
                        //    action = Intent.ACTION_SEND
                        //    putExtra(Intent.EXTRA_STREAM, uri)
                        //    type = "image/*"
                        //}
                        //val shareIntent = Intent.createChooser(sendIntent, null)
                        //context.startActivity(shareIntent)
                    }
            )
        }

        //Location
        Spacer(modifier = Modifier.size(2.dp))
        Row(
            modifier = Modifier
                .absolutePadding(top = 1.dp, bottom = 1.dp, right = 0.dp, left = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = post.locationName,
                fontSize = 14.sp,
                modifier = Modifier.padding(2.dp)
            )
        }

        //Image
        Spacer(modifier = Modifier.size(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(post.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }

        //Title
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = post.title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(2.dp)
        )

        //Description
        Text(
            text = post.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(2.dp)
        )
    }
}
