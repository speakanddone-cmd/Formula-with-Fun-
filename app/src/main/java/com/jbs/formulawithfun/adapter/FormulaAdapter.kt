package com.jbs.formulawithfun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jbs.formulawithfun.R

class FormulaAdapter(
    private var items: List<Formula>,
    private val onFavoriteClicked: (Formula, Boolean) -> Unit,
    private val onSpeakClicked: (Formula, Int) -> Unit // Pass formula and position
) : RecyclerView.Adapter<FormulaAdapter.FormulaViewHolder>() {

    private var speakingIndex: Int? = null // Tracks which formula is being spoken

    fun setSpeakingIndex(index: Int?) {
        speakingIndex = index
        notifyDataSetChanged()
    }

    inner class FormulaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvFormulaTitle)
        val tvExplanation: TextView = view.findViewById(R.id.tvFormulaExplanation)
        val imgFavorite: ImageView = view.findViewById(R.id.imgFavorite)
        val imgSpeak: ImageView = view.findViewById(R.id.imgSpeak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormulaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_formula, parent, false)
        return FormulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormulaViewHolder, position: Int) {
        val item = items[position]

        // Numbering relative to the visible list (1..N)
        val displayIndex = position + 1
        holder.tvTitle.text = "$displayIndex. ${item.title}"
        holder.tvExplanation.text = item.explanation

        // -- Favorite Star: Icon and Tint --
        if (item.isFavorite) {
            holder.imgFavorite.setImageResource(R.drawable.ic_star_24)
            holder.imgFavorite.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.starActive)
            )
        } else {
            holder.imgFavorite.setImageResource(R.drawable.ic_star_border_24)
            holder.imgFavorite.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.starInactive)
            )
        }

        holder.imgFavorite.setOnClickListener {
            // Animation for "pop"
            holder.imgFavorite.animate()
                .scaleX(1.25f).scaleY(1.25f)
                .setDuration(120)
                .withEndAction {
                    holder.imgFavorite.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(90)
                        .start()
                }.start()
            // Notify activity — the activity will update master list and refresh adapter
            onFavoriteClicked(item, !item.isFavorite)
        }

        // -- Speaker icon ON/OFF state --
        holder.imgSpeak.setImageResource(
            if (speakingIndex == position) R.drawable.ic_volume_up else R.drawable.ic_volume_off
        )
        holder.imgSpeak.setOnClickListener { onSpeakClicked(item, position) }

        // Optional: show subtle visual feedback for speaking index on the item view
        holder.itemView.isActivated = (speakingIndex == position)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Formula>) {
        items = newItems
        speakingIndex = null // Reset speaking when list changes
        notifyDataSetChanged()
    }
}
