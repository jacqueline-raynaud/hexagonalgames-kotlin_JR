package com.openclassrooms.hexagonal.games.presentation.screen.postdetail.comments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.data.model.CommentDto
import com.openclassrooms.hexagonal.games.presentation.screen.postdetail.comments.CommentAdapter

@Composable
fun FirebaseUiCommentsList(
    postId: String,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            val firestore = FirebaseFirestore.getInstance()

            val query: Query = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.ASCENDING)

            val options = FirestoreRecyclerOptions.Builder<CommentDto>()
                .setQuery(query, CommentDto::class.java)
                .setLifecycleOwner(lifecycleOwner)   // démarre/arrête l'écoute tout seul
                .build()

            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = CommentAdapter(options)
            }
        }
    )
}