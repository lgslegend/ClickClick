package xyz.imxqd.clickclick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import xyz.imxqd.clickclick.App;
import xyz.imxqd.clickclick.R;
import xyz.imxqd.clickclick.dao.DefinedFunction;

public class FunctionSpinnerAdapter extends BaseAdapter {

    private List<DefinedFunction> mFuncList;

    public FunctionSpinnerAdapter() {
        mFuncList = DefinedFunction.getOrderedAll();
        DefinedFunction f = new DefinedFunction();
        f.name = App.get().getString(R.string.more_functions);
        f.id = -1;
        mFuncList.add(f);
    }

    @Override
    public int getCount() {
        return mFuncList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFuncList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFuncList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView text = convertView.findViewById(android.R.id.text1);
        text.setText(mFuncList.get(position).name);
        return convertView;
    }
}
