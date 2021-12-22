package com.example.top10downloaderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL

class MainActivity : AppCompatActivity() {

    var arrayTitle = ArrayList<Titile>()
    lateinit var adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvMain.layoutManager = LinearLayoutManager(this)
        btnGetFeeds.setOnClickListener {

            parserRSS()

        }

//print("!!!! ${arrayTitle}")

    }

    private fun parserRSS() {

        CoroutineScope(IO).launch {
            val data = async { parser() }.await()

            try {
                withContext(Main) {
                    rvMain.adapter = RVAdapter(arrayTitle)
                    rvMain.adapter!!.notifyDataSetChanged()

                }

            } catch (e: java.lang.Exception) {
                Log.d("Main", "unable to get data")
            }
        }

    }

    fun parser(): ArrayList<Titile> {
        var qtitle = ""
        var text = ""
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            val url =
                URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
            parser.setInput(url.openStream(), null)
            var eventype = parser.eventType
            while (eventype != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (eventype) {
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when (tagName) {
                        "title" -> {
                            qtitle = text.toString()
                            val data = Titile(qtitle)
                            arrayTitle.add(data)
                        }

                    }
                    else -> {
                    }
                }
                eventype = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return arrayTitle
    }


}