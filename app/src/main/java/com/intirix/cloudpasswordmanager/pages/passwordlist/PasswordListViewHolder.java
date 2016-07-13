package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeff on 7/12/16.
 */
public class PasswordListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.password_list_row_website)
    TextView website;

    public PasswordListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void applyItem(PasswordInfo pass) {
        website.setText(pass.getWebsite());
    }
}
