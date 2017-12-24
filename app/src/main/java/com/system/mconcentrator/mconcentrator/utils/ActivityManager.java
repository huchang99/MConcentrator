package com.system.mconcentrator.mconcentrator.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by huchang on 2017/9/29.
 */

public class ActivityManager {
    private static final String TAG = "ActivityManager";
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;
    private Activity currActivity;

    private ActivityManager() {
    }

    public static ActivityManager getActivityManager() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * @description 退出栈顶Activity
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity == null || activityStack == null) {
            return;
        }
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
        currActivity = activity;
    }

    public void destoryActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.finish();
        if (activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
        activity = null;
        LogHelper.d(TAG, "destoryActivity=================");
    }

    /**
     * @des 获得当前栈顶Activity
     * @return
     */
    public Activity currentActivity() {
        if (activityStack == null || activityStack.empty()) {
            return null;
        }
        return activityStack.lastElement();
    }

    /**
     * @des 将当前Activity推入栈中
     * @param activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * @des 退出栈中除指定的Activity外的所有
     * @param cls
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            destoryActivity(activity);
        }
    }

    /**
     * @des 退出栈中所有Activity
     */
    public void popAllActivity() {
        popAllActivityExceptOne(null);
    }

    public Activity getCurrentActivity() {
        return currActivity;
    }

    public int getActivityStackSize() {
        int size = 0;
        if (activityStack != null) {
            size = activityStack.size();
        }
        return size;
    }

}
