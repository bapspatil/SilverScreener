package bapspatil.silverscreener;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieViewHolder> {

    private ArrayList<Movie> mMoviesArrayList;
    private Context mContext;
    private ItemClickListener mClickListener;

    interface ItemClickListener {
        void onItemClick(int position, CardView posterCardView);
    }

    MovieRecyclerViewAdapter(Context context, ArrayList<Movie> movieArrayList, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.mMoviesArrayList = movieArrayList;
        this.mClickListener = itemClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_movie_item, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie theMovie = mMoviesArrayList.get(position);
        if(theMovie.getPosterBytes() != null) {
            Glide.with(mContext)
                    .load(theMovie.getPosterBytes())
                    .centerCrop()
                    .error(R.drawable.no_internet_placeholder)
                    .fallback(R.drawable.no_internet_placeholder)
                    .into(holder.mPosterImageView);
        } else {
            Glide.with(mContext)
                    .load(theMovie.getPosterPath())
                    .centerCrop()
                    .error(R.drawable.no_internet_placeholder)
                    .fallback(R.drawable.no_internet_placeholder)
                    .into(holder.mPosterImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviesArrayList == null) return 0;
        else return mMoviesArrayList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.poster_image_view) ImageView mPosterImageView;
        @BindView(R.id.poster_card_view) CardView mPosterCardView;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null)
                mClickListener.onItemClick(getAdapterPosition(), mPosterCardView);
        }
    }
}
