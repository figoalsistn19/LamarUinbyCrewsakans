package com.pmsi.lamaruin.ui.recruiter.activelisting.detail.detailpelamar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.databinding.ItemListEditExperienceBinding

class ListEditExpRecAdapter constructor(
    private val data: MutableList<Experience> = mutableListOf()
) :
    RecyclerView.Adapter<ListEditExpRecAdapter.FileViewHolder>() {

    fun setData(data: MutableList<Experience>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemListEditExperienceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Experience) {
            binding.tahun.text = "${data.experience_start_date} - ${data.experience_end_date}"
            binding.namaPerusahaan.text = data.title
            binding.posisi.text = data.role
            binding.deskripsiExperience.text = data.experience_desc
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEditExpRecAdapter.FileViewHolder =
        FileViewHolder(
            ItemListEditExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListEditExpRecAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

}