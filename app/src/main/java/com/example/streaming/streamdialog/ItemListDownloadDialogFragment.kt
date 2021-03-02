package com.example.streaming.streamdialog

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streaming.R
import com.example.streaming.streamcomponentonline.SystemUIUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


const val listDownload = "item_list"

class ItemListDownloadDialogFragment : BottomSheetDialogFragment() {

    private val handler=Handler()
    private var mCallBackItemClick: ItemClickListener?=null
    fun registerCallBack(callBack: ItemClickListener){
        mCallBackItemClick=callBack
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerMedia)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val orientation: Int = requireActivity().resources.configuration.orientation
        if (orientation==Configuration.ORIENTATION_LANDSCAPE){
            SystemUIUtils.dialogHideSystemUIByStableUI(dialog!!)
        }
        return inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet = dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState==BottomSheetBehavior.STATE_HIDDEN){
                            dismiss()
                        }
                    }
                })
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            }
        })
        val recyclerView=view.findViewById<RecyclerView>(R.id.rvListItem)
        recyclerView!!.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter= ItemAdapter(requireContext(), arguments?.getIntegerArrayList(listDownload)!!)

    }

    inner class ItemAdapter(private val context: Context, private val listDownloadItem:ArrayList<Int>):RecyclerView.Adapter<ItemAdapter.ItemListHolder>() {
        inner class ItemListHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            val icon: ImageView =itemView.findViewById(R.id.iconList)
            val title: TextView =itemView.findViewById(R.id.title)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListHolder {
            return ItemListHolder(LayoutInflater.from(context).inflate(R.layout.item_list_icon_text,parent,false))
        }

        override fun getItemCount()=listDownloadItem.size

        override fun onBindViewHolder(holder: ItemListHolder, position: Int) {
            val item=listDownloadItem[position]
            holder.title.text = item.toString()
            holder.itemView.setOnClickListener {
                try {
                    handler.postDelayed({
                        notifyDataSetChanged()
                        mCallBackItemClick?.onItemClick(item,position)
                    },300)
                }catch (e:Exception){}
            }

        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemListQuality: ArrayList<Int>): ItemListDownloadDialogFragment =
                ItemListDownloadDialogFragment().apply {
                    arguments = Bundle().apply {
                        putIntegerArrayList(listDownload, itemListQuality)
                    }
                }

    }
    interface ItemClickListener{
        fun onItemClick(itemDownload:Int,index:Int)
    }
}