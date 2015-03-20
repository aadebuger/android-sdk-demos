package com.avoscloud.beijing.push.demo.keepalive;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class UserListFragment extends Fragment {

  ListView onlineUserListView;
  View joinGroup;
  List<AVUser> onlineUsers;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.onlinelist, null);
    onlineUserListView = (ListView) rootView.findViewById(R.id.onlineList);
    joinGroup = rootView.findViewById(R.id.add_new);
    joinGroup.setVisibility(View.GONE);
    AVQuery<AVUser> aviq = AVUser.getQuery();
    if (onlineUsers == null) {
      onlineUsers = new LinkedList<AVUser>();
    }
    // 取出所有的在线用户，显示出来
    aviq.orderByDescending("updatedAt");
    aviq.setLimit(100);
    aviq.whereEqualTo("v2", true);
    aviq.whereNotEqualTo("objectId", AVUser.getCurrentUser().getObjectId());
    aviq.findInBackground(new FindCallback<AVUser>() {
      @Override
      public void done(List<AVUser> parseObjects, AVException parseException) {
        if (parseException == null) {
          if (!onlineUsers.isEmpty()) {
            onlineUsers.clear();
          }
          for (AVUser o : parseObjects) {
            HTBApplication.registerLocalNameCache(o.getObjectId(), o.getUsername());
          }
          onlineUsers.addAll(parseObjects);
          UserListAdapter adapter = new UserListAdapter(getActivity(), onlineUsers);
          onlineUserListView.setAdapter(adapter);
          onlineUserListView.setOnItemClickListener(adapter);
        }
      }
    });
    return rootView;
  }

  public class UserListAdapter extends BaseAdapter implements OnItemClickListener {

    public UserListAdapter(Context context, List<AVUser> users) {
      this.onlineUsers = users;
      this.mContext = context;
      random = new Random();
    }

    Context mContext;
    List<AVUser> onlineUsers;
    Random random;

    @Override
    public int getCount() {
      return onlineUsers.size();
    }

    @Override
    public AVUser getItem(int position) {
      // TODO Auto-generated method stub
      return onlineUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder = null;
      if (convertView == null) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_target, null);
        holder = new ViewHolder();
        holder.username = (TextView) convertView.findViewById(R.id.onlinetarget);
        holder.avatar = (ImageView) convertView.findViewById(R.id.online_icon);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }
      int avatarColor =
          Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

      holder.avatar.setBackgroundColor(avatarColor);
      holder.username.setText(this.getItem(position).getUsername());

      return convertView;
    }

    public class ViewHolder {
      TextView username;
      ImageView avatar;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long itemId) {
      final AVUser u = this.getItem(position);
      final AVIMClient client = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
      AVIMConversationQuery query = client.getQuery();
      query.withMembers(Arrays.asList(u.getObjectId(), AVUser.getCurrentUser().getObjectId()));
      query.whereEqualTo("public", true);
      query.limit(1);
      query.orderByDescending("lm");
      query.findInBackground(new AVIMConversationQueryCallback() {
        @Override
        public void done(List<AVIMConversation> avimConversations, AVException e) {
          if (e == null) {
            if (avimConversations.size() > 0) {
              startConversationActivity(avimConversations.get(0));
            } else {
              Map<String, Object> attributes = new HashMap<String, Object>();
              attributes.put("public", true);
              client.createConversation(Arrays.asList(u.getObjectId()), "[" + u.getUsername() + ","
                  + AVUser.getCurrentUser().getUsername()
                  + "]", attributes, false,
                  new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation conversation, AVException e) {
                      startConversationActivity(conversation);
                    }
                  });
            }
          }
        }
      });
    }
  }

  private void startConversationActivity(AVIMConversation conversation) {
    Intent i = new Intent(getActivity(), PrivateConversationActivity.class);
    i.putExtra(PrivateConversationActivity.DATA_EXTRA_SINGLE_DIALOG_TARGET,
        conversation.getConversationId());
    startActivity(i);
    getActivity().overridePendingTransition(android.R.anim.slide_in_left,
        android.R.anim.slide_out_right);
  }

}
