package com.pmsi.lamaruin.ui.mahasiswa.applied

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pmsi.lamaruin.R
import com.pmsi.lamaruin.data.model.AppliedJob
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.databinding.ItemListAppliedBinding
import com.pmsi.lamaruin.databinding.ItemListJobBinding

class AppliedJobAdapter constructor(
    private val data: MutableList<AppliedJob> = mutableListOf()
) :
    RecyclerView.Adapter<AppliedJobAdapter.FileViewHolder>() {

    fun setData(data: MutableList<AppliedJob>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    inner class FileViewHolder(private val binding: ItemListAppliedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AppliedJob) {
            var status = data.status

            binding.appliadFotoCompany.load(data.company_photo)
            binding.appliedTitleJob.text = data.job_title
            binding.appliedNameCompany.text = data.company_name
            binding.appliedStatus.text = status

            if(status == "Pending"){
                binding.appliedMessage.text = "Kindly wait."
                binding.appliedStatus.setTextColor(Color.parseColor("#DBD200"))
            } else if (status == "Accepted") {
                binding.appliedMessage.text = "Please check your email."
                binding.appliedStatus.setTextColor(Color.parseColor("#0DD109"))
            } else if (status == "Rejected"){
                binding.appliedMessage.text = "Better luck next time!"
                binding.appliedStatus.setTextColor(Color.parseColor("DB0000"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppliedJobAdapter.FileViewHolder =
        FileViewHolder(
            ItemListAppliedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: AppliedJobAdapter.FileViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

}