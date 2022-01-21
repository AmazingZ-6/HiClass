package com.example.hiclass.schedule


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.GetClassInfo
import com.example.hiclass.R
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.item_add.ItemAdd
import com.example.hiclass.item_edit.ItemEdit
import com.example.hiclass.utils.ChangeItem.AddItemList
import com.example.hiclass.utils.ChangeItem.changedItem
import com.example.hiclass.utils.ChangeItem.itemAddFlag
import com.example.hiclass.utils.ChangeItem.itemDeleteFlag
import com.example.hiclass.utils.ChangeItem.itemUpdateFlag
import com.example.hiclass.utils.ViewUtil
import com.example.hiclass.utils.ViewUtil.getScreenHeight
import com.example.hiclass.utils.ViewUtil.getScreenWidth
import com.google.android.material.navigation.NavigationView
import java.util.regex.Pattern


class ScheduleFragment : Fragment() {

    private class ViewMap(r: RelativeLayout, v: View, i: Long) {
        val re = r
        val vi = v
        val id = i
    }

    private val viewList = mutableListOf<ViewMap>()
    private var weekNum = 0
    private lateinit var pageViewModel: ScheduleViewModel

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pageViewModel.updateFlag()
        pageViewModel.addFlag()
        Log.d("testing", weekNum.toString() + "onResume!")
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
////        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.toolbar, menu)
//    }
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.id.menu_add).isVisible = true
//        return super.onPrepareOptionsMenu(menu)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel =
            ViewModelProvider(activity as ScheduleMain).get(ScheduleViewModel::class.java).apply {
                setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 0)
            }
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_bar_main, container, false)
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_add -> {
//                val intent = Intent(activity, ItemAdd::class.java)
//                startActivity(intent)
//            }
//        }
//        return true
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_SECTION_NUMBER) }?.apply {
            setHasOptionsMenu(true)
            weekNum = getInt(ARG_SECTION_NUMBER)
            Log.d("testing", weekNum.toString() + "onViewCreated!")

            pageViewModel.changeFlag.observe(viewLifecycleOwner, Observer {
                when (it) {
                    1 -> {
                        updateRefresh(view)
                    }

                    2 -> {
                        deleteRefresh(view)
                    }

                    3 -> {
                        addRefresh(view)
                    }
                }

            })




            if (hasAskClassInfo) {
                val dayL = weekList[weekNum - 1].dayItemList
                for (i in 0 until dayL.size) {
                    createItemView(
                        view, dayL[i]
                    )
                }

                createLeftView(view)
//                val toolbar: Toolbar = view.findViewById(R.id.toolbar)
//                val drawerLayout: DrawerLayout = view.findViewById(R.id.drawer_layout)
//                val navView: NavigationView = view.findViewById(R.id.nav_view)
//                val mActivity = activity as AppCompatActivity
//                mActivity.setSupportActionBar(toolbar)
//                val titleString = "第" + weekNum + "周"
//                toolbar.title = titleString
//                mActivity.supportActionBar?.let {
//                    it.setDisplayHomeAsUpEnabled(true)
//                    it.setHomeAsUpIndicator(R.drawable.ic_baseline_settings_24)
//                }
//                toolbar.inflateMenu(R.menu.toolbar)
//                toolbar.setNavigationOnClickListener {
//                    drawerLayout.openDrawer(GravityCompat.START)
//                }
//                navView.setNavigationItemSelectedListener {
//                    when (it.itemId) {
//                        R.id.nav_login -> {
//                            val intent = Intent(      //注意在这里杀掉服务进程
//                                activity,
//                                GetClassInfo::class.java
//                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                            intent.putExtra("isReLogin", "true")
//                            startActivity(intent)
//                            android.os.Process.killProcess(android.os.Process.myPid())
//                            true
//                        }
////                        R.id.nav_timer -> {
////                            val intent = Intent(activity, AlarmEdit::class.java)
////                            startActivity(intent)
////                            true
////                        }
////                        R.id.nav_about_me -> {
////                            val intent = Intent(activity, AboutMe::class.java)
////                            startActivity(intent)
////                            true
////                        }
//
//                        else -> false
//                    }
//                }
            }


        }
    }


    private fun createItemView(
        view: View, item: ItemDataBean
    ) {
        val name = item.itemName
        val week = item.itemWeek
        val address = item.itemAddress
        val teacher = item.itemTeacher
        val time = item.getTimeString2()
        val p = Pattern.compile("[0-9]")
        var classStart = 0
        var classEnd = 0
        val m = p.matcher(time)
        val s: MutableList<Int> = ArrayList()
        var a = 0
        while (m.find()) {
            s.add(a++, m.group().toInt())
        }
        val re = ViewUtil.getDayView(view, s[0])
        val v: View = LayoutInflater.from(this.context).inflate(R.layout.course_card, null)

        val viewMapEntity = ViewMap(re, v, item.id)

        viewList.add(viewMapEntity)

        if (s.size == 3) {
            classStart = s[1]
            classEnd = s[2]
        }
        if (s.size == 4) {
            classStart = s[1]
            classEnd = s[2] * 10 + s[3]
        }
        val height = ((classStart + 1) / 2 - 1) * 360 //1-0 3-360 5-720 7-1080
        v.y = height.toFloat()
        val textView: TextView = v.findViewById(R.id.text_view)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (180) * (classEnd - classStart + 1) - 8
        ) //设置布局高度,即跨多少节课
        v.isClickable = true

        if (height <= 360) {
            v.setBackgroundColor(Color.rgb(255, 187, 51))
            textView.setBackgroundColor(Color.rgb(255, 187, 51))
        }

        if (height in 361..1080) {
            v.setBackgroundColor(Color.rgb(255, 68, 68))
            textView.setBackgroundColor(Color.rgb(255, 68, 68))
        }

        if (height > 1080) {
            v.setBackgroundColor(Color.rgb(0, 153, 204))
            textView.setBackgroundColor(Color.rgb(0, 153, 204))
        }
        textView.text = name
        v.setOnClickListener {
            val screenH = getScreenHeight(this.requireContext())
            val screenW = getScreenWidth(this.requireContext())
            val popView: View =
                LayoutInflater.from(this.context).inflate(R.layout.popup_class_window, null)
            val popWindow: PopupWindow = PopupWindow(popView, screenW, 650)
            val parentView: View =
                LayoutInflater.from(this.context).inflate(R.layout.activity_main, null)

            popWindow.isOutsideTouchable = true
            popWindow.isFocusable = true
            popWindow.animationStyle = R.style.popupAnimation
            popWindow.showAtLocation(parentView, 0, 200, 2000)
            val buttonAlarmEdit: Button = popView.findViewById(R.id.button_edit)
//            buttonAlarmEdit.setOnClickListener {
//                val alarmIntent = Intent(activity, SetAlarmClock::class.java)
//                val alarmName = "$name#$time#$week"
//                alarmIntent.putExtra("alarm_name", alarmName)
//                startActivity(alarmIntent)
//                popWindow.dismiss()
//            }
            val buttonItemEdit: Button = popView.findViewById(R.id.item_edit)
            buttonItemEdit.setOnClickListener {
                val intent = Intent(activity, ItemEdit::class.java)
                intent.putExtra("item_week", item.itemWeek)
                intent.putExtra("item_id", item.id)
                startActivity(intent)
                popWindow.dismiss()
            }

            val buttonItemDelete: Button = popView.findViewById(R.id.item_delete)
            buttonItemDelete.setOnClickListener {
                activity?.let { it1 ->
                    AlertDialog.Builder(it1).apply {
                        setTitle("是否删除单个事项")
                        setMessage("点击编辑可以进行批量删除")
                        setCancelable(false)
                        setPositiveButton("是") { dialog, which ->
                            changedItem = item
                            itemDeleteFlag = 1
                            pageViewModel.deleteFlag()
                            popWindow.dismiss()
                        }

                        setNegativeButton("否") { dialog, which ->

                        }
                    }.show()
                }
            }
            val popTextView1: TextView = popView.findViewById(R.id.popclasstext_view1)
            val popTextView2: TextView = popView.findViewById(R.id.popclasstext_view2)
            val popTextView3: TextView = popView.findViewById(R.id.popclasstext_view3)
            val popTextView4: TextView = popView.findViewById(R.id.popclasstext_view4)
            val popTextView5: TextView = popView.findViewById(R.id.popclasstext_view5)
            popTextView1.text = time
            popTextView2.text = name
            popTextView3.text = address
            popTextView4.text = teacher
//            if (isSetAlarm) {
//                val timeToString =
//                    "⏰" + AlarmTime[0].toString() + AlarmTime[1].toString() + ":" + AlarmTime[2].toString() + AlarmTime[3].toString()
//                popTextView5.text = timeToString
//            }
        }
        v.layoutParams = params
        re.addView(v)

    }

    private fun createLeftView(view: View) {
        val leftParentView: LinearLayout = view.findViewById(R.id.left_view_layout)
        for (i in 0..11) {
            val leftView = LayoutInflater.from(this.context).inflate(R.layout.left_view, null)
            leftView.y = (i * 90 + 54).toFloat()
            val textView: TextView = leftView.findViewById(R.id.time_start)
            val num = i + 1
            textView.text = "$num"
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                92
            )
            leftView.layoutParams = params
            leftParentView.addView(leftView)
        }
    }

    private fun updateRefresh(view: View) {

        val item = changedItem
        if (item != null) {
            for (entity in viewList) {
                if (entity.id == item.id) {
                    entity.re.removeView(entity.vi)
                    createItemView(view, item)
                    viewList.remove(entity)
                    Log.d("testing", weekNum.toString() + "refresh!")
                    itemUpdateFlag = 0
                    itemDeleteFlag = 0
                    itemAddFlag = 0
                    changedItem = null
                    break
                }
            }
        }

    }

    private fun deleteRefresh(view: View) {
        val item = changedItem
        for (entity in viewList) {
            if (entity.id == item?.id) {
                entity.re.removeView(entity.vi)
                viewList.remove(entity)
                itemUpdateFlag = 0
                itemDeleteFlag = 0
                itemAddFlag = 0
                changedItem = null
                break
            }
        }
    }

    private fun addRefresh(view: View) {
        var isCreated = false
        val existsList = arrayListOf<ItemDataBean>()
        if (AddItemList != null) {
            Log.d("testing", weekNum.toString() + "AddItem!!")
            for (entity in AddItemList!!) {
                if (entity.itemWeek == weekNum) {
                    for (i in viewList) {
                        if (i.id == entity.id) {
                            isCreated = true
                            break
                        }
                    }
                    if (!isCreated) {
                        weekList[weekNum - 1].dayItemList.add(entity)
                        createItemView(view, entity)
                        existsList.add(entity)
                    }
                }
            }
            for (j in existsList) {
                AddItemList!!.remove(j)
            }
        } else {
            itemUpdateFlag = 0
            itemDeleteFlag = 0
            itemAddFlag = 0
            AddItemList = null
        }
    }
}


