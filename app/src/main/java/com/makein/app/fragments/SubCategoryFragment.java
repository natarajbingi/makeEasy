package com.makein.app.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.controler.BitmapTransform;
import com.makein.app.controler.Controller;
import com.squareup.picasso.Picasso;

public class SubCategoryFragment extends Fragment {

    Context context;
    ImageView viewSelectedImg;
    LinearLayout selectedImgHolder;
    TextView subName;
    MyResponse.Data data;

    public static SubCategoryFragment newInstance() {
        SubCategoryFragment fragment = new SubCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SubCategoryFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_category, container, false);
        context = container.getContext();

        Bundle bundle = getArguments();
//            data = (MyResponse.Data) getActivity().getIntent().getSerializableExtra("data_cls");
        data = (MyResponse.Data) bundle.getSerializable("data_cls");
        init(rootView);


        return rootView;
    }

//    RecyclerView sub_items_recycle;

    private void init(View rootView) {
//        sub_items_recycle = (RecyclerView) rootView.findViewById(R.id.sub_items_recycle);
//        myResponse = (MyResponse) Sessions.getUserObj(context, Controller.Categories, MyResponse.class);
        selectedImgHolder = (LinearLayout) rootView.findViewById(R.id.selectedImgHolder);
        viewSelectedImg = (ImageView) rootView.findViewById(R.id.viewSelectedImg);
        subName = (TextView) rootView.findViewById(R.id.subName);
        viewSelectedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap image = ((BitmapDrawable) viewSelectedImg.getDrawable()).getBitmap();

                Controller.popUpImg(context, null, "Selected Image", "", image, "bitMap");
            }
        });
        addImgs();
    }

    private void addImgs() {
        if (data.subProds != null && data.subProds.size() > 0) {
            subName.setText("Category: " + data.name.toUpperCase() + "\n" + "Sub Category: " + data.subProds.get(0).name.toUpperCase());
            selectedImgHolder.removeAllViews();
            for (int i = 0; i < data.subProds.get(0).img_urls.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.img_layout, selectedImgHolder, false);
                ImageView imgNewScroll = (ImageView) newView1.findViewById(R.id.imgNewScroll);
                // imgNewScroll.setImageURI();
                setImgUrl(imgNewScroll, data.subProds.get(0).img_urls.get(i));

                final int finalI1 = i;
                imgNewScroll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setImgUrl(viewSelectedImg, data.subProds.get(0).img_urls.get(finalI1));

                    }
                });
                setImgUrl(viewSelectedImg, data.subProds.get(0).img_urls.get(0));
                selectedImgHolder.addView(newView1);
            }
        }
    }

    private void setImgUrl(ImageView imgNewScroll, String url) {
        try {
            int size = (int) Math.ceil(Math.sqrt(800 * 600));
            // Loads given image
            Picasso.get()
                    .load(url)
                    .transform(new BitmapTransform(800, 600))
                    .resize(size, size)
                    .centerInside()
//                                        .noPlaceholder()
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.load_failed)
                    .into(imgNewScroll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
