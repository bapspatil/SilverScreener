package bapspatil.silverscreener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bapspatil.silverscreener.R;
import bapspatil.silverscreener.model.Cast;
import bapspatil.silverscreener.network.RetrofitAPI;
import bapspatil.silverscreener.utils.GlideApp;

/**
 * Created by bapspatil
 */

public class CastRecyclerViewAdapter extends RecyclerView.Adapter<CastRecyclerViewAdapter.CastViewHolder> {
    private ArrayList<Cast> mCastList;
    private Context mContext;
    private OnActorClickHandler mActorClickHandler;

    public CastRecyclerViewAdapter(Context context, ArrayList<Cast> cast, OnActorClickHandler onActorClickHandler) {
        this.mContext = context;
        this.mCastList = cast;
        this.mActorClickHandler = onActorClickHandler;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        holder.mCastTextView.setText(mCastList.get(position).getName());
        GlideApp.with(mContext)
                .load(RetrofitAPI.POSTER_BASE_URL + mCastList.get(position).getProfilePath())
                .into(holder.mCastImageView);
    }

    @Override
    public int getItemCount() {
        return mCastList.size();
    }

    public interface OnActorClickHandler {
        void onActorClicked(String actorName);
    }

    public class CastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mCastTextView;
        public ImageView mCastImageView;

        public CastViewHolder(View itemView) {
            super(itemView);
            mCastImageView = itemView.findViewById(R.id.cast_iv);
            mCastTextView = itemView.findViewById(R.id.cast_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mActorClickHandler.onActorClicked(mCastTextView.getText().toString());
        }
    }
}
