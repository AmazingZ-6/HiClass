package com.example.hiclass.schedule


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.ListPopupWindow.WRAP_CONTENT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.*
import com.example.hiclass.alarm_set.SetAlarm
import com.example.hiclass.data_class.ItemDataBean
import com.example.hiclass.item_edit.ItemEdit
import com.example.hiclass.utils.ChangeItem.AddItemList
import com.example.hiclass.utils.ChangeItem.changedItem
import com.example.hiclass.utils.ChangeItem.deleteItemIdList
import com.example.hiclass.utils.ChangeItem.itemAddFlag
import com.example.hiclass.utils.ChangeItem.itemBatchDeleteFlag
import com.example.hiclass.utils.ChangeItem.itemDeleteFlag
import com.example.hiclass.utils.ChangeItem.itemUpdateFlag
import com.example.hiclass.utils.ViewUtil
import com.example.hiclass.utils.ViewUtil.getScreenHeight
import com.example.hiclass.utils.ViewUtil.getScreenWidth
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
    }


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_SECTION_NUMBER) }?.apply {
            setHasOptionsMenu(true)
            weekNum = getInt(ARG_SECTION_NUMBER)
            Log.d("testing", weekNum.toString() + "onViewCreated!")

//            pageViewModel.exitFlag.observe(viewLifecycleOwner, Observer {
//                if (it){
//                    for (v in viewList){
//                        v.re.removeView(v.vi)
//                    }
//                    viewList.clear()
//                }
//            })

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
                    4 -> {
                        deleteBatchRefresh(view)
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


        if (s.size == 3) {
            classStart = s[1]
            classEnd = s[2]
        }
        if (s.size == 4) {
            classStart = s[1]
            classEnd = s[2] * 10 + s[3]
        }
        val height = ((classStart + 1) / 2 - 1) * 360 //1-0 3-360 5-720 7-1080
        var v: View = LayoutInflater.from(this.context).inflate(R.layout.course_card_1, null)
        if (height <= 360) {
            v = LayoutInflater.from(this.context).inflate(R.layout.course_card_1, null)
        }
        if (height in 361..1080) {
            v = LayoutInflater.from(this.context).inflate(R.layout.course_card_2, null)
        }
        if (height > 1080) {
            v = LayoutInflater.from(this.context).inflate(R.layout.course_card_3, null)
        }

        v.y = height.toFloat()
        val viewMapEntity = ViewMap(re, v, item.id)
        viewList.add(viewMapEntity)
        val textView: TextView = v.findViewById(R.id.text_view)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (180) * (classEnd - classStart + 1) - 8
        ) //设置布局高度,即跨多少节课
        v.isClickable = true

        textView.text = name
        v.setOnClickListener {
            val screenH = getScreenHeight(this.requireContext())
            val screenW = getScreenWidth(this.requireContext())
            val popView: View =
                LayoutInflater.from(this.context).inflate(R.layout.popup_class_window, null)
            val popWindow: PopupWindow = PopupWindow(popView, screenW, WRAP_CONTENT)
            val parentView: View =
                LayoutInflater.from(this.context).inflate(R.layout.activity_main, null)

            popWindow.isOutsideTouchable = true
            popWindow.isFocusable = true
            popWindow.animationStyle = R.style.popupAnimation
            popWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 2000)
//            val buttonAlarmEdit: Button = popView.findViewById(R.id.button_edit)
//            buttonAlarmEdit.setOnClickListener {
//                val alarmIntent = Intent(activity, SetAlarmClock::class.java)
//                val alarmName = "$name#$time#$week"
//                alarmIntent.putExtra("alarm_name", alarmName)
//                startActivity(alarmIntent)
//                popWindow.dismiss()
//            }

            val buttonItemEdit: AppCompatTextView = popView.findViewById(R.id.item_edit)
            val buttonItemDelete: AppCompatTextView = popView.findViewById(R.id.item_delete)
            val buttonItemClock: AppCompatTextView = popView.findViewById(R.id.item_clock)
            val popName: AppCompatTextView = popView.findViewById(R.id.pop_name)
            val popAddress: AppCompatTextView = popView.findViewById(R.id.pop_address)
            val popTeacher: AppCompatTextView = popView.findViewById(R.id.pop_teacher)
            val popRemark: AppCompatTextView = popView.findViewById(R.id.pop_remark)
            val font = Typeface.createFromAsset(App.context.assets, "iconfont.ttf")
            popName.typeface = font
            popName.text = App.context.resources.getString(R.string.icon_name)
            popAddress.typeface = font
            popAddress.text = App.context.resources.getString(R.string.icon_address)
            popTeacher.typeface = font
            popTeacher.text = App.context.resources.getString(R.string.icon_user)
            popRemark.typeface = font
            popRemark.text = App.context.resources.getString(R.string.icon_remark)
            buttonItemEdit.typeface = font
            buttonItemEdit.text = App.context.resources.getString(R.string.icon_edit)
            buttonItemDelete.typeface = font
            buttonItemDelete.text = App.context.resources.getString(R.string.icon_delete_edit)
            buttonItemClock.typeface = font
            buttonItemClock.text = App.context.resources.getString(R.string.icon_set_clock)

            buttonItemClock.setOnClickListener {
                val intent = Intent(activity, SetAlarm::class.java)
                intent.putExtra("term_day", item.getTimeString3())
                intent.putExtra("name", item.itemName)
                startActivity(intent)
                popWindow.dismiss()
            }

            buttonItemEdit.setOnClickListener {
                val intent = Intent(activity, ItemEdit::class.java)
                intent.putExtra("item_week", item.itemWeek)
                intent.putExtra("item_id", item.id)
                startActivity(intent)
                popWindow.dismiss()
            }


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
            popTextView1.text = item.getTimeString3()
            popTextView2.text = name
            popTextView3.text = teacher
            popTextView4.text = address
//            popTextView5.text =
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
            Log.d("testing", weekNum.toString() + AddItemList!!.size.toString())
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

    private fun deleteBatchRefresh(view: View) {
        val idList = deleteItemIdList
        val temp = arrayListOf<Long>()
        for (i in idList) {
            for (j in viewList) {
                if (i == j.id) {
                    j.re.removeView(j.vi)
                    viewList.remove(j)
                    temp.add(i)
                    break
                }
            }
        }
        for (e in temp) {
            idList.remove(e)
        }
        if (idList.size == 0) {
            itemBatchDeleteFlag = 0
            itemUpdateFlag = 0
            itemDeleteFlag = 0
            itemAddFlag = 0
            changedItem = null
        }
    }

}


