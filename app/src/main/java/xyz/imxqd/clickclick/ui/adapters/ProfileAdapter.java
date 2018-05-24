package xyz.imxqd.clickclick.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.imxqd.clickclick.R;
import xyz.imxqd.clickclick.dao.KeyMappingEvent;
import xyz.imxqd.clickclick.dao.KeyMappingEvent_Table;

/**
 * Created by imxqd on 2017/11/26.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.KeyMapHolder> {

    List<KeyMappingEvent> mEvents;
    CheckChangeCallback mCallback;

    public ProfileAdapter() {
        mEvents = new Select().from(KeyMappingEvent.class)
                .orderBy(KeyMappingEvent_Table.id, false)
                .queryList();
    }

    public void setCheckChangeCallback(CheckChangeCallback callback) {
        mCallback = callback;
    }

    public int getEnableCount() {
        int count = 0;
        for (KeyMappingEvent e : mEvents) {
            if (e.enable) {
                count++;
            }
        }
        return count;
    }

    public KeyMappingEvent getItem(int pos) {
        return mEvents.get(pos);
    }

    public void refreshData() {
        List<KeyMappingEvent> events = new Select().from(KeyMappingEvent.class)
                .orderBy(KeyMappingEvent_Table.id, false)
                .queryList();
        mEvents.clear();
        mEvents.addAll(events);
    }

    @Override
    public ProfileAdapter.KeyMapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_profile, parent, false);
        return new KeyMapHolder(v);
    }

    @Override
    public void onBindViewHolder(ProfileAdapter.KeyMapHolder holder, int position) {
        KeyMappingEvent event = mEvents.get(position);
        holder.title.setText(event.funcName);
        holder.subTitle.setText(event.keyName + "  " + event.eventType.getName());
        holder.enable.setChecked(event.enable);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    class KeyMapHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_title)
        TextView title;
        @BindView(R.id.profile_sub_title)
        TextView subTitle;
        @BindView(R.id.profile_switch)
        SwitchCompat enable;
        public KeyMapHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getAdapterPosition();
                    mEvents.get(pos).enable = isChecked;
                    mEvents.get(pos).save();
                    if (mCallback != null) {
                        mCallback.onCheckedChanged(isChecked);
                    }
                }
            });
        }
    }

    public interface CheckChangeCallback {
        void onCheckedChanged(boolean isChecked);
    }
}
