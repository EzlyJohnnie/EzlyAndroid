package com.ezly.ezly_android.UI.DialogView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.UIHelper.UIHelper;
import com.ezly.ezly_android.R;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Johnnie on 21/03/16.
 */
public class LocationSelectDialogView extends EzlyBaseDialogView {

    private LocationSelectViewListener mListener;
    private LinearLayout container;
    private View emptyAddressView;
    private View btnAdd;
    private List<EzlyAddress> addresses;

    public static LocationSelectDialogView newInstance(Activity activity, List<EzlyAddress> addresses){
        Dialog dlg = new Dialog(activity, R.style.draw_dialog_bottom_in);
        LocationSelectDialogView languageDialogView = new LocationSelectDialogView(activity, dlg, addresses);
        return languageDialogView;
    }

    private LocationSelectDialogView(Activity activity, Dialog dialog, List<EzlyAddress> addresses) {
        super(activity);
        this.activity = activity;
        this.dialog = dialog;
        this.addresses = addresses;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_location_select, this);
        dialog.setContentView(this);
        ButterKnife.bind(this, view);
        init(view);
    }

    public void setListener(LocationSelectViewListener mListener) {
        this.mListener = mListener;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LocationSelectDialogView.this.mListener.onClose();
            }
        });
    }

    private void init(View view) {
        container = (LinearLayout) view.findViewById(R.id.container);
        emptyAddressView = view.findViewById(R.id.empty_address_view);
        btnAdd = view.findViewById(R.id.btn_add);

        initAddress();
        findViewById(R.id.top_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.hideKeyBoard(v);
                dialog.dismiss();
                if(mListener != null){
                    mListener.onClose();
                }
            }
        });

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(mListener != null){
                    mListener.onAddClick();
                }
            }
        });
    }

    private void initAddress() {
        if(addresses != null && addresses.size() > 0) {
            for (final EzlyAddress address : addresses) {
                View addressView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_address, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView txtName = (TextView) addressView.findViewById(R.id.txt_name);
                TextView txtAddress = (TextView) addressView.findViewById(R.id.txt_address);
                txtName.setText(address.getName());
                txtAddress.setText(address.getFullAddress());

                addressView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (mListener != null) {
                            mListener.onAddressSelected(address);
                        }
                    }
                });

                container.addView(addressView);
            }
        }
        else{
            emptyAddressView.setVisibility(VISIBLE);
        }
    }

    public interface LocationSelectViewListener {
        void onClose();
        void onAddressSelected(EzlyAddress address);
        void onAddClick();
    }
}
