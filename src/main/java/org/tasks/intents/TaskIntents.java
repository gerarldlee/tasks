package org.tasks.intents;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;

import com.todoroo.andlib.utility.AndroidUtilities;
import com.todoroo.astrid.activity.TaskEditActivity;
import com.todoroo.astrid.activity.TaskEditFragment;
import com.todoroo.astrid.activity.TaskListActivity;
import com.todoroo.astrid.activity.TaskListFragment;
import com.todoroo.astrid.api.Filter;
import com.todoroo.astrid.api.FilterWithCustomIntent;

import org.tasks.preferences.ActivityPreferences;

public class TaskIntents {

    public static Intent getNewTaskIntent(Context context, Filter filter) {
        Intent intent;
        boolean tablet = ActivityPreferences.isTabletSized(context);
        if (tablet) {
            intent = new Intent(context, TaskListActivity.class);
            intent.putExtra(TaskListActivity.OPEN_TASK, 0L);
        } else {
            intent = new Intent(context, TaskEditActivity.class);
        }

        intent.putExtra(TaskEditFragment.OVERRIDE_FINISH_ANIM, false);
        if (filter != null) {
            intent.putExtra(TaskListFragment.TOKEN_FILTER, filter);
            if (filter.valuesForNewTasks != null) {
                String values = AndroidUtilities.contentValuesToSerializedString(filter.valuesForNewTasks);
                intent.putExtra(TaskEditFragment.TOKEN_VALUES, values);
                intent.setAction("E" + values);
            }
            if (tablet) {
                if (filter instanceof FilterWithCustomIntent) {
                    Bundle customExtras = ((FilterWithCustomIntent) filter).customExtras;
                    intent.putExtras(customExtras);
                }
            }
        } else {
            intent.setAction("E");
        }
        return intent;
    }

    public static PendingIntent getEditTaskPendingIntent(Context context, final Filter filter, final long taskId) {
        boolean tablet = ActivityPreferences.isTabletSized(context);
        if (tablet) {
            Intent intent = new Intent(context, TaskListActivity.class) {{
                putExtra(TaskListActivity.OPEN_TASK, taskId);
                if (filter != null && filter instanceof FilterWithCustomIntent) {
                    Bundle customExtras = ((FilterWithCustomIntent) filter).customExtras;
                    putExtras(customExtras);
                }
            }};
            return PendingIntent.getActivity(context, (int) taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addParentStack(TaskEditActivity.class);
            taskStackBuilder.addNextIntent(new Intent(context, TaskEditActivity.class) {{
                putExtra(TaskEditFragment.TOKEN_ID, taskId);
            }});
            return taskStackBuilder.getPendingIntent((int) taskId, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
