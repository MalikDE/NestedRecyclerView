package com.mde.nestedrecyclerview

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mde.nestedlib.ParentRecyclerView


class MainActivity : AppCompatActivity() {

    var mNestedScrollAngle: Double = Math.PI / 4
    var mOrientation: Int = LinearLayoutManager.VERTICAL

    //just for fun, could have been a val inside onCreate()
    private val mImprovedRecycler: ParentRecyclerView by bind(R.id.improved_recycler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)
        setSupportActionBar(findViewById(R.id.main_toolbar))
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
                mNestedScrollAngle = Math.toRadians(resources.getStringArray(R.array.spinner_angle_array)[position].toDouble())
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

        spinner.adapter = adapter
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
    }

    private fun setupClassicRecycler(orientation: Int) {
        val classicRecycler: RecyclerView by bind(R.id.classic_recycler)
        classicRecycler.layoutManager = LinearLayoutManager(this, orientation, false)
        classicRecycler.adapter = SubRecyclerAdapter()
    }

    /**
     * An adapter for sub mImprovedRecycler views
     */
    class SubRecyclerAdapter : RecyclerView.Adapter<SubRecyclerAdapter.RecyclerViewHolder>() {
        class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            val recycler: RecyclerView = v.findViewById(R.id.list_child)
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
            return RecyclerViewHolder(v)
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
    class CellAdapter(private val baseColor: Int) : RecyclerView.Adapter<CellAdapter.CellHolder>() {
        class CellHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
            val cellView = View(parent.context)
            val size: Int = parent.context.convertDipsToPixel(90)
            cellView.layoutParams = ViewGroup.LayoutParams(size, size)

            val frameLayout = FrameLayout(parent.context)
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

class ScrollableRecyclerView : RecyclerView, ParentRecyclerView.ScrollableChild {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun canChildScroll(): Boolean {
        return true
    }
}
