package bapspatil.silverscreener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder> {

    private ArrayList<String> mTrailerTitles, mTrailerPaths;
    private Context mContext;
    private ItemClickListener mClickListener;

    interface ItemClickListener {
        void onItemClick(String stringUrlTrailerClicked);
    }

    TrailerRecyclerViewAdapter(Context context, ArrayList<String> trailerTitles, ArrayList<String> trailerPaths, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.mClickListener = itemClickListener;
        this.mTrailerTitles = trailerTitles;
        this.mTrailerPaths = trailerPaths;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_trailer_item, viewGroup, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder viewHolder, int position) {
        String title = mTrailerTitles.get(position);
        if(title == null)
            viewHolder.trailerTitleTextView.setText("Why aren't you connected to the internet? Or maybe there are no trailers for this movie...");
        else
            viewHolder.trailerTitleTextView.setText(title);

        String thumbnailUrlStr = "https://img.youtube.com/vi/" + mTrailerPaths.get(position) + "/0.jpg";
        Glide.with(mContext)
                .load(thumbnailUrlStr)
                .centerCrop()
                .error(R.drawable.no_internet_placeholder_landscape)
                .fallback(R.drawable.no_internet_placeholder_landscape)
                .into(viewHolder.trailerThumbnailImageView);
    }

    @Override
    public int getItemCount() {
        if (mTrailerTitles == null) return 0;
        else return mTrailerTitles.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_title_tv) TextView trailerTitleTextView;
        @BindView(R.id.trailer_thumbnail_iv) ImageView trailerThumbnailImageView;

        TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null)
                mClickListener.onItemClick(mTrailerPaths.get(getAdapterPosition()));
        }
    }

}
