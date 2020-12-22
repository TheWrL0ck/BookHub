package com.boom.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.boom.bookhub.R
import com.boom.bookhub.activity.DescriptionActivity
import com.boom.bookhub.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context,val itemList:ArrayList<Book>):RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtBookName:TextView=view.findViewById(R.id.txtBookName)
        val txtAuthor:TextView=view.findViewById(R.id.txtAuthor)
        val txtPrice:TextView=view.findViewById(R.id.txtPrice)
        val imgBook:ImageView=view.findViewById(R.id.imgBook)
        val rlContent:RelativeLayout=view.findViewById(R.id.rlContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book=itemList[position]
        holder.txtBookName.text=book.bookName
        holder.txtAuthor.text=book.bookAuthor
        holder.txtPrice.text=book.bookPrice
        //holder.imgBook.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBook)
        holder.rlContent.setOnClickListener{
            val intent=Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }
}