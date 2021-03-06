package com.yung_coder.oluwole.akeko.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.firebase.database.*
import com.yung_coder.oluwole.akeko.Material
import com.yung_coder.oluwole.akeko.Menu
import com.yung_coder.oluwole.akeko.R
import com.yung_coder.oluwole.akeko.models.Models


/**
 * Created by yung on 8/23/17.
 */
class LangAdapter constructor(mList: ArrayList<Models.lang>?) : RecyclerView.Adapter<LangAdapter.LangViewAdapter>() {

    var mList: ArrayList<Models.lang>? = null
    var generator: ColorGenerator? = ColorGenerator.MATERIAL
    var app_context: Context? = null

    init {
        this.mList = mList
    }

    override fun getItemCount(): Int {
        if (null == mList) return 0
        return mList!!.count()
    }

    override fun onBindViewHolder(holder: LangViewAdapter?, position: Int) {
        val lang_data = mList?.get(position)
        holder?.mag_name?.text = lang_data?.name
        val letter = lang_data?.name?.get(0).toString()
        val drawable = generator?.randomColor?.let { TextDrawable.builder().buildRound(letter, it) }
        holder?.mag_thumbnail?.setImageDrawable(drawable)

        holder?.lang_item?.setOnClickListener {

            val bookList: ArrayList<Models.book>? = ArrayList()
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.getReference(lang_data?.name)

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    Toast.makeText(app_context, "There seems to be a Network Connectivity Issue as a connection could not be established to the Akekọ Server", Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    bookList?.clear()
                    p0?.children?.forEach { noteSnapshot ->
                        val note = noteSnapshot.getValue(Models.book::class.java)
                        if (note != null) {
                            bookList?.add(note)
                        }
                    }
                    val count = bookList?.count()
                    if (count != null) {
                        if (count > 0) {
                            val intent = Intent(app_context as Menu, Material::class.java)
                            intent.putExtra("lang_name", lang_data?.name)
                            app_context?.startActivity(intent)
                        } else {
                            Toast.makeText(app_context, "No Materials for ${lang_data?.name} Available Yet", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(app_context, "No Materials for ${lang_data?.name} Available Yet", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LangViewAdapter {
        val rootView = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
        val view_adapter = LangViewAdapter(rootView)
        app_context = rootView.context
        return view_adapter
    }

    class LangViewAdapter(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var mag_name = layoutView.findViewById<TextView>(R.id.lang_name)!!
        var mag_thumbnail = layoutView.findViewById<ImageView>(R.id.lang_image)!!

        var lang_item: RelativeLayout = layoutView.findViewById<RelativeLayout>(R.id.lang_list)
    }
}