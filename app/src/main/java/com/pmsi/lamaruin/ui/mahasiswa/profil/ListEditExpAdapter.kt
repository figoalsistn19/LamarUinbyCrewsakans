package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.Experience
import com.pmsi.lamaruin.databinding.ItemListEditEducationBinding
import com.pmsi.lamaruin.databinding.ItemListEditExperienceBinding

class ListEditExpAdapter constructor(
    private val data: MutableList<Experience> = mutableListOf(),
    private var listener: (String?) -> Unit
) :
    RecyclerView.Adapter<ListEditExpAdapter.FileViewHolder>() {

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
            binding.btnHapus.setOnClickListener {
                listener(data.id_experience)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEditExpAdapter.FileViewHolder =
        FileViewHolder(
            ItemListEditExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListEditExpAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

}