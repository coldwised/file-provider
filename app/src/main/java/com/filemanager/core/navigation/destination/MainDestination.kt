package com.filemanager.core.navigation.destination

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.filemanager.domain.model.FileModel
import com.filemanager.presentation.main.MainScreen
import java.io.File


private const val BASE_ROUTE = "main"
private const val FILE_PATH_KEY = "path"
private const val DIRECTORY_NAME_KEY = "directory"
const val MAIN_SCREEN_ROUTE = "$BASE_ROUTE?$FILE_PATH_KEY={$FILE_PATH_KEY}?$DIRECTORY_NAME_KEY={$DIRECTORY_NAME_KEY}"

fun NavGraphBuilder.main(
    onNavigateToMain: (FileModel) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable(
        route = "$BASE_ROUTE?$FILE_PATH_KEY={$FILE_PATH_KEY}?$DIRECTORY_NAME_KEY={$DIRECTORY_NAME_KEY}",
        arguments = listOf(
            navArgument(FILE_PATH_KEY) { nullable = true; type = NavType.StringType },
            navArgument(DIRECTORY_NAME_KEY) { nullable = true; type = NavType.StringType }
        )
    ) { navBackStackEntry ->
        val arguments = navBackStackEntry.arguments
        val encodedPath = arguments?.getString(FILE_PATH_KEY)
        val encodedDirectoryName = arguments?.getString(DIRECTORY_NAME_KEY)
        val directoryName = Uri.decode(encodedDirectoryName) ?: "Проводник"
        val path = Uri.decode(encodedPath)
        val context = LocalContext.current
        MainScreen(
            directoryName = directoryName,
            path = path,
            onFileClick = { if(it.type == "folder") onNavigateToMain(it) else {
                val file = File(it.path)
                val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, "${it.type}/*")
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            },
            onBackClick = onNavigateBack,
            onShareFileClick = {
                val file = File(it.path)
                val fileUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "${it.type}/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(Intent.createChooser(shareIntent, "Share file"))
            }
        )
    }
}

fun NavController.navigateToMain(path: String?, directoryName: String?) {
    val encodedPath: String? = Uri.encode(path)
    val encodedDirectoryName = directoryName?.let {
        Uri.encode(it)
    }
    val route = if(path == null)
        BASE_ROUTE else if(encodedDirectoryName == null)
        "$BASE_ROUTE?$FILE_PATH_KEY=$encodedPath" else
        "$BASE_ROUTE?$FILE_PATH_KEY=$encodedPath?$DIRECTORY_NAME_KEY=$encodedDirectoryName"
    navigate(route)
}