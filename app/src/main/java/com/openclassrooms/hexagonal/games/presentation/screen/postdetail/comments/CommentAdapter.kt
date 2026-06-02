package com.openclassrooms.hexagonal.games.presentation.screen.postdetail.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.data.model.CommentDto

class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val authorView: TextView = view.findViewById(R.id.comment_author)
    private val contentView: TextView = view.findViewById(R.id.comment_content)

    fun bind(comment: CommentDto) {
        authorView.text = comment.author?.nameUser ?: "Anonyme"
        contentView.text = comment.content
    }
}

class CommentAdapter(options: FirestoreRecyclerOptions<CommentDto>) :
    FirestoreRecyclerAdapter<CommentDto, CommentViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    // model = le CommentDto déjà désérialisé par FirebaseUI
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: CommentDto) {
        holder.bind(model)
    }
}