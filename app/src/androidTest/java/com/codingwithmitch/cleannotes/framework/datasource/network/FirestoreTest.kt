package com.codingwithmitch.cleannotes.framework.datasource.network

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.codingwithmitch.cleannotes.business.domain.model.NoteFactory
import com.codingwithmitch.cleannotes.business.util.DateUtil
import com.codingwithmitch.cleannotes.framework.datasource.data.NoteDataFactory
import com.codingwithmitch.cleannotes.framework.datasource.network.implementation.NoteFirestoreServiceImpl
import com.codingwithmitch.cleannotes.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.NOTES_COLLECTION
import com.codingwithmitch.cleannotes.framework.datasource.network.implementation.NoteFirestoreServiceImpl.Companion.USER_ID
import com.codingwithmitch.cleannotes.framework.datasource.network.mappers.NetworkMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
open class FirestoreTest {

    private val firestoreSettings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder()
        .setHost("10.0.2.2:8080")
        .setSslEnabled(false)
        .setPersistenceEnabled(false)
        .build()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    val application: Application
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val noteDataFactory: NoteDataFactory
    val dateUtil: DateUtil = DateUtil(dateFormat)
    val noteFactory = NoteFactory(dateUtil)
    val networkMapper = NetworkMapper(dateUtil)


    init {
        firestore.firestoreSettings = firestoreSettings
        application = ApplicationProvider.getApplicationContext<Context>() as Application
        noteDataFactory = NoteDataFactory(application)
        insertFakeData()
        signIn()
    }

    fun insertFakeData() {
        val entityList = networkMapper.noteListToEntityList(
            noteDataFactory.produceListOfNotes()
        )
        for(entity in entityList){
            firestore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .document(entity.id)
                .set(entity)
        }
    }

    private fun signIn() = runBlocking{
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            NoteFirestoreServiceImpl.EMAIL,
            NoteFirestoreServiceImpl.PASSWORD
        ).await()
    }

//    @Test
//    fun firstTest() = runBlocking{
//        signIn()
//
//        val note = noteFactory.createSingleNote(
//            UUID.randomUUID().toString(),
//            UUID.randomUUID().toString(),
//            UUID.randomUUID().toString()
//        )
//        val entity = networkMapper.mapToEntity(note)
//
//        firestore
//            .collection(NOTES_COLLECTION)
//            .document(USER_ID)
//            .collection(NOTES_COLLECTION)
//            .document(entity.id)
//            .set(entity)
//            .await()
//
//        val noteList = firestore
//            .collection(NOTES_COLLECTION)
//            .document(USER_ID)
//            .collection(NOTES_COLLECTION)
//            .get()
//            .await()
//            .toObjects(NoteNetworkEntity::class.java)
//
//        println("test: size: ${noteList.size}")
//        for(noteEntity in noteList){
//            println("test: ${noteEntity.title}")
//            assert(noteEntity.title != null)
//        }
//
//    }



}




















