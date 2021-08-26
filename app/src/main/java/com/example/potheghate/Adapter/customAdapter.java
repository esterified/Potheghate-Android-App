package com.example.potheghate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potheghate.Model.R_Data;
import com.example.potheghate.R;
import com.example.potheghate.resItemActivity;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

//adapter for res activity
//Filterable implemented but not used
public class customAdapter extends RecyclerView.Adapter<customAdapter.ViewHolder> implements Filterable {

    private final ArrayList<R_Data> localDataSet;
    private final Context mContext;
    private ArrayList<R_Data> searchList;
    public static final String TAG="fayed";
    public static final String TIMEZONE="GMT+6:00";


    public customAdapter(Context context, ArrayList<R_Data> dataSet) {
        this.localDataSet = dataSet;
        this.mContext = context;
        this.searchList = new ArrayList<>(dataSet);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.res_list, viewGroup, false);

        return new ViewHolder(view);
    }

    // Create new views (invoked by the layout manager)

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ///--------------animation--------------------
        viewHolder.getCardView().setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fadeandslide));
        ////-------------Shimmer initializer-----------------
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        ////-------------animation-----------------
        R_Data _shopData = localDataSet.get(position);
        //---------------time-----------------
        time(_shopData.getOpen_at(),_shopData.getClose_at(),viewHolder.getBannerView(),viewHolder.getBannerTextView());
        ///----------------------------------------------------
        viewHolder.getTitleView().setText(_shopData.getName());
        String location = "Location: " + _shopData.getLocation();
        viewHolder.getLocationView().setText(location);
        String itemcount = _shopData.getItemCount() + "";
        viewHolder.getItemCountView().setText(itemcount);
        if(_shopData.getImage()!=null){
            Uri image = Uri.parse(_shopData.getImage());
            Picasso.get().load(image).fit().centerCrop().into(viewHolder.getImageView());
        }
        else{
            Picasso.get().load(R.drawable.image_not_found).fit().centerCrop().into(viewHolder.getImageView());
        }

        viewHolder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             try {
                    if(compare_time(_shopData.getOpen_at(),_shopData.getClose_at())){

                        Intent a = new Intent(v.getContext(), resItemActivity.class);
                        a.putExtra("name", _shopData.getId());
                        a.putExtra("image", _shopData.getImage());
                        a.putExtra("title", _shopData.getName());
                        v.getContext().startActivity(a);
                    }
                    else{
                        Snackbar.make(v,"Closed for Now. Try again later!",Snackbar.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });



    }

    private boolean compare_time(String open_at, String close_at) throws ParseException{
        assert open_at!= null;
        assert close_at!= null;
        Locale currentLocale = new Locale.Builder().setRegion("BD").build();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm",currentLocale);
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        Date open=dateFormat.parse(open_at);
        Date close=dateFormat.parse(close_at);
        Date now= dateFormat.parse(dateFormat.format(new Date()));;
       // Log.d(TAG,dateFormat.format(now) );
       // Log.d(TAG,"close at "+close_at );
        //Log.d(TAG,"open at "+open_at );
        assert now != null;
        int lowerBound=now.compareTo(open);
        int upperBound=now.compareTo(close);
        return lowerBound >= 0 && upperBound < 0;
    }
    private void time(String open_at, String close_at, RelativeLayout rel,TextView textView) {
        try {
            if(compare_time(open_at,close_at)){
                rel.setVisibility(View.GONE);
            }
            else{
                Locale currentLocale = new Locale.Builder().setRegion("BD").build();
                DateFormat df1 = new SimpleDateFormat("HH:mm",currentLocale);
                DateFormat df = new SimpleDateFormat("hh:mm a",currentLocale);
                String time=df.format(Objects.requireNonNull(df1.parse(open_at)));
                rel.setVisibility(View.VISIBLE);
                String text="Closed until "+time;
                textView.setText(text);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        //run on backend thread
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            ArrayList<R_Data> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(searchList);
            } else {
                for (R_Data data : searchList) {
                    if (data.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(data);
                    }
                }
            }
            Filter.FilterResults results = new Filter.FilterResults();
            results.values = filteredList;
            return results;

        }

        ////run on frontend thread
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            localDataSet.clear();
            ///////////////////////////////////////////////////BELOW LINE IS AVOIDED FOR NOW
            //localDataSet.addAll((Collection<? extends R_Data>) results.values);
            ///-------------------------------------------------


            notifyDataSetChanged();
        }
    };



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView TitleView;
        private final TextView LocationView;
        private final ImageView ImageView;
        private final CardView CardView;
        private final TextView itemCountView;
        private final RelativeLayout bannerView;
        private final TextView bannerTextView;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ImageView = view.findViewById(R.id.imageViewList);
            TitleView = view.findViewById(R.id.listTitle);
            LocationView = view.findViewById(R.id.listLocation);
            itemCountView = view.findViewById(R.id.listItemcount);
            CardView = view.findViewById(R.id.cardRoot);
            bannerView = view.findViewById(R.id.closed_banner);
            bannerTextView = view.findViewById(R.id.closed_banner_text);


        }

        public TextView getBannerTextView() {
            return bannerTextView;
        }

        public RelativeLayout getBannerView() {
            return bannerView;
        }

        public TextView getItemCountView() {
            return itemCountView;
        }

        public CardView getCardView() {
            return CardView;
        }

        public TextView getTitleView() {
            return TitleView;
        }

        public TextView getLocationView() {
            return LocationView;
        }

        public ImageView getImageView() {
            return ImageView;
        }
    }
}

