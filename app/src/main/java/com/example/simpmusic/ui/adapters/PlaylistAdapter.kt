package com.example.simpmusic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simpmusic.R
import com.example.simpmusic.datasource.models.Short

class PlaylistAdapter(val clickListener: MusicClickListener): RecyclerView.Adapter<PlaylistAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View, clickListener: MusicClickListener): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val creatorEmail = itemView.findViewById<TextView>(R.id.tv_creator)
        private val createdDate = itemView.findViewById<TextView>(R.id.tv_date_created)
        private val songTitle = itemView.findViewById<TextView>(R.id.tv_music_name)
        private val playOrPauseButton = itemView.findViewById<ImageView>(R.id.iv_play_or_pause)
        fun bind(item: Short) {
            creatorEmail.text = item.creator.email
            createdDate.text = item.dateCreated
            songTitle.text = item.title
            playOrPauseButton.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            clickListener.onClick(bindingAdapterPosition)
        }
    }

    private val differCallback = object: DiffUtil.ItemCallback<Short>() {
        override fun areItemsTheSame(oldItem: Short, newItem: Short): Boolean {
            return oldItem.shortID == newItem.shortID
        }

        override fun areContentsTheSame(oldItem: Short, newItem: Short): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return SongViewHolder(layout, clickListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface MusicClickListener {
        fun onClick(position: Int)
    }

}