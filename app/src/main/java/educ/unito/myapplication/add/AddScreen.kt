package educ.unito.myapplication.add

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.activity.result.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.amplifyframework.core.Amplify
import com.google.android.libraries.places.api.model.Place
import educ.unito.myapplication.*
import educ.unito.myapplication.R
import educ.unito.myapplication.profile.PostPopup
import educ.unito.myapplication.profile.RectangleImage
import kotlinx.coroutines.launch
import java.util.*


@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun AddScreen(myContext : MainActivity, viewModel : AddViewModel) {

    val context = LocalContext.current

    // Serve per levare il focus dai textFields
    val focusManager = LocalFocusManager.current

    // Colore del background del bottone per caricare la foto
    val mainButtonColor = ButtonDefaults.buttonColors(
        backgroundColor = Color.LightGray
    )

    // Colore del background del bottone per confermare
    val uploadButtonColor = ButtonDefaults.buttonColors(
        backgroundColor = Color(red = 204, green = 0, blue = 0)
    )


    // Colonna esterna in cui rientrano tutti gli item di questa pagina
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .verticalScroll(rememberScrollState())
            .absolutePadding(top = 30.dp, bottom = 30.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {

        /************** Bottone Foto **************/

        CaptureImageFromGalleryOrCameraV2(myContext, mainButtonColor, viewModel)

        /************** TextField TITOLO **************/
        var isValidTitle = viewModel.title != ""

        Spacer(modifier = Modifier.size(10.dp))
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = {
                viewModel.onTitleChanged(it)
            },
            label = {
                Text(text = "Titolo")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            isError = !isValidTitle
        )

        /************** TextField DESCRIZIONE **************/
        var isValidDescription = viewModel.description !== ""

        Spacer(modifier = Modifier.size(10.dp))
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = {
                viewModel.onDescriptionChanged(it)
            },
            label = {
                Text(text = "Descrizione")
            },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            isError = !isValidDescription
        )

        /************** Bottone TAGGA PERSONE **************/
        /**
        Spacer(modifier = Modifier.size(15.dp))
        Button(
            onClick = {
                Toast.makeText(context, "Tagga amici", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = mainButtonColor,
        )
        {
            // Riga per organizzare il bottone per taggare
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tags),
                    contentDescription = "tags",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "Tags"
                )
            }
        }
        */

        /************** Bottone CONFERMA **************/
        Spacer(modifier = Modifier.size(15.dp))
        Button(
            enabled = viewModel.title.isNotBlank() && viewModel.description.isNotBlank() && viewModel.imageDevice != null && viewModel.location != null,
            onClick = {

                var imagepath = viewModel.imageDevice!!.path //device path
                Log.i("Image path: ", imagepath!!)

                Backend.uploadFile(imagepath, viewModel)

                Thread.sleep(1000)
                Log.i("AWS Storage", "Image key = ${viewModel.imageKey}")

                viewModel.location?.let { place : Place ->

                    val location = Location(
                        id = place.id!!,
                        lat = place.latLng!!.latitude,
                        lng = place.latLng!!.longitude,
                        name = place.name!!
                    )

                    // store it in the backend
                    Backend.createLocation(location)

                    // add it to LocationData, this will trigger a UI refresh
                    LocationData.addLocation(location)

                    val post = Post(
                        id = UUID.randomUUID().toString(),
                        owner = Amplify.Auth.currentUser.username,
                        ownerName = Amplify.Auth.currentUser.username,
                        location = place.id!!,
                        locationName = place.name!!,
                        title = viewModel.title,
                        description = viewModel.description,
                        imageKey = viewModel.imageKey,
                        imageUrl = viewModel.imageStorage
                    )

                    // store it in the backend
                    Backend.createPost(post)

                    // add it to PostData, this will trigger a UI refresh
                    PostData.addPost(post)

                }

                Toast.makeText(context, "Post caricato con successo", Toast.LENGTH_SHORT).show()
                viewModel.reset()

            },
            modifier = Modifier.height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = uploadButtonColor,
        )
        {
            Text(
                text = "CREA POST",
                color = Color.White
            )
        }
    }

}

