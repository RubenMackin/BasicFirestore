package com.rubenmackin.basicfirestore

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //firestore
        firestore = Firebase.firestore

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //metodos a llamar
        //basicInsert()
        //multipleInserts()
        //basicReadData()
        //basicReadDocument()
        //basicReadDocumentWithParse()
        //basicReadDocumentFromCache()
        //subCollections()
        //basicRealTimeDocument()
        //basicRealTimeCollections()
        //basicQuery()
        basicMediumQuery()
    }

    private fun basicInsert() {
        val user = hashMapOf(
            "name" to "RubenDevs",
            "age" to 30,
            "happy" to true,
            "extraInfo" to null
        )

        //firestore.collection("users").add(user)
        //val result = firestore.collection("users").add(user).isSuccessful
        //firestore.collection("users").add(user).await()

        firestore.collection("users").add(user)
            .addOnSuccessListener {
                Log.i("ruben", "success")
            }.addOnFailureListener {
                Log.i("ruben", "error $it")
            }
    }

    private fun multipleInserts() {
        for (i in 0..50) {
            val user = hashMapOf(
                "name" to "RubenDevs $i",
                "age" to 30 + i,
                "happy" to (i % 2 == 0),
                "extraInfo" to null
            )

            firestore.collection("users").add(user)
        }
    }

    private fun basicReadData() {
        firestore.collection("users").get()
            .addOnSuccessListener { snapshot ->
                Log.i("ruben lectura", "success")

                snapshot.forEach { document ->
                    val id = document.id
                    Log.i("ruben lectura $id", document.data.toString())
                }
            }.addOnSuccessListener {
                Log.i("ruben", "error $it")
            }
    }

    private fun basicReadDocument() {
        lifecycleScope.launch {
            val result =
                firestore.collection("users").document("2ZD28BO19lQD2bkEKTt8").get().await()
            Log.i("rubendevsd ${result.id}", result.data.toString())
        }

    }

    private fun basicReadDocumentWithParse() {
        lifecycleScope.launch {
            val result =
                firestore.collection("users").document("2ZD28BO19lQD2bkEKTt8").get().await()

            val userDat = result.toObject<UserData>()

            Log.i("rubendevsd ${result.id}", userDat.toString())
        }

    }

    private fun basicReadDocumentFromCache() {
        lifecycleScope.launch {
            val reference = firestore.collection("users").document("2ZD28BO19lQD2bkEKTt8")
            val source = Source.CACHE
            val result = reference.get(source).await()


            Log.i("rubendevsd ${result.id}", result.data.toString())
        }

    }

    private fun subCollections() {
        lifecycleScope.launch {
            val reference = firestore.collection("users").document("rubendevs").collection("favs")
                .document("MBGTrsBfA3acw7hSV6e9").get().await()
            Log.i("rubendevsd}", reference.data.toString())
        }
    }

    private fun basicRealTimeDocument() {
        firestore.collection("users").document("2ZD28BO19lQD2bkEKTt8")
            .addSnapshotListener { value, error ->
                Log.i("rubendevsd}", value?.data.toString())
            }
    }

    private fun basicRealTimeCollections() {
        firestore.collection("users")
            .addSnapshotListener { value, error ->
                /*value?.forEach { document ->
                    Log.i("ruben lectura", document.data.toString())
                }*/

                Log.i("rubendevsd}", value?.size().toString())
            }
    }

    private fun basicQuery() {
        val query =
            firestore.collection("users").orderBy("age", Query.Direction.DESCENDING).limit(10)
        query.get().addOnSuccessListener {
            it.forEach { result ->
                Log.i("rubendevsd}", result.data.toString())
            }
        }
    }

    private fun basicMediumQuery() {
        val query = firestore.collection("users")
            .orderBy("age", Query.Direction.DESCENDING)
            .whereEqualTo("happy", true)
            .whereGreaterThan("age", 50)


        query.get().addOnSuccessListener {
            it.forEach { result ->
                Log.i("rubendevsd}", result.data.toString())
            }
        }
    }
}

data class UserData(
    val name: String? = null,
    val happy: Boolean? = true,
    val age: Int? = null
)