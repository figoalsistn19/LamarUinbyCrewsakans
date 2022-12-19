package com.pmsi.lamaruin.ui.mahasiswa.profil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pmsi.lamaruin.data.model.Education
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.databinding.ItemListEditEducationBinding
import java.text.SimpleDateFormat
import java.util.*

class ListEditEduAdapter constructor(
    private val data: MutableList<Education> = mutableListOf(),
    private var listener: (String?) -> Unit
) :
    RecyclerView.Adapter<ListEditEduAdapter.FileViewHolder>() {

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
            binding.btnHapus.setOnClickListener {
                listener(data.id_edu)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEditEduAdapter.FileViewHolder =
        FileViewHolder(
            ItemListEditEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListEditEduAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

}