@Composable
fun CaptureImageFromGalleryOrCamera(myContext: MainActivity, mainButtonColor: ButtonColors, viewModel : AddViewModel) {

    val context = LocalContext.current

    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            viewModel.onBitmapChanged(it)
        }

    val launcherGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            val imageuri = Backend.getFilePathFromUri(it, myContext)
            viewModel.onImageDeviceChanged(imageuri)
        }

    var cameraPermission = false


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        viewModel.imageDevice?.let {

            if (Build.VERSION.SDK_INT < 28) {
                viewModel.onBitmapChanged(
                    MediaStore.Images.Media.getBitmap(context.contentResolver,it)
                )
            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                viewModel.onBitmapChanged(ImageDecoder.decodeBitmap(source))
            }

            viewModel.bitmap?.let {  btm ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.absolutePadding(top = 10.dp)
                ) {
                    RectangleImage(
                        image = rememberImagePainter(btm),
                        modifier = Modifier
                            .size(300.dp)
                            .weight(3f)
                            .absolutePadding(bottom=20.dp, top=40.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                while (!cameraPermission) {
                    if (ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(
                            myContext,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        val permission = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        )
                        ActivityCompat.requestPermissions(myContext, permission, 121)
                    } else {
                        cameraPermission = true
                        launcherGallery.launch("image/*")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = mainButtonColor
        ) {
            // Colonna interna al bottone della foto per organizzare l'icona e la scritta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_foreground),
                    contentDescription = "Add",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(50.dp)
                        .absolutePadding(left = 5.dp)
                )
                Text(text = "Carica immagine")
            }
        }
    }
}





@ExperimentalMaterialApi
@Composable
fun CaptureImageFromGalleryOrCameraV2(myContext: MainActivity, mainButtonColor: ButtonColors, viewModel : AddViewModel) {

    val context = LocalContext.current

    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val tempUri = it?.let { it1 -> Backend.getImageUri(myContext, it1) }
            val imageuri = Backend.getFilePathFromUri(tempUri, myContext)
            viewModel.onImageDeviceChanged(imageuri)
            Log.i("CaptureImageFromGalleryOrCameraV2","Image path: $imageuri")
        }

    val launcherGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            val imageuri = Backend.getFilePathFromUri(it, myContext)
            viewModel.onImageDeviceChanged(imageuri)
        }

    var cameraPermission = false


    val openDialog = remember {
        mutableStateOf(false)
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        viewModel.imageDevice?.let {

            if (Build.VERSION.SDK_INT < 28) {
                viewModel.onBitmapChanged(
                    MediaStore.Images.Media.getBitmap(context.contentResolver,it)
                )
            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                viewModel.onBitmapChanged(ImageDecoder.decodeBitmap(source))
            }

            viewModel.bitmap?.let {  btm ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.absolutePadding(top = 10.dp)
                ) {
                    RectangleImage(
                        image = rememberImagePainter(btm),
                        modifier = Modifier
                            .size(300.dp)
                            .weight(3f)
                            .absolutePadding(bottom=20.dp, top=40.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                openDialog.value = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = mainButtonColor
        ) {
            // Colonna interna al bottone della foto per organizzare l'icona e la scritta
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_foreground),
                    contentDescription = "Add",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(50.dp)
                        .absolutePadding(left = 5.dp)
                )
                Text(text = "Carica immagine")
            }
        }

        if (openDialog.value) {
            Dialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        ListItem(
                            text = { Text("Scatta una foto") },
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.ic_camera),
                                    contentDescription = "Scatta una foto"
                                )
                            },
                            modifier = Modifier.clickable {
                                while (!cameraPermission) {
                                    if (ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.ACCESS_MEDIA_LOCATION
                                        ) == PackageManager.PERMISSION_DENIED
                                    ) {
                                        val permission = arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.ACCESS_MEDIA_LOCATION
                                        )
                                        ActivityCompat.requestPermissions(myContext, permission, 121)
                                    } else {
                                        cameraPermission = true
                                        launcherCamera.launch()
                                        openDialog.value = false
                                    }
                                }
                            }
                        )
                        ListItem(
                            text = { Text("Galleria") },
                            icon = {
                                Icon(
                                    painterResource(id = R.drawable.ic_gallery),
                                    contentDescription = "Galleria"
                                )
                            },
                            modifier = Modifier.clickable {
                                while (!cameraPermission) {
                                    if (ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_DENIED ||
                                        ContextCompat.checkSelfPermission(
                                            myContext,
                                            Manifest.permission.ACCESS_MEDIA_LOCATION
                                        ) == PackageManager.PERMISSION_DENIED
                                    ) {
                                        val permission = arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.ACCESS_MEDIA_LOCATION
                                        )
                                        ActivityCompat.requestPermissions(myContext, permission, 121)
                                    } else {
                                        cameraPermission = true
                                        launcherGallery.launch("image/*")
                                        openDialog.value = false
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }


    }
}


