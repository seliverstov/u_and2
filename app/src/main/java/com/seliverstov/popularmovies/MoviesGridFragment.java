package com.seliverstov.popularmovies;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.db.PopularMoviesDbHelper;


/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MoviesGridFragment.class.getSimpleName();
    private int VISIBLE_THRESHOLD = 2;

    private int TMDB_MOVIES_LOADER_ID = 0;
    private int CURSOR_MOVIES_LOADER_ID = 1;


    private MoviesAdapter mMoviesAdapter;

    private static String[] COLUMNS = {
            PopularMoviesContact.MovieEntry._ID,
            PopularMoviesContact.MovieEntry.COLUMN_POSTER_PATH
    };

    public static int IDX_ID = 0;
    public static int IDX_POSTER_PATH = 1;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), PopularMoviesContact.MovieEntry.CONTENT_URI,COLUMNS,null,null, PopularMoviesContact.MovieEntry.COLUMN_POPULARITY+" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();

        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        mMoviesAdapter = new MoviesAdapter(context,null,0);

        final GridView gv = (GridView)view.findViewById(R.id.movies_grid);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) mMoviesAdapter.getItem(position);
                if (c != null) {
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.setData(ContentUris.withAppendedId(PopularMoviesContact.MovieEntry.CONTENT_URI, c.getLong(IDX_ID)));
                    startActivity(intent);
                }
            }
        });

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0) {
                    if (totalItemCount - visibleItemCount <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                        Log.i(LOG_TAG, "Load additional movies on scroll");
                        getLoaderManager().getLoader(TMDB_MOVIES_LOADER_ID).forceLoad();
                        Log.i(LOG_TAG, "Database size:"+DatabaseUtils.queryNumEntries((new PopularMoviesDbHelper(getActivity())).getReadableDatabase(), PopularMoviesContact.MovieEntry.TABLE_NAME));
                    }
                }
            }
        });

        gv.setAdapter(mMoviesAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "Init CURSOR_MOVIES_LOADER_ID");
        getLoaderManager().initLoader(CURSOR_MOVIES_LOADER_ID, null, this);
        Log.i(LOG_TAG, "Init TMDB_MOVIES_LOADER_ID");
        Loader l = getLoaderManager().initLoader(TMDB_MOVIES_LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new MoviesLoader(getActivity());
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {}

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {}
        });
        long count = DatabaseUtils.queryNumEntries((new PopularMoviesDbHelper(getActivity())).getReadableDatabase(), PopularMoviesContact.MovieEntry.TABLE_NAME);
        if (count == 0){
            l.forceLoad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*MainActivity a = ((MainActivity)getActivity());
        String currentSortOrder = a.getCurrentSortOrder();
        OldMovieLoader movieLoader = a.getMovieLoader();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sp.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        if (!currentSortOrder.equals(sortOrder)){
            a.setCurrentSortOrder(sortOrder);
            movieLoader.reset();
        }

        mAdapter.clear();
        if (movieLoader.getMovies().size()==0) {
            loadMoviesToAdapter(mAdapter);
        }else{
            mAdapter.addAll(movieLoader.getMovies());
        }*/
    }
}
