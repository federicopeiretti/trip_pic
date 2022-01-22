package educ.unito.myapplication.profile


import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import android.widget.Toast
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.amplifyframework.core.Amplify
import educ.unito.myapplication.*
import educ.unito.myapplication.R



// here we have created HomeScreen function
// and we will call all functions inside it.
// and finally just call this function from mainActivity
@ExperimentalFoundationApi
@Composable
fun HomeScreen(viewModel : ProfileViewModel) {

    var currentUser = "mario.rossi"

    Log.i(
        "SearchScreen",
        "Amplify.Auth.currentUser : ${Amplify.Auth.currentUser}"
    )

    if (Amplify.Auth.currentUser != null)
        currentUser = Amplify.Auth.currentUser.username


    // this is the most outer box that will
    // contain all the views,buttons,chips,etc.
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column {
            // this is how we call
            // function adding whole UI
            TopBar(
                name = viewModel.username,
                modifier = Modifier
                    .padding(10.dp)
            )
            ProfileSection(viewModel)
            Spacer(modifier = Modifier.height(10.dp))

            PostTabView(
                //TODO da sostituire le campanelle con post, tag, amici, (mappa)
                iconWithTexts = listOf(
                    /*IconWithText(
                        image = painterResource(id = R.drawable.ic_bell),
                        text = "Mappa"
                    ),*/
                    IconWithText(
                        image = painterResource(id = R.drawable.ic_gallery),
                        text = "Posts"
                    ),
                    /**
                    IconWithText(
                        image = painterResource(id = R.drawable.ic_tags),
                        text = "Tag"
                    ),
                    */
                    IconWithText(
                        image = painterResource(id = R.drawable.ic_friends),
                        text = "Amici"
                    ),
                )
            ){
                selectedTabIndex = it
            }

            when(selectedTabIndex) {

                // POST
                0 ->
                    if (PostData.posts.none() && viewModel.loggedUserInfo == null) {
                        PostSection(
                            listOf(
                                Post(
                                    id = "1",
                                    owner = "mario.rossi",
                                    ownerName = "mario.rossi",
                                    location = "Torino",
                                    locationName = "Torino",
                                    title = "Torino",
                                    description = "Descrizione",
                                    imageKey = "mario.rossi_1",
                                    imageUrl = "android.resource://educ.unito.myapplication/drawable/torino"
                                ),
                                Post(
                                    id = "2",
                                    owner = "mario.rossi",
                                    ownerName = "mario.rossi",
                                    location = "Venezia",
                                    locationName = "Venezia",
                                    title = "Venezia",
                                    description = "Descrizione",
                                    imageKey = "mario.rossi_2",
                                    imageUrl = "android.resource://educ.unito.myapplication/drawable/venezia"
                                ),
                                Post(
                                    id = "3",
                                    owner = "mario.rossi",
                                    ownerName = "mario.rossi",
                                    location = "Firenze",
                                    locationName = "Firenze",
                                    title = "Firenze",
                                    description = "Descrizione",
                                    imageKey = "mario.rossi_3",
                                    imageUrl = "android.resource://educ.unito.myapplication/drawable/firenze"
                                ),
                                Post(
                                    id = "4",
                                    owner = "mario.rossi",
                                    ownerName = "mario.rossi",
                                    location = "Roma",
                                    locationName = "Roma",
                                    title = "Roma",
                                    description = "Descrizione",
                                    imageKey = "mario.rossi_4",
                                    imageUrl = "android.resource://educ.unito.myapplication/drawable/roma"
                                ),
                                Post(
                                    id = "5",
                                    owner = "mario.rossi",
                                    ownerName = "mario.rossi",
                                    location = "Palermo",
                                    locationName = "Palermo",
                                    title = "Palermo",
                                    description = "Descrizione",
                                    imageKey = "mario.rossi_5",
                                    imageUrl = "android.resource://educ.unito.myapplication/drawable/sicilia"
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else {
                        PostSection(PostData.posts, modifier = Modifier.fillMaxWidth())
                    }

                // AMICI
                // TODO creare lista di amici
                1 ->
                    if (FriendData.userFriends.none() && viewModel.loggedUserInfo == null) {
                        UserList(
                                listOf(
                                    User(
                                        id = "100",
                                        username = "simona.verdi",
                                        location = "Milano",
                                        locationName = "Milano",
                                        description = "La musica può rendere gli uomini liberi (Bob Marley)",
                                        image = "android.resource://educ.unito.myapplication/drawable/milano",
                                        imagePath = "android.resource://educ.unito.myapplication/drawable/milano"
                                    ),
                                    User(
                                        id = "101",
                                        username = "giovanni.bianchi",
                                        location = "Roma",
                                        locationName = "Roma",
                                        description = "fatti non foste a viver come bruti",
                                        image = "android.resource://educ.unito.myapplication/drawable/roma",
                                        imagePath = "android.resource://educ.unito.myapplication/drawable/roma"
                                    )
                                ),
                            currentUser
                        )
                    }
                    else {
                        UserList(UserData.users, currentUser)
                    }
            }
        }
    }
}

// barra in alto contenente nome e campanella col numero di notifiche
// TODO gestire le notifiche (se numerino sopra la campanella o altro)
@Composable
fun TopBar(
    name: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column() {
            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        Column() {
            Row() {
                Icon(
                    Icons.Rounded.Notifications,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .clickable {
                            Toast
                                .makeText(context, "Image clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                )

                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = "Menu",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            Toast
                                .makeText(context, "Image clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                )
            }

        }

    }
}

@Composable
fun ProfileSection(
    viewModel : ProfileViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            RoundImage(
                image = rememberImagePainter(data = viewModel.imagePath),
                modifier = Modifier
                    .size(100.dp)
                    .weight(3f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatSection(viewModel = viewModel, modifier = Modifier.weight(7f))

        }
    }
}

@Composable
fun ProfileDescription(
    viewModel : ProfileViewModel
) {
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = viewModel.location,
            fontWeight = FontWeight.Bold,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        Text(
            text = viewModel.description,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
    }
}


@Composable
fun RoundImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun RectangleImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RectangleShape
            )
            .padding(3.dp)
            .clip(RectangleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StatSection(viewModel : ProfileViewModel, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        //ProfileStat(numberText = "601", text = "Posts")
        //ProfileStat(numberText = "100K", text = "Friends")
        ProfileDescription(viewModel)
    }
}

/*
@Composable
fun ProfileStat(
numberText: String,
text: String,
modifier: Modifier = Modifier
) {
Column(
verticalArrangement = Arrangement.Center,
horizontalAlignment = Alignment.CenterHorizontally,
modifier = modifier
) {
Text(
text = numberText,
fontWeight = FontWeight.Bold,
fontSize = 20.sp
)
Spacer(modifier = Modifier.height(4.dp))
Text(text = text)
}
}
*/

@Composable
fun PostTabView(
    modifier: Modifier = Modifier,
    iconWithTexts: List<IconWithText>,
    onTabSelected: (selectedIndex: Int) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val inactiveColor = Color(0xFF777777)

    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = Color.Transparent,
        contentColor = Color.Black,
    ) {
        iconWithTexts.forEachIndexed { index, item ->
            Tab(
                selected = selectedTabIndex == index,
                selectedContentColor = Color.Black,
                unselectedContentColor = inactiveColor,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(index)
                }
            ) {
                Icon(
                    painter = item.image,
                    contentDescription = item.text,
                    tint = if(selectedTabIndex == index) Color.Black else inactiveColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                )
            }
        }
    }
}


@ExperimentalFoundationApi
@Composable
fun PostSection(
    posts: List<Post>,
    modifier: Modifier = Modifier
) {

    var isEmpty by remember{mutableStateOf<Boolean>(true)}

    val context = LocalContext.current

    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        modifier = modifier
            .scale(1.01f)
    ) {
        items(posts.size) {

            isEmpty = false

            val openDialog = remember {
                mutableStateOf(false)
            }

            Image(
                painter = rememberImagePainter(data = posts[it].imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(
                        width = 1.dp,
                        color = White
                    )
                    .clickable {
                        //OPEN DIALOG
                        openDialog.value = true
                    }
            )
            if (openDialog.value) {
                Dialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    content = {
                        PostPopup(posts[it])
                    }
                )
            }
        }
    }


    if (isEmpty) {
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
                            append("Non sono presenti post da visualizzare.")
                        }
                    }
                )
            }
        }
    }

}






