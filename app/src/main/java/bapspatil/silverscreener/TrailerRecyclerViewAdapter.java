package bapspatil.silverscreener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

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
        viewHolder.trailerTitleTextView.setText(title);
    }

    @Override
    public int getItemCount() {
        if (mTrailerTitles == null) return 0;
        else return mTrailerTitles.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trailerTitleTextView;

        TrailerViewHolder(View itemView) {
            super(itemView);
            trailerTitleTextView = (TextView) itemView.findViewById(R.id.trailer_title_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null)
                mClickListener.onItemClick(mTrailerPaths.get(getAdapterPosition()));
        }
    }

}
