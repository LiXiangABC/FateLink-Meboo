package com.crush.util

import android.content.Context
import android.net.Uri
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.storage.FirebaseStorage
import java.io.File

object UploadPhoto {

    fun uploadFileNew(context: Context,path: String, mediaType: String, onLister: OnLister){
        val firebaseApp = Firebase.initialize(context)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        val storageRef = FirebaseStorage.getInstance(firebaseApp!!).reference

        val file = Uri.fromFile(File(path))
        val path = if (Firebase.auth.uid == null || Firebase.auth.uid.equals("")){
            "images4/${file.lastPathSegment}"
        }else{
            "images5/${file.lastPathSegment}"
        }
        val riversRef = storageRef.child(path)
        val uploadTask = riversRef.putFile(file)


        uploadTask.addOnFailureListener {
            onLister.fail()
        }.addOnSuccessListener { taskSnapshot ->
            onLister.onSuccess(taskSnapshot.storage.path,"")
        }

    }
    fun uploadFileNewPhoto(context: Context,path: String, imageCode: String, onLister: OnLister){
        val firebaseApp = Firebase.initialize(context)
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
            onLister.onSuccess(taskSnapshot.storage.path,imageCode)
        }

    }
    interface OnLister{
        fun onSuccess(successPath:String,imageCode:String)
        fun fail()
    }

}