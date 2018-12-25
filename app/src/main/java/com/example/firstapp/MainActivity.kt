package com.example.firstapp

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) { //подключение библиотек
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment) //задает UI

        if (savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putString("param", "value")
            val f = MainFragment()
            f.arguments = bundle
            fragmentManager.beginTransaction().replace(R.id.fragment_place, f).commitAllowingStateLoss() //коммит просто потеряется, если что-то пойдет не так

        }
    }

    fun showArticle(url: String) {
        val bundle = Bundle()
        bundle.putString("url", url)
        val f = SecondFragment()
        f.arguments = bundle

        val frame2 = findViewById<View>(R.id.fragment_place2)
        if (frame2 != null) {
            frame2.visibility = View.VISIBLE
            fragmentManager.beginTransaction().replace(R.id.fragment_place2, f).addToBackStack("main").commitAllowingStateLoss()
        } else
            fragmentManager.beginTransaction().add(R.id.fragment_place, f).addToBackStack("main").commitAllowingStateLoss()
    }


    override fun attachBaseContext(newBase: Context ) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase)
        );
    }

}

class FeedAPI(
    val items: ArrayList<FeedItemAPI>
)

class FeedItemAPI(
    val title: String,
    val pubDate: String,
    val link: String,
    val thumbnail: String,
    val description: String
)

open class Feed(
    var items: RealmList<FeedItem> = RealmList<FeedItem>()
) : RealmObject()


open class FeedItem(
    var title: String = "",
    var pubDate: String = "",
    var link: String = "",
    var thumbnail: String = "",
    var description: String = ""
) : RealmObject()


class Adapter(val items: ArrayList<FeedItemAPI>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(parent!!.context)

        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
        val vTitle = view.findViewById<TextView>(R.id.item_title)

        val item = getItem(position) as FeedItemAPI

        vTitle.text = item.title

        return view
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}


class RecAdapter(val items: RealmList<FeedItem>) : RecyclerView.Adapter<RecHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecHolder {
        val inflater = LayoutInflater.from(parent!!.context)

        val view = inflater.inflate(R.layout.list_item, parent, false)

        return RecHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecHolder?, position: Int) {
        val item = items[position]!!

        holder?.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}


class RecHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: FeedItem) {
        val vTitle = itemView.findViewById<TextView>(R.id.item_title)
        val vPubDate = itemView.findViewById<TextView>(R.id.pub_date)
        val vDesc = itemView.findViewById<TextView>(R.id.item_description)
        val vThumb = itemView.findViewById<ImageView>(R.id.item_thumbnail)
        vTitle.text = item.title
        vDesc.text = item.description.toSpanned()
        vPubDate.text = item.pubDate

        Picasso.with(vThumb.context).load(item.thumbnail).into(vThumb)

        itemView.findViewById<TextView>(R.id.card_button).setOnClickListener {
            (vThumb.context as MainActivity).showArticle(item.link)
        }

        itemView.setOnClickListener {
            (vThumb.context as MainActivity).showArticle(item.link)
        }
    }
}

fun String.toSpanned(): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(this)
    }
}