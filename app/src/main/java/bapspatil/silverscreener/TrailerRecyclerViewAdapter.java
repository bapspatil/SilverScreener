package bapspatil.silverscreener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder> {

    private ArrayList<String> mTrailerTitles, mTrailerPaths;
    private Context mContext;
    private ItemClickListener mClickListener;

    public interface ItemClickListener {
        void onItemClick(int position);
    }

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
    public void onBindViewHolder(TrailerViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        if (mTrailerTitles == null) return 0;
        else return mTrailerTitles.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TrailerViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
