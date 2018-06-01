package online.a101app.a101

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class ChannelAdapter(context: Context, ChannelList: MutableList<Channel>) : BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = ChannelList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val channelName: String = itemList.get(position).channelName as String
        val channelSchool: String = itemList.get(position).channelSchool as String
        val view: View
        val vh: ListRowHolder




        if (convertView == null) {
            view = mInflater.inflate(R.layout.channel_row, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.nameLabel.text = channelName
        vh.schoolLabel.text = channelSchool
        return view
    }

    override fun getItem(index: Int): Any {
        return itemList.get(index)

    }

    override fun getItemId(index: Int): Long {
        return index.toLong()

    }

    override fun getCount(): Int {
        return itemList.size

    }

    private class ListRowHolder(row: View?) {
        val nameLabel: TextView = row!!.findViewById<TextView>(R.id.channel_name) as TextView
        val schoolLabel: TextView = row!!.findViewById<TextView>(R.id.channel_school) as TextView

    }
}
