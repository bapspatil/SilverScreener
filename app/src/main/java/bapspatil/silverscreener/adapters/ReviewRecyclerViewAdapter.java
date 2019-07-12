package bapspatil.silverscreener.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;
import bapspatil.silverscreener.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewItemViewHolder> {

    private ArrayList<String> mReviewAuthors, mReviewContents;
    private Context mContext;

    public ReviewRecyclerViewAdapter(Context context, ArrayList<String> reviewAuthors, ArrayList<String> reviewContents) {
        this.mContext = context;
        this.mReviewAuthors = reviewAuthors;
        this.mReviewContents = reviewContents;
    }

    @Override
    public ReviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_review_item, parent, false);
        return new ReviewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewItemViewHolder holder, int position) {
        String reviewAuthor = mReviewAuthors.get(position);
        Typeface hammersmithOne = ResourcesCompat.getFont(mContext, R.font.hammersmith_one);
        holder.reviewAuthorTextView.setTypeface(hammersmithOne);
        holder.reviewContentTextView.setTypeface(hammersmithOne);
        holder.reviewAuthorTextView.setText(reviewAuthor);
        holder.reviewContentTextView.setInterpolator(new OvershootInterpolator());
        holder.reviewContentTextView.setText(mReviewContents.get(position));
        holder.reviewExpandButton.setOnClickListener(view -> {
            holder.reviewExpandButton.setImageResource(holder.reviewContentTextView.isExpanded() ? R.drawable.ic_expand_more_white_24dp : R.drawable.ic_expand_less_white_24dp);
            holder.reviewContentTextView.toggle();
        });
        holder.reviewLinearLayout.setOnClickListener(view -> {
            holder.reviewExpandButton.setImageResource(holder.reviewContentTextView.isExpanded() ? R.drawable.ic_expand_more_white_24dp : R.drawable.ic_expand_less_white_24dp);
            holder.reviewContentTextView.toggle();
        });
    }

    @Override
    public int getItemCount() {
        if (mReviewAuthors == null) return 0;
        else return mReviewAuthors.size();
    }

    public class ReviewItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author_tv)
        TextView reviewAuthorTextView;
        @BindView(R.id.review_content_tv)
        ExpandableTextView reviewContentTextView;
        @BindView(R.id.review_expand_button)
        ImageButton reviewExpandButton;
        @BindView(R.id.review_linear_layout)
        LinearLayout reviewLinearLayout;

        ReviewItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
