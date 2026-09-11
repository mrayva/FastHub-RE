package com.fastaccess.data.dao

import com.fastaccess.data.entity.Notification
import com.fastaccess.data.entity.Repo
import com.fastaccess.helper.InputHelper
import java.util.*

/**
 * Created by Kosh on 18 Apr 2017, 8:07 PM
 */
class GroupedNotificationModel {
    var type: Int
    var repo: Repo? = null
    var notification: Notification? = null
    var date: Date? = null

    /**
     * HEADER-only: true once every notification under this repo has been
     * marked read via the header's checkmark button. Drives the button's
     * icon (single check -> double check) and, on a second tap while true,
     * removes the whole group - see AllNotificationsPresenter.onItemClick.
     */
    var allRead: Boolean = false

    private constructor(repo: Repo) {
        type = HEADER
        this.repo = repo
    }

    constructor(notification: Notification) {
        type = ROW
        this.notification = notification
        date = notification.updatedAt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val model = other as GroupedNotificationModel
        return notification != null && model.notification != null && notification!!.id == model.notification!!.id
    }

    override fun hashCode(): Int {
        return if (notification != null) InputHelper.getSafeIntId(notification!!.id) else 0
    }

    companion object {
        const val HEADER = 1
        const val ROW = 2

        @JvmStatic
        fun construct(items: List<Notification>): List<GroupedNotificationModel> {
            val models: MutableList<GroupedNotificationModel> = ArrayList()
            if (items.isEmpty()) return models
            // This used to filter to !value.unread (i.e. only ALREADY-READ
            // notifications), which meant a repo header's group could never
            // actually contain an unread row - the mark-all-as-read button
            // (visible only when a group's first row is unread, see
            // NotificationsAdapter.onBindView) was consequently unreachable
            // in practice. "All notifications" should show everything.
            val grouped: Map<Repo, List<Notification>?> = items.asSequence()
                .groupByTo(mutableMapOf()) { it.repository!! }

            grouped.asSequence()
                .filter { (_, value) -> value != null && value.isNotEmpty() }
                .forEach { (repo, notifications) ->
                    models.add(GroupedNotificationModel(repo))
                    notifications?.asSequence()?.sortedWith { o1: Notification, o2: Notification ->
                        o2.updatedAt!!.compareTo(o1.updatedAt)
                    }?.forEach { notification: Notification ->
                        models.add(
                            GroupedNotificationModel(
                                notification
                            )
                        )
                    }
                }
            return models
        }

        @JvmStatic
        fun onlyNotifications(items: List<Notification>): List<GroupedNotificationModel> {
            return items
                .map { notification -> GroupedNotificationModel(notification) }
                .toList()
        }
    }
}