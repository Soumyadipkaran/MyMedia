@file:Suppress("DEPRECATION")

package com.fpp.myapplication.utils

import android.app.ProgressDialog
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback: (String?) -> Unit) {
    var imageUrl: String? = null
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                callback(imageUrl)
            }
        }
}

fun uploadVideo(
    uri: Uri,
    folderName: String,
    progressDialog: ProgressDialog,
    callback: (String?) -> Unit
) {
    var videoUrl: String? = null
    progressDialog.setTitle("Uploading")
    progressDialog.show()

    val reference = FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
    reference.putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                videoUrl = uri.toString()
                progressDialog.dismiss()
                callback(videoUrl)
            }
        }
        .addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            progressDialog.setMessage("Uploaded $progress%")
        }
        .addOnFailureListener { exception ->
            progressDialog.dismiss()
            callback(null)
            // Optionally show an error message here
        }
}


