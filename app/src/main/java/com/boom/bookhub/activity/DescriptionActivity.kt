package com.boom.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.boom.bookhub.R
import com.boom.bookhub.database.BookDatabase
import com.boom.bookhub.database.BookEntity
import com.boom.bookhub.model.Book
import com.boom.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName:TextView
    lateinit var txtAuthor:TextView
    lateinit var txtPrice:TextView
    lateinit var txtBookRating:TextView
    lateinit var imgBook:ImageView
    lateinit var txtBookDesc:TextView
    lateinit var btnAddToFav:Button
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar
    lateinit var toolbar: Toolbar
    var bookId:String?="100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        txtBookName=findViewById(R.id.txtBookName)
        txtAuthor=findViewById(R.id.txtAuthor)
        txtPrice=findViewById(R.id.txtPrice)
        txtBookRating=findViewById(R.id.txtBooKRating)
        imgBook=findViewById(R.id.imgBook)
        txtBookDesc=findViewById(R.id.txtBookDesc)
        btnAddToFav=findViewById(R.id.btnAddToFav)
        toolbar=findViewById(R.id.toolbar)
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility= View.VISIBLE
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility=View.VISIBLE
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book Details"
        if(intent!=null){
            bookId=intent.getStringExtra("book_id")
        }
        else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some error has occured!",Toast.LENGTH_LONG).show()
        }
        if(bookId=="100"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Some error has occured!",Toast.LENGTH_LONG).show()
        }
        val queue=Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"
        val jsonParams=JSONObject()
        jsonParams.put("book_id",bookId)
        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)){
            val jsonRequest=object:JsonObjectRequest(
                Request.Method.POST,url,jsonParams, Response.Listener{
                    try{
                        val success=it.getBoolean("success")
                        if(success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            val bookImageUrl=bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBook)
                            txtBookName.text=bookJsonObject.getString("name")
                            txtAuthor.text=bookJsonObject.getString("author")
                            txtPrice.text=bookJsonObject.getString("price")
                            txtBookRating.text=bookJsonObject.getString("rating")
                            txtBookDesc.text=bookJsonObject.getString("description")
                            val bookEntity=BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtAuthor.text.toString(),
                                txtPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )
                            val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav=checkFav.get()
                            if(isFav){
                                btnAddToFav.text="Remove from Favourites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                btnAddToFav.setBackgroundColor(favColor)
                            }else{
                                btnAddToFav.text="Add to Favourites"
                                val noFavColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }
                            btnAddToFav.setOnClickListener {
                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                    val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                    val result=async.get()
                                    if(result){
                                        Toast.makeText(this@DescriptionActivity,"Book added to favourites",Toast.LENGTH_LONG).show()
                                        btnAddToFav.text="Remove from favourites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                        btnAddToFav.setBackgroundColor(favColor)
                                    }else{
                                        Toast.makeText(this@DescriptionActivity,"Some error occured",Toast.LENGTH_LONG).show()
                                    }
                                }else{
                                    val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result=async.get()
                                    if(result){
                                        Toast.makeText(this@DescriptionActivity,"Book removed from favourites",Toast.LENGTH_LONG).show()
                                        btnAddToFav.text="Add to favourites"
                                        val noFavColor=ContextCompat.getColor(applicationContext,R.color.colorAccent)
                                        btnAddToFav.setBackgroundColor(noFavColor)
                                    }else{
                                        Toast.makeText(this@DescriptionActivity,"Some error occured",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                        else{
                            Toast.makeText(this@DescriptionActivity,"Some Error Occured!!!",Toast.LENGTH_LONG).show()
                        }
                    }catch (e:Exception){
                        Toast.makeText(this@DescriptionActivity,"Some Error Occured!!!",Toast.LENGTH_LONG).show()
                    }
                },Response.ErrorListener{
                    Toast.makeText(this@DescriptionActivity,"Volley Error $it!!!",Toast.LENGTH_LONG)
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="7dfd53ed5aa6f5"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }else{
            val dialog= AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

    }
    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int): AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null
                }
                2->{
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3->{
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }

            }
            return false
        }

    }
    }
