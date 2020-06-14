package com.prerevise.grabvid.statussaver;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Magnifier;
import android.widget.Toast;

import com.prerevise.grabvid.R;
import com.prerevise.grabvid.adapters.StoryAdapter;
import com.prerevise.grabvid.models.StoryModel;
import com.prerevise.grabvid.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusSaverMainFragment extends Fragment {

    public static final int MY_PERMISSION_REQUEST_WRITE_STORAGE = 123;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout recyclerLayout;
    private StoryAdapter recyclerViewAdapter;
    private File[] files;
    ArrayList<Object> filesList = new ArrayList<>();


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

        boolean result = checkPermission();
        if (result){
            setUpRecyclerView();
        }

        return view;
    }

    private void setUpRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewAdapter = new StoryAdapter(getActivity(), getData());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    private ArrayList<Object> getData(){

        if (filesList != null) {
            filesList = new ArrayList<>();
        }
        StoryModel f;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FOLDER_NAME + "Media/.Statuses";
        File targetDirector = new File(targetPath);
        files = targetDirector.listFiles();

        try {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.lastModified() > o2.lastModified()){
                        return -1;
                    }else if (o1.lastModified() < o2.lastModified()){
                        return +1;
                    }else {
                        return 0;
                    }
                }
            });
            for (int i = 0; i<files.length; i++){
                File file = files[i];
                f = new StoryModel();
                f.setName("Download");
                f.setUri(Uri.fromFile(file));
                f.setPath(files[i].getAbsolutePath());
                filesList.add(f);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return filesList;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission Necessary");
                    alertBuilder.setMessage("Write Storage Permission is nedded to Download");
                    alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_WRITE_STORAGE);
                }
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }

    }

    public void checkAgain(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission Necessary");
            alertBuilder.setMessage("Write Storage Permission is nedded to Download");
            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_WRITE_STORAGE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        switch (requestCode){
            case MY_PERMISSION_REQUEST_WRITE_STORAGE:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    setUpRecyclerView();
                }else {
                    checkAgain();
                }
                break;
        }
    }
}
