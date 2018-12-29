package com.example.firstapp

import android.app.Fragment
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

class SecondFragment: Fragment() {

    lateinit var url: String
    lateinit var vBrowser: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vRecView = activity.findViewById<RecyclerView>(R.id.act1_recView)
        vRecView.layoutManager = LinearLayoutManager(activity)

        url = arguments.getString("url")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.sec_fragment, container, false)

        vBrowser = view.findViewById<WebView>(R.id.frag2_browser)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vBrowser.loadUrl(url)
    }

    override fun onDestroy() {
        if (fragmentManager.backStackEntryCount == 0) {
            val vRecView = activity.findViewById<RecyclerView>(R.id.act1_recView)
            vRecView.layoutManager = GridLayoutManager(activity, 2)
        }
        super.onDestroy()
    }
}