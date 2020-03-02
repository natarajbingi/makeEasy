package com.makein.app.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.controler.BitmapTransform;
import com.makein.app.controler.Controller;
import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;
import java.util.Map;

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
        data = (MyResponse.Data) bundle.getSerializable("data_cls");
        init(rootView);
        Spinner select_subcate = rootView.findViewById(R.id.select_subcate);

        Map<String, String> arr = new LinkedHashMap<String, String>();
        arr.put("Select", "Select");
        try {
            for (int i = 0; i < data.subProds.size(); i++) {
                arr.put(data.subProds.get(i).name.toUpperCase(), data.subProds.get(i).id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSpinners(select_subcate, arr.keySet().toArray(new String[0]));
        select_subcate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    addImgs(i - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    private void setSpinners(Spinner spr, String[] array) {
        // -----------------------------------------------
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                R.layout.custom_spinner,
                array);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spr.setAdapter(spinnerArrayAdapter);
    }

    private void init(View rootView) {
        selectedImgHolder = (LinearLayout) rootView.findViewById(R.id.selectedImgHolder);
        viewSelectedImg = (ImageView) rootView.findViewById(R.id.viewSelectedImg);
        subName = (TextView) rootView.findViewById(R.id.subName);
        viewSelectedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bitmap image = ((BitmapDrawable) viewSelectedImg.getDrawable()).getBitmap();
                    Controller.popUpImg(context, null, "Selected Image", "", image, "bitMap");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        addImgs(0);
    }

    private void addImgs(final int j) {
        if (data.subProds != null && data.subProds.size() > 0) {
            subName.setText("Category: " + data.name.toUpperCase() + "\n" + "Sub Category: " + data.subProds.get(j).name.toUpperCase());
            selectedImgHolder.removeAllViews();
            for (int i = 0; i < data.subProds.get(j).img_urls.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.img_layout, selectedImgHolder, false);
                ImageView imgNewScroll = (ImageView) newView1.findViewById(R.id.imgNewScroll);
                setImgUrl(imgNewScroll, data.subProds.get(j).img_urls.get(i));

                final int finalI1 = i;
                imgNewScroll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setImgUrl(viewSelectedImg, data.subProds.get(j).img_urls.get(finalI1));

                    }
                });
                setImgUrl(viewSelectedImg, data.subProds.get(j).img_urls.get(0));
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
