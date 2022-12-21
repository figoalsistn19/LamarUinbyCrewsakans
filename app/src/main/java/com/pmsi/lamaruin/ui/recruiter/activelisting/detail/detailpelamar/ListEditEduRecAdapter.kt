package com.pmsi.lamaruin.ui.recruiter.activelisting.detail.detailpelamar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.databinding.ItemListEditEducationBinding

class ListEditEduRecAdapter constructor(
    private val data: MutableList<Education> = mutableListOf()
) :
    RecyclerView.Adapter<ListEditEduRecAdapter.FileViewHolder>() {

    fun setData(data: MutableList<Education>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemListEditEducationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Education) {
            binding.jurusan.text = data.field_of_study
            binding.degree.text = data.degree
            binding.universitas.text = data.school
            binding.year.text = "${data.education_start_date} - ${data.education_end_date}"
            binding.btnHapus.isVisible = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEditEduRecAdapter.FileViewHolder =
        FileViewHolder(
            ItemListEditEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListEditEduRecAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

}