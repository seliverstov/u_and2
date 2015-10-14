package com.seliverstov.popularmovies;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public class MoviesGridFragment extends Fragment {
    private final int VISIBLE_TRESHOLD = 2;
    private final MovieLoader movieLoader = new MovieLoader();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();
        View view = inflater.inflate(R.layout.fragment_movies_grid,container,false);
        final GridView gv = (GridView)view.findViewById(R.id.movies_grid);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        final ArrayAdapter<Movie> adapter = new ArrayAdapter<Movie>(context,android.R.layout.simple_list_item_1){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.i(this.getClass().toString(),"Get View for Item "+position);
                ImageView imageView;
                if (convertView==null) {
                    imageView = new ImageView(context);
                    imageView.setLayoutParams(new GridView.LayoutParams(342,513));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }else{
                    imageView = (ImageView)convertView;
                }

                Movie m = getItem(position);
                if (m.getPosterPath()!=null) {
                    String url = "http://image.tmdb.org/t/p/w342/" + m.getPosterPath();
                    Log.i(this.getClass().toString(), url);
                    Picasso.with(context).load(url).into(imageView);
                }else{
                    imageView.setImageResource(R.drawable.noposter);
                }
                return imageView;
            }
        };

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - visibleItemCount <= (firstVisibleItem + VISIBLE_TRESHOLD)) {
                    loadMoviesToAdapter(adapter);
                }
            }
        });

        gv.setAdapter(adapter);
        loadMoviesToAdapter(adapter);
        return view;
    }

    private void loadMoviesToAdapter(ArrayAdapter<Movie> adapter){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sp.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
        movieLoader.loadMoreMovies(adapter,sortBy);
    }
}
