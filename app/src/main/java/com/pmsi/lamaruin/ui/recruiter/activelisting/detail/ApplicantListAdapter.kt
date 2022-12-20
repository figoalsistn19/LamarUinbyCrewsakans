package com.pmsi.lamaruin.ui.recruiter.activelisting.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.model.ItemListPelamar
import com.pmsi.lamaruin.databinding.ItemApplicantListBinding
import com.pmsi.lamaruin.databinding.ItemListJobBinding

class ApplicantListAdapter constructor(
    private val data: MutableList<ItemListPelamar> = mutableListOf(),
    private var listener: (ItemListPelamar) -> Unit
) :
    RecyclerView.Adapter<ApplicantListAdapter.FileViewHolder>() {

    fun setData(data: MutableList<ItemListPelamar>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemApplicantListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ItemListPelamar) {
            binding.ivApplicant.load(data.foto)
            binding.applicantName.text = data.nama
            binding.applicantEmail.text = data.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantListAdapter.FileViewHolder =
        FileViewHolder(
            ItemApplicantListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ApplicantListAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int = data.size

}