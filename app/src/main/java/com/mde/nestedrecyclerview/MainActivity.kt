package com.mde.nestedrecyclerview

import android.content.Context
import android.os.Bundle
import android.support.annotation.ArrayRes
import android.support.annotation.IdRes
import android.support.v4.graphics.ColorUtils
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
import com.mde.nestedlib.ParentRecyclerView


class MainActivity : AppCompatActivity() {

    var mNestedScrollAngle: Double = Math.PI / 4
    var mOrientation: Int = LinearLayoutManager.VERTICAL

    //just for fun, could have been a val inside onCreate()
    private val mImprovedRecycler: ParentRecyclerView by bind(R.id.improved_recycler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)
        setSupportActionBar(findViewById<Toolbar>(R.id.main_toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        setupSpinner(menu, R.id.spinner_orientation, R.array.spinner_orientation_array, object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mOrientation = if (p2 == 0) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL
                setupRecyclers()
            }

        })
        setupSpinner(menu, R.id.spinner_angle, R.array.spinner_angle_array, object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mNestedScrollAngle = Math.toRadians(resources.getStringArray(R.array.spinner_angle_array).get(position).toDouble())
                setupRecyclers()
            }
        })

        return true
    }

    private fun setupSpinner(menu: Menu, @IdRes layoutId: Int, @ArrayRes arrayId: Int, listener: AdapterView.OnItemSelectedListener) {
        val item = menu.findItem(layoutId)
        val spinner = item.actionView as Spinner

        val adapter = ArrayAdapter.createFromResource(this,
                arrayId, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.onItemSelectedListener = listener
    }

    private fun setupRecyclers() {
        setupImprovedRecycler(mOrientation)
        setupClassicRecycler(mOrientation)
    }

    private fun setupImprovedRecycler(orientation: Int) {
        //init improved recyclerView
        mImprovedRecycler.setLayoutManager(LinearLayoutManager(this, orientation, false), mNestedScrollAngle)
        mImprovedRecycler.adapter = SubRecyclerAdapter()
        mImprovedRecycler.adapter.notifyDataSetChanged()
    }

    private fun setupClassicRecycler(orientation: Int) {
        val classicRecycler: RecyclerView by bind(R.id.classic_recycler)
        classicRecycler.layoutManager = LinearLayoutManager(this, orientation, false)
        classicRecycler.adapter = SubRecyclerAdapter()
        classicRecycler.adapter.notifyDataSetChanged()
    }

    /**
     * An adapter for sub mImprovedRecycler views
     */
    class SubRecyclerAdapter() : RecyclerView.Adapter<SubRecyclerAdapter.RecyclerViewHolder>() {
        class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        class ScrollableRecycler(context: Context?) : RecyclerView(context), ParentRecyclerView.ScrollableChild {
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