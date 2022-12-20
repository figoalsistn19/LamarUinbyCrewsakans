package com.pmsi.lamaruin.ui.mahasiswa.listJob

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.databinding.ItemListJobBinding

class ListRecomJobAdapter constructor(
    private val data: MutableList<ItemJob> = mutableListOf(),
    private var listener: (ItemJob) -> Unit
) :
    RecyclerView.Adapter<ListRecomJobAdapter.FileViewHolder>() {

    fun setData(data: MutableList<ItemJob>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemListJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ItemJob) {
            binding.itemFotoCompany.load(data.foto)
            binding.itemPosition.text = data.job_name
            binding.itemCompanyName.text = data.company_name
            binding.itemCompanyLocation.text = data.company_city
            binding.itemDeadline.text = data.tenggat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecomJobAdapter.FileViewHolder =
        FileViewHolder(
            ItemListJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListRecomJobAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int = data.size

}