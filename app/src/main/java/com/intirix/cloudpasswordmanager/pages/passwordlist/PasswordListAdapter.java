package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;

/**
 * Created by jeff on 7/12/16.
 */
public class PasswordListAdapter extends RecyclerView.Adapter<PasswordListViewHolder> {

    private SessionInfo session;

    private Activity activity;

    PasswordListAdapter(Activity activity, SessionInfo session) {
        this.session = session;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        if (session.getPasswordBeanList()==null) {
            return 0;
        }
        return session.getPasswordBeanList().size();
    }

    @Override
    public PasswordListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.password_list_row, parent, false);
        return new PasswordListViewHolder(activity, v);
    }

    @Override
    public void onBindViewHolder(PasswordListViewHolder holder, int position) {
        holder.applyItem(position, session.getPasswordBeanList().get(position));
    }
}
