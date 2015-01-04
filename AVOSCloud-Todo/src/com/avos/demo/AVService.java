package com.avos.demo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.avos.avoscloud.*;
import com.avos.avoscloud.search.AVSearchQuery;

import java.util.Collections;
import java.util.List;

/**
 * Created by lzw on 14-9-12.
 */
public class AVService {
  public static void AVInit(Context ctx) {
    // 初始化应用 Id 和 应用 Key，您可以在应用设置菜单里找到这些信息
    AVOSCloud.initialize(ctx, "70l90kzm53nplnu013ala0j8wipr594d36m5zuz94ukvmh5s",
        "lamrsuheyiaqcx4o7m3jaz4awaeukerit1mucnjwk7ibokfv");
    // 启用崩溃错误报告
    AVOSCloud.setDebugLogEnabled(true);
    AVAnalytics.enableCrashReport(ctx, true);
    // 注册子类
    AVObject.registerSubclass(Todo.class);
  }

  public static void fetchTodoById(String objectId,GetCallback<AVObject> getCallback) {
    Todo todo = new Todo();
    todo.setObjectId(objectId);
    // 通过Fetch获取content内容
    todo.fetchInBackground(getCallback);
  }

  public static void createOrUpdateTodo(String objectId, String content, SaveCallback saveCallback) {
    final Todo todo = new Todo();
    if (!TextUtils.isEmpty(objectId)) {
      // 如果存在objectId，保存会变成更新操作。
      todo.setObjectId(objectId);
    }
    todo.setContent(content);
    // 异步保存
    todo.saveInBackground(saveCallback);
  }

  public static List<Todo> findTodos() {
    // 查询当前Todo列表
    AVQuery<Todo> query = AVQuery.getQuery(Todo.class);
    // 按照更新时间降序排序
    query.orderByDescending("updatedAt");
    // 最大返回1000条
    query.limit(1000);
    try {
      return query.find();
    } catch (AVException exception) {
      Log.e("tag", "Query todos failed.", exception);
      return Collections.emptyList();
    }
  }

  public static void searchQuery(String inputSearch) {
    AVSearchQuery searchQuery = new AVSearchQuery(inputSearch);
    searchQuery.search();
  }
}
