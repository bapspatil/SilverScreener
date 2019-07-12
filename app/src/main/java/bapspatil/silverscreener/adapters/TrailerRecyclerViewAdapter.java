package bapspatil.silverscreener.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import bapspatil.silverscreener.R;
import bapspatil.silverscreener.utils.GlideApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder> {

    private ArrayList<String> mTrailerTitles, mTrailerPaths;
    private Context mContext;
    private ItemClickListener mClickListener;

    public TrailerRecyclerViewAdapter(Context context, ArrayList<String> trailerTitles, ArrayList<String> trailerPaths, ItemClickListener itemClickListener) {
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
        Typeface hammersmithOne = ResourcesCompat.getFont(mContext, R.font.hammersmith_one);
        viewHolder.trailerTitleTextView.setTypeface(hammersmithOne);
        if (title == null)
            viewHolder.trailerTitleTextView.setText("Why aren't you connected to the internet? Or maybe there are no trailers for this movie...");
        else
            viewHolder.trailerTitleTextView.setText(title);

        String thumbnailUrlStr = "https://img.youtube.com/vi/" + mTrailerPaths.get(position) + "/0.jpg";
        GlideApp.with(mContext)
                .load(thumbnailUrlStr)
                .error(R.drawable.cursor_search)
                .fallback(R.drawable.cursor_search)
                .centerCrop()
                .transition(new DrawableTransitionOptions().crossFade())
                .into(viewHolder.trailerThumbnailImageView);
    }

    @Override
    public int getItemCount() {
        if (mTrailerTitles == null) return 0;
        else return mTrailerTitles.size();
    }

    public interface ItemClickListener {
        void onItemClick(String stringUrlTrailerClicked);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_title_tv)
        TextView trailerTitleTextView;
        @BindView(R.id.trailer_thumbnail_iv)
        ImageView trailerThumbnailImageView;

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
