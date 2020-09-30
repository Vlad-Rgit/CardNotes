package com.example.cardnotes.utils

import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView

fun <T> MutableList<T>.replaceAll(items: List<T>,
                                  adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {

    for (item in this.toList()) {
        if(!items.contains(item)) {
            val index = this.indexOf(item)
            this.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    for(item in items) {
        if(!this.contains(item)) {
            this.add(item)
            adapter.notifyItemInserted(this.size - 1)
        }
    }
}