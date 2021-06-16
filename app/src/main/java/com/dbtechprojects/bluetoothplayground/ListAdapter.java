package com.dbtechprojects.bluetoothplayground;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


@RequiresApi(api = Build.VERSION_CODES.R)
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final ArrayList<Device> localDataSet = new ArrayList<>();
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceName;
        private final TextView deviceMac;

        public ViewHolder(View view) {
            super(view);
            // Define views in layout

            deviceName = (TextView) view.findViewById(R.id.card_device_name);
            deviceMac  = (TextView) view.findViewById(R.id.card_device_mac);
        }

        public TextView getTextView() {
            return deviceName;
        }
        public TextView getDeviceMacTv(){
            return deviceMac;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param device containing the data to populate views to be used
     * by RecyclerView.
     */
    public void ListAdapterAddDevice(Device device) {
        Log.d("recyclerview", "ListAdapterAddDevice: " + device);
        localDataSet.add(device);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public @NotNull ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getName());
        viewHolder.getDeviceMacTv().setText(localDataSet.get(position).getMac());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

