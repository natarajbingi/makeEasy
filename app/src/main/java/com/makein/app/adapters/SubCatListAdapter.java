package com.makein.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makein.app.Models.GetAllSubsRes;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.controler.BitmapTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SubCatListAdapter extends RecyclerView.Adapter<SubCatListAdapter.MyViewHolder> {

    private Context mContext;
    private List<GetAllSubsRes.Data> data;
    private ItemClickListener mClickListener;


    public SubCatListAdapter(Context mContext, List<GetAllSubsRes.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }


    @NonNull
    @Override
    public SubCatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_items, parent, false);

        return new SubCatListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final SubCatListAdapter.MyViewHolder holder, final int position) {

        GetAllSubsRes.Data items = data.get(position);
        holder.category.setText(items.name.toUpperCase());
        holder.subcategory.setText(items.description);
        try {
            holder.loadSubCats.removeAllViews();
            //addImgs(position, holder.loadSubCats);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (items.img_urls.size()>0) {
            try {
                int size = (int) Math.ceil(Math.sqrt(800 * 600));
                // Loads given image
                Picasso.get()
                        .load(items.img_urls.get(0))
                        .transform(new BitmapTransform(800, 600))
                        .resize(size, size)
                        .centerInside()
//                                        .noPlaceholder()
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.load_failed)
                        .into(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.showHiddem.setVisibility(View.GONE);
        holder.showHiddem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (data.get(position).subProds != null && data.get(position).subProds.size() > 0) {
                    if (!holder.hide) {
                        holder.hideSubCat.setVisibility(View.VISIBLE);
                    } else {
                        holder.hideSubCat.setVisibility(View.GONE);
                    }
                    holder.hide = !holder.hide;
                }*/
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView category, subcategory;
        public ImageView image, showHiddem;
        LinearLayout hideSubCat, loadSubCats;
        boolean hide = true;

        public MyViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category_recycle);
            subcategory = (TextView) view.findViewById(R.id.subcategory_recycle);
            image = (ImageView) view.findViewById(R.id.image_recycle);
            showHiddem = (ImageView) view.findViewById(R.id.showHiddem);
            hideSubCat = (LinearLayout) view.findViewById(R.id.hideSubCat);
            loadSubCats = (LinearLayout) view.findViewById(R.id.loadSubCats);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, data.get(getAdapterPosition()));
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, GetAllSubsRes.Data data);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /*private void addImgs(int p, LinearLayout loadSubCats) throws Exception {
        if (data.get(p).subProds != null && data.get(p).subProds.size() > 0) {
            for (int i = 0; i < data.get(p).subProds.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(mContext)
                        .inflate(R.layout.text_layout, loadSubCats, false);
                TextView imgNewScroll = (TextView) newView1.findViewById(R.id.textNewScroll);
                imgNewScroll.setText((i + 1) + ": " + data.get(p).subProds.get(i).name.toUpperCase());
//                imgNewScroll.setTag(R.string.IMAGE_TAG , (mArrayUri.get(i)));
                loadSubCats.addView(newView1);
            }
        }
    }*/
}
