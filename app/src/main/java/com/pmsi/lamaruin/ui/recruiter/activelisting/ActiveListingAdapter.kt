package com.pmsi.lamaruin.ui.recruiter.activelisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.pmsi.lamaruin.data.model.ItemJob
import com.pmsi.lamaruin.data.model.JobVacancy
import com.pmsi.lamaruin.databinding.ItemListJobBinding

class ActiveListingAdapter ( private val data: MutableList<JobVacancy> = mutableListOf(), val listener: (JobVacancy) -> Unit): RecyclerView.Adapter<ActiveListingAdapter.ActiveListingViewHolder>()


{
    private lateinit var binding: ItemListJobBinding

    inner class ActiveListingViewHolder(private val binding: ItemListJobBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: JobVacancy) {
//            binding.itemFotoCompany.load(data.foto)
//            binding.itemPosition.text = data.job_name
//            binding.itemCompanyName.text = data.company_name
//            binding.itemCompanyLocation.text = data.company_city
//            binding.itemDeadline.text = data.tenggat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveListingAdapter.ActiveListingViewHolder = ActiveListingViewHolder (
        ItemListJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ActiveListingViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}