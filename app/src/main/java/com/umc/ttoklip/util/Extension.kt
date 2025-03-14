package com.umc.ttoklip.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.umc.ttoklip.presentation.honeytip.write.WriteHoneyTipActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

fun Context.uriToFile(uri: Uri): File {
    val inputStream = contentResolver.openInputStream(uri) ?: return File("")
    val file = File(cacheDir, getFileName(uri))
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    return file
}

fun Context.getFileName(uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "unknown"
}

fun EditText.showKeyboard(){
    this.requestFocus()
    val inputMethodManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.showToast(text: String){
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(text: String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
fun String.tabTextToCategory(): String {
    return when (this) {
        "집안일" -> WriteHoneyTipActivity.Category.HOUSEWORK.toString()
        "레시피" -> WriteHoneyTipActivity.Category.RECIPE.toString()
        "안전한 생활" -> WriteHoneyTipActivity.Category.SAFE_LIVING.toString()
        else -> WriteHoneyTipActivity.Category.WELFARE_POLICY.toString()
    }
}

fun String.isValidUri(): Boolean {
    return try {
        val uri = Uri.parse(this)
        Log.d("isValidUri", uri.scheme.toString())
        // 기본적으로 scheme이나 path가 비어있지 않은지를 체크
        if(uri.scheme == "https"){
            false
        } else {
            uri.scheme != null && uri.scheme!!.isNotEmpty() && uri.path != null
        }
    } catch (e: Exception) {
        false
    }
}

fun String.convertStringToTextPlain(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun List<Int>.createRequestBodyFromList(): RequestBody{
    val listString = this.joinToString(",") // List를 쉼표로 구분된 문자열로 변환
    return RequestBody.create("text/plain".toMediaTypeOrNull(), listString)
}

inline fun View.setOnSingleClickListener(
    delay: Long = 1000L,
    crossinline block: (View) -> Unit,
) {
    var previousClickedTime = 0L
    setOnClickListener { view ->
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - previousClickedTime >= delay) {
            block(view)
            previousClickedTime = clickedTime
        }
    }
}

fun String.toReplyNicknameFormat() = "@$this "