package com.prerevise.grabvid.statussaver;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prerevise.grabvid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusSaverMainFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout recyclerLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status_saver_main, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerLayout = view.findViewById(R.id.swipeRecylerViewlayout);
        recyclerLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    recyclerLayout.setRefreshing(true);
                    setUpRecyclerView();
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerLayout.setRefreshing(false);
                            Toast.makeText(getActivity(),"Refreshed",Toast.LENGTH_SHORT).show();
                        }
                    },2000);
                } catch (Exception e){
                    Toast.makeText(getActivity(),"Error"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

//        boolean result = checkPermission();
//        if (result){
            setUpRecyclerView();
//        }

        return view;
    }

    private void setUpRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

    }
}
