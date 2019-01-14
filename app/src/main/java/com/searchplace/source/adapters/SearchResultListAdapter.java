package com.searchplace.source.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.searchplace.source.R;
import com.searchplace.source.local_objects.Place;
import com.searchplace.source.views.SearchResultView;

import java.util.Vector;


public class SearchResultListAdapter extends RecyclerView.Adapter<SearchResultListAdapter.MyViewHolder> {

    private Vector<Place> _elements;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SearchResultView _searchResultView;

        public MyViewHolder(View view) {
            super(view);
            _searchResultView = (SearchResultView) view.findViewById(R.id.searchResultView);
        }
    }


    public SearchResultListAdapter(Vector<Place> _elements) {
        this._elements = _elements;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder._searchResultView.setInfo(_elements.get(position));

    }

    @Override
    public int getItemCount() {
        return _elements.size();
    }
}
