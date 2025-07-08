package io.rong.imkit.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.custom.base.manager.SDActivityManager
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import io.rong.imkit.activity.Activities
import java.io.File

object UploadPhoto {
    fun uploadFileNew(context: Context,path: String, onLister: OnLister){
        Activities.get().top?.let {
            val firebaseApp = Firebase.initialize(it)
            Firebase.appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance(),
            )
            val storageRef = FirebaseStorage.getInstance(firebaseApp!!).reference
            val file = Uri.fromFile(File(path))
            val path = if (Firebase.auth.uid == null || Firebase.auth.uid.equals("")){
                "auramix/images1/${file.lastPathSegment}"
            }else{
                "auramix/images2/${file.lastPathSegment}"
            }
            val riversRef = storageRef.child(path)
            val uploadTask = riversRef.putFile(file)

            uploadTask.addOnFailureListener {
                onLister.fail()
            }.addOnSuccessListener { taskSnapshot ->
                onLister.onSuccess(taskSnapshot.storage.path)
            }
        }


    }
    interface OnLister{
        fun onSuccess(successPath:String)
        fun fail()
    }

}