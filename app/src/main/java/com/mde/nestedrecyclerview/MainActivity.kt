package com.mde.nestedrecyclerview

import android.content.Context
import android.os.Bundle
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import com.mde.nestedlib.NestedGestureHandler


class MainActivity : AppCompatActivity() {

    //just for fun, could have been a val inside onCreate()
    private val recycler: RecyclerView by bind(R.id.improved_recycler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)
        setSupportActionBar(findViewById<Toolbar>(R.id.main_toolbar))
        setupRecyclers(LinearLayoutManager.HORIZONTAL)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val item = menu.findItem(R.id.spinner)
        val spinner = MenuItemCompat.getActionView(item) as Spinner

        val adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setupRecyclers(if (p2 == 0) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL)
            }

        }
        return true
    }

    fun setupRecyclers(orientation: Int) {
        //init improved recyclerView
        recycler.layoutManager = LinearLayoutManager(this, orientation, false)
        recycler.adapter = SubRecyclerAdapter()
        recycler.adapter.notifyDataSetChanged()

        //init classic recyclerView
        val classicRecycler: RecyclerView by bind(R.id.classic_recycler)
        classicRecycler.layoutManager = LinearLayoutManager(this, orientation, false)
        classicRecycler.adapter = SubRecyclerAdapter()
        classicRecycler.adapter.notifyDataSetChanged()
    }

    /**
     * An adapter for sub recycler views
     */
    class SubRecyclerAdapter() : RecyclerView.Adapter<SubRecyclerAdapter.RecyclerViewHolder>() {
        class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        class ScrollableRecycler(context: Context?) : RecyclerView(context), NestedGestureHandler.ScrollableChild {
            override fun canChildScroll(): Boolean {
                return true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val recycler: RecyclerView = ScrollableRecycler(parent.context)
            val layoutManager: LinearLayoutManager = (parent as RecyclerView).layoutManager as LinearLayoutManager

            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                recycler.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
                recycler.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parent.context.convertDipsToPixel(90))
            } else {
                recycler.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
                recycler.layoutParams = ViewGroup.LayoutParams(parent.context.convertDipsToPixel(90), ViewGroup.LayoutParams.MATCH_PARENT)
            }

            val baseColor: Int = 255 / itemCount * viewType
            recycler.adapter = CellAdapter(baseColor)
            recycler.adapter.notifyDataSetChanged()
            return RecyclerViewHolder(recycler)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            (holder.itemView as RecyclerView).scrollToPosition(0)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    /**
     * An adapter for cell views inside sub recyclerView
     */
    class CellAdapter(val baseColor: Int) : RecyclerView.Adapter<CellAdapter.CellHolder>() {
        class CellHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
            val cellView: View = View(parent.context)
            val size: Int = parent.context.convertDipsToPixel(90)
            cellView.layoutParams = ViewGroup.LayoutParams(size, size)

            val frameLayout: FrameLayout = FrameLayout(parent.context)
            val padding: Int = parent.context.convertDipsToPixel(5)
            frameLayout.setPadding(padding, padding, padding, padding)
            frameLayout.addView(cellView)
            return CellHolder(frameLayout)
        }

        override fun onBindViewHolder(holder: CellHolder, position: Int) {
            val cellColor: Int = ColorUtils.HSLToColor(floatArrayOf(baseColor.toFloat(), 1f - position.toFloat() / itemCount, 0.5f))
            (holder.itemView as FrameLayout).getChildAt(0).setBackgroundColor(cellColor)
        }

        override fun getItemCount(): Int {
            return 20
        }
    }
}