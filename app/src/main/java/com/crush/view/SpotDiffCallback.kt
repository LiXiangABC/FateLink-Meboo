package com.crush.view

import androidx.recyclerview.widget.DiffUtil
import com.crush.bean.MatchIndexBean

class SpotDiffCallback(
    private val old: List<MatchIndexBean>,
    private val new: List<MatchIndexBean>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].userCode == new[newPosition].userCode
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
