package android.inflabnet.mytest.mesas.adapter

import android.inflabnet.mytest.R
import android.inflabnet.mytest.mesas.model.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item.view.*

class MessageAdapter (val messages: ArrayList<Message>, val itemClick: (Message) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(messages[position])
    }

    override fun getItemCount() = messages.size

    class ViewHolder(view: View, val itemClick: (Message) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindForecast(message: Message) {
            with(message) {
                if(message.self == false) {
                    itemView.txtUserChat.text = message.userChat
                    itemView.messageAdapterMessageItem.text = message.text
                    //itemView.setOnClickListener { itemClick(this) }
                    itemView.txtUserChat.visibility = View.VISIBLE
                    itemView.messageAdapterMessageItem.visibility = View.VISIBLE
                    itemView.txtSelf.visibility = View.GONE
                    itemView.txtSelMessage.visibility = View.GONE
                    itemView.cView1.visibility = View.GONE
                    itemView.cView2.visibility = View.GONE
                    itemView.cView3.visibility = View.VISIBLE
                    itemView.cView4.visibility = View.VISIBLE
                }else{
                    itemView.txtSelf.text = message.userChat
                    itemView.txtSelMessage.text = message.text
                    //itemView.setOnClickListener { itemClick(this) }
                    itemView.txtUserChat.visibility = View.GONE
                    itemView.messageAdapterMessageItem.visibility = View.GONE
                    itemView.txtSelf.visibility = View.VISIBLE
                    itemView.txtSelMessage.visibility = View.VISIBLE
                    itemView.cView1.visibility = View.VISIBLE
                    itemView.cView2.visibility = View.VISIBLE
                    itemView.cView3.visibility = View.GONE
                    itemView.cView4.visibility = View.GONE
            }
            }
        }
    }
}