@ExperimentalFoundationApi
@Composable
fun UserList(users : List<User>, currentUser : String){

    var isEmpty by remember{mutableStateOf<Boolean>(true)}

    LazyColumn{
        items(users){
                user ->
                    if (currentUser == "mario.rossi") {
                        UserCard(user)
                        isEmpty = false
                    }
                    else{
                        if(FriendData.userFriends.contains(user.username)) {
                            user.imagePath = "https://myapplication79eda229a8aa41f1a0881204d9c4e19e210000-dev.s3.eu-central-1.amazonaws.com/public/" + user.image
                            UserCard(user)
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
                            append("Non sono presenti amici.")
                        }
                    }
                )
            }
        }
    }
}


@ExperimentalFoundationApi
@Composable
fun UserCard(user : User){

    val context = LocalContext.current
    val openDialog = remember {
        mutableStateOf(false)
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                //OPEN DIALOG
                Backend.getFriendPosts(user.username)
                Thread.sleep(1000)
                openDialog.value = true
            },
        shape = RoundedCornerShape(8.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp)
        ){
            Image(
                painter = rememberImagePainter(user.imagePath),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Column(){
                Text(
                    text = user.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.absolutePadding(top=10.dp, left=30.dp, bottom=0.dp)
                )
                /**Text(
                text = user.getCity(),
                fontSize = 16.sp,
                modifier = Modifier.absolutePadding(left=30.dp)
                )*/
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.absolutePadding(left = 27.dp)
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = user.locationName!!,
                        fontSize = 16.sp,
                        modifier = Modifier.absolutePadding(left = 4.dp)
                    )
                }
            }
        }
    }
    if (openDialog.value) {
        Dialog(
            onDismissRequest = {
                openDialog.value = false
                FriendData.resetFriendPosts()
            },
            content = {
                UserPopup(user)
            }
        )
    }
}

