package com.gaur.contentproviders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gaur.contentproviders.databinding.ViewHolderContactsBinding

class ContactAdapter(private val list:List<String>)  : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    inner class MyViewHolder(val viewDataBinding:ViewHolderContactsBinding):RecyclerView.ViewHolder(viewDataBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.MyViewHolder {
         val binding = ViewHolderContactsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactAdapter.MyViewHolder, position: Int) {
         val binding = holder.viewDataBinding
        binding.tvName.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }
}