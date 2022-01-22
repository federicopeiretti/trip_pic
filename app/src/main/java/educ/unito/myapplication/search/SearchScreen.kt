package educ.unito.myapplication.search

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.amplifyframework.core.Amplify
import educ.unito.myapplication.*
import educ.unito.myapplication.R
import educ.unito.myapplication.profile.PostPopup


@Composable
fun SearchScreen() {

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))

            PostTabView(
                iconWithTexts = listOf(
                    IconWithText(
                        image = painterResource(id = R.drawable.ic_image_search),
                        text = "Posts-search"
                    ),
                    IconWithText(
                        image = painterResource(id = R.drawable.ic_person_search),
                        text = "Friends-search"
                    ),
                )
            ) {
                selectedTabIndex = it
            }

            var currentUser = "mario.rossi"

            Log.i(
                "SearchScreen",
                "Amplify.Auth.currentUser : ${Amplify.Auth.currentUser}"
            )

            if (Amplify.Auth.currentUser != null)
                currentUser = Amplify.Auth.currentUser.username

            when (selectedTabIndex) {

                0 ->
                    if (currentUser == "mario.rossi") {
                        ShowPostListSection(
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
                                ),
                            )
                        )
                    }
                    else {
                        ShowPostListSection(FriendData.allFriendsPosts)
                    }

                1 -> ShowUserListSection(
                    users = if (UserData.users.none()) {
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
                            ),
                            User(
                                id = "102",
                                username = "giuseppe.verdi",
                                location = "Torino",
                                locationName = "Torino",
                                description = "XD",
                                image = "android.resource://educ.unito.myapplication/drawable/torino",
                                imagePath = "android.resource://educ.unito.myapplication/drawable/torino"
                            ),
                        )
                    }
                    else {
                        UserData.users
                    }
                )
            }
        }
    }
}

@Composable
fun PostTabView(
    modifier: Modifier = Modifier,
    iconWithTexts: List<IconWithText>,
    onTabSelected: (selectedIndex: Int) -> Unit
) {
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val inactiveColor = Color(0xFF777777)
    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = Color.Transparent,
        contentColor = Color.Black,
        modifier = modifier
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

@Composable
fun ShowPostListSection(posts : List<Post>){
    SearchbarPosts(posts = posts)

    var isEmpty by remember{mutableStateOf<Boolean>(true)}

    LazyColumn {
        items(posts) { post ->
            PostCard(post)
            isEmpty = false
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
                            append("Non ci sono post da visualizzare.")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PostCard(post : Post){

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
                openDialog.value = true
            },
        shape = RoundedCornerShape(8.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp)
        ){
            Image(
                painter = rememberImagePainter(data = post.imageUrl),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10))
            )
            Column(){
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.absolutePadding(left=25.dp)
                )
                Text(
                    text = post.ownerName,
                    //fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.absolutePadding(left=25.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.absolutePadding(left = 22.dp)
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = post.locationName,
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
            },
            content = {
                PostPopup(post)
            }
        )
    }
}

@Composable
fun ShowUserListSection(users : List<User>){
    SearchbarUsers(users = users)
    LazyColumn{
        items(users){
                user -> UserCard(user)
        }
    }
}


@Composable
fun UserCard(user : User){

    val context = LocalContext.current

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(8.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp)
        ){
            Column() {
                Image(
                    painter = rememberImagePainter(data = user.imagePath),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .absolutePadding(top = 0.dp, left = 0.dp, right = 30.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Column() {

                    Text(
                        text = user.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.absolutePadding(left = 30.dp)
                    )
                    /**Text(
                        text = user.getCity(),
                        fontSize = 16.sp,
                        modifier = Modifier.absolutePadding(left = 30.dp)
                    )*/
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.absolutePadding(left = 27.dp)
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
                            modifier = Modifier.absolutePadding(left = 4.dp)
                        )
                    }
                }
                Column() {

                    if (FriendData.userFriends.contains(user.username)) {
                        Icon(Icons.Filled.Done, "")
                    }
                    else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_friend),
                            contentDescription = "Add a new friend",
                            tint = Color.Black,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .size(24.dp)
                                .clickable {

                                    var currentUser = "mario.rossi"

                                    Log.i(
                                        "SearchScreen",
                                        "Amplify.Auth.currentUser : ${Amplify.Auth.currentUser}"
                                    )

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
                                            Backend.addUserFriend(currentUser, user.username)
                                            Toast
                                                .makeText(
                                                    context,
                                                    "${user.username} è tuo amico!",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        } catch (e: Exception) {
                                            Toast
                                                .makeText(context, e.message, Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchbarUsers(users : List<User>) {
    var searchState by remember {
        mutableStateOf("")
    }

    // Serve per levare il focus dai textFields
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchState,
            label = {
                Text(text = "Cerca utente")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_foreground),
                    contentDescription = "search",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(40.dp)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(red = 204, green = 0, blue = 0),
                cursorColor = Color.Black,
                focusedLabelColor = Color(red = 204, green = 0, blue = 0)
            ),
            onValueChange = {
                searchState = it
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
    }
}

@Composable
fun SearchbarPosts(posts : List<Post>) {
    var searchState by remember {
        mutableStateOf("")
    }

    // Serve per levare il focus dai textFields
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchState,
            label = {
                Text(text = "Cerca contenuto")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_foreground),
                    contentDescription = "search",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(40.dp)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(red = 204, green = 0, blue = 0),
                cursorColor = Color.Black,
                focusedLabelColor = Color(red = 204, green = 0, blue = 0)
            ),
            onValueChange = {
                searchState = it
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
    }
}


/**
@Composable
fun PostSearchPopup(post: Post) {

    val context = LocalContext.current

    var iconLiked = remember {
        mutableStateOf(false)
    }


    // Colonna esterna in cui sono contenuti tutti gli elementi del popup
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = post.getOwner(),
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
                        var uri = Uri.parse(post.getImagePath())
                        var stream =
                            context
                                .getContentResolver()
                                .openInputStream(uri) as InputStream

                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, uri)
                            type = "image/*"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
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
                text = post.getLocation(),
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
                painter = post.getImage(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }

        //Title
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = post.getTitle(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(2.dp)
        )

        //Description
        Text(
            text = post.getDescription(),
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
                    var temp = iconLiked.value
                    temp = !temp
                    iconLiked.value = temp
                }
            ) {
                // L'icona del cuore si riempie o si svuota ad ogni click
                Icon(
                    if (iconLiked.value) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null,
                    tint = Color(red = 204, green = 0, blue = 0)
                )
            }
        }

    }
}
*/