@Composable
fun PostPopup (post : Post) {

    val context = LocalContext.current

    var currentUser = "mario.rossi"

    Log.i("PostPopup", "Amplify.Auth.currentUser : ${Amplify.Auth.currentUser}")

    if (Amplify.Auth.currentUser != null) currentUser = Amplify.Auth.currentUser.username

    if (post.ownerName == currentUser) {
        PostOwnerPopup(post = post)
    }
    else {
        PostFriendPopup(post = post)
    }
}


@Composable
fun PostOwnerPopup (post : Post) {

    val context = LocalContext.current

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


            Column() {
                var showMenu by remember { mutableStateOf(false) }
                Icon(
                    // icona cliccabile per gestire la rimozione del post dai preferiti
                    // TODO spostare l'icona sulla destra
                    Icons.Rounded.MoreVert,
                    contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            showMenu = !showMenu
                        }
                )

                DropdownMenu(
                    expanded = showMenu,
                    modifier = Modifier.fillMaxWidth(0.4f),
                    onDismissRequest = { showMenu = false }
                ) {

                    DropdownMenuItem(
                        onClick = {

                            var imageUrl = "/storage/emulated/0/Android/data/educ.unito.myapplication/drawable/sydney.png"
                            var intent = Intent(Intent.ACTION_SEND)
                            var path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageUrl, "", null)
                            var screenshotUri = Uri.parse(path)
                            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                            intent.setType("image/*")
                            context.startActivity(Intent.createChooser(intent, null))

                            //var uri = Uri.parse(post.imageUrl)
                            //var stream = context.getContentResolver().openInputStream(uri) as InputStream
                            //
                            //val sendIntent: Intent = Intent().apply {
                            //    action = Intent.ACTION_SEND
                            //    putExtra(Intent.EXTRA_STREAM, uri)
                            //    type = "image/*"
                            //}
                            //val shareIntent = Intent.createChooser(sendIntent, null)
                            //context.startActivity(shareIntent)

                            showMenu = false
                        },
                        //modifier = Modifier.wrapContentSize().sizeIn(minWidth=50.dp),
                    ) {

                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Condividi")
                    }
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                        },
                        //modifier = Modifier.wrapContentSize().sizeIn(minWidth=50.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Modifica")
                    }
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                        },
                        //modifier = Modifier.wrapContentSize().sizeIn(minWidth=50.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Cancella")
                    }
                }

            }
        }

        //Location
        Spacer(modifier = Modifier.size(2.dp))

        Row(
            modifier = Modifier.absolutePadding(top = 1.dp, bottom = 1.dp, right = 0.dp, left = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.LocationOn,
                contentDescription = null, // da sostituire la campanella con icona più appropriata ( X o cestino)
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = post.locationName,
                //fontWeight = FontWeight.Bold,
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
                painter = rememberImagePainter(data = post.imageUrl),
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


@Composable
fun PostFriendPopup(post: Post) {

    val context = LocalContext.current

    var iconLiked = remember {
        mutableStateOf(false)
    }


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
                        var imageUrl =
                            "/storage/emulated/0/Android/data/educ.unito.myapplication/drawable/sydney.png"
                        var intent = Intent(Intent.ACTION_SEND)
                        var path = MediaStore.Images.Media.insertImage(
                            context.getContentResolver(),
                            imageUrl,
                            "",
                            null
                        )
                        var screenshotUri = Uri.parse(path)
                        intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                        intent.setType("image/*")
                        context.startActivity(Intent.createChooser(intent, null))

                        //var uri = Uri.parse(imageUrl)
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
                painter = rememberImagePainter(data = post.imageUrl),
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

        // Bottone per aggiungere il post ai preferiti
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {

                    var currentUser = "mario.rossi"

                    if (Amplify.Auth.currentUser != null)
                        currentUser = Amplify.Auth.currentUser.username

                    if (currentUser == "mario.rossi") {
                        Toast
                            .makeText(
                                context,
                                "Per usare l'app, registrati o fai il login!",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    } else {
                        try {
                            Backend.addUserFavorite(currentUser, post.id)

                            var temp = iconLiked.value
                            temp = !temp
                            iconLiked.value = temp

                            if (iconLiked.value) {
                                Toast
                                    .makeText(
                                        context,
                                        "Post aggiunto alla tua lista dei preferiti!",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }

                            else {
                                Toast
                                    .makeText(
                                        context,
                                        "Post rimosso dai preferiti!",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }

                        } catch (e: Exception) {
                            Toast
                                .makeText(context, e.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                }
            ) {
                // L'icona del cuore si riempie o si svuota ad ogni click
                Icon(
                    if (FavoritesData.userFavorites.contains(post.id)) {
                        Icons.Filled.Favorite
                    }
                    else {
                        Icons.Outlined.FavoriteBorder
                    },
                    /**
                    if (iconLiked.value) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    }*/
                    contentDescription = null,
                    tint = Color(red = 204, green = 0, blue = 0)
                )
            }
        }

    }
}





@ExperimentalFoundationApi
@Composable
fun UserPopup (user : User) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
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
        ) {
            Column() {
                Image(
                    painter = rememberImagePainter(data = user.imagePath),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
            }
            Column(modifier = Modifier.absolutePadding(left = 27.dp)) {
                Text(
                    text = user.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = user.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp)
                )
                Row(
                    modifier = Modifier.absolutePadding(
                        top = 1.dp,
                        bottom = 1.dp,
                        right = 0.dp,
                        left = 0.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = user.locationName!!,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        //Image
        /*Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = user.getImage(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )

         //Friend post section
        }*/

        //if (FriendData.friendPosts.none()) {
            PostSection(
                posts = FriendData.friendPosts
                /**
                posts = listOf(
                Post(
                imagePath = "android.resource://educ.unito.myapplication/drawable/torino",
                title = "Torino",
                description = "Descrizione",
                location = "Torino",
                owner = "serena"
                ),
                Post(
                imagePath = "android.resource://educ.unito.myapplication/drawable/firenze",
                title = "Torino",
                description = "Descrizione",
                location = "Torino",
                owner = "serena"
                ),
                Post(
                imagePath = "android.resource://educ.unito.myapplication/drawable/venezia",
                title = "Venezia",
                description = "Descrizione",
                location = "Venezia",
                owner = "serena"
                ),
                Post(
                imagePath = "android.resource://educ.unito.myapplication/drawable/venezia",
                title = "Venezia",
                description = "Descrizione",
                location = "Venezia",
                owner = "serena"
                ),
                Post(
                imagePath = "android.resource://educ.unito.myapplication/drawable/napoli",
                title = "Napoli",
                description = "Descrizione",
                location = "Napoli",
                owner = "serena"
                ),
                Post(
                imagePath = "android.resource://educ.unito.myapplication/drawable/sicilia",
                title = "Palermo",
                description = "Descrizione",
                location = "Palermo",
                owner = "serena"
                ),
                )
                 */
            )
        //}
        //else {
        //    Text(text = "Non sono presenti post")
        //}
    }

}
