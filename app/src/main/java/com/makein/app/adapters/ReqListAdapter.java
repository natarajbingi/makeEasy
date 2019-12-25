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

import com.makein.app.Models.MyReqsResponse;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.controler.BitmapTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReqListAdapter extends RecyclerView.Adapter<ReqListAdapter.MyViewHolder> {

    private Context mContext;
    private List<MyReqsResponse.Data> data;
    private ItemClickListener mClickListener;


    public ReqListAdapter(Context mContext, List<MyReqsResponse.Data> data) {
        this.mContext = mContext;
        this.data = data;
    }


    @NonNull
    @Override
    public ReqListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_items, parent, false);

        return new ReqListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ReqListAdapter.MyViewHolder holder, final int position) {

        MyReqsResponse.Data items = data.get(position);
        holder.category.setText(items.usrName.toUpperCase());
        String tot = items.prodName + " - " + items.SubProdCatName;
        holder.subcategory.setText(tot.toUpperCase());
        holder.subDetails.setText("Requested User details.");
        holder.secDownArraow.setVisibility(View.GONE);
        try {
            holder.loadSubCats.removeAllViews();
            addImgs(position, holder.loadSubCats);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!items.img_urls.get(0).isEmpty()) {
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
        holder.hideSubCat.setVisibility(View.VISIBLE);
        holder.showHiddem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView category, subcategory, subDetails;
        public ImageView image, showHiddem, secDownArraow;
        LinearLayout hideSubCat, loadSubCats;
        boolean hide = true;

        public MyViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category_recycle);
            subcategory = (TextView) view.findViewById(R.id.subcategory_recycle);
            subDetails = (TextView) view.findViewById(R.id.subDetails);
            image = (ImageView) view.findViewById(R.id.image_recycle);
            showHiddem = (ImageView) view.findViewById(R.id.showHiddem);
            secDownArraow = (ImageView) view.findViewById(R.id.secDownArraow);
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
        void onItemClick(View view, MyReqsResponse.Data data);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void addImgs(int p, LinearLayout loadSubCats) throws Exception {
//        if (data.get(p).subProds != null && data.get(p).subProds.size() > 0) {
//            for (int i = 0; i < data.get(p).subProds.size(); i++) {
        final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(mContext)
                .inflate(R.layout.text_layout, loadSubCats, false);
        TextView imgNewScroll = (TextView) newView1.findViewById(R.id.textNewScroll);
        String mere =
                "Invoice No: " + data.get(p).invoice_no
                        + "\nCompany Name: " + data.get(p).userCompanyName
                        + "\n\nCompany Address: " + data.get(p).userCompanyAddress
                        + "\nCompany Email: " + data.get(p).userCompnyEmailAddress
                        + "\nContact No: " + data.get(p).userContactNo
//                        + "\nGender: " + data.get(p).gender
                        + "\n\nUser Address: " + data.get(p).delivery_address
                        + "\nUser M-No: " + data.get(p).mobile_no
                        + "\nComment: " + data.get(p).comment
                        + "\n\nReq Date: " + data.get(p).created_datetime
                        + "\n";
        imgNewScroll.setText(mere.toUpperCase());
//                imgNewScroll.setTag(R.string.IMAGE_TAG , (mArrayUri.get(i)));
        loadSubCats.addView(newView1);
//            }
//        }
    }
}
