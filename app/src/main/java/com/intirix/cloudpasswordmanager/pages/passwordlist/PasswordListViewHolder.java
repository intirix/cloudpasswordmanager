package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeff on 7/12/16.
 */
public class PasswordListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.password_list_row_website)
    TextView website;

    @BindView(R.id.password_list_row_loginName)
    TextView loginName;

    @BindView(R.id.password_list_row_category)
    TextView categoryName;

    public PasswordListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void applyItem(PasswordBean pass) {
        website.setText(pass.getWebsite());
        loginName.setText(pass.getLoginName());
        if (pass.getCategoryName()==null||pass.getCategoryName().length()==0) {
            categoryName.setVisibility(View.GONE);
        } else {
            categoryName.setVisibility(View.VISIBLE);
            categoryName.setText(pass.getCategoryName());
            categoryName.setTextColor(pass.getCategoryForeground());
            categoryName.setBackgroundColor(pass.getCategoryBackground());
        }
    }
}
