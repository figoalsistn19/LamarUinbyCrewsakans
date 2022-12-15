package com.pmsi.lamaruin.ui.mahasiswa.applied

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pmsi.lamaruin.data.model.AppliedJob
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.databinding.ItemListAppliedBinding
import com.pmsi.lamaruin.databinding.ItemListJobBinding

class AppliedJobAdapter constructor(
    private val data: MutableList<AppliedJob> = mutableListOf(),
    private var listener: (ItemJob) -> Unit
) :
    RecyclerView.Adapter<AppliedJobAdapter.FileViewHolder>() {

    fun setData(data: MutableList<AppliedJob>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemListAppliedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ItemJob) {
            binding.itemFotoCompany.load(data.foto)
            binding.itemPosition.text = data.job_name
            binding.itemCompanyName.text = data.company_name
            binding.itemCompanyLocation.text = data.company_city
            binding.itemDeadline.text = data.tenggat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListJobAdapter.FileViewHolder =
        FileViewHolder(
            ItemListJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ListJobAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int = data.size

}