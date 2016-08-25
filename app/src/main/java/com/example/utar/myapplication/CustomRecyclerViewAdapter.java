package com.example.utar.myapplication;

/**
 * Created by Kei Yeng on 22-Jul-16.
 */     import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.ArrayList;
import java.util.Random;

/**
 * Created by javierg on 14/03/16.
 */
public class CustomRecyclerViewAdapter extends RecyclerView
        .Adapter<CustomRecyclerViewAdapter
        .DataObjectHolder> {
    private int red = 40,green = 100,blue = 200;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Device> mDevice;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView deviceDetails;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.text1);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CustomRecyclerViewAdapter(ArrayList<Device> myDevices) {
        mDevice = myDevices;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_list_item_2, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        int darkBlue = Color.rgb(113, 158,236);
        int lightBlue = Color.rgb(209,223,246);
        Random randomGenerator = new Random();
        /*to generate color randomly
        do {

            red = randomGenerator.nextInt(256);
            green = randomGenerator.nextInt(256);
            blue = randomGenerator.nextInt(256);
        }while(red == green && red == blue);
        */
        holder.label.setText(mDevice.get(position).getDeviceName()
               + "\n" + (int)Math.ceil(mDevice.get(position).getDistance()) +"m");
        //to set di
        if(position % 2 == 0)
            holder.label.setBackgroundColor(darkBlue);
        else
            holder.label.setBackgroundColor(lightBlue);
    }

    public void addItem(Device dataObj, int index) {
        mDevice.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDevice.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDevice.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
