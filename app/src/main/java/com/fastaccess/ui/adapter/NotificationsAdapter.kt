package com.fastaccess.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fastaccess.R
import com.fastaccess.data.dao.GroupedNotificationModel
import com.fastaccess.ui.adapter.viewholder.NotificationsHeaderViewHolder
import com.fastaccess.ui.adapter.viewholder.NotificationsViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */
class NotificationsAdapter :
    BaseRecyclerAdapter<GroupedNotificationModel, BaseViewHolder<GroupedNotificationModel>, BaseViewHolder.OnItemClickListener<GroupedNotificationModel>> {
    private var showUnreadState: Boolean
    private var hideClear = false

    constructor(
        eventsModels: MutableList<GroupedNotificationModel>,
        showUnreadState: Boolean
    ) : super(eventsModels) {
        this.showUnreadState = showUnreadState
    }

    constructor(
        eventsModels: MutableList<GroupedNotificationModel>,
        showUnreadState: Boolean,
        hideClear: Boolean
    ) : super(eventsModels) {
        this.showUnreadState = showUnreadState
        this.hideClear = hideClear
    }

    override fun viewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<GroupedNotificationModel> {
        return if (viewType == GroupedNotificationModel.HEADER) {
            NotificationsHeaderViewHolder.newInstance(parent, this)
        } else {
            NotificationsViewHolder.newInstance(parent, this, showUnreadState)
        }
    }

    override fun onBindView(holder: BaseViewHolder<GroupedNotificationModel>, position: Int) {
        if (getItemViewType(position) == GroupedNotificationModel.HEADER) {
            val header = getItem(position)!!
            (holder as NotificationsHeaderViewHolder).bind(header)
            // View holders are recycled, so this must set both VISIBLE and
            // GONE explicitly (the original only ever set VISIBLE, which
            // could leak a stale visible button onto an unrelated recycled
            // header). Stays visible once allRead so the user can tap again
            // to remove the group - see AllNotificationsPresenter.onItemClick.
            val hasUnreadFirstItem = getItem(
                (position + 1).coerceAtMost(itemCount - 1)
            )?.notification?.unread == true
            holder.itemView.findViewById<View>(R.id.markAsRead).visibility =
                if (header.allRead || (hideClear && hasUnreadFirstItem)) View.VISIBLE else View.GONE
        } else {
            (holder as NotificationsViewHolder).bind(getItem(position)!!)
        }
        if (getItem(position)!!.type == GroupedNotificationModel.HEADER) {
            val layoutParams =
                holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            layoutParams.isFullSpan = true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.type
    }
}